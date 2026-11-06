package ballistix.client.effect;

import ballistix.client.effect.particle.BlastParticle;
import ballistix.client.effect.particle.ParticleKind;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

/** Base for the phases: exposes the shared scene's values and particle spawning under short names. */
abstract class BlastPhase {

    final BlastScene scene;
    final ClientLevel level;
    final Vec3 origin;
    final BlastStyle style;
    final BlastStyle.Palette palette;
    final BlastScene.Timeline timeline;
    final RandomSource random;
    final Vec3 wind;
    final float scale;
    final float core;
    final float detail;

    BlastPhase(BlastScene scene) {
	this.scene = scene;
	level = scene.level;
	origin = scene.origin;
	style = scene.style;
	palette = scene.style.palette;
	timeline = scene.timeline;
	random = scene.random;
	wind = scene.wind;
	scale = scene.scale;
	core = scene.core;
	detail = scene.detail;
    }

    /** Emits whatever this phase wants to show at the given age of the blast, in ticks. */
    abstract void tick(int age);

    /** The last age at which the phase may still emit. The sequence ends once every phase is past it. */
    abstract int endTick();

    final BlastParticle puff(ParticleKind kind, Vec3 pos, Vec3 velocity, float size, int life, int hotColor,
	    int coolColor) {
	return scene.puff(kind, pos, velocity, size, life, hotColor, coolColor);
    }

    final int count(int fullCount) {
	return scene.count(fullCount);
    }

    final int lifetime(int ticks) {
	return scene.lifetime(ticks);
    }

    final boolean hasFineDetail() {
	return scene.hasFineDetail();
    }
}
