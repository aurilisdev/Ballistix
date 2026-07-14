package ballistix.api.blast;

import com.google.common.base.Objects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import ballistix.prefab.utils.BallistixCodecUtils;
import net.minecraft.util.math.ChunkPos;;

public class AntigravedChunk {

    public static final Codec<AntigravedChunk> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BallistixCodecUtils.CHUNK_POS.fieldOf("pos").forGetter(AntigravedChunk::getPos),
            Codec.INT.fieldOf("time").forGetter(AntigravedChunk::getTime)
    ).apply(instance, AntigravedChunk::new));

    private final ChunkPos pos;
    private int time = 0;

    public AntigravedChunk(ChunkPos pos, int time) {
        this.pos = pos;
        this.time = time;
    }

    public ChunkPos getPos() {
        return pos;
    }

    public int getTime() {
        return time;
    }

    public void decrementTime() {
        time--;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof AntigravedChunk) {
        	AntigravedChunk other = (AntigravedChunk) obj;
            return other.pos.equals(pos) && other.time == time;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pos);
    }
}
