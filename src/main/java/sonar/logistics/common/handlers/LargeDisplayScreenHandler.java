package sonar.logistics.common.handlers;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.network.sync.SyncBoolean;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.connecting.ILargeDisplay;
import sonar.logistics.api.render.LargeScreenSizing;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.helpers.DisplayHelper;
import sonar.logistics.registries.DisplayRegistry;

public class LargeDisplayScreenHandler extends DisplayScreenHandler implements IByteBufTile {

	public SyncBoolean isHandler = new SyncBoolean(0);
	public LargeScreenSizing sizing;
	public boolean resetSizing = true;
	public BlockCoords connectedTile = null;
	public int registryID = -1;

	public LargeDisplayScreenHandler(boolean isMultipart, TileEntity tile) {
		super(isMultipart, tile);
	}

	@Override
	public void update(TileEntity te) {
		if (!te.getWorldObj().isRemote) {
			boolean lastHandler = isHandler.getBoolean();
			List<BlockCoords> displays = DisplayRegistry.getScreens(registryID);
			if (displays != null) {
				if (BlockCoords.equalCoords(displays.get(0), new BlockCoords(te))) {
					isHandler.setBoolean(true);
					LargeScreenSizing lastSize = sizing;
					sizing = DisplayHelper.getScreenSizing(te);

					if (sizing == null && lastSize!=null || (lastSize == null) || !lastSize.equals(sizing)) {
						SonarCore.sendPacketAround(te, 64, 2);
						//resetSizing = false;
					}

					connectedTile = getConnectedTile(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)));
					if (connectedTile == null || connectedTile.getTileEntity(te.getWorldObj()) == null) {
						this.updateData(te, te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)));
					} else {
						this.updateData(connectedTile.getTileEntity(te.getWorldObj()), te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)));
					}
				} else {
					isHandler.setBoolean(false);
				}
			}
			if (lastHandler != isHandler.getBoolean()) {
				SonarCore.sendPacketAround(te, 64, 3);
			}
		}

	}

	public BlockCoords getConnectedTile(TileEntity te, ForgeDirection dir) {
		List<BlockCoords> displays = DisplayRegistry.getScreens(registryID);
		if (displays != null) {
			for (BlockCoords coords : displays) {
				if (coords != null && coords.getTileEntity() != null) {
					List<BlockCoords> connections = CableHelper.getConnections(coords.getTileEntity(), dir.getOpposite());
					if (!connections.isEmpty()) {
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
		if (id == 2) {
			if (sizing != null) {
				buf.writeBoolean(true);
				sizing.writeToBuf(buf);
			} else {
				buf.writeBoolean(false);
			}
		}
		if (id == 3) {
			isHandler.writeToBuf(buf);
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		super.readPacket(buf, id);
		if (id == 2) {
			if (buf.readBoolean()) {
				sizing = LargeScreenSizing.readFromBuf(buf);
			} else {
				sizing = null;
			}
		}
		if (id == 3) {
			isHandler.readFromBuf(buf);
		}
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			isHandler.readFromNBT(nbt, type);
			if (nbt.hasKey("mxY")) {
				sizing = LargeScreenSizing.readFromNBT(nbt);
			}

		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			isHandler.writeToNBT(nbt, type);
			if (sizing != null) {
				sizing.writeToNBT(nbt);
			}
		}
	}

}
