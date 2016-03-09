package sonar.logistics.api.connecting;

import net.minecraft.item.ItemStack;

/** implemented on Transceiver Arrays, or any other {@link ILogicTile} which could contain Transceivers for use on the network */
public interface IConnectionArray extends IConnectionNode {

	/** for retrieving the current Transceivers, normally just the inventory slots, therefore Transceivers on the list could be null, or another Type of Item, but preferably not
	 * @return the Transceiver ItemStacks, with Items that implement {@link ITransceiver} */
	public ItemStack[] getTransceivers();
}
