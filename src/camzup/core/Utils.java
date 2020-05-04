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
    * Finds the absolute value of a double precision real number. An alias for
    * {@link Math#abs(double)} . Relies on bit-masking to remove the sign bit.
    *
    * @param value the input value
    *
    * @return the absolute value
    *
    * @see Double#longBitsToDouble(long)
    * @see Double#doubleToLongBits(double)
    */
   public static double abs ( final double value ) {

      return Double.longBitsToDouble(Double.doubleToLongBits(value) << 1 >>> 1);
   }

   /**
    * Finds the absolute value of a single precision real number. An alias for
    * {@link Math#abs(float)}. Relies on bit-masking to remove the sign bit.
    * Equivalent to max(-a, a) .
    *
    * @param value the input value
    *
    * @return the absolute value
    *
    * @see Float#intBitsToFloat(int)
    * @see Float#floatToRawIntBits(float)
    */
   public static float abs ( final float value ) {

      return Float.intBitsToFloat(0x7fffffff & Float.floatToRawIntBits(value));
   }

   /**
    * A bounds checked approximation of the arc cosine for single precision
    * real numbers. Returns a value in the range [0.0, \u03c0] : \u03c0 when
    * the input is less than or equal to -1.0; \u03c0 / 2.0 when the input is
    * 0.0; 0.0 when the input is greater than or equal to 1.0.<br>
    * <br>
    * Based on the algorithm at the
    * <a href= "https://developer.download.nvidia.com/cg/acos.html">Nvidia Cg
    * 3.1 Toolkit Documentation</a>. This cites M. Abramowitz and I.A. Stegun,
    * Eds., <em>Handbook of Mathematical Functions</em>, possibly p. 83, which
    * cites <em>Approximations for Digital Computers</em> by C. Hastings, Jr.
    *
    * @param value the input value
    *
    * @return the angle in radians
    *
    * @see Utils#sqrt(float)
    *
    * @author M. Abramowitz
    * @author C. Hastings, Jr
    * @author I. A. Stegun
    */
   public static float acos ( final float value ) {

      if ( value <= -1.0f ) { return IUtils.PI; }
      if ( value >= 1.0f ) { return 0.0f; }

      final boolean ltZero = value < 0.0f;
      final float x = ltZero ? -value : value;
      float ret = -0.0187293f;
      ret *= x;
      ret += 0.074261f;
      ret *= x;
      ret -= 0.2121144f;
      ret *= x;
      ret += IUtils.HALF_PI;
      ret *= Utils.sqrt(1.0f - x);
      return ltZero ? IUtils.PI - ret : ret;
   }

   /**
    * Evaluates two floats like booleans using the AND logic gate.
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
    * {@link IUtils#DEFAULT_EPSILON}, {@value IUtils#DEFAULT_EPSILON}. Does
    * not handle edge cases.
    *
    * @param a the left comparisand
    * @param b the right comparisand
    *
    * @return the evaluation
    *
    * @see Utils#approx(float, float, float)
    */
   public static boolean approx ( final float a, final float b ) {

      return Utils.approx(a, b, IUtils.DEFAULT_EPSILON);
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
    * numbers. Returns a value in the range [-\u03c0 / 2.0, \u03c0 / 2.0]:
    * -\u03c0 / 2.0 when the input is less than or equal to -1.0; 0.0 when the
    * input is 0.0; \u03c0 / 2.0 when the input is greater than or equal to
    * 1.0.<br>
    * <br>
    * Based on the algorithm at the
    * <a href= "https://developer.download.nvidia.com/cg/asin.html">Nvidia Cg
    * 3.1 Toolkit Documentation</a> . This cites M. Abramowitz and I.A.
    * Stegun, Eds., <em>Handbook of Mathematical Functions</em>, possibly p.
    * 83, which cites <em>Approximations for Digital Computers</em> by C.
    * Hastings, Jr.
    *
    * @param value the input value
    *
    * @return the angle in radians
    *
    * @see Utils#sqrt(float)
    *
    * @author M. Abramowitz
    * @author C. Hastings, Jr
    * @author I. A. Stegun
    */
   public static float asin ( final float value ) {

      if ( value <= -1.0f ) { return -IUtils.HALF_PI; }
      if ( value >= 1.0f ) { return IUtils.HALF_PI; }

      final boolean ltZero = value < 0.0f;
      final float x = ltZero ? -value : value;
      float ret = -0.0187293f;
      ret *= x;
      ret += 0.074261f;
      ret *= x;
      ret -= 0.2121144f;
      ret *= x;
      ret += IUtils.HALF_PI;
      ret = IUtils.HALF_PI - ret * Utils.sqrt(1.0f - x);
      return ltZero ? -ret : ret;
   }

   /**
    * Finds a single precision approximation of a signed angle given a
    * vertical and horizontal component. The vertical component
    * <em>precedes</em> the horizontal. The return value falls in the range
    * [-\u03c0, \u03c0] . An alternative to {@link Math#atan2(double, double)}
    * . Based on the algorithm at the
    * <a href= "https://developer.download.nvidia.com/cg/atan2.html">Nvidia Cg
    * 3.1 Toolkit Documentation</a> .
    *
    * @param y the y coordinate (the ordinate)
    * @param x the x coordinate (the abscissa)
    *
    * @return the angle in radians
    */
   public static float atan2 ( final float y, final float x ) {

      final boolean yLtZero = y < 0.0f;
      final boolean xLtZero = x < 0.0f;
      final float yAbs = yLtZero ? -y : y;
      final float xAbs = xLtZero ? -x : x;

      final boolean yGtX = yAbs > xAbs;
      float t0 = yGtX ? yAbs : xAbs;
      if ( t0 == 0.0f ) { return 0.0f; }
      float t2 = ( yGtX ? xAbs : yAbs ) / t0;

      /*
       * When stored independently as floats, some of these magic numbers are
       * truncated to slightly different values.
       */
      final float t3 = t2 * t2;
      t0 = -0.01348047f;
      t0 = t0 * t3 + 0.057477314f;
      t0 = t0 * t3 - 0.121239071f; /* 0.12123907f */
      t0 = t0 * t3 + 0.195635925f; /* 0.19563593f */
      t0 = t0 * t3 - 0.332994597f; /* 0.3329946f */
      t0 = t0 * t3 + 0.99999563f; /* 0.99999565f */
      t2 = t0 * t2;
      t2 = yGtX ? IUtils.HALF_PI - t2 : t2;
      t2 = xLtZero ? IUtils.PI - t2 : t2;
      return yLtZero ? -t2 : t2;
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
    * @param a the byte
    *
    * @return the bit array
    */
   public static byte[] bitslm ( final byte a ) {

      final byte[] result = new byte[Byte.SIZE];
      for ( int i = 0; i < Byte.SIZE; ++i ) {
         result[i] = ( byte ) ( a >> i & 1 );
      }

      return result;
   }

   /**
    * Gets an array of bits from a byte, ordered from most to least
    * significant digit, as a literal would be written. For example,
    * <code>0b01010011</code> would yield the array
    * <code>{ 0, 1, 0, 1, 0, 0, 1, 1 }</code> .
    *
    * @param a the byte
    *
    * @return the bit array
    */
   public static byte[] bitsml ( final byte a ) {

      final byte[] result = new byte[8];
      for ( int i = 7, j = 0; i > -1; --i, ++j ) {
         result[j] = ( byte ) ( a >> i & 1 );
      }
      return result;
   }

   /**
    * Raises a real number to the next greatest integer. An alternative to
    * {@link Math#ceil(double)} . ceil ( <em>x</em> ) = - floor ( -<em>x</em>
    * ) .
    *
    * @param value the input value
    *
    * @return the raised value.
    */
   public static float ceil ( final float value ) {

      return value > 0.0f ? ( int ) value + 1.0f : value < 0.0f ? ( int ) value
         : 0.0f;
   }

   /**
    * Raises a real number to the next greatest integer.
    *
    * @param x the input value
    *
    * @return the integer
    */
   public static int ceilToInt ( final float x ) {

      final int y = ( int ) x;
      return x > y ? y + 1 : y;
   }

   /**
    * Clamps a real number between a lower and an upper bound.
    *
    * @param value      the input value
    * @param lowerBound the upper bound
    * @param upperBound the lower bound
    *
    * @return the clamped value
    */
   public static double clamp ( final double value, final double lowerBound,
      final double upperBound ) {

      return value < lowerBound ? lowerBound : value > upperBound ? upperBound
         : value;
   }

   /**
    * Clamps a real number between a lower and an upper bound.
    *
    * @param value      the input value
    * @param lowerBound the upper bound
    * @param upperBound the lower bound
    *
    * @return the clamped value
    */
   public static float clamp ( final float value, final float lowerBound,
      final float upperBound ) {

      return value < lowerBound ? lowerBound : value > upperBound ? upperBound
         : value;
   }

   /**
    * Clamps an integer between a lower and an upper bound.
    *
    * @param value      the input value
    * @param lowerBound the lower bound
    * @param upperBound the upper bound
    *
    * @return the clamped value
    */
   public static int clamp ( final int value, final int lowerBound,
      final int upperBound ) {

      return value < lowerBound ? lowerBound : value > upperBound ? upperBound
         : value;
   }

   /**
    * Clamps a value to the range [0.0, 1.0] .
    *
    * @param value the input value
    *
    * @return the clamped value
    *
    * @see Utils#clamp(float, float, float)
    */
   public static float clamp01 ( final float value ) {

      return value < 0.0f ? 0.0f : value > 1.0f ? 1.0f : value;
   }

   /**
    * Returns the first floating-point argument with the sign of the second
    * floating-point argument. An alias of {@link Math#copySign(float, float)}
    * .
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
         | Float.floatToRawIntBits(magnitude) & 2147483647);
   }

   /**
    * Finds the single-precision cosine of an angle in radians. Returns a
    * value in the range [-1.0, 1.0] .
    *
    * @param radians the angle in radians
    *
    * @return the cosine of the angle
    *
    * @see Math#cos(double)
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
    * Divides the left operand by the right, but returns zero when the
    * denominator is zero, avoiding the {@link java.lang.ArithmeticException}.
    * This is to simulate the convention of shading languages like GLSL and
    * OSL.
    *
    * @param a numerator
    * @param b denominator
    *
    * @return the quotient
    */
   public static double div ( final double a, final double b ) {

      return b != 0.0d ? a / b : 0.0d;
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
    * @param value the input value
    * @param lb    the lower bound
    * @param ub    the upper bound
    *
    * @return the filtered value
    */
   public static float filter ( final float value, final float lb,
      final float ub ) {

      return value >= lb && value < ub ? value : 0.0f;
   }

   /**
    * Floors a real number to the next least integer. An alternative to
    * {@link Math#floor(double)} . Returns 0.0 when the input value is NaN .
    *
    * @param value the input value
    *
    * @return the floored value
    */
   public static double floor ( final double value ) {

      return value > 0.0d ? ( int ) value : value < 0.0d ? ( int ) value - 1.0d
         : 0.0d;
   }

   /**
    * Floors a real number to the next least integer. An alternative to
    * {@link Math#floor(double)} . Returns 0.0 when the input value is NaN .
    *
    * @param value the input value
    *
    * @return the floored value.
    */
   public static float floor ( final float value ) {

      return value > 0.0f ? ( int ) value : value < 0.0f ? ( int ) value - 1.0f
         : 0.0f;
   }

   /**
    * Floors a real number to the next least integer.
    *
    * @param x the input value
    *
    * @return the integer
    */
   public static int floorToInt ( final float x ) {

      final int y = ( int ) x;
      return x < y ? y - 1 : y;
   }

   /**
    * Applies the modulo operator (%) to the operands, which implicitly uses
    * the formula fmod ( <em>a</em>, <em>b</em> ) := <em>a</em> - <em>b</em>
    * trunc ( <em>a</em> / <em>b</em> ) .<br>
    * <br>
    * When the left operand is negative and the right operand is positive, the
    * result will be negative. For periodic values, such as an angle, where
    * the direction of change could be either clockwise or counterclockwise,
    * use {@link Utils#mod(float, float)} .<br>
    * <br>
    * If the right operand is one, use {@link Utils#fract(float)} or
    * <em>a</em> - trunc ( <em>a</em> ) instead.<br>
    * <br>
    * If the right operand is zero or NaN, returns the left operand.
    *
    * @param a the left operand
    * @param b the right operand
    *
    * @return the result
    */
   public static float fmod ( final float a, final float b ) {

      if ( b != 0.0f ) { return a % b; }
      return a;
   }

   /**
    * Applies the modulo operator (%) to the operands. If the right operand is
    * zero, returns the left operand.
    *
    * @param a the left operand
    * @param b the right operand
    *
    * @return the result
    */
   public static int fmod ( final int a, final int b ) {

      if ( b != 0 ) { return a % b; }
      return a;
   }

   /**
    * Finds the signed fractional portion of the input value by subtracting
    * the value's truncation from the value, i.e., fract ( <em>a</em> ) :=
    * <em>a</em> - trunc ( <em>a</em> ) .<br>
    * <br>
    * Use this instead of fmod ( <em>a</em>, 1.0 ) or <em>a</em> % 1.0 .
    *
    * @param value the input value
    *
    * @return the fractional portion
    */
   public static float fract ( final float value ) {

      return value - ( int ) value;
   }

   /**
    * Finds the hypotenuse between two values, \u221a ( <em>a</em><sup>2</sup>
    * + <em>b</em><sup>2</sup> ) . Useful when finding the magnitude of a
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
    * Finds the hypotenuse given three values, \u221a ( <em>a</em><sup>2</sup>
    * + <em>b</em><sup>2</sup> + <em>c</em><sup>2</sup> ) . Useful when
    * finding the magnitude of a vector.
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
    * Finds one divided by the hypotenuse of two values, 1.0 / ( \u221a (
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
    * Finds one divided by the hypotenuse of three values, 1.0 / ( \u221a (
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
    * The inverse square root implementation based on the 'evil bit hack' from
    * <em>Quake 3</em>, as described by Chris Lomont in
    * "<a href= "http://www.lomont.org/papers/2003/InvSqrt.pdf">Fast Inverse
    * Square Root</a>." For accuracy, the result is refined three times with
    * the Newton-Raphson method.<br>
    * <br>
    * Useful when normalizing vectors or quaternions. Prefer this over
    * {@link Utils#sqrt(float)}, which depends on this function.
    *
    * @param value the value
    *
    * @return the inverse square root
    *
    * @author Chris Lomont
    * @author Greg Walsh
    *
    * @see Float#floatToIntBits(float)
    * @see Float#intBitsToFloat(int)
    */
   public static float invSqrtUnchecked ( final float value ) {

      final float vhalf = value * 0.5f;
      float y = Float.intBitsToFloat(0x5f375a86 - ( Float.floatToIntBits(value)
         >> 1 ));

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
    * @param origin the origin value
    * @param dest   the destination value
    * @param step   the step
    *
    * @return the interpolated value
    *
    * @see Utils#lerpUnclamped(float, float, float)
    */
   public static float lerp ( final float origin, final float dest,
      final float step ) {

      return step <= 0.0f ? origin : step >= 1.0f ? dest : ( 1.0f - step )
         * origin + step * dest;
   }

   /**
    * Linear interpolation from the origin to the destination value by a step.
    * If the step is less than zero, returns the origin. If the step is
    * greater than one, returns the destination.
    *
    * @param origin the origin value
    * @param dest   the destination value
    * @param step   the step
    *
    * @return the interpolated value
    *
    * @see Utils#lerpUnclamped(float, float, float)
    */
   public static int lerp ( final int origin, final int dest,
      final float step ) {

      if ( step <= 0.0f ) { return origin; }
      if ( step >= 1.0f ) { return dest; }
      return Utils.lerpUnclamped(origin, dest, step);
   }

   /**
    * Linear interpolation from the origin to the destination value by a step.
    * Does not check to see if the step is beyond the range [0.0, 1.0] .
    *
    * @param origin the origin value
    * @param dest   the destination value
    * @param step   the step
    *
    * @return the interpolated value
    */
   public static double lerpUnclamped ( final double origin, final double dest,
      final double step ) {

      return ( 1.0d - step ) * origin + step * dest;
   }

   /**
    * Linear interpolation from the origin to the destination value by a step.
    * Does not check to see if the step is beyond the range [0.0, 1.0] .
    *
    * @param origin the origin value
    * @param dest   the destination value
    * @param step   the step
    *
    * @return the interpolated value
    */
   public static float lerpUnclamped ( final float origin, final float dest,
      final float step ) {

      return ( 1.0f - step ) * origin + step * dest;
   }

   /**
    * Linear interpolation from the origin to the destination value by a step.
    * Does not check to see if the step is beyond the range [0.0, 1.0] .
    * Rounds the result to an integer.
    *
    * @param origin the origin value
    * @param dest   the destination value
    * @param step   the step
    *
    * @return the interpolated value
    *
    * @see Utils#round(float)
    */
   public static int lerpUnclamped ( final int origin, final int dest,
      final float step ) {

      return Utils.round( ( 1.0f - step ) * origin + step * dest);
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
    * lower bound of the destination range.
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
      return lbDest;
   }

   /**
    * Finds the greatest, or maximum, among a list of values.
    *
    * @param fs the list of values
    *
    * @return the maximum value
    */
   public final static float max ( final float... fs ) {

      float max = Float.MIN_VALUE;
      final int len = fs.length;
      for ( int i = 0; i < len; ++i ) {
         if ( fs[i] > max ) { max = fs[i]; }
      }
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

      return a >= b ? a : a < b ? b : 0.0f;
   }

   /**
    * Finds the greatest, or maximum, among three values.
    *
    * @param a the first input
    * @param b the second input
    * @param c the third input
    *
    * @return the maximum value
    */
   public static float max ( final float a, final float b, final float c ) {

      final float d = a >= b ? a : a < b ? b : 0.0f;
      return d >= c ? d : d < c ? c : 0.0f;
   }

   /**
    * Finds the least, or minimum, among a list of values.
    *
    * @param fs the list of values
    *
    * @return the minimum value
    */
   public final static float min ( final float... fs ) {

      float min = Float.MAX_VALUE;
      final int len = fs.length;
      for ( int i = 0; i < len; ++i ) {
         if ( fs[i] < min ) { min = fs[i]; }
      }
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

      return a <= b ? a : a > b ? b : 0.0f;
   }

   /**
    * Finds the least, or minimum, among three values.
    *
    * @param a the first input
    * @param b the second input
    * @param c the third input
    *
    * @return the minimum value
    */
   public static float min ( final float a, final float b, final float c ) {

      final float d = a <= b ? a : a > b ? b : 0.0f;
      return d <= c ? d : d > c ? c : 0.0f;
   }

   /**
    * Applies floor modulo to the operands, and therefore uses the formula mod
    * ( <em>a</em>, <em>b</em> ) := <em>a</em> - <em>b</em> * floor (
    * <em>a</em> / <em>b</em> ) . If the right operand is one, use
    * {@link Utils#mod1(float)} or <em>a</em> - floor(<em>a</em>) instead. If
    * the right operand is zero, returns the left operand instead of throwing
    * a java.lang.ArithmeticException for division by zero.
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
         final float value = a / b;
         return a - b * ( value > 0.0f ? ( int ) value : value < 0.0f
            ? ( int ) value - 1.0f : 0.0f );
      }
      return a;
   }

   /**
    * Applies floor modulo to the operands. Returns the left operand when the
    * right operand is zero. An alternative to {@link Math#floorMod(int, int)}
    * . Returns a when b is zero.
    *
    * @param a the left operand
    * @param b the right operand
    *
    * @return the result
    */
   public static int mod ( final int a, final int b ) {

      if ( b != 0 ) {
         final int result = a - b * ( a / b );
         return result < 0 ? result + b : result;
      }
      return a;
   }

   /**
    * Subtracts the floor of the input value from the value. Returns a
    * positive value in the range [0.0, 1.0] .
    *
    * @param value the input value
    *
    * @return the result
    */
   public static double mod1 ( final double value ) {

      return value > 0.0d ? value - ( int ) value : value < 0.0d ? value
         - ( ( int ) value - 1.0d ) : 0.0d;
   }

   /**
    * Subtracts the floor of the input value from the value. Returns a
    * positive value in the range [0.0, 1.0] .
    *
    * @param value the input value
    *
    * @return the result
    */
   public static float mod1 ( final float value ) {

      return value > 0.0f ? value - ( int ) value : value < 0.0f ? value
         - ( ( int ) value - 1.0f ) : 0.0f;
   }

   /**
    * A specialized version of modulo which shifts an angle in degrees to the
    * range [0.0, 360.0] .
    *
    * @param degrees the input angle
    *
    * @return the output angle
    */
   public static float modDegrees ( final float degrees ) {

      final double d = degrees;
      return ( float ) ( d - 360.0d * Utils.floor(d * IUtils.ONE_360_D) );
   }

   /**
    * A specialized version of modulo which shifts an angle in radians to the
    * range [0.0, \u03c4] .
    *
    * @param radians the angle in radians
    *
    * @return the unsigned radians
    *
    * @see Utils#mod(float, float)
    * @see IUtils#TAU_D
    * @see IUtils#ONE_TAU_D
    * @see Utils#floor(double)
    */
   public static float modRadians ( final float radians ) {

      final double r = radians;
      return ( float ) ( r - IUtils.TAU_D * Utils.floor(r * IUtils.ONE_TAU_D) );
   }

   /**
    * Applies floor modulo to the operands, and therefore uses the formula mod
    * ( <em>a</em>, <em>b</em> ) := <em>a</em> - <em>b</em> floor ( <em>a</em>
    * / <em>b</em> ) . If the right operand is one, use <em>a</em> - floor (
    * <em>a</em> ) instead.<br>
    * <br>
    * Does not check if the right operand is zero.
    *
    * @param a the left operand
    * @param b the right operand
    *
    * @return the result
    */
   public static float modUnchecked ( final float a, final float b ) {

      final float q = a / b;
      return a - b * ( q > 0.0f ? ( int ) q : q < 0.0f ? ( int ) q - 1.0f
         : 0.0f );
      // return a - b * Utils.floor(a / b);
   }

   /**
    * Finds the negation of a float holding a boolean value.
    *
    * @param value the input value
    *
    * @return the negation
    */
   public static int not ( final float value ) {

      return value == 0.0f || value != value ? 1 : 0;
   }

   /**
    * Evaluates two floats like booleans, using the inclusive or (OR) logic
    * gate.
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
    * Oscillates between [0.0, 1.0] based on an input step.<br>
    * <br>
    * Uses a different formula than the Unity math function of the same name:
    * 0.5 + 0.5 * cos ( step / TAU ) .
    *
    * @param step the step
    *
    * @return the oscillation
    */
   public static float pingPong ( final float step ) {

      return 0.5f + 0.5f * Utils.scNorm(step);
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

      final float t = 0.5f + 0.5f * Utils.scNorm(step);
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

      final float t = 0.5f + 0.5f * Utils.scNorm(step);
      return ( int ) ( ( 1.0f - t ) * lb + t * ub );
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
    * Reduces the signal, or granularity, of a value. Applied to a color, this
    * yields the 'posterization' effect. Applied to a vector, this yields a
    * crenelated effect. Any level less than 2 returns the value unaltered.
    *
    * @param value  the value
    * @param levels the level
    *
    * @return the quantized value
    *
    * @see Utils#floor(float)
    */
   public static float quantize ( final float value, final int levels ) {

      /*
       * The method used to describe posterize in the Blender manual, round(m x
       * n - 0.5) / (n - 1), should not be used. It will yield output values
       * exceeding the magnitude of input values, e.g. 1.0 will return 2.0 .
       */

      if ( levels < 2 ) { return value; }
      return Utils.floor(0.5f + value * levels) / levels;
   }

   /**
    * Rounds a value to an integer based on whether its fractional portion is
    * greater than or equal to plus or minus 0.5 .
    *
    * @param value the value
    *
    * @return the rounded value
    */
   public static int round ( final float value ) {

      return value < 0.0f ? ( int ) ( value - 0.5f ) : value > 0.0f
         ? ( int ) ( value + 0.5f ) : 0;
   }

   /**
    * Rounds a value to a number of places right of the decimal point.
    * Promotes the float to a double, rounds it, then demotes back to a float.
    * Note that floating (or single) precision will likely lead to
    * inaccuracies.
    *
    * @param value  value
    * @param places the number of places
    *
    * @return the rounded value
    *
    * @see Utils#round(float)
    */
   public static float round ( final float value, final int places ) {

      if ( places < 1 ) { return Utils.round(value); }
      if ( places > 7 ) { return value; }

      int n = 10;
      for ( int i = 1; i < places; ++i ) {
         n *= 10;
      }

      final float nf = n;
      return Utils.round(value * nf) / nf;
   }

   /**
    * A helper method to facilitate the approximate sine and cosine of an
    * angle with single precision real numbers. The radians supplied to this
    * function should be normalized through division by \u03c4 ,
    * {@link IUtils#ONE_TAU}, approximately {@value IUtils#ONE_TAU}. Subtract
    * 0.25 from the input value to return the sine instead of the cosine.<br>
    * <br>
    * Instead of a look-up table, this is based on the algorithm described at
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
      if ( r1y >= -9.0f ^ ( r2x | r2z ) ) {
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
    * @param value the input value
    *
    * @return the sign
    *
    * @see Math#signum(float)
    */
   public static int sign ( final float value ) {

      return value < -0.0f ? -1 : value > 0.0f ? 1 : 0;
   }

   /**
    * Finds the single precision sine of an angle in radians. Returns a value
    * in the range [-1.0, 1.0] .
    *
    * @param radians the angle in radians
    *
    * @return the sine of the angle
    *
    * @see Math#sin(double)
    */
   public static float sin ( final float radians ) {

      return ( float ) Math.sin(radians);
   }

   /**
    * Eases between an origin and destination by a step in [0.0, 1.0] .
    *
    * @param origin the origin
    * @param dest   the destination
    * @param step   the step
    *
    * @return the eased value
    */
   public static float smoothStep ( final float origin, final float dest,
      final float step ) {

      if ( step <= 0.0f ) { return origin; }
      if ( step >= 1.0f ) { return dest; }
      final float t = step * step * ( 3.0f - ( step + step ) );
      return ( 1.0f - t ) * origin + t * dest;
   }

   /**
    * Finds the approximate square root of a value. Returns 0.0 when the value
    * is less than zero. An alternative to {@link Math#sqrt(double)} . Use
    * {@link Utils#sqrtUnchecked(float)} when the value is known to be
    * positive. Use {@link Complex#sqrt(float, Complex)} when the input may be
    * negative and a complex output is desired.
    *
    * @param value the value
    *
    * @return the square root
    *
    * @see Utils#sqrtUnchecked(float)
    */
   public static float sqrt ( final float value ) {

      return value > 0.0f ? Utils.sqrtUnchecked(value) : 0.0f;
   }

   /**
    * Finds the approximate square root of a value. Does so by multiplying the
    * value by its inverse square root.
    *
    * @param value the value
    *
    * @return the square root
    *
    * @see Utils#invSqrtUnchecked(float)
    */
   public static float sqrtUnchecked ( final float value ) {

      return value * Utils.invSqrtUnchecked(value);
   }

   /**
    * Swaps the endianness of an integer. For example, a color in the AARRGGBB
    * format, <code>0xffaabbcc</code>, will yield the BBGGRRAA color,
    * <code>0xccbbaaff</code>. Because Java integers are signed, the integer
    * is promoted to a <code>long</code> to simulate an <code>uint</code> (an
    * unsigned integer), then demoted after the shift.
    *
    * @param a the input value
    *
    * @return the swap.
    */
   public static int swapEndian ( final int a ) {

      final long b = a & 0xffffffffL;
      return ( int ) ( b << 0x18 | ( b & 0xff00 ) << 0x8 | b >> 0x8 & 0xff00 | b
         >> 0x18 );
   }

   /**
    * Equivalent to dividing the sine of the angle by the cosine. Finds the
    * approximate tangent of an angle in radians. An alternative to the double
    * precision {@link Math#tan(double)} , this function uses single-precision
    * numbers.
    *
    * @param radians the angle in radians
    *
    * @return the tangent
    */
   public static float tan ( final float radians ) {

      final float nrmRad = radians * IUtils.ONE_TAU;
      final float cost = Utils.scNorm(nrmRad);
      return cost != 0.0f ? Utils.scNorm(nrmRad - 0.25f) / cost : 0.0f;
   }

   /**
    * A quick, dirty representation of a single-precision real number as a
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
    * @param value  the real number
    * @param places the desired number of decimal places
    *
    * @return the string
    */
   public static String toFixed ( final float value, final int places ) {

      final int raw = Float.floatToRawIntBits(value);
      switch ( raw ) {
         case 0x7f800000: /* Positive infinity. */
         case 0x7f7fffff: /* Max value. */
            return "3.4028235E38";

         case 0xff800000: /* Negative infinity. */
         case 0x1: /* Minimum value. */
            return "1.4E-45";

         case 0x0: /* Positive zero. */
         case 0x80000000: /* Negative zero. */
         case 0x7fc00000: /* Not a number (NaN). */
            return "0.0";

         default:
      }

      if ( places < 0 ) { return Integer.toString(( int ) value); }
      if ( places < 1 ) { return Float.toString(( int ) value); }

      final float sign = Float.intBitsToFloat(raw & -2147483648 | 1065353216);
      final float abs = Float.intBitsToFloat(raw & 2147483647);
      final int trunc = ( int ) abs;
      final StringBuilder sb = new StringBuilder(16);

      /* Append integral to StringBuilder. */
      int len = 0;
      if ( sign < -0.0f ) {
         sb.append('-').append(trunc);
         len = sb.length() - 1;
      } else {
         sb.append(trunc);
         len = sb.length();
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
      if ( maxPlaces < 1 ) { return Float.toString(value); }

      final int vetPlaces = places < maxPlaces ? places : maxPlaces;
      float frac = abs - trunc;

      /* Truncation. */
      for ( int i = 0; i < vetPlaces; ++i ) {
         frac *= 10.0f;
         final int tr = ( int ) frac;
         frac -= tr;
         sb.append(tr);
      }

      return sb.toString();
   }

   /**
    * Returns an integer formatted as a string padded by initial zeroes.
    *
    * @param value  the integer
    * @param places the number of places
    *
    * @return the string
    */
   public static String toPadded ( final int value, final int places ) {

      /*
       * Double precision is needed to preserve accuracy. The max integer value
       * is 2147483647, which is 10 digits long. The sign needs to be flipped
       * because working with positive absolute value would allow
       * Integer#MIN_VALUE to overflow to zero.
       */

      final boolean isNeg = value < 0;
      int nAbsVal = isNeg ? value : -value;

      final int[] digits = new int[10];
      int filled = 0;
      while ( nAbsVal < 0 ) {
         final double y = nAbsVal * 0.1d;
         nAbsVal = ( int ) y;
         digits[filled++] = -( int ) ( ( y - nAbsVal ) * 10.0d - 0.5d );
      }

      final StringBuilder sb = new StringBuilder(16);
      if ( isNeg ) { sb.append('-'); }
      final int vplaces = filled > places ? filled : places;
      for ( int n = vplaces - 1; n > -1; --n ) {
         sb.append(digits[n]);
      }

      return sb.toString();
   }

   /**
    * Returns a String representation of a one dimensional array of
    * <code>byte</code>s.
    *
    * @param arr the array
    *
    * @return the String
    */
   public static String toString ( final byte[] arr ) {

      final int len = arr.length;
      final int last = len - 1;

      final StringBuilder sb = new StringBuilder(len * 32);
      sb.append('[').append(' ');
      for ( int i = 0; i < arr.length; ++i ) {
         sb.append(arr[i]);
         if ( i < last ) { sb.append(',').append(' '); }
      }

      sb.append(' ').append(']');
      return sb.toString();
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

      final int len = arr.length;
      final int last = len - 1;

      final StringBuilder sb = new StringBuilder(len * 32);
      sb.append('[').append(' ');
      for ( int i = 0; i < arr.length; ++i ) {
         sb.append(Utils.toFixed(arr[i], places));
         if ( i < last ) { sb.append(',').append(' '); }
      }

      sb.append(' ').append(']');
      return sb.toString();
   }

   /**
    * Returns a String representation of a one dimensional array of objects.
    *
    * @param arr the array
    *
    * @return the String
    */
   public static String toString ( final Object[] arr ) {

      final int len = arr.length;
      final int last = len - 1;

      final StringBuilder sb = new StringBuilder(len * 32);
      sb.append('[').append(' ');
      for ( int i = 0; i < arr.length; ++i ) {
         sb.append(arr[i].toString());
         if ( i < last ) { sb.append(',').append(' '); }
      }

      sb.append(' ').append(']');
      return sb.toString();
   }

   /**
    * Returns a String representation of a two dimensional array of objects.
    *
    * @param arr the array
    *
    * @return the String
    */
   public static String toString ( final Object[][] arr ) {

      final int len = arr.length;
      final int last = len - 1;

      final StringBuilder sb = new StringBuilder(len * 128);
      sb.append('[').append(' ');
      for ( int i = 0; i < arr.length; ++i ) {
         sb.append(Utils.toString(arr[i]));
         if ( i < last ) { sb.append(',').append(' '); }
      }

      sb.append(' ').append(']');
      return sb.toString();
   }

   /**
    * Returns a String representation of a three dimensional array of objects.
    *
    * @param arr the array
    *
    * @return the String
    */
   public static String toString ( final Object[][][] arr ) {

      final int len = arr.length;
      final int last = len - 1;

      final StringBuilder sb = new StringBuilder(len * 512);
      sb.append('[').append(' ');
      for ( int i = 0; i < arr.length; ++i ) {
         sb.append(Utils.toString(arr[i]));
         if ( i < last ) { sb.append(',').append(' '); }
      }

      sb.append(' ').append(']');
      return sb.toString();
   }

   /**
    * Truncates the input value. This is an alias for explicitly casting a
    * float to an integer, then implicitly casting the integral to a float.
    *
    * @param value the input value
    *
    * @return the integral
    */
   public static float trunc ( final float value ) {

      return ( int ) value;
   }

   /**
    * An alias for {@link Byte#toUnsignedInt(byte)} . Converts a signed byte
    * in the range [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] to an
    * unsigned byte in the range [0, 255], promoted to an integer. Useful when
    * working with colors. Defined for cross-language comparison with C#,
    * which uses signed and unsigned versions of primitive data types.
    *
    * @param a the signed byte
    *
    * @return the unsigned byte, promoted
    */
   public static int ubyte ( final byte a ) { return a & 0xff; }

   /**
    * An alias for {@link Integer#toUnsignedLong(int)}. Converts a signed
    * integer in the range [{@value Integer#MIN_VALUE},
    * {@value Integer#MAX_VALUE}] to an unsigned integer in the range [0,
    * 4294967295], promoted to a long. Useful when working with colors.
    * Defined for cross-language comparison with C#, which uses signed and
    * unsigned versions of primitive data types.
    *
    * @param a the signed integer
    *
    * @return the unsigned integer, promoted
    */
   public static long uint ( final int a ) { return a & 0xffffffffL; }

   /**
    * Wraps a value around a periodic range as defined by an upper and lower
    * bound: lower bounds inclusive; upper bounds exclusive. Due to single
    * precision accuracy, results will be inexact. In cases where the lower
    * bound is greater than the upper bound, the two will be swapped. In cases
    * where the range is 0.0, 0.0 will be returned.
    *
    * @param value the value
    * @param lb    the lower bound
    * @param ub    the upper bound
    *
    * @return the wrapped value
    */
   public static float wrap ( final float value, final float lb,
      final float ub ) {

      final float span = ub - lb;
      float vlb = 0.0f;
      float vub = 0.0f;
      if ( span < 0.0f ) {
         vlb = ub;
         vub = lb;
      } else {
         if ( span <= 0.0f ) { return 0.0f; }

         vlb = lb;
         vub = ub;
      }

      if ( value < vlb ) { return vub - ( vlb - value ) % span; }
      return value >= vub ? vlb + ( value - vlb ) % span : value;
   }

   /**
    * Evaluates two floats like booleans, using the exclusive or (XOR) logic
    * gate.
    *
    * @param a the left operand
    * @param b the right operand
    *
    * @return the evaluation
    */
   public static int xor ( final float a, final float b ) {

      return ( a != 0.0F && a == a ? 1 : 0 ) ^ ( b != 0.0F && b == b ? 1 : 0 );
   }

   /**
    * A functional interface for an easing function which interpolates an an
    * array.
    *
    * @param <T> the parameter type
    */
   @FunctionalInterface
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
   @FunctionalInterface
   public interface EasingFuncObj < T > extends QuadFunction < T, T, Float, T,
      T > {

      /**
       * Apply the function.
       *
       * @param origin the origin
       * @param dest   the destination
       * @param step   the step
       * @param target the target object
       *
       * @return the eased object
       */
      @Override
      T apply ( final T origin, final T dest, final Float step, T target );

   }

   /**
    * A functional interface for an easing function which interpolates a
    * primitive data type from an origin to a destination by a float.
    *
    * @param <T> the parameter type
    */
   @FunctionalInterface
   public interface EasingFuncPrm < T > extends TriFunction < T, T, Float, T > {

      /**
       * Apply the function.
       *
       * @param origin the origin
       * @param dest   the destination
       * @param step   the step
       *
       * @return the eased object
       */
      @Override
      T apply ( final T origin, final T dest, final Float step );

   }

   /**
    * Linear interpolation for a value.
    */
   public static class Lerp extends LerpUnclamped {

      /**
       * The default constructor.
       */
      Lerp ( ) { super(); }

      /**
       * Applies the linear interpolation.
       *
       * @param origin the origin
       * @param dest   the destination
       * @param step   the step
       */
      @Override
      public Float apply ( final Float origin, final Float dest,
         final Float step ) {

         if ( step <= 0.0f ) { return origin; }
         if ( step >= 1.0f ) { return dest; }
         return super.apply(origin, dest, step);
      }

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

   /**
    * Linear interpolation for a periodic value in the counter-clockwise
    * direction.
    */
   public static class LerpCCW extends PeriodicEasing {

      /**
       * Constructs the lerp CCW functional object with a default range, TAU.
       */
      public LerpCCW ( ) { super(); }

      /**
       * Constructs the lerp CCW functional object with a specified range.
       *
       * @param range the range of the period
       */
      public LerpCCW ( final float range ) { super(range); }

      /**
       * Applies the lerp CCW function.
       *
       * @param origin the origin
       * @param dest   the destination
       * @param step   the step
       *
       * @return the eased value
       *
       * @see Utils#lerpUnclamped(float, float, float)
       * @see Utils#modUnchecked(float, float)
       */
      @Override
      protected float applyPartial ( final float origin, final float dest,
         final float step ) {

         if ( this.aGtb ) {
            this.b = this.b + this.range;
            this.modResult = true;
         }

         final float fac = ( 1.0f - step ) * this.a + step * this.b;
         if ( this.modResult ) { return Utils.modUnchecked(fac, this.range); }
         return fac;
      }

   }

   /**
    * Linear interpolation for a periodic value in the clockwise direction.
    */
   public static class LerpCW extends PeriodicEasing {

      /**
       * Constructs the lerp CW functional object with a default range, TAU.
       */
      public LerpCW ( ) { super(); }

      /**
       * Constructs the lerp CW functional object with a specified range.
       *
       * @param range the range of the period
       */
      public LerpCW ( final float range ) { super(range); }

      /**
       * Applies the lerp CW function.
       *
       * @param origin the origin
       * @param dest   the destination
       * @param step   the step
       *
       * @return the eased value
       *
       * @see Utils#lerpUnclamped(float, float, float)
       * @see Utils#modUnchecked(float, float)
       */
      @Override
      protected float applyPartial ( final float origin, final float dest,
         final float step ) {

         if ( this.aLtb ) {
            this.a = this.a + this.range;
            this.modResult = true;
         }

         final float fac = ( 1.0f - step ) * this.a + step * this.b;
         if ( this.modResult ) { return Utils.modUnchecked(fac, this.range); }
         return fac;
      }

   }

   /**
    * Linear interpolation for a periodic value in the farthest direction.
    */
   public static class LerpFar extends PeriodicEasing {

      /**
       * Constructs the lerp far functional object with a default range, TAU.
       */
      public LerpFar ( ) { super(); }

      /**
       * Constructs the lerp far functional object with a specified range.
       *
       * @param range the range of the period
       */
      public LerpFar ( final float range ) { super(range); }

      /**
       * Applies the lerp far function.
       *
       * @param origin the origin
       * @param dest   the destination
       * @param step   the step
       *
       * @return the eased value
       *
       * @see Utils#lerpUnclamped(float, float, float)
       * @see Utils#modUnchecked(float, float)
       */
      @Override
      protected float applyPartial ( final float origin, final float dest,
         final float step ) {

         if ( this.aLtb && this.diff < this.halfRange ) {
            this.a = this.a + this.range;
            this.modResult = true;
         } else if ( this.aGtb && this.diff > -this.halfRange ) {
            this.b = this.b + this.range;
            this.modResult = true;
         }

         final float fac = ( 1.0f - step ) * this.a + step * this.b;
         if ( this.modResult ) { return Utils.modUnchecked(fac, this.range); }
         return fac;
      }

   }

   /**
    * Linear interpolation for a periodic value in the nearest direction.
    */
   public static class LerpNear extends PeriodicEasing {

      /**
       * Constructs the lerp near functional object with a default range, TAU.
       */
      public LerpNear ( ) { super(); }

      /**
       * Constructs the lerp near functional object with a specified range.
       *
       * @param range the range of the period
       */
      public LerpNear ( final float range ) { super(range); }

      /**
       * Applies the lerp near function.
       *
       * @param origin the origin
       * @param dest   the destination
       * @param step   the step
       *
       * @return the eased value
       *
       * @see Utils#lerpUnclamped(float, float, float)
       * @see Utils#modUnchecked(float, float)
       */
      @Override
      protected float applyPartial ( final float origin, final float dest,
         final float step ) {

         if ( this.aLtb && this.diff > this.halfRange ) {
            this.a = this.a + this.range;
            this.modResult = true;
         } else if ( this.aGtb && this.diff < -this.halfRange ) {
            this.b = this.b + this.range;
            this.modResult = true;
         }

         final float fac = ( 1.0f - step ) * this.a + step * this.b;
         if ( this.modResult ) { return Utils.modUnchecked(fac, this.range); }
         return fac;
      }

   }

   /**
    * Linear interpolation, not clamped, for a value. Note that there is an
    * added cost to boxing and unboxing a primitive float to and from a Float
    * object.
    */
   public static class LerpUnclamped implements EasingFuncPrm < Float > {

      /**
       * The default constructor.
       */
      LerpUnclamped ( ) {}

      /**
       * Applies the unclamped lerp function.
       *
       * @param origin the origin
       * @param dest   the destination
       * @param step   the step
       *
       * @return the eased value
       */
      @Override
      public Float apply ( final Float origin, final Float dest,
         final Float step ) {

         final double td = step;
         return ( float ) ( ( 1.0d - td ) * origin + td * dest );
      }

   }

   /**
    * An abstract class to cover the easing periodic of values, such as angles
    * and hues in HSV color space. Allows the range to be set to, for example,
    * TAU radians, 360.0 degrees or 1.0 color channel.
    */
   public static abstract class PeriodicEasing implements Utils.EasingFuncPrm <
      Float > {

      /**
       * The start angle, modulated by the range.
       */
      protected transient float a = 0.0f;

      /**
       * Whether or not the start angle is greater than the stop angle.
       */
      protected transient boolean aGtb = false;

      /**
       * Whether or not the start angle is less than the stop angle.
       */
      protected transient boolean aLtb = false;

      /**
       * The stop angle, modulated by the range.
       */
      protected transient float b = 0.0f;

      /**
       * The difference between the stop and start angle.
       */
      protected transient float diff = 0.0f;

      /**
       * One-half of the range of the period.
       */
      protected transient float halfRange = 0.5f;

      /**
       * Whether or not to floor wrap the result of the easing function.
       */
      protected transient boolean modResult = false;

      /**
       * The range of the period.
       */
      protected float range = 1.0f;

      /**
       * Constructs the easing functional object with a default range, TAU.
       */
      public PeriodicEasing ( ) {

         this.setRange(PeriodicEasing.DEFAULT_RANGE);
      }

      /**
       * Constructs the easing functional object with a specified range.
       *
       * @param range the range of the period
       */
      public PeriodicEasing ( final float range ) {

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
      public Float apply ( final Float origin, final Float dest,
         final Float step ) {

         this.eval(origin, dest);

         if ( step <= 0.0f || this.diff == 0.0f ) { return this.a; }
         if ( step >= 1.0f ) { return this.b; }
         return this.applyPartial(origin, dest, step);
      }

      /**
       * Gets the range of the easing function.
       *
       * @return the range
       */
      public float getRange ( ) {

         return this.range;
      }

      /**
       * Sets the range of the easing function. The range should be a positive
       * non-zero value.
       *
       * @param range the range
       */
      public void setRange ( final float range ) {

         this.range = Utils.max(Utils.abs(range), IUtils.DEFAULT_EPSILON);
         this.halfRange = range * 0.5f;
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
       * @param origin the origin value
       * @param dest   the destination value
       * @param step   the step
       *
       * @return the interpolated value
       */
      protected abstract float applyPartial ( final float origin,
         final float dest, final float step );

      /**
       * A helper function which mutates protected fields a, b, diff, aLtb and
       * aGtb. This mods the origin (a) and destination (b) by the range. It
       * then finds the signed distance between the mod origin and destination
       * (diff). Lastly, it evaluates which of the two is greater than the
       * other, (aLtb) and (aGtb).
       *
       * @param origin origin value
       * @param dest   destination value
       *
       * @see Utils#modUnchecked(float, float)
       */
      protected void eval ( final float origin, final float dest ) {

         this.a = Utils.modUnchecked(origin, this.range);
         this.b = Utils.modUnchecked(dest, this.range);
         this.diff = this.b - this.a;
         this.aLtb = this.a < this.b;
         this.aGtb = this.a > this.b;
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
   @FunctionalInterface
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
    * Eases between two values by a smooth step.
    */
   public static class SmoothStep extends LerpUnclamped {

      /**
       * The default constructor.
       */
      SmoothStep ( ) { super(); }

      /**
       * Applies the unclamped smooth step function.
       *
       * @param origin the origin
       * @param dest   the destination
       * @param step   the step
       *
       * @return the eased value
       */
      @Override
      public Float apply ( final Float origin, final Float dest,
         final Float step ) {

         if ( step <= 0.0f ) { return origin; }
         if ( step >= 1.0f ) { return dest; }
         final double td = step;
         final float ts = ( float ) ( td * td * ( 3.0d - ( td + td ) ) );
         return super.apply(origin, dest, ts);
      }

   }

   /**
    * A functional interface for functions with three input parameters.
    *
    * @param <T> first parameter
    * @param <U> second parameter
    * @param <V> third parameter
    * @param <R> return type
    */
   @FunctionalInterface
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
