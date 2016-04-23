package sonar.logistics.info.types;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import sonar.core.api.StoredFluidStack;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.render.ScreenType;
import sonar.logistics.api.wrappers.FluidWrapper.StorageFluids;
import sonar.logistics.api.wrappers.ItemWrapper.SortingDirection;
import sonar.logistics.api.wrappers.ItemWrapper.SortingType;
import sonar.logistics.api.wrappers.ItemWrapper.StorageItems;
import sonar.logistics.helpers.FluidHelper;
import sonar.logistics.helpers.ItemHelper;
import cpw.mods.fml.common.network.ByteBufUtils;

public class FluidInventoryInfo extends ILogicInfo<FluidInventoryInfo> {

	public StorageFluids stacks = StorageFluids.EMPTY.copy();
	public boolean lastSync =false;
	public int cacheID = -1;
	public int sort = -1, order = -1;
	public static String rend = "FLUIDS";

	public static FluidInventoryInfo createInfo(StorageFluids stacks, int cacheID, int sort, int order) {
		FluidInventoryInfo info = new FluidInventoryInfo();
		info.stacks = stacks.copy();
		info.cacheID = cacheID;
		info.sort = sort;
		info.order = order;
		return info;
	}
	
	@Override
	public String getName() {
		return "FluidInventoryInfo";
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
		FluidHelper.readStorageToNBT(tag, stacks.fluids, SyncType.SYNC);
		FluidHelper.sortFluidList(stacks.fluids, SortingDirection.values()[order], SortingType.values()[sort]);
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("sort", sort);
		tag.setInteger("order", order);
		lastSync=false;
		FluidHelper.writeStorageToNBT(tag, lastSync, stacks, SyncType.SYNC);
	}

	@Override
	public void renderInfo(Tessellator tess, TileEntity tile, float minX, float minY, float maxX, float maxY, float zOffset, ScreenType type) {

		if (stacks.fluids != null) {
			int xSlots = Math.round(maxX - minX);
			int ySlots = (int) (Math.round(maxY - minY));
			if (type.isNormalSize()) {
				if (stacks.fluids != null && !stacks.fluids.isEmpty() && stacks.fluids.get(0) != null) {
					FluidStackInfo.createInfo(stacks.fluids.get(0), cacheID).renderInfo(tess, tile, minX, minY, maxX, maxY, zOffset, type);
				}

			} else {
				float fluidMaxX = 1.0F - 0.0625f * 2;
				float fluidMinX = 0;
				float fluidMaxY = 1.0F - 0.0625f * 2;
				float fluidMinY = 0;
				int currentSlot = 0;

				if (stacks != null) {
					List<StoredFluidStack> currentStacks = (List<StoredFluidStack>) (((ArrayList<StoredFluidStack>) (stacks.fluids)).clone());

					GL11.glTranslatef(minX, minY, 0.01f);

					// GL11.glTranslatef(0.0f, 0.0f, +0.19f);
					GL11.glTranslated(0.0, 0.0, zOffset - 0.01);
					float spacing = 1f;
					// GL11.glScaled(0.75, 0.75, 0.75);

					for (StoredFluidStack stack : currentStacks) {
						if (stack != null) {
							if (currentSlot < (xSlots * (ySlots))) {
								int xLevel = (int) (currentSlot - ((Math.floor((currentSlot / xSlots))) * xSlots));
								int yLevel = (int) (Math.floor((currentSlot / xSlots)));

								float width = stack.stored * (fluidMaxX - fluidMinX) / stack.capacity;
								IIcon icon = stack.fluid.getFluid().getIcon();
								if (icon != null) {
									Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

									GL11.glTranslatef(xLevel * spacing, yLevel * spacing, 0);
									tess.startDrawingQuads();
									double divide = Math.max((0.5 + (fluidMaxX - fluidMinX)), (0.5 + (fluidMaxY - fluidMinY)));
									double widthnew = (icon.getMinU() + (width * (icon.getMaxU() - icon.getMinU()) / divide));
									double heightnew = (icon.getMinV() + ((fluidMaxY - fluidMinY) * (icon.getMaxV() - icon.getMinV()) / divide));

									double height = (fluidMaxY);
									tess.addVertexWithUV((fluidMinX + 0), height, 0, icon.getMinU(), heightnew);
									tess.addVertexWithUV((fluidMinX + width), height, 0, widthnew, heightnew);
									tess.addVertexWithUV((fluidMinX + width), fluidMinY, 0, widthnew, icon.getMinV());
									tess.addVertexWithUV((fluidMinX + 0), fluidMinY, 0, icon.getMinU(), icon.getMinV());
									tess.draw();

									GL11.glTranslatef(-xLevel * spacing, -yLevel * spacing, 0);
								}
								FontRenderer rend = Minecraft.getMinecraft().fontRenderer;
								String category = (stack != null ? stack.fluid.getFluid().getLocalizedName(stack.fluid) : this.rend);

								String data = FontHelper.formatFluidSize(stack.stored);
								float itemScale = 120f;
								GL11.glPushMatrix();
								GL11.glTranslatef(xLevel * spacing, yLevel * spacing, 0);
								GL11.glTranslatef(fluidMinX + (fluidMaxX - fluidMinX) / 2, fluidMinY + (fluidMaxY - fluidMinY) / 2, 0.01f);
								GL11.glScaled(1.0f / itemScale, 1.0f / itemScale, 1.0f / itemScale);

								rend.drawString(category, -rend.getStringWidth(category) / 2, -8, -1);
								rend.drawString(data, -rend.getStringWidth(data) / 2, 4, -1);
								GL11.glScaled(itemScale, itemScale, itemScale);
								GL11.glTranslatef(-xLevel * spacing, -yLevel * spacing, 0);
								GL11.glPopMatrix();
							}
						}
						currentSlot++;
					}
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
	public FluidInventoryInfo instance() {
		return new FluidInventoryInfo();
	}

	@Override
	public void writeUpdate(FluidInventoryInfo currentInfo, NBTTagCompound tag) {
		if (currentInfo.sort != sort) {
			tag.setInteger("sort", currentInfo.sort);
			sort = currentInfo.sort;
		}
		if (currentInfo.order != order) {
			tag.setInteger("order", currentInfo.order);
			order = currentInfo.order;
		}

		lastSync = FluidHelper.writeStorageToNBT(tag, lastSync, currentInfo.stacks, SyncType.SPECIAL);

	}

	@Override
	public void readUpdate(NBTTagCompound tag) {
		if (tag.hasKey("sort")) {
			sort = tag.getInteger("sort");
		}
		if (tag.hasKey("order")) {
			order = tag.getInteger("order");
		}
		FluidHelper.readStorageToNBT(tag, stacks.fluids, SyncType.SPECIAL);
		FluidHelper.sortFluidList(stacks.fluids, SortingDirection.values()[order], SortingType.values()[sort]);
	}

	@Override
	public SyncType isMatchingData(FluidInventoryInfo currentInfo) {
		if(cacheID!=currentInfo.cacheID){
			return SyncType.SAVE;
		}
		if(sort!=currentInfo.sort || order!=currentInfo.order || !currentInfo.stacks.fluids.equals(stacks.fluids)){
			return SyncType.SYNC;
		}
		return null;
	}

}
