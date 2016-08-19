package sonar.logistics.parts;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import mcmultipart.multipart.ISlotOccludingPart;
import mcmultipart.multipart.PartSlot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import sonar.logistics.api.connecting.IDataEmitter;
import sonar.logistics.api.connecting.IDataReceiver;

public class DataReceiverPart extends FacingMultipart implements IDataReceiver, ISlotOccludingPart {

	public ArrayList<IDataEmitter> emitters = new ArrayList();
	public ArrayList<Integer> networks = new ArrayList();

	public DataReceiverPart() {
		super();
	}

	public DataReceiverPart(EnumFacing dir) {
		super(dir);
	}

	public void addEmitter(IDataEmitter emitter) {
		if (!emitters.contains(emitter)) {
			emitters.add(emitter);
		}
		networks = buildList();
	}

	public ArrayList<Integer> buildList() {
		ArrayList<Integer> networks = new ArrayList();
		emitters.forEach(emitter -> networks.add(emitter.getNetworkID()));
		return networks;
	}

	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		double p = 0.0625;
		double height = p * 12, width = p * 8, length = p * 14;

		switch (face) {
		case EAST:
			list.add(new AxisAlignedBB(1, 0, (width) / 2, 1 - length, height, 1 - width / 2));
			break;
		case NORTH:
			list.add(new AxisAlignedBB((width) / 2, 0, length, 1 - width / 2, height, 0));
			break;
		case SOUTH:
			list.add(new AxisAlignedBB((width) / 2, 0, 1, 1 - width / 2, height, 1 - length));
			break;
		case WEST:
			list.add(new AxisAlignedBB(length, 0, (width) / 2, 0, height, 1 - width / 2));
			break;
		default:
			break;

		}
	}

	@Override
	public ArrayList<Integer> getConnectedNetworks() {
		return networks;
	}

	@Override
	public EnumSet<PartSlot> getSlotMask() {
		return getOccludedSlots();
	}

	@Override
	public EnumSet<PartSlot> getOccludedSlots() {
		return EnumSet.of(PartSlot.getFaceSlot(face), PartSlot.CENTER);
	}
}
