package sonar.logistics.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.integration.fmp.ContainerSyncFMP;
import sonar.core.inventory.ContainerSync;
import sonar.core.network.PacketTileSync;
import sonar.core.network.SonarPackets;
import sonar.core.utils.helpers.NBTHelper;
import sonar.logistics.common.tileentity.TileEntityInfoReader;
import sonar.logistics.integration.multipart.InfoReaderPart;

public class ContainerInfoNode extends ContainerSync {


	public ContainerInfoNode(TileEntityInfoReader entity) {
		super(entity);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (sync != null) {
			if (crafters != null) {
				NBTTagCompound syncData = new NBTTagCompound();
				sync.writeData(syncData, NBTHelper.SyncType.SYNC);
				for (Object o : crafters) {
					if (o != null && o instanceof EntityPlayerMP) {						
						SonarPackets.network.sendTo(new PacketTileSync(tile.xCoord, tile.yCoord, tile.zCoord, syncData), (EntityPlayerMP) o);
						if (tile instanceof TileEntityInfoReader) {
							((TileEntityInfoReader) tile).sendAvailableData((EntityPlayerMP) o);
						}
					}
				}

			}

		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotID);
		return itemstack;
	}

	@Override
	public ItemStack slotClick(int slot, int button, int flag, EntityPlayer player) {
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack() == player.getHeldItem()) {
			return null;
		}
		return super.slotClick(slot, button, flag, player);
	}
	
	public static class Multipart extends ContainerSyncFMP{

		public Multipart(InfoReaderPart entity) {
			super(entity);
		}

		@Override
		public boolean canInteractWith(EntityPlayer player) {
			return true;
		}

		public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
			ItemStack itemstack = null;
			Slot slot = (Slot) this.inventorySlots.get(slotID);
			return itemstack;
		}

		@Override
		public ItemStack slotClick(int slot, int button, int flag, EntityPlayer player) {
			if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack() == player.getHeldItem()) {
				return null;
			}
			return super.slotClick(slot, button, flag, player);
		}
		
		
	}
}
