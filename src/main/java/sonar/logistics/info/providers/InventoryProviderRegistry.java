package sonar.logistics.info.providers;

import sonar.core.utils.helpers.RegistryHelper;
import sonar.logistics.api.providers.InventoryHandler;
import sonar.logistics.info.providers.inventory.AE2ExternalItemProvider;
import sonar.logistics.info.providers.inventory.AE2InventoryProvider;
import sonar.logistics.info.providers.inventory.DSUInventoryProvider;
import sonar.logistics.info.providers.inventory.DrawersInventoryProvider;
import sonar.logistics.info.providers.inventory.IInventoryProvider;
import sonar.logistics.info.providers.inventory.LPInventoryProvider;
import sonar.logistics.info.providers.inventory.StorageChamberInventoryProvider;
import cpw.mods.fml.common.Loader;

public class InventoryProviderRegistry extends RegistryHelper<InventoryHandler> {

	@Override
	public void register() {
		if (Loader.isModLoaded("appliedenergistics2")) {
			registerObject(new AE2ExternalItemProvider());
			registerObject(new AE2InventoryProvider());
		}
		registerObject(new DSUInventoryProvider());
		registerObject(new LPInventoryProvider());
		registerObject(new DrawersInventoryProvider());
		registerObject(new StorageChamberInventoryProvider());
		registerObject(new IInventoryProvider());
	}

	@Override
	public String registeryType() {
		return "Inventory Provider";
	}
}