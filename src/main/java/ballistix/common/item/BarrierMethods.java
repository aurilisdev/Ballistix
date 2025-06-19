package ballistix.common.item;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import ballistix.Ballistix;
import ballistix.api.entity.IDefusable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

//I fucking hate this game
@EventBusSubscriber(modid = Ballistix.ID, bus = EventBusSubscriber.Bus.FORGE)
public class BarrierMethods {
	
	@SubscribeEvent
    public static void tick(ServerTickEvent event) {
    	if(event.phase != Phase.START) {
    		return;
    	}
        for (Entry<ServerWorld, HashSet<Integer>> en : ItemTracker.VALID_UUIDS.entrySet()) {
            Iterator<Integer> it = en.getValue().iterator();
            while (it.hasNext()) {
                int uuid = it.next();
                Entity ent = en.getKey().getEntity(uuid);
                if (ent == null || !ent.isAlive()) {
                    it.remove();
                }
            }
        }
    }
	
	@SubscribeEvent
	public static void onInteractWithEntity(PlayerInteractEvent.EntityInteractSpecific event) {

		World world = event.getWorld();

		if (world.isClientSide) {
			return;
		}

		PlayerEntity playerIn = event.getPlayer();
		Entity entity = event.getTarget();

		ItemStack stack = playerIn.getItemInHand(event.getHand());

		boolean validItem = stack.getItem() instanceof ItemDefuser && ((ItemDefuser) stack.getItem()).getJoulesStored(stack) >= ItemDefuser.USAGE;

		if (!validItem) {
			return;
		}

		ItemDefuser defuser = (ItemDefuser) stack.getItem();

		if (entity instanceof IDefusable) {

			defuser.extractPower(stack, ItemDefuser.USAGE, false);
			((IDefusable) entity).defuse();

		} else if (entity instanceof TNTEntity) {
			TNTEntity tnt = (TNTEntity) entity;
			entity.remove(false);

			ItemEntity item = new ItemEntity(world, tnt.blockPosition().getX() + 0.5, tnt.blockPosition().getY() + 0.5, tnt.blockPosition().getZ() + 0.5, new ItemStack(Items.TNT));
			defuser.extractPower(stack, 150, false);
			world.addFreshEntity(item);

		}
	}

}
