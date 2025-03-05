package ballistix.common.tile;

import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.api.silo.ILauncherControlPanel;
import ballistix.api.silo.ILauncherPlatform;
import ballistix.common.block.BlockExplosive;
import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.inventory.container.ContainerLauncherPlatformT1;
import ballistix.common.inventory.container.ContainerLauncherPlatformT2;
import ballistix.common.inventory.container.ContainerLauncherPlatformT3;
import ballistix.common.item.ItemMissile;
import ballistix.common.settings.Constants;
import ballistix.registers.BallistixSounds;
import ballistix.registers.BallistixTiles;
import electrodynamics.api.multiblock.subnodebased.parent.IMultiblockParentBlock;
import electrodynamics.api.multiblock.subnodebased.parent.IMultiblockParentTile;
import electrodynamics.common.blockitem.types.BlockItemDescriptable;
import electrodynamics.common.tile.TileMultiSubnode;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyTypes;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentElectrodynamic;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentInventory.InventoryBuilder;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import electrodynamics.prefab.utilities.BlockEntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class TileLauncherPlatformT1 extends GenericTile implements ILauncherPlatform, IMultiblockParentTile {

	public static final int MISSILE_SLOT = 0;
	public static final int EXPLOSIVE_SLOT = 1;

	public Property<Boolean> hasExplosive = property(new Property<>(PropertyTypes.BOOLEAN, "hasexplosive", false));

	public TileLauncherPlatformT1(BlockPos pos, BlockState state) {
		this(BallistixTiles.TILE_LAUNCHER_PLATFORM_TIER1.get(), pos, state);
	}

	public TileLauncherPlatformT1(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		int tier = getTier();
		addComponent(new ComponentTickable(this));
		addComponent(new ComponentElectrodynamic(this, false, true).voltage(120 * tier).maxJoules(Constants.MISSILESILO_USAGE * 20 * tier).setInputDirections(BlockEntityUtils.MachineDirection.values()));
		addComponent(new ComponentInventory(this, InventoryBuilder.newInv().inputs(2)).setDirectionsBySlot(0, BlockEntityUtils.MachineDirection.values()).setDirectionsBySlot(1, BlockEntityUtils.MachineDirection.values()).valid(this::isItemValidForSlot));
		addComponent(new ComponentPacketHandler(this));
		if (tier == 1) {
			addComponent(new ComponentContainerProvider("container.launcherplatformtier" + tier, this).createMenu((id, player) -> new ContainerLauncherPlatformT1(id, player, getComponent(IComponentType.Inventory), getCoordsArray())));
		} else if (tier == 2) {
			addComponent(new ComponentContainerProvider("container.launcherplatformtier" + tier, this).createMenu((id, player) -> new ContainerLauncherPlatformT2(id, player, getComponent(IComponentType.Inventory), getCoordsArray())));
		} else if (tier == 3) {
			addComponent(new ComponentContainerProvider("container.launcherplatformtier" + tier, this).createMenu((id, player) -> new ContainerLauncherPlatformT3(id, player, getComponent(IComponentType.Inventory), getCoordsArray())));
		}

	}

	@Override
	public int getRange() {
		return 500;
	}

	@Override
	public int getTier() {
		return 1;
	}

	@Override
	public void launch(ILauncherControlPanel controlPanel) {
		ComponentInventory inv = getComponent(IComponentType.Inventory);
		ItemStack explosive = inv.getItem(EXPLOSIVE_SLOT);
		ItemStack mis = inv.getItem(MISSILE_SLOT);

		int ordinal = ((ItemMissile) mis.getItem()).missile.ordinal();

		VirtualMissile missile = new VirtualMissile(
				//
				new Vec3(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5),
				//
				new Vec3(0, 1, 0),
				//
				0.0F,
				//
				false,
				//
				getBlockPos().getX() + 0.5F,
				//
				getBlockPos().getZ() + 0.5F,
				//
				controlPanel.getTarget(),
				//
				ordinal,
				//
				((BlockExplosive) ((BlockItemDescriptable) explosive.getItem()).getBlock()).explosive.ordinal(),
				//
				false,
				//
				controlPanel.getFrequency()
		//
		);

		MissileManager.addMissile(level.dimension(), missile);

		inv.removeItem(MISSILE_SLOT, 1);
		inv.removeItem(EXPLOSIVE_SLOT, 1);

		level.playSound(null, getBlockPos(), BallistixSounds.SOUND_MISSILE_SILO.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

	}

	protected boolean isItemValidForSlot(int index, ItemStack stack, ComponentInventory inv) {
		Item item = stack.getItem();
		if (index == 0) {
			return item instanceof ItemMissile missile && missile.missile.tier <= getTier();
		} else if (index == 1) {
			return item instanceof BlockItemDescriptable des && des.getBlock() instanceof BlockExplosive expl && expl.explosive.tier <= getTier() && expl.explosive.tier > -1;
		}
		return false;
	}

	@Override
	public void onInventoryChange(ComponentInventory inv, int index) {
		handleExplosive(inv, index);
	}

	private void handleExplosive(ComponentInventory inv, int index) {
		if (index == 1 || index == -1) {
			ItemStack explosive = inv.getItem(1);

			if (!explosive.isEmpty() && explosive.getItem() instanceof BlockItemDescriptable blockItem && blockItem.getBlock() instanceof BlockExplosive) {
				hasExplosive.set(true);
			} else {
				hasExplosive.set(false);
			}

		}
	}

	@Override
	public boolean hasExplosive() {
		return hasExplosive.get();
	}

	@Override
	public IMultiblockParentBlock.SubnodeWrapper getSubNodes() {
		return SubtypeBallistixMachine.Subnodes.LAUNCHER_PLATFORM_TIER1;
	}

	@Override
	public void onSubnodeDestroyed(TileMultiSubnode tileMultiSubnode) {
		level.destroyBlock(worldPosition, true);
	}

	@Override
	public Direction getFacingDirection() {
		return getFacing();
	}

}
