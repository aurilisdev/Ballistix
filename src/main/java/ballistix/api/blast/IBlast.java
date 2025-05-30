package ballistix.api.blast;

import ballistix.common.blast.Blast;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import voltaic.common.block.voxelshapes.VoxelShapeProvider;

import java.util.function.Supplier;

/**
 * @author Skip999
 */
public interface IBlast {

    /**
     * Returns how long in ticks until this Blast will detonate
     * @return
     */
    int fuse();

    /**
     * Creates a new instance of this Blast
     * @param world the level this Blast is created in
     * @param pos The center of this Blast
     * @return a new Blast instance
     */
    Blast createBlast(World world, BlockPos pos);

    /**
     * returns the tier of this Blast (higher = more advanced and requires higher tiered missiles)
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
     * @return
     */
    ResourceLocation id();

    /**
     * Returns the explosive item of this blast
     * @return
     */
    Supplier<Item> getExplosiveItem();

    /**
     * Returns the explosive block of this blast
     * @return
     */
    Supplier<Block> getExplosiveBlock();

    /**
     * What this blast will do when an entity walks inside the explosive
     * @param state
     * @param level
     * @param pos
     * @param ent
     */
    void onEntityInside(BlockState state, World level, BlockPos pos, Entity ent);

}
