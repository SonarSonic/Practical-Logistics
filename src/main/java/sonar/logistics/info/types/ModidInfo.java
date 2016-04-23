package sonar.logistics.info.types;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.api.info.LogicInfo;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

public class ModidInfo extends LogicInfo<ModidInfo> {

	public UniqueIdentifier block = null;

	public ModidInfo() {}

	public ModidInfo(int providerID, int category, int subCategory, Object data, UniqueIdentifier block) {
		super(providerID, category, subCategory, data);
		this.block = block;
		this.dataType = 1;
	}

	public ModidInfo(int providerID, String category, String subCategory, Object data, UniqueIdentifier block) {
		super(providerID, category, subCategory, data);
		this.block = block;
		this.dataType = 1;
	}

	@Override
	public String getName() {
		return "Unique Identifier";
	}

	@Override
	public String getData() {
		return this.getName();
	}

	@Override
	public String getDisplayableData() {
		String modName;
		try {
			ModContainer mod = Loader.instance().getIndexedModList().get(block.modId);
			modName = mod == null ? "Minecraft" : mod.getName();
		} catch (NullPointerException e) {
			modName = "";
		}
		return modName + suffix;
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		super.writeToBuf(buf);
		ByteBufUtils.writeUTF8String(buf, block.modId);
		ByteBufUtils.writeUTF8String(buf, block.name);
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		super.readFromBuf(buf);
		block = new UniqueIdentifier(ByteBufUtils.readUTF8String(buf) + ":" + ByteBufUtils.readUTF8String(buf));
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagCompound blockTag = new NBTTagCompound();
		blockTag.setString("modId", block.modId);
		blockTag.setString("name", block.name);
		tag.setTag("block", blockTag);
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagCompound blockTag = tag.getCompoundTag("block");
		block = new UniqueIdentifier(blockTag.getString("modId") + ":" + blockTag.getString("name"));
	}

	@Override
	public void writeUpdate(LogicInfo currentInfo, NBTTagCompound tag) {
		super.writeUpdate(currentInfo, tag);
		if (currentInfo instanceof ModidInfo) {
			ModidInfo info = (ModidInfo) currentInfo;
			if (!info.block.modId.equals(block.modId) || !info.block.name.equals(block.name)) {
				NBTTagCompound blockTag = new NBTTagCompound();
				blockTag.setString("modId", block.modId);
				blockTag.setString("name", block.name);
				tag.setTag("block", blockTag);
			}
		}
	}

	@Override
	public void readUpdate(NBTTagCompound tag) {
		super.readUpdate(tag);
		if (tag.hasKey("block")) {
			NBTTagCompound blockTag = tag.getCompoundTag("block");
			block = new UniqueIdentifier(blockTag.getString("modId") + ":" + blockTag.getString("name"));
		}
	}
	
	@Override
	public SyncType isMatchingData(ModidInfo currentInfo) {
		SyncType matching = super.isMatchingData(currentInfo);
		if (matching == null) {
			return block.equals(((ModidInfo) currentInfo).block) ? SyncType.SYNC : null;
		}
		return matching;
	}

	@Override
	public ModidInfo instance() {
		return new ModidInfo();
	}
}
