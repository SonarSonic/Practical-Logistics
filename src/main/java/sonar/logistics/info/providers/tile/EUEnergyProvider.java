package sonar.logistics.info.providers.tile;

import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.providers.TileProvider;

public class EUEnergyProvider extends TileProvider {

	public static String name = "EU-Provider";
	public String[] categories = new String[] { "ENERGY EU" };
	public String[] subcategories = new String[] { "Sink Tier", "Demanded Energy", "Source Tier", "Offered Energy" };

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity target = world.getTileEntity(x, y, z);
		return target != null && target instanceof IEnergyTile;
	}

	@Override
	public void getHelperInfo(List<Info> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		byte id = this.getID();
		TileEntity target = world.getTileEntity(x, y, z);
		if (target instanceof IEnergySink) {
			IEnergySink energy = (IEnergySink) target;
			infoList.add(new StandardInfo(id, 0, 0, energy.getSinkTier()));
			infoList.add(new StandardInfo(id, 0, 1, (int) energy.getDemandedEnergy(), "EU"));
		}
		if (target instanceof IEnergySource) {
			IEnergySource energy = (IEnergySource) target;
			infoList.add(new StandardInfo(id, 0, 2, (int) energy.getSourceTier()));
			infoList.add(new StandardInfo(id, 0, 3, (int) energy.getOfferedEnergy(), "EU"));
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
}
