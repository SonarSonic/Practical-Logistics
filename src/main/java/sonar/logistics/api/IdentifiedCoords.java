package sonar.logistics.api;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import sonar.core.utils.BlockCoords;
import cpw.mods.fml.common.network.ByteBufUtils;

public class IdentifiedCoords {
	public String name;
	public BlockCoords coords;

	public IdentifiedCoords(String name, BlockCoords coords) {
		this.name = name;
		this.coords = coords;
	}

	public static IdentifiedCoords readFromNBT(NBTTagCompound tag) {
		if (tag.getBoolean("b")) {
			return new IdentifiedCoords(tag.getString("clientName"), BlockCoords.readFromNBT(tag));
		} else {
			return null;
		}

	}

	public static void writeToNBT(NBTTagCompound tag, IdentifiedCoords info) {
		if (info != null) {
			tag.setBoolean("b", true);
			tag.setString("clientName", info.name);
			BlockCoords.writeToNBT(tag, info.coords);
		} else {
			tag.setBoolean("b", false);
		}
	}

	public static IdentifiedCoords readInfo(ByteBuf buf) {
		if (buf.readBoolean()) {
			String name = ByteBufUtils.readUTF8String(buf);
			BlockCoords coords = BlockCoords.readFromBuf(buf);
			return new IdentifiedCoords(name, coords);

		} else {
			return null;
		}
	}

	public static void writeInfo(ByteBuf buf, IdentifiedCoords info) {
		if (info != null) {
			buf.writeBoolean(true);
			ByteBufUtils.writeUTF8String(buf, info.name);
			BlockCoords.writeToBuf(buf, info.coords);
		} else {
			buf.writeBoolean(false);
		}
	}

	public static void readSavedList(NBTTagCompound nbt, List<IdentifiedCoords> coordList) {
		NBTTagList list = new NBTTagList();
		if (coordList == null) {
			coordList = new ArrayList();
		}
		for (int i = 0; i < coordList.size(); i++) {
			if (coordList.get(i) != null) {
				NBTTagCompound compound = new NBTTagCompound();
				compound.setByte("Slot", (byte) i);
				IdentifiedCoords.writeToNBT(compound, coordList.get(i));
				list.appendTag(compound);
			}
		}
		nbt.setTag("COORDS", list);
	}

	public static void writeSavedList(NBTTagCompound nbt, List<IdentifiedCoords> coordList) {
		NBTTagList list = new NBTTagList();
		if (coordList == null) {
			coordList = new ArrayList();
		}
		for (int i = 0; i < coordList.size(); i++) {
			if (coordList.get(i) != null) {
				NBTTagCompound compound = new NBTTagCompound();
				compound.setByte("Slot", (byte) i);
				IdentifiedCoords.writeToNBT(compound, coordList.get(i));
				list.appendTag(compound);
			}
		}
		nbt.setTag("COORDS", list);
	}

	public static void readSyncList(NBTTagCompound nbt, List<IdentifiedCoords> coordList) {
		if (nbt.hasKey("null")) {
			coordList = new ArrayList();
			return;
		}
		NBTTagList list = nbt.getTagList("Emitters", 10);
		if (coordList == null) {
			coordList = new ArrayList();
		}
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound compound = list.getCompoundTagAt(i);
			byte slot = compound.getByte("Slot");
			boolean set = slot < coordList.size();
			switch (compound.getByte("f")) {
			case 0:
				if (set)
					coordList.set(slot, IdentifiedCoords.readFromNBT(compound));
				else
					coordList.add(slot, IdentifiedCoords.readFromNBT(compound));
				break;
			case 1:
				String name = compound.getString("Name");
				if (name != null) {
					coordList.set(slot, new IdentifiedCoords(name, coordList.get(slot).coords));
				} else {
					coordList.set(slot, null);
				}
				break;
			case 2:
				if (set)
					coordList.set(slot, null);
				else
					coordList.add(slot, null);
				break;
			}

		}
	}

	public static void writeSyncList(NBTTagCompound nbt, List<IdentifiedCoords> coordList, List<IdentifiedCoords> lastList) {
		if (coordList == null) {
			coordList = new ArrayList();
		}
		if (lastList == null) {
			lastList = new ArrayList();
		}
		if (coordList.size() <= 0 && (!(lastList.size() <= 0))) {
			nbt.setBoolean("null", true);
			lastList = new ArrayList();
			return;
		}
		NBTTagList list = new NBTTagList();
		int size = Math.max(coordList.size(), lastList.size());
		for (int i = 0; i < size; ++i) {
			IdentifiedCoords current = null;
			IdentifiedCoords last = null;
			if (i < coordList.size()) {
				current = coordList.get(i);
			}
			if (i < lastList.size()) {
				last = lastList.get(i);
			}
			NBTTagCompound compound = new NBTTagCompound();
			if (current != null) {
				if (last != null) {
					if (!BlockCoords.equalCoords(current.coords, last.coords)) {
						compound.setByte("f", (byte) 0);
						lastList.set(i, current);
						writeToNBT(compound, coordList.get(i));

					} else if (!current.name.equals(last.name)) {
						compound.setByte("f", (byte) 1);
						lastList.set(i, current);
						compound.setString("Name", current.name);
					}
				} else {
					compound.setByte("f", (byte) 0);
					lastList.add(i, current);
					writeToNBT(compound, coordList.get(i));
				}
			} else if (last != null) {
				lastList.set(i, null);
				compound.setByte("f", (byte) 2);
			}
			if (!compound.hasNoTags()) {
				compound.setByte("Slot", (byte) i);
				list.appendTag(compound);
			}

		}
		if (list.tagCount() != 0) {
			nbt.setTag("Emitters", list);
		}
	}

}
