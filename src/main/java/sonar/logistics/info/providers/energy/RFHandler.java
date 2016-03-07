package sonar.logistics.info.providers.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.ActionType;
import sonar.logistics.api.providers.EnergyProvider;
import sonar.logistics.api.utils.EnergyType;
import sonar.logistics.api.utils.StoredEnergyStack;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.tileentity.IEnergyInfo;

public class RFHandler extends EnergyProvider {

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
	public void getEnergyInfo(StoredEnergyStack energyStack, TileEntity tile, ForgeDirection dir) {
		if (tile == null) {
			return;
		}
		if (tile instanceof IEnergyInfo) {
			IEnergyInfo info = (IEnergyInfo) tile;
			energyStack.setStorageValues(info.getInfoEnergyStored(), info.getInfoMaxEnergyStored());			
			energyStack.setUsage(info.getInfoEnergyPerTick());
		}
		if (tile instanceof IEnergyReceiver) {
			IEnergyReceiver receiver = (IEnergyReceiver) tile;
			energyStack.setStorageValues(receiver.getEnergyStored(dir), receiver.getMaxEnergyStored(dir));	
			int simulateAdd = receiver.receiveEnergy(dir, Integer.MAX_VALUE, true);
			energyStack.setMaxInput(simulateAdd);
		}
		if (tile instanceof IEnergyProvider) {
			IEnergyProvider provider = (IEnergyProvider) tile;
			energyStack.setStorageValues(provider.getEnergyStored(dir), provider.getMaxEnergyStored(dir));	
			int simulateRemove = provider.extractEnergy(dir, Integer.MAX_VALUE, true);
			energyStack.setMaxOutput(simulateRemove);
		}
	}

	@Override
	public double addEnergy(long transfer, TileEntity tile, ForgeDirection dir, ActionType action) {
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
	public double removeEnergy(long transfer, TileEntity tile, ForgeDirection dir, ActionType action) {
		if (tile instanceof IEnergyProvider) {
			IEnergyProvider receiver = (IEnergyProvider) tile;
			if (receiver.canConnectEnergy(dir.getOpposite())) {
				return receiver.extractEnergy(dir.getOpposite(), transfer < Integer.MAX_VALUE ? (int) transfer : Integer.MAX_VALUE, false);
			}
		}
		return 0;
	}

	@Override
	public EnergyType getProvidedType() {
		return EnergyType.RF;
	}
}
