package sonar.logistics.parts;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import mcmultipart.MCMultiPartMod;
import mcmultipart.multipart.IRedstonePart;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.network.sync.SyncUnidentifiedObject;
import sonar.core.network.utils.IByteBufTile;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.connecting.IEntityNode;

public class RedstoneSignallerPart extends SidedMultipart implements IRedstonePart, IByteBufTile {

	public static final PropertyBool ACTIVE = PropertyBool.create("active");
	public SyncUnidentifiedObject object = new SyncUnidentifiedObject(1);

	public RedstoneSignallerPart() {
		super(3 * 0.0625, 0.0625 * 1, 0.0625 * 6);
	}

	public RedstoneSignallerPart(EnumFacing face) {
		super(face, 5 * 0.0625, 0.0625 * 1, 0.0625 * 6);
	}
	
	public void update(){
		super.update();
	}

	@Override
	public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, PartMOP hit) {
		return false;
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(LogisticsItems.partRedstoneSignaller);
	}

	public boolean isActive() {
		return true;
	}

	@Override
	public IBlockState getActualState(IBlockState state) {
		World w = getContainer().getWorldIn();
		BlockPos pos = getContainer().getPosIn();
		return state.withProperty(ORIENTATION, face).withProperty(ACTIVE, isActive());
	}

	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(MCMultiPartMod.multipart, new IProperty[] { ORIENTATION, ACTIVE });
	}

	@Override
	public boolean canConnectRedstone(EnumFacing side) {
		return side == face;
	}

	@Override
	public int getWeakSignal(EnumFacing side) {
		return side == face ? 15 : 0;
	}

	@Override
	public int getStrongSignal(EnumFacing side) {
		return side == face ? 15 : 0;
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		switch (id) {
		case 0:
			object.writeToBuf(buf);
			break;
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		switch (id) {
		case 0:
			object.readFromBuf(buf);
			break;
		}
	}
}
