package sonar.logistics.common.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import sonar.core.common.block.SonarMaterials;
import sonar.logistics.Logistics;
import sonar.logistics.common.tileentity.TileEntityRedstoneSignaller;
import sonar.logistics.network.LogisticsGui;
import sonar.logistics.registries.BlockRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRedstoneSignaller extends BaseNode {

	private boolean on;

	public BlockRedstoneSignaller(boolean on) {
		super(SonarMaterials.machine);
		this.setBlockBounds((float) (4 * 0.0625), (float) (4 * 0.0625), (float) (4 * 0.0625), (float) (1 - 4 * 0.0625), (float) (1 - 4 * 0.0625), (float) (1 - 4 * 0.0625));
		this.on = on;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityRedstoneSignaller();
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemstack) {
		super.onBlockPlacedBy(world, x, y, z, entity, itemstack);

	}

	@Override
	public void openGui(World world, int x, int y, int z, EntityPlayer player) {
		if (player != null && !world.isRemote)
			player.openGui(Logistics.instance, LogisticsGui.redstoneSignaller, world, x, y, z);
	}

	public boolean hasSpecialRenderer() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if (this.on) {
			int l = world.getBlockMetadata(x, y, z);
			double d0 = (double) ((float) x + 0.5F) + (double) (rand.nextFloat() - 0.5F) * 0.2D;
			double d1 = (double) ((float) y + 0.7F) + (double) (rand.nextFloat() - 0.5F) * 0.2D;
			double d2 = (double) ((float) z + 0.5F) + (double) (rand.nextFloat() - 0.5F) * 0.2D;
			double d3 = 0.2199999988079071D;
			double d4 = 0.27000001072883606D;

			if (l == 1) {
				world.spawnParticle("reddust", d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
			} else if (l == 2) {
				world.spawnParticle("reddust", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
			} else if (l == 3) {
				world.spawnParticle("reddust", d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
			} else if (l == 4) {
				world.spawnParticle("reddust", d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
			} else {
				world.spawnParticle("reddust", d0, d1, d2, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	public static void updateSignallerState(boolean on, World world, int x, int y, int z) {
		int l = world.getBlockMetadata(x, y, z);
		TileEntity tileentity = world.getTileEntity(x, y, z);
		if (on) {
			world.setBlock(x, y, z, BlockRegistry.redstoneSignaller_on);
		} else {
			world.setBlock(x, y, z, BlockRegistry.redstoneSignaller_off);
		}

		world.setBlockMetadataWithNotify(x, y, z, l, 2);

		if (tileentity != null) {
			tileentity.validate();
			world.setTileEntity(x, y, z, tileentity);
		}

	}

	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
		return on ? 15 : 0;
	}

	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
		return on ? 15 : 0;
	}

	public boolean hasSpecialCollisionBox() {
		return true;
	}

	public List<AxisAlignedBB> getCollisionBoxes(World world, int x, int y, int z, List<AxisAlignedBB> list) {
		this.setBlockBounds((float) (4 * 0.0625), (float) (4 * 0.0625), (float) (4 * 0.0625), (float) (1 - 4 * 0.0625), (float) (1 - 4 * 0.0625), (float) (1 - 4 * 0.0625));
		list.add(AxisAlignedBB.getBoundingBox(4 * 0.0625, 4 * 0.0625, 4 * 0.0625, 1 - 4 * 0.0625, 1 - 4 * 0.0625, 1 - 4 * 0.0625));

		return list;
	}

	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z) {
		return Item.getItemFromBlock(BlockRegistry.redstoneSignaller_off);
	}

	public Item getItemDropped(int meta, Random rand, int fortune) {
		return Item.getItemFromBlock(BlockRegistry.redstoneSignaller_off);
	}
}
