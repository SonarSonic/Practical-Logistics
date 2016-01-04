package sonar.logistics.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.block.SonarMaterials;
import sonar.logistics.Logistics;
import sonar.logistics.common.tileentity.TileEntityDataEmitter;
import sonar.logistics.network.LogisticsGui;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDataEmitter extends BaseNode {

	public BlockDataEmitter() {
		super(SonarMaterials.machine);
		this.setBlockBounds((float) (4 * 0.0625), (float) (4 * 0.0625), (float) (4 * 0.0625), (float) (1 - 4 * 0.0625), (float) (1 - 4 * 0.0625), (float) (1 - 4 * 0.0625));

	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityDataEmitter();
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public void openGui(World world, int x, int y, int z, EntityPlayer player) {
		if (player != null && !world.isRemote)
			player.openGui(Logistics.instance, LogisticsGui.dataEmitter, world, x, y, z);
	}

	public boolean hasSpecialRenderer() {
		return true;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldblock, int oldMetadata) {
		TileEntity target = world.getTileEntity(x, y, z);
		if (target != null && target instanceof TileEntityDataEmitter) {
			TileEntityDataEmitter node = (TileEntityDataEmitter) target;
			node.removeFromFrequency();
		}
		super.breakBlock(world, x, y, z, oldblock, oldMetadata);
	}

	public boolean hasSpecialCollisionBox() {
		return true;
	}

	public List<AxisAlignedBB> getCollisionBoxes(World world, int x, int y, int z, List<AxisAlignedBB> list) {
		this.setBlockBounds((float) (4 * 0.0625), (float) (4 * 0.0625), (float) (4 * 0.0625), (float) (1 - 4 * 0.0625), (float) (1 - 4 * 0.0625), (float) (1 - 4 * 0.0625));
		list.add(AxisAlignedBB.getBoundingBox(4 * 0.0625, 4 * 0.0625, 4 * 0.0625, 1 - 4 * 0.0625, 1 - 4 * 0.0625, 1 - 4 * 0.0625));

		return list;
	}
}
