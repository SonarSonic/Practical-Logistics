package sonar.logistics.common.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.integration.fmp.handlers.InventoryTileHandler;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.connecting.IConnectionArray;
import sonar.logistics.api.connecting.ITransceiver;

public class ArrayHandler extends InventoryTileHandler {

	public List<BlockCoords> coordList = Collections.EMPTY_LIST;

	public ArrayHandler(boolean isMultipart, TileEntity tile) {
		super(isMultipart, tile);
		this.slots = new ItemStack[8];
	}

	@Override
	public void update(TileEntity te) {
	}

	public void updateCoordsList() {
		List<BlockCoords> coordList = new ArrayList();
		try {
			for (int i = 0; i < 6; i++) {
				ItemStack stack = slots[i];
				if (stack != null && stack.getItem() instanceof ITransceiver && stack.hasTagCompound()) {
					ITransceiver trans = (ITransceiver) stack.getItem();
					coordList.add(trans.getCoords(stack));
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
