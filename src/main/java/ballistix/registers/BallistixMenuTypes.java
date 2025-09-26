package ballistix.registers;

import ballistix.Ballistix;
import ballistix.common.inventory.container.ContainerAirRaidSiren;
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
import ballistix.common.inventory.container.ContainerProximityDetector;
import ballistix.common.inventory.container.ContainerRailgunTurret;
import ballistix.common.inventory.container.ContainerSAMTurret;
import ballistix.common.inventory.container.ContainerSearchRadar;
import ballistix.common.inventory.container.ContainerVLS;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BallistixMenuTypes {

	public static final DeferredRegister<ContainerType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, Ballistix.ID);

	public static final RegistryObject<ContainerType<ContainerLauncherControlPanelT1>> CONTAINER_LAUNCHER_CONTROL_PANEL_T1 = register("launchercontrolpaneltier1", ContainerLauncherControlPanelT1::new);
	public static final RegistryObject<ContainerType<ContainerLauncherControlPanelT2>> CONTAINER_LAUNCHER_CONTROL_PANEL_T2 = register("launchercontrolpaneltier2", ContainerLauncherControlPanelT2::new);
	public static final RegistryObject<ContainerType<ContainerLauncherControlPanelT3>> CONTAINER_LAUNCHER_CONTROL_PANEL_T3 = register("launchercontrolpaneltier3", ContainerLauncherControlPanelT3::new);
	public static final RegistryObject<ContainerType<ContainerLauncherPlatformT1>> CONTAINER_LAUNCHER_PLATFORM_T1 = register("launcherplatformtier1", ContainerLauncherPlatformT1::new);
	public static final RegistryObject<ContainerType<ContainerLauncherPlatformT2>> CONTAINER_LAUNCHER_PLATFORM_T2 = register("launcherplatformtier2", ContainerLauncherPlatformT2::new);
	public static final RegistryObject<ContainerType<ContainerLauncherPlatformT3>> CONTAINER_LAUNCHER_PLATFORM_T3 = register("launcherplatformtier3", ContainerLauncherPlatformT3::new);
	public static final RegistryObject<ContainerType<ContainerFireControlRadar>> CONTAINER_FIRECONTROLRADAR = register("firecontrolradar", ContainerFireControlRadar::new);
	public static final RegistryObject<ContainerType<ContainerSearchRadar>> CONTAINER_SEARCHRADAR = register("searchradar", ContainerSearchRadar::new);
	public static final RegistryObject<ContainerType<ContainerESMTower>> CONTAINER_ESMTOWER = register("esmtower", ContainerESMTower::new);
	public static final RegistryObject<ContainerType<ContainerSAMTurret>> CONTAINER_SAMTURRET = register("samturret", ContainerSAMTurret::new);
	public static final RegistryObject<ContainerType<ContainerCIWSTurret>> CONTAINER_CIWSTURRET = register("ciwsturret", ContainerCIWSTurret::new);
	public static final RegistryObject<ContainerType<ContainerLaserTurret>> CONTAINER_LASERTURRET = register("laserturret", ContainerLaserTurret::new);
	public static final RegistryObject<ContainerType<ContainerRailgunTurret>> CONTAINER_RAILGUNTURRET = register("railgunturret", ContainerRailgunTurret::new);
	public static final RegistryObject<ContainerType<ContainerVLS>> CONTAINER_VLS = register("vls", ContainerVLS::new);
	public static final RegistryObject<ContainerType<ContainerProximityDetector>> CONTAINER_PROXIMITYDETECTOR = register("proximitydetector", ContainerProximityDetector::new);
	public static final RegistryObject<ContainerType<ContainerAirRaidSiren>> CONTAINER_AIRRAIDSIREN = register("airraidsiren", ContainerAirRaidSiren::new);

	private static <T extends Container> RegistryObject<ContainerType<T>> register(String id, ContainerType.IFactory<T> supplier) {
		return MENU_TYPES.register(id, () -> new ContainerType<>(supplier));
	}

}
