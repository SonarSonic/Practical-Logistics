package sonar.logistics.api.logistics;

import java.util.List;

import sonar.logistics.api.asm.LogicComparator;

@LogicComparator(handlingClass = Object.class)
public class ObjectComparator implements ILogicComparator<Object> {

	@Override
	public LogicState getLogicState(LogicOperator operator, Object info, Object object) {
		switch (operator) {
		case NOT_EQUALS:
			return LogicState.getState(!info.equals(object));
		case EQUALS:
			return LogicState.getState(info.equals(object));
		default:
			return LogicState.FALSE;
		}
	}

	@Override
	public List<LogicOperator> getValidOperators() {
		return LogicOperator.switchOperators;
	}

}