package camzup.core;

import java.util.Comparator;
import java.util.Iterator;

/**
 * A mutable, extensible class influenced by GLSL, OSL and
 * Processing's PVector. This is intended for storing points
 * and directions in two-dimensional graphics programs.
 * Instance methods are limited, while most static methods
 * require an explicit output variable to be provided.
 */
public class Vec2 extends Vec implements Comparable < Vec2 > {

   /**
    * An abstract class that may serve as an umbrella for any
    * custom comparators of Vec2 s.
    */
   public static abstract class AbstrComparator implements Comparator < Vec2 > {

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
      public abstract int compare ( Vec2 a, Vec2 b );

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
         implements Utils.EasingFuncObject < Vec2 > {

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
      public Vec2 apply (
            final Vec2 origin,
            final Vec2 dest,
            final Float step,
            final Vec2 target ) {

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
      public abstract Vec2 applyUnclamped (
         final Vec2 origin,
         final Vec2 dest,
         final float step,
         final Vec2 target );

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
      public final Vec2 aDiff = new Vec2();

      /**
       * The difference between the locus and right comparisand.
       */
      public final Vec2 bDiff = new Vec2();

      /**
       * The locus against which points are compared.
       */
      public final Vec2 locus = new Vec2();

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
      public ComparatorDist ( final Vec2 locus ) {

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
       * @see Vec2#sub(Vec2, Vec2, Vec2)
       * @see Vec2#dot(Vec2, Vec2)
       */
      @Override
      public int compare ( final Vec2 a, final Vec2 b ) {

         Vec2.sub(a, this.locus, this.aDiff);
         Vec2.sub(b, this.locus, this.bDiff);
         
         // return Float.compare(
         //       Vec2.magSq(this.aDiff),
         //       Vec2.magSq(this.bDiff));

         final float aDist = Vec2.magSq(aDiff);
         final float bDist = Vec2.magSq(bDiff);
         
         return aDist > bDist ? 1 : aDist < bDist ? -1 : 0;
      }
   }

   /**
    * Compares two vectors by subtracting them from a center
    * point, then measuring their signed heading.
    */
   public static class ComparatorWinding extends AbstrComparator {

      /**
       * The difference between the point and left comparisand.
       */
      public final Vec2 aDiff = new Vec2();

      /**
       * The difference between the point and right comparisand.
       */
      public final Vec2 bDiff = new Vec2();

      /**
       * The point against which points are compared.
       */
      public final Vec2 centroid = new Vec2();

      /**
       * The default constructor.
       */
      public ComparatorWinding () {

         super();
      }

      /**
       * A constructor which sets a point against which two points
       * are compared.
       *
       * @param centroid
       *           the centroid
       */
      public ComparatorWinding ( final Vec2 centroid ) {

         this.centroid.set(centroid);
      }

      /**
       * Compares two vectors by subtracting the locus from each,
       * then comparing the headings of the respective
       * differences.
       *
       * @param a
       *           the left comparisand
       * @param b
       *           the right comparisand
       * @return the comparison
       * @see Vec2#sub(Vec2, Vec2, Vec2)
       * @see Vec2#headingSigned(Vec2)
       */
      @Override
      public int compare ( final Vec2 a, final Vec2 b ) {

         /**
          * TODO: Is there a more efficient way than atan2? See
          * https://gamedev.stackexchange.com/questions/13229/
          * sorting-array-of-points-in-clockwise-order
          */

         Vec2.sub(a, this.centroid, this.aDiff);
         Vec2.sub(b, this.centroid, this.bDiff);

         // TODO: Are these comparisons inefficient because they autobox
         // float primitives into Float objects?
         return Float.compare(
               Vec2.headingSigned(this.aDiff),
               Vec2.headingSigned(this.bDiff));

         // final float cross = aDiff.x * bDiff.y - aDiff.y *
         // bDiff.x;
         // return Utils.sign(cross);

         // return Float.compare(
         // Vec2.dot(a, centroid),
         // Vec2.dot(b, centroid));
      }
   }

   /**
    * Compares two vectors by their y component, then by their
    * x component.
    */
   public static class ComparatorYX extends AbstrComparator {

      /**
       * The default constructor.
       */
      public ComparatorYX () {

         super();
      }

      /**
       * Compares the y and x component.
       *
       * @param a
       *           the left operand
       * @param b
       *           the right operand
       * @return the comparison
       */
      @Override
      public int compare ( final Vec2 a, final Vec2 b ) {

         return a.y > b.y ? 1
               : a.y < b.y ? -1
                     : a.x > b.x ? 1
                           : a.x < b.x ? -1 : 0;
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
       * @return the eased vector
       */
      @Override
      public Vec2 applyUnclamped (
            final Vec2 origin,
            final Vec2 dest,
            final float step,
            final Vec2 target ) {

         final double td = step;
         final double ud = 1.0d - td;
         return target.set(
               (float) (ud * origin.x + td * dest.x),
               (float) (ud * origin.y + td * dest.y));
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
      public Vec2 applyUnclamped (
            final Vec2 origin,
            final Vec2 dest,
            final float step,
            final Vec2 target ) {

         final double td = step;
         final double ts = td * td * (3.0d - (td + td));
         final double us = 1.0d - ts;
         return target.set(
               (float) (us * origin.x + ts * dest.x),
               (float) (us * origin.y + ts * dest.y));
      }
   }

   /**
    * An iterator, which allows a vector's components to be
    * accessed in an enhanced for loop.
    */
   public static final class V2Iterator implements Iterator < Float > {

      /**
       * The current index.
       */
      private int index = 0;

      /**
       * The vector being iterated over.
       */
      private final Vec2 vec;

      /**
       * The default constructor.w
       *
       * @param vec
       *           the vector to iterate
       */
      public V2Iterator ( final Vec2 vec ) {

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
       * @see Vec2#get(int)
       * @return the value
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
    * The default Vec2 comparator, ComparatorYX .
    */
   private static Comparator < Vec2 > COMPARATOR = new ComparatorYX();

   /**
    * The default easing function, lerp.
    */
   private static AbstrEasing EASING = new Lerp();

   /**
    * The unique identification for serialized classes.
    */
   private static final long serialVersionUID = 8867395334130420105L;

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
   public static Vec2 abs ( final Vec2 v, final Vec2 target ) {

      return target.set(
            Utils.abs(v.x),
            Utils.abs(v.y));
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
   public static Vec2 add ( final Vec2 a, final Vec2 b, final Vec2 target ) {

      return target.set(
            a.x + b.x,
            a.y + b.y);
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
   public static Vec2 addNorm ( final Vec2 a, final Vec2 b,
         final Vec2 target ) {

      final float dx = a.x + b.x;
      final float dy = a.y + b.y;

      final float mSq = dx * dx + dy * dy;
      if (mSq == 0.0f) {
         return target.reset();
      }

      final float m = (float) (1.0d / Math.sqrt(mSq));
      return target.set(
            dx * m,
            dy * m);
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
    * @see Vec2#add(Vec2, Vec2, Vec2)
    * @see Vec2#normalize(Vec2, Vec2)
    */
   public static Vec2 addNorm ( final Vec2 a, final Vec2 b,
         final Vec2 target,
         final Vec2 sum ) {

      Vec2.add(a, b, sum);
      Vec2.normalize(sum, target);
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
    * @see Vec2#isZero(Vec2)
    * @see Vec2#dot(Vec2, Vec2)
    * @see Vec2#mag(Vec2)
    * @see Utils#acos(float)
    */
   public static float angleBetween (
      final Vec2 a,
      final Vec2 b ) {

      if (Vec2.isZero(a) || Vec2.isZero(b)) {
         return 0.0f;
      }

      return Utils.acos(Vec2.dot(a, b) / (Vec2.mag(a) * Vec2.mag(b)));
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
      final Vec2 a, 
      final Vec2 b ) {

      return Utils.approxFast(a.y, b.y)
            && Utils.approxFast(a.x, b.x);
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
      final Vec2 a, 
      final Vec2 b,
      final float tolerance ) {

      return Utils.approxFast(a.y, b.y, tolerance)
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
    * @see Vec2#dot(Vec2, Vec2)
    */
   public static boolean approxMag ( 
      final Vec2 a, 
      final float b ) {

      return Utils.approxFast(Vec2.magSq(a), b * b);
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
    * @see Vec2#dot(Vec2, Vec2)
    */
   public static boolean approxMag ( 
      final Vec2 a, 
      final float b,
         final float tolerance ) {

      return Utils.approxFast(Vec2.magSq(a), b * b, tolerance);
   }

   /**
    * Tests to see if two vectors are parallel.
    *
    * @param a
    *           the left comparisand
    * @param b
    *           the right comparisand
    * @return the evaluation
    */
   public static boolean areParallel ( 
      final Vec2 a, 
      final Vec2 b ) {

      return a.x * b.y - a.y * b.x == 0.0f;
   }

   /**
    * Returns to a vector with a negative value on the y axis,
    * (0.0, -1.0) .
    *
    * @param target
    *           the output vector
    * @return the back vector
    */
   public static Vec2 back ( final Vec2 target ) {

      return target.set(0.0f, -1.0f);
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
   public static Vec2 bezierPoint (
         final Vec2 ap0, 
         final Vec2 cp0,
         final Vec2 cp1, 
         final Vec2 ap1,
         final float step, 
         final Vec2 target ) {

      if (step <= 0.0f) {
         return target.set(ap0);
      } else if (step >= 1.0f) {
         return target.set(ap1);
      }

      final float u = 1.0f - step;
      float tcb = step * step;
      float ucb = u * u;
      // final float usq3t = ucb * 3.0f * step;
      // final float tsq3u = tcb * 3.0f * u;
      final float usq3t = ucb * (step + step + step);
      final float tsq3u = tcb * (u + u + u);
      ucb *= u;
      tcb *= step;

      return target.set(
            ap0.x * ucb + cp0.x * usq3t + cp1.x * tsq3u + ap1.x * tcb,
            ap0.y * ucb + cp0.y * usq3t + cp1.y * tsq3u + ap1.y * tcb);
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
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    */
   public static Vec2 bezierTangent (
         final Vec2 ap0, 
         final Vec2 cp0,
         final Vec2 cp1, 
         final Vec2 ap1,
         final float step, 
         final Vec2 target ) {

      if (step <= 0.0f) {
         return Vec2.sub(cp0, ap0, target);
      } else if (step >= 1.0f) {
         return Vec2.sub(ap1, cp1, target);
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
                  + (ap1.y - cp1.y) * tsq3);
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
    * @see Vec2#bezierTangent(Vec2, Vec2, Vec2, Vec2, float,
    *      Vec2)
    */
   public static Vec2 bezierTanUnit (
         final Vec2 ap0, 
         final Vec2 cp0,
         final Vec2 cp1, 
         final Vec2 ap1,
         final float step, 
         final Vec2 target ) {

      Vec2.bezierTangent(ap0, cp0, cp1, ap1, step, target);

      final float mSq = Vec2.magSq(target);
      if (mSq == 0.0f) {
         return target.reset();
      }

      final float mInv = 1.0f / (float) Math.sqrt(mSq);
      target.set(target.x * mInv, target.y * mInv);

      return target;
   }

   /**
    * Raises each component of the vector to the nearest
    * greater integer.
    *
    * @param v
    *           the input vector
    * @param target
    *           the target vector
    * @return the output
    * @see Utils#ceil(float)
    */
   public static Vec2 ceil ( final Vec2 v, final Vec2 target ) {

      return target.set(
            Utils.ceil(v.x),
            Utils.ceil(v.y));
   }

   /**
    * Clamps a vector to a range within the lower- and
    * upper-bound.
    *
    * @param v
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
   public static Vec2 clamp ( 
      final Vec2 v, 
      final Vec2 lowerBound,
         final Vec2 upperBound, 
         final Vec2 target ) {

      return target.set(
            Utils.clamp(v.x, lowerBound.x, upperBound.x),
            Utils.clamp(v.x, lowerBound.y, upperBound.y));
   }

   /**
    * Clamps the vector to a range in [0.0, 1.0]. Useful for
    * working with uv coordinates.
    *
    * @param v
    *           the input vector
    * @param target
    *           the output vector
    * @return the clamped vector
    * @see Utils#clamp01(float)
    */
   public static Vec2 clamp01 ( final Vec2 v, final Vec2 target ) {

      return target.set(
            Utils.clamp01(v.x),
            Utils.clamp01(v.y));
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
   public static Vec2 diff ( 
      final Vec2 a, 
      final Vec2 b,
         final Vec2 target ) {

      return target.set(
            Utils.diff(a.x, b.x),
            Utils.diff(a.y, b.y));
   }

   /**
    * Finds the Euclidean distance between two vectors.
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @return the distance
    * @see Vec2#distEuclidean(Vec2, Vec2)
    */
   public static float dist ( final Vec2 a, final Vec2 b ) {

      return Vec2.distEuclidean(a, b);
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
   public static float distChebyshev ( final Vec2 a, final Vec2 b ) {

      return Utils.max(
            Utils.diff(a.x, b.x),
            Utils.diff(a.y, b.y));
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
    * @see Vec2#distSq(Vec2, Vec2)
    */
   public static float distEuclidean ( final Vec2 a, final Vec2 b ) {

      return (float) Math.sqrt(Vec2.distSq(a, b));
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
   public static float distManhattan ( final Vec2 a, final Vec2 b ) {

      return Utils.diff(a.x, b.x)
            + Utils.diff(a.y, b.y);
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
    * @see Vec2#distEuclidean(Vec2, Vec2)
    * @see Vec2#distManhattan(Vec2, Vec2)
    */
   public static float distMinkowski ( 
      final Vec2 a, 
      final Vec2 b,
         final float c ) {

      if (c == 0.0f) {
         return 0.0f;
      }
      return (float) Math.pow(
            Math.pow(Utils.diff(a.x, b.x), c)
                  + Math.pow(Utils.diff(a.y, b.y), c),
            1.0f / c);
   }

   /**
    * Finds the Euclidean distance squared between two vectors.
    * Equivalent to subtracting one vector from the other, then
    * finding the dot product of the difference.
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @return the distance squared
    * @see Vec2#distEuclidean(Vec2, Vec2)
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    */
   public static float distSq ( final Vec2 a, final Vec2 b ) {

      final float xDist = b.x - a.x;
      final float yDist = b.y - a.y;
      return xDist * xDist + yDist * yDist;
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
   public static Vec2 div ( 
      final float a, 
      final Vec2 b,
         final Vec2 target ) {

      return target.set(
            Utils.div(a, b.x),
            Utils.div(a, b.y));
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
   public static Vec2 div ( 
      final Vec2 a, 
      final float b,
         final Vec2 target ) {

      if (b == 0.0f) {
         return target.reset();
      }
      final float denom = 1.0f / b;
      return target.set(
            a.x * denom,
            a.y * denom);
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
   public static Vec2 div ( final Vec2 a, final Vec2 b,
         final Vec2 target ) {

      return target.set(
            Utils.div(a.x, b.x),
            Utils.div(a.y, b.y));
   }

   /**
    * Finds the dot product of two vectors by summing the
    * products of their corresponding components. <em>a</em>
    * \u00b7 <em>b</em> := <em>a<sub>x</sub> b<sub>x</sub></em>
    * + <em>a<sub>y</sub> b<sub>y</sub></em><br>
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
   public static float dot ( final Vec2 a, final Vec2 b ) {

      return a.x * b.x + a.y * b.y;
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
   public static Vec2 floor ( final Vec2 v, final Vec2 target ) {

      return target.set(
            Utils.floor(v.x),
            Utils.floor(v.y));
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
   public static Vec2 fmod ( final float a, final Vec2 b,
         final Vec2 target ) {

      return target.set(
            Utils.fmod(a, b.x),
            Utils.fmod(a, b.y));
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
   public static Vec2 fmod ( final Vec2 a, final float b,
         final Vec2 target ) {

      if (b == 0.0f) {
         return target.set(a);
      }
      return target.set(
            a.x % b,
            a.y % b);
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
   public static Vec2 fmod ( final Vec2 a, final Vec2 b,
         final Vec2 target ) {

      return target.set(
            Utils.fmod(a.x, b.x),
            Utils.fmod(a.x, b.y));
   }

   /**
    * Returns to a vector with a positive value on the y axis,
    * (0.0, 1.0) .
    *
    * @param target
    *           the output vector
    * @return the forward vector
    */
   public static Vec2 forward ( final Vec2 target ) {

      return target.set(0.0f, 1.0f);
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
   public static Vec2 fract ( final Vec2 v, final Vec2 target ) {

      return target.set(
            Utils.fract(v.x),
            Utils.fract(v.y));
   }

   /**
    * Creates a vector from polar coordinates: (1) theta,
    * \u03b8, an angle in radians, the vector's heading; (2)
    * rho, \u03c1, a radius, the vector's magnitude. Uses the
    * formula<br>
    * <br>
    * ( \u03c1 cos ( \u03b8 ),<br>
    * \u03c1 sin ( \u03b8 ) ).
    *
    * @param heading
    *           the angle in radians
    * @param radius
    *           the radius
    * @param target
    *           the output vector
    * @return the vector
    * @see Math#cos(double)
    * @see Math#sin(double)
    */
   public static Vec2 fromPolar ( final float heading, final float radius,
         final Vec2 target ) {

      return target.set(
            (float) Math.cos(heading) * radius,
            (float) Math.sin(heading) * radius);
   }

   /**
    * Creates a vector with a magnitude of 1.0 from an angle,
    * such that the vector is on the unit circle.
    *
    * @param heading
    *           the angle in radians
    * @param target
    *           the output vector
    * @return the vector
    */
   public static Vec2 fromPolar ( final float heading, final Vec2 target ) {

      return target.set(
            (float) Math.cos(heading),
            (float) Math.sin(heading));
   }

   /**
    * Gets the string representation of the default Vec2
    * comparator.
    *
    * @return the string
    */
   public static String getComparatorString () {

      return Vec2.COMPARATOR.toString();
   }

   /**
    * Gets the string representation of the default Vec2 easing
    * function.
    *
    * @return the string
    */
   public static String getEasingString () {

      return Vec2.EASING.toString();
   }

   /**
    * Finds the vector's heading. Defaults to headingSigned.
    *
    * @param v
    *           the input vector
    * @return the angle in radians
    * @see Vec2#headingSigned(Vec2)
    * @see Vec2#headingUnsigned(Vec2)
    */
   public static float heading ( final Vec2 v ) {

      return Vec2.headingSigned(v);
   }

   /**
    * Finds the vector's heading in the range [-\u03c0,
    * \u03c0].
    *
    * @param v
    *           the input vector
    * @return the angle in radians
    * @see Math#atan2(double, double)
    * @see Vec2#headingUnsigned(Vec2)
    */
   public static float headingSigned ( final Vec2 v ) {

      return Utils.atan2(v.y, v.x);
   }

   /**
    * Finds the vector's heading in the range [0.0, \u03c4].
    *
    * @param v
    *           the input vector
    * @return the angle in radians
    * @see Vec2#headingSigned(Vec2)
    * @see Utils#modRadians(float)
    */
   public static float headingUnsigned ( final Vec2 v ) {

      return Utils.modRadians(Vec2.headingSigned(v));
   }

   /**
    * Tests to see if all the vector's components are non-zero.
    * Useful when testing valid dimensions (width and depth)
    * stored in vectors.
    *
    * @param v
    *           the input vector
    * @return the evaluation
    */
   public static boolean isNonZero ( final Vec2 v ) {

      return v.x != 0.0f && v.y != 0.0f;
   }

   /**
    * Tests to see if the vector is on the unit circle, i.e.,
    * has a magnitude of approximately 1.0.
    *
    * @param v
    *           the input vector
    * @return the evaluation
    * @see Utils#approxFast(float, float)
    * @see Vec2#dot(Vec2, Vec2)
    */
   public static boolean isUnit ( final Vec2 v ) {

      return Utils.approxFast(Vec2.magSq(v), 1.0f);
   }

   /**
    * Tests to see if all the vector's components are zero.
    * Useful when safeguarding against invalid directions.
    *
    * @param a
    *           the input vector
    * @return the evaluation
    */
   public static boolean isZero ( final Vec2 a ) {

      return a.x == 0.0f && a.y == 0.0f;
   }

   /**
    * Returns a vector with a negative value on the x axis,
    * (-1.0, 0.0).
    *
    * @param target
    *           the output vector
    * @return the left vector
    */
   public static Vec2 left ( final Vec2 target ) {

      return target.set(-1.0f, 0.0f);
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
    * @see Vec2#dot(Vec2, Vec2)
    * @see Vec2#rescale(Vec2, float, Vec2)
    */
   public static Vec2 limit ( final Vec2 v, final float limit,
         final Vec2 target ) {

      if (Vec2.magSq(v) > limit * limit) {
         return Vec2.rescale(v, limit, target);
      }
      return target.set(v);
   }

   /**
    * Finds the length, or magnitude, of a vector, |<em>a</em>|
    * . Also referred to as the radius when using polar
    * coordinates. Uses the formula \u221a <em>a</em> \u00b7
    * <em>a</em> . Where possible, use magSq or dot to avoid
    * the computational cost of the square-root.
    *
    * @param v
    *           the input vector
    * @return the magnitude
    * @see Vec2#dot(Vec2, Vec2)
    * @see Math#sqrt(double)
    * @see Vec2#magSq(Vec2)
    */
   public static float mag ( final Vec2 v ) {

      return (float) Math.sqrt(Vec2.magSq(v));
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
    * @see Vec2#dot(Vec2, Vec2)
    * @see Vec2#mag(Vec2)
    */
   public static float magSq ( final Vec2 v ) {

      return v.x * v.x + v.y * v.y;
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
   public static Vec2 map ( final Vec2 v, final Vec2 lbOrigin,
         final Vec2 ubOrigin, final Vec2 lbDest, final Vec2 ubDest,
         final Vec2 target ) {

      return target.set(
            Utils.map(v.x, lbOrigin.x, ubOrigin.x, lbDest.x, ubDest.x),
            Utils.map(v.y, lbOrigin.y, ubOrigin.y, lbDest.y, ubDest.y));
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
   public static Vec2 max ( final Vec2 a, final float upperBound,
         final Vec2 target ) {

      return target.set(
            Utils.max(a.x, upperBound),
            Utils.max(a.y, upperBound));
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
   public static Vec2 max ( final Vec2 a, final Vec2 upperBound,
         final Vec2 target ) {

      return target.set(
            Utils.max(a.x, upperBound.x),
            Utils.max(a.y, upperBound.y));
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
   public static Vec2 min ( final Vec2 a, final float lowerBound,
         final Vec2 target ) {

      return target.set(
            Utils.min(a.x, lowerBound),
            Utils.min(a.y, lowerBound));
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
   public static Vec2 min ( final Vec2 a, final Vec2 lowerBound,
         final Vec2 target ) {

      return target.set(
            Utils.min(a.x, lowerBound.x),
            Utils.min(a.y, lowerBound.y));
   }

   /**
    * Mixes two vectors together by a step in [0.0, 1.0]. Uses
    * the easing function that is a static field belonging to
    * the Vec2 class.
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
    * @see Vec2#EASING
    */
   public static Vec2 mix ( final Vec2 origin, final Vec2 dest,
         final float step,
         final Vec2 target ) {

      return Vec2.EASING.apply(origin, dest, step, target);
   }

   /**
    * Mixes two vectors together by a step in [0.0, 1.0] with
    * the help of an easing function.
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
   public static Vec2 mix ( final Vec2 origin, final Vec2 dest,
         final float step,
         final Vec2 target, final AbstrEasing easingFunc ) {

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
   public static Vec2 mod ( final float a, final Vec2 b,
         final Vec2 target ) {

      return target.set(
            Utils.mod(a, b.x),
            Utils.mod(a, b.y));
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
   public static Vec2 mod ( final Vec2 a, final float b,
         final Vec2 target ) {

      if (b == 0.0f) {
         return target.set(a);
      }
      return target.set(
            Utils.modUnchecked(a.x, b),
            Utils.modUnchecked(a.y, b));
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
   public static Vec2 mod ( final Vec2 a, final Vec2 b, final Vec2 target ) {

      return target.set(
            Utils.mod(a.x, b.x),
            Utils.mod(a.y, b.y));
   }

   /**
    * A specialized form of mod which subtracts the floor of
    * the vector from the vector. For Vec2s, useful for
    * managing texture coordinates in the range [0.0, 1.0].
    *
    * @param v
    *           the input vector
    * @param target
    *           the output vector
    * @return the result
    * @see Utils#mod1(float)
    */
   public static Vec2 mod1 ( final Vec2 v, final Vec2 target ) {

      return target.set(
            Utils.mod1(v.x),
            Utils.mod1(v.y));
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
   public static Vec2 mult ( final float a, final Vec2 b,
         final Vec2 target ) {

      return target.set(
            a * b.x,
            a * b.y);
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
   public static Vec2 mult ( final Vec2 a, final float b,
         final Vec2 target ) {

      return target.set(
            a.x * b,
            a.y * b);
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
   public static Vec2 mult (
      final Vec2 a,
      final Vec2 b,
      final Vec2 target ) {

      return target.set(
            a.x * b.x,
            a.y * b.y);
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
   public static Vec2 negate ( final Vec2 v, final Vec2 target ) {

      return target.set(-v.x, -v.y);
   }

   /**
    * Returns a vector with all components set to negative one.
    *
    * @param target
    *           the output vector
    * @return negative one
    */
   public static Vec2 negOne ( final Vec2 target ) {

      return target.set(-1.0f, -1.0f);
   }

   /**
    * Divides a vector by its magnitude, such that the new
    * magnitude is 1.0. <em>\u00e2</em> = <em>a</em> /
    * |<em>a</em>|. The result is a unit vector, as it lies on
    * the circumference of a unit circle.
    *
    * @param a
    *           the input vector
    * @param target
    *           the output vector
    * @return the unit vector
    * @see Vec2#div(Vec2, float, Vec2)
    * @see Vec2#mag(Vec2)
    */
   public static Vec2 normalize (
         final Vec2 a,
         final Vec2 target ) {

      // return Vec2.div(a, Vec2.mag(a), target);

      final float mSq = a.x * a.x + a.y * a.y;
      
      if(mSq == 0.0f) {
         return target.reset();
      }

      if(mSq == 1.0f) {
         return target.set(a);
      }

      final float mInv = (float)(1.0d / Math.sqrt(mSq));
      return target.set(
         a.x * mInv,
         a.y * mInv);
   }

   /**
    * Returns a vector with both components set to one.
    *
    * @param target
    *           the output vector
    * @return one
    */
   public static Vec2 one ( final Vec2 target ) {

      return target.set(1.0f, 1.0f);
   }

   /**
    * Finds the perpendicular of a vector. Defaults to
    * counter-clockwise rotation, such that the perpendicular
    * of ( 1.0, 0.0 ) is ( 0.0, 1.0 ) . The 2D counterpart to
    * the 3D vector's cross product.
    *
    * @param a
    *           the input vector
    * @param target
    *           the output vector
    * @return the perpendicular
    * @see Vec2#perpendicularCCW(Vec2, Vec2)
    * @see Vec3#cross(Vec3, Vec3, Vec3)
    */
   public static Vec2 perpendicular ( final Vec2 a, final Vec2 target ) {

      return Vec2.perpendicularCCW(a, target);
   }

   /**
    * Finds the perpendicular of a vector in the
    * counter-clockwise direction, such that
    * <ul>
    *
    * <li>perp ( right ) = forward, <br>
    * perp ( 1.0, 0.0 ) = ( 0.0, 1.0 )</li>
    *
    * <li>perp ( forward ) = left, <br>
    * perp ( 0.0, 1.0 ) = ( -1.0, 0.0 )</li>
    *
    * <li>perp ( left ) = back, <br>
    * perp ( -1.0, 0.0 ) = ( 0.0, -1.0 )</li>
    *
    * <li>perp ( back ) = right, <br>
    * perp ( 0.0, -1.0 ) = ( 1.0, 0.0 )</li>
    *
    * </ul>
    * In terms of the components, perp ( x, y ) = ( -y, x ) .
    *
    * @param a
    *           the input vector
    * @param target
    *           the output vector
    * @return the perpendicular
    */
   public static Vec2 perpendicularCCW ( final Vec2 a, final Vec2 target ) {

      return target.set(-a.y, a.x);
   }

   /**
    * Finds the perpendicular of a vector in the clockwise
    * direction, such that
    * <ul>
    *
    * <li>perp ( right ) = back, <br>
    * perp( 1.0, 0.0 ) = ( 0.0, -1.0 )</li>
    *
    * <li>perp ( back ) = left, <br>
    * perp( 0.0, -1.0 ) = ( -1.0, 0.0 )</li>
    *
    * <li>perp ( left ) = forward, <br>
    * perp( -1.0, 0.0 ) = ( 0.0, 1.0 )</li>
    *
    * <li>perp ( forward ) = right, <br>
    * perp( 0.0, 1.0 ) = ( 1.0, 0.0 )</li>
    *
    * </ul>
    * In terms of the components, perp ( x, y ) = ( y, -x ) .
    *
    * @param a
    *           the input vector
    * @param target
    *           the output vector
    * @return the perpendicular
    */
   public static Vec2 perpendicularCW ( final Vec2 a, final Vec2 target ) {

      return target.set(a.y, -a.x);
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
   public static Vec2 pow (
      final float a,
      final Vec2 b,
      final Vec2 target ) {

      return target.set(
            (float) Math.pow(a, b.x),
            (float) Math.pow(a, b.y));
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
   public static Vec2 pow (
      final Vec2 a,
      final float b,
      final Vec2 target ) {

      return target.set(
            (float) Math.pow(a.x, b),
            (float) Math.pow(a.y, b));
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
   public static Vec2 pow (
      final Vec2 a,
      final Vec2 b,
      final Vec2 target ) {

      return target.set(
            (float) Math.pow(a.x, b.x),
            (float) Math.pow(a.y, b.y));
   }

   /**
    * Projects one vector onto another. Defined as<br>
    * <br>
    * proj ( <em>a</em>, <em>b</em> ) := <em>b</em> (
    * <em>a</em> \u00b7 <em>b</em> / <em>b</em> \u00b7
    * <em>b</em> )<br>
    * <br>
    * Returns the scalar projection, clamped to [0.0, 1.0].
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the vector projection
    * @return the scalar projection
    * @see Vec2#dot(Vec2, Vec2)
    * @see Vec2#mult(Vec2, float, Vec2)
    * @see Utils#clamp01(float)
    */
   public static float project (
      final Vec2 a,
      final Vec2 b,
      final Vec2 target ) {

      final float bSq = Vec2.magSq(b);
      if (bSq != 0.0f) {
         final float dAbBb = Vec2.dot(a, b) / bSq;
         Vec2.mult(b, dAbBb, target);
         return Utils.clamp01(dAbBb);
      }
      target.reset();
      return 0.0f;
   }

   /**
    * Generates a vector with a random heading and a magnitude
    * of 1.0, such that it lies on the unit circle.
    *
    * @param rng
    *           the random number generator
    * @param target
    *           the output vector
    * @return the random vector
    * @see Vec2#randomPolar(Random, float, float, Vec2)
    */
   public static Vec2 random (
      final Random rng,
      final Vec2 target ) {

      return Vec2.randomPolar(rng, 1.0f, 1.0f, target);
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
   public static Vec2 randomCartesian (
      final Random rng,
      final float lowerBound,
      final float upperBound,
      final Vec2 target ) {

      return target.set(rng.uniform(lowerBound, upperBound),
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
   public static Vec2 randomCartesian (
      final Random rng,
      final Vec2 lowerBound,
      final Vec2 upperBound,
      final Vec2 target ) {

      return target.set(
            rng.uniform(lowerBound.x, upperBound.x),
            rng.uniform(lowerBound.y, upperBound.y));
   }

   /**
    * Creates a vector at a random heading and radius.
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
   public static Vec2 randomPolar ( final Random rng,
         final float rhoMin, final float rhoMax,
         final Vec2 target ) {

      final float x = rng.uniform(-1.0f, 1.0f);
      final float y = rng.uniform(-1.0f, 1.0f);

      final float mag = x * x + y * y;
      if (mag == 0.0f) {
         return target.reset();
      }

      final float rho = rng.uniform(rhoMin, rhoMax);
      final float rhoNorm = (float) (rho / Math.sqrt(mag));

      return target.set(
            x * rhoNorm,
            y * rhoNorm);
   }

   /**
    * Creates a vector with a magnitude of 1.0 at a random
    * heading, such that it lies on the unit circle.
    *
    * @param rng
    *           the random number generator
    * @param target
    *           the output vector
    * @return the random vector
    * @see Vec2#randomPolar(Random, float, float, Vec2)
    */
   public static Vec2 randomPolar ( final Random rng, final Vec2 target ) {

      return Vec2.randomPolar(rng, 1.0f, 1.0f, target);
   }

   /**
    * Reflects an incident vector off a normal vector. Uses the
    * formula <br>
    * <br>
    * <em>i</em> - 2.0 (<em>n</em> \u00b7 <em>i</em>)
    * <em>n</em><br>
    * <br>
    *
    * Ensures that the normal is of unit length.
    *
    * @param incident
    *           the incident vector
    * @param normal
    *           the normal vector
    * @param target
    *           the output vector
    * @return the reflected vector
    * @see Vec2#dot(Vec2, Vec2)
    * @see Math#sqrt(double)
    */
   public static Vec2 reflect (
         final Vec2 incident,
         final Vec2 normal,
         final Vec2 target ) {

      // TODO: Needs testing.

      final float nMSq = Vec2.magSq(normal);
      if (nMSq == 0.0f) {
         return target.reset();
      }

      final float scalar = 2.0f * Vec2.dot(normal, incident)
            / (float) Math.sqrt(nMSq);
      return target.set(
            incident.x - scalar * normal.x,
            incident.y - scalar * normal.y);
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
    * @see Vec2#dot(Vec2, Vec2)
    * @see Math#sqrt(double)
    */
   public static Vec2 refract (
         final Vec2 incident,
         final Vec2 normal,
         final float eta,
         final Vec2 target ) {

      // TODO: Needs testing.

      final float nDotI = Vec2.dot(normal, incident);
      final float k = 1.0f - eta * eta * (1.0f - nDotI * nDotI);
      if (k < 0.0f) {
         return target.reset();
      }
      final float scalar = eta * nDotI + (float) Math.sqrt(k);
      return target.set(
            eta * incident.x - normal.x * scalar,
            eta * incident.y - normal.y * scalar);
   }

   /**
    * Subtracts the projection of vector a onto vector b from a
    * . Implicitly creates a new vector.
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @param target
    *           the output vector
    * @return the rejection
    * @see Vec2#reject(Vec2, Vec2, Vec2, Vec2)
    */
   public static float reject ( final Vec2 a, final Vec2 b,
         final Vec2 target ) {

      return Vec2.reject(a, b, target, new Vec2());
   }

   /**
    * Subtracts the projection of vector a onto vector b from a
    * .
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @param target
    *           the output vector
    * @param projected
    *           the projection
    * @return the rejection
    * @see Vec2#project(Vec2, Vec2, Vec2)
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    */
   public static float reject ( final Vec2 a, final Vec2 b,
         final Vec2 target,
         final Vec2 projected ) {

      final float fac = Vec2.project(a, b, target);
      Vec2.sub(a, projected, target);
      return fac;
   }

   /**
    * Normalizes a vector, then multiplies it by a scalar, in
    * effect setting its magnitude to that scalar. If the
    * scalar is zero, the output vector is reset. Implicitly
    * creates a new vector.
    *
    * @param v
    *           the vector
    * @param scalar
    *           the scalar
    * @param target
    *           the output vector
    * @return the rescaled vector
    * @see Vec2#rescale(Vec2, float, Vec2, Vec2)
    */
   public static Vec2 rescale ( final Vec2 v, final float scalar,
         final Vec2 target ) {
      
      // return Vec2.rescale(v, scalar, target, new Vec2());

      if(scalar == 0.0f) {
         return target.reset();
      }

      float mSq = Vec2.magSq(v);
      
      if(mSq == 0.0f) {
         return target.reset();
      }

      if(mSq == 1.0f) {
         return Vec2.mult(v, scalar, target);
      }

      return Vec2.mult(v, (float)(scalar / Math.sqrt(mSq)), target);
   }

   /**
    * Normalizes a vector, then multiplies it by a scalar, in
    * effect setting its magnitude to that scalar. If the
    * scalar is zero, the output vector is reset.
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
    * @see Vec2#normalize(Vec2, Vec2)
    * @see Vec2#mult(Vec2, float, Vec2)
    */
   public static Vec2 rescale ( final Vec2 v, final float scalar,
         final Vec2 target, final Vec2 normalized ) {

      if (scalar == 0.0f) {
         return target.reset();
      }
      Vec2.normalize(v, normalized);
      return Vec2.mult(normalized, scalar, target);
   }

   /**
    * Returns to a vector with a positive value on the x axis,
    * (1.0, 0.0) .
    *
    * @param target
    *           the output vector
    * @return the right vector
    */
   public static Vec2 right ( final Vec2 target ) {

      return target.set(1.0f, 0.0f);
   }

   /**
    * Rotates a vector around the x axis by an angle in
    * radians.
    *
    * For 2D vectors, this scales the y component by cosine of
    * the angle.
    *
    * @param v
    *           the input vector
    * @param radians
    *           the angle in radians
    * @param target
    *           the output vector
    * @return the rotated vector
    * @see Math#cos(double)
    */
   public static Vec2 rotateX (
      final Vec2 v,
      final float radians,
      final Vec2 target ) {

      return target.set(
            v.x,
            v.y * (float) Math.cos(radians));
   }

   /**
    * Rotates a vector around the y axis by an angle in
    * radians.
    *
    * For 2D vectors, this scales the x component by cosine of
    * the angle.
    *
    * @param v
    *           the input vector
    * @param radians
    *           the angle in radians
    * @param target
    *           the output vector
    * @return the rotated vector
    * @see Math#cos(double)
    */
   public static Vec2 rotateY (
      final Vec2 v,
      final float radians,
      final Vec2 target ) {

      return target.set(
            v.x * (float) Math.cos(radians),
            v.y);
   }

   /**
    * Rotates a vector around the z axis by an angle in
    * radians.
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
    */
   public static Vec2 rotateZ (
      final Vec2 v,
      final float radians,
      final Vec2 target ) {

      final float cosa = (float) Math.cos(radians);
      final float sina = (float) Math.sin(radians);

      return target.set(
            cosa * v.x - sina * v.y,
            cosa * v.y + sina * v.x);
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
   public static Vec2 round ( final Vec2 v, final Vec2 target ) {

      return target.set(
            Math.round(v.x),
            Math.round(v.y));
   }

   /**
    * Sets the comparator function by which collections of
    * vectors are compared.
    *
    * @param comparator
    *           the comparator
    */
   public static void setComparator (
         final Comparator < Vec2 > comparator ) {

      if (comparator != null) {
         Vec2.COMPARATOR = comparator;
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
         Vec2.EASING = easing;
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
    * @return the result
    */
   public static Vec2 sub (
      final Vec2 a,
      final Vec2 b,
      final Vec2 target ) {

      return target.set(
            a.x - b.x,
            a.y - b.y);
   }

   /**
    * Subtracts the right from the left vector and then
    * normalizes the difference.
    *
    * @param a
    *           the left vector
    * @param b
    *           the right vector
    * @param target
    *           the output vector
    * @return the normalized difference
    */
   public static Vec2 subNorm (
      final Vec2 a, 
      final Vec2 b,
      final Vec2 target ) {

      final float dx = a.x - b.x;
      final float dy = a.y - b.y;

      final float mSq = dx * dx + dy * dy;
      if (mSq == 0.0f) {
         return target.reset();
      }

      final float mInv = (float) (1.0d / Math.sqrt(mSq));
      return target.set(
            dx * mInv,
            dy * mInv);
   }

   /**
    * Subtracts the right from the left vector and then
    * normalizes the difference.
    *
    * @param a
    *           the left vector
    * @param b
    *           the right vector
    * @param target
    *           the output vector
    * @param dir
    *           the unnormalized direction
    * @return the normalized difference
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    * @see Vec2#normalize(Vec2, Vec2)
    */
   public static Vec2 subNorm (
         final Vec2 a,
         final Vec2 b,
         final Vec2 target,
         final Vec2 dir ) {

      Vec2.sub(a, b, dir);
      Vec2.normalize(dir, target);
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
   public static Vec2 trunc ( final Vec2 v, final Vec2 target ) {

      return target.set(
            (int) v.x,
            (int) v.y);
   }

   /**
    * Returns a vector representing the center of the UV
    * coordinate system, (0.5, 0.5) .
    *
    * @param target
    *           the output vector
    * @return the uv center
    */
   public static Vec2 uvCenter ( final Vec2 target ) {

      return target.set(0.5f, 0.5f);
   }

   /**
    * Returns a vector with all components set to 0.0 .
    *
    * @param target
    *           the output vector
    * @return the zero vector
    */
   public static Vec2 zero ( final Vec2 target ) {

      return target.set(0.0f, 0.0f);
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
    * The default vector constructor.
    */
   public Vec2 () {

      super(2);
   }

   /**
    * Constructs a vector from boolean values.
    *
    * @param x
    *           the x component
    * @param y
    *           the y component
    */
   public Vec2 (
         final boolean x,
         final boolean y ) {

      super(2);
      this.set(x, y);
   }

   /**
    * Constructs a vector from float values.
    *
    * @param x
    *           the x component
    * @param y
    *           the y component
    */
   public Vec2 (
         final float x,
         final float y ) {

      super(2);
      this.set(x, y);
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
    * @see Float#parseFloat(String)
    */
   public Vec2 (
         final String xstr,
         final String ystr ) {

      super(2);
      this.set(xstr, ystr);
   }

   /**
    * Constructs a vector from a source vector's components.
    *
    * @param v
    *           the source vector
    */
   public Vec2 ( final Vec2 v ) {

      super(2);
      this.set(v);
   }

   /**
    * Returns a new vector with this vector's components.
    * Java's cloneable interface is problematic; use set or a
    * copy constructor instead.
    *
    * @return a new vector
    * @see Vec2#set(Vec2)
    * @see Vec2#Vec2(Vec2)
    */
   @Override
   public Vec2 clone () {

      return new Vec2(this.x, this.y);
   }

   /**
    * Returns -1 when this vector is less than the comparisand;
    * 1 when it is greater than; 0 when the two are 'equal'.
    * The implementation of this method allows collections of
    * vectors to be sorted. This depends upon the static
    * comparator of the Vec2 class, which can be changed.
    *
    * @param v
    *           the comparisand
    * @return the numeric code
    * @see Vec2#COMPARATOR
    */
   @Override
   public int compareTo ( final Vec2 v ) {

      return Vec2.COMPARATOR.compare(this, v);
   }

   /**
    * Tests this vector for equivalence with another object.
    *
    * @param obj
    *           the object
    * @return the equivalence
    * @see Vec2#equals(Vec2)
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
      return this.equals((Vec2) obj);
   }

   /**
    * Simulates bracket subscript access in an array. When the
    * provided index is 1 or -1, returns y; 0 or -2, x.
    *
    * @param index
    *           the index
    * @return the component at that index
    */
   @Override
   public float get ( final int index ) {

      switch (index) {
         case 0:
         case -2:
            return this.x;
         case 1:
         case -1:
            return this.y;
         default:
            return 0.0f;
      }
   }

   /**
    * Returns a hash code for this vector based on its x and y
    * components.
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
      return result;
   }

   /**
    * Returns an iterator for this vector, which allows its
    * components to be accessed in an enhanced for-loop.
    *
    * @return the iterator
    */
   @Override
   public V2Iterator iterator () {

      return new V2Iterator(this);
   }

   /**
    * Resets this vector to an initial state, ( 0.0, 0.0 ) .
    *
    * @return this vector
    */
   @Chainable
   public Vec2 reset () {

      this.set(0.0f, 0.0f);
      return this;
   }

   /**
    * Sets the components of this vector from booleans, where
    * false is 0.0 and true is 1.0 .
    *
    * @param x
    *           the x component
    * @param y
    *           the y component
    * @return this vector
    * @see Utils#toFloat(boolean)
    */
   @Chainable
   public Vec2 set ( final boolean x, final boolean y ) {

      this.x = Utils.toFloat(x);
      this.y = Utils.toFloat(y);
      return this;
   }

   /**
    * Sets the components of this vector.
    *
    * @param x
    *           the x component
    * @param y
    *           the y component
    * @return this vector
    */
   @Chainable
   public Vec2 set ( final float x, final float y ) {

      this.x = x;
      this.y = y;
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
    * @return this vector
    * @see Float#parseFloat(String)
    */
   @Chainable
   public Vec2 set ( 
      final String xstr, 
      final String ystr ) {

      float x = 0.0f;
      float y = 0.0f;

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

      this.x = x;
      this.y = y;

      return this;
   }

   /**
    * Copies the components of the input vector to this vector.
    *
    * @param source
    *           the input vector
    * @return this vector
    */
   @Chainable
   public Vec2 set ( final Vec2 source ) {

      this.set(source.x, source.y);
      return this;
   }

   /**
    * Returns a float array of length 2 containing this
    * vector's components.
    *
    * @return the array
    */
   @Override
   public float[] toArray () {

      return new float[] { this.x, this.y };
   }

   /**
    * Returns a string representation of this vector as a space
    * separated value for use by OBJ formatting functions.
    *
    * @return the string
    */
   public String toObjString () {

      // return String.format("%.6f %.6f", this.x, this.y);

      final float xr = (float) (Math.round(this.x * 1000000) * 0.000001d);
      final float yr = (float) (Math.round(this.y * 1000000) * 0.000001d);
      return new StringBuilder()
            .append(xr)
            .append(" ")
            .append(yr)
            .toString();
   }

   /**
    * Returns a string representation of this vector according
    * to the string format.
    *
    * @return the string
    */
   @Override
   public String toString () {

      // return String.format("{ x: %+.4f, y: %+.4f }", this.x,
      // this.y);

      return toString(4);
   }

   public String toString ( final int places ) {

      return new StringBuilder(48)
            .append("{ x: ")
            .append(Utils.toFixed(this.x, places))
            .append(", y: ")
            .append(Utils.toFixed(this.y, places))
            .append(" }")
            .toString();
   }

   /**
    * Returns a string representation of this vector as a comma
    * separated value for use by SVG formatting functions.
    *
    * @return the string
    */
   public String toSvgString () {

      // return String.format("%.4f,%.4f", this.x, this.y);

      final float xr = (float) (Math.round(this.x * 10000) * 0.0001d);
      final float yr = (float) (Math.round(this.y * 10000) * 0.0001d);
      return new StringBuilder()
            .append(xr)
            .append(" ")
            .append(yr)
            .toString();
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
    * @see Vec2#approx(Vec2, Vec2)
    * @see Vec2#approx(Vec2, Vec2, float)
    */
   protected boolean equals ( final Vec2 v ) {

      if (Float.floatToIntBits(this.y) != Float.floatToIntBits(v.y)) {
         return false;
      }

      if (Float.floatToIntBits(this.x) != Float.floatToIntBits(v.x)) {
         return false;
      }

      return true;
   }
}
