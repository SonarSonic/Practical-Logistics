package sonar.logistics.common.multiparts;

import java.util.Map;

import mcmultipart.multipart.ISlottedPart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.BlockCoords;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.cache.RefreshType;
import sonar.logistics.api.connecting.IConnectionNode;

public class NodePart extends SidedMultipart implements IConnectionNode, ISlottedPart{

	public NodePart() {
		super(0.875, 0, 0.0625);
	}

	public NodePart(EnumFacing face) {
		super(face, 0.875, 0, 0.0625);
	}

	public void setLocalNetworkCache(INetworkCache network) {
		super.setLocalNetworkCache(network);
		//network.markDirty(RefreshType.CONNECTED_BLOCKS);		
	}
	
	@Override
	public void addConnections(Map<BlockCoords, EnumFacing> connections) {
		BlockCoords tileCoords = new BlockCoords(getPos().offset(face), getWorld().provider.getDimension());
		connections.put(tileCoords, face);
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(LogisticsItems.partNode);
	}
}
