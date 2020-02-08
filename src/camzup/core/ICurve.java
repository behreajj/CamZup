package camzup.core;

/**
 * Maintains consistent behavior for curves of different
 * dimensions.
 */
public interface ICurve extends Cloneable, IEntityData {

   /**
    * Gets the number of knots in the curve.
    *
    * @return the knot count
    */
   int length ();

   ICurve toggleLoop ();
}
