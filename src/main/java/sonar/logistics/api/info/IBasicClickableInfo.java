package sonar.logistics.api.info;

import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import sonar.core.api.utils.BlockInteractionType;
import sonar.logistics.api.display.IDisplayInfo;
import sonar.logistics.api.display.InfoContainer;
import sonar.logistics.api.display.ScreenInteractionEvent;
import sonar.logistics.api.info.monitor.IMonitorInfo;

/** implemented on info which can be clicked by the player */
public interface IBasicClickableInfo{

	/** @param type TODO
	 * @param doubleClick TODO
	 * @param renderInfo the infos current render properties
	 * @param player the player who clicked the info
	 * @param hand players hand
	 * @param stack players held item
	 * @param hit the RayTrace hit info
	 * @param container TODO
	 * @return if the screen was clicked */
	public boolean onStandardClick(BlockInteractionType type, boolean doubleClick, IDisplayInfo renderInfo, EntityPlayer player, EnumHand hand, ItemStack stack, PartMOP hit, InfoContainer container);
				
}
