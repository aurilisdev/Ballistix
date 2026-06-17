package ballistix.prefab.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.ChunkPos;

public class BallistixCodecUtils {

    public static final Codec<ChunkPos> CHUNK_POS = RecordCodecBuilder
	    .create(instance -> instance.group(Codec.INT.fieldOf("x").forGetter(instance0 -> instance0.x),
		    Codec.INT.fieldOf("z").forGetter(instance0 -> instance0.z)).apply(instance, ChunkPos::new));

}
