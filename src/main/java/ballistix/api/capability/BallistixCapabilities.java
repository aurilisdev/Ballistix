package ballistix.api.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class BallistixCapabilities {

	public static final Capability<CapabilitySiloRegistry> SILO_REGISTRY = CapabilityManager.get(new CapabilityToken<>() {
	});
	
	public static final Capability<CapabilityActiveMissiles> ACTIVE_MISSILES = CapabilityManager.get(new CapabilityToken<>() {
	});
	
	public static final Capability<CapabilityActiveBullets> ACTIVE_BULLETS = CapabilityManager.get(new CapabilityToken<>() {
	});
	
	public static final Capability<CapabilityActiveRailgunRounds> ACTIVE_RAILGUN_ROUNDS = CapabilityManager.get(new CapabilityToken<>() {
	});
	
	public static final Capability<CapabilityActiveSAMs> ACTIVE_SAMS = CapabilityManager.get(new CapabilityToken<>() {
	});

	public static void register(RegisterCapabilitiesEvent event) {
		event.register(CapabilitySiloRegistry.class);
		event.register(CapabilityActiveMissiles.class);
		event.register(CapabilityActiveBullets.class);
		event.register(CapabilityActiveRailgunRounds.class);
		event.register(CapabilityActiveSAMs.class);
	}

}
