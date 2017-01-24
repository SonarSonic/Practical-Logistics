package sonar.logistics.api.display;

import java.util.ArrayList;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import io.netty.buffer.ByteBuf;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import sonar.core.api.utils.BlockInteractionType;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.DirtyPart;
import sonar.core.network.sync.IDirtyPart;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncableList;
import sonar.logistics.Logistics;
import sonar.logistics.api.info.IClickableInfo;
import sonar.logistics.api.info.IInfoContainer;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.types.InfoError;
import sonar.logistics.helpers.InfoHelper;

/** used to store {@link IMonitorInfo} along with their respective {@link DisplayInfo} for rendering on a {@link IInfoDisplay}*/
public class InfoContainer extends DirtyPart implements IInfoContainer, ISyncPart {

	public static final ResourceLocation colour1 = new ResourceLocation(Logistics.MODID + ":textures/model/" + "progress1.png");
	public static final ResourceLocation colour2 = new ResourceLocation(Logistics.MODID + ":textures/model/" + "progress2.png");
	public static final ResourceLocation colour3 = new ResourceLocation(Logistics.MODID + ":textures/model/" + "progress3.png");
	public static final ResourceLocation colour4 = new ResourceLocation(Logistics.MODID + ":textures/model/" + "progress4.png");
	public final ArrayList<DisplayInfo> storedInfo = new ArrayList();
	public final IInfoDisplay display;
	public SyncableList syncParts = new SyncableList(this);
	public long lastClickTime;
	public UUID lastClickUUID;
	public boolean hasChanged = true;

	public InfoContainer(IInfoDisplay display) {
		this.display = display;
		this.setListener(display);
		for (int i = 0; i < display.maxInfo(); i++) {
			DisplayInfo syncPart = new DisplayInfo(this, i);
			storedInfo.add(syncPart);
		}
		syncParts.addParts(storedInfo);
		// if (display.getCoords()!=null && display.getCoords().getWorld().isRemote)
		resetRenderProperties();
	}

	public void resetRenderProperties() {
		for (int i = 0; i < storedInfo.size(); i++) {
			DisplayInfo info = storedInfo.get(i);
			double[] scaling = InfoHelper.getScaling(display, display.getLayout(), i);
			double[] translation = InfoHelper.getTranslation(display, display.getLayout(), i);
			info.setRenderInfoProperties(new RenderInfoProperties(this, i, scaling, translation));
		}
	}

	public static ResourceLocation getColour(int infoPos) {
		switch (infoPos) {
		case 0:
			return colour1;
		case 1:
			return colour2;
		case 2:
			return colour3;
		case 3:
			return colour4;
		default:
			return colour1;
		}
	}

	public boolean monitorsUUID(InfoUUID id) {
		for (int i = 0; i < display.getLayout().maxInfo; i++) {
			InfoUUID infoID = this.getInfoUUID(i);
			if (infoID != null && infoID.equals(id)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public InfoUUID getInfoUUID(int pos) {
		return storedInfo.get(pos).getInfoUUID();
	}

	@Override
	public void setUUID(InfoUUID id, int pos) {
		storedInfo.get(pos).setUUID(id);
		markDirty();
	}

	@Override
	public void renderContainer() {
		if (display.getDisplayType() == DisplayType.LARGE) {
			GL11.glTranslated(0, -0.0625 * 4, 0);
		}
		ScreenLayout layout = display.getLayout();
		DisplayType type = display.getDisplayType();
		for (int pos = 0; pos < layout.maxInfo; pos++) {
			IDisplayInfo info = storedInfo.get(pos);
			IMonitorInfo toDisplay = info.getCachedInfo() == null ? InfoError.noData : info.getCachedInfo();
			GL11.glPushMatrix();
			double[] translation = info.getRenderProperties().translation;
			double[] scaling = info.getRenderProperties().scaling;
			GL11.glTranslated(translation[0], translation[1], translation[2]);
			toDisplay.renderInfo(this, info, scaling[0], scaling[1], scaling[2], pos);
			GL11.glPopMatrix();
		}
	}

	@Override
	public boolean onClicked(BlockInteractionType type, World world, EntityPlayer player, EnumHand hand, ItemStack stack, PartMOP hit) {
		boolean doubleClick = false;
		if (world.getTotalWorldTime() - lastClickTime < 10 && player.getPersistentID().equals(lastClickUUID)) {
			doubleClick = true;
		}
		boolean bool = !world.isRemote;
		if (world.isRemote) {
			lastClickTime = world.getTotalWorldTime();
			lastClickUUID = player.getPersistentID();
			for (int i = 0; i < display.maxInfo(); i++) {
				IDisplayInfo info = storedInfo.get(i);
				IMonitorInfo cachedInfo = info.getCachedInfo();
				if (cachedInfo instanceof IClickableInfo) {
					boolean clicked = ((IClickableInfo) cachedInfo).onClicked(type, doubleClick, info, player, hand, stack, hit, this);
					if (clicked) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		NBTTagCompound tag = nbt.getCompoundTag(this.getTagName());
		if (!tag.hasNoTags()) {
			NBTHelper.readSyncParts(tag, type, syncParts);
			resetRenderProperties();
		}
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		NBTTagCompound tag = NBTHelper.writeSyncParts(new NBTTagCompound(), type, syncParts, type == SyncType.SYNC_OVERRIDE);
		if (!tag.hasNoTags())
			nbt.setTag(this.getTagName(), tag);

		return nbt;

	}

	@Override
	public int getMaxCapacity() {
		return Math.min(display.maxInfo(), display.getLayout().maxInfo);
	}

	@Override
	public IInfoDisplay getDisplay() {
		return display;
	}

	@Override
	public DisplayInfo getDisplayInfo(int pos) {
		return storedInfo.get(pos);
	}

	public InfoContainer cloneFromContainer(IInfoContainer container) {
		this.readData(container.writeData(new NBTTagCompound(), SyncType.SAVE), SyncType.SAVE);
		return this;
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, this.writeData(new NBTTagCompound(), SyncType.SAVE));
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		readData(ByteBufUtils.readTag(buf), SyncType.SAVE);
	}

	@Override
	public boolean canSync(SyncType sync) {
		return SyncType.isGivenType(sync, SyncType.DEFAULT_SYNC, SyncType.SAVE);
	}

	@Override
	public String getTagName() {
		return "container";
	}

	@Override
	public void markChanged(IDirtyPart part) {
		syncParts.markSyncPartChanged(part);
		listener.markChanged(part);
	}

}
