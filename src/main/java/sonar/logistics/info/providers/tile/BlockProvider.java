package sonar.logistics.info.providers.tile;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidBase;
import sonar.core.utils.helpers.SonarHelper;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.providers.TileProvider;
import sonar.logistics.info.types.BlockNameInfo;
import sonar.logistics.info.types.ModidInfo;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

public class BlockProvider extends TileProvider {

	public static String name = "Block-Helper";
	public String[] categories = new String[] { "GENERAL", "WORLD", "PROPERTIES", "REDSTONE", "FLUID" };
	public String[] subcategories = new String[] { "Metadata", "X Coord", "Y Coord", "Z Coord", "Direction", "Is Raining", "Is Thundering", "Save Name", "Dimension", "Dimension Name", "Light", "Is Side Solid", "Hardness", "Signal", "Weak Power", "Strong Power", "Temperature", "Density", "Viscosity", "Block Name", "Mod" };

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
	public void getTileInfo(List<ILogicInfo> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		int id = this.getID();
		Block target = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		ItemStack block = SonarHelper.createStackedBlock(target, meta);
		if (block != null)
			infoList.add(new BlockNameInfo(id, 0, 19, meta, block));
		UniqueIdentifier unique = GameRegistry.findUniqueIdentifierFor(target);
		if (unique != null)
			infoList.add(new ModidInfo(id, 0, 20, meta, unique));
		infoList.add(new LogicInfo(id, 0, 0, meta));
		infoList.add(new LogicInfo(id, 0, 1, x));
		infoList.add(new LogicInfo(id, 0, 2, y));
		infoList.add(new LogicInfo(id, 0, 3, z));
		infoList.add(new LogicInfo(id, 0, 4, "" + dir));
		infoList.add(new LogicInfo(id, 1, 5, "" + world.getWorldInfo().isRaining()));
		infoList.add(new LogicInfo(id, 1, 6, "" + world.getWorldInfo().isThundering()));
		infoList.add(new LogicInfo(id, 1, 7, "" + world.getWorldInfo().getWorldName()));
		infoList.add(new LogicInfo(id, 1, 8, world.provider.dimensionId));
		infoList.add(new LogicInfo(id, 1, 9, "" + world.provider.getDimensionName()));
		infoList.add(new LogicInfo(id, 2, 10, world.getBlockLightValue(x, y, z)));
		infoList.add(new LogicInfo(id, 2, 11, "" + target.isSideSolid(world, x, y, z, dir)));
		infoList.add(new LogicInfo(id, 2, 12, "" + target.getBlockHardness(world, x, y, z)));
		infoList.add(new LogicInfo(id, 3, 13, "" + world.isBlockIndirectlyGettingPowered(x, y, z)));
		if (target.canProvidePower()) {
			infoList.add(new LogicInfo(id, 3, 14, target.isProvidingWeakPower(world, x, y, z, dir.ordinal())));
			infoList.add(new LogicInfo(id, 3, 15, target.isProvidingStrongPower(world, x, y, z, dir.ordinal())));
		}
		if (target instanceof BlockFluidBase) {
			BlockFluidBase fluid = (BlockFluidBase) target;
			infoList.add(new LogicInfo(id, 4, 16, fluid.getFluid().getTemperature()));
			infoList.add(new LogicInfo(id, 4, 17, fluid.getFluid().getDensity()));
			infoList.add(new LogicInfo(id, 4, 18, fluid.getFluid().getViscosity()));
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
