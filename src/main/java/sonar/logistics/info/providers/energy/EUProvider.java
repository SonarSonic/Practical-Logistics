package sonar.logistics.info.providers.energy;

import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.tile.IEnergyStorage;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.energy.StoredEnergyStack;
import sonar.logistics.api.providers.EnergyHandler;
import cpw.mods.fml.common.Loader;

public class EUProvider extends EnergyHandler {

	public static String name = "EU-Provider";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(TileEntity tile, ForgeDirection dir) {
		return tile instanceof IEnergySink || tile instanceof IEnergySource || tile instanceof IEnergyStorage;
	}

	@Override
	public void getEnergyInfo(StoredEnergyStack energyStack, TileEntity tile, ForgeDirection dir) {
		if (tile instanceof IEnergyStorage) {
			IEnergyStorage storage = (IEnergyStorage) tile;
			energyStack.setStorageValues(storage.getStored() * 4, storage.getCapacity() * 4);
			energyStack.setMaxOutput((long)(storage.getOutputEnergyUnitsPerTick() * 4));
		}
		if (tile instanceof IEnergySink) {
			IEnergySink sink = (IEnergySink) tile;
			energyStack.setMaxInput((long)(sink.getDemandedEnergy() * 4));
		}
		if (tile instanceof IEnergySource) {
			IEnergySource source = (IEnergySource) tile;
			energyStack.setMaxOutput((long)(source.getOfferedEnergy() * 4));
		}
	}

	@Override
	public double addEnergy(double transfer, TileEntity tile, ForgeDirection dir) {
		if (tile instanceof IEnergySink) {
			IEnergySink sink = (IEnergySink) tile;
			double transferEU = transfer / 4;
			return (transferEU - sink.injectEnergy(dir, transferEU, getVoltage(sink.getSinkTier()))) * 4;
		}
		return 0;
	}

	@Override
	public double removeEnergy(double transfer, TileEntity tile, ForgeDirection dir) {
		if (tile instanceof IEnergySource) {
			IEnergySource source = (IEnergySource) tile;
			double amount = Math.min(transfer / 4, source.getOfferedEnergy());
			source.drawEnergy(amount);
			return amount;
		}
		return 0;
	}

	public double getVoltage(int tier) {
		switch (tier) {
		case 1:
			return 32;
		case 2:
			return 128;
		case 3:
			return 512;
		case 4:
			return 2048;
		default:
			return 8192;
		}
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("IC2");
	}

}
