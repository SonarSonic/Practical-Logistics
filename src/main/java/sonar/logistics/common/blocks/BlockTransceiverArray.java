package sonar.logistics.common.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.api.ActionType;
import sonar.core.api.SonarAPI;
import sonar.core.api.StoredItemStack;
import sonar.core.common.block.SonarMaterials;
import sonar.logistics.Logistics;
import sonar.logistics.common.tileentity.TileEntityArray;
import sonar.logistics.network.LogisticsGui;
import sonar.logistics.registries.ItemRegistry;

public class BlockTransceiverArray extends BaseNode {

	public BlockTransceiverArray() {
		super(SonarMaterials.machine);
		this.disableOrientation();
	}

	public boolean hasSpecialRenderer() {
		return true;
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public void openGui(World world, int x, int y, int z, EntityPlayer player) {
		if (player != null && !world.isRemote) {
			ItemStack held = player.getHeldItem();
			if (held != null && held.getItem() == ItemRegistry.transceiver) {
				TileEntity tile = world.getTileEntity(x, y, z);
				StoredItemStack item = new StoredItemStack(held);
				StoredItemStack stack = SonarAPI.getItemHelper().getStackToAdd(held.stackSize, item, SonarAPI.getItemHelper().addItems(tile, item.copy(), ForgeDirection.UP, ActionType.PERFORM, null));
				if (stack == null || stack.getStackSize()==0)
					held.stackSize-=1;
					return;
			}
			player.openGui(Logistics.instance, LogisticsGui.transceiverArray, world, x, y, z);

		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityArray();
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		this.setBlockBounds((float) 0.0625 * 3, (float) 0.0625 * 6, (float) 0.0625 * 3, (float) (1 - (0.0625 * 3)), (float) (1 - (0.0625 * 3)), (float) (1 - (0.0625 * 3)));
	}

}
