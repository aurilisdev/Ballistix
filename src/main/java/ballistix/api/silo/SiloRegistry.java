package ballistix.api.silo;

import java.util.HashMap;
import java.util.HashSet;

import ballistix.common.tile.TileLauncherControlPanelT1;
import ballistix.registers.BallistixAttachmentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class SiloRegistry {

	public static void registerSilo(int frequency, TileLauncherControlPanelT1 silo) {

		ServerLevel overworld = getOverworld();

		HashMap<Integer, HashSet<BlockPos>> siloRegistry = overworld.getData(BallistixAttachmentTypes.SILO_FREQUENCIES);

		HashSet<BlockPos> registered = siloRegistry.getOrDefault(frequency, new HashSet<>());

		registered.add(silo.getBlockPos());

		siloRegistry.put(frequency, registered);

		overworld.setData(BallistixAttachmentTypes.SILO_FREQUENCIES, siloRegistry);

	}

	public static void unregisterSilo(int frequency, TileLauncherControlPanelT1 silo) {

		ServerLevel overworld = getOverworld();

		HashMap<Integer, HashSet<BlockPos>> siloRegistry = overworld.getData(BallistixAttachmentTypes.SILO_FREQUENCIES);

		HashSet<BlockPos> registered = siloRegistry.getOrDefault(frequency, new HashSet<>());

		registered.remove(silo.getBlockPos());

		siloRegistry.put(frequency, registered);

		overworld.setData(BallistixAttachmentTypes.SILO_FREQUENCIES, siloRegistry);

	}

	public static HashSet<TileLauncherControlPanelT1> getSilos(int freq) {

		ServerLevel overworld = getOverworld();

		HashMap<Integer, HashSet<BlockPos>> siloRegistry = overworld.getData(BallistixAttachmentTypes.SILO_FREQUENCIES);

		HashSet<TileLauncherControlPanelT1> silos = new HashSet<>();

		for (BlockPos pos : siloRegistry.getOrDefault(freq, new HashSet<>())) {

			if (overworld.getBlockEntity(pos) instanceof TileLauncherControlPanelT1 silo) {
				silos.add(silo);
			}

		}

		return silos;

	}

	public static ServerLevel getOverworld() {
		return ServerLifecycleHooks.getCurrentServer().overworld();
	}
}
