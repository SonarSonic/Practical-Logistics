package sonar.logistics.common.multiparts;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.display.DisplayType;
import sonar.logistics.api.display.InfoContainer;
import sonar.logistics.api.info.IInfoContainer;
import sonar.logistics.api.viewers.ViewerType;
import sonar.logistics.api.viewers.ViewersList;

public class DisplayScreenPart extends ScreenMultipart {

	public ViewersList viewers = new ViewersList(this, Lists.newArrayList(ViewerType.INFO));
	public InfoContainer container = new InfoContainer(this);
	
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

	@Override
	public IInfoContainer container() {
		return container;
	}
	
	@Override
	public NBTTagCompound writeData(NBTTagCompound tag, SyncType type) {
		super.writeData(tag, type);
		container().writeData(tag, type);
		return tag;
	}

	@Override
	public void readData(NBTTagCompound tag, SyncType type) {
		super.readData(tag, type);
		container().readData(tag, type);
	}
	@Override
	public void writeUpdatePacket(PacketBuffer buf) {
		super.writeUpdatePacket(buf);
		ByteBufUtils.writeTag(buf, container().writeData(new NBTTagCompound(), SyncType.SAVE));
	}

	@Override
	public void readUpdatePacket(PacketBuffer buf) {
		super.readUpdatePacket(buf);
		container().readData(ByteBufUtils.readTag(buf), SyncType.SAVE);
	}

	@Override
	public ViewersList getViewersList() {
		return viewers;
	}
}