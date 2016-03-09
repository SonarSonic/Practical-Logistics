package sonar.logistics.info.providers.tile;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.providers.TileProvider;

public class VanillaTileEntityProvider extends TileProvider {

	public static String name = "Vanilla-Tile Helper";
	public String[] categories = new String[] { "SPECIAL"};
	public String[] subcategories = new String[] {"Burn Time","Current Time","Cook Time","Current Fuel","Current Note"};

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity target = world.getTileEntity(x, y, z);
		if (target != null) {
			if (target instanceof TileEntityFurnace) {
				return true;
			}
			if (target instanceof TileEntityNote) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void getTileInfo(List<ILogicInfo> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		int id = this.getID();
		TileEntity target = world.getTileEntity(x, y, z);
		if (target != null) {
			if (target instanceof TileEntityFurnace) {
				TileEntityFurnace furnace = (TileEntityFurnace) target;
				infoList.add(new LogicInfo(id, 0, 0, furnace.furnaceBurnTime));
				infoList.add(new LogicInfo(id, 0, 1, furnace.furnaceCookTime).addSuffix("ticks"));
				infoList.add(new LogicInfo(id, 0, 2, 200).addSuffix("ticks"));
				infoList.add(new LogicInfo(id, 0, 3, furnace.currentItemBurnTime));
			}
			if (target instanceof TileEntityNote) {
				TileEntityNote noteBlock = (TileEntityNote) target;
				infoList.add(new LogicInfo(id, 0, 4, noteBlock.note));
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
}
