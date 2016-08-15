package sonar.logistics.api.wrappers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import sonar.core.api.fluids.StoredFluidStack;
import sonar.core.api.utils.ActionType;
import sonar.logistics.api.cache.INetworkCache;

public class FluidWrapper {

	/** used for adding Fluids to the network
	 * @param add {@link StoredFluidStack} to add
	 * @param network the {@link INetworkCache} to add to
	 * @param action should this action be simulated
	 * @return remaining {@link StoredFluidStack} (what wasn't added), can be null */
	@Deprecated
	public StoredFluidStack addFluids(StoredFluidStack add, INetworkCache network, ActionType action) {
		return add;
	}

	/** used for removing Fluids from the network
	 * @param remove {@link StoredFluidStack} to remove
	 * @param network the {@link INetworkCache} to remove from
	 * @param action should this action be simulated
	 * @return remaining {@link StoredFluidStack} (what wasn't removed), can be null */
	@Deprecated
	public StoredFluidStack removeFluids(StoredFluidStack remove, INetworkCache network, ActionType action) {
		return remove;
	}

	/** used for filling ItemStacks with the network
	 * @param container the {@link ItemStack} to try and fill
	 * @param fill the {@link StoredFluidStack} type to fill with
	 * @param network the {@link INetworkCache} to fill from
	 * @param action should this action be simulated
	 * @return the new ItemStack */
	@Deprecated
	public ItemStack fillFluidItemStack(ItemStack container, StoredFluidStack fill, INetworkCache network, ActionType action) {
		return container;
	}

	/** used for draining ItemStacks with the network
	 * @param container the {@link ItemStack} to try and drain
	 * @param network the {@link INetworkCache} to drain into
	 * @param action should this action be simulated
	 * @return the new ItemStack */
	@Deprecated
	public ItemStack drainFluidItemStack(ItemStack container, INetworkCache network, ActionType action) {
		return container;
	}

	/** fills the players current item with a specific fluid from the network
	 * @param player the player interacting
	 * @param cache the network to fill from
	 * @param toFill the {@link StoredFluidStack} to fill with */
	@Deprecated
	public void fillHeldItem(EntityPlayer player, INetworkCache cache, StoredFluidStack toFill) {
	}

	/** drains the players current item into the network
	 * @param player the player interacting
	 * @param cache the network to drain into */
	@Deprecated
	public void drainHeldItem(EntityPlayer player, INetworkCache cache) {
	}
}
