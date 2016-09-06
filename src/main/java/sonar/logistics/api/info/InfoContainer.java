package sonar.logistics.api.info;

import java.util.ArrayList;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.api.utils.BlockInteractionType;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncNBTAbstract;
import sonar.logistics.Logistics;
import sonar.logistics.api.display.DisplayInfo;
import sonar.logistics.api.display.DisplayType;
import sonar.logistics.api.display.IDisplayInfo;
import sonar.logistics.api.display.IInfoDisplay;
import sonar.logistics.api.display.ScreenLayout;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.helpers.InfoHelper;

/** the typical implementation */
public class InfoContainer implements IInfoContainer, INBTSyncable {

	public static final ResourceLocation colour1 = new ResourceLocation(Logistics.MODID + ":textures/model/" + "progress1.png");
	public static final ResourceLocation colour2 = new ResourceLocation(Logistics.MODID + ":textures/model/" + "progress2.png");
	public static final ResourceLocation colour3 = new ResourceLocation(Logistics.MODID + ":textures/model/" + "progress3.png");
	public static final ResourceLocation colour4 = new ResourceLocation(Logistics.MODID + ":textures/model/" + "progress4.png");
	public final ArrayList<SyncNBTAbstract<DisplayInfo>> storedInfo = new ArrayList();
	public final IInfoDisplay display;
	public ArrayList<ISyncPart> syncParts = new ArrayList<ISyncPart>();
	public long lastClickTime;
	public UUID lastClickUUID;
	
	public InfoContainer(IInfoDisplay display) {
		this.display = display;
		for (int i = 0; i < display.maxInfo(); i++) {
			SyncNBTAbstract<DisplayInfo> syncPart = new SyncNBTAbstract<DisplayInfo>(DisplayInfo.class, i);
			syncPart.setObject(new DisplayInfo());
			storedInfo.add(syncPart);
		}
		syncParts.addAll(storedInfo);
		resetRenderProperties();
	}

	public void resetRenderProperties() {
		for (int i = 0; i < storedInfo.size(); i++) {
			DisplayInfo info = storedInfo.get(i).getObject();
			double[] scaling = InfoHelper.getScaling(display.getDisplayType(), display.getLayout(), i);
			double[] translation = InfoHelper.getTranslation(display.getDisplayType(), display.getLayout(), i);
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

	@Override
	public InfoUUID getInfoUUID(int pos) {
		return storedInfo.get(pos).getObject().getInfoUUID();
	}

	@Override
	public void setUUID(InfoUUID id, int pos) {
		storedInfo.get(pos).getObject().setUUID(id);
	}

	@Override
	public void renderContainer() {
		if (display.getDisplayType() == DisplayType.LARGE) {
			GL11.glTranslated(0, -0.0625 * 4, 0);
		}
		ScreenLayout layout = display.getLayout();
		DisplayType type = display.getDisplayType();
		for (int pos = 0; pos < layout.maxInfo; pos++) {
			IDisplayInfo info = storedInfo.get(pos).getObject();
			IMonitorInfo toDisplay = info.getCachedInfo() == null ? InfoError.noData : info.getCachedInfo();
			GL11.glPushMatrix();
			double[] translation = info.getRenderProperties().translation;
			double[] scaling = info.getRenderProperties().scaling;
			GL11.glTranslated(translation[0], translation[1], translation[2]);
			toDisplay.renderInfo(type, scaling[0], scaling[1], scaling[2], pos);
			GL11.glPopMatrix();
		}
	}

	@Override
	public boolean onClicked(BlockInteractionType type, World world, EntityPlayer player, EnumHand hand, ItemStack stack, PartMOP hit) {
		boolean doubleClick = false;
		if (world.getTotalWorldTime() - lastClickTime < 10 && player.getPersistentID().equals(lastClickUUID)) {
			doubleClick = true;
		}
		lastClickTime = world.getTotalWorldTime();
		lastClickUUID = player.getPersistentID();
		for (int i = 0; i < display.maxInfo(); i++) {
			IDisplayInfo info = storedInfo.get(i).getObject();
			IMonitorInfo cachedInfo = info.getCachedInfo();
			if (cachedInfo instanceof IClickableInfo) {
				boolean clicked = ((IClickableInfo) cachedInfo).onClicked(type, doubleClick, info.getRenderProperties(), player, hand, stack, hit);
				if (clicked) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		NBTHelper.readSyncParts(nbt.getCompoundTag("parts"), type, syncParts);
		resetRenderProperties();
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		nbt.setTag("parts", NBTHelper.writeSyncParts(new NBTTagCompound(), type, syncParts, type == SyncType.SYNC_OVERRIDE));
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

}
