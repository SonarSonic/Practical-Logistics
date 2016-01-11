package sonar.logistics.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import sonar.core.SonarCore;
import sonar.core.inventory.GuiSonar;
import sonar.core.inventory.SonarButtons;
import sonar.core.inventory.StoredItemStack;
import sonar.core.network.PacketByteBufServer;
import sonar.core.utils.helpers.FontHelper;
import sonar.core.utils.helpers.RenderHelper;
import sonar.logistics.common.containers.ContainerInventoryReader;
import sonar.logistics.common.handlers.InventoryReaderHandler;
import sonar.logistics.common.tileentity.TileEntityInventoryReader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GuiInventoryReader extends GuiSonar {

	public int xCoord, yCoord, zCoord;

	public InventoryReaderHandler handler;
	public TileEntity tile;
	
	public GuiInventoryReader(InventoryReaderHandler handler, TileEntity entity, InventoryPlayer inventoryPlayer) {
		super(new ContainerInventoryReader(handler, entity, inventoryPlayer), entity);
		this.xCoord = entity.xCoord;
		this.yCoord = entity.yCoord;
		this.zCoord = entity.zCoord;
		this.handler=handler;
		this.tile=entity;
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
	
	public List<Integer> getCategoryPositions() {
		if (getStacks() == null) {
			return null;
		}
		List<Integer> positions = new ArrayList();
		int start = (int) (stackListSize() * this.currentScroll);
		int finish = Math.min(start + (12 * 7), stackListSize());
		for (int i = start; i < finish; i++) {
			if (getStacks().get(i) != null) {
				StoredItemStack info = getStacks().get(i);
			}
		}
		return positions;
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
					StoredItemStack storedStack = getStacks().get(i);
					if (storedStack != null) {
						handler.current=storedStack.item;
						SonarCore.network.sendToServer(new PacketByteBufServer(handler, tile.xCoord, tile.yCoord,tile.zCoord, 1));
					}
				}
			}
		}
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		FontHelper.textCentre(StatCollector.translateToLocal("tile.InventoryReader.name"), xSize, 6, 1);
		FontHelper.textCentre("Click the item you wish to monitor", xSize, 18, 0);
		if (cycle == 100) {
			cycle = 0;
		} else {
			cycle++;
		}
		if (getStacks() != null) {
			int start = (int) (stackListSize() / 12 * this.currentScroll);
			int i = start * 12;
			int finish = Math.min(i + (12 * 7), stackListSize());
			for (int Y = 0; Y < 7; Y++) {
				for (int X = 0; X < 12; X++) {
					if (i < finish) {
						StoredItemStack storedStack = getStacks().get(i);
						if (storedStack != null) {
							ItemStack stack = storedStack.item;
							stack.stackSize = (int) storedStack.stored;
							RenderItem.getInstance().renderItemAndEffectIntoGUI(fontRendererObj, this.mc.getTextureManager(), stack, 13 + (X * 18), 32 + (Y * 18));
							RenderHelper.renderStoredItemStackOverlay(this.fontRendererObj, this.mc.getTextureManager(), stack, storedStack.stored, 13 + (X * 18), 32 + (Y * 18), null);
						}
					}
					i++;
				}
			}
		}
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
		// drawTexturedModalRect(this.guiLeft + 7, this.guiTop + 29 + (12 * i), 0, positions != null && positions.contains(i) ? 190 : i == pos ? 178 : 166, 154 + 72, 12);
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

	public List<StoredItemStack> getStacks(){
		return handler.stacks;
	}

}
