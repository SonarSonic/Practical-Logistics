package sonar.logistics.parts;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import mcmultipart.MCMultiPartMod;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.ISlotOccludingPart;
import mcmultipart.multipart.PartSlot;
import mcmultipart.raytrace.RayTraceUtils.AdvancedRayTraceResultPart;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.multipart.SonarMultipart;
import sonar.core.utils.LabelledAxisAlignedBB;
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
import sonar.logistics.api.connecting.IOperatorProvider;
import sonar.logistics.api.connecting.IOperatorTile;
import sonar.logistics.api.connecting.OperatorMode;
import sonar.logistics.api.info.monitor.ILogicMonitor;

public class DataCablePart extends SonarMultipart implements ISlotOccludingPart, IDataCable, IOperatorTile, IOperatorProvider {

	public int registryID = -1;
	public boolean connection = false;
	public boolean wasAdded = false;
	public boolean[] isBlocked = new boolean[6];

	public static final PropertyEnum<CableConnection> NORTH = PropertyEnum.<CableConnection>create("north", CableConnection.class);
	public static final PropertyEnum<CableConnection> EAST = PropertyEnum.<CableConnection>create("east", CableConnection.class);
	public static final PropertyEnum<CableConnection> SOUTH = PropertyEnum.<CableConnection>create("south", CableConnection.class);
	public static final PropertyEnum<CableConnection> WEST = PropertyEnum.<CableConnection>create("west", CableConnection.class);
	public static final PropertyEnum<CableConnection> DOWN = PropertyEnum.<CableConnection>create("down", CableConnection.class);
	public static final PropertyEnum<CableConnection> UP = PropertyEnum.<CableConnection>create("up", CableConnection.class);

	public enum CableConnection implements IStringSerializable {
		CABLE, INTERNAL, HALF, /* BLOCK, */ NONE;

		public boolean canConnect() {
			return this == CABLE || this == INTERNAL || this == HALF;
		}

		public double offsetBounds() {
			return this == INTERNAL ? 0.0625 : this == HALF ? 0.0625 * 3 : 0;
		}

		public String getName() {
			return this.toString().toLowerCase();
		}
	}

	public DataCablePart() {
		super();
	}

	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		list.add(new LabelledAxisAlignedBB(6 * 0.0625, 6 * 0.0625, 6 * 0.0625, 1 - 6 * 0.0625, 1 - 6 * 0.0625, 1 - 6 * 0.0625).labelAxis("c"));
		for (EnumFacing face : EnumFacing.values()) {
			CableConnection connect = checkBlockInDirection(face);
			if (connect.canConnect()) {
				double p = 0.0625;
				double w = (1 - 2 * 0.0625) / 2;
				double heightMin = connect.offsetBounds();
				double heightMax = 6 * 0.0625;
				switch (face) {
				case DOWN:
					list.add(new LabelledAxisAlignedBB(w, heightMin, w, 1 - w, heightMax, 1 - w).labelAxis(face.toString()));
					break;
				case EAST:
					list.add(new LabelledAxisAlignedBB(1 - heightMax, w, w, 1 - heightMin, 1 - w, 1 - w).labelAxis(face.toString()));
					break;
				case NORTH:
					list.add(new LabelledAxisAlignedBB(w, w, heightMin, 1 - w, 1 - w, heightMax).labelAxis(face.toString()));
					break;
				case SOUTH:
					list.add(new LabelledAxisAlignedBB(w, w, 1 - heightMax, 1 - w, 1 - w, 1 - heightMin).labelAxis(face.toString()));
					break;
				case UP:
					list.add(new LabelledAxisAlignedBB(w, 1 - heightMax, w, 1 - w, 1 - heightMin, 1 - w).labelAxis(face.toString()));
					break;
				case WEST:
					list.add(new LabelledAxisAlignedBB(heightMin, w, w, heightMax, 1 - w, 1 - w).labelAxis(face.toString()));
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
		for (EnumFacing face : EnumFacing.values()) {
			BlockCoords offset = BlockCoords.translateCoords(this.getCoords(), face.getOpposite());
			ILogicTile tile = LogisticsAPI.getCableHelper().getMultipart(offset, face);
			if (tile instanceof ILogicMonitor) {
				network.addLocalMonitor((ILogicMonitor) tile);
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
		return isBlocked[dir.ordinal()] ? false : getContainer().getPartInSlot(PartSlot.getFaceSlot(dir)) == null;
	}

	@Override
	public int getNetworkID() {
		return registryID;
	}

	@Override
	public void setRegistryID(int id) {
		if (!this.getWorld().isRemote) {
			registryID = id;
			// configureConnections(LogisticsAPI.getCableHelper().getNetwork(registryID));
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
			if (isBlocked[dir.ordinal()]) {
				return CableConnection.NONE;
			}
			IMultipart part = container.getPartInSlot(PartSlot.getFaceSlot(dir));
			if (part == null)
				part = (IMultipart) LogisticsAPI.getCableHelper().getDisplayScreen(this.getCoords(), dir);
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
			return !canConnect(dir) || !connection.a.canConnect(getCableType()) ? CableConnection.NONE : CableConnection.CABLE;
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
	public boolean performOperation(AdvancedRayTraceResultPart rayTrace, OperatorMode mode, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (mode == OperatorMode.DEFAULT) {
			List<AxisAlignedBB> bounds = new ArrayList();
			addSelectionBoxes(bounds);
			for (AxisAlignedBB bound : bounds) {
				if (bound instanceof LabelledAxisAlignedBB && bound.equals(rayTrace.bounds)) {
					if (!this.getWorld().isRemote) {
						String label = ((LabelledAxisAlignedBB) bound).label;
						EnumFacing face = null;
						if (!label.equals("c")) {
							face = EnumFacing.valueOf(label.toUpperCase());
						} else {
							face = facing;
						}
						isBlocked[face.ordinal()] = !isBlocked[face.ordinal()];
						IDataCable cable = LogisticsAPI.getCableHelper().getCableFromCoords(BlockCoords.translateCoords(getCoords(), face));
						removeCable();
						addCable();
						if (cable != null && cable instanceof DataCablePart) {
							DataCablePart part = (DataCablePart) cable;
							part.isBlocked[face.getOpposite().ordinal()] = isBlocked[face.ordinal()];
							part.sendUpdatePacket(true);
							part.markDirty();
							part.removeCable();
							part.addCable();
						}
						sendUpdatePacket(true);
						markDirty();
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void writeUpdatePacket(PacketBuffer buf) {
		super.writeUpdatePacket(buf);
		for (int i = 0; i < isBlocked.length; i++) {
			buf.writeBoolean(isBlocked[i]);
		}
	}

	@Override
	public void readUpdatePacket(PacketBuffer buf) {
		super.readUpdatePacket(buf);
		for (int i = 0; i < isBlocked.length; i++) {
			isBlocked[i] = buf.readBoolean();
		}
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		isBlocked = new boolean[6];
		NBTTagCompound tag = nbt.getCompoundTag("isBlocked");
		for (int i = 0; i < isBlocked.length; i++) {
			isBlocked[i] = tag.getBoolean("" + i);
		}
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		NBTTagCompound tag = new NBTTagCompound();
		for (int i = 0; i < isBlocked.length; i++) {
			tag.setBoolean("" + i, isBlocked[i]);
		}
		nbt.setTag("isBlocked", tag);
		return super.writeData(nbt, type);
	}

	@Override
	public void addInfo(List<String> info) {
		ItemStack dropStack = getItemStack();
		if (dropStack != null)info.add(TextFormatting.UNDERLINE + dropStack.getDisplayName());
		info.add("Network ID: " + registryID);	
	}

}