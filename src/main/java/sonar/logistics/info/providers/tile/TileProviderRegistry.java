package sonar.logistics.info.providers.tile;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sonar.logistics.Logistics;
import sonar.logistics.api.data.TileProvider;

public class TileProviderRegistry {

	private static List<TileProvider> providers = new ArrayList();
	private static Map<String, Byte> providerIDs = new THashMap<String, Byte>();
	private static Map<Byte, String> providerNames = new THashMap<Byte, String>();

	public static void registerProviders() {
		addProvider(new BlockProvider());
		addProvider(new RFEnergyProvider());
		addProvider(new TankProvider());
		addProvider(new GrowableProvider());
		addProvider(new VanillaTileEntityProvider());
		addProvider(new CalculatorProvider());
		addProvider(new ThaumcraftProvider());
		addProvider(new BloodMagicProvider());
		addProvider(new ManaProvider());
		addProvider(new EUEnergyProvider());
		addProvider(new IC2ReactorProvider());
		addProvider(new HammerProvider());
	}

	public static void removeAll() {
		providers.clear();
	}

	public static List<TileProvider> getProviders() {
		return providers;
	}

	public static TileProvider getProvider(byte providerID) {
		String helperName=providerNames.get(providerID);
		if (helperName == null || helperName.isEmpty()) {
			return null;
		}
		for (TileProvider provider : providers) {
			if (provider.helperName().equals(helperName)) {
				return provider;
			}
		}
		return null;
	}
	
	public static TileProvider getProvider(String helperName) {
		if (helperName == null || helperName.isEmpty()) {
			return null;
		}
		for (TileProvider provider : providers) {
			if (provider.helperName().equals(helperName)) {
				return provider;
			}
		}
		return null;
	}
	public static void addProvider(TileProvider provider) {
		Logistics.logger.warn("Loading Info Provider: " + provider.helperName());
		if(!provider.isLoadable()){
			return;
		}
		if (provider != null) {
			if (getProvider(provider.helperName()) == null) {
				providers.add(provider);
				byte id = (byte) providerIDs.size();
				providerIDs.put(provider.helperName(), id);
				providerNames.put(id,provider.helperName());
				Logistics.logger.info("Loaded Info Provider: " + provider.helperName());
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