package sonar.logistics.logic.comparators;

import java.util.List;

import sonar.logistics.api.asm.LogicComparator;
import sonar.logistics.api.logistics.LogicOperator;
import sonar.logistics.api.logistics.LogicState;

@LogicComparator(handlingClass = Number.class)
public class NumberComparator implements ILogicComparator<Number> {

	@Override
	public LogicState getLogicState(LogicOperator operator, Number info, Number object) {
		return LogicState.getState(operator.basicComparison(info.doubleValue(), object.doubleValue()));
	}

	@Override
	public List<LogicOperator> getValidOperators() {
		return LogicOperator.numOperators;
	}

}
