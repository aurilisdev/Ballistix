package ballistix.common.block;

import javax.annotation.Nullable;

import ballistix.common.entity.EntityExplosive;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockExplosive extends Block {
	private SubtypeExplosive explosive;

	public BlockExplosive(SubtypeExplosive explosive) {
		super(AbstractBlock.Properties.create(Material.TNT).zeroHardnessAndResistance().sound(SoundType.PLANT));
		this.explosive = explosive;
	}

	@Override
	public void catchFire(BlockState state, World world, BlockPos pos, @Nullable net.minecraft.util.Direction face, @Nullable LivingEntity igniter) {
		explode(world, pos, explosive);
	}

	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (!oldState.isIn(state.getBlock())) {
			if (worldIn.isBlockPowered(pos)) {
				catchFire(state, worldIn, pos, null, null);
				worldIn.removeBlock(pos, false);
			}
		}
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (worldIn.isBlockPowered(pos)) {
			catchFire(state, worldIn, pos, null, null);
			worldIn.removeBlock(pos, false);
		}

	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!worldIn.isRemote() && !player.isCreative()) {
			catchFire(state, worldIn, pos, null, null);
		}
		super.onBlockHarvested(worldIn, pos, state, player);
	}

	@Override
	public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
		if (!worldIn.isRemote) {
			explode(worldIn, pos, explosive);
		}
	}

	private static void explode(World worldIn, BlockPos pos, SubtypeExplosive explosive) {
		if (!worldIn.isRemote) {
			EntityExplosive explosiveEntity = new EntityExplosive(worldIn, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
			explosiveEntity.setExplosiveType(explosive);
			worldIn.addEntity(explosiveEntity);
			worldIn.playSound((PlayerEntity) null, explosiveEntity.getPosX(), explosiveEntity.getPosY(), explosiveEntity.getPosZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		ItemStack itemstack = player.getHeldItem(handIn);
		Item item = itemstack.getItem();
		if (item != Items.FLINT_AND_STEEL && item != Items.FIRE_CHARGE) {
			return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
		} else {
			catchFire(state, worldIn, pos, hit.getFace(), player);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
			if (!player.isCreative()) {
				if (item == Items.FLINT_AND_STEEL) {
					itemstack.damageItem(1, player, (player1) -> {
						player1.sendBreakAnimation(handIn);
					});
				} else {
					itemstack.shrink(1);
				}
			}

			return ActionResultType.func_233537_a_(worldIn.isRemote);
		}
	}

	@Override
	public void onProjectileCollision(World worldIn, BlockState state, BlockRayTraceResult hit, ProjectileEntity projectile) {
		if (!worldIn.isRemote) {
			Entity entity = projectile.func_234616_v_();
			if (projectile.isBurning()) {
				BlockPos blockpos = hit.getPos();
				catchFire(state, worldIn, blockpos, null, entity instanceof LivingEntity ? (LivingEntity) entity : null);
				worldIn.removeBlock(blockpos, false);
			}
		}

	}

	@Override
	public boolean canDropFromExplosion(Explosion explosionIn) {
		return false;
	}
}
