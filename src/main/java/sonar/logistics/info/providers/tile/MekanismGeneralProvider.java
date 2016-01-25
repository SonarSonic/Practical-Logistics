package sonar.logistics.info.providers.tile;

import java.util.List;

import mekanism.api.IHeatTransfer;
import mekanism.api.ISalinationSolar;
import mekanism.api.lasers.ILaserReceptor;
import mekanism.api.reactor.IFusionReactor;
import mekanism.api.reactor.IReactorBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.providers.TileProvider;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridConnection;
import cpw.mods.fml.common.Loader;

public class MekanismGeneralProvider extends TileProvider {

	public static String name = "Mekanism-General";
	public String[] categories = new String[] { "Mekanism General" };
	public String[] subcategories = new String[] { "Temperature", "Can See Sun", "Can Laser Dig" };

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity target = world.getTileEntity(x, y, z);
		return target != null && (target instanceof IHeatTransfer || target instanceof ISalinationSolar || target instanceof ILaserReceptor);
	}

	@Override
	public void getHelperInfo(List<Info> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		byte id = this.getID();
		TileEntity target = world.getTileEntity(x, y, z);
		if (target instanceof IHeatTransfer) {
			IHeatTransfer block = (IHeatTransfer) target;
			infoList.add(new StandardInfo(id, 0, 0, (int) block.getTemp(), "degrees"));
		}
		if (target instanceof ISalinationSolar) {
			ISalinationSolar block = (ISalinationSolar) target;
			infoList.add(new StandardInfo(id, 0, 1, block.seesSun()));
		}
		if (target instanceof ILaserReceptor) {
			ILaserReceptor block = (ILaserReceptor) target;
			infoList.add(new StandardInfo(id, 0, 2, block.canLasersDig()));
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

	public boolean isLoadable() {
		return Loader.isModLoaded("Mekanism");
	}

}
