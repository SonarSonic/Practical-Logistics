package sonar.logistics.integration.multipart;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.SonarHandlerPart;
import sonar.core.integration.fmp.SonarTilePart;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.utils.ITextField;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.Logistics;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.connecting.IDataConnection;
import sonar.logistics.api.connecting.IInfoReader;
import sonar.logistics.api.render.ICableRenderer;
import sonar.logistics.client.renderers.RenderDisplayScreen;
import sonar.logistics.client.renderers.RenderHandlers;
import sonar.logistics.common.handlers.DisplayScreenHandler;
import sonar.logistics.common.handlers.InfoCreatorHandler;
import sonar.logistics.common.handlers.InfoReaderHandler;
import sonar.logistics.common.tileentity.TileEntityInventoryReader;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.network.LogisticsGui;
import sonar.logistics.registries.BlockRegistry;
import sonar.logistics.registries.ItemRegistry;
import codechicken.lib.vec.Cuboid6;

public class InfoCreatorPart extends SonarHandlerPart implements IDataConnection, ICableRenderer, ITextField {

	public InfoCreatorHandler handler = new InfoCreatorHandler(true);

	public InfoCreatorPart() {
		super();
	}

	public InfoCreatorPart(int meta) {
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
	}

	@Override
	public Info currentInfo() {
		return handler.currentInfo();
	}

	@Override
	public void textTyped(String string, int id) {
		handler.textTyped(string, id);
		
	}

	@Override
	public boolean canRenderConnection(ForgeDirection dir) {
		return handler.canRenderConnection(tile(), dir);
	}
	
	public boolean activate(EntityPlayer player, MovingObjectPosition pos, ItemStack stack) {
		if (player != null) {
			player.openGui(Logistics.instance, LogisticsGui.infoCreator, tile().getWorldObj(), x(), y(), z());
			return true;

		}
		return false;
	}
	
	@Override
	public Cuboid6 getBounds() {
		return new Cuboid6(4 * 0.0625, 4 * 0.0625, 4 * 0.0625, 1 - 4 * 0.0625, 1 - 4 * 0.0625, 1 - 4 * 0.0625);
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.infoCreator;
	}

	@Override
	public String getType() {
		return "Info Creator";
	}

	@Override
	public Object getSpecialRenderer() {
		return new RenderHandlers.InfoCreator();
	}


}
