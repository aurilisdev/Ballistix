package ballistix.api.blast;

import ballistix.common.blast.Blast;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.HashMap;
import java.util.Map;

public class RegisterBlastEvent extends Event implements IModBusEvent {

    private final HashMap<ResourceLocation, IBlast> registeredBlasts = new HashMap<>();
    private final HashMap<IBlast, Item> registeredMinecarts = new HashMap<>();
    private final HashMap<IBlast, Item> registeredGrenades = new HashMap<>();

    public void registerBlast(IBlast blast) {
        registeredBlasts.put(blast.id(), blast);
    }

    public void registerGrenade(IBlast blast, Item item) {
        registeredGrenades.put(blast, item);
    }

    public void registerMinecart(IBlast blast, Item item) {
        registeredMinecarts.put(blast, item);
    }

    public void stashBlasts() {
        Blast.BLAST_MAP.clear();
        Blast.BLAST_MAP.putAll(registeredBlasts);
        Blast.ITEM_TO_BLAST_MAP.clear();
        for(Map.Entry<ResourceLocation, IBlast> entry : registeredBlasts.entrySet()) {
            Blast.ITEM_TO_BLAST_MAP.put(entry.getValue().getExplosiveItem().get(), entry.getValue());
        }
        Blast.BLAST_TO_GRENADE_MAP.clear();
        Blast.BLAST_TO_GRENADE_MAP.putAll(registeredGrenades);
        Blast.BLAST_TO_MINECART_MAP.clear();
        Blast.BLAST_TO_MINECART_MAP.putAll(registeredMinecarts);
    }
}
