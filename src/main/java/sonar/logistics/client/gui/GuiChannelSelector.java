package sonar.logistics.client.gui;

import java.awt.Color;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.FontHelper;
import sonar.logistics.Logistics;
import sonar.logistics.api.IdentifiedCoords;
import sonar.logistics.common.containers.ContainerChannelSelector;
import sonar.logistics.common.containers.ContainerDataReceiver;
import sonar.logistics.common.handlers.ChannelSelectorHandler;
import sonar.logistics.common.tileentity.TileEntityDataReceiver;
import sonar.logistics.network.packets.PacketCoordsSelection;

public class GuiChannelSelector extends GuiSelectionList<IdentifiedCoords> {

	public TileEntity tile;
	public ChannelSelectorHandler handler;

	public GuiChannelSelector(TileEntity tile, ChannelSelectorHandler handler, InventoryPlayer inventory) {
		super(new ContainerChannelSelector(tile, handler, inventory), tile);
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
		return BlockCoords.equalCoords(selection.coords, current.coords);
	}

	@Override
	public void renderStrings(int x, int y) {
		FontHelper.textCentre(StatCollector.translateToLocal("tile.ChannelSelector.name"), xSize, 6, 1);
		FontHelper.textCentre("Select the channel you wish to default to", xSize, 18, 0);
	}

	@Override
	public void renderSelection(IdentifiedCoords selection, boolean isSelected, int pos) {
		FontHelper.text(selection.name.substring(0, Math.min(22, selection.name.length())), 10, 31 + (12 * pos), isSelected ? Color.GREEN.getRGB() : Color.WHITE.getRGB());
		GL11.glPushMatrix();
		GL11.glScaled(0.75, 0.75, 0.75);
		FontHelper.text(selection.coords.getRender(), 174, 43 + (16 * pos), isSelected ? Color.GREEN.getRGB() : Color.WHITE.getRGB());
		GL11.glPopMatrix();

	}

	@Override
	public void sendPacket(IdentifiedCoords selection) {
		Logistics.network.sendToServer(new PacketCoordsSelection(tile.xCoord, tile.yCoord, tile.zCoord, selection));

	}

}
