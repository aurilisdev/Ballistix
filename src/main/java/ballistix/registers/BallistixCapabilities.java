package ballistix.registers;

import ballistix.api.capability.CapabilityActiveBullets;
import ballistix.api.capability.CapabilityActiveMissiles;
import ballistix.api.capability.CapabilityActiveRailgunRounds;
import ballistix.api.capability.CapabilityActiveSAMs;
import ballistix.api.capability.CapabilitySiloRegistry;
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
	@CapabilityInject(CapabilityActiveBullets.class)
	public static Capability<CapabilityActiveBullets> ACTIVE_BULLETS;
	@CapabilityInject(CapabilityActiveRailgunRounds.class)
	public static Capability<CapabilityActiveRailgunRounds> ACTIVE_RAILGUN_ROUNDS;
	@CapabilityInject(CapabilityActiveSAMs.class)
	public static Capability<CapabilityActiveSAMs> ACTIVE_SAMS;
	@CapabilityInject(CapabilityActiveMissiles.class)
	public static Capability<CapabilityActiveMissiles> ACTIVE_MISSILES;
	
	public static void register() {
		
		CapabilityManager.INSTANCE.register(CapabilitySiloRegistry.class, new IStorage<CapabilitySiloRegistry>() {

			@Override
			public INBT writeNBT(Capability<CapabilitySiloRegistry> capability, CapabilitySiloRegistry instance, Direction side) {
				return instance.serializeNBT();
			}

			@Override
			public void readNBT(Capability<CapabilitySiloRegistry> capability, CapabilitySiloRegistry instance, Direction side, INBT nbt) {
				if(nbt instanceof CompoundNBT) {
					instance.deserializeNBT((CompoundNBT) nbt);
				}
			}
			
		}, () -> new CapabilitySiloRegistry());
		
		CapabilityManager.INSTANCE.register(CapabilityActiveBullets.class, new IStorage<CapabilityActiveBullets>() {

			@Override
			public INBT writeNBT(Capability<CapabilityActiveBullets> capability, CapabilityActiveBullets instance, Direction side) {
				return instance.serializeNBT();
			}

			@Override
			public void readNBT(Capability<CapabilityActiveBullets> capability, CapabilityActiveBullets instance, Direction side, INBT nbt) {
				if(nbt instanceof CompoundNBT) {
					instance.deserializeNBT((CompoundNBT) nbt);
				}
			}
			
		}, () -> new CapabilityActiveBullets());
		
		CapabilityManager.INSTANCE.register(CapabilityActiveRailgunRounds.class, new IStorage<CapabilityActiveRailgunRounds>() {

			@Override
			public INBT writeNBT(Capability<CapabilityActiveRailgunRounds> capability, CapabilityActiveRailgunRounds instance, Direction side) {
				return instance.serializeNBT();
			}

			@Override
			public void readNBT(Capability<CapabilityActiveRailgunRounds> capability, CapabilityActiveRailgunRounds instance, Direction side, INBT nbt) {
				if(nbt instanceof CompoundNBT) {
					instance.deserializeNBT((CompoundNBT) nbt);
				}
			}
			
		}, () -> new CapabilityActiveRailgunRounds());
		
		CapabilityManager.INSTANCE.register(CapabilityActiveSAMs.class, new IStorage<CapabilityActiveSAMs>() {

			@Override
			public INBT writeNBT(Capability<CapabilityActiveSAMs> capability, CapabilityActiveSAMs instance, Direction side) {
				return instance.serializeNBT();
			}

			@Override
			public void readNBT(Capability<CapabilityActiveSAMs> capability, CapabilityActiveSAMs instance, Direction side, INBT nbt) {
				if(nbt instanceof CompoundNBT) {
					instance.deserializeNBT((CompoundNBT) nbt);
				}
			}
			
		}, () -> new CapabilityActiveSAMs());
		
		CapabilityManager.INSTANCE.register(CapabilityActiveMissiles.class, new IStorage<CapabilityActiveMissiles>() {

			@Override
			public INBT writeNBT(Capability<CapabilityActiveMissiles> capability, CapabilityActiveMissiles instance, Direction side) {
				return instance.serializeNBT();
			}

			@Override
			public void readNBT(Capability<CapabilityActiveMissiles> capability, CapabilityActiveMissiles instance, Direction side, INBT nbt) {
				if(nbt instanceof CompoundNBT) {
					instance.deserializeNBT((CompoundNBT) nbt);
				}
			}
			
		}, () -> new CapabilityActiveMissiles());
		
	}

}