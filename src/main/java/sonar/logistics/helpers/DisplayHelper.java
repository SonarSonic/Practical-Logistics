package sonar.logistics.helpers;
/*
import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.BlockCoords;
import sonar.core.integration.fmp.OLDMultipartHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.logistics.api.connecting.ILargeDisplay;
import sonar.logistics.api.render.LargeScreenSizing;
import sonar.logistics.connections.managers.DisplayRegistry;

public class DisplayHelper {

	public static void addScreen(ILargeDisplay screen) {
		Object screenTile = screen.getCoords().getTileEntity();
		if (screenTile != null) {
			List adjacents = new ArrayList();
			List<Integer> ids = new ArrayList();

			for (int i = 0; i < 6; i++) {
				EnumFacing dir = EnumFacing.getFront(i);
				Object adjacent = BlockCoords.translateCoords(screen.getCoords(), dir).getTileEntity();
				if (dir!=EnumFacing.getFront(screen.getOrientation()) && adjacent != null && adjacent instanceof ILargeDisplay) {
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
		Object tileObj = OLDMultipartHelper.getHandler(OLDMultipartHelper.checkObject(tile));
		if (tileObj == null || !(tileObj instanceof LargeDisplayScreenHandler)) {
			return null;
		}
		LargeDisplayScreenHandler remove = (LargeDisplayScreenHandler) tileObj;
		BlockCoords max = new BlockCoords(tile.getPos());
		BlockCoords min = new BlockCoords(tile.getPos());
		
		int meta = tile.getBlockMetadata();
		//MIGHT NEED TO CHECK THIS DIR!!
		EnumFacing dir = EnumFacing.getFront(meta).rotateAround(EnumFacing.Axis.Y);
		boolean north = false;
		if (dir.getFrontOffsetX() == -1 || dir.getFrontOffsetX() == 1) {
			north = true;
		}
		int screens = 0;
		List<BlockCoords> displays = DisplayRegistry.getScreens(remove.registryID);
		if (displays != null) {
			for (BlockCoords coords : displays) {
				screens++;
				if (coords.getX() > max.getX()) {	
					max.setX(coords.getX());
				} else if (coords.getX() < min.getX()) {
					min.setX(coords.getX());
				}
				if (coords.getY() > max.getY()) {
					max.setY(coords.getY());
				} else if (coords.getY() < min.getY()) {
					min.setY(coords.getY());
				}
				if (coords.getZ() > max.getZ()) {
					max.setZ(coords.getZ());
				} else if (coords.getZ() < min.getZ()) {
					min.setZ(coords.getZ());
				}
			}
		}
		int maxH = north ? max.getX() : max.getZ();
		int minH = north ? min.getX() : min.getZ();

		for (int h = minH; h <= maxH; h++) {
			for (int y = min.getY(); y <= max.getY(); y++) {
				BlockCoords coords = new BlockCoords(north ? h : tile.getPos().getX(), y, !north ? h : tile.getPos().getZ());
				TileEntity target = coords.getTileEntity(tile.getWorld());
				TileHandler targetObj = OLDMultipartHelper.getHandler(target);
				if (targetObj == null || !(targetObj instanceof LargeDisplayScreenHandler) || !(target.getBlockMetadata() == meta)) {
					return null;
				}
			}
		}
		max.setY(max.getY() - tile.getPos().getY());
		min.setY(tile.getPos().getY() - min.getY());
		maxH = maxH - (north ? tile.getPos().getX() : tile.getPos().getZ());
		minH = minH - (north ? tile.getPos().getX() : tile.getPos().getZ());

		return new LargeScreenSizing(max.getY(), min.getY(), maxH, minH);

	}
}
*/