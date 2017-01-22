package sonar.logistics.connections.monitoring;

import com.google.common.collect.Lists;

import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.network.sync.SyncNBTAbstract;
import sonar.logistics.Logistics;
import sonar.logistics.api.asm.LogicInfoType;
import sonar.logistics.api.display.IDisplayInfo;
import sonar.logistics.api.info.BaseInfo;
import sonar.logistics.api.info.INameableInfo;
import sonar.logistics.api.info.InfoContainer;
import sonar.logistics.api.info.monitor.IJoinableInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;
import sonar.logistics.network.SyncMonitoredType;

@LogicInfoType(id = MonitoredEnergyStack.id, modid = Logistics.MODID)
public class MonitoredEnergyStack extends BaseInfo<MonitoredEnergyStack> implements IJoinableInfo<MonitoredEnergyStack>, INameableInfo<MonitoredEnergyStack> {

	public static final String id = "energy";
	public static LogicMonitorHandler<MonitoredEnergyStack> handler = LogicMonitorHandler.instance(EnergyMonitorHandler.id);
	public SyncNBTAbstract<StoredEnergyStack> energyStack = new SyncNBTAbstract<StoredEnergyStack>(StoredEnergyStack.class, 0);
	public SyncMonitoredType<MonitoredBlockCoords> coords = new SyncMonitoredType<MonitoredBlockCoords>(1);

	{
		syncParts.addParts(energyStack, coords);
	}

	public MonitoredEnergyStack() {
	}

	public MonitoredEnergyStack(StoredEnergyStack stack, MonitoredBlockCoords coords) {
		this.energyStack.setObject(stack);
		this.coords.setInfo(coords);
	}

	@Override
	public boolean isIdenticalInfo(MonitoredEnergyStack info) {
		return energyStack.getObject().equals(info.energyStack.getObject()) && coords.getMonitoredInfo().isIdenticalInfo(info.coords.getMonitoredInfo());
	}

	@Override
	public boolean isMatchingInfo(MonitoredEnergyStack info) {
		return energyStack.getObject().energyType.equals(info.energyStack.getObject().energyType) && coords.getMonitoredInfo().isMatchingInfo(info.coords.getMonitoredInfo());
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
		return false;//isMatchingInfo(info);
	}

	@Override
	public IJoinableInfo joinInfo(MonitoredEnergyStack info) {
		energyStack.getObject().add(info.energyStack.getObject());
		return this;
	}

	@Override
	public boolean isValid() {
		return energyStack.getObject() != null && energyStack.getObject().energyType != null && coords.getMonitoredInfo()!=null;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public MonitoredEnergyStack copy() {
		return new MonitoredEnergyStack(energyStack.getObject().copy(), coords.getMonitoredInfo().copy());
	}

	@Override
	public void renderInfo(InfoContainer container, IDisplayInfo displayInfo, double width, double height, double scale, int infoPos) {
		
	}

	@Override
	public String getClientIdentifier() {
		return "Energy: " + (energyStack.getObject() != null && energyStack.getObject().energyType != null ? energyStack.getObject().energyType.getName() : "ENERGYSTACK");
	}

	@Override
	public String getClientObject() {
		return energyStack.getObject() != null ? "" + energyStack.getObject().stored : "ERROR";
	}

	@Override
	public String getClientType() {
		return "energy";
	}

}
