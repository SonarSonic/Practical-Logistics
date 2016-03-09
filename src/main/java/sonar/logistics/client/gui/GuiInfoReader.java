package sonar.logistics.client.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import sonar.core.inventory.GuiSonar;
import sonar.core.inventory.SonarButtons;
import sonar.core.utils.helpers.FontHelper;
import sonar.logistics.Logistics;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.common.containers.ContainerInfoNode;
import sonar.logistics.common.handlers.InfoReaderHandler;
import sonar.logistics.info.types.CategoryInfo;
import sonar.logistics.network.packets.PacketInfoBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GuiInfoReader extends GuiSonar {

	public int xCoord, yCoord, zCoord;
	public InfoReaderHandler handler;

	public GuiInfoReader(InfoReaderHandler handler, TileEntity tile) {
		super(new ContainerInfoNode(handler, tile), tile);
		this.xCoord = tile.xCoord;
		this.yCoord = tile.yCoord;
		this.zCoord = tile.zCoord;
		this.handler = handler;
	}

	public static final ResourceLocation bground = new ResourceLocation("PracticalLogistics:textures/gui/infoselect.png");

	private float currentScroll;
	private boolean isScrolling;
	private boolean wasClicking;
	public int scrollerLeft, scrollerStart, scrollerEnd, scrollerWidth;
	private GuiButton rselectButton;

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
			this.buttonList.add(new NetworkButton(10 + i, guiLeft + 7, guiTop + 29 + (i * 12)));
		}
	}

	public int getDataPosition() {
		if (getPrimaryInfo() == null) {
			return -1;
		}
		if (getInfo() == null) {
			return -1;
		}
		int start = (int) (infoSize() * this.currentScroll);
		int finish = Math.min(start + 11, infoSize());
		for (int i = start; i < finish; i++) {
			if (getInfo().get(i) != null) {
				ILogicInfo info = getInfo().get(i);
				if (info != null) {
					if (info.equals(getPrimaryInfo())) {
						return i - start;
					}
				}
			}
		}
		return -1;
	}

	public int getSecondaryPosition() {
		if (getSecondInfo() == null) {
			return -1;
		}
		if (getInfo() == null) {
			return -1;
		}
		int start = (int) (infoSize() * this.currentScroll);
		int finish = Math.min(start + 11, infoSize());
		for (int i = start; i < finish; i++) {
			if (getInfo().get(i) != null) {
				ILogicInfo info = getInfo().get(i);
				if (info != null) {
					if (info.equals(getSecondInfo())) {
						return i - start;
					}
				}
			}
		}
		return -1;
	}

	public List<Integer> getCategoryPositions() {
		if (getInfo() == null) {
			return null;
		}
		List<Integer> positions = new ArrayList();
		int start = (int) (infoSize() * this.currentScroll);
		int finish = Math.min(start + 11, infoSize());
		for (int i = start; i < finish; i++) {
			if (getInfo().get(i) != null) {
				ILogicInfo info = getInfo().get(i);
				if (info instanceof CategoryInfo) {
					positions.add(i - start);

				}
			}
		}
		return positions;
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		FontHelper.textCentre(StatCollector.translateToLocal("tile.InfoReader.name"), xSize, 6, 1);
		FontHelper.textCentre("Select the data you wish to monitor", xSize, 18, 0);
		if (getInfo() != null) {
			int start = (int) (infoSize() * this.currentScroll);
			int finish = Math.min(start + 11, infoSize());
			for (int i = start; i < finish; i++) {
				ILogicInfo info = getInfo().get(i);
				if (info != null) {
					boolean isSelected = info.equals(getPrimaryInfo());
					if (!(info instanceof CategoryInfo)) {
						int colour = isSelected ? Color.green.getRGB() : Color.lightGray.getRGB();
						FontHelper.text(info.getSubCategory().substring(0, Math.min(15, info.getSubCategory().length())), 10, 31 + (12 * i) - (12 * start), colour);
						FontHelper.text(info.getDisplayableData().substring(0, Math.min(25, info.getDisplayableData().length())), 10 + 92, 31 + (12 * i) - (12 * start), colour);

					} else {
						FontHelper.text(info.getSubCategory(), 10, 31 + (12 * i) - (12 * start), Color.BLACK.getRGB());
					}
				}

			}

		}
	}

	public void handleMouseInput() {
		super.handleMouseInput();
		float lastScroll = currentScroll;
		int i = Mouse.getEventDWheel();

		if (i != 0 && this.needsScrollBars()) {
			int j = infoSize() + 1;

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
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		if (button == 1) {
			for (int l = 0; l < this.buttonList.size(); ++l) {
				GuiButton guibutton = (GuiButton) this.buttonList.get(l);
				if (guibutton.mousePressed(this.mc, x, y)) {
					ActionPerformedEvent.Pre event = new ActionPerformedEvent.Pre(this, guibutton, this.buttonList);
					if (MinecraftForge.EVENT_BUS.post(event))
						break;
					this.rselectButton = event.button;
					event.button.func_146113_a(this.mc.getSoundHandler());
					this.buttonPressed(event.button, 1);
					if (this.equals(this.mc.currentScreen))
						MinecraftForge.EVENT_BUS.post(new ActionPerformedEvent.Post(this, event.button, this.buttonList));
				}
			}
		}
	}

	protected void mouseMovedOrUp(int x, int y, int id) {
		super.mouseMovedOrUp(x, y, id);
		if (this.rselectButton != null && id == 1) {
			this.rselectButton.mouseReleased(x, y);
			this.rselectButton = null;
		}
	}

	protected void buttonPressed(GuiButton button, int buttonID) {
		if (button != null) {
			if (button.id >= 10) {
				if (getInfo() != null) {
					int start = (int) (infoSize() * this.currentScroll);
					int network = start + button.id - 10;
					if (network < infoSize()) {
						ILogicInfo info = getInfo().get(network);
						if (info == null || (info instanceof CategoryInfo)) {
							return;
						}
						if (buttonID == 0) {
							if (getPrimaryInfo() != null && info.equals(getPrimaryInfo())) {
								Logistics.network.sendToServer(new PacketInfoBlock(xCoord, yCoord, zCoord, true));
								//if (handler.isMultipart.getObject()) {
									handler.primaryInfo.setObject(null);
								//}
							} else {
								Logistics.network.sendToServer(new PacketInfoBlock(xCoord, yCoord, zCoord, info, true));
								//if (handler.isMultipart.getObject()) {
									handler.primaryInfo.setObject(info);
								//}
							}

						} else if (buttonID == 1) {
							if (!info.equals(getPrimaryInfo())) {
								if (getSecondInfo() != null && info.equals(getSecondInfo())) {
									Logistics.network.sendToServer(new PacketInfoBlock(xCoord, yCoord, zCoord, false));
									//if (handler.isMultipart.getObject()) {
										handler.secondaryInfo.setObject(null);
									//}
								} else {
									Logistics.network.sendToServer(new PacketInfoBlock(xCoord, yCoord, zCoord, info, false));
									//if (handler.isMultipart.getObject()) {
										handler.secondaryInfo.setObject(info);
									//}
								}
							}
						}
					}
				}
			}
		}
	}

	protected void actionPerformed(GuiButton button) {
		this.buttonPressed(button, 0);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		Minecraft.getMinecraft().getTextureManager().bindTexture(this.getBackground());

		drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		List<Integer> positions = this.getCategoryPositions();
		int primary = getDataPosition();
		int secondary = getSecondaryPosition();
		for (int i = 0; i < 11; i++) {
			drawTexturedModalRect(this.guiLeft + 7, this.guiTop + 29 + (12 * i), 0, positions != null && positions.contains(i) ? 190 : i == primary ? 178 : i == secondary ? 202 : 166, 154 + 72, 12);
		}

		this.drawTexturedModalRect(scrollerLeft, scrollerStart + (int) ((float) (scrollerEnd - scrollerStart - 17) * this.currentScroll), 176 + 72, 0, 8, 15);

	}

	private boolean needsScrollBars() {
		if (infoSize() <= 11)
			return false;

		return true;

	}

	@SideOnly(Side.CLIENT)
	public class NetworkButton extends SonarButtons.ImageButton {

		public NetworkButton(int id, int x, int y) {
			super(id, x, y, bground, 0, 224, 154 + 72, 11);
		}
	}

	@Override
	public ResourceLocation getBackground() {
		return bground;
	}

	public int infoSize() {
		return getInfo() == null ? 0 : getInfo().size();
	}

	public List<ILogicInfo> getInfo() {
		return handler.clientInfo;
	}

	public ILogicInfo getPrimaryInfo(){
		return handler.primaryInfo.getObject();
	}

	public ILogicInfo getSecondInfo(){
		return handler.secondaryInfo.getObject();
	}

}
