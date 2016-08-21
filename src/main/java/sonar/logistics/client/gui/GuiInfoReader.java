package sonar.logistics.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
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
import sonar.logistics.Logistics;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.MonitorType;
import sonar.logistics.client.LogisticsColours;
import sonar.logistics.client.RenderBlockSelection;
import sonar.logistics.common.containers.ContainerInfoReader;
import sonar.logistics.connections.CacheRegistry;
import sonar.logistics.connections.MonitoredList;
import sonar.logistics.helpers.InfoRenderer;
import sonar.logistics.monitoring.MonitoredBlockCoords;
import sonar.logistics.network.PacketMonitorType;
import sonar.logistics.parts.InfoReaderPart;
import sonar.logistics.parts.LogicReaderPart;
import sonar.logistics.parts.MonitorMultipart;

public class GuiInfoReader extends GuiLogistics {

	public BlockPos pos;
	public LogicReaderPart part;

	public GuiInfoReader(EntityPlayer player, InfoReaderPart part) {
		super(new ContainerInfoReader(player, part), part);
		this.pos = part.getPos();
		this.part = part;
	}

	private int size = 11;
	public int start, finish;
	private GuiButton rselectButton;
	public ArrayList<IMonitorInfo> infoList = new ArrayList();
	public ArrayList<IMonitorInfo> coords = new ArrayList();
	public GuiState state = GuiState.INFO;

	public enum GuiState {
		INFO(MonitorType.INFO), CHANNEL(MonitorType.CHANNEL);

		public String getButtonString() {
			return this == INFO ? "Configure Channel" : "Configure Data";
		}

		public MonitorType type;

		GuiState(MonitorType type) {
			this.type = type;
		}
	}

	public ItemStack getButtonStack() {
		return state == GuiState.INFO ? new ItemStack(LogisticsItems.partNode, 1) : part.getItemStack();
	}

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

	public int getColour(int i, int type) {
		switch (state) {
		case CHANNEL:
			// MonitoredBlockCoords coordIndo = (MonitoredBlockCoords) coords.get(i + start);
			return LogisticsColours.getDefaultSelection().getRGB();
		case INFO:
			IMonitorInfo info = infoList.get(i + start);
			if (info == null || info.isHeader()) {
				return LogisticsColours.layers[1].getRGB();
			}
			ArrayList<IMonitorInfo> selectedInfo = type == 0 ? part.getSelectedInfo() : part.getPairedInfo();
			int pos = 0;
			for (IMonitorInfo selected : selectedInfo) {
				if (selected != null && !selected.isHeader() && info.isMatchingType(selected) && info.isMatchingInfo(selected)) {
					return LogisticsColours.infoColours[pos].getRGB();
				}
				pos++;
			}
			return LogisticsColours.layers[1].getRGB();
		default:
			return LogisticsColours.layers[1].getRGB();

		}
	}

	public ArrayList<Integer> getPairedPositions() {
		ArrayList<Integer> pos = new ArrayList();
		if (state == GuiState.INFO) {
			ArrayList<IMonitorInfo> pairedInfo = part.getPairedInfo();
			if (getCurrentList() == null || (pairedInfo.isEmpty())) {
				return pos;
			}
			for (int i = start; i < finish; i++) {
				IMonitorInfo info = getCurrentList().get(i);
				if (info != null && !info.isHeader()) {
					for (IMonitorInfo selected : pairedInfo) {
						if (selected != null && !selected.isHeader() && info.isMatchingType(selected) && info.isMatchingInfo(selected)) {
							pos.add(i - start);
							break;
						}
					}
				}
			}
		}
		return pos;
	}

	public ArrayList<Integer> getDataPositions() {
		ArrayList<Integer> pos = new ArrayList();
		ArrayList<IMonitorInfo> selectedInfo = part.getSelectedInfo();
		if (getCurrentList() == null || (state != GuiState.CHANNEL && selectedInfo.isEmpty())) {
			return pos;
		}
		for (int i = start; i < finish; i++) {
			IMonitorInfo info = getCurrentList().get(i);
			if (info != null && !info.isHeader()) {
				if (state == GuiState.INFO) {
					for (IMonitorInfo selected : selectedInfo) {
						if (selected != null && !selected.isHeader() && info.isMatchingType(selected) && info.isMatchingInfo(selected)) {
							pos.add(i - start);
							break;
						}
					}
				} else if (info.isValid() && part.getMonitoringCoords().contains(((MonitoredBlockCoords) info).coords)) {
					pos.add(i - start);
				}
			}
		}
		return pos;
	}

	public ArrayList<Integer> getCategoryPositions() {
		ArrayList<Integer> pos = new ArrayList();
		if (getCurrentList() == null) {
			return pos;
		}
		for (int i = start; i < finish; i++) {
			IMonitorInfo info = getCurrentList().get(i);
			if (info != null && info.isHeader()) {
				pos.add(i - start);
			} else if (RenderBlockSelection.getPosition() != null && info instanceof MonitoredBlockCoords) {
				if (RenderBlockSelection.getPosition().equals(((MonitoredBlockCoords) info).coords)) {
					pos.add(i - start);
				}
			}
		}
		return pos;
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		setInfo();
		start = (int) (infoSize() * scroller.getCurrentScroll());
		finish = Math.min(start + size, infoSize());

		FontHelper.textCentre(FontHelper.translate("item.InfoReader.name"), xSize, 6, LogisticsColours.white_text);
		FontHelper.textCentre(String.format("Select the %s you wish to monitor", state == GuiState.INFO ? "data" : "channel"), xSize, 18, LogisticsColours.grey_text);
		GL11.glPushMatrix();
		GL11.glScaled(0.75, 0.75, 0.75);
		int identifierLeft = (int) ((1.0 / 0.75) * 10);
		int objectLeft = (int) ((1.0 / 0.75) * (10 + 92));
		int kindLeft = (int) ((1.0 / 0.75) * (10 + 92 + 92));
		for (int i = start; i < finish; i++) {
			IMonitorInfo info = getCurrentList().get(i);
			if (info != null) {
				int yPos = (int) ((1.0 / 0.75) * (32 + (12 * i) - (12 * start)));
				InfoRenderer.renderMonitorInfoInGUI(info, yPos, LogisticsColours.white_text.getRGB());
			}
		}
		GL11.glPopMatrix();
		super.drawGuiContainerForegroundLayer(x, y);

		GlStateManager.enableDepth();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		RenderItem item = this.mc.getRenderItem();
		ItemStack stack = getButtonStack();
		if (stack != null) {
			item.zLevel = 0.0F;
			item.renderItemIntoGUI(stack, -18, 11);
			item.renderItemOverlayIntoGUI(this.fontRendererObj, stack, -18, 11, "");
		}
		if (x > guiLeft - 20 && x < guiLeft && y > guiTop + 10 && y < guiTop + 30) {
			this.drawCreativeTabHoveringText(state.getButtonString(), x - guiLeft, y - guiTop);
		}

	}

	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		scroller.handleMouse(needsScrollBars(), infoSize());
	}

	public void drawScreen(int x, int y, float var) {
		super.drawScreen(x, y, var);
		scroller.drawScreen(x, y, needsScrollBars());
	}

	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException {
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

		if (x > guiLeft - 20 && x < guiLeft && y > guiTop + 10 && y < guiTop + 30) {
			state = SonarHelper.incrementEnum(state, state.values());
			scroller.currentScroll = 0;
			setInfo();
			start = (int) (infoSize() * scroller.getCurrentScroll());
			finish = Math.min(start + size, infoSize());
			Logistics.network.sendToServer(new PacketMonitorType(part, state.type));
		}
	}

	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		if (this.rselectButton != null && state == 1) {
			this.rselectButton.mouseReleased(mouseX, mouseY);
			this.rselectButton = null;
		}
	}

	protected void buttonPressed(GuiButton button, int buttonID) {
		if (button != null && button.id >= 10) {
			int start = (int) (infoSize() * scroller.getCurrentScroll());
			int network = start + button.id - 10;
			if (network < infoSize()) {
				IMonitorInfo info = getCurrentList().get(network);
				switch (state) {
				case CHANNEL:
					if (info.isValid()) {
						if (buttonID == 0) {
							part.lastSelected = ((MonitoredBlockCoords) info).coords.getCoords();
							part.sendByteBufPacket(-3);
						} else {
							RenderBlockSelection.setPosition(((MonitoredBlockCoords) info).coords.getCoords());
						}
					}
					break;
				case INFO:
					if (!info.isHeader() && info.isValid()) {
						part.selectedInfo.setInfo(info);
						part.sendByteBufPacket(buttonID == 0 ? -9 : -10);
					}
					break;
				}
			}
		}

	}

	protected void actionPerformed(GuiButton button) {
		this.buttonPressed(button, 0);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
		// RenderHelper.saveBlendState();
		int width = 225;
		int height = 12;
		int left = guiLeft + 7;

		ArrayList<Integer> data = getDataPositions();
		ArrayList<Integer> pairs = getPairedPositions();
		ArrayList<Integer> cats = getCategoryPositions();
		for (int i = 0; i < size; i++) {
			int top = guiTop + 29 + (height * i);
			int mainColour = cats.contains(i) ? LogisticsColours.category.getRGB() : (data.contains(i)) ? getColour(i, 0) : pairs.contains(i) ? getColour(i, 1) : LogisticsColours.layers[1].getRGB();
			drawRect(left + 1, top + 1, left - 1 + width, top - 1 + height, mainColour);
			drawRect(left, top, left + width, top + height, LogisticsColours.layers[2].getRGB());
		}

		drawRect(guiLeft - 20, guiTop + 10, guiLeft, guiTop + 30, LogisticsColours.layers[2].getRGB());
		drawRect(guiLeft - 19, guiTop + 11, guiLeft - 1, guiTop + 29, LogisticsColours.layers[2].getRGB());
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

	public ArrayList<IMonitorInfo> getCurrentList() {
		return state == GuiState.CHANNEL ? coords : infoList;
	}

	public int infoSize() {
		List list = state == GuiState.CHANNEL ? coords : infoList;
		return list == null ? 0 : list.size();
	}

	public void setInfo() {
		coords = (ArrayList<IMonitorInfo>) CacheRegistry.coordMap.getOrDefault(part.registryID.getObject(), MonitoredList.<MonitoredBlockCoords>newMonitoredList()).info.clone();
		infoList = (ArrayList<IMonitorInfo>) part.getMonitoredList().info.clone();
	}

	@Override
	public ResourceLocation getBackground() {
		return null;
	}

}
