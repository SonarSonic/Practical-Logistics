package sonar.logistics.info.providers.energy;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.energy.StoredEnergyStack;
import sonar.logistics.api.providers.EnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.tileentity.IEnergyInfo;

public class RFHandler extends EnergyHandler {

	public static String name = "RF-Provider";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(TileEntity tile, ForgeDirection dir) {
		return tile != null && (tile instanceof IEnergyInfo || tile instanceof IEnergyReceiver || tile instanceof IEnergyProvider);
	}

	@Override
	public void getEnergyInfo(List<StoredEnergyStack> energyList, TileEntity tile, ForgeDirection dir) {
		if (tile == null) {
			return;
		}
		boolean addedStorage = false;
		if (tile instanceof IEnergyInfo) {
			IEnergyInfo info = (IEnergyInfo) tile;
			if (!addedStorage) {
				energyList.add(new StoredEnergyStack(StoredEnergyStack.storage, 1, info.getInfoEnergyStored(), info.getInfoMaxEnergyStored()));
				addedStorage = true;
			}
			energyList.add(new StoredEnergyStack(StoredEnergyStack.usage, 1, info.getInfoEnergyPerTick(), info.getInfoMaxEnergyPerTick()));
		}
		if (tile instanceof IEnergyReceiver) {
			IEnergyReceiver receiver = (IEnergyReceiver) tile;
			if (!addedStorage) {
				energyList.add(new StoredEnergyStack(StoredEnergyStack.storage, 1, receiver.getEnergyStored(dir), receiver.getMaxEnergyStored(dir)));
				addedStorage = true;
			}
			int simulateAdd = receiver.receiveEnergy(dir, Integer.MAX_VALUE, true);
			energyList.add(new StoredEnergyStack(StoredEnergyStack.input, 1, simulateAdd, simulateAdd));
		}
		if (tile instanceof IEnergyProvider) {
			IEnergyProvider provider = (IEnergyProvider) tile;
			if (!addedStorage) {
				energyList.add(new StoredEnergyStack(StoredEnergyStack.storage, 1, provider.getEnergyStored(dir), provider.getMaxEnergyStored(dir)));
				addedStorage = true;
			}
			int simulateRemove = provider.extractEnergy(dir, Integer.MAX_VALUE, true);
			//energyList.add(new StoredEnergyStack(StoredEnergyStack.output, 1, simulateRemove, simulateRemove));
		}
	}

	@Override
	public double addEnergy(double transfer, TileEntity tile, ForgeDirection dir) {
		if (tile instanceof IEnergyReceiver) {
			IEnergyReceiver receiver = (IEnergyReceiver) tile;
			if (receiver.canConnectEnergy(dir.getOpposite())) {
				int transferRF = transfer < Integer.MAX_VALUE ? (int) transfer : Integer.MAX_VALUE;
				return receiver.receiveEnergy(dir.getOpposite(), transferRF, false);
			}
		}
		return 0;
	}

	@Override
	public double removeEnergy(double transfer, TileEntity tile, ForgeDirection dir) {
		if (tile instanceof IEnergyProvider) {
			IEnergyProvider receiver = (IEnergyProvider) tile;
			if (receiver.canConnectEnergy(dir.getOpposite())) {
				return receiver.extractEnergy(dir.getOpposite(), transfer < Integer.MAX_VALUE ? (int) transfer : Integer.MAX_VALUE, false);
			}
		}
		return 0;
	}

	@Override
	public String getSuffix(byte type) {
		switch (type) {
		case StoredEnergyStack.storage:
			return "RF";
		case StoredEnergyStack.input:
		case StoredEnergyStack.output:
		case StoredEnergyStack.usage:
			return "RF/T";
		}
		return "";
	}
}
