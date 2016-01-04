package sonar.logistics.common.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.block.SonarMaterials;
import sonar.logistics.Logistics;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.common.tileentity.TileEntityDataReceiver;
import sonar.logistics.network.LogisticsGui;
import sonar.logistics.network.packets.PacketDataEmitters;
import sonar.logistics.registries.EmitterRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDataReceiver extends BaseNode {

	public BlockDataReceiver() {
		super(SonarMaterials.machine);
		this.setBlockBounds((float) (4 * 0.0625), (float) (4 * 0.0625), (float) (4 * 0.0625), (float) (1 - 4 * 0.0625), (float) (1 - 4 * 0.0625), (float) (1 - 4 * 0.0625));

	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityDataReceiver();
	}

	@Override
	public boolean hasGui() {
		return true;
	}

	@Override
	public void openGui(World world, int x, int y, int z, EntityPlayer player) {
		if (player != null && player instanceof EntityPlayerMP) {
			Logistics.network.sendTo(new PacketDataEmitters(x, y, z, EmitterRegistry.getEmitters(((EntityPlayerMP) player).getGameProfile().getName())), (EntityPlayerMP) player);

		}
		player.openGui(Logistics.instance, LogisticsGui.dataReceiver, world, x, y, z);
	}

	public boolean hasSpecialRenderer() {
		return true;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block oldblock, int oldMetadata) {
		super.breakBlock(world, x, y, z, oldblock, oldMetadata);
		ForgeDirection dir = ForgeDirection.getOrientation(oldMetadata);
		TileEntity tile = world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
		if (tile != null) {
			if (tile instanceof IDataCable) {
				IDataCable cable = (IDataCable) tile;
				if (cable.getCoords() != null) {
					cable.setCoords(null);
				}
			}
		}
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
