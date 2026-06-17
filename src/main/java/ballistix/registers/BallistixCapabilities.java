package ballistix.registers;

import ballistix.Ballistix;
import ballistix.api.capability.CapabilityActiveBullets;
import ballistix.api.capability.CapabilityActiveMissiles;
import ballistix.api.capability.CapabilityActiveRailgunRounds;
import ballistix.api.capability.CapabilityActiveSAMs;
import ballistix.api.capability.CapabilityAntigravedChunks;
import ballistix.api.capability.CapabilitySiloRegistry;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Ballistix.ID, bus = EventBusSubscriber.Bus.MOD)
public class BallistixCapabilities {

    public static final Capability<CapabilitySiloRegistry> SILO_REGISTRY = CapabilityManager
	    .get(new CapabilityToken<>() {
	    });

    public static final Capability<CapabilityActiveMissiles> ACTIVE_MISSILES = CapabilityManager
	    .get(new CapabilityToken<>() {
	    });

    public static final Capability<CapabilityActiveBullets> ACTIVE_BULLETS = CapabilityManager
	    .get(new CapabilityToken<>() {
	    });

    public static final Capability<CapabilityActiveRailgunRounds> ACTIVE_RAILGUN_ROUNDS = CapabilityManager
	    .get(new CapabilityToken<>() {
	    });

    public static final Capability<CapabilityActiveSAMs> ACTIVE_SAMS = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static final Capability<CapabilityAntigravedChunks> ANTIGRAVED_CHUNKS = CapabilityManager
	    .get(new CapabilityToken<>() {
	    });

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
	event.register(CapabilitySiloRegistry.class);
	event.register(CapabilityActiveMissiles.class);
	event.register(CapabilityActiveBullets.class);
	event.register(CapabilityActiveRailgunRounds.class);
	event.register(CapabilityActiveSAMs.class);
	event.register(CapabilityAntigravedChunks.class);
    }

}
