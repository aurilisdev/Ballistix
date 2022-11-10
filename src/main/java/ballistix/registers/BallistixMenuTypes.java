package ballistix.registers;

import ballistix.References;
import ballistix.common.inventory.container.ContainerMissileSilo;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BallistixMenuTypes {
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, References.ID);

	public static final RegistryObject<MenuType<ContainerMissileSilo>> CONTAINER_MISSILESILO = MENU_TYPES.register("missilesilo", () -> new MenuType<>(ContainerMissileSilo::new));

}
