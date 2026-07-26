package ballistix.api.entity;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;

public interface ITraceableEntity {

    @Nullable
    Entity getOwner();
}