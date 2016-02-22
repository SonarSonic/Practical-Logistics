package sonar.logistics.common.handlers;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.connecting.ILargeDisplay;
import sonar.logistics.api.render.LargeScreenSizing;
import sonar.logistics.helpers.DisplayHelper;
import sonar.logistics.registries.DisplayRegistry;

public class LargeDisplayScreenHandler extends DisplayScreenHandler implements IByteBufTile {

	public SyncTagType.BOOLEAN isHandler = new SyncTagType.BOOLEAN(0);
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
			boolean lastHandler = isHandler.getObject();
			List<BlockCoords> displays = DisplayRegistry.getScreens(registryID);
			if (displays != null) {
				if (BlockCoords.equalCoords(displays.get(0), new BlockCoords(te))) {
					TileEntity target = te.getWorldObj().getTileEntity(te.xCoord, te.yCoord - 1, te.zCoord);
					if (target != null && target instanceof ILargeDisplay) {
						if (((ILargeDisplay) target).registryID() == registryID) {
							List<BlockCoords> screens = (List<BlockCoords>) ((ArrayList<BlockCoords>) DisplayRegistry.getScreens(registryID)).clone();
							int pos = 0;
							for (BlockCoords coords : screens) {
								if (BlockCoords.equalCoords(coords, new BlockCoords(te.xCoord, te.yCoord - 1, te.zCoord, te.getWorldObj().provider.dimensionId))) {
									Collections.swap(DisplayRegistry.getScreens(registryID), 0, pos);
									return;
								}
								pos++;
							}
						}
					}
					isHandler.setObject(true);
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
				} else {
					isHandler.setObject(false);
				}
			}
			if (lastHandler != isHandler.getObject()) {
				SonarCore.sendPacketAround(te, 64, 4);
			}

			if (updateTicks == updateTime){
				updateTicks = 0;
				SonarCore.sendPacketAround(te, 64, 0);
			}else
				updateTicks++;
		}

	}

	public BlockCoords getConnectedTile(TileEntity te, ForgeDirection dir) {
		List<BlockCoords> displays = DisplayRegistry.getScreens(registryID);
		if (displays != null) {
			for (BlockCoords coords : displays) {
				if (coords != null && coords.getTileEntity() != null) {
					List<BlockCoords> connections = LogisticsAPI.getCableHelper().getConnections(coords.getTileEntity(), dir.getOpposite());
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
