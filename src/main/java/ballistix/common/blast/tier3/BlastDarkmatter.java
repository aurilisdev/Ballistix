package ballistix.common.blast.tier3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ballistix.api.blast.IBlast;
import ballistix.common.blast.util.Blast;
import ballistix.common.blast.util.thread.ThreadSimpleBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConstants;
import ballistix.compatibility.griefdefender.GriefDefenderHandler;
import ballistix.registers.BallistixSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import voltaic.prefab.utilities.WorldUtils;

public class BlastDarkmatter extends Blast {

    public BlastDarkmatter(World world, BlockPos position) {
        super(world, position);
    }

    @Override
    public void doPreExplode() {
        if (!world.isClientSide) {
            thread = new ThreadSimpleBlast(world, position, (int) BallistixConstants.EXPLOSIVE_DARKMATTER_RADIUS, Integer.MAX_VALUE, null, getBlastType().id());
            thread.start();
            world.playSound(null, position, BallistixSounds.SOUND_DARKMATTER.get(), SoundCategory.BLOCKS, 1, 1);
        }
    }

    public ThreadSimpleBlast thread;
    private int callAtStart = -1;
    private int pertick = -1;
    public boolean canceled = false;

    private Iterator<BlockPos> cachedIterator;

    @Override
    public boolean doExplode(int callCount) {
        if(world.isClientSide) {
            return false;
        }

        hasStarted = true;
        if (thread == null || canceled) {
            return true;
        }
        Explosion ex = new Explosion(world, null, null, null, position.getX(), position.getY(), position.getZ(), (float) BallistixConstants.EXPLOSIVE_DARKMATTER_RADIUS, false, Explosion.Mode.DESTROY);
        if (thread.isComplete) {
            if (callAtStart == -1) {
                callAtStart = callCount;
            }
            if (pertick == -1) {
                pertick = (int) (thread.results.size() / BallistixConstants.EXPLOSIVE_DARKMATTER_DURATION);
                cachedIterator = thread.results.iterator();
            }
            int finished = pertick;
            while (cachedIterator.hasNext()) {
                if (finished-- < 0) {
                    break;
                }
                BlockPos p = new BlockPos(cachedIterator.next()).offset(position);
                BlockState state = world.getBlockState(p);
                Block block = state.getBlock();
                if (!state.isAir() && state.getDestroySpeed(world, p) >= 0) {
                    switch (griefPreventionMethod) {
                        case NONE :
                            block.wasExploded(world, p, ex);
                            world.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
                            break;
                        case GRIEF_DEFENDER:
                            GriefDefenderHandler.destroyBlock(block, ex, p, world);
                            break;
                        case SABER_FACTIONS:


                            break;
                    }
                }
            }
            if (!cachedIterator.hasNext()) {
                WorldUtils.clearChunkCache();
                return true;
            }
        }

        float x = position.getX();
        float y = position.getY();
        float z = position.getZ();

        float size = (float) BallistixConstants.EXPLOSIVE_DARKMATTER_RADIUS;

        float doubleSize = size * 2.0F;

        int x0 = MathHelper.floor(x - (double) doubleSize - 1.0D);
        int x1 = MathHelper.floor(x + (double) doubleSize + 1.0D);
        int y0 = MathHelper.floor(y - (double) doubleSize - 1.0D);
        int y1 = MathHelper.floor(y + (double) doubleSize + 1.0D);
        int z0 = MathHelper.floor(z - (double) doubleSize - 1.0D);
        int z1 = MathHelper.floor(z + (double) doubleSize + 1.0D);

        List<Entity> entities = world.getEntities(null, new AxisAlignedBB(x0, y0, z0, x1, y1, z1));

        for (Entity entity : entities) {

            switch (griefPreventionMethod) {
                case GRIEF_DEFENDER :
                    if(!GriefDefenderHandler.shouldEntityBeHarmed(entity)) {
                        continue;
                    }
                    break;
                default:
                    break;
            }


            double deltaX = entity.getX() - x;
            double deltaY = (entity instanceof TNTEntity ? entity.getY() : entity.getEyeY()) - y;
            double deltaZ = entity.getZ() - z;
            double deltaDistance = MathHelper.sqrt((float) (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ));
            if (deltaDistance == 0.0D) {
                continue;
            }
            deltaX = deltaX / deltaDistance;
            deltaY = deltaY / deltaDistance;
            deltaZ = deltaZ / deltaDistance;
            double d11 = (-0.2 - (callCount - callAtStart) / 150.0) / deltaDistance;
            entity.setDeltaMovement(entity.getDeltaMovement().add(deltaX * d11, deltaY * d11, deltaZ * d11));
            if (entity instanceof ServerPlayerEntity) {
            	ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) entity;
                if (!serverplayerentity.isCreative()) {
                    serverplayerentity.connection.send(new SExplosionPacket(x, y, z, size, new ArrayList<>(), new Vector3d(deltaX * d11, deltaY * d11, deltaZ * d11)));
                }
            } else if (entity instanceof FallingBlockEntity) {
                entity.remove(false);
            }
        }
        attackEntities((float) ((callCount - callAtStart) / 75.0), ex);
        if (world.random.nextFloat() < 0.5) {
            world.explode(null, position.getX(), position.getY(), position.getZ(), 2, Explosion.Mode.NONE);
        }
        return false;
    }

    @Override
    public boolean isInstantaneous() {
        return false;
    }

    @Override
    public IBlast getBlastType() {
        return SubtypeBlast.darkmatter;
    }

}
