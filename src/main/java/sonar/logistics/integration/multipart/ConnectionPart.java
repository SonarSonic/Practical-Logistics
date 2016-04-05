package sonar.logistics.integration.multipart;

import sonar.logistics.api.connecting.IInfoEmitter;
import codechicken.multipart.TMultiPart;

public abstract class ConnectionPart extends LogisticsPart.Handler implements IInfoEmitter {
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
