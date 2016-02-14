package sonar.logistics.client.gui;

import java.awt.Color;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.FontHelper;
import sonar.core.utils.helpers.RenderHelper;
import sonar.logistics.Logistics;
import sonar.logistics.api.IdentifiedCoords;
import sonar.logistics.common.containers.ContainerDataReceiver;
import sonar.logistics.common.containers.ContainerEmptySync;
import sonar.logistics.common.handlers.ChannelSelectorHandler;
import sonar.logistics.common.tileentity.TileEntityDataReceiver;
import sonar.logistics.network.packets.PacketCoordsSelection;

public class GuiChannelSelector extends GuiSelectionList<IdentifiedCoords> {

	public static final ResourceLocation bground = new ResourceLocation("PracticalLogistics:textures/gui/channelSelection.png");

	public TileEntity tile;
	public ChannelSelectorHandler handler;

	public GuiChannelSelector(TileEntity tile, ChannelSelectorHandler handler, InventoryPlayer inventory) {
		super(new ContainerEmptySync(handler, tile), tile);
		this.tile = tile;
		this.handler = handler;
	}

	@Override
	public List<IdentifiedCoords> getSelectionList() {
		return handler.channels;
	}

	@Override
	public IdentifiedCoords getCurrentSelection() {
		return handler.channel.getCoords();
	}

	@Override
	public boolean isEqualSelection(IdentifiedCoords selection, IdentifiedCoords current) {
		return BlockCoords.equalCoords(selection.blockCoords, current.blockCoords);
	}

	@Override
	public void renderStrings(int x, int y) {
		FontHelper.textCentre(StatCollector.translateToLocal("tile.ChannelSelector.name"), xSize, 6, 1);
		FontHelper.textCentre("Select the channel you wish to default to", xSize, 18, 0);
	}

	@Override
	public void renderSelection(IdentifiedCoords selection, boolean isSelected, int pos) {
		String string = (selection.block != null ? (selection.block.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips)).get(0).toString() : selection.suffix);

		int offsetTop = 29;
		if (getViewableSize() == 7) {
			offsetTop = offsetTop + 2;
		}
		FontHelper.text(string.substring(0, Math.min(17, string.length())), 28, offsetTop + 5 + (getSelectionHeight() * pos), Color.WHITE.getRGB());
		GL11.glPushMatrix();
		GL11.glScaled(0.75, 0.75, 0.75);
		FontHelper.text(selection.blockCoords.getRender(), 174, offsetTop + 18 + ((getSelectionHeight() + 6) * pos), Color.WHITE.getRGB());
		GL11.glPopMatrix();

		if (selection.block != null) {
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			RenderItem.getInstance().renderItemAndEffectIntoGUI(fontRendererObj, this.mc.getTextureManager(), selection.block, 8, offsetTop + 1 + (getSelectionHeight() * pos));
			RenderHelper.renderStoredItemStackOverlay(this.fontRendererObj, this.mc.getTextureManager(), selection.block, 0, 8, offsetTop + 1 + (getSelectionHeight() * pos), null);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		}
	}

	@Override
	public void sendPacket(IdentifiedCoords selection) {
		Logistics.network.sendToServer(new PacketCoordsSelection(tile.xCoord, tile.yCoord, tile.zCoord, selection));
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
