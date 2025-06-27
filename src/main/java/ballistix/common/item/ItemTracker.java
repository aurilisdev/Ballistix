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
import ballistix.registers.BallistixDataComponentTypes;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import voltaic.prefab.item.ElectricItemProperties;
import voltaic.prefab.item.ItemElectric;
import voltaic.prefab.utilities.object.TransferPack;

@EventBusSubscriber(modid = Ballistix.ID, bus = EventBusSubscriber.Bus.GAME)
public class ItemTracker extends ItemElectric {

    public static final double USAGE = 150;

    public static HashMap<ServerLevel, HashSet<Integer>> validuuids = new HashMap<>();

    public ItemTracker() {
        super((ElectricItemProperties) new ElectricItemProperties().capacity(1666666.66667).receive(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).extract(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).stacksTo(1), BallistixCreativeTabs.MAIN, item -> Items.AIR);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        Component name = BallistixTextUtils.tooltip("tracker.none");
        if (stack.has(BallistixDataComponentTypes.TRACKER_TARGET) && stack.has(BallistixDataComponentTypes.TRACKER_ID)) {
            Entity entity = context.level().getEntity(stack.get(BallistixDataComponentTypes.TRACKER_ID));
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
            if ((selected || entity instanceof Player player && player.getOffhandItem() == stack) && stack.has(BallistixDataComponentTypes.TRACKER_TARGET) && stack.has(BallistixDataComponentTypes.TRACKER_ID)) {
                int uuid = stack.get(BallistixDataComponentTypes.TRACKER_ID);
                if (validuuids.containsKey(level) && validuuids.get(level).contains(uuid)) {
                    Entity ent = slevel.getEntity(uuid);
                    if (ent != null) {
                        stack.set(BallistixDataComponentTypes.TRACKER_TARGET, new Target(ent.position().x, ent.position().y));
                    }
                } else {
                    stack.remove(BallistixDataComponentTypes.TRACKER_TARGET);
                    stack.remove(BallistixDataComponentTypes.TRACKER_ID);
                }
            }
        }
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (player != null && player.level() instanceof ServerLevel server && getJoulesStored(stack) >= USAGE) {
            Inventory inv = player.getInventory();
            inv.removeItem(stack);
            stack.set(BallistixDataComponentTypes.TRACKER_ID, entity.getId());
            HashSet<Integer> set = validuuids.getOrDefault(server, new HashSet<>());
            set.add(entity.getId());
            validuuids.put(server, set);
            if (hand == InteractionHand.MAIN_HAND) {
                inv.setItem(inv.selected, stack);
            } else {
                inv.offhand.set(0, stack);
            }
            extractPower(stack, USAGE, false);
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.is(newStack.getItem());
    }

    @SubscribeEvent
    public static void tick(ServerTickEvent.Pre event) {
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

        public static final StreamCodec<ByteBuf, Target> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, Target::x,
                ByteBufCodecs.DOUBLE, Target::z,
                Target::new
        );

    }

}
