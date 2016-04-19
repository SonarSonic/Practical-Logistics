package sonar.logistics.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;

import sonar.core.SonarCore;
import sonar.core.api.StoredItemStack;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.RenderHelper;
import sonar.core.inventory.SonarButtons;
import sonar.core.network.PacketByteBufServer;
import sonar.logistics.Logistics;
import sonar.logistics.api.cache.ICacheViewer;
import sonar.logistics.common.containers.ContainerInventoryCache;
import sonar.logistics.common.containers.ContainerInventoryReader;
import sonar.logistics.common.handlers.InventoryReaderHandler;
import sonar.logistics.network.LogisticsGui;
import sonar.logistics.network.packets.PacketGuiChange;
import sonar.logistics.network.packets.PacketInventoryReader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GuiInventoryViewer extends GuiSelectionGrid<StoredItemStack> {

	public static final ResourceLocation clearBGround = new ResourceLocation("PracticalLogistics:textures/gui/inventoryReader_clear.png");

	private GuiTextField slotField;
	private GuiTextField searchField;
	public ICacheViewer viewer;
	public InventoryPlayer inventoryPlayer;

	public GuiInventoryViewer(ICacheViewer viewer, TileEntity entity, InventoryPlayer inventoryPlayer) {
		super(new ContainerInventoryCache(viewer, entity, inventoryPlayer), entity);
		this.viewer = viewer;
		this.inventoryPlayer = inventoryPlayer;
	}
	
	public void initGui() {
		super.initGui();
		this.buttonList.add(new FilterButton(0, guiLeft + 193, guiTop + 9));
		this.buttonList.add(new FilterButton(1, guiLeft + 193 + 18, guiTop + 9));
		
		searchField = new GuiTextField(this.fontRendererObj, 195 - (18 * 3), 9, 13 + 18 * 2, 16);
		searchField.setMaxStringLength(20);
	}

	protected void actionPerformed(GuiButton button) {
		if (button != null) {
			if (button.id == 0) {
				viewer.setSortingDirection(viewer.getSortingDirection().switchDir());				
				//SonarCore.network.sendToServer(new PacketByteBufServer(handler, entity.xCoord, entity.yCoord, entity.zCoord, 3));
			}
			if (button.id == 1) {
				viewer.setSortingType(viewer.getSortingType().switchDir());
				//SonarCore.network.sendToServer(new PacketByteBufServer(handler, entity.xCoord, entity.yCoord, entity.zCoord, 4));
			}
		}
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		searchField.drawTextBox();
		super.drawGuiContainerForegroundLayer(x, y);
	}

	@Override
	protected void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
		searchField.mouseClicked(i - guiLeft, j - guiTop, k);
	}

	@Override
	protected void keyTyped(char c, int i) {
		if (searchField.isFocused()) {
			if (c == 13 || c == 27) {
				searchField.setFocused(false);
			} else {
				searchField.textboxKeyTyped(c, i);
			}
		} else {
			super.keyTyped(c, i);
		}
	}
	
	@Override
	public List<StoredItemStack> getGridList() {
		String search = searchField.getText();
		if (search == null || search.isEmpty() || search.equals(" "))
			return viewer.getItemStacks();
		else {
			List<StoredItemStack> searchList = new ArrayList();
			List<StoredItemStack> currentList = (List<StoredItemStack>) ((ArrayList<StoredItemStack>) viewer.getItemStacks()).clone();
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
				text = viewer.getSortingType().getTypeName();
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
				return 0 + viewer.getSortingDirection().ordinal() * 16;
			case 1:
				return 32 + viewer.getSortingType().ordinal() * 16;
			}
			return 0;
		}

		@Override
		public int getTextureY() {
			return 0;
		}

	}

}
