package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.BlockCoords;
import sonar.logistics.Logistics;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.IEntityNode;
import sonar.logistics.api.providers.EntityProvider;
import sonar.logistics.api.providers.InventoryHandler;
import sonar.logistics.api.providers.TileProvider;
import sonar.logistics.info.types.CategoryInfo;
import sonar.logistics.info.types.FluidInfo;
import sonar.logistics.info.types.ProgressInfo;

public class InfoHelper {

	public static List<Info> getInfo(BlockCoords coords) {
		List<Info> infoList = new ArrayList();
		TileEntity tile = coords.getTileEntity();
		if (tile == null) {
			return infoList;
		}
		if (tile instanceof IConnectionNode) {
			infoList = InfoHelper.getTileInfo((IConnectionNode) tile);
		} else if (tile instanceof IEntityNode) {
			infoList = InfoHelper.getEntityInfo((IEntityNode) tile);
		}
		return infoList;

	}

	public static List<Info> getTileInfo(IConnectionNode tileNode) {
		List<TileProvider> providers = Logistics.tileProviders.getObjects();
		List<Info> providerInfo = new ArrayList();

		Map<BlockCoords, ForgeDirection> connections = tileNode.getConnections();
		for (TileProvider provider : providers) {
			for (Map.Entry<BlockCoords, ForgeDirection> entry : connections.entrySet()) {
				BlockCoords coords = entry.getKey();
				if (provider.canProvideInfo(coords.getWorld(), coords.getX(), coords.getY(), coords.getZ(), entry.getValue())) {
					List<Info> info = new ArrayList();
					provider.getHelperInfo(info, coords.getWorld(), coords.getX(), coords.getY(), coords.getZ(), entry.getValue());
					for (Info blockInfo : info) {
						providerInfo.add(blockInfo);
					}
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

	public static List<Info> getEntityInfo(IEntityNode tileNode) {
		List<EntityProvider> providers = Logistics.entityProviders.getObjects();
		List<Info> providerInfo = new ArrayList();

		List<Entity> entityList = tileNode.getEntities();
		for (EntityProvider provider : providers) {
			for (Entity entity : entityList) {
				if (entity != null && provider.canProvideInfo(entity)) {
					List<Info> info = new ArrayList();
					provider.getHelperInfo(info, entity);
					for (Info blockInfo : info) {
						providerInfo.add(blockInfo);
					}
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

	public static Info getLatestTileInfo(Info tileInfo, IConnectionNode tileNode) {
		if (tileNode == null || tileInfo == null) {
			return null;
		}
		TileProvider provider = Logistics.tileProviders.getRegisteredObject(tileInfo.getProviderID());
		if (provider == null) {
			return null;
		}

		Map<BlockCoords, ForgeDirection> connections = tileNode.getConnections();
		for (Map.Entry<BlockCoords, ForgeDirection> entry : connections.entrySet()) {
			BlockCoords coords = entry.getKey();
			if (provider.canProvideInfo(coords.getWorld(), coords.getX(), coords.getY(), coords.getZ(), entry.getValue())) {
				List<Info> info = new ArrayList();
				provider.getHelperInfo(info, coords.getWorld(), coords.getX(), coords.getY(), coords.getZ(), entry.getValue());
				for (Info currentInfo : info) {
					if (currentInfo.equals(tileInfo)) {
						return currentInfo;
					}
				}
			}
		}

		return null;
	}

	public static Info getLatestEntityInfo(Info entityInfo, IEntityNode entityNode) {
		if (entityInfo == null) {
			return null;
		}
		List<Entity> entityList = entityNode.getEntities();
		EntityProvider provider = Logistics.entityProviders.getRegisteredObject(entityInfo.getProviderID());
		if (provider != null) {
			for (Entity entity : entityList) {
				if (entity != null && provider.canProvideInfo(entity)) {
					List<Info> info = new ArrayList();
					provider.getHelperInfo(info, entity);

					for (Info currentInfo : info) {
						if (currentInfo.equals(entityInfo)) {
							return currentInfo;
						}
					}
				}
			}
		}
		return null;
	}

	public static StoredItemStack getStack(List<BlockCoords> connections, int slot) {
		if (connections == null) {
			return null;
		}
		for (BlockCoords connect : connections) {
			Object tile = connect.getTileEntity();
			if (tile != null) {
				if (tile instanceof IConnectionNode) {
					return getTileStack((IConnectionNode) tile, slot);
				}
				if (tile instanceof IEntityNode) {
					return getEntityStack((IEntityNode) tile, slot);
				}
			}
		}

		return null;
	}

	public static StoredItemStack getTileStack(IConnectionNode node, int slot) {
		Map<BlockCoords, ForgeDirection> connections = node.getConnections();
		for (InventoryHandler provider : Logistics.inventoryProviders.getObjects()) {
			for (Map.Entry<BlockCoords, ForgeDirection> entry : connections.entrySet()) {
				TileEntity tile = entry.getKey().getTileEntity();
				if (tile != null && provider.canHandleItems(tile, entry.getValue())) {
					return provider.getStack(slot, tile, entry.getValue());
				}

			}
		}
		return null;
	}

	public static StoredItemStack getEntityStack(IEntityNode node, int slot) {
		List<StoredItemStack> storedStacks = new ArrayList();
		List<Entity> entityList = node.getEntities();
		for (Entity entity : entityList) {
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				IInventory inv = (IInventory) player.inventory;
				if (slot < inv.getSizeInventory()) {
					ItemStack stack = inv.getStackInSlot(slot);
					if (stack == null) {
						return null;
					} else {
						return new StoredItemStack(stack);
					}
				}
			}
		}

		return null;
	}

	public static Info combineData(Info primary, Info secondary) {
		if (!(primary instanceof CategoryInfo) && !(secondary instanceof CategoryInfo)) {
			if (primary.getDataType() == 0 && secondary.getDataType() == 0) {
				long stored = Long.parseLong(secondary.getData());
				long max = Long.parseLong(primary.getData());

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

}
