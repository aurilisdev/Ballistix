package ballistix.common.blast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ballistix.common.blast.thread.ThreadSimpleBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.settings.BallistixConstants;
import ballistix.compatibility.griefdefender.GriefDefenderHandler;
import ballistix.registers.BallistixSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import voltaic.prefab.utilities.WorldUtils;

public class BlastDarkmatter extends Blast {

    public BlastDarkmatter(Level world, BlockPos position) {
        super(world, position);
    }

    @Override
    public void doPreExplode() {
        if (!world.isClientSide) {
            thread = new ThreadSimpleBlast(world, position, (int) BallistixConstants.EXPLOSIVE_DARKMATTER_RADIUS, Integer.MAX_VALUE, null, getBlastType().ordinal());
            thread.start();
            world.playSound(null, position, BallistixSounds.SOUND_DARKMATTER.get(), SoundSource.BLOCKS, 1, 1);
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
        Explosion ex = new Explosion(world, null, null, null, position.getX(), position.getY(), position.getZ(), (float) BallistixConstants.EXPLOSIVE_DARKMATTER_RADIUS, false, Explosion.BlockInteraction.DESTROY);
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

        int x0 = Mth.floor(x - (double) doubleSize - 1.0D);
        int x1 = Mth.floor(x + (double) doubleSize + 1.0D);
        int y0 = Mth.floor(y - (double) doubleSize - 1.0D);
        int y1 = Mth.floor(y + (double) doubleSize + 1.0D);
        int z0 = Mth.floor(z - (double) doubleSize - 1.0D);
        int z1 = Mth.floor(z + (double) doubleSize + 1.0D);

        List<Entity> entities = world.getEntities(null, new AABB(x0, y0, z0, x1, y1, z1));

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
            double deltaY = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - y;
            double deltaZ = entity.getZ() - z;
            double deltaDistance = Mth.sqrt((float) (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ));
            if (deltaDistance == 0.0D) {
                continue;
            }
            deltaX = deltaX / deltaDistance;
            deltaY = deltaY / deltaDistance;
            deltaZ = deltaZ / deltaDistance;
            double d11 = (-0.2 - (callCount - callAtStart) / 150.0) / deltaDistance;
            entity.setDeltaMovement(entity.getDeltaMovement().add(deltaX * d11, deltaY * d11, deltaZ * d11));
            if (entity instanceof ServerPlayer serverplayerentity) {
                if (!serverplayerentity.isCreative()) {
                    serverplayerentity.connection.send(new ClientboundExplodePacket(x, y, z, size, new ArrayList<>(), new Vec3(deltaX * d11, deltaY * d11, deltaZ * d11)));
                }
            } else if (entity instanceof FallingBlockEntity) {
                entity.remove(RemovalReason.DISCARDED);
            }
        }
        attackEntities((float) ((callCount - callAtStart) / 75.0), ex);
        if (world.random.nextFloat() < 0.5) {
            world.explode(null, position.getX(), position.getY(), position.getZ(), 2, ExplosionInteraction.NONE);
        }
        return false;
    }

    @Override
    public boolean isInstantaneous() {
        return false;
    }

    @Override
    public SubtypeBlast getBlastType() {
        return SubtypeBlast.darkmatter;
    }

}
