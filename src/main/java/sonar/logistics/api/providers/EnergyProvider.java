package sonar.logistics.api.providers;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.fluid.StoredFluidStack;
import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.ActionType;
import sonar.core.utils.IRegistryObject;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.providers.InventoryHandler.StorageSize;
import sonar.logistics.api.utils.EnergyType;
import sonar.logistics.api.utils.StoredEnergyStack;

/** used for providing information on Block/TileEntity for the Info Reader to read, the Provider must be registered in the {@link LogisticsAPI} to be used */
public abstract class EnergyProvider extends LogicProvider {

	public int getID() {
		return LogisticsAPI.getRegistry().getEntityProviderID(getName());
	}
	/**the {@link EnergyType} this provider can handle*/
	public abstract EnergyType getProvidedType();

	/** @param tile the {@link TileEntity} to check
	 * @param dir the {@link ForgeDirection} to check from
	 * @return can this provider handle energy for this side of the TileEntity */
	public abstract boolean canProvideEnergy(TileEntity tile, ForgeDirection dir);

	/** used for adding an a {@link StoredEnergyStack} to the TileEntity
	 * @param add the {@link StoredEnergyStack} to add, the {@link EnergyType} will always be the provided type, therefore there is no need to convert stored amounts
	 * @param tile the {@link TileEntity} to check
	 * @param dir the {@link ForgeDirection} to check from
	 * @param action should this action be simulated
	 * @return what wasn't added */
	public abstract StoredEnergyStack addEnergy(StoredEnergyStack transfer, TileEntity tile, ForgeDirection dir, ActionType action);

	/** used for removing an a {@link StoredEnergyStack} from the Inventory
	 * @param remove the {@link StoredEnergyStack} to remove, the {@link EnergyType} will always be the provided type, therefore there is no need to convert stored amounts
	 * @param tile the {@link TileEntity} to check
	 * @param dir the {@link ForgeDirection} to check from
	 * @param action should this action be simulated
	 * @return what wasn't extracted */
	public abstract StoredEnergyStack removeEnergy(StoredEnergyStack transfer, TileEntity tile, ForgeDirection dir, ActionType action);

	/** only called if canProvideEnergy is true
	 * @param storedStacks current list of energy for the block, providers only add to this and don't remove.
	 * @param tile the {@link TileEntity} to check
	 * @param dir the {@link ForgeDirection} to check from */
	public abstract void getEnergy(StoredEnergyStack energyStack, TileEntity tile, ForgeDirection dir);

}
