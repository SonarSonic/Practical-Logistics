package sonar.logistics.common.tileentity;

import io.netty.buffer.ByteBuf;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.Info;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.CacheTypes;
import sonar.logistics.info.types.BlockCoordsInfo;
import sonar.logistics.info.types.ProgressInfo;

public class TileEntityClock extends TileEntityConnection implements IByteBufTile {

	// server only
	public long lastMillis;// when the movement was started
	public long currentMillis;// the current millis
	public long offset = 0;
	// public long tickTime;// tick time in millis

	public SyncTagType.LONG tickTime = new SyncTagType.LONG(0);

	public float rotation;// 0-360 indicating rotation of the clock hand.
	public boolean isSet;

	public boolean lastSignal;
	public boolean wasStarted;
	public boolean powering;

	public long finalStopTime;

	public void updateEntity() {
		super.updateEntity();
		if (isClient()) {
			return;
		}
		currentMillis = (worldObj.getTotalWorldTime() * 50);
		if (!(tickTime.getObject() < 10)) {
			long start = currentMillis - lastMillis;
			rotation = (start) * 360 / (tickTime.getObject());
			if (start > tickTime.getObject()) {
				this.lastMillis = currentMillis;
				powering = true;
				this.worldObj.notifyBlockChange(xCoord, yCoord, zCoord, getBlockType());
				// send signal
			} else {
				if (powering) {
					powering = false;
					this.worldObj.notifyBlockChange(xCoord, yCoord, zCoord, getBlockType());
				}
			}
			markDirty();
		}
		SonarCore.sendPacketAround(this, 64, 0);
	}

	public void checkStopwatch() {
		/*
		 * if (isServer()) { ForgeDirection dir =
		 * ForgeDirection.getOrientation(this.getBlockMetadata()).getOpposite();
		 * boolean power = worldObj.isBlockIndirectlyGettingPowered(xCoord,
		 * yCoord, zCoord); if (power != lastSignal) { lastSignal = power; if
		 * (!wasStarted) { this.lastMillis = worldObj.getTotalWorldTime() * 100;
		 * wasStarted = true; } else { finalStopTime =
		 * (worldObj.getTotalWorldTime()) - lastMillis; wasStarted = false; } }
		 * }
		 */
	}

	@Override
	public Info currentInfo() {
		if (!(tickTime.getObject() < 10)) {
			long start = currentMillis - lastMillis;
			String timeString = new SimpleDateFormat("HH:mm:ss:SSS").format((start) - (60 * 60 * 1000)).substring(0, 11);
			return new ProgressInfo(start, tickTime.getObject(), timeString);
		} else {
			return BlockCoordsInfo.createInfo("CLOCK", new BlockCoords(this));
		}

		/*
		 * if (setting.getObject() == 1) { if (wasStarted) { String timeString =
		 * new SimpleDateFormat("HH:mm:ss:SSS").format(start * 100 - (60 * 60 *
		 * 1000)).substring(0, 11); return new StandardInfo(-1, "TIME",
		 * " Running ", timeString); } else { String timeString = new
		 * SimpleDateFormat("HH:mm:ss:SSS").format(finalStopTime * 100 - (60 *
		 * 60 * 1000)).substring(0, 11); return new StandardInfo(-1, "TIME",
		 * " Completed ", timeString); } } return
		 * BlockCoordsInfo.createInfo("CLOCK", new BlockCoords(this));
		 */
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return dir.equals(ForgeDirection.getOrientation(FMPHelper.getMeta(this)));

	}

	public boolean maxRender() {
		return true;
	}

	@Override
	public void addConnections() {
		LogisticsAPI.getCableHelper().addConnection(this, ForgeDirection.getOrientation(FMPHelper.getMeta(this)));
	}

	@Override
	public void removeConnections() {
		LogisticsAPI.getCableHelper().removeConnection(this, ForgeDirection.getOrientation(FMPHelper.getMeta(this)));
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		tickTime.readFromNBT(nbt, type);
		if (type == SyncType.SAVE) {
			nbt.setBoolean("isSet", isSet);
			nbt.setBoolean("lastSignal", lastSignal);
			nbt.setBoolean("wasStarted", wasStarted);
			nbt.setLong("finalStopTime", finalStopTime);
		}

	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		tickTime.writeToNBT(nbt, type);
		if (type == SyncType.SAVE) {
			this.isSet = nbt.getBoolean("isSet");
			this.lastSignal = nbt.getBoolean("lastSignal");
			this.wasStarted = nbt.getBoolean("wasStarted");
			this.finalStopTime = nbt.getLong("finalStopTime");
		}
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		if (id == 0) {
			buf.writeFloat(rotation);
		}
		if (id == 1) {
			tickTime.writeToBuf(buf);
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		if (id == 0) {
			rotation = buf.readFloat();
		}
		if (id == 1) {
			tickTime.readFromBuf(buf);
		}
		if (id == 2) {
			tickTime.increaseBy(100);
		}
		if (id == 3) {
			tickTime.increaseBy(-100);
		}
		if (id == 4) {
			tickTime.increaseBy(1000);
		}
		if (id == 5) {
			tickTime.increaseBy(-1000);
		}
		if (id == 6) {
			tickTime.increaseBy(60000);
		}
		if (id == 7) {
			tickTime.increaseBy(-60000);
		}
		if (id == 8) {
			tickTime.increaseBy(60000 * 60);
		}
		if (id == 9) {
			tickTime.increaseBy(-(60000 * 60));

		}
		if (tickTime.getObject() < 0) {
			tickTime.setObject((long) 0);
		} else if (tickTime.getObject() > (1000 * 60 * 60 * 24)) {
			tickTime.setObject((long) ((1000 * 60 * 60 * 24)-1));
		}
	}
}
