
package sonar.logistics.client.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelInfoCreator extends ModelBase
{
  //fields
    ModelRenderer Corner1;
    ModelRenderer Corner2;
    ModelRenderer Corner3;
    ModelRenderer Corner4;
    ModelRenderer Corner5;
    ModelRenderer Corner6;
    ModelRenderer Corner7;
    ModelRenderer Corner8;
    ModelRenderer Side1;
    ModelRenderer Side2;
    ModelRenderer Side3;
    ModelRenderer Side4;
    ModelRenderer Side5;
  
  public ModelInfoCreator()
  {
    textureWidth = 64;
    textureHeight = 32;
    
      Corner1 = new ModelRenderer(this, 0, 0);
      Corner1.addBox(0F, 0F, 0F, 2, 2, 2);
      Corner1.setRotationPoint(-4.5F, 10.5F, -4.5F);
      Corner1.setTextureSize(64, 32);
      Corner1.mirror = true;
      setRotation(Corner1, 0F, 0F, 0F);
      Corner2 = new ModelRenderer(this, 0, 0);
      Corner2.addBox(0F, 0F, 0F, 2, 2, 2);
      Corner2.setRotationPoint(2.5F, 10.5F, -4.5F);
      Corner2.setTextureSize(64, 32);
      Corner2.mirror = true;
      setRotation(Corner2, 0F, 0F, 0F);
      Corner3 = new ModelRenderer(this, 0, 0);
      Corner3.addBox(0F, 0F, 0F, 2, 2, 2);
      Corner3.setRotationPoint(2.5F, 10.5F, 2.5F);
      Corner3.setTextureSize(64, 32);
      Corner3.mirror = true;
      setRotation(Corner3, 0F, 0F, 0F);
      Corner4 = new ModelRenderer(this, 0, 0);
      Corner4.addBox(0F, 0F, 0F, 2, 2, 2);
      Corner4.setRotationPoint(-4.5F, 10.5F, 2.5F);
      Corner4.setTextureSize(64, 32);
      Corner4.mirror = true;
      setRotation(Corner4, 0F, 0F, 0F);
      Corner5 = new ModelRenderer(this, 0, 0);
      Corner5.addBox(0F, 0F, 0F, 2, 2, 2);
      Corner5.setRotationPoint(-4.5F, 17.5F, -4.5F);
      Corner5.setTextureSize(64, 32);
      Corner5.mirror = true;
      setRotation(Corner5, 0F, 0F, 0F);
      Corner6 = new ModelRenderer(this, 0, 0);
      Corner6.addBox(0F, 0F, 0F, 2, 2, 2);
      Corner6.setRotationPoint(2.5F, 17.5F, -4.5F);
      Corner6.setTextureSize(64, 32);
      Corner6.mirror = true;
      setRotation(Corner6, 0F, 0F, 0F);
      Corner7 = new ModelRenderer(this, 0, 0);
      Corner7.addBox(0F, 0F, 0F, 2, 2, 2);
      Corner7.setRotationPoint(-4.5F, 17.5F, 2.5F);
      Corner7.setTextureSize(64, 32);
      Corner7.mirror = true;
      setRotation(Corner7, 0F, 0F, 0F);
      Corner8 = new ModelRenderer(this, 0, 0);
      Corner8.addBox(0F, 0F, 0F, 2, 2, 2);
      Corner8.setRotationPoint(2.5F, 17.5F, 2.5F);
      Corner8.setTextureSize(64, 32);
      Corner8.mirror = true;
      setRotation(Corner8, 0F, 0F, 0F);
      Side1 = new ModelRenderer(this, 0, 23);
      Side1.addBox(0F, 0F, 0F, 8, 8, 1);
      Side1.setRotationPoint(-4F, 11F, 3F);
      Side1.setTextureSize(64, 32);
      Side1.mirror = true;
      setRotation(Side1, 0F, 0F, 0F);
      Side2 = new ModelRenderer(this, 0, 23);
      Side2.addBox(0F, 0F, 0F, 8, 8, 1);
      Side2.setRotationPoint(3F, 11F, 4F);
      Side2.setTextureSize(64, 32);
      Side2.mirror = true;
      setRotation(Side2, 0F, 1.570796F, 0F);
      Side3 = new ModelRenderer(this, 0, 23);
      Side3.addBox(0F, 0F, 0F, 8, 8, 1);
      Side3.setRotationPoint(-3F, 11F, -4F);
      Side3.setTextureSize(64, 32);
      Side3.mirror = true;
      setRotation(Side3, 0F, -1.570796F, 0F);
      Side4 = new ModelRenderer(this, 0, 23);
      Side4.addBox(0F, 0F, 0F, 8, 8, 1);
      Side4.setRotationPoint(-4F, 12F, -4F);
      Side4.setTextureSize(64, 32);
      Side4.mirror = true;
      setRotation(Side4, 1.570796F, 0F, 0F);
      Side5 = new ModelRenderer(this, 0, 23);
      Side5.addBox(0F, 0F, 0F, 8, 8, 1);
      Side5.setRotationPoint(-4F, 18F, 4F);
      Side5.setTextureSize(64, 32);
      Side5.mirror = true;
      setRotation(Side5, -1.570796F, 0F, 0F);
  }
  
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(entity, f, f1, f2, f3, f4, f5);
    Corner1.render(f5);
    Corner2.render(f5);
    Corner3.render(f5);
    Corner4.render(f5);
    Corner5.render(f5);
    Corner6.render(f5);
    Corner7.render(f5);
    Corner8.render(f5);
    Side1.render(f5);
    Side2.render(f5);
    Side3.render(f5);
    Side4.render(f5);
    Side5.render(f5);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
  
  public void setRotationAngles(Entity entity,float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
  }

}
