package ballistix.common.entity;

import ballistix.Ballistix;
import ballistix.References;
import ballistix.api.blast.IHasCustomRender;
import ballistix.common.blast.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.registers.BallistixEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;

public class EntityBlast extends Entity {
	private static final EntityDataAccessor<Integer> CALLCOUNT = SynchedEntityData.defineId(EntityBlast.class,
			EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(EntityBlast.class,
			EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> SHOULDSTARTCUSTOMRENDER = SynchedEntityData
			.defineId(EntityBlast.class, EntityDataSerializers.BOOLEAN);

	private Blast blast;
	public int blastOrdinal = -1;
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

	public void setBlastType(SubtypeBlast explosive) {
		blastOrdinal = explosive.ordinal();
		blast = getBlastType().createBlast(level(), blockPosition());
	}

	public SubtypeBlast getBlastType() {
		return blastOrdinal == -1 ? null : SubtypeBlast.values()[blastOrdinal];
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(CALLCOUNT, 0);
		builder.define(TYPE, -1);
		builder.define(SHOULDSTARTCUSTOMRENDER, false);
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
			entityData.set(TYPE, blastOrdinal);
			entityData.set(CALLCOUNT, callcount);
			entityData.set(SHOULDSTARTCUSTOMRENDER, blast instanceof IHasCustomRender has && has.shouldRender());
		} else {
			blastOrdinal = entityData.get(TYPE);
			callcount = entityData.get(CALLCOUNT);
			if (!shouldRenderCustom && entityData.get(SHOULDSTARTCUSTOMRENDER)) {
				ticksWhenCustomRender = tickCount;
			}
			shouldRenderCustom = entityData.get(SHOULDSTARTCUSTOMRENDER);
			if (blast != null) {
				blast.shouldRenderCustomClient = shouldRenderCustom;
			}
		}

		if (blastOrdinal == -1) {
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
			ChunkloaderManager.TICKET_CONTROLLER.forceChunk((ServerLevel) level(), blockPosition(), pos.x, pos.z, true,
					true);
		}
	}

	@Override
	public void remove(RemovalReason reason) {
		if (!level().isClientSide && reason == RemovalReason.DISCARDED) {
			ChunkPos pos = level().getChunk(blockPosition()).getPos();
			ChunkloaderManager.TICKET_CONTROLLER.forceChunk((ServerLevel) level(), blockPosition(), pos.x, pos.z, false,
					true);
		}
		super.remove(reason);
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		compound.putInt("type", blastOrdinal);
		compound.putInt("callcount", callcount);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		blastOrdinal = compound.getInt("type");
		callcount = compound.getInt("callcount");
		if (blastOrdinal != -1) {
			setBlastType(getBlastType());
		}
	}

	public Blast getBlast() {
		return blast;
	}

	@EventBusSubscriber(modid = References.ID, bus = EventBusSubscriber.Bus.MOD)
	private static final class ChunkloaderManager {

		private static final TicketController TICKET_CONTROLLER = new TicketController(Ballistix.rl("blastcontroller"));

		@SubscribeEvent
		public static void register(RegisterTicketControllersEvent event) {
			event.register(TICKET_CONTROLLER);
		}

	}

}
