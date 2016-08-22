package sonar.logistics.api.asm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import sonar.logistics.api.info.IInfoRegistry;

/**use this with {@link IInfoRegistry}*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface InfoRegistry {

	/**specify the MODID required for the Info Registry to load, note if you want it to always load use the Practical Logistics MODID*/
	String modid();
}