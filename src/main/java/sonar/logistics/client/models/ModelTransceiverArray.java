package sonar.logistics.client.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelTransceiverArray extends ModelBase
{
    ModelRenderer Slots;
    ModelRenderer Mount;
  
  public ModelTransceiverArray()
  {
    textureWidth = 64;
    textureHeight = 32;
    
      Slots = new ModelRenderer(this, 0, 12);
      Slots.addBox(0F, 0F, 0F, 6, 1, 6);
      Slots.setRotationPoint(-3F, 13F, -3F);
      Slots.setTextureSize(64, 32);
      Slots.mirror = true;
      setRotation(Slots, 0F, 0F, 0F);
      Mount = new ModelRenderer(this, 0, 0);
      Mount.addBox(0F, 0F, 0F, 10, 2, 10);
      Mount.setRotationPoint(-5F, 11F, -5F);
      Mount.setTextureSize(64, 32);
      Mount.mirror = true;
      setRotation(Mount, 0F, 0F, 0F);
  }
  
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    Slots.render(f5);
    Mount.render(f5);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }

}
