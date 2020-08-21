package camzup.core;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates methods that are recursive and may result in Stack Overflow if
 * improperly escaped.
 */
@Retention ( SOURCE )
@Target ( METHOD )
public @interface Recursive {}
