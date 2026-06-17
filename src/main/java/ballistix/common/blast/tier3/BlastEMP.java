package ballistix.common.blast.tier3;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import ballistix.api.blast.IBlast;
import ballistix.api.blast.IHasCustomRender;
import ballistix.common.blast.util.Blast;
import ballistix.common.blast.util.thread.ThreadSimpleBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConfig;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import voltaic.api.electricity.ICapabilityElectrodynamic;
import voltaic.api.item.IItemElectric;
import voltaic.registers.VoltaicCapabilities;

public class BlastEMP extends Blast implements IHasCustomRender {

    public BlastEMP(Level world, BlockPos position, @Nullable Entity owner, @Nullable Entity blastEntity) {
	super(world, position, owner, blastEntity);
    }

    @Override
    public void doPreExplode() {
	if (!world.isClientSide) {
	    thread = new ThreadSimpleBlast(world, position,
		    (int) BallistixConfig.INSTANCE.EXPLOSIVE_EMP_RADIUS.getAsDouble(), Integer.MAX_VALUE, null,
		    getBlastType().id());
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
	    pertick = (int) (thread.results.size()
		    / BallistixConfig.INSTANCE.EXPLOSIVE_ANTIMATTER_DURATION.getAsDouble() + 1);
	    cachedIterator = thread.results.iterator();
	}
	int finished = pertick;
	while (cachedIterator.hasNext()) {
	    if (finished-- < 0) {
		break;
	    }
	    BlockPos p = new BlockPos(cachedIterator.next()).offset(position);

	    if (!canHarmBlock(p)) {
		continue;
	    }
	    BlockEntity entity = world.getBlockEntity(p);
	    if (entity != null) {
		for (Direction dir : Direction.values()) {

		    ICapabilityElectrodynamic electro = world.getCapability(
			    VoltaicCapabilities.CAPABILITY_ELECTRODYNAMIC_BLOCK, p, world.getBlockState(p), entity,
			    dir);

		    if (electro != null) {

			electro.setJoulesStored(0);

		    } else {
			IEnergyStorage fe = world.getCapability(Capabilities.EnergyStorage.BLOCK, p,
				world.getBlockState(p), entity, dir);

			if (fe != null) {
			    fe.extractEnergy(Integer.MAX_VALUE, false);
			}
		    }
		}
	    }
	}
	if (!cachedIterator.hasNext()) {
	    float doubleSize = (float) (BallistixConfig.INSTANCE.EXPLOSIVE_EMP_RADIUS.getAsDouble() * 2.0F);
	    int x0 = Mth.floor(position.getX() - (double) doubleSize - 1.0D);
	    int x1 = Mth.floor(position.getX() + (double) doubleSize + 1.0D);
	    int y0 = Mth.floor(position.getY() - (double) doubleSize - 1.0D);
	    int y1 = Mth.floor(position.getY() + (double) doubleSize + 1.0D);
	    int z0 = Mth.floor(position.getZ() - (double) doubleSize - 1.0D);
	    int z1 = Mth.floor(position.getZ() + (double) doubleSize + 1.0D);

	    List<Entity> entities = world.getEntities(null, new AABB(x0, y0, z0, x1, y1, z1));

	    for (Entity entity : entities) {

		if (!entity.isAlive() || !canHarmEntity(entity)) {
		    continue;
		}

		IEnergyStorage entityFE = entity.getCapability(Capabilities.EnergyStorage.ENTITY, null);

		if (entityFE != null && entityFE.canExtract()) {
		    while (entityFE.getEnergyStored() > 0) {
			entityFE.extractEnergy(Integer.MAX_VALUE, false);
		    }
		}

		if (entity instanceof Player player) {
		    Inventory inv = player.getInventory();
		    for (int i = 0; i < inv.getContainerSize(); i++) {

			ItemStack stack = inv.getItem(i);

			if (stack.isEmpty()) {
			    continue;
			}

			IEnergyStorage itemFE = stack.getCapability(Capabilities.EnergyStorage.ITEM);

			if (itemFE != null && itemFE.canExtract()) {
			    while (itemFE.getEnergyStored() > 0) {
				itemFE.extractEnergy(Integer.MAX_VALUE, false);
			    }
			}

			if (stack.getItem() instanceof IItemElectric electric) {
			    while (electric.getJoulesStored(stack) > 0) {
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
