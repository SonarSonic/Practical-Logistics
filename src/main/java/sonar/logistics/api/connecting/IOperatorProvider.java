package sonar.logistics.api.connecting;

import java.util.List;

/**provides information to be displayed by the Operator Tool*/
public interface IOperatorProvider {

	/**add to the list of info what you would like to be displayed, the block name should be added first*/
	public void addInfo(List<String> info);

}
