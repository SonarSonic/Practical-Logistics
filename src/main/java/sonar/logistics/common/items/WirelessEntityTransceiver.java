package sonar.logistics.common.items;

import java.util.List;
import java.util.UUID;

import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.MultipartHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
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
import sonar.logistics.api.connecting.IEntityTransceiver;
import sonar.logistics.api.connecting.ITileTransceiver;

public class WirelessEntityTransceiver extends SonarItem implements IEntityTransceiver {

	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if (player.isSneaking()) {
			if (!world.isRemote) {
				onRightClickEntity(player, stack, player);
			}
			return new ActionResult(EnumActionResult.SUCCESS, stack);
		}
		return new ActionResult(EnumActionResult.PASS, stack);
	}

	@Override
	public void onRightClickEntity(EntityPlayer player, ItemStack stack, Entity entity) {
		if (entity != null) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag == null)
				tag = new NBTTagCompound();
			tag.setUniqueId("uuid", entity.getPersistentID());
			tag.setString("targetName", entity.getDisplayName().getFormattedText());
			FontHelper.sendMessage(stack.hasTagCompound() ? "Overwritten Entity" : "Saved Entity", player.getEntityWorld(), player);
			stack.setTagCompound(tag);
		}
	}

	@Override
	public UUID getEntityUUID(ItemStack stack) {
		if (stack.hasTagCompound()) {
			return stack.getTagCompound().getUniqueId("uuid");
		}
		return null;
	}

	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par) {
		super.addInformation(stack, player, list, par);
		if (stack.hasTagCompound()) {
			list.add("Entity: " + TextFormatting.ITALIC + FontHelper.translate(stack.getTagCompound().getString("targetName") + ".name"));
			list.add("UUID: " + TextFormatting.ITALIC + getEntityUUID(stack).toString());
		}
	}
}
