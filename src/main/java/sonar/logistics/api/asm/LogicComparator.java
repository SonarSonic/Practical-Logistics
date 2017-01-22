package sonar.logistics.api.asm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import sonar.logistics.api.info.ICustomTileHandler;

/**use this with {@link ICustomTileHandler}*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LogicComparator {

	/**type of class this comparator can compare*/
	Class handlingClass();
}
