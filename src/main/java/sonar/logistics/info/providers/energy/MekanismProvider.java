package sonar.logistics.info.providers.energy;

import java.util.List;

import mekanism.api.energy.IStrictEnergyAcceptor;
import mekanism.api.energy.IStrictEnergyStorage;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.energy.StoredEnergyStack;
import sonar.logistics.api.providers.EnergyHandler;
import cpw.mods.fml.common.Loader;

public class MekanismProvider extends EnergyHandler {

	public static String name = "Mekanism-Provider";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(TileEntity tile, ForgeDirection dir) {
		return tile instanceof IStrictEnergyStorage;
	}

	@Override
	public void getEnergyInfo(List<StoredEnergyStack> energyList, TileEntity tile, ForgeDirection dir) {
		if (tile instanceof IStrictEnergyStorage) {
			IStrictEnergyStorage storage = (IStrictEnergyStorage) tile;
			energyList.add(new StoredEnergyStack(StoredEnergyStack.storage, 10, storage.getEnergy(), storage.getMaxEnergy()));
		}

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

	@Override
	public double addEnergy(double transfer, TileEntity tile, ForgeDirection dir) {
		if (tile instanceof IStrictEnergyAcceptor) {
			IStrictEnergyAcceptor acceptor = (IStrictEnergyAcceptor) tile;
			if (acceptor.canReceiveEnergy(dir)) {
				return acceptor.transferEnergyToAcceptor(dir, transfer / 10);
			}
		}
		return 0;
	}

	@Override
	public double removeEnergy(double transfer, TileEntity tile, ForgeDirection dir) {
		if (tile instanceof IStrictEnergyStorage) {
			IStrictEnergyStorage storage = (IStrictEnergyStorage) tile;
			double maxRemove = Math.min(transfer / 10, storage.getEnergy());
			storage.setEnergy(storage.getEnergy() - maxRemove);
			return maxRemove;
		}
		return 0;
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("Mekanism");
	}

}
