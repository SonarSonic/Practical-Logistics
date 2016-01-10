package sonar.logistics.common.blocks;

import java.util.Random;

import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.logistics.common.tileentity.TileEntityDisplayScreen;
import sonar.logistics.registries.ItemRegistry;

public class DisplayScreen extends AbstractScreen {

	@Override
	public float height(){
		return 1.0F/16 * 3.7F;
	}

	@Override
	public float width() {
		return 1.0F;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityDisplayScreen();
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int par) {
		return ItemRegistry.displayScreen;
	}

}
