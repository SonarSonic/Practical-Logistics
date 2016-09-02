package sonar.logistics.parts;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.display.DisplayType;
import sonar.logistics.api.info.InfoUUID;

public class DisplayScreenPart extends ScreenMultipart  {

	public DisplayScreenPart() {
		super();
	}

	public DisplayScreenPart(EnumFacing dir, EnumFacing rotation) {
		super(dir, rotation);
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(LogisticsItems.displayScreen);
	}

	@Override
	public int maxInfo() {
		return 2;
	}

	@Override
	public boolean monitorsUUID(InfoUUID id) {
		return true;// container().getStoredInfo();
	}

	@Override
	public DisplayType getDisplayType() {
		return DisplayType.SMALL;
	}

	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		double p = 0.0625;
		double height = p * 16, width = 0, length = p * 1;

		switch (face) {
		case EAST:
			list.add(new AxisAlignedBB(1, p * 4, (width) / 2, 1 - length, 1 - p * 4, 1 - width / 2));
			break;
		case NORTH:
			list.add(new AxisAlignedBB((width) / 2, p * 4, length, 1 - width / 2, 1 - p * 4, 0));
			break;
		case SOUTH:
			list.add(new AxisAlignedBB((width) / 2, p * 4, 1, 1 - width / 2, 1 - p * 4, 1 - length));
			break;
		case WEST:
			list.add(new AxisAlignedBB(length, p * 4, (width) / 2, 0, 1 - p * 4, 1 - width / 2));
			break;
		case DOWN:
			list.add(new AxisAlignedBB(0, 0, 0, 1, 0.0625, 1));
			break;
		case UP:
			list.add(new AxisAlignedBB(0, 1 - 0, 0, 1, 1 - 0.0625, 1));
			break;
		default:
			break;

		}
	}
}
