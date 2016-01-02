package sonar.logistics.client.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.FMPHelper;
import sonar.logistics.api.render.ICableRenderer;
import sonar.logistics.common.tileentity.TileEntityBlockNode;

public class ModelDataCable extends ModelBase {
	// fields
	ModelRenderer Bottom;
	ModelRenderer Centre;
	ModelRenderer Top;
	ModelRenderer Arm1;
	ModelRenderer Arm2;
	ModelRenderer Arm3;
	ModelRenderer Arm4;

	public ModelDataCable() {
		textureWidth = 64;
		textureHeight = 32;

		Bottom = new ModelRenderer(this, 0, 0);
		Bottom.addBox(0F, 0F, 0F, 2, 6, 2);
		Bottom.setRotationPoint(-1F, 24F, 1F);
		Bottom.setTextureSize(64, 32);
		Bottom.mirror = true;
		setRotation(Bottom, 3.141593F, 0F, 0F);
		Centre = new ModelRenderer(this, 0, 8);
		Centre.addBox(0F, 0F, 0F, 4, 4, 4);
		Centre.setRotationPoint(-2F, 14F, -2F);
		Centre.setTextureSize(64, 32);
		Centre.mirror = true;
		setRotation(Centre, 0F, 0F, 0F);
		Top = new ModelRenderer(this, 0, 0);
		Top.addBox(0F, 0F, 0F, 2, 6, 2);
		Top.setRotationPoint(-1F, 8F, -1F);
		Top.setTextureSize(64, 32);
		Top.mirror = true;
		setRotation(Top, 0F, 0F, 0F);
		Arm1 = new ModelRenderer(this, 0, 0);
		Arm1.addBox(0F, 0F, 0F, 2, 6, 2);
		Arm1.setRotationPoint(1F, 15F, 8F);
		Arm1.setTextureSize(64, 32);
		Arm1.mirror = true;
		setRotation(Arm1, -1.570796F, 0F, 1.570796F);

		Arm2 = new ModelRenderer(this, 0, 0);
		Arm2.addBox(0F, 0F, 0F, 2, 6, 2);
		Arm2.setRotationPoint(-8F, 17F, -1F);
		Arm2.setTextureSize(64, 32);
		Arm2.mirror = true;
		setRotation(Arm2, 0F, 0F, -1.570796F);

		Arm3 = new ModelRenderer(this, 0, 0);
		Arm3.addBox(0F, 0F, 0F, 2, 6, 2);
		Arm3.setRotationPoint(-1F, 15F, -2F);
		Arm3.setTextureSize(64, 32);
		Arm3.mirror = true;
		setRotation(Arm3, -1.570796F, 0, 0);

		Arm4 = new ModelRenderer(this, 0, 0);
		Arm4.addBox(0F, 0F, 0F, 2, 6, 2);
		Arm4.setRotationPoint(8F, 15F, 1F);
		Arm4.setTextureSize(64, 32);
		Arm4.mirror = true;
		setRotation(Arm4, 0F, 1.570796F, 1.570796F);
	}

	public void renderTile(TileEntity tile, Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(entity, f, f1, f2, f3, f4, f5);
		boolean blockNode = tile instanceof TileEntityBlockNode;
		if (!blockNode) {
			Centre.render(f5);
		}
		Object object = FMPHelper.checkObject(tile);
		if (tile.getWorldObj() != null && object instanceof ICableRenderer) {
			ICableRenderer cable = (ICableRenderer) object;
			boolean renderCentre = false;
			if (cable.canRenderConnection(ForgeDirection.DOWN)) {
				Bottom.render(f5);
				renderCentre=true;
			}
			
			if (cable.canRenderConnection(ForgeDirection.UP)) {
				Top.render(f5);
				renderCentre=true;
			}
			if (cable.canRenderConnection(ForgeDirection.NORTH)) {
				Arm1.render(f5);
				renderCentre=true;
			}
			if (cable.canRenderConnection(ForgeDirection.WEST)) {
				Arm2.render(f5);
				renderCentre=true;
			}
			if (cable.canRenderConnection(ForgeDirection.SOUTH)) {
				Arm3.render(f5);
				renderCentre=true;
			}
			if (cable.canRenderConnection(ForgeDirection.EAST)) {
				Arm4.render(f5);
				renderCentre=true;
			}
			if(blockNode&&renderCentre){
				Centre.render(f5);
			}
		}
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	}

}
