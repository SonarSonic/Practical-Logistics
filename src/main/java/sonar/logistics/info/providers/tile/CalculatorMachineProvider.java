package sonar.logistics.info.providers.tile;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.calculator.mod.api.CalculatorAPI;
import sonar.calculator.mod.api.machines.IFlawlessGreenhouse;
import sonar.calculator.mod.api.machines.IGreenhouse;
import sonar.calculator.mod.api.machines.IProcessMachine;
import sonar.calculator.mod.api.nutrition.IHealthProcessor;
import sonar.calculator.mod.api.nutrition.IHungerProcessor;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.providers.TileProvider;
import cpw.mods.fml.common.Loader;

public class CalculatorMachineProvider extends TileProvider {

	public static String name = "Calculator-Machine-Provider";
	public String[] categories = new String[] { "Calculator", "Calculator Greenhouse", "Calculator Machine" };
	public String[] subcategories = new String[] { "Health Points", "Hunger Points", "Carbon Level", "Oxygen Level", "Max Gas Level", "Plants Grown", "Plants Harvested","Current Process Time", "Process Time", "Energy Usage" };

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity target = world.getTileEntity(x, y, z);
		return target != null && (target instanceof IHealthProcessor || target instanceof IHungerProcessor || target instanceof IGreenhouse || target instanceof IProcessMachine);
	}

	@Override
	public void getTileInfo(List<ILogicInfo> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		int id = this.getID();
		TileEntity target = world.getTileEntity(x, y, z);
		if (target != null) {
			if (target instanceof IHealthProcessor) {
				IHealthProcessor health = (IHealthProcessor) target;
				infoList.add(new LogicInfo(id, 0, 0, health.getHealthPoints()));
			}
			if (target instanceof IHungerProcessor) {
				IHungerProcessor hunger = (IHungerProcessor) target;
				infoList.add(new LogicInfo(id, 0, 1, hunger.getHungerPoints()));
			}
			if (target instanceof IGreenhouse) {
				IGreenhouse greenhouse = (IGreenhouse) target;
				infoList.add(new LogicInfo(id, 1, 2, greenhouse.getCarbon()));
				infoList.add(new LogicInfo(id, 1, 3, greenhouse.getOxygen()));
				infoList.add(new LogicInfo(id, 1, 4, greenhouse.maxGasLevel()));
				if (target instanceof IFlawlessGreenhouse) {
					IFlawlessGreenhouse flawless = (IFlawlessGreenhouse) target;
					infoList.add(new LogicInfo(id, 1, 5, flawless.getPlantsGrown()));
					infoList.add(new LogicInfo(id, 1, 6, flawless.getPlantsHarvested()));
				}
			}
			if (target instanceof IProcessMachine) {
				IProcessMachine greenhouse = (IProcessMachine) target;
				infoList.add(new LogicInfo(id, 2, 7, greenhouse.getCurrentProcessTime()).addSuffix("ticks"));
				infoList.add(new LogicInfo(id, 2, 8, greenhouse.getProcessTime()).addSuffix("ticks"));
				infoList.add(new LogicInfo(id, 2, 9, (int) greenhouse.getEnergyUsage()).addSuffix("rf/t"));
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
