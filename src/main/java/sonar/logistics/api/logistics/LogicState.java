package sonar.logistics.api.logistics;

//this may need expanding at some point
public enum LogicState {
	TRUE, FALSE;

	public boolean getBool() {
		return this == TRUE ? true : false;
	}

	public static LogicState getState(boolean bool) {
		return bool ? TRUE : FALSE;
	}
}
