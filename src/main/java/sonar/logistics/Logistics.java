package sonar.logistics;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sonar.core.SonarCore;
import sonar.core.integration.SonarAPI;
import sonar.logistics.info.providers.entity.EntityProviderRegistry;
import sonar.logistics.info.providers.tile.TileProviderRegistry;
import sonar.logistics.info.types.InfoTypeRegistry;
import sonar.logistics.integration.LogisticsWailaModule;
import sonar.logistics.integration.multipart.ForgeMultipartHandler;
import sonar.logistics.network.LogisticsCommon;
import sonar.logistics.registries.BlockRegistry;
import sonar.logistics.registries.ChannelRegistry;
import sonar.logistics.registries.CraftingRegistry;
import sonar.logistics.registries.EmitterRegistry;
import sonar.logistics.registries.EventRegistry;
import sonar.logistics.registries.ItemRegistry;
import sonar.logistics.registries.OreDictRegistry;
import sonar.logistics.utils.SapphireOreGen;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = Logistics.modid, name = "Practical Logistics", version = Logistics.version)
public class Logistics {

	@SidedProxy(clientSide = "sonar.logistics.network.LogisticsClient", serverSide = "sonar.logistics.network.LogisticsCommon")
	public static LogisticsCommon proxy;

	public static final String modid = "PracticalLogistics";
	public static final String version = "0.0.3";

	public static SimpleNetworkWrapper network;
	public static Logger logger = (Logger) LogManager.getLogger(modid);

	@Instance(modid)
	public static Logistics instance;

	public static CreativeTabs creativeTab = new CreativeTabs("Practical Logistics") {
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(BlockRegistry.dataCable);
		}
	};

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		if (!Loader.isModLoaded("SonarCore")) {
			logger.fatal("Sonar Core is not loaded");
		} else {
			logger.info("Successfully loaded with Sonar Core");
		}
		network = NetworkRegistry.INSTANCE.newSimpleChannel(modid);
		logger.info("Registered Network");

		SonarCore.registerPackets();
		LogisticsCommon.registerPackets();
		logger.info("Registered Packets");

		LogisticsConfig.initConfiguration(event);
		logger.info("Loaded Configuration");

		BlockRegistry.registerBlocks();
		logger.info("Loaded Blocks");

		ItemRegistry.registerItems();
		logger.info("Loaded Items");

		if (SonarAPI.wailaLoaded()) {
			LogisticsWailaModule.register();
			logger.info("'WAILA' integration was loaded");
		} else {
			logger.warn("'WAILA' integration wasn't loaded");
		}
		if (SonarAPI.forgeMultipartLoaded()) {
			ForgeMultipartHandler.init();
			logger.info("'Forge Multipart' integration was loaded");
		} else {
			logger.warn("'Forge Multipart' integration wasn't loaded");
		}
		if (LogisticsConfig.sapphireOre) {
			GameRegistry.registerWorldGenerator(new SapphireOreGen(), 1);
			logger.info("Registered Sapphire World Generator");
		} else
			logger.info("Sapphire Ore Generation is disabled in the config");
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		InfoTypeRegistry.registerProviders();
		logger.info("Registered " + InfoTypeRegistry.getInfoTypes().size() + " Format Types");

		CraftingRegistry.addRecipes();
		logger.info("Registered Crafting Recipes");

		OreDictRegistry.registerOres();
		logger.info("Registered OreDict");

		MinecraftForge.EVENT_BUS.register(new EventRegistry());
		FMLCommonHandler.instance().bus().register(new EventRegistry());
		logger.info("Registered Events");
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new LogisticsCommon());
		logger.info("Registered GUI Handler");

		proxy.registerRenderThings();
		logger.info("Registered Renderers");

	}

	@EventHandler
	public void postLoad(FMLPostInitializationEvent evt) {
		TileProviderRegistry.registerProviders();
		logger.info("Registered " + TileProviderRegistry.getProviders().size() + " Info Providers");
		EntityProviderRegistry.registerProviders();
		logger.info("Registered " + EntityProviderRegistry.getProviders().size() + " Entity Providers");
	}

	@EventHandler
	public void onClose(FMLServerStoppingEvent event) {
		EmitterRegistry.removeAll();
		ChannelRegistry.removeAll();
		//TileProviderRegistry.removeAll();
		//EntityProviderRegistry.removeAll();
	}
}