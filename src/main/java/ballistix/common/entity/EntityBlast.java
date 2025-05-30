package ballistix.common.entity;

import ballistix.Ballistix;
import ballistix.api.blast.IBlast;
import ballistix.api.blast.IHasCustomRender;
import ballistix.common.blast.Blast;
import ballistix.registers.BallistixEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;

import javax.annotation.Nullable;

public class EntityBlast extends Entity {
    private static final EntityDataAccessor<Integer> CALLCOUNT = SynchedEntityData.defineId(EntityBlast.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> TYPE = SynchedEntityData.defineId(EntityBlast.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> SHOULDSTARTCUSTOMRENDER = SynchedEntityData.defineId(EntityBlast.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TICKCOUNT = SynchedEntityData.defineId(EntityBlast.class, EntityDataSerializers.INT);

    private Blast blast;
    public ResourceLocation blastId;
    public int callcount = 0;
    public boolean shouldRenderCustom = false;
    public int ticksWhenCustomRender;

    public boolean detonated = false;

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }

    public EntityBlast(EntityType<? extends EntityBlast> type, Level worldIn) {
        super(type, worldIn);
        blocksBuilding = true;
    }

    public EntityBlast(Level worldIn) {
        this(BallistixEntities.ENTITY_BLAST.get(), worldIn);
    }

    public void setBlastType(IBlast explosive) {
        blastId = explosive.id();
        blast = getBlastType().createBlast(level(), blockPosition());
    }

    @Nullable
    public IBlast getBlastType() {
        return blastId == null ? null : Blast.BLAST_MAP.get(blastId);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(CALLCOUNT, 0);
        builder.define(TYPE, "");
        builder.define(SHOULDSTARTCUSTOMRENDER, false);
        builder.define(TICKCOUNT, 0);
    }

    @Override
    public void tick() {
        tickCount++;
        if (detonated /* || tickCount > 1000 */) {
            if (!level().isClientSide && tickCount > 20) {
                remove(RemovalReason.DISCARDED);
            }
            return;
        }

        if (!level().isClientSide) {
            if(blastId != null) {
                entityData.set(TYPE, blastId.toString());
            }
            entityData.set(CALLCOUNT, callcount);
            entityData.set(SHOULDSTARTCUSTOMRENDER, blast instanceof IHasCustomRender has && has.shouldRender());
            entityData.set(TICKCOUNT, tickCount);
        } else {
            String str = entityData.get(TYPE);
            if(!str.isEmpty()) {
                blastId = ResourceLocation.parse(str);
            }
            callcount = entityData.get(CALLCOUNT);
            if (!shouldRenderCustom && entityData.get(SHOULDSTARTCUSTOMRENDER)) {
                ticksWhenCustomRender = tickCount;
            }
            shouldRenderCustom = entityData.get(SHOULDSTARTCUSTOMRENDER);
            if (blast != null) {
                blast.shouldRenderCustomClient = shouldRenderCustom;
            }
            tickCount = entityData.get(TICKCOUNT);
        }

        if (blastId == null) {
            return;
        }

        if (blast == null) {
            blast = getBlastType().createBlast(level(), blockPosition());
        }

        if (blast != null) {
            if (callcount == 0) {
                blast.preExplode();
            } else {
                if (blast.explode(callcount)) {
                    detonated = true;
                    blast.postExplode();
                }
            }
            callcount++;
        }
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        if (!level().isClientSide()) {
            ChunkPos pos = level().getChunk(blockPosition()).getPos();
            ChunkloaderManager.TICKET_CONTROLLER.forceChunk((ServerLevel) level(), blockPosition(), pos.x, pos.z, true, true);
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!level().isClientSide && reason == RemovalReason.DISCARDED) {
            ChunkPos pos = level().getChunk(blockPosition()).getPos();
            ChunkloaderManager.TICKET_CONTROLLER.forceChunk((ServerLevel) level(), blockPosition(), pos.x, pos.z, false, true);
        }
        super.remove(reason);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, blastId).ifSuccess(tag -> compound.put("type", tag));
        compound.putInt("callcount", callcount);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        ResourceLocation.CODEC.decode(NbtOps.INSTANCE, compound.get("type")).ifSuccess(pair -> blastId = pair.getFirst());
        callcount = compound.getInt("callcount");
        if (blastId != null) {
            setBlastType(getBlastType());
        }
    }

    public Blast getBlast() {
        return blast;
    }

    @EventBusSubscriber(modid = Ballistix.ID, bus = EventBusSubscriber.Bus.MOD)
    private static final class ChunkloaderManager {

        private static final TicketController TICKET_CONTROLLER = new TicketController(Ballistix.rl("blastcontroller"));

        @SubscribeEvent
        public static void register(RegisterTicketControllersEvent event) {
            event.register(TICKET_CONTROLLER);
        }

    }

}
