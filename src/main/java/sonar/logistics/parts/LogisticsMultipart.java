package sonar.logistics.parts;

import java.util.List;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;
import sonar.core.integration.multipart.SonarMultipart;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.INT;
import sonar.logistics.api.cache.EmptyNetworkCache;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.ILogicTile;
import sonar.logistics.api.connecting.IOperatorProvider;

public abstract class LogisticsMultipart extends SonarMultipart implements ILogicTile, IOperatorProvider {

	public INetworkCache network = EmptyNetworkCache.INSTANCE;
	public SyncTagType.INT registryID = (INT) new SyncTagType.INT(0).setDefault(-1);
	public static final PropertyDirection ORIENTATION = PropertyDirection.create("facing");
	public static final PropertyDirection ROTATION = PropertyDirection.create("rotation");
	{
		syncParts.add(registryID);
	}

	public LogisticsMultipart() {
		super();
	}

	public LogisticsMultipart(AxisAlignedBB collisionBox) {
		super(collisionBox);
	}

	public void setLocalNetworkCache(INetworkCache network) {
		if (!this.wasRemoved) {
			this.network = network;
			this.registryID.setObject(network.getNetworkID());
		}
	}

	public INetworkCache getNetwork() {
		return network;
	}

	public void addInfo(List<String> info) {
		ItemStack dropStack = getItemStack();
		if (dropStack != null)
			info.add(TextFormatting.UNDERLINE + dropStack.getDisplayName());
		info.add("Network ID: " + registryID.getObject());
		info.add("Has channels: " + (this instanceof InfoReaderPart));
	}

	public int getNetworkID() {
		return registryID.getObject();
	}
}
