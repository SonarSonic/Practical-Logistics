package sonar.logistics.api.info;

import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

/** implemented on info which can be clicked by the player */
public interface IClickableInfo {

	/** @param renderInfo the infos current render properties
	 * @param player the player who clicked the info
	 * @param hand players hand
	 * @param stack players held item
	 * @param hit the RayTrace hit info
	 * @return if the screen was clicked */
	public boolean onClicked(RenderInfoProperties renderInfo, EntityPlayer player, EnumHand hand, ItemStack stack, PartMOP hit);

}
