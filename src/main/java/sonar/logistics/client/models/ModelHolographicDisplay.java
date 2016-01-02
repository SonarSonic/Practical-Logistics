
package sonar.logistics.client.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelHolographicDisplay extends ModelBase
{
  //fields
    ModelRenderer HologramBar;
    ModelRenderer CableConnection;
  
  public ModelHolographicDisplay()
  {
    textureWidth = 64;
    textureHeight = 32;
    
      HologramBar = new ModelRenderer(this, 0, 0);
      HologramBar.addBox(0F, 0F, 0F, 16, 1, 2);
      HologramBar.setRotationPoint(-8F, 13F, 6F);
      HologramBar.setTextureSize(64, 32);
      HologramBar.mirror = true;
      setRotation(HologramBar, 0F, 0F, 0F);
      CableConnection = new ModelRenderer(this, 0, 3);
      CableConnection.addBox(0F, 0F, 0F, 4, 4, 1);
      CableConnection.setRotationPoint(-2F, 14F, 7F);
      CableConnection.setTextureSize(64, 32);
      CableConnection.mirror = true;
      setRotation(CableConnection, 0F, 0F, 0F);
  }
  
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(entity, f, f1, f2, f3, f4, f5);
    HologramBar.render(f5);
    CableConnection.render(f5);
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
