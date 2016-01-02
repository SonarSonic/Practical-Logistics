package sonar.logistics.client.models;

import org.lwjgl.opengl.GL11;

import sonar.logistics.common.tileentity.TileEntityEntityNode;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelEntityNode extends ModelBase {
	// fields
	ModelRenderer CableBuilder;
	ModelRenderer Base;
	ModelRenderer Spinner1;
	ModelRenderer Spinner2;
	ModelRenderer Spinner3;

	public ModelEntityNode() {
		textureWidth = 64;
		textureHeight = 32;

		CableBuilder = new ModelRenderer(this, 0, 24);
		CableBuilder.addBox(0F, 0F, 0F, 4, 4, 4);
		CableBuilder.setRotationPoint(-2F, 14F, -2F);
		CableBuilder.setTextureSize(64, 32);
		CableBuilder.mirror = true;
		setRotation(CableBuilder, 0F, 0F, 0F);
		Base = new ModelRenderer(this, 0, 16);
		Base.addBox(0F, 0F, 0F, 5, 3, 5);
		Base.setRotationPoint(-2.5F, 11F, -2.5F);
		Base.setTextureSize(64, 32);
		Base.mirror = true;
		setRotation(Base, 0F, 0F, 0F);
		Spinner1 = new ModelRenderer(this, 0, 2);
		Spinner1.addBox(0F, 0F, 0F, 1, 3, 1);
		Spinner1.setRotationPoint(-0.5F, 6F, -0.5F);
		Spinner1.setTextureSize(64, 32);
		Spinner1.mirror = true;
		setRotation(Spinner1, 0F, 0F, 0F);
		Spinner2 = new ModelRenderer(this, 0, 2);
		Spinner2.addBox(0F, 0F, 0F, 1, 3, 1);
		Spinner2.setRotationPoint(-1.5F, 8F, 0.5F);
		Spinner2.setTextureSize(64, 32);
		Spinner2.mirror = true;
		setRotation(Spinner2, 1.570796F, 1.570796F, 0F);
		Spinner3 = new ModelRenderer(this, 0, 2);
		Spinner3.addBox(0F, 0F, 0F, 1, 3, 1);
		Spinner3.setRotationPoint(-0.5F, 8F, -1.5F);
		Spinner3.setTextureSize(64, 32);
		Spinner3.mirror = true;
		setRotation(Spinner3, 1.570796F, 0F, 0F);
	}

	public void renderTile(TileEntityEntityNode tile, Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(entity, f, f1, f2, f3, f4, f5);
		boolean movement = tile.getWorldObj() != null;
		CableBuilder.render(f5);
		Base.render(f5);
		if (movement) {
			GL11.glRotated(tile.rotate*360, 0, 1, 0);
		}
		Spinner1.render(f5);
		Spinner2.render(f5);
		Spinner3.render(f5);
		if (movement) {
			GL11.glRotated(tile.rotate*360, 0, -1, 0);
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
