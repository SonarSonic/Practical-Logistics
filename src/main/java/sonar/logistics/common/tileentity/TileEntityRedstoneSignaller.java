package sonar.logistics.common.tileentity;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.network.utils.ITextField;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.Info;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.CacheTypes;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.connecting.ILogicTile;
import sonar.logistics.common.blocks.BlockRedstoneSignaller;
import sonar.logistics.registries.BlockRegistry;

import com.google.common.collect.Lists;

public class TileEntityRedstoneSignaller extends TileEntitySonar implements ILogicTile, IByteBufTile, ITextField {

	public Info currentInfo;
	public SyncTagType.INT integerEmitType = new SyncTagType.INT(0);
	public SyncTagType.INT integerTarget = new SyncTagType.INT(1);
	public SyncTagType.INT dataType = new SyncTagType.INT(2);
	public SyncTagType.INT errorFlag = new SyncTagType.INT(3);
	public SyncTagType.STRING stringName = new SyncTagType.STRING(4);

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return ForgeDirection.getOrientation(this.getBlockMetadata()).getOpposite() == dir;
	}

	public void addSyncParts(List<ISyncPart> parts) {
		super.addSyncParts(parts);
		parts.addAll(Lists.newArrayList(stringName, integerEmitType, integerTarget, dataType, errorFlag));
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
			List<BlockCoords> connections = LogisticsAPI.getCableHelper().getConnections(this, ForgeDirection.getOrientation(this.getBlockMetadata()).getOpposite());
			if (!connections.isEmpty()) {
				Object object = FMPHelper.getTile(connections.get(0).getTileEntity());
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
			if (currentInfo.getDataType() == dataType.getObject()) {
				if (dataType.getObject() == 0) {
					this.errorFlag.setObject(0);
					long integer = Long.parseLong(currentInfo.getData());
					switch (integerEmitType.getObject()) {
					case 0:
						return integer == integerTarget.getObject();
					case 1:
						return integer > integerTarget.getObject();
					case 2:
						return integer < integerTarget.getObject();
					case 3:
						return integer != integerTarget.getObject();
					}
				} else if (dataType.getObject() == 1) {
					this.errorFlag.setObject(0);
					return currentInfo.getData().equals(stringName.getObject());
				}
			} else {
				this.errorFlag.setObject(2);
			}
		} else {
			this.errorFlag.setObject(1);
			return false;
		}
		return false;
	}

	@Override
	public void textTyped(String string, int id) {
		if (id == 0) {
			if (string == null || string.isEmpty()) {
				this.integerTarget.setObject(0);
			} else {
				this.integerTarget.setObject(Integer.parseInt(string));
			}
		}
		if (id == 1) {
			if (string == null || string.isEmpty()) {
				this.stringName.setObject("Unnamed Emitter");
			} else {
				this.stringName.setObject(string);
				;
			}
		}
	}

	@Override
	public BlockCoords getCoords() {
		return new BlockCoords(this);
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		switch (id) {
		case 0:
			buf.writeInt(dataType.getObject());
			break;
		case 1:
			buf.writeInt(integerEmitType.getObject());
			break;
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		switch (id) {
		case 0:
			dataType.setObject(buf.readInt());
			break;
		case 1:
			integerEmitType.setObject(buf.readInt());
			break;
		}
	}

	@Override
	public void getCacheTypes(ArrayList<CacheTypes> types) {
		types.add(CacheTypes.EMITTER);

	}
}
