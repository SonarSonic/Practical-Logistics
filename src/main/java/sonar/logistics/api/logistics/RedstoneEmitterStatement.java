package sonar.logistics.api.logistics;

import sonar.core.network.sync.SyncNBTAbstract;
import sonar.logistics.api.info.InfoUUID;

public class RedstoneEmitterStatement {

	public SyncNBTAbstract<InfoUUID> uuid = new SyncNBTAbstract<InfoUUID>(InfoUUID.class, 1);
	
	
	
}
