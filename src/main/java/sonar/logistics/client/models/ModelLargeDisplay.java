package sonar.logistics.client.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelLargeDisplay extends ModelBase {
	ModelRenderer screen;

	public ModelLargeDisplay() {
		textureWidth = 64;
		textureHeight = 32;

		screen = new ModelRenderer(this, 0, 0);
		screen.addBox(0F, 0F, 0F, 16, 16, 1);
		screen.setRotationPoint(-8F, 8F, 7F);
		screen.setTextureSize(64, 32);
		screen.mirror = true;
		setRotation(screen, 0F, 0F, 0F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(entity, f, f1, f2, f3, f4, f5);
		screen.render(f5);
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
