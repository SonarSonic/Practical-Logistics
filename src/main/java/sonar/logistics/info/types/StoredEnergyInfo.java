package sonar.logistics.info.types;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import sonar.core.api.BlockCoords;
import sonar.core.api.SonarAPI;
import sonar.core.api.StoredEnergyStack;
import sonar.core.helpers.RenderHelper;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.render.ScreenType;
import sonar.logistics.api.utils.IdentifiedCoords;
import sonar.logistics.client.renderers.RenderHandlers;

public class StoredEnergyInfo extends ILogicInfo<StoredEnergyInfo> {

	private static final ResourceLocation progress = new ResourceLocation(RenderHandlers.modelFolder + "progressBar.png");
	public StoredEnergyStack stack;
	public IdentifiedCoords coords;
	public String rend = "ITEMREND";

	public static StoredEnergyInfo createInfo(IdentifiedCoords coords, StoredEnergyStack stack) {
		StoredEnergyInfo info = new StoredEnergyInfo();
		info.stack = stack;
		info.coords = coords;
		return info;
	}

	@Override
	public String getName() {
		return "StoredEnergy";
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
		return stack != null ? stack.toString() : rend;
	}

	@Override
	public String getDisplayableData() {
		return getData();
	}

	@Override
	public String getData() {
		return (stack != null ? String.valueOf(stack.stored) : String.valueOf(0));
	}

	@Override
	public int getDataType() {
		return 0;
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		stack = StoredEnergyStack.readFromBuf(buf);
		coords = IdentifiedCoords.readCoords(buf);
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		StoredEnergyStack.writeToBuf(buf, stack);
		IdentifiedCoords.writeCoords(buf, coords);
	}

	public void readFromNBT(NBTTagCompound tag) {
		this.stack = StoredEnergyStack.readFromNBT(tag);
		this.coords = IdentifiedCoords.readFromNBT(tag);
	}

	public void writeToNBT(NBTTagCompound tag) {
		StoredEnergyStack.writeToNBT(tag, stack);
		IdentifiedCoords.writeToNBT(tag, coords);
	}

	@Override
	public void renderInfo(Tessellator tess, TileEntity tile, float minX, float minY, float maxX, float maxY, float zOffset, ScreenType type) {
		GL11.glTranslated(0, 0, zOffset + 0.002);
		float width = stack.stored * (maxX - minX) / stack.capacity;
		Minecraft.getMinecraft().renderEngine.bindTexture(progress);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.drawTexturedModalRect(minX, minY, maxY, width, (maxY - minY));	
		GL11.glTranslated(0, 0, -zOffset - 0.002);	
		FontRenderer rend = Minecraft.getMinecraft().fontRenderer;
		GL11.glTranslatef(minX + (maxX - minX) / 2, minY + (maxY - minY) / 2, 0.01f);
		int sizing = (int) Math.round(Math.min((maxX - minX), (maxY - minY) * 1.5));
		GL11.glTranslatef(0.0f, (float) (type.isNormalSize() ? -0.1F : -0.31F + ((sizing - 1) * -0.04)), zOffset);
		double itemScale = sizing >= 2 ? LogisticsAPI.getInfoRenderer().getScale(sizing) : 120;
		GL11.glScaled(1.0f / itemScale, 1.0f / itemScale, 1.0f / itemScale);
		String coordString = coords.block.getDisplayName();
		rend.drawString(EnumChatFormatting.UNDERLINE + coordString, -rend.getStringWidth(coordString) / 2, -20, -1);
		String stored = "Stored: " + String.valueOf(stack.stored) + " " + stack.energyType.getStorageSuffix();
		String capacity = "Capacity: " + String.valueOf(stack.capacity) + " " + stack.energyType.getStorageSuffix();		
		rend.drawString(stored, -rend.getStringWidth(stored) / 2, -8, -1);
		rend.drawString(capacity, -rend.getStringWidth(capacity) / 2, 4, -1);
		if (stack.hasInput) {
			String input = "Max Input: " + String.valueOf(stack.input) + " " + stack.energyType.getStorageSuffix();
			rend.drawString(input, -rend.getStringWidth(input) / 2, 16, -1);
		}
		if (stack.hasOutput) {
			String output = "Max Output: " + String.valueOf(stack.output) + " " + stack.energyType.getStorageSuffix();
			rend.drawString(output, -rend.getStringWidth(output) / 2, 28, -1);
		}
		if (stack.hasUsage) {
			String usage = "Usage: " + String.valueOf(stack.usage) + " " + stack.energyType.getUsageSuffix();
			rend.drawString(usage, -rend.getStringWidth(usage) / 2, 28, -1);
		}
	}

	public double getXTranslate(float scale, double sizing) {
		if (scale >= 120) {
			return -0.02F;
		}
		return (-0.08F + ((sizing - 1) * 0.17));

	}

	@Override
	public StoredEnergyInfo instance() {
		return new StoredEnergyInfo();
	}

	@Override
	public void writeUpdate(StoredEnergyInfo currentInfo, NBTTagCompound tag) {
		if (currentInfo.stack.hasInput != stack.hasInput) {
			stack.hasInput = currentInfo.stack.hasInput;
			tag.setBoolean("hI", stack.hasInput);
		}
		if (currentInfo.stack.hasOutput != stack.hasOutput) {
			stack.hasOutput = currentInfo.stack.hasOutput;
			tag.setBoolean("hO", stack.hasOutput);
		}
		if (currentInfo.stack.hasStorage != stack.hasStorage) {
			stack.hasStorage = currentInfo.stack.hasStorage;
			tag.setBoolean("hS", stack.hasStorage);
		}
		if (currentInfo.stack.hasUsage != stack.hasUsage) {
			stack.hasUsage = currentInfo.stack.hasUsage;
			tag.setBoolean("hU", stack.hasUsage);
		}
		if (currentInfo.stack.capacity != stack.capacity) {
			stack.capacity = currentInfo.stack.capacity;
			tag.setLong("c", stack.capacity);
		}
		if (currentInfo.stack.input != stack.input) {
			stack.input = currentInfo.stack.input;
			tag.setLong("i", stack.input);
		}
		if (currentInfo.stack.output != stack.output) {
			stack.output = currentInfo.stack.output;
			tag.setLong("o", stack.output);
		}
		if (currentInfo.stack.stored != stack.stored) {
			stack.stored = currentInfo.stack.stored;
			tag.setLong("s", stack.stored);
		}
		if (currentInfo.stack.usage != stack.usage) {
			stack.usage = currentInfo.stack.usage;
			tag.setLong("u", stack.usage);
		}
		if (!currentInfo.stack.energyType.getStorageSuffix().equals(stack.energyType.getStorageSuffix())) {
			stack.energyType = currentInfo.stack.energyType;
			tag.setString("energyType", stack.energyType.getStorageSuffix());
		}
		if (!currentInfo.coords.blockCoords.equals(coords.blockCoords)) {
			coords.blockCoords = currentInfo.coords.blockCoords;
			BlockCoords.writeToNBT(tag, coords.blockCoords);
		}
		if (!ItemStack.areItemStacksEqual(coords.block, currentInfo.coords.block)) {
			coords.block = currentInfo.coords.block;
			coords.block.writeToNBT(tag);
		}
		if (!currentInfo.coords.coordString.equals(coords.coordString)) {
			coords.coordString = currentInfo.coords.coordString;
			tag.setString("suffix", coords.coordString);
		}
	}

	@Override
	public void readUpdate(NBTTagCompound tag) {
		if (tag.hasKey("hI")) {
			stack.hasInput = tag.getBoolean("hI");
		}
		if (tag.hasKey("hO")) {
			stack.hasOutput = tag.getBoolean("hO");
		}
		if (tag.hasKey("hS")) {
			stack.hasStorage = tag.getBoolean("hS");
		}
		if (tag.hasKey("hU")) {
			stack.hasUsage = tag.getBoolean("hU");
		}
		if (tag.hasKey("c")) {
			stack.capacity = tag.getLong("c");
		}
		if (tag.hasKey("i")) {
			stack.input = tag.getLong("i");
		}
		if (tag.hasKey("o")) {
			stack.output = tag.getLong("o");
		}
		if (tag.hasKey("s")) {
			stack.stored = tag.getLong("s");
		}
		if (tag.hasKey("u")) {
			stack.usage = tag.getLong("u");
		}
		if (tag.hasKey("energyType")) {
			stack.energyType = SonarAPI.getRegistry().getEnergyType(tag.getString("energyType"));
		}
		if (tag.hasKey("x")) {
			coords.blockCoords = BlockCoords.readFromNBT(tag);
		}
		if (tag.hasKey("id")) {
			coords.block = ItemStack.loadItemStackFromNBT(tag);
		}
		if (tag.hasKey("suffix")) {
			coords.coordString = tag.getString("suffix");
		}
	}

	@Override
	public boolean matches(StoredEnergyInfo currentInfo) {
		return currentInfo.stack.equals(stack) && currentInfo.coords.equals(coords);
	}

}
