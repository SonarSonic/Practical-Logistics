package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.connecting.ILargeDisplay;
import sonar.logistics.api.render.LargeScreenSizing;
import sonar.logistics.common.handlers.LargeDisplayScreenHandler;
import sonar.logistics.registries.DisplayRegistry;

public class DisplayHelper {

	public static void addScreen(ILargeDisplay screen) {
		Object screenTile = screen.getCoords().getTileEntity();
		if (screenTile != null) {
			List adjacents = new ArrayList();
			List<Integer> ids = new ArrayList();

			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.getOrientation(i);
				Object adjacent = BlockCoords.translateCoords(screen.getCoords(), dir).getTileEntity();
				if (adjacent != null && adjacent instanceof ILargeDisplay) {
					ILargeDisplay adjTile = (ILargeDisplay) adjacent;
					if (adjTile.getOrientation() == screen.getOrientation() && adjTile.registryID() != -2) {
						adjacents.add(adjacent);
						ids.add(adjTile.registryID());
					}
				}
			}

			int screenID = -1;
			int lastSize = -1;
			for (Integer id : ids) {
				if (id != -1) {
					List<BlockCoords> screens = DisplayRegistry.getScreens(id);
					if (screens.size() > lastSize) {
						screenID = id;
						lastSize = screens.size();
					}
				}
			}
			if (screenID == -1) {
				screenID = DisplayRegistry.getNextAvailableID();
			}
			DisplayRegistry.addScreen(screenID, screen.getCoords());

			List<BlockCoords> coords = new ArrayList();
			for (Object adjacent : adjacents) {
				if (adjacent instanceof ILargeDisplay) {
					ILargeDisplay adjScreen = (ILargeDisplay) adjacent;
					if (adjScreen.registryID() != screenID) {
						DisplayRegistry.connectScreens(screenID, adjScreen.registryID());
					}
				}
			}
		}
	}

	public static void removeScreen(ILargeDisplay screen) {
		DisplayRegistry.removeScreen(screen.registryID(), screen);
	}

	public static LargeScreenSizing getScreenSizing(TileEntity tile) {
		Object tileObj = FMPHelper.getHandler(FMPHelper.checkObject(tile));
		if (tileObj == null || !(tileObj instanceof LargeDisplayScreenHandler)) {
			return null;
		}
		LargeDisplayScreenHandler remove = (LargeDisplayScreenHandler) tileObj;
		int maxX = tile.xCoord, maxY = tile.yCoord, maxZ = tile.zCoord, minX = tile.xCoord, minY = tile.yCoord, minZ = tile.zCoord;
		int meta = tile.getBlockMetadata();
		ForgeDirection dir = ForgeDirection.getOrientation(meta).getRotation(ForgeDirection.UP);
		boolean north = false;
		if (dir.offsetX == -1 || dir.offsetX == 1) {
			north = true;
		}
		int screens = 0;

		List<BlockCoords> displays = DisplayRegistry.getScreens(remove.registryID);
		if (displays != null) {

			for (BlockCoords coords : displays) {
				screens++;
				if (coords.getX() > maxX) {
					maxX = coords.getX();
				} else if (coords.getX() < minX) {
					minX = coords.getX();
				}
				if (coords.getY() > maxY) {
					maxY = coords.getY();
				} else if (coords.getY() < minY) {
					minY = coords.getY();
				}
				if (coords.getZ() > maxZ) {
					maxZ = coords.getZ();
				} else if (coords.getZ() < minZ) {
					minZ = coords.getZ();
				}
			}
		}
		int maxH = north ? maxX : maxZ;
		int minH = north ? minX : minZ;

		for (int h = minH; h <= maxH; h++) {
			for (int y = minY; y <= maxY; y++) {
				BlockCoords coords = new BlockCoords(north ? h : tile.xCoord, y, !north ? h : tile.zCoord);
				TileEntity target = coords.getTileEntity(tile.getWorldObj());
				TileHandler targetObj = FMPHelper.getHandler(target);
				if (targetObj == null || !(targetObj instanceof LargeDisplayScreenHandler) || !(target.getBlockMetadata() == meta)) {
					return null;
				}
			}
		}
		maxY = maxY - tile.yCoord;
		minY = tile.yCoord - minY;
		maxH = maxH - (north ? tile.xCoord : tile.zCoord);
		minH = minH - (north ? tile.xCoord : tile.zCoord);

		return new LargeScreenSizing(maxY, minY, maxH, minH);

	}
}
