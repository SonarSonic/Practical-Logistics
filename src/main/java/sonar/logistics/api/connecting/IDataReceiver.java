package sonar.logistics.api.connecting;

import sonar.logistics.api.DataEmitter;

/**implemented by blocks which can connect to Data Emitters namely the Data Receiver itself*/
public interface IDataReceiver extends IDataConnection {

	/**the connected emitter, this could be null*/
	public DataEmitter getEmitter();
}
