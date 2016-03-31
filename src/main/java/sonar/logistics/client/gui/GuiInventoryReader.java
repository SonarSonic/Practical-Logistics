package sonar.logistics.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import sonar.core.SonarCore;
import sonar.core.api.StoredItemStack;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.RenderHelper;
import sonar.core.inventory.SonarButtons;
import sonar.core.network.PacketByteBufServer;
import sonar.logistics.Logistics;
import sonar.logistics.common.containers.ContainerInventoryReader;
import sonar.logistics.common.handlers.InventoryReaderHandler;
import sonar.logistics.network.LogisticsGui;
import sonar.logistics.network.packets.PacketGuiChange;
import sonar.logistics.network.packets.PacketInventoryReader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GuiInventoryReader extends GuiSelectionGrid<StoredItemStack> {

	public static final ResourceLocation stackBGround = new ResourceLocation("PracticalLogistics:textures/gui/inventoryReader_stack.png");
	public static final ResourceLocation clearBGround = new ResourceLocation("PracticalLogistics:textures/gui/inventoryReader_clear.png");

	public InventoryReaderHandler handler;
	private GuiTextField slotField;
	private GuiTextField searchField;
	public static final int STACK = 0, SLOT = 1, POS = 2, INV = 3, STORAGE = 4;

	public InventoryPlayer inventoryPlayer;

	public GuiInventoryReader(InventoryReaderHandler handler, TileEntity entity, InventoryPlayer inventoryPlayer) {
		super(new ContainerInventoryReader(handler, entity, inventoryPlayer), entity);
		this.handler = handler;
		this.inventoryPlayer = inventoryPlayer;
	}

	public int getSetting() {
		return handler.setting.getObject();
	}

	public void initGui() {
		super.initGui();
		this.buttonList.add(new GuiButton(-1, guiLeft + 120 - (18 * 6), guiTop + 7, 65 + 3, 20, getSettingsString()));
		this.buttonList.add(new FilterButton(0, guiLeft + 193, guiTop + 9));
		this.buttonList.add(new FilterButton(1, guiLeft + 193 + 18, guiTop + 9));
		switch (getSetting()) {
		case SLOT:
		case POS:
			slotField = new GuiTextField(this.fontRendererObj, 195 - (18 * 6), 8, 34 + 14, 18);
			slotField.setMaxStringLength(7);
			if (getSetting() == SLOT)
				slotField.setText("" + handler.targetSlot.getObject());
			else if (getSetting() == POS)
				slotField.setText("" + handler.posSlot.getObject());
			break;
		}
		searchField = new GuiTextField(this.fontRendererObj, 195 - (18 * 3), 9, 13 + 18 * 2, 16);
		// searchField = new GuiTextField(this.fontRendererObj, 95 - (18 * 3), 160, 16 + 18 * 8, 10);
		searchField.setMaxStringLength(20);
		// searchField.setText("");
	}

	protected void actionPerformed(GuiButton button) {
		if (button != null) {
			if (button.id == -1) {

				if (handler.setting.getObject() == 4) {
					handler.setting.setObject(0);
				} else {
					handler.setting.increaseBy(1);
				}
				SonarCore.network.sendToServer(new PacketByteBufServer(handler, entity.xCoord, entity.yCoord, entity.zCoord, 0));
				switchState();
				reset();
			}
			if (button.id == 0) {
				if (handler.sortingOrder.getObject() == 1) {
					handler.sortingOrder.setObject(0);
				} else {
					handler.sortingOrder.increaseBy(1);
				}
				SonarCore.network.sendToServer(new PacketByteBufServer(handler, entity.xCoord, entity.yCoord, entity.zCoord, 3));
			}
			if (button.id == 1) {
				if (handler.sortingType.getObject() == 2) {
					handler.sortingType.setObject(0);
				} else {
					handler.sortingType.increaseBy(1);
				}
				SonarCore.network.sendToServer(new PacketByteBufServer(handler, entity.xCoord, entity.yCoord, entity.zCoord, 4));
			}
		}
	}

	public void switchState() {
		Logistics.network.sendToServer(new PacketGuiChange(tile.xCoord, tile.yCoord, tile.zCoord, getSetting() == STACK, LogisticsGui.inventoryReader));
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
		handler.targetSlot.setObject(Integer.parseInt(string));
		SonarCore.network.sendToServer(new PacketByteBufServer(handler, entity.xCoord, entity.yCoord, entity.zCoord, 1));
	}

	public void setPosSlot(String string) {
		handler.posSlot.setObject(Integer.parseInt(string));
		SonarCore.network.sendToServer(new PacketByteBufServer(handler, entity.xCoord, entity.yCoord, entity.zCoord, 2));
	}

	public String getSettingsString() {
		switch (handler.setting.getObject()) {
		case 0:
			return "Stack";
		case 1:
			return "Slot";
		case 2:
			return "Pos";
		case 3:
			return "Inventories";
		case 4:
			return "Storage";
		default:
			return "Inventories";
		}
	}

	@Override
	public List<StoredItemStack> getGridList() {
		String search = searchField.getText();
		if (search == null || search.isEmpty() || search.equals(" "))
			return handler.stacks;
		else {
			List<StoredItemStack> searchList = new ArrayList();
			List<StoredItemStack> currentList = (List<StoredItemStack>) ((ArrayList<StoredItemStack>) handler.stacks).clone();
			for (StoredItemStack stack : currentList) {
				if (stack != null && stack.item != null && stack.item.getDisplayName().toLowerCase().contains(search.toLowerCase())) {
					searchList.add(stack);
				}
			}
			return searchList;
		}
	}

	@Override
	public void onGridClicked(StoredItemStack selection, int pos, int button, boolean empty) {
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			button = 2;
		}
		if (!empty) {
			Logistics.network.sendToServer(new PacketInventoryReader(tile.xCoord, tile.yCoord, tile.zCoord, selection.item, button));
		} else {
			Logistics.network.sendToServer(new PacketInventoryReader(tile.xCoord, tile.yCoord, tile.zCoord, null, button));
		}
		/*
		 * if (getSetting() == STACK) { handler.current = selection.item; handler.current.stackSize = 1; Logistics.network.sendToServer(new PacketInventoryReader(tile.xCoord, tile.yCoord, tile.zCoord, handler.current)); } if (getSetting() == POS) { List<StoredItemStack> currentList = (List<StoredItemStack>) ((ArrayList<StoredItemStack>) handler.stacks).clone(); int position = 0; for (StoredItemStack stack : currentList) { if (stack != null) { if (stack.equals(selection)) { String posString = String.valueOf(position); slotField.setText(posString); setPosSlot(posString); } } position++; } }
		 */
	}

	@Override
	public void renderStrings(int x, int y) {
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

	@SideOnly(Side.CLIENT)
	public class FilterButton extends SonarButtons.AnimatedButton {
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
				text = (handler.sortingType.getObject() == 0 ? "Items Stored" : handler.sortingType.getObject() == 1 ? "Item Name" : "Mod");
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
				return 0 + handler.sortingOrder.getObject() * 16;
			case 1:
				return 32 + (handler.sortingType.getObject() * 16);
			}
			return 0;
		}

		@Override
		public int getTextureY() {
			return 0;
		}

	}

}
