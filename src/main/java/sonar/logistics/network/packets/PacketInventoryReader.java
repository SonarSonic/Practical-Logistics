package sonar.logistics.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import sonar.core.SonarCore;
import sonar.core.api.ActionType;
import sonar.core.api.SonarAPI;
import sonar.core.api.StoredItemStack;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.network.PacketCoords;
import sonar.core.network.PacketStackUpdate;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.common.handlers.InventoryReaderHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketInventoryReader extends PacketCoords {

	public ItemStack selected;
	public int button;

	public PacketInventoryReader() {
	}

	public PacketInventoryReader(int x, int y, int z, ItemStack selected, int button) {
		super(x, y, z);
		this.selected = selected;
		this.button = button;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		if (buf.readBoolean()) {
			this.selected = ByteBufUtils.readItemStack(buf);
		}
		this.button = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		if(selected!=null){
			buf.writeBoolean(true);
			ByteBufUtils.writeItemStack(buf, selected);
		}else{
			buf.writeBoolean(false);
		}
		buf.writeInt(button);
	}

	public static class Handler implements IMessageHandler<PacketInventoryReader, IMessage> {
		@Override
		public IMessage onMessage(PacketInventoryReader message, MessageContext ctx) {
			EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
			if (player == null || player.getEntityWorld().isRemote) {
				return null;
			}
			TileEntity te = player.worldObj.getTileEntity(message.xCoord, message.yCoord, message.zCoord);
			Object target = FMPHelper.getHandler(te);
			if (target != null && target instanceof InventoryReaderHandler) {
				InventoryReaderHandler reader = (InventoryReaderHandler) target;
				INetworkCache network = reader.getNetwork(te);
				if (message.button == 2) {
					if (message.selected == null) {
						return null;
					}
					LogisticsAPI.getItemHelper().removeToPlayerInventory(new StoredItemStack(message.selected), (long) 64, network, player, ActionType.PERFORM);
				} else if (player.inventory.getItemStack() != null) {
					ItemStack add = player.inventory.getItemStack().copy();
					int stackSize = Math.min(message.button == 1 ? 1 : 64, add.stackSize);

					StoredItemStack stack = LogisticsAPI.getItemHelper().addItems(new StoredItemStack(add.copy()).setStackSize(stackSize), network, ActionType.PERFORM);

					if (stack == null || stack.stored == 0) {
						add.stackSize = add.stackSize - stackSize;
					} else {
						add.stackSize = (int) (add.stackSize - (stackSize - stack.stored));
					}
					if (add.stackSize <= 0) {
						add = null;
					}
					if (!ItemStack.areItemStacksEqual(add, player.inventory.getItemStack())) {
						player.inventory.setItemStack(add);
						SonarCore.network.sendTo(new PacketStackUpdate(add), (EntityPlayerMP) player);
					}
					return null;
				} else if (player.inventory.getItemStack() == null) {
					if (message.selected == null) {
						return null;
					}
					ItemStack stack = message.selected;
					int extractSize = (int) Math.min(stack.getMaxStackSize(), 64);
					StoredItemStack toAdd = new StoredItemStack(stack.copy()).setStackSize(extractSize);

					StoredItemStack simulate = SonarAPI.getItemHelper().getStackToAdd(toAdd.stored, toAdd, LogisticsAPI.getItemHelper().removeItems(toAdd.copy(), network, ActionType.SIMULATE));
					if (simulate != null && simulate.stored != 0) {
						if (message.button == 1 && simulate.stored != 1) {
							simulate.setStackSize((long) Math.ceil(simulate.getStackSize() / 2));
						}
						StoredItemStack storedStack = SonarAPI.getItemHelper().getStackToAdd(simulate.stored, simulate, LogisticsAPI.getItemHelper().removeItems(simulate.copy(), network, ActionType.PERFORM));
						if (storedStack != null && storedStack.stored != 0) {
							player.inventory.setItemStack(storedStack.getFullStack());
							SonarCore.network.sendTo(new PacketStackUpdate(storedStack.getFullStack()), (EntityPlayerMP) player);
						}
					}
					return null;
				}

			}
			return null;
		}
	}
}
