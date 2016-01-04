package sonar.logistics.common.blocks;

import sonar.core.common.block.SonarMaterials;
import sonar.logistics.Logistics;
import sonar.logistics.common.tileentity.TileEntityEntityNode;
import sonar.logistics.network.LogisticsGui;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockEntityNode extends BaseNode {

	public BlockEntityNode() {
		super(SonarMaterials.machine, false);
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
		if (player != null && !world.isRemote)
			player.openGui(Logistics.instance, LogisticsGui.entityNode, world, x, y, z);
	}
	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityEntityNode();
	}


	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		this.setBlockBounds((float) 0.0625 * 5, (float) 0.0625 * 6, (float) 0.0625 * 5, (float) (1 - (0.0625 * 5)), (float) (1 - (0.0625 * 3)), (float) (1 - (0.0625 * 5)));
	}
	
}
