package sonar.logistics.info.providers;

import cpw.mods.fml.common.Loader;
import sonar.core.utils.helpers.RegistryHelper;
import sonar.logistics.api.providers.InventoryProvider;
import sonar.logistics.info.providers.inventory.AE2InventoryProvider;
import sonar.logistics.info.providers.inventory.DSUInventoryProvider;
import sonar.logistics.info.providers.inventory.DrawersInventoryProvider;
import sonar.logistics.info.providers.inventory.LPInventoryProvider;
import sonar.logistics.info.providers.inventory.StorageChamberInventoryProvider;

public class InventoryProviderRegistry extends RegistryHelper<InventoryProvider> {

	@Override
	public void register() {
		if (Loader.isModLoaded("appliedenergistics2"))
			registerObject(new AE2InventoryProvider());
		registerObject(new DSUInventoryProvider());
		registerObject(new LPInventoryProvider());
		registerObject(new DrawersInventoryProvider());
		registerObject(new StorageChamberInventoryProvider());
	}

	@Override
	public String registeryType() {
		return "Inventory Provider";
	}
}