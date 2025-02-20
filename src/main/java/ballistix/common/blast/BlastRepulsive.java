package ballistix.common.blast;

import java.util.ArrayList;
import java.util.List;

import ballistix.api.blast.IHasCustomRender;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.Constants;
import ballistix.compatibility.griefdefender.GriefDefenderHandler;
import net.minecraft.core.BlockPos;
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

public class BlastRepulsive extends Blast implements IHasCustomRender {

    public BlastRepulsive(Level world, BlockPos position) {
	super(world, position);
    }

    @Override
    public boolean doExplode(int callCount) {
	super.doExplode(callCount);
	hasStarted = true;
	if (!world.isClientSide) {
	    world.explode(null, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5,
		    (float) Constants.EXPLOSIVE_REPULSIVE_SIZE, ExplosionInteraction.BLOCK);
	} else {
	    produceParticles();
	}
	float x = position.getX();
	float y = position.getY();
	float z = position.getZ();

	float size = 5f;
	float doubleSize = size * 2.0F;

	int x0 = Mth.floor(x - (double) doubleSize - 1.0D);
	int x1 = Mth.floor(x + (double) doubleSize + 1.0D);
	int y0 = Mth.floor(y - (double) doubleSize - 1.0D);
	int y1 = Mth.floor(y + (double) doubleSize + 1.0D);
	int z0 = Mth.floor(z - (double) doubleSize - 1.0D);
	int z1 = Mth.floor(z + (double) doubleSize + 1.0D);

	List<Entity> entities = world.getEntities(null, new AABB(x0, y0, z0, x1, y1, z1));

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
	    double deltaY = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - y;
	    double deltaZ = entity.getZ() - z;
	    double deltaDistance = Mth.sqrt((float) (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ));
	    if (deltaDistance == 0.0D) {
		continue;
	    }
	    deltaX = deltaX / deltaDistance;
	    deltaY = deltaY / deltaDistance;
	    deltaZ = deltaZ / deltaDistance;
	    double d11 = Constants.EXPLOSIVE_ATTRACTIVE_REPULSIVE_PUSH_STRENGTH;
	    entity.setDeltaMovement(entity.getDeltaMovement().add(deltaX * d11, deltaY * d11, deltaZ * d11));
	    if (entity instanceof ServerPlayer serverplayerentity) {
		serverplayerentity.connection.send(new ClientboundExplodePacket(x, y, z, size, new ArrayList<>(),
			new Vec3(deltaX * d11, deltaY * d11, deltaZ * d11), Explosion.BlockInteraction.DESTROY,
			ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE));
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
    }

}
