package sonar.logistics.integration.multipart;

import net.minecraft.block.Block;
import sonar.logistics.client.renderers.RenderHandlers;
import sonar.logistics.registries.BlockRegistry;

public class DataCablePart extends MultiDataCablePart {

	public DataCablePart() {
		super();
	}

	public DataCablePart(int meta) {
		super(meta);
	}

	@Override
	public Object getSpecialRenderer() {
		return new RenderHandlers.BlockCable();
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.dataCable;
	}

	@Override
	public String getType() {
		return "Cable Part";
	}

	@Override
	public boolean unlimitedChannels() {
		return false;
	}
}
