package ballistix.api.blast;

import ballistix.common.blast.util.Blast;
import ballistix.common.blast.util.BlastLasting;

public interface IHasCustomRender {

    default boolean shouldRender() {
        if (this instanceof Blast bl) {
            return bl.isInstantaneous() ? false : bl instanceof BlastLasting bll ? bll.isDoneCalculating() : false;
        }
        return false;
    }

    void produceParticles();
}
