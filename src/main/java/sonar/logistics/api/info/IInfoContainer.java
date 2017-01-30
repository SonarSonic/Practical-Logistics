package sonar.logistics.api.info;

import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.api.utils.BlockInteractionType;
import sonar.core.network.sync.IDirtyPart;
import sonar.core.network.sync.ISyncableListener;
import sonar.logistics.api.display.DisplayInfo;
import sonar.logistics.api.display.IInfoDisplay;
import sonar.logistics.common.multiparts.ScreenMultipart;

/** used for storing display info to be used on Screens */
public interface IInfoContainer extends INBTSyncable, IDirtyPart, ISyncableListener {

	/** if this Display requires this InfoUUID to be synced */
	public boolean monitorsUUID(InfoUUID id);

	/** get the current info UUID of the Monitor Info at the given position */
	public InfoUUID getInfoUUID(int pos);

	/** set the current info UUID at the given position */
	public void setUUID(InfoUUID id, int pos);

	/** renders the container, you should never need to call this yourself */
	public void renderContainer();

	/** the maximum amount of info to be displayed at a given time */
	public int getMaxCapacity();

	/** called when a display associated with this Container is clicked */
	public boolean onClicked(ScreenMultipart part, BlockInteractionType type, World world, EntityPlayer player, EnumHand hand, ItemStack stack, PartMOP hit);

	/** gets the display this InfoContainer is connected to */
	public IInfoDisplay getDisplay();

	public DisplayInfo getDisplayInfo(int pos);
}
