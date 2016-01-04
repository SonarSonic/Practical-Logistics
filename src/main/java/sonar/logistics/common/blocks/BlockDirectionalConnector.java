package sonar.logistics.common.blocks;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.UP;
import static net.minecraftforge.common.util.ForgeDirection.WEST;

import java.util.List;

import sonar.core.integration.fmp.FMPHelper;
import sonar.core.network.utils.ISyncTile;
import sonar.core.utils.helpers.SonarHelper;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.connecting.IDataCable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockDirectionalConnector extends BaseNode {

	protected BlockDirectionalConnector(Material material) {
		super(material);
		this.setBlockBounds((float) (4 * 0.0625), (float) (4 * 0.0625), (float) (4 * 0.0625), (float) (1 - 4 * 0.0625), (float) (1 - 4 * 0.0625), (float) (1 - 4 * 0.0625));
	}

	public boolean hasSpecialCollisionBox() {
		return true;
	}

	public boolean hasSpecialRenderer() {
		return true;
	}

	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int meta) {
		ForgeDirection dir = ForgeDirection.getOrientation(meta).getOpposite();
		return !world.isAirBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldblock, int oldMetadata) {
		super.breakBlock(world, x, y, z, oldblock, oldMetadata);
		ForgeDirection dir = ForgeDirection.getOrientation(oldMetadata);
		Object tile = world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
		tile = FMPHelper.checkObject(tile);
		if (tile != null) {
			if (tile instanceof IDataCable) {
				IDataCable cable = (IDataCable) tile;
				if (cable.getCoords() != null) {
					cable.setCoords(null);
				}
			}
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemstack) {
		int l = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 0x3;
		int k = MathHelper.floor_double(player.rotationPitch * 3.0F / 360.0F + 0.5D) & 3;

		if (k != 3 && k != 1) {
			if (l == 0) {
				world.setBlockMetadataWithNotify(x, y, z, 2, 2);
			}

			if (l == 1) {
				world.setBlockMetadataWithNotify(x, y, z, 5, 2);
			}

			if (l == 2) {
				world.setBlockMetadataWithNotify(x, y, z, 3, 2);
			}

			if (l == 3) {
				world.setBlockMetadataWithNotify(x, y, z, 4, 2);
			}
		} else {
			if(k==1){
				world.setBlockMetadataWithNotify(x, y, z, 1, 2);
			}else{
				world.setBlockMetadataWithNotify(x, y, z, 0, 2);
			}
		}

	}

	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		int l = world.getBlockMetadata(x, y, z) & 7;
		/* float f = 0.0625F; if (l == 1) { this.setBlockBounds(0F, f, f, 0.4F, 1 - f, 1 - f); } else if (l == 2) { this.setBlockBounds(0.6F, f, f, 1F, 1 - f, 1 - f); } else if (l == 3) { this.setBlockBounds(0.0F + f, 0.0F + f, 0F, 1F - f, 1F - f, 0.4F); } else if (l == 4) { this.setBlockBounds(0, f, 0.6F, 1 - f, 1 - f, 1); } else if (l != 5 && l != 6) { if (l == 0 || l == 7) { f = 0.25F;
		 * this.setBlockBounds(0.0F, 0.4F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f); } } else { f = 0.0625F; this.setBlockBounds(0.0F + f, 0.0F, 0.0F + f, 1F - f, 0.4F, 1F - f); } */
	}

	public List<AxisAlignedBB> getCollisionBoxes(World world, int x, int y, int z, List<AxisAlignedBB> list) {
		this.setBlockBounds((float) (4 * 0.0625), (float) (4 * 0.0625), (float) (4 * 0.0625), (float) (1 - 4 * 0.0625), (float) (1 - 4 * 0.0625), (float) (1 - 4 * 0.0625));
		list.add(AxisAlignedBB.getBoundingBox(4 * 0.0625, 4 * 0.0625, 4 * 0.0625, 1 - 4 * 0.0625, 1 - 4 * 0.0625, 1 - 4 * 0.0625));

		int meta = world.getBlockMetadata(x, y, z);
		if (meta == 2 || meta == 3) {
			list.add(AxisAlignedBB.getBoundingBox(6 * 0.0625, 6 * 0.0625, 0.0F, 1.0F - 6 * 0.0625, 1.0F - 6 * 0.0625, 1.0F));
		}
		if (meta == 4 || meta == 5) {
			list.add(AxisAlignedBB.getBoundingBox(0.0F, 6 * 0.0625, 6 * 0.0625, 1.0F, 1.0F - 6 * 0.0625, 1.0F - 6 * 0.0625));
		}
		return list;
	}
}
