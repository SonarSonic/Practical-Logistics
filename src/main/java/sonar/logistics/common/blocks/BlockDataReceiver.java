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
import sonar.logistics.common.tileentity.TileEntityDataReceiver;
import sonar.logistics.network.LogisticsGui;

public class BlockDataReceiver extends BaseNode {

	public BlockDataReceiver() {
		super(SonarMaterials.machine);
		this.setBlockBounds((float) (4 * 0.0625), (float) (4 * 0.0625), (float) (4 * 0.0625), (float) (1 - 4 * 0.0625), (float) (1 - 4 * 0.0625), (float) (1 - 4 * 0.0625));

	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityDataReceiver();
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public void openGui(World world, int x, int y, int z, EntityPlayer player) {
		TileEntity target = world.getTileEntity(x, y, z);
		if (target != null && target instanceof TileEntityDataReceiver) {
			TileEntityDataReceiver sonar = (TileEntityDataReceiver) target;
			sonar.sendAvailableData(target, player);
		}
		player.openGui(Logistics.instance, LogisticsGui.dataReceiver, world, x, y, z);
	}

	public boolean hasSpecialRenderer() {
		return true;
	}
	public boolean hasSpecialCollisionBox() {
		return true;
	}

	public List<AxisAlignedBB> getCollisionBoxes(World world, int x, int y, int z, List<AxisAlignedBB> list) {
		this.setBlockBounds((float) (4 * 0.0625), (float) (4 * 0.0625), (float) (4 * 0.0625), (float) (1 - 4 * 0.0625), (float) (1 - 4 * 0.0625), (float) (1 - 4 * 0.0625));
		list.add(AxisAlignedBB.getBoundingBox(4 * 0.0625, 4 * 0.0625, 4 * 0.0625, 1 - 4 * 0.0625, 1 - 4 * 0.0625, 1 - 4 * 0.0625));

		return list;
	}
}
