package camzup.core;

/**
 * Implements basic math utilities for single-precision numbers.
 */
public abstract class Utils implements IUtils {

   /**
    * Discourage overriding with a private constructor.
    */
   private Utils ( ) {}

   /**
    * Finds the absolute value of a single precision real number. An alias for
    * {@link Math#abs(float)}. Relies on bit-masking to remove the sign bit.
    * Equivalent to <code>Utils.max(-a, a)</code>.
    *
    * @param v the input value
    *
    * @return the absolute value
    *
    * @see Float#intBitsToFloat(int)
    * @see Float#floatToRawIntBits(float)
    */
   public static float abs ( final float v ) {

      return Float.intBitsToFloat(0x7fffffff & Float.floatToRawIntBits(v));
   }

   /**
    * A bounds checked approximation of the arc-cosine for single precision
    * real numbers. Returns a value in the range [0.0, pi] : pi when the input
    * is less than or equal to -1.0; pi / 2.0 when the input is 0.0; 0.0 when
    * the input is greater than or equal to 1.0.<br>
    * <br>
    * {@link Math#acos(double) } defers to {@link StrictMath#acos(double) },
    * which is implemented natively. This is not a "fast" alternative.<br>
    * <br>
    * Based on the algorithm in
    * <a href="https://developer.download.nvidia.com/cg/acos.html">Nvidia Cg
    * 3.1 Toolkit Documentation</a>. This cites M. Abramowitz and I.A. Stegun,
    * Eds., <em>Handbook of Mathematical Functions</em>, possibly p. 83, which
    * cites <em>Approximations for Digital Computers</em> by C. Hastings, Jr.
    *
    * @param y the input value
    *
    * @return the angle in radians
    *
    * @see Utils#sqrtUnchecked(float)
    *
    * @author M. Abramowitz
    * @author C. Hastings, Jr
    * @author I. A. Stegun
    */
   public static float acos ( final float y ) {

      if ( y <= -1.0f ) { return IUtils.PI; }
      if ( y >= 1.0f ) { return 0.0f; }
      final boolean ltZero = y < -0.0f;
      final float x = ltZero ? -y : y;
      float ret = ( 0.074261f - 0.0187293f * x ) * x - 0.2121144f;
      ret = ( ret * x + IUtils.HALF_PI ) * Utils.sqrtUnchecked(1.0f - x);
      return ltZero ? IUtils.PI - ret : ret;
   }

   /**
    * Evaluates two floats like booleans using the AND logic gate. Non-zero
    * inputs evaluate to true, or 1. Zero evaluates to false, or 0.
    *
    * @param a the left operand
    * @param b the right operand
    *
    * @return the evaluation
    */
   public static int and ( final float a, final float b ) {

      return ( a != 0.0f && a == a ? 1 : 0 ) & ( b != 0.0f && b == b ? 1 : 0 );
   }

   /**
    * A quick approximation test. Tests to see if the absolute of the
    * difference between two values is less than or equal to
    * {@link IUtils#EPSILON}, {@value IUtils#EPSILON}. Does not handle edge
    * cases.
    *
    * @param a the left comparisand
    * @param b the right comparisand
    *
    * @return the evaluation
    *
    * @see Utils#approx(float, float, float)
    */
   public static boolean approx ( final float a, final float b ) {

      return Utils.approx(a, b, IUtils.EPSILON);
   }

   /**
    * A quick approximation test. Tests to see if the absolute of the
    * difference between two values is less than or equal to a tolerance. Does
    * not handle edge cases.
    *
    * @param a the left comparisand
    * @param b the right comparisand
    * @param t the tolerance
    *
    * @return the evaluation
    */
   public static boolean approx ( final float a, final float b,
      final float t ) {

      final float diff = b - a;
      return diff <= t && diff >= -t;
   }

   /**
    * A bounds checked approximation of the arc-sine for single precision real
    * numbers. Returns a value in the range [-pi / 2.0, pi / 2.0]: -pi / 2.0
    * when the input is less than or equal to -1.0; 0.0 when the input is 0.0;
    * pi / 2.0 when the input is greater than or equal to 1.0.<br>
    * <br>
    * {@link Math#asin(double) } defers to {@link StrictMath#asin(double) },
    * which is implemented natively. This is not a "fast" alternative.<br>
    * <br>
    * Based on the algorithm in
    * <a href="https://developer.download.nvidia.com/cg/asin.html">Nvidia Cg
    * 3.1 Toolkit Documentation</a> . This cites M. Abramowitz and I.A.
    * Stegun, Eds., <em>Handbook of Mathematical Functions</em>, possibly p.
    * 83, which cites <em>Approximations for Digital Computers</em> by C.
    * Hastings, Jr.
    *
    * @param y the input value
    *
    * @return the angle in radians
    *
    * @see Utils#sqrtUnchecked(float)
    *
    * @author M. Abramowitz
    * @author C. Hastings, Jr
    * @author I. A. Stegun
    */
   public static float asin ( final float y ) {

      if ( y <= -1.0f ) { return -IUtils.HALF_PI; }
      if ( y >= 1.0f ) { return IUtils.HALF_PI; }
      final boolean ltZero = y < -0.0f;
      final float x = ltZero ? -y : y;
      float ret = ( 0.074261f - 0.0187293f * x ) * x - 0.2121144f;
      ret = ret * x + IUtils.HALF_PI;
      ret = IUtils.HALF_PI - ret * Utils.sqrtUnchecked(1.0f - x);
      return ltZero ? -ret : ret;
   }

   /**
    * Finds a single precision approximation of a signed angle given a
    * vertical and horizontal component. The vertical component precedes the
    * horizontal. The return value falls in the range [-pi, pi] .<br>
    * <br>
    * This is not a "fast" alternative to
    * {@link Math#atan2(double,double) }.<br>
    * <br>
    * Based on the algorithm in
    * <a href="https://developer.download.nvidia.com/cg/atan2.html">Nvidia Cg
    * 3.1 Toolkit Documentation</a> .
    *
    * @param y the y coordinate (the ordinate)
    * @param x the x coordinate (the abscissa)
    *
    * @return the angle in radians
    */
   public static float atan2 ( final float y, final float x ) {

      final boolean yLtZero = y < -0.0f;
      final boolean xLtZero = x < -0.0f;
      final float yAbs = yLtZero ? -y : y;
      final float xAbs = xLtZero ? -x : x;

      final boolean yGtX = yAbs > xAbs;
      float t0 = yGtX ? yAbs : xAbs;
      if ( t0 != 0.0f ) {
         /* t2 chooses between xAbs / yAbs and yAbs / xAbs. */
         final float t2 = ( yGtX ? xAbs : yAbs ) / t0;
         final float t3 = t2 * t2;
         t0 = 0.057477314f - 0.01348047f * t3;
         t0 = t0 * t3 - 0.12123907f;
         t0 = t0 * t3 + 0.19563593f;
         t0 = t0 * t3 - 0.3329946f;
         t0 = t0 * t3 + 0.99999565f;
         t0 = t0 * t2;
         t0 = yGtX ? IUtils.HALF_PI - t0 : t0;
         t0 = xLtZero ? IUtils.PI - t0 : t0;
         return yLtZero ? -t0 : t0;
      }
      return 0.0f;
   }

   /**
    * Gets a bit from a byte at an index, ordered from least to most
    * significant digit. There are 8 bits in a byte, so the index should be in
    * the range [0, 7] . The bit is promoted to a byte.
    *
    * @param a the byte
    * @param i the bit position
    *
    * @return the bit
    */
   public static byte bitlm ( final byte a, final int i ) {

      return ( byte ) ( a >> i & 1 );
   }

   /**
    * Gets a bit from a byte at an index, ordered from most to least
    * significant digit. There are 8 bits in a byte, so the index should be in
    * the range [0, 7] . The bit is promoted to a byte.
    *
    * @param a the byte
    * @param i the bit position
    *
    * @return the bit
    */
   public static byte bitml ( final byte a, final int i ) {

      return ( byte ) ( a >> 7 - i & 1 );
   }

   /**
    * Gets an array of bits from a byte, ordered from least to most
    * significant digit, the reverse of a literal. For example,
    * <code>0b01010011</code> would yield
    * <code>{ 1, 1, 0, 0, 1, 0, 1, 0 }</code> .
    *
    * @param b the byte
    *
    * @return the bit array
    */
   public static byte[] bitslm ( final byte b ) {

      return Utils.bitslm(b, new byte[Byte.SIZE], 0);
   }

   /**
    * Appends to an array of bits from a byte, ordered from least to most
    * significant digit, the reverse of a literal. For example,
    * <code>0b01010011</code> would yield
    * <code>{ 1, 1, 0, 0, 1, 0, 1, 0 }</code> .
    *
    * @param b   the byte
    * @param arr the array
    * @param i   the index
    *
    * @return the bit array
    */
   public static byte[] bitslm ( final byte b, final byte[] arr, final int i ) {

      for ( int j = 0; j < Byte.SIZE; ++j ) {
         arr[i + j] = ( byte ) ( b >> j & 1 );
      }
      return arr;
   }

   /**
    * Gets an array of bits from a byte, ordered from most to least
    * significant digit, as a literal would be written. For example,
    * <code>0b01010011</code> would yield the array
    * <code>{ 0, 1, 0, 1, 0, 0, 1, 1 }</code> .
    *
    * @param b the byte
    *
    * @return the bit array
    */
   public static byte[] bitsml ( final byte b ) {

      return Utils.bitsml(b, new byte[Byte.SIZE], 0);
   }

   /**
    * Appends to an array of bits from a byte, ordered from most to least
    * significant digit, as a literal would be written. For example,
    * <code>0b01010011</code> would yield the array
    * <code>{ 0, 1, 0, 1, 0, 0, 1, 1 }</code> .
    *
    * @param b   the byte
    * @param arr the array
    * @param i   the index
    *
    * @return the bit array
    */
   public static byte[] bitsml ( final byte b, final byte[] arr, final int i ) {

      for ( int j = Byte.SIZE - 1, k = 0; j > -1; --j, ++k ) {
         arr[i + k] = ( byte ) ( b >> j & 1 );
      }
      return arr;
   }

   /**
    * Raises a real number to the next greatest integer. An alternative to
    * {@link Math#ceil(double)} . ceil ( <em>x</em> ) = - floor ( -<em>x</em>
    * ) .
    *
    * @param v the input value
    *
    * @return the raised value
    */
   public static int ceil ( final float v ) {

      return v > 0.0f ? ( int ) v + 1 : v < 0.0f ? ( int ) v : 0;
   }

   /**
    * Clamps a real number between a lower and an upper bound.
    *
    * @param v  the input value
    * @param lb the upper bound
    * @param ub the lower bound
    *
    * @return the clamped value
    */
   public static float clamp ( final float v, final float lb, final float ub ) {

      return v < lb ? lb : v > ub ? ub : v;
   }

   /**
    * Clamps an integer between a lower and an upper bound.
    *
    * @param v  the input value
    * @param lb the lower bound
    * @param ub the upper bound
    *
    * @return the clamped value
    */
   public static int clamp ( final int v, final int lb, final int ub ) {

      return v < lb ? lb : v > ub ? ub : v;
   }

   /**
    * Clamps a value to the range [0.0, 1.0] .
    *
    * @param v the input value
    *
    * @return the clamped value
    */
   public static float clamp01 ( final float v ) {

      return v < 0.0f ? 0.0f : v > 1.0f ? 1.0f : v;
   }

   /**
    * Returns the first floating-point argument with the sign of the second
    * floating-point argument. An alias of {@link Math#copySign(float, float)}
    * .<br>
    * <br>
    * When the sign is zero, the return value will depend on the sign of the
    * zero.
    *
    * @param magnitude the magnitude
    * @param sign      the sign
    *
    * @return the magnified sign
    *
    * @see Float#intBitsToFloat(int)
    * @see Float#floatToRawIntBits(float)
    */
   public static float copySign ( final float magnitude, final float sign ) {

      return Float.intBitsToFloat(Float.floatToRawIntBits(sign) & 0x80000000
         | Float.floatToRawIntBits(magnitude) & 0x7fffffff);
   }

   /**
    * Returns the first floating-point argument with the sign of the second
    * integer argument. When the sign is zero, the return value will be
    * positive, as integer zeroes are unsigned.
    *
    * @param magnitude the magnitude
    * @param sign      the sign
    *
    * @return the magnified sign
    *
    * @see Float#intBitsToFloat(int)
    * @see Float#floatToRawIntBits(float)
    */
   public static float copySign ( final float magnitude, final int sign ) {

      return Float.intBitsToFloat(sign & 0x80000000 | Float.floatToRawIntBits(
         magnitude) & 0x7fffffff);
   }

   /**
    * Finds the single-precision cosine of an angle in radians. Returns a
    * value in the range [-1.0, 1.0] .<br>
    * <br>
    * Wraps around {@link Math#cos(double)}.
    *
    * @param radians the angle in radians
    *
    * @return the cosine of the angle
    */
   public static float cos ( final float radians ) {

      return ( float ) Math.cos(radians);
   }

   /**
    * Finds the approximate cotangent of the angle in radians. Equivalent to
    * dividing the cosine of the angle by the sine, or to 1.0 / tan (
    * <em>a</em> ) .
    *
    * @param radians the angle in radians
    *
    * @return the cotangent
    */
   public static float cot ( final float radians ) {

      final float nrmRad = radians * IUtils.ONE_TAU;
      final float sint = Utils.scNorm(nrmRad - 0.25f);
      return sint != 0.0f ? Utils.scNorm(nrmRad) / sint : 0.0f;
   }

   /**
    * Finds the absolute value of the left operand minus the right.
    *
    * @param a left operand
    * @param b right operand
    *
    * @return the difference
    *
    * @see Float#intBitsToFloat(int)
    * @see Float#floatToRawIntBits(float)
    */
   public static float diff ( final float a, final float b ) {

      return Float.intBitsToFloat(0x7fffffff & Float.floatToRawIntBits(b - a));
   }

   /**
    * Finds the absolute value of the left operand minus the right.
    *
    * @param a left operand
    * @param b right operand
    *
    * @return the difference
    */
   public static int diff ( final int a, final int b ) {

      final int d = b - a;
      return d < 0 ? -d : d;
   }

   /**
    * Finds the unsigned distance between two angles. Angles are expected to
    * be in radians.
    *
    * @param a left operand
    * @param b right operand
    *
    * @return unsigned distance
    *
    * @see Utils#modRadians(float)
    * @see Utils#abs(float)
    */
   public static float distAngleUnsigned ( final float a, final float b ) {

      /*
       * See https://gamedev.stackexchange.com/a/4472,
       * https://stackoverflow.com/a/28037434 (for signed)
       */

      return IUtils.PI - Utils.abs(Utils.abs(Utils.modRadians(b) - Utils
         .modRadians(a)) - IUtils.PI);
   }

   /**
    * Divides the left operand by the right, but returns zero when the
    * denominator is zero, avoiding the {@link java.lang.ArithmeticException}.
    * This is to simulate the convention of shading languages like GLSL and
    * OSL.
    *
    * @param a the numerator
    * @param b the denominator
    *
    * @return the quotient
    */
   public static float div ( final float a, final float b ) {

      return b != 0.0f ? a / b : 0.0f;
   }

   /**
    * Divides the left operand by the right, but returns zero when the
    * denominator is zero, avoiding the {@link java.lang.ArithmeticException}.
    * This is to simulate the convention of shading languages like GLSL and
    * OSL.
    *
    * @param a the numerator
    * @param b the denominator
    *
    * @return the quotient
    */
   public static int div ( final int a, final int b ) {

      return b != 0 ? a / b : 0;
   }

   /**
    * Returns the value if it is greater than the lower bound, inclusive, and
    * less than the upper bound, exclusive. Otherwise, returns 0.0 .
    *
    * @param v  the input value
    * @param lb the lower bound
    * @param ub the upper bound
    *
    * @return the filtered value
    */
   public static float filter ( final float v, final float lb,
      final float ub ) {

      return v >= lb && v < ub ? v : 0.0f;
   }

   /**
    * Floors a real number to the next least integer. An alternative to
    * {@link Math#floor(double)} .
    *
    * @param v the input value
    *
    * @return the floored value
    */
   public static int floor ( final float v ) {

      return v > 0.0f ? ( int ) v : v < 0.0f ? ( int ) v - 1 : 0;
   }

   /**
    * Applies the modulo operator (<code>%</code>) to the operands. Known in
    * some languages as <code>rem</code> for remainder. An analogous formula
    * would be fmod ( <em>a</em>, <em>b</em> ) := <em>a</em> - <em>b</em>
    * trunc ( <em>a</em> / <em>b</em> ) . When <em>b</em> is zero, returns
    * <em>a</em>. When <em>a</em> is negative and <em>b</em> is positive, the
    * result will be negative. If <em>b</em> is one, use
    * {@link Utils#fract(float)} or <em>a</em> - trunc ( <em>a</em> ).
    *
    * @param a the left operand
    * @param b the right operand
    *
    * @return the result
    */
   public static float fmod ( final float a, final float b ) {

      return b != 0.0f ? a % b : a;
   }

   /**
    * Applies the modulo operator (%) to the operands. Known in some languages
    * as <code>rem</code> for remainder. When <em>b</em> is zero, returns
    * <em>a</em>.
    *
    * @param a the left operand
    * @param b the right operand
    *
    * @return the result
    */
   public static int fmod ( final int a, final int b ) {

      return b != 0 ? a % b : a;
   }

   /**
    * Finds the signed fractional portion of the input value by subtracting
    * the value's truncation from the value, i.e., fract ( <em>a</em> ) :=
    * <em>a</em> - trunc ( <em>a</em> ) .<br>
    * <br>
    * Use this instead of fmod ( <em>a</em>, 1.0 ) or <em>a</em> % 1.0 .
    *
    * @param v the input value
    *
    * @return the fractional portion
    */
   public static float fract ( final float v ) { return v - ( int ) v; }

   /**
    * Finds the hypotenuse between two values, sqrt ( <em>a</em><sup>2</sup> +
    * <em>b</em><sup>2</sup> ) . Useful when finding the magnitude of a
    * vector.
    *
    * @param a the first value
    * @param b the second value
    *
    * @return the hypotenuse
    *
    * @see Utils#sqrtUnchecked(float)
    */
   public static float hypot ( final float a, final float b ) {

      /*
       * Do not use Math.hypot . It is not a HotSpotIntrinsicCandidate , and it
       * delegates to StrictMath .
       */

      return Utils.sqrtUnchecked(a * a + b * b);
   }

   /**
    * Finds the hypotenuse given three values, sqrt ( <em>a</em><sup>2</sup> +
    * <em>b</em><sup>2</sup> + <em>c</em><sup>2</sup> ) . Useful when finding
    * the magnitude of a vector.
    *
    * @param a the first value
    * @param b the second value
    * @param c the third value
    *
    * @return the hypotenuse
    *
    * @see Utils#sqrtUnchecked(float)
    */
   public static float hypot ( final float a, final float b, final float c ) {

      return Utils.sqrtUnchecked(a * a + b * b + c * c);
   }

   /**
    * Finds one divided by the hypotenuse of two values, 1.0 / ( sqrt (
    * <em>a</em><sup>2</sup> + <em>b</em><sup>2</sup> ) ) . Useful when
    * normalizing vectors.
    *
    * @param a the first value
    * @param b the second value
    *
    * @return the inverse hypotenuse
    *
    * @see Utils#invSqrtUnchecked
    */
   public static float invHypot ( final float a, final float b ) {

      return Utils.invSqrtUnchecked(a * a + b * b);
   }

   /**
    * Finds one divided by the hypotenuse of three values, 1.0 / ( sqrt (
    * <em>a</em><sup>2</sup> + <em>b</em><sup>2</sup> + <em>c</em><sup>2</sup>
    * ) ) . Useful when normalizing vectors.
    *
    * @param a the first value
    * @param b the second value
    * @param c the third value
    *
    * @return the inverse hypotenuse
    *
    * @see Utils#invSqrtUnchecked
    */
   public static float invHypot ( final float a, final float b,
      final float c ) {

      return Utils.invSqrtUnchecked(a * a + b * b + c * c);
   }

   /**
    * An inverse square-root. Returns 0.0 when the value is less then or equal
    * to zero. Use the unchecked version when the input value is known to be
    * positive.
    *
    * @param value the value
    *
    * @return the inverse square root
    *
    * @see Utils#invSqrtUnchecked(float)
    */
   public static float invSqrt ( final float value ) {

      return value > 0.0f ? Utils.invSqrtUnchecked(value) : 0.0f;
   }

   /**
    * An inverse square root implementation for single precision real numbers
    * based on the 'evil bit hack' from <em>Quake III</em>, as described by
    * Chris Lomont in
    * "<a href= "http://www.lomont.org/papers/2003/InvSqrt.pdf">Fast Inverse
    * Square Root</a>." For accuracy, the result is refined three times with
    * the Newton-Raphson method.<br>
    * <br>
    * Useful when normalizing vectors or quaternions. Prefer this over
    * {@link Utils#sqrt(float)}, which depends on this function.<br>
    * <br>
    * Contrary to the name, this should not be assumed to be faster than
    * {@link Math#sqrt(double)}.
    *
    * @param x the value
    *
    * @return the inverse square root
    *
    * @author Chris Lomont
    * @author Greg Walsh
    *
    * @see Float#floatToIntBits(float)
    * @see Float#intBitsToFloat(int)
    */
   public static float invSqrtUnchecked ( final float x ) {

      float y = Float.intBitsToFloat(0x5f375a86 - ( Float.floatToIntBits(x)
         >> 1 ));

      final float vhalf = x * 0.5f;
      y *= 1.5f - vhalf * y * y;
      y *= 1.5f - vhalf * y * y;
      y *= 1.5f - vhalf * y * y;

      return y;
   }

   /**
    * Linear interpolation from the origin to the destination value by a step.
    * If the step is less than zero, returns the origin. If the step is
    * greater than one, returns the destination.
    *
    * @param orig the origin value
    * @param dest the destination value
    * @param step the step
    *
    * @return the interpolated value
    */
   public static float lerp ( final float orig, final float dest,
      final float step ) {

      return step <= 0.0f ? orig : step >= 1.0f ? dest : ( 1.0f - step ) * orig
         + step * dest;
   }

   /**
    * Linear interpolation from the origin to the destination value by a step.
    * If the step is less than zero, returns the origin. If the step is
    * greater than one, returns the destination.
    *
    * @param orig the origin value
    * @param dest the destination value
    * @param step the step
    *
    * @return the interpolated value
    *
    * @see Utils#lerpUnclamped(float, float, float)
    */
   public static int lerp ( final int orig, final int dest, final float step ) {

      if ( step <= 0.0f ) { return orig; }
      if ( step >= 1.0f ) { return dest; }
      return Utils.lerpUnclamped(orig, dest, step);
   }

   /**
    * Linear interpolation from the origin to the destination value by a step.
    * Does not check to see if the step is beyond the range [0.0, 1.0] .
    *
    * @param orig the origin value
    * @param dest the destination value
    * @param step the step
    *
    * @return the interpolated value
    */
   public static float lerpUnclamped ( final float orig, final float dest,
      final float step ) {

      return ( 1.0f - step ) * orig + step * dest;
   }

   /**
    * Linear interpolation from the origin to the destination value by a step.
    * Does not check to see if the step is beyond the range [0.0, 1.0] .
    * Rounds the result to an integer.
    *
    * @param orig the origin value
    * @param dest the destination value
    * @param step the step
    *
    * @return the interpolated value
    *
    * @see Utils#round(float)
    */
   public static int lerpUnclamped ( final int orig, final int dest,
      final float step ) {

      return Utils.round( ( 1.0f - step ) * orig + step * dest);
   }

   /**
    * Finds the logarithm of a value in an arbitrary base. Equivalent to
    * finding the natural logarithm of a value, then dividing it by the
    * logarithm of the base. Returns zero if the base is less than or equal to
    * zero.
    *
    * @param a the value
    * @param b the base
    *
    * @return the evaluation
    *
    * @see Math#log(double)
    */
   public static float log ( final float a, final float b ) {

      if ( a > 0.0f && b > 0.0f ) {
         return ( float ) ( Math.log(a) / Math.log(b) );
      }
      return 0.0f;
   }

   /**
    * Maps an input value from an original range to a target range. If the
    * upper and lower bound of the original range are equal, will return the
    * value.
    *
    * @param value    the input value
    * @param lbOrigin lower bound of original range
    * @param ubOrigin upper bound of original range
    * @param lbDest   lower bound of destination range
    * @param ubDest   upper bound of destination range
    *
    * @return the mapped value
    */
   public static float map ( final float value, final float lbOrigin,
      final float ubOrigin, final float lbDest, final float ubDest ) {

      final float denom = ubOrigin - lbOrigin;
      if ( denom != 0.0f ) {
         return lbDest + ( ubDest - lbDest ) * ( ( value - lbOrigin ) / denom );
      }
      return value;
   }

   /**
    * Finds the greatest, or maximum, among a list of values. Returns negative
    * {@link Float#MAX_VALUE}, {@value Float#MAX_VALUE}, if the list's length
    * is zero.
    *
    * @param fs the list of values
    *
    * @return the maximum value
    */
   public static float max ( final float... fs ) {

      float max = -Float.MAX_VALUE;
      final int len = fs.length;
      for ( int i = 0; i < len; ++i ) { if ( fs[i] > max ) { max = fs[i]; } }
      return max;
   }

   /**
    * Finds the greater, or maximum, of two values. An alternative to
    * {@link Math#max(float, float)} .
    *
    * @param a the left operand
    * @param b the right operand
    *
    * @return the maximum value
    */
   public static float max ( final float a, final float b ) {

      return a > b ? a : b;
   }

   /**
    * Finds the greatest, or maximum, among three values.
    *
    * @param a the first value
    * @param b the second value
    * @param c the third value
    *
    * @return the maximum value
    */
   public static float max ( final float a, final float b, final float c ) {

      final float d = a > b ? a : b;
      return d > c ? d : c;
   }

   /**
    * Finds the greatest, or maximum, among three values.
    *
    * @param a the first value
    * @param b the second value
    * @param c the third value
    *
    * @return the maximum value
    */
   public static int max ( final int a, final int b, final int c ) {

      final int d = a > b ? a : b;
      return d > c ? d : c;
   }

   /**
    * Finds the least, or minimum, among a list of values. Returns
    * {@link Float#MAX_VALUE}, {@value Float#MAX_VALUE}, if the list's length
    * is zero.
    *
    * @param fs the list of values
    *
    * @return the minimum value
    */
   public static float min ( final float... fs ) {

      float min = Float.MAX_VALUE;
      final int len = fs.length;
      for ( int i = 0; i < len; ++i ) { if ( fs[i] < min ) { min = fs[i]; } }
      return min;
   }

   /**
    * Finds the lesser, or minimum, of two values. An alternative to
    * {@link Math#min(float, float)} .
    *
    * @param a the left operand
    * @param b the right operand
    *
    * @return the minimum value
    */
   public static float min ( final float a, final float b ) {

      return a < b ? a : b;
   }

   /**
    * Finds the least, or minimum, among three values.
    *
    * @param a the first value
    * @param b the second value
    * @param c the third value
    *
    * @return the minimum value
    */
   public static float min ( final float a, final float b, final float c ) {

      final float d = a < b ? a : b;
      return d < c ? d : c;
   }

   /**
    * Finds the least, or minimum, among three values.
    *
    * @param a the first value
    * @param b the second value
    * @param c the third value
    *
    * @return the minimum value
    */
   public static int min ( final int a, final int b, final int c ) {

      final int d = a < b ? a : b;
      return d < c ? d : c;
   }

   /**
    * Applies floor modulo to the operands. Uses the formula mod ( <em>a</em>,
    * <em>b</em> ) := <em>a</em> - <em>b</em> * floor ( <em>a</em> /
    * <em>b</em> ). When <em>b</em> is zero, returns <em>a</em>. If <em>b</em>
    * is one, use {@link Utils#mod1(float)} or <em>a</em> - floor(<em>a</em>)
    * instead.
    *
    * @param a the left operand
    * @param b the right operand
    *
    * @return the result
    *
    * @see Utils#modUnchecked(float, float)
    */
   public static float mod ( final float a, final float b ) {

      if ( b != 0.0f ) {
         final float quot = a / b;
         return a - b * ( quot > 0.0f ? ( int ) quot : quot < 0.0f
            ? ( int ) quot - 1.0f : 0.0f );
      }
      return a;
   }

   /**
    * Applies floor modulo to the operands. Returns the left operand when the
    * right operand is zero. An alternative to {@link Math#floorMod(int, int)}
    * .
    *
    * @param a the left operand
    * @param b the right operand
    *
    * @return the result
    */
   public static int mod ( final int a, final int b ) {

      /*
       * Floor mod is not the same as Euclidean remainder. See
       * https://harry.garrood.me/blog/integer-division/ .
       */

      // return b != 0 ? ( a % b + b ) % b : a;
      if ( b != 0 ) {
         final int result = a - b * ( a / b );
         return result < 0 ? result + b : result;
      }
      return a;
   }

   /**
    * Subtracts the floor of the input value from the value. Returns a
    * positive value in the range [0.0, 1.0] . Equivalent to GLSL's
    * <code>fract</code>.
    *
    * @param v the value
    *
    * @return the result
    */
   public static float mod1 ( final float v ) {

      return v > 0.0f ? v - ( int ) v : v < 0.0f ? v - ( ( int ) v - 1.0f )
         : 0.0f;
   }

   /**
    * A specialized version of modulo which shifts an angle in degrees to the
    * range [0.0, 360.0] .
    *
    * @param degrees the angle in degrees
    *
    * @return the wrapped angle
    *
    * @see Utils#floor(float)
    */
   public static float modDegrees ( final float degrees ) {

      // return degrees - 360.0f * Utils.floor(degrees * IUtils.ONE_360);
      final float dNorm = degrees * IUtils.ONE_360;
      return degrees - 360.0f * ( dNorm > 0.0f ? ( int ) dNorm : dNorm < 0.0f
         ? ( int ) dNorm - 1.0f : 0.0f );
   }

   /**
    * A specialized version of modulo which shifts an angle in radians to the
    * range [0.0, tau] .
    *
    * @param radians the angle in radians
    *
    * @return the wrapped radians
    *
    * @see Utils#mod(float, float)
    * @see Utils#floor(float)
    */
   public static float modRadians ( final float radians ) {

      // return radians - IUtils.TAU * Utils.floor(radians * IUtils.ONE_TAU);
      final float rNorm = radians * IUtils.ONE_TAU;
      return radians - IUtils.TAU * ( rNorm > 0.0f ? ( int ) rNorm : rNorm
         < 0.0f ? ( int ) rNorm - 1.0f : 0.0f );
   }

   /**
    * Applies floor modulo to the operands. Does not check if the right
    * operand is zero.
    *
    * @param a the left operand
    * @param b the right operand
    *
    * @return the result
    */
   public static float modUnchecked ( final float a, final float b ) {

      // return a - b * Utils.floor(a / b);
      final float q = a / b;
      return a - b * ( q > 0.0f ? ( int ) q : q < 0.0f ? ( int ) q - 1.0f
         : 0.0f );
   }

   /**
    * Finds the logical opposite of a float holding a boolean value. Returns 0
    * for non-zero inputs. Returns 1 for inputs equal to zero.
    *
    * @param v the input value
    *
    * @return the opposite
    */
   public static int not ( final float v ) {

      return v != 0.0f && v == v ? 0 : 1;
   }

   /**
    * Evaluates two floats like booleans, using the inclusive or (OR) logic
    * gate. Non-zero inputs evaluate to true, or 1. Zero evaluates to false,
    * or 0.
    *
    * @param a the left operand
    * @param b the right operand
    *
    * @return the evaluation
    */
   public static int or ( final float a, final float b ) {

      return ( a != 0.0f && a == a ? 1 : 0 ) | ( b != 0.0f && b == b ? 1 : 0 );
   }

   /**
    * Oscillates between [0.0, 1.0] based on an input step.
    *
    * @param step the step
    *
    * @return the oscillation
    */
   public static float pingPong ( final float step ) {

      return Utils.pingPong(0.0f, 1.0f, step, 1.0f);
   }

   /**
    * Oscillates between [0.0, 1.0] based on an input step and a pause factor.
    * When the pause is greater than 1.0, the value will be clamped to the
    * bound before returning to the other pole.
    *
    * @param step  the step
    * @param pause the pause factor
    *
    * @return the oscillation
    */
   public static float pingPong ( final float step, final float pause ) {

      return Utils.pingPong(0.0f, 1.0f, step, pause);
   }

   /**
    * Oscillates between a lower and upper bound based on an input step.
    *
    * @param lb   the lower bound
    * @param ub   the upper bound
    * @param step the step
    *
    * @return the oscillation
    */
   public static float pingPong ( final float lb, final float ub,
      final float step ) {

      return Utils.pingPong(lb, ub, step, 1.0f);
   }

   /**
    * Oscillates between a lower and upper bound based on an input step and a
    * pause factor. When the pause is greater than 1.0, the value will be
    * clamped to the bound before returning to the other pole.
    *
    * @param lb    the lower bound
    * @param ub    the upper bound
    * @param step  the step
    * @param pause the pause factor
    *
    * @return the oscillation
    */
   public static float pingPong ( final float lb, final float ub,
      final float step, final float pause ) {

      /*
       * Cheaper alternative: float z = 2.0f * (x - floor(x)) - 1.0f; return z >
       * 0.0f ? 1.0f - z : z < -0.0f ? 1.0f + z : 1.0f; By subjecting the
       * results to smoothStep, a sine wave can be approximated.
       */

      final float t = 0.5f + 0.5f * pause * Utils.scNorm(step - 0.5f);
      if ( t <= 0.0f ) { return lb; }
      if ( t >= 1.0f ) { return ub; }
      return ( 1.0f - t ) * lb + t * ub;
   }

   /**
    * Oscillates between a lower and upper bound based on an input step.
    *
    * @param lb   the lower bound
    * @param ub   the upper bound
    * @param step the step
    *
    * @return the oscillation
    */
   public static int pingPong ( final int lb, final int ub, final float step ) {

      return ( int ) Utils.pingPong(( float ) lb, ( float ) ub, step);
   }

   /**
    * Oscillates between a lower and upper bound based on an input step and a
    * pause factor. When the pause is greater than 1.0, the value will be
    * clamped to the bound before returning to the other pole.
    *
    * @param lb    the lower bound
    * @param ub    the upper bound
    * @param step  the step
    * @param pause the pause factor
    *
    * @return the oscillation
    */
   public static int pingPong ( final int lb, final int ub, final float step,
      final float pause ) {

      return ( int ) Utils.pingPong(( float ) lb, ( float ) ub, step, pause);
   }

   /**
    * Finds the single-precision of a number raised to the power of another.
    * Wraps {@link Math#pow(double, double)} .
    *
    * @param a left operand
    * @param b right operand
    *
    * @return the power
    *
    * @see Math#pow(double, double)
    */
   public static float pow ( final float a, final float b ) {

      return ( float ) Math.pow(a, b);
   }

   /**
    * Reduces the signal, or granularity, of a value. Defaults to signed
    * quantization.
    *
    * @param v      the value
    * @param levels the levels
    *
    * @return the quantized value
    */
   public static float quantize ( final float v, final int levels ) {

      return Utils.quantizeSigned(v, levels);
   }

   /**
    * Reduces the signal, or granularity, of a signed value. The quantization
    * is centered about zero. Applied to a vector, this yields a crenelated
    * effect. If the levels are zero, returns the value unaltered.
    *
    * @param v      the value
    * @param levels the levels
    *
    * @return the quantized value
    */
   public static float quantizeSigned ( final float v, final int levels ) {

      if ( levels == 0 ) { return v; }
      final float levf = levels < 0 ? -levels : levels;
      return Utils.quantizeSigned(v, levf, 1.0f / levf);
   }

   /**
    * Reduces the signal, or granularity, of an unsigned value. The
    * quantization treats zero as a left edge. Applied to a color, this yields
    * a posterization effect. If the levels are 1 or -1, returns the value
    * unaltered.
    *
    * @param v      the value
    * @param levels the levels
    *
    * @return the quantized value
    */
   public static float quantizeUnsigned ( final float v, final int levels ) {

      if ( levels == 1 || levels == -1 ) { return v; }
      final float levf = levels < 0 ? -levels : levels;
      return Utils.quantizeUnsigned(v, levf, 1.0f / ( levf - 1.0f ));
   }

   /**
    * Rounds a value to an integer based on whether its fractional portion is
    * greater than or equal to plus or minus 0.5 .
    *
    * @param v the input value
    *
    * @return the rounded value
    */
   public static int round ( final float v ) {

      return v < -0.0f ? ( int ) ( v - 0.5f ) : v > 0.0f ? ( int ) ( v + 0.5f )
         : 0;
   }

   /**
    * Rounds a value to a number of places right of the decimal point.
    * Promotes the float to a double, rounds it, then demotes back to a float.
    *
    * @param v      the value
    * @param places the number of places
    *
    * @return the rounded value
    */
   public static float round ( final float v, final int places ) {

      if ( places < 1 ) { return Utils.round(v); }
      if ( places > 7 ) { return v; }

      int n = 10;
      for ( int i = 1; i < places; ++i ) { n *= 10; }

      final float nf = n;
      return Utils.round(v * nf) / nf;
   }

   /**
    * A helper method to facilitate the approximate sine and cosine of an
    * angle with single precision real numbers. The radians supplied to this
    * function should be normalized through division by tau. Subtract
    * <code>0.25</code> from the input value to return the sine instead of the
    * cosine.<br>
    * <br>
    * This is based on the algorithm described in
    * <a href="https://developer.download.nvidia.com/cg/sin.html">Nvidia Cg
    * 3.1 Toolkit Documentation</a> .
    *
    * @param normRad the normalized radians
    *
    * @return the approximate value
    */
   @Experimental
   public static float scNorm ( final float normRad ) {

      // float r1y = Utils.mod1(normRad);
      float r1y = normRad > 0.0f ? normRad - ( int ) normRad : normRad < 0.0f
         ? normRad - ( ( int ) normRad - 1.0f ) : 0.0f;

      final boolean r2x = r1y < 0.25f;
      float r1x = 0.0f;
      if ( r2x ) {
         final float r0x = r1y * r1y;
         r1x = 24.980804f * r0x - 60.14581f;
         r1x = r1x * r0x + 85.45379f;
         r1x = r1x * r0x - 64.939354f;
         r1x = r1x * r0x + 19.739208f;
         r1x = r1x * r0x - 1.0f;
      }

      final boolean r2z = r1y >= 0.75f;
      float r1z = 0.0f;
      if ( r2z ) {
         float r0z = 1.0f - r1y;
         r0z = r0z * r0z;
         r1z = 24.980804f * r0z - 60.14581f;
         r1z = r1z * r0z + 85.45379f;
         r1z = r1z * r0z - 64.939354f;
         r1z = r1z * r0z + 19.739208f;
         r1z = r1z * r0z - 1.0f;
      }

      float r0y = 0.5f - r1y;
      r1y = 0.0f;
      if ( r1y >= -9.0f ^ ( r2x || r2z ) ) {
         r0y = r0y * r0y;
         r1y = 60.14581f - r0y * 24.980804f;
         r1y = r1y * r0y - 85.45379f;
         r1y = r1y * r0y + 64.939354f;
         r1y = r1y * r0y - 19.739208f;
         r1y = r1y * r0y + 1.0f;
      }

      return -r1x - r1z - r1y;
   }

   /**
    * An alternative to the {@link Math#signum(float)} function. Returns the
    * integer 0 for both -0.0 (signed negative zero) and 0.0 (signed positive
    * zero).
    *
    * @param v the value
    *
    * @return the sign
    *
    * @see Math#signum(float)
    */
   public static int sign ( final float v ) {

      return v < -0.0f ? -1 : v > 0.0f ? 1 : 0;
   }

   /**
    * Finds the single precision sine of an angle in radians. Returns a value
    * in the range [-1.0, 1.0] .<br>
    * <br>
    * Wraps around {@link Math#sin(double)}.
    *
    * @param radians the angle in radians
    *
    * @return the sine of the angle
    */
   public static float sin ( final float radians ) {

      return ( float ) Math.sin(radians);
   }

   /**
    * Eases between an origin and destination by a step in [0.0, 1.0] .
    *
    * @param orig the origin
    * @param dest the destination
    * @param step the step
    *
    * @return the eased value
    */
   public static float smoothStep ( final float orig, final float dest,
      final float step ) {

      if ( step <= 0.0f ) { return orig; }
      if ( step >= 1.0f ) { return dest; }
      final float t = step * step * ( 3.0f - ( step + step ) );
      return ( 1.0f - t ) * orig + t * dest;
   }

   /**
    * Finds the approximate square root of a value. Returns 0.0 when the value
    * is less than zero. An alternative to {@link Math#sqrt(double)} . Use
    * {@link Utils#sqrtUnchecked(float)} when the value is known to be
    * positive. Use {@link Complex#sqrt(float, Complex)} when the input may be
    * negative and a complex output is desired.
    *
    * @param v the value
    *
    * @return the square root
    *
    * @see Utils#sqrtUnchecked(float)
    */
   public static float sqrt ( final float v ) {

      return v > 0.0f ? Utils.sqrtUnchecked(v) : 0.0f;
   }

   /**
    * Finds the approximate square root of a value. Does so by multiplying the
    * value by its inverse square root.
    *
    * @param v the value
    *
    * @return the square root
    *
    * @see Utils#invSqrtUnchecked(float)
    */
   public static float sqrtUnchecked ( final float v ) {

      return v * Utils.invSqrtUnchecked(v);
   }

   /**
    * Swaps the endianness of an integer. For example, a color in the AARRGGBB
    * format, <code>0xffaabbcc</code>, will yield the BBGGRRAA color,
    * <code>0xccbbaaff</code>.<br>
    * <br>
    * An alias for {@link Integer#reverseBytes(int)}.
    *
    * @param v the value
    *
    * @return the swap
    */
   public static int swapEndian ( final int v ) {

      return v << 24 | ( v & '\uff00' ) << 8 | v >>> 8 & '\uff00' | v >>> 24;
   }

   /**
    * Finds the tangent of an angle. Equivalent to dividing the sine of the
    * angle by the cosine. An alternative to the double precision
    * {@link Math#tan(double)} , this function uses single-precision numbers.
    *
    * @param radians the angle in radians
    *
    * @return the tangent
    *
    * @see Utils#scNorm(float)
    */
   public static float tan ( final float radians ) {

      final float nrmRad = radians * IUtils.ONE_TAU;
      final float cost = Utils.scNorm(nrmRad);
      return cost != 0.0f ? Utils.scNorm(nrmRad - 0.25f) / cost : 0.0f;
   }

   /**
    * A quick, dirty representation of a single precision real number as a
    * String to a number of places. Truncates the final digit right of the
    * decimal place. Edge cases:
    * <ul>
    * <li>When the number of places is less than one, returns the String of
    * the truncated value.</li>
    * <li>When the value is not a number ( {@link Float#NaN} ), returns
    * "0.0".</li>
    * <li>When the value is greater than {@link Float#MAX_VALUE}, returns the
    * max value; when less than {@link Float#MIN_VALUE}, the minimum value.
    * This truncates positive and negative infinities to these bounds.</li>
    * <li>When the integral contains so many digits that accurate
    * representation is unlikely, such as with scientific notation, defers to
    * {@link Float#toString(float)} .</li>
    * </ul>
    * Intended to serve as an alternative to
    * {@link String#format(String, Object...)}, which is very slow, and
    * DecimalFormat, which extrapolates values beyond the last decimal place.
    *
    * @param v      the real number
    * @param places the number of decimal places
    *
    * @return the string
    */
   public static String toFixed ( final float v, final int places ) {

      return Utils.toFixed(new StringBuilder(16), v, places).toString();
   }

   /**
    * Returns an integer formatted as a string padded by initial zeroes.
    *
    * @param v      the integer
    * @param places the number of places
    *
    * @return the string
    */
   public static String toPadded ( final int v, final int places ) {

      return Utils.toPadded(new StringBuilder(16), v, places).toString();
   }

   /**
    * Returns a String representation of a one dimensional array of
    * <code>float</code>s.
    *
    * @param arr    the array
    * @param places the print precision
    *
    * @return the String
    */
   public static String toString ( final float[] arr, final int places ) {

      return Utils.toString(new StringBuilder(256), arr, places).toString();
   }

   /**
    * Returns a String representation of a one dimensional array of
    * <code>int</code>s.
    *
    * @param arr     the array
    * @param padding the digit padding
    *
    * @return the String
    */
   public static String toString ( final int[] arr, final int padding ) {

      return Utils.toString(new StringBuilder(256), arr, padding).toString();
   }

   /**
    * Truncates the input value. This is an alias for explicitly casting a
    * float to an integer, then implicitly casting the integral to a float.
    *
    * @param v the input value
    *
    * @return the integral
    */
   public static float trunc ( final float v ) { return ( int ) v; }

   /**
    * An alias for {@link Byte#toUnsignedInt(byte)} . Converts a signed byte
    * in the range [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] to an
    * unsigned byte in the range [0, 255], promoted to an integer. Useful when
    * working with colors.<br>
    * <br>
    * Defined for cross-language comparison with C#, which uses signed and
    * unsigned versions of primitive data types.
    *
    * @param v the signed byte
    *
    * @return the unsigned byte, promoted
    */
   public static int ubyte ( final byte v ) { return v & 0xff; }

   /**
    * An alias for {@link Integer#toUnsignedLong(int)}. Converts a signed
    * integer in the range [{@value Integer#MIN_VALUE},
    * {@value Integer#MAX_VALUE}] to an unsigned integer in the range [0,
    * 4294967295], promoted to a long. Useful when working with colors.<br>
    * <br>
    * Defined for cross-language comparison with C#, which uses signed and
    * unsigned versions of primitive data types.
    *
    * @param v the signed integer
    *
    * @return the unsigned integer, promoted
    */
   public static long uint ( final int v ) { return v & 0xffffffffL; }

   /**
    * Wraps a value around a periodic range as defined by an upper and lower
    * bound. The lower bound is inclusive; the upper bound is exclusive.
    * Returns the value unchanged if the range is zero.
    *
    * @param v  the value
    * @param lb the lower bound
    * @param ub the upper bound
    *
    * @return the wrapped value
    */
   public static float wrap ( final float v, final float lb, final float ub ) {

      final float range = ub - lb;
      if ( range != 0.0f ) {
         final float b = ( v - lb ) / range;
         return v - range * ( b > 0.0f ? ( int ) b : b < 0.0f ? ( int ) b - 1.0f
            : 0.0f );
      }
      return v;
   }

   /**
    * Evaluates two floats like booleans using the exclusive or (XOR) logic
    * gate. Non-zero inputs evaluate to true, or 1. Zero evaluates to false,
    * or 0.
    *
    * @param a the left operand
    * @param b the right operand
    *
    * @return the evaluation
    */
   public static int xor ( final float a, final float b ) {

      return ( a != 0.0f && a == a ? 1 : 0 ) ^ ( b != 0.0f && b == b ? 1 : 0 );
   }

   /**
    * An internal helper function. Reduces the signal, or granularity, of a
    * signed value. The quantization is centered about zero. Applied to a
    * vector, this yields a crenelated effect.
    *
    * @param v      the value
    * @param levels the levels
    *
    * @return the quantized value
    *
    * @see Utils#floor(float)
    */
   static float quantizeSigned ( final float v, final float levels,
      final float delta ) {

      return Utils.floor(0.5f + v * levels) * delta;
   }

   /**
    * An internal helper function. Reduces the signal, or granularity, of an
    * unsigned value. The quantization treats zero as a left edge. Applied to
    * a color, this yields a posterization effect.
    *
    * @param v      the value
    * @param levels the levels
    *
    * @return the quantized value
    *
    * @see Utils#ceil(float)
    * @see Utils#max(float, float)
    */
   static float quantizeUnsigned ( final float v, final float levels,
      final float delta ) {

      return Utils.max(0.0f, ( Utils.ceil(v * levels) - 1.0f ) * delta);
   }

   /**
    * An internal helper function to the {@link Utils#toFixed(float, int)}
    * method and other String representation functions. Appends to a
    * {@link StringBuilder} passed in by reference.
    *
    * @param sb     the string builder
    * @param v      the real number
    * @param places the number of decimal places
    *
    * @return the string builder
    */
   static StringBuilder toFixed ( final StringBuilder sb, final float v,
      final int places ) {

      final int raw = Float.floatToRawIntBits(v);
      switch ( raw ) {
         case 0x0: /* Positive zero. */
         case 0x80000000: /* Negative zero. */
         case 0x7fc00000: /* Not a number (NaN). */
            return sb.append("0.0");

         case 0xff800000: /* Negative infinity. */
         case 0xff7fffff:
            return sb.append("-3.4028235E38");

         case 0x1: /* Minimum value. */
            return sb.append("1.4E-45");

         case 0x7f800000: /* Positive infinity. */
         case 0x7f7fffff: /* Max value. */
            return sb.append("3.4028235E38");

         default:
      }

      if ( places < 0 ) { return sb.append(Integer.toString(( int ) v)); }
      if ( places < 1 ) { return sb.append(Float.toString(( int ) v)); }

      final float sign = Float.intBitsToFloat(raw & -2147483648 | 1065353216);
      final float abs = Float.intBitsToFloat(raw & 2147483647);
      final int trunc = ( int ) abs;

      /* Append integral to StringBuilder. */
      final int oldLen = sb.length();
      int len = 0;
      if ( sign < -0.0f ) {
         sb.append('-').append(trunc);
         len = sb.length() - oldLen - 1;
      } else {
         sb.append(trunc);
         len = sb.length() - oldLen;
      }
      sb.append('.');

      /*
       * Hard-coded limit on the number of worthwhile decimal places beyond
       * which single precision is no longer worth representing accurately.
       */
      final int maxPlaces = 9 - len;

      /*
       * The integral has so many digits that it has consumed the allotment.
       * (Might be scientific notation?)
       */
      if ( maxPlaces < 1 ) { return sb.append(Float.toString(v)); }

      final int vetPlaces = places < maxPlaces ? places : maxPlaces;
      float frac = abs - trunc;

      /* Truncation. */
      for ( int i = 0; i < vetPlaces; ++i ) {
         frac *= 10.0f;
         final int tr = ( int ) frac;
         frac -= tr;
         sb.append(tr);
      }

      return sb;
   }

   /**
    * An internal helper function to the {@link Utils#toPadded(int, int)}
    * method and other String representation functions. Appends to a
    * {@link StringBuilder} passed in by reference.
    *
    * @param sb     the string builder
    * @param v      the integer
    * @param places the number of places
    *
    * @return the string builder
    */
   static StringBuilder toPadded ( final StringBuilder sb, final int v,
      final int places ) {

      /*
       * Double precision is needed to preserve accuracy. The max integer value
       * is 2147483647, which is 10 digits long. The sign needs to be flipped
       * because working with positive absolute value would allow
       * Integer#MIN_VALUE to overflow to zero.
       */

      final boolean isNeg = v < 0;
      int nAbsVal = isNeg ? v : -v;

      final int[] digits = new int[10];
      int filled = 0;
      while ( nAbsVal < 0 ) {
         final double y = nAbsVal * 0.1d;
         nAbsVal = ( int ) y;
         digits[filled] = -( int ) ( ( y - nAbsVal ) * 10.0d - 0.5d );
         ++filled;
      }

      if ( isNeg ) { sb.append('-'); }
      int vplaces = places < 1 ? 1 : places;
      vplaces = filled > vplaces ? filled : vplaces;
      for ( int n = vplaces - 1; n > -1; --n ) { sb.append(digits[n]); }

      return sb;
   }

   /**
    * An internal helper function. Appends to a {@link StringBuilder} passed
    * in by reference.
    *
    * @param sb     the string builder
    * @param arr    the array
    * @param places the number of places
    *
    * @return the string builder
    *
    * @see Utils#toFixed(StringBuilder, float, int)
    */
   static StringBuilder toString ( final StringBuilder sb, final float[] arr,
      final int places ) {

      final int len = arr.length;
      final int last = len - 1;

      sb.append('[').append(' ');
      for ( int i = 0; i < last; ++i ) {
         Utils.toFixed(sb, arr[i], places);
         sb.append(',').append(' ');
      }
      Utils.toFixed(sb, arr[last], places);
      sb.append(' ').append(']');
      return sb;
   }

   /**
    * An internal helper function. Appends to a {@link StringBuilder} passed
    * in by reference.
    *
    * @param sb      the string builder
    * @param arr     the array
    * @param padding the padding
    *
    * @return the string builder
    *
    * @see Utils#toPadded(StringBuilder, int, int)
    */
   static StringBuilder toString ( final StringBuilder sb, final int[] arr,
      final int padding ) {

      final int len = arr.length;
      final int last = len - 1;

      sb.append('[').append(' ');
      for ( int i = 0; i < last; ++i ) {
         Utils.toPadded(sb, arr[i], padding);
         sb.append(',').append(' ');
      }
      Utils.toPadded(sb, arr[last], padding);
      sb.append(' ').append(']');
      return sb;
   }

   /**
    * A functional interface for an easing function which interpolates an an
    * array.
    *
    * @param <T> the parameter type
    */
   public interface EasingFuncArr < T > {

      /**
       * Apply the function.
       *
       * @param arr    the array
       * @param step   the step
       * @param target the target object
       *
       * @return the eased object
       */
      T apply ( final T[] arr, Float step, T target );

   }

   /**
    * A functional interface for an easing function which interpolates an
    * object from an origin to a destination by a float, with a final output
    * parameter.
    *
    * @param <T> the parameter type
    */
   public interface EasingFuncObj < T > extends QuadFunction < T, T, Float, T,
      T > {

      /**
       * Apply the function.
       *
       * @param orig   the origin
       * @param dest   the destination
       * @param step   the step
       * @param target the target object
       *
       * @return the eased object
       */
      @Override
      T apply ( final T orig, final T dest, final Float step, T target );

   }

   /**
    * A functional interface for an easing function which interpolates a
    * primitive data type from an origin to a destination by a float.
    *
    * @param <T> the parameter type
    */
   public interface EasingFuncPrm < T > extends TriFunction < T, T, Float, T > {

      /**
       * Apply the function.
       *
       * @param orig the origin
       * @param dest the destination
       * @param step the step
       *
       * @return the eased object
       */
      @Override
      T apply ( final T orig, final T dest, final Float step );

   }

   /**
    * Linear interpolation for a periodic value in the counter-clockwise
    * direction.
    */
   public static class LerpCCW extends PeriodicEasing {

      /**
       * Constructs the lerp CCW functional object with a default range,
       * {@link IUtils#TAU} ({@value IUtils#TAU}).
       */
      public LerpCCW ( ) {}

      /**
       * Constructs the lerp CCW functional object with a specified range.
       *
       * @param range the range of the period
       */
      public LerpCCW ( final float range ) { super(range); }

      /**
       * Applies the lerp CCW function.
       *
       * @param step the step
       *
       * @return the eased value
       *
       * @see Utils#modUnchecked(float, float)
       */
      @Override
      protected float applyPartial ( final float step ) {

         if ( this.diff == 0.0f ) { return this.o; }
         if ( this.oGtd ) {
            return Utils.modUnchecked( ( 1.0f - step ) * this.o + step
               * ( this.d + this.range ), this.range);
         }
         return ( 1.0f - step ) * this.o + step * this.d;
      }

   }

   /**
    * Linear interpolation for a periodic value in the clockwise direction.
    */
   public static class LerpCW extends PeriodicEasing {

      /**
       * Constructs the lerp CW functional object with a default range,
       * {@link IUtils#TAU} ({@value IUtils#TAU}).
       */
      public LerpCW ( ) {}

      /**
       * Constructs the lerp CW functional object with a specified range.
       *
       * @param range the range of the period
       */
      public LerpCW ( final float range ) { super(range); }

      /**
       * Applies the lerp CW function.
       *
       * @param step the step
       *
       * @return the eased value
       *
       * @see Utils#modUnchecked(float, float)
       */
      @Override
      protected float applyPartial ( final float step ) {

         if ( this.diff == 0.0f ) { return this.d; }
         if ( this.oLtd ) {
            return Utils.modUnchecked( ( 1.0f - step ) * ( this.o + this.range )
               + step * this.d, this.range);
         }
         return ( 1.0f - step ) * this.o + step * this.d;
      }

   }

   /**
    * Linear interpolation for a periodic value in the nearest direction.
    */
   public static class LerpNear extends PeriodicEasing {

      /**
       * Constructs the lerp near functional object with a default range,
       * {@link IUtils#TAU} ({@value IUtils#TAU}).
       */
      public LerpNear ( ) {}

      /**
       * Constructs the lerp near functional object with a specified range.
       *
       * @param range the range of the period
       */
      public LerpNear ( final float range ) { super(range); }

      /**
       * Applies the lerp near function.
       *
       * @param step the step
       *
       * @return the eased value
       *
       * @see Utils#modUnchecked(float, float)
       */
      @Override
      protected float applyPartial ( final float step ) {

         if ( this.diff == 0.0f ) { return this.o; }
         if ( this.oLtd && this.diff > this.halfRange ) {
            return Utils.modUnchecked( ( 1.0f - step ) * ( this.o + this.range )
               + step * this.d, this.range);
         }
         if ( this.oGtd && this.diff < -this.halfRange ) {
            return Utils.modUnchecked( ( 1.0f - step ) * this.o + step
               * ( this.d + this.range ), this.range);
         }
         return ( 1.0f - step ) * this.o + step * this.d;
      }

   }

   /**
    * An abstract class to cover the easing periodic of values, such as angles
    * and hues in HSV color space. Allows the range to be set to, for example,
    * TAU radians, 360.0 degrees or 1.0 color channel.
    */
   public abstract static class PeriodicEasing implements Utils.EasingFuncPrm <
      Float > {

      /**
       * The stop angle, modulated by the range.
       */
      protected float d = 0.0f;

      /**
       * The difference between the stop and start angle.
       */
      protected float diff = 0.0f;

      /**
       * One-half of the range of the period.
       */
      protected float halfRange = 0.5f;

      /**
       * The start angle, modulated by the range.
       */
      protected float o = 0.0f;

      /**
       * Whether or not the start angle is greater than the stop angle.
       */
      protected boolean oGtd = false;

      /**
       * Whether or not the start angle is less than the stop angle.
       */
      protected boolean oLtd = false;

      /**
       * The range of the period.
       */
      protected float range = 1.0f;

      /**
       * Constructs the easing functional object with a default range, TAU.
       */
      protected PeriodicEasing ( ) {

         this.setRange(PeriodicEasing.DEFAULT_RANGE);
      }

      /**
       * Constructs the easing functional object with a specified range.
       *
       * @param range the range of the period
       */
      protected PeriodicEasing ( final float range ) {

         this.setRange(range);
      }

      /**
       * Applies the easing function. The abstract class's implementation check
       * to see if the step is out of bounds, [0.0, 1.0] and if mod(origin,
       * range) is equal to mod(dest, range). The origin is returned when the
       * step is less than 0.0; the destination, when the step is greater than
       * 1.0.
       *
       * @see PeriodicEasing#eval(float, float)
       */
      @Override
      public Float apply ( final Float orig, final Float dest,
         final Float step ) {

         this.eval(orig, dest);
         final float tf = step;
         if ( tf <= 0.0f ) { return this.o; }
         if ( tf >= 1.0f ) { return this.d; }
         return this.applyPartial(tf);
      }

      /**
       * Gets the range of the easing function.
       *
       * @return the range
       */
      public float getRange ( ) { return this.range; }

      /**
       * Sets the range of the easing function. The range should be a positive
       * non-zero value.
       *
       * @param range the range
       *
       * @see Utils#abs(float)
       * @see Utils#max(float, float)
       */
      public void setRange ( final float range ) {

         this.range = Utils.max(Utils.abs(range), IUtils.EPSILON);
         this.halfRange = this.range * 0.5f;
      }

      /**
       * Returns the simple name of this class, allowing the functional
       * interface to be identified without being directly accessible.
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

      /**
       * Applies the easing function without checking whether the step is out of
       * bounds, [0.0, 1.0] .<br>
       * <br>
       * This function needs to be protected because the public apply above
       * verifies the data upon which applyUnclamped operates.
       *
       * @param step the step
       *
       * @return the interpolated value
       */
      protected abstract float applyPartial ( final float step );

      /**
       * A helper function which mutates fields {@link o}, {@link d},
       * {@link diff}, {@link oLtd} and {@link oGtd}. This mods the origin and
       * destination by the range. It then finds the signed distance between the
       * mod origin and destination . Lastly, it evaluates which of the two is
       * greater than the other.
       *
       * @param orig the origin value
       * @param dest the destination value
       *
       * @see Utils#modUnchecked(float, float)
       */
      protected void eval ( final float orig, final float dest ) {

         this.o = Utils.modUnchecked(orig, this.range);
         this.d = Utils.modUnchecked(dest, this.range);
         this.diff = this.d - this.o;
         this.oLtd = this.o < this.d;
         this.oGtd = this.o > this.d;
      }

      /**
       * The default range of the period, \u03c4 .
       */
      public static final float DEFAULT_RANGE = IUtils.TAU;

   }

   /**
    * A functional interface for functions with four input parameters.
    *
    * @param <T> first parameter
    * @param <U> second parameter
    * @param <V> third parameter
    * @param <W> fourth parameter
    * @param <R> return type
    */
   public interface QuadFunction < T, U, V, W, R > {

      /**
       * Apply the function.
       *
       * @param t first parameter
       * @param u second parameter
       * @param v third parameter
       * @param w fourth parameter
       *
       * @return return type
       */
      R apply ( T t, U u, V v, W w );

   }

   /**
    * A functional interface for functions with three input parameters.
    *
    * @param <T> first parameter
    * @param <U> second parameter
    * @param <V> third parameter
    * @param <R> return type
    */
   public interface TriFunction < T, U, V, R > {

      /**
       * Apply the function.
       *
       * @param t first parameter
       * @param u second parameter
       * @param v third parameter
       *
       * @return result
       */
      R apply ( T t, U u, V v );

   }

}
