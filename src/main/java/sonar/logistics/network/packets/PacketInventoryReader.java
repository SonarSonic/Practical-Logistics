package sonar.logistics.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.ITileHandler;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.PacketCoords;
import sonar.core.network.PacketTileEntityHandler;
import sonar.logistics.common.handlers.InventoryReaderHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class PacketInventoryReader extends PacketCoords {

	public ItemStack selected;

	public PacketInventoryReader() {}

	public PacketInventoryReader(int x, int y, int z, ItemStack selected) {
		super(x,y,z);
		this.selected = selected;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		this.selected = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		ByteBufUtils.writeItemStack(buf, selected);
	}

	public static class Handler extends PacketTileEntityHandler<PacketInventoryReader> {

		@Override
		public IMessage processMessage(PacketInventoryReader message, TileEntity te) {
			Object target = FMPHelper.checkObject(te);
			if(target!=null && target instanceof ITileHandler){
				TileHandler handler = ((ITileHandler) target).getTileHandler();;
				if(handler!=null && handler instanceof InventoryReaderHandler){
					InventoryReaderHandler reader = (InventoryReaderHandler) handler;			
					reader.slots[0]=message.selected;				
				}
			}
			return null;
		}
	}
}
