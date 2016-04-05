package sonar.logistics.integration.multipart;

import sonar.core.integration.fmp.SonarHandlerPart;
import sonar.core.integration.fmp.SonarTilePart;
import sonar.logistics.integration.multipart.ForgeMultipartHandler.MultiPart;

public abstract class LogisticsPart extends SonarTilePart {

	public abstract MultiPart getPartType();

	public LogisticsPart() {
		super();
	}

	public LogisticsPart(int meta) {
		super(meta);
	}

	@Override
	public final String getType() {
		return getPartType().name;
	}

	public static abstract class Handler extends SonarHandlerPart {

		public abstract MultiPart getPartType();

		public Handler() {
			super();
		}

		public Handler(int meta) {
			super(meta);
		}

		@Override
		public final String getType() {
			return getPartType().name;
		}
	}

}
