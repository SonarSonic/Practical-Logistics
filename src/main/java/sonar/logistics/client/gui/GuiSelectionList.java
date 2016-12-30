package sonar.logistics.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.client.gui.SonarButtons.ImageButton;
import sonar.core.client.gui.widgets.SonarScroller;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.RenderHelper;
import sonar.core.helpers.SonarHelper;
import sonar.core.utils.IWorldPosition;
import sonar.logistics.Logistics;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.MonitorType;
import sonar.logistics.client.LogisticsColours;
import sonar.logistics.client.RenderBlockSelection;
import sonar.logistics.common.containers.ContainerInfoReader;
import sonar.logistics.common.multiparts.InfoReaderPart;
import sonar.logistics.common.multiparts.LogisticsReader;
import sonar.logistics.connections.managers.NetworkManager;
import sonar.logistics.connections.monitoring.MonitoredBlockCoords;
import sonar.logistics.connections.monitoring.MonitoredList;
import sonar.logistics.helpers.InfoRenderer;
import sonar.logistics.network.PacketMonitorType;

public abstract class GuiSelectionList<T> extends GuiLogistics {

	public GuiSelectionList(Container container, IWorldPosition part) {
		super(container, part);
	}
	
	private int size = 11;
	public int start, finish;
	private GuiButton rselectButton;
	public ArrayList<T> infoList = new ArrayList();

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.mc.thePlayer.openContainer = this.inventorySlots;
		this.xSize = 176 + 72;
		this.ySize = 166;
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		scroller = new SonarScroller(this.guiLeft + 164 + 71, this.guiTop + 29, 134, 10);
		for (int i = 0; i < size; i++) {
			this.buttonList.add(new SelectionButton(10 + i, guiLeft + 7, guiTop + 29 + (i * 12)));
		}
	}

	public abstract int getColour(int i, int type);

	public ArrayList<ArrayList<Integer>> getListTypes() {
		ArrayList<Integer> categories = new ArrayList(), data = new ArrayList(), paired = new ArrayList();
		if (infoList != null) {
			for (int i = start; i < finish; i++) {
				T info = infoList.get(i);
				if (info == null)
					continue;
				if (isCategoryHeader(info))
					categories.add(i - start);
				if (isSelectedInfo(info))
					data.add(i - start);
				if (isPairedInfo(info))
					paired.add(i - start);
			}
		}
		return Lists.newArrayList(categories, data, paired);
	}

	public abstract boolean isPairedInfo(T info);

	public abstract boolean isSelectedInfo(T info);

	public abstract boolean isCategoryHeader(T info);

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		setInfo();
		start = (int) (infoSize() * scroller.getCurrentScroll());
		finish = Math.min(start + size, infoSize());
		GL11.glPushMatrix();
		GL11.glScaled(0.75, 0.75, 0.75);
		for (int i = start; i < finish; i++) {
			T info = infoList.get(i);
			if (info != null) {
				int yPos = (int) ((1.0 / 0.75) * (32 + (12 * i) - (12 * start)));
				renderInfo(info, yPos);
			}
		}
		GL11.glPopMatrix();
		super.drawGuiContainerForegroundLayer(x, y);

	}

	public abstract void renderInfo(T info, int yPos);

	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		scroller.handleMouse(needsScrollBars(), infoSize());
	}

	public void drawScreen(int x, int y, float var) {
		super.drawScreen(x, y, var);
		scroller.drawScreen(x, y, needsScrollBars());
	}

	@Override
	public void mouseClicked(int x, int y, int button) throws IOException {
		super.mouseClicked(x, y, button);
		if (button == 1) {
			for (int l = 0; l < this.buttonList.size(); ++l) {
				GuiButton guibutton = (GuiButton) this.buttonList.get(l);
				if (guibutton.mousePressed(this.mc, x, y)) {
					ActionPerformedEvent.Pre event = new ActionPerformedEvent.Pre(this, guibutton, this.buttonList);
					if (MinecraftForge.EVENT_BUS.post(event))
						break;
					this.rselectButton = event.getButton();
					event.getButton().playPressSound(this.mc.getSoundHandler());
					this.buttonPressed(event.getButton(), 1);
					if (this.equals(this.mc.currentScreen))
						MinecraftForge.EVENT_BUS.post(new ActionPerformedEvent.Post(this, event.getButton(), this.buttonList));
				}
			}
		}
	}

	public void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		if (this.rselectButton != null && state == 1) {
			this.rselectButton.mouseReleased(mouseX, mouseY);
			this.rselectButton = null;
		}
	}

	public void buttonPressed(GuiButton button, int buttonID) {
		if (button != null && button.id >= 10) {
			int start = (int) (infoSize() * scroller.getCurrentScroll());
			int network = start + button.id - 10;
			if (network < infoSize()) {
				T info = infoList.get(network);
				if (info != null) {
					selectionPressed(button, buttonID, info);
				}
			}
		}
	}

	public abstract void selectionPressed(GuiButton button, int buttonID, T info);

	public void actionPerformed(GuiButton button) {
		this.buttonPressed(button, 0);
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
		// RenderHelper.saveBlendState();
		int width = 225;
		int height = 12;
		int left = guiLeft + 7;

		ArrayList<ArrayList<Integer>> data = this.getListTypes();
		for (int i = 0; i < size; i++) {
			int top = guiTop + 29 + (height * i);
			if (!data.get(2).contains(i)) {
				int mainColour = data.get(0).contains(i) ? LogisticsColours.category.getRGB() : (data.get(1).contains(i)) ? getColour(i, 0) : LogisticsColours.layers[1].getRGB();
				drawRect(left + 1, top + 1, left - 1 + width, top - 1 + height, mainColour);
				drawRect(left, top, left + width, top + height, LogisticsColours.layers[2].getRGB());
			} else {
				int rgb = getColour(i, 1);
				drawRect(left, top, left + width, top + height, LogisticsColours.layers[2].getRGB());
				drawRect(left + 1, top + 1, left - 1 + width, top - 1 + height, LogisticsColours.grey_base.getRGB());
				this.drawHorizontalLine(left + 1, left - 2 + width, top + 1, rgb);
				this.drawHorizontalLine(left + 1, left - 2 + width, top + 10, rgb);
				this.drawVerticalLine(left + 1, top + 1, top + 10, rgb);
				this.drawVerticalLine(left - 2 + width, top + 1, top + 10, rgb);
				// drawRect(left + 1, top + 1, left - 1 + width, top - 1 + height, getColour(i, 1));
			}
		}

		//drawRect(guiLeft - 20, guiTop + 10, guiLeft, guiTop + 30, LogisticsColours.layers[2].getRGB());
		//drawRect(guiLeft - 19, guiTop + 11, guiLeft - 1, guiTop + 29, LogisticsColours.layers[2].getRGB());
		RenderHelper.restoreBlendState();
	}

	private boolean needsScrollBars() {
		if (infoSize() <= 11)
			return false;
		return true;
	}

	@SideOnly(Side.CLIENT)
	public class SelectionButton extends ImageButton {

		public SelectionButton(int id, int x, int y) {
			super(id, x, y, null, 0, 224, 154 + 72, 11);
		}

		public void drawButton(Minecraft mc, int x, int y) {
		}
	}

	public int infoSize() {
		return infoList == null ? 0 : infoList.size();
	}

	public abstract void setInfo();

	@Override
	public ResourceLocation getBackground() {
		return null;
	}

}
