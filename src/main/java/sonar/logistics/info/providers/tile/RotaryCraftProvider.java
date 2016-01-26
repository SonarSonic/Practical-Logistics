package sonar.logistics.info.providers.tile;

import java.util.List;

import Reika.RotaryCraft.API.Interfaces.BasicMachine;
import Reika.RotaryCraft.API.Interfaces.TemperatureTile;
import Reika.RotaryCraft.API.Interfaces.TensionStorage;
import Reika.RotaryCraft.API.Power.PowerAcceptor;
import Reika.RotaryCraft.API.Power.PowerGenerator;
import Reika.RotaryCraft.API.Power.PowerTracker;
import Reika.RotaryCraft.API.Power.ShaftMachine;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.providers.TileProvider;
import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyStorage;
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
		byte id = this.getID();

		TileEntity handler = world.getTileEntity(x, y, z);
		if (handler instanceof TemperatureTile) {
			TemperatureTile tile = (TemperatureTile) handler;
			infoList.add(new StandardInfo(id, 0, 0, tile.getTemperature(), "degrees"));
			infoList.add(new StandardInfo(id, 0, 0, tile.getMaxTemperature(), "degrees"));
		}
		if (handler instanceof ShaftMachine) {
			ShaftMachine tile = (ShaftMachine) handler;
			infoList.add(new StandardInfo(id, 0, 0, tile.getOmega(), "rad/s"));
			infoList.add(new StandardInfo(id, 0, 0, tile.getTorque(), "Nm"));
			infoList.add(new StandardInfo(id, 0, 0, tile.getPower(), "watts"));
		}
		if (handler instanceof PowerAcceptor) {
			PowerAcceptor tile = (PowerAcceptor) handler;
			infoList.add(new StandardInfo(id, 0, 0, tile.isReceiving()));
		}
		if (handler instanceof PowerGenerator) {
			PowerGenerator tile = (PowerGenerator) handler;
			infoList.add(new StandardInfo(id, 0, 0, tile.getCurrentPower(), "watts"));
		}
		if (handler instanceof TensionStorage) {
			infoList.add(new StandardInfo(id, 0, 0, handler.getBlockMetadata(), "watts"));
		}
	}

	@Override
	public String getCategory(byte id) {
		return categories[id];
	}

	@Override
	public String getSubCategory(byte id) {
		return subcategories[id];
	}

	@Override
	public boolean isLoadable() {
		return Loader.isModLoaded("RotaryCraft");
	}
}
