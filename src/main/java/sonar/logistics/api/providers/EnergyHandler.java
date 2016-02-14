package sonar.logistics.api.providers;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.energy.StoredEnergyStack;
import sonar.core.utils.IRegistryObject;
import sonar.logistics.Logistics;

/**
 * used for providing information on Block/TileEntity for the Info Reader to
 * read, the Provider must be registered in the PractialLogisticsAPI to be used
 */
public abstract class EnergyHandler implements IRegistryObject {

	public byte getID() {
		return Logistics.energyProviders.getObjectID(getName());
	}

	/** the name the info helper will be registered too */
	public abstract String getName();

	public abstract boolean canProvideInfo(TileEntity tile, ForgeDirection dir);

	public abstract void getEnergyInfo(StoredEnergyStack energyStack, TileEntity tile, ForgeDirection dir);

	/** returns how much was successfully added */
	public abstract double addEnergy(double transfer, TileEntity tile, ForgeDirection dir);

	/** returns how much was successfully removed */
	public abstract double removeEnergy(double transfer, TileEntity tile, ForgeDirection dir);

	/**
	 * used when the provider is loaded normally used to check if relevant mods
	 * are loaded for APIs to work
	 */
	public boolean isLoadable() {
		return true;
	}

}
