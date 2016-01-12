package sonar.logistics.api.signals;

import sonar.core.network.sync.SyncString;
import sonar.logistics.api.Info;

public class StringStatement extends SignallerStatement {

	public SyncString target = new SyncString(0);

	public StringStatement(String target) {
		this.target.setString(target);
	}

	@Override
	public int getType() {
		return string;
	}

	public void setTarget(String target) {
		this.target.setString(target);
	}

	@Override
	public boolean canSignal(Info info) {
		if (info.getDataType() == string) {
			boolean signal = info.getData().equals(target.getString());
			return invert ? !signal : signal;
		}
		return false;
	}
}
