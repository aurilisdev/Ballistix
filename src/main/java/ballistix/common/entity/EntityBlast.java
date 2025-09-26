package ballistix.common.entity;

import javax.annotation.Nullable;

import ballistix.Ballistix;
import ballistix.api.blast.IBlast;
import ballistix.api.blast.IHasCustomRender;
import ballistix.common.blast.util.Blast;
import ballistix.registers.BallistixEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityBlast extends Entity {
	
    private static final DataParameter<Integer> CALLCOUNT = EntityDataManager.defineId(EntityBlast.class, DataSerializers.INT);
    private static final DataParameter<String> TYPE = EntityDataManager.defineId(EntityBlast.class, DataSerializers.STRING);
    private static final DataParameter<Boolean> SHOULDSTARTCUSTOMRENDER = EntityDataManager.defineId(EntityBlast.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> TICKCOUNT = EntityDataManager.defineId(EntityBlast.class, DataSerializers.INT);
    private static final DataParameter<Boolean> SHOULD_PERSIST = EntityDataManager.defineId(EntityBlast.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> PERSISTANCE_TICKS = EntityDataManager.defineId(EntityBlast.class, DataSerializers.INT);
    private static final DataParameter<Integer> TICKS_PERSISTED = EntityDataManager.defineId(EntityBlast.class, DataSerializers.INT);
    private static final DataParameter<Boolean> HAS_MATURED = EntityDataManager.defineId(EntityBlast.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> TICKS_AT_MATURITY = EntityDataManager.defineId(EntityBlast.class, DataSerializers.INT);
    private static final DataParameter<Integer> MOVEMENT_TICKS = EntityDataManager.defineId(EntityBlast.class, DataSerializers.INT);
    private static final DataParameter<Integer> TICKS_MOVING = EntityDataManager.defineId(EntityBlast.class, DataSerializers.INT);
    private static final DataParameter<Boolean> MOVING = EntityDataManager.defineId(EntityBlast.class, DataSerializers.BOOLEAN);

    private Blast blast;
    public ResourceLocation blastId;
    public int callcount = 0;
    public boolean shouldRenderCustom = false;
    public int ticksWhenCustomRender;
    private boolean detonated = false;
    private boolean shouldPersist = false;
    private int persistanceTicks = 0;
    private int ticksPersisted = 0;
    private int movementTicks = 0;
    private int ticksMoving = 0;
    public boolean hasMatured = false; // has the blast completed its initial explosion
    public int ticksAtMaturity = 0; // keeps track of ticks at maturity for rendering purposes if needed
    private boolean moving = false;

    public EntityBlast(EntityType<? extends EntityBlast> type, World worldIn) {
        super(type, worldIn);
        blocksBuilding = true;
    }

    public EntityBlast(World worldIn) {
        this(BallistixEntities.ENTITY_BLAST.get(), worldIn);
    }
    
    @Override
    public boolean shouldRender(double x, double y, double z) {
        return true;
    }
    
    public void setPersistant(int tickCount, int movementTicks) { // set to -1 for infinite
        //shouldPersist = true;
        persistanceTicks = tickCount;
        this.movementTicks = movementTicks;
    }

    public void setBlastType(IBlast explosive) {
        blastId = explosive.id();
        blast = getBlastType().createBlast(level, blockPosition());
    }

    @Nullable
    public IBlast getBlastType() {
        return blastId == null ? null : Blast.BLAST_MAP.get(blastId);
    }

    @Override
    protected void defineSynchedData() {
    	entityData.define(CALLCOUNT, 0);
    	entityData.define(TYPE, "");
    	entityData.define(SHOULDSTARTCUSTOMRENDER, false);
    	entityData.define(TICKCOUNT, 0);
    	entityData.define(SHOULD_PERSIST, false);
        entityData.define(PERSISTANCE_TICKS, 0);
        entityData.define(HAS_MATURED, false);
        entityData.define(TICKS_AT_MATURITY, 0);
        entityData.define(TICKS_PERSISTED, 0);
        entityData.define(MOVEMENT_TICKS, 0);
        entityData.define(MOVING, false);
        entityData.define(TICKS_MOVING, 0);
    }

    @Override
    public void tick() {
        tickCount++;
        if (detonated /* || tickCount > 1000 */) {
            if (!level.isClientSide && tickCount > 20) {
                remove(false);
            }
            return;
        }

        if (!level.isClientSide) {
        	if(blastId != null) {
                entityData.set(TYPE, blastId.toString());
            }
            entityData.set(CALLCOUNT, callcount);
            entityData.set(SHOULDSTARTCUSTOMRENDER, blast instanceof IHasCustomRender && ((IHasCustomRender) blast).shouldRender());
            entityData.set(TICKCOUNT, tickCount);
            entityData.set(SHOULD_PERSIST, shouldPersist);
            entityData.set(PERSISTANCE_TICKS, persistanceTicks);
            entityData.set(TICKS_PERSISTED, ticksPersisted);
            entityData.set(HAS_MATURED, hasMatured);
            entityData.set(TICKS_AT_MATURITY, ticksAtMaturity);
            entityData.set(MOVEMENT_TICKS, movementTicks);
            entityData.set(MOVING, moving);
            entityData.set(TICKS_MOVING, ticksMoving);
        } else {
        	String str = entityData.get(TYPE);
            if(!str.isEmpty()) {
                blastId = new ResourceLocation(str);
            }
            callcount = entityData.get(CALLCOUNT);
            if (!shouldRenderCustom && entityData.get(SHOULDSTARTCUSTOMRENDER)) {
                ticksWhenCustomRender = tickCount;
            }
            shouldRenderCustom = entityData.get(SHOULDSTARTCUSTOMRENDER);
            if (blast != null) {
                blast.shouldRenderCustomClient = shouldRenderCustom;
            }
            tickCount = entityData.get(TICKCOUNT);
            shouldPersist = entityData.get(SHOULD_PERSIST);
            persistanceTicks = entityData.get(PERSISTANCE_TICKS);
            ticksPersisted = entityData.get(TICKS_PERSISTED);
            hasMatured = entityData.get(HAS_MATURED);
            ticksAtMaturity = entityData.get(TICKS_AT_MATURITY);
            movementTicks = entityData.get(MOVEMENT_TICKS);
            moving = entityData.get(MOVING);
            ticksMoving = entityData.get(TICKS_MOVING);
        }

        if (blastId == null) {
            return;
        }

        if (blast == null) {
            blast = getBlastType().createBlast(level, blockPosition());
            if(shouldPersist && hasMatured) {
                blast.isRepeating = true;
            }
        }

        if (blast != null) {

            if(shouldPersist) {

                if(persistanceTicks == -1 || ticksPersisted > persistanceTicks) {
                    detonated = true;
                    return;
                }

                if(hasMatured) {

                    if(moving) {

                        setPos(getX() + getDeltaMovement().x, getY() + getDeltaMovement().y, getZ() + getDeltaMovement().z);

                        ticksMoving++;

                        if(ticksMoving >= movementTicks) {
                            moving = false;
                            ticksMoving = 0;
                            callcount = 0;
                            blast = getBlastType().createBlast(level, blockPosition());
                            blast.isRepeating = true;
                        }

                    } else {

                        if (callcount == 0) {
                            blast.preExplode();
                        } else {
                            if (blast.explode(callcount)) {
                                blast.postExplode();
                                if(!level.isClientSide) {
                                    double dX = level.random.nextDouble() * (level.random.nextBoolean() ? 1 : -1);
                                    double dY = level.random.nextDouble() * (level.random.nextBoolean() ? 1 : -1);
                                    double dZ = level.random.nextDouble() * (level.random.nextBoolean() ? 1 : -1);

                                    //Weights to keep it between min and max build heights

                                    int deltaHeight = level.getMaxBuildHeight() - 0;

                                    float fifths = deltaHeight / 5.0F;

                                    // min weight

                                    if(dY < 0 && getY() <= (0 + fifths)) {

                                        float relativeHeight = (float) (getY() - 0);
                                        float perc = 1.0F - relativeHeight / fifths;

                                        if(level.random.nextFloat() <= perc) {
                                            dY = Math.abs(dY);
                                        }


                                    }

                                    // max weight

                                    if(dY > 0 && getY() >= (0 + fifths * 3)) {

                                        float relativeHeight = (float) (getY() - 0);
                                        float perc = relativeHeight / (fifths * 5);

                                        if(level.random.nextFloat() <= perc) {
                                            dY = -dY;
                                        }


                                    }



                                    setDeltaMovement(dX, dY, dZ);

                                }
                                moving = true;
                                persistanceTicks++;
                            }
                        }
                        callcount++;

                    }


                } else {

                    if (callcount == 0) {
                        blast.preExplode();
                    } else {
                        if (blast.explode(callcount)) {
                            blast.postExplode();
                            hasMatured = true;
                            ticksAtMaturity = tickCount;
                            ticksPersisted = 0;
                            ticksMoving = 0;
                            callcount = 0;
                            //unload the chunk at this point
                            ChunkPos pos = level.getChunk(blockPosition()).getPos();
                            ForgeChunkManager.forceChunk((ServerWorld) level, Ballistix.ID, blockPosition(), pos.x, pos.z, false, true);
                        }
                    }

                    callcount++;
                }

            } else {
                if (callcount == 0) {
                    blast.preExplode();
                } else {
                    if (blast.explode(callcount)) {
                        detonated = true;
                        blast.postExplode();
                    }
                }

                callcount++;

            }




        }
    }

    @Override
	public void onAddedToWorld() {
		super.onAddedToWorld();
		if (!level.isClientSide() && !hasMatured) {
			ChunkPos pos = level.getChunk(blockPosition()).getPos();
			ForgeChunkManager.forceChunk((ServerWorld) level, Ballistix.ID, blockPosition(), pos.x, pos.z, true, true);
		}
	}

	@Override
	public void remove(boolean reason) {
		if (!level.isClientSide && reason == false && !hasMatured) {
			ChunkPos pos = level.getChunk(blockPosition()).getPos();
			ForgeChunkManager.forceChunk((ServerWorld) level, Ballistix.ID, blockPosition(), pos.x, pos.z, false, true);
		}
		super.remove(reason);
	}

	@Override
	protected void addAdditionalSaveData(CompoundNBT compound) {
		ResourceLocation.CODEC.encodeStart(NBTDynamicOps.INSTANCE, blastId).result().ifPresent(tag -> compound.put("type", tag));
		compound.putInt("callcount", callcount);
		compound.putInt("persistanceticks", persistanceTicks);
        compound.putInt("tickspersisted", ticksPersisted);
        compound.putInt("ticksatmaturity", ticksAtMaturity);
        compound.putInt("movementticks", movementTicks);
        compound.putInt("ticksmoving", ticksMoving);
        compound.putBoolean("moivng", moving);
        compound.putBoolean("shouldpersist", shouldPersist);
        compound.putBoolean("hasmatured", hasMatured);
	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT compound) {
		ResourceLocation.CODEC.decode(NBTDynamicOps.INSTANCE, compound.get("type")).result().ifPresent(pair -> blastId = pair.getFirst());
		callcount = compound.getInt("callcount");
		if (blastId != null) {
			setBlastType(getBlastType());
		}
		persistanceTicks = compound.getInt("persistanceticks");
        ticksPersisted = compound.getInt("tickspersisted");
        ticksAtMaturity = compound.getInt("ticksatmaturity");
        shouldPersist = compound.getBoolean("shouldpersist");
        hasMatured = compound.getBoolean("hasmatured");
        movementTicks = compound.getInt("movementticks");
        moving = compound.getBoolean("moving");
        ticksMoving = compound.getInt("ticksmoving");
	}

	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

    public Blast getBlast() {
        return blast;
    }

}
