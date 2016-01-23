package sonar.logistics.info.filters.items;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.oredict.OreDictionary;
import sonar.logistics.api.ItemFilter;

public class ItemStackFilter extends ItemFilter<ItemStackFilter> {

	public ItemStack filter = null;
	public boolean matchNBT, ignoreDamage, matchOreDict, blacklisted;

	@Override
	public String getName() {
		return "ItemStack Filter";
	}

	@Override
	public boolean matchesFilter(ItemStack stack) {
		if (stack != null && filter != null) {
			int[] stackIDs = OreDictionary.getOreIDs(stack);
			int[] filterIDs = OreDictionary.getOreIDs(filter);
			for (int sID : stackIDs) {
				for (int fID : filterIDs) {
					if (sID == fID) {
						return true;
					}
				}
			}
			if (stack.getItem() == filter.getItem()) {
				if (ignoreDamage || stack.getItemDamage() == filter.getItemDamage()) {
					if (!matchNBT || ItemStack.areItemStackTagsEqual(stack, filter)) {
						return blacklisted ? false : true;

					}
				}
			}
		}
		return blacklisted ? true : false;
	}

	public boolean equalFilter(ItemFilter itemFilter) {
		if (itemFilter != null && itemFilter instanceof ItemStackFilter) {
			ItemStackFilter sFilter = (ItemStackFilter) itemFilter;
			if (filter != null && sFilter.matchNBT == matchNBT && sFilter.ignoreDamage == ignoreDamage && sFilter.matchOreDict == matchOreDict && sFilter.blacklisted == blacklisted && ItemStack.areItemStacksEqual(filter, sFilter.filter)) {
				return true;
			}
		}
		return false;

	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setBoolean("matchNBT", matchNBT);
		tag.setBoolean("dam", ignoreDamage);
		tag.setBoolean("oreDict", matchOreDict);
		tag.setBoolean("blacklist", blacklisted);
		NBTTagCompound stackTag = new NBTTagCompound();
		if (filter != null) {
			filter.writeToNBT(stackTag);
			tag.setTag("stackTag", stackTag);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		matchNBT = tag.getBoolean("matchNBT");
		ignoreDamage = tag.getBoolean("dam");
		matchOreDict = tag.getBoolean("oreDict");
		blacklisted = tag.getBoolean("blacklist");
		if (tag.hasKey("stackTag"))
			filter = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("stackTag"));
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		matchNBT = buf.readBoolean();
		ignoreDamage = buf.readBoolean();
		matchOreDict = buf.readBoolean();
		blacklisted = buf.readBoolean();
		if (buf.readBoolean()) {
			filter = ByteBufUtils.readItemStack(buf);
		}
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		buf.writeBoolean(matchNBT);
		buf.writeBoolean(ignoreDamage);
		buf.writeBoolean(blacklisted);
		buf.writeBoolean(matchNBT);
		if (filter != null) {
			buf.writeBoolean(true);
			ByteBufUtils.writeItemStack(buf, filter);
		} else {
			buf.writeBoolean(false);
		}
	}

	@Override
	public ItemStackFilter instance() {
		return new ItemStackFilter();
	}

}
