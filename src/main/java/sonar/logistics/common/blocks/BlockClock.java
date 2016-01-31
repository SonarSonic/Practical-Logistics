package sonar.logistics.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.block.SonarMaterials;
import sonar.logistics.Logistics;
import sonar.logistics.common.tileentity.TileEntityClock;
import sonar.logistics.common.tileentity.TileEntityDataReceiver;
import sonar.logistics.network.LogisticsGui;

public class BlockClock extends BaseNode {

	public BlockClock() {
		super(SonarMaterials.machine);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityClock();
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public void openGui(World world, int x, int y, int z, EntityPlayer player) {
		TileEntity target = world.getTileEntity(x, y, z);
		player.openGui(Logistics.instance, LogisticsGui.clock, world, x, y, z);
	}

	public boolean hasSpecialRenderer() {
		return true;
	}

	public boolean hasSpecialCollisionBox() {
		return false;
	}

	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if (!world.isRemote) {
			TileEntity target = world.getTileEntity(x, y, z);
			if(target instanceof TileEntityClock){
				TileEntityClock clock = (TileEntityClock) target;
				clock.checkStopwatch();
			}
		}

	}

}
