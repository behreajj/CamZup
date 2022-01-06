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
    * Magnitude for orthogonal handles when four curve knots are used to
    * approximate an ellipse or circle (90 degrees per knot),
    * {@value Curve#KAPPA} . Derived from
    * <code>(Math.sqrt(2.0) - 1.0) * 4.0 / 3.0</code>.
    */
   float KAPPA = 0.552285f;

   /**
    * Half the orthogonal handle magnitude for curve knots,
    * {@value Curve#KAPPA_2} .
    */
   float KAPPA_2 = ICurve.KAPPA * 0.5f;

   /**
    * Half the orthogonal handle magnitude for curve knots,
    * {@value Curve#KAPPA_2_D} .
    */
   double KAPPA_2_D = ICurve.KAPPA_D * 0.5d;

   /**
    * Magnitude for orthogonal handles when four curve knots are used to
    * approximate an ellipse or circle (90 degrees per knot),
    * {@value Curve#KAPPA_D} . Derived from
    * <code>(Math.sqrt(2.0) - 1.0) * 4.0 / 3.0</code>.
    */
   double KAPPA_D = 0.5522847498307936d;

   /**
    * Default number of knots to expect when creating an array list in curves.
    */
   int KNOT_CAPACITY = 8;

   /**
    * Default number of cubic Bezier knots used to approximate a circle.
    */
   int KNOTS_PER_CIRCLE = 4;

}
