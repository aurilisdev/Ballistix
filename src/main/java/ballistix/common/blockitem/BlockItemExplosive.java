package ballistix.common.blockitem;

import java.util.function.Supplier;

import ballistix.api.blast.IBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.entity.EntityExplosive;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import voltaic.common.blockitem.BlockItemDescriptable;

public class BlockItemExplosive extends BlockItemDescriptable {

    private final IBlast blast;

    public BlockItemExplosive(IBlast blast, Block block, Properties properties, Supplier<ItemGroup> creativeTab) {
        super(block, properties, creativeTab);
        this.blast = blast;
        if(blast != SubtypeBlast.landmine) {
            DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
        }
    }


    public static final IDispenseItemBehavior DISPENSER_BEHAVIOR = new IDispenseItemBehavior() {
        @Override
        public ItemStack dispense(IBlockSource source, ItemStack item) {

            World level = source.getLevel();
            Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
            BlockPos pos = source.getPos().relative(direction);

            if(item.getItem() instanceof BlockItemExplosive && !source.getLevel().isClientSide) {
            	BlockItemExplosive explosive = (BlockItemExplosive) item.getItem();
                EntityExplosive explosiveEntity = new EntityExplosive(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                explosiveEntity.setBlastType(explosive.blast);
                level.addFreshEntity(explosiveEntity);
                level.playSound((PlayerEntity) null, explosiveEntity.getX(), explosiveEntity.getY(), explosiveEntity.getZ(), SoundEvents.TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);

            }


            return item;
        }

    };


}
