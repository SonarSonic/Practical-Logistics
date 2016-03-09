package sonar.logistics.info.providers.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.providers.EntityProvider;

public class PlayerProvider extends EntityProvider {

	public static String name = "Player-Provider";
	public String[] categories = new String[] { "GENERAL", "ABILITIES", "NUTRITION", "EXPERIENCE" };
	public String[] subcategories = new String[] { "Name", "Team", "Fly Speed", "Walk Speed", "Is Creative Mode", "Needs Food", "Food Level", "Saturation Level", "Absorption Amount", "Experience Bar", "Experience Level", "Total Experience" };

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(Entity entity) {
		return entity != null && entity instanceof EntityPlayer;
	}

	@Override
	public void getHelperInfo(List<ILogicInfo> infoList, Entity entity) {
		int id = this.getID();
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			infoList.add(new LogicInfo(id, 0, 0, player.getGameProfile().getName()).isEntityData(true));
			if (player.getTeam() != null) {
				infoList.add(new LogicInfo(id, 0, 1, player.getTeam().getRegisteredName()));
			}

			infoList.add(new LogicInfo(id, 1, 2, player.capabilities.getFlySpeed()).isEntityData(true));
			infoList.add(new LogicInfo(id, 1, 3, player.capabilities.getWalkSpeed()).isEntityData(true));
			infoList.add(new LogicInfo(id, 1, 4, player.capabilities.isCreativeMode).isEntityData(true));

			// infoList.add(new StandardInfo(id, 2, 0, player.getHealth()));
			// infoList.add(new StandardInfo(id, 2, 0, player.getMaxHealth()));
			infoList.add(new LogicInfo(id, 2, 5, player.getFoodStats().needFood()).isEntityData(true));
			infoList.add(new LogicInfo(id, 2, 6, player.getFoodStats().getFoodLevel()).isEntityData(true));
			infoList.add(new LogicInfo(id, 2, 7, player.getFoodStats().getSaturationLevel()).isEntityData(true));
			infoList.add(new LogicInfo(id, 2, 8, player.getAbsorptionAmount()).isEntityData(true));
			infoList.add(new LogicInfo(id, 3, 9, player.experience).isEntityData(true));
			infoList.add(new LogicInfo(id, 3, 10, player.experienceLevel).isEntityData(true));
			infoList.add(new LogicInfo(id, 3, 11, player.experienceTotal).isEntityData(true));
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
