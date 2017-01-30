package sonar.logistics.api.info;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import sonar.logistics.api.display.IDisplayInfo;
import sonar.logistics.api.display.InfoContainer;
import sonar.logistics.api.display.ScreenInteractionEvent;
import sonar.logistics.api.info.monitor.IMonitorInfo;

/** implemented on info which can be clicked by the player */
public interface IAdvancedClickableInfo{

	/** @param event TODO
	 * @param renderInfo the infos current render properties
	 * @param player the player who clicked the info
	 * @param hand players hand
	 * @param stack players held item
	 * @param container TODO
	 * @return if the screen was clicked */
	public NBTTagCompound onClientClick(ScreenInteractionEvent event, IDisplayInfo renderInfo, EntityPlayer player, ItemStack stack, InfoContainer container);
		
	public void onClickEvent(InfoContainer container, IDisplayInfo displayInfo, ScreenInteractionEvent event, NBTTagCompound tag);
		
}
