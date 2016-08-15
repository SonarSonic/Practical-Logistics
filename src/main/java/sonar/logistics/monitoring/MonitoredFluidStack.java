package sonar.logistics.monitoring;

import net.minecraftforge.fluids.FluidStack;
import sonar.core.api.fluids.StoredFluidStack;
import sonar.logistics.api.info.monitor.IJoinableInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;

public class MonitoredFluidStack extends StoredFluidStack implements IJoinableInfo<MonitoredFluidStack> {

	public MonitoredFluidStack(FluidStack stack) {
		super(stack);
	}

	public MonitoredFluidStack(FluidStack stack, long capacity) {
		super(stack, capacity);
	}

	public MonitoredFluidStack(FluidStack stack, long stored, long capacity) {
		super(stack, stored, capacity);
	}

	public MonitoredFluidStack(StoredFluidStack stack) {
		super(stack.fluid, stack.stored, stack.capacity);
	}

	@Override
	public boolean isIdenticalInfo(MonitoredFluidStack info) {
		return equals(info);
	}

	@Override
	public boolean isMatchingInfo(MonitoredFluidStack info) {
		return equalStack(info.fluid);
	}

	@Override
	public void updateFrom(MonitoredFluidStack info) {
		this.fluid = info.fluid;
		this.capacity = info.capacity;
		this.stored = info.stored;
	}

	@Override
	public boolean canJoinInfo(MonitoredFluidStack info) {
		return isMatchingInfo(info);
	}

	@Override
	public IJoinableInfo joinInfo(MonitoredFluidStack info) {
		add((MonitoredFluidStack) info);
		return this;
	}

	@Override
	public boolean isHeader() {
		return true;
	}

	@Override
	public boolean isMatchingType(IMonitorInfo info) {
		return info instanceof MonitoredFluidStack;
	}
}
