package sonar.logistics.api.display;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import sonar.core.helpers.SonarHelper;

public enum DisplayConnections implements IStringSerializable {
	NONE, //
	ONE_N(EnumFacing.NORTH), //
	ONE_E(EnumFacing.EAST), //
	ONE_S(EnumFacing.SOUTH), //
	ONE_W(EnumFacing.WEST), //
	TWO_N(EnumFacing.NORTH, EnumFacing.EAST), //
	TWO_E(EnumFacing.EAST, EnumFacing.SOUTH), //
	TWO_S(EnumFacing.SOUTH, EnumFacing.WEST), //
	TWO_W(EnumFacing.WEST, EnumFacing.NORTH), //
	OPPOSITE_1(EnumFacing.NORTH, EnumFacing.SOUTH), //
	OPPOSITE_2(EnumFacing.EAST, EnumFacing.WEST), //
	THREE_N(EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST), //
	THREE_E(EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST), //
	THREE_S(EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST), //
	THREE_W(EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH), //
	ALL(EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST);//

	public static final HashMap<Integer, ArrayList<DisplayConnections>> connections = new HashMap();
	public static final DisplayConnections[] VALUES = new DisplayConnections[] { NONE, ONE_N, ONE_E, ONE_S, ONE_W, TWO_N, TWO_E, TWO_S, TWO_W, OPPOSITE_1, OPPOSITE_2, THREE_N, THREE_E, THREE_S, THREE_W, ALL };

	static {
		for (DisplayConnections connect : DisplayConnections.VALUES) {

			int size = connect.getFaces().size();
			ArrayList list = connections.get(size);
			if (list == null) {
				connections.put(size, new ArrayList());
				list = connections.get(size);
			}
			list.add(connect);

		}
	}

	List<EnumFacing> faces;

	DisplayConnections(EnumFacing... faces) {
		this.faces = SonarHelper.<EnumFacing>convertArray(faces);
	}

	public List<EnumFacing> getFaces() {
		return faces;
	}

	public String getName() {
		return this.toString().toLowerCase();
	}

	public static DisplayConnections getType(List<EnumFacing> faces) {
		if (faces.size() == 4) {
			return DisplayConnections.ALL;
		}
		for (DisplayConnections type : connections.get(faces.size())) {
			if ((type.getFaces().containsAll(faces))) {
				return type;
			}
		}
		return DisplayConnections.NONE;
	}
}