package ballistix.common.network;

import java.util.HashSet;

import ballistix.api.capability.BallistixCapabilities;
import ballistix.common.tile.TileMissileSilo;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class SiloRegistry {

	public static void registerSilo(int frequency, TileMissileSilo silo) {

		ServerLifecycleHooks.getCurrentServer().overworld().getCapability(BallistixCapabilities.SILO_REGISTRY).ifPresent(cap -> {

			cap.addSilo(frequency, silo.getBlockPos());

		});

	}

	public static void unregisterSilo(int frequency, TileMissileSilo silo) {
		ServerLifecycleHooks.getCurrentServer().overworld().getCapability(BallistixCapabilities.SILO_REGISTRY).ifPresent(cap -> {

			cap.removeSilo(frequency, silo.getBlockPos());

		});
	}

	public static HashSet<TileMissileSilo> getSilos(int freq, World world) {

		return ServerLifecycleHooks.getCurrentServer().overworld().getCapability(BallistixCapabilities.SILO_REGISTRY).map(cap -> {

			HashSet<TileMissileSilo> silos = new HashSet<>();

			TileEntity tile;

			for (BlockPos pos : cap.getSilosForFrequency(freq)) {

				tile = world.getBlockEntity(pos);

				if (tile != null && tile instanceof TileMissileSilo) {
					silos.add((TileMissileSilo) tile);
				}

			}

			return silos;

		}).orElse(new HashSet<>());

	}
}