/**
 *
 */
package camzup.core;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates classes and methods that are brittle,
 * experimental, works in progress, or not intended for
 * robust public access.
 */
@Retention(SOURCE)
@Target({ TYPE, METHOD })
public @interface Experimental {

}
