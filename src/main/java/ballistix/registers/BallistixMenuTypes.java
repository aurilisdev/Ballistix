package ballistix.registers;

import ballistix.References;
import ballistix.common.inventory.container.ContainerCIWSTurret;
import ballistix.common.inventory.container.ContainerESMTower;
import ballistix.common.inventory.container.ContainerFireControlRadar;
import ballistix.common.inventory.container.ContainerLaserTurret;
import ballistix.common.inventory.container.ContainerMissileSilo;
import ballistix.common.inventory.container.ContainerRailgunTurret;
import ballistix.common.inventory.container.ContainerSAMTurret;
import ballistix.common.inventory.container.ContainerSearchRadar;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BallistixMenuTypes {
	
	public static final DeferredRegister<ContainerType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, References.ID);

	public static final RegistryObject<ContainerType<ContainerMissileSilo>> CONTAINER_MISSILESILO = register("missilesilo", ContainerMissileSilo::new);
	public static final RegistryObject<ContainerType<ContainerFireControlRadar>> CONTAINER_FIRECONTROLRADAR = register("firecontrolradar", ContainerFireControlRadar::new);
	public static final RegistryObject<ContainerType<ContainerSearchRadar>> CONTAINER_SEARCHRADAR = register("searchradar", ContainerSearchRadar::new);
	public static final RegistryObject<ContainerType<ContainerESMTower>> CONTAINER_ESMTOWER = register("esmtower", ContainerESMTower::new);
	public static final RegistryObject<ContainerType<ContainerSAMTurret>> CONTAINER_SAMTURRET = register("samturret", ContainerSAMTurret::new);
	public static final RegistryObject<ContainerType<ContainerCIWSTurret>> CONTAINER_CIWSTURRET = register("ciwsturret", ContainerCIWSTurret::new);
	public static final RegistryObject<ContainerType<ContainerLaserTurret>> CONTAINER_LASERTURRET = register("laserturret", ContainerLaserTurret::new);
	public static final RegistryObject<ContainerType<ContainerRailgunTurret>> CONTAINER_RAILGUNTURRET = register("railgunturret", ContainerRailgunTurret::new);
	
	private static <T extends Container> RegistryObject<ContainerType<T>> register(String id, ContainerType.IFactory<T> supplier) {
		return MENU_TYPES.register(id, () -> new ContainerType<>(supplier));
	}

}
