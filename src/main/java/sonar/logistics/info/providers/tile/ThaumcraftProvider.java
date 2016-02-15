package sonar.logistics.info.providers.tile;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.providers.TileProvider;
import sonar.logistics.info.types.ThaumcraftAspectInfo;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.nodes.INode;
import cpw.mods.fml.common.Loader;

public class ThaumcraftProvider extends TileProvider {

	public static String name = "Thaumcraft-Provider";
	public String[] categories = new String[] { "THAUMCRAFT ASPECTS","THAUMCRAFT NODE" };
	public String[] subcategories = new String[] {"Has Primal Aspect","Modifier","Type"};

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity target = world.getTileEntity(x, y, z);
		if (target != null) {
			if (target instanceof IAspectContainer) {
				return true;
			}
			if (target instanceof INode) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void getHelperInfo(List<Info> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		int id = this.getID();
		TileEntity target = world.getTileEntity(x, y, z);
		if (target != null) {
			if (target instanceof IAspectContainer) {
				IAspectContainer container = (IAspectContainer) target;
				Aspect[] list = container.getAspects().getAspects();
				if (target instanceof INode) {
					INode node = (INode) target;
					list = node.getAspectsBase().getAspects();
				}
				if (list != null) {
					boolean hasPrimal = false;
					for (int i = 0; i < list.length; i++) {
						Aspect aspect = list[i];
						if (aspect != null) {
							if (aspect.isPrimal()) {
								hasPrimal = true;
							}
							int contains = container.containerContains(aspect);
							if (target instanceof INode) {
								INode node = (INode) target;
								contains = node.getNodeVisBase(aspect);
							}
							infoList.add(new ThaumcraftAspectInfo(id, "THAUMCRAFT ASPECTS", aspect.getName(), contains, aspect.getTag()));
						}
					}
					infoList.add(new StandardInfo(id, 0, 0, hasPrimal));
				}
			}
			if (target instanceof INode) {
				INode node = (INode) target;
				if (node.getNodeModifier() != null) {
					infoList.add(new StandardInfo(id, 1, 1, node.getNodeModifier().name()));
				}
				if (node.getNodeType() != null) {
					infoList.add(new StandardInfo(id, 1, 2, node.getNodeType().name()));
				}
			}
		}

	}

	public boolean isLoadable() {
		return Loader.isModLoaded("Thaumcraft");
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
