package ballistix.client.effect;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/** One running blast: its phases advancing together from the moment of detonation. */
final class BlastSequence {

    private final BlastScene scene;
    private final CraterGround craterGround;
    private final List<BlastPhase> phases = new ArrayList<>();
    private int age;

    BlastSequence(BlastScene scene, boolean serverReportsCraterFloors) {
	this.scene = scene;
	craterGround = new CraterGround(scene, serverReportsCraterFloors && scene.style.hasAftermath());

	// Phases overlap: they all see the same age, and emit in this order each tick.
	phases.add(new DetonationPhase(scene));
	phases.add(new PressureFrontPhase(scene));
	if (scene.style == BlastStyle.NUCLEAR) {
	    phases.add(new MushroomCloudPhase.GroundHaze(scene, craterGround));
	    phases.add(new MushroomCloudPhase(scene, craterGround));
	} else if (scene.style.isAntimatter()) {
	    AntimatterCloudPhase cloud = new AntimatterCloudPhase(scene);
	    phases.add(new CraterPhase.Dust(scene, craterGround, cloud));
	    phases.add(cloud);
	} else if (scene.timeline.craterLingerTicks() > 0) {
	    phases.add(new CraterPhase.Dust(scene, craterGround, null));
	}
    }

    BlastScene scene() {
	return scene;
    }

    int age() {
	return age;
    }

    boolean isIn(@Nullable ClientLevel level) {
	return scene.level == level;
    }

    boolean matches(ClientLevel level, Vec3 origin, BlastStyle style) {
	return isIn(level) && scene.origin.equals(origin) && scene.style == style;
    }

    boolean acceptsCraterFloors() {
	return craterGround.isServerReported();
    }

    void reportCraterFloors(List<BlockPos> floors) {
	craterGround.report(floors, age);
    }

    /** Advances every phase by one tick and returns whether the sequence has finished. */
    boolean tick() {
	for (BlastPhase phase : phases)
	    phase.tick(age);
	return ++age > lastTick();
    }

    private int lastTick() {
	int last = 0;
	for (BlastPhase phase : phases)
	    last = Math.max(last, phase.endTick());
	return last;
    }
}
