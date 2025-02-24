package ballistix.common.tile.turret;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import ballistix.References;
import ballistix.api.turret.ITarget;
import ballistix.common.settings.Constants;
import ballistix.common.tile.radar.TileFireControlRadar;
import electrodynamics.api.capability.ElectrodynamicsCapabilities;
import electrodynamics.common.item.ItemUpgrade;
import electrodynamics.common.item.subtype.SubtypeItemUpgrade;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyType;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentElectrodynamic;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.world.ForgeChunkManager;

public abstract class GenericTileTurret extends GenericTile {

    public final Property<Vec3> turretRotation = property(new Property<>(PropertyType.Vec3, "turrot", getDefaultOrientation()));
    public final Property<Vec3> desiredRotation = property(new Property<>(PropertyType.Vec3, "currot", getDefaultOrientation()));
    public final Property<Vec3> targetMovement = property(new Property<>(PropertyType.Vec3, "movevec", Vec3.ZERO));
    public final Property<Boolean> hasTarget = property(new Property<>(PropertyType.Boolean, "hastarget", false)).onChange((prop, val) -> {

        if(level == null || level.isClientSide) {
            return;
        }

        if(prop.get() && val != prop.get()) {
            movementCooldown = 0;
        } else if (prop.get() != val) {
            movementCooldown = 20;
        }

    });
    public final Property<Boolean> hasNoPower = property(new Property<>(PropertyType.Boolean, "haspower", false));
    public final Property<Boolean> inRange = property(new Property<>(PropertyType.Boolean, "isrange", false));
    public final Property<Double> currentRange;
    public final Property<Double> inaccuracyMultiplier = property(new Property<>(PropertyType.Double, "inaccuracymultiplier", 1.0));
    public final Property<Boolean> canFire = property(new Property<>(PropertyType.Boolean, "canfire", false));
    public final Property<List<String>> whitelistedPlayers = property(new Property<>(PropertyType.StringList, "whitelistedplayers", new ArrayList<>()));

    public final double baseRange;
    public final double rotationSpeedRadians;
    public final double usage;
    public final double minimumRange;
    public final double inaccuracy;
    @Nullable
    public ITarget target;

    private int movementCooldown = 0;

    public GenericTileTurret(BlockEntityType<?> tileEntityTypeIn, BlockPos worldPos, BlockState blockState, double baseRange, double minimumRange, double usage, double rotationSpeedRadians, double inaccuracy) {
        super(tileEntityTypeIn, worldPos, blockState);
        addComponent(new ComponentTickable(this).tickServer(this::tickServer).tickClient(this::tickClient));
        addComponent(new ComponentPacketHandler(this));
        addComponent(new ComponentElectrodynamic(this, false, true).setInputDirections(Direction.DOWN).voltage(ElectrodynamicsCapabilities.DEFAULT_VOLTAGE).maxJoules(usage * 20));
        addComponent(getInventory().validUpgrades(SubtypeItemUpgrade.range));
        addComponent(getContainer());
        this.usage = usage;
        this.baseRange = baseRange;
        this.minimumRange = minimumRange;
        this.rotationSpeedRadians = rotationSpeedRadians;
        this.inaccuracy = inaccuracy;
        currentRange = property(new Property<>(PropertyType.Double, "currentrange", baseRange));
    }


    public void tickServer(ComponentTickable tickable) {

        ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);

        hasNoPower.set(electro.getJoulesStored() < usage);

        if (hasNoPower.get()) {
            return;
        }

        electro.setJoulesStored(electro.getJoulesStored() - usage);

        tickServerActive(tickable);

        target = getTarget(tickable.getTicks());

        if(!isValidPlacement()) {
            return;
        }

        hasTarget.set(target != null);

        double distanceToTarget = 0;

        if (hasTarget.get()) {

            Vec3 interceptionPos = getTargetPosition(target);

            if(interceptionPos != null) {

                Vec3 launchPos = getProjectileLaunchPosition();

                distanceToTarget = TileFireControlRadar.getDistanceToMissile(launchPos, interceptionPos);

                double deltaX = interceptionPos.x - launchPos.x;
                double deltaY = interceptionPos.y - launchPos.y;
                double deltaZ = interceptionPos.z - launchPos.z;

                double sumXZ = deltaX * deltaX + deltaZ * deltaZ;

                double magXZ = Math.sqrt(sumXZ);

                if(magXZ <= 0) {
                    magXZ = 1;
                }

                double thetaY = Math.atan(deltaY / magXZ);

                targetMovement.set(new Vec3(deltaX, deltaY, deltaZ).normalize());

                desiredRotation.set(new Vec3(deltaX / magXZ, Math.sin(thetaY), deltaZ / magXZ));

            }

        } else if(movementCooldown <= 0) {
            desiredRotation.set(getDefaultOrientation());
        } else {
            movementCooldown--;
        }

        inRange.set(distanceToTarget >= minimumRange && distanceToTarget <= currentRange.get());

        if (turretRotation.get().equals(desiredRotation.get())) {

            canFire.set(hasTarget.get() && inRange.get());

        } else if(movementCooldown <= 0) {

            double thetaDesiredXZ = getXZAngleRadians(desiredRotation.get());
            double thetaCurrXZ = getXZAngleRadians(turretRotation.get());

            double angleDifXZ = thetaDesiredXZ - thetaCurrXZ;

            double deltaY = desiredRotation.get().y - turretRotation.get().y;

            if (deltaY < 0) {
                turretRotation.set(turretRotation.get().add(0, -Math.cos(rotationSpeedRadians) * 0.125, 0));
                if (turretRotation.get().y < getMinElevation()) {
                    turretRotation.set(new Vec3(turretRotation.get().x, Math.max(getMinElevation(), desiredRotation.get().y), turretRotation.get().z));
                } else if (turretRotation.get().y < desiredRotation.get().y) {
                    turretRotation.set(new Vec3(turretRotation.get().x, desiredRotation.get().y, turretRotation.get().z));
                }
            } else if (deltaY > 0) {
                turretRotation.set(turretRotation.get().add(0, Math.cos(rotationSpeedRadians) * 0.125, 0));

                if (turretRotation.get().y > getMaxElevation()) {
                    turretRotation.set(new Vec3(turretRotation.get().x, Math.min(getMaxElevation(), desiredRotation.get().y), turretRotation.get().z));
                } else if (turretRotation.get().y > desiredRotation.get().y) {
                    turretRotation.set(new Vec3(turretRotation.get().x, desiredRotation.get().y, turretRotation.get().z));
                }
            }

            if (angleDifXZ >= 0) {

                thetaCurrXZ += rotationSpeedRadians;

            } else {

                thetaCurrXZ -= rotationSpeedRadians;

            }

            //thetaCurrXZ = getXZAngleRadians(turretRotation.get());

            if (angleDifXZ >= 0 && thetaCurrXZ > thetaDesiredXZ) {

                turretRotation.set(new Vec3(desiredRotation.get().x, turretRotation.get().y, desiredRotation.get().z));

            } else if (angleDifXZ < 0 && thetaCurrXZ < thetaDesiredXZ) {

                turretRotation.set(new Vec3(desiredRotation.get().x, turretRotation.get().y, desiredRotation.get().z));

            } else {
                turretRotation.set(new Vec3(Math.cos(thetaCurrXZ), turretRotation.get().y, Math.sin(thetaCurrXZ)));
            }

            canFire.set(hasTarget.get() && turretRotation.get().equals(desiredRotation.get()) && inRange.get());

        } else {
            canFire.set(false);
        }

        if (canFire.get()) {
            fireTickServer(tickable.getTicks());
        }

    }

    public void tickClient(ComponentTickable tickable) {

    }

    public abstract ComponentInventory getInventory();
    public abstract ComponentContainerProvider getContainer();

    public abstract void tickServerActive(ComponentTickable tickable);
    public abstract void fireTickServer(long ticks);

    public abstract Vec3 getDefaultOrientation();

    public abstract Vec3 getProjectileLaunchPosition();

    @Nullable
    public abstract Vec3 getTargetPosition(@Nonnull ITarget target);

    public abstract double getMinElevation();

    public abstract double getMaxElevation();

    @Nullable
    public abstract ITarget getTarget(long ticks);

    public abstract boolean isValidPlacement();

    @Override
    public void onInventoryChange(ComponentInventory inv, int slot) {

        super.onInventoryChange(inv, slot);

        if(slot >= inv.getUpgradeSlotStartIndex() || slot == -1) {

            int rangeUpgrades = 0;

            for(ItemStack stack : inv.getUpgradeContents()) {

                if(stack.getItem() instanceof ItemUpgrade upgrade && upgrade.subtype == SubtypeItemUpgrade.range) {
                    rangeUpgrades += stack.getCount();
                }

            }

            double inaccuracyMulitplier = 1;
            double range = baseRange;

            for(int i = 0; i < rangeUpgrades; i++) {
                inaccuracyMulitplier *= Constants.RANGE_INCREASE_INACCURACY_MULTIPLIER;
                range += 5.55;
            }

            range = Math.min(range, Constants.FIRE_CONTROL_RADAR_RANGE);

            currentRange.set(range);
            inaccuracyMultiplier.set(inaccuracyMulitplier);


        }

    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("turncooldown", movementCooldown);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        movementCooldown = compound.getInt("turncooldown");
    }

    public static double getXZAngleRadians(Vec3 vector) {
        return Math.atan2(vector.z, vector.x);
    }

    public static List<Block> raycastToBlockPos(Level world, BlockPos start, BlockPos end) {

        List<Block> blocks = new ArrayList<>();

        int deltaX = end.getX() - start.getX();
        int deltaY = end.getY() - start.getY();
        int deltaZ = end.getZ() - start.getZ();

        double magnitude = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

        int maxChecks = (int) magnitude;

        double incX = deltaX / magnitude;
        double incY = deltaY / magnitude;
        double incZ = deltaZ / magnitude;

        double x = 0;
        double y = 0;
        double z = 0;

        BlockPos toCheck = start;
        BlockState state;

        int i = 0;

        while (i < maxChecks) {

            x += incX;
            y += incY;
            z += incZ;
            toCheck = new BlockPos((int) Math.ceil(start.getX() + x), (int) Math.ceil(start.getY() + y), (int) Math.ceil(start.getZ() + z));
            if (!toCheck.equals(start) && !toCheck.equals(end)) {
                state = world.getBlockState(toCheck);
                if(!state.isAir() && state.isCollisionShapeFullBlock(world, toCheck)) {
                    blocks.add(state.getBlock());
                }
                //world.setBlockAndUpdate(toCheck, Blocks.COBBLESTONE.defaultBlockState());
            }
            toCheck = new BlockPos((int) Math.floor(start.getX() + x), (int) Math.ceil(start.getY() + y), (int) Math.floor(start.getZ() + z));
            if (!toCheck.equals(start) && !toCheck.equals(end)) {
                state = world.getBlockState(toCheck);
                if(!state.isAir() && state.isCollisionShapeFullBlock(world, toCheck)) {
                    blocks.add(state.getBlock());
                }
                //world.setBlockAndUpdate(toCheck, Blocks.COBBLESTONE.defaultBlockState());
            }

            i++;

        }

        return blocks;
    }

    @Override
    public void setPlacedBy(LivingEntity player, ItemStack stack) {
        super.setPlacedBy(player, stack);
        if(player instanceof Player pl) {
            whitelistedPlayers.get().add(pl.getName().getString());
            whitelistedPlayers.forceDirty();
        }
    }

    @Override
    public void onBlockDestroyed() {
        super.onBlockDestroyed();

        if(!level.isClientSide) {
            ChunkPos pos = level.getChunk(getBlockPos()).getPos();
            ForgeChunkManager.forceChunk((ServerLevel) level, References.ID, getBlockPos(), pos.x, pos.z, false, true);
        }


    }

    @Override
    public void onPlace(BlockState oldState, boolean isMoving) {
        super.onPlace(oldState, isMoving);
        if(!level.isClientSide) {
            ChunkPos pos = level.getChunk(getBlockPos()).getPos();
            ForgeChunkManager.forceChunk((ServerLevel) level, References.ID, getBlockPos(), pos.x, pos.z, true, true);
        }
    }

}
