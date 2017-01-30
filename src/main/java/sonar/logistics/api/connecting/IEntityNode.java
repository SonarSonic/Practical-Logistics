package sonar.logistics.api.connecting;

import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.BlockCoords;

/** implemented by Entity Nodes, provides a list of all the entities they are connected to, normally just one, but can be more */
public interface IEntityNode {

	public void addEntities(List<Entity> entities);
}
