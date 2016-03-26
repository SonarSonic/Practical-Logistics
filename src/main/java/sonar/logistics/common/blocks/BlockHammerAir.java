package sonar.logistics.common.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import sonar.core.helpers.SonarHelper;
import sonar.logistics.Logistics;
import sonar.logistics.network.LogisticsGui;
import sonar.logistics.registries.BlockRegistry;
import cofh.api.block.IDismantleable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockHammerAir extends Block implements IDismantleable {
	public BlockHammerAir() {
		super(Material.cloth);
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldblock, int oldMetadata) {
		world.setBlockToAir(x, y, z);
		if (world.getBlock(x, y - 1, z) == BlockRegistry.hammer) {
			TileEntity i = world.getTileEntity(x, y - 1, z);
			Block bi = world.getBlock(x, y - 1, z);
			bi.dropBlockAsItem(world, x, y - 1, z, world.getBlockMetadata(x, y - 1, z), 0);
			world.setBlockToAir(x, y - 1, z);
		} else if (world.getBlock(x, y - 2, z) == BlockRegistry.hammer) {
			TileEntity i = world.getTileEntity(x, y - 2, z);
			Block bi = world.getBlock(x, y - 2, z);
			bi.dropBlockAsItem(world, x, y - 2, z, world.getBlockMetadata(x, y - 2, z), 0);
			world.setBlockToAir(x, y - 2, z);
		}

	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitx, float hity, float hitz) {
		if (world.getBlock(x, y - 1, z) == BlockRegistry.hammer) {
			player.openGui(Logistics.instance, LogisticsGui.hammer, world, x, y - 1, z);
			return true;
		} else if (world.getBlock(x, y - 2, z) == BlockRegistry.hammer) {
			player.openGui(Logistics.instance, LogisticsGui.hammer, world, x, y - 2, z);
			return true;
		}
		return false;

	}

	@Override
	public int quantityDropped(Random p_149745_1_) {
		return 0;
	}

	@Override
	public Item getItem(World world, int x, int y, int z) {
		return null;
	}

	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		if (world.getBlock(x, y - 1, z) == BlockRegistry.hammer) {
			this.setBlockBounds(0.0F, -1.0F, 0.0F, 1.0F, 2.0F, 1.0F);
		} else if (world.getBlock(x, y - 2, z) == BlockRegistry.hammer) {
			this.setBlockBounds(0.0F, -2.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	@Override
	public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnDrops) {

		SonarHelper.dropTile(player, world.getBlock(x, y, z), world, x, y, z);
		return null;
	}

	@Override
	public boolean canDismantle(EntityPlayer player, World world, int x, int y, int z) {
		return true;
	}

	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
		return new ItemStack(BlockRegistry.hammer, 1, 0);
	}

	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		list.add(new ItemStack(BlockRegistry.hammer_air, 1, 0));
	}
}
