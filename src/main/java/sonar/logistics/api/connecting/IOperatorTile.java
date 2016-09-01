package sonar.logistics.api.connecting;

import mcmultipart.raytrace.RayTraceUtils.AdvancedRayTraceResultPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

/** implemented on blocks which can be affected by an IOperatorTool */
public interface IOperatorTile {

	/** performs an operation on a tile when it is clicked by the {@link IOperatorTool} *
	 * @param rayTrace the raytrace which obtained the current tile, can be used for further detection
	 * @param mode the mode of {@link IOperatorTool} which clicked the tile
	 * @param player the EntityPlayer using the {@link IOperatorTool}
	 * @param hand the players hand
	 * @param facing the side clicked
	 * @param hitX the x hit
	 * @param hitY the y hit
	 * @param hitZ the z hit
	 * @return if an operation was performed */
	public boolean performOperation(AdvancedRayTraceResultPart rayTrace, OperatorMode mode, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ);
}
