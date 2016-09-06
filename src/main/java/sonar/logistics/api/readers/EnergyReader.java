package sonar.logistics.api.readers;

/**all the modes used by the Fluid Reader*/
public class EnergyReader {

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
		STORED, CAPACITY, INPUT, TYPE, NAME;
		
		public String getTypeName() {
			switch(this){
			case CAPACITY:
				return "Energy Capacity";
			case INPUT:
				return "Energy Input";
			case NAME:
				return "Block Name";
			case STORED:
				return "Energy Stored";
			case TYPE:
				return "Energy Type";
			default:
				return "ERROR";
			
			}
		}
	}
}
