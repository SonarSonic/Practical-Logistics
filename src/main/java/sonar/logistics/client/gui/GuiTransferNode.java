package sonar.logistics.client.gui;

import net.minecraft.inventory.Container;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.RenderHelper;
import sonar.core.utils.IWorldPosition;
import sonar.logistics.client.LogisticsColours;

public class GuiTransferNode extends GuiLogistics {

	public GuiTransferNode(Container container, IWorldPosition entity) {
		super(container, entity);
	}
	
	@Override
	public void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		FontHelper.textCentre(FontHelper.translate("item.TransferNode.name"), xSize, 6, LogisticsColours.white_text.getRGB());
		
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
		renderPlayerInventory(7, 50);
		drawTexturedModalRect(this.guiLeft + 16, this.guiTop + 19, 0, 0, 18 * 8, 18);
		RenderHelper.restoreBlendState();
	}
}
