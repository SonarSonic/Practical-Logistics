package sonar.logistics.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.common.block.SonarMachineBlock;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.utils.BlockInteraction;
import sonar.core.utils.helpers.FontHelper;
import sonar.logistics.common.tileentity.TileEntityBlockNode;
import sonar.logistics.common.tileentity.TileEntityChannelledCable;
import sonar.logistics.common.tileentity.TileEntityNode;

public abstract class BaseNode extends SonarMachineBlock {

	protected BaseNode(Material material) {
		super(material);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemstack) {
		super.onBlockPlacedBy(world, x, y, z, entity, itemstack);
		TileEntity target = world.getTileEntity(x, y, z);
		if (entity instanceof EntityPlayer && target instanceof TileEntityNode) {
			EntityPlayer player = (EntityPlayer) entity;
			TileEntityNode node = (TileEntityNode) target;
			node.playerName = player.getGameProfile().getName();

		}
	}

	@Override
	public boolean dropStandard(World world, int x, int y, int z) {
		return true;
	}

	public boolean hasGui() {
		return false;
	}

	@Override
	public boolean operateBlock(World world, int x, int y, int z, EntityPlayer player, BlockInteraction interact) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(!world.isRemote && tile instanceof TileEntityChannelledCable){
			TileEntityChannelledCable cable = (TileEntityChannelledCable) tile;
			FontHelper.sendMessage("" + cable.registryID, world, player);
		}
		if (!hasGui()) {
			return false;
		}
		if (player != null) {
			TileEntity target = world.getTileEntity(x, y, z);
			if (target instanceof TileEntitySonar) {
				if (!world.isRemote) {
					TileEntitySonar sonar = (TileEntitySonar) target;
					if (sonar instanceof TileEntityNode) {
						TileEntityNode node = (TileEntityNode) target;
						if (!node.playerName.equals(player.getGameProfile().getName())) {
							FontHelper.sendMessage(FontHelper.translate("dataBlock.denied")+ ": " + node.playerName, world, player);
							return false;
						}
					}
					sonar.sendSyncPacket(player);
					openGui(world, x, y, z, player);
				}
				return true;
			}

		}
		return false;
	}

	public void openGui(World world, int x, int y, int z, EntityPlayer player) {
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityBlockNode();
	}

}
