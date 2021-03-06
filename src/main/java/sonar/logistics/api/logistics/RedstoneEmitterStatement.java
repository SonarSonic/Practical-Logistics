package sonar.logistics.api.logistics;

import sonar.core.network.sync.SyncEnum;
import sonar.core.network.sync.SyncNBTAbstract;
import sonar.logistics.api.info.InfoUUID;

public class RedstoneEmitterStatement {

	public SyncNBTAbstract<InfoUUID> uuid = new SyncNBTAbstract<InfoUUID>(InfoUUID.class, 1);
	public SyncEnum<LogicOperator> operator = new SyncEnum(LogicOperator.values(), 2);
		
}
