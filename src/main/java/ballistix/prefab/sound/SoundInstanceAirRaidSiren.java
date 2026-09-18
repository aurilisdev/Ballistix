package ballistix.prefab.sound;

import ballistix.common.tile.TileAirRaidSiren;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import voltaic.prefab.sound.TickableSoundTile;
import voltaic.prefab.utilities.WorldUtils;

public class SoundInstanceAirRaidSiren extends TickableSoundTile<TileAirRaidSiren> {

    public SoundInstanceAirRaidSiren(SoundEvent event, TileAirRaidSiren tile, double range, boolean repeat) {
	super(event, tile, range, repeat);
    }

    @Override
    public void tick() {
	if (!tile.shouldPlaySound() || tile.isRemoved()) {
	    stop();
	}
	pitch = tile.pitch.getValue().floatValue();
	Player player = Minecraft.getInstance().player;
	if (player == null)
	    return;

	double distance = WorldUtils.distanceBetweenPositions(player.blockPosition(), tile.getBlockPos());
	if (distance <= tile.range.getValue() + 1) {
	    volume = tile.volume.getValue().floatValue();
	} else if (distance > tile.range.getValue() + 1 && distance <= tile.range.getValue() * 2) {
	    volume = (float) (tile.volume.getValue() / (distance - tile.range.getValue()));
	} else if (distance > tile.range.getValue() * 2) {
	    volume = 0.0F;
	} else {
	    volume = tile.volume.getValue().floatValue();
	}
    }

}
