package sonar.logistics.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.logistics.Logistics;
import sonar.logistics.api.display.ConnectedDisplayScreen;

public class PacketConnectedDisplayScreen implements IMessage {

	public ByteBuf savedBuf;
	public ConnectedDisplayScreen screen;
	public int registryID;

	public PacketConnectedDisplayScreen() {}

	public PacketConnectedDisplayScreen(ConnectedDisplayScreen screen, int registryID) {
		super();
		this.screen = screen;
		this.registryID = registryID;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		registryID = buf.readInt();
		screen = Logistics.getClientManager().connectedDisplays.get(registryID);
		savedBuf = buf;
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(registryID);
		screen.writeToBuf(buf);
	}

	public static class Handler implements IMessageHandler<PacketConnectedDisplayScreen,IMessage> {
		
		@Override
		public IMessage onMessage(PacketConnectedDisplayScreen message, MessageContext ctx) {			
			if (message.screen == null) {
				message.screen = new ConnectedDisplayScreen(message.registryID);
			}
			message.screen.readFromBuf(message.savedBuf);
			Logistics.getClientManager().connectedDisplays.put(message.registryID, message.screen);			
			message.screen.getTopLeftScreen().setConnectedDisplay(message.screen);
			return null;
		}
	}
}