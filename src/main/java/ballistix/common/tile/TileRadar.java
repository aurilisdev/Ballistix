package ballistix.common.tile;

import ballistix.common.settings.Constants;
import ballistix.registers.BallistixBlockTypes;
import electrodynamics.api.capability.ElectrodynamicsCapabilities;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentElectrodynamic;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class TileRadar extends GenericTile {
	public double savedTickRotation;
	public double rotationSpeed;

	public TileRadar(BlockPos pos, BlockState state) {
		super(BallistixBlockTypes.TILE_RADAR.get(), pos, state);
		addComponent(new ComponentTickable(this).tickServer(this::tickServer).tickCommon(this::tickCommon));
		addComponent(new ComponentPacketHandler(this));
		addComponent(new ComponentElectrodynamic(this, false, true).voltage(ElectrodynamicsCapabilities.DEFAULT_VOLTAGE).setInputDirections(Direction.DOWN, Direction.UP).maxJoules(Constants.RADAR_USAGE));
	}

	public void tickServer(ComponentTickable tickable) {
		ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);
		electro.joules(electro.getJoulesStored() - Constants.RADAR_USAGE / 20.0);
	}

	protected void tickCommon(ComponentTickable tickable) {
		savedTickRotation += rotationSpeed;
		boolean hasPower = this.<ComponentElectrodynamic>getComponent(IComponentType.Electrodynamic).getJoulesStored() > 0;
		rotationSpeed = Mth.clamp(rotationSpeed + 0.25 * (hasPower ? 1 : -1), 0.0, 10.0);
	}

	static {
	}
}
