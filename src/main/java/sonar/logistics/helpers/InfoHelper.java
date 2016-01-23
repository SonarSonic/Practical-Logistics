package sonar.logistics.helpers;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.helpers.SonarHelper;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.Logistics;
import sonar.logistics.api.Info;
import sonar.logistics.api.ItemFilter;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.providers.EntityProvider;
import sonar.logistics.api.providers.InventoryProvider;
import sonar.logistics.api.providers.TileProvider;
import sonar.logistics.common.tileentity.TileEntityBlockNode;
import sonar.logistics.common.tileentity.TileEntityEntityNode;
import sonar.logistics.info.types.CategoryInfo;
import sonar.logistics.info.types.FluidInfo;
import sonar.logistics.info.types.ProgressInfo;
import cpw.mods.fml.common.network.ByteBufUtils;

public class InfoHelper {

	public static List<Info> getTileInfo(TileEntityBlockNode tileNode) {
		List<TileProvider> providers = Logistics.tileProviders.getObjects();
		List<Info> providerInfo = new ArrayList();
		for (TileProvider provider : providers) {
			ForgeDirection dir = ForgeDirection.getOrientation(SonarHelper.invertMetadata(tileNode.getBlockMetadata())).getOpposite();

			if (provider.canProvideInfo(tileNode.getWorldObj(), tileNode.xCoord + dir.offsetX, tileNode.yCoord + dir.offsetY, tileNode.zCoord + dir.offsetZ, dir)) {
				List<Info> info = new ArrayList();
				provider.getHelperInfo(info, tileNode.getWorldObj(), tileNode.xCoord + dir.offsetX, tileNode.yCoord + dir.offsetY, tileNode.zCoord + dir.offsetZ, dir);
				for (Info blockInfo : info) {
					providerInfo.add(blockInfo);
				}
			}
		}
		Collections.sort(providerInfo, new Comparator<Info>() {
			public int compare(Info str1, Info str2) {
				int res = String.CASE_INSENSITIVE_ORDER.compare(str1.getCategory(), str2.getCategory());
				if (res == 0) {
					res = str1.getCategory().compareTo(str2.getCategory());
				}
				return res;
			}
		});
		return providerInfo;
	}

	public static List<Info> getEntityInfo(TileEntityEntityNode tileNode) {
		List<EntityProvider> providers = Logistics.entityProviders.getObjects();
		List<Info> providerInfo = new ArrayList();
		Entity entity = tileNode.getNearestEntity();
		if (entity == null) {
			return null;
		}
		for (EntityProvider provider : providers) {
			ForgeDirection dir = ForgeDirection.getOrientation(SonarHelper.invertMetadata(tileNode.getBlockMetadata())).getOpposite();
			if (provider.canProvideInfo(entity)) {
				List<Info> info = new ArrayList();
				provider.getHelperInfo(info, entity);
				for (Info blockInfo : info) {
					providerInfo.add(blockInfo);
				}
			}
		}
		Collections.sort(providerInfo, new Comparator<Info>() {
			public int compare(Info str1, Info str2) {
				int res = String.CASE_INSENSITIVE_ORDER.compare(str1.getCategory(), str2.getCategory());
				if (res == 0) {
					res = str1.getCategory().compareTo(str2.getCategory());
				}
				return res;
			}
		});

		return providerInfo;
	}

	public static Info getLatestTileInfo(Info blockInfo, TileEntityBlockNode tileNode) {
		if (blockInfo == null) {
			return null;
		}
		TileProvider provider = Logistics.tileProviders.getRegisteredObject(blockInfo.getProviderID());
		if (provider != null) {
			ForgeDirection dir = ForgeDirection.getOrientation(SonarHelper.invertMetadata(tileNode.getBlockMetadata())).getOpposite();
			List<Info> info = new ArrayList();
			provider.getHelperInfo(info, tileNode.getWorldObj(), tileNode.xCoord + dir.offsetX, tileNode.yCoord + dir.offsetY, tileNode.zCoord + dir.offsetZ, dir);
			for (Info currentInfo : info) {
				if (currentInfo.isEqualType(blockInfo)) {
					return currentInfo;
				}
			}
		}
		blockInfo.emptyData();
		return blockInfo;
	}

	public static Info getLatestEntityInfo(Info blockInfo, TileEntityEntityNode tileNode) {
		if (blockInfo == null) {
			return null;
		}
		Entity entity = tileNode.getNearestEntity();
		if (entity == null) {
			return null;
		}
		EntityProvider provider = Logistics.entityProviders.getRegisteredObject(blockInfo.getProviderID());
		if (provider != null) {
			ForgeDirection dir = ForgeDirection.getOrientation(SonarHelper.invertMetadata(tileNode.getBlockMetadata())).getOpposite();
			List<Info> info = new ArrayList();
			provider.getHelperInfo(info, entity);

			for (Info currentInfo : info) {
				if (currentInfo.isEqualType(blockInfo)) {
					return currentInfo;
				}
			}
		}
		blockInfo.emptyData();
		return blockInfo;
	}

	public static List<StoredItemStack> getTileInventory(TileEntityBlockNode tileNode) {
		List<StoredItemStack> storedStacks = new ArrayList();
		ForgeDirection dir = ForgeDirection.getOrientation(SonarHelper.invertMetadata(tileNode.getBlockMetadata())).getOpposite();
		TileEntity tile = tileNode.getWorldObj().getTileEntity(tileNode.xCoord + dir.offsetX, tileNode.yCoord + dir.offsetY, tileNode.zCoord + dir.offsetZ);
		if (tile == null) {
			return storedStacks;
		}
		boolean specialProvider = false;
		for (InventoryProvider provider : Logistics.inventoryProviders.getObjects()) {
			if (provider.canProvideItems(tileNode.getWorldObj(), tileNode.xCoord + dir.offsetX, tileNode.yCoord + dir.offsetY, tileNode.zCoord + dir.offsetZ, dir)) {
				specialProvider = provider.getItems(storedStacks, tileNode.getWorldObj(), tileNode.xCoord + dir.offsetX, tileNode.yCoord + dir.offsetY, tileNode.zCoord + dir.offsetZ, dir);
			}
		}
		if (!specialProvider && tile instanceof IInventory) {
			addInventoryToList(storedStacks, (IInventory) tile);
		}

		Collections.sort(storedStacks, new Comparator<StoredItemStack>() {
			public int compare(StoredItemStack str1, StoredItemStack str2) {
				if (str1.stored < str2.stored)
					return 1;
				if (str1.stored == str2.stored)
					return 0;
				return -1;
			}
		});
		return storedStacks;
	}

	public static List<StoredItemStack> getEntityInventory(TileEntityEntityNode tileNode) {
		List<StoredItemStack> storedStacks = new ArrayList();
		Entity entity = tileNode.getNearestEntity();
		if (entity == null) {
			return null;
		}
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			addInventoryToList(storedStacks, player.inventory);
		} else if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			addInventoryToList(storedStacks, player.inventory);
		}
		Collections.sort(storedStacks, new Comparator<StoredItemStack>() {
			public int compare(StoredItemStack str1, StoredItemStack str2) {
				if (str1.stored < str2.stored)
					return 1;
				if (str1.stored == str2.stored)
					return 0;
				return -1;
			}
		});
		return storedStacks;

	}

	public static void addInventoryToList(List<StoredItemStack> list, IInventory inv) {
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack != null) {
				addStackToList(list, inv.getStackInSlot(i));
			}
		}
	}

	public static void addStackToList(List<StoredItemStack> list, ItemStack stack) {
		boolean added = false;
		int pos = 0;
		for (StoredItemStack storedStack : list) {
			if (storedStack.equalStack(stack)) {
				list.get(pos).add(stack);
				added = true;
			}
			pos++;
		}
		if (!added) {
			list.add(new StoredItemStack(stack));
		}
	}

	public static Info combineData(Info primary, Info secondary) {
		if (!(primary instanceof CategoryInfo) && !(secondary instanceof CategoryInfo)) {
			if (primary.getDataType() == 0 && secondary.getDataType() == 0) {
				int stored = Integer.parseInt(secondary.getData());
				int max = Integer.parseInt(primary.getData());

				if (stored < 0 || max < 0) {
					return primary;
				}
				int fluidId = -1;
				if (primary instanceof FluidInfo) {
					FluidInfo fluidinfo = (FluidInfo) primary;
					fluidId = fluidinfo.fluidID;
				}
				if (stored > max) {
					if (stored != 0) {
						return new ProgressInfo(max, stored, primary.getDisplayableData(), fluidId);
					}
				}
				if (max != 0) {
					return new ProgressInfo(stored, max, secondary.getDisplayableData(), fluidId);
				}
			} else {
				return primary;
			}
		} else {
			return new StandardInfo((byte) -1, primary.getCategory(), "Combined Data", primary.getDisplayableData() + secondary.getDisplayableData());

		}
		return primary;
	}
/*
	public static Info readInfo(ByteBuf buf) {
		if (buf.readBoolean()) {
			String type = ByteBufUtils.readUTF8String(buf);
			if (Logistics.infoTypes.getRegisteredObject(type) == null) {
				Logistics.logger.warn("Unregistered Info Type: " + type);
				return null;
			}
			Info info = Logistics.infoTypes.getRegisteredObject(type).instance();
			info.readFromBuf(buf);
			return info;

		} else {
			return null;
		}
	}
	*/
	public static void writeInfo(ByteBuf buf, Info info) {
		if (info != null) {
			buf.writeBoolean(true);
			ByteBufUtils.writeUTF8String(buf, info.getName());
			info.writeToBuf(buf);
		} else {
			buf.writeBoolean(false);
		}
	}
	/*
	public static Info readInfo(NBTTagCompound tag) {
		if (tag.hasKey("type")) {
			String type = tag.getString("type");
			if (type.equals("NULLED")) {
				return null;
			}
			if (Logistics.infoTypes.getRegisteredObject(type) == null) {
				Logistics.logger.warn("Unregistered Info Type: " + type);
				return null;
			}
			Info info = Logistics.infoTypes.getRegisteredObject(type).instance();
			info.readFromNBT(tag);

			return info;
		} else {
			return null;
		}
	}
	public static void writeInfo(NBTTagCompound tag, Info info) {
		if (info != null) {
			tag.setString("type", info.getName());
			info.writeToNBT(tag);
		} else {
			tag.setString("type", "NULLED");
		}
	}

	public static ItemFilter readFilter(ByteBuf buf) {
		if (buf.readBoolean()) {
			String type = ByteBufUtils.readUTF8String(buf);
			if (Logistics.infoTypes.getRegisteredObject(type) == null) {
				Logistics.logger.warn("Unregistered Info Type: " + type);
				return null;
			}
			ItemFilter info = Logistics.itemFilters.getRegisteredObject(type).instance();
			info.readFromNBT(ByteBufUtils.readTag(buf));
			return info;

		} else {
			return null;
		}
	}

	public static void writeFilter(ByteBuf buf, ItemFilter info) {
		if (info != null) {
			buf.writeBoolean(true);
			ByteBufUtils.writeUTF8String(buf, info.getName());
			NBTTagCompound tag = new NBTTagCompound();
			info.writeToNBT(tag);
			ByteBufUtils.writeTag(buf, tag);
		} else {
			buf.writeBoolean(false);
		}
	}

	public static ItemFilter readFilter(NBTTagCompound tag) {
		if (tag.hasKey("type")) {
			String type = tag.getString("type");
			if (type.equals("NULLED")) {
				return null;
			}
			if (Logistics.itemFilters.getRegisteredObject(type) == null) {
				Logistics.logger.warn("Unregistered Item Filter: " + type);
				return null;
			}
			ItemFilter filter = Logistics.itemFilters.getRegisteredObject(type).instance();
			filter.readFromNBT(tag);

			return filter;
		} else {
			return null;
		}
	}

	public static void writeFilter(NBTTagCompound tag, ItemFilter info) {
		if (info != null) {
			tag.setString("type", info.getName());
			info.writeToNBT(tag);
		} else {
			tag.setString("type", "NULLED");
		}
	}
	
*/
}
