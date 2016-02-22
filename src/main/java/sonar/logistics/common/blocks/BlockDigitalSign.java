package sonar.logistics.common.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import sonar.logistics.registries.ItemRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDigitalSign extends BlockContainer {
	private Class tileEntity;
	private boolean isStanding;

	public BlockDigitalSign(Class tileEntity, boolean isStanding) {
		super(Material.wood);
		this.isStanding = isStanding;
		this.tileEntity = tileEntity;
		float f = 0.25F;
		float f1 = 1.0F;
		this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return Blocks.stone.getBlockTextureFromSide(side);
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		if (!this.isStanding) {
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
	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		this.setBlockBoundsBasedOnState(world, x, y, z);
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	public int getRenderType() {
		return -1;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public boolean getBlocksMovement(IBlockAccess p_149655_1_, int p_149655_2_, int p_149655_3_, int p_149655_4_) {
		return true;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public TileEntity createNewTileEntity(World world, int meta) {
		try {
			return (TileEntity) this.tileEntity.newInstance();
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public Item getItemDropped(int meta, Random rand, int fortune) {
		return ItemRegistry.digitalSign;
	}

	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		boolean flag = false;

		if (this.isStanding) {
			if (!world.getBlock(x, y - 1, z).getMaterial().isSolid()) {
				flag = true;
			}
		} else {
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
		}

		if (flag) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlockToAir(x, y, z);
		}

		super.onNeighborBlockChange(world, x, y, z, block);
	}

	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z) {
		return ItemRegistry.digitalSign;
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister icon) {
	}
}