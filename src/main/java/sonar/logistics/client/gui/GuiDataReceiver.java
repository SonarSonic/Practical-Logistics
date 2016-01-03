package sonar.logistics.client.gui;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
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
import sonar.logistics.api.DataEmitter;
import sonar.logistics.common.containers.ContainerDataReceiver;
import sonar.logistics.common.tileentity.TileEntityDataReceiver;
import sonar.logistics.network.packets.PacketDataReceiver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GuiDataReceiver extends GuiSonar {

	public static final ResourceLocation bground = new ResourceLocation("PracticalLogistics:textures/gui/dataReceiver.png");

	private float currentScroll;
	private boolean isScrolling;
	private boolean wasClicking;
	public int scrollerLeft, scrollerStart, scrollerEnd, scrollerWidth;
	public TileEntityDataReceiver tile;
	public int cycle;

	public GuiDataReceiver(InventoryPlayer inventory, TileEntityDataReceiver tile) {
		super(new ContainerDataReceiver(tile, inventory), tile);
		this.tile = tile;
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
			this.buttonList.add(new NetworkButton(10 + i, guiLeft + 7, guiTop + 29 + (i * 12)));
		}
	}

	public int getDataPosition() {
		if (tile.emitter == null) {
			return -1;
		}
		if (tile.emitters == null) {
			return -1;
		}
		int start = (int) (emitterSize() * this.currentScroll);
		int finish = Math.min(start + 11, emitterSize());
		for (int i = start; i < finish; i++) {
			if (tile.emitters.get(i) != null) {
				DataEmitter info = tile.emitters.get(i);
				if (BlockCoords.equalCoords(info.coords, tile.emitter.coords)) {
					return i - start;

				}
			}
		}
		return -1;
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		FontHelper.textCentre(StatCollector.translateToLocal("tile.DataReceiver.name"), xSize, 6, 1);
		FontHelper.textCentre("Select the emitter you wish to connect to", xSize, 18, 0);
		if (cycle == 100) {
			cycle = 0;
		} else {
			cycle++;
		}
		if (tile.emitters != null) {
			int start = (int) (emitterSize() * this.currentScroll);
			int finish = Math.min(start + 11, emitterSize());
			int pos = this.getDataPosition();
			for (int i = start; i < finish; i++) {
				DataEmitter emitter = tile.emitters.get(i);
				if (emitter != null) {
					boolean isSelected = pos==i;
					FontHelper.text(emitter.name, 10, 31 + (12 * i) - (12 * start), isSelected ? Color.GREEN.getRGB() : Color.WHITE.getRGB());

					GL11.glPushMatrix();
					GL11.glScaled(0.75, 0.75, 0.75);
					FontHelper.text("D: " +emitter.coords.getDimension() +" " + emitter.coords.getRender(), 174, 43 + (16 * i) - (16 * start), isSelected ? Color.GREEN.getRGB() : Color.WHITE.getRGB());
					GL11.glPopMatrix();
					// FontHelper.text("Dimension: " + emitter.coords.getDimension() + " " +emitter.coords.getRender(), 10, 31 + (12 * i) - (12 * start), isSelected ? Color.GREEN.getRGB() : Color.WHITE.getRGB());
				}

			}

		}
	}

	public void handleMouseInput() {
		super.handleMouseInput();
		float lastScroll = currentScroll;
		int i = Mouse.getEventDWheel();

		if (i != 0 && this.needsScrollBars()) {
			int j = emitterSize() + 1;

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
				if (tile.emitters != null) {
					int start = (int) (emitterSize() * this.currentScroll);
					int network = start + button.id - 10;
					if (network < emitterSize()) {
						if (tile.emitters.get(network) != null) {
							Logistics.network.sendToServer(new PacketDataReceiver(tile.xCoord, tile.yCoord, tile.zCoord, tile.emitters.get(network)));
						}
					}
				}
			}
		}
	}

	/* public void changeNetworkName(String string, int type) { Calculator.network.sendToServer(new PacketFluxPoint(string, tile.xCoord, tile.yCoord, tile.zCoord, type)); this.setNetworkName(string); this.currentName = string; }
	 * @Override protected void mouseClicked(int i, int j, int k) { super.mouseClicked(i, j, k); id.mouseClicked(i - guiLeft, j - guiTop, k); if (id.isFocused() && id.getText().equals("NETWORK")) id.setText(""); }
	 * @Override protected void keyTyped(char c, int i) { if (id.isFocused()) { if (c == 13 || c == 27) { id.setFocused(false); } else { id.textboxKeyTyped(c, i); final String text = id.getText(); if (text.isEmpty() || text == "" || text == null) this.setNetworkName("NETWORK"); else this.setNetworkName(text); } } else { super.keyTyped(c, i); } } */
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		Minecraft.getMinecraft().getTextureManager().bindTexture(this.getBackground());

		drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		// List<Integer> positions = this.getCategoryPositions();
		int pos = getDataPosition();
		for (int i = 0; i < 11; i++) {
			drawTexturedModalRect(this.guiLeft + 7, this.guiTop + 29 + (12 * i), 0, i == pos ? 178 : 166, 154 + 72, 12);
		}

		this.drawTexturedModalRect(scrollerLeft, scrollerStart + (int) ((float) (scrollerEnd - scrollerStart - 17) * this.currentScroll), 176 + 72, 0, 8, 15);

	}

	private boolean needsScrollBars() {
		if (emitterSize() <= 11)
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

	public int emitterSize() {
		return tile.emitters == null ? 0 : tile.emitters.size();
	}

}
