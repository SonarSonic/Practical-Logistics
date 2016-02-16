package sonar.logistics.common.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.SonarCore;
import sonar.core.common.block.SonarMaterials;
import sonar.core.network.PacketTileSync;
import sonar.core.utils.BlockInteraction;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.Logistics;
import sonar.logistics.common.tileentity.TileEntityItemRouter;
import sonar.logistics.network.LogisticsGui;

public class BlockItemRouter extends BaseNode {

	public BlockItemRouter() {
		super(SonarMaterials.machine);
		this.disableOrientation();
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
		if (player != null && !world.isRemote) {
			NBTTagCompound packetTag = new NBTTagCompound();
			((TileEntityItemRouter) world.getTileEntity(x, y, z)).writeData(packetTag, SyncType.PACKET);
			SonarCore.network.sendTo(new PacketTileSync(x, y, z, packetTag, SyncType.PACKET), (EntityPlayerMP) player);
			player.openGui(Logistics.instance, LogisticsGui.itemRouter, world, x, y, z);
		}
	}

	@Override
	public boolean operateBlock(World world, int x, int y, int z, EntityPlayer player, int side, float hitx, float hity, float hitz, BlockInteraction interact) {
		if (!world.isRemote && player != null) {
			TileEntity target = world.getTileEntity(x, y, z);
			if (target instanceof TileEntityItemRouter) {
				TileEntityItemRouter router = (TileEntityItemRouter) target;
				if (interact == BlockInteraction.SHIFT_RIGHT) {
					if (router.handler.sideConfigs[side].getInt() < 2) {
						router.handler.sideConfigs[side].increaseBy(1);
					} else {
						router.handler.sideConfigs[side].setInt(0);
					}

					SonarCore.sendFullSyncAround(router, 64);
					return true;
				}
			}
		}

		return super.operateBlock(world, x, y, z, player, side, hitx, hity, hitz, interact);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityItemRouter();
	}

	public boolean hasSpecialCollisionBox() {
		return false;
	}
}
