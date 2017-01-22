package sonar.logistics.api.logistics;

import java.util.List;

public interface ILogicComparator<T> {

	public LogicState getLogicState(LogicOperator operator, T info, T object);

	public List<LogicOperator> getValidOperators();

}
