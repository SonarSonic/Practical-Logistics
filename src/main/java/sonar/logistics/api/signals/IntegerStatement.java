package sonar.logistics.api.signals;

import sonar.core.network.sync.SyncInt;
import sonar.core.network.sync.SyncLong;
import sonar.logistics.api.Info;

public class IntegerStatement extends SignallerStatement {

	public SyncInt emitType = new SyncInt(0);
	public SyncLong target = new SyncLong(1);

	/**
	 * @param emitType ==, >, <, !=;
	 * @param target the integer number which has been typed.
	 */
	public IntegerStatement(int emitType, long target) {
		this.emitType.setInt(emitType);
		this.target.setLong(target);
	}

	@Override
	public int getType() {
		return number;
	}

	public void setTarget(long target) {
		this.target.setLong(target);
	}

	@Override
	public boolean canSignal(Info info) {
		boolean statement = false;
		if (info.getDataType() == this.getType()) {
			int integer = Integer.parseInt(info.getData());
			switch (emitType.getInt()) {
			case 0:
				statement = integer == target.getLong();
				break;
			case 1:
				statement = integer > target.getLong();
				break;
			case 2:
				statement = integer < target.getLong();
				break;
			case 3:
				statement = integer != target.getLong();
				break;
			}
		}
		return invert ? !statement : statement;
	}
}
