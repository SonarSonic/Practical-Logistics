package sonar.logistics.api.info;

import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import sonar.core.api.utils.BlockInteractionType;
import sonar.logistics.api.display.IInfoDisplay;

public interface IInfoContainer {

	// public void updateInfo(InfoUUID id, ByteBuf updateBuf);

	public InfoUUID getInfoUUID(int pos);

	public void setUUID(InfoUUID id, int pos);

	public void renderContainer();

	public int getMaxCapacity();

	public boolean onClicked(BlockInteractionType type, EntityPlayer player, EnumHand hand, ItemStack stack, PartMOP hit);
	
	public IInfoDisplay getDisplay();
}
