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
import sonar.logistics.common.tileentity.TileEntityDataCable;
import sonar.logistics.common.tileentity.TileEntityDataModifier;
import sonar.logistics.network.LogisticsGui;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDataModifier extends BaseNode {

	public BlockDataModifier() {
		super(SonarMaterials.machine);
		this.setBlockBounds((float) 0.0625 * 4, (float) 0.0625 * 4, (float) 0.0625 * 4, (float) (1 - (0.0625 * 4)), (float) (1 - (0.0625 * 4)), (float) (1 - (0.0625 * 4)));
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldblock, int oldMetadata) {
		Object target =FMPHelper.getTile(world, x, y, z);		
		if(target instanceof ITileHandler){
			((ITileHandler)target).getTileHandler().removed(world, x, y, z, oldMetadata);
		}
		super.breakBlock(world, x, y, z, oldblock, oldMetadata);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityDataModifier();
	}

	@Override
	public boolean operateBlock(World world, int x, int y, int z, EntityPlayer player, int side, float hitx, float hity, float hitz) {
		return super.operateBlock(world, x, y, z, player, side, hitx, hity, hitz);
	}

	public boolean hasSpecialRenderer() {
		return true;
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public void openGui(World world, int x, int y, int z, EntityPlayer player) {
		player.openGui(Logistics.instance, LogisticsGui.dataModifier, world, x, y, z);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		super.setBlockBoundsBasedOnState(world, x, y, z);
		TileEntity tileentity = world.getTileEntity(x, y, z);
		if (tileentity != null && tileentity instanceof TileEntityDataCable) {
			TileEntityDataCable cable = (TileEntityDataCable) world.getTileEntity(x, y, z);
			this.setBlockBounds((float) (cable.canRenderConnection(ForgeDirection.WEST)!=0 ? 0 : 0.0625 * 6), (float) (cable.canRenderConnection(ForgeDirection.DOWN)!=0 ? 0 : 0.0625 * 6), (float) (cable.canRenderConnection(ForgeDirection.NORTH)!=0 ? 0 : 0.0625 * 6), (float) (cable.canRenderConnection(ForgeDirection.EAST)!=0 ? 1 : (1 - (0.0625 * 6))), (float) (cable.canRenderConnection(ForgeDirection.UP)!=0 ? 1
					: (1 - (0.0625 * 6))), (float) (cable.canRenderConnection(ForgeDirection.SOUTH)!=0 ? 1 : (1 - (0.0625 * 6))));
		} else {
			this.setBlockBounds((float) 0.0625 * 4, (float) 0.0625 * 4, (float) 0.0625 * 4, (float) (1 - (0.0625 * 4)), (float) (1 - (0.0625 * 4)), (float) (1 - (0.0625 * 4)));
		}
	}

	public boolean hasSpecialCollisionBox() {
		return true;
	}

	public List<AxisAlignedBB> getCollisionBoxes(World world, int x, int y, int z, List<AxisAlignedBB> list) {
		this.setBlockBounds((float) (4 * 0.0625), (float) (4 * 0.0625), (float) (4 * 0.0625), (float) (1 - 4 * 0.0625), (float) (1 - 4 * 0.0625), (float) (1 - 4 * 0.0625));
		list.add(AxisAlignedBB.getBoundingBox(4 * 0.0625, 4 * 0.0625, 4 * 0.0625, 1 - 4 * 0.0625, 1 - 4 * 0.0625, 1 - 4 * 0.0625));
		list.add(AxisAlignedBB.getBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 0.1F, 1.0F));
		return list;
	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		return AxisAlignedBB.getBoundingBox(4 * 0.0625, 4 * 0.0625, 4 * 0.0625, 1 - 4 * 0.0625, 1 - 4 * 0.0625, 1 - 4 * 0.0625);
	}

}
