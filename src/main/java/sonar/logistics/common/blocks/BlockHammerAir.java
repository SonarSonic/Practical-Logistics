package sonar.logistics.common.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import sonar.logistics.Logistics;
import sonar.logistics.LogisticsBlocks;
import sonar.logistics.network.LogisticsGui;

public class BlockHammerAir extends Block {
	public BlockHammerAir() {
		super(Material.CLOTH);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		world.setBlockToAir(pos);
		IBlockState hammerState;
		if ((hammerState = world.getBlockState(pos.offset(EnumFacing.DOWN))).getBlock() == LogisticsBlocks.hammer) {
			TileEntity i = world.getTileEntity(pos.offset(EnumFacing.DOWN));
			Block bi = hammerState.getBlock();
			bi.dropBlockAsItem(world, pos.offset(EnumFacing.DOWN), hammerState, 0);
			world.setBlockToAir(pos.offset(EnumFacing.DOWN));
		} else if ((hammerState = world.getBlockState(pos.offset(EnumFacing.DOWN, 2))).getBlock() == LogisticsBlocks.hammer) {
			TileEntity i = world.getTileEntity(pos.offset(EnumFacing.DOWN, 2));
			Block bi = hammerState.getBlock();
			bi.dropBlockAsItem(world, pos.offset(EnumFacing.DOWN, 2), hammerState, 0);
			world.setBlockToAir(pos.offset(EnumFacing.DOWN, 2));
		}

	}

	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ){
		if (world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() == LogisticsBlocks.hammer) {
			player.openGui(Logistics.instance, LogisticsGui.hammer, world, pos.getX(), pos.getY() - 1, pos.getZ());
			return true;
		} else if (world.getBlockState(pos.offset(EnumFacing.DOWN, 2)).getBlock() == LogisticsBlocks.hammer) {
			player.openGui(Logistics.instance, LogisticsGui.hammer, world, pos.getX(), pos.getY() - 2, pos.getZ());
			return true;
		}
		return false;

	}

	@Override
	public int quantityDropped(Random p_149745_1_) {
		return 0;
	}
/*
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		if (world.getBlockState(x, y - 1, z) == BlockRegistry.hammer) {
			this.setBlockBounds(0.0F, -1.0F, 0.0F, 1.0F, 2.0F, 1.0F);
		} else if (world.getBlock(x, y - 2, z) == BlockRegistry.hammer) {
			this.setBlockBounds(0.0F, -2.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		}
	}
*/
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player){
		return new ItemStack(LogisticsBlocks.hammer, 1, 0);
    }
}
