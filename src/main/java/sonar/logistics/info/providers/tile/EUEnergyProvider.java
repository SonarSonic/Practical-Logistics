package sonar.logistics.info.providers.tile;

import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.tile.IEnergyStorage;

import java.util.List;

import mekanism.api.energy.IStrictEnergyStorage;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.providers.TileProvider;
import cpw.mods.fml.common.Loader;

public class EUEnergyProvider extends TileProvider {

	public static String name = "EU-Provider";
	public String[] categories = new String[] { "ENERGY EU" };
	public String[] subcategories = new String[] { "Sink Tier", "Demanded Energy", "Source Tier", "Offered Energy", "Stored", "Capacity", "Output" };

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity target = world.getTileEntity(x, y, z);
		return target != null && (target instanceof IEnergyTile || target instanceof IEnergyStorage)  && (!Loader.isModLoaded("Mekanism") || !(target instanceof IStrictEnergyStorage));
	}

	@Override
	public void getTileInfo(List<ILogicInfo> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		int id = this.getID();
		TileEntity target = world.getTileEntity(x, y, z);
		if (target instanceof IEnergyStorage) {
			IEnergyStorage energy = (IEnergyStorage) target;
			infoList.add(new LogicInfo(id, 0, 4, (long) energy.getStored()).addSuffix("EU"));
			infoList.add(new LogicInfo(id, 0, 5, (long) energy.getCapacity()).addSuffix("EU"));
			infoList.add(new LogicInfo(id, 0, 6, (long) energy.getOutput()).addSuffix("EU/t"));;
		}
		if (target instanceof IEnergySink) {
			IEnergySink energy = (IEnergySink) target;
			infoList.add(new LogicInfo(id, 0, 0, (long) energy.getSinkTier()));
			infoList.add(new LogicInfo(id, 0, 1, (long) energy.getDemandedEnergy()).addSuffix("EU"));
		}
		if (target instanceof IEnergySource) {
			IEnergySource energy = (IEnergySource) target;
			infoList.add(new LogicInfo(id, 0, 2, (long) energy.getSourceTier()));
			infoList.add(new LogicInfo(id, 0, 3, (long) energy.getOfferedEnergy()).addSuffix("EU"));
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
