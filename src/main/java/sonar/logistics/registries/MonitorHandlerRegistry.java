package sonar.logistics.registries;

import sonar.core.helpers.RegistryHelper;
import sonar.logistics.api.info.monitor.MonitorHandler;
import sonar.logistics.monitoring.ChannelMonitorHandler;
import sonar.logistics.monitoring.EnergyMonitorHandler;
import sonar.logistics.monitoring.FluidMonitorHandler;
import sonar.logistics.monitoring.InfoMonitorHandler;
import sonar.logistics.monitoring.ItemMonitorHandler;

public class MonitorHandlerRegistry extends RegistryHelper<MonitorHandler> {

	@Override
	public void register() {
		//registerObject(new LogicInfoMonitorHandler());
		registerObject(new InfoMonitorHandler());
		registerObject(new ItemMonitorHandler());
		registerObject(new FluidMonitorHandler());
		registerObject(new EnergyMonitorHandler());
		registerObject(new ChannelMonitorHandler());
	}

	@Override
	public String registeryType() {
		return "Monitor Handlers";
	}

}
