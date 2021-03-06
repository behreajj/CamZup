package camzup.core;

/**
 * Maintains consistent behavior between different kind of materials, which
 * store color and other display style information.
 */
public interface IMaterial extends IUtils {

   /**
    * The default fill color used when none is specified, (0.6039, 0.8471,
    * 0.8863) in RGB.
    */
   int DEFAULT_FILL = 0xff9ad8e2;

   /**
    * The default stroke color used when none is specified.
    */
   int DEFAULT_STROKE = 0xff232323;

   /**
    * The default stroke weight used when none is specified,
    * {@value IMaterial#DEFAULT_STROKE_WEIGHT}.
    */
   float DEFAULT_STROKE_WEIGHT = 1.0f;

}
