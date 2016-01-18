package sonar.logistics.info.providers.tile;

import java.util.List;

import logisticspipes.api.IProgressProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.Info;
import sonar.logistics.api.providers.TileProvider;
import sonar.logistics.info.types.ProgressInfo;
import cpw.mods.fml.common.Loader;

public class LogisticsPipesProvider extends TileProvider {

	public static String name = "Logistics-Pipes-Provider";
	public static String[] categories = new String[] {};
	public static String[] subcategories = new String[] {};

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity te = world.getTileEntity(x, y, z);
		return te != null && (te instanceof IProgressProvider);
	}

	@Override
	public void getHelperInfo(List<Info> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		byte id = this.getID();
		TileEntity te = world.getTileEntity(x, y, z);
		if (te == null) {
			return;
		}
		if (te instanceof IProgressProvider) {
			IProgressProvider pool = (IProgressProvider) te;
			infoList.add(new ProgressInfo(pool.getMachineProgressForLP(), 100, "Progress"));
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
		return Loader.isModLoaded("LogisticsPipes");
	}
}
