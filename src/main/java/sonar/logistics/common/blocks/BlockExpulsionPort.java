package sonar.logistics.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.block.SonarMaterials;
import sonar.core.utils.helpers.SonarHelper;
import sonar.logistics.common.tileentity.TileEntityBlockNode;
import sonar.logistics.common.tileentity.TileEntityExpulsionPort;

public class BlockExpulsionPort extends BlockNode {

		@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityExpulsionPort();
	}

}
