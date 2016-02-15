package sonar.logistics.api.wrappers;

import java.util.Collections;
import java.util.List;

import sonar.core.utils.BlockCoords;
import sonar.logistics.info.types.StoredEnergyInfo;

public class EnergyWrapper {

	/**used for getting the full list of Energy Info on a given network
	 * @param network current coordinates of the network
	 * @return list of {@link StoredEnergyInfo}  on the network
	 */
	public List<StoredEnergyInfo> getEnergyList(List<BlockCoords> network) {
		return Collections.EMPTY_LIST;
	}
}
