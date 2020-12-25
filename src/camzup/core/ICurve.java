package camzup.core;

/**
 * Maintains consistent behavior for curves of different dimensions.
 */
public interface ICurve extends IEntityData {

   /**
    * Gets the number of knots in the curve.
    *
    * @return the knot count
    */
   int length ( );

   /**
    * Toggles whether or not this is a closed loop.
    *
    * @return this curve
    */
   ICurve toggleLoop ( );

   /**
    * Handle magnitude for orthogonal handles when four curve knots are used
    * to approximate an ellipse or circle (90 degrees per knot),
    * {@value Curve#HNDL_MAG_ORTHO} .
    */
   float HNDL_MAG_ORTHO = 0.552285f;

   /**
    * Half the orthogonal handle magnitude for curve knots,
    * {@value Curve#HNDL_MAG_ORTHO_2} .
    */
   float HNDL_MAG_ORTHO_2 = ICurve.HNDL_MAG_ORTHO * 0.5f;

   /**
    * Half the orthogonal handle magnitude for curve knots,
    * {@value Curve#HNDL_MAG_ORTHO_2_D} .
    */
   double HNDL_MAG_ORTHO_2_D = ICurve.HNDL_MAG_ORTHO_D * 0.5d;

   /**
    * Handle magnitude for orthogonal handles when four curve knots are used
    * to approximate an ellipse or circle (90 degrees per knot),
    * {@value Curve#HNDL_MAG_ORTHO_D} .
    */
   double HNDL_MAG_ORTHO_D = 0.552285d;

   /**
    * Default number of knots to expect when creating an array list in curves.
    */
   int KNOT_CAPACITY = 8;

   /**
    * Default number of cubic Bezier knots used to approximate a circle.
    */
   int KNOTS_PER_CIRCLE = 4;

}
