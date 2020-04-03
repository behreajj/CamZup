package camzup.core;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that an instance method returns itself, such that dot-method
 * accesses can be chained together.
 */
@Retention ( SOURCE )
@Target ( METHOD )
public @interface Chainable {}
