package sonar.logistics.info.providers.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import sonar.logistics.api.EntityInfo;
import sonar.logistics.api.Info;
import sonar.logistics.api.providers.EntityProvider;

public class NormalEntityProvider extends EntityProvider {

	public static String name = "Normal-Entity-Provider";
	public String[] categories = new String[] { "GENERAL" };
	public String[] subcategories = new String[] { "Type", "Age", "Health", "Max Health", "Hostile Mob", "Growing Age" };

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(Entity entity) {
		return entity != null && entity instanceof EntityLivingBase;
	}

	@Override
	public void getHelperInfo(List<Info> infoList, Entity entity) {
		int id = this.getID();
		if (entity instanceof EntityLivingBase) {
			EntityLivingBase living = (EntityLivingBase) entity;
			if (!(entity instanceof EntityPlayer)) {
				String name = EntityList.getEntityString(entity);
				if (name != null) {
					infoList.add(new EntityInfo(id, 0, 0, name));
				}
			}
			infoList.add(new EntityInfo(id, 0, 1, living.getAge()));

			infoList.add(new EntityInfo(id, 0, 2, (int) living.getHealth()).addSuffix("HP"));
			infoList.add(new EntityInfo(id, 0, 3, (int) living.getMaxHealth()).addSuffix("HP"));
			if (entity instanceof EntityMob) {
				infoList.add(new EntityInfo(id, 0, 4, true));
			} else {
				infoList.add(new EntityInfo(id, 0, 4, false));
			}
		}
		if (entity instanceof EntityAgeable) {
			EntityAgeable living = (EntityAgeable) entity;
			infoList.add(new EntityInfo(id, 0, 5, living.getGrowingAge()));
		}

	}

	@Override
	public String getCategory(int id) {
		return categories[id];
	}

	@Override
	public String getSubCategory(int id) {
		return subcategories[id];
	}

}
