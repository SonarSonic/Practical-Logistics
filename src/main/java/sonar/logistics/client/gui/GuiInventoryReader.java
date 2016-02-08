package sonar.logistics.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
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
import sonar.core.network.PacketMachineButton;
import sonar.core.network.PacketTextField;
import sonar.core.utils.helpers.FontHelper;
import sonar.core.utils.helpers.RenderHelper;
import sonar.logistics.Logistics;
import sonar.logistics.common.containers.ContainerInventoryReader;
import sonar.logistics.common.containers.ContainerItemRouter;
import sonar.logistics.common.handlers.InventoryReaderHandler;
import sonar.logistics.network.packets.PacketInventoryReader;
import sonar.logistics.network.packets.PacketInventoryReaderGui;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GuiInventoryReader extends GuiSelectionGrid<StoredItemStack> {

	public static final ResourceLocation stackBGround = new ResourceLocation("PracticalLogistics:textures/gui/inventoryReader_stack.png");

	public static final ResourceLocation clearBGround = new ResourceLocation("PracticalLogistics:textures/gui/inventoryReader_clear.png");

	public InventoryReaderHandler handler;
	private GuiTextField slotField;
	private GuiTextField searchField;
	public static final int STACK = 0, SLOT = 1, POS = 2, INV = 3;

	public InventoryPlayer inventoryPlayer;

	public GuiInventoryReader(InventoryReaderHandler handler, TileEntity entity, InventoryPlayer inventoryPlayer) {
		super(new ContainerInventoryReader(handler, entity, inventoryPlayer), entity);
		this.handler = handler;
		this.inventoryPlayer = inventoryPlayer;
	}

	public int getSetting() {
		return handler.setting.getInt();
	}

	public void initGui() {
		super.initGui();
		// System.out.print(" RESET: " + handler.setting.getInt());
		this.buttonList.add(new GuiButton(0, guiLeft + 120 - (18 * 6), guiTop + 7, 65 + 3, 20, getSettingsString()));
		switch (getSetting()) {
		case SLOT:
		case POS:
			slotField = new GuiTextField(this.fontRendererObj, 195 - (18 * 6), 8, 34 + 14, 18);
			slotField.setMaxStringLength(7);
			if (getSetting() == SLOT)
				slotField.setText("" + handler.targetSlot.getInt());
			else if (getSetting() == POS)
				slotField.setText("" + handler.posSlot.getInt());
			break;
		}
		searchField = new GuiTextField(this.fontRendererObj, 195 - (18 * 3), 8, 32 + 18 * 3, 18);
		searchField.setMaxStringLength(20);
		// searchField.setText("");
	}

	protected void actionPerformed(GuiButton button) {
		if (button != null) {
			if (button.id == 0) {

				if (handler.setting.getInt() == 3) {
					handler.setting.setInt(0);
				} else {
					handler.setting.increaseBy(1);
				}

				// System.out.print(" AFTER: " + handler.setting.getInt());
				SonarCore.network.sendToServer(new PacketByteBufServer(handler, entity.xCoord, entity.yCoord, entity.zCoord, 0));
				switchState();
				reset();
			}
		}
	}

	public void switchState() {
		Logistics.network.sendToServer(new PacketInventoryReaderGui(tile.xCoord, tile.yCoord, tile.zCoord, getSetting() == STACK));
		if (this.mc.thePlayer.openContainer instanceof ContainerInventoryReader) {
			((ContainerInventoryReader) this.mc.thePlayer.openContainer).addSlots(handler, inventoryPlayer, getSetting() == STACK);
		}
		this.inventorySlots = this.mc.thePlayer.openContainer;
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		switch (getSetting()) {
		case SLOT:
		case POS:
			slotField.drawTextBox();
			break;
		}
		searchField.drawTextBox();
		super.drawGuiContainerForegroundLayer(x, y);
	}

	@Override
	protected void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
		switch (getSetting()) {
		case SLOT:
		case POS:
			slotField.mouseClicked(i - guiLeft, j - guiTop, k);
			break;
		}
		searchField.mouseClicked(i - guiLeft, j - guiTop, k);
	}

	@Override
	protected void keyTyped(char c, int i) {
		if ((getSetting() == SLOT || getSetting() == POS) && slotField.isFocused()) {
			if (c == 13 || c == 27) {
				slotField.setFocused(false);
			} else {
				FontHelper.addDigitsToString(slotField, c, i);
				final String text = slotField.getText();
				if (text.isEmpty() || text == "" || text == null) {
					if (getSetting() == SLOT)
						setTargetSlot("0");
					else if (getSetting() == POS)
						setPosSlot("0");
				} else {
					if (getSetting() == SLOT)
						setTargetSlot(text);
					else if (getSetting() == POS)
						setPosSlot(text);
				}

			}
		} else if (searchField.isFocused()) {
			if (c == 13 || c == 27) {
				searchField.setFocused(false);
			} else {
				searchField.textboxKeyTyped(c, i);
			}
		} else {
			super.keyTyped(c, i);
		}
	}

	public void setTargetSlot(String string) {
		handler.targetSlot.setInt(Integer.parseInt(string));
		SonarCore.network.sendToServer(new PacketByteBufServer(handler, entity.xCoord, entity.yCoord, entity.zCoord, 1));
	}

	public void setPosSlot(String string) {
		handler.posSlot.setInt(Integer.parseInt(string));
		SonarCore.network.sendToServer(new PacketByteBufServer(handler, entity.xCoord, entity.yCoord, entity.zCoord, 2));
	}

	public String getSettingsString() {
		switch (handler.setting.getInt()) {
		case 0:
			return "Stack";
		case 1:
			return "Slot";
		case 2:
			return "Pos";
		case 3:
			return "Inventories";
		default:
			return "Inventories";
		}
	}

	@Override
	public List<StoredItemStack> getGridList() {
		if (searchField.getText() == null || searchField.getText().isEmpty() || searchField.getText().equals(" "))
			return handler.stacks;
		else {
			List<StoredItemStack> searchList = new ArrayList();
			List<StoredItemStack> currentList = (List<StoredItemStack>) ((ArrayList<StoredItemStack>) handler.stacks).clone();
			for (StoredItemStack stack : currentList) {
				if (stack.item.getDisplayName().toLowerCase().contains(searchField.getText().toLowerCase())) {
					searchList.add(stack);
				}
			}
			return searchList;
		}
	}

	@Override
	public void onGridClicked(StoredItemStack selection, int pos) {
		if (getSetting() == STACK) {
			handler.current = selection.item;
			handler.current.stackSize = 1;
			Logistics.network.sendToServer(new PacketInventoryReader(tile.xCoord, tile.yCoord, tile.zCoord, handler.current));
		}
		if (getSetting() == POS) {
			List<StoredItemStack> currentList = (List<StoredItemStack>) ((ArrayList<StoredItemStack>) handler.stacks).clone();
			int position = 0;
			for (StoredItemStack stack : currentList) {
				if (stack != null) {
					if (stack.equals(selection)) {
						String posString = String.valueOf(position);
						slotField.setText(posString);
						setPosSlot(posString);
					}
				}
				position++;
			}

			// Logistics.network.sendToServer(new
			// PacketInventoryReader(tile.xCoord, tile.yCoord, tile.zCoord,
			// handler.current));
		}
	}

	@Override
	public void renderStrings(int x, int y) {
		// FontHelper.textOffsetCentre(StatCollector.translateToLocal("tile.InventoryReader.name").split(" ")[0],
		// 197, 8, 1);
		// FontHelper.textOffsetCentre(StatCollector.translateToLocal("tile.InventoryReader.name").split(" ")[1],
		// 197, 18, 1);
	}

	@Override
	public void renderSelection(StoredItemStack selection, int x, int y) {
		ItemStack stack = selection.item;
		stack.stackSize = (int) selection.stored;
		RenderItem.getInstance().renderItemAndEffectIntoGUI(fontRendererObj, this.mc.getTextureManager(), stack, 13 + (x * 18), 32 + (y * 18));
		RenderHelper.renderStoredItemStackOverlay(this.fontRendererObj, this.mc.getTextureManager(), stack, selection.stored, 13 + (x * 18), 32 + (y * 18), null);
	}

	@Override
	public void renderToolTip(StoredItemStack selection, int x, int y) {
		List list = selection.item.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);
		list.add(1, "Stored: " + selection.stored);
		for (int k = 0; k < list.size(); ++k) {
			if (k == 0) {
				list.set(k, selection.item.getRarity().rarityColor + (String) list.get(k));
			} else {
				list.set(k, EnumChatFormatting.GRAY + (String) list.get(k));
			}
		}

		FontRenderer font = selection.item.getItem().getFontRenderer(selection.item);
		drawHoveringText(list, x, y, (font == null ? fontRendererObj : font));
	}

	@Override
	public ResourceLocation getBackground() {
		if (getSetting() == 0) {
			return stackBGround;
		}
		return clearBGround;
	}
}
