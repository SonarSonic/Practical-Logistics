package sonar.logistics.api.info;

import com.google.common.base.Objects;

import io.netty.buffer.ByteBuf;

public class InfoUUID {

	public int hashCode;
	public int pos;

	public InfoUUID(int hashCode, int pos) {
		this.hashCode = hashCode;
		this.pos = pos;
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof InfoUUID) {
			return this.hashCode == ((InfoUUID) obj).hashCode && this.pos == ((InfoUUID) obj).pos;
		}
		return false;
	}

	public int hashCode() {
		return Objects.hashCode(hashCode, pos);
	}

	public boolean valid() {
		if (hashCode == -1 && pos == -1) {
			return false;
		}
		return true;
	}

	public static InfoUUID getUUID(ByteBuf buf) {
		return new InfoUUID(buf.readInt(), buf.readInt());
	}

	public void writeToBuf(ByteBuf buf) {
		buf.writeInt(hashCode);
		buf.writeInt(pos);
	}
}
