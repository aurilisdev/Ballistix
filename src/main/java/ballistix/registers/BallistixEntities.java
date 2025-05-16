package ballistix.registers;

import ballistix.Ballistix;
import ballistix.common.entity.EntityBlast;
import ballistix.common.entity.EntityBullet;
import ballistix.common.entity.EntityExplosive;
import ballistix.common.entity.EntityGrenade;
import ballistix.common.entity.EntityMinecart;
import ballistix.common.entity.EntityMissile;
import ballistix.common.entity.EntityRailgunRound;
import ballistix.common.entity.EntitySAM;
import ballistix.common.entity.EntityShrapnel;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BallistixEntities {
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Ballistix.ID);

	public static final RegistryObject<EntityType<EntityExplosive>> ENTITY_EXPLOSIVE = ENTITIES.register("explosive", () -> EntityType.Builder.<EntityExplosive>of(EntityExplosive::new, EntityClassification.MISC).fireImmune().sized(1, 1).clientTrackingRange(10).build(Ballistix.ID + ".explosive"));
	public static final RegistryObject<EntityType<EntityGrenade>> ENTITY_GRENADE = ENTITIES.register("grenade", () -> EntityType.Builder.<EntityGrenade>of(EntityGrenade::new, EntityClassification.MISC).fireImmune().sized(0.25f, 0.55f).build(Ballistix.ID + ".grenade"));
	public static final RegistryObject<EntityType<EntityMinecart>> ENTITY_MINECART = ENTITIES.register("minecart", () -> EntityType.Builder.<EntityMinecart>of(EntityMinecart::new, EntityClassification.MISC).fireImmune().clientTrackingRange(8).sized(0.98F, 0.7F).build(Ballistix.ID + ".minecart"));
	public static final RegistryObject<EntityType<EntityBlast>> ENTITY_BLAST = ENTITIES.register("blast", () -> EntityType.Builder.<EntityBlast>of(EntityBlast::new, EntityClassification.MISC).sized(1.0F, 1.0F).fireImmune().build(Ballistix.ID + ".blast"));
	public static final RegistryObject<EntityType<EntityShrapnel>> ENTITY_SHRAPNEL = ENTITIES.register("shrapnel", () -> EntityType.Builder.<EntityShrapnel>of(EntityShrapnel::new, EntityClassification.MISC).fireImmune().sized(0.5f, 0.5f).build(Ballistix.ID + ".shrapnel"));
	public static final RegistryObject<EntityType<EntityMissile>> ENTITY_MISSILE = ENTITIES.register("missile", () -> EntityType.Builder.<EntityMissile>of(EntityMissile::new, EntityClassification.MISC).fireImmune().sized(1.0F, 1.0F).build(Ballistix.ID + ".missile"));
	public static final RegistryObject<EntityType<EntitySAM>> ENTITY_SAM = ENTITIES.register("sam", () -> EntityType.Builder.<EntitySAM>of(EntitySAM::new, EntityClassification.MISC).fireImmune().sized(0.5F, 2.0F).clientTrackingRange(10).build(Ballistix.ID + ".sam"));
	public static final RegistryObject<EntityType<EntityBullet>> ENTITY_BULLET = ENTITIES.register("bullet", () -> EntityType.Builder.<EntityBullet>of(EntityBullet::new, EntityClassification.MISC).fireImmune().sized(0.1F, 0.1F).clientTrackingRange(10).build(Ballistix.ID + ".bullet"));
	public static final RegistryObject<EntityType<EntityRailgunRound>> ENTITY_RAILGUNROUND = ENTITIES.register("railgunround", () -> EntityType.Builder.<EntityRailgunRound>of(EntityRailgunRound::new, EntityClassification.MISC).fireImmune().sized(0.1F, 0.1F).clientTrackingRange(10).build(Ballistix.ID + ".railgunround"));
}
