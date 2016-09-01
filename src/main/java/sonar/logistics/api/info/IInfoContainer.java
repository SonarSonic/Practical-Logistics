package sonar.logistics.api.info;

import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import sonar.core.api.utils.BlockInteractionType;
import sonar.logistics.api.display.IInfoDisplay;

/**used for storing display info to be used on Screens*/
public interface IInfoContainer {

	/**get the current info UUID of the Monitor Info at the given position*/
	public InfoUUID getInfoUUID(int pos);

	/**set the current info UUID at the given position*/
	public void setUUID(InfoUUID id, int pos);

	/**renders the container, you should never need to call this yourself*/
	public void renderContainer();

	/**the maximum amount of info to be displayed at a given time*/
	public int getMaxCapacity();

	/**called when a display associated with this Container is clicked*/
	public boolean onClicked(BlockInteractionType type, EntityPlayer player, EnumHand hand, ItemStack stack, PartMOP hit);
	
	/**gets the display this InfoContainer is connected to*/
	public IInfoDisplay getDisplay();
}
