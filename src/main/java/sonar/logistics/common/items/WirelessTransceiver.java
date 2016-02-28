package sonar.logistics.common.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.common.item.SonarItem;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.FontHelper;
import sonar.core.utils.helpers.SonarHelper;
import sonar.logistics.api.connecting.ITransceiver;

public class WirelessTransceiver extends SonarItem implements ITransceiver {

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitx, float hity, float hitz) {
		Block target = world.getBlock(x, y, z);
		if (target != null) {
			if (!world.isRemote) {
				NBTTagCompound tag = new NBTTagCompound();
				try {
					BlockCoords coords = new BlockCoords(x, y, z, world.provider.dimensionId);
					ForgeDirection dir = ForgeDirection.getOrientation(side);
					ItemStack block = SonarHelper.createStackedBlock(coords.getBlock(coords.getWorld()), world.getBlockMetadata(x, y, z));

					NBTTagCompound coordTag = new NBTTagCompound();
					BlockCoords.writeToNBT(coordTag, coords);
					tag.setTag("COORDS", coordTag);

					tag.setString("dir", dir.name());

					NBTTagCompound itemTag = new NBTTagCompound();
					block.writeToNBT(itemTag);
					tag.setTag("ITEM", itemTag);
				} catch (NullPointerException exception) {
					SonarCore.logger.error("Wireless Transceiver: Null", exception);
				} finally {
					if(stack.hasTagCompound()){
						FontHelper.sendMessage("Overwritten Position", world, player);
					}else{
						FontHelper.sendMessage("Saved Position", world, player);
					}
					stack.setTagCompound(tag);
				}

			}
			return true;
		}

		return false;
	}

	@Override
	public ItemStack getBlockStack(ItemStack stack) {
		if (stack.hasTagCompound()) {
			return ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag("ITEM"));
		}
		return null;
	}

	@Override
	public BlockCoords getCoords(ItemStack stack) {
		if (stack.hasTagCompound()) {
			return BlockCoords.readFromNBT(stack.getTagCompound().getCompoundTag("COORDS"));
		}
		return null;
	}

	@Override
	public ForgeDirection getDirection(ItemStack stack) {
		if (stack.hasTagCompound()) {
			return ForgeDirection.valueOf(stack.getTagCompound().getString("dir"));
		}
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par) {
		super.addInformation(stack, player, list, par);
		if (stack.hasTagCompound()) {
			try {
				list.add("Block: " + EnumChatFormatting.ITALIC + getBlockStack(stack).getDisplayName());
				list.add("Coords: " + EnumChatFormatting.ITALIC + getCoords(stack).toString());
				list.add("Side: " + EnumChatFormatting.ITALIC + getDirection(stack).name());
			} catch (NullPointerException exception) {
				SonarCore.logger.error("Wireless Transceiver: Add Info Null", exception);
			}
		}
	}

}
