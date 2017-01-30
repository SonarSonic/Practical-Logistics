package sonar.logistics.api.asm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**use this with Monitor Handlers*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EntityMonitorHandler {

	/**specify the MODID required for the handler to load, note if you want it to always load use the Practical Logistics MODID*/
	String modid();

	/**the identification string of the Monitor Handlers*/
	String handlerID();
}
