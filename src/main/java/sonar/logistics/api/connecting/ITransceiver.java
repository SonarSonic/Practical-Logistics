package sonar.logistics.api.connecting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.BlockCoords;

public interface ITransceiver {

	public ItemStack getBlockStack(ItemStack stack);
	
	public BlockCoords getCoords(ItemStack stack);
	
	public ForgeDirection getDirection(ItemStack stack);
}
