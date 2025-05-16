package ballistix.compatibility.griefdefender;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class GriefDefenderHandler {

	public static void destroyBlock(Block block, Explosion explosion, BlockPos pos, World world) {

		Claim claim = GriefDefender.getCore().getClaimAt(pos);

		if (claim == null || claim.isWilderness()) {
			return;
		}

		block.wasExploded(world, pos, explosion);
		world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

	}

	public static boolean shouldEntityBeHarmed(Entity entity) {

		Claim claim = GriefDefender.getCore().getClaimAt(entity.blockPosition());

		return claim == null || !claim.isWilderness();
	}

	public static boolean shouldAddParticle(BlockPos pos) {

		Claim claim = GriefDefender.getCore().getClaimAt(pos);

		return claim == null || !claim.isWilderness();

	}

	public static boolean shouldHarmBlock(BlockPos pos) {

		Claim claim = GriefDefender.getCore().getClaimAt(pos);

		return claim == null || !claim.isWilderness();

	}

}
