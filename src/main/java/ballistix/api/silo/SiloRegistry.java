package ballistix.api.silo;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import ballistix.registers.BallistixAttachmentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class SiloRegistry {

    public static void registerSilo(int frequency, ILauncherControlPanel silo) {
	ServerLevel overworld = getOverworld();

	ConcurrentHashMap<Integer, Set<BlockPos>> siloRegistry = overworld
		.getData(BallistixAttachmentTypes.SILO_FREQUENCIES);
	siloRegistry.computeIfAbsent(frequency, ignored -> ConcurrentHashMap.newKeySet()).add(silo.getPos());

    }

    public static void unregisterSilo(int frequency, ILauncherControlPanel silo) {

	ServerLevel overworld = getOverworld();

	ConcurrentHashMap<Integer, Set<BlockPos>> siloRegistry = overworld
		.getData(BallistixAttachmentTypes.SILO_FREQUENCIES);
	Set<BlockPos> registered = siloRegistry.get(frequency);
	if (registered != null) {
	    registered.remove(silo.getPos());
	}
    }

    public static Set<BlockPos> getSiloPositions(int frequency) {
	Set<BlockPos> positions = getOverworld().getData(BallistixAttachmentTypes.SILO_FREQUENCIES).get(frequency);
	return positions == null ? Set.of() : Set.copyOf(positions);
    }

    public static HashSet<ILauncherControlPanel> getSilos(int freq) {

	ServerLevel overworld = getOverworld();

	ConcurrentHashMap<Integer, Set<BlockPos>> siloRegistry = overworld
		.getData(BallistixAttachmentTypes.SILO_FREQUENCIES);

	HashSet<ILauncherControlPanel> silos = new HashSet<>();

	for (BlockPos pos : siloRegistry.getOrDefault(freq, Set.of())) {

	    if (overworld.getBlockEntity(pos) instanceof ILauncherControlPanel silo) {
		silos.add(silo);
	    }

	}

	return silos;

    }

    public static ServerLevel getOverworld() {
	return ServerLifecycleHooks.getCurrentServer().overworld();
    }
}
