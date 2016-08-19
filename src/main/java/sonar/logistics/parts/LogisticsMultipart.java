package sonar.logistics.parts;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.util.math.AxisAlignedBB;
import sonar.core.integration.multipart.SonarMultipart;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.INT;
import sonar.logistics.api.cache.EmptyNetworkCache;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.ILogicTile;

public abstract class LogisticsMultipart extends SonarMultipart implements ILogicTile {

	public INetworkCache network = EmptyNetworkCache.INSTANCE;
	public SyncTagType.INT registryID = (INT) new SyncTagType.INT(0).setDefault(-1);
	public static final PropertyDirection ORIENTATION = PropertyDirection.create("facing");
	public static final PropertyDirection ROTATION = PropertyDirection.create("rotation");

	public LogisticsMultipart() {
		super();
		syncParts.add(registryID);
	}
	
	public LogisticsMultipart(AxisAlignedBB collisionBox) {
		super(collisionBox);
		syncParts.add(registryID);
	}

	public void setLocalNetworkCache(INetworkCache network) {
		this.network = network;
		this.registryID.setObject(network.getNetworkID());
	}

	public INetworkCache getNetwork() {
		return network;
	}
}
