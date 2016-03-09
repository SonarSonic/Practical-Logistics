package sonar.logistics.api.providers;

import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.IRegistryObject;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.info.ILogicInfo;

/** used for providing information on Block/TileEntity for the Info Reader to read, the Provider must be registered in the {@link LogisticsAPI} to be used */
public abstract class TileProvider extends LogicProvider implements ICategoryProvider {

	public int getID() {
		return LogisticsAPI.getRegistry().getTileProviderID(getName());
	}

	/**used for checking if this provider can provide info at a given position
	 * @param world The World
	 * @param x X Coordinate
	 * @param y Y Coordinate
	 * @param z Z Coordinate
	 * @param dir the {@link ForgeDirection} to check from
	 * @return can this provider give info for the block/tile in the world at x,y,z */
	public abstract boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir);

	/** only called if canProvideInfo is true
	 * @param infoList current list of information for the block from this Helper, providers only add to this and don't remove.
	 * @param world The World
	 * @param x X Coordinate
	 * @param y Y Coordinate
	 * @param z Z Coordinate
	 * @param dir the {@link ForgeDirection} to check from */
	public abstract void getTileInfo(List<ILogicInfo> infoList, World world, int x, int y, int z, ForgeDirection dir);

}
