package sonar.logistics.api.info;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ICustomTileHandler {

	public boolean canProvideInfo(World world, IBlockState state, BlockPos pos, EnumFacing dir, @Nullable TileEntity tile, @Nullable Block block);
	
	public void addInfo(List<LogicInfo> infoList, World world, IBlockState state, BlockPos pos, EnumFacing dir, @Nullable TileEntity tile, @Nullable Block block);
}
