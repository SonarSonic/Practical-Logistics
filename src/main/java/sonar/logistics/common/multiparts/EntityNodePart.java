package sonar.logistics.common.multiparts;

import java.util.ArrayList;
import java.util.List;

import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.connecting.IEntityNode;

public class EntityNodePart extends SidedMultipart implements IEntityNode {

	public EntityNodePart() {
		super(5*0.0625, 0.0625*1, 0.0625*4);
	}

	public EntityNodePart(EnumFacing face) {
		super(face, 5*0.0625, 0.0625*1, 0.0625*4);
	}

    @Override
    public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, PartMOP hit) {
        return false;
    }

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(LogisticsItems.partEntityNode);
	}

	@Override
	public void addEntities(List<Entity> entities) {
		
	}
}
