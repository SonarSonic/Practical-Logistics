package sonar.logistics.common.items;

import java.util.List;

import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.MultipartHelper;
import mcmultipart.raytrace.RayTraceUtils;
import mcmultipart.raytrace.RayTraceUtils.AdvancedRayTraceResultPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import sonar.core.common.item.SonarItem;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.SonarHelper;
import sonar.core.integration.multipart.SonarMultipartHelper;
import sonar.core.utils.IGuiItem;
import sonar.logistics.Logistics;
import sonar.logistics.api.connecting.IChannelledTile;
import sonar.logistics.api.connecting.IOperatorTile;
import sonar.logistics.api.connecting.IOperatorTool;
import sonar.logistics.api.connecting.OperatorMode;

public class ItemOperator extends SonarItem implements IOperatorTool {

	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		IMultipartContainer container = (IMultipartContainer) MultipartHelper.getPartContainer(world, pos);
		if (container != null) {
			Vec3d start = RayTraceUtils.getStart(player);
			Vec3d end = RayTraceUtils.getEnd(player);
			AdvancedRayTraceResultPart result = SonarMultipartHelper.collisionRayTrace(container, start, end);
			IMultipart part = result.hit.partHit;
			OperatorMode mode = getOperatorMode(stack);
			switch (mode) {
			case ANALYSE:
				break;
			case DEFAULT:
				if (part != null && part instanceof IOperatorTile) {
					boolean operation = ((IOperatorTile) part).performOperation(result, mode, player, hand, facing, hitX, hitY, hitZ);
					return operation ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
				}
				break;
			case CHANNELS:
				if (part != null && part instanceof IChannelledTile) {
					boolean operation = ((IOperatorTile) part).performOperation(result, mode, player, hand, facing, hitX, hitY, hitZ);
					return operation ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
				}
				break;
			case INFO:
				break;
			case ROTATE:
				break;
			default:
				break;

			}
		}
		return EnumActionResult.PASS;
	}

	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if (player.isSneaking()) {
			stack = changeOperatorMode(stack);
			FontHelper.sendMessage("Mode: " + getOperatorMode(stack), world, player);
			return new ActionResult(EnumActionResult.PASS, stack);
		}
		return new ActionResult(EnumActionResult.PASS, stack);
	}

	public ItemStack changeOperatorMode(ItemStack stack) {
		OperatorMode mode = SonarHelper.incrementEnum(getOperatorMode(stack), OperatorMode.values());
		NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
		tag.setInteger("mode", mode.ordinal());
		stack.setTagCompound(tag);
		return stack;
	}

	@Override
	public OperatorMode getOperatorMode(ItemStack stack) {
		if (stack.hasTagCompound()) {
			return OperatorMode.values()[stack.getTagCompound().getInteger("mode")];
		}
		return OperatorMode.DEFAULT;
	}

	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		super.addInformation(stack, player, list, par4);
		list.add("Mode: " + getOperatorMode(stack));
	}
}
