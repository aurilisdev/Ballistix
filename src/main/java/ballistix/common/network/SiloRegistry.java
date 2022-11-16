package ballistix.common.network;

import java.util.HashMap;
import java.util.HashSet;

import ballistix.common.tile.TileMissileSilo;

public class SiloRegistry {
	private static final HashMap<Integer, HashSet<TileMissileSilo>> silos = new HashMap<>();

	public static void registerSilo(TileMissileSilo silo) {
		if (!silos.containsKey(silo.frequency.get())) {
			silos.put(silo.frequency.get(), new HashSet<>());
		}
		silos.get(silo.frequency.get()).add(silo);
	}

	public static void unregisterSilo(TileMissileSilo silo) {
		silos.remove(silo.frequency.get());
	}

	public static HashSet<TileMissileSilo> getSilos(int freq) {
		return silos.getOrDefault(freq, new HashSet<>());
	}
}
