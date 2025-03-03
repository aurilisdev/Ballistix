package ballistix.registers;

import ballistix.References;
import ballistix.common.entity.EntityBlast;
import ballistix.common.entity.EntityBullet;
import ballistix.common.entity.EntityExplosive;
import ballistix.common.entity.EntityGrenade;
import ballistix.common.entity.EntityMinecart;
import ballistix.common.entity.EntityMissile;
import ballistix.common.entity.EntityRailgunRound;
import ballistix.common.entity.EntitySAM;
import ballistix.common.entity.EntityShrapnel;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BallistixEntities {
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, References.ID);

	public static final DeferredHolder<EntityType<?>, EntityType<EntityExplosive>> ENTITY_EXPLOSIVE = ENTITIES.register("explosive", () -> EntityType.Builder.<EntityExplosive>of(EntityExplosive::new, MobCategory.MISC).fireImmune().sized(1, 1).clientTrackingRange(10).build(References.ID + ".explosive"));
	public static final DeferredHolder<EntityType<?>, EntityType<EntityGrenade>> ENTITY_GRENADE = ENTITIES.register("grenade", () -> EntityType.Builder.<EntityGrenade>of(EntityGrenade::new, MobCategory.MISC).fireImmune().clientTrackingRange(64).sized(0.25f, 0.55f).build(References.ID + ".grenade"));
	public static final DeferredHolder<EntityType<?>, EntityType<EntityMinecart>> ENTITY_MINECART = ENTITIES.register("minecart", () -> EntityType.Builder.<EntityMinecart>of(EntityMinecart::new, MobCategory.MISC).fireImmune().clientTrackingRange(64).sized(0.98F, 0.7F).build(References.ID + ".minecart"));
	public static final DeferredHolder<EntityType<?>, EntityType<EntityBlast>> ENTITY_BLAST = ENTITIES.register("blast", () -> EntityType.Builder.<EntityBlast>of(EntityBlast::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(512).fireImmune().build(References.ID + ".blast"));
	public static final DeferredHolder<EntityType<?>, EntityType<EntityShrapnel>> ENTITY_SHRAPNEL = ENTITIES.register("shrapnel", () -> EntityType.Builder.<EntityShrapnel>of(EntityShrapnel::new, MobCategory.MISC).fireImmune().sized(0.5f, 0.5f).build(References.ID + ".shrapnel"));
	public static final DeferredHolder<EntityType<?>, EntityType<EntityMissile>> ENTITY_MISSILE = ENTITIES.register("missile", () -> EntityType.Builder.<EntityMissile>of(EntityMissile::new, MobCategory.MISC).clientTrackingRange(512).fireImmune().sized(1.0F, 1.0F).attach(EntityAttachment.PASSENGER, 0.5F, 1.0F, 0).build(References.ID + ".missile"));
	public static final DeferredHolder<EntityType<?>, EntityType<EntitySAM>> ENTITY_SAM = ENTITIES.register("sam", () -> EntityType.Builder.<EntitySAM>of(EntitySAM::new, MobCategory.MISC).clientTrackingRange(512).fireImmune().sized(0.5F, 2.0F).attach(EntityAttachment.PASSENGER, 0F, 0F, 0).build(References.ID + ".sam"));
	public static final DeferredHolder<EntityType<?>, EntityType<EntityBullet>> ENTITY_BULLET = ENTITIES.register("bullet", () -> EntityType.Builder.<EntityBullet>of(EntityBullet::new, MobCategory.MISC).clientTrackingRange(512).fireImmune().sized(0.1F, 0.1F).build(References.ID + ".bullet"));
	public static final DeferredHolder<EntityType<?>, EntityType<EntityRailgunRound>> ENTITY_RAILGUNROUND = ENTITIES.register("railgunround", () -> EntityType.Builder.<EntityRailgunRound>of(EntityRailgunRound::new, MobCategory.MISC).fireImmune().sized(0.1F, 0.1F).clientTrackingRange(512).build(References.ID + ".railgunround"));

}
