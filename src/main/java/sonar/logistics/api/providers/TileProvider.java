package sonar.logistics.api.providers;

import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.IRegistryObject;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.info.Info;

/** used for providing information on Block/TileEntity for the Info Reader to read, the Provider must be registered in the {@link LogisticsAPI} to be used */
public abstract class TileProvider implements IRegistryObject {

	public int getID() {
		return LogisticsAPI.getRegistry().getTileProviderID(getName());
	}

	/** the name the info helper will be registered too */
	public abstract String getName();

	/** @param world The World
	 * @param x X Coordinate
	 * @param y Y Coordinate
	 * @param z Z Coordinate
	 * @param dir The direction of the Node to the Block
	 * @return can this provider give info for the block/tile in the world at x,y,z */
	public abstract boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir);

	/** only called if canProvideInfo is true
	 * 
	 * @param infoList current list of information for the block from this Helper, providers only add to this and don't remove.
	 * @param world The World
	 * @param x X Coordinate
	 * @param y Y Coordinate
	 * @param z Z Coordinate */
	public abstract void getHelperInfo(List<Info> infoList, World world, int x, int y, int z, ForgeDirection dir);

	public abstract String getCategory(int id);

	public abstract String getSubCategory(int id);

	/** used when the provider is loaded normally used to check if relevant mods are loaded for APIs to work */
	public boolean isLoadable() {
		return true;
	}

}
