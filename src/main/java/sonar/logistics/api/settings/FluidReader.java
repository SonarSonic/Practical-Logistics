package sonar.logistics.api.settings;

import sonar.core.helpers.FontHelper;

/** all the modes used by the Fluid Reader */
public class FluidReader {

	public static enum Modes {
		SELECTED, POS, TANKS, STORAGE;

		public String getDescription() {
			return FontHelper.translate("pl.fluid.desc." + name().toLowerCase());
		}

		public String getClientName() {
			return FontHelper.translate("pl.fluid.mode." + name().toLowerCase());
		}
	}

	public static enum SortingType {
		STORED, NAME, MODID, TEMPERATURE;

		public String getClientName() {
			return FontHelper.translate("pl.fluid.sort." + name().toLowerCase());
		}
	}
}
