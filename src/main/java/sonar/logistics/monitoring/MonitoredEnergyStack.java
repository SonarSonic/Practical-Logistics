package sonar.logistics.monitoring;

import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.logistics.api.info.monitor.IJoinableInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;

public class MonitoredEnergyStack extends StoredEnergyStack implements IJoinableInfo<MonitoredEnergyStack> {

	public MonitoredEnergyStack(EnergyType type) {
		super(type);
	}

	public MonitoredEnergyStack(StoredEnergyStack stack) {
		super(stack.energyType);
		stored = stack.stored;
		capacity = stack.stored;
		input = stack.input;
		output = stack.output;
		usage = stack.usage;
		hasStorage = stack.hasStorage;
		hasInput = stack.hasInput;
		hasOutput = stack.hasOutput;
		hasUsage = stack.hasUsage;
	}

	@Override
	public boolean isIdenticalInfo(MonitoredEnergyStack info) {
		return equals(info);
	}

	@Override
	public boolean isMatchingInfo(MonitoredEnergyStack info) {
		return energyType.equals(info.energyType);
	}

	@Override
	public void updateFrom(MonitoredEnergyStack info) {
		info.stored = stored;
		info.capacity = stored;
		info.input = input;
		info.output = output;
		info.usage = usage;
		info.hasStorage = hasStorage;
		info.hasInput = hasInput;
		info.hasOutput = hasOutput;
		info.hasUsage = hasUsage;
	}

	@Override
	public boolean canJoinInfo(MonitoredEnergyStack info) {
		return isMatchingInfo(info);
	}

	@Override
	public IJoinableInfo joinInfo(MonitoredEnergyStack info) {
		add((MonitoredEnergyStack) info);
		return this;
	}

	@Override
	public boolean isHeader() {
		return true;
	}

	@Override
	public boolean isMatchingType(IMonitorInfo info) {
		return info instanceof MonitoredEnergyStack;
	}
}
