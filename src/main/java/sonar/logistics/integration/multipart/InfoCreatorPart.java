package sonar.logistics.integration.multipart;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.api.BlockCoords;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.utils.ITextField;
import sonar.logistics.Logistics;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.connecting.CableType;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.render.ICableRenderer;
import sonar.logistics.client.renderers.RenderHandlers;
import sonar.logistics.common.handlers.InfoCreatorHandler;
import sonar.logistics.network.LogisticsGui;
import sonar.logistics.registries.BlockRegistry;
import codechicken.lib.vec.Cuboid6;

public class InfoCreatorPart extends ConnectionPart implements IInfoEmitter, ICableRenderer, ITextField {

	public InfoCreatorHandler handler = new InfoCreatorHandler(true, tile());

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
	public ILogicInfo currentInfo() {
		return handler.currentInfo();
	}

	@Override
	public void textTyped(String string, int id) {
		handler.textTyped(string, id);

	}

	@Override
	public CableType canRenderConnection(ForgeDirection dir) {
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

	@Override
	public BlockCoords getCoords() {
		return new BlockCoords(tile());
	}

	@Override
	public void addConnections() {
		LogisticsAPI.getCableHelper().addConnection(tile(), ForgeDirection.getOrientation(FMPHelper.getMeta(tile())));
	}

	@Override
	public void removeConnections() {
		LogisticsAPI.getCableHelper().removeConnection(tile(), ForgeDirection.getOrientation(FMPHelper.getMeta(tile())));
	}
}
