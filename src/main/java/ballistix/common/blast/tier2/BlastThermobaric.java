package ballistix.common.blast.tier2;

import java.util.Iterator;

import ballistix.api.blast.IHasCustomRender;
import ballistix.client.particle.ParticleOptionsBlastSmoke;
import ballistix.client.shake.CameraShakeEffect;
import ballistix.client.shake.CameraShakeManager;
import ballistix.common.blast.util.BlastLasting;
import ballistix.common.blast.util.thread.raycast.ThreadDynamicRaycastBlast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.packet.NetworkHandler;
import ballistix.common.packet.type.client.particle.BlastParticleSpawnType;
import ballistix.common.packet.type.client.particle.PacketSpawnBlastParticle;
import ballistix.common.settings.BallistixConstants;
import ballistix.compatibility.griefdefender.GriefDefenderHandler;
import ballistix.prefab.utils.ParticleUtilities;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;

public class BlastThermobaric extends BlastLasting implements IHasCustomRender {

    public BlastThermobaric(World world, BlockPos position) {
        super(world, position);
    }

    @Override
    public void doPreExplode() {
        if (!world.isClientSide) {
            thread = new ThreadDynamicRaycastBlast(world, position, (int) BallistixConstants.EXPLOSIVE_THERMOBARIC_SIZE, (float) BallistixConstants.EXPLOSIVE_THERMOBARIC_ENERGY, null);
            thread.start();
        }

    }

    private ThreadDynamicRaycastBlast thread;
    private int pertick = -1;
    private Iterator<BlockPos> cachedIterator;

    @Override
    public boolean doExplode(int callCount) {
        super.doExplode(callCount);
        if (thread == null) {
            return !world.isClientSide;
        }
        if (callCount % 2 == 0) {
            Explosion ex = new Explosion(world, null, null, null, position.getX(), position.getY(), position.getZ(), (float) BallistixConstants.EXPLOSIVE_THERMOBARIC_SIZE, false, Explosion.Mode.DESTROY);
            synchronized (thread.finishedBlocks) {
                if (pertick == -1) {
                    hasStarted = true;
                    attackEntities((float) BallistixConstants.EXPLOSIVE_THERMOBARIC_SIZE * 2, ex);
                    world.playSound(null, position, SoundEvents.GENERIC_EXPLODE, SoundCategory.BLOCKS, 25, 1);
                    pertick = (int) (1200 * 45.0 / BallistixConstants.EXPLOSIVE_THERMOBARIC_DURATION);
                }
                cachedIterator = thread.finishedBlocks.iterator();
                int finished = pertick;
                while (cachedIterator.hasNext()) {
                    if (finished-- < 0) {
                        break;
                    }
                    BlockPos p = cachedIterator.next();
                    Block block = world.getBlockState(p).getBlock();
                    switch (griefPreventionMethod) {
                        case NONE:
                            block.wasExploded(world, p, ex);
                            world.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
                            break;
                        case GRIEF_DEFENDER:
                            GriefDefenderHandler.destroyBlock(block, ex, p, world);
                            break;
                        case SABER_FACTIONS:
                            break;
                    }
                    if (world.random.nextFloat() < 1 / 20.0 && world instanceof ServerWorld) {
                    	ServerWorld serverlevel = (ServerWorld) world;
                    	serverlevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(p), false).forEach(pl -> NetworkHandler.CHANNEL.sendTo(new PacketSpawnBlastParticle(p, BlastParticleSpawnType.EXPLOSIVE_BLOCK_BREAK), pl.connection.connection, NetworkDirection.PLAY_TO_CLIENT));
                    }
                    cachedIterator.remove();
                }
                if (!cachedIterator.hasNext() && thread.isComplete) {
                    attackEntities((float) BallistixConstants.EXPLOSIVE_THERMOBARIC_SIZE * 2, ex);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasShaken = false;

    @Override
    @OnlyIn(Dist.CLIENT)
    public void produceParticles() {
        double x = position.getX() + 0.5;
        double y = position.getY() + 0.5;
        double z = position.getZ() + 0.5;

        double initialSpeed = 0.8;

        if (ticksSinceBlastStart <= 5) {
            // Fireball
            IParticleData particle = new ParticleOptionsBlastSmoke().setParameters(1.0f, 1.0f, 1.0f, 2f, -0.045f, 750, true, true, 40, 0.95);
            ParticleUtilities.spawnParticleSphere(particle, x, y, z, 100, 50, 90, initialSpeed, true);

            // Centersmokes
            initialSpeed = 1; // Increase/decrease to taste
            particle = new ParticleOptionsBlastSmoke().setParameters(0.8f, 0.8f, 0.8f, 2.5f, 0.033f, 750, true, 0.95);
            ParticleUtilities.spawnParticleSphere(particle, x, y, z, 100, 0, 20, initialSpeed, true);

            // Centersmokes
            initialSpeed = 1; // Increase/decrease to taste
            particle = new ParticleOptionsBlastSmoke().setParameters(0.8f, 0.8f, 0.8f, 2.5f, -0.033f, 750, true, 0.95);
            ParticleUtilities.spawnParticleSphere(particle, x, y, z, 100, 0, 20, initialSpeed, true);
        }
        double spawnSize = 3;
        double endSize = BallistixConstants.EXPLOSIVE_THERMOBARIC_SIZE * 7.5;
        int diff = (int) (endSize - spawnSize);
        if (ticksSinceBlastStart > diff) return;
        double size = ParticleUtilities.progressGroundShockwave(world, x, z, ticksSinceBlastStart * 2 / (double) diff, spawnSize, endSize, 0.3);
        if (hasShaken) return;
        Vector3d pos = new Vector3d(x, y, z);
        double realDistance = Minecraft.getInstance().player.position().distanceTo(pos);
        double dist = MathHelper.abs((float) (realDistance - size));
        if (dist < 3) {
            hasShaken = true;
            CameraShakeEffect effect = CameraShakeManager.createBlastSourcedEffect(BallistixConstants.EXPLOSIVE_THERMOBARIC_DURATION / 1.5, endSize, world.getGameTime(), pos);
            CameraShakeManager.addShake(effect);
        }
    }

    @Override
    public boolean isInstantaneous() {
        return false;
    }

    @Override
    public SubtypeBlast getBlastType() {
        return SubtypeBlast.thermobaric;
    }
    // TODO: Finish block model

    @Override
    public boolean isDoneCalculating() {
        if (world.isClientSide) {
            return shouldRenderCustomClient;
        }
        return thread == null || thread.isComplete;
    }

    @Override
    public boolean shouldRender() {
        return pertick > 0;
    }

}
