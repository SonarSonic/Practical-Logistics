package sonar.logistics.common.multiparts;

import java.util.List;

import mcmultipart.MCMultiPartMod;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.INormallyOccludingPart;
import mcmultipart.multipart.ISlottedPart;
import mcmultipart.multipart.PartSlot;
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
import net.minecraftforge.fml.common.network.ByteBufUtils;
import sonar.core.SonarCore;
import sonar.core.api.IFlexibleGui;
import sonar.core.api.utils.BlockInteractionType;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.inventory.ContainerMultipartSync;
import sonar.core.network.PacketMultipartSync;
import sonar.core.network.sync.SyncEnum;
import sonar.logistics.api.connecting.IOperatorTile;
import sonar.logistics.api.connecting.IOperatorTool;
import sonar.logistics.api.connecting.OperatorMode;
import sonar.logistics.api.display.IInfoDisplay;
import sonar.logistics.api.display.ScreenLayout;
import sonar.logistics.api.info.IInfoContainer;
import sonar.logistics.api.info.InfoContainer;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.client.gui.GuiDisplayScreen;
import sonar.logistics.connections.managers.LogicMonitorManager;

public abstract class ScreenMultipart extends LogisticsMultipart implements INormallyOccludingPart, IInfoDisplay, IOperatorTile, IFlexibleGui<ScreenMultipart> {

	public SyncEnum<ScreenLayout> layout = new SyncEnum(ScreenLayout.values(), 0);
	public DisplayState state = DisplayState.NONE;
	public ILogicMonitor monitor = null;
	public EnumFacing rotation, face;
	public InfoContainer container = new InfoContainer(this);
	{
		syncParts.add(layout);
	}

	public void update() {
		super.update();
		if (this.isClient()) {
			return;
		}
		switch (state) {
		case SET:
			if (monitor == null || monitor.getNetwork().getNetworkID() != network.getNetworkID()) {
				state = DisplayState.NONE;
			}
			break;
		case NONE:
			ISlottedPart part = getContainer().getPartInSlot(PartSlot.getFaceSlot(face));
			monitor = (part != null && part instanceof ILogicMonitor) ? (ILogicMonitor) part : this.getNetwork().getLocalMonitor();
			if (monitor != null) {
				int max = Math.min(container().getMaxCapacity(), monitor.getMaxInfo());
				for (int i = 0; i < max; i++) {
					InfoUUID id = new InfoUUID(monitor.getIdentity().hashCode(), i);
					container().setUUID(id, i);
					LogicMonitorManager.changedInfo.add(id);
				}
				state = DisplayState.SET;
				this.sendUpdatePacket();
			}
			break;
		default:
			break;

		}
	}

	public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack stack, PartMOP hit) {
		if (stack != null && stack.getItem() instanceof IOperatorTool) {
			return false;
		}
		if (hit.sideHit != face) {
			if (isServer()) {
				SonarCore.network.sendTo(new PacketMultipartSync(getPos(), writeData(new NBTTagCompound(), SyncType.SYNC_OVERRIDE), SyncType.SYNC_OVERRIDE, getUUID()), (EntityPlayerMP) player);
				openBasicGui(player, 0);
			}
			return true;
		}
		return container.onClicked(player.isSneaking() ? BlockInteractionType.SHIFT_RIGHT : BlockInteractionType.RIGHT, getWorld(), player, hand, stack, hit);
	}

	@Override
	public void harvest(EntityPlayer player, PartMOP hit) {
		if (hit.sideHit == face) {
			container.onClicked(player.isSneaking() ? BlockInteractionType.SHIFT_LEFT : BlockInteractionType.LEFT, getWorld(), player, player.getActiveHand(), player.getActiveItemStack(), hit);
			return;
		}
		super.harvest(player, hit);
	}

	public static enum DisplayState {
		NONE, SET;
	}

	public ScreenMultipart() {
		super();
	}

	public ScreenMultipart(EnumFacing face, EnumFacing rotation) {
		super();
		this.rotation = rotation;
		this.face = face;
	}

	public void onFirstTick() {
		super.onFirstTick();
		LogicMonitorManager.addDisplay(this);
	}

	public void onLoaded() {
		super.onLoaded();
		LogicMonitorManager.addDisplay(this);

	}

	public void onRemoved() {
		super.onRemoved();
		LogicMonitorManager.removeDisplay(this);
	}

	public void onUnloaded() {
		super.onUnloaded();
		LogicMonitorManager.removeDisplay(this);
	}

	@Override
	public void addOcclusionBoxes(List<AxisAlignedBB> list) {
		this.addSelectionBoxes(list);
	}

	@Override
	public IInfoContainer container() {
		return container;
	}

	@Override
	public ScreenLayout getLayout() {
		return layout.getObject();
	}

	public void onPartChanged(IMultipart changedPart) {
		super.onPartChanged(changedPart);
		state = DisplayState.NONE;
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound tag, SyncType type) {
		super.writeData(tag, type);
		tag.setByte("rotation", (byte) rotation.ordinal());
		tag.setByte("face", (byte) face.ordinal());
		container.writeData(tag, type);
		return tag;
	}

	@Override
	public void readData(NBTTagCompound tag, SyncType type) {
		super.readData(tag, type);
		rotation = EnumFacing.VALUES[tag.getByte("rotation")];
		face = EnumFacing.VALUES[tag.getByte("face")];
		container.readData(tag, type);
	}

	@Override
	public void writeUpdatePacket(PacketBuffer buf) {
		super.writeUpdatePacket(buf);
		buf.writeByte((byte) rotation.ordinal());
		buf.writeByte((byte) face.ordinal());
		ByteBufUtils.writeTag(buf, container.writeData(new NBTTagCompound(), SyncType.SAVE));
	}

	@Override
	public void readUpdatePacket(PacketBuffer buf) {
		super.readUpdatePacket(buf);
		rotation = EnumFacing.VALUES[buf.readByte()];
		face = EnumFacing.VALUES[buf.readByte()];
		container.readData(ByteBufUtils.readTag(buf), SyncType.SAVE);
	}

	@Override
	public boolean canConnect(EnumFacing dir) {
		return dir != face;
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
			state = DisplayState.NONE;
			FontHelper.sendMessage("Screen Layout: " + layout.getObject(), getWorld(), player);
		}

		return true;
	}

	public void addInfo(List<String> info) {
		super.addInfo(info);
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
