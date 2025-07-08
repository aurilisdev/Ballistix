package ballistix.common.tile;

import ballistix.common.inventory.container.ContainerAirRaidSiren;
import ballistix.prefab.sound.SoundBarrierMethods;
import ballistix.registers.BallistixSounds;
import ballistix.registers.BallistixTiles;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import voltaic.prefab.properties.types.PropertyTypes;
import voltaic.prefab.properties.variant.SingleProperty;
import voltaic.prefab.sound.ITickableSound;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.type.ComponentContainerProvider;
import voltaic.prefab.tile.components.type.ComponentTickable;
import voltaic.prefab.utilities.BlockEntityUtils;

public class TileAirRaidSiren extends GenericTile implements ITickableSound {

    public static final double MIN_VOLUME = 0;
    public static final double MAX_VOLUME = 1;

    public static final double MIN_PITCH = 0.5;
    public static final double MAX_PITCH = 2;

    public static final int MIN_RANGE = 1;
    public static final int MAX_RANGE = 256;

    public final SingleProperty<Boolean> hasRedstoneSignal = property(new SingleProperty<>(PropertyTypes.BOOLEAN, "hasredstone", false));
    public final SingleProperty<Double> volume = property(new SingleProperty<>(PropertyTypes.DOUBLE, "volume", 0.5D));
    public final SingleProperty<Double> pitch = property(new SingleProperty<>(PropertyTypes.DOUBLE, "pitch", 1.0D));
    public final SingleProperty<Integer> range = property(new SingleProperty<>(PropertyTypes.INTEGER, "range", 32));

    private boolean isPlaying = false;

    public TileAirRaidSiren() {
        super(BallistixTiles.TILE_AIRRAIDSIREN.get());
        addComponent(new ComponentTickable(this).tickClient(this::tickClient));
        addComponent(new ComponentContainerProvider("airraidsiren", this).createMenu((id, player) -> new ContainerAirRaidSiren(id, player, new Inventory(0), getCoordsArray())));
    }

    public void tickClient(ComponentTickable componentTickable) {
        if(!isPlaying && shouldPlaySound()) {
            isPlaying = true;
            SoundBarrierMethods.playAirRaidSirenSound(BallistixSounds.SOUND_AIRRAIDSIREN.get(), this, 32);
        }
    }

    @Override
    public void setNotPlaying() {
        isPlaying = false;
    }

    @Override
    public boolean shouldPlaySound() {
        return hasRedstoneSignal.getValue();
    }

    @Override
    public void onNeightborChanged(BlockPos neighbor, boolean blockStateTrigger) {
        if(!level.isClientSide) {
            boolean currVal = hasRedstoneSignal.getValue();
            hasRedstoneSignal.setValue(level.getBestNeighborSignal(getBlockPos()) > 0);
            boolean newVal = hasRedstoneSignal.getValue();
            if(currVal ^ newVal) {
                BlockEntityUtils.updateLit(this, newVal);
            }
        }
    }
}
