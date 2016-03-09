package sonar.logistics.info.providers.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.ActionType;
import sonar.logistics.api.providers.EnergyProvider;
import sonar.logistics.api.utils.EnergyType;
import sonar.logistics.api.utils.StoredEnergyStack;
import sonar.logistics.integration.AE2Helper;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.networking.energy.IEnergyGrid;
import cpw.mods.fml.common.Loader;

public class AEProvider extends EnergyProvider {

	public static String name = "AE-Provider";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideEnergy(TileEntity tile, ForgeDirection dir) {
		return (tile instanceof IAEPowerStorage || tile instanceof IEnergyGrid);
	}

	@Override
	public void getEnergy(StoredEnergyStack energyStack, TileEntity tile, ForgeDirection dir) {
		if (tile instanceof IEnergyGrid) {
			IEnergyGrid grid = (IEnergyGrid) tile;
			energyStack.setUsage((long) grid.getAvgPowerUsage());
			energyStack.setStorageValues((long) grid.getStoredPower(), (long) grid.getMaxStoredPower());
		} else if (tile instanceof IAEPowerStorage) {
			IAEPowerStorage power = (IAEPowerStorage) tile;
			energyStack.setStorageValues((long) power.getAECurrentPower(), (long) power.getAEMaxPower());
		}
	}

	@Override
	public StoredEnergyStack addEnergy(StoredEnergyStack transfer, TileEntity tile, ForgeDirection dir, ActionType action) {
		if (tile instanceof IEnergyGrid) {
			IEnergyGrid grid = (IEnergyGrid) tile;
			transfer.stored = (long) grid.injectPower(Math.min(transfer.stored, 10000), AE2Helper.getActionable(action));
		} else if (tile instanceof IAEPowerStorage) {
			IAEPowerStorage grid = (IAEPowerStorage) tile;
			transfer.stored = (long) grid.injectAEPower(Math.min(transfer.stored, 10000), AE2Helper.getActionable(action));
		}
		if(transfer.stored==0)
			transfer=null;
		return transfer;
	}

	@Override
	public StoredEnergyStack removeEnergy(StoredEnergyStack transfer, TileEntity tile, ForgeDirection dir, ActionType action) {
		if (tile instanceof IEnergyGrid) {
			IEnergyGrid grid = (IEnergyGrid) tile;
			transfer.stored -= grid.extractAEPower((double) Math.min(transfer.stored, 10000), AE2Helper.getActionable(action), PowerMultiplier.CONFIG);
		} else if (tile instanceof IAEPowerStorage) {
			IAEPowerStorage grid = (IAEPowerStorage) tile;
			transfer.stored -= grid.extractAEPower(Math.min(transfer.stored, 10000), AE2Helper.getActionable(action), PowerMultiplier.CONFIG);
		}
		if(transfer.stored==0)
			transfer=null;
		return transfer;
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("appliedenergistics2");
	}

	@Override
	public EnergyType getProvidedType() {
		return EnergyType.AE;
	}
}