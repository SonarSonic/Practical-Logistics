package sonar.logistics.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import sonar.core.helpers.FontHelper;
import sonar.core.inventory.GuiSonar;
import sonar.logistics.common.containers.ContainerArray;
import sonar.logistics.common.handlers.ArrayHandler;

public class GuiArray extends GuiSonar {
	public static final ResourceLocation bground = new ResourceLocation("PracticalLogistics:textures/gui/transceiverArray.png");

	public ArrayHandler handler;

	public GuiArray(InventoryPlayer player, ArrayHandler handler, TileEntity tile) {
		super(new ContainerArray(player, handler, tile), tile);
		this.handler = handler;
		this.ySize=132;
	}

	@Override
	public void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		FontHelper.textCentre(FontHelper.translate("tile.TransceiverArray.name"), xSize, 6, 0);
	}

	@Override
	public ResourceLocation getBackground() {
		return bground;
	}

}
