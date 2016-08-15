package sonar.logistics.api.readers;

public class InventoryReader {
	public static enum Modes {
		STACK, SLOT, POS, INVENTORIES, STORAGE;
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
