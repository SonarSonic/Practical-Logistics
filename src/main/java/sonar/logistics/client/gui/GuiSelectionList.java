package sonar.logistics.client.gui;

import java.awt.Color;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import sonar.core.inventory.GuiSonar;
import sonar.core.inventory.SonarButtons;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.FontHelper;
import sonar.logistics.Logistics;
import sonar.logistics.api.IdentifiedCoords;
import sonar.logistics.common.containers.ContainerDataReceiver;
import sonar.logistics.common.tileentity.TileEntityDataReceiver;
import sonar.logistics.network.packets.PacketCoordsSelection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class GuiSelectionList<T> extends GuiSonar {

	public static final ResourceLocation bground = new ResourceLocation("PracticalLogistics:textures/gui/dataReceiver.png");

	private float currentScroll;
	private boolean isScrolling;
	private boolean wasClicking;
	public int scrollerLeft, scrollerStart, scrollerEnd, scrollerWidth;

	public GuiSelectionList(Container container, TileEntity entity) {
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
		for (int i = 0; i < 11; i++) {
			this.buttonList.add(new SelectionButton(10 + i, guiLeft + 7, guiTop + 29 + (i * 12)));
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
		int finish = Math.min(start + 11, getSelectionSize());
		for (int i = start; i < finish; i++) {
			if (getSelectionList().get(i) != null) {
				T info = getSelectionList().get(i);
				if (isEqualSelection(info, getCurrentSelection())) {
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
			int finish = Math.min(start + 11, getSelectionSize());
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

	public void handleMouseInput() {
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
			if (button.id >= 10) {
				if (getSelectionList() != null) {
					int start = (int) (getSelectionSize() * this.currentScroll);
					int network = start + button.id - 10;
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
		for (int i = 0; i < 11; i++) {
			drawTexturedModalRect(this.guiLeft + 7, this.guiTop + 29 + (12 * i), 0, i == pos ? 178 : 166, 154 + 72, 12);
		}

	}

	private boolean needsScrollBars() {
		if (getSelectionSize() <= 11)
			return false;

		return true;

	}

	@SideOnly(Side.CLIENT)
	public class SelectionButton extends SonarButtons.ImageButton {

		public SelectionButton(int id, int x, int y) {
			super(id, x, y, getBackground(), 0, 202, 154 + 72, 11);
		}
	}

	@Override
	public ResourceLocation getBackground() {
		return bground;
	}

}
