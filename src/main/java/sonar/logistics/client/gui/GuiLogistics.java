package sonar.logistics.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import sonar.core.client.gui.GuiSonar;
import sonar.core.client.gui.widgets.SonarScroller;
import sonar.core.helpers.RenderHelper;
import sonar.core.utils.IWorldPosition;
import sonar.logistics.client.LogisticsColours;

public class GuiLogistics extends GuiSonar {

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
	public void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		RenderHelper.saveBlendState();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTransparentRect(this.guiLeft, this.guiTop, this.guiLeft + this.xSize, this.guiTop + this.ySize, LogisticsColours.layers[1].getRGB());
		drawTransparentRect(this.guiLeft + 1, this.guiTop + 1, this.guiLeft + this.xSize - 1, this.guiTop + this.ySize - 1, LogisticsColours.layers[2].getRGB());
		if (scroller != null) {
			int scrollYPos = scroller.start + (int) ((float) (scroller.end - scroller.start - 17) * scroller.getCurrentScroll());
			drawRect(scroller.left, scroller.start, scroller.left + 8, scroller.end - 2, LogisticsColours.layers[1].getRGB());
			drawRect(scroller.left, scrollYPos, scroller.left + 8, scrollYPos + 15, LogisticsColours.layers[2].getRGB());
			drawRect(scroller.left, scrollYPos, scroller.left + 8, scrollYPos + 15, LogisticsColours.layers[2].getRGB());
		}

		// RenderHelper.restoreBlendState();
	}

	public void renderPlayerInventory(int xPos, int yPos) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getMinecraft().getTextureManager().bindTexture(playerInv);
		drawTexturedModalRect(this.guiLeft + xPos, this.guiTop + yPos, 0, 0, this.xSize, this.ySize);
	}

}
