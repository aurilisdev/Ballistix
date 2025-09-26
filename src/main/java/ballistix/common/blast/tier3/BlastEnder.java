package ballistix.common.blast.tier3;

import ballistix.api.blast.IBlast;
import ballistix.api.blast.IHasCustomRender;
import ballistix.client.particle.ParticleOptionsShockwave;
import ballistix.common.blast.util.Blast;
import ballistix.common.blast.util.thread.ThreadSimpleBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConstants;
import ballistix.compatibility.griefdefender.GriefDefenderHandler;
import net.minecraft.block.PortalInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

public class BlastEnder extends Blast implements IHasCustomRender {
	
	private static final DimensionManager MANAGER = new DimensionManager();
	
    public BlastEnder(World world, BlockPos position) {
        super(world, position);
    }

    @Override
    public IBlast getBlastType() {
        return SubtypeBlast.ender;
    }

    @Override
    public boolean doExplode(int callCount) {
        super.doExplode(callCount);

        hasStarted = true;

        if (world.isClientSide) {
            produceParticles();
            return true;
        }

        float x = position.getX();
        float y = position.getY();
        float z = position.getZ();

        float size = 7f;

        float f2 = size * 2.0F;

        int x0 = MathHelper.floor(x - (double) f2 - 1.0D);
        int x1 = MathHelper.floor(x + (double) f2 + 1.0D);
        int y0 = MathHelper.floor(y - (double) f2 - 1.0D);
        int y1 = MathHelper.floor(y + (double) f2 + 1.0D);
        int z0 = MathHelper.floor(z - (double) f2 - 1.0D);
        int z1 = MathHelper.floor(z + (double) f2 + 1.0D);

        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(x0, y0, z0, x1, y1, z1));

        for (LivingEntity entity : entities) {

            switch (griefPreventionMethod) {
                case GRIEF_DEFENDER:
                    if (!GriefDefenderHandler.shouldEntityBeHarmed(entity)) {
                        continue;
                    }
                    break;
                default:
                    break;
            }

            if(world.dimension().equals(World.END)) {
            	entity.changeDimension(ServerLifecycleHooks.getCurrentServer().getLevel(World.OVERWORLD), MANAGER);
                entity.teleportTo(entity.getX(), entity.getY(), entity.getZ());
            } else {
            	entity.changeDimension(ServerLifecycleHooks.getCurrentServer().getLevel(World.END), MANAGER);
                entity.teleportTo(entity.getX(), entity.getY(), entity.getZ());
            }

        }

        for(int i = 0; i < BallistixConstants.EXPLOSIVE_ENDER_ENDERMANCOUNT; i++) {
            EndermanEntity entity = new EndermanEntity(EntityType.ENDERMAN, world);
            entity.setPos(
                    //
                    position.getX() + ThreadSimpleBlast.boundedNextInt(world.random, (int) -BallistixConstants.EXPLOSIVE_ENDER_RADIUS, (int) BallistixConstants.EXPLOSIVE_ENDER_RADIUS + 1),
                    //
                    position.getY() + ThreadSimpleBlast.boundedNextInt(world.random, (int) -BallistixConstants.EXPLOSIVE_ENDER_RADIUS, (int) BallistixConstants.EXPLOSIVE_ENDER_RADIUS + 1),
                    //
                    position.getZ() + ThreadSimpleBlast.boundedNextInt(world.random, (int) -BallistixConstants.EXPLOSIVE_ENDER_RADIUS, (int) BallistixConstants.EXPLOSIVE_ENDER_RADIUS + 1)
            //
            );
            world.addFreshEntity(entity);
        }

        world.explode(null, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, (float) BallistixConstants.EXPLOSIVE_ENDER_RADIUS, Explosion.Mode.DESTROY);

        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
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
                            IParticleData particle = new ParticleOptionsShockwave().setParameters(0.933F, 0.212F, 0.933F, 1, (float) 0.1, 15, false, 1);
                            Minecraft.getInstance().particleEngine.createParticle(particle, dx, dy, dz, (x - dx) / 15.0, (y - dy) / 15.0, (z - dz) / 15.0);
                        }
                    }
                }
            }
        }
    }
    
    private static final class DimensionManager implements ITeleporter {

		@Override
		public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
			return repositionEntity.apply(false);
		}

		@Override
		public @Nullable PortalInfo getPortalInfo(Entity entity, ServerWorld destWorld, Function<ServerWorld, PortalInfo> defaultPortalInfo) {
			return new PortalInfo(entity.position(), Vector3d.ZERO, entity.yRot, entity.xRot);
		}

		@Override
		public boolean isVanilla() {
			return false;
		}

		@Override
		public boolean playTeleportSound(ServerPlayerEntity player, ServerWorld sourceWorld, ServerWorld destWorld) {
			return false;
		}

	}

}
