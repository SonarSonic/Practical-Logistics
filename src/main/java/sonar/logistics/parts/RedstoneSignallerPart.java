package sonar.logistics.parts;

import java.util.ArrayList;
import java.util.List;

import mcmultipart.MCMultiPartMod;
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
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.connecting.IEntityNode;

public class RedstoneSignallerPart extends SidedMultipart implements IEntityNode {

	public static final PropertyBool ACTIVE = PropertyBool.create("active");

	public RedstoneSignallerPart() {
		super(3 * 0.0625, 0.0625 * 1, 0.0625 * 6);
	}

	public RedstoneSignallerPart(EnumFacing face) {
		super(face, 5 * 0.0625, 0.0625 * 1, 0.0625 * 6);
	}

	@Override
	public List<Entity> getEntities() {
		return new ArrayList();
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
		return false;
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
}
