package sonar.logistics.client.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelRedstoneSignaller extends ModelBase
{
    ModelRenderer Connector1;
    ModelRenderer Support1;
    ModelRenderer Torch;
    ModelRenderer Base;
    ModelRenderer Grip1;
    ModelRenderer Grip2;
  
  public ModelRedstoneSignaller()
  {
    textureWidth = 64;
    textureHeight = 32;
    
      Connector1 = new ModelRenderer(this, 40, 0);
      Connector1.addBox(0F, 0F, 0F, 4, 4, 4);
      Connector1.setRotationPoint(-2F, 15F, -8F);
      Connector1.setTextureSize(64, 32);
      Connector1.mirror = true;
      setRotation(Connector1, 0F, 0F, 0F);
      Support1 = new ModelRenderer(this, 46, 20);
      Support1.addBox(0F, 0F, 0F, 8, 11, 1);
      Support1.setRotationPoint(-4F, 13F, -7F);
      Support1.setTextureSize(64, 32);
      Support1.mirror = true;
      setRotation(Support1, 0F, 0F, 0F);
      Torch = new ModelRenderer(this, 0, 20);
      Torch.addBox(0F, 0F, 0F, 2, 10, 2);
      Torch.setRotationPoint(-1F, 13F, -1F);
      Torch.setTextureSize(64, 32);
      Torch.mirror = true;
      setRotation(Torch, 0F, 0F, 0F);
      Base = new ModelRenderer(this, 0, 0);
      Base.addBox(0F, 0F, 0F, 8, 1, 10);
      Base.setRotationPoint(-4F, 23F, -6F);
      Base.setTextureSize(64, 32);
      Base.mirror = true;
      setRotation(Base, 0F, 0F, 0F);
      Grip1 = new ModelRenderer(this, 8, 27);
      Grip1.addBox(0F, 0F, 0F, 3, 2, 3);
      Grip1.setRotationPoint(-1.5F, 16F, -1.5F);
      Grip1.setTextureSize(64, 32);
      Grip1.mirror = true;
      setRotation(Grip1, 0F, 0F, 0F);
      Grip2 = new ModelRenderer(this, 8, 23);
      Grip2.addBox(0F, 0F, 0F, 1, 1, 3);
      Grip2.setRotationPoint(-0.5F, 16.5F, -4F);
      Grip2.setTextureSize(64, 32);
      Grip2.mirror = true;
      setRotation(Grip2, 0F, 0F, 0F);
  }
  
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(entity, f, f1, f2, f3, f4, f5);
    Connector1.render(f5);
    Support1.render(f5);
    Torch.render(f5);
    Base.render(f5);
    Grip1.render(f5);
    Grip2.render(f5);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
  
  public void setRotationAngles(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
  }

}
