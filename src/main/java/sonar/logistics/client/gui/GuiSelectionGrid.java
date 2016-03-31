package sonar.logistics.client.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import sonar.core.inventory.GuiSonar;

public abstract class GuiSelectionGrid<T> extends GuiSonar {

	public static final ResourceLocation bground = new ResourceLocation("PracticalLogistics:textures/gui/gridSelection.png");
	public static final ResourceLocation sorting_icons = new ResourceLocation("PracticalLogistics:textures/gui/sorting_icons.png");

	public TileEntity tile;
	public int xCoord, yCoord, zCoord;

	private float currentScroll;
	private boolean isScrolling;
	private boolean wasClicking;
	private int scrollerLeft, scrollerStart, scrollerEnd, scrollerWidth;

	public abstract void onGridClicked(T selection, int pos, int button, boolean empty);

	public abstract void renderSelection(T selection, int x, int y);

	public abstract void renderStrings(int x, int y);

	public abstract void renderToolTip(T selection, int x, int y);

	public abstract List<T> getGridList();

	public int getGridSize() {
		return getGridList() == null ? 0 : getGridList().size();
	}

	private boolean needsScrollBars() {
		if (getGridSize() <= (12 * 7))
			return false;
		return true;
	}

	public GuiSelectionGrid(Container container, TileEntity entity) {
		super(container, entity);
		this.xCoord = entity.xCoord;
		this.yCoord = entity.yCoord;
		this.zCoord = entity.zCoord;
		this.tile = entity;
	}

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
				int start = (int) (getGridSize() / 12 * this.currentScroll);
				int X = (x - guiLeft - 13) / 18;
				int Y = (y - guiTop - 32) / 18;
				int i = (start * 12) + (12 * Y) + X;
				if (i < getGridList().size()) {
					T storedStack = getGridList().get(i);
					if (storedStack != null) {
						onGridClicked(storedStack, i, button, false);
						return;
					}
				}
				onGridClicked(null, i, button, true);
			}
		}
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
		renderStrings(x, y);
		preRender();
		List<T> list = getGridList();
		if (list != null) {
			int start = (int) (getGridSize() / 12 * this.currentScroll);
			int i = start * 12;
			int finish = Math.min(i + (12 * 7), getGridSize());
			for (int Y = 0; Y < 7; Y++) {
				for (int X = 0; X < 12; X++) {
					if (i < finish) {
						T selection = list.get(i);
						if (selection != null) {
							renderSelection(selection, X, Y);
						}
					}
					i++;
				}
			}
		}
		postRender();
		if (x - guiLeft >= 13 && x - guiLeft <= 13 + (12 * 18) && y - guiTop >= 32 && y - guiTop <= 32 + (7 * 18)) {
			int start = (int) (getGridSize() / 12 * this.currentScroll);
			int X = (x - guiLeft - 13) / 18;
			int Y = (y - guiTop - 32) / 18;
			int i = (start * 12) + X + ((Y) * 12);

			if (list != null) {
				if (i < list.size()) {
					T selection = list.get(i);
					if (selection != null) {

						GL11.glDisable(GL11.GL_DEPTH_TEST);
						GL11.glDisable(GL11.GL_LIGHTING);
						this.renderToolTip(selection, x - guiLeft, y - guiTop);
						GL11.glEnable(GL11.GL_LIGHTING);
						GL11.glEnable(GL11.GL_DEPTH_TEST);
						net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();

					}
				}
			}
		}
		super.drawGuiContainerForegroundLayer(x, y);
	}

	public void preRender() {
	}

	public void postRender() {
	}

	public void handleMouseInput() {
		super.handleMouseInput();
		float lastScroll = currentScroll;
		int i = Mouse.getEventDWheel();

		if (i != 0 && this.needsScrollBars()) {
			int j = getGridSize() + 1;

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
		this.drawTexturedModalRect(scrollerLeft, scrollerStart + (int) ((float) (scrollerEnd - scrollerStart - 17) * this.currentScroll), 176 + 72, 0, 8, 15);

	}

	@Override
	public ResourceLocation getBackground() {
		return bground;
	}

}
