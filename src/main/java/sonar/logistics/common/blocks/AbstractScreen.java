package sonar.logistics.common.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.common.block.SonarMachineBlock;
import sonar.core.common.block.SonarMaterials;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.PacketBlockInteraction;
import sonar.core.utils.BlockInteraction;
import sonar.core.utils.BlockInteractionType;
import sonar.logistics.common.handlers.DisplayScreenHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** basically a fabrication of the BlockSign code */
public abstract class AbstractScreen extends SonarMachineBlock {

	public AbstractScreen() {
		super(SonarMaterials.machine);
		this.disableOrientation();
		float height = height();
		float width = width();
		this.setBlockBounds(0.5F - height, 0.0F, 0.5F - height, 0.5F + height, width, 0.5F + height);
	}

	public abstract float height();

	public abstract float width();

	public abstract TileEntity createNewTileEntity(World world, int i);
	
	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
		if (world.isRemote && allowLeftClick()) {
			MovingObjectPosition pos = Minecraft.getMinecraft().objectMouseOver;
			float hitX = (float) (pos.hitVec.xCoord - pos.blockX);
			float hitY = (float) (pos.hitVec.yCoord - pos.blockY);
			float hitZ = (float) (pos.hitVec.zCoord - pos.blockZ);
			operateBlock(world, x,y,z,player,new BlockInteraction(pos.sideHit, hitX, hitY, hitZ, player.isSneaking() ? BlockInteractionType.SHIFT_LEFT : BlockInteractionType.LEFT));
			//SonarCore.network.sendToServer(new PacketBlockInteraction(x, y, z, new BlockInteraction(pos.sideHit, hitX, hitY, hitZ, player.isSneaking() ? BlockInteractionType.SHIFT_LEFT : BlockInteractionType.LEFT)));
		}
	}

	@Override
	public final boolean operateBlock(World world, int x, int y, int z, EntityPlayer player, BlockInteraction interact) {
		if (player != null) {
			TileHandler target = FMPHelper.getHandler(world.getTileEntity(x, y, z));
			if (target != null && target instanceof DisplayScreenHandler) {
				DisplayScreenHandler handler = (DisplayScreenHandler) target;
				if (world.isRemote) {
					handler.screenClicked(world, player, x, y, z, interact);
				} else {
					world.playSound(x, y, z, "dig.stone", 1.0F, 1.0F, true);
				}
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean allowLeftClick() {
		return true;
	}

	public boolean isClickableSide(World world, int x, int y, int z, int side) {
		return side == world.getBlockMetadata(x, y, z);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return this.blockIcon;
	}

	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {

		int l = world.getBlockMetadata(x, y, z);
		float f = 0.27125F;
		float f1 = 1 - height();
		float f2 = 0.0F;
		float f3 = width();
		float f4 = 0.080F;
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

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		this.setBlockBoundsBasedOnState(world, x, y, z);
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	public boolean getBlocksMovement(IBlockAccess world, int x, int y, int z) {
		return super.getBlocksMovement(world, x, y, z);
	}

	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z) {
		return getItemDropped(0, null, 0);
	}

	public boolean hasSpecialRenderer() {
		return true;
	}

	@Override
	public boolean dropStandard(World world, int x, int y, int z) {
		return true;
	}

	public List<AxisAlignedBB> getCollisionBoxes(World world, int x, int y, int z, List<AxisAlignedBB> list) {
		this.setBlockBoundsBasedOnState(world, x, y, z);
		list.add(this.getCollisionBoundingBoxFromPool(world, x, y, z));

		return list;
	}

	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axis, List list, Entity entity) {
		if (hasSpecialCollisionBox()) {
			List<AxisAlignedBB> collisionList = this.getCollisionBoxes(world, x, y, z, new ArrayList());
			for (AxisAlignedBB collision : collisionList) {
				collision.offset(x, y, z);
				if (collision != null && collision.intersectsWith(axis)) {
					list.add(collision);
				}
			}
		} else {
			super.addCollisionBoxesToList(world, x, y, z, axis, list, entity);
		}
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return AxisAlignedBB.getBoundingBox((double) x + this.minX, (double) y + this.minY, (double) z + this.minZ, (double) x + this.maxX, (double) y + this.maxY, (double) z + this.maxZ);
	}

	public boolean hasSpecialCollisionBox() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
		effectRenderer.addBlockHitEffects(x, y, z, meta);
		effectRenderer.addBlockHitEffects(x, y, z, ForgeDirection.OPPOSITES[meta]);
		return true;
	}
}
