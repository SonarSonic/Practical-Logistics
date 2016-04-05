package sonar.logistics.info.interaction;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.api.BlockCoords;
import sonar.core.api.SonarAPI;
import sonar.core.api.StoredItemStack;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.utils.BlockInteraction;
import sonar.core.utils.BlockInteractionType;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.render.InfoInteractionHandler;
import sonar.logistics.api.render.LargeScreenSizing;
import sonar.logistics.api.render.ScreenType;
import sonar.logistics.common.handlers.LargeDisplayScreenHandler;
import sonar.logistics.info.types.InventoryInfo;
import sonar.logistics.registries.CacheRegistry;

public class InventoryInteraction extends InfoInteractionHandler<InventoryInfo> {

	@Override
	public String getName() {
		return "Inventory Info";
	}

	@Override
	public void handleInteraction(InventoryInfo info, ScreenType type, TileEntity screen, EntityPlayer player, int x, int y, int z, BlockInteraction interact, boolean doubleClick) {
		INetworkCache cache = CacheRegistry.getCache(info.cacheID);
		if (cache == null || cache.getNetworkID() == -1) {
			return;
		}
		TileHandler screenHandler = FMPHelper.getHandler(screen);
		ForgeDirection dir = interact.getDir();
		BlockCoords screenCoords = new BlockCoords(screen);
		if (interact.type == BlockInteractionType.RIGHT || interact.type == BlockInteractionType.SHIFT_RIGHT) {
			if (interact.type == BlockInteractionType.RIGHT) {
				if (player.getHeldItem() != null) {
					if (!doubleClick) {
						LogisticsAPI.getItemHelper().insertItemFromPlayer(player, cache, player.inventory.currentItem);
					} else {
						LogisticsAPI.getItemHelper().insertInventoryFromPlayer(player, cache, player.inventory.currentItem);
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
					StoredItemStack stack = info.stacks.get(slot);
					if (stack != null) {
						StoredItemStack extract = LogisticsAPI.getItemHelper().extractItem(cache, stack.setStackSize(interact.type == BlockInteractionType.LEFT ? 1 : 64));
						if (extract != null) {
							SonarAPI.getItemHelper().spawnStoredItemStack(extract, screen.getWorldObj(), x, y, z, dir);
						}
					}
				}
			}
		} else {
			int slot = -1;
			if (dir == ForgeDirection.NORTH) {
				slot = interact.hitx < 0.5 ? 1 : 0;
			} else if (dir == ForgeDirection.SOUTH) {
				slot = interact.hitx < 0.5 ? 0 : 1;
			} else if (dir == ForgeDirection.EAST) {
				slot = interact.hitz < 0.5 ? 1 : 0;
			} else if (dir == ForgeDirection.WEST) {
				slot = interact.hitz < 0.5 ? 0 : 1;
			}
			if (slot >= 0 && slot < info.stacks.size()) {
				StoredItemStack stack = info.stacks.get(slot);
				if (stack != null) {
					StoredItemStack extract = LogisticsAPI.getItemHelper().extractItem(cache, stack.setStackSize(interact.type == BlockInteractionType.LEFT ? 1 : 64));
					if (extract != null) {
						SonarAPI.getItemHelper().spawnStoredItemStack(extract, screen.getWorldObj(), x, y, z, dir);
					}
				}
			}
		}
	}
}
