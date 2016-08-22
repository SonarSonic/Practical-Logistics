package sonar.logistics.parts;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import mcmultipart.MCMultiPartMod;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.ISlotOccludingPart;
import mcmultipart.multipart.ISlottedPart;
import mcmultipart.multipart.PartSlot;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import sonar.core.helpers.FontHelper;
import sonar.core.integration.multipart.SonarMultipart;
import sonar.core.utils.Pair;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.EmptyNetworkCache;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.cache.IRefreshCache;
import sonar.logistics.api.cache.RefreshType;
import sonar.logistics.api.connecting.CableType;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.connecting.ILogicTile;

public class DataCablePart extends SonarMultipart implements ISlotOccludingPart, IDataCable {

	public enum CableConnection implements IStringSerializable {

		CABLE, INTERNAL, HALF,/*BLOCK,*/ NONE;

		public boolean canConnect() {
			return this == CABLE || this == INTERNAL || this == HALF;
		}

		public double offsetBounds() {
			if (this == INTERNAL) {
				return 0.0625;
			} else if (this == HALF) {
				return 0.0625 * 3;
			} else {
				return 0;
			}
		}

		public String getName() {
			return this.toString().toLowerCase();
		}
	}

	public static final PropertyEnum<CableConnection> NORTH = PropertyEnum.<CableConnection> create("north", CableConnection.class);
	public static final PropertyEnum<CableConnection> EAST = PropertyEnum.<CableConnection> create("east", CableConnection.class);
	public static final PropertyEnum<CableConnection> SOUTH = PropertyEnum.<CableConnection> create("south", CableConnection.class);
	public static final PropertyEnum<CableConnection> WEST = PropertyEnum.<CableConnection> create("west", CableConnection.class);
	public static final PropertyEnum<CableConnection> DOWN = PropertyEnum.<CableConnection> create("down", CableConnection.class);
	public static final PropertyEnum<CableConnection> UP = PropertyEnum.<CableConnection> create("up", CableConnection.class);

	public int registryID = -1;
	/** if this MultiPart has connections connected */
	public boolean connection = false;
	public boolean wasAdded = false;

	public DataCablePart() {
		super();
	}

	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		list.add(new AxisAlignedBB(6 * 0.0625, 6 * 0.0625, 6 * 0.0625, 1 - 6 * 0.0625, 1 - 6 * 0.0625, 1 - 6 * 0.0625));
		for (EnumFacing face : EnumFacing.values()) {
			CableConnection connect = checkBlockInDirection(face);
			if (connect.canConnect()) {
				double p = 0.0625;
				double w = (1 - 2 * 0.0625) / 2;
				double heightMin = connect.offsetBounds();
				double heightMax = 6 * 0.0625;
				switch (face) {
				case DOWN:
					list.add(new AxisAlignedBB(w, heightMin, w, 1 - w, heightMax, 1 - w));
					break;
				case EAST:
					list.add(new AxisAlignedBB(1 - heightMax, w, w, 1 - heightMin, 1 - w, 1 - w));
					break;
				case NORTH:
					list.add(new AxisAlignedBB(w, w, heightMin, 1 - w, 1 - w, heightMax));
					break;
				case SOUTH:
					list.add(new AxisAlignedBB(w, w, 1 - heightMax, 1 - w, 1 - w, 1 - heightMin));
					break;
				case UP:
					list.add(new AxisAlignedBB(w, 1 - heightMax, w, 1 - w, 1 - heightMin, 1 - w));
					break;
				case WEST:
					list.add(new AxisAlignedBB(heightMin, w, w, heightMax, 1 - w, 1 - w));
					break;
				default:
					list.add(new AxisAlignedBB(w, heightMin, w, 1 - w, heightMax, 1 - w));
					break;
				}
			}
		}

	}

	@Override
	public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		ArrayList<AxisAlignedBB> boxes = new ArrayList();
		addSelectionBoxes(boxes);
		boxes.forEach(box -> {
			if (box.intersectsWith(mask)) {
				list.add(box);
			}
		});
	}

	@Override
	public EnumSet<PartSlot> getSlotMask() {
		return EnumSet.of(PartSlot.CENTER);
	}

	@Override
	public EnumSet<PartSlot> getOccludedSlots() {
		EnumSet set = EnumSet.noneOf(PartSlot.class);
		for (PartSlot slot : PartSlot.FACES) {
			EnumFacing face = slot.f1;
			if (checkBlockInDirection(face).canConnect()) {
				set.add(slot);
			}
		}
		return set;
	}

	@Override
	public void onPartChanged(IMultipart changedPart) {
		if (!this.getWorld().isRemote) {
			if (changedPart instanceof SonarMultipart) {
				INetworkCache cache = LogisticsAPI.getCableHelper().getNetwork(registryID);
				if (cache instanceof IRefreshCache) {
					IRefreshCache toRefresh = (IRefreshCache) cache;
					toRefresh.refreshCache(cache.getNetworkID(), RefreshType.FULL);
				}
			}
			refreshConnections();
		}

	}

	public void configureConnections(INetworkCache network) {
		boolean found = false;
		for (IMultipart part : getContainer().getParts()) {
			if (!(part instanceof IDataCable) && part instanceof ILogicTile) {
				if (part instanceof LogisticsMultipart) {
					LogisticsMultipart networkPart = (LogisticsMultipart) part;
					if (!networkPart.wasRemoved) {
						networkPart.setLocalNetworkCache(network);
					}
				}
				found = true;
			}
		}
		connection = found;
		return;
	}

	@Override
	public boolean hasConnections() {
		return connection;
	}

	@Override
	public void onFirstTick() {
		if (!this.getWorld().isRemote && !wasAdded) {
			addCable();
			wasAdded = true;
		}
	}

	@Override
	public void onRemoved() {
		super.onRemoved();
		this.onUnloaded();
	}

	@Override
	public void onUnloaded() {
		if (!this.getWorld().isRemote) {
			this.removeCable();
			wasAdded = false;
		}
	}

	@Override
	public CableType canRenderConnection(EnumFacing dir) {
		return LogisticsAPI.getCableHelper().getConnectionType(getContainer().getWorldIn(), getContainer().getPosIn(), dir, getCableType()).a;
	}

	@Override
	public boolean canConnect(EnumFacing dir) {
		return getContainer().getPartInSlot(PartSlot.getFaceSlot(dir)) == null;
	}

	@Override
	public int registryID() {
		return registryID;
	}

	@Override
	public void setRegistryID(int id) {
		if (!this.getWorld().isRemote) {
			registryID = id;
			//configureConnections(LogisticsAPI.getCableHelper().getNetwork(registryID));
		}
	}

	@Override
	public CableType getCableType() {
		return CableType.DATA_CABLE;
	}

	public void addCable() {
		LogisticsAPI.getCableHelper().addCable(this);
	}

	public void removeCable() {
		if (!this.getWorld().isRemote) {
			LogisticsAPI.getCableHelper().removeCable(this);
			configureConnections(EmptyNetworkCache.INSTANCE);
			INetworkCache cache = LogisticsAPI.getCableHelper().getNetwork(registryID);
			if (cache instanceof IRefreshCache) {
				IRefreshCache toRefresh = (IRefreshCache) cache;
				toRefresh.refreshCache(cache.getNetworkID(), RefreshType.FULL);
			}
		}
	}

	@Override
	public void refreshConnections() {
		LogisticsAPI.getCableHelper().refreshConnections(this);
	}

	public CableConnection checkBlockInDirection(EnumFacing dir) {
		IMultipartContainer container = getContainer();
		if (container != null) {
			ISlottedPart part = container.getPartInSlot(PartSlot.getFaceSlot(dir));
			if (part != null && part instanceof ILogicTile) {
				if (part instanceof SidedMultipart) {
					SidedMultipart sided = (SidedMultipart) part;
					if (sided.heightMax == 0.0625 * 6) {
						return CableConnection.NONE;
					} else if (sided.heightMax == 0.0625 * 4) {
						return CableConnection.HALF;
					}
				}
				ILogicTile tile = (ILogicTile) part;
				if (tile.canConnect(dir.getOpposite())) {
					return CableConnection.INTERNAL;
				}

			}
			Pair<CableType, Integer> connection = LogisticsAPI.getCableHelper().getConnectionType(getContainer().getWorldIn(), getContainer().getPosIn(), dir, getCableType());
			if (!canConnect(dir) || !connection.a.canConnect(getCableType())) {
				return CableConnection.NONE;
			}
			if (CableType.BLOCK_CONNECTION == connection.a) {
				//return CableConnection.BLOCK;
				return CableConnection.CABLE;
			}
			return CableConnection.CABLE;
		}
		return CableConnection.NONE;
	}

	@Override
	public IBlockState getActualState(IBlockState state) {
		return state.withProperty(NORTH, checkBlockInDirection(EnumFacing.NORTH)).withProperty(SOUTH, checkBlockInDirection(EnumFacing.SOUTH)).withProperty(WEST, checkBlockInDirection(EnumFacing.WEST)).withProperty(EAST, checkBlockInDirection(EnumFacing.EAST)).withProperty(UP, checkBlockInDirection(EnumFacing.UP)).withProperty(DOWN, checkBlockInDirection(EnumFacing.DOWN));
	}

	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(MCMultiPartMod.multipart, new IProperty[] { NORTH, EAST, SOUTH, WEST, DOWN, UP });
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(LogisticsItems.partCable);
	}

	@Override
	public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, PartMOP hit) {
		if (!this.getWorld().isRemote) {
			FontHelper.sendMessage("Has connections?" + " " + hasConnections(), this.getWorld(), player);
			FontHelper.sendMessage("Registry ID?" + " " + this.registryID(), this.getWorld(), player);
		}
		return false;
	}
}
