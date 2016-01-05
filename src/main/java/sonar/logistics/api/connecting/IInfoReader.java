package sonar.logistics.api.connecting;

import sonar.logistics.api.Info;

public interface IInfoReader extends IDataConnection {

	/**the secondary info for use with Screens when creating progress bars*/
	public Info getSecondaryInfo();
}
