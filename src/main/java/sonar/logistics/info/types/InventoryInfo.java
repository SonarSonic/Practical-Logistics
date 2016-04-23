package sonar.logistics.info.types;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import sonar.core.api.StoredItemStack;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.RenderHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.render.ScreenType;
import sonar.logistics.api.wrappers.ItemWrapper.SortingDirection;
import sonar.logistics.api.wrappers.ItemWrapper.SortingType;
import sonar.logistics.api.wrappers.ItemWrapper.StorageItems;
import sonar.logistics.helpers.ItemHelper;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

public class InventoryInfo extends ILogicInfo<InventoryInfo> {

	public StorageItems stacks = StorageItems.EMPTY.copy();
	public boolean lastSync =false;
	public int cacheID = -1;
	public int sort = -1, order = -1;
	public String rend = "ITEMINV";

	public static InventoryInfo createInfo(StorageItems stacks, int cacheID, int sort, int order) {
		InventoryInfo info = new InventoryInfo();
		info.stacks = stacks.copy();
		info.cacheID = cacheID;
		info.sort = sort;
		info.order = order;
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
		sort = buf.readInt();
		order = buf.readInt();
		this.readFromNBT(ByteBufUtils.readTag(buf));
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		buf.writeInt(sort);
		buf.writeInt(order);
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		ByteBufUtils.writeTag(buf, tag);
	}

	public void readFromNBT(NBTTagCompound tag) {
		sort = tag.getInteger("sort");
		order = tag.getInteger("order");
		ItemHelper.readStorageToNBT(tag, stacks.items, SyncType.SYNC);
		ItemHelper.sortItemList(stacks.items, SortingDirection.values()[order], SortingType.values()[sort]);
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("sort", sort);
		tag.setInteger("order", order);
		lastSync=false;
		ItemHelper.writeStorageToNBT(tag, lastSync, stacks, SyncType.SYNC);
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
				List<StoredItemStack> currentStacks = (List<StoredItemStack>) (((ArrayList<StoredItemStack>) (stacks.items)).clone());

				GL11.glTranslatef(minX + 0.18f, minY + 0.18f, 0.01f);

				GL11.glTranslatef(0.0f, 0.0f, +0.19f);
				GL11.glTranslated(0.0, 0.07F, zOffset - 0.01);
				float spacing = 0.665f;
				GL11.glScaled(0.75, 0.75, 0.75);

				for (StoredItemStack stack : currentStacks) {
					if (stack != null) {
						if (currentSlot < (xSlots * (ySlots))) {
							GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
							int xLevel = (int) (currentSlot - ((Math.floor((currentSlot / xSlots))) * xSlots));
							int yLevel = (int) (Math.floor((currentSlot / xSlots)));

							FontRenderer rend = Minecraft.getMinecraft().fontRenderer;
							if (stack.item != null) {
								GL11.glPushMatrix();
								stack.item.stackSize = 1;

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
								GL11.glPopMatrix();
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
		if (currentInfo.sort != sort) {
			tag.setInteger("sort", currentInfo.sort);
			sort = currentInfo.sort;
		}
		if (currentInfo.order != order) {
			tag.setInteger("order", currentInfo.order);
			order = currentInfo.order;
		}

		lastSync = ItemHelper.writeStorageToNBT(tag, lastSync, currentInfo.stacks, SyncType.SPECIAL);

	}

	@Override
	public void readUpdate(NBTTagCompound tag) {
		if (tag.hasKey("sort")) {
			sort = tag.getInteger("sort");
		}
		if (tag.hasKey("order")) {
			order = tag.getInteger("order");
		}
		ItemHelper.readStorageToNBT(tag, stacks.items, SyncType.SPECIAL);
		ItemHelper.sortItemList(stacks.items, SortingDirection.values()[order], SortingType.values()[sort]);
	}
	
	@Override
	public SyncType isMatchingData(InventoryInfo currentInfo) {
		if(cacheID!=currentInfo.cacheID){
			return SyncType.SAVE;
		}
		if(sort!=currentInfo.sort || order!=currentInfo.order || !currentInfo.stacks.items.equals(stacks.items)){
			return SyncType.SYNC;
		}
		return null;
	}

}
