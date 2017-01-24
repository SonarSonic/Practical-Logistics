package sonar.logistics.info.handlers;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import sonar.logistics.Logistics;
import sonar.logistics.api.asm.CustomTileHandler;
import sonar.logistics.api.info.ICustomTileHandler;
import sonar.logistics.api.info.types.LogicInfo;
import sonar.logistics.info.LogicInfoRegistry;
import sonar.logistics.info.LogicInfoRegistry.RegistryType;

@CustomTileHandler(handlerID = "normal-fluids", modid = Logistics.MODID)
public class NormalFluidHandler implements ICustomTileHandler {

	@Override
	public boolean canProvideInfo(World world, IBlockState state, BlockPos pos, EnumFacing dir, TileEntity tile, Block block) {
		return block != null && block instanceof BlockLiquid;
	}

	@Override
	public void addInfo(List<LogicInfo> infoList, World world, IBlockState state, BlockPos pos, EnumFacing dir, TileEntity tile, Block block) {
		BlockLiquid liquid = (BlockLiquid) block;
		Fluid fluid = state.getMaterial() == Material.WATER ? FluidRegistry.WATER : FluidRegistry.LAVA;
		LogicInfoRegistry.getAssignableMethods(fluid.getClass(), RegistryType.BLOCK).forEach(method -> LogicInfoRegistry.getClassInfo(infoList, RegistryType.BLOCK, fluid, method, world, state, pos, dir, tile, block));
	}

}
