package sonar.logistics.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.block.SonarMaterials;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.ITileHandler;
import sonar.logistics.Logistics;
import sonar.logistics.common.tileentity.TileEntityChannelSelector;
import sonar.logistics.common.tileentity.TileEntityDataCable;
import sonar.logistics.common.tileentity.TileEntityInfoCreator;
import sonar.logistics.network.LogisticsGui;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockChannelSelector extends BlockFramedCable {

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityChannelSelector();
	}

	@Override
	public void openGui(World world, int x, int y, int z, EntityPlayer player) {
		player.openGui(Logistics.instance, LogisticsGui.channelSelector, world, x, y, z);
	}

}
