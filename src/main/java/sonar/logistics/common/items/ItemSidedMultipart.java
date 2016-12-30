package sonar.logistics.common.items;

import mcmultipart.item.ItemMultiPart;
import mcmultipart.multipart.IMultipart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import sonar.logistics.common.multiparts.SidedMultipart;

public class ItemSidedMultipart extends ItemMultiPart {
	public final Class<? extends SidedMultipart> type;

	public ItemSidedMultipart(Class<? extends SidedMultipart> type) {
		this.type = type;
	}

	@Override
	public IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player) {
		try {			
			return type.getConstructor(EnumFacing.class).newInstance(side);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
