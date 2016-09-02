package sonar.logistics.monitoring;

import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.network.sync.SyncNBTAbstract;
import sonar.logistics.Logistics;
import sonar.logistics.api.asm.LogicInfoType;
import sonar.logistics.api.display.DisplayType;
import sonar.logistics.api.info.BaseInfo;
import sonar.logistics.api.info.monitor.IJoinableInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;

@LogicInfoType(id = MonitoredEnergyStack.id, modid = Logistics.MODID)
public class MonitoredEnergyStack extends BaseInfo<MonitoredEnergyStack> implements IJoinableInfo<MonitoredEnergyStack> {

	public static final String id = "energy";
	public static LogicMonitorHandler<MonitoredEnergyStack> handler = LogicMonitorHandler.instance(EnergyMonitorHandler.id);
	public SyncNBTAbstract<StoredEnergyStack> energyStack = new SyncNBTAbstract<StoredEnergyStack>(StoredEnergyStack.class, 0);

	{
		syncParts.add(energyStack);
	}

	public MonitoredEnergyStack() {
	}

	public MonitoredEnergyStack(StoredEnergyStack stack) {
		this.energyStack.setObject(stack);
	}

	@Override
	public boolean isIdenticalInfo(MonitoredEnergyStack info) {
		return energyStack.getObject().equals(info.energyStack.getObject());
	}

	@Override
	public boolean isMatchingInfo(MonitoredEnergyStack info) {
		return energyStack.getObject().energyType.equals(info.energyStack.getObject().energyType);
	}

	@Override
	public boolean isMatchingType(IMonitorInfo info) {
		return info instanceof MonitoredEnergyStack;
	}

	@Override
	public LogicMonitorHandler<MonitoredEnergyStack> getHandler() {
		return handler;
	}

	@Override
	public boolean canJoinInfo(MonitoredEnergyStack info) {
		return isMatchingInfo(info);
	}

	@Override
	public IJoinableInfo joinInfo(MonitoredEnergyStack info) {
		energyStack.getObject().add(info.energyStack.getObject());
		return this;
	}

	@Override
	public boolean isValid() {
		return energyStack.getObject() != null && energyStack.getObject().energyType != null;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public MonitoredEnergyStack copy() {
		return new MonitoredEnergyStack(energyStack.getObject().copy());
	}

	@Override
	public void renderInfo(DisplayType displayType, double width, double height, double scale, int infoPos) {
		// TODO Auto-generated method stub
		
	}

}
