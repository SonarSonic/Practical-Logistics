package sonar.logistics.integration.multipart;

import java.util.Collections;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.SonarTilePart;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.client.renderers.RenderHandlers;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.registries.BlockRegistry;
import codechicken.lib.vec.Cuboid6;
import codechicken.multipart.NormallyOccludedPart;
import codechicken.multipart.TMultiPart;

public class DataCablePart extends SonarTilePart implements IDataCable {
	public BlockCoords coords;

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

	public DataCablePart() {
		super();
	}

	public DataCablePart(int meta) {
		super(meta);
	}

	@Override
	public Cuboid6 getBounds() {
		return new Cuboid6((float) 0.0625 * 6, (float) 0.0625 * 6, (float) 0.0625 * 6, (float) (1 - (0.0625 * 6)), (float) (1 - (0.0625 * 6)), (float) (1 - (0.0625 * 6)));

		// return new Cuboid6((float) (this.canRenderConnection(ForgeDirection.WEST) ? 0 : 0.0625 * 6), (float) (this.canRenderConnection(ForgeDirection.DOWN) ? 0 : 0.0625 * 6), (float) (this.canRenderConnection(ForgeDirection.NORTH) ? 0 : 0.0625 * 6), (float) (this.canRenderConnection(ForgeDirection.EAST) ? 1 : (1 - (0.0625 * 6))), (float) (this.canRenderConnection(ForgeDirection.UP) ? 1
		// : (1 - (0.0625 * 6))), (float) (this.canRenderConnection(ForgeDirection.SOUTH) ? 1 : (1 - (0.0625 * 6))));
	}

	@Override
	public Object getSpecialRenderer() {
		return new RenderHandlers.BlockCable();
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.dataCable;
	}

	@Override
	public String getType() {
		return "Cable Part";
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

	public boolean canRenderConnection(ForgeDirection dir) {
		if (this.isBlocked(dir)) {
			return false;
		}
		return CableHelper.canRenderConnection(tile(), dir);
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			if (nbt.hasKey("coords")) {
				if (nbt.getCompoundTag("coords").getBoolean("hasCoords")) {
					coords = BlockCoords.readFromNBT(nbt.getCompoundTag("coords"));
				} else {
					coords = null;
				}
			}
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			NBTTagCompound infoTag = new NBTTagCompound();
			if (coords != null) {
				BlockCoords.writeToNBT(infoTag, coords);
				infoTag.setBoolean("hasCoords", true);
			} else {
				infoTag.setBoolean("hasCoords", false);
			}
			nbt.setTag("coords", infoTag);

		}

	}

	@Override
	public BlockCoords getCoords() {
		return coords;
	}

	@Override
	public void setCoords(BlockCoords coords) {
		if (!BlockCoords.equalCoords(this.coords, coords)) {
			this.coords = coords;
			CableHelper.updateAdjacentCoords(tile(), coords, true);
		}
	}

	@Override
	public void onWorldJoin() {
		super.onWorldJoin();
		CableHelper.updateAdjacentCoords(tile(), coords, true);
	}

	@Override
	public void onNeighborChanged() {
		super.onNeighborChanged();
		CableHelper.updateAdjacentCoords(tile(), coords, true);
	}

	@Override
	public void onWorldSeparate() {
		super.onWorldSeparate();
		CableHelper.updateAdjacentCoords(tile().getWorldObj(), x(), y(), z(), null, true);
	}

	public void onPartChanged(TMultiPart part) {
		this.coords = null;
		CableHelper.removeAllBlockedAdjacentCoords(tile());
	}
}
