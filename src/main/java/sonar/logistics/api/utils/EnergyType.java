package sonar.logistics.api.utils;

import sonar.core.utils.IRegistryObject;

/**used for the various energy types created by different mods
 * You can create one yourself for custom energy systems and register it with {@link RegistryWrapper}
 * NOTE: this may not accommodate for all as some have far more to them*/
public abstract class EnergyType implements IRegistryObject {
	
	public static final RedstoneFlux RF = new RedstoneFlux();
	public static final EnergyUnits EU = new EnergyUnits();
	public static final MinecraftJoules MJ = new MinecraftJoules();
	public static final AppliedEnergistics AE = new AppliedEnergistics();
	
	private String name = "";
	private String storage = "";
	private String usage = "";
	private double rfConversion = 1;
	
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

	public static class RedstoneFlux extends EnergyType {
		public RedstoneFlux() {
			super.name = "Redstone Flux";
			super.storage = "RF";
			super.usage = "RF/T";			
		}
	}

	public static class EnergyUnits extends EnergyType {
		public EnergyUnits() {
			super.name = "Energy Units";
			super.storage = "EU";
			super.usage = "EU/T";
			super.rfConversion = 1/4;
		}
	}

	public static class MinecraftJoules extends EnergyType {
		public MinecraftJoules() {
			super.name = "Minecraft Joules";
			super.storage = "MJ";
			super.usage = "MJ/T";
			super.rfConversion = 1/10;
		}
	}
	
	public static class AppliedEnergistics extends EnergyType{
		public AppliedEnergistics() {
			super.name = "Applied Energistics";
			super.storage = "AE";
			super.usage = "AE/t";
			super.rfConversion = 1/2;
		}
	}
}
