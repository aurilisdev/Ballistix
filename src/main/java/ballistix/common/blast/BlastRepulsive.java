package ballistix.common.blast;

import java.util.ArrayList;
import java.util.List;

import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BlastRepulsive extends Blast {

	public BlastRepulsive(Level world, BlockPos position) {
		super(world, position);
	}

	@Override
	public boolean doExplode(int callCount) {
		hasStarted = true;
		if (!world.isClientSide) {
			world.explode(null, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, (float) Constants.EXPLOSIVE_REPULSIVE_SIZE, BlockInteraction.BREAK);
		}
		float x = position.getX();
		float y = position.getY();
		float z = position.getZ();
		float size = 5f;
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
				double d11 = Constants.EXPLOSIVE_ATTRACTIVE_REPULSIVE_PUSH_STRENGTH;
				entity.setDeltaMovement(entity.getDeltaMovement().add(d5 * d11, d7 * d11, d9 * d11));
				if (entity instanceof ServerPlayer serverplayerentity) {
					serverplayerentity.connection.send(new ClientboundExplodePacket(x, y, z, size, new ArrayList<>(), new Vec3(d5 * d11, d7 * d11, d9 * d11)));
				}
			}
		}
		return true;
	}

	@Override
	public SubtypeBlast getBlastType() {
		return SubtypeBlast.repulsive;
	}

}
