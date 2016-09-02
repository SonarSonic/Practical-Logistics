package sonar.logistics.client.gui;
/*
import java.awt.Color;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.InventoryPlayer;
import sonar.core.SonarCore;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.FontHelper;
import sonar.core.network.PacketByteBuf;
import sonar.logistics.api.utils.ExternalCoords;
import sonar.logistics.common.containers.ContainerDataReceiver;
import sonar.logistics.common.tileentity.TileEntityDataReceiver;
import sonar.logistics.monitoring.MonitoredBlockCoords;

public class GuiOLDDataReceiver extends GuiOLDSelectionList<MonitoredBlockCoords> {

	public TileEntityDataReceiver tile;

	public GuiOLDDataReceiver(InventoryPlayer inventory, TileEntityDataReceiver tile) {
		super(new ContainerDataReceiver(tile, inventory), tile);
		this.tile = tile;
	}

	@Override
	public List<MonitoredBlockCoords> getSelectionList() {
		return tile.emitters;
	}

	@Override
	public MonitoredBlockCoords getCurrentSelection() {
		ExternalCoords channel = tile.getChannel();
		if (channel != null) {
			return tile.getChannel().getIdentifiedCoords();
		}
		return null;
	}

	@Override
	public boolean isEqualSelection(MonitoredBlockCoords selection, MonitoredBlockCoords current) {
		return BlockCoords.equalCoords(selection.blockCoords, current.blockCoords);
	}

	@Override
	public void renderStrings(int x, int y) {
		FontHelper.textCentre(FontHelper.translate("tile.DataReceiver.name"), xSize, 6, 1);
		FontHelper.textCentre("Select the emitter you wish to connect to", xSize, 18, 0);
	}

	@Override
	public void renderSelection(MonitoredBlockCoords selection, boolean isSelected, int pos) {
		FontHelper.text(selection.coordString, 10, 31 + (12 * pos), isSelected ? Color.GREEN.getRGB() : Color.WHITE.getRGB());
		GL11.glPushMatrix();
		GL11.glScaled(0.75, 0.75, 0.75);
		FontHelper.text(selection.blockCoords.toString(), 174, 43 + (16 * pos), isSelected ? Color.GREEN.getRGB() : Color.WHITE.getRGB());
		GL11.glPopMatrix();

	}

	@Override
	public void sendPacket(MonitoredBlockCoords selection) {
		tile.selectedColour = selection;
		SonarCore.network.sendToServer(new PacketByteBuf(tile, tile.getPos(), 0));

	}

}
*/