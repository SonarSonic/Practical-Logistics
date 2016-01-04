package sonar.logistics.integration.nei;

import java.awt.Rectangle;

import net.minecraft.client.gui.inventory.GuiContainer;
import sonar.core.integration.nei.AbstractProcessorHandler;
import sonar.core.utils.helpers.FontHelper;
import sonar.core.utils.helpers.RecipeHelper;
import sonar.logistics.client.gui.GuiHammer;
import sonar.logistics.utils.HammerRecipes;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class HammerHandler extends AbstractProcessorHandler {

	@Override
	public RecipeHelper recipeHelper() {
		return HammerRecipes.instance();
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return GuiHammer.class;
	}

	@Override
	public String getRecipeName() {
		return FontHelper.translate("tile.Hammer.name");
	}

	@Override
	public String getGuiTexture() {
		return "PracticalLogistics:textures/gui/hammer_nei.png";
	}

	@Override
	public String getOverlayIdentifier() {
		return "hammer";
	}

	@Override
	public void drawExtras(int recipe) {
		drawProgressBar(71, 13, 176, 0, 24, 16, 48, 0);
	}
	@Override
	public void loadTransferRects() {
		this.transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(66, 19, 24, 10), this.getOverlayIdentifier(), new Object[0]));
	}
}
