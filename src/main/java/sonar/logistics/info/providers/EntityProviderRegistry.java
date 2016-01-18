package sonar.logistics.info.providers;

import sonar.core.utils.helpers.RegistryHelper;
import sonar.logistics.api.providers.EntityProvider;
import sonar.logistics.info.providers.entity.NormalEntityProvider;
import sonar.logistics.info.providers.entity.PlayerProvider;

public class EntityProviderRegistry extends RegistryHelper<EntityProvider> {

	@Override
	public void register() {
		registerObject(new PlayerProvider());
		registerObject(new NormalEntityProvider());		
	}

	@Override
	public String registeryType() {
		return "Entity Provider";
	}

}