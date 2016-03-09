package sonar.logistics.info.providers.tile;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.providers.TileProvider;
import sonar.logistics.info.types.ManaInfo;
import vazkii.botania.api.mana.IManaBlock;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.common.block.tile.mana.TilePool;
import cpw.mods.fml.common.Loader;

public class ManaProvider extends TileProvider {

	public static String name = "Mana-Provider";
	public static String[] categories = new String[] { "Mana" };
	public static String[] subcategories = new String[] { "Current Mana", "Is Full", "Max Mana", "Is Outputting" };

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity te = world.getTileEntity(x, y, z);
		return te != null && (te instanceof IManaBlock || te instanceof IManaBlock);
	}

	@Override
	public void getTileInfo(List<ILogicInfo> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		int id = this.getID();
		TileEntity te = world.getTileEntity(x, y, z);
		if (te == null) {
			return;
		}
		if(te instanceof TilePool){
			TilePool pool = (TilePool) te;
			infoList.add(new ManaInfo(id, pool.getCurrentMana(), pool.manaCap));
		}
		
		if (te instanceof IManaReceiver) {
			IManaReceiver manaReceiver = (IManaReceiver) te;
			infoList.add(new LogicInfo(id, 0, 1, manaReceiver.isFull()));
		}
		if (te instanceof IManaPool) {
			IManaPool manaPool = (IManaPool) te;
			infoList.add(new LogicInfo(id, 0, 3, manaPool.isOutputtingPower()));
			
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

	public boolean isLoadable() {
		return Loader.isModLoaded("Botania");
	}
}
