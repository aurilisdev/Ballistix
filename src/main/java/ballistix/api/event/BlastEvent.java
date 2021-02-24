package ballistix.api.event;

import ballistix.common.blast.Blast;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class BlastEvent extends Event {

    public World world;
    public Blast iExplosion;

    public BlastEvent(World world, Blast iExplosion) {
	this.world = world;
	this.iExplosion = iExplosion;
    }

    public static class ConstructBlastEvent extends BlastEvent {

	public ConstructBlastEvent(World world, Blast explosion) {
	    super(world, explosion);
	}
    }

    public static class PreBlastEvent extends BlastEvent {

	public PreBlastEvent(World world, Blast explosion) {
	    super(world, explosion);
	}
    }

    public static class DoExplosionEvent extends BlastEvent {

	public DoExplosionEvent(World world, Blast explosion) {
	    super(world, explosion);
	}
    }

    public static class PostBlastEvent extends BlastEvent {

	public PostBlastEvent(World world, Blast explosion) {
	    super(world, explosion);
	}
    }
}