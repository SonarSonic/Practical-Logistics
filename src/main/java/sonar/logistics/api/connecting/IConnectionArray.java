package sonar.logistics.api.connecting;

import net.minecraft.item.ItemStack;

public interface IConnectionArray extends IConnectionNode {

	public ItemStack[] getTransceivers();
}
