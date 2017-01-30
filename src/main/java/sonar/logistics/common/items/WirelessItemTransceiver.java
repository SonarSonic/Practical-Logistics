package sonar.logistics.common.items;

import java.util.List;

import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.MultipartHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.api.utils.BlockCoords;
import sonar.core.common.item.SonarItem;
import sonar.core.helpers.FontHelper;
import sonar.logistics.api.connecting.ITileTransceiver;

public class WirelessItemTransceiver extends SonarItem implements ITileTransceiver {

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		IBlockState state = world.getBlockState(pos);
		Block target = state.getBlock();
		if (target != null && !world.isRemote) {
			IMultipartContainer container = MultipartHelper.getPartContainer(world, pos);
			if (container == null) { // ADD COMPATIBILITY FOR MULTIPARTS HERE
				NBTTagCompound tag = stack.getTagCompound();
				if (tag == null)
					tag = new NBTTagCompound();
				tag.setTag("coord", BlockCoords.writeToNBT(new NBTTagCompound(), new BlockCoords(pos, world.provider.getDimension())));
				tag.setInteger("dir", side.ordinal());
				tag.setString("targetName", target.getUnlocalizedName());
				FontHelper.sendMessage(stack.hasTagCompound() ? "Overwritten Position" : "Saved Position", world, player);
				stack.setTagCompound(tag);
			}
		}
		return EnumActionResult.SUCCESS;
	}

	@Override
	public BlockCoords getCoords(ItemStack stack) {
		if (stack.hasTagCompound()) {
			return BlockCoords.readFromNBT(stack.getTagCompound().getCompoundTag("coord"));
		}
		return null;
	}

	@Override
	public EnumFacing getDirection(ItemStack stack) {
		if (stack.hasTagCompound()) {
			return EnumFacing.values()[stack.getTagCompound().getInteger("dir")];
		}
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par) {
		super.addInformation(stack, player, list, par);
		if (stack.hasTagCompound()) {
			list.add("Block: " + TextFormatting.ITALIC + FontHelper.translate(stack.getTagCompound().getString("targetName") + ".name"));
			list.add("Coords: " + TextFormatting.ITALIC + getCoords(stack).toString());
			list.add("Side: " + TextFormatting.ITALIC + getDirection(stack).name());
		}
	}

}
