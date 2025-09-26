package ballistix.api.blast;

import ballistix.common.blast.util.Blast;

public interface IHasCustomRender {

    default boolean shouldRender() {
        if (this instanceof Blast) {
            return !((Blast) this).isInstantaneous();
        }
        return false;
    }

    void produceParticles();
}
