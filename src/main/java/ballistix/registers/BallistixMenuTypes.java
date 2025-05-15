package ballistix.registers;

import ballistix.Ballistix;
import ballistix.common.inventory.container.ContainerCIWSTurret;
import ballistix.common.inventory.container.ContainerESMTower;
import ballistix.common.inventory.container.ContainerFireControlRadar;
import ballistix.common.inventory.container.ContainerLaserTurret;
import ballistix.common.inventory.container.ContainerLauncherControlPanelT1;
import ballistix.common.inventory.container.ContainerLauncherControlPanelT2;
import ballistix.common.inventory.container.ContainerLauncherControlPanelT3;
import ballistix.common.inventory.container.ContainerLauncherPlatformT1;
import ballistix.common.inventory.container.ContainerLauncherPlatformT2;
import ballistix.common.inventory.container.ContainerLauncherPlatformT3;
import ballistix.common.inventory.container.ContainerRailgunTurret;
import ballistix.common.inventory.container.ContainerSAMTurret;
import ballistix.common.inventory.container.ContainerSearchRadar;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MenuType.MenuSupplier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BallistixMenuTypes {

	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Ballistix.ID);

	public static final RegistryObject< MenuType<ContainerLauncherControlPanelT1>> CONTAINER_LAUNCHER_CONTROL_PANEL_T1 = register("launchercontrolpaneltier1", ContainerLauncherControlPanelT1::new);
	public static final RegistryObject< MenuType<ContainerLauncherControlPanelT2>> CONTAINER_LAUNCHER_CONTROL_PANEL_T2 = register("launchercontrolpaneltier2", ContainerLauncherControlPanelT2::new);
	public static final RegistryObject< MenuType<ContainerLauncherControlPanelT3>> CONTAINER_LAUNCHER_CONTROL_PANEL_T3 = register("launchercontrolpaneltier3", ContainerLauncherControlPanelT3::new);
	public static final RegistryObject< MenuType<ContainerLauncherPlatformT1>> CONTAINER_LAUNCHER_PLATFORM_T1 = register("launcherplatformtier1", ContainerLauncherPlatformT1::new);
	public static final RegistryObject< MenuType<ContainerLauncherPlatformT2>> CONTAINER_LAUNCHER_PLATFORM_T2 = register("launcherplatformtier2", ContainerLauncherPlatformT2::new);
	public static final RegistryObject< MenuType<ContainerLauncherPlatformT3>> CONTAINER_LAUNCHER_PLATFORM_T3 = register("launcherplatformtier3", ContainerLauncherPlatformT3::new);
	public static final RegistryObject<MenuType<ContainerFireControlRadar>> CONTAINER_FIRECONTROLRADAR = register("firecontrolradar", ContainerFireControlRadar::new);
	public static final RegistryObject<MenuType<ContainerSearchRadar>> CONTAINER_SEARCHRADAR = register("searchradar", ContainerSearchRadar::new);
	public static final RegistryObject<MenuType<ContainerESMTower>> CONTAINER_ESMTOWER = register("esmtower", ContainerESMTower::new);
	public static final RegistryObject< MenuType<ContainerSAMTurret>> CONTAINER_SAMTURRET = register("samturret", ContainerSAMTurret::new);
	public static final RegistryObject< MenuType<ContainerCIWSTurret>> CONTAINER_CIWSTURRET = register("ciwsturret", ContainerCIWSTurret::new);
	public static final RegistryObject< MenuType<ContainerLaserTurret>> CONTAINER_LASERTURRET = register("laserturret", ContainerLaserTurret::new);
	public static final RegistryObject< MenuType<ContainerRailgunTurret>> CONTAINER_RAILGUNTURRET = register("railgunturret", ContainerRailgunTurret::new);

	private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String id, MenuSupplier<T> supplier) {
		return MENU_TYPES.register(id, () -> new MenuType<>(supplier));
	}

}
