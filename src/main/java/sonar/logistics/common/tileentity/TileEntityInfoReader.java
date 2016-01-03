package sonar.logistics.common.tileentity;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.tileentity.TileEntityHandler;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.ITileHandler;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.Logistics;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.connecting.IDataConnection;
import sonar.logistics.api.connecting.IInfoReader;
import sonar.logistics.common.handlers.InfoReaderHandler;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.network.packets.PacketProviders;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityInfoReader extends TileEntityHandler implements IDataConnection, IInfoReader, ITileHandler {

	public InfoReaderHandler handler = new InfoReaderHandler(false);

	@Override
	public TileHandler getTileHandler() {
		return handler;
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return handler.canConnect(this, dir);
	}

	@Override
	public Info getSecondaryInfo() {
		return handler.getSecondaryInfo(this);
	}

	@Override
	public void updateData(ForgeDirection dir) {
		handler.updateData(this, dir);
	}

	@Override
	public Info currentInfo() {
		return handler.currentInfo(this);
	}

	public void sendAvailableData(EntityPlayer player) {
		handler.sendAvailableData(this, player);
	}

	public boolean maxRender() {
		return true;
	}
}
