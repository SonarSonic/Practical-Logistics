package sonar.logistics.client.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
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
import sonar.core.inventory.GuiSonar;
import sonar.core.inventory.SonarButtons;
import sonar.core.network.PacketByteBufServer;
import sonar.core.utils.helpers.FontHelper;
import sonar.logistics.Logistics;
import sonar.logistics.api.ItemFilter;
import sonar.logistics.common.containers.ContainerItemRouter;
import sonar.logistics.common.handlers.ItemRouterHandler;
import sonar.logistics.common.tileentity.TileEntityItemRouter;
import sonar.logistics.info.filters.items.ItemStackFilter;
import sonar.logistics.network.packets.PacketRouterGui;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GuiItemRouter extends GuiSonar {

	public static final ResourceLocation bground = new ResourceLocation("PracticalLogistics:textures/gui/itemRouter.png");
	public static final ResourceLocation itemFilter = new ResourceLocation("PracticalLogistics:textures/gui/itemRouter_item.png");

	public TileEntityItemRouter tile;
	public EntityPlayer player;
	public ItemRouterHandler handler;
	public int xCoord, yCoord, zCoord;
	public int type = 0;
	private float currentScroll;
	private boolean isScrolling;
	private boolean wasClicking;
	public int scrollerLeft, scrollerStart, scrollerEnd, scrollerWidth;
	public int cycle;
	private GuiButton rselectButton;

	public int state = 0;

	public GuiItemRouter(ItemRouterHandler handler, TileEntityItemRouter entity, EntityPlayer player) {
		super(new ContainerItemRouter(entity, player.inventory), entity);
		this.xCoord = entity.xCoord;
		this.yCoord = entity.yCoord;
		this.zCoord = entity.zCoord;
		this.handler = handler;
		this.tile = entity;
		this.player = player;
	}

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.mc.thePlayer.openContainer = this.inventorySlots;
		if (state == 0) {
			this.xSize = 176;
			this.ySize = 233;
		}
		if (state == 1) {
			this.xSize = 176;
			this.ySize = 138;
		}
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		if (state == 0) {
			scrollerLeft = this.guiLeft + 157;
			scrollerStart = this.guiTop + 33;
			scrollerEnd = scrollerStart + 92;
			scrollerWidth = 10;

			this.buttonList.add(new SideButton(0, guiLeft + 9, guiTop + 20, 47, 12, ForgeDirection.getOrientation(handler.side.getInt()).name()));
			for (int i = 1; i < 8; i++) {
				this.buttonList.add(new ListButton(i, guiLeft + 57 + ((i - 1) * 14), guiTop + 20));
			}
		}
		if (state == 1) {
			for (int i = 0; i < 6; i++) {
				this.buttonList.add(new FilterButton(i, guiLeft + 46 + ((i) * 22), guiTop + 23));
			}
			this.buttonList.add(new SideButton(6, guiLeft + 6, guiTop + 6, 34, 12, "DONE"));
		}
	}

	public int getFilterPosition() {
		List<ItemFilter> filters = handler.listType.getInt() == 0 ? handler.whitelist[handler.side.getInt()] : handler.blacklist[handler.side.getInt()];

		if (filters == null) {
			return -1;
		}
		if (handler.filterPos.getInt() == -1 || filters.get(handler.filterPos.getInt()) == null) {
			return -1;
		}
		int size = filters.size();
		int start = (int) (size * this.currentScroll);
		int finish = Math.min(start + 5, size);
		for (int i = start; i < finish; i++) {
			if (filters.get(i) != null) {
				ItemFilter filter = filters.get(i);
				if (filter != null) {
					if (filter.equalFilter(filters.get(handler.filterPos.getInt()))) {
						return i - start;
					}
				}
			}
		}
		return -1;
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		if (state == 0) {
			FontHelper.textCentre(StatCollector.translateToLocal("tile.ItemRouter.name"), xSize, 7, 1);
			GL11.glTranslated(0, 0.5, 0);
			FontHelper.textOffsetCentre(ForgeDirection.getOrientation(handler.side.getInt()).name(), 31, 22, -1);
			GL11.glTranslated(0, -0.5, 0);

			super.drawGuiContainerForegroundLayer(x, y);

			List<ItemFilter> filters = handler.listType.getInt() == 0 ? handler.whitelist[handler.side.getInt()] : handler.blacklist[handler.side.getInt()];
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
							FontHelper.text("Item Filter", 32, 38 + (i * 18) - (start * 18), colour);

							double scale = 0.5;
							RenderItem.getInstance().renderItemAndEffectIntoGUI(fontRendererObj, this.mc.getTextureManager(), stackFilter.getFilters().get(0), 10, 34 + (i * 18) - (start * 18));
						}
					}

				}

				if (x - guiLeft >= 13 && x - guiLeft <= 152 && y - guiTop >= 32 && y - guiTop <= 32 + (7 * 18)) {
					int X = (x - guiLeft - 13) / 18;
					int Y = (y - guiTop - 32) / 18;
					int i = ((start * 5) + Y);

					if (i < filters.size()) {
						ItemFilter filter = filters.get(i);
						if (filter instanceof ItemStackFilter) {
							ItemStackFilter stackFilter = (ItemStackFilter) filter;
							if (stackFilter.getFilters().get(0) != null) {
								List list = new ArrayList();
								List itemTip = stackFilter.getFilters().get(0).getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);
								if (itemTip != null && itemTip.size() != 0)
									list.add(0, stackFilter.getFilters().get(0).getRarity().rarityColor + (String) itemTip.get(0));

								list.add(1, (EnumChatFormatting.GRAY) + "Ignore Damage: " + stackFilter.ignoreDamage);
								list.add(2, (EnumChatFormatting.GRAY) + "Use NBT: " + stackFilter.matchNBT);

								list.add(3, (EnumChatFormatting.GRAY) + "Use OreDict: " + stackFilter.matchOreDict);
								list.add(4, (EnumChatFormatting.GRAY) + "Use Modid: " + stackFilter.matchModid);

								FontRenderer font = stackFilter.getFilters().get(0).getItem().getFontRenderer(stackFilter.getFilters().get(0));
								GL11.glDisable(GL11.GL_DEPTH_TEST);
								GL11.glDisable(GL11.GL_LIGHTING);
								drawHoveringText(list, x - guiLeft, y - guiTop, (font == null ? fontRendererObj : font));
								GL11.glEnable(GL11.GL_LIGHTING);
								GL11.glEnable(GL11.GL_DEPTH_TEST);
								net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
							}
						}

					}
				}

			}
		}
		if (state == 1) {
			FontHelper.textCentre(StatCollector.translateToLocal("Create Item Filter"), xSize, 9, 1);
			super.drawGuiContainerForegroundLayer(x, y);
		}
	}

	public void handleMouseInput() {
		super.handleMouseInput();
		if (state == 0) {
			float lastScroll = currentScroll;
			int i = Mouse.getEventDWheel();

			if (i != 0 && this.needsScrollBars()) {
				List<ItemFilter> filters = handler.listType.getInt() == 0 ? handler.whitelist[handler.side.getInt()] : handler.blacklist[handler.side.getInt()];
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
		if(cycle<40){
			cycle++;
		}else{
			cycle=0;
		}
		if (state == 0) {
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
		if (state == 0) {
			if (button == 0 || button == 1) {

				List<ItemFilter> filters = handler.listType.getInt() == 0 ? handler.whitelist[handler.side.getInt()] : handler.blacklist[handler.side.getInt()];
				if (filters != null) {
					int size = filters.size();
					if (x - guiLeft >= 13 && x - guiLeft <= 152 && y - guiTop >= 32 && y - guiTop <= 32 + (7 * 18)) {
						int start = (int) (size / 12 * this.currentScroll);
						int X = (x - guiLeft - 13) / 18;
						int Y = (y - guiTop - 32) / 18;
						handler.clientClick = ((start * 5) + Y);
						SonarCore.network.sendToServer(new PacketByteBufServer(handler, tile.xCoord, tile.yCoord, tile.zCoord, 8));

					}
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
			if (state == 0) {
				if (button.id == 3) {
					handler.clientStackFilter = new ItemStackFilter();
					state = 1;
					Logistics.network.sendToServer(new PacketRouterGui(tile.xCoord, tile.yCoord, tile.zCoord, state));
					if (this.mc.thePlayer.openContainer instanceof ContainerItemRouter) {
						((ContainerItemRouter) this.mc.thePlayer.openContainer).switchState(player.inventory, tile, state);
					}
					this.inventorySlots = this.mc.thePlayer.openContainer;
					reset();
				} else {
					SonarCore.network.sendToServer(new PacketByteBufServer(handler, tile.xCoord, tile.yCoord, tile.zCoord, button.id));
					if (button.id == 0)
						reset();
				}
			} else if (state == 1) {
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
					System.out.print("modid");
					handler.clientStackFilter.matchModid = !handler.clientStackFilter.matchModid;
				}
				if (button.id == 4) {
					SonarCore.network.sendToServer(new PacketByteBufServer(handler, tile.xCoord, tile.yCoord, tile.zCoord, 2));
				}
				if (button.id == 6) {
					state = 0;
					SonarCore.network.sendToServer(new PacketByteBufServer(handler, tile.xCoord, tile.yCoord, tile.zCoord, -1));
					Logistics.network.sendToServer(new PacketRouterGui(tile.xCoord, tile.yCoord, tile.zCoord, state));
					if (this.mc.thePlayer.openContainer instanceof ContainerItemRouter) {
						((ContainerItemRouter) this.mc.thePlayer.openContainer).switchState(player.inventory, tile, state);
					}
					this.inventorySlots = this.mc.thePlayer.openContainer;
					reset();
				}
			}
		}
	}

	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		this.buttonPressed(button, button.id);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		Minecraft.getMinecraft().getTextureManager().bindTexture(this.getBackground());
		drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		if (state == 0) {
			List<ItemFilter> filters = handler.listType.getInt() == 0 ? handler.whitelist[handler.side.getInt()] : handler.blacklist[handler.side.getInt()];

			if (filters != null) {

				int size = filters.size();
				int start = (int) (size * this.currentScroll);
				int finish = Math.min(start + 5, size);

				int pos = handler.filterPos.getInt() - start;
				if (pos > -1 && handler.filterPos.getInt() < finish) {
					drawTexturedModalRect(this.guiLeft + 9, this.guiTop + 33 + (18 * pos), 0, 233, 154 + 72, 18);
				}
			}
			this.drawTexturedModalRect(scrollerLeft, scrollerStart + (int) ((float) (scrollerEnd - scrollerStart - 17) * this.currentScroll), 176, 0, 8, 15);
		}
	}

	private boolean needsScrollBars() {
		List<ItemFilter> filters = handler.listType.getInt() == 0 ? handler.whitelist[handler.side.getInt()] : handler.blacklist[handler.side.getInt()];
		int size = filters == null ? 0 : filters.size();
		if (size <= 11)
			return false;

		return true;

	}

	public ItemFilter getCurrentFilter() {
		return null;
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
				int config = handler.sideConfigs[handler.side.getInt()].getInt();
				String type = config == 1 ? "Input" : config == 2 ? "Output" : "None";
				text = "Side Config: " + type;
				break;
			case 2:
				int listType = handler.listType.getInt();
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
				int config = handler.sideConfigs[handler.side.getInt()].getInt();
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
				int listType = handler.listType.getInt();
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
				int listType = handler.listType.getInt();
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
				secondary = handler.listType.getInt() != 0;
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
