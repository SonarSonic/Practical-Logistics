package sonar.logistics.info.providers.tile;

import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorChamber;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.providers.TileProvider;
import cpw.mods.fml.common.Loader;

public class IC2ReactorProvider extends TileProvider {

	public static String name = "IC2-Reactor";
	public String[] categories = new String[] { "IC2 Reactor" };
	public String[] subcategories = new String[] { "Is Active", "Heat", "Max Heat", "Energy Output", "Fluid Cooled" };

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity target = world.getTileEntity(x, y, z);
		return target != null && (target instanceof IReactor || target instanceof IReactorChamber);
	}

	@Override
	public void getTileInfo(List<ILogicInfo> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		int id = this.getID();
		TileEntity target = world.getTileEntity(x, y, z);
		IReactor reactor = null;
		if (target instanceof IReactorChamber) {
			IReactorChamber chamber = (IReactorChamber) target;
			reactor = chamber.getReactor();
		}
		if (target instanceof IReactor) {
			reactor = (IReactor) target;
		}
		if (reactor != null) {
			infoList.add(new LogicInfo(id, 0, 0, reactor.produceEnergy()));
			infoList.add(new LogicInfo(id, 0, 1, reactor.getHeat()));
			infoList.add(new LogicInfo(id, 0, 2, reactor.getMaxHeat()));
			infoList.add(new LogicInfo(id, 0, 3, (int) reactor.getReactorEUEnergyOutput()));
			infoList.add(new LogicInfo(id, 0, 4, reactor.isFluidCooled()));
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
		return Loader.isModLoaded("IC2");
	}
}
