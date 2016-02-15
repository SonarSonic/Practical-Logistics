package sonar.logistics.info.providers.tile;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.providers.TileProvider;

public class GrowableProvider extends TileProvider {

	public static String name = "Growable-Helper";
	public String[] categories = new String[] { "CROPS" };
	public String[] subcategories = new String[] { "Is fully grown" };

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir) {
		Block target = world.getBlock(x, y, z);
		return target instanceof BlockCrops;
	}

	@Override
	public void getHelperInfo(List<Info> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		int id = this.getID();
		Block target = world.getBlock(x, y, z);
		if (target instanceof BlockCrops) {
			BlockCrops crop = (BlockCrops) target;
			infoList.add(new StandardInfo(id, 0, 0, "" + crop.func_149851_a(world, x, y, z, false)));
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
}
