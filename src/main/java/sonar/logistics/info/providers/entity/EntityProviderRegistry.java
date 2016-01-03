package sonar.logistics.info.providers.entity;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sonar.logistics.Logistics;
import sonar.logistics.api.data.EntityProvider;

public class EntityProviderRegistry {

	private static List<EntityProvider> providers = new ArrayList();
	private static Map<String, Byte> providerIDs = new THashMap<String, Byte>();
	private static Map<Byte, String> providerNames = new THashMap<Byte, String>();

	public static void registerProviders() {
		addProvider(new PlayerProvider());
		addProvider(new NormalEntityProvider());
	}

	public static void removeAll() {
		providers.clear();
	}

	public static List<EntityProvider> getProviders() {
		return providers;
	}

	public static EntityProvider getProvider(byte providerID) {
		String helperName=providerNames.get(providerID);
		if (helperName == null || helperName.isEmpty()) {
			return null;
		}
		for (EntityProvider provider : providers) {
			if (provider.helperName().equals(helperName)) {
				return provider;
			}
		}
		return null;
	}
	
	public static EntityProvider getProvider(String helperName) {
		if (helperName == null || helperName.isEmpty()) {
			return null;
		}
		for (EntityProvider provider : providers) {
			if (provider.helperName().equals(helperName)) {
				return provider;
			}
		}
		return null;
	}
	public static void addProvider(EntityProvider provider) {
		Logistics.logger.warn("Loading Entity Provider: " + provider.helperName());
		if(!provider.isLoadable()){
			return;
		}
		if (provider != null) {
			if (getProvider(provider.helperName()) == null) {
				providers.add(provider);
				byte id = (byte) providerIDs.size();
				providerIDs.put(provider.helperName(), id);
				providerNames.put(id,provider.helperName());
				Logistics.logger.info("Loaded Entity Provider: " + provider.helperName());
			} else {
				Logistics.logger.warn("DUPLICATE PROVIDER ID - skipping " + provider.helperName());
			}
		}
	}
	public static byte getProviderID(String helperName){
		byte id = providerIDs.get(helperName);
		return id;		
	}
}