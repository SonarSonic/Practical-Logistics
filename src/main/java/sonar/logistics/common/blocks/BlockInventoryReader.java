package sonar.logistics.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.block.SonarMaterials;
import sonar.core.integration.fmp.FMPHelper;
import sonar.logistics.Logistics;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.common.tileentity.TileEntityInfoReader;
import sonar.logistics.common.tileentity.TileEntityInventoryReader;
import sonar.logistics.network.LogisticsGui;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockInventoryReader extends BlockDirectionalConnector {

	public BlockInventoryReader() {
		super(SonarMaterials.machine);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityInventoryReader();
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public void openGui(World world, int x, int y, int z, EntityPlayer player) {
		TileEntity target = world.getTileEntity(x, y, z);
		if (!world.isRemote && target != null && target instanceof TileEntityInventoryReader) {
			TileEntityInventoryReader node = (TileEntityInventoryReader) target;
			node.sendAvailableData(player);
		}
		player.openGui(Logistics.instance, LogisticsGui.inventoryReader, world, x, y, z);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldblock, int oldMetadata) {
		world.removeTileEntity(x, y, z);
	}

}
