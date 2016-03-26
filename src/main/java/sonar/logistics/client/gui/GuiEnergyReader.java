package sonar.logistics.client.gui;

import java.awt.Color;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import sonar.core.helpers.FontHelper;
import sonar.core.helpers.RenderHelper;
import sonar.logistics.Logistics;
import sonar.logistics.client.renderers.RenderHandlers;
import sonar.logistics.common.containers.ContainerEmptySync;
import sonar.logistics.common.handlers.EnergyReaderHandler;
import sonar.logistics.info.types.StoredEnergyInfo;
import sonar.logistics.network.packets.PacketInfoBlock;

public class GuiEnergyReader extends GuiSelectionList<StoredEnergyInfo> {

	public static final ResourceLocation bground = new ResourceLocation("PracticalLogistics:textures/gui/channelSelection.png");

	public TileEntity tile;
	public EnergyReaderHandler handler;

	public GuiEnergyReader(TileEntity tile, EnergyReaderHandler handler, InventoryPlayer inventory) {
		super(new ContainerEmptySync(tile), tile);
		this.tile = tile;
		this.handler = handler;
	}

	@Override
	public List<StoredEnergyInfo> getSelectionList() {
		return handler.stacks;
	}

	@Override
	public StoredEnergyInfo getCurrentSelection() {
		return handler.primaryInfo.getObject();
	}

	@Override
	public boolean isEqualSelection(StoredEnergyInfo selection, StoredEnergyInfo current) {
		return selection.equals(current);
	}

	@Override
	public void renderStrings(int x, int y) {
		FontHelper.textCentre(StatCollector.translateToLocal("tile.EnergyReader.name"), xSize, 6, 1);
		FontHelper.textCentre("Select the Energy Info you wish to display", xSize, 18, 0);
	}

	@Override
	public void renderSelection(StoredEnergyInfo selection, boolean isSelected, int pos) {
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(RenderHandlers.modelFolder + "progressBar.png"));

		if (selection.stack.capacity != 0) {
			int l = (int) (selection.stack.stored * 207 / selection.stack.capacity);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			drawTexturedModalRect(25, 32 + (getSelectionHeight() * pos), 176, 10, l, 16);
		}
		if (selection.coords != null) {
			String string = (selection.coords.block != null ? (selection.coords.block.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips)).get(0).toString() : selection.coords.coordString);

			int offsetTop = 29;
			if (getViewableSize() == 7) {
				offsetTop = offsetTop + 2;
			}
			FontHelper.text(string + ": " + selection.stack.stored + " " + selection.stack.energyType.getStorageSuffix(), 28, offsetTop + 5 + (getSelectionHeight() * pos), Color.WHITE.getRGB());
			if (selection.coords.block != null) {
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				RenderItem.getInstance().renderItemAndEffectIntoGUI(fontRendererObj, this.mc.getTextureManager(), selection.coords.block, 8, offsetTop + 1 + (getSelectionHeight() * pos));
				RenderHelper.renderStoredItemStackOverlay(this.fontRendererObj, this.mc.getTextureManager(), selection.coords.block, 0, 8, offsetTop + 1 + (getSelectionHeight() * pos), null);
			}
		}

	}

	public void drawSelectionBackground(int offsetTop, int i, int pos) {
		drawTexturedModalRect(this.guiLeft + 7, this.guiTop + offsetTop + (getSelectionHeight() * i), 0, i == pos ? 166 + getSelectionHeight() : 166, 154 + 72, getSelectionHeight());		// }else{
		drawTexturedModalRect(this.guiLeft + 7, this.guiTop + offsetTop + (getSelectionHeight() * i), 0, 166, 154 + 72, getSelectionHeight());
		if (i == pos)
			drawTexturedModalRect(this.guiLeft + 7, this.guiTop + offsetTop + (getSelectionHeight() * i), 0, 166 + getSelectionHeight(), 18, getSelectionHeight());

		// }
	}

	@Override
	public void sendPacket(StoredEnergyInfo selection) {
		Logistics.network.sendToServer(new PacketInfoBlock(tile.xCoord, tile.yCoord, tile.zCoord, selection, true));
		handler.primaryInfo.setObject(selection);
	}

	@Override
	public ResourceLocation getBackground() {
		return bground;
	}

	public int getViewableSize() {
		return 7;
	}

	public int getSelectionHeight() {
		return 18;
	}
}
