package sonar.logistics.info.providers.tile;

import java.util.List;

import com.nauktis.solarflux.blocks.SolarPanelTileEntity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.providers.TileProvider;
import cpw.mods.fml.common.Loader;

public class SolarFluxProvider extends TileProvider {

	public static String name = "Solar Flux";
	public String[] categories = new String[] { "Solar Panel" };
	public String[] subcategories = new String[] { "Current Generation", "Max Generation", "Sun Intensity", "Upgrades Installed"};

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity target = world.getTileEntity(x, y, z);
		return target != null && target instanceof SolarPanelTileEntity;
	}

	@Override
	public void getTileInfo(List<ILogicInfo> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		int id = this.getID();
		TileEntity target = world.getTileEntity(x, y, z);
		if (target instanceof SolarPanelTileEntity) {
			SolarPanelTileEntity panel = (SolarPanelTileEntity) target;
			infoList.add(new LogicInfo(id, 0, 0, panel.getCurrentEnergyGeneration()).addSuffix("rf/t"));
			infoList.add(new LogicInfo(id, 0, 1, panel.getMaximumEnergyGeneration()).addSuffix("rf/t"));
			infoList.add(new LogicInfo(id, 0, 2, (int)(Math.floor(panel.getSunIntensity()*100))).addSuffix("%"));
			infoList.add(new LogicInfo(id, 0, 3, panel.getTotalUpgradeInstalled()).setUpdateTime(5));
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
		return Loader.isModLoaded("SolarFlux");
	}
}
