package sonar.logistics.common.blocks;

import java.util.Random;

import net.minecraft.block.BlockOre;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import sonar.logistics.registries.ItemRegistry;

public class BlockSapphireOre extends BlockOre {

	private Random rand = new Random();

	public BlockSapphireOre() {
		setHarvestLevel("pickaxe", 2);
	}

	public Item getItemDropped(int meta, Random rand, int fortune) {
		return ItemRegistry.sapphire;
	}

	public int quantityDropped(Random rand) {
		return 1;
	}

	@Override
	public int getExpDrop(IBlockAccess world, int meta, int fortune) {
		return MathHelper.getRandomIntegerInRange(rand, 1, 3);
	}
}
