package sonar.logistics.api.info;

public interface IInfoContainer {

	// public void updateInfo(InfoUUID id, ByteBuf updateBuf);

	public InfoUUID getInfoUUID(int pos);

	public void setUUID(InfoUUID id, int pos);

	public void renderContainer();

	public int getMaxCapacity();

}
