
package sonar.logistics.client.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelBlockNode extends ModelBase
{
  //fields
    ModelRenderer Base1;
    ModelRenderer Cube;
    ModelRenderer Stem;
    ModelRenderer Base2;
    ModelRenderer Base3;
    ModelRenderer Detector1;
    ModelRenderer Detector2;
    ModelRenderer Detector3;
    ModelRenderer Detector4;
    ModelRenderer Base5;
    ModelRenderer Base4;
  
  public ModelBlockNode()
  {
    textureWidth = 128;
    textureHeight = 64;
    
      Base1 = new ModelRenderer(this, 0, 51);
      Base1.addBox(0F, 0F, 0F, 10, 3, 10);
      Base1.setRotationPoint(-5F, 21F, -5F);
      Base1.setTextureSize(128, 64);
      Base1.mirror = true;
      setRotation(Base1, 0F, 0F, 0F);
      Cube = new ModelRenderer(this, 0, 0);
      Cube.addBox(-3F, -6F, -3F, 6, 6, 6);
      Cube.setRotationPoint(0F, 18F, 0F);
      Cube.setTextureSize(128, 64);
      Cube.mirror = true;
      setRotation(Cube, 0F, 0F, 0F);
      Stem = new ModelRenderer(this, 0, 12);
      Stem.addBox(0F, 0F, 0F, 2, 3, 2);
      Stem.setRotationPoint(-1F, 18F, -1F);
      Stem.setTextureSize(128, 64);
      Stem.mirror = true;
      setRotation(Stem, 0F, 0F, 0F);
      Base2 = new ModelRenderer(this, 72, 37);
      Base2.addBox(0F, 0F, 0F, 2, 1, 26);
      Base2.setRotationPoint(-3F, 22F, -13F);
      Base2.setTextureSize(128, 64);
      Base2.mirror = true;
      setRotation(Base2, 0F, 0F, 0F);
      Base3 = new ModelRenderer(this, 72, 37);
      Base3.addBox(0F, 0F, 0F, 2, 1, 26);
      Base3.setRotationPoint(-13F, 22F, 3F);
      Base3.setTextureSize(128, 64);
      Base3.mirror = true;
      setRotation(Base3, 0F, 1.570796F, 0F);
      Detector1 = new ModelRenderer(this, 0, 17);
      Detector1.addBox(0F, 0F, 0F, 10, 3, 2);
      Detector1.setRotationPoint(-5F, 21F, -15F);
      Detector1.setTextureSize(128, 64);
      Detector1.mirror = true;
      setRotation(Detector1, 0F, 0F, 0F);
      Detector2 = new ModelRenderer(this, 0, 17);
      Detector2.addBox(0F, 0F, 0F, 10, 3, 2);
      Detector2.setRotationPoint(5F, 21F, 15F);
      Detector2.setTextureSize(128, 64);
      Detector2.mirror = true;
      setRotation(Detector2, 0F, -3.141593F, 0F);
      Detector3 = new ModelRenderer(this, 0, 17);
      Detector3.addBox(0F, 0F, 0F, 10, 3, 2);
      Detector3.setRotationPoint(15F, 21F, -5F);
      Detector3.setTextureSize(128, 64);
      Detector3.mirror = true;
      setRotation(Detector3, 0F, -1.570796F, 0F);
      Detector4 = new ModelRenderer(this, 0, 17);
      Detector4.addBox(0F, 0F, 0F, 10, 3, 2);
      Detector4.setRotationPoint(-15F, 21F, 5F);
      Detector4.setTextureSize(128, 64);
      Detector4.mirror = true;
      setRotation(Detector4, 0F, 1.570796F, 0F);
      Base5 = new ModelRenderer(this, 72, 37);
      Base5.addBox(0F, 0F, 0F, 2, 1, 26);
      Base5.setRotationPoint(-13F, 22F, -1F);
      Base5.setTextureSize(128, 64);
      Base5.mirror = true;
      setRotation(Base5, 0F, 1.570796F, 0F);
      Base4 = new ModelRenderer(this, 72, 37);
      Base4.addBox(0F, 0F, 0F, 2, 1, 26);
      Base4.setRotationPoint(1F, 22F, -13F);
      Base4.setTextureSize(128, 64);
      Base4.mirror = true;
      setRotation(Base4, 0F, 0F, 0F);
  }
  
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    Base1.render(f5);
    Cube.render(f5);
    Stem.render(f5);
    Base2.render(f5);
    Base3.render(f5);
    Detector1.render(f5);
    Detector2.render(f5);
    Detector3.render(f5);
    Detector4.render(f5);
    Base5.render(f5);
    Base4.render(f5);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
  
  public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
  {
    super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
  }

}
