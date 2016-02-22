package sonar.logistics.api.connecting;

public enum CableType {
	DATA_CABLE,CHANNELLED_CABLE, BLOCK_CONNECTION, NONE;
	
	public boolean canConnect(CableType type){		
		if(this == BLOCK_CONNECTION){
			return true;
		}
		switch(type){
		case NONE:
			return false;
		case BLOCK_CONNECTION:
			return true;
		default:
			return type==this;	
		}	
	}
	public boolean hasUnlimitedConnections(){
		if(this==DATA_CABLE || this==NONE){
			return false;
		}
		return true;
	}
}
