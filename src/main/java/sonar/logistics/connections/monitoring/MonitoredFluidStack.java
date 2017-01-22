package sonar.logistics.connections.monitoring;

import org.lwjgl.opengl.GL11;

import mcmultipart.raytrace.PartMOP;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fluids.FluidStack;
import sonar.core.api.fluids.StoredFluidStack;
import sonar.core.api.utils.BlockInteractionType;
import sonar.core.helpers.FontHelper;
import sonar.core.network.sync.SyncNBTAbstract;
import sonar.logistics.Logistics;
import sonar.logistics.api.asm.LogicInfoType;
import sonar.logistics.api.display.IDisplayInfo;
import sonar.logistics.api.info.BaseInfo;
import sonar.logistics.api.info.IClickableInfo;
import sonar.logistics.api.info.INameableInfo;
import sonar.logistics.api.info.InfoContainer;
import sonar.logistics.api.info.monitor.IJoinableInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;
import sonar.logistics.helpers.InfoRenderer;

@LogicInfoType(id = MonitoredFluidStack.id, modid = Logistics.MODID)
public class MonitoredFluidStack extends BaseInfo<MonitoredFluidStack> implements IJoinableInfo<MonitoredFluidStack>, IClickableInfo, INameableInfo<MonitoredFluidStack> {

	public static final String id = "fluid";
	public static LogicMonitorHandler<MonitoredFluidStack> handler = LogicMonitorHandler.instance(FluidMonitorHandler.id);
	public SyncNBTAbstract<StoredFluidStack> fluidStack = new SyncNBTAbstract<StoredFluidStack>(StoredFluidStack.class, 0);

	{
		syncParts.addParts(fluidStack);
	}

	public MonitoredFluidStack() {}

	public MonitoredFluidStack(StoredFluidStack stack) {
		this.fluidStack.setObject(stack);
	}

	@Override
	public boolean isIdenticalInfo(MonitoredFluidStack info) {
		return fluidStack.getObject().equals(info.fluidStack.getObject());
	}

	@Override
	public boolean isMatchingInfo(MonitoredFluidStack info) {
		return fluidStack.getObject().equalStack(info.fluidStack.getObject().fluid);
	}

	@Override
	public boolean isMatchingType(IMonitorInfo info) {
		return info instanceof MonitoredFluidStack;
	}

	@Override
	public LogicMonitorHandler<MonitoredFluidStack> getHandler() {
		return handler;
	}

	@Override
	public boolean canJoinInfo(MonitoredFluidStack info) {
		return isMatchingInfo(info);
	}

	@Override
	public IJoinableInfo joinInfo(MonitoredFluidStack info) {
		fluidStack.getObject().add(info.fluidStack.getObject());
		return this;
	}

	@Override
	public boolean isValid() {
		return fluidStack.getObject() != null && fluidStack.getObject().fluid != null;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public MonitoredFluidStack copy() {
		return new MonitoredFluidStack(fluidStack.getObject().copy());
	}

	@Override
	public void renderInfo(InfoContainer container, IDisplayInfo displayInfo, double width, double height, double scale, int infoPos) {
		FluidStack stack = fluidStack.getObject().fluid;
		if (stack != null) {
			GL11.glPushMatrix();
			GL11.glPushMatrix();
			GlStateManager.disableLighting();
			GL11.glTranslated(-1, -0.0625 * 12, +0.004);
			TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(stack.getFluid().getStill().toString());
			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			InfoRenderer.renderProgressBarWithSprite(sprite, width, height, scale, fluidStack.getObject().stored, fluidStack.getObject().capacity);
			GlStateManager.enableLighting();
			GL11.glTranslated(0, 0, -0.001);
			GL11.glPopMatrix();
			InfoRenderer.renderNormalInfo(container.display.getDisplayType(), width, height, scale, displayInfo.getFormattedStrings());
			GL11.glPopMatrix();
		}
	}

	@Override
	public boolean onClicked(BlockInteractionType type, boolean doubleClick, IDisplayInfo renderInfo, EntityPlayer player, EnumHand hand, ItemStack stack, PartMOP hit, InfoContainer container) {
		//FluidHelper.		
		return false;
	}

	@Override
	public String getClientIdentifier() {
		return  (fluidStack.getObject() != null && fluidStack.getObject().fluid != null ? fluidStack.getObject().fluid.getLocalizedName() : "FLUIDSTACK");
	}

	@Override
	public String getClientObject() {
		return fluidStack.getObject() != null ? "" + FontHelper.formatFluidSize(fluidStack.getObject().stored) : "ERROR";
	}

	@Override
	public String getClientType() {
		return "fluid";
	}

}
