package camzup.core;

import java.util.Comparator;
import java.util.Iterator;

/**
 * A mutable, extensible class influenced by GLSL, OSL and
 * Processing's PVector. This is intended for storing points
 * and directions in three-dimensional graphics programs.
 * Instance methods are limited, while most static methods
 * require an explicit output variable to be provided.
 */
public class Vec3 extends Vec implements Comparable < Vec3 > {

   /**
    * An abstract class that may serve as an umbrella for any
    * custom comparators of Vec3 s.
    */
   public static abstract class AbstrComparator implements Comparator < Vec3 > {

      /**
       * The default constructor.
       */
      public AbstrComparator () {

      }

      /**
       * The compare function which must be implemented by sub-
       * (child) classes of this class. Negative one should be
       * returned when the left comparisand, a, is less than the
       * right comparisand, b, by a measure. One should be
       * returned when it is greater. Zero should be returned as a
       * last resort, when a and b are equal or uncomparable.
       *
       * @param a
       *           the left comparisand
       * @param b
       *           the right comparisand
       * @return the comparison
       *
       */
      @Override
      public abstract int compare ( final Vec3 a, final Vec3 b );

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString () {

         return this.getClass().getSimpleName();
      }
   }

   /**
    * An abstract class to facilitate the creation of vector
    * easing functions.
    */
   public static abstract class AbstrEasing
         implements Utils.EasingFuncObject < Vec3 > {

      /**
       * The default constructor.
       */
      public AbstrEasing () {

      }

      /**
       * A clamped interpolation between the origin and
       * destination. Defers to an unclamped interpolation, which
       * is to be defined by sub-classes of this class.
       *
       * @param origin
       *           the origin vector
       * @param dest
       *           the destination vector
       * @param step
       *           a factor in [0.0, 1.0]
       * @param target
       *           the output vector
       * @return the eased vector
       */
      @Override
      public Vec3 apply (
            final Vec3 origin,
            final Vec3 dest,
            final Float step,
            final Vec3 target ) {

         if (step <= 0.0f) {
            return target.set(origin);
         }

         if (step >= 1.0f) {
            return target.set(dest);
         }

         return this.applyUnclamped(origin, dest, step, target);
      }

      /**
       * The interpolation to be defined by subclasses.
       *
       * @param origin
       *           the origin vector
       * @param dest
       *           the destination vector
       * @param step
       *           a factor in [0.0, 1.0]
       * @param target
       *           the output vector
       * @return the eased vector
       */
      public abstract Vec3 applyUnclamped (
            final Vec3 origin,
            final Vec3 dest,
            final float step,
            Vec3 target );

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString () {

         return this.getClass().getSimpleName();
      }
   }

   /**
    * Compares two vectors by their distance from a locus. The
    * locus should be set in the creation of the comparator.
    */
   public static class ComparatorDist extends AbstrComparator {

      /**
       * The difference between the locus and left comparisand.
       */
      public final Vec3 aDiff = new Vec3();

      /**
       * The difference between the locus and right comparisand.
       */
      public final Vec3 bDiff = new Vec3();

      /**
       * The locus against which points are compared.
       */
      public final Vec3 locus = new Vec3();

      /**
       * The default constructor.
       */
      public ComparatorDist () {

         super();
      }

      /**
       * A constructor which sets a locus against which two points
       * are compared.
       *
       * @param locus
       *           the locus
       */
      public ComparatorDist ( final Vec3 locus ) {

         this.locus.set(locus);
      }

      /**
       * Compares two vectors by subtracting the locus from each,
       * then comparing the dot products of the respective
       * differences.
       *
       * @param a
       *           the left comparisand
       * @param b
       *           the right comparisand
       * @return the comparison
       * @see Vec3#sub(Vec3, Vec3, Vec3)
       * @see Vec3#dot(Vec3, Vec3)
       */
      @Override
      public int compare ( final Vec3 a, final Vec3 b ) {

         Vec3.sub(a, this.locus, this.aDiff);
         Vec3.sub(b, this.locus, this.bDiff);

         // return Float.compare(
         // Vec3.magSq(this.aDiff),
         // Vec3.magSq(this.bDiff));

         final float aDist = Vec3.magSq(this.aDiff);
         final float bDist = Vec3.magSq(this.bDiff);

         return aDist > bDist ? 1 : aDist < bDist ? -1 : 0;
      }
   }

   /**
    * Compares two vectors by their z component, then by their
    * y component, then by their x component.
    */
   public static class ComparatorZYX extends AbstrComparator {

      /**
       * The default constructor.
       */
      public ComparatorZYX () {

         super();
      }

      /**
       * Compares the z, y and x component.
       *
       * @param a
       *           the left operand
       * @param b
       *           the right operand
       * @return the comparison
       */
      @Override
      public int compare ( final Vec3 a, final Vec3 b ) {

         return a.z > b.z ? 1
               : a.z < b.z ? -1
                     : a.y > b.y ? 1
                           : a.y < b.y ? -1
                                 : a.x > b.x ? 1 : a.x < b.x ? -1 : 0;
      }
   }

   /**
    * A linear interpolation functional class.
    */
   public static class Lerp extends AbstrEasing {

      /**
       * The default constructor.
       */
      public Lerp () {

         super();
      }

      /**
       * Lerps between two vectors by a step using the formula (1
       * - t) * a + b . Promotes the step from a float to a
       * double.
       *
       * @param origin
       *           the origin vector
       * @param dest
       *           the destination vector
       * @param step
       *           the step
       * @param target
       *           the output vector
       * @return the result
       */
      @Override
      public Vec3 applyUnclamped (
            final Vec3 origin,
            final Vec3 dest,
            final float step,
            final Vec3 target ) {

         final double td = step;
         final double ud = 1.0d - td;
         return target.set(
               (float) (ud * origin.x + td * dest.x),
               (float) (ud * origin.y + td * dest.y),
               (float) (ud * origin.z + td * dest.z));
      }
   }

   /**
    * Eases between two vectors with the smooth step formula:
    * <em>t</em><sup>2</sup> ( 3.0 - 2.0 <em>t</em> ) .
    */
   public static class SmoothStep extends AbstrEasing {

      /**
       * The default constructor.
       */
      public SmoothStep () {

         super();
      }

      /**
       * Applies the function.
       *
       * @param origin
       *           the origin vector
       * @param dest
       *           the destination vector
       * @param step
       *           the step in a range 0 to 1
       * @param target
       *           the output vector
       * @return the smoothed vector
       */
      @Override
      public Vec3 applyUnclamped (
            final Vec3 origin,
            final Vec3 dest,
            final float step,
            final Vec3 target ) {

         final double td = step;
         final double ts = td * td * (3.0d - (td + td));
         final double us = 1.0d - ts;
         return target.set(
               (float) (us * origin.x + ts * dest.x),
               (float) (us * origin.y + ts * dest.y),
               (float) (us * origin.z + ts * dest.z));
      }
   }

   /**
    * An iterator, which allows a vector's components to be
    * accessed in an enhanced for loop.
    */
   public static final class V3Iterator implements Iterator < Float > {

      /**
       * The current index.
       */
      private int index = 0;

      /**
       * The vector being iterated over.
       */
      private final Vec3 vec;

      /**
       * The default constructor.
       *
       * @param vec
       *           the vector to iterate
       */
      public V3Iterator ( final Vec3 vec ) {

         this.vec = vec;
      }

      /**
       * Tests to see if the iterator has another value.
       *
       * @return the evaluation
       */
      @Override
      public boolean hasNext () {

         return this.index < this.vec.size();
      }

      /**
       * Gets the next value in the iterator.
       *
       * @return the value
       * @see Vec3#get(int)
       */
      @Override
      public Float next () {

         return this.vec.get(this.index++);
      }

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString () {

         return this.getClass().getSimpleName();
      }
   }

   /**
    * The default Vec3 comparator, ComparatorZYX .
    */
   private static Comparator < Vec3 > COMPARATOR = new ComparatorZYX();

   /**
    * The default easing function, lerp.
    */
   private static AbstrEasing EASING = new Lerp();

   /**
    * The unique identification for serialized classes.
    */
   private static final long serialVersionUID = -7814214074840696365L;

   /**
    * Finds the absolute value of each vector component.
    *
    * @param v
    *           the input vector
    * @param target
    *           the output vector
    * @return the absolute vector
    * @see Utils#abs(float)
    */
   public static Vec3 abs (
         final Vec3 v,
         final Vec3 target ) {

      return target.set(
            Utils.abs(v.x),
            Utils.abs(v.y),
            Utils.abs(v.z));
   }

   /**
    * Adds two vectors together.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output vector
    * @return the sum
    */
   public static Vec3 add (
         final Vec3 a,
         final Vec3 b,
         final Vec3 target ) {

      return target.set(
            a.x + b.x,
            a.y + b.y,
            a.z + b.z);
   }

   /**
    * Adds and then normalizes two vectors.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output vector
    * @return the normalized sum
    */
   public static Vec3 addNorm (
         final Vec3 a,
         final Vec3 b,
         final Vec3 target ) {

      final float dx = a.x + b.x;
      final float dy = a.y + b.y;
      final float dz = a.z + b.z;

      final float mSq = dx * dx + dy * dy + dz * dz;
      if (mSq == 0.0f) {
         return target.reset();
      }

      final float mInv = (float) (1.0d / Math.sqrt(mSq));
      return target.set(
            dx * mInv,
            dy * mInv,
            dz * mInv);
   }

   /**
    * Adds and then normalizes two vectors. Discloses the
    * intermediate, unnormalized sum as an output.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output vector
    * @param sum
    *           the sum
    *
    * @return the normalized sum
    * @see Vec3#add(Vec3, Vec3, Vec3)
    * @see Vec3#normalize(Vec3, Vec3)
    */
   public static Vec3 addNorm (
         final Vec3 a,
         final Vec3 b,
         final Vec3 target,
         final Vec3 sum ) {

      Vec3.add(a, b, sum);
      Vec3.normalize(sum, target);
      return target;
   }

   /**
    * Finds the angle between two vectors.
    *
    * @param a
    *           the first vector
    * @param b
    *           the second vector
    * @return the angle
    * @see Vec3#isZero(Vec3)
    * @see Vec3#dot(Vec3, Vec3)
    * @see Vec3#mag(Vec3)
    * @see Utils#acos(float)
    */
   public static float angleBetween (
         final Vec3 a,
         final Vec3 b ) {

      if (Vec3.isZero(a) || Vec3.isZero(b)) {
         return 0.0f;
      }

      return Utils.acos(Vec3.dot(a, b) / (Vec3.mag(a) * Vec3.mag(b)));
   }

   /**
    * Tests to see if two vectors approximate each other.
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @return the evaluation
    * @see Utils#approxFast(float, float)
    */
   public static boolean approx (
         final Vec3 a,
         final Vec3 b ) {

      return Utils.approxFast(a.z, b.z) &&
            Utils.approxFast(a.y, b.y) &&
            Utils.approxFast(a.x, b.x);
   }

   /**
    * Tests to see if two vectors approximate each other.
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @param tolerance
    *           the tolerance
    * @return the evaluation
    * @see Utils#approxFast(float, float, float)
    */
   public static boolean approx (
         final Vec3 a,
         final Vec3 b,
         final float tolerance ) {

      return Utils.approxFast(a.z, b.z, tolerance)
            && Utils.approxFast(a.y, b.y, tolerance)
            && Utils.approxFast(a.x, b.x, tolerance);
   }

   /**
    * Tests to see if a vector has, approximately, the
    * specified magnitude according to the default EPSILON.
    *
    * @param a
    *           the input vector
    * @param b
    *           the magnitude
    * @return the evaluation
    * @see Utils#approxFast(float, float)
    * @see Vec3#dot(Vec3, Vec3)
    */
   public static boolean approxMag (
         final Vec3 a,
         final float b ) {

      return Utils.approxFast(Vec3.magSq(a), b * b);
   }

   /**
    * Tests to see if a vector has, approximately, the
    * specified magnitude.
    *
    * @param a
    *           the input vector
    * @param b
    *           the magnitude
    * @param tolerance
    *           the tolerance
    * @return the evaluation
    * @see Utils#approxFast(float, float, float)
    * @see Vec3#dot(Vec3, Vec3)
    */
   public static boolean approxMag (
         final Vec3 a,
         final float b,
         final float tolerance ) {

      return Utils.approxFast(Vec3.magSq(a), b * b, tolerance);
   }

   /**
    * Tests to see if two vectors are parallel. Does so by
    * evaluating whether the cross product of the two equals
    * zero.
    *
    * @param a
    *           the left comparisand
    * @param b
    *           the right comparisand
    * @return the evaluation
    */
   public static boolean areParallel (
         final Vec3 a,
         final Vec3 b ) {

      return a.y * b.z - a.z * b.y == 0.0f &&
            a.z * b.x - a.x * b.z == 0.0f &&
            a.x * b.y - a.y * b.x == 0.0f;
   }

   /**
    * Tests to see if two vectors are parallel. Does so by
    * evaluating whether the cross product of the two equals
    * zero.
    *
    * @param a
    *           the left comparisand
    * @param b
    *           the right comparisand
    * @param cross
    *           the cross product
    * @return the evaluation
    * @see Vec3#cross(Vec3, Vec3, Vec3)
    * @see Vec3#isZero(Vec3)
    */
   public static boolean areParallel (
         final Vec3 a,
         final Vec3 b,
         final Vec3 cross ) {

      return Vec3.isZero(Vec3.cross(a, b, cross));
   }

   /**
    * Finds the vector's azimuth. Defaults to azimuthSigned.
    *
    * @param a
    *           the input vector
    * @return the angle in radians
    * @see Vec3#azimuthSigned(Vec3)
    * @see Vec3#azimuthUnsigned(Vec3)
    */
   public static float azimuth ( final Vec3 a ) {

      return Vec3.azimuthSigned(a);
   }

   /**
    * Finds the vector's azimuth in the range [-\u03c0,
    * \u03c0].
    *
    * @param v
    *           the input vector
    * @return the angle in radians
    * @see Math#atan2(double, double)
    * @see Vec3#azimuthUnsigned(Vec3)
    */
   public static float azimuthSigned ( final Vec3 v ) {

      return Utils.atan2(v.y, v.x);
   }

   /**
    * Finds the vector's azimuth in the range [0.0, \u03c4].
    *
    * @param v
    *           the input vector
    * @return the angle in radians
    * @see Vec3#azimuthSigned(Vec3)
    * @see Utils#modRadians(float)
    */
   public static float azimuthUnsigned ( final Vec3 v ) {

      return Utils.modRadians(Vec3.azimuthUnsigned(v));
   }

   /**
    * Returns to a vector with a negative value on the y axis,
    * (0.0, -1.0, 0.0) .
    *
    * @param target
    *           the output vector
    * @return the back vector
    */
   public static Vec3 back ( final Vec3 target ) {

      return target.set(0.0f, -1.0f, 0.0f);
   }

   /**
    * Returns a point on a Bezier curve described by two anchor
    * points and two control points according to a step in
    * [0.0, 1.0].
    *
    * When the step is less than one, returns the first anchor
    * point. When the step is greater than one, returns the
    * second anchor point.
    *
    * @param ap0
    *           the first anchor point
    * @param cp0
    *           the first control point
    * @param cp1
    *           the second control point
    * @param ap1
    *           the second anchor point
    * @param step
    *           the step
    * @param target
    *           the output vector
    * @return the point along the curve
    */
   public static Vec3 bezierPoint (
         final Vec3 ap0,
         final Vec3 cp0,
         final Vec3 cp1,
         final Vec3 ap1,
         final float step,
         final Vec3 target ) {

      if (step <= 0.0f) {
         return target.set(ap0);
      } else if (step >= 1.0f) {
         return target.set(ap1);
      }

      final float u = 1.0f - step;
      float tcb = step * step;
      float ucb = u * u;
      final float usq3t = ucb * (step + step + step);
      final float tsq3u = tcb * (u + u + u);
      ucb *= u;
      tcb *= step;

      return target.set(
            ap0.x * ucb + cp0.x * usq3t + cp1.x * tsq3u + ap1.x * tcb,
            ap0.y * ucb + cp0.y * usq3t + cp1.y * tsq3u + ap1.y * tcb,
            ap0.z * ucb + cp0.z * usq3t + cp1.z * tsq3u + ap1.z * tcb);
   }

   /**
    * Returns a tangent on a Bezier curve described by two
    * anchor points and two control points according to a step
    * in [0.0, 1.0].
    *
    * When the step is less than one, returns the first anchor
    * point subtracted from the first control point. When the
    * step is greater than one, returns the second anchor point
    * subtracted from the second control point.
    *
    * @param ap0
    *           the first anchor point
    * @param cp0
    *           the first control point
    * @param cp1
    *           the second control point
    * @param ap1
    *           the second anchor point
    * @param step
    *           the step
    * @param target
    *           the output vector
    * @return the tangent along the curve
    * @see Vec3#sub(Vec3, Vec3, Vec3)
    */
   public static Vec3 bezierTangent (
         final Vec3 ap0,
         final Vec3 cp0,
         final Vec3 cp1,
         final Vec3 ap1,
         final float step,
         final Vec3 target ) {

      if (step <= 0.0f) {
         return Vec3.sub(cp0, ap0, target);
      } else if (step >= 1.0f) {
         return Vec3.sub(ap1, cp1, target);
      }

      final float u = 1.0f - step;
      final float t3 = step + step + step;
      final float usq3 = u * (u + u + u);
      final float tsq3 = step * t3;
      final float ut6 = u * (t3 + t3);

      return target.set(
            (cp0.x - ap0.x) * usq3 + (cp1.x - cp0.x) * ut6
                  + (ap1.x - cp1.x) * tsq3,

            (cp0.y - ap0.y) * usq3 + (cp1.y - cp0.y) * ut6
                  + (ap1.y - cp1.y) * tsq3,

            (cp0.z - ap0.z) * usq3 + (cp1.z - cp0.z) * ut6
                  + (ap1.z - cp1.z) * tsq3);
   }

   /**
    * Returns a normalized tangent on a Bezier curve.
    *
    * @param ap0
    *           the first anchor point
    * @param cp0
    *           the first control point
    * @param cp1
    *           the second control point
    * @param ap1
    *           the second anchor point
    * @param step
    *           the step
    * @param target
    *           the output vector
    * @return the tangent along the curve
    * @see Vec3#bezierTangent(Vec3, Vec3, Vec3, Vec3, float,
    *      Vec3)
    */
   public static Vec3 bezierTanUnit (
         final Vec3 ap0,
         final Vec3 cp0,
         final Vec3 cp1,
         final Vec3 ap1,
         final float step,
         final Vec3 target ) {

      Vec3.bezierTangent(ap0, cp0, cp1, ap1, step, target);

      final float mSq = Vec3.magSq(target);
      if (mSq == 0.0f) {
         return target.reset();
      }

      final float mInv = 1.0f / (float) Math.sqrt(mSq);
      return target.set(
            target.x * mInv,
            target.y * mInv,
            target.z * mInv);
   }

   /**
    * Raises each component of the vector to the nearest
    * greater integer.
    *
    * @param a
    *           the input vector
    * @param target
    *           the target vector
    * @return the output
    * @see Utils#ceil(float)
    */
   public static Vec3 ceil (
         final Vec3 a,
         final Vec3 target ) {

      return target.set(
            Utils.ceil(a.x),
            Utils.ceil(a.y),
            Utils.ceil(a.z));
   }

   /**
    * Clamps a vector to a range within the lower- and
    * upper-bound.
    *
    * @param a
    *           the input vector
    * @param lowerBound
    *           the lower bound of the range
    * @param upperBound
    *           the upper bound of the range
    * @param target
    *           the output vector
    * @return the clamped vector
    * @see Utils#clamp(float, float, float)
    */
   public static Vec3 clamp (
         final Vec3 a,
         final Vec3 lowerBound,
         final Vec3 upperBound,
         final Vec3 target ) {

      return target.set(
            Utils.clamp(a.x, lowerBound.x, upperBound.x),
            Utils.clamp(a.y, lowerBound.y, upperBound.y),
            Utils.clamp(a.z, lowerBound.z, upperBound.z));
   }

   /**
    * Clamps the vector to a range in [0, 1].
    *
    * @param a
    *           the input vector
    * @param target
    *           the output vector
    * @return the clamped vector
    * @see Utils#clamp01(float)
    */
   public static Vec3 clamp01 ( final Vec3 a, final Vec3 target ) {

      return target.set(
            Utils.clamp01(a.x),
            Utils.clamp01(a.y),
            Utils.clamp01(a.z));
   }

   /**
    * Finds first vector argument with the sign of the second
    * vector argument.
    *
    * @param magnitude
    *           the magnitude
    * @param sign
    *           the sign
    * @param target
    *           the output vector
    * @return the signed vector
    */
   public static Vec3 copySign (
         final Vec3 magnitude,
         final Vec3 sign,
         final Vec3 target ) {

      return target.set(
            Math.copySign(magnitude.x, sign.x),
            Math.copySign(magnitude.y, sign.y),
            Math.copySign(magnitude.z, sign.z));
   }

   /**
    * The cross product returns a vector perpendicular to both
    * <em>a</em> and <em>b</em>, and therefore normal to the
    * plane on which <em>a</em> and <em>b</em> rest.<br>
    * <br>
    *
    * <em>a</em> \u00d7 <em>b</em> :=<br>
    * ( <em>a<sub>y</sub> b<sub>z</sub></em> -
    * <em>a<sub>z</sub> b<sub>y</sub></em> ,<br>
    * <em>a<sub>z</sub> b<sub>x</sub></em> - <em>a<sub>x</sub>
    * b<sub>z</sub></em> ,<br>
    * <em>a<sub>x</sub> b<sub>y</sub></em> - <em>a<sub>y</sub>
    * b<sub>x</sub></em> )<br>
    * <br>
    *
    * The cross product is anti-commutative, meaning <em>a</em>
    * \u00d7 <em>b</em> = - ( <em>b</em> \u00d7 <em>a</em> ) .
    * A unit vector does not necessarily result from the cross
    * of two unit vectors. Crossed orthonormal vectors are as
    * follows:
    * <ul>
    * <li>right \u00d7 forward = up, <br>
    * ( 1.0, 0.0, 0.0 ) \u00d7 ( 0.0, 1.0, 0.0 ) = ( 0.0, 0.0,
    * 1.0 )</li>
    * <li>forward \u00d7 up = right, <br>
    * ( 0.0, 1.0, 0.0 ) \u00d7 ( 0.0, 0.0, 1.0 ) = ( 1.0, 0.0,
    * 0.0 )</li>
    * <li>up \u00d7 right = forward, <br>
    * ( 0.0, 0.0, 1.0 ) \u00d7 ( 1.0, 0.0, 0.0 ) = ( 0.0, 1.0,
    * 0.0 )</li>
    * </ul>
    * The 3D equivalent to the 2D vector's perpendicular.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output vector
    * @return the cross product
    * @see Vec2#perpendicular(Vec2, Vec2)
    */
   public static Vec3 cross (
         final Vec3 a,
         final Vec3 b,
         final Vec3 target ) {

      return target.set(
            a.y * b.z - a.z * b.y,
            a.z * b.x - a.x * b.z,
            a.x * b.y - a.y * b.x);
   }

   /**
    * A specialized form of the cross product which normalizes
    * the result. This is to facilitate the creation of lookAt
    * matrices.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output vector
    * @return the normalized cross product
    * @see Vec3#cross(Vec3, Vec3, Vec3)
    */
   public static Vec3 crossNorm (
         final Vec3 a,
         final Vec3 b,
         final Vec3 target ) {

      final float x = a.y * b.z - a.z * b.y;
      final float y = a.z * b.x - a.x * b.z;
      final float z = a.x * b.y - a.y * b.x;

      final float mSq = x * x + y * y + z * z;
      if (mSq == 0.0f) {
         return target.reset();
      }

      final float mInv = 1.0f / (float) Math.sqrt(mSq);
      return target.set(
            x * mInv,
            y * mInv,
            z * mInv);
   }

   /**
    * A specialized form of the cross product which normalizes
    * the result. The unnormalized cross product is disclosed
    * as an output.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output vector
    * @param crossed
    *           the cross product
    * @return the normalized cross product
    * @see Vec3#cross(Vec3, Vec3, Vec3)
    * @see Vec3#normalize(Vec3, Vec3)
    */
   public static Vec3 crossNorm (
         final Vec3 a,
         final Vec3 b,
         final Vec3 target,
         final Vec3 crossed ) {

      Vec3.cross(a, b, crossed);
      Vec3.normalize(crossed, target);
      return target;
   }

   /**
    * Finds the absolute value of the difference between two
    * vectors.
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @param target
    *           the output vector
    * @return the absolute difference
    * @see Utils#diff(float, float)
    */
   public static Vec3 diff (
         final Vec3 a,
         final Vec3 b,
         final Vec3 target ) {

      return target.set(
            Utils.diff(a.x, b.x),
            Utils.diff(a.y, b.y),
            Utils.diff(a.z, b.z));
   }

   /**
    * Finds the Euclidean distance between two vectors.
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @return the distance
    * @see Vec3#distEuclidean(Vec3, Vec3)
    */
   public static float dist ( final Vec3 a, final Vec3 b ) {

      return Vec3.distEuclidean(a, b);
   }

   /**
    * Finds the Chebyshev distance between two vectors. Forms a
    * square pattern when plotted.
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @return the distance
    * @see Utils#max(float, float)
    * @see Utils#diff(float, float)
    */
   public static float distChebyshev (
         final Vec3 a,
         final Vec3 b ) {

      return Utils.max(
            Utils.diff(a.x, b.x),
            Utils.diff(a.y, b.y),
            Utils.diff(a.z, b.z));
   }

   /**
    * Finds the Euclidean distance between two vectors. Where
    * possible, use distance squared to avoid the computational
    * cost of the square-root.
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @return the Euclidean distance
    * @see Vec3#distSq(Vec3, Vec3)
    */
   public static float distEuclidean (
         final Vec3 a,
         final Vec3 b ) {

      return (float) Math.sqrt(Vec3.distSq(a, b));
   }

   /**
    * Finds the Manhattan distance between two vectors. Forms a
    * diamond pattern when plotted.
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @return the Manhattan distance
    * @see Utils#diff(float, float)
    */
   public static float distManhattan (
         final Vec3 a,
         final Vec3 b ) {

      return Utils.diff(a.x, b.x) +
            Utils.diff(a.y, b.y) +
            Utils.diff(a.z, b.z);
   }

   /**
    * Finds the Minkowski distance between two vectors. This is
    * a generalization of other distance formulae. When the
    * exponent value, c, is 1.0, the Minkowski distance equals
    * the Manhattan distance; when it is 2.0, Minkowski equals
    * the Euclidean distance.
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @param c
    *           exponent
    * @return the Minkowski distance
    * @see Vec3#distEuclidean(Vec3, Vec3)
    * @see Vec3#distManhattan(Vec3, Vec3)
    */
   public static float distMinkowski (
         final Vec3 a,
         final Vec3 b,
         final float c ) {

      if (c == 0.0f) {
         return 0.0f;
      }

      return (float) Math.pow(
            Math.pow(Utils.diff(a.x, b.x), c)
                  + Math.pow(Utils.diff(a.y, b.y), c)
                  + Math.pow(Utils.diff(a.z, b.z), c),
            1.0f / c);
   }

   /**
    * Finds the Euclidean distance squared between two vectors.
    * Equivalent to subtracting one vector from the other, then
    * finding the dot product of the difference with itself.
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @return the distance squared
    * @see Vec3#distEuclidean(Vec3, Vec3)
    * @see Vec3#sub(Vec3, Vec3, Vec3)
    */
   public static float distSq ( final Vec3 a, final Vec3 b ) {

      final float xDist = b.x - a.x;
      final float yDist = b.y - a.y;
      final float zDist = b.z - a.z;
      return xDist * xDist + yDist * yDist + zDist * zDist;
   }

   /**
    * Divides a scalar by a vector.
    *
    * @param a
    *           scalar, numerator
    * @param b
    *           vector, denominator
    * @param target
    *           the output vector
    * @return the quotient
    * @see Utils#div(float, float)
    */
   public static Vec3 div (
         final float a,
         final Vec3 b,
         final Vec3 target ) {

      return target.set(
            Utils.div(a, b.x),
            Utils.div(a, b.y),
            Utils.div(a, b.z));
   }

   /**
    * Divides a vector by a scalar.
    *
    * @param a
    *           vector, numerator
    * @param b
    *           scalar, denominator
    * @param target
    *           the output vector
    * @return the quotient
    */
   public static Vec3 div (
         final Vec3 a,
         final float b,
         final Vec3 target ) {

      if (b == 0.0f) {
         return target.reset();
      }
      final float denom = 1.0f / b;
      return target.set(
            a.x * denom,
            a.y * denom,
            a.z * denom);
   }

   /**
    * Divides the left operand by the right, component-wise.
    * This is mathematically incorrect, but serves as a
    * shortcut for transforming a vector by the inverse of a
    * scalar matrix.
    *
    * @param a
    *           numerator
    * @param b
    *           denominator
    * @param target
    *           the output vector
    * @return the quotient
    * @see Utils#div(float, float)
    */
   public static Vec3 div (
         final Vec3 a,
         final Vec3 b,
         final Vec3 target ) {

      return target.set(
            Utils.div(a.x, b.x),
            Utils.div(a.y, b.y),
            Utils.div(a.z, b.z));
   }

   /**
    * Finds the dot product of two vectors by summing the
    * products of their corresponding components.<br>
    * <br>
    * <em>a</em> \u00b7 <em>b</em> := <em>a<sub>x</sub>
    * b<sub>x</sub></em> + <em>a<sub>y</sub> b<sub>y</sub></em>
    * + <em>a<sub>z</sub> b<sub>z</sub></em><br>
    * <br>
    * The dot product of a vector with itself is equal to its
    * magnitude squared.
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @return the dot product
    */
   public static float dot ( final Vec3 a, final Vec3 b ) {

      return a.x * b.x + a.y * b.y + a.z * b.z;
   }

   /**
    * Returns to a vector with a negative value on the z axis,
    * (0.0, 0.0, -1.0) .
    *
    * @param target
    *           the output vector
    * @return the down vector
    */
   public static Vec3 down ( final Vec3 target ) {

      return target.set(0.0f, 0.0f, -1.0f);
   }

   /**
    * Floors each component of the vector.
    *
    * @param v
    *           the input vector
    * @param target
    *           the target vector
    * @return the output
    * @see Utils#floor(float)
    */
   public static Vec3 floor ( final Vec3 v, final Vec3 target ) {

      return target.set(
            Utils.floor(v.x),
            Utils.floor(v.y),
            Utils.floor(v.z));
   }

   /**
    * Applies the % operator (truncation-based modulo) to the
    * left operand.
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @param target
    *           the output vector
    * @return the result
    * @see Utils#fmod(float, float)
    */
   public static Vec3 fmod (
         final float a,
         final Vec3 b,
         final Vec3 target ) {

      return target.set(
            Utils.fmod(a, b.x),
            Utils.fmod(a, b.y),
            Utils.fmod(a, b.z));
   }

   /**
    * Applies the % operator (truncation-based modulo) to each
    * component of the left operand.
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @param target
    *           the output vector
    * @return the result
    */
   public static Vec3 fmod (
         final Vec3 a,
         final float b,
         final Vec3 target ) {

      if (b == 0.0f) {
         return target.set(a);
      }
      return target.set(a.x % b, a.y % b, a.z % b);
   }

   /**
    * Applies the % operator (truncation-based modulo) to each
    * component of the left operand.
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @param target
    *           the output vector
    * @return the result
    * @see Utils#fmod(float, float)
    */
   public static Vec3 fmod (
         final Vec3 a,
         final Vec3 b,
         final Vec3 target ) {

      return target.set(
            Utils.fmod(a.x, b.x),
            Utils.fmod(a.y, b.y),
            Utils.fmod(a.z, b.z));
   }

   /**
    * Returns to a vector with a positive value on the y axis,
    * (0.0, 1.0, 0.0) .
    *
    * @param target
    *           the output vector
    * @return the forward vector
    */
   public static Vec3 forward ( final Vec3 target ) {

      return target.set(0.0f, 1.0f, 0.0f);
   }

   /**
    * Returns the fractional portion of the vector's
    * components.
    *
    * @param v
    *           the input vector
    * @param target
    *           the output vector
    * @return the fractional portion
    * @see Utils#fract(float)
    */
   public static Vec3 fract ( final Vec3 v, final Vec3 target ) {

      return target.set(
            Utils.fract(v.x),
            Utils.fract(v.y),
            Utils.fract(v.z));
   }

   /**
    * Creates a vector from polar coordinates: (1) theta,
    * \u03b8, an angle in radians, the vector's azimuth; (2)
    * rho, \u03c1, a radius, the vector's magnitude. Uses the
    * formula<br>
    * <br>
    * ( \u03c1 cos ( \u03b8 ),<br>
    * \u03c1 sin ( \u03b8 ) )
    *
    * @param azimuth
    *           the angle in radians
    * @param radius
    *           the radius
    * @param target
    *           the output vector
    * @return the vector
    * @see Math#cos(double)
    * @see Math#sin(double)
    */
   public static Vec3 fromPolar (
         final float azimuth,
         final float radius,
         final Vec3 target ) {

      return target.set(
            (float) (radius * Math.cos(azimuth)),
            (float) (radius * Math.sin(azimuth)),
            0.0f);
   }

   /**
    * Creates a vector with a magnitude of 1.0 from an angle,
    * such that the vector is on the equator of the unit
    * sphere.
    *
    * @param azimuth
    *           the angle in radians
    * @param target
    *           the output vector
    * @return the vector
    */
   public static Vec3 fromPolar (
         final float azimuth,
         final Vec3 target ) {

      return target.set(
            (float) Math.cos(azimuth),
            (float) Math.sin(azimuth),
            0.0f);
   }

   /**
    * Creates a vector from spherical coordinates: (1) theta,
    * \u03b8, the azimuth or longitude; (2) phi, \u03c6, the
    * inclination or latitude; (3) rho, \u03c1, the radius or
    * magnitude. Uses the formula<br>
    * <br>
    * ( \u03c1 cos ( \u03b8 ) cos ( \u03c6 ),<br>
    * \u03c1 sin ( \u03b8 ) cos ( \u03c6 ),<br>
    * - \u03c1 sin ( \u03c6 ) )<br>
    * <br>
    * The poles will be upright in a z-up coordinate system;
    * sideways in a y-up coordinate system.
    *
    * @param azimuth
    *           the angle theta in radians
    * @param inclination
    *           the angle phi in radians
    * @param radius
    *           rho, the vector's magnitude
    * @param target
    *           the output vector
    * @return the vector
    */
   public static Vec3 fromSpherical (
         final float azimuth,
         final float inclination,
         final float radius,
         final Vec3 target ) {

      final float rhoCosPhi = radius * (float) Math.cos(inclination);
      return target.set(
            rhoCosPhi * (float) Math.cos(azimuth),
            rhoCosPhi * (float) Math.sin(azimuth),
            radius * -(float) Math.sin(inclination));
   }

   /**
    * Creates a vector with a magnitude of 1.0 from an azimuth
    * and inclination, such that the vector is on the unit
    * sphere.
    *
    * @param azimuth
    *           the azimuth in radians
    * @param inclination
    *           the inclination in radians
    * @param target
    *           the output vector
    * @return the vector
    */
   public static Vec3 fromSpherical (
         final float azimuth,
         final float inclination,
         final Vec3 target ) {

      return Vec3.fromSpherical(azimuth, inclination, 1.0f, target);
   }

   /**
    * Gets the string representation of the default Vec2
    * comparator.
    *
    * @return the string
    */
   public static String getComparatorString () {

      return Vec3.COMPARATOR.toString();
   }

   /**
    * Gets the string representation of the default Vec2 easing
    * function.
    *
    * @return the string
    */
   public static String getEasingString () {

      return Vec3.EASING.toString();
   }

   /**
    * Generates a 3D array of vectors.
    *
    * @param rows
    *           number of rows
    * @param cols
    *           number of columns
    * @param layers
    *           number of layers
    * @param lowerBound
    *           the lower bound
    * @param upperBound
    *           the upper bound
    * @return the array
    */
   public static Vec3[][][] grid (
         final int rows,
         final int cols,
         final int layers,
         final Vec3 lowerBound,
         final Vec3 upperBound ) {

      final int rval = rows < 3 ? 3 : rows;
      final int cval = cols < 3 ? 3 : cols;
      final int lval = layers < 3 ? 3 : layers;

      final float hToStep = 1.0f / (lval - 1.0f);
      final float iToStep = 1.0f / (rval - 1.0f);
      final float jToStep = 1.0f / (cval - 1.0f);

      /* Calculate x values in separate loop. */
      final float[] xs = new float[cval];
      for (int j = 0; j < cval; ++j) {
         xs[j] = Utils.lerpUnclamped(lowerBound.x, upperBound.x, j * jToStep);
      }

      /* Calculate y values in separate loop. */
      final float[] ys = new float[rval];
      for (int i = 0; i < rval; ++i) {
         ys[i] = Utils.lerpUnclamped(lowerBound.y, upperBound.y, i * iToStep);
      }

      final Vec3[][][] result = new Vec3[lval][rval][cval];
      for (int h = 0; h < lval; ++h) {

         final Vec3[][] layer = result[h];
         final float z = Utils.lerpUnclamped(
               lowerBound.z,
               upperBound.z,
               h * hToStep);

         for (int i = 0; i < rval; ++i) {

            final Vec3[] row = layer[i];
            final float y = ys[i];

            for (int j = 0; j < cval; ++j) {
               row[j] = new Vec3(xs[j], y, z);
            }
         }
      }
      return result;
   }

   /**
    * Finds the vector's inclination. Defaults to inclination
    * signed.
    *
    * @param v
    *           the input vector
    * @return the inclination
    * @see Vec3#inclinationSigned(Vec3)
    * @see Vec3#inclinationUnsigned(Vec3)
    */
   public static float inclination ( final Vec3 v ) {

      return Vec3.inclinationSigned(v);
   }

   /**
    * Finds the vector's inclination in the range [-\u03c0 /
    * 2.0, \u03c0 / 2.0]. It is necessary to calculate the
    * vector's magnitude in order to find its inclination.
    *
    * @param v
    *           the input vector
    * @return the inclination
    * @see Vec3#inclinationUnsigned(Vec3)
    * @see Utils#asin(float)
    * @see Vec3#dot(Vec3, Vec3)
    * @see Math#sqrt(double)
    */
   public static float inclinationSigned ( final Vec3 v ) {

      final float mSq = Vec3.magSq(v);
      if (mSq == 0.0f) {
         return 0.0f;
      }
      return Utils.asin((float) (v.z / Math.sqrt(mSq)));
   }

   /**
    * Finds the vector's inclination in the range [3.0 \u03c0 /
    * 2.0, \u03c0 / 2.0].
    *
    * @param v
    *           the input vector
    * @return the inclination
    * @see Vec3#inclinationSigned(Vec3)
    * @see Utils#modRadians(float)
    */
   public static float inclinationUnsigned ( final Vec3 v ) {

      return Utils.modRadians(Vec3.inclinationSigned(v));
   }

   /**
    * Tests to see if all the vector's components are non-zero.
    * Useful when testing valid dimensions (width, depth and
    * height) stored in vectors.
    *
    * @param v
    *           the input vector
    * @return the evaluation
    */
   public static boolean isNonZero ( final Vec3 v ) {

      return v.x != 0.0f && v.y != 0.0f && v.z != 0.0f;
   }

   /**
    * Tests to see if the vector is on the unit sphere, i.e.,
    * has a magnitude of approximately 1.0.
    *
    * @param v
    *           the input vector
    * @return the evaluation
    * @see Utils#approxFast(float, float)
    * @see Vec3#dot(Vec3, Vec3)
    */
   public static boolean isUnit ( final Vec3 v ) {

      return Utils.approxFast(Vec3.magSq(v), 1.0f);
   }

   /**
    * Tests to see if all the vector's components are zero.
    * Useful when safeguarding against invalid directions.
    *
    * @param a
    *           the input vector
    * @return the evaluation
    */
   public static boolean isZero ( final Vec3 a ) {

      return a.x == 0.0f && a.y == 0.0f && a.z == 0.0f;
   }

   /**
    * Returns a vector with a negative value on the x axis,
    * (-1.0, 0.0, 0.0).
    *
    * @param target
    *           the output vector
    * @return the left vector
    */
   public static Vec3 left ( final Vec3 target ) {

      return target.set(-1.0f, 0.0f, 0.0f);
   }

   /**
    * Limits a vector's magnitude to a scalar. Does nothing if
    * the vector is beneath the limit.
    *
    * @param v
    *           the input vector
    * @param limit
    *           the limit
    * @param target
    *           the output vector
    * @return the limited vector
    * @see Vec3#dot(Vec3, Vec3)
    * @see Vec3#rescale(Vec3, float, Vec3)
    */
   public static Vec3 limit (
         final Vec3 v,
         final float limit,
         final Vec3 target ) {

      if (Vec3.magSq(v) > limit * limit) {
         return Vec3.rescale(v, limit, target);
      }
      return target.set(v);
   }

   /**
    * Finds the length, or magnitude, of a vector, |<em>a</em>|
    * . Also referred to as the radius when using spherical
    * coordinates. Uses the formula \u221a <em>a</em> \u00b7
    * <em>a</em> . Where possible, use magSq or dot to avoid
    * the computational cost of the square-root.
    *
    * @param v
    *           the input vector
    * @return the magnitude
    * @see Vec3#dot(Vec3, Vec3)
    * @see Math#sqrt(double)
    * @see Vec3#magSq(Vec3)
    */
   public static float mag ( final Vec3 v ) {

      return (float) Math.sqrt(Vec3.magSq(v));
   }

   /**
    * Finds the length-, or magnitude-, squared of a vector,
    * |<em>a</em>|<sup>2</sup>. Returns the same result as
    * <em>a</em> \u00b7 <em>a</em> . Useful when calculating
    * the lengths of many vectors, so as to avoid the
    * computational cost of the square-root.
    *
    * @param v
    *           the input vector
    * @return the magnitude squared
    * @see Vec3#dot(Vec3, Vec3)
    * @see Vec3#mag(Vec3)
    */
   public static float magSq ( final Vec3 v ) {

      return v.x * v.x + v.y * v.y + v.z * v.z;
   }

   /**
    * Maps an input vector from an original range to a target
    * range.
    *
    * @param v
    *           the input vector
    * @param lbOrigin
    *           lower bound of original range
    * @param ubOrigin
    *           upper bound of original range
    * @param lbDest
    *           lower bound of destination range
    * @param ubDest
    *           upper bound of destination range
    * @param target
    *           the output vector
    * @return the mapped value
    * @see Utils#map(float, float, float, float, float)
    */
   public static Vec3 map (
         final Vec3 v,
         final Vec3 lbOrigin,
         final Vec3 ubOrigin,
         final Vec3 lbDest,
         final Vec3 ubDest,
         final Vec3 target ) {

      return target.set(
            Utils.map(v.x, lbOrigin.x, ubOrigin.x, lbDest.x, ubDest.x),
            Utils.map(v.y, lbOrigin.y, ubOrigin.y, lbDest.y, ubDest.y),
            Utils.map(v.z, lbOrigin.z, ubOrigin.z, lbDest.z, ubDest.z));
   }

   /**
    * Sets the target vector to the maximum of the input vector
    * and an upper bound.
    *
    * @param a
    *           the input value
    * @param upperBound
    *           the upper bound
    * @param target
    *           the output vector
    * @return the maximum values
    */
   public static Vec3 max (
         final Vec3 a,
         final float upperBound,
         final Vec3 target ) {

      return target.set(
            Utils.max(a.x, upperBound),
            Utils.max(a.y, upperBound),
            Utils.max(a.z, upperBound));
   }

   /**
    * Sets the target vector to the maximum components of the
    * input vector and a upper bound.
    *
    * @param a
    *           the input vector
    * @param upperBound
    *           the upper bound
    * @param target
    *           the output vector
    * @return the maximum values
    * @see Utils#max(float, float)
    */
   public static Vec3 max (
         final Vec3 a,
         final Vec3 upperBound,
         final Vec3 target ) {

      return target.set(
            Utils.max(a.x, upperBound.x),
            Utils.max(a.y, upperBound.y),
            Utils.max(a.z, upperBound.z));
   }

   /**
    * Sets the target vector to the minimum components of the
    * input vector and a lower bound.
    *
    * @param a
    *           the input value
    * @param lowerBound
    *           the lower bound
    * @param target
    *           the output vector
    * @return the minimum values
    */
   public static Vec3 min (
         final Vec3 a,
         final float lowerBound,
         final Vec3 target ) {

      return target.set(
            Utils.min(a.x, lowerBound),
            Utils.min(a.y, lowerBound),
            Utils.min(a.z, lowerBound));
   }

   /**
    * Sets the target vector to the minimum components of the
    * input vector and a lower bound.
    *
    * @param a
    *           the input vector
    * @param lowerBound
    *           the lower bound
    * @param target
    *           the output vector
    * @return the minimal values
    * @see Utils#min(float, float)
    */
   public static Vec3 min (
         final Vec3 a,
         final Vec3 lowerBound,
         final Vec3 target ) {

      return target.set(
            Utils.min(a.x, lowerBound.x),
            Utils.min(a.y, lowerBound.y),
            Utils.min(a.z, lowerBound.z));
   }

   /**
    * Mixes two vectors together by a step in [0.0, 1.0]. Uses
    * the easing function that is a static field belonging to
    * the Vec3 class.
    *
    * @param origin
    *           the original vector
    * @param dest
    *           the destination vector
    * @param step
    *           the step
    * @param target
    *           the output vector
    * @return the mix
    * @see Vec3#EASING
    */
   public static Vec3 mix (
         final Vec3 origin,
         final Vec3 dest,
         final float step,
         final Vec3 target ) {

      return Vec3.EASING.apply(origin, dest, step, target);
   }

   /**
    * Mixes two vectors together by a step in [0.0, 1.0] with
    * the help of a easing function.
    *
    * @param origin
    *           the original vector
    * @param dest
    *           the destination vector
    * @param step
    *           the step
    * @param target
    *           the output vector
    * @param easingFunc
    *           the easing function
    * @return the mix
    */
   public static Vec3 mix (
         final Vec3 origin,
         final Vec3 dest,
         final float step,
         final Vec3 target,
         final AbstrEasing easingFunc ) {

      return easingFunc.apply(origin, dest, step, target);
   }

   /**
    * Mods a scalar by each component of a vector.
    *
    * @param a
    *           the scalar
    * @param b
    *           the vector
    * @param target
    *           the output vector
    * @return the modulated vector
    * @see Utils#mod(float, float)
    */
   public static Vec3 mod (
         final float a,
         final Vec3 b,
         final Vec3 target ) {

      return target.set(
            Utils.mod(a, b.x),
            Utils.mod(a, b.y),
            Utils.mod(a, b.z));
   }

   /**
    * Mods each component of a vector by a scalar
    *
    * @param a
    *           the vector
    * @param b
    *           the scalar
    * @param target
    *           the output vector
    * @return the modulated vector
    * @see Utils#modUnchecked(float, float)
    */
   public static Vec3 mod (
         final Vec3 a,
         final float b,
         final Vec3 target ) {

      if (b == 0.0f) {
         return target.set(a);
      }

      return target.set(
            Utils.modUnchecked(a.x, b),
            Utils.modUnchecked(a.y, b),
            Utils.modUnchecked(a.z, b));
   }

   /**
    * Mods each component of the left vector by those of the
    * right.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output vector
    * @return the modulated vector
    * @see Utils#mod(float, float)
    */
   public static Vec3 mod (
         final Vec3 a,
         final Vec3 b,
         final Vec3 target ) {

      return target.set(
            Utils.mod(a.x, b.x),
            Utils.mod(a.y, b.y),
            Utils.mod(a.z, b.z));
   }

   /**
    * A specialized form of mod which subtracts the floor of
    * the vector from the vector.
    *
    * @param v
    *           the input vector
    * @param target
    *           the output vector
    * @return the result
    * @see Utils#mod1(float)
    */
   public static Vec3 mod1 (
         final Vec3 v,
         final Vec3 target ) {

      return target.set(
            Utils.mod1(v.x),
            Utils.mod1(v.y),
            Utils.mod1(v.z));
   }

   /**
    * Multiplies a vector by a scalar.
    *
    * @param a
    *           left operand, the scalar
    * @param b
    *           right operand, the vector
    * @param target
    *           the output vector
    * @return the product
    */
   public static Vec3 mul (
         final float a,
         final Vec3 b,
         final Vec3 target ) {

      return target.set(a * b.x, a * b.y, a * b.z);
   }

   /**
    * Multiplies a vector by a scalar.
    *
    * @param a
    *           left operand, the vector
    * @param b
    *           right operand, the scalar
    * @param target
    *           the output vector
    * @return the product
    */
   public static Vec3 mul (
         final Vec3 a,
         final float b,
         final Vec3 target ) {

      return target.set(
            a.x * b,
            a.y * b,
            a.z * b);
   }

   /**
    * Multiplies two vectors, component-wise. Such
    * multiplication is mathematically incorrect, but serves as
    * a shortcut for transforming a vector by a scalar matrix.
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @param target
    *           the output vector
    * @return the product
    */
   public static Vec3 mul (
         final Vec3 a,
         final Vec3 b,
         final Vec3 target ) {

      return target.set(
            a.x * b.x,
            a.y * b.y,
            a.z * b.z);
   }

   /**
    * Negates the input vector.
    *
    * @param v
    *           the input vector
    * @param target
    *           the output vector
    * @return the negation
    */
   public static Vec3 negate (
         final Vec3 v,
         final Vec3 target ) {

      return target.set(-v.x, -v.y, -v.z);
   }

   /**
    * Returns a vector with all components set to negative one.
    *
    * @param target
    *           the output vector
    * @return negative one
    */
   public static Vec3 negOne ( final Vec3 target ) {

      return target.set(-1.0f, -1.0f, -1.0f);
   }

   /**
    * Divides a vector by its magnitude, such that the new
    * magnitude is 1.0. <em>\u00e2</em> = <em>a</em> /
    * |<em>a</em>|. The result is a unit vector, as it lies on
    * the unit sphere.
    *
    * @param v
    *           the input vector
    * @param target
    *           the output vector
    * @return the unit vector
    * @see Vec3#div(Vec3, float, Vec3)
    * @see Vec3#mag(Vec3)
    */
   public static Vec3 normalize (
         final Vec3 v,
         final Vec3 target ) {

      final float mSq = v.x * v.x + v.y * v.y + v.z * v.z;

      if (mSq == 0.0f) {
         return target.reset();
      }

      if (Utils.approxFast(mSq, 1.0f)) {
         return target.set(v);
      }

      final float mInv = (float) (1.0d / Math.sqrt(mSq));
      return target.set(
            v.x * mInv,
            v.y * mInv,
            v.z * mInv);
   }

   /**
    * Returns a vector with all components set to one.
    *
    * @param target
    *           the output vector
    * @return one
    */
   public static Vec3 one ( final Vec3 target ) {

      return target.set(1.0f, 1.0f, 1.0f);
   }

   /**
    * Raises a scalar to a vector.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output vector
    * @return the result
    * @see Math#pow(double, double)
    */
   public static Vec3 pow (
         final float a,
         final Vec3 b,
         final Vec3 target ) {

      return target.set(
            (float) Math.pow(a, b.x),
            (float) Math.pow(a, b.y),
            (float) Math.pow(a, b.z));
   }

   /**
    * Raises a vector to the power of a scalar.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output vector
    * @return the result
    * @see Math#pow(double, double)
    */
   public static Vec3 pow (
         final Vec3 a,
         final float b,
         final Vec3 target ) {

      return target.set(
            (float) Math.pow(a.x, b),
            (float) Math.pow(a.y, b),
            (float) Math.pow(a.z, b));
   }

   /**
    * Raises a vector to the power of another vector.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output vector
    * @return the result
    * @see Math#pow(double, double)
    */
   public static Vec3 pow (
         final Vec3 a,
         final Vec3 b,
         final Vec3 target ) {

      return target.set(
            (float) Math.pow(a.x, b.x),
            (float) Math.pow(a.y, b.y),
            (float) Math.pow(a.z, b.z));
   }

   /**
    * Projects one vector onto another.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output vector
    * @return the projection
    * @see Vec3#projectVector(Vec3, Vec3, Vec3)
    */
   public static Vec3 project (
         final Vec3 a,
         final Vec3 b,
         final Vec3 target ) {

      return Vec3.projectVector(a, b, target);
   }

   /**
    * Returns the scalar projection of <em>a</em> onto
    * <em>b</em>.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @return the scalar projection
    * @see Vec3#magSq(Vec3)
    * @see Vec3#dot(Vec3, Vec3)
    */
   public static float projectScalar (
         final Vec3 a,
         final Vec3 b ) {

      final float bSq = Vec3.magSq(b);
      if (bSq != 0.0f) {
         return Vec3.dot(a, b) / bSq;
      }
      return 0.0f;
   }

   /**
    * Projects one vector onto another. Defined as<br>
    * <br>
    * proj ( <em>a</em>, <em>b</em> ) := <em>b</em> (
    * <em>a</em> \u00b7 <em>b</em> / <em>b</em> \u00b7
    * <em>b</em> )
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output vector
    * @return the projection
    * @see Vec3#projectScalar(Vec3, Vec3)
    * @see Vec3#mul(Vec3, float, Vec3)
    */
   public static Vec3 projectVector (
         final Vec3 a,
         final Vec3 b,
         final Vec3 target ) {

      return Vec3.mul(b, Vec3.projectScalar(a, b), target);
   }

   /**
    * Creates a vector with a magnitude of 1.0 at a random
    * azimuth and inclination, such that it lies on the unit
    * sphere.
    *
    * @param rng
    *           the random number generator
    * @param target
    *           the output vector
    * @return the random vector
    * @see Vec3#randomSpherical(Random, float, float, Vec3)
    */
   public static Vec3 random (
         final Random rng,
         final Vec3 target ) {

      return Vec3.randomSpherical(rng, 1.0f, 1.0f, target);
   }

   /**
    * Creates a random point in the Cartesian coordinate system
    * given a lower and an upper bound.
    *
    * @param rng
    *           the random number generator
    * @param lowerBound
    *           the lower bound
    * @param upperBound
    *           the upper bound
    * @param target
    *           the output vector
    * @return the random vector
    * @see Random#uniform(float, float)
    */
   public static Vec3 randomCartesian (
         final Random rng,
         final float lowerBound,
         final float upperBound,
         final Vec3 target ) {

      return target.set(
            rng.uniform(lowerBound, upperBound),
            rng.uniform(lowerBound, upperBound),
            rng.uniform(lowerBound, upperBound));
   }

   /**
    * Creates a random point in the Cartesian coordinate system
    * given a lower and an upper bound.
    *
    * @param rng
    *           the random number generator
    * @param lowerBound
    *           the lower bound
    * @param upperBound
    *           the upper bound
    * @param target
    *           the output vector
    * @return the random vector
    * @see Random#uniform(float, float)
    */
   public static Vec3 randomCartesian (
         final Random rng,
         final Vec3 lowerBound,
         final Vec3 upperBound,
         final Vec3 target ) {

      return target.set(
            rng.uniform(lowerBound.x, upperBound.x),
            rng.uniform(lowerBound.y, upperBound.y),
            rng.uniform(lowerBound.z, upperBound.z));
   }

   /**
    * Creates a vector at a random azimuth, inclination and
    * radius.
    *
    * @param rng
    *           the random number generator
    * @param rhoMin
    *           the minimum radius
    * @param rhoMax
    *           the maximum radius
    * @param target
    *           the output vector
    * @return the random vector
    * @see Random#uniform(float, float)
    * @see Math#sqrt(double)
    */
   public static Vec3 randomSpherical (
         final Random rng,
         final float rhoMin,
         final float rhoMax,
         final Vec3 target ) {

      // TODO: Is random distribution an issue, as it
      // was with Quaternion random?
      final float x = rng.uniform(-1.0f, 1.0f);
      final float y = rng.uniform(-1.0f, 1.0f);
      final float z = rng.uniform(-1.0f, 1.0f);

      final float mSq = x * x + y * y + z * z;
      if (mSq == 0.0f) {
         return target.reset();
      }

      final float rho = rng.uniform(rhoMin, rhoMax);
      final float rhoNorm = (float) (rho / Math.sqrt(mSq));

      return target.set(
            x * rhoNorm,
            y * rhoNorm,
            z * rhoNorm);
   }

   /**
    * Creates a vector with a magnitude of 1.0 at a random
    * azimuth and inclination, such that it lies on the unit
    * sphere.
    *
    * @param rng
    *           the random number generator
    * @param target
    *           the output vector
    * @return the random vector
    * @see Vec3#randomSpherical(Random, float, float, Vec3)
    */
   public static Vec3 randomSpherical (
         final Random rng,
         final Vec3 target ) {

      return Vec3.randomSpherical(rng, 1.0f, 1.0f, target);
   }

   /**
    * Reflects an incident vector off a normal vector. Uses the
    * formula <br>
    * <br>
    * <em>i</em> - 2.0 (<em>n</em> \u00b7 <em>i</em>)
    * <em>n</em><br>
    * <br>
    *
    * @param incident
    *           the incident vector
    * @param normal
    *           the normal vector
    * @param target
    *           the output vector
    * @return the reflected vector
    * @see Vec3#dot(Vec3, Vec3)
    * @see Math#sqrt(double)
    */
   public static Vec3 reflect (
         final Vec3 incident,
         final Vec3 normal,
         final Vec3 target ) {

      // TODO: Needs testing.

      final float nMSq = Vec3.magSq(normal);
      if (Utils.abs(nMSq) < Utils.EPSILON) {
         return target.reset();
      }

      if (Utils.approxFast(nMSq, 1.0f)) {
         final float scalar = 2.0f * Vec3.dot(normal, incident);
         return target.set(
               incident.x - scalar * normal.x,
               incident.y - scalar * normal.y,
               incident.z - scalar * normal.z);
      }

      final float mInv = (float) (1.0d / Math.sqrt(nMSq));
      final float nx = normal.x * mInv;
      final float ny = normal.y * mInv;
      final float nz = normal.z * mInv;
      final float scalar = 2.0f * (nx * incident.x +
            ny * incident.y +
            nz * incident.z);
      return target.set(
            incident.x - scalar * nx,
            incident.y - scalar * ny,
            incident.z - scalar * nz);
   }

   /**
    * Refracts a vector through a volume using Snell's law.
    *
    * @param incident
    *           the incident vector
    * @param normal
    *           the normal vector
    * @param eta
    *           ratio of refraction indices
    * @param target
    *           the output vector
    * @return the refraction
    * @see Vec3#dot(Vec3, Vec3)
    * @see Math#sqrt(double)
    */
   public static Vec3 refract (
         final Vec3 incident,
         final Vec3 normal,
         final float eta,
         final Vec3 target ) {

      // TODO: Needs testing.

      final float nDotI = Vec3.dot(normal, incident);
      final float k = 1.0f - eta * eta * (1.0f - nDotI * nDotI);
      if (k < 0.0f) {
         return target.reset();
      }
      final float scalar = eta * nDotI + (float) Math.sqrt(k);
      return target.set(
            eta * incident.x - normal.x * scalar,
            eta * incident.y - normal.y * scalar,
            eta * incident.z - normal.z * scalar);
   }

   /**
    * Normalizes a vector, then multiplies it by a scalar, in
    * effect setting its magnitude to that scalar.
    *
    * @param v
    *           the vector
    * @param scalar
    *           the scalar
    * @param target
    *           the output vector
    * @return the rescaled vector
    * @see Vec3#rescale(Vec3, float, Vec3, Vec3)
    */
   public static Vec3 rescale (
         final Vec3 v,
         final float scalar,
         final Vec3 target ) {

      if (scalar == 0.0f) {
         return target.reset();
      }

      final float mSq = Vec3.magSq(v);

      if (mSq == 0.0f) {
         return target.reset();
      }

      if (Utils.approxFast(mSq, 1.0f)) {
         return Vec3.mul(v, scalar, target);
      }

      return Vec3.mul(v, (float) (scalar / Math.sqrt(mSq)), target);
   }

   /**
    * Normalizes a vector, then multiplies it by a scalar, in
    * effect setting its magnitude to that scalar.
    *
    * @param v
    *           the vector
    * @param scalar
    *           the scalar
    * @param target
    *           the output vector
    * @param normalized
    *           the normalized vector
    * @return the rescaled vector
    * @see Vec3#normalize(Vec3, Vec3)
    * @see Vec3#mul(Vec3, float, Vec3)
    */
   public static Vec3 rescale (
         final Vec3 v,
         final float scalar,
         final Vec3 target,
         final Vec3 normalized ) {

      if (scalar == 0.0f) {
         return target.reset();
      }
      Vec3.normalize(v, normalized);
      return Vec3.mul(normalized, scalar, target);
   }

   /**
    * Returns to a vector with a positive value on the x axis,
    * (1.0, 0.0, 0.0) .
    *
    * @param target
    *           the output vector
    * @return the right vector
    */
   public static Vec3 right ( final Vec3 target ) {

      return target.set(1.0f, 0.0f, 0.0f);
   }

   /**
    * Rotates a vector around an axis by an angle in radians.
    * The axis is assumed to be of unit length. Accepts
    * pre-calculated sine and cosine of an angle, so that
    * collections of vectors can be efficiently rotated without
    * repeatedly calling cos and sin.
    *
    * @param v
    *           the vector to rotate
    * @param cosa
    *           cosine of the angle
    * @param sina
    *           sine of the angle
    * @param axis
    *           the axis of rotation
    * @param target
    *           the output vector
    * @return the rotated vector
    */
   public static Vec3 rotate (
         final Vec3 v,
         final float cosa,
         final float sina,
         final Vec3 axis,
         final Vec3 target ) {

      final float complcos = 1.0f - cosa;
      final float complxy = complcos * axis.x * axis.y;
      final float complxz = complcos * axis.x * axis.z;
      final float complyz = complcos * axis.y * axis.z;

      final float sinx = sina * axis.x;
      final float siny = sina * axis.y;
      final float sinz = sina * axis.z;

      return target.set(
            (complcos * axis.x * axis.x + cosa) * v.x + (complxy - sinz) * v.y
                  + (complxz + siny) * v.z,

            (complxy + sinz) * v.x + (complcos * axis.y * axis.y + cosa) * v.y
                  + (complyz - sinx) * v.z,

            (complxz - siny) * v.x + (complyz + sinx) * v.y
                  + (complcos * axis.z * axis.z + cosa) * v.z);
   }

   /**
    * Rotates a vector around an axis by an angle in radians.
    * The axis is assumed to be of unit length.
    *
    * @param v
    *           the vector to rotate
    * @param radians
    *           the angle in radians
    * @param axis
    *           the axis of rotation
    * @param target
    *           the output vector
    * @return the rotated vector
    * @see Math#cos(double)
    * @see Math#sin(double)
    */
   public static Vec3 rotate (
         final Vec3 v,
         final float radians,
         final Vec3 axis,
         final Vec3 target ) {

      return Vec3.rotate(
            v,
            (float) Math.cos(radians),
            (float) Math.sin(radians),
            axis,
            target);
   }

   /**
    * Rotates a vector around the x axis. Accepts
    * pre-calculated sine and cosine of an angle, so that
    * collections of vectors can be efficiently rotated without
    * repeatedly calling cos and sin.
    *
    * @param v
    *           the input vector
    * @param cosa
    *           cosine of the angle
    * @param sina
    *           sine of the angle
    * @param target
    *           the output vector
    * @return the rotated vector
    */
   public static Vec3 rotateX (
         final Vec3 v,
         final float cosa,
         final float sina,
         final Vec3 target ) {

      return target.set(
            v.x,
            cosa * v.y - sina * v.z,
            cosa * v.z + sina * v.y);
   }

   /**
    * Rotates a vector around the x axis by an angle in
    * radians.
    *
    * Do not use sequences of ortho-normal rotations by Euler
    * angles; this will result in gimbal lock. Instead, rotate
    * by an angle around an axis or create a quaternion.
    *
    * @param v
    *           the input vector
    * @param radians
    *           the angle in radians
    * @param target
    *           the output vector
    * @return the rotated vector
    * @see Math#cos(double)
    * @see Math#sin(double)
    * @see Vec3#rotate(Vec3, float, Vec3, Vec3)
    */
   public static Vec3 rotateX (
         final Vec3 v,
         final float radians,
         final Vec3 target ) {

      return Vec3.rotateX(
            v,
            (float) Math.cos(radians),
            (float) Math.sin(radians),
            target);
   }

   /**
    * Rotates a vector around the y axis. Accepts
    * pre-calculated sine and cosine of an angle, so that
    * collections of vectors can be efficiently rotated without
    * repeatedly calling cos and sin.
    *
    * @param v
    *           the input vector
    * @param cosa
    *           cosine of the angle
    * @param sina
    *           sine of the angle
    * @param target
    *           the output vector
    * @return the rotated vector
    */
   public static Vec3 rotateY (
         final Vec3 v,
         final float cosa,
         final float sina,
         final Vec3 target ) {

      return target.set(
            cosa * v.x + sina * v.z,
            v.y,
            cosa * v.z - sina * v.x);
   }

   /**
    * Rotates a vector around the y axis by an angle in
    * radians.
    *
    * Do not use sequences of ortho-normal rotations by Euler
    * angles; this will result in gimbal lock. Instead, rotate
    * by an angle around an axis or create a quaternion.
    *
    * @param v
    *           the input vector
    * @param radians
    *           the angle in radians
    * @param target
    *           the output vector
    * @return the rotated vector
    * @see Math#cos(double)
    * @see Math#sin(double)
    * @see Vec3#rotate(Vec3, float, Vec3, Vec3)
    */
   public static Vec3 rotateY (
         final Vec3 v,
         final float radians,
         final Vec3 target ) {

      return Vec3.rotateY(
            v,
            (float) Math.cos(radians),
            (float) Math.sin(radians),
            target);
   }

   /**
    * Rotates a vector around the z axis. Accepts
    * pre-calculated sine and cosine of an angle, so that
    * collections of vectors can be efficiently rotated without
    * repeatedly calling cos and sin.
    *
    * @param v
    *           the input vector
    * @param cosa
    *           cosine of the angle
    * @param sina
    *           sine of the angle
    * @param target
    *           the output vector
    * @return the rotated vector
    */
   public static Vec3 rotateZ (
         final Vec3 v,
         final float cosa,
         final float sina,
         final Vec3 target ) {

      return target.set(
            cosa * v.x - sina * v.y,
            cosa * v.y + sina * v.x,
            v.z);
   }

   /**
    * Rotates a vector around the z axis by an angle in
    * radians.
    *
    * Do not use sequences of ortho-normal rotations by Euler
    * angles; this will result in gimbal lock. Instead, rotate
    * by an angle around an axis or create a quaternion.
    *
    * @param v
    *           the input vector
    * @param radians
    *           the angle in radians
    * @param target
    *           the output vector
    * @return the rotated vector
    * @see Math#cos(double)
    * @see Math#sin(double)
    * @see Vec3#rotate(Vec3, float, Vec3, Vec3)
    */
   public static Vec3 rotateZ (
         final Vec3 v,
         final float radians,
         final Vec3 target ) {

      return Vec3.rotateZ(
            v,
            (float) Math.cos(radians),
            (float) Math.sin(radians),
            target);
   }

   /**
    * Rounds each component of the vector to the nearest whole
    * number.
    *
    * @param v
    *           the input vector
    * @param target
    *           the output vector
    * @return the rounded vector
    * @see Math#round(float)
    */
   public static Vec3 round (
         final Vec3 v,
         final Vec3 target ) {

      return target.set(
            Math.round(v.x),
            Math.round(v.y),
            Math.round(v.z));
   }

   /**
    * Sets the comparator function by which collections of
    * vectors are compared.
    *
    * @param comparator
    *           the comparator
    */
   public static void setComparator ( final Comparator < Vec3 > comparator ) {

      if (comparator != null) {
         Vec3.COMPARATOR = comparator;
      }
   }

   /**
    * Sets the easing function by which vectors are
    * interpolated.
    *
    * @param easing
    *           the easing function
    */
   public static void setEasing ( final AbstrEasing easing ) {

      if (easing != null) {
         Vec3.EASING = easing;
      }
   }

   /**
    * Subtracts the right vector from the left vector.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output vector
    * @return the difference
    */
   public static Vec3 sub (
         final Vec3 a,
         final Vec3 b,
         final Vec3 target ) {

      return target.set(
            a.x - b.x,
            a.y - b.y,
            a.z - b.z);
   }

   /**
    * Subtracts the right from the left vector and then
    * normalizes the difference.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output vector
    * @return the normalized difference
    */
   public static Vec3 subNorm (
         final Vec3 a,
         final Vec3 b,
         final Vec3 target ) {

      final float dx = a.x - b.x;
      final float dy = a.y - b.y;
      final float dz = a.z - b.z;

      final float mSq = dx * dx + dy * dy + dz * dz;
      if (mSq == 0.0f) {
         return target.reset();
      }

      final float mInv = (float) (1.0d / Math.sqrt(mSq));
      return target.set(
            dx * mInv,
            dy * mInv,
            dz * mInv);
   }

   /**
    * Subtracts the right from the left vector and then
    * normalizes the difference.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output vector
    * @param dir
    *           the unnormalized direction
    * @return the normalized difference
    * @see Vec3#sub(Vec3, Vec3, Vec3)
    * @see Vec3#normalize(Vec3, Vec3)
    */
   public static Vec3 subNorm (
         final Vec3 a,
         final Vec3 b,
         final Vec3 target,
         final Vec3 dir ) {

      Vec3.sub(a, b, dir);
      Vec3.normalize(dir, target);
      return target;
   }

   /**
    * Truncates each component of the vector.
    *
    * @param v
    *           the input vector
    * @param target
    *           the target vector
    * @return the truncation
    */
   public static Vec3 trunc (
         final Vec3 v,
         final Vec3 target ) {

      return target.set(
            (int) v.x,
            (int) v.y,
            (int) v.z);
   }

   /**
    * Returns to a vector with a positive value on the z-axis,
    * (0.0, 0.0, 1.0) .
    *
    * @param target
    *           the output vector
    * @return the up vector
    */
   public static Vec3 up ( final Vec3 target ) {

      return target.set(0.0f, 0.0f, 1.0f);
   }

   /**
    * Returns a vector with all components set to zero.
    *
    * @param target
    *           the output vector
    * @return the zero vector
    */
   public static Vec3 zero ( final Vec3 target ) {

      return target.set(0.0f, 0.0f, 0.0f);
   }

   /**
    * Component on the x axis in the Cartesian coordinate
    * system.
    */
   public float x = 0.0f;

   /**
    * Component on the y axis in the Cartesian coordinate
    * system.
    */
   public float y = 0.0f;

   /**
    * Component on the z axis in the Cartesian coordinate
    * system.
    */
   public float z = 0.0f;

   /**
    * The default vector constructor.
    */
   public Vec3 () {

      super(3);
   }

   /**
    * Constructs a vector from boolean values.
    *
    * @param x
    *           the x component
    * @param y
    *           the y component
    * @param z
    *           the z component
    */
   public Vec3 (
         final boolean x,
         final boolean y,
         final boolean z ) {

      super(3);
      this.set(x, y, z);
   }

   /**
    * Constructs a vector from float values.
    *
    * @param x
    *           the x component
    * @param y
    *           the y component
    * @param z
    *           the z component
    */
   public Vec3 (
         final float x,
         final float y,
         final float z ) {

      super(3);
      this.set(x, y, z);
   }

   /**
    * Attempts to construct a vector from Strings using
    * {@link Float#parseFloat(String)} . If a
    * NumberFormatException is thrown, the component is set to
    * zero.
    *
    * @param xstr
    *           the x string
    * @param ystr
    *           the y string
    * @param zstr
    *           the z string
    * @see Float#parseFloat(String)
    */
   public Vec3 (
         final String xstr,
         final String ystr,
         final String zstr ) {

      super(3);
      this.set(xstr, ystr, zstr);
   }

   /**
    * Promotes a Vec2 to a Vec3 with an extra component.
    *
    * @param v2
    *           the vector
    * @param z
    *           the z component
    */
   public Vec3 (
         final Vec2 v2,
         final float z ) {

      super(3);
      this.set(v2, z);
   }

   /**
    * Constructs a vector from a source vector's components.
    *
    * @param source
    *           the source vector
    */
   public Vec3 ( final Vec3 source ) {

      super(3);
      this.set(source);
   }

   /**
    * Tests equivalence between this and another vector. For
    * rough equivalence of floating point components, use the
    * static approx function instead.
    *
    * @param v
    *           the vector
    * @return the evaluation
    * @see Float#floatToIntBits(float)
    * @see Vec3#approx(Vec3, Vec3)
    * @see Vec3#approx(Vec3, Vec3, float)
    */
   protected boolean equals ( final Vec3 v ) {

      if (Float.floatToIntBits(this.z) != Float.floatToIntBits(v.z)) {
         return false;
      }

      if (Float.floatToIntBits(this.y) != Float.floatToIntBits(v.y)) {
         return false;
      }

      if (Float.floatToIntBits(this.x) != Float.floatToIntBits(v.x)) {
         return false;
      }

      return true;
   }

   /**
    * Returns a new vector with this vector's components.
    * Java's cloneable interface is problematic; use set or a
    * copy constructor instead.
    *
    * @return a new vector
    * @see Vec3#set(Vec3)
    * @see Vec3#Vec3(Vec3)
    */
   @Override
   public Vec3 clone () {

      return new Vec3(this.x, this.y, this.z);
   }

   /**
    * Returns -1 when this vector is less than the comparisand;
    * 1 when it is greater than; 0 when the two are 'equal'.
    * The implementation of this method allows collections of
    * vectors to be sorted. This depends upon the static
    * comparator of the Vec3 class, which can be changed.
    *
    * @param v
    *           the comparisand
    * @return the numeric code
    * @see Vec3#COMPARATOR
    */
   @Override
   public int compareTo ( final Vec3 v ) {

      return Vec3.COMPARATOR.compare(this, v);
   }

   /**
    * Tests this vector for equivalence with another object.
    *
    * @param obj
    *           the object
    * @return the equivalence
    * @see Vec3#equals(Vec3)
    */
   @Override
   public boolean equals ( final Object obj ) {

      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (this.getClass() != obj.getClass()) {
         return false;
      }
      return this.equals((Vec3) obj);
   }

   /**
    * Simulates bracket subscript access in an array. When the
    * provided index is 2 or -1, returns z; 1 or -2, y; 0 or
    * -3, x.
    *
    * @param index
    *           the index
    * @return the component at that index
    */
   @Override
   public float get ( final int index ) {

      switch (index) {
         case 0:
         case -3:
            return this.x;
         case 1:
         case -2:
            return this.y;
         case 2:
         case -1:
            return this.z;
         default:
            return 0.0f;
      }
   }

   /**
    * Returns a hash code for this vector based on its x, y and
    * z components.
    *
    * @return the hash code
    * @see Float#floatToIntBits(float)
    */
   @Override
   public int hashCode () {

      final int prime = 31;
      int result = 1;
      result = prime * result + Float.floatToIntBits(this.x);
      result = prime * result + Float.floatToIntBits(this.y);
      result = prime * result + Float.floatToIntBits(this.z);
      return result;
   }

   /**
    * Returns an iterator for this vector, which allows its
    * components to be accessed in an enhanced for-loop.
    *
    * @return the iterator
    */
   @Override
   public V3Iterator iterator () {

      return new V3Iterator(this);
   }

   /**
    * Resets this vector to an initial state, ( 0.0, 0.0, 0.0 )
    * .
    *
    * @return this vector
    */
   @Chainable
   public Vec3 reset () {

      return this.set(0.0f, 0.0f, 0.0f);
   }

   /**
    * Sets the components of this vector from booleans, where
    * false is 0.0 and true is 1.0 .
    *
    * @param x
    *           the x component
    * @param y
    *           the y component
    * @param z
    *           the z component
    * @return this vector
    * @see Utils#toFloat(boolean)
    */
   @Chainable
   public Vec3 set (
         final boolean x,
         final boolean y,
         final boolean z ) {

      this.x = Utils.toFloat(x);
      this.y = Utils.toFloat(y);
      this.z = Utils.toFloat(z);

      return this;
   }

   /**
    * Sets the components of this vector.
    *
    * @param x
    *           the x component
    * @param y
    *           the y component
    * @param z
    *           the z component
    * @return this vector
    */
   @Chainable
   public Vec3 set (
         final float x,
         final float y,
         final float z ) {

      this.x = x;
      this.y = y;
      this.z = z;

      return this;
   }

   /**
    * Attempts to set the components of this vector from
    * Strings using {@link Float#parseFloat(String)} . If a
    * NumberFormatException is thrown, the component is set to
    * zero.
    *
    * @param xstr
    *           the x string
    * @param ystr
    *           the y string
    * @param zstr
    *           the z string
    * @return this vector
    * @see Float#parseFloat(String)
    */
   @Chainable
   public Vec3 set (
         final String xstr,
         final String ystr,
         final String zstr ) {

      float x = 0.0f;
      float y = 0.0f;
      float z = 0.0f;

      try {
         x = Float.parseFloat(xstr);
      } catch (final NumberFormatException e) {
         x = 0.0f;
      }

      try {
         y = Float.parseFloat(ystr);
      } catch (final NumberFormatException e) {
         y = 0.0f;
      }

      try {
         z = Float.parseFloat(zstr);
      } catch (final NumberFormatException e) {
         z = 0.0f;
      }

      this.x = x;
      this.y = y;
      this.z = z;

      return this;
   }

   /**
    * Promotes a Vec2 to a Vec3 with an extra component.
    *
    * @param v2
    *           the vector
    * @param z
    *           the w component
    * @return this vector
    */
   @Chainable
   public Vec3 set ( final Vec2 v2, final float z ) {

      return this.set(v2.x, v2.y, z);
   }

   /**
    * Copies the components of the input vector to this vector.
    *
    * @param source
    *           the input vector
    * @return this vector
    */
   @Chainable
   public Vec3 set ( final Vec3 source ) {

      return this.set(source.x, source.y, source.z);
   }

   /**
    * Returns a float array of length 3 containing this
    * vector's components.
    *
    * @return the array
    */
   @Override
   public float[] toArray () {

      return new float[] {
            this.x, this.y, this.z
      };
   }

   /**
    * Returns a string representation of this vector as a space
    * separated value for use by OBJ formatting functions.
    *
    * @return the string
    */
   public String toObjString () {

      return new StringBuilder(32)
            .append(Utils.toFixed(this.x, 6))
            .append(' ')
            .append(Utils.toFixed(this.y, 6))
            .append(' ')
            .append(Utils.toFixed(this.z, 6))
            .toString();
   }

   /**
    * Returns a string representation of this vector.
    *
    * @return the string
    */
   @Override
   public String toString () {

      return this.toString(4);
   }

   /**
    * Returns a string representation of this vector.
    *
    * @param places
    *           number of decimal places
    * @return the string
    */
   public String toString ( final int places ) {

      return new StringBuilder(80)
            .append("{ x: ")
            .append(Utils.toFixed(this.x, places))
            .append(", y: ")
            .append(Utils.toFixed(this.y, places))
            .append(", z: ")
            .append(Utils.toFixed(this.z, places))
            .append(' ')
            .append('}')
            .toString();
   }
}
