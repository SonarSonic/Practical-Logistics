package sonar.logistics.api.logistics;

import java.util.Map;

public interface IComparableProvider<T> {

	public void getComparableObjects(String parent, T obj, Map<LogicIdentifier, Object> objects);
	
}
