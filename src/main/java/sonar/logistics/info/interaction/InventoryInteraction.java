package sonar.logistics.info.interaction;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.BlockInteraction;
import sonar.core.utils.BlockInteractionType;
import sonar.logistics.api.Info;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.connecting.ILargeDisplay;
import sonar.logistics.api.render.InfoInteractionHandler;
import sonar.logistics.api.render.LargeScreenSizing;
import sonar.logistics.api.render.ScreenType;
import sonar.logistics.common.handlers.InventoryReaderHandler;
import sonar.logistics.common.handlers.LargeDisplayScreenHandler;
import sonar.logistics.info.types.InventoryInfo;

public class InventoryInteraction extends InfoInteractionHandler<InventoryInfo> {

	@Override
	public String getName() {
		return "Inventory Info";
	}
	
	@Override
	public boolean canHandle(ScreenType type, TileEntity te, TileEntity object) {
		TileHandler handler = FMPHelper.getHandler(object);
		return handler != null && handler instanceof InventoryReaderHandler;
	}

	@Override
	public void handleInteraction(InventoryInfo info, ScreenType type, TileEntity screen, TileEntity reader, EntityPlayer player, int x, int y, int z, BlockInteraction interact, boolean doubleClick) {
		InventoryReaderHandler handler = (InventoryReaderHandler) FMPHelper.getHandler(reader);
		TileHandler screenHandler = FMPHelper.getHandler(screen);
		
		ForgeDirection dir = ForgeDirection.getOrientation(interact.side);
		BlockCoords screenCoords = new BlockCoords(screen);
		if (interact.type == BlockInteractionType.RIGHT || interact.type == BlockInteractionType.SHIFT_RIGHT) {
			if (interact.type == BlockInteractionType.RIGHT) {
				if (player.getHeldItem() != null) {
					if (!doubleClick) {
						handler.insertItem(player, reader, player.inventory.currentItem);
					} else {
						handler.insertInventory(player, reader, player.inventory.currentItem);
					}
				}
			} else if (interact.type == BlockInteractionType.SHIFT_RIGHT) {
				
			}
		} else if (screenHandler instanceof LargeDisplayScreenHandler) {			
			LargeDisplayScreenHandler largeScreen = (LargeDisplayScreenHandler) screenHandler;
			LargeScreenSizing sizing = largeScreen.sizing;
			if (sizing != null) {
				int slot = -1;						
				if (dir == ForgeDirection.NORTH) {
					int hSlots = (Math.round(sizing.maxH - sizing.minH) * 2);
					int yPos = (sizing.maxY - (y - screenCoords.getY())) * 2;
					int hPos = (sizing.maxH - (x - screenCoords.getX())) * 2;
					int hSlot = interact.hitx < 0.5 ? hPos + 1 : hPos;
					int ySlot = interact.hity < 0.5 ? yPos + 1 : yPos;
					slot = ((ySlot * hSlots) + hSlot) + (ySlot * 2);
				} else if (dir == ForgeDirection.SOUTH) {
					int hSlots = (Math.round(sizing.maxH - sizing.minH) * 2);
					int yPos = (sizing.maxY - (y - screenCoords.getY())) * 2;
					int hPos = (sizing.maxH - sizing.minH + (x - screenCoords.getX())) * 2;
					int hSlot = interact.hitx < 0.5 ? hPos : hPos + 1;
					int ySlot = interact.hity < 0.5 ? yPos + 1 : yPos;
					slot = ((ySlot * hSlots) + hSlot) + (ySlot * 2) - sizing.maxH * 2;
				} else if (dir == ForgeDirection.EAST) {
					int hSlots = (Math.round(sizing.maxH - sizing.minH) * 2);
					int yPos = (sizing.maxY - (y - screenCoords.getY())) * 2;
					int hPos = (sizing.maxH - (z - screenCoords.getZ())) * 2;
					int hSlot = interact.hitz < 0.5 ? hPos + 1 : hPos;
					int ySlot = interact.hity < 0.5 ? yPos + 1 : yPos;
					slot = ((ySlot * hSlots) + hSlot) + (ySlot * 2);
				} else if (dir == ForgeDirection.WEST) {
					int hSlots = (Math.round(sizing.maxH - sizing.minH) * 2);
					int yPos = (sizing.maxY - (y - screenCoords.getY())) * 2;
					int hPos = (sizing.maxH - sizing.minH + (z - screenCoords.getZ())) * 2;
					int hSlot = interact.hitz < 0.5 ? hPos : hPos + 1;
					int ySlot = interact.hity < 0.5 ? yPos + 1 : yPos;
					slot = ((ySlot * hSlots) + hSlot) + (ySlot * 2) - sizing.maxH * 2;
				}
				if (slot >= 0 && slot < info.stacks.size()) {
					StoredItemStack extract = handler.extractItem(reader, info.stacks.get(slot), interact.type == BlockInteractionType.LEFT ? 1 : 64);
					if (extract != null) {
						LogisticsAPI.getItemHelper().spawnStoredItemStack(extract, screen.getWorldObj(), x, y, z, dir);
					}
				}
			}
		}
	}

}
