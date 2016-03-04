package sonar.logistics.integration.multipart;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.utils.BlockCoords;
import sonar.logistics.Logistics;
import sonar.logistics.api.Info;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.CacheTypes;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.client.renderers.RenderHandlers;
import sonar.logistics.common.handlers.InventoryReaderHandler;
import sonar.logistics.network.LogisticsGui;
import sonar.logistics.registries.BlockRegistry;
import codechicken.lib.vec.Cuboid6;

public class InventoryReaderPart extends ConnectionPart implements IInfoEmitter{

	public InventoryReaderHandler handler = new InventoryReaderHandler(true, tile());

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
	public Info currentInfo() {
		return handler.currentInfo(tile());
	}

	public boolean activate(EntityPlayer player, MovingObjectPosition pos, ItemStack stack) {
		if (player != null) {
			this.sendSyncPacket(player);			
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
