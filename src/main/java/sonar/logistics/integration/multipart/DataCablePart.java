package sonar.logistics.integration.multipart;

import net.minecraft.block.Block;
import sonar.logistics.api.connecting.CableType;
import sonar.logistics.client.renderers.RenderHandlers;
import sonar.logistics.integration.multipart.ForgeMultipartHandler.MultiPart;
import sonar.logistics.registries.BlockRegistry;

public class DataCablePart extends ChannelledCablePart {

	public DataCablePart() {
		super();
	}

	public DataCablePart(int meta) {
		super(meta);
	}

	@Override
	public Object getSpecialRenderer() {
		return new RenderHandlers.DataCable();
	}

	@Override
	public MultiPart getPartType() {
		return MultiPart.DATA_CABLE;
	}

	@Override
	public CableType getCableType() {
		return CableType.DATA_CABLE;
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.dataCable;
	}
}
