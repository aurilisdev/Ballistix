package ballistix.api.silo;

import java.util.HashSet;

import ballistix.registers.BallistixCapabilities;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class SiloRegistry {

	public static void registerSilo(int frequency, ILauncherControlPanel silo) {

		ServerLifecycleHooks.getCurrentServer().overworld().getCapability(BallistixCapabilities.SILO_REGISTRY).ifPresent(cap -> {

			cap.addSilo(frequency, silo.getPos());

		});

	}

	public static void unregisterSilo(int frequency, ILauncherControlPanel silo) {
		ServerLifecycleHooks.getCurrentServer().overworld().getCapability(BallistixCapabilities.SILO_REGISTRY).ifPresent(cap -> {

			cap.removeSilo(frequency, silo.getPos());

		});
	}

	public static HashSet<ILauncherControlPanel> getSilos(int freq, World world) {

		return ServerLifecycleHooks.getCurrentServer().overworld().getCapability(BallistixCapabilities.SILO_REGISTRY).map(cap -> {

			HashSet<ILauncherControlPanel> silos = new HashSet<>();

			TileEntity tile;

			for (BlockPos pos : cap.getSilosForFrequency(freq)) {

				tile = world.getBlockEntity(pos);

				if (tile != null && tile instanceof ILauncherControlPanel) {
					silos.add((ILauncherControlPanel) tile);
				}

			}

			return silos;

		}).orElse(new HashSet<>());

	}
}
