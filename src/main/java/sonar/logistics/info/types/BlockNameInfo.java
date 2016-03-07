package sonar.logistics.info.types;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import sonar.logistics.api.info.StandardInfo;
import cpw.mods.fml.common.network.ByteBufUtils;

public class BlockNameInfo extends StandardInfo<BlockNameInfo> {

	public ItemStack block = null;

	public BlockNameInfo() {
	}

	public BlockNameInfo(int providerID, int category, int subCategory, Object data, ItemStack block) {
		super(providerID, category, subCategory, data);
		this.block = block;
		this.dataType=1;
	}

	public BlockNameInfo(int providerID, String category, String subCategory, Object data, ItemStack block) {
		super(providerID, category, subCategory, data);
		this.block = block;
		this.dataType=1;
	}

	@Override
	public String getName() {
		return "Block Name";
	}

	@Override
	public String getData() {
		return this.getName();
	}

	@Override
	public String getDisplayableData() {
		return block.getDisplayName() + suffix;
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		super.writeToBuf(buf);
		ByteBufUtils.writeItemStack(buf, block);
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		super.readFromBuf(buf);
		block = ByteBufUtils.readItemStack(buf);
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagCompound blockTag = new NBTTagCompound();
		block.writeToNBT(blockTag);
		tag.setTag("block", blockTag);
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		block = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("block"));
	}

	@Override
	public void writeUpdate(BlockNameInfo currentInfo, NBTTagCompound tag) {
		super.writeUpdate(currentInfo, tag);
		if (!ItemStack.areItemStacksEqual(currentInfo.block, block) || !ItemStack.areItemStackTagsEqual(currentInfo.block, block)) {
			block = currentInfo.block;
			NBTTagCompound blockTag = new NBTTagCompound();
			block.writeToNBT(blockTag);
			tag.setTag("block", blockTag);
		}
	}

	@Override
	public void readUpdate(NBTTagCompound tag) {
		super.readUpdate(tag);
		if (tag.hasKey("block")) {
			block = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("block"));
		}
	}

	@Override
	public boolean matches(BlockNameInfo currentInfo) {
		return currentInfo.getProviderID() == this.providerID && currentInfo.dataType == dataType && currentInfo.category.equals(category) && currentInfo.subCategory.equals(subCategory) && currentInfo.suffix.equals(suffix) && currentInfo.catID == catID && currentInfo.subCatID == subCatID;
	}

	@Override
	public BlockNameInfo instance() {
		return new BlockNameInfo();
	}
}
