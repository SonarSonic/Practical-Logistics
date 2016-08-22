package sonar.logistics.parts;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import mcmultipart.multipart.ISlottedPart;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import sonar.core.SonarCore;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.inventory.SonarMultipartInventory;
import sonar.core.network.PacketMultipartSync;
import sonar.core.utils.IGuiTile;
import sonar.logistics.Logistics;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.cache.IRefreshCache;
import sonar.logistics.api.cache.RefreshType;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.ITransceiver;
import sonar.logistics.client.gui.GuiArray;
import sonar.logistics.common.containers.ContainerArray;

public class ArrayPart extends SidedMultipart implements ISlottedPart, IConnectionNode, IGuiTile {

	public Map<BlockCoords, EnumFacing> coordList = Collections.EMPTY_MAP;
	public SonarMultipartInventory inventory = new SonarMultipartInventory(this, 8) {
		@Override
		public void setChanged(boolean set) {
			super.setChanged(set);
			updateCoordsList();
		}

		public boolean isItemValidForSlot(int slot, ItemStack stack) {
			return stack != null && stack.getItem() instanceof ITransceiver;
		}
	};

	public ArrayPart() {
		super(0.625, 0.0625 * 1, 0.0625 * 4);
		syncParts.add(inventory);
	}

	public ArrayPart(EnumFacing face) {
		super(face, 0.625, 0.0625 * 1, 0.0625 * 4);
		syncParts.add(inventory);
	}

	/* @Override public <T> T getCapability(Capability<T> capability, EnumFacing facing) { if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) { return (T) inventory; } return super.getCapability(capability, facing); } */
	public void updateCoordsList() {
		Map<BlockCoords, EnumFacing> coordList = new LinkedHashMap();
		for (int i = 0; i < 8; i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack != null && stack.getItem() instanceof ITransceiver && stack.hasTagCompound()) {
				ITransceiver trans = (ITransceiver) stack.getItem();
				coordList.put(trans.getCoords(stack), trans.getDirection(stack));
			}
		}
		this.coordList = coordList;
		if (network instanceof IRefreshCache) {
			IRefreshCache toRefresh = (IRefreshCache) network;
			toRefresh.refreshCache(network.getNetworkID(), RefreshType.FULL);
		}
	}

	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return stack != null && stack.getItem() instanceof ITransceiver;
	}

	@Override
	public void addConnections(Map<BlockCoords, EnumFacing> connections) {
		connections.putAll(coordList);
	}

	@Override
	public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, PartMOP hit) {
		if (!this.getWorld().isRemote) {
			SonarCore.network.sendTo(new PacketMultipartSync(getPos(), this.writeData(new NBTTagCompound(), SyncType.SYNC_OVERRIDE), SyncType.SYNC_OVERRIDE, getUUID()), (EntityPlayerMP) player);
			openGui(player, Logistics.instance);
		}
		return false;
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(LogisticsItems.partNode);
	}

	@Override
	public Object getGuiContainer(EntityPlayer player) {
		return new ContainerArray(player, this);
	}

	@Override
	public Object getGuiScreen(EntityPlayer player) {
		return new GuiArray(player, this);
	}
}
