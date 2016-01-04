package sonar.logistics.common.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.common.block.SonarMaterials;
import sonar.logistics.Logistics;
import sonar.logistics.common.tileentity.TileEntityInfoReader;
import sonar.logistics.network.LogisticsGui;

public class BlockInfoReader extends BlockDirectionalConnector {

	public BlockInfoReader() {
		super(SonarMaterials.machine);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityInfoReader();
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public void openGui(World world, int x, int y, int z, EntityPlayer player) {
		TileEntity target = world.getTileEntity(x, y, z);
		if (!world.isRemote && target != null && target instanceof TileEntityInfoReader) {
			TileEntityInfoReader node = (TileEntityInfoReader) target;
			node.sendAvailableData(player);
		}
		player.openGui(Logistics.instance, LogisticsGui.infoNode, world, x, y, z);
	}
}
