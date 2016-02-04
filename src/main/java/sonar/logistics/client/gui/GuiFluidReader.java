package sonar.logistics.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import sonar.core.fluid.StoredFluidStack;
import sonar.core.inventory.GuiSonar;
import sonar.core.inventory.SonarButtons;
import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.helpers.FontHelper;
import sonar.core.utils.helpers.RenderHelper;
import sonar.logistics.Logistics;
import sonar.logistics.common.containers.ContainerFluidReader;
import sonar.logistics.common.containers.ContainerInventoryReader;
import sonar.logistics.common.handlers.FluidReaderHandler;
import sonar.logistics.common.handlers.InventoryReaderHandler;
import sonar.logistics.network.packets.PacketFluidReader;
import sonar.logistics.network.packets.PacketInventoryReader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GuiFluidReader extends GuiSelectionGrid<StoredFluidStack> {

	public FluidReaderHandler handler;

	public GuiFluidReader(FluidReaderHandler handler, TileEntity entity, InventoryPlayer inventoryPlayer) {
		super(new ContainerFluidReader(handler, entity, inventoryPlayer), entity);
		this.handler = handler;
	}

	@Override
	public List<StoredFluidStack> getGridList() {
		return handler.stacks;
	}

	@Override
	public void onGridClicked(StoredFluidStack selection, int pos) {
		if (selection.fluid != null) {
			Logistics.network.sendToServer(new PacketFluidReader(tile.xCoord, tile.yCoord, tile.zCoord, selection.fluid));
		}
	}

	@Override
	public void renderStrings(int x, int y) {
		FontHelper.textCentre(StatCollector.translateToLocal("tile.FluidReader.name"), xSize, 6, 1);
		FontHelper.textCentre("Click the fluid you wish to monitor", xSize, 18, 0);
	}

	@Override
	public void renderSelection(StoredFluidStack selection, int x, int y) {
		if (selection.fluid != null) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
			RenderItem.getInstance().renderIcon(13 + (x * 18), 32 + (y * 18), selection.fluid.getFluid().getIcon(), 16, 16);
		}
	}

	@Override
	public void renderToolTip(StoredFluidStack selection, int x, int y) {
		List list = new ArrayList();
		list.add(selection.fluid.getFluid().getLocalizedName(selection.fluid));
		if (selection.stored != 0) {
			list.add(EnumChatFormatting.GRAY + (String) "Stored: " + selection.stored + " mB");
		}
		drawHoveringText(list, x, y, fontRendererObj);
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		if (x - guiLeft >= 13 && x - guiLeft <= 13 + 16 && y - guiTop >= 9 && y - guiTop <= 9 + 16) {
			FluidStack storedStack = handler.current;
			if (storedStack != null) {
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_LIGHTING);
				this.renderToolTip(storedStack, x - guiLeft, y - guiTop);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();

			}
		}
	}

	public void preRender() {
		if (getGridList() != null) {
			final int br = 16 << 20 | 16 << 4;
			final int var11 = br % 65536;
			final int var12 = br / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var11 * 0.8F, var12 * 0.8F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	public void postRender() {
		if (handler.current != null) {
			final int br = 16 << 20 | 16 << 4;
			final int var11 = br % 65536;
			final int var12 = br / 65536;
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

			RenderItem.getInstance().renderIcon(13, 9, handler.current.getFluid().getIcon(), 16, 16);
		}

	}

	protected void renderToolTip(FluidStack storedStack, int x, int y) {
		List list = new ArrayList();
		list.add(storedStack.getLocalizedName());
		drawHoveringText(list, x, y, fontRendererObj);
	}
}
