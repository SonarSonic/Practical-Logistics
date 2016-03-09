package sonar.logistics.info.providers.energy;

import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.tile.IEnergyStorage;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.ActionType;
import sonar.logistics.api.providers.EnergyProvider;
import sonar.logistics.api.utils.EnergyType;
import sonar.logistics.api.utils.StoredEnergyStack;
import cpw.mods.fml.common.Loader;

public class EUProvider extends EnergyProvider {

	public static String name = "EU-Provider";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideEnergy(TileEntity tile, ForgeDirection dir) {
		return tile instanceof IEnergySink || tile instanceof IEnergySource || tile instanceof IEnergyStorage;
	}

	@Override
	public void getEnergy(StoredEnergyStack energyStack, TileEntity tile, ForgeDirection dir) {
		if (tile instanceof IEnergyStorage) {
			IEnergyStorage storage = (IEnergyStorage) tile;
			energyStack.setStorageValues(storage.getStored() * 4, storage.getCapacity() * 4);
			energyStack.setMaxOutput((long) (storage.getOutputEnergyUnitsPerTick() * 4));
		}
		if (tile instanceof IEnergySink) {
			IEnergySink sink = (IEnergySink) tile;
			energyStack.setMaxInput((long) (sink.getDemandedEnergy() * 4));
		}
		if (tile instanceof IEnergySource) {
			IEnergySource source = (IEnergySource) tile;
			energyStack.setMaxOutput((long) (source.getOfferedEnergy() * 4));
		}
	}

	@Override
	public StoredEnergyStack addEnergy(StoredEnergyStack transfer, TileEntity tile, ForgeDirection dir, ActionType action) {
		if (tile instanceof IEnergySink) {
			IEnergySink sink = (IEnergySink) tile;
			transfer.stored = (long) (transfer.stored - sink.injectEnergy(dir, transfer.stored, getVoltage(sink.getSinkTier())));
		}
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}

	@Override
	public StoredEnergyStack removeEnergy(StoredEnergyStack transfer, TileEntity tile, ForgeDirection dir, ActionType action) {
		if (tile instanceof IEnergySource) {
			IEnergySource source = (IEnergySource) tile;
			double amount = Math.min(transfer.stored, source.getOfferedEnergy());
			source.drawEnergy(amount);
			transfer.stored -= amount;
		}
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
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

	@Override
	public EnergyType getProvidedType() {
		return EnergyType.EU;
	}

}
