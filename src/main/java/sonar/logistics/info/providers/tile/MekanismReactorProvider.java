package sonar.logistics.info.providers.tile;

import java.util.List;

import mekanism.api.reactor.IFusionReactor;
import mekanism.api.reactor.IReactorBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.providers.TileProvider;
import cpw.mods.fml.common.Loader;

public class MekanismReactorProvider extends TileProvider {

	public static String name = "Mekanism-Reactor";
	public String[] categories = new String[] { "Mekanism Reactor" };
	public String[] subcategories = new String[] { "Case Temp", "Plasma Temp", "Injection Rate", "Is Complete", "Is Active" };

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity target = world.getTileEntity(x, y, z);
		return target != null && (target instanceof IReactorBlock);
	}

	@Override
	public void getTileInfo(List<ILogicInfo> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		int id = this.getID();
		TileEntity target = world.getTileEntity(x, y, z);
		if (target instanceof IReactorBlock) {
			IReactorBlock block = (IReactorBlock) target;
			if (block.getReactor() != null) {
				IFusionReactor reactor = block.getReactor();
				infoList.add(new LogicInfo(id, 0, 0, (int) reactor.getCaseTemp()).addSuffix("degrees"));
				infoList.add(new LogicInfo(id, 0, 1, (int) reactor.getPlasmaTemp()).addSuffix("degrees"));
				infoList.add(new LogicInfo(id, 0, 2, (int) reactor.getInjectionRate()));
				infoList.add(new LogicInfo(id, 0, 3, reactor.isFormed()));
				infoList.add(new LogicInfo(id, 0, 4, reactor.isBurning()));
			}
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
		return Loader.isModLoaded("Mekanism");
	}

}
