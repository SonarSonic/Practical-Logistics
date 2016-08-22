package sonar.logistics.monitoring;

import sonar.core.api.inventories.StoredItemStack;
import sonar.core.network.sync.SyncNBTAbstract;
import sonar.logistics.Logistics;
import sonar.logistics.api.asm.LogicInfoType;
import sonar.logistics.api.info.BaseInfo;
import sonar.logistics.api.info.monitor.IJoinableInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.MonitorHandler;

@LogicInfoType(id = MonitoredItemStack.id, modid = Logistics.MODID)
public class MonitoredItemStack extends BaseInfo<MonitoredItemStack> implements IJoinableInfo<MonitoredItemStack> {

	public static final String id = "item";
	public final MonitorHandler<MonitoredItemStack> handler = Logistics.monitorHandlers.getRegisteredObject(MonitorHandler.ITEMS);
	public SyncNBTAbstract<StoredItemStack> itemStack = new SyncNBTAbstract<StoredItemStack>(StoredItemStack.class, 0);

	{
		syncParts.add(itemStack);
	}

	public MonitoredItemStack() {
	}

	public MonitoredItemStack(StoredItemStack stack) {
		this.itemStack.setObject(stack);
	}

	@Override
	public boolean isIdenticalInfo(MonitoredItemStack info) {
		return itemStack.getObject().equals(info.itemStack.getObject());
	}

	@Override
	public boolean isMatchingInfo(MonitoredItemStack info) {
		return itemStack.getObject().equalStack(info.itemStack.getObject().item);
	}

	@Override
	public boolean isMatchingType(IMonitorInfo info) {
		return info instanceof MonitoredItemStack;
	}

	@Override
	public MonitorHandler<MonitoredItemStack> getHandler() {
		return handler;
	}

	@Override
	public boolean canJoinInfo(MonitoredItemStack info) {
		return isMatchingInfo(info);
	}

	@Override
	public IJoinableInfo joinInfo(MonitoredItemStack info) {
		itemStack.getObject().add(info.itemStack.getObject());
		return this;
	}

	@Override
	public boolean isValid() {
		return itemStack.getObject() != null && itemStack.getObject().item != null;
	}

	@Override
	public String getID() {
		return id;
	}

	public String toString() {
		if (itemStack.getObject() != null)
			return itemStack.getObject().toString();
		return super.toString() + " : NULL";
	}

}
