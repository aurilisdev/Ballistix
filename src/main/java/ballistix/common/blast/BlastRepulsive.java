package ballistix.common.blast;

import java.util.ArrayList;
import java.util.List;

import ballistix.api.blast.IHasCustomRender;
import ballistix.client.particle.ParticleOptionsShockwave;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConstants;
import ballistix.compatibility.griefdefender.GriefDefenderHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlastRepulsive extends Blast implements IHasCustomRender {

	public BlastRepulsive(World world, BlockPos position) {
		super(world, position);
	}

	@Override
	public boolean doExplode(int callCount) {
		super.doExplode(callCount);
		hasStarted = true;
		if (!world.isClientSide) {
			world.explode(null, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, (float) BallistixConstants.EXPLOSIVE_REPULSIVE_SIZE, Explosion.Mode.BREAK);
		} else {
			produceParticles();
		}
		float x = position.getX();
		float y = position.getY();
		float z = position.getZ();

		float size = 7f;
		float doubleSize = size * 2.0F;

		int x0 = MathHelper.floor(x - (double) doubleSize - 1.0D);
		int x1 = MathHelper.floor(x + (double) doubleSize + 1.0D);
		int y0 = MathHelper.floor(y - (double) doubleSize - 1.0D);
		int y1 = MathHelper.floor(y + (double) doubleSize + 1.0D);
		int z0 = MathHelper.floor(z - (double) doubleSize - 1.0D);
		int z1 = MathHelper.floor(z + (double) doubleSize + 1.0D);

		List<Entity> entities = world.getEntities(null, new AxisAlignedBB(x0, y0, z0, x1, y1, z1));

		for (Entity entity : entities) {

			switch (griefPreventionMethod) {
			case GRIEF_DEFENDER:
				if (!GriefDefenderHandler.shouldEntityBeHarmed(entity)) {
					continue;
				}
				break;
			default:
				break;
			}

			double deltaX = entity.getX() - x;
			double deltaY = (entity instanceof TNTEntity ? entity.getY() : entity.getEyeY()) - y;
			double deltaZ = entity.getZ() - z;
			double deltaDistance = MathHelper.sqrt((float) (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ));
			if (deltaDistance == 0.0D) {
				continue;
			}
			deltaX = deltaX / deltaDistance;
			deltaY = deltaY / deltaDistance;
			deltaZ = deltaZ / deltaDistance;
			double d11 = BallistixConstants.EXPLOSIVE_ATTRACTIVE_REPULSIVE_PUSH_STRENGTH;
			entity.setDeltaMovement(entity.getDeltaMovement().add(deltaX * d11, deltaY * d11, deltaZ * d11));
			if (entity instanceof ServerPlayerEntity) {
				ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) entity;
				serverplayerentity.connection.send(new SExplosionPacket(x, y, z, size, new ArrayList<>(), new Vector3d(deltaX * d11, deltaY * d11, deltaZ * d11)));
			}
		}
		return true;
	}

	@Override
	public SubtypeBlast getBlastType() {
		return SubtypeBlast.repulsive;
	}

	@Override
	public boolean shouldRender() {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void produceParticles() {
		float x = position.getX() + 0.5f;
		float y = position.getY() + 0.5f;
		float z = position.getZ() + 0.5f;

		float size = 7f;

		float f2 = size * 2.0F;

		int x0 = MathHelper.floor(x - f2 - 1.0D);
		int x1 = MathHelper.floor(x + f2 + 1.0D);
		int y0 = MathHelper.floor(y - f2 - 1.0D);
		int y1 = MathHelper.floor(y + f2 + 1.0D);
		int z0 = MathHelper.floor(z - f2 - 1.0D);
		int z1 = MathHelper.floor(z + f2 + 1.0D);
		for (int dx = x0; dx < x1; dx++) {
			for (int dy = y0; dy < y1; dy++) {
				for (int dz = z0; dz < z1; dz++) {
					if ((x - dx) * (x - dx) + (y - dy) * (y - dy) + (z - dz) * (z - dz) <= (2 * size + 1) * (2 * size + 1)) {
						if (world.random.nextFloat() < 1 / 40.0) {
							IParticleData particle = new ParticleOptionsShockwave().setParameters(1, 1, 1, 1, (float) 0.3, 15, false, 1);
							Minecraft.getInstance().particleEngine.createParticle(particle, x, y, z, -(x - dx) / 15.0, -(y - dy) / 15.0, -(z - dz) / 15.0);
						}
					}
				}
			}
		}
	}

}
