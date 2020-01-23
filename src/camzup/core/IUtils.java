package camzup.core;

/**
 * Holds mathematical constants used by the rest of the
 * library. Provides convenience functions for printing a
 * hash code and identity hash code as hexadecimal strings.
 */
public interface IUtils {

   /**
    * The smallest positive non-zero value, \u03b5. Useful for
    * testing approximation between two floats. Set to 0.000001
    * .
    *
    * @see Utils#approx(float, float)
    */
   float DEFAULT_EPSILON = 0.000001f;

   /**
    * An angle in degrees is multiplied by this constant to
    * convert it to radians. \u03c0 / 180.0 , approximately
    * 0.017453292 .
    */
   float DEG_TO_RAD = 0.017453292f;

   /**
    * An angle in degrees is multiplied by this constant to
    * convert it to radians. \u03c0 / 180.0 , approximately
    * 0.017453292519943295 .
    */
   double DEG_TO_RAD_D = 0.017453292519943295d;

   /**
    * Four-thirds, 4.0 / 3.0 . Approximately 1.3333333 . Useful
    * when creating a circular shape with a series of Bezier
    * curves.
    */
   float FOUR_THIRDS = 1.3333333f;

   /**
    * Four-thirds, 4.0 / 3.0 . Approximately 1.3333333333333333
    * . Useful when creating a circular shape with a series of
    * Bezier curves.
    */
   double FOUR_THIRDS_D = 1.3333333333333333d;

   /**
    * \u03c0 divided by two. Approximately 1.5707964 .
    */
   float HALF_PI = 1.5707964f;

   /**
    * \u03c0 divided by two. Approximately 1.5707963267948966 .
    */
   double HALF_PI_D = 1.5707963267948966d;

   /**
    * base value used by hash code functions.
    */
   int HASH_BASE = -2128831035;

   /**
    * Multiplier used by hash code functions.
    */
   int HASH_MUL = 16777619;

   /**
    * One divided by 2.2 , 1.0 / 2.2 . Gamma-adjustments to
    * color commonly raise the color's RGB channels to the
    * power 2.2 or ( 1.0 / 2.0 ). Approximately 0.45454545 .
    */
   float ONE_2_2 = 0.45454545f;

   /**
    * One divided by 2.2 , 1.0 / 2.2 . Gamma-adjustments to
    * color commonly raise the color's RGB channels to the
    * power 2.2 or ( 1.0 / 2.0 ). Approximately
    * 0.45454545454545454 .
    */
   double ONE_2_2_D = 0.45454545454545454d;

   /**
    * One-255th, 1.0 / 255.0 . Useful when converting a color
    * with channels in the range [0, 255] to a color in the
    * range [0, 1] . Approximately 0.003921569 .
    */
   float ONE_255 = 0.003921569f;

   /**
    * One-255th, 1.0 / 255.0 . Useful when converting a color
    * with channels in the range [0, 255] to a color in the
    * range [0, 1]. Approximately 0.00392156862745098 .
    */
   double ONE_255_D = 0.00392156862745098d;

   /**
    * One divided by 360 degrees, 1.0 / 360 ; approximately
    * 0.0027777778 . Useful for converting an index in a
    * for-loop to an angle in degrees.
    */
   float ONE_360 = 0.0027777778f;

   /**
    * One divided by 360 degrees, 1.0 / 360 ; approximately
    * 0.002777777777777778 . Useful for converting an index in
    * a for-loop to an angle in degrees.
    */
   double ONE_360_D = 0.002777777777777778d;

   /**
    * One-sixth, 1.0 / 6.0 . Useful when converting a color in
    * RGB color space to one in HSB, given the six sectors
    * formed by primary and secondary colors . Approximately
    * 0.16666667 .
    */
   float ONE_SIX = 0.16666667f;

   /**
    * One-sixth, 1.0 / 6.0 . Useful when converting a color in
    * RGB color space to one in HSB, given the six sectors
    * formed by primary and secondary colors . Approximately
    * 0.16666666666666667 .
    */
   double ONE_SIX_D = 0.16666666666666667d;

   /**
    * An approximation of 1.0 / ( \u221a 2.0 ), 0.70710677 .
    */
   float ONE_SQRT_2 = 0.70710677f;

   /**
    * An approximation of 1.0 / ( \u221a 2.0 ),
    * 0.7071067811865475 .
    */
   double ONE_SQRT_2_D = 0.7071067811865475d;

   /**
    * An approximation of 1.0 / ( \u221a 3.0 ), 0.57735026 .
    */
   float ONE_SQRT_3 = 0.57735026f;

   /**
    * An approximation of 1.0 / ( \u221a 3.0 ),
    * 0.5773502691896258 .
    */
   double ONE_SQRT_3_D = 0.5773502691896258d;

   /**
    * One divided by \u03c4. Approximately 0.15915494 . Useful
    * for converting an index in a for-loop to an angle and for
    * applying mod to an angle.
    */
   float ONE_TAU = 0.15915494f;

   /**
    * 1.0 / 4.0 \u03c0 . Useful when normalizing angles
    * supplied to quaternions. Approximately 0.07957747 .
    */
   float ONE_TAU_2 = 0.07957747154594767f;

   /**
    * 1.0 / 4.0 \u03c0 . Useful when normalizing angles
    * supplied to quaternions. Approximately
    * 0.07957747154594767 .
    */
   double ONE_TAU_2_D = 0.07957747154594767d;

   /**
    * One divided by \u03c4. Approximately 0.15915494309189535
    * . Useful for converting an index in a for-loop to an
    * angle and for applying mod to an angle.
    */
   double ONE_TAU_D = 0.15915494309189535d;

   /**
    * One-third, 1.0 / 3.0 . Approximately 0.33333333 . Useful
    * for setting handles on the knot of a Bezier curve.
    */
   float ONE_THIRD = 0.33333334f;

   /**
    * One-third, 1.0 / 3.0 . Approximately 0.3333333333333333 .
    * Useful for setting handles on the knot of a Bezier curve.
    */
   double ONE_THIRD_D = 0.3333333333333333d;

   /**
    * An approximation of \u03c6, or ( 1.0 + \u221a 5.0 ) / 2.0
    * , 1.618034 .
    */
   float PHI = 1.618034f;

   /**
    * An approximation of \u03c6, or ( 1.0 + \u221a 5.0 ) / 2.0
    * , 1.618033988749895 .
    */
   double PHI_D = 1.618033988749895d;

   /**
    * An approximation of \u03c0, 3.1415927 .
    */
   float PI = 3.1415927f;

   /**
    * An angle in radians is multiplied by this constant to
    * convert it to degrees. 180.0 / \u03c0, approximately
    * 57.29578 .
    */
   float RAD_TO_DEG = 57.29578f;

   /**
    * An angle in radians is multiplied by this constant to
    * convert it to degrees. 180.0 / \u03c0, approximately
    * 57.29577951308232 .
    */
   double RAD_TO_DEG_D = 57.29577951308232d;

   /**
    * An approximation of \u221a 2.0, 1.4142137 .
    */
   float SQRT_2 = 1.4142137f;

   /**
    * An approximation of \u221a 2.0, 1.4142135623730951 .
    */
   double SQRT_2_D = 1.4142135623730951d;

   /**
    * An approximation of \u221a 3.0, 1.7320508 .
    */
   float SQRT_3 = 1.7320508f;

   /**
    * An approximation of ( \u221a 3.0 ) / 2.0 , 0.8660254 .
    */
   float SQRT_3_2 = 0.8660254f;

   /**
    * An approximation of ( \u221a 3.0 ) / 2.0 ,
    * 0.8660254037844386 .
    */
   double SQRT_3_2_D = 0.8660254037844386d;

   /**
    * An approximation of \u221a 3.0, 1.7320508075688772 .
    */
   double SQRT_3_D = 1.7320508075688772d;

   /**
    * An approximation of \u03c4 . Equal to 2.0 \u03c0, or
    * 6.2831853 .
    */
   float TAU = 6.2831855f;

   /**
    * An approximation of \u03c4 . Equal to 2.0 \u03c0, or
    * 6.283185307179586 .
    */
   double TAU_D = 6.283185307179586d;

   /**
    * An approximation of \u03c0 / 3.0 , 1.0471976 . Useful for
    * describing the field of view in a perspective camera.
    */
   float THIRD_PI = 1.0471976f;

   /**
    * An approximation of \u03c0 / 3.0 , 1.0471975511965976 .
    * Useful for describing the field of view in a perspective
    * camera.
    */
   double THIRD_PI_D = 1.0471975511965976d;

   /**
    * Returns an object's identity hash code.
    *
    * @return the identity hash
    */
   default int hashIdentity () {

      return System.identityHashCode(this);
   }

   /**
    * Returns an object's identity hash code as a String.
    *
    * @return the identity hash string
    */
   default String hashIdentityString () {

      return Integer.toHexString(this.hashIdentity());
   }

   /**
    * Returns an object's hash code as a String.
    *
    * @return the hash string
    */
   default String hashString () {

      return Integer.toHexString(this.hashCode());
   }
}
