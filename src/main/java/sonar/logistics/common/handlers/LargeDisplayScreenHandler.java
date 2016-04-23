package sonar.logistics.common.handlers;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.api.BlockCoords;
import sonar.core.api.StoredFluidStack;
import sonar.core.api.StoredItemStack;
import sonar.core.helpers.SonarHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.utils.IByteBufTile;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.CacheTypes;
import sonar.logistics.api.cache.ICacheViewer;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.render.LargeScreenSizing;
import sonar.logistics.helpers.DisplayHelper;
import sonar.logistics.registries.DisplayRegistry;

import com.google.common.collect.Lists;

public class LargeDisplayScreenHandler extends DisplayScreenHandler implements IByteBufTile {

	public SyncTagType.BOOLEAN isHandler = new SyncTagType.BOOLEAN(0);
	public LargeScreenSizing sizing;
	public boolean resetSizing = true, resetHandler = true;;
	public BlockCoords connectedTile = null;
	public int registryID = -1;

	public LargeDisplayScreenHandler(boolean isMultipart, TileEntity tile) {
		super(isMultipart, tile);
	}

	@Override
	public void update(TileEntity te) {
		if (!te.getWorldObj().isRemote) {
			if (this.isHandler.getObject()) {
				if (DisplayRegistry.hasChanged(registryID)) {
					LargeScreenSizing lastSize = sizing;
					sizing = DisplayHelper.getScreenSizing(te);
					if (sizing == null && lastSize != null || (lastSize == null) || !lastSize.equals(sizing)) {
						SonarCore.sendPacketAround(te, 64, 3);
					}
					DisplayRegistry.onUpdate(registryID);
				}
				ForgeDirection side = ForgeDirection.getOrientation(FMPHelper.getMeta(te));

				boolean updateTile = true;
				if (connectedTile != null) {
					TileEntity tile = connectedTile.getTileEntity();
					if (tile != null) {
						INetworkCache network = LogisticsAPI.getCableHelper().getNetwork(tile, side.getOpposite());
						if (network != null && !network.getConnections(CacheTypes.EMITTER, true).isEmpty()) {
							updateTile = false;
						}
					}
				}

				if (updateTile) {
					connectedTile = getConnectedTile(te, side);
				}
				if (connectedTile == null || connectedTile.getTileEntity(te.getWorldObj()) == null) {
					this.updateData(te, te, side);
				} else {
					this.updateData(connectedTile.getTileEntity(te.getWorldObj()), te, side);
				}
			}
			if (resetHandler) {
				SonarCore.sendPacketAround(te, 64, 4);
				resetHandler = false;
			}
			/*
			if (updateTicks == updateTime) {
				updateTicks = 0;
				SonarCore.sendPacketAround(te, 64, 0);
			} else
				updateTicks++;
		*/
		}

	}

	public LargeDisplayScreenHandler getHandler(TileEntity te) {
		List<ForgeDirection> dirs = new ArrayList();
		ForgeDirection meta = ForgeDirection.getOrientation(FMPHelper.getMeta(te));
		for (ForgeDirection dir : ForgeDirection.values()) {
			if (dir != meta && dir != meta.getOpposite()) {
				dirs.add(dir);
			}
		}
		ArrayList<BlockCoords> handlers = new ArrayList();
		return addCoords(te, handlers, dirs);

	}

	public LargeDisplayScreenHandler addCoords(TileEntity te, ArrayList<BlockCoords> handlers, List<ForgeDirection> dirs) {
		for (ForgeDirection side : dirs) {
			TileEntity tile = SonarHelper.getAdjacentTileEntity(te, side);
			if (tile == null) {
				continue;
			}
			TileHandler handler = FMPHelper.getHandler(tile);
			if (handler != null && handler instanceof LargeDisplayScreenHandler) {
				if (tile.getBlockMetadata() == te.getBlockMetadata()) {
					LargeDisplayScreenHandler screen = (LargeDisplayScreenHandler) handler;
					if (screen.isHandler.getObject()) {
						return screen;
					}
					BlockCoords coords = new BlockCoords(tile);
					if (!handlers.contains(coords)) {
						handlers.add(coords);
						return addCoords(tile, handlers, dirs);
					}
				}
			}
		}
		return null;
	}

	public BlockCoords getConnectedTile(TileEntity te, ForgeDirection dir) {
		List<BlockCoords> displays = DisplayRegistry.getScreens(registryID);
		if (displays != null) {
			for (BlockCoords coords : displays) {
				if (coords != null && coords.getTileEntity() != null) {
					INetworkCache network = LogisticsAPI.getCableHelper().getNetwork(coords.getTileEntity(), dir.getOpposite());
					if (!network.getConnections(CacheTypes.EMITTER, true).isEmpty()) {
						return coords;
					}
				}
			}
		}
		return null;

	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		super.writePacket(buf, id);
		if (id == 3) {
			if (sizing != null) {
				buf.writeBoolean(true);
				sizing.writeToBuf(buf);
			} else {
				buf.writeBoolean(false);
			}
		}
		if (id == 4) {
			isHandler.writeToBuf(buf);
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		super.readPacket(buf, id);
		if (id == 3) {
			if (buf.readBoolean()) {
				sizing = LargeScreenSizing.readFromBuf(buf);
			} else {
				sizing = null;
			}
		}
		if (id == 4) {
			isHandler.readFromBuf(buf);
		}
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			if (nbt.hasKey("mxY")) {
				sizing = LargeScreenSizing.readFromNBT(nbt);
			}
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			if (sizing != null) {
				sizing.writeToNBT(nbt);
			}
		}
	}

	public void addSyncParts(List<ISyncPart> parts) {
		super.addSyncParts(parts);
		parts.addAll(Lists.newArrayList(isHandler));
	}
}
