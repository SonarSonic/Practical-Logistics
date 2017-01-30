package sonar.logistics.common.multiparts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Lists;

import mcmultipart.multipart.ISlottedPart;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import sonar.core.api.utils.BlockCoords;
import sonar.core.integration.multipart.SonarMultipartHelper;
import sonar.core.inventory.SonarMultipartInventory;
import sonar.core.utils.IGuiTile;
import sonar.logistics.Logistics;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.cache.RefreshType;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.IEntityNode;
import sonar.logistics.api.connecting.IEntityTransceiver;
import sonar.logistics.api.connecting.ITileTransceiver;
import sonar.logistics.api.connecting.ITransceiver;
import sonar.logistics.client.gui.GuiArray;
import sonar.logistics.common.containers.ContainerArray;

public class ArrayPart extends SidedMultipart implements ISlottedPart, IConnectionNode, IEntityNode, IGuiTile {

	public Map<BlockCoords, EnumFacing> coordList = Collections.EMPTY_MAP;
	public ArrayList<Entity> entityList = Lists.newArrayList();

	public SonarMultipartInventory inventory = new SonarMultipartInventory(this, 8) {
		@Override
		public void markDirty() {
			super.markDirty();
			updateConnectionLists();
		}

		public boolean isItemValidForSlot(int slot, ItemStack stack) {
			return stack != null && stack.getItem() instanceof ITransceiver;
		}
	};

	public ArrayPart() {
		super(0.625, 0.0625 * 1, 0.0625 * 4);
		syncList.addPart(inventory);
	}

	public ArrayPart(EnumFacing face) {
		super(face, 0.625, 0.0625 * 1, 0.0625 * 4);
		syncList.addPart(inventory);
	}

	/* @Override public <T> T getCapability(Capability<T> capability, EnumFacing facing) { if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) { return (T) inventory; } return super.getCapability(capability, facing); } */
	public void updateConnectionLists() {
		if (this.isServer()) {
			Map<BlockCoords, EnumFacing> coordList = new LinkedHashMap();
			ArrayList<Entity> entityList = Lists.newArrayList();
			for (int i = 0; i < 8; i++) {
				ItemStack stack = inventory.getStackInSlot(i);
				if (stack != null && stack.hasTagCompound()) {
					if (stack.getItem() instanceof ITileTransceiver) {
						ITileTransceiver trans = (ITileTransceiver) stack.getItem();
						coordList.put(trans.getCoords(stack), trans.getDirection(stack));
					}
					if (stack.getItem() instanceof IEntityTransceiver) {
						IEntityTransceiver trans = (IEntityTransceiver) stack.getItem();
						UUID uuid = trans.getEntityUUID(stack);
						if (uuid != null) {
							for (Entity entity : getWorld().getLoadedEntityList()) {
								if (entity.getPersistentID().equals(uuid)) {
									entityList.add(entity);
									break;
								}
							}
						}
					}
				}
			}
			this.coordList = coordList;
			this.entityList = entityList;
			network.markDirty(RefreshType.FULL);
		}
	}

	public void onLoaded() {
		super.onLoaded();
		this.updateConnectionLists();
	}

	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return stack != null && stack.getItem() instanceof ITransceiver;
	}

	@Override
	public void addConnections(Map<BlockCoords, EnumFacing> connections) {
		connections.putAll(coordList);
	}

	@Override
	public void addEntities(List<Entity> entities) {
		entities.addAll(entityList);
	}

	@Override
	public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, PartMOP hit) {
		if (!this.getWorld().isRemote) {
			SonarMultipartHelper.sendMultipartSyncToPlayer(this, (EntityPlayerMP) player);
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
