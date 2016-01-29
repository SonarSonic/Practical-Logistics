package sonar.logistics.common.tileentity;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.network.sync.SyncInt;
import sonar.core.network.sync.SyncString;
import sonar.core.network.utils.ITextField;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.IMachineButtons;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.Info;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.connecting.IMultiTile;
import sonar.logistics.common.blocks.BlockRedstoneSignaller;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.registries.BlockRegistry;

public class TileEntityRedstoneSignaller extends TileEntitySonar implements IMultiTile, IMachineButtons, ITextField {

	public Info currentInfo;
	public SyncInt integerEmitType = new SyncInt(0);
	public SyncInt integerTarget = new SyncInt(1);
	public SyncInt dataType = new SyncInt(2);
	public SyncInt errorFlag = new SyncInt(3);
	public SyncString stringName = new SyncString(4);

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return ForgeDirection.getOrientation(this.getBlockMetadata()).getOpposite() == dir;
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			this.stringName.readFromNBT(nbt, type);
			this.integerEmitType.readFromNBT(nbt, type);
			this.integerTarget.readFromNBT(nbt, type);
			this.dataType.readFromNBT(nbt, type);
			this.errorFlag.readFromNBT(nbt, type);
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			this.stringName.writeToNBT(nbt, type);
			this.integerEmitType.writeToNBT(nbt, type);
			this.integerTarget.writeToNBT(nbt, type);
			this.dataType.writeToNBT(nbt, type);
			this.errorFlag.writeToNBT(nbt, type);

		}
	}

	public void updateEntity() {
		if (isServer()) {
			BlockRedstoneSignaller block = ((BlockRedstoneSignaller) worldObj.getBlock(xCoord, yCoord, zCoord));
			boolean isOn = block == BlockRegistry.redstoneSignaller_on;
			boolean canEmit = canEmit();
			if (isOn != canEmit) {
				block.updateSignallerState(canEmit, worldObj, xCoord, yCoord, zCoord);
			}
			boolean setNull = true;
			List<BlockCoords> connections = CableHelper.getConnections(this, ForgeDirection.getOrientation(this.getBlockMetadata()).getOpposite());
			if (!connections.isEmpty()) {
				Object object = CableHelper.getTile(connections.get(0).getTileEntity());
				if (object != null) {
					if (object instanceof IInfoEmitter) {
						IInfoEmitter infoNode = (IInfoEmitter) object;
						this.currentInfo = infoNode.currentInfo();
						setNull = false;
					}
				}
			}
			if (setNull) {
				this.currentInfo = null;
			}
		}
	}

	public boolean maxRender() {
		return true;
	}

	public boolean canEmit() {
		if (currentInfo != null) {
			if (currentInfo.getDataType() == dataType.getInt()) {
				if (dataType.getInt() == 0) {
					this.errorFlag.setInt(0);
					long integer = Long.parseLong(currentInfo.getData());
					switch (integerEmitType.getInt()) {
					case 0:
						return integer == integerTarget.getInt();
					case 1:
						return integer > integerTarget.getInt();
					case 2:
						return integer < integerTarget.getInt();
					case 3:
						return integer != integerTarget.getInt();
					}
				} else if (dataType.getInt() == 1) {
					this.errorFlag.setInt(0);
					return currentInfo.getData().equals(stringName.getString());
				}
			} else {
				this.errorFlag.setInt(2);
			}
		} else {
			this.errorFlag.setInt(1);
			return false;
		}
		return false;
	}

	@Override
	public void buttonPress(int buttonID, int value) {

		switch (buttonID) {
		case 0:
			dataType.setInt(value);
			break;
		case 1:
			integerEmitType.setInt(value);
			break;
		}

	}

	@Override
	public void textTyped(String string, int id) {
		if (id == 0) {
			if (string == null || string.isEmpty()) {
				this.integerTarget.setInt(0);
			} else {
				this.integerTarget.setInt(Integer.parseInt(string));
			}
		}
		if (id == 1) {
			if (string == null || string.isEmpty()) {
				this.stringName.setString("Unnamed Emitter");
			} else {
				this.stringName.setString(string);
				;
			}
		}
	}

	@Override
	public BlockCoords getCoords() {
		return new BlockCoords(this);
	}
}
