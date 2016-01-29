package sonar.logistics.api.connecting;

import sonar.logistics.api.DataEmitter;

/**implemented by blocks which can connect to Data Emitters namely the Data Receiver itself*/
public interface IDataReceiver extends IInfoEmitter {

	/**the connected emitter, this could be null if one hasn't been selected*/
	public DataEmitter getEmitter();
}
