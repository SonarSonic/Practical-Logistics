package sonar.logistics.client;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.MultipartHelper;
import mcmultipart.raytrace.RayTraceUtils;
import mcmultipart.raytrace.RayTraceUtils.AdvancedRayTraceResultPart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import sonar.core.client.gui.GuiSonar;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.RenderHelper;
import sonar.core.integration.multipart.SonarMultipartHelper;
import sonar.logistics.api.connecting.IOperatorProvider;
import sonar.logistics.api.connecting.IOperatorTool;

public class RenderOperatorOverlay {

	public static boolean isUsing, gotFirstPacket;
	public static BlockPos lastPos;

	public static void tick(DrawBlockHighlightEvent evt) {
		if (!Minecraft.getMinecraft().inGameHasFocus || !isUsing) {
			return;
		}
		ItemStack stack = evt.getPlayer().getHeldItemMainhand();
		
		if(stack==null || !(stack.getItem() instanceof IOperatorTool)){
			//isUsing=false;
			return;
		}
		BlockPos pos = evt.getTarget().getBlockPos();
		if (pos == null) {
			return;
		}
		boolean requestPacket = !pos.equals(lastPos);
		lastPos = pos;
		IMultipartContainer container = (IMultipartContainer) MultipartHelper.getPartContainer(Minecraft.getMinecraft().theWorld, pos);
		
		if (container != null) {
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			Vec3d start = RayTraceUtils.getStart(player);
			Vec3d end = RayTraceUtils.getEnd(player);
			AdvancedRayTraceResultPart result = SonarMultipartHelper.collisionRayTrace(container, start, end);
			
			if (result != null) {				
				RenderHelper.offsetRendering(pos, evt.getPartialTicks());
				Entity view = Minecraft.getMinecraft().getRenderViewEntity();
				IMultipart part = result.hit.partHit;
				
				if (part != null && part instanceof IOperatorProvider) {					
					IOperatorProvider provider = (IOperatorProvider) part;					
					if(requestPacket)provider.updateOperatorInfo();					
					ArrayList<String> infoList = new ArrayList();
					provider.addInfo(infoList);
					if (infoList.isEmpty()) {
						return;
					}
					int maxWidth = 60;
					int maxHeight = infoList.size() * 12;
					for (int i = 0; i < infoList.size(); i++) {
						int length = (int) ((RenderHelper.fontRenderer.getStringWidth(infoList.get(i)) + 4) * 0.8);
						if (length > maxWidth) {
							maxWidth = length;
						}
					}
					GlStateManager.translate(0.5, 1, 0.5);
					GlStateManager.rotate(-view.rotationYaw - 180, 0, 1, 0);
					GlStateManager.rotate(-view.rotationPitch, 1, 0, 0);
					RenderHelper.saveBlendState();
					GlStateManager.disableDepth();
					GlStateManager.scale(0.016, 0.016, 1);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					
					GuiSonar.drawTransparentRect(-maxWidth / 2, -maxHeight / 2, maxWidth / 2, maxHeight / 2, LogisticsColours.layers[1].getRGB());
					GuiSonar.drawTransparentRect(-maxWidth / 2 + 1, -maxHeight / 2 + 1, maxWidth / 2 - 1, maxHeight / 2 - 1, LogisticsColours.layers[2].getRGB());
					GuiSonar.drawTransparentRect(-maxWidth / 2 + 1, -maxHeight / 2 + 1, maxWidth / 2 - 1, maxHeight / 2 - 1, LogisticsColours.layers[2].getRGB());
					GuiSonar.drawTransparentRect(-maxWidth / 2 + 1, -maxHeight / 2 + 1, maxWidth / 2 - 1, maxHeight / 2 - 1, LogisticsColours.layers[2].getRGB());

					GlStateManager.scale(0.8, -0.8, 0.8);
					// FontHelper.textCentre(infoList.get(0), 0, -maxHeight/2 + 4, -1);
					double yCentre = 0;
					double centre = ((double) (infoList.size()) / 2) - yCentre;
					float offset = 12F;
					
					for (int i = 0; i < infoList.size(); i++) {
						String info = infoList.get(i);
						FontHelper.textCentre(info, 0, (int) (i == centre ? yCentre : i < centre ? yCentre - offset * -(i - centre) : yCentre + offset * (i - centre)), -1);
					}

					RenderHelper.restoreBlendState();
					GlStateManager.enableDepth();

				}
				GlStateManager.popMatrix();
			}
		}
		// }
	}

}
