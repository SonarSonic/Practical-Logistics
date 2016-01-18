package sonar.logistics.api.providers;

import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.IRegistryObject;
import sonar.logistics.Logistics;

/** used for providing information on Inventories for the Inventory Reader to read, the Provider must be registered in the PractialLogisticsAPI to be used */
public abstract class InventoryProvider implements IRegistryObject {

	public byte getID(){
		return Logistics.inventoryProviders.getObjectID(getName());		
	}
	
	/** the name the info helper will be registered too */
	public abstract String getName();

	/**
	 * @param world The World
	 * @param x X Coordinate
	 * @param y Y Coordinate
	 * @param z Z Coordinate
	 * @param dir The direction of the Node to the Block
	 * @return can this provider give info for the block/tile in the world at x,y,z
	 */
	public abstract boolean canProvideItems(World world, int x, int y, int z, ForgeDirection dir);

	
	/**
	 * only called if canProvideInfo is true
	 * 
	 * @param storedStacks current list of items for the block from this Helper, providers only add to this and don't remove.
	 * @param world The World
	 * @param x X Coordinate
	 * @param y Y Coordinate
	 * @param z Z Coordinate
	 * @return TODO
	 */
	public abstract boolean getItems(List<StoredItemStack> storedStacks, World world, int x, int y, int z, ForgeDirection dir);

	/** used when the provider is loaded normally used to check if relevant mods are loaded for APIs to work */
	public boolean isLoadable() {
		return true;
	}
}
