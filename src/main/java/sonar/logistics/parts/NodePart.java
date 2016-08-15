package sonar.logistics.parts;

import java.util.Map;

import mcmultipart.multipart.ISlottedPart;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.FontHelper;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.connecting.IConnectionNode;

public class NodePart extends SidedMultipart implements IConnectionNode, ISlottedPart{

	public NodePart() {
		super(0.875, 0, 0.0625);
	}

	public NodePart(EnumFacing face) {
		super(face, 0.875, 0, 0.0625);
	}

	@Override
	public void addConnections(Map<BlockCoords, EnumFacing> connections) {
		BlockCoords tileCoords = new BlockCoords(getPos().offset(face), getWorld().provider.getDimension());
		connections.put(tileCoords, face);
	}

	@Override
	public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, PartMOP hit) {
		FontHelper.sendMessage(getWorld().isRemote + " " + face, this.getWorld(), player);
		return false;
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(LogisticsItems.partNode);
	}
}
