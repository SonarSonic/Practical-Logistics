package sonar.logistics.integration.multipart;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import sonar.logistics.registries.BlockRegistry;
import codechicken.lib.vec.BlockCoord;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.MultiPartRegistry.IPartConverter;
import codechicken.multipart.MultiPartRegistry.IPartFactory;
import codechicken.multipart.TMultiPart;

public class ForgeMultipartHandler implements IPartFactory, IPartConverter {
	public static ForgeMultipartHandler instance;

	@Override
	public TMultiPart createPart(String name, boolean client) {
		if (name.equals("Screen Part"))
			return new DisplayScreenPart();
		if (name.equals("Cable Part"))
			return new DataCablePart();
		if (name.equals("Info Reader"))
			return new InfoReaderPart();
		if (name.equals("Inventory Reader"))
			return new InventoryReaderPart();
		if (name.equals("Info Creator"))
			return new InfoCreatorPart();
		if (name.equals("Data Modifier"))
			return new DataModifierPart();
		if (name.equals("Fluid Reader"))
			return new FluidReaderPart();
		return null;
	}

	public static void init() {
		instance = new ForgeMultipartHandler();
		MultiPartRegistry.registerConverter(instance);
		MultiPartRegistry.registerParts(instance, new String[] { "Screen Part","Cable Part","Info Reader","Inventory Reader","Info Creator","Data Modifier","Fluid Reader" });
	}

	@Override
	public Iterable<Block> blockTypes() {
		return Arrays.asList(BlockRegistry.displayScreen,BlockRegistry.dataCable, BlockRegistry.infoReader, BlockRegistry.inventoryReader,BlockRegistry.infoCreator,BlockRegistry.dataModifier,BlockRegistry.fluidReader);
	}

	@Override
	public TMultiPart convert(World world, BlockCoord pos) {
		Block b = world.getBlock(pos.x, pos.y, pos.z);
		int meta = world.getBlockMetadata(pos.x, pos.y, pos.z);
		if (b == BlockRegistry.displayScreen) {
			return new DisplayScreenPart(meta);
		}
		if (b == BlockRegistry.dataCable) {
			return new DataCablePart(meta);
		}
		if (b == BlockRegistry.infoReader) {
			return new InfoReaderPart(meta);
		}
		if (b == BlockRegistry.inventoryReader) {
			return new InventoryReaderPart(meta);
		}
		if (b == BlockRegistry.infoCreator) {
			return new InfoCreatorPart(meta);
		}
		if (b == BlockRegistry.dataModifier) {
			return new DataModifierPart(meta);
		}
		if (b == BlockRegistry.fluidReader) {
			return new FluidReaderPart(meta);
		}
		return null;
	}
}
