package sonar.logistics.api.display;

/**implemented on {@link IMonitorInfo} which can have adjustments added to the info*/
public interface ISuffixable {

	/**gets the suffix for the info*/
	public String getSuffix();

	/**gets the prefix for the info*/
	public String getPrefix();

	/**gets the raw unedited data from the info*/
	public String getRawData();

}
