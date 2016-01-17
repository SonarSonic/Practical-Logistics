package sonar.logistics.integration.multipart;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.IWailaInfo;
import sonar.core.integration.fmp.SonarHandlerPart;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.logistics.Logistics;
import sonar.logistics.api.Info;
import sonar.logistics.api.connecting.IDataConnection;
import sonar.logistics.api.connecting.IInfoReader;
import sonar.logistics.client.renderers.RenderHandlers;
import sonar.logistics.common.handlers.InfoReaderHandler;
import sonar.logistics.network.LogisticsGui;
import sonar.logistics.registries.BlockRegistry;
import codechicken.lib.vec.Cuboid6;

public class InfoReaderPart extends SonarHandlerPart implements IDataConnection, IInfoReader {

	public InfoReaderHandler handler = new InfoReaderHandler(true);

	public InfoReaderPart() {
		super();
	}

	public InfoReaderPart(int meta) {
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
	public Info getSecondaryInfo() {
		return handler.getSecondaryInfo(tile());
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
			player.openGui(Logistics.instance, LogisticsGui.infoNode, tile().getWorldObj(), x(), y(), z());
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
		return new RenderHandlers.InfoNode();
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.infoReader;
	}

	@Override
	public String getType() {
		return "Info Reader";
	}
}
