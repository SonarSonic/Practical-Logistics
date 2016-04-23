package sonar.logistics.info.interaction;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import sonar.core.api.SonarAPI;
import sonar.core.api.StoredItemStack;
import sonar.core.utils.BlockInteraction;
import sonar.core.utils.BlockInteractionType;
import sonar.logistics.Logistics;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.render.InfoInteractionHandler;
import sonar.logistics.api.render.ScreenType;
import sonar.logistics.info.types.StoredStackInfo;
import sonar.logistics.network.packets.PacketScreenInteraction;
import sonar.logistics.registries.CacheRegistry;

public class StoredStackInteraction extends InfoInteractionHandler<StoredStackInfo> {

	@Override
	public String getName() {
		return "Stored Stack";
	}

	@Override
	public void handleInteraction(StoredStackInfo info, ScreenType type, TileEntity screen, EntityPlayer player, int x, int y, int z, BlockInteraction interact) {
		if (info.stack == null) {
			return;
		}
		Logistics.network.sendToServer(new PacketScreenInteraction.PacketItemStack(screen.xCoord,screen.yCoord,screen.zCoord, x, y, z, interact, info.stack));
		/*
		if (interact.type == BlockInteractionType.RIGHT) {
			if (player.getHeldItem() != null && info.stack.equalStack(player.getHeldItem())) {
				Logistics.network.sendToServer(new PacketScreenInteraction.PacketItemStack(x, y, z, interact, new StoredItemStack(player.getHeldItem())));
			}
		} else if (interact.type != BlockInteractionType.SHIFT_RIGHT) {
			Logistics.network.sendToServer(new PacketScreenInteraction.PacketItemStack(x, y, z, interact, info.stack));

		}
		*/
	}

}
