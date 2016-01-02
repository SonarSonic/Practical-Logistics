package sonar.logistics.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import sonar.core.inventory.GuiSonar;
import sonar.core.utils.helpers.FontHelper;
import sonar.logistics.common.containers.ContainerHammer;
import sonar.logistics.common.tileentity.TileEntityHammer;

public class GuiHammer extends GuiSonar {
	public TileEntityHammer entity;

	public static final ResourceLocation bground = new ResourceLocation("PracticalLogistics:textures/gui/hammer.png");

	public GuiHammer(InventoryPlayer inventoryPlayer, TileEntityHammer entity) {
		super(new ContainerHammer(inventoryPlayer, entity), entity);

		this.entity = entity;

		this.xSize = 176;
		this.ySize = 143;
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		FontHelper.textCentre(StatCollector.translateToLocal("tile.Hammer.name"), xSize, 6, 0);
	}
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);;
		int l = this.entity.progress.getInt() * 23 / this.entity.speed;
		drawTexturedModalRect(this.guiLeft + 76, this.guiTop + 24, 176, 0, l, 16);
	}

	@Override
	public ResourceLocation getBackground() {
		return bground;
	}
}
