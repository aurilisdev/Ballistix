package ballistix.common.blast.util;

import javax.annotation.Nullable;

import ballistix.client.effect.BlastEffects;
import ballistix.client.effect.BlastEffects.Style;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public abstract class BlastLasting extends Blast {

    protected BlastLasting(Level world, BlockPos position, @Nullable Entity owner, @Nullable Entity blastEntity) {
	super(world, position, owner, blastEntity);
    }

    public int ticksSinceBlastStart;
    private boolean playedDetonationEffects;

    @OnlyIn(Dist.CLIENT)
    protected void playDetonationEffects(Style style, double radius) {
	if (playedDetonationEffects || ticksSinceBlastStart < 1 || ticksSinceBlastStart > 24)
	    return;
	playedDetonationEffects = true;
	BlastEffects.startFromEntity((ClientLevel) world, Vec3.atCenterOf(position), style, radius);
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
