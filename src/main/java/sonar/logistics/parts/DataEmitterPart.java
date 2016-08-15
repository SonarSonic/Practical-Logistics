package sonar.logistics.parts;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import mcmultipart.multipart.ISlotOccludingPart;
import mcmultipart.multipart.PartSlot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import sonar.core.api.utils.BlockCoords;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.connecting.IDataEmitter;
import sonar.logistics.connections.EmitterRegistry;

public class DataEmitterPart extends FacingMultipart implements IDataEmitter, ISlotOccludingPart {

	public int networkID;	

	public DataEmitterPart() {
		super();
	}

	public DataEmitterPart(EnumFacing dir) {
		super(dir);
	}

	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		double p = 0.0625;
		double height = p * 12, width = p * 8, length = p * 14;

		switch (face) {
		case EAST:
			list.add(new AxisAlignedBB(length, 0, (width) / 2, 0, height, 1 - width / 2));
			break;
		case NORTH:
			list.add(new AxisAlignedBB((width) / 2, 0, 1, 1 - width / 2, height, 1 - length));
			break;
		case SOUTH:
			list.add(new AxisAlignedBB((width) / 2, 0, length, 1 - width / 2, height, 0));
			break;
		case WEST:
			list.add(new AxisAlignedBB(1, 0, (width) / 2, 1 - length, height, 1 - width / 2));
			break;
		default:
			break;

		}
	}

	public void onFirstTick() {
		super.onFirstTick();
		EmitterRegistry.addEmitter(this);
	}

	public void onRemoved() {
		super.onRemoved();
		EmitterRegistry.removeEmitter(this);
	}

	public void onUnloaded() {
		super.onUnloaded();
		EmitterRegistry.removeEmitter(this);
	}

	@Override
	public int getNetworkID() {
		return networkID;
	}

	public int getConnectedNetwork() {
		IDataCable cable = LogisticsAPI.getCableHelper().getCableFromCoords(BlockCoords.translateCoords(this.getCoords(), face));
		return cable == null ? -1 : cable.registryID();
	}

	public void onNeighborTileChange(EnumFacing facing) {
		if (facing == face) {
			networkID = getConnectedNetwork();
		}
	}

	@Override
	public EnumSet<PartSlot> getSlotMask() {
		return getOccludedSlots();
	}

	@Override
	public EnumSet<PartSlot> getOccludedSlots() {
		return EnumSet.of(PartSlot.getFaceSlot(face.getOpposite()), PartSlot.CENTER);
	}

	@Override
	public boolean canPlayerConnect(UUID uuid) {
		return true;
	}
}
