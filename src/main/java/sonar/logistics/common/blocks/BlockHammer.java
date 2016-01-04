package sonar.logistics.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import sonar.core.common.block.SonarMachineBlock;
import sonar.core.common.block.SonarMaterials;
import sonar.logistics.Logistics;
import sonar.logistics.common.tileentity.TileEntityHammer;
import sonar.logistics.network.LogisticsGui;
import sonar.logistics.registries.BlockRegistry;

public class BlockHammer extends SonarMachineBlock {

	public BlockHammer() {
		super(SonarMaterials.machine);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityHammer();
	}

	@Override
	public boolean operateBlock(World world, int x, int y, int z, EntityPlayer player, int side, float hitx, float hity, float hitz) {
		if (player != null) {
			player.openGui(Logistics.instance, LogisticsGui.hammer, world, x, y, z);
			return true;
		}
		return false;
	}

	public boolean hasSpecialRenderer() {
		return true;
	}

	@Override
	public boolean dropStandard(World world, int x, int y, int z) {
		return true;
	}

	private void setBlocks(World world, int x, int y, int z, int i) {
		world.setBlock(x, y + 1, z, BlockRegistry.hammer_air, i, 2);
		world.setBlock(x, y + 2, z, BlockRegistry.hammer_air, i, 2);

	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldblock, int oldMetadata) {
		super.breakBlock(world, x, y, z, oldblock, oldMetadata);
		world.setBlockToAir(x, y + 1, z);
		world.setBlockToAir(x, y + 2, z);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		setBlocks(world, x, y, z, world.getBlockMetadata(x, y, z));
	}

	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 3.0F, 1.0F);
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		if (world.getBlock(x, y + 1, z) != Blocks.air) {
			return false;
		}
		if (world.getBlock(x, y + 2, z) != Blocks.air) {
			return false;
		}
		return true;

	}
}
