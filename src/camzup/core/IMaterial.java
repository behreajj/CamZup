package camzup.core;

/**
 * Maintains consistent behavior between different kind of
 * materials, which store color and other display style
 * information.
 */
public interface IMaterial extends IUtils {

   /**
    * The default fill color used when none is specified.
    * Equivalent to (0.6039, 0.8471, 0.8863, 1.0) .
    */
   int DEFAULT_FILL = 0xff9ad8e2;

   /**
    * The default stroke color used when none is specified.
    * Equivalent to (0.125, 0.125, 0.125, 1.0) .
    */
   int DEFAULT_STROKE = 0xff202020;

   /**
    * The default stroke weight used when none is specified.
    */
   float DEFAULT_STROKE_WEIGHT = 0.825f;
}
