package sonar.logistics.common.tileentity;

import io.netty.buffer.ByteBuf;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.network.sync.SyncInt;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.core.utils.helpers.SonarHelper;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.info.types.BlockCoordsInfo;
import sonar.logistics.info.types.ProgressInfo;

public class TileEntityClock extends TileEntityConnection implements IByteBufTile {

	// server only
	public long lastMillis;// when the movement was started
	public long currentMillis;// the current millis
	public long tickTime;// tick time in millis

	public float rotation;// 0-360 indicating rotation of the clock hand.
	public boolean isSet;

	public boolean lastSignal;
	public boolean wasStarted;

	public long finalStopTime;

	/** 0 = Clock, 1 = Stop watch, 3 = Countdown */
	public SyncInt setting = new SyncInt(0);

	public void updateEntity() {
		super.updateEntity();
		if (isClient()) {
			return;
		}
		this.setting.setInt(1);
		currentMillis = worldObj.getTotalWorldTime() * 100;
		if (setting.getInt() == 0) {
			tickTime = 100000000;
			if (tickTime == 0) {
				return;
			}
			long start = currentMillis - lastMillis;
			rotation = start * 360 / tickTime;
			if (currentMillis > lastMillis + tickTime) {
				this.lastMillis = currentMillis;
				// send signal
			}

		}
		if (setting.getInt() == 1) {
			long start = currentMillis - lastMillis;
			rotation = start * 360 / 1000;
		}
		SonarCore.sendPacketAround(this, 64, 0);
	}

	public void checkStopwatch() {
		if (isServer()) {
			ForgeDirection dir = ForgeDirection.getOrientation(this.getBlockMetadata()).getOpposite();
			System.out.print(dir);
			int power = worldObj.isBlockProvidingPowerTo(xCoord+dir.offsetX,yCoord+ dir.offsetY, zCoord+dir.offsetZ, ForgeDirection.getOrientation(this.getBlockMetadata()).ordinal());
			System.out.print(power);
			if ((power != 0) != lastSignal) {
				if (!wasStarted) {
					this.lastMillis = worldObj.getTotalWorldTime() * 100;
					wasStarted = true;
				} else {
					finalStopTime = worldObj.getTotalWorldTime() * 100 - lastMillis;
					wasStarted = false;
				}
			}
		}
	}

	@Override
	public Info currentInfo() {
		long start = currentMillis - lastMillis;
		if (setting.getInt() == 0) {

			long second = (start / 1000) % 60;
			long minute = (start / (1000 * 60)) % 60;
			long hour = (start / (1000 * 60 * 60)) % 24;

			String timeString = new SimpleDateFormat("HH:mm:ss:SSS").format(start - (60 * 60 * 1000)).substring(0, 11);
			return new ProgressInfo(start, tickTime, timeString);
		}
		if (setting.getInt() == 1) {
			if (wasStarted) {
				String timeString = new SimpleDateFormat("HH:mm:ss:SSS").format(start - (60 * 60 * 1000)).substring(0, 11);
				return new StandardInfo(-1, "TIME", " Running ", timeString);
			} else {
				String timeString = new SimpleDateFormat("HH:mm:ss:SSS").format(finalStopTime - (60 * 60 * 1000)).substring(0, 11);
				return new StandardInfo(-1, "TIME", " Completed ", timeString);
			}
		}

		return BlockCoordsInfo.createInfo("CLOCK", new BlockCoords(this));
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
		CableHelper.addConnection(this, ForgeDirection.getOrientation(FMPHelper.getMeta(this)));
	}

	@Override
	public void removeConnections() {
		CableHelper.removeConnection(this, ForgeDirection.getOrientation(FMPHelper.getMeta(this)));
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE) {
			nbt.setBoolean("isSet", isSet);
			nbt.setLong("tickTime", tickTime);
			setting.readFromNBT(nbt, type);
		}

	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE) {
			this.isSet = nbt.getBoolean("isSet");
			this.tickTime = nbt.getLong("tickTime");
			setting.writeToNBT(nbt, type);
		}
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		if (id == 0) {
			buf.writeFloat(rotation);
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		if (id == 0) {
			rotation = buf.readFloat();
		}

	}

}
