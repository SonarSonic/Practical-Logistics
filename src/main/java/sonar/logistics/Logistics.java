package sonar.logistics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.commands.CommandResetInfoRegistry;
import sonar.logistics.connections.managers.CableManager;
import sonar.logistics.connections.managers.ClientInfoManager;
import sonar.logistics.connections.managers.DisplayManager;
import sonar.logistics.connections.managers.EmitterManager;
import sonar.logistics.connections.managers.IInfoManager;
import sonar.logistics.connections.managers.NetworkManager;
import sonar.logistics.connections.managers.ServerInfoManager;
import sonar.logistics.info.LogicInfoRegistry;
import sonar.logistics.utils.SapphireOreGen;

@Mod(modid = Logistics.MODID, name = Logistics.NAME, version = Logistics.VERSION, dependencies = "required-after:sonarcore")
public class Logistics {

	@SidedProxy(clientSide = "sonar.logistics.LogisticsClient", serverSide = "sonar.logistics.LogisticsCommon")
	public static LogisticsCommon proxy;

	public static final String MODID = "practicallogistics";
	public static final String NAME = "Practical Logistics";
	public static final String VERSION = "2.0.0";

	public static SimpleNetworkWrapper network;
	public static Logger logger = (Logger) LogManager.getLogger(MODID);
	
	@Instance(MODID)
	public static Logistics instance;

	//@SideOnly(Side.SERVER)
	public NetworkManager networkManager = new NetworkManager();
	//@SideOnly(Side.SERVER)
	public CableManager cableManager = new CableManager();
	//@SideOnly(Side.SERVER)
	public DisplayManager displayManager = new DisplayManager();
	//@SideOnly(Side.SERVER)
	public ServerInfoManager serverManager = new ServerInfoManager();	
	//@SideOnly(Side.CLIENT)
	public ClientInfoManager clientManager = new ClientInfoManager();
	
	public static CreativeTabs creativeTab = new CreativeTabs("Practical Logistics") {
		@Override
		public Item getTabIconItem() {
			return LogisticsItems.partCable;
		}
	};

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger.info("Releasing the Kraken");
		if (!(Loader.isModLoaded("SonarCore") || Loader.isModLoaded("sonarcore"))) {
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
		if (LogisticsConfig.sapphireOre) {
			GameRegistry.registerWorldGenerator(new SapphireOreGen(), 1);
			logger.info("Registered Sapphire World Generator");
		} else
			logger.info("Sapphire Ore Generation is disabled in the config");

		ASMDataTable asmDataTable = event.getAsmData();
		LogisticsASMLoader.loadInfoTypes(asmDataTable);
		LogisticsASMLoader.loadMonitorHandlers(asmDataTable);
		LogicInfoRegistry.infoRegistries.addAll(LogisticsASMLoader.getInfoRegistries(asmDataTable));
		LogicInfoRegistry.customTileHandlers.addAll(LogisticsASMLoader.getCustomTileHandlers(asmDataTable));
		LogicInfoRegistry.customEntityHandlers.addAll(LogisticsASMLoader.getCustomEntityHandlers(asmDataTable));
		LogicInfoRegistry.init();
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		logger.info("Breaking into the pentagon");
		LogisticsCrafting.addRecipes();
		logger.info("Registered Crafting Recipes");

		OreDictionary.registerOre("oreSapphire", LogisticsBlocks.sapphire_ore);
		OreDictionary.registerOre("gemSapphire", LogisticsItems.sapphire);
		OreDictionary.registerOre("dustSapphire", LogisticsItems.sapphire_dust);
		logger.info("Registered OreDict");

		MinecraftForge.EVENT_BUS.register(new LogisticsEvents());
		logger.info("Registered Events");
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new LogisticsCommon());
		logger.info("Registered GUI Handler");
		proxy.registerTextures();
	}

	@EventHandler
	public void postLoad(FMLPostInitializationEvent evt) {
		logger.info("Please Wait: We are saving Harambe with a time machine");
		if (Loader.isModLoaded("MineTweaker3") || Loader.isModLoaded("MineTweaker3".toLowerCase())) {
			MineTweakerIntegration.init();
			logger.info("'Mine Tweaker' integration was loaded");
		}
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandResetInfoRegistry());
	}

	@EventHandler
	public void serverClose(FMLServerStoppingEvent event) {
		EmitterManager.removeAll();
		
	}

	//@SideOnly(Side.SERVER)
	public static NetworkManager getNetworkManager(){
		if(Thread.currentThread().getName()!="Server thread"){
			return null;
		}
		return Logistics.instance.networkManager;
	}

	//@SideOnly(Side.SERVER)
	public static CableManager getCableManager(){
		if(Thread.currentThread().getName()!="Server thread"){
			return null;
		}
		return Logistics.instance.cableManager;
	}

	//@SideOnly(Side.SERVER)
	public static DisplayManager getDisplayManager(){
		if(Thread.currentThread().getName()!="Server thread"){
			return null;
		}
		return Logistics.instance.displayManager;
	}

	//@SideOnly(Side.CLIENT)
	public static ServerInfoManager getServerManager(){
		if(Thread.currentThread().getName()!="Server thread"){
			return null;
		}
		return Logistics.instance.serverManager;
	}

	//@SideOnly(Side.CLIENT)
	public static ClientInfoManager getClientManager(){
		if(Thread.currentThread().getName()=="Server thread"){
			return null;
		}
		return Logistics.instance.clientManager;
	}
	
	public static IInfoManager getInfoManager(boolean isRemote){
		
		
		return !isRemote ? getServerManager() : getClientManager();		
	}
}