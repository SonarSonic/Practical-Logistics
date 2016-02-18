package sonar.logistics.integration;

import sonar.core.fluid.StoredFluidStack;
import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.ActionType;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.MachineSource;
import appeng.api.storage.IExternalStorageHandler;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;

public class AE2Helper {

	public static SourceHandler sourceHandler = new SourceHandler();

	public static IMEInventory getMEInventory(TileEntity tile, ForgeDirection dir, StorageChannel channel) {
		IExternalStorageHandler handler = AEApi.instance().registries().externalStorage().getHandler(tile, dir, channel, sourceHandler);
		if (handler != null) {
			IMEInventory inv = handler.getInventory(tile, dir, channel, sourceHandler);
			return inv;
		}
		return null;
	}

	public static IAEItemStack convertStoredItemStack(StoredItemStack stack) {
		return AEApi.instance().storage().createItemStack(stack.item).setStackSize(stack.stored);
	}

	public static IAEFluidStack convertStoredFluidStack(StoredFluidStack stack) {
		return AEApi.instance().storage().createFluidStack(stack.fluid).setStackSize(stack.stored);
	}

	public static StoredItemStack convertAEItemStack(IAEStack stack) {
		if (stack.isItem()) {
			IAEItemStack item = (IAEItemStack) stack;
			return new StoredItemStack(item.getItemStack(), item.getStackSize());
		}
		return null;
	}

	public static StoredFluidStack convertAEFluidStack(IAEStack stack) {
		if (stack.isFluid()) {
			IAEFluidStack fluid = (IAEFluidStack) stack;
			return new StoredFluidStack(fluid.getFluidStack(), fluid.getStackSize());
		}
		return null;
	}

	public static Actionable getActionable(ActionType action) {
		switch (action) {
		case PERFORM:
			return Actionable.MODULATE;
		default:
			return Actionable.SIMULATE;
		}
	}

	public static ActionType getActionType(Actionable action) {
		switch (action) {
		case MODULATE:
			return ActionType.PERFORM;
		default:
			return ActionType.SIMULATE;
		}
	}
	public static class SourceHandler extends BaseActionSource {

	}

}
