package sonar.logistics.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.ITileHandler;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.PacketTileEntity;
import sonar.core.network.PacketTileEntityHandler;
import sonar.logistics.common.handlers.FluidReaderHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketFluidReader extends PacketTileEntity {

	public FluidStack selected;

	public PacketFluidReader() {
	}

	public PacketFluidReader(int x, int y, int z, FluidStack selected) {
		super(x, y, z);
		this.selected = selected;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		this.selected = FluidStack.loadFluidStackFromNBT(ByteBufUtils.readTag(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		NBTTagCompound tag = new NBTTagCompound();
		selected.writeToNBT(tag);
		ByteBufUtils.writeTag(buf, tag);
	}

	public static class Handler extends PacketTileEntityHandler<PacketFluidReader> {

		@Override
		public IMessage processMessage(PacketFluidReader message, TileEntity te) {
			Object target = FMPHelper.checkObject(te);
			if (target != null && target instanceof ITileHandler) {
				TileHandler handler = ((ITileHandler) target).getTileHandler();
				if (handler != null && handler instanceof FluidReaderHandler) {
					FluidReaderHandler reader = (FluidReaderHandler) handler;
					reader.current = message.selected;
				}
			}

			return null;
		}
	}
}
