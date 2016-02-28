package sonar.logistics.api.connecting;

import java.util.List;

import sonar.core.utils.BlockCoords;
import net.minecraft.item.ItemStack;

public interface IConnectionArray extends ILogicTile {

	public ItemStack[] getTransceivers();
	
	public List<BlockCoords> getCoordList();
}
