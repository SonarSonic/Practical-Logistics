package sonar.logistics.client.gui;

import java.awt.Color;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.inventory.ContainerMultipartSync;
import sonar.logistics.api.info.DEADILogicInfo;
import sonar.logistics.client.renderers.RenderHandlers;
import sonar.logistics.common.parts.EnergyReaderPart;
import sonar.logistics.info.types.StoredEnergyInfo;

public class GuiEnergyReader extends GuiSelectionList<DEADILogicInfo> {

	public static final ResourceLocation bground = new ResourceLocation("PracticalLogistics:textures/gui/channelSelection.png");

	public EnergyReaderPart part;

	public GuiEnergyReader(EnergyReaderPart part) {
		super(new ContainerMultipartSync(part), part);
		this.part = part;
	}

	@Override
	public List<DEADILogicInfo> getSelectionList() {
		return part.stacks;
	}

	@Override
	public DEADILogicInfo getCurrentSelection() {
		return part.primaryInfo.getObject();
	}

	@Override
	public boolean isEqualSelection(DEADILogicInfo selection, DEADILogicInfo current) {
		return selection.isIdenticalInfo(current) && !SyncType.isGivenType(selection.isMatchingData(current), SyncType.SAVE);
	}

	@Override
	public void renderStrings(int x, int y) {
		FontHelper.textCentre(FontHelper.translate("item.EnergyReader.name"), xSize, 6, 1);
		FontHelper.textCentre("Select the Energy Info you wish to display", xSize, 18, 0);
	}

	@Override
	public void renderSelection(DEADILogicInfo selection, boolean isSelected, int pos) {
		Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(RenderHandlers.modelFolder + "progressBar.png"));
		if (selection instanceof StoredEnergyInfo) {
			StoredEnergyInfo info = (StoredEnergyInfo) selection;
			if (info.stack.capacity != 0) {
				int l = (int) (info.stack.stored * 207 / info.stack.capacity);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				drawTexturedModalRect(25, 32 + (getSelectionHeight() * pos), 176, 10, l, 16);
			}
			if (info.coords != null) {
				String string = (info.coords.block != null ? (info.coords.block.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips)).get(0).toString() : info.coords.coordString);

				int offsetTop = 29;
				if (getViewableSize() == 7) {
					offsetTop = offsetTop + 2;
				}
				FontHelper.text(string + ": " + info.stack.stored + " " + info.stack.energyType.getStorageSuffix(), 28, offsetTop + 5 + (getSelectionHeight() * pos), Color.WHITE.getRGB());
				if (info.coords.block != null) {
					/*
					GL11.glEnable(GL11.GL_DEPTH_TEST);
					RenderItem.getInstance().renderItemAndEffectIntoGUI(fontRendererObj, this.mc.getTextureManager(), info.coords.block, 8, offsetTop + 1 + (getSelectionHeight() * pos));
					RenderHelper.renderStoredItemStackOverlay(this.fontRendererObj, this.mc.getTextureManager(), info.coords.block, 0, 8, offsetTop + 1 + (getSelectionHeight() * pos), null);
					*/
				}
			}
		}
	}

	public void drawSelectionBackground(int offsetTop, int i, int pos) {
		drawTexturedModalRect(this.guiLeft + 7, this.guiTop + offsetTop + (getSelectionHeight() * i), 0, i == pos ? 166 + getSelectionHeight() : 166, 154 + 72, getSelectionHeight()); // }else{
		drawTexturedModalRect(this.guiLeft + 7, this.guiTop + offsetTop + (getSelectionHeight() * i), 0, 166, 154 + 72, getSelectionHeight());
		if (i == pos)
			drawTexturedModalRect(this.guiLeft + 7, this.guiTop + offsetTop + (getSelectionHeight() * i), 0, 166 + getSelectionHeight(), 18, getSelectionHeight());

		// }
	}

	@Override
	public void sendPacket(DEADILogicInfo selection) {
		part.primaryInfo.setObject(selection);
		part.sendByteBufPacket(1);
	}

	@Override
	public ResourceLocation getBackground() {
		return bground;
	}

	public int getViewableSize() {
		return 7;
	}

	public int getSelectionHeight() {
		return 18;
	}
}
