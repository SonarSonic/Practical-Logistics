package sonar.logistics.client.gui;

import java.awt.Color;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.FontHelper;
import sonar.logistics.Logistics;
import sonar.logistics.api.IdentifiedCoords;
import sonar.logistics.common.containers.ContainerDataReceiver;
import sonar.logistics.common.tileentity.TileEntityDataReceiver;
import sonar.logistics.network.packets.PacketCoordsSelection;

public class GuiDataReceiver extends GuiSelectionList<IdentifiedCoords> {

	public TileEntityDataReceiver tile;

	public GuiDataReceiver(InventoryPlayer inventory, TileEntityDataReceiver tile) {
		super(new ContainerDataReceiver(tile, inventory), tile);
		this.tile = tile;
	}

	@Override
	public List<IdentifiedCoords> getSelectionList() {
		return tile.emitters;
	}

	@Override
	public IdentifiedCoords getCurrentSelection() {
		return tile.getChannel();
	}

	@Override
	public boolean isEqualSelection(IdentifiedCoords selection, IdentifiedCoords current) {
		return BlockCoords.equalCoords(selection.blockCoords, current.blockCoords);
	}

	@Override
	public void renderStrings(int x, int y) {
		FontHelper.textCentre(StatCollector.translateToLocal("tile.DataReceiver.name"), xSize, 6, 1);
		FontHelper.textCentre("Select the emitter you wish to connect to", xSize, 18, 0);
	}

	@Override
	public void renderSelection(IdentifiedCoords selection, boolean isSelected, int pos) {
		FontHelper.text(selection.suffix, 10, 31 + (12 * pos), isSelected ? Color.GREEN.getRGB() : Color.WHITE.getRGB());
		GL11.glPushMatrix();
		GL11.glScaled(0.75, 0.75, 0.75);
		FontHelper.text(selection.blockCoords.getRender(), 174, 43 + (16 * pos), isSelected ? Color.GREEN.getRGB() : Color.WHITE.getRGB());
		GL11.glPopMatrix();

	}

	@Override
	public void sendPacket(IdentifiedCoords selection) {
		Logistics.network.sendToServer(new PacketCoordsSelection(tile.xCoord, tile.yCoord, tile.zCoord, selection));

	}

}
