package sonar.logistics.api.connecting;

import net.minecraft.item.ItemStack;

/**implemented on an item which can be used as an IOperatorTool, typically this will be the Operator itself*/
public interface IOperatorTool {

	/**the current mode of the operator*/
	public OperatorMode getOperatorMode(ItemStack stack);

}
