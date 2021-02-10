//package ballistix.common.blast;
//
//import java.util.Iterator;
//
//import ballistix.common.blast.thread.ThreadEMP;
//import ballistix.common.block.SubtypeBlast;
//import electrodynamics.api.tile.electric.IJoulesStorage;
//import electrodynamics.api.tile.electric.IPowerProvider;
//import electrodynamics.api.utilities.TransferPack;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.Direction;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//import net.minecraftforge.common.util.LazyOptional;
//import net.minecraftforge.energy.CapabilityEnergy;
//import net.minecraftforge.energy.IEnergyStorage;
//
//public class BlastEmp extends Blast {
//
//	public BlastEmp(World world, BlockPos position) {
//		super(world, position);
//	}
//
//	@Override
//	public void doPreExplode() {
//		if (!world.isRemote) {
//			thread = new ThreadEMP(world, position, 25, 90, null);
//			thread.start();
//		}
//
//	}
//
//	private ThreadEMP thread;
//	private int pertick = -1;
//
//	@Override
//	public boolean doExplode(int callCount) {
//		if (!world.isRemote) {
//			if (thread == null) {
//				return true;
//			}
//			if (thread.isComplete) {
//				if (pertick == -1) {
//					pertick = (int) (thread.results.size() / 5.0 + 1);
//				}
//				int finished = pertick;
//				Iterator<BlockPos> iterator = thread.results.iterator();
//				while (iterator.hasNext()) {
//					if (finished-- < 0) {
//						break;
//					}
//					BlockPos pos = iterator.next();
//					TileEntity tile = world.getTileEntity(pos);
//					if (tile != null) {
//						for (Direction dir : Direction.values()) {
//							LazyOptional<IEnergyStorage> storage = tile.getCapability(CapabilityEnergy.ENERGY, dir);
//							storage.ifPresent(storage1 -> {
//								while (storage1.getEnergyStored() > 0) {
//									storage1.extractEnergy(Integer.MAX_VALUE, false);
//								}
//							});
//						}
//						if (tile instanceof IEnergyStorage) {
//							IEnergyStorage storage = (IEnergyStorage) tile;
//							storage.extractEnergy(Integer.MAX_VALUE, false);
//						}
//						if (tile instanceof IJoulesStorage) {
//							IJoulesStorage storage = (IJoulesStorage) tile;
//							storage.setJoulesStored(0);
//						}
//						if (tile instanceof IPowerProvider) {
//							IPowerProvider storage = (IPowerProvider) tile;
//							for (Direction dir : Direction.values()) {
//								storage.extractPower(TransferPack.ampsVoltage(Double.MAX_VALUE, storage.getVoltage(dir)), dir, false);
//							}
//						}
//					}
//					iterator.remove();
//				}
//				return thread.results.isEmpty();
//			}
//		}
//		return false;
//	}
//
//	@Override
//	public void doPostExplode() {
//	}
//
//	@Override
//	public boolean isInstantaneous() {
//		return false;
//	}
//
//	@Override
//	public SubtypeBlast getBlastType() {
//		return SubtypeBlast.emp;
//	}
//
//}
