package sonar.logistics.integration.nei;

import net.minecraft.item.ItemStack;
import sonar.logistics.Logistics;
import sonar.logistics.client.gui.GuiHammer;
import sonar.logistics.registries.BlockRegistry;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

public class NEILogisticsConfig implements IConfigureNEI {
	
	@Override
	public void loadConfig() {
		API.registerUsageHandler(new HammerNEIHandler());
		API.registerRecipeHandler(new HammerNEIHandler());
		API.registerGuiOverlay(GuiHammer.class, "hammer");

		API.hideItem(new ItemStack(BlockRegistry.displayScreen));
		API.hideItem(new ItemStack(BlockRegistry.redstoneSignaller_on));
		API.hideItem(new ItemStack(BlockRegistry.hammer_air));
		API.hideItem(new ItemStack(BlockRegistry.digitalSign_standing));
		API.hideItem(new ItemStack(BlockRegistry.digitalSign_wall));
	}

	@Override
	public String getName() {
		return Logistics.modid;
	}

	@Override
	public String getVersion() {
		return Logistics.version;
	}
}
