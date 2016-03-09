package sonar.logistics.api.signals;

import sonar.core.network.sync.SyncTagType;
import sonar.logistics.api.info.ILogicInfo;

public class IntegerStatement extends SignallerStatement {

	public SyncTagType.INT emitType = new SyncTagType.INT(0);
	public SyncTagType.LONG target = new SyncTagType.LONG(1);

	/**
	 * @param emitType ==, >, <, !=;
	 * @param target the integer number which has been typed.
	 */
	public IntegerStatement(int emitType, long target) {
		this.emitType.setObject(emitType);
		this.target.setObject(target);
	}

	@Override
	public int getType() {
		return number;
	}

	public void setTarget(long target) {
		this.target.setObject(target);
	}

	@Override
	public boolean canSignal(ILogicInfo info) {
		boolean statement = false;
		if (info.getDataType() == this.getType()) {
			int integer = Integer.parseInt(info.getData());
			switch (emitType.getObject()) {
			case 0:
				statement = integer == target.getObject();
				break;
			case 1:
				statement = integer > target.getObject();
				break;
			case 2:
				statement = integer < target.getObject();
				break;
			case 3:
				statement = integer != target.getObject();
				break;
			}
		}
		return invert ? !statement : statement;
	}
}
