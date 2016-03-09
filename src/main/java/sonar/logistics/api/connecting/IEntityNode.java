package sonar.logistics.api.connecting;

import java.util.ArrayList;

import net.minecraft.entity.Entity;

/** implemented by Entity Nodes, provides a list of all the entities they are connected to, normally just one, but can be more */
public interface IEntityNode {

	/** gets the full list of Entities this Entity Node is connected to */
	public ArrayList<Entity> getEntities();
}
