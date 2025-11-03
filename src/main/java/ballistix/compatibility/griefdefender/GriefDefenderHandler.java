package ballistix.compatibility.griefdefender;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class GriefDefenderHandler {

    public static void destroyBlock(Block block, Explosion explosion, BlockPos pos, Level world) {

        Claim claim = GriefDefender.getCore().getClaimAt(pos);

        if (claim == null || claim.isWilderness()) {
            return;
        }

        block.wasExploded(world, pos, explosion);
        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

    }

    public static boolean shouldEntityBeHarmed(Entity entity) {

        Claim claim = GriefDefender.getCore().getClaimAt(entity.getOnPos());

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
