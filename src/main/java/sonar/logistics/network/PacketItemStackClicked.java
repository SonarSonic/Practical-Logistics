package sonar.logistics.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import sonar.core.network.PacketMultipart;
import sonar.logistics.connections.monitoring.MonitoredItemStack;

public class PacketItemStackClicked extends PacketMultipart {

	public int registryID;

	public PacketItemStackClicked(UUID partUUID, BlockPos pos, MonitoredItemStack stack, int registryID) {
		super(partUUID, pos);
		this.registryID = registryID;
	}

	@Override
	public void fromBytes(ByteBuf buf) {

	}

	@Override
	public void toBytes(ByteBuf buf) {

	}

}
