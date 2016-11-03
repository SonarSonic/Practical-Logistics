package sonar.logistics.api.settings;

import sonar.core.helpers.FontHelper;

public class RedstoneSignaller {

	public enum StatementType {
		DEFAULT, OVERRIDE;

		public String getClientName() {
			return FontHelper.translate("pl.signaller. " + name().toLowerCase());
		}
	}

	public enum StatementSetting {
		ALL, ONE;

		public String getClientName() {
			return FontHelper.translate("pl.signaller. " + name().toLowerCase());
		}
	}

}
