package sonar.logistics;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sonar.core.SonarCore;
import sonar.core.integration.SonarLoader;
import sonar.core.registries.EnergyTypeRegistry;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.info.providers.EntityProviderRegistry;
import sonar.logistics.info.providers.TileProviderRegistry;
import sonar.logistics.integration.MineTweakerIntegration;
import sonar.logistics.integration.multipart.ForgeMultipartHandler;
import sonar.logistics.network.LogisticsCommon;
import sonar.logistics.registries.BlockRegistry;
import sonar.logistics.registries.CableRegistry;
import sonar.logistics.registries.CacheRegistry;
import sonar.logistics.registries.CraftingRegistry;
import sonar.logistics.registries.EmitterRegistry;
import sonar.logistics.registries.EventRegistry;
import sonar.logistics.registries.InfoInteractionRegistry;
import sonar.logistics.registries.InfoTypeRegistry;
import sonar.logistics.registries.ItemFilterRegistry;
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

@Mod(modid = Logistics.MODID, name = Logistics.NAME, version = Logistics.VERSION)
public class Logistics {

	@SidedProxy(clientSide = "sonar.logistics.network.LogisticsClient", serverSide = "sonar.logistics.network.LogisticsCommon")
	public static LogisticsCommon proxy;

	public static final String MODID = "PracticalLogistics";
	public static final String NAME = "Practical Logistics";
	public static final String VERSION = "0.1.8";

	public static SimpleNetworkWrapper network;
	public static Logger logger = (Logger) LogManager.getLogger(MODID);

	public static InfoTypeRegistry infoTypes = new InfoTypeRegistry();
	public static InfoInteractionRegistry infoInteraction = new InfoInteractionRegistry();
	public static ItemFilterRegistry itemFilters = new ItemFilterRegistry();
	public static TileProviderRegistry tileProviders = new TileProviderRegistry();
	public static EntityProviderRegistry entityProviders = new EntityProviderRegistry();
	
	@Instance(MODID)
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
		
		LogisticsAPI.init();
		logger.info("Initilised API");
		
		network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
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

		if (SonarLoader.forgeMultipartLoaded()) {
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

		infoTypes.register();
		infoInteraction.register();
		itemFilters.register();
		tileProviders.register();
		entityProviders.register();
	}

	@EventHandler
	public void postLoad(FMLPostInitializationEvent evt) {
		logger.info("Registered " + infoTypes.getObjects().size() + " Info Types");
		logger.info("Registered " + infoInteraction.getObjects().size() + " Info Interactions");
		logger.info("Registered " + itemFilters.getObjects().size() + " Item Filters");
		logger.info("Registered " + tileProviders.getObjects().size() + " Tile Providers");
		logger.info("Registered " + entityProviders.getObjects().size() + " Entity Providers");

		if (Loader.isModLoaded("MineTweaker3")) {
			MineTweakerIntegration.integrate();
			logger.info("'Mine Tweaker' integration was loaded");
		}
	}

	@EventHandler
	public void onClose(FMLServerStoppingEvent event) {
		EmitterRegistry.removeAll();
		CableRegistry.removeAll();
		CacheRegistry.removeAll();
	}
}