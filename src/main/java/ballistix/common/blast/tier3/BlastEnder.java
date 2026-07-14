package ballistix.common.blast.tier3;

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

import ballistix.api.blast.IBlast;
import ballistix.api.blast.IHasCustomRender;
import ballistix.client.particle.ParticleOptionsShockwave;
import ballistix.common.blast.util.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConstants;
import ballistix.compatibility.griefdefender.GriefDefenderHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.server.ServerLifecycleHooks;

public class BlastEnder extends Blast implements IHasCustomRender {
	
	private static final DimensionManager MANAGER = new DimensionManager();
	
    public BlastEnder(Level world, BlockPos position) {
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

        int x0 = Mth.floor(x - (double) f2 - 1.0D);
        int x1 = Mth.floor(x + (double) f2 + 1.0D);
        int y0 = Mth.floor(y - (double) f2 - 1.0D);
        int y1 = Mth.floor(y + (double) f2 + 1.0D);
        int z0 = Mth.floor(z - (double) f2 - 1.0D);
        int z1 = Mth.floor(z + (double) f2 + 1.0D);

        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, new AABB(x0, y0, z0, x1, y1, z1));

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

            if(world.dimension().equals(Level.END)) {
            	entity.changeDimension(ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD), MANAGER);
            } else {
            	entity.changeDimension(ServerLifecycleHooks.getCurrentServer().getLevel(Level.END), MANAGER);
            }
	    entity.teleportTo(entity.getX(), entity.getY(), entity.getZ());

        }

        for(int i = 0; i < BallistixConstants.EXPLOSIVE_ENDER_ENDERMANCOUNT; i++) {
            EnderMan entity = new EnderMan(EntityType.ENDERMAN, world);
            entity.setPos(
                    //
                    position.getX() + world.random.nextIntBetweenInclusive((int) -BallistixConstants.EXPLOSIVE_ENDER_RADIUS, (int) BallistixConstants.EXPLOSIVE_ENDER_RADIUS),
                    //
                    position.getY() + world.random.nextIntBetweenInclusive((int) -BallistixConstants.EXPLOSIVE_ENDER_RADIUS, (int) BallistixConstants.EXPLOSIVE_ENDER_RADIUS),
                    //
                    position.getZ() + world.random.nextIntBetweenInclusive((int) -BallistixConstants.EXPLOSIVE_ENDER_RADIUS, (int) BallistixConstants.EXPLOSIVE_ENDER_RADIUS)
            //
            );
            world.addFreshEntity(entity);
        }

        world.explode(null, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, (float) BallistixConstants.EXPLOSIVE_ENDER_RADIUS, BlockInteraction.DESTROY);

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

        int x0 = Mth.floor(x - f2 - 1.0D);
        int x1 = Mth.floor(x + f2 + 1.0D);
        int y0 = Mth.floor(y - f2 - 1.0D);
        int y1 = Mth.floor(y + f2 + 1.0D);
        int z0 = Mth.floor(z - f2 - 1.0D);
        int z1 = Mth.floor(z + f2 + 1.0D);
        for (int dx = x0; dx < x1; dx++) {
            for (int dy = y0; dy < y1; dy++) {
                for (int dz = z0; dz < z1; dz++) {
                    if ((x - dx) * (x - dx) + (y - dy) * (y - dy) + (z - dz) * (z - dz) <= (2 * size + 1) * (2 * size + 1)) {
                        if (world.random.nextFloat() < 1 / 40.0) {
                            ParticleOptions particle = new ParticleOptionsShockwave().setParameters(0.933F, 0.212F, 0.933F, 1, (float) 0.1, 15, false, 1);
                            Minecraft.getInstance().particleEngine.createParticle(particle, dx, dy, dz, (x - dx) / 15.0, (y - dy) / 15.0, (z - dz) / 15.0);
                        }
                    }
                }
            }
        }
    }
    
    private static final class DimensionManager implements ITeleporter {

		@Override
		public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
			return repositionEntity.apply(false);
		}

		@Override
		public @Nullable PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
			return new PortalInfo(entity.position(), Vec3.ZERO, entity.getYRot(), entity.getXRot());
		}

		@Override
		public boolean isVanilla() {
			return false;
		}

		@Override
		public boolean playTeleportSound(ServerPlayer player, ServerLevel sourceWorld, ServerLevel destWorld) {
			return false;
		}

	}

}
