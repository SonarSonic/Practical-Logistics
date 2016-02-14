package sonar.logistics.client.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

public class ModelClock extends ModelBase {
	// fields
	ModelRenderer Base;
	ModelRenderer DataSupport;
	ModelRenderer DataOutput;
	ModelRenderer Centre1;
	ModelRenderer Centre2;
	ModelRenderer ClockHand;
	ModelRenderer Centre4;
	ModelRenderer Centre5;
	ModelRenderer RedstoneOutput;
	ModelRenderer RedstoneSupport;
	ModelRenderer Notch1;
	ModelRenderer Notch2;
	ModelRenderer Notch3;
	ModelRenderer Notch4;

	public ModelClock() {
		textureWidth = 128;
		textureHeight = 64;

		Base = new ModelRenderer(this, 0, 47);
		Base.addBox(0F, 0F, 0F, 16, 1, 16);
		Base.setRotationPoint(-8F, 23F, -8F);
		Base.setTextureSize(64, 32);
		Base.mirror = true;
		setRotation(Base, 0F, 0F, 0F);
		DataSupport = new ModelRenderer(this, 0, 0);
		DataSupport.addBox(0F, 0F, 0F, 2, 5, 3);
		DataSupport.setRotationPoint(-1F, 18F, -8F);
		DataSupport.setTextureSize(64, 32);
		DataSupport.mirror = true;
		setRotation(DataSupport, 0F, 0F, 0F);
		DataOutput = new ModelRenderer(this, 18, 18);
		DataOutput.addBox(0F, 0F, 0F, 4, 4, 1);
		DataOutput.setRotationPoint(-2F, 14F, -8F);
		DataOutput.setTextureSize(64, 32);
		DataOutput.mirror = true;
		setRotation(DataOutput, 0F, 0F, 0F);
		Centre1 = new ModelRenderer(this, 0, 14);
		Centre1.addBox(0F, 0F, 0F, 2, 4, 2);
		Centre1.setRotationPoint(-1F, 13F, -1F);
		Centre1.setTextureSize(64, 32);
		Centre1.mirror = true;
		setRotation(Centre1, 0F, 0F, 0F);
		Centre2 = new ModelRenderer(this, 0, 32);
		Centre2.addBox(0F, 0F, 0F, 10, 5, 10);
		Centre2.setRotationPoint(-5F, 18F, -5F);
		Centre2.setTextureSize(64, 32);
		Centre2.mirror = true;
		setRotation(Centre2, 0F, 0F, 0F);
		ClockHand = new ModelRenderer(this, 32, 0);
		ClockHand.addBox(-0.5F, 0F, -0.5F, 1, 1, 7);
		ClockHand.setRotationPoint(0F, 14.5F, 0F);
		ClockHand.setTextureSize(64, 32);
		ClockHand.mirror = true;
		setRotation(ClockHand, 0F, 0F, 0F);
		Centre4 = new ModelRenderer(this, 0, 23);
		Centre4.addBox(0F, 0F, 0F, 8, 1, 8);
		Centre4.setRotationPoint(-4F, 17F, -4F);
		Centre4.setTextureSize(64, 32);
		Centre4.mirror = true;
		setRotation(Centre4, 0F, 0F, 0F);
		Centre5 = new ModelRenderer(this, 0, 9);
		Centre5.addBox(0F, 0F, 0F, 3, 2, 3);
		Centre5.setRotationPoint(-1.5F, 14F, -1.5F);
		Centre5.setTextureSize(64, 32);
		Centre5.mirror = true;
		setRotation(Centre5, 0F, 0F, 0F);
		RedstoneOutput = new ModelRenderer(this, 8, 18);
		RedstoneOutput.addBox(0F, 0F, 0F, 4, 4, 1);
		RedstoneOutput.setRotationPoint(-2F, 14F, 7F);
		RedstoneOutput.setTextureSize(64, 32);
		RedstoneOutput.mirror = true;
		setRotation(RedstoneOutput, 0F, 0F, 0F);
		RedstoneSupport = new ModelRenderer(this, 0, 0);
		RedstoneSupport.addBox(0F, 0F, 0F, 2, 5, 3);
		RedstoneSupport.setRotationPoint(-1F, 18F, 5F);
		RedstoneSupport.setTextureSize(64, 32);
		RedstoneSupport.mirror = true;
		setRotation(RedstoneSupport, 0F, 0F, 0F);
		Notch1 = new ModelRenderer(this, 0, 0);
		Notch1.addBox(0F, 0F, 0F, 2, 1, 1);
		Notch1.setRotationPoint(-4F, 22F, 5F);
		Notch1.setTextureSize(64, 32);
		Notch1.mirror = true;
		setRotation(Notch1, 0F, 0F, 0F);
		Notch2 = new ModelRenderer(this, 0, 0);
		Notch2.addBox(0F, 0F, 0F, 2, 1, 1);
		Notch2.setRotationPoint(-4F, 22F, -6F);
		Notch2.setTextureSize(64, 32);
		Notch2.mirror = true;
		setRotation(Notch2, 0F, 0F, 0F);
		Notch3 = new ModelRenderer(this, 0, 0);
		Notch3.addBox(0F, 0F, 0F, 2, 1, 1);
		Notch3.setRotationPoint(2F, 22F, -6F);
		Notch3.setTextureSize(64, 32);
		Notch3.mirror = true;
		setRotation(Notch3, 0F, 0F, 0F);
		Notch4 = new ModelRenderer(this, 0, 0);
		Notch4.addBox(0F, 0F, 0F, 2, 1, 1);
		Notch4.setRotationPoint(2F, 22F, 5F);
		Notch4.setTextureSize(64, 32);
		Notch4.mirror = true;
		setRotation(Notch4, 0F, 0F, 0F);
	}

	public void render(TileEntity entity, float f, float f1, float f2, float f3, float f4, float f5, float rotation) {
		super.render(null, f, f1, f2, f3, f4, f5);
		setRotationAngles(null, f, f1, f2, f3, f4, f5);
		Base.render(f5);
		DataSupport.render(f5);
		DataOutput.render(f5);
		Centre1.render(f5);
		Centre2.render(f5);
		Centre4.render(f5);
		Centre5.render(f5);
		RedstoneOutput.render(f5);
		RedstoneSupport.render(f5);
		Notch1.render(f5);
		Notch2.render(f5);
		Notch3.render(f5);
		Notch4.render(f5);
		if (entity.getWorldObj() != null) {
			GL11.glRotated(rotation, 0, 1, 0);
		}
		ClockHand.render(f5);
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
