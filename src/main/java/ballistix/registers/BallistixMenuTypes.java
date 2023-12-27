package ballistix.registers;

import ballistix.References;
import ballistix.common.inventory.container.ContainerMissileSilo;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BallistixMenuTypes {
	public static final DeferredRegister<ContainerType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, References.ID);

	public static final RegistryObject<ContainerType<ContainerMissileSilo>> CONTAINER_MISSILESILO = MENU_TYPES.register("missilesilo", () -> new ContainerType<>(ContainerMissileSilo::new));

}
