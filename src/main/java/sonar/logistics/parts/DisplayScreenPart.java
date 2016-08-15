package sonar.logistics.parts;

import java.util.List;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

public class DisplayScreenPart extends FacingMultipart {

	public DisplayScreenPart() {
		super();
	}

	public DisplayScreenPart(EnumFacing dir) {
		super(dir);
	}

	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		double p = 0.0625;
		double height = p * 16, width = 0, length = p * 1;

		switch (face) {
		case EAST:
			list.add(new AxisAlignedBB(length, p*4, (width) / 2, 0, 1-p*4, 1 - width / 2));
			break;
		case NORTH:
			list.add(new AxisAlignedBB((width) / 2, p*4, 1, 1 - width / 2, 1-p*4, 1 - length));
			break;
		case SOUTH:
			list.add(new AxisAlignedBB((width) / 2, p*4, length, 1 - width / 2, 1-p*4, 0));
			break;
		case WEST:
			list.add(new AxisAlignedBB(1, p*4, (width) / 2, 1 - length, 1-p*4, 1 - width / 2));
			break;
		default:
			break;

		}
	}
}
