package sonar.logistics.api.connecting;

import net.minecraft.util.IStringSerializable;

public enum CableConnection implements IStringSerializable {
	CABLE, INTERNAL, HALF, /* BLOCK, */ NONE;

	public boolean canConnect() {
		return this == CABLE || this == INTERNAL || this == HALF;
	}

	public double offsetBounds() {
		return this == INTERNAL ? 0.0625 : this == HALF ? 0.0625 * 3 : 0;
	}

	public String getName() {
		return this.toString().toLowerCase();
	}
}