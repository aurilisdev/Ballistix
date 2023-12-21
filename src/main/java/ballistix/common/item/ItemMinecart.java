package ballistix.common.item;

import ballistix.References;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.entity.EntityMinecart;
import electrodynamics.api.ISubtype;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gameevent.GameEvent;

public class ItemMinecart extends Item {

	private SubtypeMinecart minecart;

	public ItemMinecart(SubtypeMinecart minecart) {
		super(new Item.Properties().tab(References.BALLISTIXTAB).stacksTo(1));
		this.minecart = minecart;
		DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos blockpos = context.getClickedPos();
		BlockState blockstate = level.getBlockState(blockpos);
		if (!blockstate.is(BlockTags.RAILS)) {
			return InteractionResult.FAIL;
		}
		ItemStack itemstack = context.getItemInHand();
		if (!level.isClientSide) {
			RailShape railshape = blockstate.getBlock() instanceof BaseRailBlock rail ? rail.getRailDirection(blockstate, level, blockpos, null) : RailShape.NORTH_SOUTH;
			double d0 = 0.0D;
			if (railshape.isAscending()) {
				d0 = 0.5D;
			}

			EntityMinecart cart = new EntityMinecart(level);
			cart.setPos(blockpos.getX() + 0.5D, blockpos.getY() + 0.0625D + d0, blockpos.getZ() + 0.5D);
			if (itemstack.hasCustomHoverName()) {
				cart.setCustomName(itemstack.getHoverName());
			}
			cart.setExplosiveType(minecart);

			level.addFreshEntity(cart);
			level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);
		}

		itemstack.shrink(1);
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	public SubtypeBlast getExplosive() {
		return minecart.explosiveType;
	}

	private static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
		private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

		@Override
		public ItemStack execute(BlockSource source, ItemStack stack) {
			Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
			Level level = source.getLevel();
			double d0 = source.x() + direction.getStepX() * 1.125D;
			double d1 = Math.floor(source.y()) + direction.getStepY();
			double d2 = source.z() + direction.getStepZ() * 1.125D;
			BlockPos blockpos = source.getPos().relative(direction);
			BlockState blockstate = level.getBlockState(blockpos);
			RailShape railshape = blockstate.getBlock() instanceof BaseRailBlock rail ? rail.getRailDirection(blockstate, level, blockpos, null) : RailShape.NORTH_SOUTH;
			double d3;
			if (blockstate.is(BlockTags.RAILS)) {
				if (railshape.isAscending()) {
					d3 = 0.6D;
				} else {
					d3 = 0.1D;
				}
			} else {
				if (!blockstate.isAir() || !level.getBlockState(blockpos.below()).is(BlockTags.RAILS)) {
					return defaultDispenseItemBehavior.dispense(source, stack);
				}

				BlockState blockstate1 = level.getBlockState(blockpos.below());
				RailShape railshape1 = blockstate1.getBlock() instanceof BaseRailBlock rail ? blockstate1.getValue(rail.getShapeProperty()) : RailShape.NORTH_SOUTH;
				if (direction != Direction.DOWN && railshape1.isAscending()) {
					d3 = -0.4D;
				} else {
					d3 = -0.9D;
				}
			}

			EntityMinecart cart = new EntityMinecart(level);
			cart.setPos(d0, d1 + d3, d2);
			if (stack.hasCustomHoverName()) {
				cart.setCustomName(stack.getHoverName());
			}
			cart.setExplosiveType(((ItemMinecart) stack.getItem()).minecart);
			level.addFreshEntity(cart);
			stack.shrink(1);
			return stack;
		}

		@Override
		protected void playSound(BlockSource source) {
			source.getLevel().levelEvent(1000, source.getPos(), 0);
		}
	};

	public enum SubtypeMinecart implements ISubtype {
		obsidian(SubtypeBlast.obsidian),
		condensive(SubtypeBlast.condensive),
		attractive(SubtypeBlast.attractive),
		repulsive(SubtypeBlast.repulsive),
		incendiary(SubtypeBlast.incendiary),
		shrapnel(SubtypeBlast.shrapnel),
		debilitation(SubtypeBlast.debilitation),
		chemical(SubtypeBlast.chemical),
		emp(SubtypeBlast.emp),
		breaching(SubtypeBlast.breaching),
		thermobaric(SubtypeBlast.thermobaric),
		contagious(SubtypeBlast.contagious),
		fragmentation(SubtypeBlast.fragmentation),
		nuclear(SubtypeBlast.nuclear),
		antimatter(SubtypeBlast.antimatter),
		largeantimatter(SubtypeBlast.largeantimatter),
		darkmatter(SubtypeBlast.darkmatter);

		public final SubtypeBlast explosiveType;

		SubtypeMinecart(SubtypeBlast explosive) {
			explosiveType = explosive;
		}

		@Override
		public String forgeTag() {
			return "explosive_minecarts/" + name();
		}

		@Override
		public boolean isItem() {
			return true;
		}

		@Override
		public String tag() {
			return "minecart" + name();
		}

	}

}