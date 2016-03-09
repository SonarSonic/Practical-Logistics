package sonar.logistics.info.providers.energy;

import mekanism.api.energy.IStrictEnergyAcceptor;
import mekanism.api.energy.IStrictEnergyStorage;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.ActionType;
import sonar.logistics.api.providers.EnergyProvider;
import sonar.logistics.api.utils.EnergyType;
import sonar.logistics.api.utils.StoredEnergyStack;
import cpw.mods.fml.common.Loader;

public class MekanismProvider extends EnergyProvider {

	public static String name = "Mekanism-Provider";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideEnergy(TileEntity tile, ForgeDirection dir) {
		return tile instanceof IStrictEnergyStorage;
	}

	@Override
	public void getEnergy(StoredEnergyStack energyStack, TileEntity tile, ForgeDirection dir) {
		if (tile instanceof IStrictEnergyStorage) {
			IStrictEnergyStorage storage = (IStrictEnergyStorage) tile;
			energyStack.setStorageValues((long) (storage.getEnergy() / 10), (long) (storage.getMaxEnergy() / 10));
		}

	}

	@Override
	public StoredEnergyStack addEnergy(StoredEnergyStack transfer, TileEntity tile, ForgeDirection dir, ActionType action) {
		if (tile instanceof IStrictEnergyAcceptor) {
			IStrictEnergyAcceptor acceptor = (IStrictEnergyAcceptor) tile;
			if (acceptor.canReceiveEnergy(dir)) {
				transfer.stored -= acceptor.transferEnergyToAcceptor(dir, transfer.stored);
			}
		}
		if (transfer.stored == 0)
			transfer = null;
		return transfer;
	}

	@Override
	public StoredEnergyStack removeEnergy(StoredEnergyStack transfer, TileEntity tile, ForgeDirection dir, ActionType action) {
		if (tile instanceof IStrictEnergyStorage) {
			IStrictEnergyStorage storage = (IStrictEnergyStorage) tile;
			double maxRemove = Math.min(transfer.stored, storage.getEnergy());
			transfer.stored -= maxRemove;
			storage.setEnergy(storage.getEnergy() - maxRemove);
		}
		return transfer;
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("Mekanism");
	}

	@Override
	public EnergyType getProvidedType() {
		return EnergyType.MJ;
	}

}
