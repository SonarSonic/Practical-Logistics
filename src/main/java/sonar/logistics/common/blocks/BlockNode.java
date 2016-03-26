package sonar.logistics.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.block.SonarMaterials;
import sonar.core.helpers.SonarHelper;

public class BlockNode extends BaseNode {

	public BlockNode(){
		super(SonarMaterials.machine);
		this.disableOrientation();
		float f = 0.0625F;
		this.setBlockBounds(0.0F+f, 0.0F, 0.0F+f, 1F-f, 0.4F, 1F-f);
	}
	
	public boolean hasSpecialRenderer() {
		return true;
	}
	
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int meta) {
		ForgeDirection dir = ForgeDirection.getOrientation(meta).getOpposite();
		return !world.isAirBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
	}

	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		super.canPlaceBlockAt(world, x, y, z);
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			if (!world.isAirBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ)) {
				return true;
			}
		}

		return false;
	}

	private boolean canStayAt(World world, int x, int y, int z) {
		if (!this.canPlaceBlockAt(world, x, y, z)) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlockToAir(x, y, z);
			return false;
		} else {
			return true;
		}
	}

	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
		int k1 = metadata & 8;
		byte meta = -1;
		ForgeDirection dir = ForgeDirection.getOrientation(side).getOpposite();
		if (!world.isAirBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ)) {
			meta = (byte) SonarHelper.invertMetadata(side);
		}
		return meta + k1;
	}

	public void onNeighborBlockChange(World world, int x, int y, int z, Block oldBlock) {
		if (this.canStayAt(world, x, y, z)) {
			int l = world.getBlockMetadata(x, y, z) & 7;
			boolean flag = false;

			if (world.isAirBlock(x - 1, y, z) && l == 1) {
				flag = true;
			}

			if (world.isAirBlock(x + 1, y, z) && l == 2) {
				flag = true;
			}

			if (world.isAirBlock(x, y, z - 1) && l == 3) {
				flag = true;
			}

			if (world.isAirBlock(x, y, z + 1) && l == 4) {
				flag = true;
			}

			if (world.isAirBlock(x, y - 1, z) && l == 5) {
				flag = true;
			}

			if (world.isAirBlock(x, y - 1, z) && l == 6) {
				flag = true;
			}

			if (world.isAirBlock(x, y + 1, z) && l == 0) {
				flag = true;
			}

			if (world.isAirBlock(x, y + 1, z) && l == 7) {
				flag = true;
			}

			if (flag) {
				this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
				world.setBlockToAir(x, y, z);
			}
		}
	}

	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		int l = world.getBlockMetadata(x, y, z) & 7;
		float f = 0.0625F;
		
		if (l == 1) {
			this.setBlockBounds(0F, f, f, 0.4F, 1-f, 1-f);
			
		} else if (l == 2) {
			this.setBlockBounds(0.6F, f, f, 1F, 1-f, 1-f);
		} else if (l == 3) {
			this.setBlockBounds(0.0F+f, 0.0F+f, 0F, 1F-f, 1F-f, 0.4F);
		} else if (l == 4) {
			this.setBlockBounds(0, f, 0.6F, 1-f, 1-f, 1);
		} else if (l != 5 && l != 6) {
			if (l == 0 || l == 7) {
				this.setBlockBounds(0.0F, 0.6F, f, 1-f, 1.0F, 1-f);
			}
		} else {
			f = 0.0625F;
			this.setBlockBounds(0.0F+f, 0.0F, 0.0F+f, 1F-f, 0.4F, 1F-f);
		}
	}
	public boolean hasSpecialCollisionBox() {
		return true;
	}

	public List<AxisAlignedBB> getCollisionBoxes(World world, int x, int y, int z, List<AxisAlignedBB> list) {
		this.setBlockBoundsBasedOnState(world, x, y, z);		
		list.add(AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ));
		
		return list;
	}
}
