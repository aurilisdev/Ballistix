package ballistix.compatibility.griefdefender;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

public class GriefDefenderHandler {

    public static boolean shouldHarmBlock(BlockPos pos) {
	Claim claim = GriefDefender.getCore().getClaimAt(pos);
	return claim == null || claim.isWilderness();
    }

    public static boolean shouldEntityBeHarmed(Entity entity) {
	return shouldHarmBlock(entity.blockPosition());
    }

    public static boolean shouldAddParticle(BlockPos pos) {
	return shouldHarmBlock(pos);
    }

}
