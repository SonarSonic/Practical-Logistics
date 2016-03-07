package sonar.logistics.api.providers;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.ActionType;
import sonar.core.utils.IRegistryObject;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.utils.EnergyType;
import sonar.logistics.api.utils.StoredEnergyStack;

/** used for providing information on Block/TileEntity for the Info Reader to read, the Provider must be registered in the {@link LogisticsAPI} to be used */
public abstract class EnergyProvider implements IRegistryObject {

	public int getID() {
		return LogisticsAPI.getRegistry().getEntityProviderID(getName());
	}

	public abstract EnergyType getProvidedType();
	
	/** the name the info helper will be registered too */
	public abstract String getName();

	public abstract boolean canProvideInfo(TileEntity tile, ForgeDirection dir);

	public abstract void getEnergyInfo(StoredEnergyStack energyStack, TileEntity tile, ForgeDirection dir);

	/** returns how much was successfully added 
	 * @param action TODO*/
	public abstract double addEnergy(long transfer, TileEntity tile, ForgeDirection dir, ActionType action);

	/** returns how much was successfully removed 
	 * @param action TODO*/
	public abstract double removeEnergy(long transfer, TileEntity tile, ForgeDirection dir, ActionType action);

	/** used when the provider is loaded normally used to check if relevant mods are loaded for APIs to work */
	public boolean isLoadable() {
		return true;
	}

}
