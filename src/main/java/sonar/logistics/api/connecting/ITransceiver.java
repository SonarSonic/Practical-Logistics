package sonar.logistics.api.connecting;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.BlockCoords;

/** implemented on Items which can provide Channels for a network when placed in a {@link IConnectionArray} */
public interface ITransceiver {

	/** gets the BlockStack retrieved from the last clicked coordinates, typically used for displaying the name, could be null */
	public String getUnlocalizedName(ItemStack stack);

	/** the connected BlockCoords, this should include dimension, could be null */
	public BlockCoords getCoords(ItemStack stack);

	/** the side the transceiver was clicked on, could be null */
	public EnumFacing getDirection(ItemStack stack);
}
