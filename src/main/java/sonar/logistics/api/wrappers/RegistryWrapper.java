package sonar.logistics.api.wrappers;

import sonar.logistics.api.Info;
import sonar.logistics.api.providers.EntityProvider;
import sonar.logistics.api.providers.FluidHandler;
import sonar.logistics.api.providers.InventoryHandler;
import sonar.logistics.api.providers.TileProvider;

public class RegistryWrapper {
	public void registerInfoType(Info provider){}

	public void registerTileProvider(TileProvider provider){}

	public void registerEntityProvider(EntityProvider provider){}

	public void registerInventoryProvider(InventoryHandler provider){}

	public void registerFluidProvider(FluidHandler provider){}

	/**two objects, input and output, they can be any of the following ItemStacks, Items, Block, Ore Stacks, or OreDictionary strings*/
	public void registerForgingHammerRecipe(Object... objects){}
}
