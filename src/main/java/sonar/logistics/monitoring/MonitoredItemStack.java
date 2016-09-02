package sonar.logistics.monitoring;

import org.lwjgl.opengl.GL11;

import mcmultipart.raytrace.PartMOP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.helpers.RenderHelper;
import sonar.core.network.sync.SyncNBTAbstract;
import sonar.logistics.Logistics;
import sonar.logistics.api.asm.LogicInfoType;
import sonar.logistics.api.display.DisplayType;
import sonar.logistics.api.info.BaseInfo;
import sonar.logistics.api.info.IClickableInfo;
import sonar.logistics.api.info.RenderInfoProperties;
import sonar.logistics.api.info.monitor.IJoinableInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;
import sonar.logistics.helpers.InfoHelper;

@LogicInfoType(id = MonitoredItemStack.id, modid = Logistics.MODID)
public class MonitoredItemStack extends BaseInfo<MonitoredItemStack> implements IJoinableInfo<MonitoredItemStack>, IClickableInfo {

	public static final String id = "item";
	public static LogicMonitorHandler<MonitoredItemStack> handler = LogicMonitorHandler.instance(ItemMonitorHandler.id);
	public final SyncNBTAbstract<StoredItemStack> itemStack = new SyncNBTAbstract<StoredItemStack>(StoredItemStack.class, 0);

	{
		syncParts.add(itemStack);
	}

	public MonitoredItemStack() {
	}

	public MonitoredItemStack(StoredItemStack stack) {
		this.itemStack.setObject(stack);
	}

	@Override
	public boolean isIdenticalInfo(MonitoredItemStack info) {
		return itemStack.getObject().equals(info.itemStack.getObject());
	}

	@Override
	public boolean isMatchingInfo(MonitoredItemStack info) {
		return itemStack.getObject().equalStack(info.itemStack.getObject().item);
	}

	@Override
	public boolean isMatchingType(IMonitorInfo info) {
		return info instanceof MonitoredItemStack;
	}

	@Override
	public LogicMonitorHandler<MonitoredItemStack> getHandler() {
		return handler;
	}

	@Override
	public boolean canJoinInfo(MonitoredItemStack info) {
		return isMatchingInfo(info);
	}

	@Override
	public IJoinableInfo joinInfo(MonitoredItemStack info) {
		itemStack.getObject().add(info.itemStack.getObject());
		return this;
	}

	@Override
	public boolean isValid() {
		return itemStack.getObject() != null && itemStack.getObject().item != null;
	}

	@Override
	public String getID() {
		return id;
	}

	public String toString() {
		if (itemStack.getObject() != null)
			return itemStack.getObject().toString();
		return super.toString() + " : NULL";
	}

	@Override
	public MonitoredItemStack copy() {
		return new MonitoredItemStack(itemStack.getObject().copy());
	}

	@Override
	public void renderInfo(DisplayType displayType, double width, double height, double scale, int infoPos) {
		if (itemStack.getObject() != null) {
			StoredItemStack stack = itemStack.getObject();
			ItemStack item = stack.item;
			GL11.glPushMatrix();
			GL11.glTranslated(-0.5, -0.5, 0.00);
			GL11.glRotated(180, 0, 1, 0);
			GL11.glScaled(-1, 1, 1);
			double actualScale = displayType==DisplayType.LARGE?0.03:scale*2;
			GL11.glScaled(actualScale, actualScale, actualScale);
			double trans = displayType == DisplayType.SMALL ? -8 : 0;
			GL11.glTranslated(-8, trans, 0);
			GlStateManager.disableLighting();
			GlStateManager.enablePolygonOffset();
			GlStateManager.doPolygonOffset(-1, -1);
			GlStateManager.enableCull();
			RenderHelper.renderItemIntoGUI(item, 0, 0);
			GlStateManager.disablePolygonOffset();
			RenderHelper.renderStoredItemStackOverlay(item, 0, 0, 0, "" + stack.stored);
			GL11.glPopMatrix();
		}
	}

	@Override
	public boolean onClicked(RenderInfoProperties renderInfo, EntityPlayer player, EnumHand hand, ItemStack stack, PartMOP hit) {
		if (InfoHelper.canBeClickedStandard(renderInfo, player, hand, stack, hit)) {	
			if(!player.getEntityWorld().isRemote){
				
			}
			return true;
		}
		return false;
	}

}
