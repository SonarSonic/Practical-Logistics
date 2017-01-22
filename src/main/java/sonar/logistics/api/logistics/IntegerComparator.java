package sonar.logistics.api.logistics;

import java.util.List;

import sonar.logistics.api.asm.LogicComparator;

@LogicComparator(handlingClass = Integer.class)
public class IntegerComparator implements ILogicComparator<Integer> {

	@Override
	public LogicState getLogicState(LogicOperator operator, Integer info, Integer object) {
		return LogicState.getState(operator.basicComparison(info, object));
	}

	@Override
	public List<LogicOperator> getValidOperators() {
		return LogicOperator.numOperators;
	}

}
