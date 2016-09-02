package sonar.logistics.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.client.gui.GuiSonar;
import sonar.core.client.gui.SonarButtons.ImageButton;
import sonar.core.utils.IWorldPosition;
import sonar.logistics.api.info.monitor.IMonitorInfo;

public abstract class GuiOLDSelectionList<T> extends GuiSonar {

	public static final ResourceLocation bground = new ResourceLocation("PracticalLogistics:textures/gui/listSelection.png");

	public float currentScroll;
	private boolean isScrolling;
	private boolean wasClicking;
	public int scrollerLeft, scrollerStart, scrollerEnd, scrollerWidth;

	public GuiOLDSelectionList(Container container, IWorldPosition entity) {
		super(container, entity);
	}

	public abstract List<T> getSelectionList();

	public abstract T getCurrentSelection();

	public abstract boolean isEqualSelection(T selection, T current);

	public abstract void renderStrings(int x, int y);

	public abstract void renderSelection(T selection, boolean isSelected, int pos);

	public abstract void sendPacket(T selection);

	public int getSelectionSize() {
		return getSelectionList() == null ? 0 : getSelectionList().size();
	}

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.mc.thePlayer.openContainer = this.inventorySlots;
		this.xSize = 176 + 72;
		this.ySize = 166;

		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		scrollerLeft = this.guiLeft + 164 + 72;
		scrollerStart = this.guiTop + 29;
		scrollerEnd = scrollerStart + 134;
		scrollerWidth = 10;
		int offsetTop = 29;
		if (getViewableSize() == 7) {
			offsetTop = offsetTop + 2;
		}
		for (int i = 0; i < getViewableSize(); i++) {
			this.buttonList.add(new SelectionButton(i, guiLeft + 7, guiTop + offsetTop + (i * getSelectionHeight())));
		}
	}

	public int getDataPosition() {
		if (getSelectionList() == null) {
			return -1;
		}
		if (getCurrentSelection() == null) {
			return -1;
		}
		int start = (int) (getSelectionSize() * this.currentScroll);
		int finish = Math.min(start + getViewableSize(), getSelectionSize());
		for (int i = start; i < finish; i++) {
			if (getSelectionList().get(i) != null) {
				T info = getSelectionList().get(i);
				if (info != null && isEqualSelection(info, getCurrentSelection())) {
					return i - start;
				}
			}
		}
		return -1;
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		this.renderStrings(x, y);
		if (getSelectionList() != null) {
			int start = (int) (getSelectionSize() * this.currentScroll);
			int finish = Math.min(start + this.getViewableSize(), getSelectionSize());
			int pos = this.getDataPosition();
			for (int i = start; i < finish; i++) {
				T selection = getSelectionList().get(i);
				if (selection != null) {
					boolean isSelected = pos == i - start;
					renderSelection(selection, isSelected, i - start);
				}
			}
		}
	}

	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		float lastScroll = currentScroll;
		int i = Mouse.getEventDWheel();

		if (i != 0 && this.needsScrollBars()) {
			int j = getSelectionSize() + 1;

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

	protected void actionPerformed(GuiButton button) {
		if (button != null) {
			if (button.id < getViewableSize()) {
				if (getSelectionList() != null) {
					int start = (int) (getSelectionSize() * this.currentScroll);
					int network = start + button.id;
					if (network < getSelectionSize()) {
						if (getSelectionList().get(network) != null) {
							sendPacket(getSelectionList().get(network));
						}
					}
				}
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		Minecraft.getMinecraft().getTextureManager().bindTexture(this.getBackground());
		drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		drawTexturedModalRect(scrollerLeft, scrollerStart + (int) ((float) (scrollerEnd - scrollerStart - 17) * this.currentScroll), 176 + 72, 0, 8, 15);
		int pos = getDataPosition();
		int offsetTop = 29;
		if (getViewableSize() == 7) {
			offsetTop = offsetTop + 2;
		}
		for (int i = 0; i < getViewableSize(); i++) {
			drawSelectionBackground(offsetTop, i, pos);
		}

	}

	public void drawSelectionBackground(int offsetTop, int i, int pos) {
		drawTexturedModalRect(this.guiLeft + 7, this.guiTop + offsetTop + (getSelectionHeight() * i), 0, i == pos ? 166 + getSelectionHeight() : 166, 154 + 72, getSelectionHeight());
	}

	public int getViewableSize() {
		return 11;
	}

	public int getSelectionHeight() {
		return 12;
	}

	private boolean needsScrollBars() {
		if (getSelectionSize() <= getViewableSize())
			return false;

		return true;

	}

	@SideOnly(Side.CLIENT)
	public class SelectionButton extends ImageButton {

		public SelectionButton(int id, int x, int y) {
			super(id, x, y, getBackground(), 0, 202, 154 + 72, getSelectionHeight());
		}
	}

	@Override
	public ResourceLocation getBackground() {
		return bground;
	}

}
