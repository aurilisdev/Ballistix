package ballistix.registers;

import ballistix.References;
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
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MenuType.MenuSupplier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BallistixMenuTypes {

	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, References.ID);

	public static final DeferredHolder<MenuType<?>, MenuType<ContainerLauncherControlPanelT1>> CONTAINER_LAUNCHER_CONTROL_PANEL_T1 = register("launchercontrolpaneltier1", ContainerLauncherControlPanelT1::new);
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerLauncherControlPanelT2>> CONTAINER_LAUNCHER_CONTROL_PANEL_T2 = register("launchercontrolpaneltier2", ContainerLauncherControlPanelT2::new);
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerLauncherControlPanelT3>> CONTAINER_LAUNCHER_CONTROL_PANEL_T3 = register("launchercontrolpaneltier3", ContainerLauncherControlPanelT3::new);
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerLauncherPlatformT1>> CONTAINER_LAUNCHER_PLATFORM_T1 = register("launcherplatformtier1", ContainerLauncherPlatformT1::new);
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerLauncherPlatformT2>> CONTAINER_LAUNCHER_PLATFORM_T2 = register("launcherplatformtier2", ContainerLauncherPlatformT2::new);
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerLauncherPlatformT3>> CONTAINER_LAUNCHER_PLATFORM_T3 = register("launcherplatformtier3", ContainerLauncherPlatformT3::new);
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerFireControlRadar>> CONTAINER_FIRECONTROLRADAR = register("firecontrolradar", ContainerFireControlRadar::new);
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerSearchRadar>> CONTAINER_SEARCHRADAR = register("searchradar", ContainerSearchRadar::new);
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerESMTower>> CONTAINER_ESMTOWER = register("esmtower", ContainerESMTower::new);
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerSAMTurret>> CONTAINER_SAMTURRET = register("samturret", ContainerSAMTurret::new);
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerCIWSTurret>> CONTAINER_CIWSTURRET = register("ciwsturret", ContainerCIWSTurret::new);
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerLaserTurret>> CONTAINER_LASERTURRET = register("laserturret", ContainerLaserTurret::new);
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerRailgunTurret>> CONTAINER_RAILGUNTURRET = register("railgunturret", ContainerRailgunTurret::new);

	private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> register(String id, MenuSupplier<T> supplier) {
		return MENU_TYPES.register(id, () -> new MenuType<>(supplier, FeatureFlags.VANILLA_SET));
	}

}
