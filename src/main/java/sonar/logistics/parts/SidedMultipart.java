package sonar.logistics.parts;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import mcmultipart.MCMultiPartMod;
import mcmultipart.multipart.ISlottedPart;
import mcmultipart.multipart.MultipartHelper;
import mcmultipart.multipart.PartSlot;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.utils.Pair;

public abstract class SidedMultipart extends LogisticsMultipart implements ISlottedPart {
	public double width, heightMin, heightMax;
	public EnumFacing face;

	public SidedMultipart(double width, double heightMin, double heightMax) {
		super();
		this.width = width;
		this.heightMin = heightMin;
		this.heightMax = heightMax;
		if (face == null)
			this.face = EnumFacing.DOWN;
	}

	public SidedMultipart(EnumFacing face, double width, double heightMin, double heightMax) {
		this(width, heightMin, heightMax);
		this.face = face;
	}

	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		double p = 0.0625;
		double w = (1 - width) / 2;
		// double h = (1 - width) / 2;
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
		return EnumSet.of(PartSlot.getFaceSlot(face));
	}

	@Override
	public boolean rotatePart(EnumFacing axis) {
		Pair<Boolean, EnumFacing> rotate = rotatePart(face, axis);
		if (rotate.a) {
			if (getContainer().getPartInSlot(PartSlot.getFaceSlot(rotate.b)) != null) {
				return false;
			}
			if (isServer()) {
				UUID uuid = getUUID();
				BlockPos pos = getPos();
				World world = getWorld();
				getContainer().removePart(this);
				face = rotate.b;	
				firstTick=false;
				MultipartHelper.addPart(world, pos, this, uuid);
				//getWorld().notifyNeighborsOfStateChange(pos, getWorld().getBlockState(pos).getBlock());
				sendUpdatePacket(true);
			}
		}
		return rotate.a;
	}

	@Override
	public EnumFacing[] getValidRotations() {
		return EnumFacing.VALUES;
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound tag, SyncType type) {
		super.writeData(tag, type);
		tag.setByte("face", (byte) face.ordinal());
		return tag;
	}

	@Override
	public void readData(NBTTagCompound tag, SyncType type) {
		super.readData(tag, type);
		face = EnumFacing.VALUES[tag.getByte("face")];
	}

	@Override
	public void writeUpdatePacket(PacketBuffer buf) {
		super.writeUpdatePacket(buf);
		buf.writeByte((byte) face.ordinal());
	}

	@Override
	public void readUpdatePacket(PacketBuffer buf) {
		super.readUpdatePacket(buf);
		face = EnumFacing.VALUES[buf.readByte()];
	}

	@Override
	public IBlockState getActualState(IBlockState state) {
		World w = getContainer().getWorldIn();
		BlockPos pos = getContainer().getPosIn();
		return state.withProperty(ORIENTATION, face);
	}

	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(MCMultiPartMod.multipart, new IProperty[] { ORIENTATION });
	}

	@Override
	public boolean canConnect(EnumFacing dir) {
		return dir != face;
	}
}
