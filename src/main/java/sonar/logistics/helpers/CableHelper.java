package sonar.logistics.helpers;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.SonarAPI;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.SonarHelper;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.connecting.IDataConnection;
import sonar.logistics.api.connecting.IDataReceiver;
import sonar.logistics.api.connecting.IDataTile;
import sonar.logistics.common.tileentity.TileEntityDataEmitter;
import codechicken.multipart.TileMultipart;

public class CableHelper {

	public static void updateAdjacentCoords(TileEntity tile, BlockCoords coords, boolean overwrite) {
		updateAdjacentCoords(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord, coords, overwrite);
	}

	public static void updateAdjacentCoords(World world, int x, int y, int z, BlockCoords newCoords, boolean overwrite) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			updateAdjacentCoord(world, x, y, z, newCoords, overwrite, dir);
		}
	}

	public static void updateAdjacentCoords(TileEntity tile, BlockCoords coords, boolean overwrite, ForgeDirection[] remove) {
		updateAdjacentCoords(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord, coords, overwrite, remove);
	}

	public static void updateAdjacentCoords(World world, int x, int y, int z, BlockCoords newCoords, boolean overwrite, ForgeDirection[] remove) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			if (remove != null) {
				for (int d = 0; d < remove.length; d++) {
					if (!dir.equals(remove[d])) {
						updateAdjacentCoord(world, x, y, z, newCoords, overwrite, dir);
					}
				}
			} else {
				updateAdjacentCoord(world, x, y, z, newCoords, overwrite, dir);
			}
		}
	}

	public static void updateAdjacentCoord(TileEntity tile, BlockCoords coords, boolean overwrite, ForgeDirection dir) {
		updateAdjacentCoord(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord, coords, overwrite, dir);
	}

	public static void updateAdjacentCoord(World world, int x, int y, int z, BlockCoords newCoords, boolean overwrite, ForgeDirection dir) {
		Object tile = world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
		Object originTile = world.getTileEntity(x, y, z);
		tile = FMPHelper.checkObject(tile);
		originTile = FMPHelper.checkObject(originTile);
		if (tile != null && tile instanceof IDataCable) {
			IDataCable cable = (IDataCable) tile;
			if (originTile != null && originTile instanceof IDataCable) {
				IDataCable originCable = (IDataCable) originTile;
				if (originCable.isBlocked(dir)) {
					return;
				}
			}
			if (!cable.isBlocked(dir.getOpposite())) {
				if (cable.getCoords() == null || overwrite && newCoords == null) {
					cable.setCoords(newCoords);
				} else if (overwrite && !BlockCoords.equalCoords(cable.getCoords(), newCoords)) {
					cable.setCoords(newCoords);
				}
			}

		}
	}

	public static Object getConnectedTile(TileEntity tile, ForgeDirection dir) {
		Object target = SonarHelper.getAdjacentTileEntity(tile, dir);
		target = FMPHelper.checkObject(target);
		if (target == null) {
			return null;
		}

		if (target instanceof IDataCable) {
			IDataCable cable = (IDataCable) target;
			if (!cable.isBlocked(dir.getOpposite())) {
				if (cable.getCoords() != null) {
					Object dataProvider = cable.getCoords().getTileEntity(tile.getWorldObj());
					dataProvider = FMPHelper.checkObject(dataProvider);
					if (dataProvider != null) {
						if (dataProvider instanceof IDataReceiver) {
							IDataReceiver receiver = (IDataReceiver) dataProvider;
							if (receiver.getEmitter() != null) {
								TileEntity emitter = receiver.getEmitter().coords.getTileEntity();
								if (emitter instanceof TileEntityDataEmitter) {
									TileEntityDataEmitter dataemitter = (TileEntityDataEmitter) emitter;
									return dataemitter.getConnectedTile();
								}
								if (emitter != null) {
									return emitter;
								}
							}
						} else if (dataProvider instanceof IDataConnection) {
							return dataProvider;
						}
					}
				}
			}
		}
		if (target instanceof IDataReceiver) {
			IDataReceiver receiver = (IDataReceiver) target;
			if (receiver.getEmitter() != null) {
				TileEntity emitter = receiver.getEmitter().coords.getTileEntity();
				if (emitter instanceof TileEntityDataEmitter) {
					TileEntityDataEmitter dataemitter = (TileEntityDataEmitter) emitter;
					return dataemitter.getConnectedTile();
				}
				if (emitter != null) {
					return emitter;
				}
			}
		}
		if (target instanceof IDataConnection) {
			if (((IDataConnection) target).canConnect(dir.getOpposite()))
				return target;
		}
		if (SonarAPI.forgeMultipartLoaded() && target instanceof TileMultipart) {
			return target;
		}

		return null;

	}

	/** checks connection on direction */
	public static boolean canRenderConnection(TileEntity te, ForgeDirection dir) {
		Object tile = SonarHelper.getAdjacentTileEntity(te, dir);
		tile = FMPHelper.checkObject(tile);
		if (tile != null) {
			if (tile instanceof IDataTile) {
				return (((IDataTile) tile).canConnect(dir.getOpposite()));
			} else if (tile instanceof IDataCable) {
				IDataCable cable = (IDataCable) tile;
				if (!cable.isBlocked(dir.getOpposite())) {
					return true;
				}
			}
		}
		return false;
	}
}
