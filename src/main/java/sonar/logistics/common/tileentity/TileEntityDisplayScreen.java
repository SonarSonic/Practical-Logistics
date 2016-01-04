package sonar.logistics.common.tileentity;

import io.netty.buffer.ByteBuf;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.common.tileentity.TileEntityHandler;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.connecting.IDataConnection;
import sonar.logistics.api.connecting.IInfoReader;
import sonar.logistics.common.handlers.DisplayScreenHandler;
import sonar.logistics.common.handlers.InfoReaderHandler;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.helpers.InfoHelper;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityDisplayScreen extends TileEntityHandler implements IDataConnection {

	public DisplayScreenHandler handler = new DisplayScreenHandler(false);

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
		return handler.currentInfo();
	}

	public boolean maxRender() {
		return true;
	}

}