package sonar.logistics.api.display;

import io.netty.buffer.ByteBuf;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import sonar.core.api.utils.BlockInteractionType;
import sonar.logistics.api.info.IAdvancedClickableInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.common.multiparts.ScreenMultipart;

public class ScreenInteractionEvent {

	public int hashCode;
	public IMonitorInfo currentInfo;
	public int infoPos;
	public EntityPlayer player;
	public BlockInteractionType type;
	public boolean doubleClick;
	public EnumHand hand;
	public PartMOP hit;

	public ScreenInteractionEvent() {
	}

	public ScreenInteractionEvent(int hashCode, IMonitorInfo currentInfo, int infoPos, EntityPlayer player, BlockInteractionType type, boolean doubleClick, EnumHand hand, PartMOP hit) {
		this.hashCode = hashCode;
		this.currentInfo = currentInfo;
		this.infoPos = infoPos;
		this.player = player;
		this.type = type;
		this.doubleClick = doubleClick;
		this.hand = hand;
		this.hit = hit;
	}

	public void writeToBuf(ByteBuf buf) {
		buf.writeInt(hashCode);
		buf.writeInt(infoPos);
		buf.writeInt(type.ordinal());
		buf.writeInt(hand.ordinal());
		buf.writeBoolean(doubleClick);
	}

	public static ScreenInteractionEvent readFromBuf(ByteBuf buf, EntityPlayer player, ScreenMultipart part) {
		ScreenInteractionEvent event = new ScreenInteractionEvent();
		event.hashCode = buf.readInt();
		event.infoPos = buf.readInt();
		event.player = player;
		event.type = BlockInteractionType.values()[buf.readInt()];
		event.hand = EnumHand.values()[buf.readInt()];
		event.doubleClick = buf.readBoolean();
		event.hit = part.getPartHit(player);
		event.currentInfo = part.container().getDisplayInfo(event.infoPos).getCachedInfo();
		return event;
	}

	public int hashCode() {
		return hashCode;
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ScreenInteractionEvent) {
			return ((ScreenInteractionEvent) obj).hashCode == hashCode;
		}
		return false;
	}
}
