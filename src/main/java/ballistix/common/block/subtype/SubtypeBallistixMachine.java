package ballistix.common.block.subtype;

import java.util.function.Supplier;

import ballistix.common.block.BallistixVoxelShapes;
import ballistix.common.tile.TileESMTower;
import ballistix.common.tile.TileLauncherControlPanelT1;
import ballistix.common.tile.TileLauncherControlPanelT2;
import ballistix.common.tile.TileLauncherControlPanelT3;
import ballistix.common.tile.radar.TileFireControlRadar;
import ballistix.common.tile.radar.TileSearchRadar;
import ballistix.common.tile.turret.antimissile.TileTurretCIWS;
import ballistix.common.tile.turret.antimissile.TileTurretLaser;
import ballistix.common.tile.turret.antimissile.TileTurretRailgun;
import ballistix.common.tile.turret.antimissile.TileTurretSAM;
import electrodynamics.api.ISubtype;
import electrodynamics.api.multiblock.subnodebased.Subnode;
import electrodynamics.api.multiblock.subnodebased.parent.IMultiblockParentBlock;
import electrodynamics.api.tile.IMachine;
import electrodynamics.api.tile.MachineProperties;
import electrodynamics.common.block.voxelshapes.VoxelShapeProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.shapes.Shapes;

public enum SubtypeBallistixMachine implements ISubtype, IMachine {

	launchercontrolpaneltier1(true, TileLauncherControlPanelT1::new, MachineProperties.builder().setShapeProvider(BallistixVoxelShapes.LAUNCHER_CONTROL_PANEL)),
	launchercontrolpaneltier2(true, TileLauncherControlPanelT2::new, MachineProperties.builder().setShapeProvider(BallistixVoxelShapes.LAUNCHER_CONTROL_PANEL)),
	launchercontrolpaneltier3(true, TileLauncherControlPanelT3::new, MachineProperties.builder().setShapeProvider(BallistixVoxelShapes.LAUNCHER_CONTROL_PANEL)),
	radar(true, TileSearchRadar::new, MachineProperties.builder().setShapeProvider(BallistixVoxelShapes.RADAR)),
	firecontrolradar(true, TileFireControlRadar::new, MachineProperties.builder().setShapeProvider(BallistixVoxelShapes.FIRE_CONTROL_RADAR)),
	esmtower(true, TileESMTower::new,MachineProperties.builder().setRenderShape(RenderShape.INVISIBLE)
.setShapeProvider(BallistixVoxelShapes.ESM_TOWER).setSubnodes(Subnodes.ESM_TOWER)),
	samturret(true, TileTurretSAM::new, MachineProperties.builder().setShapeProvider(BallistixVoxelShapes.SAM_TURRET)),
	ciwsturret(true, TileTurretCIWS::new,
			MachineProperties.builder().setShapeProvider(BallistixVoxelShapes.CIWS_TURRET)),
	laserturret(true, TileTurretLaser::new,
			MachineProperties.builder().setShapeProvider(BallistixVoxelShapes.LASER_TURRET)),
	railgunturret(true, TileTurretRailgun::new,
			MachineProperties.builder().setShapeProvider(BallistixVoxelShapes.RAILGUN_TURRET)),;

	private final BlockEntityType.BlockEntitySupplier<BlockEntity> blockEntitySupplier;
	private final boolean showInItemGroup;
	private final MachineProperties properties;

	private SubtypeBallistixMachine(boolean showInItemGroup,
			BlockEntityType.BlockEntitySupplier<BlockEntity> blockEntitySupplier) {
		this(showInItemGroup, blockEntitySupplier, MachineProperties.DEFAULT);
	}

	private SubtypeBallistixMachine(boolean showInItemGroup,
			BlockEntityType.BlockEntitySupplier<BlockEntity> blockEntitySupplier, MachineProperties properties) {
		this.showInItemGroup = showInItemGroup;
		this.blockEntitySupplier = blockEntitySupplier;
		this.properties = properties;
	}

	@Override
	public BlockEntityType.BlockEntitySupplier<BlockEntity> getBlockEntitySupplier() {
		return blockEntitySupplier;
	}

	@Override
	public int getLitBrightness() {
		return properties.litBrightness;
	}

	@Override
	public RenderShape getRenderShape() {
		return properties.renderShape;
	}

	@Override
	public boolean isMultiblock() {
		return properties.isMultiblock;
	}

	@Override
	public boolean propegatesLightDown() {
		return properties.propegatesLightDown;
	}

	@Override
	public String tag() {
		return name();
	}

	@Override
	public String forgeTag() {
		return tag();
	}

	@Override
	public boolean isItem() {
		return false;
	}

	@Override
	public boolean isPlayerStorable() {
		return false;
	}

	@Override
	public IMultiblockParentBlock.SubnodeWrapper getSubnodes() {
		return properties.wrapper;
	}

	@Override
	public VoxelShapeProvider getVoxelShapeProvider() {
		return properties.provider;
	}

	@Override
	public boolean usesLit() {
		return properties.usesLit;
	}

	public boolean showInItemGroup() {
		return showInItemGroup;
	}

	public static class Subnodes {

		public static IMultiblockParentBlock.SubnodeWrapper ESM_TOWER = make(() -> {

			Subnode middle = new Subnode(new BlockPos(0, 1, 0),
					Shapes.or(Block.box(6, 0, 6, 10, 13, 10), Block.box(5, 13, 5, 11, 16, 11)));
			Subnode top = new Subnode(new BlockPos(0, 2, 0), Block.box(5, 0, 5, 11, 16, 11));

			return IMultiblockParentBlock.SubnodeWrapper.createOmni(new Subnode[] { middle, top });

		});

		public static IMultiblockParentBlock.SubnodeWrapper make(Supplier<IMultiblockParentBlock.SubnodeWrapper> sup) {
			return sup.get();
		}
	}

}
