package sonar.logistics.api.readers;

/**all the modes used by the Fluid Reader*/
public class FluidReader {

	public static enum Modes {
		FLUID, POS, TANKS, STORAGE;

		public String getDescription() {
			switch (this) {
			case FLUID:
				return "Selected Fluid";
			case POS:
				return "Fluid at the given position";
			case TANKS:
				return "List of Fluids";
			case STORAGE:
				return "Current Tank Usage";
			default:
				return "ERROR";
			}
		}

		public String getName() {
			switch (this) {
			case FLUID:
				return "Fluid";
			case POS:
				return "Pos";
			case TANKS:
				return "Tanks";
			case STORAGE:
				return "Storage";
			default:
				return "ERROR";
			}
		}
	}

	public static enum SortingType {
		STORED, NAME, MODID, TEMPERATURE;

		public String getTypeName() {
			switch (this) {
			case STORED:
				return "Fluids Stored";
			case NAME:
				return "Fluid Name";
			case MODID:
				return "Mode Name";
			case TEMPERATURE:
				return "TEMPERATURE";
			default:
				return "ERROR";

			}
		}
	}
}
