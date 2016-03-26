package sonar.logistics.client.gui;

import java.awt.Color;
import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import sonar.core.api.BlockCoords;
import sonar.core.helpers.FontHelper;
import sonar.logistics.Logistics;
import sonar.logistics.api.utils.ExternalCoords;
import sonar.logistics.api.utils.IdentifiedCoords;
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
		ExternalCoords channel = tile.getChannel();
		if (channel != null) {
			return tile.getChannel().getIdentifiedCoords();
		}
		return null;
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
		FontHelper.text(selection.coordString, 10, 31 + (12 * pos), isSelected ? Color.GREEN.getRGB() : Color.WHITE.getRGB());
		GL11.glPushMatrix();
		GL11.glScaled(0.75, 0.75, 0.75);
		FontHelper.text(selection.blockCoords.toString(), 174, 43 + (16 * pos), isSelected ? Color.GREEN.getRGB() : Color.WHITE.getRGB());
		GL11.glPopMatrix();

	}

	@Override
	public void sendPacket(IdentifiedCoords selection) {
		Logistics.network.sendToServer(new PacketCoordsSelection(tile.xCoord, tile.yCoord, tile.zCoord, new ExternalCoords(selection, ForgeDirection.getOrientation(selection.blockCoords.getTileEntity().getBlockMetadata()))));

	}

}
