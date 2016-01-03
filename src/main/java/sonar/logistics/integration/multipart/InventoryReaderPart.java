package sonar.logistics.integration.multipart;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.IWailaInfo;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.SonarHandlerPart;
import sonar.core.integration.fmp.SonarTilePart;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.Logistics;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.connecting.IDataConnection;
import sonar.logistics.api.connecting.IInfoReader;
import sonar.logistics.client.renderers.RenderHandlers;
import sonar.logistics.common.handlers.InfoReaderHandler;
import sonar.logistics.common.handlers.InventoryReaderHandler;
import sonar.logistics.common.tileentity.TileEntityBlockNode;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.network.LogisticsGui;
import sonar.logistics.network.packets.PacketProviders;
import sonar.logistics.registries.BlockRegistry;
import codechicken.lib.vec.Cuboid6;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class InventoryReaderPart extends SonarHandlerPart implements IDataConnection{

	public InventoryReaderHandler handler = new InventoryReaderHandler(true);

	public InventoryReaderPart() {
		super();
	}

	public InventoryReaderPart(int meta) {
		super(meta);
	}

	@Override
	public TileHandler getTileHandler() {
		return handler;
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return handler.canConnect(tile(), dir);
	}


	@Override
	public void updateData(ForgeDirection dir) {
		handler.updateData(tile(), dir);
	}

	@Override
	public Info currentInfo() {
		return handler.currentInfo(tile());
	}

	public void sendAvailableData(EntityPlayer player) {
		handler.sendAvailableData(tile(), player);
	}

	public boolean activate(EntityPlayer player, MovingObjectPosition pos, ItemStack stack) {
		if (player != null) {
			sendAvailableData(player);
			player.openGui(Logistics.instance, LogisticsGui.inventoryReader, tile().getWorldObj(), x(), y(), z());
			return true;

		}
		return false;
	}

	@Override
	public Cuboid6 getBounds() {
		if (meta == 2 || meta == 3) {
			return new Cuboid6(6 * 0.0625, 6 * 0.0625, 0.0F, 1.0F - 6 * 0.0625, 1.0F - 6 * 0.0625, 1.0F);
		}
		if (meta == 4 || meta == 5) {
			return new Cuboid6(0.0F, 6 * 0.0625, 6 * 0.0625, 1.0F, 1.0F - 6 * 0.0625, 1.0F - 6 * 0.0625);
		}
		return new Cuboid6(4 * 0.0625, 4 * 0.0625, 4 * 0.0625, 1 - 4 * 0.0625, 1 - 4 * 0.0625, 1 - 4 * 0.0625);
	}

	@Override
	public Object getSpecialRenderer() {
		return new RenderHandlers.InventoryReader();
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.inventoryReader;
	}

	@Override
	public String getType() {
		return "Inventory Reader";
	}
}
