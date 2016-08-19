package sonar.logistics.connections;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.api.utils.BlockCoords;
import sonar.core.utils.Pair;
import sonar.logistics.api.display.IInfoDisplay;
import sonar.logistics.api.info.IDisplayInfo;
import sonar.logistics.api.info.IInfoContainer;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.info.monitor.IMonitorInfo;

public class LogicMonitorCache {

	public static final int UPDATE_RADIUS = 64;

	// chunk syncing - the player won't necessarily be inside the chunk itself but will be within the range.
	public static LinkedHashMap<Integer, ArrayList<StoredChunkPos>> monitoredChunks = new LinkedHashMap(); // The dimension id then the Chunk Pos which contain monitors...
	public static LinkedHashMap<EntityPlayer, ArrayList<StoredChunkPos>> activeChunks = new LinkedHashMap(); // these chunks will always be the same dimension as the player

	// server side
	public static final ArrayList<ILogicMonitor> monitors = new ArrayList();
	public static final ArrayList<IInfoDisplay> displays = new ArrayList();
	public static LinkedHashMap<InfoUUID, IMonitorInfo> lastInfo = new LinkedHashMap();
	// client side
	public static LinkedHashMap<ILogicMonitor, MonitoredList<?>> monitoredLists = new LinkedHashMap();

	// both
	public static LinkedHashMap<InfoUUID, IMonitorInfo> info = new LinkedHashMap();
	// public static LinkedHashMap<IInfoDisplay, IInfoContainer> displayLists = new LinkedHashMap();

	public static boolean enableEvents() {
		return !displays.isEmpty();
	}

	public static void addMonitor(ILogicMonitor monitor) {
		if (monitors.contains(monitor)) {
			return;
		}
		monitors.add(monitor);
	}

	public static void addDisplay(IInfoDisplay display) {
		if (displays.contains(display)) {
			return;
		}
		displays.add(display);
		onChunkAdded(new StoredChunkPos(display.getCoords()));
	}

	public static boolean removeMonitor(ILogicMonitor monitor) {
		return monitors.remove(monitor);
	}

	public static boolean removeDisplay(IInfoDisplay display) {
		onChunkRemoved(new StoredChunkPos(display.getCoords()));
		return displays.remove(display);
	}

	public static void onChunkAdded(StoredChunkPos pos) {
		monitoredChunks.putIfAbsent(pos.dim, new ArrayList());
		for (StoredChunkPos chunk : monitoredChunks.get(pos.dim)) {
			if (chunk.equals(pos)) {
				chunk.addMonitor();
				return;
			}
		}
		pos.addMonitor();
		monitoredChunks.get(pos.dim).add(pos);
	}

	public static void onChunkRemoved(StoredChunkPos pos) {
		if (monitoredChunks.get(pos.dim).contains(pos)) {
			for (StoredChunkPos chunk : monitoredChunks.get(pos.dim)) {
				if (chunk.equals(pos)) {
					if (chunk.removeMonitor() <= 0) {
						monitoredChunks.get(pos.dim).remove(chunk);
					}
					return;
				}
			}
		}
	}

	public static void sendFirstPacket(EntityPlayer player) {

	}

	public static ArrayList<IInfoDisplay> getDisplaysInRadius(TargetPoint point) {
		ArrayList<IInfoDisplay> displays = new ArrayList();
		for (IInfoDisplay monitor : displays) {
			BlockCoords coords = (BlockCoords) monitor.getCoords();
			if (coords.getDimension() == point.dimension) {
				double d4 = point.x - coords.getX();
				double d5 = point.y - coords.getY();
				double d6 = point.z - coords.getZ();

				if (d4 * d4 + d5 * d5 + d6 * d6 < point.range * point.range) {
					displays.add(monitor);
				}
			}
		}
		return displays;
	}

	// server methods
	public static ILogicMonitor getMonitorFromClient(int hashCode) {
		for (ILogicMonitor monitor : monitors) {
			if (monitor.getMonitorUUID().hashCode() == hashCode) {
				return monitor;
			}
		}
		return null;
	}

	public static void sendFullPacket(EntityPlayer player, ByteBuf buf) {
		if (player != null) {
			ArrayList<IMonitorInfo> infoList = getInfoFromUUIDs(getUUIDsToSync(getDisplaysInRadius(getTargetPointFromPlayer(player))));
			for (IMonitorInfo info : infoList) {

			}
		}
	}

	public static ArrayList<IMonitorInfo> getInfoFromUUIDs(ArrayList<InfoUUID> ids) {
		ArrayList<IMonitorInfo> infoList = new ArrayList();
		for (InfoUUID id : ids) {
			ILogicMonitor monitor = getMonitorFromClient(id.hashCode);
			if (monitor != null) {
				IMonitorInfo info = monitor.getMonitorInfo(id.pos);
				if (info != null) {
					infoList.add(info);
				}
			}
		}
		return infoList;
	}

	public static ArrayList<InfoUUID> getUUIDsToSync(ArrayList<IInfoDisplay> displays) {
		ArrayList<InfoUUID> ids = new ArrayList();
		for (IInfoDisplay display : displays) {
			for (IDisplayInfo info : display.container().getStoredInfo()) {
				InfoUUID id = info.getInfoUUID();
				if (id.valid() && !ids.contains(id)) {
					ids.add(id);
				}
			}
		}
		return ids;
	}

	public IMonitorInfo getInfoFromUUID(InfoUUID uuid) {
		return info.get(uuid);
	}

	public static TargetPoint getTargetPointFromPlayer(EntityPlayer player) {
		return new TargetPoint(player.getEntityWorld().provider.getDimension(), player.posX, player.posY, player.posZ, UPDATE_RADIUS);
	}

	// public static void updateInfoFromServer(EntityPlayer player, InfoUUID infoID, ByteBuf updateBuf) {
	// World world = player.getEntityWorld();
	// getDisplaysInRadius(.forEach(display -> display.container().updateInfo(infoID, updateBuf));
	// }

	// client methods
	public static Pair<ILogicMonitor, MonitoredList<?>> getMonitorFromServer(int hashCode) {
		for (Entry<ILogicMonitor, ?> entry : monitoredLists.entrySet()) {
			if (entry.getKey().getMonitorUUID().hashCode() == hashCode) {
				return new Pair(entry.getKey(), entry.getValue());
			}
		}
		return null;
	}

	public static <T extends IMonitorInfo> MonitoredList<T> getMonitoredList(ILogicMonitor monitor) {
		monitoredLists.putIfAbsent(monitor, MonitoredList.<T>newMonitoredList());
		for (Entry<ILogicMonitor, MonitoredList<?>> entry : monitoredLists.entrySet()) {
			if (entry.getKey().getMonitorUUID().equals(monitor.getMonitorUUID())) {
				return (MonitoredList<T>) entry.getValue();
			}
		}
		return MonitoredList.<T>newMonitoredList();
	}

	public static class StoredChunkPos extends ChunkPos {

		// these won't be included in the hashCode
		public int monitorCount = 0;
		public int dim;

		public StoredChunkPos(int dim, BlockPos pos) {
			super(pos);
			this.dim = dim;
		}

		public StoredChunkPos(BlockCoords coords) {
			this(coords.getDimension(), coords.getBlockPos());
		}

		public int addMonitor() {
			return monitorCount++;
		}

		public int removeMonitor() {
			return monitorCount--;
		}

	}
}
