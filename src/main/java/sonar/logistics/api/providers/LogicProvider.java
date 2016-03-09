package sonar.logistics.api.providers;

import sonar.core.utils.IRegistryObject;

public abstract class LogicProvider implements IRegistryObject {

	/** used when the provider is loaded normally used to check if relevant mods are loaded for APIs to work */
	public boolean isLoadable() {
		return true;
	}
}
