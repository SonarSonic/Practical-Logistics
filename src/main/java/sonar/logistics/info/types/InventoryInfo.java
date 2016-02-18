package sonar.logistics.info.types;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.helpers.FontHelper;
import sonar.core.utils.helpers.RenderHelper;
import sonar.logistics.api.Info;
import sonar.logistics.api.render.ScreenType;
import cpw.mods.fml.common.network.ByteBufUtils;

public class InventoryInfo extends Info<InventoryInfo> {

	public List<StoredItemStack> stacks = new ArrayList();
	public String rend = "ITEMINV";

	public static InventoryInfo createInfo(List<StoredItemStack> stacks) {
		InventoryInfo info = new InventoryInfo();
		info.stacks = stacks;
		return info;
	}

	@Override
	public String getName() {
		return "InventoryInfo";
	}

	@Override
	public int getProviderID() {
		return -1;
	}

	@Override
	public String getCategory() {
		return rend;
	}

	@Override
	public String getSubCategory() {
		return rend;
	}

	@Override
	public String getDisplayableData() {
		return getData();
	}

	@Override
	public String getData() {
		return "Inventory";
	}

	@Override
	public int getDataType() {
		return 1;
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		this.readFromNBT(ByteBufUtils.readTag(buf));
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		ByteBufUtils.writeTag(buf, tag);
	}

	public void readFromNBT(NBTTagCompound tag) {
		NBTTagList list = tag.getTagList("StoredStacks", 10);
		this.stacks = new ArrayList();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound compound = list.getCompoundTagAt(i);
			this.stacks.add(StoredItemStack.readFromNBT(compound));

		}
	}

	public void writeToNBT(NBTTagCompound tag) {
		NBTTagList list = new NBTTagList();
		if (stacks == null) {
			stacks = new ArrayList();
		}
		for (int i = 0; i < this.stacks.size(); i++) {
			if (this.stacks.get(i) != null) {
				NBTTagCompound compound = new NBTTagCompound();
				StoredItemStack.writeToNBT(compound, this.stacks.get(i));
				list.appendTag(compound);
			}
		}

		tag.setTag("StoredStacks", list);
	}

	@Override
	public void renderInfo(Tessellator tess, TileEntity tile, float minX, float minY, float maxX, float maxY, float zOffset, ScreenType type) {

		if (stacks != null) {
			int xSlots = Math.round(maxX - minX) * 2;
			int ySlots = (int) (Math.round(maxY - minY) * 2);
			if (type.isNormalSize()) {
				xSlots = 2;
				ySlots = 1;
			}
			int currentSlot = 0;

			if (stacks != null) {
				List<StoredItemStack> currentStacks = (List<StoredItemStack>) (((ArrayList<StoredItemStack>) (stacks)).clone());

				GL11.glTranslatef(minX + 0.18f, minY + 0.18f, 0.01f);

				GL11.glTranslatef(0.0f, 0.0f, +0.19f);
				GL11.glTranslated(0.0, 0.07F, zOffset - 0.01);
				float spacing = 0.665f;
				GL11.glScaled(0.75, 0.75, 0.75);

				for (StoredItemStack stack : currentStacks) {
					if (stack != null) {
						if (currentSlot < (xSlots * (ySlots))) {
							int xLevel = (int) (currentSlot - ((Math.floor((currentSlot / xSlots))) * xSlots));
							int yLevel = (int) (Math.floor((currentSlot / xSlots)));

							FontRenderer rend = Minecraft.getMinecraft().fontRenderer;
							if (stack.item != null) {
								stack.item.stackSize = 1;

								GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

								GL11.glDisable(GL12.GL_RESCALE_NORMAL);
								GL11.glEnable(GL11.GL_CULL_FACE);
								tess.setColorOpaque_F(1.0f, 1.0f, 1.0f);
								double sizing = Math.round(Math.min((maxX - minX), (maxY - minY)));
								double itemScale = sizing >= 2 ? (2.5F + sizing - 1 * 1.0F) : type.isNormalSize() ? 0.8F : 1.4F;

								GL11.glTranslatef(xLevel * spacing, yLevel * spacing, 0);
								RenderHelper.doRenderItem(stack.item, tile.getWorldObj(), false);
								GL11.glDisable(GL11.GL_CULL_FACE);
								GL11.glEnable(GL12.GL_RESCALE_NORMAL);
								GL11.glTranslatef(0.0f, 0.0f, -0.242f);

								GL11.glScalef(1.0f / 40.0f, 1.0f / 40.0f, 1.0f / 40.0f);
								String s1 = FontHelper.formatStackSize(stack.stored);
								final float scaleFactor = 0.5F;
								final float inverseScaleFactor = 1.0f / scaleFactor;
								GL11.glScaled(scaleFactor, scaleFactor, scaleFactor);

								final int X = (int) (((float) -8 + 15.0f - rend.getStringWidth(s1) * scaleFactor) * inverseScaleFactor);
								final int Y = (int) (((float) -12 + 15.0f - 7.0f * scaleFactor) * inverseScaleFactor);

								GL11.glDisable(GL11.GL_LIGHTING);
								rend.drawString(s1, X, Y, 16777215);
								GL11.glScaled(inverseScaleFactor, inverseScaleFactor, inverseScaleFactor);
								GL11.glScaled(40.0f, 40.0f, 40.0f);
								GL11.glTranslatef(0.0f, 0.0f, 0.242f);
								GL11.glTranslatef(-xLevel * spacing, -yLevel * spacing, 0);
							}
						}
					}
					currentSlot++;
				}
			}

		}

	}

	public double getXTranslate(float scale, double sizing) {
		if (scale >= 120) {
			return 0.07F;
		}
		return (0.13F + ((sizing - 1) * 0.17));

	}

	@Override
	public InventoryInfo instance() {
		return new InventoryInfo();
	}

	@Override
	public void writeUpdate(InventoryInfo currentInfo, NBTTagCompound tag) {
		if (currentInfo.stacks == null) {
			currentInfo.stacks = new ArrayList();
		}
		if (stacks == null) {
			stacks = new ArrayList();
		}
		if (currentInfo.stacks.size() <= 0 && (!(this.stacks.size() <= 0))) {
			tag.setBoolean("null", true);
			this.stacks = new ArrayList();
			return;
		}
		NBTTagList list = new NBTTagList();
		int size = Math.max(currentInfo.stacks.size(), this.stacks.size());
		for (int i = 0; i < size; ++i) {
			StoredItemStack current = null;
			StoredItemStack last = null;
			if (i < currentInfo.stacks.size()) {
				current = currentInfo.stacks.get(i);
			}
			if (i < this.stacks.size()) {
				last = this.stacks.get(i);
			}
			NBTTagCompound compound = new NBTTagCompound();
			if (current != null) {
				if (last != null) {
					if (!ItemStack.areItemStacksEqual(last.item, current.item)) {
						compound.setByte("f", (byte) 0);
						this.stacks.set(i, current);
						StoredItemStack.writeToNBT(compound, currentInfo.stacks.get(i));

					} else if (last.stored != current.stored) {
						compound.setByte("f", (byte) 1);
						this.stacks.set(i, current);
						StoredItemStack.writeToNBT(compound, currentInfo.stacks.get(i));
						compound.setLong("Stored", current.stored);
					}
				} else {
					compound.setByte("f", (byte) 0);
					this.stacks.add(i, current);
					StoredItemStack.writeToNBT(compound, currentInfo.stacks.get(i));
				}
			} else if (last != null) {
				this.stacks.set(i, null);
				compound.setByte("f", (byte) 2);
			}
			if (!compound.hasNoTags()) {
				compound.setInteger("Slot", i);
				list.appendTag(compound);
			}

		}
		if (list.tagCount() != 0) {
			tag.setTag("Stacks", list);
		}
	}

	@Override
	public void readUpdate(NBTTagCompound tag) {
		if (tag.hasKey("null")) {
			this.stacks = new ArrayList();
			return;
		}
		NBTTagList list = tag.getTagList("Stacks", 10);
		if (this.stacks == null) {
			this.stacks = new ArrayList();
		}
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound compound = list.getCompoundTagAt(i);
			int slot = compound.getInteger("Slot");
			boolean set = slot < stacks.size();
			switch (compound.getByte("f")) {
			case 0:
				if (set)
					stacks.set(slot, StoredItemStack.readFromNBT(compound));
				else
					stacks.add(slot, StoredItemStack.readFromNBT(compound));
				break;
			case 1:
				long stored = compound.getLong("Stored");
				if (stored != 0) {
					stacks.set(slot, new StoredItemStack(stacks.get(slot).item, stored));
				} else {
					stacks.set(slot, null);
				}
				break;
			case 2:
				if (set)
					stacks.set(slot, null);
				else
					stacks.add(slot, null);
				break;
			}
		}

	}

	@Override
	public boolean matches(InventoryInfo currentInfo) {
		return currentInfo.stacks.equals(stacks);
	}

}
