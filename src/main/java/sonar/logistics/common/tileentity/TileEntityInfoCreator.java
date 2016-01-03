package sonar.logistics.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.tileentity.TileEntityHandler;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.sync.SyncString;
import sonar.core.network.utils.ITextField;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.connecting.IDataConnection;
import sonar.logistics.api.render.ICableRenderer;
import sonar.logistics.common.handlers.InfoCreatorHandler;
import sonar.logistics.common.handlers.InfoReaderHandler;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.helpers.InfoHelper;

public class TileEntityInfoCreator extends TileEntityHandler implements IDataConnection, ICableRenderer, ITextField {

	public InfoCreatorHandler handler = new InfoCreatorHandler(false);

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
	}

	@Override
	public Info currentInfo() {
		return handler.currentInfo();
	}

	public boolean maxRender() {
		return true;
	}

	@Override
	public void textTyped(String string, int id) {
		handler.textTyped(string, id);
		
	}

	@Override
	public boolean canRenderConnection(ForgeDirection dir) {
		return handler.canRenderConnection(this, dir);
	}
}
