package sonar.logistics.helpers;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.connecting.ILargeDisplay;
import sonar.logistics.api.render.LargeScreenSizing;

public class DisplayHelper {

	public static void onDisplayAdded(TileEntity tile) {
		System.out.print("add");
		Object tileObj = FMPHelper.checkObject(tile);
		if (!(tileObj instanceof ILargeDisplay)) {
			tileObj = FMPHelper.getHandler(tileObj);
		}
		if (tileObj == null || !(tileObj instanceof ILargeDisplay)) {
			return;
		}
		ILargeDisplay added = (ILargeDisplay) tileObj;
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			BlockCoords coords = new BlockCoords(tile.xCoord + dir.offsetX, tile.yCoord + dir.offsetY, tile.zCoord + dir.offsetZ);
			Object target = FMPHelper.checkObject(coords.getTileEntity(tile.getWorldObj()));
			if (!(target instanceof ILargeDisplay)) {
				target = FMPHelper.getHandler(target);
			}
			if (target instanceof ILargeDisplay) {
				ILargeDisplay display = (ILargeDisplay) target;
				if (display.isHandler()) {
					added.setHandlerCoords(coords);
					added.setHandler(false);
					display.addDisplay(new BlockCoords(tile));
					return;
				}
			}
		}
		added.setHandler(true);
		added.addDisplay(new BlockCoords(tile));
	}

	public static void onDisplayRemoved(TileEntity tile) {
		System.out.print("remove");
		Object tileObj = FMPHelper.checkObject(tile);
		if (!(tileObj instanceof ILargeDisplay)) {
			tileObj = FMPHelper.getHandler(tileObj);
		}
		if (tileObj == null || !(tileObj instanceof ILargeDisplay)) {
			return;
		}
		ILargeDisplay remove = (ILargeDisplay) tileObj;
		if (remove.isHandler() && remove.getConnectedScreens() != null || !remove.getConnectedScreens().isEmpty()) {
			BlockCoords coords = remove.getConnectedScreens().get(0);
			if (coords != null) {
				Object target = FMPHelper.checkObject(coords.getTileEntity(tile.getWorldObj()));
				if (!(target instanceof ILargeDisplay)) {
					target = FMPHelper.getHandler(target);
				}
				if (target instanceof ILargeDisplay) {
					ILargeDisplay display = (ILargeDisplay) target;
					remove.removeDisplay(new BlockCoords(tile));
					display.setHandlerCoords(coords);
					display.setConnectedScreens(remove.getConnectedScreens());
					display.setHandler(true);
				}
			}
		} else {
			BlockCoords handler = remove.getHandlerCoords();
			if (handler == null) {
				return;
			}
			Object target = FMPHelper.checkObject(handler.getTileEntity(tile.getWorldObj()));
			if (!(target instanceof ILargeDisplay)) {
				target = FMPHelper.getHandler(target);
			}
			if (target instanceof ILargeDisplay) {
				ILargeDisplay display = (ILargeDisplay) target;
				display.removeDisplay(new BlockCoords(tile));
			}
		}
	}

	public static LargeScreenSizing getScreenSizing(TileEntity tile) {
		Object tileObj = FMPHelper.checkObject(tile);
		if (!(tileObj instanceof ILargeDisplay)) {
			tileObj = FMPHelper.getHandler(tileObj);
		}
		if (tileObj == null || !(tileObj instanceof ILargeDisplay)) {
			return null;
		}
		ILargeDisplay remove = (ILargeDisplay) tileObj;
		if (!remove.isHandler()) {
			return null;
		} else {
			int maxY = 0, minY = 0, maxH = 0, minH = 0;
			int meta = tile.getBlockMetadata();
			boolean north = false;
			if (meta == 2 || meta == 3) {
				north = true;
			}
			for (BlockCoords coords : remove.getConnectedScreens()) {
				System.out.print("screen");
				if (tile.xCoord - coords.getX() > maxH) {
					maxH = tile.xCoord - coords.getX();
				} else if (coords.getX() - tile.xCoord < minH) {
					minH = coords.getX() - tile.xCoord;
				}
				if (tile.zCoord - coords.getZ() > maxH) {
					maxH = tile.zCoord - coords.getZ();
				} else if (coords.getZ() - tile.zCoord < minH) {
					minH = coords.getZ() - tile.zCoord;
				}
				if (tile.yCoord - coords.getY() > maxY) {
					maxY = tile.yCoord - coords.getY();
				} else if (coords.getY() - tile.yCoord < minY) {
					minY = coords.getY() - tile.yCoord;
				}
			}
			for (int h = minH; h <= maxH; h++) {
				for (int y = minY; y <= maxY; y++) {
					BlockCoords coords = new BlockCoords(north ? tile.xCoord + h : tile.xCoord, tile.yCoord, !north ? tile.zCoord + h : tile.zCoord);
					TileEntity target = coords.getTileEntity(tile.getWorldObj());

					Object targetObj = FMPHelper.checkObject(target);
					if (!(targetObj instanceof ILargeDisplay)) {
						targetObj = FMPHelper.getHandler(tileObj);
					}
					if (target == null || !(targetObj instanceof ILargeDisplay) || !(target.getBlockMetadata() == meta)) {
						return null;
					}
				}
			}
			return new LargeScreenSizing(maxY, minY, maxH, minH);
		}
	}
}
