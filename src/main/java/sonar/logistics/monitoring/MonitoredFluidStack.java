package sonar.logistics.monitoring;

import sonar.core.api.fluids.StoredFluidStack;
import sonar.core.network.sync.SyncNBTAbstract;
import sonar.logistics.Logistics;
import sonar.logistics.api.asm.LogicInfoType;
import sonar.logistics.api.info.BaseInfo;
import sonar.logistics.api.info.monitor.IJoinableInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.MonitorHandler;

@LogicInfoType(id = MonitoredFluidStack.id, modid = Logistics.MODID)
public class MonitoredFluidStack extends BaseInfo<MonitoredFluidStack> implements IJoinableInfo<MonitoredFluidStack> {

	public static final String id = "fluid";
	public static final MonitorHandler<MonitoredFluidStack> handler = Logistics.monitorHandlers.getRegisteredObject(MonitorHandler.FLUIDS);
	public SyncNBTAbstract<StoredFluidStack> fluidStack = new SyncNBTAbstract<StoredFluidStack>(StoredFluidStack.class, 0);

	{
		syncParts.add(fluidStack);
	}

	public MonitoredFluidStack() {
	}

	public MonitoredFluidStack(StoredFluidStack stack) {
		this.fluidStack.setObject(stack);
	}

	@Override
	public boolean isIdenticalInfo(MonitoredFluidStack info) {
		return fluidStack.getObject().equals(info.fluidStack.getObject());
	}

	@Override
	public boolean isMatchingInfo(MonitoredFluidStack info) {
		return fluidStack.getObject().equalStack(info.fluidStack.getObject().fluid);
	}

	@Override
	public boolean isMatchingType(IMonitorInfo info) {
		return info instanceof MonitoredFluidStack;
	}

	@Override
	public MonitorHandler<MonitoredFluidStack> getHandler() {
		return handler;
	}

	@Override
	public boolean canJoinInfo(MonitoredFluidStack info) {
		return isMatchingInfo(info);
	}

	@Override
	public IJoinableInfo joinInfo(MonitoredFluidStack info) {
		fluidStack.getObject().add(info.fluidStack.getObject());
		return this;
	}

	@Override
	public boolean isValid() {
		return fluidStack.getObject()!=null && fluidStack.getObject().fluid != null;
	}

	@Override
	public String getID() {
		return id;
	}

}
