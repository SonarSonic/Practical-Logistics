package sonar.logistics.utils;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import sonar.logistics.registries.BlockRegistry;
import cpw.mods.fml.common.IWorldGenerator;

public class SapphireOreGen implements IWorldGenerator {
	public static int sapphireOreveinmin;
	public static int sapphireOreveinmax;
	public static int sapphireOreveinchance;
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.provider.dimensionId == 0) {
			generateOre(BlockRegistry.sapphire_ore, world, random, chunkX, chunkZ, sapphireOreveinmin, sapphireOreveinmax, sapphireOreveinchance, 1, 100, Blocks.stone);
		}
	}

	public static void generateOre(Block block, World world, Random random, int chunkX, int chunkZ, int minVeinSize, int maxVeinSize, int chance, int minY, int maxY, Block generateIn) {
		int veinSize = minVeinSize + random.nextInt(maxVeinSize - minVeinSize);
		int heightRange = maxY - minY;

		WorldGenMinable gen = new WorldGenMinable(block, veinSize);
		for (int i = 0; i < chance; i++) {
			int xRand = chunkX * 16 + random.nextInt(16);
			int yRand = random.nextInt(heightRange) + minY;
			int zRand = chunkZ * 16 + random.nextInt(16);
			gen.generate(world, random, xRand, yRand, zRand);
		}

	}
}
