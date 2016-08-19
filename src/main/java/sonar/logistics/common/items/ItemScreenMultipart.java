package sonar.logistics.common.items;

import mcmultipart.item.ItemMultiPart;
import mcmultipart.multipart.IMultipart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import sonar.logistics.parts.ScreenMultipart;

public class ItemScreenMultipart extends ItemMultiPart {
	public final Class<? extends ScreenMultipart> type;

	public ItemScreenMultipart(Class<? extends ScreenMultipart> type) {
		this.type = type;
	}

	@Override
	public IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player) {
		try {
			EnumFacing rotation = EnumFacing.NORTH;
			if (side == EnumFacing.DOWN || side == EnumFacing.UP) {
				rotation = player.getHorizontalFacing().getOpposite();
			}
			return type.getConstructor(EnumFacing.class, EnumFacing.class).newInstance(side, rotation);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
