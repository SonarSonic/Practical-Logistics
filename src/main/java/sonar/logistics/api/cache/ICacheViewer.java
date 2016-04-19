package sonar.logistics.api.cache;

import java.util.ArrayList;

import sonar.core.api.StoredFluidStack;
import sonar.core.api.StoredItemStack;
import sonar.core.network.utils.ISyncTile;

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

	public static enum SortingDirection {
		DOWN, UP;

		public SortingDirection switchDir() {
			switch (this) {
			case DOWN:
				return UP;
			default:
				return DOWN;
			}
		}
	}

	public static enum SortingType {
		STORED, NAME, MODID;

		public SortingType switchDir() {
			switch (this) {
			case STORED:
				return NAME;
			case NAME:
				return MODID;
			default:
				return STORED;
			}
		}

		public String getTypeName() {
			switch (this) {
			case STORED:
				return "Items Stored";
			case NAME:
				return "Item Name";
			default:
				return "Item Name";
			}
		}
	}
}
