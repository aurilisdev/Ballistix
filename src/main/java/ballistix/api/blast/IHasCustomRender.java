package ballistix.api.blast;

import ballistix.common.blast.util.Blast;

public interface IHasCustomRender {

    default boolean shouldRender() {
        if (this instanceof Blast bl) {
            return !bl.isInstantaneous();
        }
        return false;
    }

    void produceParticles();
}
