package ballistix.api.turret;

import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import voltaic.prefab.utilities.CodecUtils;

public class PlayerData {

	private final UUID id;
	private final String name;

	public PlayerData(UUID id, String name) {
		this.id = id;
		this.name = name;
	}

	public static final Codec<PlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(CodecUtils.UUID_CODEC.fieldOf("id").forGetter(PlayerData::id), Codec.STRING.fieldOf("name").forGetter(PlayerData::name)

	).apply(instance, PlayerData::new));

	public UUID id() {
		return id;
	}

	public String name() {
		return name;
	}

}
