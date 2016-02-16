package sonar.logistics.common.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.utils.BlockInteraction;
import sonar.logistics.Logistics;
import sonar.logistics.common.tileentity.TileEntityChannelSelector;
import sonar.logistics.network.LogisticsGui;

public class BlockChannelSelector extends BlockFramedCable {

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityChannelSelector();
	}

	@Override
	public void openGui(World world, int x, int y, int z, EntityPlayer player) {
		player.openGui(Logistics.instance, LogisticsGui.channelSelector, world, x, y, z);
	}

}
