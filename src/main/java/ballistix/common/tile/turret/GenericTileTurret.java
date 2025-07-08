package ballistix.common.tile.turret;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ballistix.Ballistix;
import ballistix.api.turret.ITarget;
import ballistix.common.settings.BallistixConstants;
import ballistix.common.tags.BallistixTags;
import ballistix.common.tile.radar.TileFireControlRadar;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.ForgeChunkManager;
import voltaic.common.item.ItemUpgrade;
import voltaic.common.item.subtype.SubtypeItemUpgrade;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.ListProperty;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.*;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.registers.VoltaicCapabilities;

public abstract class GenericTileTurret extends GenericTile {

    public final SingleProperty<Vector3d> turretRotation = property(new SingleProperty<>(PropertyTypes.VEC3, "turrot", Vector3d.ZERO));
    public final SingleProperty<Vector3d> desiredRotation = property(new SingleProperty<>(PropertyTypes.VEC3, "currot", Vector3d.ZERO));
    public final SingleProperty<Vector3d> targetMovement = property(new SingleProperty<>(PropertyTypes.VEC3, "movevec", Vector3d.ZERO));
    public final SingleProperty<Boolean> hasTarget = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "hastarget", false)).onChange((prop, val) -> {

        if(level == null || level.isClientSide) {
            return;
        }

        if(prop.getValue() && val != prop.getValue()) {
            movementCooldown = 0;
        } else if (prop.getValue() != val) {
            movementCooldown = 20;
        }

    });
    public final SingleProperty<Boolean> hasNoPower = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "haspower", false));
    public final SingleProperty<Boolean> inRange = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "isrange", false));
    public final SingleProperty<Double> currentRange;
    public final SingleProperty<Double> inaccuracyMultiplier = property(new SingleProperty<>(PropertyTypes.DOUBLE, "inaccuracymultiplier", 1.0));
    public final SingleProperty<Boolean> canFire = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "canfire", false));
    public final ListProperty<String> whitelistedPlayers = property(new ListProperty<>(PropertyTypes.STRING_LIST, "whitelistedplayers", new ArrayList<>()));
    public final SingleProperty<Integer> entityTargetingMode = property(new SingleProperty<>(PropertyTypes.INTEGER, "entitytargetingmode", 0));

    public final double baseRange;
    public final double rotationSpeedRadians;
    public final double usage;
    public final double minimumRange;
    public final double inaccuracy;
    @Nullable
    public ITarget target;

    private int movementCooldown = 0;

    public GenericTileTurret(TileEntityType<?> tileEntityTypeIn, double baseRange, double minimumRange, double usage, double rotationSpeedRadians, double inaccuracy) {
        super(tileEntityTypeIn);
        addComponent(new ComponentTickable(this).tickServer(this::tickServer).tickClient(this::tickClient));
        addComponent(new ComponentElectrodynamic(this, false, true).setInputDirections(BlockEntityUtils.MachineDirection.BOTTOM).voltage(VoltaicCapabilities.DEFAULT_VOLTAGE).maxJoules(usage * 20));
        addComponent(getInventory().validUpgrades(SubtypeItemUpgrade.range));
        addComponent(getContainer());
        addComponent(new ComponentForgeEnergy(this));
        this.usage = usage;
        this.baseRange = baseRange;
        this.minimumRange = minimumRange;
        this.rotationSpeedRadians = rotationSpeedRadians;
        this.inaccuracy = inaccuracy;
        currentRange = property(new SingleProperty<>(PropertyTypes.DOUBLE, "currentrange", baseRange));
    }
    
    @Override
    public void setLevelAndPosition(World world, BlockPos pos) {
    	super.setLevelAndPosition(world, pos);
    	//turretRotation.setValue(getDefaultOrientation());
        //desiredRotation.setValue(getDefaultOrientation());
    }


    public void tickServer(ComponentTickable tickable) {

        ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);

        hasNoPower.setValue(electro.getJoulesStored() < usage);

        if (hasNoPower.getValue()) {
            return;
        }

        electro.setJoulesStored(electro.getJoulesStored() - usage);

        tickServerActive(tickable);

        target = getTarget(tickable.getTicks());

        if(!isValidPlacement()) {
            return;
        }

        hasTarget.setValue(target != null);

        double distanceToTarget = 0;

        if (hasTarget.getValue()) {

            Vector3d interceptionPos = getTargetPosition(target);

            if(interceptionPos != null) {

                Vector3d launchPos = getProjectileLaunchPosition();

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

                targetMovement.setValue(new Vector3d(deltaX, deltaY, deltaZ).normalize());

                desiredRotation.setValue(new Vector3d(deltaX / magXZ, Math.sin(thetaY), deltaZ / magXZ));

            }

        } else if(movementCooldown <= 0) {
            desiredRotation.setValue(getDefaultOrientation());
        } else {
            movementCooldown--;
        }

        inRange.setValue(distanceToTarget >= minimumRange && distanceToTarget <= currentRange.getValue());

        if (turretRotation.getValue().equals(desiredRotation.getValue())) {

            canFire.setValue(hasTarget.getValue() && inRange.getValue());

        } else if(movementCooldown <= 0) {

            double thetaDesiredXZ = getXZAngleRadians(desiredRotation.getValue());
            double thetaCurrXZ = getXZAngleRadians(turretRotation.getValue());

            double angleDifXZ = thetaDesiredXZ - thetaCurrXZ;

            double deltaY = desiredRotation.getValue().y - turretRotation.getValue().y;

            if (deltaY < 0) {
                turretRotation.setValue(turretRotation.getValue().add(0, -Math.cos(rotationSpeedRadians) * 0.125, 0));
                if (turretRotation.getValue().y < getMinElevation()) {
                    turretRotation.setValue(new Vector3d(turretRotation.getValue().x, Math.max(getMinElevation(), desiredRotation.getValue().y), turretRotation.getValue().z));
                } else if (turretRotation.getValue().y < desiredRotation.getValue().y) {
                    turretRotation.setValue(new Vector3d(turretRotation.getValue().x, desiredRotation.getValue().y, turretRotation.getValue().z));
                }
            } else if (deltaY > 0) {
                turretRotation.setValue(turretRotation.getValue().add(0, Math.cos(rotationSpeedRadians) * 0.125, 0));

                if (turretRotation.getValue().y > getMaxElevation()) {
                    turretRotation.setValue(new Vector3d(turretRotation.getValue().x, Math.min(getMaxElevation(), desiredRotation.getValue().y), turretRotation.getValue().z));
                } else if (turretRotation.getValue().y > desiredRotation.getValue().y) {
                    turretRotation.setValue(new Vector3d(turretRotation.getValue().x, desiredRotation.getValue().y, turretRotation.getValue().z));
                }
            }

            if (angleDifXZ >= 0) {

                thetaCurrXZ += rotationSpeedRadians;

            } else {

                thetaCurrXZ -= rotationSpeedRadians;

            }

            //thetaCurrXZ = getXZAngleRadians(turretRotation.getValue());

            if (angleDifXZ >= 0 && thetaCurrXZ > thetaDesiredXZ) {

                turretRotation.setValue(new Vector3d(desiredRotation.getValue().x, turretRotation.getValue().y, desiredRotation.getValue().z));

            } else if (angleDifXZ < 0 && thetaCurrXZ < thetaDesiredXZ) {

                turretRotation.setValue(new Vector3d(desiredRotation.getValue().x, turretRotation.getValue().y, desiredRotation.getValue().z));

            } else {
                turretRotation.setValue(new Vector3d(Math.cos(thetaCurrXZ), turretRotation.getValue().y, Math.sin(thetaCurrXZ)));
            }

            canFire.setValue(hasTarget.getValue() && turretRotation.getValue().equals(desiredRotation.getValue()) && inRange.getValue());

        } else {
            canFire.setValue(false);
        }

        if (canFire.getValue()) {
            fireTickServer(tickable.getTicks());
        }

    }

    public void tickClient(ComponentTickable tickable) {

    }

    public abstract ComponentInventory getInventory();
    public abstract ComponentContainerProvider getContainer();

    public abstract void tickServerActive(ComponentTickable tickable);
    public abstract void fireTickServer(long ticks);

    public abstract Vector3d getDefaultOrientation();

    public abstract Vector3d getProjectileLaunchPosition();

    @Nullable
    public abstract Vector3d getTargetPosition(@Nonnull ITarget target);

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

                if(stack.getItem() instanceof ItemUpgrade && ((ItemUpgrade) stack.getItem()).subtype == SubtypeItemUpgrade.range) {
                    rangeUpgrades += stack.getCount();
                }

            }

            double inaccuracyMulitplier = 1;
            double range = baseRange;

            for(int i = 0; i < rangeUpgrades; i++) {
                inaccuracyMulitplier *= BallistixConstants.RANGE_INCREASE_INACCURACY_MULTIPLIER;
                range += 5.55;
            }

            range = Math.min(range, BallistixConstants.FIRE_CONTROL_RADAR_RANGE);

            currentRange.setValue(range);
            inaccuracyMultiplier.setValue(inaccuracyMulitplier);


        }

    }

    @Override
	public CompoundNBT save(CompoundNBT compound) {
        compound.putInt("turncooldown", movementCooldown);
        return super.save(compound);
    }

    @Override
	public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        movementCooldown = compound.getInt("turncooldown");
    }

    public static double getXZAngleRadians(Vector3d vector) {
        return Math.atan2(vector.z, vector.x);
    }

    public static List<Block> raycastToBlockPos(World world, Vector3d start, Vector3d end) {

        List<Block> blocks = new ArrayList<>();

        Vector3d delta = end.subtract(start);
        int maxChecks = (int) Math.ceil(delta.length());

        delta = delta.normalize();

        int i = 0;
        BlockPos toCheck;
        BlockState state;

        while(i < maxChecks) {

            start = start.add(delta);

            //Cieled Y
            toCheck = new BlockPos((int) Math.ceil(start.x), (int) Math.ceil(start.y), (int) Math.ceil(start.z));
            if (!toCheck.equals(start) && !toCheck.equals(end)) {
                state = world.getBlockState(toCheck);
                if (willStopTurrret(state)) {
                    blocks.add(state.getBlock());
                }
            }

            toCheck = new BlockPos((int) Math.ceil(start.x), (int) Math.ceil(start.y), (int) Math.floor(start.z));
            if (!toCheck.equals(start) && !toCheck.equals(end)) {
                state = world.getBlockState(toCheck);
                if (willStopTurrret(state)) {
                    blocks.add(state.getBlock());
                }
            }

            toCheck = new BlockPos((int) Math.floor(start.x), (int) Math.ceil(start.y), (int) Math.ceil(start.z));
            if (!toCheck.equals(start) && !toCheck.equals(end)) {
                state = world.getBlockState(toCheck);
                if (willStopTurrret(state)) {
                    blocks.add(state.getBlock());
                }
            }

            toCheck = new BlockPos((int) Math.floor(start.x), (int) Math.ceil(start.y), (int) Math.floor(start.z));
            if (!toCheck.equals(start) && !toCheck.equals(end)) {
                state = world.getBlockState(toCheck);
                if (willStopTurrret(state)) {
                    blocks.add(state.getBlock());
                }
            }

            // Floored Y

            toCheck = new BlockPos((int) Math.ceil(start.x), (int) Math.floor(start.y), (int) Math.ceil(start.z));
            if (!toCheck.equals(start) && !toCheck.equals(end)) {
                state = world.getBlockState(toCheck);
                if (willStopTurrret(state)) {
                    blocks.add(state.getBlock());
                }
            }

            toCheck = new BlockPos((int) Math.ceil(start.x), (int) Math.floor(start.y), (int) Math.floor(start.z));
            if (!toCheck.equals(start) && !toCheck.equals(end)) {
                state = world.getBlockState(toCheck);
                if (willStopTurrret(state)) {
                    blocks.add(state.getBlock());
                }
            }

            toCheck = new BlockPos((int) Math.floor(start.x), (int) Math.floor(start.y), (int) Math.ceil(start.z));
            if (!toCheck.equals(start) && !toCheck.equals(end)) {
                state = world.getBlockState(toCheck);
                if (willStopTurrret(state)) {
                    blocks.add(state.getBlock());
                }
            }

            toCheck = new BlockPos((int) Math.floor(start.x), (int) Math.floor(start.y), (int) Math.floor(start.z));
            if (!toCheck.equals(start) && !toCheck.equals(end)) {
                state = world.getBlockState(toCheck);
                if (willStopTurrret(state)) {
                    blocks.add(state.getBlock());
                }
            }

            i++;


        }

        return blocks;
    }

    @Override
    public void setPlacedBy(LivingEntity player, ItemStack stack) {
        super.setPlacedBy(player, stack);
        if(player instanceof PlayerEntity) {
            whitelistedPlayers.addValue(((PlayerEntity) player).getName().getString());
        }
    }

    @Override
    public void onBlockDestroyed() {
        super.onBlockDestroyed();

        if(!level.isClientSide) {
            ChunkPos pos = level.getChunk(getBlockPos()).getPos();
            ForgeChunkManager.forceChunk((ServerWorld) level, Ballistix.ID, getBlockPos(), pos.x, pos.z, false, true);
        }


    }

    @Override
    public void onPlace(BlockState oldState, boolean isMoving) {
        super.onPlace(oldState, isMoving);
        if(!level.isClientSide) {
            ChunkPos pos = level.getChunk(getBlockPos()).getPos();
            ForgeChunkManager.forceChunk((ServerWorld) level, Ballistix.ID, getBlockPos(), pos.x, pos.z, true, true);
        }
    }
    
    public static boolean willStopTurrret(BlockState state) {
        if(state.isAir()) {
            return false;
        }
        if(state.is(Blocks.SNOW) && state.getValue(SnowBlock.LAYERS) < 4) {
            return false;
        }
        if(state.is(BallistixTags.Blocks.WHITELISTED_TURRET_BLOCKS)) {
            return false;
        }
        return true;
    }
    
    public static enum TargetingMode {
        ALL, ONLY_PLAYERS, NONE;
    }

}
