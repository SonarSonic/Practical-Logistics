package sonar.logistics.info.providers;

import sonar.core.utils.helpers.RegistryHelper;
import sonar.logistics.api.providers.TileProvider;
import sonar.logistics.info.providers.tile.AE2CraftingProvider;
import sonar.logistics.info.providers.tile.AE2EnergyProvider;
import sonar.logistics.info.providers.tile.AE2GridProvider;
import sonar.logistics.info.providers.tile.BlockProvider;
import sonar.logistics.info.providers.tile.BloodMagicProvider;
import sonar.logistics.info.providers.tile.BuildcraftProvider;
import sonar.logistics.info.providers.tile.CalculatorMachineProvider;
import sonar.logistics.info.providers.tile.CalculatorProvider;
import sonar.logistics.info.providers.tile.EUEnergyProvider;
import sonar.logistics.info.providers.tile.GrowableProvider;
import sonar.logistics.info.providers.tile.HammerProvider;
import sonar.logistics.info.providers.tile.IC2ReactorProvider;
import sonar.logistics.info.providers.tile.LogisticsPipesProvider;
import sonar.logistics.info.providers.tile.ManaProvider;
import sonar.logistics.info.providers.tile.MekanismGeneralProvider;
import sonar.logistics.info.providers.tile.MekanismReactorProvider;
import sonar.logistics.info.providers.tile.RFEnergyProvider;
import sonar.logistics.info.providers.tile.RotaryCraftProvider;
import sonar.logistics.info.providers.tile.ThaumcraftProvider;
import sonar.logistics.info.providers.tile.VanillaTileEntityProvider;

public class TileProviderRegistry extends RegistryHelper<TileProvider> {

	@Override
	public void register() {
		registerObject(new BlockProvider());
		registerObject(new RFEnergyProvider());
		// registerObject(new TankProvider());
		registerObject(new GrowableProvider());
		registerObject(new VanillaTileEntityProvider());
		registerObject(new CalculatorProvider());
		registerObject(new ThaumcraftProvider());
		registerObject(new BloodMagicProvider());
		registerObject(new ManaProvider());
		registerObject(new EUEnergyProvider());
		registerObject(new IC2ReactorProvider());
		registerObject(new HammerProvider());
		registerObject(new AE2CraftingProvider());
		registerObject(new AE2EnergyProvider());
		registerObject(new AE2GridProvider());
		registerObject(new LogisticsPipesProvider());
		registerObject(new BuildcraftProvider());
		registerObject(new MekanismGeneralProvider());
		registerObject(new MekanismReactorProvider());
		registerObject(new RotaryCraftProvider());
		registerObject(new CalculatorMachineProvider());

	}

	@Override
	public String registeryType() {
		return "Tile Provider";
	}

}