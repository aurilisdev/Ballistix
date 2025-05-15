package ballistix.api.silo;

import java.util.HashSet;

import ballistix.registers.BallistixCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.server.ServerLifecycleHooks;

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

	public static HashSet<ILauncherControlPanel> getSilos(int freq, Level world) {

		return ServerLifecycleHooks.getCurrentServer().overworld().getCapability(BallistixCapabilities.SILO_REGISTRY).map(cap -> {

			HashSet<ILauncherControlPanel> silos = new HashSet<>();

			BlockEntity tile;

			for (BlockPos pos : cap.getSilosForFrequency(freq)) {

				tile = world.getBlockEntity(pos);

				if (tile != null && tile instanceof ILauncherControlPanel silo) {
					silos.add(silo);
				}

			}

			return silos;

		}).orElse(new HashSet<>());

	}
}
