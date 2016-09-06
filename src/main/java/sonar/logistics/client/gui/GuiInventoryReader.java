package sonar.logistics.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.client.gui.SonarButtons.AnimatedButton;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.RenderHelper;
import sonar.logistics.Logistics;
import sonar.logistics.api.readers.InventoryReader.Modes;
import sonar.logistics.api.readers.InventoryReader.SortingType;
import sonar.logistics.client.LogisticsColours;
import sonar.logistics.common.containers.ContainerInventoryReader;
import sonar.logistics.connections.MonitoredList;
import sonar.logistics.monitoring.MonitoredItemStack;
import sonar.logistics.network.PacketInventoryReader;
import sonar.logistics.parts.InventoryReaderPart;

public class GuiInventoryReader extends GuiSelectionGrid<MonitoredItemStack> {

	// public static final ResourceLocation stackBGround = new ResourceLocation("PracticalLogistics:textures/gui/inventoryReader_stack.png");
	// public static final ResourceLocation clearBGround = new ResourceLocation("PracticalLogistics:textures/gui/inventoryReader_clear.png");

	public static final ResourceLocation sorting_icons = new ResourceLocation("PracticalLogistics:textures/gui/sorting_icons.png");

	private InventoryReaderPart part;
	private GuiTextField slotField;
	private GuiTextField searchField;

	public InventoryPlayer inventoryPlayer;

	public GuiInventoryReader(InventoryReaderPart part, EntityPlayer player) {
		super(new ContainerInventoryReader(part, player), part);
		this.part = part;
		this.inventoryPlayer = player.inventory;
	}

	public Modes getSetting() {
		return part.setting.getObject();
	}

	public void initGui() {
		super.initGui();
		this.buttonList.add(new GuiButton(-1, guiLeft + 120 - (18 * 6), guiTop + 7, 65 + 3, 20, getSettingsString()));
		this.buttonList.add(new FilterButton(0, guiLeft + 193, guiTop + 9));
		this.buttonList.add(new FilterButton(1, guiLeft + 193 + 18, guiTop + 9));
		switch (getSetting()) {
		case SLOT:
		case POS:
			slotField = new GuiTextField(0, this.fontRendererObj, 195 - (18 * 6), 8, 34 + 14, 18);
			slotField.setMaxStringLength(7);
			if (getSetting() == Modes.SLOT)
				slotField.setText("" + part.targetSlot.getObject());
			else if (getSetting() == Modes.POS)
				slotField.setText("" + part.posSlot.getObject());
			break;
		default:
			break;
		}
		searchField = new GuiTextField(1, this.fontRendererObj, 195 - (18 * 3), 9, 13 + 18 * 2, 16);
		// searchField = new GuiTextField(this.fontRendererObj, 95 - (18 * 3), 160, 16 + 18 * 8, 10);
		searchField.setMaxStringLength(20);
		// searchField.setText("");
	}

	public void actionPerformed(GuiButton button) {
		if (button != null) {
			if (button.id == -1) {
				part.setting.incrementEnum();
				part.sendByteBufPacket(1);
				switchState();
				reset();
			}
			if (button.id == 0) {
				part.sortingOrder.incrementEnum();
				part.sendByteBufPacket(4);
			}
			if (button.id == 1) {
				part.sortingType.incrementEnum();
				part.sendByteBufPacket(5);
			}
		}
	}

	public void switchState() {
		/*
		 * FIXME
		Logistics.network.sendToServer(new PacketGuiChange(part.getPos(), getSetting() == STACK, LogisticsGui.inventoryReader));
		if (this.mc.thePlayer.openContainer instanceof ContainerInventoryReader) {
			((ContainerInventoryReader) this.mc.thePlayer.openContainer).addSlots(part, inventoryPlayer, getSetting() == STACK);
		}
		this.inventorySlots = this.mc.thePlayer.openContainer;
		*/
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		RenderHelper.restoreBlendState();
		super.drawGuiContainerForegroundLayer(x, y);
		switch (getSetting()) {
		case SLOT:
		case POS:
			slotField.drawTextBox();
			break;
		default:
			break;
		}
		searchField.drawTextBox();
	}

	@Override
	public void mouseClicked(int i, int j, int k) throws IOException {
		super.mouseClicked(i, j, k);
		switch (getSetting()) {
		case SLOT:
		case POS:
			slotField.mouseClicked(i - guiLeft, j - guiTop, k);
			break;
		default:
			break;
		}
		if (k == 1) {
			searchField.setText("");
		}
		searchField.mouseClicked(i - guiLeft, j - guiTop, k);
	}

	@Override
	public void keyTyped(char c, int i) throws IOException {
		if ((getSetting() == Modes.SLOT || getSetting() == Modes.POS) && slotField.isFocused()) {
			if (c == 13 || c == 27) {
				slotField.setFocused(false);
			} else {
				FontHelper.addDigitsToString(slotField, c, i);
				final String text = slotField.getText();
				if (text.isEmpty() || text == "" || text == null) {
					if (getSetting() == Modes.SLOT)
						setTargetSlot("0");
					else if (getSetting() == Modes.POS)
						setPosSlot("0");
				} else {
					if (getSetting() == Modes.SLOT)
						setTargetSlot(text);
					else if (getSetting() == Modes.POS)
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
		part.targetSlot.setObject(Integer.parseInt(string));
		part.sendByteBufPacket(2);
	}

	public void setPosSlot(String string) {
		part.posSlot.setObject(Integer.parseInt(string));
		part.sendByteBufPacket(3);
	}

	public String getSettingsString() {
		return part.setting.getObject().name();
	}

	@Override
	public MonitoredList<MonitoredItemStack> getGridList() {
		String search = searchField.getText();
		if (search == null || search.isEmpty() || search.equals(" "))
			return part.getMonitoredList();
		else {
			MonitoredList<MonitoredItemStack> searchList = MonitoredList.newMonitoredList();
			for (MonitoredItemStack stack : (ArrayList<MonitoredItemStack>) part.getMonitoredList().clone()) {
				StoredItemStack item = stack.itemStack.getObject();
				if (stack != null && item != null && item.item.getDisplayName().toLowerCase().contains(search.toLowerCase())) {
					searchList.add(stack);
				}
			}
			return searchList;
		}
	}

	@Override
	public void onGridClicked(MonitoredItemStack selection, int pos, int button, boolean empty) {
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			button = 2;
		}
		if (!empty) {
			Logistics.network.sendToServer(new PacketInventoryReader(part.getUUID(), part.getPos(), selection.itemStack.getObject().item, button));
		} else {
			Logistics.network.sendToServer(new PacketInventoryReader(part.getUUID(), part.getPos(), null, button));
		}
		/* if (getSetting() == STACK) { handler.current = selection.item; handler.current.stackSize = 1; Logistics.network.sendToServer(new PacketInventoryReader(tile.xCoord, tile.yCoord, tile.zCoord, handler.current)); } if (getSetting() == POS) { List<StoredItemStack> currentList = (List<StoredItemStack>) ((ArrayList<StoredItemStack>) handler.stacks).clone(); int position = 0; for (StoredItemStack stack : currentList) { if (stack != null) { if (stack.equals(selection)) { String posString = String.valueOf(position); slotField.setText(posString); setPosSlot(posString); } } position++; } } */
	}

	@Override
	public void renderStrings(int x, int y) {
	}

	@Override
	public void renderSelection(MonitoredItemStack selection, int x, int y) {
		StoredItemStack storedStack = selection.itemStack.getObject();
		if (storedStack == null) {
			return;
		}
		ItemStack stack = storedStack.item;
		RenderHelper.renderItem(this, 13 + (x * 18), 32 + (y * 18), stack);
		// this.itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, stack, 13 + (x * 18), 32 + (y * 18), "" + storedStack.stored);
		RenderHelper.renderStoredItemStackOverlay(stack, storedStack.stored, 13 + (x * 18), 32 + (y * 18), null);
	}

	@Override
	public void renderToolTip(MonitoredItemStack selection, int x, int y) {
		StoredItemStack storedStack = selection.itemStack.getObject();
		if (storedStack == null) {
			return;
		}
		List list = storedStack.item.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);
		list.add(1, "Stored: " + storedStack.stored);
		for (int k = 0; k < list.size(); ++k) {
			if (k == 0) {
				list.set(k, storedStack.item.getRarity().rarityColor + (String) list.get(k));
			} else {
				list.set(k, TextFormatting.GRAY + (String) list.get(k));
			}
		}

		FontRenderer font = storedStack.item.getItem().getFontRenderer(storedStack.item);
		drawHoveringText(list, x, y, (font == null ? fontRendererObj : font));

	}

	@Override
	public void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
		// RenderHelper.saveBlendState();
		// StorageSize size = getGridList().sizing;
		
		// RenderHelper.restoreBlendState();
	}

	/* @Override public ResourceLocation getBackground() { if (getSetting() == Modes.STACK) { return stackBGround; } return clearBGround; } */
	@SideOnly(Side.CLIENT)
	public class FilterButton extends AnimatedButton {
		public int id;

		public FilterButton(int id, int x, int y) {
			super(id, x, y, sorting_icons, 15, 15);
			this.id = id;
		}

		public void func_146111_b(int x, int y) {
			String text = "BUTTON TEXT";
			switch (id) {
			case 0:
				text = ("Sorting Direction");
				break;
			case 1:
				text = (part.sortingType.getObject() == SortingType.STORED ? "Items Stored" : part.sortingType.getObject() == SortingType.NAME ? "Item Name" : "Mod");
			}

			drawCreativeTabHoveringText(text, x, y);
		}

		@Override
		public void onClicked() {
		}

		@Override
		public int getTextureX() {
			switch (id) {
			case 0:
				return 0 + part.sortingOrder.getObject().ordinal() * 16;
			case 1:
				return 32 + (part.sortingType.getObject().ordinal() * 16);
			}
			return 0;
		}

		@Override
		public int getTextureY() {
			return 0;
		}

	}

}
