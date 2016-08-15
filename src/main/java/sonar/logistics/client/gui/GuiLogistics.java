package sonar.logistics.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import sonar.core.client.gui.GuiSonar;
import sonar.core.client.gui.widgets.SonarScroller;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.RenderHelper;
import sonar.core.utils.IWorldPosition;

public class GuiLogistics extends GuiSonar {

	public static int backgroundColour = FontHelper.getIntFromColor(7, 7, 9);
	public static int grey_base = FontHelper.getIntFromColor(5, 5, 2);
	public static int blue_overlay = FontHelper.getIntFromColor(5, 5, 16);
	public static int category = FontHelper.getIntFromColor(25, 25, 35);
	public static int selectedColour = FontHelper.getIntFromColor(5, 50, 2);
	public static int selectedColour2 = FontHelper.getIntFromColor(50, 5, 2);
	public static int selectedColour3 = FontHelper.getIntFromColor(5, 50, 20);
	public static int selectedColour4 = FontHelper.getIntFromColor(50, 50, 2);

	public static int grey_text = FontHelper.getIntFromColor(105, 105, 116);
	public static int white_text = FontHelper.getIntFromColor(170, 170, 170);
	public static final ResourceLocation playerInv = new ResourceLocation("PracticalLogistics:textures/gui/player_inventory.png");
	public SonarScroller scroller;

	public GuiLogistics(Container container, IWorldPosition entity) {
		super(container, entity);
	}

	@Override
	public ResourceLocation getBackground() {
		return null;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		RenderHelper.saveBlendState();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTransparentRect(this.guiLeft, this.guiTop, this.guiLeft + this.xSize, this.guiTop + this.ySize, grey_base);
		drawTransparentRect(this.guiLeft+1, this.guiTop+1, this.guiLeft + this.xSize-1, this.guiTop + this.ySize-1, blue_overlay);
		if (scroller != null) {
			int scrollYPos = scroller.start + (int) ((float) (scroller.end - scroller.start - 17) * scroller.getCurrentScroll());
			drawRect(scroller.left, scroller.start, scroller.left + 8, scroller.end - 2, grey_base);
			drawRect(scroller.left, scrollYPos, scroller.left + 8, scrollYPos + 15, blue_overlay);
			drawRect(scroller.left, scrollYPos, scroller.left + 8, scrollYPos + 15, blue_overlay);
		}

		//RenderHelper.restoreBlendState();
	}
	
	public void renderPlayerInventory(int xPos, int yPos){
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(playerInv);
		drawTexturedModalRect(this.guiLeft+xPos, this.guiTop+yPos, 0, 0, this.xSize, this.ySize);
	}
	

}
