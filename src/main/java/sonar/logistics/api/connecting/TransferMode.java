package sonar.logistics.api.connecting;

import net.minecraft.util.IStringSerializable;

/** the various transfer modes for the Transfer Node */
public enum TransferMode implements IStringSerializable {

	/** pulls items into the system */
	PULL,
	/** pushes items into of the block */
	PUSH,
	/** makes the transfer node inactive */
	PASSIVE;

	public boolean shouldPull() {
		return this == PULL;
	}

	public boolean shouldPush() {
		return this == PUSH;
	}

	public boolean isPassive() {
		return this == PASSIVE;
	}

	@Override
	public String getName() {
		return name().toLowerCase();
	}

}
