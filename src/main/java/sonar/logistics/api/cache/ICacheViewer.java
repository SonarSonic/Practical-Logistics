package sonar.logistics.api.cache;

import java.util.ArrayList;

import sonar.core.api.StoredFluidStack;
import sonar.core.api.StoredItemStack;
import sonar.core.network.utils.ISyncTile;
import sonar.logistics.api.wrappers.ItemWrapper.SortingDirection;
import sonar.logistics.api.wrappers.ItemWrapper.SortingType;

public interface ICacheViewer extends ISyncTile {

	public int cacheID();

	public ArrayList<StoredItemStack> getItemStacks();

	public ArrayList<StoredFluidStack> getFluidStacks();

	public SortingDirection getSortingDirection();

	public SortingType getSortingType();

	public void setSortingDirection(SortingDirection idr);

	public void setSortingType(SortingType type);

	/** server only */
	public INetworkCache getNetwork();

}
