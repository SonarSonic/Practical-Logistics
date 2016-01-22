package sonar.logistics.client.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelItemRouter extends ModelBase
{
  //fields
    ModelRenderer Base;
    ModelRenderer Top;
    ModelRenderer Support1;
    ModelRenderer Support2;
    ModelRenderer Support3;
    ModelRenderer Support4;
    ModelRenderer Side1;
    ModelRenderer Side2;
    ModelRenderer Side3;
    ModelRenderer Side4;
  
  public ModelItemRouter()
  {
    textureWidth = 128;
    textureHeight = 64;
    
      Base = new ModelRenderer(this, 0, 0);
      Base.addBox(0F, 0F, 0F, 16, 2, 16);
      Base.setRotationPoint(-8F, 24F, 8F);
      Base.setTextureSize(128, 64);
      Base.mirror = true;
      setRotation(Base, 3.141593F, 0F, 0F);
      Top = new ModelRenderer(this, 0, 0);
      Top.addBox(0F, 0F, 0F, 16, 2, 16);
      Top.setRotationPoint(-8F, 8F, -8F);
      Top.setTextureSize(128, 64);
      Top.mirror = true;
      setRotation(Top, 0F, 0F, 0F);
      Support1 = new ModelRenderer(this, 0, 30);
      Support1.addBox(0F, 0F, 0F, 2, 12, 2);
      Support1.setRotationPoint(-8F, 10F, 6F);
      Support1.setTextureSize(128, 64);
      Support1.mirror = true;
      setRotation(Support1, 0F, 0F, 0F);
      Support2 = new ModelRenderer(this, 0, 30);
      Support2.addBox(0F, 0F, 0F, 2, 12, 2);
      Support2.setRotationPoint(6F, 10F, -8F);
      Support2.setTextureSize(128, 64);
      Support2.mirror = true;
      setRotation(Support2, 0F, 0F, 0F);
      Support3 = new ModelRenderer(this, 0, 30);
      Support3.addBox(0F, 0F, 0F, 2, 12, 2);
      Support3.setRotationPoint(6F, 10F, 6F);
      Support3.setTextureSize(128, 64);
      Support3.mirror = true;
      setRotation(Support3, 0F, 0F, 0F);
      Support4 = new ModelRenderer(this, 0, 30);
      Support4.addBox(0F, 0F, 0F, 2, 12, 2);
      Support4.setRotationPoint(-8F, 10F, -8F);
      Support4.setTextureSize(128, 64);
      Support4.mirror = true;
      setRotation(Support4, 0F, 0F, 0F);
      Side1 = new ModelRenderer(this, 0, 18);
      Side1.addBox(0F, 0F, 0F, 10, 10, 2);
      Side1.setRotationPoint(-8F, 11F, 5F);
      Side1.setTextureSize(128, 64);
      Side1.mirror = true;
      setRotation(Side1, 0F, 1.570796F, 0F);
      Side2 = new ModelRenderer(this, 0, 18);
      Side2.addBox(0F, 0F, 0F, 10, 10, 2);
      Side2.setRotationPoint(6F, 11F, 5F);
      Side2.setTextureSize(128, 64);
      Side2.mirror = true;
      setRotation(Side2, 0F, 1.570796F, 0F);
      Side3 = new ModelRenderer(this, 0, 18);
      Side3.addBox(0F, 0F, 0F, 10, 10, 2);
      Side3.setRotationPoint(-5F, 11F, -8F);
      Side3.setTextureSize(128, 64);
      Side3.mirror = true;
      setRotation(Side3, 0F, 0F, 0F);
      Side4 = new ModelRenderer(this, 0, 18);
      Side4.addBox(0F, 0F, 0F, 10, 10, 2);
      Side4.setRotationPoint(-5F, 11F, 6F);
      Side4.setTextureSize(128, 64);
      Side4.mirror = true;
      setRotation(Side4, 0F, 0F, 0F);
  }
  
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(entity, f, f1, f2, f3, f4, f5);
    Base.render(f5);
    Top.render(f5);
    Support1.render(f5);
    Support2.render(f5);
    Support3.render(f5);
    Support4.render(f5);
    Side1.render(f5);
    Side2.render(f5);
    Side3.render(f5);
    Side4.render(f5);
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
