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
       * @see Utils#modUnchecked(float, float)
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
            return Utils.modUnchecked(fac, this.range);
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
       * @see Utils#modUnchecked(float, float)
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
            return Utils.modUnchecked(fac, this.range);
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
       * @see Utils#modUnchecked(float, float)
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
            return Utils.modUnchecked(fac, this.range);
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
       * @see Utils#modUnchecked(float, float)
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
            return Utils.modUnchecked(fac, this.range);
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
      protected transient float a = 0.0f;

      /**
       * Whether or not the start angle is greater than the stop
       * angle.
       */
      protected transient boolean aGtb = false;

      /**
       * Whether or not the start angle is less than the stop
       * angle.
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
       * Whether or not to floor mod the result of the easing
       * function.
       */
      protected transient boolean modResult = false;

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
         final double td = step;
         final float ts = (float) (td * td * (3.0d - (td + td)));
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

      // abs(a) := max(-a, a)
      return Float.intBitsToFloat(0x7fffffff & Float.floatToRawIntBits(value));
   }

   /**
    * A bounds-checked approximation of the arc-cosine for
    * single precision real numbers.
    *
    * Returns a value in the range [0.0, \u03c0]: \u03c0, when
    * the input is -1.0; \u03c0 / 2.0, when the input is 0.0;
    * 0.0, when the input is 1.0.
    *
    * Based on the algorithm at the <a href=
    * "https://developer.download.nvidia.com/cg/acos.html">Nvidia
    * Cg 3.1 Toolkit Documentation</a> , which in turn cites M.
    * Abramowitz and I.A. Stegun, Eds., <em>Handbook of
    * Mathematical Functions</em> .
    *
    * @param value
    *           the input value
    * @return the angle in radians
    * @see Math#acos(double)
    */
   public static float acos ( final float value ) {

      if (value <= -1.0f) {
         return IUtils.PI;
      }
      if (value >= 1.0f) {
         return 0.0f;
      }

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
    * Evaluates two floats like booleans using the and logic
    * gate.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @return the evaluation
    * @see Utils#bool(float)
    */
   public static int and ( final float a, final float b ) {

      return Utils.toInt(Utils.toBool(a) & Utils.toBool(b));
   }

   /**
    * Evaluates two integers like booleans, using the and (AND)
    * logic gate.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @return the evaluation
    * @see Utils#bool(int)
    */
   public static int and ( final int a, final int b ) {

      return Utils.toInt(Utils.toBool(a) & Utils.toBool(b));
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
   public static boolean approx ( final float a, final float b ) {

      return Utils.approx(a, b, Utils.EPSILON);
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
   public static boolean approx (
         final float a,
         final float b,
         final float tolerance ) {

      final float diff = b - a;
      return diff <= tolerance && diff >= -tolerance;
   }

   /**
    * A bounds-checked approximation of the arc-sine for single
    * precision real numbers.
    *
    * Returns a value in the range [-\u03c0 / 2.0, \u03c0 /
    * 2.0]: -\u03c0 / 2.0, when the input is -1.0; 0.0, when
    * the input is 0.0; \u03c0 / 2.0, when the input is 1.0.
    *
    * Based on the algorithm at the <a href=
    * "https://developer.download.nvidia.com/cg/asin.html">Nvidia
    * Cg 3.1 Toolkit Documentation</a> , which in turn cites M.
    * Abramowitz and I.A. Stegun, Eds., <em>Handbook of
    * Mathematical Functions</em> .
    *
    * @param value
    *           the input value
    * @return the angle in radians
    * @see Math#asin(double)
    */
   public static float asin ( final float value ) {

      if (value <= -1.0f) {
         return -IUtils.HALF_PI;
      }
      if (value >= 1.0f) {
         return IUtils.HALF_PI;
      }

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
    * Finds a single precision approximation of a signed angle
    * given a vertical and horizontal component. Note that the
    * vertical component <em>precedes</em> the horizontal. The
    * return value falls in the range [-\u03c0, \u03c0] .
    *
    * An alternative to {@link Math#atan2(double, double)} .
    * Based on the algorithm at the <a href=
    * "https://developer.download.nvidia.com/cg/atan2.html">Nvidia
    * Cg 3.1 Toolkit Documentation</a> .
    *
    * @param y
    *           the y coordinate (the ordinate)
    * @param x
    *           the x coordinate (the abscissa)
    * @return the angle
    */
   public static float atan2 ( final float y, final float x ) {

      final boolean yLtZero = y < 0.0f;
      final boolean xLtZero = x < 0.0f;
      final float yAbs = yLtZero ? -y : y;
      final float xAbs = xLtZero ? -x : x;

      final boolean yGtX = yAbs > xAbs;
      float t0 = yGtX ? yAbs : xAbs;
      if (t0 == 0.0f) {
         return 0.0f;
      }
      float t2 = (yGtX ? xAbs : yAbs) / t0;

      /*
       * When stored independently as floats, some of these magic
       * numbers are truncated to slightly different values.
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
    * Converts a float to a boolean, then to an int. In effect,
    * this asks if the value is true, or is the case.
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
   public static int bool ( final float value ) {

      return Utils.toInt(Utils.toBool(value));
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
    * = -floor ( -x ) .
    *
    * @param value
    *           the input value
    * @return the ceiled value.
    * @see Math#ceil(double)
    * @see Float#isNaN(float)
    */
   public static float ceil ( final float value ) {

      return value > 0.0f ? (int) value + 1.0f
            : value < 0.0f ? (int) value : 0.0f;
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
    * Clamps a real number value between a lower and an upper
    * bound.
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
    * Clamps a whole number value between a lower and an upper
    * bound.
    *
    * @param value
    *           the input value
    * @param lowerBound
    *           the upper bound
    * @param upperBound
    *           the lower bound
    * @return the clamped value
    */
   public static int clamp (
         final int value,
         final int lowerBound,
         final int upperBound ) {

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
    * Finds the approximate cosine of an angle in radians.
    * Returns a value in the range [-1.0, 1.0] . An alternative
    * to the double precision {@link Math#cos(double)} , this
    * function uses single-precision numbers.<br>
    * <br>
    * To find the cosine and sine of an angle simultaneously,
    * use either {@link Vec2#fromPolar(float, Vec2)} (the
    * target vector should be created once, in advance)..
    *
    * @param radians
    *           the angle in radians
    * @return the cosine of the angle
    * @see SinCos#eval(float)
    */
   public static float cos ( final float radians ) {

      return SinCos.eval(IUtils.ONE_TAU * radians);
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

      return b == 0.0d || b != b ? 0.0d : a / b;
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

      return value > 0.0d ? (int) value
            : value < 0.0d ? (int) value - 1.0d : 0.0d;
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

      return value > 0.0f ? (int) value
            : value < 0.0f ? (int) value - 1.0f : 0.0f;
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
         return 0.0f;
      }
      return value - (int) value;
   }

   /**
    * Finds the hypotenuse between two values. Useful when
    * finding the magnitude of a vector.
    *
    * @param a
    *           the first value
    * @param b
    *           the second value
    * @return the hypotenuse
    * @see Utils#sqrtUnchecked(float)
    */
   public static float hypot ( final float a, final float b ) {

      /*
       * Do not use Math.hypot . It is not a
       * HotSpotIntrinsicCandidate , and it delegates to
       * StrictMath .
       */

      return Utils.sqrtUnchecked(a * a + b * b);
   }

   /**
    * Finds the hypotenuse given three values. Useful when
    * finding the magnitude of a vector.
    *
    * @param a
    *           the first value
    * @param b
    *           the second value
    * @param c
    *           the third value
    * @return the hypotenuse
    * @see Utils#sqrtUnchecked(float)
    */
   public static float hypot (
         final float a,
         final float b,
         final float c ) {

      return Utils.sqrtUnchecked(a * a + b * b + c * c);
   }

   /**
    * Finds one divided by the hypotenuse of two values. Useful
    * when normalizing vectors.
    *
    * @param a
    *           the first value
    * @param b
    *           the second value
    * @return the inverse hypotenuse
    * @see Utils#invSqrtUnchecked
    */
   public static float invHypot (
         final float a,
         final float b ) {

      return Utils.invSqrtUnchecked(a * a + b * b);
   }

   /**
    * Finds one divided by the hypotenuse of three values.
    * Useful when normalizing vectors.
    *
    * @param a
    *           the first value
    * @param b
    *           the second value
    * @param c
    *           the third value
    * @return the inverse hypotenuse
    * @see Utils#invSqrtUnchecked
    */
   public static float invHypot (
         final float a,
         final float b,
         final float c ) {

      return Utils.invSqrtUnchecked(a * a + b * b + c * c);
   }

   /**
    * A fast inverse square-root. Returns 0.0 when the value is
    * less then or equal to zero. Use the unchecked version
    * when the input value is known to be positive.
    *
    * @param value
    *           the value
    * @return the inverse square root
    * @see Utils#invSqrtUnchecked(float)
    */
   public static float invSqrt ( final float value ) {

      return value > 0.0f ? Utils.invSqrtUnchecked(value) : 0.0f;
   }

   /**
    * The fast inverse square root implementation based on the
    * 'evil bit hack' from Quake 3, as described by Chris
    * Lomont in "<a href=
    * "http://www.lomont.org/papers/2003/InvSqrt.pdf">Fast
    * Inverse Square Root</a>." For accuracy, the result is
    * refined three times with the Newton-Raphson method.<br>
    * <br>
    * Useful when normalizing vectors or quaternions. Give this
    * preference over {@link Utils#sqrt(float)} when
    * normalizing.
    *
    * @param value
    *           the value
    * @return the inverse square root
    * @author Chris Lomont
    * @author Greg Walsh
    * @see Float#floatToIntBits(float)
    * @see Float#intBitsToFloat(int)
    */
   public static float invSqrtUnchecked ( final float value ) {

      final float vhalf = value * 0.5f;
      float y = Float.intBitsToFloat(0x5f375a86 -
            (Float.floatToIntBits(value) >> 1));

      y *= 1.5f - vhalf * y * y;
      y *= 1.5f - vhalf * y * y;
      y *= 1.5f - vhalf * y * y;

      return y;
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
   public static int lerp (
         final int origin,
         final int dest,
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

      return (1.0f - step) * origin + step * dest;
   }

   /**
    * Linear interpolation from the origin to the destination
    * value by a step. Does not check to see if the step is
    * beyond the range [0.0, 1.0] .
    *
    * Rounds the result to an integer.
    *
    * @param origin
    *           the origin value
    * @param dest
    *           the destination value
    * @param step
    *           the step
    * @return the interpolated value
    * @see Utils#round(float)
    */
   public static int lerpUnclamped (
         final int origin,
         final int dest,
         final float step ) {

      return Utils.round((1.0f - step) * origin + step * dest);
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

      final float d = a >= b ? a : a < b ? b : 0.0f;
      return d >= c ? d : d < c ? c : 0.0f;
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

      final float d = a <= b ? a : a > b ? b : 0.0f;
      return d <= c ? d : d > c ? c : 0.0f;
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

      final float value = a / b;
      return a - b * (value > 0.0f ? (int) value
            : value < 0.0f ? (int) value - 1.0f : 0.0f);
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
   public static double mod1 ( final double value ) {

      return value > 0.0d ? value - (int) value
            : value < 0.0d ? value - ((int) value - 1.0d) : 0.0d;
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

      return value > 0.0f ? value - (int) value
            : value < 0.0f ? value - ((int) value - 1.0f) : 0.0f;
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

      final float value = a / b;
      return a - b * (value > 0.0f ? (int) value
            : value < 0.0f ? (int) value - 1.0f : 0.0f);
   }

   /**
    * The negation of a float holding a boolean value. Given a
    * == 1.0, !a == 0 . Given a == 0.0, !a == 1 .
    *
    * @param value
    *           the input value
    * @return the negation
    * @see Utils#bool(float)
    */
   public static int not ( final float value ) {

      return Utils.toInt(!Utils.toBool(value));
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

      return Utils.toInt(!Utils.toBool(value));
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
   public static int or ( final float a, final float b ) {

      return Utils.toInt(Utils.toBool(a) | Utils.toBool(b));
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

      return Utils.toInt(Utils.toBool(a) | Utils.toBool(b));
   }

   /**
    * Reduces the signal, or granularity, of a value. Applied
    * to a color, this yields the 'posterization' effect.
    * Applied to a vector, this yields a pixellated, or
    * crenelated effect.
    *
    * Any level less than 2 returns the value unaltered.
    *
    * @param value
    *           the value
    * @param levels
    *           the level
    * @return the quantized value
    * @see Utils#floor(float)
    */
   public static float quantize ( final float value, final int levels ) {

      if (levels < 2) {
         return value;
      }

      /*
       * The method used to describe posterize in the Blender
       * manual, round(m x n - 0.5) / (n - 1), should not be used.
       * It will yield output values exceeding the magnitude of
       * input values, e.g. 1.0 will return 2.0 .
       */

      return Utils.floor(0.5f + value * levels) / levels;
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
    * Rounds a value to an integer based on whether its
    * fractional portion is greater than or equal to plus or
    * minus 0.5 .
    *
    * @param value
    *           the value
    * @return the rounded value
    */
   public static int round ( final float value ) {

      return value < 0.0f ? (int) (value - 0.5f)
            : value > 0.0f ? (int) (value + 0.5f) : 0;
   }

   /**
    * Rounds a value to a number of places right of the decimal
    * point. Promotes the float to a double, rounds it, then
    * downcasts back to a float.
    *
    * Note that floating (or single) precision will likely lead
    * to inaccuracies.
    *
    * @param value
    *           value
    * @param places
    *           the number of places
    * @return the rounded value
    * @see Math#pow(double, double)
    * @see Math#round(double)
    */
   public static float round (
         final float value,
         final int places ) {

      if (places < 1) {
         return Utils.round(value);
      }

      if (places > 7) {
         return value;
      }

      int n = 10;
      for (int i = 1; i < places; ++i) {
         n *= 10;
      }
      return Utils.round(value * n) / (float) n;
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
   }

   /**
    * Finds the approximate sine of an angle in radians.
    * Returns a value in the range [-1.0, 1.0] . An alternative
    * to the double precision {@link Math#sin(double)} , this
    * function uses single-precision numbers.<br>
    * <br>
    * To find the cosine and sine of an angle simultaneously,
    * use either {@link Vec2#fromPolar(float, Vec2)} (the
    * target vector should be created once, in advance).
    *
    * @param radians
    *           the angle in radians
    * @return the sine of the angle
    * @see SinCos#eval(float)
    */
   public static float sin ( final float radians ) {

      return SinCos.eval(IUtils.ONE_TAU * radians - 0.25f);
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
    * Finds the approximate square root of a value. Returns 0.0
    * when the value is less than zero. Use
    * {@link Utils#sqrtUnchecked(float)} when the value is
    * known to be positive. Use
    * {@link Complex#sqrt(float, Complex)} when the input may
    * be negative and a complex output is desired.
    *
    * @param value
    *           the value
    * @return the square root
    * @see Utils#sqrtUnchecked(float)
    */
   public static float sqrt ( final float value ) {

      return value > 0.0f ? Utils.sqrtUnchecked(value) : 0.0f;
   }

   /**
    * Finds the approximate square root of a value. Does so by
    * multiplying the value by its inverse square root.
    *
    * @param value
    *           the value
    * @return the square root
    * @see Utils#invSqrtUnchecked(float)
    */
   public static float sqrtUnchecked ( final float value ) {

      return value * Utils.invSqrtUnchecked(value);
   }

   /**
    * Finds the approximate tangent of an angle in radians. An
    * alternative to the double precision
    * {@link Math#tan(double)} , this function uses
    * single-precision numbers. Equivalent to dividing the sine
    * of the angle by the cosine.
    *
    * @param radians
    *           the angle in radians
    * @return the tangent
    * @see SinCos#eval(float)
    */
   public static float tan ( final float radians ) {

      final float nrm = IUtils.ONE_TAU * radians;
      final float cost = SinCos.eval(nrm);
      return cost == 0.0f ? 0.0f : SinCos.eval(nrm - 0.25f) / cost;
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
       * Hard-coded values from jdk.internal.math.FloatConsts
       * class for fast absolute value and sign.
       */
      final int raw = Float.floatToRawIntBits(value);
      final float sign = Float.intBitsToFloat(raw & -2147483648 | 1065353216);
      final float abs = Float.intBitsToFloat(raw & 2147483647);
      final int trunc = (int) abs;
      final StringBuilder sb = new StringBuilder(16);

      /* Append integral to StringBuilder. */
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
       * The integral has so many digits that it has consumed the
       * allotment. (Might be scientific notation?)
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
    * An alias for {@link Byte#toUnsignedInt(byte)} .
    *
    * Converts a signed byte in the range [-127, 128] to an
    * unsigned byte in the range [0, 255], promoted to an int.
    * Useful when working with colors.
    *
    * Defined for cross-language comparison with C#, which uses
    * signed and unsigned versions of primitive data types.
    *
    * @param a
    *           the signed byte
    * @return the unsigned byte, promoted
    */
   public static int ubyte ( final byte a ) {

      return a & 0xff;
   }

   /**
    *
    * An alias for {@link Integer#toUnsignedLong(int)}.
    *
    * Converts a signed int in the range [-2147483648 ,
    * 2147483647] to an unsigned int in the range [0,
    * 4294967295], promoted to a long. Useful when working with
    * colors.
    *
    * Defined for cross-language comparison with C#, which uses
    * signed and unsigned versions of primitive data types.
    *
    * @param a
    *           the signed int
    * @return the unsigned int, promoted
    */
   public static long uint ( final int a ) {

      return a & 0xffffffffL;
   }

   /**
    * Wraps a value around a periodic range as defined by an
    * upper and lower bound: lower bounds inclusive; upper
    * bounds exclusive. Due to single precision accuracy,
    * results will be inexact.
    *
    * In cases where the lower bound is greater than the upper
    * bound, the two will be swapped. In cases where the range
    * is 0.0, 0.0 will be returned.
    *
    * @param value
    *           the value
    * @param lb
    *           the lower bound
    * @param ub
    *           the upper bound
    * @return the wrapped value
    */
   @Experimental
   public static float wrap (
         final float value,
         final float lb,
         final float ub ) {

      float lbc = 0.0f;
      float ubc = 0.0f;
      final float span = ub - lb;

      if (span < 0.0f) {
         lbc = ub;
         ubc = lb;
      } else if (span > 0.0f) {
         lbc = lb;
         ubc = ub;
      } else {
         return 0.0f;
      }

      if (value < lbc) {
         return ubc - (lbc - value) % span;
      } else if (value >= ubc) {
         return lbc + (value - lbc) % span;
      }
      return value;
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
   public static int xor ( final float a, final float b ) {

      return Utils.toInt(Utils.toBool(a) ^ Utils.toBool(b));
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

      return Utils.toInt(Utils.toBool(a) ^ Utils.toBool(b));
   }
}
