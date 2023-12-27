package ballistix.api.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class BallistixCapabilities {
	
	@CapabilityInject(CapabilitySiloRegistry.class)
	public static Capability<CapabilitySiloRegistry> SILO_REGISTRY;
	
	public static void register() {
		
		CapabilityManager.INSTANCE.register(CapabilitySiloRegistry.class, new IStorage<CapabilitySiloRegistry>() {

			@Override
			public INBT writeNBT(Capability<CapabilitySiloRegistry> capability, CapabilitySiloRegistry instance, Direction side) {
				return new CompoundNBT();
			}

			@Override
			public void readNBT(Capability<CapabilitySiloRegistry> capability, CapabilitySiloRegistry instance, Direction side, INBT nbt) {
				
				
			}
			
		}, () -> new CapabilitySiloRegistry());
		
	}

}