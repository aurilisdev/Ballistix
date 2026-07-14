package ballistix.common.blast.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

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
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.Explosion.Mode;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.ModList;

public abstract class Blast {
	
	public static final HashMap<ResourceLocation, IBlast> BLAST_MAP = new HashMap<>();
    public static final HashMap<Item, IBlast> ITEM_TO_BLAST_MAP = new HashMap<>();
    public static final HashMap<IBlast, Item> BLAST_TO_GRENADE_MAP = new HashMap<>();
    public static final HashMap<IBlast, Item> BLAST_TO_MINECART_MAP = new HashMap<>();

    public BlockPos position;
    public World world;
    public boolean hasStarted;
    public final GriefPreventionMethod griefPreventionMethod;
    public boolean shouldRenderCustomClient;
    public boolean isRepeating = false;

    protected Blast(World world, BlockPos position) {
        this.world = world;
        this.position = position;
        griefPreventionMethod = getGriefPreventionMethod();
    }

    public static GriefPreventionMethod getGriefPreventionMethod() {
        if (ModList.get().isLoaded(Ballistix.GRIEF_DEFENDER_ID)) {
            return GriefPreventionMethod.GRIEF_DEFENDER;
        }
        return GriefPreventionMethod.NONE;
    }

    public boolean isInstantaneous() {
        return true;
    }

    public abstract IBlast getBlastType();

    public void doPreExplode() {
    }

    public boolean doExplode(int callCount) {
        if (world.isClientSide && (shouldRenderCustomClient || this.isInstantaneous()) && this instanceof IHasCustomRender) {
            ((IHasCustomRender) this).produceParticles();
        }
        return false;
    }

    public void doPostExplode() {
    }

    @Deprecated
    public final void preExplode() {
        PreBlastEvent evt = new PreBlastEvent(world, this);
        MinecraftForge.EVENT_BUS.post(evt);

        if (!evt.isCanceled()) {
            doPreExplode();
        }
    }

    @Deprecated
    public final boolean explode(int callcount) {
        BlastEvent evt = new BlastEvent(world, this);
        MinecraftForge.EVENT_BUS.post(evt);
        if (!evt.isCanceled()) {
            return doExplode(callcount);
        }
        return true;
    }

    @Deprecated
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
        Explosion explosion = new Explosion(world, null, null, null, position.getX(), position.getY(), position.getZ(), 3, true, Mode.DESTROY);
        if (!ForgeEventFactory.onExplosionStart(world, explosion) && !evt.isCanceled()) {
            if (isInstantaneous() && !(this instanceof IHasCustomRender)) {
                doPreExplode();
                doExplode(0);
                doPostExplode();
            } else if (!world.isClientSide) {
                EntityBlast entity = new EntityBlast(world);
                entity.setPos(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5);
                entity.setBlastType(getBlastType());
                if(this instanceof IMovingBlast) {
                	IMovingBlast moving = (IMovingBlast) this;
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
        Map<PlayerEntity, Vector3d> playerKnockbackMap = Maps.newHashMap();
        float doubleSize = size * 2.0F;
        int x0 = MathHelper.floor(position.getX() - (double) doubleSize - 1.0D);
        int x1 = MathHelper.floor(position.getX() + (double) doubleSize + 1.0D);
        int y0 = MathHelper.floor(position.getY() - (double) doubleSize - 1.0D);
        int y1 = MathHelper.floor(position.getY() + (double) doubleSize + 1.0D);
        int z0 = MathHelper.floor(position.getZ() - (double) doubleSize - 1.0D);
        int z1 = MathHelper.floor(position.getZ() + (double) doubleSize + 1.0D);

        List<Entity> entities = world.getEntities(null, new AxisAlignedBB(x0, y0, z0, x1, y1, z1));

        Vector3d posVector = new Vector3d(position.getX(), position.getY(), position.getZ());

        for (Entity entity : entities) {

            boolean ignoreExplosion = entity.ignoreExplosion();

            switch (griefPreventionMethod) {
                case NONE:
                    if (ignoreExplosion) {
                        continue;
                    }
                    break;
                case GRIEF_DEFENDER:
                    if (!GriefDefenderHandler.shouldEntityBeHarmed(entity) && ignoreExplosion) {
                        continue;
                    }
                    break;
                case SABER_FACTIONS:

                    break;

            }

            double normalizedDiameter = MathHelper.sqrt((float) entity.distanceToSqr(posVector)) / doubleSize;
            if (normalizedDiameter > 1.0D) {
                continue;
            }
            double deltaX = entity.getX() - position.getX();
            double deltaY = (entity instanceof TNTEntity ? entity.getY() : entity.getEyeY()) - position.getY();
            double deltaZ = entity.getZ() - position.getZ();
            double deltaDistance = MathHelper.sqrt((float) (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ));

            if (deltaDistance == 0.0D) {
                continue;
            }

            deltaX = deltaX / deltaDistance;
            deltaY = deltaY / deltaDistance;
            deltaZ = deltaZ / deltaDistance;

            double seenAmount = useRaytrace ? Explosion.getSeenPercent(posVector, entity) : 1;

            double damageAmount = (1.0D - normalizedDiameter) * seenAmount;

            entity.hurt(DamageSource.explosion(explosion), (int) ((damageAmount * damageAmount + damageAmount) / 2.0D * 7.0D * doubleSize + 1.0D));

            double actualDamange = damageAmount;

            if (entity instanceof LivingEntity) {
            	LivingEntity le = (LivingEntity) entity;
                double damage = damageAmount;
                double i = ProtectionEnchantment.getExplosionKnockbackAfterDampener(le, damageAmount);
                if (i > 0) {
                    damage *= MathHelper.clamp(1.0D - i * 0.15D, 0.0D, 1.0D);
                }

                actualDamange = damage;
            }

            entity.setDeltaMovement(entity.getDeltaMovement().add(deltaX * actualDamange, deltaY * actualDamange, deltaZ * actualDamange));
            if (entity instanceof PlayerEntity) {
            	PlayerEntity playerentity = (PlayerEntity) entity;
                if (!playerentity.isSpectator() && (!playerentity.isCreative() || !playerentity.abilities.flying)) {
                    playerKnockbackMap.put(playerentity, new Vector3d(deltaX * damageAmount, deltaY * damageAmount, deltaZ * damageAmount));
                }
            }
        }
        for (Entry<PlayerEntity, Vector3d> entry : playerKnockbackMap.entrySet()) {
            if (entry.getKey() instanceof ServerPlayerEntity) {
            	ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) entry.getKey();
                serverplayerentity.connection.send(new SExplosionPacket(position.getX(), position.getY(), position.getZ(), size, new ArrayList<>(), entry.getValue()));
            }
        }
    }

    // public FakePlayer getFakePlayer(ServerWorld level) {
    // return Conastants.SHOULD_EXPLOSIONS_BYPASS_CLAIMS ? null :
    // FakePlayerFactory.get(level, FAKE_PLAYER_PROFILE);
    // }

    public static interface BlastFactory<T extends Blast> {
        T create(World world, BlockPos pos);
    }

    public static enum GriefPreventionMethod {

        NONE, GRIEF_DEFENDER, SABER_FACTIONS;

    }
}
