package sonar.logistics.info.providers.tile;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.data.TileProvider;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.networking.energy.IEnergyGrid;
import cpw.mods.fml.common.Loader;

public class AE2EnergyProvider extends TileProvider {

	public static String name = "AE2-Energy-Provider";
	public String[] categories = new String[] { "AE2 Energy"};
	public String[] subcategories = new String[] { "Stored Power", "Max Power", "Avg Power Injection", "Avg Power Usage", "Idle Power Usage"};

	@Override
	public String helperName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity target = world.getTileEntity(x, y, z);
		return target!=null && (target instanceof IAEPowerStorage || target instanceof IEnergyGrid);
	}

	@Override
	public void getHelperInfo(List<Info> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		byte id = this.getID();
		TileEntity target = world.getTileEntity(x, y, z);
		if (target instanceof IAEPowerStorage) {
			IAEPowerStorage power = (IAEPowerStorage) target;
			infoList.add(new StandardInfo(id, 0, 0, (int)power.getAECurrentPower(), "ae"));
			infoList.add(new StandardInfo(id, 0, 1, (int)power.getAEMaxPower(), "ae"));
		}
		if (target instanceof IEnergyGrid) {
			IEnergyGrid grid = (IEnergyGrid) target;
			infoList.add(new StandardInfo(id, 0, 2, (int)grid.getAvgPowerInjection(), "ae/t"));
			infoList.add(new StandardInfo(id, 0, 3, (int)grid.getAvgPowerUsage(), "ae/t"));
			infoList.add(new StandardInfo(id, 0, 4, (int)grid.getIdlePowerUsage(), "ae/t"));
			infoList.add(new StandardInfo(id, 0, 0, (int)grid.getStoredPower(), "ae"));
			infoList.add(new StandardInfo(id, 0, 1, (int)grid.getMaxStoredPower(), "ae"));
		}
	}

	@Override
	public String getCategory(byte id) {
		return categories[id];
	}

	@Override
	public String getSubCategory(byte id) {
		return subcategories[id];
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("appliedenergistics2");
	}
}
