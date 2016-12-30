package sonar.logistics;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.integration.multipart.SonarMultipart;
import sonar.core.integration.multipart.SonarMultipartHelper;
import sonar.core.utils.IGuiItem;
import sonar.core.utils.IGuiTile;
import sonar.logistics.network.PacketClientEmitters;
import sonar.logistics.network.PacketInfoList;
import sonar.logistics.network.PacketInventoryReader;
import sonar.logistics.network.PacketMonitorType;
import sonar.logistics.network.PacketMonitoredCoords;
import sonar.logistics.network.PacketMonitoredList;
import sonar.logistics.network.PacketClientEmitters.Handler;

public class LogisticsCommon implements IGuiHandler {

	public static void registerPackets() {
		Logistics.network.registerMessage(PacketMonitoredList.Handler.class, PacketMonitoredList.class, 0, Side.CLIENT);
		Logistics.network.registerMessage(PacketMonitoredCoords.Handler.class, PacketMonitoredCoords.class, 1, Side.CLIENT);
		Logistics.network.registerMessage(PacketMonitorType.Handler.class, PacketMonitorType.class, 2, Side.SERVER);
		Logistics.network.registerMessage(PacketInfoList.Handler.class, PacketInfoList.class, 3, Side.CLIENT);
		Logistics.network.registerMessage(PacketInventoryReader.Handler.class, PacketInventoryReader.class, 4, Side.SERVER);
		Logistics.network.registerMessage(PacketClientEmitters.Handler.class, PacketClientEmitters.class, 5, Side.CLIENT);
		/* Logistics.network.registerMessage(PacketProviders.Handler.class, PacketProviders.class, 0, Side.CLIENT); // Logistics.network.registerMessage(PacketInfoBlock.Handler.class, PacketInfoBlock.class, 1, Side.SERVER); Logistics.network.registerMessage(PacketDataEmitters.Handler.class, PacketDataEmitters.class, 2, Side.CLIENT); Logistics.network.registerMessage(PacketCoordsSelection.Handler.class, PacketCoordsSelection.class, 3, Side.SERVER); // Logistics.network.registerMessage(PacketFluidReader.Handler.class, PacketFluidReader.class, 5, Side.SERVER); Logistics.network.registerMessage(PacketRouterGui.Handler.class, PacketRouterGui.class, 6, Side.SERVER); Logistics.network.registerMessage(PacketGuiChange.Handler.class, PacketGuiChange.class, 7, Side.SERVER); Logistics.network.registerMessage(ItemHandler.class, PacketScreenInteraction.PacketItemStack.class, 8, Side.SERVER); Logistics.network.registerMessage(FluidHandler.class, PacketScreenInteraction.PacketFluidStack.class, 9, Side.SERVER); */
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		Object part = SonarMultipartHelper.getPartFromHash(ID, world, new BlockPos(x, y, z));
		if (part == null || !(part instanceof IGuiTile)) {
			part = world.getTileEntity(new BlockPos(x, y, z));
		}
		if (part != null && ID != IGuiItem.ID && part instanceof IGuiTile) {
			if (part instanceof SonarMultipart) {
				((SonarMultipart) part).forceNextSync();
			}
			IGuiTile guiTile = (IGuiTile) part;
			return guiTile.getGuiContainer(player);
		} else if (ID == IGuiItem.ID) {
			ItemStack equipped = player.getHeldItemMainhand();
			if (equipped != null && equipped.getItem() instanceof IGuiItem) {
				return ((IGuiItem) equipped.getItem()).getGuiContainer(player, equipped);
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		Object part = SonarMultipartHelper.getPartFromHash(ID, world, new BlockPos(x, y, z));
		if (part == null || !(part instanceof IGuiTile)) {
			part = world.getTileEntity(new BlockPos(x, y, z));
		}
		if (part != null && part instanceof IGuiTile) {
			if (part instanceof SonarMultipart) {
				((SonarMultipart) part).forceNextSync();
			}
			IGuiTile guiTile = (IGuiTile) part;
			return guiTile.getGuiScreen(player);
		} else {
			ItemStack equipped = player.getHeldItemMainhand();
			if (equipped != null && equipped.getItem() instanceof IGuiItem) {
				return ((IGuiItem) equipped.getItem()).getGuiScreen(player, equipped);
			}
		}
		return null;
	}

	public boolean isUsingOperator(){
		return false;
	}
	
	public void setUsingOperator(boolean bool){}
	
	public void registerRenderThings() {}

	public void registerTextures() {}
}
