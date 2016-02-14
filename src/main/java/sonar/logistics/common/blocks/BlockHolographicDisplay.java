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
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.logistics.common.handlers.DisplayScreenHandler;
import sonar.logistics.common.tileentity.TileEntityHolographicDisplay;

public class BlockHolographicDisplay extends BaseNode {

	public BlockHolographicDisplay() {
		super(SonarMaterials.machine);
		this.setBlockBounds((float) (4 * 0.0625), (float) (4 * 0.0625), (float) (4 * 0.0625), (float) (1 - 4 * 0.0625), (float) (1 - 4 * 0.0625), (float) (1 - 4 * 0.0625));

	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityHolographicDisplay();
	}

	@Override
	public boolean operateBlock(World world, int x, int y, int z, EntityPlayer player, int side, float hitx, float hity, float hitz) {
		if (player != null) {
			TileHandler target = FMPHelper.getHandler(world.getTileEntity(x, y, z));
			if (target != null && target instanceof DisplayScreenHandler) {
				DisplayScreenHandler handler = (DisplayScreenHandler) target;
				if (!world.isRemote)
					handler.screenClicked(world, player, x, y, z, ForgeDirection.getOrientation(side), hitx, hity, hitz);
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	public boolean hasSpecialRenderer() {
		return true;
	}

	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		boolean flag = false;

		int l = world.getBlockMetadata(x, y, z);
		flag = true;

		if (l == 2 && world.getBlock(x, y, z + 1).getMaterial().isSolid()) {
			flag = false;
		}

		if (l == 3 && world.getBlock(x, y, z - 1).getMaterial().isSolid()) {
			flag = false;
		}

		if (l == 4 && world.getBlock(x + 1, y, z).getMaterial().isSolid()) {
			flag = false;
		}

		if (l == 5 && world.getBlock(x - 1, y, z).getMaterial().isSolid()) {
			flag = false;
		}

		if (flag) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlockToAir(x, y, z);
		}

		super.onNeighborBlockChange(world, x, y, z, block);
	}

	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {

		int l = world.getBlockMetadata(x, y, z);
		float f = 0.28125F;
		float f1 = 0.78125F;
		float f2 = 0.0F;
		float f3 = 1.0F;
		float f4 = 0.125F;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);

		if (l == 2) {
			this.setBlockBounds(f2, f, 1.0F - f4, f3, f1, 1.0F);
		}

		if (l == 3) {
			this.setBlockBounds(f2, f, 0.0F, f3, f1, f4);
		}

		if (l == 4) {
			this.setBlockBounds(1.0F - f4, f, f2, 1.0F, f1, f3);
		}

		if (l == 5) {
			this.setBlockBounds(0.0F, f, f2, f4, f1, f3);
		}

	}

	public boolean hasSpecialCollisionBox() {
		return true;
	}

	public List<AxisAlignedBB> getCollisionBoxes(World world, int x, int y, int z, List<AxisAlignedBB> list) {
		this.setBlockBoundsBasedOnState(world, x, y, z);
		list.add(getCollisionBoundingBoxFromPool(world, x, y, z));
		return list;
	}
}
