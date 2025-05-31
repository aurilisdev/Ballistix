package ballistix.common.item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import ballistix.Ballistix;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixCreativeTabs;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import voltaic.api.codec.StreamCodec;
import voltaic.prefab.item.ElectricItemProperties;
import voltaic.prefab.item.ItemElectric;
import voltaic.prefab.utilities.object.TransferPack;

@EventBusSubscriber(modid = Ballistix.ID, bus = EventBusSubscriber.Bus.FORGE)
public class ItemTracker extends ItemElectric {

    public static final double USAGE = 150;
    
    public static final String X = "target_x";
	public static final String Z = "target_z";

	public static final String UUID = "uuid";

    public static HashMap<ServerWorld, HashSet<Integer>> validuuids = new HashMap<>();

    public ItemTracker() {
        super((ElectricItemProperties) new ElectricItemProperties().capacity(1666666.66667).receive(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).extract(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).stacksTo(1), () -> BallistixCreativeTabs.MAIN);
    }

    @Override
    public void appendHoverText(ItemStack stack, World context, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    	ITextComponent name = BallistixTextUtils.tooltip("tracker.none");
		if (hasTarget(stack)) {
			Entity entity = context.getEntity(getUUID(stack));
			if (entity != null) {
				name = entity.getName();
			}
		}
        tooltip.add(BallistixTextUtils.tooltip("tracker.tracking", name.copy().withStyle(TextFormatting.GRAY)).withStyle(TextFormatting.DARK_GRAY));
        super.appendHoverText(stack, context, tooltip, flagIn);
    }

    @Override
    public void inventoryTick(ItemStack stack, World level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (level instanceof ServerWorld) {
        	ServerWorld slevel = (ServerWorld) level;
			if ((selected || entity instanceof PlayerEntity && ((PlayerEntity) entity).getOffhandItem() == stack) && hasTarget(stack)) {
				int uuid = getUUID(stack);
				if (validuuids.containsKey(level) && validuuids.get(level).contains(uuid)) {
					Entity ent = slevel.getEntity(uuid);
					if (ent != null) {
						setX(stack, ent.position().x);
						setZ(stack, ent.position().z);
					}
				} else {
					wipeData(stack);
				}
			}
		}
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
    	if (player != null && player.level instanceof ServerWorld && getJoulesStored(stack) >= 150) {
    		ServerWorld server = (ServerWorld) player.level;
			PlayerInventory inv = player.inventory;
			inv.removeItem(stack);
			setUUID(stack, entity.getId());
			HashSet<Integer> set = validuuids.getOrDefault(server, new HashSet<>());
			set.add(entity.getId());
			validuuids.put(server, set);
			if (hand == Hand.MAIN_HAND) {
				inv.setItem(inv.selected, stack);
			} else {
				inv.offhand.set(0, stack);
			}
			extractPower(stack, 150, false);
		}
        return ActionResultType.PASS;
    }

    @Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return oldStack.getItem() != newStack.getItem();
	}
    
    public static double getX(ItemStack stack) {
		return stack.getOrCreateTag().getDouble(X);
	}

	public static double getZ(ItemStack stack) {
		return stack.getOrCreateTag().getDouble(Z);
	}

	public static int getUUID(ItemStack stack) {
		return stack.getOrCreateTag().getInt(UUID);
	}

	public static void setX(ItemStack stack, double x) {
		stack.getOrCreateTag().putDouble(X, x);
	}

	public static void setZ(ItemStack stack, double z) {
		stack.getOrCreateTag().putDouble(Z, z);
	}

	public static void setUUID(ItemStack stack, int uuid) {
		stack.getOrCreateTag().putInt(UUID, uuid);
	}

	public static void wipeData(ItemStack stack) {
		CompoundNBT tag = stack.getOrCreateTag();
		tag.remove(X);
		tag.remove(Z);
		tag.remove(UUID);
	}

	public static boolean hasTargetCoords(ItemStack stack) {
		CompoundNBT tag = stack.getOrCreateTag();
		return tag.contains(X) && tag.contains(Z);
	}

	public static boolean hasTarget(ItemStack stack) {
		return stack.getOrCreateTag().contains(UUID);
	}

    @SubscribeEvent
    public static void tick(ServerTickEvent event) {
    	if(event.phase != Phase.START) {
    		return;
    	}
        for (Entry<ServerWorld, HashSet<Integer>> en : validuuids.entrySet()) {
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

    public static class Target {

        public static final Codec<Target> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.fieldOf("x").forGetter(Target::x),
                Codec.DOUBLE.fieldOf("z").forGetter(Target::z)
        ).apply(instance, Target::new));

        public static final StreamCodec<PacketBuffer, Target> STREAM_CODEC = new StreamCodec<PacketBuffer, ItemTracker.Target>() {
			
			@Override
			public void encode(PacketBuffer buf, Target data) {
				buf.writeDouble(data.x);
				buf.writeDouble(data.z);
			}
			
			@Override
			public Target decode(PacketBuffer buf) {
				return new Target(buf.readDouble(), buf.readDouble());
			}
		};
		
		private final double x;
		private final double z;
		
		public Target(double x, double z) {
			this.x = x;
			this.z = z;
		}
		
		public double x() {
			return x;
		}
		
		public double z() {
			return z;
		}

    }

}
