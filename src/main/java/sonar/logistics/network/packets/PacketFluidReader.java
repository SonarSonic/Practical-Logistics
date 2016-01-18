package sonar.logistics.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.ITileHandler;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.logistics.common.handlers.FluidReaderHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketFluidReader implements IMessage {

	public int xCoord, yCoord, zCoord;
	public FluidStack selected;

	public PacketFluidReader() {
	}

	public PacketFluidReader(int xCoord, int yCoord, int zCoord, FluidStack selected) {
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.zCoord = zCoord;
		this.selected = selected;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.xCoord = buf.readInt();
		this.yCoord = buf.readInt();
		this.zCoord = buf.readInt();
		this.selected = FluidStack.loadFluidStackFromNBT(ByteBufUtils.readTag(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(xCoord);
		buf.writeInt(yCoord);
		buf.writeInt(zCoord);
		NBTTagCompound tag = new NBTTagCompound();
		selected.writeToNBT(tag);
		ByteBufUtils.writeTag(buf, tag);
	}

	public static class Handler implements IMessageHandler<PacketFluidReader, IMessage> {

		@Override
		public IMessage onMessage(PacketFluidReader message, MessageContext ctx) {
			World world = ctx.getServerHandler().playerEntity.worldObj;

			TileEntity te = world.getTileEntity(message.xCoord, message.yCoord, message.zCoord);
			if (te == null) {
				return null;
			}
			Object target = FMPHelper.checkObject(te);
			if (target != null && target instanceof ITileHandler) {
				TileHandler handler = ((ITileHandler) target).getTileHandler();
				;
				if (handler != null && handler instanceof FluidReaderHandler) {
					FluidReaderHandler reader = (FluidReaderHandler) handler;
					reader.current = message.selected;
				}
			}
			return null;
		}
	}
}
