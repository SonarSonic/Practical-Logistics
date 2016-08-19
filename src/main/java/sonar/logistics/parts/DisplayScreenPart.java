package sonar.logistics.parts;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import sonar.core.network.sync.SyncEnum;
import sonar.core.network.sync.SyncUUID;
import sonar.core.network.utils.IByteBufTile;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.display.IInfoDisplay;
import sonar.logistics.api.display.ScreenLayout;
import sonar.logistics.api.info.DisplayInfo;
import sonar.logistics.api.info.IInfoContainer;
import sonar.logistics.api.info.InfoContainer;

public class DisplayScreenPart extends ScreenMultipart implements IInfoDisplay, IByteBufTile {

	protected SyncUUID uuid = new SyncUUID(-2);
	public SyncEnum<ScreenLayout> layout = new SyncEnum(ScreenLayout.values(), 0);
	public InfoContainer container;	

	public DisplayScreenPart() {
		super();
		this.container().addInfo(new DisplayInfo());
	}

	public DisplayScreenPart(EnumFacing dir, EnumFacing rotation) {
		super(dir, rotation);
		this.container().addInfo(new DisplayInfo());
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
	public ItemStack getItemStack() {
		return new ItemStack(LogisticsItems.displayScreen);
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {

	}

	@Override
	public void readPacket(ByteBuf buf, int id) {

	}

	@Override
	public IInfoContainer container(){
		if(container==null){
			container=new InfoContainer(this);
		}
		return container;
	}

	@Override
	public ScreenLayout getLayout() {
		return layout.getObject();
	}

	@Override
	public int maxInfo() {
		return 2;
	}
}
