package sonar.logistics.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.network.sync.SyncString;
import sonar.core.network.utils.ITextField;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.Info;
import sonar.logistics.api.connecting.IDataConnection;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.info.types.BlockCoordsInfo;
import sonar.logistics.registries.EmitterRegistry;

public class TileEntityDataEmitter extends TileEntityNode implements IDataConnection, ITextField {

	public SyncString clientName = new SyncString(0, "Unnamed Emitter");

	public TileEntityDataEmitter() {
	}

	public Object getConnectedTile() {
		return CableHelper.getConnectedTile(this, ForgeDirection.getOrientation(this.getBlockMetadata()).getOpposite());
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			clientName.readFromNBT(nbt, type);
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			clientName.writeToNBT(nbt, type);
		}
	}

	public void addToFrequency() {
		if (!this.worldObj.isRemote) {
			EmitterRegistry.addEmitters(playerName, new BlockCoords(this, this.worldObj.provider.dimensionId));
		}
	}

	public void removeFromFrequency() {
		if (!this.worldObj.isRemote) {
			EmitterRegistry.removeEmitter(playerName, new BlockCoords(this, this.worldObj.provider.dimensionId));
		}
	}

	public void onChunkUnload() {
		super.onChunkUnload();
		if (!this.worldObj.isRemote) {
			this.removeFromFrequency();
		}
	}

	public void onLoaded() {
		super.onLoaded();
		if (!this.worldObj.isRemote) {
			this.addToFrequency();
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (!this.worldObj.isRemote) {
			this.removeFromFrequency();
		}
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return dir == ForgeDirection.getOrientation(this.getBlockMetadata()).getOpposite();
	}

	@Override
	public void updateData(ForgeDirection dir) {

	}

	@Override
	public Info currentInfo() {
		return BlockCoordsInfo.createInfo("Data Emitter", new BlockCoords(this));
	}

	public boolean maxRender() {
		return true;
	}

	@Override
	public void textTyped(String string, int id) {
		if (id == 0) {
			if (string == null || string.isEmpty()) {
				this.clientName.setString("Unnamed Emitter");
			} else {
				this.clientName.setString(string);
			}
		}
	}
}