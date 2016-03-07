package sonar.logistics.api.signals;

import sonar.core.network.sync.SyncTagType;
import sonar.logistics.api.info.Info;

public class StringStatement extends SignallerStatement {

	public SyncTagType.STRING target = new SyncTagType.STRING(0);

	public StringStatement(String target) {
		this.target.setObject(target);
	}

	@Override
	public int getType() {
		return string;
	}

	public void setTarget(String target) {
		this.target.setObject(target);
	}

	@Override
	public boolean canSignal(Info info) {
		if (info.getDataType() == string) {
			boolean signal = info.getData().equals(target.getObject());
			return invert ? !signal : signal;
		}
		return false;
	}
}
