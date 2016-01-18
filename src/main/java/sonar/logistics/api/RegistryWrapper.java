package sonar.logistics.api;

import sonar.logistics.api.providers.EntityProvider;
import sonar.logistics.api.providers.FluidProvider;
import sonar.logistics.api.providers.InventoryProvider;
import sonar.logistics.api.providers.TileProvider;

public class RegistryWrapper {
	public void registerInfoType(Info provider){}

	public void registerTileProvider(TileProvider provider){}

	public void registerEntityProvider(EntityProvider provider){}

	public void registerInventoryProvider(InventoryProvider provider){}

	public void registerFluidProvider(FluidProvider provider){}

	/**two objects, input and output, they can be any of the following ItemStacks, Items, Block, Ore Stacks, or OreDictionary strings*/
	public void registerForgingHammerRecipe(Object... objects){}
}
