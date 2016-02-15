package sonar.logistics.api.wrappers;

import sonar.logistics.api.Info;
import sonar.logistics.api.providers.EntityProvider;
import sonar.logistics.api.providers.FluidHandler;
import sonar.logistics.api.providers.InventoryHandler;
import sonar.logistics.api.providers.TileProvider;
import sonar.core.utils.helpers.RecipeHelper.OreStack;

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

	/**, */
	/**used for registering a new Forging Hammer Recipe
	 * @param recipe two objects, input and output they can be any of the following ItemStacks, Items, Block, {@link OreStack}, or OreDictionary strings
	 */
	public void registerForgingHammerRecipe(Object... recipe){}
}
