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
	public boolean canProvideInfo(TileEntity tile, ForgeDirection dir) {
		return (tile instanceof IAEPowerStorage || tile instanceof IEnergyGrid);
	}

	@Override
	public void getEnergyInfo(StoredEnergyStack energyStack, TileEntity tile, ForgeDirection dir) {
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
	public double addEnergy(long transfer, TileEntity tile, ForgeDirection dir, ActionType action) {
		if (tile instanceof IEnergyGrid) {
			IEnergyGrid grid = (IEnergyGrid) tile;
			transfer -= grid.injectPower(Math.min(transfer, 10000), AE2Helper.getActionable(action));
			return transfer;
		}
		if (tile instanceof IAEPowerStorage) {
			IAEPowerStorage grid = (IAEPowerStorage) tile;
			transfer -= grid.injectAEPower(Math.min(transfer, 10000), AE2Helper.getActionable(action));
			return transfer;
		}
		return 0;
	}

	@Override
	public double removeEnergy(long transfer, TileEntity tile, ForgeDirection dir, ActionType action) {
		if (tile instanceof IEnergyGrid) {
			IEnergyGrid grid = (IEnergyGrid) tile;
			transfer = (long) grid.extractAEPower((double)Math.min(transfer, 10000), AE2Helper.getActionable(action), PowerMultiplier.CONFIG);
			return transfer;
		}
		if (tile instanceof IAEPowerStorage) {
			IAEPowerStorage grid = (IAEPowerStorage) tile;
			transfer = (long) grid.extractAEPower(Math.min(transfer, 10000), AE2Helper.getActionable(action), PowerMultiplier.CONFIG);
			return transfer;
		}
		return 0;
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("appliedenergistics2");
	}

	@Override
	public EnergyType getProvidedType() {
		return EnergyType.AE;
	}
}