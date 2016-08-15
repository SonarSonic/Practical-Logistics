package sonar.logistics.client;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.RenderHelper;

public class RenderBlockSelection {

	private static BlockCoords position = null;
	public static int displayTime = 8000;
	public static long clickTime;

	public static void tick(RenderWorldLastEvent evt) {
		if (position == null) {
			return;
		}
		long time = System.currentTimeMillis();
		if (time > clickTime + displayTime) {
			position = null;
		} else {
			GlStateManager.pushMatrix();
			GlStateManager.disableAlpha();
			RenderHelper.drawBoundingBox(Block.FULL_BLOCK_AABB, position.getBlockPos(), evt.getPartialTicks(), 120.0F, 100.0F, 20.0F, 0.5F);
			GlStateManager.enableAlpha();
			GlStateManager.popMatrix();
		}

	}

	public static BlockCoords getPosition() {
		return position;
	}

	public static void setPosition(BlockCoords position) {
		RenderBlockSelection.position = position;
		RenderBlockSelection.clickTime = System.currentTimeMillis();
	}

}
