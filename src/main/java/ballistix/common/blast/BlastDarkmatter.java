package ballistix.common.blast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ballistix.common.blast.thread.ThreadSimpleBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.Constants;
import ballistix.registers.BallistixSounds;
import electrodynamics.api.sound.SoundAPI;
import electrodynamics.prefab.utilities.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BlastDarkmatter extends Blast {

	public BlastDarkmatter(Level world, BlockPos position) {
		super(world, position);
	}

	@Override
	public void doPreExplode() {
		if (!world.isClientSide) {
			thread = new ThreadSimpleBlast(world, position, (int) Constants.EXPLOSIVE_DARKMATTER_RADIUS, Integer.MAX_VALUE, null, true);
			thread.start();
		} else {
			SoundAPI.playSound(BallistixSounds.SOUND_DARKMATTER.get(), SoundSource.BLOCKS, 1, 1, position);
		}
	}

	private ThreadSimpleBlast thread;
	private int callAtStart = -1;
	private int pertick = -1;

	private Iterator<BlockPos> cachedIterator;

	@Override
	public boolean doExplode(int callCount) {
		if (!world.isClientSide) {
			hasStarted = true;
			if (thread == null) {
				return true;
			}
			if (thread.isComplete) {
				if (callAtStart == -1) {
					callAtStart = callCount;
				}
				if (pertick == -1) {
					pertick = (int) (thread.results.size() / Constants.EXPLOSIVE_DARKMATTER_DURATION);
					cachedIterator = thread.results.iterator();
				}
				int finished = pertick;
				while (cachedIterator.hasNext()) {
					if (finished-- < 0) {
						break;
					}
					BlockPos p = new BlockPos(cachedIterator.next()).offset(position);
					BlockState state = world.getBlockState(p);
					if (state != Blocks.AIR.defaultBlockState() && state != Blocks.VOID_AIR.defaultBlockState() && state.getDestroySpeed(world, p) >= 0) {
						world.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
					}
				}
				if (!cachedIterator.hasNext()) {
					WorldUtils.clearChunkCache();
					return true;
				}
			}
			float x = position.getX();
			float y = position.getY();
			float z = position.getZ();
			float size = (float) Constants.EXPLOSIVE_DARKMATTER_RADIUS;
			float f2 = size * 2.0F;
			int k1 = Mth.floor(x - (double) f2 - 1.0D);
			int l1 = Mth.floor(x + (double) f2 + 1.0D);
			int i2 = Mth.floor(y - (double) f2 - 1.0D);
			int i1 = Mth.floor(y + (double) f2 + 1.0D);
			int j2 = Mth.floor(z - (double) f2 - 1.0D);
			int j1 = Mth.floor(z + (double) f2 + 1.0D);
			List<Entity> list = world.getEntities(null, new AABB(k1, i2, j2, l1, i1, j1));

			for (Entity entity : list) {
				double d5 = entity.getX() - x;
				double d7 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - y;
				double d9 = entity.getZ() - z;
				double d13 = Mth.sqrt((float) (d5 * d5 + d7 * d7 + d9 * d9));
				if (d13 != 0.0D) {
					d5 = d5 / d13;
					d7 = d7 / d13;
					d9 = d9 / d13;
					double d11 = (-0.2 - (callCount - callAtStart) / 150.0) / d13;
					entity.setDeltaMovement(entity.getDeltaMovement().add(d5 * d11, d7 * d11, d9 * d11));
					if (entity instanceof ServerPlayer serverplayerentity) {
						if (!serverplayerentity.isCreative()) {
							serverplayerentity.connection.send(new ClientboundExplodePacket(x, y, z, size, new ArrayList<>(), new Vec3(d5 * d11, d7 * d11, d9 * d11)));
						}
					} else if (entity instanceof FallingBlockEntity) {
						entity.remove(RemovalReason.DISCARDED);
					}
				}
			}
			attackEntities((float) ((callCount - callAtStart) / 75.0));
			if (world.random.nextFloat() < 0.5) {
				world.explode(null, position.getX(), position.getY(), position.getZ(), 2, BlockInteraction.NONE);
			}
		}
		return false;
	}

	@Override
	public boolean isInstantaneous() {
		return false;
	}

	@Override
	public SubtypeBlast getBlastType() {
		return SubtypeBlast.darkmatter;
	}

}
