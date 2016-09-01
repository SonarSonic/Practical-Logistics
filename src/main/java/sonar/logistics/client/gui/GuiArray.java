package sonar.logistics.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.RenderHelper;
import sonar.logistics.client.LogisticsColours;
import sonar.logistics.common.containers.ContainerArray;
import sonar.logistics.parts.ArrayPart;

public class GuiArray extends GuiLogistics {
	public static final ResourceLocation bground = new ResourceLocation("PracticalLogistics:textures/gui/transceiverArray.png");

	public ArrayPart part;

	public GuiArray(EntityPlayer player, ArrayPart part) {
		super(new ContainerArray(player, part), part);
		this.part = part;
		this.ySize = 132;
	}

	@Override
	public void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		FontHelper.textCentre(FontHelper.translate("item.Array.name"), xSize, 6, LogisticsColours.white_text.getRGB());
	}

	@Override
	public ResourceLocation getBackground() {
		return bground;
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
		this.renderPlayerInventory(7, 50);
		drawTexturedModalRect(this.guiLeft + 16, this.guiTop + 19, 0, 0, 18 * 8, 18);
		RenderHelper.restoreBlendState();

	}
}
