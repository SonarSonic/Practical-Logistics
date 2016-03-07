package sonar.logistics.info.providers.tile;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.info.Info;
import sonar.logistics.api.info.StandardInfo;
import sonar.logistics.api.providers.TileProvider;
import Reika.RotaryCraft.API.Interfaces.TemperatureTile;
import Reika.RotaryCraft.API.Interfaces.TensionStorage;
import Reika.RotaryCraft.API.Power.PowerAcceptor;
import Reika.RotaryCraft.API.Power.PowerGenerator;
import Reika.RotaryCraft.API.Power.PowerTracker;
import Reika.RotaryCraft.API.Power.ShaftMachine;
import cpw.mods.fml.common.Loader;

public class RotaryCraftProvider extends TileProvider {

	public static String name = "Rotary-Craft Provider";
	public String[] categories = new String[] { "ENERGY RF" };
	public String[] subcategories = new String[] { "Connects: ", "Current", "Max Energy", "Stored", "Max Stored" };

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity target = world.getTileEntity(x, y, z);
		return target != null && (target instanceof TemperatureTile || target instanceof ShaftMachine || target instanceof PowerAcceptor || target instanceof PowerGenerator || target instanceof PowerTracker || target instanceof TensionStorage);
	}

	@Override
	public void getHelperInfo(List<Info> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		int id = this.getID();

		TileEntity handler = world.getTileEntity(x, y, z);
		if (handler instanceof TemperatureTile) {
			TemperatureTile tile = (TemperatureTile) handler;
			infoList.add(new StandardInfo(id, 0, 0, tile.getTemperature()).addSuffix("degrees"));
			infoList.add(new StandardInfo(id, 0, 0, tile.getMaxTemperature()).addSuffix("degrees"));
		}
		if (handler instanceof ShaftMachine) {
			ShaftMachine tile = (ShaftMachine) handler;
			infoList.add(new StandardInfo(id, 0, 0, tile.getOmega()).addSuffix("rad/s"));
			infoList.add(new StandardInfo(id, 0, 0, tile.getTorque()).addSuffix("Nm"));
			infoList.add(new StandardInfo(id, 0, 0, tile.getPower()).addSuffix("watts"));
		}
		if (handler instanceof PowerAcceptor) {
			PowerAcceptor tile = (PowerAcceptor) handler;
			infoList.add(new StandardInfo(id, 0, 0, tile.isReceiving()));
		}
		if (handler instanceof PowerGenerator) {
			PowerGenerator tile = (PowerGenerator) handler;
			infoList.add(new StandardInfo(id, 0, 0, tile.getCurrentPower()).addSuffix("watts"));
		}
		if (handler instanceof TensionStorage) {
			infoList.add(new StandardInfo(id, 0, 0, handler.getBlockMetadata()).addSuffix("watts"));
		}
	}

	@Override
	public String getCategory(int id) {
		return categories[id];
	}

	@Override
	public String getSubCategory(int id) {
		return subcategories[id];
	}

	@Override
	public boolean isLoadable() {
		return Loader.isModLoaded("RotaryCraft");
	}
}
