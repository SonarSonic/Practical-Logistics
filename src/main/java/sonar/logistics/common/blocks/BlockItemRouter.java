package sonar.logistics.common.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import sonar.core.common.block.SonarMaterials;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.utils.helpers.FontHelper;
import sonar.logistics.Logistics;
import sonar.logistics.common.tileentity.TileEntityEntityNode;
import sonar.logistics.common.tileentity.TileEntityItemRouter;
import sonar.logistics.common.tileentity.TileEntityNode;
import sonar.logistics.network.LogisticsGui;

public class BlockItemRouter extends BaseNode {

	public BlockItemRouter() {
		super(SonarMaterials.machine, false);
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
			// player.openGui(Logistics.instance, LogisticsGui.entityNode,
			// world, x, y, z);
		}
	}

	@Override
	public boolean operateBlock(World world, int x, int y, int z, EntityPlayer player, int side, float hitx, float hity, float hitz) {
		if (!world.isRemote && player != null && player.isSneaking()) {
			TileEntity target = world.getTileEntity(x, y, z);
			if (target instanceof TileEntityItemRouter) {
				TileEntityItemRouter router = (TileEntityItemRouter) target;
				if(router.handler.sideConfigs[side].getInt()<2){
					router.handler.sideConfigs[side].increaseBy(1);
				}else{
					router.handler.sideConfigs[side].setInt(0);
				}
			}
			return true;
		}

		return super.operateBlock(world, x, y, z, player, side, hitx, hity, hitz);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityItemRouter();
	}

	public boolean hasSpecialCollisionBox() {
		return false;
	}
}
