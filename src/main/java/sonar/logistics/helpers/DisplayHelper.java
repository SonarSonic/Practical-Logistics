package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.ITileHandler;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.connecting.ILargeDisplay;
import sonar.logistics.api.render.LargeScreenSizing;

public class DisplayHelper {

	public static void onDisplayAdded(TileEntity tile) {
		TileHandler tileObj = FMPHelper.getHandler(tile);
		if (tileObj == null || !(tileObj instanceof ILargeDisplay)) {
			return;
		}
		ILargeDisplay added = (ILargeDisplay) tileObj;
		ForgeDirection forward = ForgeDirection.getOrientation(tile.blockMetadata);

		BlockCoords handlerCoords = null;
		int connectedSize = -1;
		int listPos = -1;
		ILargeDisplay[] handlers = new ILargeDisplay[6];
		BlockCoords[] coordsList = new BlockCoords[6];
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			if (dir != forward || dir != forward.getOpposite()) {
				BlockCoords coords = new BlockCoords(tile.xCoord + dir.offsetX, tile.yCoord + dir.offsetY, tile.zCoord + dir.offsetZ);
				TileHandler target = FMPHelper.getHandler(coords.getTileEntity(tile.getWorldObj()));
				if (target instanceof ILargeDisplay) {
					ILargeDisplay display = (ILargeDisplay) target;
					if (display.isHandler()) {
						if (display.getConnectedScreens().size() > connectedSize) {
							handlerCoords = coords;
							connectedSize = display.getConnectedScreens().size();
							listPos = i;
						}
						handlers[i] = display;
						coordsList[i] = coords;
					} else {
						BlockCoords handler = display.getHandlerCoords();
						if (handler != null) {
							TileHandler handTarg = FMPHelper.getHandler(handler.getTileEntity(tile.getWorldObj()));
							if (handTarg != null && handTarg instanceof ILargeDisplay) {
								ILargeDisplay handlerDis = (ILargeDisplay) handTarg;
								if (handlerDis.getConnectedScreens().size() > connectedSize) {
									handlerCoords = handler;
									connectedSize = handlerDis.getConnectedScreens().size();
									listPos = i;
								}
								handlers[i] = handlerDis;
								coordsList[i] = handler;
							}
						}
					}
				}
			}
		}
		if (handlerCoords != null) {
			added.setHandlerCoords(handlerCoords);
			added.setHandler(false);
			TileHandler handTarg = FMPHelper.getHandler(handlerCoords.getTileEntity(tile.getWorldObj()));
			if (handTarg != null && handTarg instanceof ILargeDisplay) {
				ILargeDisplay handlerDis = (ILargeDisplay) handTarg;
				handlerDis.addDisplay(new BlockCoords(tile));
			}
			/*if (listPos != -1) { for (int i = 0; i < 6; i++) { if (i != listPos && handlers[i] != null) { ILargeDisplay handler = handlers[i]; List<BlockCoords> list = new ArrayList(); for (BlockCoords coords : handler.getConnectedScreens()) { TileHandler target = FMPHelper.getHandler(coords.getTileEntity(tile.getWorldObj())); if (target instanceof ILargeDisplay) { ILargeDisplay display =
			 * (ILargeDisplay) target; display.setHandler(false); display.setHandlerCoords(handlerCoords); display.setConnectedScreens(new ArrayList()); list.add(coords); } } for (BlockCoords coords : list) { handlers[i].addDisplay(coords); } } } handlers[listPos].resetSizing(); */

		} else {
			added.setHandler(true);
			added.addDisplay(new BlockCoords(tile));
		}

	}

	public static void onDisplayRemoved(TileEntity tile) {
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
			int maxX = tile.xCoord, maxY = tile.yCoord, maxZ = tile.zCoord, minX = tile.xCoord, minY = tile.yCoord, minZ = tile.zCoord;
			int meta = tile.getBlockMetadata();
			ForgeDirection dir = ForgeDirection.getOrientation(meta).getRotation(ForgeDirection.UP);
			boolean north = false;
			if (dir.offsetX == -1 || dir.offsetX == 1) {
				north = true;
			}
			int screens = 0;
			for (BlockCoords coords : remove.getConnectedScreens()) {
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
			int maxH = north ? maxX : maxZ;
			int minH = north ? minX : minZ;

			for (int h = minH; h <= maxH; h++) {
				for (int y = minY; y <= maxY; y++) {
					BlockCoords coords = new BlockCoords(north ? h : tile.xCoord, y, !north ? h : tile.zCoord);
					TileEntity target = coords.getTileEntity(tile.getWorldObj());
					TileHandler targetObj = FMPHelper.getHandler(target);
					if (targetObj == null || !(targetObj instanceof ILargeDisplay) || !(target.getBlockMetadata() == meta)) {
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
}
