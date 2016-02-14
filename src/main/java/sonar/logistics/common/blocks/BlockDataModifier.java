package sonar.logistics.common.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.logistics.Logistics;
import sonar.logistics.common.tileentity.TileEntityDataModifier;
import sonar.logistics.network.LogisticsGui;

public class BlockDataModifier extends BlockFramedCable {

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityDataModifier();
	}

	@Override
	public void openGui(World world, int x, int y, int z, EntityPlayer player) {
		player.openGui(Logistics.instance, LogisticsGui.dataModifier, world, x, y, z);
	}

}
