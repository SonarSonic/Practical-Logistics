package sonar.logistics.api.providers;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.fluid.StoredFluidStack;
import sonar.core.utils.IRegistryObject;
import sonar.logistics.Logistics;
import sonar.logistics.api.LogisticsAPI;;

/** used for providing information on Fluids stored in Block/TileEntities for the Fluid Reader to read, the Provider must be registered in the {@link LogisticsAPI} to be used */
public abstract class FluidHandler implements IRegistryObject {

	public byte getID(){
		return Logistics.fluidProviders.getObjectID(getName());		
	}
	
	/** the name the info helper will be registered too */
	public abstract String getName();

	/**
	 * @param tile The World
	 * @param dir The direction of the Node to the Block
	 * @return can this provider give info for the block/tile in the world at x,y,z
	 */
	public abstract boolean canHandleFluids(TileEntity tile, ForgeDirection dir);

	/**only called if canProvideFluids is true*/
	public abstract void getFluids(List<StoredFluidStack> fluids, TileEntity tile, ForgeDirection dir);	

	/**returns what wasn't added*/
	public abstract StoredFluidStack addStack(StoredFluidStack add, TileEntity tile, ForgeDirection dir);

	/**returns what wasn't extracted*/
	public abstract StoredFluidStack removeStack(StoredFluidStack remove, TileEntity tile, ForgeDirection dir);
	
	/** used when the provider is loaded normally used to check if relevant mods are loaded for APIs to work */
	public boolean isLoadable() {
		return true;
	}
}
