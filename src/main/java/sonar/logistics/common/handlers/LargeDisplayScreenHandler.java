package sonar.logistics.common.handlers;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.api.BlockCoords;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.utils.IByteBufTile;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.CacheTypes;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.ILargeDisplay;
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
				LargeScreenSizing lastSize = sizing;
				sizing = DisplayHelper.getScreenSizing(te);
				if (sizing == null && lastSize != null || (lastSize == null) || !lastSize.equals(sizing)) {
					SonarCore.sendPacketAround(te, 64, 3);
				}

				connectedTile = getConnectedTile(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)));
				if (connectedTile == null || connectedTile.getTileEntity(te.getWorldObj()) == null) {
					this.updateData(te, te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)));
				} else {
					this.updateData(connectedTile.getTileEntity(te.getWorldObj()), te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)));
				}
			}
			if (resetHandler) {
				SonarCore.sendPacketAround(te, 64, 4);
				resetHandler = false;
			}

			if (updateTicks == updateTime) {
				updateTicks = 0;
				SonarCore.sendPacketAround(te, 64, 0);
			} else
				updateTicks++;
		}

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
