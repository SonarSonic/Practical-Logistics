package sonar.logistics.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.api.fluids.StoredFluidStack;
import sonar.core.client.gui.SonarButtons.AnimatedButton;
import sonar.core.helpers.FontHelper;
import sonar.logistics.api.readers.FluidReader;
import sonar.logistics.api.readers.FluidReader.Modes;
import sonar.logistics.api.readers.FluidReader.SortingType;
import sonar.logistics.common.containers.ContainerFluidReader;
import sonar.logistics.connections.MonitoredList;
import sonar.logistics.monitoring.MonitoredFluidStack;
import sonar.logistics.parts.FluidReaderPart;

public class GuiFluidReader extends GuiSelectionGrid<MonitoredFluidStack> {

	public static final ResourceLocation stackBGround = new ResourceLocation("PracticalLogistics:textures/gui/inventoryReader_stack.png");
	public static final ResourceLocation clearBGround = new ResourceLocation("PracticalLogistics:textures/gui/inventoryReader_clear.png");

	public FluidReaderPart part;
	private GuiTextField slotField;
	private GuiTextField searchField;
	public static final int STACK = 0, POS = 1, INV = 2, STORAGE = 3;

	public EntityPlayer player;

	public GuiFluidReader(FluidReaderPart part, EntityPlayer player) {
		super(new ContainerFluidReader(part, player), part);
		this.part = part;
		this.player = player;
	}

	public FluidReader.Modes getSetting() {
		return part.setting.getObject();
	}

	public void initGui() {
		super.initGui();
		this.buttonList.add(new GuiButton(-1, guiLeft + 120 - (18 * 6), guiTop + 7, 65 + 3, 20, getSetting().getName()) {
			public void drawButtonForegroundLayer(int x, int y) {
				drawCreativeTabHoveringText(getSetting().getDescription(), x, y);
			}
		});

		this.buttonList.add(new FilterButton(0, guiLeft + 193, guiTop + 9));
		this.buttonList.add(new FilterButton(1, guiLeft + 193 + 18, guiTop + 9));
		switch (getSetting()) {
		case POS:
			slotField = new GuiTextField(2, this.fontRendererObj, 195 - (18 * 6), 8, 34 + 14, 18);
			slotField.setMaxStringLength(7);
			slotField.setText("" + part.posSlot.getObject());
			break;
		default:
			break;
		}
		searchField = new GuiTextField(3, this.fontRendererObj, 195 - (18 * 3), 9, 13 + 18 * 2, 16);
		searchField.setMaxStringLength(20);
	}

	protected void actionPerformed(GuiButton button) {
		if (button != null) {
			if (button.id == -1) {
				part.setting.incrementEnum();
				part.sendByteBufPacket(2);
				switchState();
				reset();
			}
			if (button.id == 0) {
				part.sortingOrder.incrementEnum();
				part.sendByteBufPacket(5);
			}
			if (button.id == 1) {
				part.sortingType.incrementEnum();
				part.sendByteBufPacket(6);
			}
		}
	}

	public void switchState() {
		/* Logistics.network.sendToServer(new PacketGuiChange(part.getPos(), getSetting() == STACK, LogisticsGui.fluidReader)); if (this.mc.thePlayer.openContainer instanceof ContainerFluidReader) { ((ContainerFluidReader) this.mc.thePlayer.openContainer).addSlots(part, player, getSetting() == STACK); } this.inventorySlots = this.mc.thePlayer.openContainer; */
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		searchField.drawTextBox();
		if (getSetting() == Modes.POS) {
			slotField.drawTextBox();
		}
		if (getSetting() == Modes.FLUID) {
			if (x - guiLeft >= 103 && x - guiLeft <= 103 + 16 && y - guiTop >= 9 && y - guiTop <= 9 + 16) {
				/*
				if (!part.getSelectedInfo().isEmpty()) {
					MonitoredFluidStack stack = ((MonitoredFluidStack) part.getSelectedInfo().get(0));
					if (stack != null) {
						GL11.glDisable(GL11.GL_DEPTH_TEST);
						GL11.glDisable(GL11.GL_LIGHTING);
						List list = new ArrayList();
						list.add(stack.fluidStack.getObject().fluid.getLocalizedName());
						drawHoveringText(list, x - guiLeft, y - guiTop, fontRendererObj);
						GL11.glEnable(GL11.GL_LIGHTING);
						GL11.glEnable(GL11.GL_DEPTH_TEST);
						net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
					}
				}
				*/
			}
		}
		super.drawGuiContainerForegroundLayer(x, y);
	}

	@Override
	protected void mouseClicked(int i, int j, int k) throws IOException {
		super.mouseClicked(i, j, k);
		switch (getSetting()) {
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
	protected void keyTyped(char c, int i) {
		if ((getSetting() == Modes.POS) && slotField.isFocused()) {
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
			try {
				super.keyTyped(c, i);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void setPosSlot(String string) {
		part.posSlot.setObject(Integer.parseInt(string));
		part.sendByteBufPacket(4);
	}

	@Override
	public MonitoredList<MonitoredFluidStack> getGridList() {
		String search = searchField.getText();
		if (search == null || search.isEmpty() || search.equals(" ") || search.equals(""))
			return part.getMonitoredList();
		else {
			MonitoredList<MonitoredFluidStack> searchList = MonitoredList.<MonitoredFluidStack>newMonitoredList();
			for (MonitoredFluidStack stack : (ArrayList<MonitoredFluidStack>) part.getMonitoredList().info.clone()) {
				StoredFluidStack fluidStack = stack.fluidStack.getObject();
				if (stack != null && fluidStack.fluid != null && fluidStack.fluid.getLocalizedName().toLowerCase().contains(searchField.getText().toLowerCase())) {
					searchList.info.add(stack);
				}
			}
			return searchList;
		}
	}

	@Override
	public void onGridClicked(MonitoredFluidStack selection, int pos, int button, boolean empty) {
		if (empty) {
			return;
		}
		if (getSetting() == Modes.FLUID) {
			part.lastInfo = selection;
			part.sendByteBufPacket(0);
		}
		if (getSetting() == Modes.POS) {
			ArrayList<MonitoredFluidStack> currentList = (ArrayList<MonitoredFluidStack>) this.getGridList().info.clone();
			int position = 0;
			for (MonitoredFluidStack stack : currentList) {
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
		// FontHelper.textOffsetCentre(FontHelper.translate("tile.InventoryReader.name").split("
		// ")[0],
		// 197, 8, 1);
		// FontHelper.textOffsetCentre(FontHelper.translate("tile.InventoryReader.name").split("
		// ")[1],
		// 197, 18, 1);
	}

	public void preRender() {
		if (getGridList() != null) {
			final int br = 16 << 20 | 16 << 4;
			final int var11 = br % 65536;
			final int var12 = br / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var11 * 0.8F, var12 * 0.8F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableLighting();
			net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
		}
	}

	public void postRender() {
		/*
		if (!part.getSelectedInfo().isEmpty()) {
			MonitoredFluidStack stack = ((MonitoredFluidStack) part.getSelectedInfo().get(0));
			if (stack != null) {
				StoredFluidStack fluidStack = stack.fluidStack.getObject();
				final int br = 16 << 20 | 16 << 4;
				final int var11 = br % 65536;
				final int var12 = br / 65536;
				GL11.glPushMatrix();
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.drawTexturedModalRect(103, 9, mc.getTextureMapBlocks().getAtlasSprite(fluidStack.fluid.getFluid().getStill().toString()), 16, 16);
				GL11.glPopMatrix();
			}
		}
		 */
	}

	@Override
	public void renderSelection(MonitoredFluidStack selection, int x, int y) {
		StoredFluidStack fluidStack = selection.fluidStack.getObject();
		if (fluidStack.fluid != null) {
			//GL11.glPushMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			ResourceLocation location = fluidStack.fluid.getFluid().getStill();
			this.drawTexturedModalRect(13 + (x * 18), 32 + (y * 18), mc.getTextureMapBlocks().getAtlasSprite(location.toString()), 16, 16);
			//GL11.glPopMatrix();
		}
	}

	@Override
	public void renderToolTip(MonitoredFluidStack selection, int x, int y) {
		StoredFluidStack fluidStack = selection.fluidStack.getObject();
		List list = new ArrayList();
		list.add(fluidStack.fluid.getFluid().getLocalizedName(fluidStack.fluid));
		if (fluidStack.stored != 0) {
			list.add(TextFormatting.GRAY + (String) "Stored: " + fluidStack.stored + " mB");
		}
		drawHoveringText(list, x, y, fontRendererObj);
	}

	@Override
	public ResourceLocation getBackground() {
		if (getSetting() == Modes.FLUID) {
			return stackBGround;
		}
		return clearBGround;
	}

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
				text = (part.sortingType.getObject().getTypeName());
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
				if (part.sortingType.getObject() == SortingType.TEMPERATURE) {
					return 32 + 3 * 16;
				}
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
