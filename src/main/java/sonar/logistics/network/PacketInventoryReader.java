package sonar.logistics.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;
import sonar.core.api.SonarAPI;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.ActionType;
import sonar.core.network.PacketMultipart;
import sonar.core.network.PacketMultipartHandler;
import sonar.core.network.PacketStackUpdate;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.parts.InventoryReaderPart;

/** called when the player clicks an item in the inventory reader */
public class PacketInventoryReader extends PacketMultipart {

	public ItemStack selected;
	public int button;

	public PacketInventoryReader() {
	}

	public PacketInventoryReader(UUID partUUID, BlockPos pos, ItemStack selected, int button) {
		super(partUUID, pos);
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
		if (selected != null) {
			buf.writeBoolean(true);
			ByteBufUtils.writeItemStack(buf, selected);
		} else {
			buf.writeBoolean(false);
		}
		buf.writeInt(button);
	}

	public static class Handler extends PacketMultipartHandler<PacketInventoryReader> {
		@Override
		public IMessage processMessage(PacketInventoryReader message, IMultipartContainer target, IMultipart part, MessageContext ctx) {
			EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
			if (player == null || player.getEntityWorld().isRemote || !(part instanceof InventoryReaderPart)) {
				return null;
			}
			InventoryReaderPart reader = (InventoryReaderPart) part;
			INetworkCache network = reader.network;
			if (message.button == 2) {
				if (message.selected == null) {
					return null;
				}
				LogisticsAPI.getItemHelper().removeToPlayerInventory(new StoredItemStack(message.selected), (long) 64, network, player, ActionType.PERFORM);
			} else if (player.inventory.getItemStack() != null) {
				StoredItemStack add = new StoredItemStack(player.inventory.getItemStack().copy());
				int stackSize = Math.min(message.button == 1 ? 1 : 64, add.getValidStackSize());
				StoredItemStack stack = LogisticsAPI.getItemHelper().addItems(add.copy().setStackSize(stackSize), network, ActionType.PERFORM);
				// stack = SonarAPI.getItemHelper().getStackToAdd(stackSize, add, stack);

				ItemStack actualStack = StoredItemStack.getActualStack(stack);
				if (actualStack == null || (actualStack.stackSize != stackSize && !(actualStack.stackSize <= 0)) && !ItemStack.areItemStacksEqual(StoredItemStack.getActualStack(stack), player.inventory.getItemStack())) {
					player.inventory.setItemStack(actualStack);
					SonarCore.network.sendTo(new PacketStackUpdate(actualStack), (EntityPlayerMP) player);
				}
			} else if (player.inventory.getItemStack() == null) {
				if (message.selected == null) {
					return null;
				}

				ItemStack stack = message.selected;
				StoredItemStack toAdd = new StoredItemStack(stack.copy()).setStackSize(Math.min(stack.getMaxStackSize(), 64));
				StoredItemStack removed = LogisticsAPI.getItemHelper().removeItems(toAdd.copy(), network, ActionType.SIMULATE);

				StoredItemStack simulate = SonarAPI.getItemHelper().getStackToAdd(toAdd.stored, toAdd, removed);
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

			}
			return null;
		}
	}
}