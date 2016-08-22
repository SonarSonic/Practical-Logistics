package sonar.logistics.api.asm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import sonar.logistics.api.info.ICustomEntityHandler;

/**use this with {@link ICustomEntityHandler}*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CustomEntityHandler {

	/**specify the MODID required for the handler to load, note if you want it to always load use the Practical Logistics MODID*/
	String modid();

	/**the identification string of the EntityHandler*/
	String handlerID();
}
