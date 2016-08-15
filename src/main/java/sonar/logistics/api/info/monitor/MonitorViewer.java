package sonar.logistics.api.info.monitor;

import net.minecraft.entity.player.EntityPlayer;

public class MonitorViewer {
	public boolean[] sentPackets = new boolean[MonitorType.values().length];
	public final EntityPlayer player;
	public MonitorType type;

	public MonitorViewer(EntityPlayer player, MonitorType type) {
		this.player = player;
		this.type = type;
	}
	
	public boolean wasSent(MonitorType type) {
		return sentPackets[type.ordinal()];
	}

	public void sentFirstPacket(MonitorType type) {
		sentPackets[type.ordinal()] = true;
	}

	public void setMonitorType(MonitorType type) {
		this.type = type;
		for (Boolean bool : sentPackets) {
			bool = false;
		}
	}

}