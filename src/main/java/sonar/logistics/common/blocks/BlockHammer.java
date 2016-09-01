package sonar.logistics.common.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import sonar.core.api.utils.BlockInteraction;
import sonar.core.common.block.SonarMachineBlock;
import sonar.core.common.block.SonarMaterials;
import sonar.core.utils.IGuiTile;
import sonar.logistics.Logistics;
import sonar.logistics.LogisticsBlocks;
import sonar.logistics.common.blocks.tileentity.TileEntityHammer;

public class BlockHammer extends SonarMachineBlock {

	public BlockHammer() {
		super(SonarMaterials.machine, true, true);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityHammer();
	}

	@Override
	public boolean operateBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, BlockInteraction interact){
		if (player != null) {
			player.openGui(Logistics.instance, IGuiTile.ID, world, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
		return false;
	}

	public boolean hasSpecialRenderer() {
		return true;
	}
	
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}

	@Override
	public boolean dropStandard(IBlockAccess world, BlockPos pos) {
		return true;
	}

	private void setBlocks(World world, BlockPos pos) {
		world.setBlockState(pos.offset(EnumFacing.UP), LogisticsBlocks.hammer_air.getDefaultState(), 2);
		world.setBlockState(pos.offset(EnumFacing.UP, 2), LogisticsBlocks.hammer_air.getDefaultState(), 2);
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		super.breakBlock(world, pos, state);
		world.setBlockToAir(pos.offset(EnumFacing.UP));
		world.setBlockToAir(pos.offset(EnumFacing.UP, 2));
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		super.onBlockAdded(world, pos, state);
		setBlocks(world, pos);
	}

	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 3.0F, 1.0F);
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		if (!world.isAirBlock(pos.offset(EnumFacing.UP, 1)) || !world.isAirBlock(pos.offset(EnumFacing.UP, 2))) {
			return false;
		}
		return true;

	}
}
