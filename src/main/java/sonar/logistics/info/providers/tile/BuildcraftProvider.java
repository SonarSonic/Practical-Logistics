package sonar.logistics.info.providers.tile;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.providers.TileProvider;
import buildcraft.api.power.ILaserTarget;
import buildcraft.api.tiles.IHasWork;
import buildcraft.api.tiles.IHeatable;
import cpw.mods.fml.common.Loader;

public class BuildcraftProvider extends TileProvider {

	public static String name = "Buildcraft-Provider";
	public static String[] categories = new String[] { "Buildcraft: General", "Buildcraft: Lasers" };
	public static String[] subcategories = new String[] { "Active", "Current Heat", "Min Heat", "Max Heat", "Ideal Heat", "Requires Energy", "Valid Target" };

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity te = world.getTileEntity(x, y, z);
		return te != null && (te instanceof IHasWork || te instanceof IHeatable || te instanceof ILaserTarget);
	}

	@Override
	public void getHelperInfo(List<Info> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		int id = this.getID();
		TileEntity te = world.getTileEntity(x, y, z);
		if (te == null) {
			return;
		}
		if (te instanceof IHasWork) {
			IHasWork worker = (IHasWork) te;
			infoList.add(new StandardInfo(id, 0, 0, worker.hasWork()));
		}
		if (te instanceof IHeatable) {
			IHeatable heat = (IHeatable) te;
			infoList.add(new StandardInfo(id, 0, 1, (int)heat.getCurrentHeatValue()).addSuffix("degrees"));
			infoList.add(new StandardInfo(id, 0, 2, (int)heat.getMinHeatValue()).addSuffix("degrees"));
			infoList.add(new StandardInfo(id, 0, 3, (int)heat.getMaxHeatValue()).addSuffix("degrees"));
			infoList.add(new StandardInfo(id, 0, 4, (int)heat.getIdealHeatValue()).addSuffix("degrees"));
		}
		if (te instanceof ILaserTarget) {
			ILaserTarget laser = (ILaserTarget) te;
			infoList.add(new StandardInfo(id, 1, 5, laser.requiresLaserEnergy()));
			infoList.add(new StandardInfo(id, 1, 6, laser.isInvalidTarget()));
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
		return Loader.isModLoaded("BuildCraft|Core");
	}
}
