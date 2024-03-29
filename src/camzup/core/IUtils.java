package camzup.core;

/**
 * Holds mathematical constants used by the rest of the library. Provides
 * convenience functions for printing a hash code and identity hash code as
 * hexadecimal strings.
 */
public interface IUtils {

   /**
    * Returns an object's identity hash code.
    *
    * @return the identity hash
    */
   default int hashIdentity ( ) { return System.identityHashCode(this); }

   /**
    * Returns an object's identity hash code as a String.
    *
    * @return the identity hash string
    */
   default String hashIdentityString ( ) {

      return Integer.toHexString(this.hashIdentity());
   }

   /**
    * Returns an object's hash code as a String.
    *
    * @return the hash string
    */
   default String hashString ( ) {

      return Integer.toHexString(this.hashCode());
   }

   /**
    * An angle in degrees is multiplied by this constant to convert it to
    * radians. pi / 180.0 , approximately {@value IUtils#DEG_TO_RAD} .
    */
   float DEG_TO_RAD = 0.017453292f;

   /**
    * An angle in degrees is multiplied by this constant to convert it to
    * radians. pi / 180.0 , approximately {@value IUtils#DEG_TO_RAD_D} .
    */
   double DEG_TO_RAD_D = 0.017453292519943295d;

   /**
    * The smallest positive non-zero value. Useful for testing approximation
    * between two floats. Set to {@value IUtils#EPSILON} .
    *
    * @see Utils#approx(float, float)
    */
   float EPSILON = 0.000001f;

   /**
    * The smallest positive non-zero value. Set to {@value IUtils#EPSILON_D} .
    */
   double EPSILON_D = 0.000001d;

   /**
    * The default number of decimal places to print real numbers,
    * {@value IUtils#FIXED_PRINT}.
    */
   int FIXED_PRINT = 4;

   /**
    * Four-thirds, 4.0 / 3.0 . Approximately {@value IUtils#FOUR_THIRDS} .
    * Useful when creating a circular shape with a series of Bezier curves.
    */
   float FOUR_THIRDS = 1.3333333f;

   /**
    * Four-thirds, 4.0 / 3.0 . Approximately {@value IUtils#FOUR_THIRDS_D} .
    * Useful when creating a circular shape with a series of Bezier curves.
    */
   double FOUR_THIRDS_D = 1.3333333333333333d;

   /**
    * An approximation of tau / phi<sup>2</sup> , {@value IUtils#GOLDEN_ANGLE}
    * . Useful for replicating phyllotaxis. In degrees, 137.50777 .
    */
   float GOLDEN_ANGLE = 2.3999631f;

   /**
    * An approximation of tau / phi<sup>2</sup> ,
    * {@value IUtils#GOLDEN_ANGLE_D} . Useful for replicating phyllotaxis. In
    * degrees, 137.50776405003785 .
    */
   double GOLDEN_ANGLE_D = 2.399963229728653d;

   /**
    * pi divided by two. Approximately {@value IUtils#HALF_PI} .
    */
   float HALF_PI = 1.5707964f;

   /**
    * pi divided by two. Approximately {@value IUtils#HALF_PI_D} .
    */
   double HALF_PI_D = 1.5707963267948966d;

   /**
    * Base value used by hash code functions.
    */
   int HASH_BASE = -2128831035;

   /**
    * Multiplier used by hash code functions.
    */
   int HASH_MUL = 16777619;

   /**
    * The {@link IUtils#HASH_BASE} multiplied by the {@link IUtils#HASH_MUL}.
    */
   int MUL_BASE = IUtils.HASH_BASE * IUtils.HASH_MUL;

   /**
    * One-255th, 1.0 / 255.0 . Useful when converting a color with channels in
    * the range [0, 255] to a color in the range [0.0, 1.0] . Approximately
    * {@value IUtils#ONE_255} .
    */
   float ONE_255 = 0.003921569f;

   /**
    * One-255th, 1.0 / 255.0 . Useful when converting a color with channels in
    * the range [0, 255] to a color in the range [0.0, 1.0] . Approximately
    * {@value IUtils#ONE_255_D} .
    */
   double ONE_255_D = 0.00392156862745098d;

   /**
    * One divided by 360 degrees, 1.0 / 360 ; approximately
    * {@value IUtils#ONE_360} . Useful for converting an index in a for-loop
    * to an angle in degrees.
    */
   float ONE_360 = 0.0027777778f;

   /**
    * One divided by 360 degrees, 1.0 / 360.0 ; approximately
    * {@value IUtils#ONE_360_D} . Useful for converting an index in a for-loop
    * to an angle in degrees.
    */
   double ONE_360_D = 0.002777777777777778d;

   /**
    * One divided by pi. Useful when converting inclinations to the range
    * [0.0, 1.0] . Approximately {@value IUtils#ONE_PI} .
    */
   float ONE_PI = 0.31830987f;

   /**
    * One divided by pi. Useful when converting inclinations to linear ranges.
    * Approximately {@value IUtils#ONE_PI_D} .
    */
   double ONE_PI_D = 0.3183098861837907d;

   /**
    * One-sixth, 1.0 / 6.0 . Useful when converting a color in RGB color space
    * to one in HSB, given the six sectors formed by primary and secondary
    * colors . Approximately {@value IUtils#ONE_SIX} .
    */
   float ONE_SIX = 0.16666667f;

   /**
    * One-sixth, 1.0 / 6.0 . Useful when converting a color in RGB color space
    * to one in HSB, given the six sectors formed by primary and secondary
    * colors . Approximately {@value IUtils#ONE_SIX_D} .
    */
   double ONE_SIX_D = 0.16666666666666667d;

   /**
    * An approximation of 1.0 / sqrt(2.0), {@value IUtils#ONE_SQRT_2} . The
    * sine and cosine of 45 degrees.
    */
   float ONE_SQRT_2 = 0.70710677f;

   /**
    * An approximation of 1.0 / sqrt(2.0), {@value IUtils#ONE_SQRT_2_D} . The
    * sine and cosine of 45 degrees.
    */
   double ONE_SQRT_2_D = 0.7071067811865475d;

   /**
    * An approximation of 1.0 / sqrt(3.0), {@value IUtils#ONE_SQRT_3} .
    */
   float ONE_SQRT_3 = 0.57735026f;

   /**
    * An approximation of 1.0 / sqrt(3.0), {@value IUtils#ONE_SQRT_3_D} .
    */
   double ONE_SQRT_3_D = 0.5773502691896258d;

   /**
    * One divided by tau. Useful for converting an index in a for-loop to an
    * angle. Approximately {@value IUtils#ONE_TAU} .
    */
   float ONE_TAU = 0.15915494f;

   /**
    * 1.0 / 4.0 pi. Useful when normalizing angles supplied to quaternions.
    * Approximately {@value IUtils#ONE_TAU_2} .
    */
   float ONE_TAU_2 = 0.07957747f;

   /**
    * 1.0 / 4.0 pi. Useful when normalizing angles supplied to quaternions.
    * Approximately {@value IUtils#ONE_TAU_2_D} .
    */
   double ONE_TAU_2_D = 0.07957747154594767d;

   /**
    * One divided by tau. Useful for converting an index in a for-loop to an
    * angle. Approximately {@value IUtils#ONE_TAU_D} .
    */
   double ONE_TAU_D = 0.15915494309189535d;

   /**
    * One-third, 1.0 / 3.0 . Approximately {@value IUtils#ONE_THIRD} . Useful
    * for setting handles on the knot of a Bezier curve.
    */
   float ONE_THIRD = 0.33333334f;

   /**
    * One-third, 1.0 / 3.0 . Approximately {@value IUtils#ONE_THIRD_D} .
    * Useful for setting handles on the knot of a Bezier curve.
    */
   double ONE_THIRD_D = 0.3333333333333333d;

   /**
    * The golden ratio. An approximation of phi, or ( 1.0 + sqrt(5.0) ) / 2.0
    * , {@value IUtils#PHI} .
    */
   float PHI = 1.618034f;

   /**
    * The golden ratio. An approximation of phi, or ( 1.0 + sqrt(5.0) ) / 2.0
    * , {@value IUtils#PHI_D} .
    */
   double PHI_D = 1.618033988749895d;

   /**
    * An approximation of pi, {@value IUtils#PI} .
    */
   float PI = 3.1415927f;

   /**
    * An angle in radians is multiplied by this constant to convert it to
    * degrees. 180.0 / pi, approximately {@value IUtils#RAD_TO_DEG} .
    */
   float RAD_TO_DEG = 57.29578f;

   /**
    * An angle in radians is multiplied by this constant to convert it to
    * degrees. 180.0 / pi, approximately {@value IUtils#RAD_TO_DEG_D} .
    */
   double RAD_TO_DEG_D = 57.29577951308232d;

   /**
    * An approximation of sqrt(2.0), {@value IUtils#SQRT_2} .
    */
   float SQRT_2 = 1.4142137f;

   /**
    * An approximation of sqrt(2.0), {@value IUtils#SQRT_2_D} .
    */
   double SQRT_2_D = 1.4142135623730951d;

   /**
    * An approximation of sqrt(3.0), {@value IUtils#SQRT_3} .
    */
   float SQRT_3 = 1.7320508f;

   /**
    * An approximation of sqrt(3.0) / 2.0 , {@value IUtils#SQRT_3_2} . The
    * cosine of 30 degrees.
    */
   float SQRT_3_2 = 0.8660254f;

   /**
    * An approximation of sqrt(3.0) / 2.0 , {@value IUtils#SQRT_3_2_D} . The
    * cosine of 30 degrees.
    */
   double SQRT_3_2_D = 0.8660254037844386d;

   /**
    * An approximation of sqrt(3.0), {@value IUtils#SQRT_3_D} .
    */
   double SQRT_3_D = 1.7320508075688772d;

   /**
    * An approximation of tau, {@value IUtils#TAU} . Equal to 2.0 pi.
    */
   float TAU = 6.2831855f;

   /**
    * An approximation of tau, {@value IUtils#TAU_D} . Equal to 2.0 pi.
    */
   double TAU_D = 6.283185307179586d;

   /**
    * pi divided by three, {@value IUtils#THIRD_PI} . 60 degrees. Useful for
    * describing the field of view in a perspective camera.
    */
   float THIRD_PI = 1.0471976f;

   /**
    * pi divided by three, {@value IUtils#THIRD_PI_D} . 60 degrees. Useful for
    * describing the field of view in a perspective camera.
    */
   double THIRD_PI_D = 1.0471975511965976d;

   /**
    * Two-thirds, 2.0 / 3.0 . Approximately {@value IUtils#TWO_THIRDS} .
    * Useful for setting handles on the knot of a Bezier curve.
    */
   float TWO_THIRDS = 0.6666667f;

   /**
    * Two-thirds, 2.0 / 3.0 . Approximately {@value IUtils#TWO_THIRDS_D} .
    * Useful for setting handles on the knot of a Bezier curve.
    */
   double TWO_THIRDS_D = 0.6666666666666667d;

}
