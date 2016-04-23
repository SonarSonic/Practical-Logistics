package sonar.logistics.network.packets;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.api.ActionType;
import sonar.core.api.BlockCoords;
import sonar.core.api.SonarAPI;
import sonar.core.api.StoredFluidStack;
import sonar.core.api.StoredItemStack;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.PacketCoords;
import sonar.core.network.PacketStackUpdate;
import sonar.core.network.PacketTileEntityHandler;
import sonar.core.utils.BlockInteraction;
import sonar.core.utils.BlockInteractionType;
import sonar.logistics.Logistics;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.ILargeDisplay;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.render.InfoInteractionHandler;
import sonar.logistics.api.render.ScreenType;
import sonar.logistics.common.containers.ContainerItemRouter;
import sonar.logistics.common.handlers.DisplayScreenHandler;
import sonar.logistics.common.handlers.EnergyReaderHandler;
import sonar.logistics.common.handlers.InfoReaderHandler;
import sonar.logistics.common.handlers.InventoryReaderHandler;
import sonar.logistics.common.handlers.LargeDisplayScreenHandler;
import sonar.logistics.info.interaction.InventoryInteraction;
import sonar.logistics.info.types.FluidInventoryInfo;
import sonar.logistics.info.types.FluidStackInfo;
import sonar.logistics.info.types.InventoryInfo;
import sonar.logistics.info.types.StoredEnergyInfo;
import sonar.logistics.info.types.StoredStackInfo;
import sonar.logistics.registries.CacheRegistry;
import sonar.logistics.registries.DisplayRegistry;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketScreenInteraction extends PacketCoords {

	public BlockInteraction interact;
	public int X, Y, Z;

	public PacketScreenInteraction() {
	}

	public PacketScreenInteraction(int x, int y, int z, int X, int Y, int Z, BlockInteraction interact) {
		super(x, y, z);
		this.interact = interact;
		this.X = X;
		this.Y = Y;
		this.Z = Z;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		this.interact = BlockInteraction.readFromBuf(buf);
		this.X = buf.readInt();
		this.Y = buf.readInt();
		this.Z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		interact.writeToBuf(buf);
		buf.writeInt(X);
		buf.writeInt(Y);
		buf.writeInt(Z);
	}

	public static class PacketItemStack extends PacketScreenInteraction {

		public StoredItemStack selected;

		public PacketItemStack() {
		}

		public PacketItemStack(int x, int y, int z, int X, int Y, int Z, BlockInteraction interact, StoredItemStack selected) {
			super(x, y, z, X, Y, Z, interact);
			this.selected = selected;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			super.fromBytes(buf);
			this.selected = StoredItemStack.readFromBuf(buf);
		}

		@Override
		public void toBytes(ByteBuf buf) {
			super.toBytes(buf);
			StoredItemStack.writeToBuf(buf, selected);
		}
	}

	public static class PacketFluidStack extends PacketScreenInteraction {

		public StoredFluidStack selected;

		public PacketFluidStack() {
		}

		public PacketFluidStack(int x, int y, int z, int X, int Y, int Z, BlockInteraction interact, StoredFluidStack selected) {
			super(x, y, z, X, Y, Z, interact);
			this.selected = selected;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			super.fromBytes(buf);
			if (buf.readBoolean())
				this.selected = StoredFluidStack.readFromBuf(buf);
		}

		@Override
		public void toBytes(ByteBuf buf) {
			super.toBytes(buf);
			if (selected != null) {
				buf.writeBoolean(true);
				StoredFluidStack.writeToBuf(buf, selected);
			} else {
				buf.writeBoolean(false);
			}
		}
	}

	public static class ItemHandler extends ScreenHandler<PacketItemStack> {

		public void handlePacket(TileEntity te, PacketItemStack message, MessageContext ctx, ILogicInfo screenInfo, EntityPlayerMP player, boolean doubleClick) {
			INetworkCache cache = null;
			if (screenInfo instanceof InventoryInfo) {
				InventoryInfo info = (InventoryInfo) screenInfo;
				cache = CacheRegistry.getCache(info.cacheID);
			}
			if (screenInfo instanceof StoredStackInfo) {
				StoredStackInfo info = (StoredStackInfo) screenInfo;
				cache = CacheRegistry.getCache(info.cacheID);
			}
			if (message.interact.type == BlockInteractionType.RIGHT) {
				if (player.getHeldItem() != null && message.selected.equalStack(player.getHeldItem())) {
					if (!doubleClick) {
						LogisticsAPI.getItemHelper().insertItemFromPlayer(player, cache, player.inventory.currentItem);
					} else {
						LogisticsAPI.getItemHelper().insertInventoryFromPlayer(player, cache, player.inventory.currentItem);
					}
				}
			} else if (message.interact.type != BlockInteractionType.SHIFT_RIGHT) {
				if (message.selected != null) {
					StoredItemStack extract = LogisticsAPI.getItemHelper().extractItem(cache, message.selected.setStackSize(message.interact.type == BlockInteractionType.LEFT ? 1 : 64));
					if (extract != null) {
						SonarAPI.getItemHelper().spawnStoredItemStack(extract, te.getWorldObj(), message.X, message.Y, message.Z, ForgeDirection.getOrientation(FMPHelper.getMeta(te)));
					}
				}
			}
		}
	}

	public static class FluidHandler extends ScreenHandler<PacketFluidStack> {

		@Override
		public void handlePacket(TileEntity te, PacketFluidStack message, MessageContext ctx, ILogicInfo screenInfo, EntityPlayerMP player, boolean doubleClick) {
			INetworkCache cache = null;
			if (screenInfo instanceof FluidInventoryInfo) {
				FluidInventoryInfo info = (FluidInventoryInfo) screenInfo;
				cache = CacheRegistry.getCache(info.cacheID);
			}
			if (screenInfo instanceof FluidStackInfo) {
				FluidStackInfo info = (FluidStackInfo) screenInfo;
				cache = CacheRegistry.getCache(info.cacheID);
			}
			if (message.interact.type == BlockInteractionType.RIGHT) {
				LogisticsAPI.getFluidHelper().drainHeldItem(player, cache);
			} else if (message.interact.type == BlockInteractionType.LEFT) {
				LogisticsAPI.getFluidHelper().fillHeldItem(player, cache, message.selected.setStackSize(1000));
			} else if (message.interact.type == BlockInteractionType.SHIFT_LEFT) {
				LogisticsAPI.getFluidHelper().fillHeldItem(player, cache, message.selected);
			}
		}
	}
}