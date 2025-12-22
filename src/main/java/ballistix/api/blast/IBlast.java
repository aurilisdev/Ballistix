package ballistix.api.blast;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import ballistix.common.blast.util.Blast;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import voltaic.common.block.voxelshapes.VoxelShapeProvider;

public interface IBlast {

    /**
     * Returns how long in ticks until this Blast will detonate
     * 
     * @return
     */
    int fuse();

    /**
     * Creates a new instance of this Blast
     * 
     * @param world the level this Blast is created in
     * @param pos   The center of this Blast
     * @param owner   The causing entity of this blast. Can be null
     * @param blastEntity   The entity exploding in this blast. Can be null
     * @return a new Blast instance
     */
    Blast createBlast(Level world, BlockPos pos, @Nullable Entity owner, @Nullable Entity blastEntity);

    /**
     * returns the tier of this Blast (higher = more advanced and requires higher
     * tiered missiles)
     * 
     * @return
     */
    int tier();

    /**
     * Returns the shape of the block of this Blast
     *
     * @return
     */
    VoxelShapeProvider getShape();

    /**
     * Returns the id of this Blast
     * 
     * @return
     */
    ResourceLocation id();

    /**
     * Returns the explosive item of this blast
     * 
     * @return
     */
    Supplier<Item> getExplosiveItem();

    /**
     * Returns the explosive block of this blast
     * 
     * @return
     */
    Supplier<Block> getExplosiveBlock();

    /**
     * What this blast will do when an entity walks inside the explosive
     * 
     * @param state
     * @param level
     * @param pos
     * @param ent
     */
    void onEntityInside(BlockState state, Level level, BlockPos pos, Entity ent);

}
