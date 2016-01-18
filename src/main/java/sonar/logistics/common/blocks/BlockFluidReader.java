package sonar.logistics.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.common.block.SonarMaterials;
import sonar.logistics.Logistics;
import sonar.logistics.common.tileentity.TileEntityFluidReader;
import sonar.logistics.network.LogisticsGui;

public class BlockFluidReader extends BlockDirectionalConnector {

	public BlockFluidReader() {
		super(SonarMaterials.machine);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityFluidReader();
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public void openGui(World world, int x, int y, int z, EntityPlayer player) {
		TileEntity target = world.getTileEntity(x, y, z);
		if (!world.isRemote && target != null && target instanceof TileEntityFluidReader) {
			TileEntityFluidReader node = (TileEntityFluidReader) target;
			player.openGui(Logistics.instance, LogisticsGui.fluidReader, world, x, y, z);
		}
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldblock, int oldMetadata) {
		world.removeTileEntity(x, y, z);
	}

}
