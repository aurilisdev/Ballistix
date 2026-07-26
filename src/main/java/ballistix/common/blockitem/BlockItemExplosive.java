package ballistix.common.blockitem;

import java.util.function.Supplier;

import ballistix.api.blast.IBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.entity.EntityExplosive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import voltaic.common.blockitem.BlockItemDescriptable;

public class BlockItemExplosive extends BlockItemDescriptable {

    private final IBlast blast;

    public BlockItemExplosive(IBlast blast, Block block, Properties properties, Supplier<CreativeModeTab> creativeTab) {
	super(block, properties, creativeTab);
	this.blast = blast;
	if (blast != SubtypeBlast.landmine) {
	    DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
	}
    }

    public static final DispenseItemBehavior DISPENSER_BEHAVIOR = new DispenseItemBehavior() {
	@Override
	public ItemStack dispense(BlockSource source, ItemStack item) {

	    Level level = source.getLevel();
	    Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
	    BlockPos pos = source.getPos().relative(direction);

	    if (item.getItem() instanceof BlockItemExplosive explosive && !source.getLevel().isClientSide) {

		EntityExplosive explosiveEntity = new EntityExplosive(level, pos.getX() + 0.5D, pos.getY(),
			pos.getZ() + 0.5D, null);
		explosiveEntity.setBlastType(explosive.blast);
		level.addFreshEntity(explosiveEntity);
		level.playSound((Player) null, explosiveEntity.getX(), explosiveEntity.getY(), explosiveEntity.getZ(),
			SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);

	    }
	    item.shrink(1);

	    return item;
	}

    };

}
