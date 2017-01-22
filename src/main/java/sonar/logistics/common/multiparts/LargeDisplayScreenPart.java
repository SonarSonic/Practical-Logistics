package sonar.logistics.common.multiparts;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import mcmultipart.MCMultiPartMod;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.api.utils.BlockCoords;
import sonar.core.api.utils.BlockInteractionType;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.multipart.SonarMultipartHelper;
import sonar.core.inventory.ContainerMultipartSync;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.BOOLEAN;
import sonar.logistics.Logistics;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.ConnectableType;
import sonar.logistics.api.connecting.IOperatorTool;
import sonar.logistics.api.display.ConnectedDisplayScreen;
import sonar.logistics.api.display.DisplayConnections;
import sonar.logistics.api.display.DisplayType;
import sonar.logistics.api.display.IInfoDisplay;
import sonar.logistics.api.display.ILargeDisplay;
import sonar.logistics.api.info.InfoContainer;
import sonar.logistics.client.gui.GuiDisplayScreen;
import sonar.logistics.connections.managers.DisplayManager;

public class LargeDisplayScreenPart extends ScreenMultipart implements ILargeDisplay {

	public int registryID = -1;
	public boolean wasAdded = false;
	public static final PropertyEnum<DisplayConnections> TYPE = PropertyEnum.<DisplayConnections>create("type", DisplayConnections.class);
	public ConnectedDisplayScreen connectedDisplay = null;
	public NBTTagCompound savedTag = null;

	public SyncTagType.BOOLEAN shouldRender = (BOOLEAN) new SyncTagType.BOOLEAN(3); // set default info
	{
		syncList.addPart(shouldRender);
	}

	public LargeDisplayScreenPart() {
		super();
	}

	public LargeDisplayScreenPart(EnumFacing dir, EnumFacing rotation) {
		super(dir, rotation);
	}

	public void updateDefaultInfo() {
		if (this.connectedDisplay != null && this.shouldRender()) {
			super.updateDefaultInfo();
		}
	}

	@Override
	public int getRegistryID() {
		return registryID;
	}

	@Override
	public void setRegistryID(int id) {
		if (!this.getWorld().isRemote) {
			registryID = id;
		}
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(LogisticsItems.largeDisplayScreen);
	}

	@Override
	public DisplayType getDisplayType() {
		return DisplayType.LARGE;
	}

	@Override
	public int maxInfo() {
		return 4;
	}

	@Override
	public void onFirstTick() {
		if (isServer() && !wasAdded) {
			addToNetwork();
			wasAdded = true;
		} else {
			this.requestSyncPacket();
		}
	}

	@Override
	public void onRemoved() {
		wasRemoved = true;
		this.onUnloaded();
	}

	@Override
	public void onUnloaded() {
		if (isServer()) {
			this.removeFromNetwork();
			wasAdded = false;
		}
	}

	@Override
	public ConnectableType getCableType() {
		return ConnectableType.CONNECTION;
	}

	public void addToNetwork() {
		if (isServer()) {
			Logistics.getDisplayManager().addConnection(this);
			this.sendSyncPacket();
		}
	}

	public void removeFromNetwork() {
		if (isServer()) {
			Logistics.getDisplayManager().removeConnection(this.getRegistryID(), this);
		}
	}

	@Override
	public boolean canConnectOnSide(EnumFacing dir) {
		return dir != face && dir != face.getOpposite();
	}

	@Override
	public InfoContainer container() {
		return getDisplayScreen().container;
	}

	public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack stack, PartMOP hit) {
		if (stack != null && stack.getItem() instanceof IOperatorTool) {
			return false;
		}
		if (hit.sideHit != face) {
			if (isServer()) {
				LargeDisplayScreenPart part = (LargeDisplayScreenPart) this.getDisplayScreen().getTopLeftScreen();
				if (part != null) {
					Logistics.getServerManager().sendLocalMonitorsToClient(part, player);
					SonarMultipartHelper.sendMultipartSyncToPlayer(this, (EntityPlayerMP) player);
					part.openFlexibleGui(player, 0);
				}
			}
			return true;
		}
		return this.container().onClicked(player.isSneaking() ? BlockInteractionType.SHIFT_RIGHT : BlockInteractionType.RIGHT, getWorld(), player, hand, stack, hit);
	}

	public void setLocalNetworkCache(INetworkCache network) {
		super.setLocalNetworkCache(network);
		if (this.isServer()) {
			connectedDisplay = Logistics.getInfoManager(this.getWorld().isRemote).getConnectedDisplays().get(registryID);
			if (connectedDisplay != null) {
				connectedDisplay.setHasChanged();
			}
		}
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type.isType(SyncType.DEFAULT_SYNC)) {
			registryID = nbt.getInteger("id");
			// shouldRender.readData(nbt, type);
		}
		if (this.isServer() && this.shouldRender.getObject() && type.isType(SyncType.SAVE)) {
			if (this.connectedDisplay == null && nbt.hasKey("connected")) {
				this.savedTag = nbt.getCompoundTag("connected");
			}
		}
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		if (type.isType(SyncType.DEFAULT_SYNC)) {
			nbt.setInteger("id", registryID);
		}
		return super.writeData(nbt, type);
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		super.writePacket(buf, id);
		switch (id) {
		case 5:
			shouldRender.writeToBuf(buf);
			break;
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		super.readPacket(buf, id);
		switch (id) {
		case 5:
			this.shouldRender.readFromBuf(buf);
			break;
		}
	}

	@Override
	public void addInfo(List<String> info) {
		super.addInfo(info);
		info.add("Large Display ID: " + registryID);
		info.add("Should Render " + this.shouldRender.getObject());
	}

	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		double p = 0.0625;
		double height = p * 16, width = 0, length = p * 1;

		switch (face) {
		case EAST:
			list.add(new AxisAlignedBB(1, 0, (width) / 2, 1 - length, height, 1 - width / 2));
			break;
		case NORTH:
			list.add(new AxisAlignedBB((width) / 2, 0, length, 1 - width / 2, height, 0));
			break;
		case SOUTH:
			list.add(new AxisAlignedBB((width) / 2, 0, 1, 1 - width / 2, height, 1 - length));
			break;
		case WEST:
			list.add(new AxisAlignedBB(length, 0, (width) / 2, 0, height, 1 - width / 2));
			break;
		case DOWN:
			list.add(new AxisAlignedBB(0, 0, 0, 1, length, 1));
			break;
		case UP:
			list.add(new AxisAlignedBB(0, 1 - 0, 0, 1, 1 - length, 1));
			break;
		default:
			break;

		}
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess w, BlockPos pos) {
		IBlockState currentState = state;
		List<EnumFacing> faces = new ArrayList();
		for (EnumFacing face : EnumFacing.VALUES) {
			if (face == this.face || face == this.face.getOpposite()) {
				continue;
			}
			IInfoDisplay display = LogisticsAPI.getCableHelper().getDisplayScreen(BlockCoords.translateCoords(getCoords(), face), this.face);
			if (display != null && display.getDisplayType() == DisplayType.LARGE) {
				switch (this.face) {
				case DOWN:
					EnumFacing toAdd = face.rotateAround(Axis.Y);
					if (toAdd == EnumFacing.NORTH || toAdd == EnumFacing.SOUTH) {
						toAdd = toAdd.getOpposite();
					}
					faces.add(toAdd);
					break;
				case EAST:
					toAdd = face.rotateAround(Axis.Z).rotateAround(Axis.Y);
					if (toAdd == EnumFacing.NORTH || toAdd == EnumFacing.SOUTH) {
						toAdd = toAdd.getOpposite();
					}
					faces.add(toAdd);
					break;
				case NORTH:
					toAdd = face.rotateAround(Axis.Z).rotateAround(Axis.X).rotateAround(Axis.Y);
					if (toAdd == EnumFacing.NORTH || toAdd == EnumFacing.SOUTH) {
						toAdd = toAdd.getOpposite();
					}
					faces.add(toAdd);
					break;
				case SOUTH:
					toAdd = face.rotateAround(Axis.Z).rotateAround(Axis.X).rotateAround(Axis.Y).getOpposite();
					faces.add(toAdd);
					break;
				case UP:
					faces.add(face);
					break;
				case WEST:
					faces.add(face.rotateAround(Axis.Z).rotateAround(Axis.Y).getOpposite());
					break;
				default:
					break;

				}
			}
		}
		DisplayConnections type = DisplayConnections.getType(faces);
		return currentState.withProperty(ORIENTATION, face).withProperty(ROTATION, rotation).withProperty(TYPE, type);
	}

	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(MCMultiPartMod.multipart, new IProperty[] { ORIENTATION, ROTATION, TYPE });
	}

	@Override
	public void setConnectedDisplay(ConnectedDisplayScreen connectedDisplay) {
		if (savedTag != null && registryID != -1) {
			connectedDisplay.readData(savedTag, SyncType.SAVE);
			savedTag = null;
		}
		this.connectedDisplay = connectedDisplay;
		if (shouldRender.getObject()) {
			syncList.addPart(connectedDisplay);
		} else {
			syncList.removePart(connectedDisplay);
		}
	}

	@Override
	public ConnectedDisplayScreen getDisplayScreen() {
		if (connectedDisplay == null) {
			setConnectedDisplay(connectedDisplay = DisplayManager.getOrCreateDisplayScreen(getWorld(), this, registryID));
		}
		return connectedDisplay;
	}

	@Override
	public void setShouldRender(boolean shouldRender) {
		if (shouldRender != this.shouldRender.getObject()) {
			this.shouldRender.setObject(shouldRender);
		}
		if (shouldRender) {
			syncList.addPart(connectedDisplay);
		} else {
			syncList.removePart(connectedDisplay);
		}
		// this.sendByteBufPacket(5);
		this.sendSyncPacket();
		this.markDirty();
	}

	@Override
	public boolean shouldRender() {
		// return connectedDisplay != null ? connectedDisplay.canBeRendered.getObject() && shouldRender.getObject() : shouldRender.getObject();
		return shouldRender.getObject();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {

		return TileEntity.INFINITE_EXTENT_AABB;
	}

	public Object getServerElement(ScreenMultipart obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		switch (id) {
		case 0:
			return new ContainerMultipartSync(obj);
		}
		return null;
	}

	public Object getClientElement(ScreenMultipart obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		switch (id) {
		case 0:
			return new GuiDisplayScreen(obj);
		}
		return null;
	}
}
