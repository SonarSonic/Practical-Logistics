package sonar.logistics.api.settings;

import sonar.core.helpers.FontHelper;

/**all the modes used by the Inventory Reader*/
public class InventoryReader {
	public static enum Modes {
		STACK, SLOT, POS, INVENTORIES, STORAGE;

		public String getClientName() {
			return FontHelper.translate("pl.inv.mode." + name().toLowerCase());
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

		public String getClientName() {
			return FontHelper.translate("pl.inv.sort." + name().toLowerCase());
		}
	}
}
