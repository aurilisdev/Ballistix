package ballistix.common.block.subtype;

import java.util.function.Supplier;
import java.util.stream.Stream;

import ballistix.common.block.BallistixVoxelShapes;
import ballistix.common.tile.TileESMTower;
import ballistix.common.tile.TileVerticalLaunchSilo;
import ballistix.common.tile.radar.TileFireControlRadar;
import ballistix.common.tile.radar.TileSearchRadar;
import ballistix.common.tile.silo.TileLauncherControlPanelT1;
import ballistix.common.tile.silo.TileLauncherControlPanelT2;
import ballistix.common.tile.silo.TileLauncherControlPanelT3;
import ballistix.common.tile.silo.TileLauncherPlatformT1;
import ballistix.common.tile.silo.TileLauncherPlatformT2;
import ballistix.common.tile.silo.TileLauncherPlatformT3;
import ballistix.common.tile.silo.TileLauncherSupportFrameT1;
import ballistix.common.tile.silo.TileLauncherSupportFrameT2;
import ballistix.common.tile.silo.TileLauncherSupportFrameT3;
import ballistix.common.tile.turret.antimissile.TileTurretCIWS;
import ballistix.common.tile.turret.antimissile.TileTurretLaser;
import ballistix.common.tile.turret.antimissile.TileTurretRailgun;
import ballistix.common.tile.turret.antimissile.TileTurretSAM;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import voltaic.api.ISubtype;
import voltaic.api.multiblock.subnodebased.Subnode;
import voltaic.api.multiblock.subnodebased.parent.IMultiblockParentBlock;
import voltaic.api.tile.IMachine;
import voltaic.api.tile.MachineProperties;
import voltaic.common.block.voxelshapes.VoxelShapeProvider;

public enum SubtypeBallistixMachine implements ISubtype, IMachine {

	launchercontrolpaneltier1(true, TileLauncherControlPanelT1::new, MachineProperties.builder().setShapeProvider(BallistixVoxelShapes.LAUNCHER_CONTROL_PANEL_TIER1)),
	//
	launchercontrolpaneltier2(true, TileLauncherControlPanelT2::new, MachineProperties.builder().setShapeProvider(BallistixVoxelShapes.LAUNCHER_CONTROL_PANEL_TIER2)),
	//
	launchercontrolpaneltier3(true, TileLauncherControlPanelT3::new, MachineProperties.builder().setShapeProvider(BallistixVoxelShapes.LAUNCHER_CONTROL_PANEL_TIER3)),
	//
	launchersupportframetier1(true, TileLauncherSupportFrameT1::new, MachineProperties.builder().setShapeProvider(BallistixVoxelShapes.LAUNCHER_SUPPORTFRAME_TIER1).setSubnodes(Subnodes.LAUNCHER_SUPPORT_FRAME_TIER1)),
	//
	launchersupportframetier2(true, TileLauncherSupportFrameT2::new, MachineProperties.builder().setShapeProvider(BallistixVoxelShapes.LAUNCHER_SUPPORTFRAME_TIER2).setSubnodes(Subnodes.LAUNCHER_SUPPORT_FRAME_TIER2)),
	//
	launchersupportframetier3(true, TileLauncherSupportFrameT3::new, MachineProperties.builder().setShapeProvider(BallistixVoxelShapes.LAUNCHER_SUPPORTFRAME_TIER3).setSubnodes(Subnodes.LAUNCHER_SUPPORT_FRAME_TIER3)),
	//
	launcherplatformtier1(true, TileLauncherPlatformT1::new, MachineProperties.builder().setShapeProvider(BallistixVoxelShapes.LAUNCHER_PLATFORM_TIER1).setSubnodes(Subnodes.LAUNCHER_PLATFORM_TIER1)),
	//
	launcherplatformtier2(true, TileLauncherPlatformT2::new, MachineProperties.builder().setShapeProvider(BallistixVoxelShapes.LAUNCHER_PLATFORM_TIER2).setSubnodes(Subnodes.LAUNCHER_PLATFORM_TIER2)),
	//
	launcherplatformtier3(true, TileLauncherPlatformT3::new, MachineProperties.builder().setShapeProvider(BallistixVoxelShapes.LAUNCHER_PLATFORM_TIER3).setSubnodes(Subnodes.LAUNCHER_PLATFORM_TIER3)),
	vls(true, TileVerticalLaunchSilo::new, MachineProperties.builder().setSubnodes(Subnodes.VLS)),
	//
	radar(true, TileSearchRadar::new, MachineProperties.builder().setShapeProvider(BallistixVoxelShapes.RADAR)),
	//
	firecontrolradar(true, TileFireControlRadar::new, MachineProperties.builder().setShapeProvider(BallistixVoxelShapes.FIRE_CONTROL_RADAR)),
	//
	esmtower(true, TileESMTower::new, MachineProperties.builder().setRenderShape(RenderShape.INVISIBLE).setShapeProvider(BallistixVoxelShapes.ESM_TOWER).setSubnodes(Subnodes.ESM_TOWER)),
	//
	samturret(true, TileTurretSAM::new, MachineProperties.builder().setShapeProvider(BallistixVoxelShapes.SAM_TURRET)),
	//
	ciwsturret(true, TileTurretCIWS::new, MachineProperties.builder().setShapeProvider(BallistixVoxelShapes.CIWS_TURRET)),
	//
	laserturret(true, TileTurretLaser::new, MachineProperties.builder().setShapeProvider(BallistixVoxelShapes.LASER_TURRET)),
	//
	railgunturret(true, TileTurretRailgun::new, MachineProperties.builder().setShapeProvider(BallistixVoxelShapes.RAILGUN_TURRET));
	//

	private final BlockEntityType.BlockEntitySupplier<BlockEntity> blockEntitySupplier;
	private final boolean showInItemGroup;
	private final MachineProperties properties;

	private SubtypeBallistixMachine(boolean showInItemGroup, BlockEntityType.BlockEntitySupplier<BlockEntity> blockEntitySupplier) {
		this(showInItemGroup, blockEntitySupplier, MachineProperties.DEFAULT);
	}

	private SubtypeBallistixMachine(boolean showInItemGroup, BlockEntityType.BlockEntitySupplier<BlockEntity> blockEntitySupplier, MachineProperties properties) {
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

			Subnode middle = new Subnode(new BlockPos(0, 1, 0), Shapes.or(Block.box(6, 0, 6, 10, 13, 10), Block.box(5, 13, 5, 11, 16, 11)));
			Subnode top = new Subnode(new BlockPos(0, 2, 0), Block.box(5, 0, 5, 11, 16, 11));

			return IMultiblockParentBlock.SubnodeWrapper.createOmni(new Subnode[] { middle, top });

		});

		public static final IMultiblockParentBlock.SubnodeWrapper LAUNCHER_SUPPORT_FRAME_TIER1 = make(() -> {
			Subnode[] subnodesSouth = new Subnode[1];
			Subnode[] subnodesNorth = new Subnode[1];
			Subnode[] subnodesEast = new Subnode[1];
			Subnode[] subnodesWest = new Subnode[1];
			VoxelShape topShape = Block.box(5, 0, 9, 11, 16, 15);
			subnodesNorth[0] = new Subnode(new BlockPos(0, 1, 0), topShape);
			subnodesSouth[0] = new Subnode(new BlockPos(0, 1, 0), rotate(Direction.NORTH, topShape));
			subnodesEast[0] = new Subnode(new BlockPos(0, 1, 0), rotate(Direction.WEST, topShape));
			subnodesWest[0] = new Subnode(new BlockPos(0, 1, 0), rotate(Direction.EAST, topShape));
			return IMultiblockParentBlock.SubnodeWrapper.createDirectional(subnodesNorth, subnodesEast, subnodesSouth, subnodesWest);
		});

		public static final IMultiblockParentBlock.SubnodeWrapper LAUNCHER_SUPPORT_FRAME_TIER2 = LAUNCHER_SUPPORT_FRAME_TIER1;
		public static final IMultiblockParentBlock.SubnodeWrapper LAUNCHER_SUPPORT_FRAME_TIER3 = make(() -> {
			Subnode[] subnodesSouth = new Subnode[1];
			Subnode[] subnodesNorth = new Subnode[1];
			Subnode[] subnodesEast = new Subnode[1];
			Subnode[] subnodesWest = new Subnode[1];
			VoxelShape topShape = Stream.of(
					//
					Block.box(5, 0, 9, 11, 16, 15),
					//
					Block.box(4.75, 14.5, 8.75, 11.25, 15.5, 15.25),
					//
					Block.box(4.75, 9, 8.75, 11.25, 10, 15.25),
					//
					Block.box(4.75, 4, 8.75, 11.25, 5, 15.25)
			//
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
			subnodesNorth[0] = new Subnode(new BlockPos(0, 1, 0), topShape);
			subnodesSouth[0] = new Subnode(new BlockPos(0, 1, 0), rotate(Direction.NORTH, topShape));
			subnodesEast[0] = new Subnode(new BlockPos(0, 1, 0), rotate(Direction.WEST, topShape));
			subnodesWest[0] = new Subnode(new BlockPos(0, 1, 0), rotate(Direction.EAST, topShape));
			return IMultiblockParentBlock.SubnodeWrapper.createDirectional(subnodesNorth, subnodesEast, subnodesSouth, subnodesWest);
		});
		public static final IMultiblockParentBlock.SubnodeWrapper LAUNCHER_PLATFORM_TIER1 = make(() -> {
			Subnode[] subnodesSouth = new Subnode[3];
			Subnode[] subnodesNorth = new Subnode[3];
			Subnode[] subnodesEast = new Subnode[3];
			Subnode[] subnodesWest = new Subnode[3];
			VoxelShape topShape = Stream.of(
					//
					Block.box(1, 0, 7, 2, 7, 9),
					//
					Block.box(2, 7, 7, 3, 16, 9),
					//
					Block.box(13, 7, 7, 14, 16, 9),
					//
					Block.box(2, 0, 6, 3, 7, 10),
					//
					Block.box(13, 0, 6, 14, 7, 10),
					//
					Block.box(14, 0, 7, 15, 7, 9)
			//
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
			VoxelShape leftShape = Shapes.join(Block.box(0, 1, 5, 2, 5, 11), Block.box(0, 0, 3, 4, 1, 13), BooleanOp.OR);
			VoxelShape rightShape = Shapes.join(Block.box(12, 0, 3, 16, 1, 13), Block.box(14, 1, 5, 16, 5, 11), BooleanOp.OR);
			subnodesNorth[0] = new Subnode(new BlockPos(0, 1, 0), topShape);
			subnodesNorth[1] = new Subnode(new BlockPos(1, 0, 0), leftShape);
			subnodesNorth[2] = new Subnode(new BlockPos(-1, 0, 0), rightShape);

			subnodesSouth[0] = new Subnode(new BlockPos(0, 1, 0), rotate(Direction.NORTH, topShape));
			subnodesSouth[1] = new Subnode(new BlockPos(-1, 0, 0), rotate(Direction.NORTH, leftShape));
			subnodesSouth[2] = new Subnode(new BlockPos(1, 0, 0), rotate(Direction.NORTH, rightShape));

			subnodesEast[0] = new Subnode(new BlockPos(0, 1, 0), rotate(Direction.WEST, topShape));
			subnodesEast[1] = new Subnode(new BlockPos(0, 0, 1), rotate(Direction.WEST, leftShape));
			subnodesEast[2] = new Subnode(new BlockPos(0, 0, -1), rotate(Direction.WEST, rightShape));

			subnodesWest[0] = new Subnode(new BlockPos(0, 1, 0), rotate(Direction.EAST, topShape));
			subnodesWest[1] = new Subnode(new BlockPos(0, 0, -1), rotate(Direction.EAST, leftShape));
			subnodesWest[2] = new Subnode(new BlockPos(0, 0, 1), rotate(Direction.EAST, rightShape));
			return IMultiblockParentBlock.SubnodeWrapper.createDirectional(subnodesNorth, subnodesEast, subnodesSouth, subnodesWest);
		});

		public static final IMultiblockParentBlock.SubnodeWrapper LAUNCHER_PLATFORM_TIER2 = make(() -> {
			Subnode[] subnodesSouth = new Subnode[3];
			Subnode[] subnodesNorth = new Subnode[3];
			Subnode[] subnodesEast = new Subnode[3];
			Subnode[] subnodesWest = new Subnode[3];
			VoxelShape topShape = Stream.of(
					//
					Block.box(0, 0, 7, 1, 7, 9),
					//
					Block.box(1, 7, 7, 2, 16, 9),
					//
					Block.box(14, 7, 7, 15, 16, 9),
					//
					Block.box(1, 0, 6, 2, 7, 10),
					//
					Block.box(14, 0, 6, 15, 7, 10),
					//
					Block.box(15, 0, 7, 16, 7, 9)
			//
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
			VoxelShape leftShape = Shapes.join(Block.box(0, 0, 3, 4, 1, 13), Block.box(0, 1, 5, 3, 5, 11), BooleanOp.OR);
			VoxelShape rightShape = Shapes.join(Block.box(12, 0, 3, 16, 1, 13), Block.box(13, 1, 5, 16, 5, 11), BooleanOp.OR);
			subnodesNorth[0] = new Subnode(new BlockPos(0, 1, 0), topShape);
			subnodesNorth[1] = new Subnode(new BlockPos(1, 0, 0), leftShape);
			subnodesNorth[2] = new Subnode(new BlockPos(-1, 0, 0), rightShape);

			subnodesSouth[0] = new Subnode(new BlockPos(0, 1, 0), rotate(Direction.NORTH, topShape));
			subnodesSouth[1] = new Subnode(new BlockPos(-1, 0, 0), rotate(Direction.NORTH, leftShape));
			subnodesSouth[2] = new Subnode(new BlockPos(1, 0, 0), rotate(Direction.NORTH, rightShape));

			subnodesEast[0] = new Subnode(new BlockPos(0, 1, 0), rotate(Direction.WEST, topShape));
			subnodesEast[1] = new Subnode(new BlockPos(0, 0, 1), rotate(Direction.WEST, leftShape));
			subnodesEast[2] = new Subnode(new BlockPos(0, 0, -1), rotate(Direction.WEST, rightShape));

			subnodesWest[0] = new Subnode(new BlockPos(0, 1, 0), rotate(Direction.EAST, topShape));
			subnodesWest[1] = new Subnode(new BlockPos(0, 0, -1), rotate(Direction.EAST, leftShape));
			subnodesWest[2] = new Subnode(new BlockPos(0, 0, 1), rotate(Direction.EAST, rightShape));
			return IMultiblockParentBlock.SubnodeWrapper.createDirectional(subnodesNorth, subnodesEast, subnodesSouth, subnodesWest);
		});
		public static final IMultiblockParentBlock.SubnodeWrapper LAUNCHER_PLATFORM_TIER3 = make(() -> {
			Subnode[] subnodesSouth = new Subnode[5];
			Subnode[] subnodesNorth = new Subnode[5];
			Subnode[] subnodesEast = new Subnode[5];
			Subnode[] subnodesWest = new Subnode[5];
			VoxelShape topShape = Stream.of(
					//
					Block.box(0, 0, 7, 1, 7, 9),
					//
					Block.box(1, 7, 7, 2, 16, 9),
					//
					Block.box(14, 7, 7, 15, 16, 9),
					//
					Block.box(1, 0, 6, 2, 7, 10),
					//
					Block.box(14, 0, 6, 15, 7, 10),
					//
					Block.box(15, 0, 7, 16, 7, 9)
			//
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
			VoxelShape leftShape = Stream.of(
					//
					Block.box(0, 1, 5, 3, 5, 11),
					//
					Block.box(0, 0, 3, 4, 1, 13),
					//
					Block.box(1, 5, 7, 2, 16, 9),
					//
					Block.box(2, 5, 6, 3, 16, 10),
					//
					Block.box(0, 8.025, 7, 1, 9.975, 9)
			//
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
			VoxelShape topLeftShape = Stream.of(Block.box(2, 0, 6, 3, 7, 10), Block.box(1, 0, 7, 2, 7, 9), Block.box(0, 0.025, 7, 1, 1.975, 9), Block.box(2, 7, 7, 3, 16, 9)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
			VoxelShape rightShape = Stream.of(
					//
					Block.box(12, 0, 3, 16, 1, 13),
					//
					Block.box(13, 1, 5, 16, 5, 11),
					//
					Block.box(14, 5, 7, 15, 16, 9),
					//
					Block.box(13, 5, 6, 14, 16, 10),
					//
					Block.box(15, 8.025, 7, 16, 9.975, 9)
			//
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
			VoxelShape topRightShape = Stream.of(
					//
					Block.box(14, 0, 7, 15, 7, 9),
					//
					Block.box(13, 0, 6, 14, 7, 10),
					//
					Block.box(15, 0.025, 7, 16, 1.975, 9),
					//
					Block.box(13, 7, 7, 14, 16, 9)
			//
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
			subnodesNorth[0] = new Subnode(new BlockPos(0, 1, 0), topShape);
			subnodesNorth[1] = new Subnode(new BlockPos(1, 0, 0), leftShape);
			subnodesNorth[2] = new Subnode(new BlockPos(1, 1, 0), topLeftShape);
			subnodesNorth[3] = new Subnode(new BlockPos(-1, 0, 0), rightShape);
			subnodesNorth[4] = new Subnode(new BlockPos(-1, 1, 0), topRightShape);

			subnodesSouth[0] = new Subnode(new BlockPos(0, 1, 0), rotate(Direction.NORTH, topShape));
			subnodesSouth[1] = new Subnode(new BlockPos(-1, 0, 0), rotate(Direction.NORTH, leftShape));
			subnodesSouth[2] = new Subnode(new BlockPos(-1, 1, 0), rotate(Direction.NORTH, topLeftShape));
			subnodesSouth[3] = new Subnode(new BlockPos(1, 0, 0), rotate(Direction.NORTH, rightShape));
			subnodesSouth[4] = new Subnode(new BlockPos(1, 1, 0), rotate(Direction.NORTH, topRightShape));

			subnodesEast[0] = new Subnode(new BlockPos(0, 1, 0), rotate(Direction.WEST, topShape));
			subnodesEast[1] = new Subnode(new BlockPos(0, 0, 1), rotate(Direction.WEST, leftShape));
			subnodesEast[2] = new Subnode(new BlockPos(0, 1, 1), rotate(Direction.WEST, topLeftShape));
			subnodesEast[3] = new Subnode(new BlockPos(0, 0, -1), rotate(Direction.WEST, rightShape));
			subnodesEast[4] = new Subnode(new BlockPos(0, 1, -1), rotate(Direction.WEST, topRightShape));

			subnodesWest[0] = new Subnode(new BlockPos(0, 1, 0), rotate(Direction.EAST, topShape));
			subnodesWest[1] = new Subnode(new BlockPos(0, 0, -1), rotate(Direction.EAST, leftShape));
			subnodesWest[2] = new Subnode(new BlockPos(0, 1, -1), rotate(Direction.EAST, topLeftShape));
			subnodesWest[3] = new Subnode(new BlockPos(0, 0, 1), rotate(Direction.EAST, rightShape));
			subnodesWest[4] = new Subnode(new BlockPos(0, 1, 1), rotate(Direction.EAST, topRightShape));
			return IMultiblockParentBlock.SubnodeWrapper.createDirectional(subnodesNorth, subnodesEast, subnodesSouth, subnodesWest);
		});
		
		public static final IMultiblockParentBlock.SubnodeWrapper VLS = make(() -> IMultiblockParentBlock.SubnodeWrapper.createOmni(new Subnode[]{new Subnode(new BlockPos(0, 1, 0), Shapes.block())}));

		public static IMultiblockParentBlock.SubnodeWrapper make(Supplier<IMultiblockParentBlock.SubnodeWrapper> sup) {
			return sup.get();
		}

		private static VoxelShape rotate(Direction to, VoxelShape shape) {
			VoxelShape[] buffer = new VoxelShape[] { shape, Shapes.empty() };

			int times = (to.get2DDataValue() - Direction.SOUTH.get2DDataValue() + 4) % 4;
			for (int i = 0; i < times; i++) {
				buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1], Shapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
				buffer[0] = buffer[1];
				buffer[1] = Shapes.empty();
			}

			return buffer[0];
		}
	}

}
