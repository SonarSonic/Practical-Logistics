package sonar.logistics.integration.multipart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.SonarTilePart;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.connecting.CableType;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.connecting.ILogicTile;
import sonar.logistics.client.renderers.RenderHandlers;
import sonar.logistics.registries.BlockRegistry;
import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.NormallyOccludedPart;
import codechicken.multipart.TMultiPart;

public class ChannelledCablePart extends SonarTilePart implements IDataCable {

	public int registryID = -1;

	public boolean occlusion;

	// taken from Applied Energistics Code
	final double SHORTER = 6.0 / 16.0;
	final double LONGER = 10.0 / 16.0;
	final double MIN_DIRECTION = 0;
	final double MAX_DIRECTION = 1.0;
	final Cuboid6[] SIDE_TESTS = {

			// DOWN(0, -1, 0),
			new Cuboid6(SHORTER, MIN_DIRECTION, SHORTER, LONGER, SHORTER, LONGER),

			// UP(0, 1, 0),
			new Cuboid6(SHORTER, LONGER, SHORTER, LONGER, MAX_DIRECTION, LONGER),

			// NORTH(0, 0, -1),
			new Cuboid6(SHORTER, SHORTER, MIN_DIRECTION, LONGER, LONGER, SHORTER),

			// SOUTH(0, 0, 1),
			new Cuboid6(SHORTER, SHORTER, LONGER, LONGER, LONGER, MAX_DIRECTION),

			// WEST(-1, 0, 0),
			new Cuboid6(MIN_DIRECTION, SHORTER, SHORTER, SHORTER, LONGER, LONGER),

			// EAST(1, 0, 0),
			new Cuboid6(LONGER, SHORTER, SHORTER, MAX_DIRECTION, LONGER, LONGER), };

	public ChannelledCablePart() {
		super();
	}

	public ChannelledCablePart(int meta) {
		super(meta);
	}

	@Override
	public Cuboid6 getBounds() {
		return new Cuboid6((float) 0.0625 * 6, (float) 0.0625 * 6, (float) 0.0625 * 6, (float) (1 - (0.0625 * 6)), (float) (1 - (0.0625 * 6)), (float) (1 - (0.0625 * 6)));
	}

	@Override
	public Object getSpecialRenderer() {
		return new RenderHandlers.ChannelledCable();
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.channelledCable;
	}

	@Override
	public String getType() {
		return "Multi Cable Part";
	}

	@Override
	public Iterable<Cuboid6> getOcclusionBoxes() {
		if (this.occlusion) {
			return Collections.emptyList();
		}
		return super.getOcclusionBoxes();

	}

	@Override
	public boolean isBlocked(final ForgeDirection side) {
		if (side == null || side == ForgeDirection.UNKNOWN || this.tile() == null) {
			return false;
		}
		occlusion = true;
		boolean blocked = !this.tile().canAddPart(new NormallyOccludedPart(SIDE_TESTS[side.ordinal()]));
		occlusion = false;

		return blocked;
	}

	public CableType canRenderConnection(ForgeDirection dir) {
		if (this.isBlocked(dir)) {
			return CableType.NONE;
		}
		return LogisticsAPI.getCableHelper().canRenderConnection(tile(), dir, CableType.CHANNELLED_CABLE);
	}

	@Override
	public BlockCoords getCoords() {
		return new BlockCoords(tile());
	}

	@Override
	public void onWorldJoin() {
		super.onWorldJoin();
		LogisticsAPI.getCableHelper().addCable(this);
	}

	@Override
	public void onWorldSeparate() {
		super.onWorldSeparate();
		LogisticsAPI.getCableHelper().removeCable(this);
	}

	public void onPartChanged(TMultiPart part) {
		super.onPartChanged(part);
		List<ILogicTile> adjacents = new ArrayList();
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			BlockCoords coords = new BlockCoords(tile());
			Object object = FMPHelper.checkObject(BlockCoords.translateCoords(coords, dir).getTileEntity());
			if (object != null && object instanceof ILogicTile) {
				adjacents.add((ILogicTile) object);
			}
		}

		for (ILogicTile tile : adjacents) {
			if (tile instanceof IDataCable) {
				IDataCable cable = (IDataCable) tile;
				cable.removeCable();
				cable.addCable();
			}
			if (tile instanceof IInfoEmitter) {
				IInfoEmitter emitter = (IInfoEmitter) tile;
				emitter.removeConnections();
				emitter.addConnections();
			}
		}
		this.removeCable();
		this.addCable();
	}

	@Override
	public int registryID() {
		return registryID;
	}

	@Override
	public void setRegistryID(int id) {
		this.registryID = id;
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return true;
	}

	@Override
	public CableType getCableType() {
		return CableType.CHANNELLED_CABLE;
	}

	@Override
	public void addCable() {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeCable() {
		// TODO Auto-generated method stub

	}

}
