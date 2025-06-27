package ballistix.common.entity;

import javax.annotation.Nullable;

import ballistix.Ballistix;
import ballistix.api.blast.IBlast;
import ballistix.api.blast.IHasCustomRender;
import ballistix.common.blast.util.Blast;
import ballistix.registers.BallistixEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityBlast extends Entity {
	
    private static final DataParameter<Integer> CALLCOUNT = EntityDataManager.defineId(EntityBlast.class, DataSerializers.INT);
    private static final DataParameter<String> TYPE = EntityDataManager.defineId(EntityBlast.class, DataSerializers.STRING);
    private static final DataParameter<Boolean> SHOULDSTARTCUSTOMRENDER = EntityDataManager.defineId(EntityBlast.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> TICKCOUNT = EntityDataManager.defineId(EntityBlast.class, DataSerializers.INT);

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

    public EntityBlast(EntityType<? extends EntityBlast> type, World worldIn) {
        super(type, worldIn);
        blocksBuilding = true;
    }

    public EntityBlast(World worldIn) {
        this(BallistixEntities.ENTITY_BLAST.get(), worldIn);
    }

    public void setBlastType(IBlast explosive) {
        blastId = explosive.id();
        blast = getBlastType().createBlast(level, blockPosition());
    }

    @Nullable
    public IBlast getBlastType() {
        return blastId == null ? null : Blast.BLAST_MAP.get(blastId);
    }

    @Override
    protected void defineSynchedData() {
    	entityData.define(CALLCOUNT, 0);
    	entityData.define(TYPE, "");
    	entityData.define(SHOULDSTARTCUSTOMRENDER, false);
    	entityData.define(TICKCOUNT, 0);
    }

    @Override
    public void tick() {
        tickCount++;
        if (detonated /* || tickCount > 1000 */) {
            if (!level.isClientSide && tickCount > 20) {
                remove(false);
            }
            return;
        }

        if (!level.isClientSide) {
        	if(blastId != null) {
                entityData.set(TYPE, blastId.toString());
            }
            entityData.set(CALLCOUNT, callcount);
            entityData.set(SHOULDSTARTCUSTOMRENDER, blast instanceof IHasCustomRender && ((IHasCustomRender) blast).shouldRender());
            entityData.set(TICKCOUNT, tickCount);
        } else {
        	String str = entityData.get(TYPE);
            if(!str.isEmpty()) {
                blastId = new ResourceLocation(str);
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
            blast = getBlastType().createBlast(level, blockPosition());
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
	public void onAddedToWorld() {
		super.onAddedToWorld();
		if (!level.isClientSide()) {
			ChunkPos pos = level.getChunk(blockPosition()).getPos();
			ForgeChunkManager.forceChunk((ServerWorld) level, Ballistix.ID, blockPosition(), pos.x, pos.z, true, true);
		}
	}

	@Override
	public void remove(boolean reason) {
		if (!level.isClientSide && reason == false) {
			ChunkPos pos = level.getChunk(blockPosition()).getPos();
			ForgeChunkManager.forceChunk((ServerWorld) level, Ballistix.ID, blockPosition(), pos.x, pos.z, false, true);
		}
		super.remove(reason);
	}

	@Override
	protected void addAdditionalSaveData(CompoundNBT compound) {
		ResourceLocation.CODEC.encodeStart(NBTDynamicOps.INSTANCE, blastId).result().ifPresent(tag -> compound.put("type", tag));
		compound.putInt("callcount", callcount);
	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT compound) {
		ResourceLocation.CODEC.decode(NBTDynamicOps.INSTANCE, compound.get("type")).result().ifPresent(pair -> blastId = pair.getFirst());
		callcount = compound.getInt("callcount");
		if (blastId != null) {
			setBlastType(getBlastType());
		}
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

    public Blast getBlast() {
        return blast;
    }

}
