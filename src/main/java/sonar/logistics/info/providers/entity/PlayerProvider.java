package sonar.logistics.info.providers.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import sonar.logistics.api.EntityInfo;
import sonar.logistics.api.Info;
import sonar.logistics.api.providers.EntityProvider;

public class PlayerProvider extends EntityProvider {

	public static String name = "Player-Provider";
	public String[] categories = new String[] { "GENERAL", "ABILITIES", "NUTRITION", "EXPERIENCE"};
	public String[] subcategories = new String[] {"Name", "Team","Fly Speed","Walk Speed","Is Creative Mode", "Needs Food", "Food Level", "Saturation Level", "Absorption Amount", "Experience Bar", "Experience Level", "Total Experience"};

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(Entity entity) {
		return entity != null && entity instanceof EntityPlayer;
	}

	@Override
	public void getHelperInfo(List<Info> infoList, Entity entity) {
		byte id = this.getID();
		if(entity instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer) entity;
			infoList.add(new EntityInfo(id, 0, 0, player.getGameProfile().getName()));
			if(player.getTeam()!=null){
				infoList.add(new EntityInfo(id, 0, 1, player.getTeam().getRegisteredName()));
			}
			
			infoList.add(new EntityInfo(id, 1, 2, player.capabilities.getFlySpeed()));
			infoList.add(new EntityInfo(id, 1, 3, player.capabilities.getWalkSpeed()));
			infoList.add(new EntityInfo(id, 1, 4, player.capabilities.isCreativeMode));
			
			//infoList.add(new StandardInfo(id, 2, 0, player.getHealth()));
			//infoList.add(new StandardInfo(id, 2, 0, player.getMaxHealth()));
			infoList.add(new EntityInfo(id, 2, 5, player.getFoodStats().needFood()));
			infoList.add(new EntityInfo(id, 2, 6, player.getFoodStats().getFoodLevel()));
			infoList.add(new EntityInfo(id, 2, 7, player.getFoodStats().getSaturationLevel()));
			infoList.add(new EntityInfo(id, 2, 8, player.getAbsorptionAmount()));
			infoList.add(new EntityInfo(id, 3, 9, player.experience));
			infoList.add(new EntityInfo(id, 3, 10, player.experienceLevel));
			infoList.add(new EntityInfo(id, 3, 11, player.experienceTotal));
		}
	}

	@Override
	public String getCategory(byte id) {
		return categories[id];
	}

	@Override
	public String getSubCategory(byte id) {
		return subcategories[id];
	}

}
