package sonar.logistics.info.providers.tile;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.info.Info;
import sonar.logistics.api.info.StandardInfo;
import sonar.logistics.api.providers.TileProvider;
import appeng.api.networking.crafting.ICraftingCPU;
import cpw.mods.fml.common.Loader;

public class AE2CraftingProvider extends TileProvider {

	public static String name = "AE2-Crafting-Provider";
	public String[] categories = new String[] { "AE2 Crafting"};
	public String[] subcategories = new String[] { "Available Storage", "Connect Co Processors", "Is Crafting"};

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity target = world.getTileEntity(x, y, z);
		return target!=null && target instanceof ICraftingCPU;
	}

	@Override
	public void getHelperInfo(List<Info> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		int id = this.getID();
		TileEntity target = world.getTileEntity(x, y, z);
		if (target instanceof ICraftingCPU) {
			ICraftingCPU cpu = (ICraftingCPU) target;
			infoList.add(new StandardInfo(id, 0, 0, cpu.getAvailableStorage()).addSuffix("bytes"));
			infoList.add(new StandardInfo(id, 0, 1, cpu.getCoProcessors()));
			infoList.add(new StandardInfo(id, 0, 2, cpu.isBusy()));
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
