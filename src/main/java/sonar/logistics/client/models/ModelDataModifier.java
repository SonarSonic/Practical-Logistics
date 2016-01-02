
package sonar.logistics.client.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelDataModifier extends ModelBase
{
  //fields
    ModelRenderer Input;
    ModelRenderer Bar1;
    ModelRenderer Bar2;
    ModelRenderer Bar3;
    ModelRenderer Bar4;
    ModelRenderer Bar5;
    ModelRenderer Bar6;
    ModelRenderer Bar7;
    ModelRenderer Bar8;
    ModelRenderer Corner1;
    ModelRenderer Corner2;
    ModelRenderer Corner3;
    ModelRenderer Corner4;
    ModelRenderer Corner5;
    ModelRenderer Corner6;
    ModelRenderer Corner7;
    ModelRenderer Corner8;
  
  public ModelDataModifier()
  {
    textureWidth = 64;
    textureHeight = 32;
    
      Input = new ModelRenderer(this, 0, 23);
      Input.addBox(0F, 0F, 0F, 8, 8, 1);
      Input.setRotationPoint(-4F, 11F, 3F);
      Input.setTextureSize(64, 32);
      Input.mirror = true;
      setRotation(Input, 0F, 0F, 0F);
      Bar1 = new ModelRenderer(this, 0, 4);
      Bar1.addBox(0F, 0F, 0F, 1, 6, 1);
      Bar1.setRotationPoint(-4F, 12F, -3F);
      Bar1.setTextureSize(64, 32);
      Bar1.mirror = true;
      setRotation(Bar1, 1.570796F, 0F, 0F);
      Bar2 = new ModelRenderer(this, 0, 4);
      Bar2.addBox(0F, 0F, 0F, 1, 6, 1);
      Bar2.setRotationPoint(3F, 12F, -3F);
      Bar2.setTextureSize(64, 32);
      Bar2.mirror = true;
      setRotation(Bar2, 1.570796F, 0F, 0F);
      Bar3 = new ModelRenderer(this, 0, 4);
      Bar3.addBox(0F, 0F, 0F, 1, 6, 1);
      Bar3.setRotationPoint(3F, 19F, -3F);
      Bar3.setTextureSize(64, 32);
      Bar3.mirror = true;
      setRotation(Bar3, 1.570796F, 0F, 0F);
      Bar4 = new ModelRenderer(this, 0, 4);
      Bar4.addBox(0F, 0F, 0F, 1, 6, 1);
      Bar4.setRotationPoint(-4F, 19F, -3F);
      Bar4.setTextureSize(64, 32);
      Bar4.mirror = true;
      setRotation(Bar4, 1.570796F, 0F, 0F);
      Bar5 = new ModelRenderer(this, 0, 4);
      Bar5.addBox(0F, 0F, 0F, 1, 6, 1);
      Bar5.setRotationPoint(3F, 12F, -4F);
      Bar5.setTextureSize(64, 32);
      Bar5.mirror = true;
      setRotation(Bar5, 0F, 0F, 0F);
      Bar6 = new ModelRenderer(this, 0, 4);
      Bar6.addBox(0F, 0F, 0F, 1, 6, 1);
      Bar6.setRotationPoint(-4F, 12F, -4F);
      Bar6.setTextureSize(64, 32);
      Bar6.mirror = true;
      setRotation(Bar6, 0F, 0F, 0F);
      Bar7 = new ModelRenderer(this, 0, 4);
      Bar7.addBox(0F, 0F, 0F, 1, 6, 1);
      Bar7.setRotationPoint(3F, 11F, -4F);
      Bar7.setTextureSize(64, 32);
      Bar7.mirror = true;
      setRotation(Bar7, 0F, 0F, 1.570796F);
      Bar8 = new ModelRenderer(this, 0, 4);
      Bar8.addBox(0F, 0F, 0F, 1, 6, 1);
      Bar8.setRotationPoint(3F, 18F, -4F);
      Bar8.setTextureSize(64, 32);
      Bar8.mirror = true;
      setRotation(Bar8, 0F, 0F, 1.570796F);
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
  }
  
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(entity, f, f1, f2, f3, f4, f5);
    Input.render(f5);
    Bar1.render(f5);
    Bar2.render(f5);
    Bar3.render(f5);
    Bar4.render(f5);
    Bar5.render(f5);
    Bar6.render(f5);
    Bar7.render(f5);
    Bar8.render(f5);
    Corner1.render(f5);
    Corner2.render(f5);
    Corner3.render(f5);
    Corner4.render(f5);
    Corner5.render(f5);
    Corner6.render(f5);
    Corner7.render(f5);
    Corner8.render(f5);
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
