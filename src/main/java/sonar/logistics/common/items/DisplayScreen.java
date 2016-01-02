package sonar.logistics.common.items;

import net.minecraft.block.Block;
import sonar.core.common.item.SonarItemScreen;
import sonar.logistics.registries.BlockRegistry;

public class DisplayScreen extends SonarItemScreen {

	@Override
	public Block getScreenBlock() {
		return BlockRegistry.displayScreen;
	}

}
