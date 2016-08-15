package sonar.logistics.monitoring;

import net.minecraft.item.ItemStack;
import sonar.core.api.inventories.StoredItemStack;
import sonar.logistics.api.info.monitor.IJoinableInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;

public class MonitoredItemStack extends StoredItemStack implements IJoinableInfo<MonitoredItemStack> {

	public MonitoredItemStack(ItemStack stack) {
		super(stack);
	}

	public MonitoredItemStack(ItemStack stack, long stored) {
		super(stack, stored);
	}

	public MonitoredItemStack(StoredItemStack stack) {
		super(stack.item, stack.stored);
	}

	@Override
	public boolean isIdenticalInfo(MonitoredItemStack info) {
		return equals(info);
	}

	@Override
	public boolean isMatchingInfo(MonitoredItemStack info) {
		return equalStack(info.item);
	}

	@Override
	public void updateFrom(MonitoredItemStack info) {
		this.item = info.item;
		this.stored = info.stored;
	}

	@Override
	public boolean canJoinInfo(MonitoredItemStack info) {
		return isMatchingInfo(info);
	}

	@Override
	public IJoinableInfo joinInfo(MonitoredItemStack info) {
		add((MonitoredItemStack) info);
		return this;
	}

	@Override
	public boolean isHeader() {
		return true;
	}

	@Override
	public boolean isMatchingType(IMonitorInfo info) {
		return info instanceof MonitoredItemStack;
	}
}
