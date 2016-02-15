package sonar.logistics.info.providers.tile;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.providers.TileProvider;
import WayofTime.alchemicalWizardry.api.alchemy.energy.IReagentHandler;
import WayofTime.alchemicalWizardry.api.alchemy.energy.ISegmentedReagentHandler;
import WayofTime.alchemicalWizardry.api.alchemy.energy.ReagentContainerInfo;
import WayofTime.alchemicalWizardry.api.rituals.IMasterRitualStone;
import WayofTime.alchemicalWizardry.api.tile.IBloodAltar;
import cpw.mods.fml.common.Loader;

public class BloodMagicProvider extends TileProvider {

	public static String name = "BloodMagic-Provider";
	public String[] categories = new String[] {"Blood Altar","Reagent Handler","Ritual Stone"};
	public String[] subcategories = new String[] {"Tier","Blood Stored","Capacity","Buffer Capacity","Progress","Has Demon Blood","Connected Tanks","Owner","Cool Down","Are Tanks Empty"};

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity te = world.getTileEntity(x, y, z);
		return te != null && (te instanceof IBloodAltar || te instanceof IReagentHandler);
	}

	@Override
	public void getHelperInfo(List<Info> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		int id = this.getID();
		TileEntity te = world.getTileEntity(x, y, z);
		if (te == null) {
			return;
		}
		if (te instanceof IBloodAltar) {
			IBloodAltar altar = (IBloodAltar) te;
			infoList.add(new StandardInfo(id, 0, 0, altar.getTier()));
			infoList.add(new StandardInfo(id, 0, 1, altar.getCurrentBlood()).addSuffix("LP"));
			infoList.add(new StandardInfo(id, 0, 2, altar.getCapacity()).addSuffix("LP"));
			infoList.add(new StandardInfo(id, 0, 3, altar.getBufferCapacity()).addSuffix("LP"));
			infoList.add(new StandardInfo(id, 0, 4, altar.getProgress()));
			infoList.add(new StandardInfo(id, 0, 5, altar.hasDemonBlood()));
		}
		if (te instanceof IReagentHandler) {
			IReagentHandler handler = (IReagentHandler) te;
			ReagentContainerInfo[] containerList = handler.getContainerInfo(dir);
			int tankID = 0;
			for (ReagentContainerInfo info : containerList) {
				if (info != null) {
					tankID++;
					String prefix = "";
					if (containerList.length > 1) {
						prefix = "Tank " + tankID + ": ";
					}
					String category = getCategory((byte)1);
					infoList.add(new StandardInfo(id, category, prefix + "Capacity", info.capacity));
					if (info.reagent != null) {
						infoList.add(new StandardInfo(id, category, prefix + "Stored", info.reagent.amount));
						infoList.add(new StandardInfo(id, category, prefix + "Reagent", info.reagent.reagent.name));
					} else {
						infoList.add(new StandardInfo(id, category, prefix + "Stored", 0));
						infoList.add(new StandardInfo(id, category, prefix + "Reagent", "NONE"));
					}
				}
			}
		}

		if (te instanceof ISegmentedReagentHandler) {
			ISegmentedReagentHandler handler = (ISegmentedReagentHandler) te;
			infoList.add(new StandardInfo(id, 1, 6, handler.getNumberOfTanks()));
		}
		if (te instanceof IMasterRitualStone) {
			IMasterRitualStone stone = (IMasterRitualStone) te;
			infoList.add(new StandardInfo(id, 2, 7, stone.getOwner()));
			infoList.add(new StandardInfo(id, 2, 8, stone.getCooldown()));
			infoList.add(new StandardInfo(id, 2, 9, stone.areTanksEmpty()));
		}
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("AWWayofTime");
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
