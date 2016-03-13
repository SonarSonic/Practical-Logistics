package sonar.logistics.api.utils;

import sonar.core.utils.IRegistryObject;

/** used for the various energy types created by different mods You can create one yourself for custom energy systems and register it with {@link RegistryWrapper} NOTE: this may not accommodate for all energy systems as some have far more to them */
public class EnergyType implements IRegistryObject {

	public static final EnergyType RF = new EnergyType("Redstone Flux", "RF", "RF/T", 1);
	public static final EnergyType EU = new EnergyType("Energy Units", "EU", "EU/T", 1 / 4);
	public static final EnergyType MJ = new EnergyType("Minecraft Joules", "MJ", "MJ/T", 1 / 10);
	public static final EnergyType AE = new EnergyType("Applied Energistics", "AE", "AE/t", 1 / 2);

	private String name = "";
	private String storage = "";
	private String usage = "";
	private double rfConversion = 1;

	public EnergyType(String name, String storage, String usage, double rfConversion) {
		this.name = name;
		this.storage = storage;
		this.usage = usage;
		this.rfConversion = rfConversion;
	}

	@Override
	public boolean isLoadable() {
		return true;
	}

	public String getName() {
		return name;
	}

	public String getStorageSuffix() {
		return storage;
	}

	public String getUsageSuffix() {
		return usage;
	}

	public String getRFConversion() {
		return usage;
	}
}
