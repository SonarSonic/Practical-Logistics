package sonar.logistics.api.info;

public abstract class IInfoRegistry {

	/**register all the possible returns of the base methods which are not primitive types*/
	public void registerBaseReturns(){}

	/**register all the possible base methods including the ones of base returns to ensure only primitive types are returns*/
	public void registerBaseMethods(){}
	
	/**register any IInventory fields which can be read here*/
	public void registerAllFields(){}
	
	public void registerAdjustments(){}
	
}
