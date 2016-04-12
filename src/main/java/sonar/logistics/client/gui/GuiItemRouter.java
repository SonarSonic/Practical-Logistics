package sonar.logistics.client.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import sonar.core.SonarCore;
import sonar.core.helpers.FontHelper;
import sonar.core.inventory.GuiSonar;
import sonar.core.inventory.SonarButtons;
import sonar.core.network.PacketByteBufServer;
import sonar.logistics.Logistics;
import sonar.logistics.api.utils.ItemFilter;
import sonar.logistics.common.containers.ContainerItemRouter;
import sonar.logistics.common.handlers.ItemRouterHandler;
import sonar.logistics.common.tileentity.TileEntityItemRouter;
import sonar.logistics.info.filters.items.ItemStackFilter;
import sonar.logistics.info.filters.items.OreDictionaryFilter;
import sonar.logistics.network.packets.PacketRouterGui;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GuiItemRouter extends GuiSonar {

	public static final ResourceLocation bground = new ResourceLocation("PracticalLogistics:textures/gui/itemRouter.png");
	public static final ResourceLocation itemFilter = new ResourceLocation("PracticalLogistics:textures/gui/itemRouter_item.png");

	public static final int MAIN = 0, ITEM = 1, ORE = 2;

	public TileEntityItemRouter tile;
	public EntityPlayer player;
	public ItemRouterHandler handler;
	private float currentScroll;
	private boolean isScrolling;
	private boolean wasClicking;
	public int scrollerLeft, scrollerStart, scrollerEnd, scrollerWidth;
	public int cycle;
	private GuiButton rselectButton;
	private GuiTextField oreDictField;
	boolean switching = true;

	public int state = 0;

	public GuiItemRouter(ItemRouterHandler handler, TileEntityItemRouter entity, EntityPlayer player) {
		super(new ContainerItemRouter(entity, player.inventory), entity);
		this.handler = handler;
		this.tile = entity;
		this.player = player;
	}

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(false);
		switching = true;
		this.currentScroll = 0;
		this.mc.thePlayer.openContainer = this.inventorySlots;
		if (state == MAIN) {
			this.xSize = 176;
			this.ySize = 233;
		}
		if (state == ITEM || state == ORE) {
			this.xSize = 176;
			this.ySize = 138;
		}
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		if (state == MAIN) {
			scrollerLeft = this.guiLeft + 157;
			scrollerStart = this.guiTop + 33;
			scrollerEnd = scrollerStart + 92;
			scrollerWidth = 10;

			this.buttonList.add(new SideButton(0, guiLeft + 9, guiTop + 20, 47, 12, ForgeDirection.getOrientation(handler.side.getObject()).name()));
			for (int i = 1; i < 8; i++) {
				this.buttonList.add(new ListButton(i, guiLeft + 57 + ((i - 1) * 14), guiTop + 20));
			}
		} else if (state == ITEM) {
			for (int i = 0; i < 6; i++) {
				this.buttonList.add(new FilterButton(i, guiLeft + 46 + ((i) * 22), guiTop + 23));
			}
			this.buttonList.add(new SideButton(6, guiLeft + 6, guiTop + 6, 34, 12, "DONE"));
		} else if (state == ORE) {
			Keyboard.enableRepeatEvents(true);
			oreDictField = new GuiTextField(this.fontRendererObj, 44, 25, 109, 12);
			oreDictField.setMaxStringLength(20);
			oreDictField.setText(handler.clientOreFilter.oreDict);
			this.buttonList.add(new SideButton(12, guiLeft + 6, guiTop + 6, 34, 12, "DONE"));
		}
		switching = false;
	}

	public int getFilterPosition() {
		List<ItemFilter> filters = handler.listType.getObject() == 0 ? handler.whitelist[handler.side.getObject()] : handler.blacklist[handler.side.getObject()];

		if (filters == null) {
			return -1;
		}
		if (handler.filterPos.getObject() == -1 || filters.get(handler.filterPos.getObject()) == null) {
			return -1;
		}
		int size = filters.size();
		int start = (int) (size * this.currentScroll);
		int finish = Math.min(start + 5, size);
		for (int i = start; i < finish; i++) {
			if (filters.get(i) != null) {
				ItemFilter filter = filters.get(i);
				if (filter != null) {
					if (filter.equalFilter(filters.get(handler.filterPos.getObject()))) {
						return i - start;
					}
				}
			}
		}
		return -1;
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		if (state == MAIN) {
			FontHelper.textCentre(StatCollector.translateToLocal("tile.ItemRouter.name"), xSize, 7, 1);
			GL11.glTranslated(0, 0.5, 0);
			FontHelper.textOffsetCentre(ForgeDirection.getOrientation(handler.side.getObject()).name(), 31, 22, -1);
			GL11.glTranslated(0, -0.5, 0);

			super.drawGuiContainerForegroundLayer(x, y);
			List<ItemFilter> filters = handler.getFilters();
			if (filters != null) {
				int size = filters.size();
				int start = (int) (size * this.currentScroll);
				int finish = Math.min(start + 5, size);
				for (int i = start; i < finish; i++) {
					ItemFilter filter = filters.get(i);
					if (filter != null) {
						int colour = Color.white.getRGB();
						String type = filter.getName() + " : " + i;
						if (filter instanceof ItemStackFilter) {
							ItemStackFilter stackFilter = (ItemStackFilter) filter;
							FontHelper.textOffsetCentre("Item Stack Filter", 90, 38 + (i * 18) - (start * 18), colour);

							double scale = 0.5;
							RenderItem.getInstance().renderItemAndEffectIntoGUI(fontRendererObj, this.mc.getTextureManager(), stackFilter.getFilters().get(0), 10, 34 + (i * 18) - (start * 18));
						}
						if (filter instanceof OreDictionaryFilter) {
							OreDictionaryFilter oreFilter = (OreDictionaryFilter) filter;
							if (oreFilter.getFilters() != null && oreFilter.getFilters().size() != 0) {
								RenderItem.getInstance().renderItemAndEffectIntoGUI(fontRendererObj, this.mc.getTextureManager(), oreFilter.getFilters().get(0), 10, 34 + (i * 18) - (start * 18));
							}
							FontHelper.textOffsetCentre("Ore Dictionary Filter ", 90, 38 + (i * 18) - (start * 18), colour);
						}
					}

				}

				if (x - guiLeft >= 13 && x - guiLeft <= 152 && y - guiTop >= 32 && y - guiTop <= 32 + (7 * 18)) {
					int X = (x - guiLeft - 13) / 18;
					int Y = (y - guiTop - 32) / 18;
					int i = ((start) + Y);
					if (i < filters.size() && Y < finish) {
						ItemFilter filter = filters.get(i);
						if (filter instanceof ItemStackFilter) {
							ItemStackFilter stackFilter = (ItemStackFilter) filter;
							if (stackFilter.getFilters().get(0) != null) {
								List list = new ArrayList();
								List itemTip = stackFilter.getFilters().get(0).getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);
								if (itemTip != null && itemTip.size() != 0)
									list.add(0, stackFilter.getFilters().get(0).getRarity().rarityColor + (String) itemTip.get(0));
								list.add(1, (EnumChatFormatting.GRAY) + "Use NBT: " + (stackFilter.matchNBT ? EnumChatFormatting.WHITE : "") + stackFilter.matchNBT);
								list.add(2, (EnumChatFormatting.GRAY) + "Use OreDict: " + (stackFilter.matchOreDict ? EnumChatFormatting.WHITE : "") + stackFilter.matchOreDict);
								list.add(3, (EnumChatFormatting.GRAY) + "Ignore Damage: " + (stackFilter.ignoreDamage ? EnumChatFormatting.WHITE : "") + stackFilter.ignoreDamage);
								list.add(4, (EnumChatFormatting.GRAY) + "Use Modid: " + (stackFilter.matchModid ? EnumChatFormatting.WHITE : "") + stackFilter.matchModid);
								FontRenderer font = stackFilter.getFilters().get(0).getItem().getFontRenderer(stackFilter.getFilters().get(0));
								drawSpecialToolTip(list, x, y, font);

							}
						}

						if (filter instanceof OreDictionaryFilter) {
							OreDictionaryFilter oreFilter = (OreDictionaryFilter) filter;
							if (oreFilter.getFilters() != null && !oreFilter.getFilters().isEmpty() && oreFilter.getFilters().get(0) != null) {
								List list = new ArrayList();
								list.add(0, (EnumChatFormatting.WHITE) + "Ore String: " + (EnumChatFormatting.AQUA) + oreFilter.oreDict);
								drawSpecialToolTip(list, x, y, null);
							}
						}

					}
				}

			}
		} else if (state == ITEM) {
			FontHelper.textCentre(StatCollector.translateToLocal("Create Item Filter"), xSize, 9, 1);
			super.drawGuiContainerForegroundLayer(x, y);
		} else if (state == ORE) {
			FontHelper.textCentre(StatCollector.translateToLocal("Create Ore Filter"), xSize, 9, 1);
			oreDictField.drawTextBox();
			List<ItemStack> filters = handler.clientOreFilter.getFilters();
			if (filters != null && filters.size() != 0) {
				ItemStack stack = filters.get(0);
				net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
				RenderItem.getInstance().renderItemAndEffectIntoGUI(fontRendererObj, this.mc.getTextureManager(), stack, 23, 23);

				if (x - guiLeft >= 23 && x - guiLeft <= 23 + 16 && y - guiTop >= 23 && y - guiTop <= 23 + 16) {
					this.drawNormalToolTip(stack, x, y);
				}
			}
			super.drawGuiContainerForegroundLayer(x, y);
		}
	}

	public void handleMouseInput() {
		super.handleMouseInput();
		if (state == MAIN) {
			float lastScroll = currentScroll;
			int i = Mouse.getEventDWheel();

			if (i != 0 && this.needsScrollBars()) {
				List<ItemFilter> filters = handler.getFilters();
				int size = filters == null ? 0 : filters.size();
				int j = size + 1;

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
	}

	public void drawScreen(int x, int y, float var) {
		super.drawScreen(x, y, var);
		if (cycle < 40) {
			cycle++;
		} else {
			cycle = 0;
		}
		if (state == MAIN) {
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
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		if (state == MAIN && !switching) {
			if (button == 1 || button == 0) {
				List<ItemFilter> filters = handler.getFilters();
				if (filters != null) {
					int size = filters.size();
					if (x - guiLeft >= 13 && x - guiLeft <= 152 && y - guiTop >= 32 && y - guiTop <= 32 + (7 * 18)) {
						int start = (int) (size * this.currentScroll);
						int Y = (y - guiTop - 32) / 18;
						int i = ((start) + Y);
						int finish = Math.min(start + 5, size);
						if (Y < finish) {
							if (button == 0 && !switching) {
								handler.clientClick = i;
								SonarCore.network.sendToServer(new PacketByteBufServer(handler, tile.xCoord, tile.yCoord, tile.zCoord, 8));
							} else {
								if (i < filters.size() && filters.get(i) != null) {
									handler.clientClick = i;
									ItemFilter filter = filters.get(i);
									if (filter instanceof ItemStackFilter) {
										handler.editStack = i;
										handler.editOre = -1;
										SonarCore.network.sendToServer(new PacketByteBufServer(handler, tile.xCoord, tile.yCoord, tile.zCoord, 9));
										switchState(1);
									} else {
										handler.editStack = -1;
										handler.editOre = i;
										SonarCore.network.sendToServer(new PacketByteBufServer(handler, tile.xCoord, tile.yCoord, tile.zCoord, 9));
										switchState(2);
									}

								}
							}
						}
					}
				}
			}
		} else if (state == ORE) {
			oreDictField.mouseClicked(x - guiLeft, y - guiTop, button);
		}
	}

	protected void mouseMovedOrUp(int x, int y, int id) {
		super.mouseMovedOrUp(x, y, id);
		if (this.rselectButton != null && id == 1) {
			this.rselectButton.mouseReleased(x, y);
			this.rselectButton = null;
		}

	}

	public void buttonPressed(GuiButton button, int buttonID) {
		System.out.print(buttonID);
		if (button != null) {
			if (state == MAIN) {
				if (button.id == 3) {
					switchState(1);
				} else if (button.id == 4) {
					switchState(2);
				} else {
					SonarCore.network.sendToServer(new PacketByteBufServer(handler, tile.xCoord, tile.yCoord, tile.zCoord, button.id));
					if (button.id == 0) {
						this.currentScroll = 0;
						reset();
					}
					if (button.id == 7 || button.id == 2) {
						this.currentScroll = 0;
					}

				}
			} else if (state == ITEM) {
				if (button.id == 0) {
					handler.clientStackFilter.matchNBT = !handler.clientStackFilter.matchNBT;
				}
				if (button.id == 1) {
					handler.clientStackFilter.matchOreDict = !handler.clientStackFilter.matchOreDict;
				}
				if (button.id == 2) {
					handler.clientStackFilter.ignoreDamage = !handler.clientStackFilter.ignoreDamage;
				}
				if (button.id == 3) {
					handler.clientStackFilter.matchModid = !handler.clientStackFilter.matchModid;
				}
				if (button.id == 4) {
					SonarCore.network.sendToServer(new PacketByteBufServer(handler, tile.xCoord, tile.yCoord, tile.zCoord, 2));
				}
				if (button.id == 6) {
					switching = true;
					SonarCore.network.sendToServer(new PacketByteBufServer(handler, tile.xCoord, tile.yCoord, tile.zCoord, -1));
					handler.editStack = -1;
					switchState(0);
				}
			} else if (state == ORE) {
				if (button.id == 12) {
					switching = true;
					SonarCore.network.sendToServer(new PacketByteBufServer(handler, tile.xCoord, tile.yCoord, tile.zCoord, -2));
					handler.editOre = -1;
					switchState(0);
				}
			}
		}
	}

	public void switchState(int state) {
		switching = true;
		this.currentScroll = 0;
		Logistics.network.sendToServer(new PacketRouterGui(tile.xCoord, tile.yCoord, tile.zCoord, state));
		if (this.mc.thePlayer.openContainer instanceof ContainerItemRouter) {
			((ContainerItemRouter) this.mc.thePlayer.openContainer).switchState(player.inventory, tile, state);
		}
		this.inventorySlots = this.mc.thePlayer.openContainer;
		this.state = state;
		reset();
	}

	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		System.out.print("hi");
		this.buttonPressed(button, button.id);
	}

	@Override
	protected void keyTyped(char c, int i) {
		if (state == ORE && oreDictField.isFocused()) {
			if (c == 13 || c == 27) {
				oreDictField.setFocused(false);
			} else {
				oreDictField.textboxKeyTyped(c, i);
				final String text = oreDictField.getText();
				if (text.isEmpty() || text == "" || text == null) {
					handler.clientOreFilter.oreDict = " ";
				} else {
					handler.clientOreFilter.oreDict = text;
				}
			}
		} else {
			super.keyTyped(c, i);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		Minecraft.getMinecraft().getTextureManager().bindTexture(this.getBackground());
		drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		if (state == MAIN) {
			List<ItemFilter> filters = handler.getFilters();

			if (filters != null) {

				int size = filters.size();
				int start = (int) (size * this.currentScroll);
				int finish = Math.min(start + 5, size);

				int pos = handler.filterPos.getObject() - start;
				if (pos > -1 && handler.filterPos.getObject() < finish) {
					drawTexturedModalRect(this.guiLeft + 9, this.guiTop + 33 + (18 * pos), 0, 233, 154 + 72, 18);
				}
			}
			this.drawTexturedModalRect(scrollerLeft, scrollerStart + (int) ((float) (scrollerEnd - scrollerStart - 17) * this.currentScroll), 176, 0, 8, 15);
		}
	}

	private boolean needsScrollBars() {
		List<ItemFilter> filters = handler.getFilters();
		int size = filters == null ? 0 : filters.size();
		if (size <= 5)
			return false;

		return true;

	}

	@SideOnly(Side.CLIENT)
	public class ListButton extends SonarButtons.AnimatedButton {
		public int id;

		public ListButton(int id, int x, int y) {
			super(id, x, y, bground, 11, 11);
			this.id = id;
		}

		public void func_146111_b(int x, int y) {
			String text = "BUTTON TEXT";
			switch (id) {
			case 1:
				int config = handler.sideConfigs[handler.side.getObject()].getObject();
				String type = config == 1 ? "Input" : config == 2 ? "Output" : "None";
				text = "Side Config: " + type;
				break;
			case 2:
				int listType = handler.listType.getObject();
				text = listType == 0 ? "Whitelist" : "Blacklist";
				break;
			case 3:
				text = "Item Filter";
				break;

			case 4:
				text = "OreDict Filter";
				break;
			case 5:
				text = "Move Up";
				break;
			case 6:
				text = "Move Down";
				break;
			case 7:
				text = "Delete";
				break;
			}

			drawCreativeTabHoveringText(text, x, y);
		}

		@Override
		public void onClicked() {
		}

		@Override
		public int getTextureX() {
			if (id == 1) {
				int config = handler.sideConfigs[handler.side.getObject()].getObject();
				return 176 + (config * 12);
			}

			if (id != 2 && isButtonDown) {
				return 176 + 24;
			}
			return 176;
		}

		@Override
		public int getTextureY() {
			if (id == 1) {
				return 15;
			}
			if (id == 2) {
				int listType = handler.listType.getObject();
				return listType == 0 ? 15 + 12 : 15 + 24;
			}
			return 15 + (id * 12);
		}

	}

	@SideOnly(Side.CLIENT)
	public class FilterButton extends SonarButtons.AnimatedButton {
		public int id;

		public FilterButton(int id, int x, int y) {
			super(id, x, y, itemFilter, 15, 15);
			this.id = id;
		}

		public void func_146111_b(int x, int y) {
			String text = "BUTTON TEXT";
			ItemStackFilter filter = handler.clientStackFilter;
			switch (id) {
			case 0:
				text = (filter.matchNBT ? EnumChatFormatting.WHITE : EnumChatFormatting.GRAY) + "Use NBT: " + filter.matchNBT;
				break;
			case 1:
				text = (filter.matchOreDict ? EnumChatFormatting.WHITE : EnumChatFormatting.GRAY) + "Use OreDict: " + filter.matchOreDict;
				break;
			case 2:
				text = (filter.ignoreDamage ? EnumChatFormatting.WHITE : EnumChatFormatting.GRAY) + "Ignore Damage: " + filter.ignoreDamage;
				break;

			case 3:
				text = (filter.matchModid ? EnumChatFormatting.WHITE : EnumChatFormatting.GRAY) + "Use MODID: " + filter.matchModid;
				break;
			case 4:
				int listType = handler.listType.getObject();
				text = listType == 0 ? "Whitelist" : "Blacklist";
				break;
			}

			drawCreativeTabHoveringText(text, x, y);
		}

		@Override
		public void onClicked() {
		}

		@Override
		public int getTextureX() {
			boolean secondary = false;
			ItemStackFilter filter = handler.clientStackFilter;
			switch (id) {
			case 0:
				secondary = !filter.matchNBT;
				break;
			case 1:
				secondary = !filter.matchOreDict;
				break;
			case 2:
				secondary = !filter.ignoreDamage;
				break;
			case 3:
				secondary = !filter.matchModid;
				break;
			case 4:
				secondary = handler.listType.getObject() != 0;
				break;
			}

			return !secondary ? 176 : 176 + 16;
		}

		@Override
		public int getTextureY() {
			return (id * 16);
		}

	}

	@SideOnly(Side.CLIENT)
	public class SideButton extends GuiButton {

		public SideButton(int id, int x, int y, int width, int height, String tex) {
			super(id, x, y, width, height, tex);
		}

		@Override
		public void drawButton(Minecraft minecraft, int x, int y) {

		}

		public void func_146111_b(int x, int y) {
			if (displayString != null)
				drawCreativeTabHoveringText(displayString, x, y);
		}
	}

	@Override
	public ResourceLocation getBackground() {
		if (state == 0) {
			return bground;
		} else {
			return itemFilter;
		}
	}

}
