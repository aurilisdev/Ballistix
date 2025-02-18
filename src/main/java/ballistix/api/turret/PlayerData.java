package ballistix.api.turret;

import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.UUIDUtil;

public record PlayerData(UUID id, String name) {

    public static final Codec<PlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("id").forGetter(PlayerData::id),
            Codec.STRING.fieldOf("name").forGetter(PlayerData::name)

    ).apply(instance, PlayerData::new));

}
