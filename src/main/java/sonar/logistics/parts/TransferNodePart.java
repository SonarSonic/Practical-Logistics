package sonar.logistics.parts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mcmultipart.MCMultiPartMod;
import mcmultipart.multipart.ISlottedPart;
import mcmultipart.raytrace.RayTraceUtils.AdvancedRayTraceResultPart;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.api.SonarAPI;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.FontHelper;
import sonar.core.network.sync.SyncEnum;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.IOperatorTile;
import sonar.logistics.api.connecting.OperatorMode;
import sonar.logistics.api.connecting.TransferMode;

public class TransferNodePart extends SidedMultipart implements IConnectionNode, IOperatorTile {

	public static final PropertyEnum<TransferMode> TRANSFER = PropertyEnum.<TransferMode>create("transfer", TransferMode.class);
	public SyncEnum<TransferMode> transferMode = new SyncEnum(TransferMode.values(), 1).setDefault(TransferMode.PULL);
	public int ticks = 20;
	{
		syncParts.add(transferMode);
	}

	public TransferNodePart() {
		super(0.0625 * 8, 0, 0.0625 * 2);
	}

	public TransferNodePart(EnumFacing face) {
		super(face, 0.0625 * 8, 0, 0.0625 * 2);
	}

	public void update() {
		super.update();
		if (this.isClient()) {
			return;
		}
		if (!transferMode.getObject().isPassive()) {
			if (ticks >= 20) {
				ticks = 0;
				TileEntity localTile = new BlockCoords(getPos().offset(face), getWorld().provider.getDimension()).getTileEntity(getWorld());
				if (localTile != null) {
					HashMap<BlockCoords, EnumFacing> tiles = network.getExternalBlocks(true);
					for (Entry<BlockCoords, EnumFacing> entry : tiles.entrySet()) {
						TileEntity netTile = entry.getKey().getTileEntity();
						if (netTile != null) {
							EnumFacing dirFrom = transferMode.getObject().shouldPull() ? face : entry.getValue();
							EnumFacing dirTo = !transferMode.getObject().shouldPull() ? face : entry.getValue();
							TileEntity from = transferMode.getObject().shouldPull() ? localTile : netTile;
							TileEntity to = !transferMode.getObject().shouldPull() ? localTile : netTile;
							SonarAPI.getItemHelper().transferItems(from, to, dirFrom.getOpposite(), dirTo.getOpposite(), null);
						}
					}
				}
			} else {
				ticks++;
			}
		}
	}

	@Override
	public void addConnections(Map<BlockCoords, EnumFacing> connections) {
		// if (should add the connection) {
		// BlockCoords tileCoords = new BlockCoords(getPos().offset(face), getWorld().provider.getDimension());
		// connections.put(tileCoords, face);
		// }
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(LogisticsItems.partTransferNode);
	}

	@Override
	public IBlockState getActualState(IBlockState state) {
		World w = getContainer().getWorldIn();
		BlockPos pos = getContainer().getPosIn();
		return state.withProperty(ORIENTATION, face).withProperty(TRANSFER, transferMode.getObject());
	}

	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(MCMultiPartMod.multipart, new IProperty[] { ORIENTATION, TRANSFER });
	}

	@Override
	public boolean performOperation(AdvancedRayTraceResultPart rayTrace, OperatorMode mode, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!getWorld().isRemote) {
			transferMode.incrementEnum();
			sendSyncPacket();
			sendUpdatePacket(true);
			FontHelper.sendMessage("Transfer Mode: " + transferMode.getObject(), getWorld(), player);
		}
		return true;
	}

	public void addInfo(List<String> info) {
		super.addInfo(info);
		info.add("Transfer Mode: " + transferMode.getObject());
	}
}
