package sonar.logistics.api.connecting;

import mcmultipart.raytrace.RayTraceUtils.AdvancedRayTraceResultPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

/**implemented on blocks which can be affected by an IOperatorTool*/
public interface IOperatorTile {
	
	/**performs an operation on a given block as specified.*/
	public boolean performOperation(AdvancedRayTraceResultPart rayTrace, OperatorMode mode, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ);
}
