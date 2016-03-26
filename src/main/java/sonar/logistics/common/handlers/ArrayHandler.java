package sonar.logistics.common.handlers;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.api.BlockCoords;
import sonar.core.integration.fmp.handlers.InventoryTileHandler;
import sonar.logistics.api.connecting.ITransceiver;

public class ArrayHandler extends InventoryTileHandler {

	public Map<BlockCoords, ForgeDirection> coordList = Collections.EMPTY_MAP;

	public ArrayHandler(boolean isMultipart, TileEntity tile) {
		super(isMultipart, tile);
		this.slots = new ItemStack[8];
	}

	@Override
	public void update(TileEntity te) {
	}

	public void updateCoordsList() {
		Map<BlockCoords, ForgeDirection>coordList = new LinkedHashMap();
		try {
			for (int i = 0; i < 8; i++) {
				ItemStack stack = slots[i];
				if (stack != null && stack.getItem() instanceof ITransceiver && stack.hasTagCompound()) {
					ITransceiver trans = (ITransceiver) stack.getItem();
					coordList.put(trans.getCoords(stack), trans.getDirection(stack));
				}
			}
		} catch (NullPointerException exception) {
			SonarCore.logger.error("[Transceiver Array] Issues reading coord lists", exception);
		}
		this.coordList = coordList;
	}

	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return stack != null && stack.getItem() instanceof ITransceiver;
	}
}
