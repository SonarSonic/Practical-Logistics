package sonar.logistics.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
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
import sonar.core.utils.helpers.FontHelper;
import sonar.core.utils.helpers.RenderHelper;
import sonar.logistics.Logistics;
import sonar.logistics.common.containers.ContainerFluidReader;
import sonar.logistics.common.handlers.FluidReaderHandler;
import sonar.logistics.network.packets.PacketFluidReader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GuiFluidReader extends GuiSonar {

	public int xCoord, yCoord, zCoord;

	public FluidReaderHandler handler;
	public TileEntity tile;

	public GuiFluidReader(FluidReaderHandler handler, TileEntity entity, InventoryPlayer inventoryPlayer) {
		super(new ContainerFluidReader(handler, entity, inventoryPlayer), entity);
		this.xCoord = entity.xCoord;
		this.yCoord = entity.yCoord;
		this.zCoord = entity.zCoord;
		this.handler = handler;
		this.tile = entity;
	}

	public static final ResourceLocation bground = new ResourceLocation("PracticalLogistics:textures/gui/inventoryReader.png");

	private float currentScroll;
	private boolean isScrolling;
	private boolean wasClicking;
	public int scrollerLeft, scrollerStart, scrollerEnd, scrollerWidth;
	public int cycle;

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.mc.thePlayer.openContainer = this.inventorySlots;
		this.xSize = 176 + 72;
		this.ySize = 256;

		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		scrollerLeft = this.guiLeft + 164 + 68;
		scrollerStart = this.guiTop + 31;
		scrollerEnd = scrollerStart + 128;
		scrollerWidth = 10;
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		if (button == 0 || button == 1) {
			if (x - guiLeft >= 13 && x - guiLeft <= 13 + (12 * 18) && y - guiTop >= 32 && y - guiTop <= 32 + (7 * 18)) {
				int start = (int) (stackListSize() / 12 * this.currentScroll);
				int X = (x - guiLeft - 13) / 18;
				int Y = (y - guiTop - 32) / 18;
				int i = (start * 12) + X + ((Y) * 12);

				if (i < getStacks().size()) {
					StoredFluidStack fluidStack = getStacks().get(i);
					if (fluidStack != null && fluidStack.fluid != null) {
						Logistics.network.sendToServer(new PacketFluidReader(tile.xCoord, tile.yCoord, tile.zCoord, fluidStack.fluid));
					}
				}
			}
			if (x - guiLeft >= 13 && x - guiLeft <= 13 + 16 && y - guiTop >= 9 && y - guiTop <= 9 + 16) {

			}
		}
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {

		super.drawGuiContainerForegroundLayer(x, y);
		FontHelper.textCentre(StatCollector.translateToLocal("tile.FluidReader.name"), xSize, 6, 1);
		FontHelper.textCentre("Click the fluid you wish to monitor", xSize, 18, 0);
		if (cycle == 100) {
			cycle = 0;
		} else {
			cycle++;
		}
		if (getStacks() != null) {
			final int br = 16 << 20 | 16 << 4;
			final int var11 = br % 65536;
			final int var12 = br / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var11 * 0.8F, var12 * 0.8F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			int start = (int) (stackListSize() / 12 * this.currentScroll);
			int i = start * 12;
			int finish = Math.min(i + (12 * 7), stackListSize());
			for (int Y = 0; Y < 7; Y++) {
				for (int X = 0; X < 12; X++) {
					if (i < finish) {
						StoredFluidStack fluidStack = getStacks().get(i);
						if (fluidStack != null && fluidStack.fluid != null) {
							Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

							RenderItem.getInstance().renderIcon(13 + (X * 18), 32 + (Y * 18), fluidStack.fluid.getFluid().getIcon(), 16, 16);
							RenderHelper.renderFluidInGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), fluidStack.fluid, fluidStack.stored, 13 + (X * 18), 32 + (Y * 18), null);
						}
					}
					i++;
				}
			}
		}
		if (handler.current != null) {

			final int br = 16 << 20 | 16 << 4;
			final int var11 = br % 65536;
			final int var12 = br / 65536;
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

			RenderItem.getInstance().renderIcon(13, 9, handler.current.getFluid().getIcon(), 16, 16);
		}
		if (x - guiLeft >= 13 && x - guiLeft <= 13 + (12 * 18) && y - guiTop >= 32 && y - guiTop <= 32 + (7 * 18)) {
			int start = (int) (stackListSize() / 12 * this.currentScroll);
			int X = (x - guiLeft - 13) / 18;
			int Y = (y - guiTop - 32) / 18;
			int i = (start * 12) + X + ((Y) * 12);
			if (getStacks() != null) {
				if (i < getStacks().size()) {
					StoredFluidStack storedStack = getStacks().get(i);
					if (storedStack != null && storedStack.fluid != null) {

						GL11.glDisable(GL11.GL_DEPTH_TEST);
						GL11.glDisable(GL11.GL_LIGHTING);
						this.renderToolTip(storedStack, x - guiLeft, y - guiTop);
						GL11.glEnable(GL11.GL_LIGHTING);
						GL11.glEnable(GL11.GL_DEPTH_TEST);
						net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();

					}
				}
			}
		}
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
	protected void renderToolTip(FluidStack storedStack, int x, int y) {
		List list = new ArrayList();
		list.add(storedStack.getLocalizedName());
		drawHoveringText(list, x, y, fontRendererObj);
	}
	protected void renderToolTip(StoredFluidStack storedStack, int x, int y) {
		List list = new ArrayList();
		list.add(storedStack.fluid.getFluid().getLocalizedName(storedStack.fluid));
		if (storedStack.stored != 0) {
			list.add(EnumChatFormatting.GRAY + (String) "Stored: " + storedStack.stored + " mB");
		}
		drawHoveringText(list, x, y, fontRendererObj);
	}

	public void handleMouseInput() {
		super.handleMouseInput();
		float lastScroll = currentScroll;
		int i = Mouse.getEventDWheel();

		if (i != 0 && this.needsScrollBars()) {
			int j = stackListSize() + 1;

			if (i > 0) {
				i = 1;
			}

			if (i < 0) {
				i = -1;
			}

			this.currentScroll = (float) ((double) this.currentScroll - (double) i / (double) j);

			if (this.currentScroll < 0.0F) {
				this.currentScroll = 0.0F;
			}

			if (this.currentScroll > 1.0F) {
				this.currentScroll = 1.0F;
			}
		}

	}

	public void drawScreen(int x, int y, float var) {
		super.drawScreen(x, y, var);
		float lastScroll = currentScroll;
		boolean flag = Mouse.isButtonDown(0);

		if (!this.wasClicking && flag && x >= scrollerLeft && y >= scrollerStart && x < scrollerLeft + scrollerWidth && y < scrollerEnd) {
			this.isScrolling = this.needsScrollBars();
		}

		if (!flag) {
			this.isScrolling = false;
		}

		this.wasClicking = flag;

		if (this.isScrolling) {
			this.currentScroll = ((float) (y - scrollerStart) - 7.5F) / ((float) (scrollerEnd - scrollerStart) - 15.0F);

			if (this.currentScroll < 0.0F) {
				this.currentScroll = 0.0F;
			}

			if (this.currentScroll > 1.0F) {
				this.currentScroll = 1.0F;
			}

		}

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		Minecraft.getMinecraft().getTextureManager().bindTexture(this.getBackground());

		drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		// int pos = getDataPosition();
		// for (int i = 0; i < 11; i++) {
		// drawTexturedModalRect(this.guiLeft + 7, this.guiTop + 29 + (12 * i),
		// 0, positions != null && positions.contains(i) ? 190 : i == pos ? 178
		// : 166, 154 + 72, 12);
		// }
		this.drawTexturedModalRect(scrollerLeft, scrollerStart + (int) ((float) (scrollerEnd - scrollerStart - 17) * this.currentScroll), 176 + 72, 0, 8, 15);

	}

	private boolean needsScrollBars() {
		if (stackListSize() <= (12 * 7))
			return false;

		return true;

	}

	@SideOnly(Side.CLIENT)
	public class NetworkButton extends SonarButtons.ImageButton {

		public NetworkButton(int id, int x, int y) {
			super(id, x, y, bground, 0, 202, 154 + 72, 11);
		}
	}

	@Override
	public ResourceLocation getBackground() {
		return bground;
	}

	public int stackListSize() {
		return getStacks() == null ? 0 : getStacks().size();
	}

	public List<StoredFluidStack> getStacks() {
		return handler.stacks;
	}

}
