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
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import sonar.core.SonarCore;
import sonar.core.api.IFlexibleGui;
import sonar.core.common.item.SonarItem;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.SonarHelper;
import sonar.core.integration.multipart.SonarMultipartHelper;
import sonar.core.network.FlexibleGuiHandler;
import sonar.logistics.Logistics;
import sonar.logistics.api.connecting.IChannelledTile;
import sonar.logistics.api.connecting.IOperatorTile;
import sonar.logistics.api.connecting.IOperatorTool;
import sonar.logistics.api.connecting.OperatorMode;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.client.gui.GuiChannelSelection;
import sonar.logistics.common.containers.ContainerChannelSelection;
import sonar.logistics.helpers.CableHelper;

public class ItemOperator extends SonarItem implements IOperatorTool, IFlexibleGui<ItemStack> {

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
				if (!player.isSneaking()) {
					if (part != null && part instanceof IOperatorTile) {
						boolean operation = ((IOperatorTile) part).performOperation(result, mode, player, hand, facing, hitX, hitY, hitZ);
						return operation ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
					}
				} else {
				}

				break;
			case CHANNELS:
				if (part != null && part instanceof IChannelledTile) {
					if (!world.isRemote) {
						IChannelledTile tile = (IChannelledTile) part;
						NBTTagCompound tag = new NBTTagCompound();
						tag.setBoolean(FlexibleGuiHandler.ITEM, true);
						tag.setInteger(FlexibleGuiHandler.ID, 0);
						tag.setInteger("hash", tile.getIdentity().hashCode());
						SonarCore.instance.guiHandler.openGui(player, world, pos, 0, tag);
					}
					return EnumActionResult.SUCCESS;
				}
				//FIXME - Need a version for Logic Monitors
				break;
			case INFO:
				break;
			case ROTATE:
				return (part!=null && part.rotatePart(facing)) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
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
		} else if (world.isRemote) {
			OperatorMode mode = getOperatorMode(stack);
			if (mode == OperatorMode.INFO) {
				boolean isUsing = !Logistics.proxy.isUsingOperator();
				Logistics.proxy.setUsingOperator(isUsing);
				player.addChatComponentMessage(new TextComponentTranslation("Display Info: " + TextFormatting.AQUA + isUsing));
			}
		}
		return new ActionResult(EnumActionResult.PASS, stack);
	}

	public ItemStack changeOperatorMode(ItemStack stack) {
		OperatorMode mode = SonarHelper.incrementEnum(getOperatorMode(stack), OperatorMode.values());
		NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
		tag.setInteger("mode", mode.ordinal());
		stack.setTagCompound(tag);
		boolean isUsing = mode == OperatorMode.INFO;
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

	@Override
	public Object getServerElement(ItemStack obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		switch (id) {
		case 0:
			int hash = tag.getInteger("hash");
			ILogicMonitor part = CableHelper.getMonitorFromHashCode(hash, world.isRemote);
			if (part != null && part instanceof IChannelledTile) {
				return new ContainerChannelSelection((IChannelledTile) part);
			}
		}
		return null;
	}

	@Override
	public Object getClientElement(ItemStack obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		switch (id) {
		case 0:
			int hash = tag.getInteger("hash");
			ILogicMonitor part = CableHelper.getMonitorFromHashCode(hash, world.isRemote);
			if (part != null && part instanceof IChannelledTile) {
				return new GuiChannelSelection((IChannelledTile) part, 0);
			}
		}
		return null;
	}

}
