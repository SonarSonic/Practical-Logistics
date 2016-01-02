package sonar.logistics.client.models;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelHammer extends ModelBase {
	// fields
	ModelRenderer Surface;
	ModelRenderer BasePart1;
	ModelRenderer BasePart2;
	ModelRenderer BasePart3;
	ModelRenderer BasePart4;
	ModelRenderer Leg1;
	ModelRenderer Leg2;
	ModelRenderer Leg3;
	ModelRenderer Leg4;
	ModelRenderer Pedestal;
	ModelRenderer Beam1;
	ModelRenderer Beam2;
	ModelRenderer Beam3;
	ModelRenderer Beam4;
	ModelRenderer HammerTop;
	ModelRenderer HammerWeight;

	public ModelHammer() {
		textureWidth = 128;
		textureHeight = 64;

		Surface = new ModelRenderer(this, 0, 0);
		Surface.addBox(0F, 0F, 0F, 16, 4, 16);
		Surface.setRotationPoint(-8F, 12F, -8F);
		Surface.setTextureSize(64, 32);
		Surface.mirror = true;
		setRotation(Surface, 0F, 0F, 0F);
		BasePart1 = new ModelRenderer(this, 76, 14);
		BasePart1.addBox(0F, 0F, 0F, 16, 2, 2);
		BasePart1.setRotationPoint(-8F, 22F, 6F);
		BasePart1.setTextureSize(64, 32);
		BasePart1.mirror = true;
		setRotation(BasePart1, 0F, 0F, 0F);
		BasePart2 = new ModelRenderer(this, 76, 14);
		BasePart2.addBox(0F, 0F, 0F, 16, 2, 2);
		BasePart2.setRotationPoint(-8F, 22F, -8F);
		BasePart2.setTextureSize(64, 32);
		BasePart2.mirror = true;
		setRotation(BasePart2, 0F, 0F, 0F);
		BasePart3 = new ModelRenderer(this, 76, 0);
		BasePart3.addBox(0F, 0F, 0F, 2, 2, 12);
		BasePart3.setRotationPoint(-8F, 22F, -6F);
		BasePart3.setTextureSize(64, 32);
		BasePart3.mirror = true;
		setRotation(BasePart3, 0F, 0F, 0F);
		BasePart4 = new ModelRenderer(this, 76, 0);
		BasePart4.addBox(0F, 0F, 0F, 2, 2, 12);
		BasePart4.setRotationPoint(6F, 22F, -6F);
		BasePart4.setTextureSize(64, 32);
		BasePart4.mirror = true;
		setRotation(BasePart4, 0F, 0F, 0F);
		Leg1 = new ModelRenderer(this, 76, 18);
		Leg1.addBox(-2F, -8F, 0F, 2, 8, 2);
		Leg1.setRotationPoint(-6F, 23F, 6F);
		Leg1.setTextureSize(64, 32);
		Leg1.mirror = true;
		setRotation(Leg1, 0.2094395F, 0F, 0.2094395F);
		Leg2 = new ModelRenderer(this, 76, 18);
		Leg2.addBox(-2F, -8F, -2F, 2, 8, 2);
		Leg2.setRotationPoint(-6F, 23F, -6F);
		Leg2.setTextureSize(64, 32);
		Leg2.mirror = true;
		setRotation(Leg2, -0.2094395F, 0F, 0.2094395F);
		Leg3 = new ModelRenderer(this, 76, 18);
		Leg3.addBox(0F, -8F, -2F, 2, 8, 2);
		Leg3.setRotationPoint(6F, 23F, -6F);
		Leg3.setTextureSize(64, 32);
		Leg3.mirror = true;
		setRotation(Leg3, -0.2094395F, 0F, -0.2094395F);
		Leg4 = new ModelRenderer(this, 76, 18);
		Leg4.addBox(0F, -8F, 0F, 2, 8, 2);
		Leg4.setRotationPoint(6F, 23F, 6F);
		Leg4.setTextureSize(64, 32);
		Leg4.mirror = true;
		setRotation(Leg4, 0.2094395F, 0F, -0.2094395F);
		Pedestal = new ModelRenderer(this, 32, 40);
		Pedestal.addBox(0F, 0F, 0F, 8, 2, 8);
		Pedestal.setRotationPoint(-4F, 10F, -4F);
		Pedestal.setTextureSize(64, 32);
		Pedestal.mirror = true;
		setRotation(Pedestal, 0F, 0F, 0F);
		Beam1 = new ModelRenderer(this, 64, 0);
		Beam1.addBox(0F, 0F, 0F, 3, 32, 3);
		Beam1.setRotationPoint(4F, -20F, 4F);
		Beam1.setTextureSize(64, 32);
		Beam1.mirror = true;
		setRotation(Beam1, 0F, 0F, 0F);
		Beam2 = new ModelRenderer(this, 64, 0);
		Beam2.addBox(0F, 0F, 0F, 3, 32, 3);
		Beam2.setRotationPoint(4F, -20F, -7F);
		Beam2.setTextureSize(64, 32);
		Beam2.mirror = true;
		setRotation(Beam2, 0F, 0F, 0F);
		Beam3 = new ModelRenderer(this, 64, 0);
		Beam3.addBox(0F, 0F, 0F, 3, 32, 3);
		Beam3.setRotationPoint(-7F, -20F, 4F);
		Beam3.setTextureSize(64, 32);
		Beam3.mirror = true;
		setRotation(Beam3, 0F, 0F, 0F);
		Beam4 = new ModelRenderer(this, 64, 0);
		Beam4.addBox(0F, 0F, 0F, 3, 32, 3);
		Beam4.setRotationPoint(-7F, -20F, -7F);
		Beam4.setTextureSize(64, 32);
		Beam4.mirror = true;
		setRotation(Beam4, 0F, 0F, 0F);
		HammerTop = new ModelRenderer(this, 0, 20);
		HammerTop.addBox(0F, 0F, 0F, 16, 4, 16);
		HammerTop.setRotationPoint(-8F, -24F, -8F);
		HammerTop.setTextureSize(64, 32);
		HammerTop.mirror = true;
		setRotation(HammerTop, 0F, 0F, 0F);
		HammerWeight = new ModelRenderer(this, 0, 40);
		HammerWeight.addBox(0F, 0F, 0F, 8, 4, 8);
		HammerWeight.setRotationPoint(-4F, -20F, -4F);
		HammerWeight.setTextureSize(64, 32);
		HammerWeight.mirror = true;
		setRotation(HammerWeight, 0F, 0F, 0F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5, boolean world, double move) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(entity, f, f1, f2, f3, f4, f5);
		Surface.render(f5);
		BasePart1.render(f5);
		BasePart2.render(f5);
		BasePart3.render(f5);
		BasePart4.render(f5);
		Leg1.render(f5);
		Leg2.render(f5);
		Leg3.render(f5);
		Leg4.render(f5);
		Pedestal.render(f5);
		Beam1.render(f5);
		Beam2.render(f5);
		Beam3.render(f5);
		Beam4.render(f5);
		HammerTop.render(f5);
		if (!world) {
			HammerWeight.render(f5);
		} else {
			GL11.glTranslated(0, move, 0);
			HammerWeight.render(f5);
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