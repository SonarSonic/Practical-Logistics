package sonar.logistics.common.tileentity;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.common.tileentity.TileEntityHandlerInventory;
import sonar.core.common.tileentity.TileEntityInventory;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.inventory.StoredItemStack;
import sonar.core.network.PacketByteBufServer;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.connecting.IDataConnection;
import sonar.logistics.common.handlers.InfoReaderHandler;
import sonar.logistics.common.handlers.InventoryReaderHandler;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.info.types.StoredStackInfo;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityInventoryReader extends TileEntityHandlerInventory implements IDataConnection, IByteBufTile, ISidedInventory {

	public InventoryReaderHandler handler = new InventoryReaderHandler(false);

	@Override
	public TileHandler getTileHandler() {
		return handler;
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return handler.canConnect(this, dir);
	}

	@Override
	public void updateData(ForgeDirection dir) {
		handler.updateData(this, dir);
	}

	@Override
	public Info currentInfo() {
		return handler.currentInfo(this);
	}

	public boolean maxRender() {
		return true;
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		handler.writePacket(buf, id);		
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		handler.readPacket(buf, id);		
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int slot) {
		return null;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack p_102007_2_, int p_102007_3_) {
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack p_102008_2_, int p_102008_3_) {
		return false;
	}

}
