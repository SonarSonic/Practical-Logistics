package sonar.logistics.api.wrappers;

import java.util.Collections;
import java.util.List;

import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.info.types.StoredEnergyInfo;

public class EnergyWrapper {

	/** used for getting the full list of Energy Info on a given network
	 * @param network the {@link INetworkCache} 
	 * @return list of {@link StoredEnergyInfo} on the network */
	public List<StoredEnergyInfo> getEnergyList(INetworkCache network) {
		return Collections.EMPTY_LIST;
	}

	//METHODS FOR ADDING / REMOVING ENERGY WILL BE COMING SOON
}
