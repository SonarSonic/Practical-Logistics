package sonar.logistics.logic.comparators;

import java.util.List;

import sonar.logistics.api.logistics.LogicOperator;
import sonar.logistics.api.logistics.LogicState;

public interface ILogicComparator<T> {

	public LogicState getLogicState(LogicOperator operator, T info, T object);

	public List<LogicOperator> getValidOperators();

}
