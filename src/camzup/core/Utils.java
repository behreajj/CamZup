package camzup.core;

/**
 * Implements basic math utilities for single- and
 * double-precision numbers which may serve as alternatives
 * to Math library functions.
 */
public abstract class Utils implements IUtils {

   /**
    * A functional interface for an easing function which
    * interpolates an an array.
    *
    * @param <T>
    *           the parameter type
    */
   @FunctionalInterface
   public interface EasingFuncArr < T > {

      /**
       * Apply the function.
       *
       * @param arr
       *           the array
       * @param step
       *           the step
       * @param target
       *           the target object
       * @return the eased object
       */
      T apply ( final T[] arr, Float step, T target );
   }

   /**
    * A functional interface for an easing function which
    * interpolates an object from an origin to a destination by
    * a float, with a final output parameter.
    *
    * @param <T>
    *           the parameter type
    */
   @FunctionalInterface
   public interface EasingFuncObject < T >
         extends QuadFunction < T, T, Float, T, T > {

      /**
       * Apply the function.
       *
       * @param origin
       *           the origin
       * @param dest
       *           the destination
       * @param step
       *           the step
       * @param target
       *           the target object
       * @return the eased object
       */
      @Override
      T apply (
            final T origin,
            final T dest,
            final Float step,
            T target );
   }

   /**
    * A functional interface for an easing function which
    * interpolates a primitive data type from an origin to a
    * destination by a float.
    *
    * @param <T>
    *           the parameter type
    */
   @FunctionalInterface
   public interface EasingFuncPrimitive < T >
         extends TriFunction < T, T, Float, T > {

      /**
       * Apply the function.
       *
       * @param origin
       *           the origin
       * @param dest
       *           the destination
       * @param step
       *           the step
       * @return the eased object
       */
      @Override
      T apply (
            final T origin,
            final T dest,
            final Float step );
   }

   /**
    * Linear interpolator for a value.
    */
   public static class Lerp extends LerpUnclamped {

      /**
       * The default constructor.
       */
      Lerp () {

         super();
      }

      /**
       * Applies the linear interpolation.
       *
       * @param origin
       *           the origin
       * @param dest
       *           the destination
       * @param step
       *           the step
       */
      @Override
      public Float apply (
            final Float origin,
            final Float dest,
            final Float step ) {

         if (step <= 0.0f) {
            return origin;
         }
         if (step >= 1.0f) {
            return dest;
         }
         return super.apply(origin, dest, step);
      }
   }

   /**
    * Linear interpolator for a periodic value in the
    * counter-clockwise direction.
    */
   public static class LerpCCW extends PeriodicEasing {

      /**
       * Constructs the lerp CCW functional object with a default
       * range, TAU.
       */
      public LerpCCW () {

         super();
      }

      /**
       * Constructs the lerp CCW functional object with a
       * specified range.
       *
       * @param range
       *           the range of the period
       */
      public LerpCCW ( final float range ) {

         super(range);
      }

      /**
       * Applies the lerp CCW function.
       *
       * @param origin
       *           the origin
       * @param dest
       *           the destination
       * @param step
       *           the step
       * @return the eased value
       * @see Utils#lerpUnclamped(float, float, float)
       * @see Utils#mod(float, float)
       */
      @Override
      public float applyUnclamped (
            final float origin,
            final float dest,
            final float step ) {

         if (this.aGtb) {
            this.b = this.b + this.range;
            this.modResult = true;
         }

         final float fac = Utils.lerpUnclamped(this.a, this.b, step);
         if (this.modResult) {
            return Utils.mod(fac, this.range);
         }
         return fac;
      }
   }

   /**
    * Linear interpolator for a periodic value in the clockwise
    * direction.
    */
   public static class LerpCW extends PeriodicEasing {

      /**
       * Constructs the lerp CW functional object with a default
       * range, TAU.
       */
      public LerpCW () {

         super();
      }

      /**
       * Constructs the lerp CW functional object with a specified
       * range.
       *
       * @param range
       *           the range of the period
       */
      public LerpCW ( final float range ) {

         super(range);
      }

      /**
       * Applies the lerp CW function.
       *
       * @param origin
       *           the origin
       * @param dest
       *           the destination
       * @param step
       *           the step
       * @return the eased value
       * @see Utils#lerpUnclamped(float, float, float)
       * @see Utils#mod(float, float)
       */
      @Override
      public float applyUnclamped (
            final float origin,
            final float dest,
            final float step ) {

         if (this.aLtb) {
            this.a = this.a + this.range;
            this.modResult = true;
         }

         final float fac = Utils.lerpUnclamped(this.a, this.b, step);
         if (this.modResult) {
            return Utils.mod(fac, this.range);
         }
         return fac;
      }
   }

   /**
    * Linear interpolator for a periodic value in the furthest
    * direction.
    */
   public static class LerpFar extends PeriodicEasing {

      /**
       * Constructs the lerp far functional object with a default
       * range, TAU.
       */
      public LerpFar () {

         super();
      }

      /**
       * Constructs the lerp far functional object with a
       * specified range.
       *
       * @param range
       *           the range of the period
       */
      public LerpFar ( final float range ) {

         super(range);
      }

      /**
       * Applies the lerp far function.
       *
       * @param origin
       *           the origin
       * @param dest
       *           the destination
       * @param step
       *           the step
       * @return the eased value
       * @see Utils#lerpUnclamped(float, float, float)
       * @see Utils#mod(float, float)
       */
      @Override
      public float applyUnclamped (
            final float origin,
            final float dest,
            final float step ) {

         if (this.aLtb && this.diff < this.halfRange) {
            this.a = this.a + this.range;
            this.modResult = true;
         } else if (this.aGtb && this.diff > -this.halfRange) {
            this.b = this.b + this.range;
            this.modResult = true;
         }

         final float fac = Utils.lerpUnclamped(this.a, this.b, step);
         if (this.modResult) {
            return Utils.mod(fac, this.range);
         }
         return fac;
      }
   }

   /**
    * Linear interpolator for a periodic value in the nearest
    * direction.
    */
   public static class LerpNear extends PeriodicEasing {

      /**
       * Constructs the lerp near functional object with a default
       * range, TAU.
       */
      public LerpNear () {

         super();
      }

      /**
       * Constructs the lerp near functional object with a
       * specified range.
       *
       * @param range
       *           the range of the period
       */
      public LerpNear ( final float range ) {

         super(range);
      }

      /**
       * Applies the lerp near function.
       *
       * @param origin
       *           the origin
       * @param dest
       *           the destination
       * @param step
       *           the step
       * @return the eased value
       * @see Utils#lerpUnclamped(float, float, float)
       * @see Utils#mod(float, float)
       */
      @Override
      public float applyUnclamped (
            final float origin,
            final float dest,
            final float step ) {

         if (this.aLtb && this.diff > this.halfRange) {
            this.a = this.a + this.range;
            this.modResult = true;
         } else if (this.aGtb && this.diff < -this.halfRange) {
            this.b = this.b + this.range;
            this.modResult = true;
         }

         final float fac = Utils.lerpUnclamped(this.a, this.b, step);
         if (this.modResult) {
            return Utils.mod(fac, this.range);
         }
         return fac;
      }
   }

   /**
    * Unclamped linear interpolation for a value.
    *
    * Note that there is an added cost to boxing and unboxing a
    * primitive float to and from a Float object.
    */
   public static class LerpUnclamped implements EasingFuncPrimitive < Float > {

      /**
       * The default constructor.
       */
      LerpUnclamped () {

      }

      @Override
      public Float apply (
            final Float origin,
            final Float dest,
            final Float step ) {

         final double td = step;
         return (float) ((1.0d - td) * origin + td * dest);
      }
   }

   /**
    * An abstract class to cover the easing periodic of values,
    * such as angles and hues in HSV color space. Allows the
    * range to be set to, for example, TAU radians, 360.0
    * degrees or 1.0 color channel.
    */
   public static abstract class PeriodicEasing
         implements Utils.EasingFuncPrimitive < Float > {

      /**
       * The default range of the period, \u03c4.
       */
      public static final float DEFAULT_RANGE = IUtils.TAU;

      /**
       * The start angle, modulated by the range.
       */
      protected float a = 0.0f;

      /**
       * Whether or not the start angle is greater than the stop
       * angle.
       */
      protected boolean aGtb = false;

      /**
       * Whether or not the start angle is less than the stop
       * angle.
       */
      protected boolean aLtb = false;

      /**
       * The stop angle, modulated by the range.
       */
      protected float b = 0.0f;

      /**
       * The difference between the stop and start angle.
       */
      protected float diff = 0.0f;

      /**
       * One-half of the range of the period.
       */
      protected float halfRange = 0.5f;

      /**
       * Whether or not to floor mod the result of the easing
       * function.
       */
      protected boolean modResult = false;

      /**
       * The range of the period.
       */
      protected float range = 1.0f;

      /**
       * Constructs the easing functional object with a default
       * range, TAU.
       */
      public PeriodicEasing () {

         this.setRange(PeriodicEasing.DEFAULT_RANGE);
      }

      /**
       * Constructs the easing functional object with a specified
       * range.
       *
       * @param range
       *           the range of the period
       */
      public PeriodicEasing ( final float range ) {

         this.setRange(range);
      }

      /**
       * A helper function which mutates protected fields a, b,
       * diff, aLtb and aGtb. This mods the origin (a) and
       * destination (b) by the range. It then finds the signed
       * distance between the mod origin and destination (diff).
       * Lastly, it evaluates which of the two is greater than the
       * other, (aLtb) and (aGtb).
       *
       * @param origin
       *           origin value
       * @param dest
       *           destination value
       * @see Utils#mod(float, float)
       */
      protected void eval ( final float origin, final float dest ) {

         this.a = Utils.mod(origin, this.range);
         this.b = Utils.mod(dest, this.range);
         this.diff = this.b - this.a;
         this.aLtb = this.a < this.b;
         this.aGtb = this.a > this.b;
      }

      /**
       * Applies the easing function. The abstract class's
       * implementation check to see if the step is out of bounds,
       * [0.0, 1.0] and if mod(origin, range) is equal to
       * mod(dest, range). The origin is returned when the step is
       * less than 0.0; the destination, when the step is greater
       * than 1.0.
       *
       * @see PeriodicEasing#eval(float, float)
       */
      @Override
      public Float apply (
            final Float origin,
            final Float dest,
            final Float step ) {

         this.eval(origin, dest);

         if (step <= 0.0f || this.diff == 0.0f) {
            return this.a;
         }
         if (step >= 1.0f) {
            return this.b;
         }
         return this.applyUnclamped(origin, dest, step);
      }

      /**
       * Applies the easing function without checked whether the
       * step is out of bounds, [0.0, 1.0].
       *
       * @param origin
       *           the origin value
       * @param dest
       *           the destination value
       * @param step
       *           the step
       * @return the interpolated value
       */
      public abstract float applyUnclamped (
            final float origin,
            final float dest,
            final float step );

      /**
       * Gets the range of the easing function.
       *
       * @return the range
       */
      public float getRange () {

         return this.range;
      }

      /**
       * Sets the range of the easing function. The range should
       * be a positive non-zero value.
       *
       * @param range
       *           the range
       */
      public void setRange ( final float range ) {

         this.range = Utils.max(Utils.abs(range), Utils.EPSILON);
         this.halfRange = range * 0.5f;
      }

      /**
       * Returns the simple name of this class, allowing the
       * functional interface to be identified without being
       * directly accessible.
       */
      @Override
      public String toString () {

         return this.getClass().getSimpleName();
      }
   }

   /**
    * A functional interface for functions with four input
    * parameters.
    *
    * @param <T>
    *           first parameter
    * @param <U>
    *           second parameter
    * @param <V>
    *           third parameter
    * @param <W>
    *           fourth parameter
    * @param <R>
    *           return type
    */
   @FunctionalInterface
   public interface QuadFunction < T, U, V, W, R > {

      /**
       * Apply the function.
       *
       * @param t
       *           first parameter
       * @param u
       *           second parameter
       * @param v
       *           third parameter
       * @param w
       *           fourth parameter
       * @return return type
       */
      R apply ( T t, U u, V v, W w );
   }

   /**
    * Eases between two values by a smooth step.
    */
   public static class SmoothStep extends LerpUnclamped {

      SmoothStep () {

         super();
      }

      @Override
      public Float apply ( final Float origin, final Float dest,
            final Float step ) {

         if (step <= 0.0f) {
            return origin;
         }
         if (step >= 1.0f) {
            return dest;
         }
         final double td = step;
         final float ts = (float) (td * td * (3.0d - 2.0d * td));
         return super.apply(origin, dest, ts);
      }
   }

   /**
    * A functional interface for functions with three input
    * parameters.
    *
    * @param <T>
    *           first parameter
    * @param <U>
    *           second parameter
    * @param <V>
    *           third parameter
    * @param <R>
    *           return type
    */
   @FunctionalInterface
   public interface TriFunction < T, U, V, R > {

      /**
       * Apply the function.
       *
       * @param t
       *           first parameter
       * @param u
       *           second parameter
       * @param v
       *           third parameter
       * @return result
       */
      R apply ( T t, U u, V v );
   }

   /**
    * The epsilon, \u03b5, provided to approximation functions
    * when none is specified by the user.
    */
   public static float EPSILON = 0.0f;

   static {
      Utils.EPSILON = IUtils.DEFAULT_EPSILON;
   }

   /**
    * An alternative to the {@link Math#abs(double)} function.
    * Relies on bit-masking to remove the sign bit.
    *
    * @param value
    *           the input value
    * @return the absolute value
    * @see Math#abs(double)
    * @see Double#longBitsToDouble(long)
    */
   public static double abs ( final double value ) {

      return Double.longBitsToDouble(Double.doubleToLongBits(value) << 1 >>> 1);
   }

   /**
    * An alternative to the {@link Math#abs(float)} function.
    * Relies on bit-masking to remove the sign bit.
    *
    * @param value
    *           the input value
    * @return the absolute value
    * @see Math#abs(float)
    * @see Float#intBitsToFloat(int)
    */
   public static float abs ( final float value ) {

      return Float.intBitsToFloat(0x7fffffff & Float.floatToRawIntBits(value));
   }

   /**
    * Returns a value in the range [0.0, \u03c0]: \u03c0, when
    * the input is -1.0; \u03c0 / 2.0, when the input is 0.0;
    * 0.0, when the input is 1.0.
    *
    * Based on the algorithm at <a href=
    * "https://developer.download.nvidia.com/cg/acos.html">https://developer.download.nvidia.com/cg/acos.html</a>
    * , which in turn cites M. Abramowitz and I.A. Stegun,
    * Eds., <em>Handbook of Mathematical Functions</em> .
    *
    * @param value
    *           the input value
    * @return the angle in radians
    * @see Math#acos(double)
    */
   public static float acos ( float value ) {

      if (value <= -1.0f) {
         return IUtils.PI;
      }
      if (value >= 1.0f) {
         return 0.0f;
      }

      final float negate = value < 0.0f ? 1.0f : 0.0f;
      value = Utils.abs(value);
      float ret = -0.0187293f;
      ret = ret * value;
      ret = ret + 0.0742610f;
      ret = ret * value;
      ret = ret - 0.2121144f;
      ret = ret * value;
      ret = ret + IUtils.HALF_PI;
      ret = ret * (float) Math.sqrt(1.0f - value);
      ret = ret - negate * (ret + ret);
      return negate * IUtils.PI + ret;
   }

   /**
    * Evaluates two floats like booleans, using the analytic
    * definition of the and (AND) logic gate.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @return the evaluation
    * @see Utils#bool(float)
    */
   public static float and ( final float a, final float b ) {

      return Utils.bool(a) * Utils.bool(b);
   }

   /**
    * Evaluates two integers like booleans, using the analytic
    * definition of the and (AND) logic gate.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @return the evaluation
    * @see Utils#bool(int)
    */
   public static int and ( final int a, final int b ) {

      return Utils.bool(a) * Utils.bool(b);
   }

   /**
    * A quick approximation test. Tests to see if the absolute
    * of the difference between two values is less than an
    * epsilon. Does not handle edge cases.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @return the evaluation
    * @see Utils#diff(float, float)
    * @see IUtils#DEFAULT_EPSILON
    */
   public static boolean approxFast ( final float a, final float b ) {

      return Utils.approxFast(a, b, Utils.EPSILON);
   }

   /**
    * A quick approximation test. Tests to see if the absolute
    * of the difference between two values is less than a
    * tolerance. Does not handle edge cases.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param tolerance
    *           the tolerance
    * @return the evaluation
    * @see Utils#diff(float, float)
    */
   public static boolean approxFast (
         final float a,
         final float b,
         final float tolerance ) {

      final float diff = b - a;
      return diff <= tolerance && diff >= -tolerance;
   }

   /**
    * Returns a value in the range [-\u03c0 / 2.0, \u03c0 /
    * 2.0]: -\u03c0 / 2.0, when the input is -1.0; 0.0, when
    * the input is 0.0; \u03c0 / 2.0, when the input is 1.0.
    *
    * Based on the algorithm at <a href=
    * "https://developer.download.nvidia.com/cg/asin.html">https://developer.download.nvidia.com/cg/asin.html</a>
    * , which in turn cites M. Abramowitz and I.A. Stegun,
    * Eds., <em>Handbook of Mathematical Functions</em> .
    *
    * @param value
    *           the input value
    * @return the angle in radians
    * @see Math#asin(double)
    */
   public static float asin ( float value ) {

      if (value <= -1.0f) {
         return -IUtils.HALF_PI;
      }
      if (value >= 1.0f) {
         return IUtils.HALF_PI;
      }

      final float negate = value < 0.0f ? 1.0f : 0.0f;
      value = Utils.abs(value);
      float ret = -0.0187293f;
      ret *= value;
      ret += 0.0742610f;
      ret *= value;
      ret -= 0.2121144f;
      ret *= value;
      ret += IUtils.HALF_PI;
      ret = IUtils.PI * 0.5f - (float) Math.sqrt(1.0f - value) * ret;
      return ret - negate * (ret + ret);
   }

   /**
    * An alternative to {@link Math#atan2(double, double)} .
    * Based on the algorithm at <a href=
    * "https://developer.download.nvidia.com/cg/atan2.html">https://developer.download.nvidia.com/cg/atan2.html</a>
    * .
    *
    * @param y
    *           the y coordinate
    * @param x
    *           the x coordinate
    * @return the angle
    */
   public static float atan2 ( final float y, final float x ) {

      final float yAbs = Utils.abs(y);
      final float xAbs = Utils.abs(x);
      float t1 = yAbs;
      float t2 = xAbs;
      float t0 = Utils.max(t1, t2);
      t1 = Utils.min(t1, t2);
      t2 = 1.0f / t0;
      t2 = t1 * t2;
      final float t3 = t2 * t2;
      t0 = -0.013480470f;
      t0 = t0 * t3 + 0.057477314f;
      t0 = t0 * t3 - 0.121239071f;
      t0 = t0 * t3 + 0.195635925f;
      t0 = t0 * t3 - 0.332994597f;
      t0 = t0 * t3 + 0.999995630f;
      t2 = t0 * t2;
      t2 = yAbs > xAbs ? IUtils.HALF_PI - t2 : t2;
      t2 = x < 0.0f ? IUtils.PI - t2 : t2;
      return y < 0.0f ? -t2 : t2;
   }

   /**
    * Converts a float to a boolean, then back to a float. In
    * effect, this asks if the value is true, or is the case.
    *
    * Used in conjunction with float-based boolean operators:
    * or, and, etc.
    *
    * @param value
    *           the input float
    * @return the float
    * @see Utils#toFloat(boolean)
    * @see Utils#toBool(float)
    * @see Utils#or(float, float)
    * @see Utils#and(float, float)
    */
   public static float bool ( final float value ) {

      return Utils.toFloat(Utils.toBool(value));
   }

   /**
    * Converts an integer to a boolean, then back to a float.
    * In effect, this asks if the value is true, or is the
    * case.
    *
    * Used in conjunction with integer-based boolean operators:
    * or, and, etc.
    *
    * @param value
    *           the input float
    * @return the integer
    * @see Utils#toFloat(boolean)
    * @see Utils#toBool(float)
    * @see Utils#or(float, float)
    * @see Utils#and(float, float)
    */
   public static int bool ( final int value ) {

      return Utils.toInt(Utils.toBool(value));
   }

   /**
    * An alternative to {@link Math#ceil(double)} . ceil ( x )
    * = -floor ( -x )
    *
    * @param value
    *           the input value
    * @return the ceiled value.
    * @see Math#ceil(double)
    * @see Float#isNaN(float)
    */
   public static float ceil ( final float value ) {

      return -Utils.floor(-value);
   }

   /**
    * Ceils the input float to an integer.
    *
    * @param x
    *           the input value
    * @return the integer
    */
   public static int ceilToInt ( final float x ) {

      final int xi = (int) x;
      if (x > xi) {
         return xi + 1;
      }
      return xi;
   }

   /**
    * Clamps a value between a lower and an upper bound.
    *
    * @param value
    *           the input value
    * @param lowerBound
    *           the upper bound
    * @param upperBound
    *           the lower bound
    * @return the clamped value
    */
   public static float clamp (
         final float value,
         final float lowerBound,
         final float upperBound ) {

      return value < lowerBound ? lowerBound
            : value > upperBound ? upperBound : value;
   }

   /**
    * Clamps a value to the range [0.0, 1.0].
    *
    * @param value
    *           the input value
    * @return the clamped value
    * @see Utils#clamp(float, float, float)
    */
   public static float clamp01 ( final float value ) {

      return value < 0.0f ? 0.0f : value > 1.0f ? 1.0f : value;
   }

   /**
    * Converts an angle in radians to an angle in degrees.
    *
    * @param radians
    *           the angle in radians
    * @return the angle in degrees
    */
   public static float degrees ( final float radians ) {

      return IUtils.RAD_TO_DEG * radians;
   }

   /**
    * Finds the absolute value of the left operand minus the
    * right.
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @return the difference
    * @see Utils#abs(float)
    */
   public static float diff ( final float a, final float b ) {

      return Utils.abs(a - b);
   }

   /**
    * Divides the left operand by the right, but returns zero
    * when the denominator is zero, avoiding the
    * java.lang.ArithmeticException . This is to simulate the
    * convention of shading languages like GLSL and OSL.
    *
    * @param a
    *           the numerator
    * @param b
    *           the denominator
    * @return the quotient
    */
   public static double div ( final double a, final double b ) {

      return b == 0.0d || Double.isNaN(b) ? 0.0d : a / b;
   }

   /**
    * Divides the left operand by the right, but returns zero
    * when the denominator is zero, avoiding the
    * java.lang.ArithmeticException . This is to simulate the
    * convention of shading languages like GLSL and OSL.
    *
    * @param a
    *           the numerator
    * @param b
    *           the denominator
    * @return the quotient
    */
   public static float div ( final float a, final float b ) {

      return b == 0.0f || b != b ? 0.0f : a / b;
   }

   /**
    * An alternative to {@link Math#floor(double)} . Returns
    * 0.0 when the input value is NaN .
    *
    * @param value
    *           the input value
    * @return the floored value
    * @see Double#isNaN(double)
    * @see Math#floor(double)
    */
   public static double floor ( final double value ) {

      if (value == 0.0d || Double.isNaN(value)) {
         return 0.0d;
      }

      return value > 0.0d ? (long) value : (long) value - 1;
   }

   /**
    * An alternative to {@link Math#floor(double)} . Returns
    * 0.0 when the input value is NaN .
    *
    * @param value
    *           the input value
    * @return the floored value.
    * @see Float#isNaN(float)
    * @see Math#floor(double)
    */
   public static float floor ( final float value ) {

      if (value == 0.0f || value != value) {
         return 0.0f;
      }

      return value > 0.0f ? (int) value : (int) value - 1;
   }

   /**
    * Floors the input float to an integer.
    *
    * @param value
    *           the input value
    * @return the integer
    */
   public static int floorToInt ( final float value ) {

      final int xi = (int) value;
      if (value < xi) {
         return xi - 1;
      }
      return xi;
   }

   /**
    * Applies the modulo operator (%) to the operands, which
    * implicitly uses the formula fmod ( a, b ) := a - b trunc
    * ( a / b ) .
    *
    * When the left operand is negative and the right operand
    * is positive, the result will be negative. For periodic
    * values, such as an angle, where the direction of change
    * could be either clockwise or counterclockwise, use mod.
    *
    * If the right operand is one, use fract(a) or a - trunc(a)
    * instead.
    *
    * If the right operand is zero or NaN, returns the left
    * operand.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @return the result
    * @see Utils#mod(float, float)
    * @see Utils#fract(float)
    * @see Utils#trunc(float)
    * @see Float#isNaN(float)
    */
   public static float fmod ( final float a, final float b ) {

      if (b == 0.0f || b != b) {
         return a;
      }
      return a % b;
   }

   /**
    * Finds the fractional portion of the input value by
    * subtracting the value's truncation from the value, i.e.,
    * fract ( a ) := a - trunc ( a ) . Returns 1.0 if the input
    * value is NaN.
    *
    * Use this instead of fmod ( a, 1.0 ) or a % 1.0.
    *
    * @param value
    *           the input value
    * @return the fractional portion
    * @see Float#isNaN(float)
    * @see Utils#trunc(float)
    */
   public static float fract ( final float value ) {

      if (value != value) {
         return 1.0f;
      }
      return value - (int) value;
   }

   /**
    * Finds the hypotenuse between two values.
    * 
    * @param a the first value
    * @param b the second value
    * @return the hypotenuse
    * @see Math#sqrt(double)
    */
   public static float hypot ( final float a, final float b ) {

      return (float) Math.sqrt(a * a + b * b);
   }

   /**
    * Finds the hypotenuse between three values.
    * 
    * @param a the first value
    * @param b the second value
    * @param c the third value
    * @return the hypotenuse
    * @see Math#sqrt(double)
    */
   public static float hypot ( 
         final float a, 
         final float b, 
         final float c ) {

      return (float) Math.sqrt(a * a + b * b + c * c);
   }

   /**
    * Finds 1.0 divided by the square-root of a value. This is
    * <em>not</em> a fast inverse square root function; it
    * depends on {@link Math#sqrt(double)} . Returns zero when
    * the value is zero or NaN.
    *
    * @param value
    *           the value
    * @return the inverse square-root
    */
   public static float invSqrt ( final float value ) {

      if (value <= 0.0f || value != value) {
         return 0.0f;
      }
      return (float) (1.0d / Math.sqrt(value));
   }

   /**
    * Linear interpolation from the origin to the destination
    * value by a step. If the step is less than zero, returns
    * the origin. If the step is greater than one, returns the
    * destination.
    *
    * @param origin
    *           the origin value
    * @param dest
    *           the destination value
    * @param step
    *           the step
    * @return the interpolated value
    * @see Utils#lerpUnclamped(float, float, float)
    */
   public static float lerp (
         final float origin,
         final float dest,
         final float step ) {

      if (step <= 0.0f) {
         return origin;
      }
      if (step >= 1.0f) {
         return dest;
      }
      return Utils.lerpUnclamped(origin, dest, step);
   }

   /**
    * Linear interpolation from the origin to the destination
    * value by a step. Does not check to see if the step is
    * beyond the range [0.0, 1.0] .
    *
    * @param origin
    *           the origin value
    * @param dest
    *           the destination value
    * @param step
    *           the step
    * @return the interpolated value
    */
   public static float lerpUnclamped (
         final float origin,
         final float dest,
         final float step ) {

      // final double td = step;
      // return (float) ((1.0d - td) * origin + td * dest);

      return (1.0f - step) * origin + step * dest;
   }

   /**
    * Maps an input value from an original range to a target
    * range. If the upper and lower bound of the original range
    * are equal, will return the lower bound of the destination
    * range.
    *
    * @param value
    *           the input value
    * @param lbOrigin
    *           lower bound of original range
    * @param ubOrigin
    *           upper bound of original range
    * @param lbDest
    *           lower bound of destination range
    * @param ubDest
    *           upper bound of destination range
    * @return the mapped value
    */
   public static float map (
         final float value,
         final float lbOrigin,
         final float ubOrigin,
         final float lbDest,
         final float ubDest ) {

      final float denom = ubOrigin - lbOrigin;
      if (denom == 0.0f) {
         return lbDest;
      }
      return lbDest + (ubDest - lbDest) * ((value - lbOrigin) / denom);
   }

   /**
    * An alternative to {@link Math#max(float, float)} .
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @return the minimal value
    * @see Math#max(float, float)
    */
   public static float max ( final float a, final float b ) {

      return a >= b ? a : a < b ? b : 0.0f;
   }

   /**
    * A convenience function for finding the maximum of three
    * inputs.
    *
    * @param a
    *           the first input
    * @param b
    *           the second input
    * @param c
    *           the third input
    * @return the minimum value
    * @see Utils#max(float, float)
    */
   public static float max (
         final float a,
         final float b,
         final float c ) {

      return Utils.max(Utils.max(a, b), c);
   }

   /**
    * An alternative to {@link Math#min(float, float)} .
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @return the minimal value
    * @see Math#min(float, float)
    */
   public static float min ( final float a, final float b ) {

      return a <= b ? a : a > b ? b : 0.0f;
   }

   /**
    * A convenience function for finding the minimum of three
    * inputs.
    *
    * @param a
    *           the first input
    * @param b
    *           the second input
    * @param c
    *           the third input
    * @return the minimum value
    * @see Utils#min(float, float)
    */
   public static float min (
         final float a,
         final float b,
         final float c ) {

      return Utils.min(Utils.min(a, b), c);
   }

   /**
    * Applies floorMod to the operands, and therefore uses the
    * formula mod ( a, b ) := a - b * floor ( a / b ) .
    *
    * If the right operand is one, use a - floor(a) instead.
    *
    * If the right operand is zero, returns the left operand
    * instead of throwing a java.lang.ArithmeticException for
    * division by zero.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @return the result
    * @see Utils#modUnchecked(float, float)
    */
   public static float mod ( final float a, final float b ) {

      if (b == 0.0f || b != b) {
         return a;
      }
      return Utils.modUnchecked(a, b);
   }

   /**
    * An alternative to {@link Math#floorMod(int, int)} .
    * Returns a when b is zero.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @return the result
    * @see Math#floorMod(int, int)
    */
   public static int mod ( final int a, final int b ) {

      if (b == 0) {
         return a;
      }
      final int result = a - b * (a / b);
      return result < 0 ? result + b : result;
   }

   /**
    * Subtracts the floor of the input value from the value.
    *
    * Use this instead of mod(a, 1.0).
    *
    * @param value
    *           the input value
    * @return the result
    * @see Utils#mod(float, float)
    * @see Utils#floor(float)
    */
   public static float mod1 ( final float value ) {

      return value - Utils.floor(value);
   }

   /**
    * A specialized version of mod which shifts an angle in
    * degrees to the range [0, 360]. Casts float input to a
    * double, uses a constant for 1.0 / 360, then casts back to
    * a float upon return.
    *
    * @param degrees
    *           the input angle
    * @return the output angle
    */
   public static float modDegrees ( final float degrees ) {

      final double degd = degrees;
      return (float) (degd - 360.0d * Utils.floor(degd * IUtils.ONE_360_D));
   }

   /**
    * A specialized version of mod which shifts an angle in
    * radians to the range [0, \u03c4] .
    *
    * @param radians
    *           the angle in radians
    * @return the unsigned radians
    * @see Utils#mod(float, float)
    * @see IUtils#TAU_D
    * @see IUtils#ONE_TAU_D
    * @see Utils#floor(double)
    */
   public static float modRadians ( final float radians ) {

      final double radd = radians;
      return (float) (radd
            - IUtils.TAU_D * Utils.floor(radd * IUtils.ONE_TAU_D));
   }

   /**
    * Applies floorMod to the operands, and therefore uses the
    * formula mod ( a, b ) := a - b * floor ( a / b ) .
    *
    * If the right operand is one, use moda = a - floor(a)
    * instead.
    *
    * Does not check if the right operand is zero.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @return the result
    * @see Utils#mod1(float)
    * @see Utils#floor(float)
    */
   public static float modUnchecked ( final float a, final float b ) {

      return a - b * Utils.floor(a / b);
   }

   /**
    * The negation of a float holding a boolean value. Given a
    * == 1.0, !a == 0.0 . Given a == 0.0, !a == 1.0 .
    *
    * @param value
    *           the input value
    * @return the negation
    * @see Utils#bool(float)
    */
   public static float not ( final float value ) {

      return 1.0f - Utils.bool(value);
   }

   /**
    * The negation of an integer holding a boolean value. Given
    * a == 1, !a == 0 . Given a == 0, !a == 1 .
    *
    * @param value
    *           the input value
    * @return the negation
    * @see Utils#bool(int)
    */
   public static int not ( final int value ) {

      return 1 - Utils.bool(value);
   }

   /**
    * Evaluates two floats like booleans, using the analytic
    * definition of the inclusive or (OR) logic gate.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @return the evaluation
    * @see Utils#bool(float)
    */
   public static float or ( final float a, final float b ) {

      final float aBool = Utils.bool(a);
      final float bBool = Utils.bool(b);
      return aBool + bBool - aBool * bBool;
   }

   /**
    * Evaluates two integers like booleans, using the analytic
    * definition of the inclusive or (OR) logic gate.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @return the evaluation
    * @see Utils#bool(int)
    */
   public static int or ( final int a, final int b ) {

      final int aBool = Utils.bool(a);
      final int bBool = Utils.bool(b);
      return aBool + bBool - aBool * bBool;
   }

   /**
    * Converts an angle in degrees to an angle in radians.
    *
    * @param degrees
    *           the angle in degrees
    * @return the angle in radians
    */
   public static float radians ( final float degrees ) {

      return IUtils.DEG_TO_RAD * degrees;
   }

   /**
    * An alternative to the {@link Math#signum(float)}
    * function.
    *
    * @param value
    *           the input value
    * @return the sign
    * @see Math#signum(float)
    */
   public static int sign ( final float value ) {

      return value < 0.0f ? -1 : value > 0.0f ? 1 : 0;

      // return
      // Float.intBitsToFloat((Float.floatToRawIntBits(value) &
      // -2147483648) | 1065353216);
   }

   /**
    * Eases between an origin and destination by a step in
    * [0.0, 1.0] .
    *
    * @param origin
    *           the origin
    * @param dest
    *           the destination
    * @param step
    *           the step
    * @return the eased value
    */
   public static float smootherStep (
         final float origin,
         final float dest,
         final float step ) {

      if (step <= 0.0f) {
         return origin;
      }
      if (step >= 1.0f) {
         return dest;
      }

      // TODO: Needs testing.
      final float t = step * step * (step * (step * 6.0f - 15.0f) + 10.0f);
      return (1.0f - t) * origin + t * dest;
   }

   /**
    * Eases between an origin and destination by a step in
    * [0.0, 1.0] .
    *
    * @param origin
    *           the origin
    * @param dest
    *           the destination
    * @param step
    *           the step
    * @return the eased value
    */
   public static float smoothStep (
         final float origin,
         final float dest,
         final float step ) {

      if (step <= 0.0f) {
         return origin;
      }
      if (step >= 1.0f) {
         return dest;
      }

      final float t = step * step * (3.0f - (step + step));
      return (1.0f - t) * origin + t * dest;
   }

   /**
    * Converts a float to a boolean, where 0.0 and NaN are
    * false; all other values are true.
    *
    * @param value
    *           the input value
    * @return the boolean
    */
   public static boolean toBool ( final float value ) {

      return value == 0.0f || value != value ? false : true;
   }

   /**
    * Converts an integer to a boolean, where 0 is false; all
    * other values are true.
    *
    * @param value
    *           the input value
    * @return the boolean
    */
   public static boolean toBool ( final int value ) {

      return value == 0 ? false : true;
   }

   /**
    * A quick, dirty representation of a single-precision real
    * number as a String to a number of places. Edge cases:
    * <ul>
    * <li>When the number of places is less than one, returns
    * the String of the truncated value.</li>
    * <li>When the value is not a number ( {@link Float#NaN} ),
    * returns "0.0".</li>
    * <li>When the value exceeds {@link Float#MAX_VALUE},
    * defers to {@link Float#toString(float)} .</li>
    * <li>When the length of the integral exceeds a limit
    * beyond which accurate representation is unlikely, such as
    * with scientific notation, defers to
    * {@link Float#toString(float)} .</li>
    * </ul>
    *
    * Intended to serve as an alternative to
    * {@link String#format(String, Object...)}, which is very
    * slow, and DecimalFormat, which extrapolates values beyond
    * the last decimal place.
    *
    * @param value
    *           the real number
    * @param places
    *           the desired number of decimal places
    * @return the string
    */
   public static String toFixed (
         final float value,
         final int places ) {

      /* Value is Not a number (NaN). */
      if (value != value) {
         return "0.0";
      }

      if (places < 0) {
         return Integer.toString((int) value);
      }

      if (places < 1) {
         return Float.toString((int) value);
      }

      /* Value is too big. */
      if (value <= -3.4028235E38f || value >= 3.4028235E38f) {
         return Float.toString(value);
      }

      /*
       * Hard-coded values from FloatConsts class for fast
       * absolute value and sign.
       */
      final int raw = Float.floatToRawIntBits(value);
      final float sign = Float.intBitsToFloat(raw & -2147483648 | 1065353216);
      final float abs = Float.intBitsToFloat(raw & 2147483647);
      final int trunc = (int) abs;
      final StringBuilder sb = new StringBuilder(16);

      /*
       * Append integral to StringBuilder.
       */
      int len = 0;
      if (sign < 0.0f) {
         sb.append('-').append(trunc);
         len = sb.length() - 1;
      } else {
         sb.append(trunc);
         len = sb.length();
      }
      sb.append('.');

      /*
       * Hard-coded limit on the number of worthwhile decimal
       * places beyond which single precision is no longer worth
       * representing accurately.
       */
      final int maxPlaces = 9 - len;

      /*
       * The integral has so many decimal places that it has
       * consumed the allotment. (Might be scientific notation?)
       */
      if (maxPlaces < 1) {
         return Float.toString(value);
      }

      final int vetPlaces = places < maxPlaces ? places : maxPlaces;
      float frac = abs - trunc;

      /* Truncation. */
      for (int i = 0; i < vetPlaces; ++i) {
         frac *= 10.0f;
         final int tr = (int) frac;
         frac -= tr;
         sb.append(tr);
      }

      /*
       * Cache digits up to one beyond the number of requested
       * places for rounding purposes.
       */

      // final int[] digits = new int[vetPlaces + 1];
      // for (int i = 0; i <= vetPlaces; ++i) {
      // frac *= 10.0f;
      // frac -= digits[i] = (int) frac;
      // }

      /* Append to StringBuilder. */
      // for (int i = 0; i < vetPlaces; ++i) {
      // sb.append(digits[i]);
      // }

      return sb.toString();
   }

   /**
    * Converts a boolean value to a float, where 1.0 is true
    * and 0.0 is false.
    *
    * @param bool
    *           the input boolean
    * @return the float value
    */
   public static float toFloat ( final boolean bool ) {

      return bool ? 1.0f : 0.0f;
   }

   /**
    * Converts a boolean value to an integer, where 1 is true
    * and 0 is false.
    *
    * @param bool
    *           the input boolean
    * @return the integer value
    */
   public static int toInt ( final boolean bool ) {

      return bool ? 1 : 0;
   }

   /**
    * Truncates the input value. This is an alias for
    * explicitly casting a float to an integer, then implicitly
    * casting the integral to a float.
    *
    * @param value
    *           the input value
    * @return the integral
    */
   public static float trunc ( final float value ) {

      return (int) value;
   }

   /**
    * Evaluates two floats like booleans, using the analytic
    * definition of the exclusive or (XOR) logic gate.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @return the evaluation
    * @see Utils#toBool(float)
    */
   public static float xor ( final float a, final float b ) {

      final float aBool = Utils.bool(a);
      final float bBool = Utils.bool(b);
      return aBool + bBool - 2.0f * aBool * bBool;
   }

   /**
    * Evaluates two integers like booleans, using the analytic
    * definition of the exclusive or (XOR) logic gate.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @return the evaluation
    * @see Utils#bool(int)
    */
   public static int xor ( final int a, final int b ) {

      final int aBool = Utils.bool(a);
      final int bBool = Utils.bool(b);
      return aBool + bBool - 2 * aBool * bBool;
   }
}
