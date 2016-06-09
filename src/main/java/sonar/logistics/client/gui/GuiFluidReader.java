package sonar.logistics.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import sonar.core.SonarCore;
import sonar.core.api.StoredFluidStack;
import sonar.core.helpers.FontHelper;
import sonar.core.inventory.SonarButtons;
import sonar.core.network.PacketByteBufServer;
import sonar.logistics.Logistics;
import sonar.logistics.common.containers.ContainerFluidReader;
import sonar.logistics.common.handlers.FluidReaderHandler;
import sonar.logistics.network.LogisticsGui;
import sonar.logistics.network.packets.PacketFluidReader;
import sonar.logistics.network.packets.PacketGuiChange;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GuiFluidReader extends GuiSelectionGrid<StoredFluidStack> {

	public static final ResourceLocation stackBGround = new ResourceLocation("PracticalLogistics:textures/gui/inventoryReader_stack.png");
	public static final ResourceLocation clearBGround = new ResourceLocation("PracticalLogistics:textures/gui/inventoryReader_clear.png");

	public FluidReaderHandler handler;
	private GuiTextField slotField;
	private GuiTextField searchField;
	public static final int STACK = 0, POS = 1, INV = 2, STORAGE = 3;

	public InventoryPlayer inventoryPlayer;

	public GuiFluidReader(FluidReaderHandler handler, TileEntity entity, InventoryPlayer inventoryPlayer) {
		super(new ContainerFluidReader(handler, entity, inventoryPlayer), entity);
		this.handler = handler;
		this.inventoryPlayer = inventoryPlayer;
	}

	public int getSetting() {
		return handler.setting.getObject();
	}

	public void initGui() {
		super.initGui();
		this.buttonList.add(new GuiButton(-1, guiLeft + 120 - (18 * 6), guiTop + 7, 65 + 3, 20, getSettingsString()) {

			public void func_146111_b(int x, int y) {
				drawCreativeTabHoveringText(getSettingsHover(), x, y);
			}
		});

		this.buttonList.add(new FilterButton(0, guiLeft + 193, guiTop + 9));
		this.buttonList.add(new FilterButton(1, guiLeft + 193 + 18, guiTop + 9));
		switch (getSetting()) {
		case POS:
			slotField = new GuiTextField(this.fontRendererObj, 195 - (18 * 6), 8, 34 + 14, 18);
			slotField.setMaxStringLength(7);
			slotField.setText("" + handler.posSlot.getObject());
			break;
		}
		searchField = new GuiTextField(this.fontRendererObj, 195 - (18 * 3), 9, 13 + 18 * 2, 16);
		searchField.setMaxStringLength(20);
	}

	protected void actionPerformed(GuiButton button) {
		if (button != null) {
			if (button.id == -1) {

				if (handler.setting.getObject() == 3) {
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
		Logistics.network.sendToServer(new PacketGuiChange(tile.xCoord, tile.yCoord, tile.zCoord, getSetting() == STACK, LogisticsGui.fluidReader));
		if (this.mc.thePlayer.openContainer instanceof ContainerFluidReader) {
			((ContainerFluidReader) this.mc.thePlayer.openContainer).addSlots(handler, inventoryPlayer, getSetting() == STACK);
		}
		this.inventorySlots = this.mc.thePlayer.openContainer;
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		searchField.drawTextBox();
		switch (getSetting()) {
		case STACK:
			if (x - guiLeft >= 103 && x - guiLeft <= 103 + 16 && y - guiTop >= 9 && y - guiTop <= 9 + 16) {
				FluidStack storedStack = handler.current;
				if (storedStack != null) {
					GL11.glDisable(GL11.GL_DEPTH_TEST);
					GL11.glDisable(GL11.GL_LIGHTING);
					List list = new ArrayList();
					list.add(storedStack.getLocalizedName());
					drawHoveringText(list, x - guiLeft, y - guiTop, fontRendererObj);
					GL11.glEnable(GL11.GL_LIGHTING);
					GL11.glEnable(GL11.GL_DEPTH_TEST);
					net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();

				}
			}
			break;
		case POS:
			slotField.drawTextBox();
			break;
		}
		super.drawGuiContainerForegroundLayer(x, y);
	}

	@Override
	protected void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
		switch (getSetting()) {
		case POS:
			slotField.mouseClicked(i - guiLeft, j - guiTop, k);
			break;
		}
		if(k==1){
			searchField.setText("");
		}
		searchField.mouseClicked(i - guiLeft, j - guiTop, k);
	}

	@Override
	protected void keyTyped(char c, int i) {
		if ((getSetting() == POS) && slotField.isFocused()) {
			if (c == 13 || c == 27) {
				slotField.setFocused(false);
			} else {
				FontHelper.addDigitsToString(slotField, c, i);
				final String text = slotField.getText();
				if (text.isEmpty() || text == "" || text == null) {
					setPosSlot("0");
				} else {
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

	public void setPosSlot(String string) {
		handler.posSlot.setObject(Integer.parseInt(string));
		SonarCore.network.sendToServer(new PacketByteBufServer(handler, entity.xCoord, entity.yCoord, entity.zCoord, 1));
	}

	public String getSettingsHover() {
		switch (handler.setting.getObject()) {
		case 0:
			return "Selected Fluid";
		case 1:
			return "Fluid at the given position";
		case 2:
			return "List of Fluids";
		case 3:
			return "Current Tank Usage";
		default:
			return "ERROR";
		}
	}

	public String getSettingsString() {
		switch (handler.setting.getObject()) {
		case 0:
			return "Fluid";
		case 1:
			return "Pos";
		case 2:
			return "Tanks";
		case 3:
			return "Storage";
		default:
			return "ERROR";
		}
	}

	@Override
	public List<StoredFluidStack> getGridList() {
		if (searchField.getText() == null || searchField.getText().isEmpty() || searchField.getText().equals(" "))
			return handler.cachedFluids.fluids;
		else {
			List<StoredFluidStack> searchList = new ArrayList();
			List<StoredFluidStack> currentList = (List<StoredFluidStack>) ((ArrayList<StoredFluidStack>) handler.cachedFluids.fluids).clone();
			for (StoredFluidStack stack : currentList) {
				if (stack.fluid.getLocalizedName().toLowerCase().contains(searchField.getText().toLowerCase())) {
					searchList.add(stack);
				}
			}
			return searchList;
		}
	}

	@Override
	public void onGridClicked(StoredFluidStack selection, int pos, int button, boolean empty) {
		if(empty){
			return;
		}
		if (getSetting() == STACK) {
			Logistics.network.sendToServer(new PacketFluidReader(tile.xCoord, tile.yCoord, tile.zCoord, selection.fluid));

		}
		if (getSetting() == POS) {
			List<StoredFluidStack> currentList = (List<StoredFluidStack>) ((ArrayList<StoredFluidStack>) handler.cachedFluids.fluids).clone();
			int position = 0;
			for (StoredFluidStack stack : currentList) {
				if (stack != null) {
					if (stack.equals(selection)) {
						String posString = String.valueOf(position);
						slotField.setText(posString);
						setPosSlot(posString);
					}
				}
				position++;
			}

		}
	}

	@Override
	public void renderStrings(int x, int y) {
		// FontHelper.textOffsetCentre(StatCollector.translateToLocal("tile.InventoryReader.name").split(" ")[0],
		// 197, 8, 1);
		// FontHelper.textOffsetCentre(StatCollector.translateToLocal("tile.InventoryReader.name").split(" ")[1],
		// 197, 18, 1);
	}

	public void preRender() {
		if (getGridList() != null) {
			final int br = 16 << 20 | 16 << 4;
			final int var11 = br % 65536;
			final int var12 = br / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var11 * 0.8F, var12 * 0.8F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	public void postRender() {
		if (handler.current != null) {
			final int br = 16 << 20 | 16 << 4;
			final int var11 = br % 65536;
			final int var12 = br / 65536;
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
			if (STACK == handler.setting.getObject())
				RenderItem.getInstance().renderIcon(103, 9, handler.current.getFluid().getIcon(), 16, 16);
		}

	}

	@Override
	public void renderSelection(StoredFluidStack selection, int x, int y) {
		if (selection.fluid != null) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

			RenderItem.getInstance().renderIcon(13 + (x * 18), 32 + (y * 18), selection.fluid.getFluid().getIcon(), 16, 16);
		}
	}

	@Override
	public void renderToolTip(StoredFluidStack selection, int x, int y) {
		List list = new ArrayList();
		list.add(selection.fluid.getFluid().getLocalizedName(selection.fluid));
		if (selection.stored != 0) {
			list.add(EnumChatFormatting.GRAY + (String) "Stored: " + selection.stored + " mB");
		}
		drawHoveringText(list, x, y, fontRendererObj);
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
				text = (handler.sortingType.getObject() == 0 ? "Amount Stored" : handler.sortingType.getObject() == 1 ? "Fluid Name" : "Temperature");
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
				if (handler.sortingType.getObject() == 2) {
					return 32 + 3 * 16;
				}
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
