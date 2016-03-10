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

import sonar.core.fluid.StoredFluidStack;
import sonar.core.utils.helpers.FontHelper;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.render.ScreenType;
import cpw.mods.fml.common.network.ByteBufUtils;

public class FluidInventoryInfo extends ILogicInfo<FluidInventoryInfo> {

	public ArrayList<StoredFluidStack> stacks = new ArrayList();
	public String rend = "ITEMINV";

	public static FluidInventoryInfo createInfo(ArrayList<StoredFluidStack> stacks) {
		FluidInventoryInfo info = new FluidInventoryInfo();
		info.stacks = stacks;
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
			this.stacks.add(StoredFluidStack.readFromNBT(compound));

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
				StoredFluidStack.writeToNBT(compound, this.stacks.get(i));
				list.appendTag(compound);
			}
		}

		tag.setTag("StoredStacks", list);
	}

	@Override
	public void renderInfo(Tessellator tess, TileEntity tile, float minX, float minY, float maxX, float maxY, float zOffset, ScreenType type) {
	
		if (stacks != null) {
			int xSlots = Math.round(maxX - minX);
			int ySlots = (int) (Math.round(maxY - minY));
			if (type.isNormalSize()) {
				if (stacks != null && !stacks.isEmpty() && stacks.get(0) != null) {
					FluidStackInfo.createInfo(stacks.get(0)).renderInfo(tess, tile, minX, minY, maxX, maxY, zOffset, type);
				}

			} else {
				float fluidMaxX = 1.0F - 0.0625f * 2;
				float fluidMinX = 0;
				float fluidMaxY = 1.0F - 0.0625f * 2;
				float fluidMinY = 0;
				int currentSlot = 0;

				if (stacks != null) {
					List<StoredFluidStack> currentStacks = (List<StoredFluidStack>) (((ArrayList<StoredFluidStack>) (stacks)).clone());

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
		List<StoredFluidStack> currentList = new ArrayList();
		if (currentInfo.stacks != null) {
			currentList= currentInfo.stacks;
		}
		if (stacks == null) {
			stacks = new ArrayList();
		}
		if (currentList.size() <= 0 && (!(this.stacks.size() <= 0))) {
			tag.setBoolean("null", true);
			this.stacks = new ArrayList();
			return;
		}
		NBTTagList list = new NBTTagList();
		int size = Math.max(currentList.size(), this.stacks.size());
		for (int i = 0; i < size; ++i) {
			StoredFluidStack current = null;
			StoredFluidStack last = null;
			if (i < currentList.size()) {
				current = currentList.get(i);
			}
			if (i < this.stacks.size()) {
				last = this.stacks.get(i);
			}
			NBTTagCompound compound = new NBTTagCompound();
			if (current != null) {
				if (last != null) {
					if (!last.equalStack(current.fluid) || current.stored != last.stored) {
						compound.setByte("f", (byte) 0);
						this.stacks.set(i, current);
						StoredFluidStack.writeToNBT(compound, currentList.get(i));

					} else if (last.stored != current.stored) {
						compound.setByte("f", (byte) 1);
						this.stacks.set(i, current);
						StoredFluidStack.writeToNBT(compound, currentList.get(i));
						compound.setLong("Stored", current.stored);
					}
				} else {
					compound.setByte("f", (byte) 0);
					if (i < stacks.size()) {
						 stacks.set(i, current);
					} else{
						stacks.add(i, current);
					}
					StoredFluidStack.writeToNBT(compound, current);
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
					stacks.set(slot, StoredFluidStack.readFromNBT(compound));
				else
					stacks.add(slot, StoredFluidStack.readFromNBT(compound));
				break;
			case 1:
				long stored = compound.getLong("Stored");
				if (stored != 0) {
					stacks.set(slot, new StoredFluidStack(stacks.get(slot).fluid, stored));
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
	public boolean matches(FluidInventoryInfo currentInfo) {
		return currentInfo.stacks.equals(stacks);
	}

}
