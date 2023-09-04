package ballistix.registers;

import ballistix.References;
import ballistix.common.inventory.container.ContainerMissileSilo;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MenuType.MenuSupplier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BallistixMenuTypes {
	
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, References.ID);

	public static final RegistryObject<MenuType<ContainerMissileSilo>> CONTAINER_MISSILESILO = register("missilesilo", ContainerMissileSilo::new);
	
	private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String id, MenuSupplier<T> supplier) {
		return MENU_TYPES.register(id, () -> new MenuType<T>(supplier, FeatureFlags.VANILLA_SET));
	}

}
