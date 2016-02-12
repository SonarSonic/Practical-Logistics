package sonar.logistics.client.gui;

import java.awt.Color;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import sonar.core.energy.StoredEnergyStack;
import sonar.core.inventory.ContainerSync;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.FontHelper;
import sonar.core.utils.helpers.RenderHelper;
import sonar.logistics.Logistics;
import sonar.logistics.api.IdentifiedCoords;
import sonar.logistics.client.renderers.RenderHandlers;
import sonar.logistics.common.containers.ContainerChannelSelector;
import sonar.logistics.common.containers.ContainerDataReceiver;
import sonar.logistics.common.containers.ContainerEmptySync;
import sonar.logistics.common.handlers.ChannelSelectorHandler;
import sonar.logistics.common.handlers.EnergyReaderHandler;
import sonar.logistics.common.tileentity.TileEntityDataReceiver;
import sonar.logistics.network.packets.PacketCoordsSelection;

public class GuiEnergyReader extends GuiSelectionList<StoredEnergyStack> {

	public TileEntity tile;
	public EnergyReaderHandler handler;

	public GuiEnergyReader(TileEntity tile, EnergyReaderHandler handler, InventoryPlayer inventory) {
		super(new ContainerEmptySync(tile), tile);
		this.tile = tile;
		this.handler = handler;
	}

	@Override
	public List<StoredEnergyStack> getSelectionList() {
		return handler.stacks;
	}

	@Override
	public StoredEnergyStack getCurrentSelection() {
		return null;
	}

	@Override
	public boolean isEqualSelection(StoredEnergyStack selection, StoredEnergyStack current) {
		return selection.equals(current);
	}

	@Override
	public void renderStrings(int x, int y) {
		FontHelper.textCentre(StatCollector.translateToLocal("tile.EnergyReader.name"), xSize, 6, 1);
		FontHelper.textCentre("Select the Energy Info you wish to display", xSize, 18, 0);
	}

	@Override
	public void renderSelection(StoredEnergyStack selection, boolean isSelected, int pos) {
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(RenderHandlers.modelFolder + "progressBar.png"));

		if (selection.capacity != 0) {
			int l = (int) (selection.stored * 224 / selection.capacity);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			drawTexturedModalRect(8, 30 + (12 * pos), 176, 10, l, 10);
		}

		FontHelper.text((selection.type==0?"Storage: ": selection.type==1?"Max Input: ":  selection.type==2?"Max Output: ":  selection.type==3?"Usage: ": "") + String.valueOf(selection.stored) + " / " + Double.toString(selection.capacity), 14, 31 + (12 * pos), isSelected ? Color.GREEN.getRGB() : Color.WHITE.getRGB());
		
	}

	@Override
	public void sendPacket(StoredEnergyStack selection) {
		// Logistics.network.sendToServer(new PacketCoordsSelection(tile.xCoord,
		// tile.yCoord, tile.zCoord, selection));

	}

}
