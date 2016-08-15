package sonar.logistics.common.items;

import mcmultipart.item.ItemMultiPart;
import mcmultipart.multipart.IMultipart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import sonar.logistics.parts.FacingMultipart;

public class ItemFacingMultipart extends ItemMultiPart {
	public final Class<? extends FacingMultipart> type;

	public ItemFacingMultipart(Class<? extends FacingMultipart> type) {
		this.type = type;
	}

	@Override
	public IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player) {
		try {
			return type.getConstructor(EnumFacing.class).newInstance(player.getHorizontalFacing().getOpposite());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
