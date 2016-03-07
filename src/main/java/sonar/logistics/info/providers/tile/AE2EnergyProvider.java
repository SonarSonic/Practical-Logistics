package sonar.logistics.info.providers.tile;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.info.Info;
import sonar.logistics.api.info.StandardInfo;
import sonar.logistics.api.providers.TileProvider;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.networking.energy.IEnergyGrid;
import cpw.mods.fml.common.Loader;

public class AE2EnergyProvider extends TileProvider {

	public static String name = "AE2-Energy-Provider";
	public String[] categories = new String[] { "AE2 Energy" };
	public String[] subcategories = new String[] { "Stored Power", "Max Power", "Avg Power Injection", "Avg Power Usage", "Idle Power Usage" };

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity target = world.getTileEntity(x, y, z);
		return target != null && (target instanceof IAEPowerStorage || target instanceof IEnergyGrid);
	}

	@Override
	public void getHelperInfo(List<Info> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		int id = this.getID();
		TileEntity target = world.getTileEntity(x, y, z);
		if (target instanceof IAEPowerStorage) {
			IAEPowerStorage power = (IAEPowerStorage) target;
			infoList.add(new StandardInfo(id, 0, 0, (long) power.getAECurrentPower()).addSuffix("ae"));
			infoList.add(new StandardInfo(id, 0, 1, (long) power.getAEMaxPower()).addSuffix("ae"));
		}
		if (target instanceof IEnergyGrid) {
			IEnergyGrid grid = (IEnergyGrid) target;
			infoList.add(new StandardInfo(id, 0, 2, (int) grid.getAvgPowerInjection()).addSuffix("ae/t"));
			infoList.add(new StandardInfo(id, 0, 3, (int) grid.getAvgPowerUsage()).addSuffix("ae/t"));
			infoList.add(new StandardInfo(id, 0, 4, (int) grid.getIdlePowerUsage()).addSuffix("ae/t"));
			infoList.add(new StandardInfo(id, 0, 0, (int) grid.getStoredPower()).addSuffix("ae"));
			infoList.add(new StandardInfo(id, 0, 1, (int) grid.getMaxStoredPower()).addSuffix("ae"));
		}
	}

	@Override
	public String getCategory(int id) {
		return categories[id];
	}

	@Override
	public String getSubCategory(int id) {
		return subcategories[id];
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("appliedenergistics2");
	}

}
