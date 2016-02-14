package sonar.logistics.common.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.logistics.Logistics;
import sonar.logistics.common.tileentity.TileEntityInfoCreator;
import sonar.logistics.network.LogisticsGui;

public class BlockInfoCreator extends BlockFramedCable {

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityInfoCreator();
	}

	@Override
	public void openGui(World world, int x, int y, int z, EntityPlayer player) {
		player.openGui(Logistics.instance, LogisticsGui.infoCreator, world, x, y, z);
	}

}
