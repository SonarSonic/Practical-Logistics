package sonar.logistics.api.info;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.logistics.api.info.types.LogicInfo;

public interface ICustomTileHandler {

	/** returns if this tile handler can provide info at the given position */
	public boolean canProvideInfo(World world, IBlockState state, BlockPos pos, EnumFacing dir, @Nullable TileEntity tile, @Nullable Block block);

	/** allows you to add all types of info for a given position for use in the Info Reader
	 * @param infoList the current info list
	 * @param world the world
	 * @param state the current block state
	 * @param pos the position
	 * @param dir the direction to obtain info from
	 * @param tile the TileEntity (can be null)
	 * @param block the Block (can be null) */
	public void addInfo(List<LogicInfo> infoList, World world, IBlockState state, BlockPos pos, EnumFacing dir, @Nullable TileEntity tile, @Nullable Block block);
}
