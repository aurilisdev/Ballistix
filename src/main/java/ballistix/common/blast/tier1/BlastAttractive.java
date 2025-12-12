package ballistix.common.blast.tier1;

import java.util.ArrayList;
import java.util.List;

import ballistix.api.blast.IHasCustomRender;
import ballistix.client.particle.ParticleOptionsShockwave;
import ballistix.common.blast.util.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class BlastAttractive extends Blast implements IHasCustomRender {

    public BlastAttractive(Level world, BlockPos position, Entity owner) {
	super(world, position, owner);
    }

    @Override
    public boolean doExplode(int callCount) {
	super.doExplode(callCount);

	hasStarted = true;

	if (!world.isClientSide) {

	    world.explode(null, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5,
		    (float) BallistixConfig.INSTANCE.EXPLOSIVE_ATTRACTIVE_SIZE.getAsDouble(),
		    ExplosionInteraction.BLOCK);

	} else {
	    produceParticles();
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

	List<Entity> entities = world.getEntities(null, new AABB(x0, y0, z0, x1, y1, z1));

	for (Entity entity : entities) {

	    if (!canHarmEntity(entity)) {
		continue;
	    }
	    double deltaX = entity.getX() - x;
	    double deltaY = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - y;
	    double deltaZ = entity.getZ() - z;
	    double deltaDistance = Mth.sqrt((float) (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ));
	    if (deltaDistance == 0.0F) {
		continue;
	    }
	    deltaX = deltaX / deltaDistance;
	    deltaY = deltaY / deltaDistance;
	    deltaZ = deltaZ / deltaDistance;
	    double d11 = -BallistixConfig.INSTANCE.EXPLOSIVE_ATTRACTIVE_REPULSIVE_PUSH_STRENGTH.getAsDouble();
	    entity.setDeltaMovement(entity.getDeltaMovement().add(deltaX * d11, deltaY * d11, deltaZ * d11));
	    if (entity instanceof ServerPlayer serverplayerentity) {
		serverplayerentity.connection.send(new ClientboundExplodePacket(x, y, z, size, new ArrayList<>(),
			new Vec3(deltaX * d11, deltaY * d11, deltaZ * d11), Explosion.BlockInteraction.DESTROY,
			ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE));
	    }
	}
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
		    if ((x - dx) * (x - dx) + (y - dy) * (y - dy) + (z - dz) * (z - dz) <= (2 * size + 1)
			    * (2 * size + 1)) {
			if (world.random.nextFloat() < 1 / 40.0) {
			    ParticleOptions particle = new ParticleOptionsShockwave().setParameters(1, 1, 1, 1,
				    (float) 0.3, 15, false, 1);
			    Minecraft.getInstance().particleEngine.createParticle(particle, dx, dy, dz, (x - dx) / 15.0,
				    (y - dy) / 15.0, (z - dz) / 15.0);
			}
		    }
		}
	    }
	}
    }

    @Override
    public SubtypeBlast getBlastType() {
	return SubtypeBlast.attractive;
    }

}
