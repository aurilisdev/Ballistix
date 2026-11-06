package ballistix.common.blast.util;

import javax.annotation.Nullable;

import ballistix.prefab.utils.ParticleUtilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public abstract class BlastLasting extends Blast {

    protected BlastLasting(Level world, BlockPos position, @Nullable Entity owner, @Nullable Entity blastEntity) {
	super(world, position, owner, blastEntity);
    }

    public int ticksSinceBlastStart;
    private boolean playedDetonationBurst;

    @OnlyIn(Dist.CLIENT)
    protected void playDetonationBurst(double x, double y, double z, float sparkSpread, ParticleOptions spark) {
	if (playedDetonationBurst || ticksSinceBlastStart < 1 || ticksSinceBlastStart > 10)
	    return;
	playedDetonationBurst = true;
	ParticleUtilities.spawnDetonationBurst(x, y, z, sparkSpread, spark);
    }

    @Override
    public boolean doExplode(int callCount) {
	if (isDoneCalculating()) {
	    ticksSinceBlastStart++;
	}
	return super.doExplode(callCount);
    }

    public abstract boolean isDoneCalculating();

}
