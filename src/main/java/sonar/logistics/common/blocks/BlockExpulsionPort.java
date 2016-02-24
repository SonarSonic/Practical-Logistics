package sonar.logistics.common.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.logistics.common.tileentity.TileEntityExpulsionPort;

public class BlockExpulsionPort extends BlockNode {

		@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityExpulsionPort();
	}

}
