package sonar.logistics.api.connecting;

import sonar.logistics.api.info.Info;

/**implemented on blocks which can provide two bits of info (they must be emitters) namely the Info Reader itself*/
public interface IInfoReader extends IInfoEmitter {

	/**the secondary info for use with Screens when creating progress bars*/
	public Info getSecondaryInfo();
}
