package ballistix.common.tile;

import ballistix.Ballistix;
import ballistix.api.blast.IBlast;
import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.api.silo.ILauncherControlPanel;
import ballistix.api.silo.ILauncherPlatform;
import ballistix.api.silo.SiloRegistry;
import ballistix.common.blast.util.Blast;
import ballistix.common.block.subtype.SubtypeBallistixMachine;
import ballistix.common.inventory.container.ContainerVLS;
import ballistix.common.item.ItemLaserDesignator;
import ballistix.common.item.ItemMissile;
import ballistix.common.item.ItemRadarGun;
import ballistix.common.settings.BallistixConstants;
import ballistix.common.tile.silo.TileLauncherControlPanelT1;
import ballistix.registers.BallistixItems;
import ballistix.registers.BallistixSounds;
import ballistix.registers.BallistixTiles;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.ForgeChunkManager;
import voltaic.api.multiblock.subnodebased.TileMultiSubnode;
import voltaic.api.multiblock.subnodebased.parent.IMultiblockParentBlock;
import voltaic.api.multiblock.subnodebased.parent.IMultiblockParentTile;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.*;
import voltaic.prefab.utilities.BlockEntityUtils;
import voltaic.prefab.utilities.NBTUtils;
import voltaic.prefab.utilities.object.CachedTileOutput;
import voltaic.registers.VoltaicCapabilities;

public class TileVerticalLaunchSilo extends GenericTile implements ILauncherControlPanel, ILauncherPlatform, IMultiblockParentTile {

    public static final int COOLDOWN = 100;

    public static final int MISSILE_SLOT = 0;
    public static final int EXPLOSIVE_SLOT = 1;

    public SingleProperty<Integer> frequency = property(new SingleProperty<>(PropertyTypes.INTEGER, "frequency", 0).onChange((prop, prevFreq) -> {

        if (level == null || level.isClientSide) {
            return;
        }

        int newFreq = prop.getValue();

        SiloRegistry.unregisterSilo(prevFreq, this);
        SiloRegistry.registerSilo(newFreq, this);

    }));

    public SingleProperty<BlockPos> target = property(new SingleProperty<>(PropertyTypes.BLOCK_POS, "target", BlockPos.ZERO));

    public SingleProperty<Boolean> hasExplosive = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "hasexplosive", false));
    public SingleProperty<Boolean> hasMissile = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "hasmissile", false));

    private int cooldown = 100;
    public final SingleProperty<Boolean> shouldLaunch = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "shouldlaunch", false));

    public TileVerticalLaunchSilo() {
        super(BallistixTiles.TILE_VLS.get());

        addComponent(new ComponentTickable(this).tickServer(this::tickServer));
        addComponent(new ComponentElectrodynamic(this, false, true).voltage(VoltaicCapabilities.DEFAULT_VOLTAGE).maxJoules(BallistixConstants.MISSILESILO_USAGE).setInputDirections(BlockEntityUtils.MachineDirection.values()));
        addComponent(new ComponentInventory(this, ComponentInventory.InventoryBuilder.newInv().inputs(3)).setDirectionsBySlot(0, BlockEntityUtils.MachineDirection.values()).setDirectionsBySlot(1, BlockEntityUtils.MachineDirection.values()).valid(this::isItemValidForSlot));
        addComponent(new ComponentContainerProvider("vls", this).createMenu((id, player) -> new ContainerVLS(id, player, getComponent(IComponentType.Inventory), getCoordsArray())));
        addComponent(new ComponentForgeEnergy(this));

    }

    protected void tickServer(ComponentTickable tickable) {
        Direction facing = getFacing();

        if (target.getValue() == null) {
            target.setValue(getBlockPos());
        }

        ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);

        if (cooldown > 0 || electro.getJoulesStored() < BallistixConstants.MISSILESILO_USAGE * getTier()) {
            cooldown--;
            return;
        }

        boolean hasRedstone = level.hasNeighborSignal(getBlockPos());


        ILauncherPlatform platform = this;

        if (!platform.hasMissile() || (platform.hasExplosive() && platform.hasSAM()) || (!platform.hasExplosive() && !platform.hasSAM()) || (!hasRedstone && !shouldLaunch.getValue())) {
            return;
        }

        int inaccuracy = 10; // Tier 1 frame

        shouldLaunch.setValue(false);

        double dist = TileLauncherControlPanelT1.calculateDistance(worldPosition, target.getValue());

        if (platform.getRange() < dist) {
            return;
        }

        int newCool = platform.launch(this, hasRedstone, inaccuracy);
        if (newCool != -1) {
            cooldown = newCool;
        }
        electro.joules(electro.getJoulesStored() - BallistixConstants.MISSILESILO_USAGE * getTier());
    }

    protected boolean isItemValidForSlot(int index, ItemStack stack, ComponentInventory inv) {
    	Item item = stack.getItem();
        if (index == 0) {
            return (item instanceof ItemMissile && ((ItemMissile) item).missile.tier() <= getTier()) || stack.getItem() == BallistixItems.ITEM_AAMISSILEMK2.get();
        } else if (index == 1) {
        	IBlast blast = Blast.ITEM_TO_BLAST_MAP.get(item);
            return blast != null && blast.tier() <= getTier() && blast.tier() > -1;
        } else if (index == 2) {
            return stack.getItem() == BallistixItems.ITEM_RADARGUN.get() || stack.getItem() == BallistixItems.ITEM_LASERDESIGNATOR.get();
        }
        return false;
    }

    @Override
    public void onBlockDestroyed() {
        if (level.isClientSide) {
            return;
        }
        SiloRegistry.unregisterSilo(frequency.getValue(), this);

        ChunkPos pos = level.getChunk(worldPosition).getPos();

        ForgeChunkManager.forceChunk((ServerWorld) level, Ballistix.ID, getBlockPos(), pos.x, pos.z, false, true);

    }

    @Override
    public void onPlace(BlockState oldState, boolean isMoving) {
        super.onPlace(oldState, isMoving);
        if (level.isClientSide) {
            return;
        }
        ChunkPos pos = level.getChunk(worldPosition).getPos();

		ForgeChunkManager.forceChunk((ServerWorld) level, Ballistix.ID, getBlockPos(), pos.x, pos.z, true, true);
    }

    @Override
    public void onInventoryChange(ComponentInventory inv, int index) {
        handleMissile(inv, index);
        handleExplosive(inv, index);
        handleSync(inv, index);
    }

    private void handleMissile(ComponentInventory inv, int index) {
        if (index == 0 || index == -1) {

            ItemStack missile = inv.getItem(0);

            if (missile.isEmpty()) {
                hasMissile.setValue(false);
                return;
            }

            if (missile.getItem() instanceof ItemMissile) {

                hasMissile.setValue(true);

            } else {
                hasMissile.setValue(false);
            }

        }
    }

    private void handleExplosive(ComponentInventory inv, int index) {
        if (index == 1 || index == -1) {
            ItemStack explosive = inv.getItem(1);
            if ((!explosive.isEmpty() && Blast.ITEM_TO_BLAST_MAP.get(explosive.getItem()) != null) || (explosive.isEmpty() && inv.getItem(MISSILE_SLOT).getItem() == BallistixItems.ITEM_AAMISSILEMK2.get())) {
                hasExplosive.setValue(true);
            } else {
                hasExplosive.setValue(false);
            }

        }
    }

    private void handleSync(ComponentInventory inv, int index) {

        if (index == 2 || index == -1) {
        	ItemStack sync = inv.getItem(0);

    		if (sync.isEmpty()) {
    			return;
    		}

    		if (sync.getItem() == BallistixItems.ITEM_LASERDESIGNATOR.get()) {

    			CompoundNBT nbt = sync.getOrCreateTag();
    			nbt.putInt(ItemLaserDesignator.FREQUENCY_KEY, frequency.getValue());

    		} else if (sync.getItem() == BallistixItems.ITEM_RADARGUN.get()) {

    			if (sync.getOrCreateTag().contains(NBTUtils.LOCATION)) {
    				target.setValue(ItemRadarGun.getCoordiantes(sync));
    			}

    		}
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!level.isClientSide) {
            SiloRegistry.registerSilo(frequency.getValue(), this);
        }
    }

    @Override
	public CompoundNBT save(CompoundNBT compound) {
		compound.putInt("silocooldown", cooldown);
		return super.save(compound);
	}

	@Override
	public void load(BlockState state, CompoundNBT compound) {
		super.load(state, compound);
		cooldown = compound.getInt("silocooldown");
	}

	@Override
	public ActionResultType use(PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		ItemStack handStack = player.getItemInHand(hand);
		if (handStack.getItem() == BallistixItems.ITEM_RADARGUN.get() || handStack.getItem() == BallistixItems.ITEM_LASERDESIGNATOR.get()) {
			return ActionResultType.FAIL;
		}
		return super.use(player, hand, hit);
	}

    @Override
    public int getRange() {
        return BallistixConstants.VLS_RANGE;
    }

    @Override
    public int getTier() {
        return 1;
    }

    @Override
    public boolean hasMissile() {
        return hasMissile.getValue();
    }

    @Override
    public boolean hasExplosive() {
        return hasMissile.getValue();
    }

    @Override
    public boolean hasSAM() {
        return false;
    }

    @Override
    public int launch(ILauncherControlPanel panel, boolean redstoneTriggered, int inaccuracy) {
        double length = inaccuracy * level.random.nextDouble();
        double angle = level.random.nextDouble() * 2 * Math.PI;
        int offsetX = (int) (length * Math.cos(angle));
        int offsetZ = (int) (length * Math.sin(angle));

        BlockPos pos = panel.getTarget().offset(offsetX, 0, offsetZ);

        if(launchMissile(pos, panel.getFrequency())) {
            cooldown = COOLDOWN;
        }

        if(cooldown > 0) {
            level.playSound(null, getBlockPos(), BallistixSounds.SOUND_VLSLAUNCH.get(), SoundCategory.BLOCKS, 1.0F, 1.0F);
        }

        return cooldown;

    }

    public boolean launchMissile(BlockPos target, int frequency) {
        ComponentInventory inv = getComponent(IComponentType.Inventory);

        ItemStack mis = inv.getItem(MISSILE_SLOT);

        ItemStack explosive = inv.getItem(EXPLOSIVE_SLOT);
        if (mis.getItem() instanceof ItemMissile && Blast.ITEM_TO_BLAST_MAP.get(explosive.getItem()) != null) {
            IBlast blast = Blast.ITEM_TO_BLAST_MAP.get(explosive.getItem());
            ItemMissile itmissile = (ItemMissile) mis.getItem();
            if (blast.tier() > itmissile.missile.tier() || itmissile.missile.tier() > getTier() || blast.tier() > getTier()) {
                return false;
            }
            VirtualMissile missile = new VirtualMissile(
                    //
                    new Vector3d(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5),
                    //
                    new Vector3d(0, 1, 0),
                    //
                    0.45F,
                    //
                    VirtualMissile.FlightPath.VLS,
                    //
                    getBlockPos().getX() + 0.5F,
                    //
                    getBlockPos().getZ() + 0.5F,
                    //
                    target,
                    //
                    itmissile.missile.ordinal() + 1,
                    //
                    blast,
                    //
                    frequency,
                    //
                    getTier() > 1
                    //
            );

            MissileManager.addMissile(level.dimension(), missile);

            inv.removeItem(MISSILE_SLOT, 1);
            inv.removeItem(EXPLOSIVE_SLOT, 1);

            return true;
        }
        return false;


    }

    @Override
    public BlockPos getTarget() {
        return target.getValue();
    }

    @Override
    public int getFrequency() {
        return frequency.getValue();
    }

    @Override
    public BlockPos getPos() {
        return getBlockPos();
    }

    @Override
    public CachedTileOutput getPlatform() {
        return new CachedTileOutput(getLevel(), getPos());
    }

    @Override
    public CachedTileOutput getSupportFrame() {
        throw new UnsupportedOperationException("Need to implement this");
    }

    @Override
    public void launch() {
        shouldLaunch.setValue(true);
    }

    @Override
    public void setTarget(BlockPos blockPos) {
        target.setValue(blockPos);
    }
    
    @Override
    public void setTargetFromDesignator(BlockPos target) {
    	setTarget(target);
    }

    @Override
    public IMultiblockParentBlock.SubnodeWrapper getSubNodes() {
        return SubtypeBallistixMachine.Subnodes.VLS;
    }

    @Override
	public ActionResultType onSubnodeUse(PlayerEntity player, Hand hand, BlockRayTraceResult hit, TileMultiSubnode subnode) {
		return use(player, hand, hit);
	}

    public void onSubnodeDestroyed(TileMultiSubnode subnode) {
        this.level.destroyBlock(this.worldPosition, true);
    }

    @Override
    public Direction getFacingDirection() {
        return getFacing();
    }

}
