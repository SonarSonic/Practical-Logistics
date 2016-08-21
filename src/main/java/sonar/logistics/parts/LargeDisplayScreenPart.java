package sonar.logistics.parts;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.display.ScreenLayout;
import sonar.logistics.api.info.IInfoContainer;
import sonar.logistics.api.info.InfoUUID;

public class LargeDisplayScreenPart extends ScreenMultipart {

	public LargeDisplayScreenPart() {
		super();
	}

	public LargeDisplayScreenPart(EnumFacing dir, EnumFacing rotation) {
		super(dir, rotation);
	}
	
	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		double p = 0.0625;
		double height = p * 16, width = 0, length = p * 1;

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
		case DOWN:
			list.add(new AxisAlignedBB(0, 0, 0, 1, length, 1));
			break;
		case UP:
			list.add(new AxisAlignedBB(0, 1 - 0, 0, 1, 1 - length, 1));
			break;
		default:
			break;

		}
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(LogisticsItems.largeDisplayScreen);
	}

	@Override
	public boolean canConnect(EnumFacing dir) {
		return false;
	}

	@Override
	public IInfoContainer container() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScreenLayout getLayout() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int maxInfo() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean monitorsUUID(InfoUUID id) {
		// TODO Auto-generated method stub
		return false;
	}
}
