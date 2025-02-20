package ballistix.common.blast;

import ballistix.api.blast.IHasCustomRender;
import ballistix.client.particle.ParticleOptionBlastSmoke;
import ballistix.client.render.ShakeManager;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class BlastBreaching extends Blast implements IHasCustomRender {

    public BlastBreaching(Level world, BlockPos position) {
	super(world, position);
    }

    @Override
    public boolean doExplode(int callCount) {
	super.doExplode(callCount);
	hasStarted = true;
	if (!world.isClientSide) {
	    world.explode(null, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5,
		    (float) Constants.EXPLOSIVE_BREACHING_SIZE, ExplosionInteraction.BLOCK);
	}
	return true;
    }

    @Override
    public SubtypeBlast getBlastType() {
	return SubtypeBlast.breaching;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void produceParticles() {
	ShakeManager.startShake(1, 20);
	RandomSource random = world.random;

	double x = position.getX() + 0.5;
	double y = position.getY() - 2;
	double z = position.getZ() + 0.5;

	double centerX = x;
	double centerY = y;
	double centerZ = z;

	double initialSpeed = 0.8;
//	// Shockwave
//	initialSpeed = 1; // Increase/decrease to taste
//	ParticleOptions particle = new ParticleOptionBlastSmoke().setParameters(1.5f, 1.5f, 1.5f, 0.5f, 0, 150, false,
//		false, 1);
//	BlastThermobaric.spawnSurroundingParticles(particle, random, centerX, centerY + 6, centerZ, 100, 0, 0,
//		initialSpeed, false);
	initialSpeed = 0.4;
	// Fireball
	ParticleOptions particle = new ParticleOptionBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 1f, -0.045f, 200, true, true, 20, 0.95);
	BlastThermobaric.spawnSurroundingParticles(particle, random, centerX, centerY, centerZ, 100, 40, 90,
		initialSpeed, true);

	// Centersmokes
	initialSpeed = 0.4; // Increase/decrease to taste
	particle = new ParticleOptionBlastSmoke().setParameters(1.0f, 1.0f, 1.0f,1f, 0.033f, 200, true, 0.95);
	BlastThermobaric.spawnSurroundingParticles(particle, random, centerX, centerY, centerZ, 100, 0, 20,
		initialSpeed, true);

	// Centersmokes
	initialSpeed = 0.4; // Increase/decrease to taste
	particle = new ParticleOptionBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 1f, -0.033f, 200, true,
		0.95);
	BlastThermobaric.spawnSurroundingParticles(particle, random, centerX, centerY, centerZ, 100, 0, 20,
		initialSpeed, true);

    }
}
