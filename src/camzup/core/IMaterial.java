package camzup.core;

/**
 * Maintains consistent behavior between different kind of
 * materials, which store color and other display style
 * information.
 */
public interface IMaterial extends IUtils {

   /**
    * The default fill color used when none is specified.
    */
   public static final Color DEFAULT_FILL = new Color(0.6039f, 0.8471f, 0.8863f,
         1.0f);

   /**
    * The default stroke color used when none is specified.
    */
   public static final Color DEFAULT_STROKE = new Color(0.125f, 0.125f,
         0.125f, 1.0f);

   /**
    * The default stroke weight used when none is specified.
    */
   public static final float DEFAULT_STROKE_WEIGHT = 0.825f;
}
