package sonar.logistics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mcmultipart.multipart.MultipartRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.connections.CableRegistry;
import sonar.logistics.connections.CacheRegistry;
import sonar.logistics.connections.EmitterRegistry;
import sonar.logistics.connections.LogicMonitorCache;
import sonar.logistics.network.LogisticsCommon;
import sonar.logistics.parts.ArrayPart;
import sonar.logistics.parts.DataCablePart;
import sonar.logistics.parts.DataEmitterPart;
import sonar.logistics.parts.DataReceiverPart;
import sonar.logistics.parts.DisplayScreenPart;
import sonar.logistics.parts.FluidReaderPart;
import sonar.logistics.parts.InfoReaderPart;
import sonar.logistics.parts.InventoryReaderPart;
import sonar.logistics.parts.LargeDisplayScreenPart;
import sonar.logistics.parts.NodePart;
import sonar.logistics.registries.InfoLoaderRegistry;
import sonar.logistics.registries.LogicRegistry;
import sonar.logistics.registries.MonitorHandlerRegistry;
import sonar.logistics.utils.SapphireOreGen;

@Mod(modid = Logistics.MODID, name = Logistics.NAME, version = Logistics.VERSION, dependencies = "required-after:SonarCore")
public class Logistics {

	@SidedProxy(clientSide = "sonar.logistics.network.LogisticsClient", serverSide = "sonar.logistics.network.LogisticsCommon")
	public static LogisticsCommon proxy;

	public static final String MODID = "PracticalLogistics";
	public static final String NAME = "Practical Logistics";
	public static final String VERSION = "2.0.0";

	public static SimpleNetworkWrapper network;
	public static Logger logger = (Logger) LogManager.getLogger(MODID);

	// public static InfoTypeRegistry infoTypes = new InfoTypeRegistry();
	// public static InfoInteractionRegistry infoInteraction = new InfoInteractionRegistry();
	// public static ItemFilterRegistry itemFilters = new ItemFilterRegistry();
	// public static EntityProviderRegistry entityProviders = new EntityProviderRegistry();
	public static MonitorHandlerRegistry monitorHandlers = new MonitorHandlerRegistry();

	@Instance(MODID)
	public static Logistics instance;

	public static CreativeTabs creativeTab = new CreativeTabs("Practical Logistics") {
		@Override
		public Item getTabIconItem() {
			return LogisticsItems.partCable;
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

		LogisticsCommon.registerPackets();
		logger.info("Registered Packets");

		LogisticsConfig.initConfiguration(event);
		logger.info("Loaded Configuration");

		LogisticsBlocks.registerBlocks();
		logger.info("Loaded Blocks");

		LogisticsItems.registerItems();
		logger.info("Loaded Items");

		proxy.registerRenderThings();
		logger.info("Registered Renderers");
		MultipartRegistry.registerPart(DataCablePart.class, "practicallogistics:DataCable");
		MultipartRegistry.registerPart(NodePart.class, "practicallogistics:Node");
		MultipartRegistry.registerPart(ArrayPart.class, "practicallogistics:Array");
		MultipartRegistry.registerPart(InventoryReaderPart.class, "practicallogistics:InventoryReader");
		MultipartRegistry.registerPart(FluidReaderPart.class, "practicallogistics:FluidReader");
		MultipartRegistry.registerPart(InfoReaderPart.class, "practicallogistics:InfoReader");
		MultipartRegistry.registerPart(DataEmitterPart.class, "practicallogistics:DataEmitter");
		MultipartRegistry.registerPart(DataReceiverPart.class, "practicallogistics:DataReceiver");
		MultipartRegistry.registerPart(DisplayScreenPart.class, "practicallogistics:DisplayScreen");
		MultipartRegistry.registerPart(LargeDisplayScreenPart.class, "practicallogistics:LargeDisplayScreen");
		/* MultipartRegistry.registerPart(NodePart.class, "practicallogistics:Node"); MultipartRegistry.registerPart(EntityNodePart.class, "practicallogistics:EntityNode"); MultipartRegistry.registerPart(InfoReaderPart.class, "practicallogistics:InfoReader"); MultipartRegistry.registerPart(InventoryReaderPart.class, "practicallogistics:InventoryReader"); MultipartRegistry.registerPart(EnergyReaderPart.class, "practicallogistics:EnergyReader"); MultipartRegistry.registerPart(RedstoneSignallerPart.class, "practicallogistics:RedstoneSignaller"); */
		/* if (SonarLoader.forgeMultipartLoaded()) { ForgeMultipartHandler.init(); logger.info("'Forge Multipart' integration was loaded"); } else { logger.warn("'Forge Multipart' integration wasn't loaded"); } */
		if (LogisticsConfig.sapphireOre) {
			GameRegistry.registerWorldGenerator(new SapphireOreGen(), 1);
			logger.info("Registered Sapphire World Generator");
		} else
			logger.info("Sapphire Ore Generation is disabled in the config");

		ASMDataTable asmDataTable = event.getAsmData();
		LogicRegistry.infoRegistries.addAll(InfoLoaderRegistry.getInfoRegistries(asmDataTable));
		LogicRegistry.customTileHandlers.addAll(InfoLoaderRegistry.getCustomTileHandlers(asmDataTable));
		LogicRegistry.customEntityHandlers.addAll(InfoLoaderRegistry.getCustomEntityHandlers(asmDataTable));
		LogicRegistry.init();
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		LogisticsCrafting.addRecipes();
		logger.info("Registered Crafting Recipes");

		OreDictionary.registerOre("oreSapphire", LogisticsBlocks.sapphire_ore);
		OreDictionary.registerOre("gemSapphire", LogisticsItems.sapphire);
		OreDictionary.registerOre("dustSapphire", LogisticsItems.sapphire_dust);
		logger.info("Registered OreDict");

		MinecraftForge.EVENT_BUS.register(new LogisticsEvents());
		// FMLCommonHandler.instance().bus().register(new LogisticsEvents());
		logger.info("Registered Events");
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new LogisticsCommon());
		logger.info("Registered GUI Handler");

		// infoTypes.register();
		// infoInteraction.register();
		// itemFilters.register();
		// entityProviders.register();
		monitorHandlers.register();
	}

	@EventHandler
	public void postLoad(FMLPostInitializationEvent evt) {
		// logger.info("Registered " + infoTypes.getObjects().size() + " Info Types");
		// logger.info("Registered " + infoInteraction.getObjects().size() + " Info Interactions");
		// logger.info("Registered " + itemFilters.getObjects().size() + " Item Filters");
		// logger.info("Registered " + entityProviders.getObjects().size() + " Entity Providers");
		logger.info("Registered " + monitorHandlers.getObjects().size() + " " + monitorHandlers.registeryType());

		if (Loader.isModLoaded("MineTweaker3")) {
			// MineTweakerIntegration.integrate();
			logger.info("'Mine Tweaker' integration was loaded");
		}
	}

	@EventHandler
	public void onClose(FMLServerStoppingEvent event) {
		EmitterRegistry.removeAll();
		CableRegistry.removeAll();
		CacheRegistry.removeAll();
		LogicMonitorCache.monitoredLists.clear();
	}
}