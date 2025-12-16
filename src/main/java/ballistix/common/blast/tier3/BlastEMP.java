package ballistix.common.blast.tier3;

import java.util.Iterator;
import java.util.List;

import ballistix.api.blast.IBlast;
import ballistix.api.blast.IHasCustomRender;
import ballistix.common.blast.util.Blast;
import ballistix.common.blast.util.thread.ThreadSimpleBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConstants;
import ballistix.compatibility.griefdefender.GriefDefenderHandler;
import ballistix.registers.BallistixSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import voltaic.api.electricity.ICapabilityElectrodynamic;
import voltaic.api.item.IItemElectric;
import voltaic.prefab.utilities.CapabilityUtils;
import voltaic.registers.VoltaicCapabilities;

public class BlastEMP extends Blast implements IHasCustomRender {

    public BlastEMP(Level world, BlockPos position) {
        super(world, position);
    }

    @Override
    public void doPreExplode() {
        if (!world.isClientSide) {
            thread = new ThreadSimpleBlast(world, position, (int) BallistixConstants.EXPLOSIVE_EMP_RADIUS, Integer.MAX_VALUE, null, getBlastType().id());
            thread.start();
            world.playSound(null, position, BallistixSounds.SOUND_EMPEXPLOSION.get(), SoundSource.BLOCKS, 25, 1);
        }
    }

    private ThreadSimpleBlast thread;
    private int pertick = -1;

    @Override
    public boolean shouldRender() {
        return pertick > 0;
    }

    private Iterator<BlockPos> cachedIterator;

    @Override
    public boolean doExplode(int callCount) {
        super.doExplode(callCount);
        if (world.isClientSide) {
            return false;
        }
        if (thread == null) {
            return true;
        }
        if (!thread.isComplete) {
            return false;
        }
        hasStarted = true;
        if (pertick == -1) {
            pertick = (int) (thread.results.size() / BallistixConstants.EXPLOSIVE_ANTIMATTER_DURATION + 1);
            cachedIterator = thread.results.iterator();
        }
        int finished = pertick;
        while (cachedIterator.hasNext()) {
            if (finished-- < 0) {
                break;
            }
            BlockPos p = new BlockPos(cachedIterator.next()).offset(position);

            switch (griefPreventionMethod) {
                case GRIEF_DEFENDER:
                    if (!GriefDefenderHandler.shouldHarmBlock(p)) {
                        continue;
                    }
                    break;
                default:
                    break;
            }

            BlockEntity entity = world.getBlockEntity(p);
            if (entity != null) {
                for (Direction dir : Direction.values()) {

                    ICapabilityElectrodynamic electro = entity.getCapability(VoltaicCapabilities.CAPABILITY_ELECTRODYNAMIC_BLOCK, dir).orElse(CapabilityUtils.EMPTY_ELECTRO);

                    if (electro != CapabilityUtils.EMPTY_ELECTRO) {

                        electro.setJoulesStored(0);

                    } else {
                        IEnergyStorage fe = entity.getCapability(ForgeCapabilities.ENERGY, dir).orElse(CapabilityUtils.EMPTY_FE);

                        if (fe != CapabilityUtils.EMPTY_FE) {
                            fe.extractEnergy(Integer.MAX_VALUE, false);
                        }
                    }
                }
            }
        }
        if (!cachedIterator.hasNext()) {
            float doubleSize = (float) (BallistixConstants.EXPLOSIVE_EMP_RADIUS * 2.0F);
            int x0 = Mth.floor(position.getX() - (double) doubleSize - 1.0D);
            int x1 = Mth.floor(position.getX() + (double) doubleSize + 1.0D);
            int y0 = Mth.floor(position.getY() - (double) doubleSize - 1.0D);
            int y1 = Mth.floor(position.getY() + (double) doubleSize + 1.0D);
            int z0 = Mth.floor(position.getZ() - (double) doubleSize - 1.0D);
            int z1 = Mth.floor(position.getZ() + (double) doubleSize + 1.0D);

            List<Entity> entities = world.getEntities(null, new AABB(x0, y0, z0, x1, y1, z1));


            for (Entity entity : entities) {

                if(!entity.isAlive()) {
                    continue;
                }


                switch (griefPreventionMethod) {
                    case NONE:
                        break;
                    case GRIEF_DEFENDER:
                        if (!GriefDefenderHandler.shouldEntityBeHarmed(entity)) {
                            continue;
                        }
                        break;
                    case SABER_FACTIONS:
                        break;

                }

                IEnergyStorage entityFE = entity.getCapability(ForgeCapabilities.ENERGY).orElse(CapabilityUtils.EMPTY_FE);

                if(entityFE != CapabilityUtils.EMPTY_FE && entityFE.canExtract()) {
                    while(entityFE.getEnergyStored() > 0) {
                        entityFE.extractEnergy(Integer.MAX_VALUE, false);
                    }
                }

                if(entity instanceof Player player) {
                    Inventory inv = player.getInventory();
                    for(int i = 0; i < inv.getContainerSize(); i++) {

                        ItemStack stack = inv.getItem(i);

                        if(stack.isEmpty()) {
                            continue;
                        }

                        IEnergyStorage itemFE = stack.getCapability(ForgeCapabilities.ENERGY).orElse(CapabilityUtils.EMPTY_FE);

                        if(itemFE != null && itemFE.canExtract()) {
                            while(itemFE.getEnergyStored() > 0) {
                                itemFE.extractEnergy(Integer.MAX_VALUE, false);
                            }
                        }

                        if(stack.getItem() instanceof IItemElectric electric) {
                            while(electric.getJoulesStored(stack) > 0) {
                                electric.extractPower(stack, Double.MAX_VALUE, false);
                            }
                        }

                        inv.setItem(i, stack);

                    }

                    inv.setChanged();
                }

            }

            return true;
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void produceParticles() {
    }

    @Override
    public boolean isInstantaneous() {
        return false;
    }

    @Override
    public IBlast getBlastType() {
        return SubtypeBlast.emp;
    }

}
