package sonar.logistics.api.wrappers;

import sonar.logistics.api.Info;
import sonar.logistics.api.providers.EnergyHandler;
import sonar.logistics.api.providers.EntityProvider;
import sonar.logistics.api.providers.FluidHandler;
import sonar.logistics.api.providers.InventoryHandler;
import sonar.logistics.api.providers.TileProvider;

public class RegistryWrapper {
	
	/**used for registering Info Types, this should have a unique id!
	 * @param info {@link Info} to register
	 */
	public void registerInfoType(Info info){}

	/**used for registering Tile Entity providers, this should have a unique id!
	 * @param provider {@link TileProvider} to register
	 */
	public void registerTileProvider(TileProvider provider){}
	
	/**used for registering Entity providers, this should have a unique id!
	 * @param provider {@link EntityProvider} to register
	 */
	public void registerEntityProvider(EntityProvider provider){}
	
	/**used for registering Inventory handlers, this should have a unique id!
	 * @param handler {@link InventoryHandler} to register
	 */
	public void registerInventoryHandler(InventoryHandler handler){}
	
	/**used for registering Fluid handlers, this should have a unique id!
	 * @param handler {@link FluidHandler} to register
	 */
	public void registerFluidHandler(FluidHandler handler){}
	
	/**used for registering Fluid handlers, this should have a unique id!
	 * @param handler {@link EnergyHandler} to register
	 */
	public void registerEnergyHandler(EnergyHandler handler){}

	/**used for registering a new Forging Hammer Recipe
	 * @param recipe two objects, input and output they can be any of the following ItemStacks, Items, Block, OreStack, or OreDictionary strings
	 */
	public void registerForgingHammerRecipe(Object... recipe){}
	
	/**gets the {@link TileProvider} for the given id
	 * @param id the id to check
	 * @return the {@link TileProvider}, can be null
	 */
	public TileProvider getTileProvider(int id){
		return null;
	}
	
	/**gets the {@link EntityProvider} for the given id
	 * @param id the id to check
	 * @return the {@link EntityProvider}, can be null
	 */
	public EntityProvider getEntityProvider(int id){
		return null;
	}
	
	/**gets the {@link InventoryHandler} for the given id
	 * @param id the id to check
	 * @return the {@link InventoryHandler}, can be null
	 */
	public InventoryHandler getInventoryHandler(int id){
		return null;
	}
	
	/**gets the {@link FluidHandler} for the given id
	 * @param id the id to check
	 * @return the {@link FluidHandler}, can be null
	 */
	public FluidHandler getFluidHandler(int id){
		return null;
	}
	
	/**gets the {@link EnergyHandler} for the given id
	 * @param id the id to check
	 * @return the {@link EnergyHandler}, can be null
	 */
	public EnergyHandler getEnergyHandler(int id){
		return null;
	}
	
	/**gets the {@link TileProvider} id from its name
	 * @param name the name to check
	 * @return the id of the {@link TileProvider} 
	 */
	public int getTileProviderID(String name){
		return -1;
	}
	
	/**gets the {@link EntityProvider} id from its name
	 * @param name the name to check
	 * @return the id of the {@link EntityProvider} 
	 */
	public int getEntityProviderID(String name){
		return -1;
	}
	
	/**gets the {@link InventoryHandler} id from its name
	 * @param name the name to check
	 * @return the id of the {@link InventoryHandler} 
	 */
	public int getInventorHandlerID(String name){
		return -1;
	}
	
	/**gets the {@link FluidHandler} id from its name
	 * @param name the name to check
	 * @return the id of the {@link FluidHandler} 
	 */
	public int getFluidHandlerID(String name){
		return -1;
	}
	
	/**gets the {@link EnergyHandler} id from its name
	 * @param name the name to check
	 * @return the id of the {@link EnergyHandler} 
	 */
	public int getEnergyHandlerID(String name){
		return -1;
	}
	
	/**gets the {@link ItemFilter} id from its name
	 * @param name the name to check
	 * @return the id of the {@link ItemFilter} 
	 */
	public int getItemFilterID(String name){
		return -1;
	}
}
