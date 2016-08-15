package sonar.logistics.common.blocks;

import java.util.Random;

import net.minecraft.block.BlockOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import sonar.core.SonarCore;
import sonar.logistics.LogisticsItems;

public class BlockSapphireOre extends BlockOre {

	public BlockSapphireOre() {
		setHarvestLevel("pickaxe", 2);
	}

	public Item getItemDropped(int meta, Random rand, int fortune) {
		return LogisticsItems.sapphire;
	}

	public int quantityDropped(Random rand) {
		return 1;
	}

	@Override
	public int getExpDrop(IBlockState state, net.minecraft.world.IBlockAccess world, BlockPos pos, int fortune) {
		return MathHelper.getRandomIntegerInRange(SonarCore.rand, 1, 3);
	}
}
