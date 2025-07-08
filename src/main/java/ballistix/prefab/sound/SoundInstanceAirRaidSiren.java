package ballistix.prefab.sound;

import ballistix.common.tile.TileAirRaidSiren;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvent;
import voltaic.prefab.sound.TickableSoundTile;
import voltaic.prefab.utilities.WorldUtils;

public class SoundInstanceAirRaidSiren extends TickableSoundTile<TileAirRaidSiren> {

    public SoundInstanceAirRaidSiren(SoundEvent event, TileAirRaidSiren tile, double range, boolean repeat) {
        super(event, tile, range, repeat);
    }

    @Override
    public void tick() {
        if (!tile.shouldPlaySound() || this.tile.isRemoved()) {
            this.stop();
        }
        this.pitch = tile.pitch.getValue().floatValue();
        PlayerEntity player = Minecraft.getInstance().player;
        double distance = WorldUtils.distanceBetweenPositions(player.blockPosition(), this.tile.getBlockPos());
        if (distance <= (tile.range.getValue() + 1)) {
            this.volume = tile.volume.getValue().floatValue();
        } else if (distance > (tile.range.getValue() + 1) && distance <= tile.range.getValue() * 2) {
            this.volume = (float) (tile.volume.getValue() / (distance - tile.range.getValue()));
        } else if (distance > tile.range.getValue() * 2) {
            this.volume = 0.0F;
        } else {
            this.volume = tile.volume.getValue().floatValue();
        }
    }

}
