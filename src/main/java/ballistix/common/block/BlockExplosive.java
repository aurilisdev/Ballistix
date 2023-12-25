package ballistix.common.block;

import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.entity.EntityExplosive;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockExplosive extends Block {
	public final SubtypeBlast explosive;

	public BlockExplosive(SubtypeBlast explosive) {
		super(Properties.copy(Blocks.TNT).instabreak().sound(SoundType.GRASS).noOcclusion().isRedstoneConductor((a, b, c) -> false));
		this.explosive = explosive;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
		return explosive.shape.apply(state, level, pos, context);
	}

	@Override
	public void catchFire(BlockState state, World world, BlockPos pos, Direction face, LivingEntity igniter) {
		explode(world, pos, explosive);
	}

	@Override
	public void onPlace(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (!oldState.is(state.getBlock()) && worldIn.hasNeighborSignal(pos)) {
			catchFire(state, worldIn, pos, null, null);
			worldIn.removeBlock(pos, false);
		}
	}

	@Override
	public void entityInside(BlockState state, World level, BlockPos pos, Entity ent) {
		super.entityInside(state, level, pos, ent);
		if (explosive == SubtypeBlast.landmine) {
			explode(level, pos, explosive);
			level.removeBlock(pos, false);
		}
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (worldIn.hasNeighborSignal(pos)) {
			catchFire(state, worldIn, pos, null, null);
			worldIn.removeBlock(pos, false);
		}

	}

	@Override
	public void wasExploded(World worldIn, BlockPos pos, Explosion explosionIn) {
		if (!worldIn.isClientSide) {
			explode(worldIn, pos, explosive);
		}
	}

	private static void explode(World worldIn, BlockPos pos, SubtypeBlast explosive) {
		if (!worldIn.isClientSide) {
			EntityExplosive explosiveEntity = new EntityExplosive(worldIn, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
			explosiveEntity.setBlastType(explosive);
			worldIn.addFreshEntity(explosiveEntity);
			worldIn.playSound((PlayerEntity) null, explosiveEntity.getX(), explosiveEntity.getY(), explosiveEntity.getZ(), SoundEvents.TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		ItemStack itemstack = player.getItemInHand(handIn);
		Item item = itemstack.getItem();
		if (item != Items.FLINT_AND_STEEL && item != Items.FIRE_CHARGE) {
			return super.use(state, worldIn, pos, player, handIn, hit);
		}
		catchFire(state, worldIn, pos, hit.getDirection(), player);
		worldIn.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
		if (!player.isCreative()) {
			if (item == Items.FLINT_AND_STEEL) {
				itemstack.hurtAndBreak(1, player, player1 -> player1.broadcastBreakEvent(handIn));
			} else {
				itemstack.shrink(1);
			}
		}
		return ActionResultType.sidedSuccess(worldIn.isClientSide);
	}

	@Override
	public void onProjectileHit(World worldIn, BlockState state, BlockRayTraceResult hit, ProjectileEntity projectile) {
		if (!worldIn.isClientSide) {
			Entity entity = projectile.getOwner();
			if (projectile.isOnFire()) {
				BlockPos blockpos = hit.getBlockPos();
				catchFire(state, worldIn, blockpos, null, entity instanceof LivingEntity ? (LivingEntity) entity : null);
				worldIn.removeBlock(blockpos, false);
			}
		}
	}

	@Override
	public boolean dropFromExplosion(Explosion explosionIn) {
		return false;
	}
}