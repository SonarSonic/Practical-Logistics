package sonar.logistics.common.multiparts;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import mcmultipart.MCMultiPartMod;
import mcmultipart.multipart.INormallyOccludingPart;
import mcmultipart.raytrace.PartMOP;
import mcmultipart.raytrace.RayTraceUtils.AdvancedRayTraceResultPart;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sonar.core.api.IFlexibleGui;
import sonar.core.api.utils.BlockCoords;
import sonar.core.api.utils.BlockInteractionType;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.multipart.SonarMultipartHelper;
import sonar.core.inventory.ContainerMultipartSync;
import sonar.core.network.sync.IDirtyPart;
import sonar.core.network.sync.SyncEnum;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.utils.IByteBufTile;
import sonar.logistics.Logistics;
import sonar.logistics.api.connecting.IOperatorTile;
import sonar.logistics.api.connecting.IOperatorTool;
import sonar.logistics.api.connecting.OperatorMode;
import sonar.logistics.api.display.IInfoDisplay;
import sonar.logistics.api.display.ScreenLayout;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.viewers.MonitorTally;
import sonar.logistics.api.viewers.ViewerType;
import sonar.logistics.client.gui.GuiDisplayScreen;

public abstract class ScreenMultipart extends LogisticsMultipart implements IByteBufTile, INormallyOccludingPart, IInfoDisplay, IOperatorTile, IFlexibleGui<ScreenMultipart> {

	public SyncEnum<ScreenLayout> layout = new SyncEnum(ScreenLayout.values(), 1);
	public SyncTagType.BOOLEAN defaultData = new SyncTagType.BOOLEAN(2); // set default info
	public ILogicMonitor monitor = null;
	public EnumFacing rotation, face;
	public BlockCoords lastSelected = null;
	public int currentSelected = -1;
	{
		syncList.addPart(defaultData);
	}

	public ScreenMultipart() {
		super();
	}

	public ScreenMultipart(EnumFacing face, EnumFacing rotation) {
		super();
		this.rotation = rotation;
		this.face = face;
	}

	public void update() {
		super.update();
		updateDefaultInfo();
	}

	public void updateDefaultInfo() {
		if (isServer() && !defaultData.getObject()) {
			ArrayList<ILogicMonitor> monitors = Logistics.getServerManager().getLocalMonitors(new ArrayList(), this);
			if (!monitors.isEmpty()) {
				ILogicMonitor monitor = monitors.get(0);
				if (container() != null && monitor != null && monitor.getIdentity() != null) {
					for (int i = 0; i < Math.min(monitor.getMaxInfo(), maxInfo()); i++) {
						container().setUUID(new InfoUUID(monitor.getIdentity().hashCode(), i), i);
					}
					defaultData.setObject(true);
					sendSyncPacket();
				}
			}
		}
	}

	public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack stack, PartMOP hit) {
		if (stack != null && stack.getItem() instanceof IOperatorTool) {
			return false;
		}
		if (hit.sideHit != face) {
			if (isServer()) {
				Logistics.getServerManager().sendLocalMonitorsToClient(this, player);
				SonarMultipartHelper.sendMultipartSyncToPlayer(this, (EntityPlayerMP) player);
				openFlexibleGui(player, 0);
			}
			return true;
		}
		return container().onClicked(player.isSneaking() ? BlockInteractionType.SHIFT_RIGHT : BlockInteractionType.RIGHT, getWorld(), player, hand, stack, hit);
	}

	@Override
	public void harvest(EntityPlayer player, PartMOP hit) {
		if (hit.sideHit == face) {
			container().onClicked(player.isSneaking() ? BlockInteractionType.SHIFT_LEFT : BlockInteractionType.LEFT, getWorld(), player, player.getActiveHand(), player.getActiveItemStack(), hit);
			return;
		}
		super.harvest(player, hit);
	}

	public void markChanged(IDirtyPart part) {
		super.markChanged(part);		
		ArrayList<EntityPlayer> viewers = getViewersList().getViewers(false, ViewerType.INFO);
		for (EntityPlayer player : viewers) {
			SonarMultipartHelper.sendMultipartSyncToPlayer(this, (EntityPlayerMP) player);
		}
	}

	public void onSyncPacketRequested(EntityPlayer player) {
		super.onSyncPacketRequested(player);
		this.getViewersList().addViewer(player, ViewerType.INFO);
	}

	public void onFirstTick() {
		super.onFirstTick();
		if (!this.getWorld().isRemote)
			Logistics.getServerManager().addDisplay(this);
		if (this.isClient())
			this.requestSyncPacket();
	}

	public void onLoaded() {
		super.onLoaded();
		if (!this.getWorld().isRemote)
			Logistics.getServerManager().addDisplay(this);
	}

	public void onRemoved() {
		super.onRemoved();
		if (!this.getWorld().isRemote)
			Logistics.getServerManager().removeDisplay(this);
	}

	public void onUnloaded() {
		super.onUnloaded();
		if (!this.getWorld().isRemote)
			Logistics.getServerManager().removeDisplay(this);
	}

	@Override
	public void addOcclusionBoxes(List<AxisAlignedBB> list) {
		this.addSelectionBoxes(list);
	}

	@Override
	public ScreenLayout getLayout() {
		return layout.getObject();
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		switch (id) {
		case 0:
			buf.writeInt(currentSelected);
			container().getInfoUUID(currentSelected).writeToBuf(buf);
			break;
		case 1:
			buf.writeInt(currentSelected);
			container().getDisplayInfo(currentSelected).formatList.writeToBuf(buf);
			break;
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		switch (id) {
		case 0:
			currentSelected = buf.readInt();
			container().setUUID(InfoUUID.getUUID(buf), currentSelected);
			if (FMLCommonHandler.instance().getEffectiveSide().isServer())
				Logistics.getServerManager().updateViewingMonitors = true;
			this.sendSyncPacket();
			break;
		case 1:
			currentSelected = buf.readInt();
			container().getDisplayInfo(currentSelected).formatList.readFromBuf(buf);
			this.sendSyncPacket();
			break;
		}
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound tag, SyncType type) {
		super.writeData(tag, type);
		tag.setByte("rotation", (byte) rotation.ordinal());
		tag.setByte("face", (byte) face.ordinal());
		layout.writeData(tag, type);
		// container().writeData(tag, type);
		return tag;
	}

	@Override
	public void readData(NBTTagCompound tag, SyncType type) {
		super.readData(tag, type);
		rotation = EnumFacing.VALUES[tag.getByte("rotation")];
		face = EnumFacing.VALUES[tag.getByte("face")];
		layout.readData(tag, type);
		// container().readData(tag, type);
	}

	@Override
	public void writeUpdatePacket(PacketBuffer buf) {
		super.writeUpdatePacket(buf);
		buf.writeByte((byte) rotation.ordinal());
		buf.writeByte((byte) face.ordinal());
		layout.writeToBuf(buf);
		// ByteBufUtils.writeTag(buf, container().writeData(new NBTTagCompound(), SyncType.SAVE));
	}

	@Override
	public void readUpdatePacket(PacketBuffer buf) {
		super.readUpdatePacket(buf);
		rotation = EnumFacing.VALUES[buf.readByte()];
		face = EnumFacing.VALUES[buf.readByte()];
		layout.readFromBuf(buf);
		// container().readData(ByteBufUtils.readTag(buf), SyncType.SAVE);
	}

	@Override
	public ConnectionType canConnect(EnumFacing dir) {
		return dir != face ? ConnectionType.NETWORK : ConnectionType.NONE;
	}

	@Override
	public IBlockState getActualState(IBlockState state) {
		World w = getContainer().getWorldIn();
		BlockPos pos = getContainer().getPosIn();
		return state.withProperty(ORIENTATION, face).withProperty(ROTATION, rotation);
	}

	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(MCMultiPartMod.multipart, new IProperty[] { ORIENTATION, ROTATION });
	}

	@Override
	public EnumFacing getFace() {
		return face;
	}

	@Override
	public boolean performOperation(AdvancedRayTraceResultPart rayTrace, OperatorMode mode, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!getWorld().isRemote) {
			layout.incrementEnum();
			while (!(layout.getObject().maxInfo <= this.maxInfo())) {
				layout.incrementEnum();
			}
			sendSyncPacket();
			sendUpdatePacket(true);
			FontHelper.sendMessage("Screen Layout: " + layout.getObject(), getWorld(), player);
		}

		return true;
	}

	public void addInfo(List<String> info) {
		super.addInfo(info);
	}

	@Override
	public void onViewerAdded(EntityPlayer player, List<MonitorTally> type) {
	}

	@Override
	public void onViewerRemoved(EntityPlayer player, List<MonitorTally> type) {
	}

	public Object getServerElement(ScreenMultipart obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		return id == 0 ? new ContainerMultipartSync(obj) : null;
	}

	public Object getClientElement(ScreenMultipart obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		return id == 0 ? new GuiDisplayScreen(obj) : null;
	}

	public UUID getIdentity() {
		return getUUID();
	}
}
