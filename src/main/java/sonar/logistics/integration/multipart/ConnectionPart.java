package sonar.logistics.integration.multipart;

import sonar.core.integration.fmp.SonarHandlerPart;
import sonar.logistics.api.connecting.IInfoEmitter;
import codechicken.multipart.TMultiPart;

public abstract class ConnectionPart extends SonarHandlerPart implements IInfoEmitter {
	public ConnectionPart() {
		super();
	}

	public ConnectionPart(int meta) {
		super(meta);
	}

	@Override
	public void onWorldJoin() {
		super.onWorldJoin();
		this.addConnections();
	}

	@Override
	public void onWorldSeparate() {
		super.onWorldSeparate();
		this.removeConnections();
	}

	public void onPartChanged(TMultiPart part) {
		super.onPartChanged(part);
		this.removeConnections();
		this.addConnections();
	}
}
