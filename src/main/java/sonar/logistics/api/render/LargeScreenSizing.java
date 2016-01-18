package sonar.logistics.api.render;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

/**used to work out the Scaling of Large Display screens, still kind of w.i.p*/
public class LargeScreenSizing {

	public int maxY = 0, minY = 0, maxH = 0, minH = 0;

	public LargeScreenSizing(int maxY, int minY, int maxH, int minH) {
		this.maxY = maxY;
		this.minY = minY;
		this.maxH = maxH;
		this.minH = minH;
	}

	public void writeToBuf(ByteBuf buf) {
		buf.writeInt(maxY);
		buf.writeInt(minY);
		buf.writeInt(maxH);
		buf.writeInt(minH);
	}

	public static LargeScreenSizing readFromBuf(ByteBuf buf) {
		return new LargeScreenSizing(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
	}
	
	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("mxY", maxY);
		tag.setInteger("mnY", minY);
		tag.setInteger("mxH", maxH);
		tag.setInteger("mnH", minH);
	}

	public static LargeScreenSizing readFromNBT(NBTTagCompound tag) {
		return new LargeScreenSizing(tag.getInteger("mxY"), tag.getInteger("mnY"), tag.getInteger("mxH"), tag.getInteger("mnH"));
	}
}
