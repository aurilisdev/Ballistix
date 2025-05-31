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
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
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

    public static HashMap<ServerLevel, HashSet<Integer>> validuuids = new HashMap<>();

    public ItemTracker() {
        super((ElectricItemProperties) new ElectricItemProperties().capacity(1666666.66667).receive(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).extract(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).stacksTo(1), BallistixCreativeTabs.MAIN, item -> Items.AIR);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level context, List<Component> tooltip, TooltipFlag flagIn) {
    	Component name = BallistixTextUtils.tooltip("tracker.none");
		if (hasTarget(stack)) {
			Entity entity = context.getEntity(getUUID(stack));
			if (entity != null) {
				name = entity.getName();
			}
		}
        tooltip.add(BallistixTextUtils.tooltip("tracker.tracking", name.copy().withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
        super.appendHoverText(stack, context, tooltip, flagIn);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (level instanceof ServerLevel slevel) {
			if ((selected || entity instanceof Player player && player.getOffhandItem() == stack) && hasTarget(stack)) {
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
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
    	if (player != null && player.level() instanceof ServerLevel server && getJoulesStored(stack) >= 150) {
			Inventory inv = player.getInventory();
			inv.removeItem(stack);
			setUUID(stack, entity.getId());
			HashSet<Integer> set = validuuids.getOrDefault(server, new HashSet<>());
			set.add(entity.getId());
			validuuids.put(server, set);
			if (hand == InteractionHand.MAIN_HAND) {
				inv.setItem(inv.selected, stack);
			} else {
				inv.offhand.set(0, stack);
			}
			extractPower(stack, 150, false);
		}
        return InteractionResult.PASS;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
    	return !oldStack.is(newStack.getItem());
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
		CompoundTag tag = stack.getOrCreateTag();
		tag.remove(X);
		tag.remove(Z);
		tag.remove(UUID);
	}

	public static boolean hasTargetCoords(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
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
        for (Entry<ServerLevel, HashSet<Integer>> en : validuuids.entrySet()) {
            Iterator<Integer> it = en.getValue().iterator();
            while (it.hasNext()) {
                int uuid = it.next();
                Entity ent = en.getKey().getEntity(uuid);
                if (ent == null || ent.isRemoved()) {
                    it.remove();
                }
            }
        }
    }

    public static record Target(double x, double z) {

        public static final Codec<Target> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.fieldOf("x").forGetter(Target::x),
                Codec.DOUBLE.fieldOf("z").forGetter(Target::z)
        ).apply(instance, Target::new));

        public static final StreamCodec<ByteBuf, Target> STREAM_CODEC = new StreamCodec<ByteBuf, ItemTracker.Target>() {
			
			@Override
			public void encode(ByteBuf buf, Target data) {
				buf.writeDouble(data.x);
				buf.writeDouble(data.z);
			}
			
			@Override
			public Target decode(ByteBuf buf) {
				return new Target(buf.readDouble(), buf.readDouble());
			}
		};

    }

}
