package sonar.logistics.api.signals;

import sonar.core.network.sync.SyncInt;
import sonar.logistics.api.Info;

public class IntegerStatement extends SignallerStatement {

	public SyncInt emitType = new SyncInt(0);
	public SyncInt target = new SyncInt(1);

	public IntegerStatement(int emitType, int target) {
		this.emitType.setInt(emitType);
		this.target.setInt(target);
	}

	@Override
	public int getType() {
		return number;
	}

	public void setTarget(int target) {
		this.target.setInt(target);
	}

	@Override
	public boolean canSignal(Info info) {
		boolean statement = false;
		if (info.getDataType() == this.getType()) {
			int integer = Integer.parseInt(info.getData());
			switch (emitType.getInt()) {
			case 0:
				statement = integer == target.getInt();
				break;
			case 1:
				statement = integer > target.getInt();
				break;
			case 2:
				statement = integer < target.getInt();
				break;
			case 3:
				statement = integer != target.getInt();
				break;
			}
		}
		return invert ? !statement : statement;
	}
}
