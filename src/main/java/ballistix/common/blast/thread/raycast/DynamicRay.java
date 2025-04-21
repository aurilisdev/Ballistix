package ballistix.common.blast.thread.raycast;

import java.util.HashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import voltaic.prefab.block.HashDistanceBlockPos;

public class DynamicRay {

    private float currentX;
    private float currentY;
    private float currentZ;
    private float dx;
    private float dy;
    private float dz;
    private float power;
    private BlockPos currentBlockPos;
    private float power_decrease;
    private ThreadDynamicRaycastBlast mainBlast;

    public DynamicRay(float currentX, float currentY, float currentZ, float dx, float dy, float dz, float power, BlockPos currentBlockPos, float power_decrease, ThreadDynamicRaycastBlast mainBlast) {
        this.currentX = currentX;
        this.currentY = currentY;
        this.currentZ = currentZ;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.power = power;
        this.currentBlockPos = currentBlockPos;
        this.power_decrease = power_decrease;
        this.mainBlast = mainBlast;
    }

    public boolean tick(final BlockPos position, final Level world, final IResistanceCallback callback, final Entity explosionSource, HashMap<BlockPos, BlockState> alreadyDestroyed) {
        if (power > 0.0F) {
            boolean foundNextBlock = false;
            while (!foundNextBlock) {
                BlockPos next = new BlockPos((int) Math.floor(currentX), (int) Math.floor(currentY), (int) Math.floor(currentZ));
                if (!alreadyDestroyed.containsKey(next)) {
                    if (!next.equals(currentBlockPos) && currentBlockPos != position) {
                        foundNextBlock = true;
                        currentBlockPos = next;
                        BlockState block = world.getBlockState(currentBlockPos);
                        if (!block.isAir()) {
                            if (block.getDestroySpeed(world, currentBlockPos) >= 0) {
                                alreadyDestroyed.put(next, block);
                                power -= Math.max(power_decrease, callback.getResistance(world, position, currentBlockPos, explosionSource, block));
                                if (power > 0f) {
                                    synchronized (mainBlast.intermediateResults) {
                                        int idistancesq = (int) (Math.pow(currentBlockPos.getX() - position.getX(), 2) + Math.pow(currentBlockPos.getY() - position.getY(), 2) + Math.pow(currentBlockPos.getZ() - position.getZ(), 2));
                                        HashDistanceBlockPos hashedPos = new HashDistanceBlockPos(currentBlockPos.getX(), currentBlockPos.getY(), currentBlockPos.getZ(), idistancesq);
                                        mainBlast.intermediateResults.add(hashedPos);
                                    }
                                }
                            } else {
                                power = 0;
                                return true;
                            }
                        }
                    }
                    power -= power_decrease;
                } else {
                    BlockState block = alreadyDestroyed.get(next);
                    if (!block.isAir()) {
                        if (block.getDestroySpeed(world, currentBlockPos) >= 0) {
                            power -= Math.max(power_decrease, callback.getResistance(world, position, next, explosionSource, block));
                        }
                    }
                }
                currentX += dx;
                currentY += dy;
                currentZ += dz;
                power -= power_decrease;

            }
            return false;
        } else {
            return true;
        }
    }
}
