package sonar.logistics.info.providers.tile;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.data.TileProvider;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridConnection;
import cpw.mods.fml.common.Loader;

public class AE2GridProvider extends TileProvider {

	public static String name = "AE2-Grid-Provider";
	public String[] categories = new String[] { "AE2 Energy", "AE2 Channels"};
	public String[] subcategories = new String[] { "Idle Power Usage", "Used Channels", "Is Active", "Is Powered" };

	@Override
	public String helperName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity target = world.getTileEntity(x, y, z);
		return target!=null && (target instanceof IGridBlock || target instanceof IGridConnection || target instanceof IPowerChannelState);
	}

	@Override
	public void getHelperInfo(List<Info> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		byte id = this.getID();
		TileEntity target = world.getTileEntity(x, y, z);
		if (target instanceof IGridBlock) {
			IGridBlock grid = (IGridBlock) target;
			infoList.add(new StandardInfo(id, 0, 0, (int)grid.getIdlePowerUsage(), "ae/t"));
		}
		if (target instanceof IGridConnection) {
			IGridConnection grid = (IGridConnection) target;
			infoList.add(new StandardInfo(id, 1, 1, grid.getUsedChannels()));
		}
		if(target instanceof IPowerChannelState){
			IPowerChannelState grid = (IPowerChannelState) target;
			infoList.add(new StandardInfo(id, 1, 2, grid.isActive()));
			infoList.add(new StandardInfo(id, 1, 3, grid.isPowered()));
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
		return Loader.isModLoaded("appliedenergistics2");
	}
}
