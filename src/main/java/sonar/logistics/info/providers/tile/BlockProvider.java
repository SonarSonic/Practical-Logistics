package sonar.logistics.info.providers.tile;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidBase;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.providers.TileProvider;

public class BlockProvider extends TileProvider {

	public static String name = "Block-Helper";
	public String[] categories = new String[] { "GENERAL", "WORLD", "PROPERTIES", "REDSTONE", "FLUID" };
	public String[] subcategories = new String[] { "Metadata", "X Coord", "Y Coord", "Z Coord", "Direction", "Is Raining", "Is Thundering", "Save Name", "Dimension", "Dimension Name", "Light", "Is Side Solid", "Hardness", "Signal", "Weak Power", "Strong Power", "Temperature", "Density", "Viscosity" };

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir) {
		Block target = world.getBlock(x, y, z);
		return target != null;
	}

	@Override
	public void getHelperInfo(List<Info> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		int id = this.getID();
		Block target = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		infoList.add(new StandardInfo(id, 0, 0, meta));
		infoList.add(new StandardInfo(id, 0, 1, x));
		infoList.add(new StandardInfo(id, 0, 2, y));
		infoList.add(new StandardInfo(id, 0, 3, z));
		infoList.add(new StandardInfo(id, 0, 4, "" + dir));
		infoList.add(new StandardInfo(id, 1, 5, "" + world.getWorldInfo().isRaining()));
		infoList.add(new StandardInfo(id, 1, 6, "" + world.getWorldInfo().isThundering()));
		infoList.add(new StandardInfo(id, 1, 7, "" + world.getWorldInfo().getWorldName()));
		infoList.add(new StandardInfo(id, 1, 8, world.provider.dimensionId));
		infoList.add(new StandardInfo(id, 1, 9, "" + world.provider.getDimensionName()));
		infoList.add(new StandardInfo(id, 2, 10, world.getBlockLightValue(x, y, z)));
		infoList.add(new StandardInfo(id, 2, 11, "" + target.isSideSolid(world, x, y, z, dir)));
		infoList.add(new StandardInfo(id, 2, 12, "" + target.getBlockHardness(world, x, y, z)));
		infoList.add(new StandardInfo(id, 3, 13, "" + world.isBlockIndirectlyGettingPowered(x, y, z)));
		if (target.canProvidePower()) {
			infoList.add(new StandardInfo(id, 3, 14, target.isProvidingWeakPower(world, x, y, z, dir.flag)));
			infoList.add(new StandardInfo(id, 3, 15, target.isProvidingStrongPower(world, x, y, z, dir.flag)));
		}
		if (target instanceof BlockFluidBase) {
			BlockFluidBase fluid = (BlockFluidBase) target;
			infoList.add(new StandardInfo(id, 4, 16, fluid.getFluid().getTemperature()));
			infoList.add(new StandardInfo(id, 4, 17, fluid.getFluid().getDensity()));
			infoList.add(new StandardInfo(id, 4, 18, fluid.getFluid().getViscosity()));
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
