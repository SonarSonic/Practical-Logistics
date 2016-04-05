package sonar.logistics.integration.multipart;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import sonar.logistics.Logistics;
import sonar.logistics.registries.BlockRegistry;
import codechicken.lib.vec.BlockCoord;
import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.MultiPartRegistry.IPartConverter;
import codechicken.multipart.MultiPartRegistry.IPartFactory;
import codechicken.multipart.TMultiPart;

public class ForgeMultipartHandler implements IPartFactory, IPartConverter {
	public static ForgeMultipartHandler instance;

	public static enum MultiPart {
		//
		DISPLAY_SCREEN("Screen Part"),
		//
		DATA_CABLE("Cable Part"),
		//
		INFO_READER("Info Reader"),
		//
		INV_READER("Inventory Reader"),
		//
		INFO_CREATOR("Info Creator"),
		//
		DATA_MODIFIER("Data Modifier"),
		//
		FLUID_READER("Fluid Reader"),
		//
		CHANNELLED_CABLE("Multi Cable Part"),

		ENERGY_READER("EnergyReader");

		public String name;

		MultiPart(String name) {
			this.name = name;
		}

		public static String[] getNames() {
			String[] names = new String[values().length];
			for (int i = 0; i < values().length; i++) {
				names[i] = values()[i].name;
			}
			return names;
		}

		public static MultiPart getPart(String name) {
			for (MultiPart part : values()) {
				if (part.name.equals(name)) {
					return part;
				}
			}
			Logistics.logger.error("NO MULTIPART NAMED: " + name);
			return null;
		}
	}

	public static void init() {
		instance = new ForgeMultipartHandler();
		MultiPartRegistry.registerConverter(instance);
		MultiPartRegistry.registerParts(instance, MultiPart.getNames());
	}

	@Override
	public Iterable<Block> blockTypes() {
		return Arrays.asList(BlockRegistry.displayScreen, BlockRegistry.dataCable, BlockRegistry.infoReader, BlockRegistry.inventoryReader, BlockRegistry.infoCreator, BlockRegistry.dataModifier, BlockRegistry.fluidReader, BlockRegistry.channelledCable, BlockRegistry.energyReader);
	}

	@Override
	public TMultiPart createPart(String name, boolean client) {
		MultiPart part = MultiPart.getPart(name);
		if (part != null) {
			switch (part) {
			case DISPLAY_SCREEN:
				return new DisplayScreenPart();
			case DATA_CABLE:
				return new DataCablePart();
			case INFO_READER:
				return new InfoReaderPart();
			case INV_READER:
				return new InventoryReaderPart();
			case INFO_CREATOR:
				return new InfoCreatorPart();
			case DATA_MODIFIER:
				return new DataModifierPart();
			case FLUID_READER:
				return new FluidReaderPart();
			case CHANNELLED_CABLE:
				return new ChannelledCablePart();
			case ENERGY_READER:
				return new EnergyReaderPart();
			default:
				break;
			}
		}
		return null;
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
		if (b == BlockRegistry.channelledCable) {
			return new ChannelledCablePart(meta);
		}
		if (b == BlockRegistry.energyReader) {
			return new EnergyReaderPart(meta);
		}
		return null;
	}
}
