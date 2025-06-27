package ballistix.common.blast.tier3;

import java.util.Iterator;

import ballistix.api.blast.IBlast;
import ballistix.api.blast.IHasCustomRender;
import ballistix.common.blast.util.Blast;
import ballistix.common.blast.util.thread.ThreadSimpleBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConstants;
import ballistix.compatibility.griefdefender.GriefDefenderHandler;
import ballistix.registers.BallistixSounds;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import voltaic.api.electricity.ICapabilityElectrodynamic;
import voltaic.prefab.utilities.CapabilityUtils;
import voltaic.registers.VoltaicCapabilities;

public class BlastEMP extends Blast implements IHasCustomRender {

    public BlastEMP(World world, BlockPos position) {
        super(world, position);
    }

    @Override
    public void doPreExplode() {
        if (!world.isClientSide) {
            thread = new ThreadSimpleBlast(world, position, (int) BallistixConstants.EXPLOSIVE_EMP_RADIUS, Integer.MAX_VALUE, null, getBlastType().id());
            thread.start();
            world.playSound(null, position, BallistixSounds.SOUND_EMPEXPLOSION.get(), SoundCategory.BLOCKS, 25, 1);
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

            TileEntity entity = world.getBlockEntity(p);
            if (entity != null) {
                for (Direction dir : Direction.values()) {

                    ICapabilityElectrodynamic electro = entity.getCapability(VoltaicCapabilities.CAPABILITY_ELECTRODYNAMIC_BLOCK, dir).orElse(CapabilityUtils.EMPTY_ELECTRO);

                    if (electro != CapabilityUtils.EMPTY_ELECTRO) {

                        electro.setJoulesStored(0);

                    } else {
                        IEnergyStorage fe = entity.getCapability(CapabilityEnergy.ENERGY, dir).orElse(CapabilityUtils.EMPTY_FE);

                        if (fe != CapabilityUtils.EMPTY_FE) {
                            fe.extractEnergy(Integer.MAX_VALUE, false);
                        }
                    }
                }
            } // TODO: Implement player inventory energy clearing
        }
        if (!cachedIterator.hasNext()) {
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
