package sonar.logistics.api.connecting;

import java.util.List;
import java.util.Map;

import sonar.core.utils.BlockCoords;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public interface IConnectionArray extends IConnectionNode {

	public ItemStack[] getTransceivers();
}
