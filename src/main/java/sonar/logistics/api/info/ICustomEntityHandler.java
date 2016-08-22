package sonar.logistics.api.info;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public interface ICustomEntityHandler {

	public boolean canProvideInfo(World world, Entity entity);
	
	public void addInfo(List<LogicInfo> infoList, World world, Entity entity);
}
