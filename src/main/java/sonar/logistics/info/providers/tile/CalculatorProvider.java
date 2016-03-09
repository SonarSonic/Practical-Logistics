package sonar.logistics.info.providers.tile;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.calculator.mod.api.CalculatorAPI;
import sonar.calculator.mod.api.flux.IFlux;
import sonar.calculator.mod.api.flux.IFluxPoint;
import sonar.calculator.mod.api.machines.ITeleport;
import sonar.calculator.mod.utils.FluxRegistry;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.providers.TileProvider;
import cpw.mods.fml.common.Loader;

public class CalculatorProvider extends TileProvider {

	public static String name = "Calculator-Provider";
	public String[] categories = new String[] { "Calculator" };
	public String[] subcategories = new String[] { "Flux Network ID", "Flux Network Name", "Flux Network Owner", "Max Transfer", "Priority", "Teleporter ID", "Teleporter Name" };

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity target = world.getTileEntity(x, y, z);
		return target != null && (target instanceof IFlux || target instanceof ITeleport);
	}

	@Override
	public void getTileInfo(List<ILogicInfo> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		int id = this.getID();
		TileEntity target = world.getTileEntity(x, y, z);
		if (target != null) {
			if (target instanceof IFlux) {
				IFlux flux = (IFlux) target;
				infoList.add(new LogicInfo(id, 0, 1, flux.networkID()));
				infoList.add(new LogicInfo(id, 0, 2, FluxRegistry.getNetwork(flux.networkID())));
				infoList.add(new LogicInfo(id, 0, 3, flux.masterName()));
				if (target instanceof IFluxPoint) {
					IFluxPoint plug = (IFluxPoint) target;
					infoList.add(new LogicInfo(id, 0, 4, plug.maxTransfer()));
					infoList.add(new LogicInfo(id, 0, 5, plug.priority()));
				}
			}
			if (target instanceof ITeleport) {
				ITeleport teleporter = (ITeleport) target;
				infoList.add(new LogicInfo(id, 0, 6, teleporter.teleporterID()));
				infoList.add(new LogicInfo(id, 0, 7, teleporter.name()));
			}
		}

	}

	public boolean isLoadable() {
		return Loader.isModLoaded("Calculator") && CalculatorAPI.VERSION == "1.7.10 - 1.1";
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
