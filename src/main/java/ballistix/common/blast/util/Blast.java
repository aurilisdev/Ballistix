package ballistix.common.blast.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;

import ballistix.Ballistix;
import ballistix.api.blast.IBlast;
import ballistix.api.blast.IHasCustomRender;
import ballistix.api.blast.IMovingBlast;
import ballistix.api.event.BlastEvent;
import ballistix.api.event.BlastEvent.ConstructBlastEvent;
import ballistix.api.event.BlastEvent.PostBlastEvent;
import ballistix.api.event.BlastEvent.PreBlastEvent;
import ballistix.common.entity.EntityBlast;
import ballistix.compatibility.griefdefender.GriefDefenderHandler;
import modularforcefields.common.settings.MFFSConstants;
import modularforcefields.common.tile.TileFortronFieldProjector;
import modularforcefields.common.world.FortronFieldData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.fml.ModList;

public abstract class Blast {
    private static final GameProfile BALLISTIX_PROFILE = new GameProfile(
	    UUID.fromString("2e2b0c9e-6f5a-4a8b-9d5c-3b43f3d2c2b1"), "Ballistix");

    public static ServerPlayer resolveActor(ServerLevel level, @Nullable Entity owner) {
	if (owner instanceof ServerPlayer sp) {
	    return sp;
	}
	if (owner instanceof LivingEntity living && living.getUUID() != null) {
	    GameProfile profile = new GameProfile(living.getUUID(), living.getName().getString());
	    return FakePlayerFactory.get(level, profile);
	}

	return FakePlayerFactory.get(level, BALLISTIX_PROFILE);
    }

    public static final HashMap<ResourceLocation, IBlast> BLAST_MAP = new HashMap<>();
    public static final HashMap<Item, IBlast> ITEM_TO_BLAST_MAP = new HashMap<>();
    public static final HashMap<IBlast, Item> BLAST_TO_GRENADE_MAP = new HashMap<>();
    public static final HashMap<IBlast, Item> BLAST_TO_MINECART_MAP = new HashMap<>();

    public BlockPos position;
    public Level world;
    public boolean hasStarted;
    public boolean shouldRenderCustomClient;
    public boolean isRepeating = false;
    public @Nullable Entity owner;
    public @Nullable Entity blastEntity;

    protected Blast(Level world, BlockPos position, @Nullable Entity owner, @Nullable Entity blastEntity) {
	this.world = world;
	this.position = position;
	this.owner = owner;
	this.blastEntity = blastEntity;
    }

    public static boolean canBreakBlock(Level level, ServerPlayer player, BlockPos pos, BlockState state) {
	BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(level, pos, state, player);
	boolean canceled = MinecraftForge.EVENT_BUS.post(event);
	return !canceled;
    }

    public static boolean canBreakBlockState(Level world, BlockState state, BlockPos pos, @Nullable Entity owner) {
	if (!(world instanceof ServerLevel)) {
	    return false;
	}
	if (state.isAir())
	    return false;
	if (ModList.get().isLoaded(Ballistix.GRIEF_DEFENDER_ID)) {
	    if (!GriefDefenderHandler.shouldHarmBlock(pos)) {
		return false;
	    }
	}
	ServerPlayer onlineOwner = resolveActor((ServerLevel) world, owner);

	return canBreakBlock(world, onlineOwner, pos, state);
    }

    public static boolean canPlaceBlockState(Level level, BlockState toPlace, BlockPos pos,
	    @Nullable BlockState placedAgainst, @Nullable Entity owner) {
	if (!(level instanceof ServerLevel))
	    return false;
	BlockState against = (placedAgainst != null) ? placedAgainst : level.getBlockState(pos);

	BlockSnapshot snapshot = BlockSnapshot.create(level.dimension(), level, pos);

	BlockEvent.EntityPlaceEvent event = new BlockEvent.EntityPlaceEvent(snapshot, against, owner);
	MinecraftForge.EVENT_BUS.post(event);
	return !event.isCanceled();
    }

    public static boolean canHarmBlock(BlockPos pos) {
	boolean canHarm = true;
	if (ModList.get().isLoaded(Ballistix.GRIEF_DEFENDER_ID)) {
	    canHarm = GriefDefenderHandler.shouldHarmBlock(pos);
	}
	return canHarm;
    }

    public static boolean canHarmEntity(Entity entity) {
	boolean canHarm = true;
	if (ModList.get().isLoaded(Ballistix.GRIEF_DEFENDER_ID)) {
	    canHarm = GriefDefenderHandler.shouldEntityBeHarmed(entity);
	}
	return canHarm;
    }

    public static boolean canSpawnParticle(BlockPos pos) {
	boolean canSpawn = true;
	if (ModList.get().isLoaded(Ballistix.GRIEF_DEFENDER_ID)) {
	    canSpawn = GriefDefenderHandler.shouldAddParticle(pos);
	}
	return canSpawn;
    }

    public boolean isInstantaneous() {
	return true;
    }

    public abstract IBlast getBlastType();

    public void doPreExplode() {
    }

    public boolean doExplode(int callCount) {
	if (world.isClientSide && (shouldRenderCustomClient || this.isInstantaneous())
		&& this instanceof IHasCustomRender renderer) {
	    renderer.produceParticles();
	}
	return false;
    }

    public void doPostExplode() {
    }

    @Deprecated(since = "Should not be called externally!", forRemoval = false)
    public final void preExplode() {
	PreBlastEvent evt = new PreBlastEvent(world, this);
	MinecraftForge.EVENT_BUS.post(evt);

	if (!evt.isCanceled()) {
	    doPreExplode();
	}
    }

    @Deprecated(since = "Should not be called externally!", forRemoval = false)
    public final boolean explode(int callcount) {
	BlastEvent evt = new BlastEvent(world, this);
	MinecraftForge.EVENT_BUS.post(evt);
	if (!evt.isCanceled()) {
	    return doExplode(callcount);
	}
	return true;
    }

    @Deprecated(since = "Should not be called externally!", forRemoval = false)
    public final void postExplode() {
	PostBlastEvent evt = new PostBlastEvent(world, this);
	MinecraftForge.EVENT_BUS.post(evt);

	if (!evt.isCanceled()) {
	    doPostExplode();
	}
    }

    public EntityBlast performExplosion() {
	ConstructBlastEvent evt = new ConstructBlastEvent(world, this);
	MinecraftForge.EVENT_BUS.post(evt);
	Explosion explosion = new Explosion(world, blastEntity,
		DamageSource.explosion(owner instanceof LivingEntity ent ? ent : null), null, position.getX(),
		position.getY(), position.getZ(), 3, true, BlockInteraction.DESTROY);
	if (!ForgeEventFactory.onExplosionStart(world, explosion) && !evt.isCanceled()) {
	    if (isInstantaneous() && !(this instanceof IHasCustomRender)) {
		doPreExplode();
		doExplode(0);
		doPostExplode();
	    } else if (!world.isClientSide) {
		EntityBlast entity = new EntityBlast(world, blastEntity);
		entity.setPos(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5);
		entity.setBlastType(getBlastType());
		if (this instanceof IMovingBlast moving) {
		    entity.setPersistant(moving.persistenceTicks(), moving.movementTicks());
		}
		world.addFreshEntity(entity);
		return entity;
	    }
	}
	return null;
    }

    protected void attackEntities(float size, Explosion explosion) {
	this.attackEntities(size, true, explosion);
    }

    protected void attackEntities(float size, boolean useRaytrace, Explosion explosion) {
	Map<Player, Vec3> playerKnockbackMap = Maps.newHashMap();
	float doubleSize = size * 2.0F;
	int x0 = Mth.floor(position.getX() - (double) doubleSize - 1.0D);
	int x1 = Mth.floor(position.getX() + (double) doubleSize + 1.0D);
	int y0 = Mth.floor(position.getY() - (double) doubleSize - 1.0D);
	int y1 = Mth.floor(position.getY() + (double) doubleSize + 1.0D);
	int z0 = Mth.floor(position.getZ() - (double) doubleSize - 1.0D);
	int z1 = Mth.floor(position.getZ() + (double) doubleSize + 1.0D);

	List<Entity> entities = world.getEntities(null, new AABB(x0, y0, z0, x1, y1, z1));

	Vec3 posVector = new Vec3(position.getX(), position.getY(), position.getZ());

	for (Entity entity : entities) {

	    boolean ignoreEntity = entity.ignoreExplosion() || !canHarmEntity(entity);
	    if (ignoreEntity) {
		continue;
	    }

	    double normalizedDiameter = Mth.sqrt((float) entity.distanceToSqr(posVector)) / doubleSize;
	    if (normalizedDiameter > 1.0D) {
		continue;
	    }
	    double deltaX = entity.getX() - position.getX();
	    double deltaY = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - position.getY();
	    double deltaZ = entity.getZ() - position.getZ();
	    double deltaDistance = Mth.sqrt((float) (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ));

	    if (deltaDistance == 0.0D) {
		continue;
	    }

	    deltaX = deltaX / deltaDistance;
	    deltaY = deltaY / deltaDistance;
	    deltaZ = deltaZ / deltaDistance;

	    double seenAmount = useRaytrace ? Explosion.getSeenPercent(posVector, entity) : 1;

	    double damageAmount = (1.0D - normalizedDiameter) * seenAmount;

	    entity.hurt(explosion.getDamageSource(),
		    (int) ((damageAmount * damageAmount + damageAmount) / 2.0D * 7.0D * doubleSize + 1.0D));

	    double actualDamage = damageAmount;

	    if (entity instanceof LivingEntity le) {
		double damage = damageAmount;
		double i = ProtectionEnchantment.getExplosionKnockbackAfterDampener(le, damageAmount);
		if (i > 0) {
		    damage *= Mth.clamp(1.0D - i * 0.15D, 0.0D, 1.0D);
		}

		actualDamage = damage;
	    }

	    entity.push(deltaX * actualDamage, deltaY * actualDamage, deltaZ * actualDamage);

	    if (entity instanceof Player playerentity) {
		if (!playerentity.isSpectator()
			&& (!playerentity.isCreative() || !playerentity.getAbilities().flying)) {
		    playerKnockbackMap.put(playerentity,
			    new Vec3(deltaX * damageAmount, deltaY * damageAmount, deltaZ * damageAmount));
		}
	    }
	}
	for (Entry<Player, Vec3> entry : playerKnockbackMap.entrySet()) {
	    if (entry.getKey() instanceof ServerPlayer serverplayerentity) {
		serverplayerentity.connection.send(new ClientboundExplodePacket(position.getX(), position.getY(),
			position.getZ(), size, new ArrayList<>(), entry.getValue()));
	    }
	}
    }

    // public FakePlayer getFakePlayer(ServerLevel level) {
    // return Conastants.SHOULD_EXPLOSIONS_BYPASS_CLAIMS ? null :
    // FakePlayerFactory.get(level, FAKE_PLAYER_PROFILE);
    // }

    public static interface BlastFactory<T extends Blast> {
	T create(Level world, BlockPos pos, @Nullable Entity owner, @Nullable Entity blastEntity);
    }

    public static enum GriefPreventionMethod {

	NONE, GRIEF_DEFENDER, SABER_FACTIONS;
    }

    public static void damageFortronField(ServerLevel level, BlockPos fieldPos, double damagePercentage) {

	FortronFieldData data = FortronFieldData.get(level);

	for (long owner : data.getOwners(fieldPos)) {

	    TileFortronFieldProjector projector = data.getLoadedProjector(level, owner);

	    if (projector == null) {
		continue;
	    }

	    double maxHealth = MFFSConstants.FORTRONFIELD_MAXHEALTH;
	    double damage = maxHealth * damagePercentage;

	    projector.health.setValue(projector.health.getValue() - damage);
	}
    }

    public static void damageFortronProjector(ServerLevel level, long owner, double damagePercentage) {

	FortronFieldData data = FortronFieldData.get(level);

	TileFortronFieldProjector projector = data.getLoadedProjector(level, owner);
	if (projector != null) {

	    double maxHealth = MFFSConstants.FORTRONFIELD_MAXHEALTH;
	    double damage = maxHealth * damagePercentage;

	    projector.health.setValue(projector.health.getValue() - damage);
	}
    }
}
