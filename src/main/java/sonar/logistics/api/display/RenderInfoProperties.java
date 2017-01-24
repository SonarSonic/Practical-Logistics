package sonar.logistics.api.display;

import sonar.logistics.api.info.IInfoContainer;

/**the render properties for a {@link IMonitorInfo} when rendered in a {@link InfoContainer}*/
public class RenderInfoProperties {

	public IInfoContainer container;
	public double[] scaling, translation;
	public int infoPos;

	public RenderInfoProperties(IInfoContainer container, int infoPos, double[] scaling, double[] translation) {
		this.container = container;
		this.infoPos = infoPos;
		this.scaling = scaling;
		this.translation = translation;
	}

	public IInfoContainer getContainer() {
		return container;
	}

	public double[] getScaling() {
		return scaling;
	}

	public double[] getTranslation() {
		return translation;
	}

}
