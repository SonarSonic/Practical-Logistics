package sonar.logistics;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.utils.SimpleProfiler;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.cache.IRefreshCache;
import sonar.logistics.api.cache.RefreshType;
import sonar.logistics.connections.managers.CableManager;
import sonar.logistics.connections.managers.EmitterManager;
import sonar.logistics.connections.managers.LogicMonitorManager;
import sonar.logistics.connections.managers.NetworkManager;

public class LogisticsEvents {
	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.side == Side.CLIENT) {
			return;
		}
		if (event.phase == Phase.END) {
			//SimpleProfiler.start("logic");
			LinkedHashMap<Integer, INetworkCache> currentCache;
			NetworkManager.tick();
			LogicMonitorManager.onServerTick();
			NetworkManager.updateEmitters = false;
			EmitterManager.tick(); // this must happen at the end, since the dirty boolean will be changed and will upset tiles
			//System.out.println(SimpleProfiler.finish("logic") / 1000000000.0);
		}
	}

	@SubscribeEvent
	public void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		LogicMonitorManager.sendFullPacket(event.player);
	}

	@SubscribeEvent
	public void onLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer()) {
			LogicMonitorManager.sendFullPacket(event.player);
		}
	}

}
