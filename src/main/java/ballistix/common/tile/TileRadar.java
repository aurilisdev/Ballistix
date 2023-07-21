package ballistix.common.tile;

import ballistix.registers.BallistixBlockTypes;
import electrodynamics.api.capability.ElectrodynamicsCapabilities;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.ComponentType;
import electrodynamics.prefab.tile.components.type.ComponentDirection;
import electrodynamics.prefab.tile.components.type.ComponentElectrodynamic;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import electrodynamics.prefab.utilities.object.TransferPack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import nuclearscience.common.settings.Constants;

public class TileRadar extends GenericTile {
	public double savedTickRotation;
	public double rotationSpeed;

	public TileRadar(BlockPos pos, BlockState state) {
		super(BallistixBlockTypes.TILE_RADAR.get(), pos, state);
		addComponent(new ComponentTickable(this).tickServer(this::tickServer).tickCommon(this::tickCommon));
		addComponent(new ComponentPacketHandler(this));
		addComponent(new ComponentDirection(this));
		addComponent(new ComponentElectrodynamic(this).voltage(ElectrodynamicsCapabilities.DEFAULT_VOLTAGE).extractPower((x, y) -> TransferPack.EMPTY).input(Direction.UP).input(Direction.DOWN).maxJoules(Constants.FREEZEPLUG_USAGE_PER_TICK * 20));
	}

	public void tickServer(ComponentTickable tickable) {
		ComponentElectrodynamic electro = getComponent(ComponentType.Electrodynamic);
		ComponentInventory inv = getComponent(ComponentType.Inventory);
	}

	protected void tickCommon(ComponentTickable tickable) {
		savedTickRotation += rotationSpeed;
		boolean hasPower = this.<ComponentElectrodynamic>getComponent(ComponentType.Electrodynamic).getJoulesStored() > 0;
		rotationSpeed = Mth.clamp(rotationSpeed + 0.05 * (hasPower ? 1 : -1), 0.0, 1.0);
	}

	static {
	}
}
