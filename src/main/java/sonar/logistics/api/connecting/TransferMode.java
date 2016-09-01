package sonar.logistics.api.connecting;

import net.minecraft.util.IStringSerializable;

public enum TransferMode implements IStringSerializable {

	PULL, PUSH, PASSIVE;

	public boolean shouldPull(){
		return this == PULL;
	}

	public boolean shouldPush(){
		return this == PUSH;
	}

	public boolean isPassive(){
		return this == PASSIVE;
	}
	
	@Override
	public String getName() {
		return name().toLowerCase();
	}

}
