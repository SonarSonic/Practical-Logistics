package sonar.logistics.client.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelDirectionalConnector extends ModelBase {
	// fields
	ModelRenderer Connector1;
	ModelRenderer Connector2;
	ModelRenderer Support1;
	ModelRenderer Support2;
	ModelRenderer Support3;
	ModelRenderer Base;
	ModelRenderer Receiver;

	public ModelDirectionalConnector() {
		textureWidth = 64;
		textureHeight = 32;

		Connector1 = new ModelRenderer(this, 40, 0);
		Connector1.addBox(0F, 0F, 0F, 4, 4, 4);
		Connector1.setRotationPoint(-2F, 15F, -8F);
		Connector1.setTextureSize(64, 32);
		Connector1.mirror = true;
		setRotation(Connector1, 0F, 0F, 0F);
		Connector2 = new ModelRenderer(this, 40, 8);
		Connector2.addBox(0F, 0F, 0F, 4, 4, 4);
		Connector2.setRotationPoint(-2F, 19F, 8F);
		Connector2.setTextureSize(64, 32);
		Connector2.mirror = true;
		setRotation(Connector2, -3.141593F, 0F, 0F);
		Support1 = new ModelRenderer(this, 46, 20);
		Support1.addBox(0F, 0F, 0F, 8, 11, 1);
		Support1.setRotationPoint(-4F, 13F, -7F);
		Support1.setTextureSize(64, 32);
		Support1.mirror = true;
		setRotation(Support1, 0F, 0F, 0F);
		Support2 = new ModelRenderer(this, 32, 26);
		Support2.addBox(0F, 0F, 0F, 2, 4, 2);
		Support2.setRotationPoint(-1F, 19F, -1F);
		Support2.setTextureSize(64, 32);
		Support2.mirror = true;
		setRotation(Support2, 0F, 0F, 0F);
		Support3 = new ModelRenderer(this, 46, 20);
		Support3.addBox(0F, 0F, 0F, 8, 11, 1);
		Support3.setRotationPoint(-4F, 13F, 6F);
		Support3.setTextureSize(64, 32);
		Support3.mirror = true;
		setRotation(Support3, 0F, 0F, 0F);
		Base = new ModelRenderer(this, 0, 0);
		Base.addBox(0F, 0F, 0F, 8, 1, 12);
		Base.setRotationPoint(-4F, 23F, -6F);
		Base.setTextureSize(64, 32);
		Base.mirror = true;
		setRotation(Base, 0F, 0F, 0F);
		Receiver = new ModelRenderer(this, 0, 16);
		Receiver.addBox(0F, 0F, 0F, 8, 8, 8);
		Receiver.setRotationPoint(-4F, 13F, -4F);
		Receiver.setTextureSize(64, 32);
		Receiver.mirror = true;
		setRotation(Receiver, 0F, 0F, 0F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(entity, f, f1, f2, f3, f4, f5);
		Connector1.render(f5);
		Connector2.render(f5);
		// Support1.render(f5);
		// Support2.render(f5);
		// Support3.render(f5);
		// Base.render(f5);
		Receiver.render(f5);
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
