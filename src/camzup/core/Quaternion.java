package camzup.core;

import java.util.Comparator;
import java.util.Iterator;

/**
 * A four-dimensional complex number. The <em>w</em>
 * component is the real number; the <em>x</em>, <em>y</em>
 * and <em>z</em> components are coefficients of the
 * imaginary <em>i</em>, <em>j</em> and <em>k</em>.
 * Discovered by William R. Hamilton in the formula
 * <em>i</em><sup>2</sup> = <em>j</em><sup>2</sup> =
 * <em>k</em><sup>2</sup> = <em>ijk</em> = -1.0 .
 * Quaternions with a magnitude of 1.0 are commonly used to
 * rotate 3D objects from one orientation to another.
 */
public class Quaternion extends Imaginary implements Comparable < Quaternion > {

   /**
    * An abstract class that may serve as an umbrella for any
    * custom comparators of Quaternions.
    */
   public static abstract class AbstrComparator
         implements Comparator < Quaternion > {

      /**
       * The default constructor.
       */
      public AbstrComparator () {

         super();
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
       *             the left comparisand
       * @param b
       *             the right comparisand
       * @return the comparison
       *
       */
      @Override
      public abstract int compare ( Quaternion a, Quaternion b );

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
    * An abstract class to facilitate the creation of
    * quaternion easing functions.
    */
   public static abstract class AbstrEasing
         implements Utils.EasingFuncObject < Quaternion > {

      /**
       * The default constructor.
       */
      public AbstrEasing () {

         super();
      }

      /**
       * A clamped interpolation between the origin and
       * destination. Normalizes the result even when the step is
       * out of bounds. Defers to an unclamped interpolation,
       * which is to be defined by sub-classes of this class.
       *
       * @param origin
       *                  the origin vector
       * @param dest
       *                  the destination vector
       * @param step
       *                  a factor in [0.0, 1.0]
       * @param target
       *                  the output quaternion
       * @return the eased quaternion
       */
      @Override
      public Quaternion apply ( final Quaternion origin, final Quaternion dest,
            final Float step, final Quaternion target ) {

         if (step <= 0.0f) {
            return Quaternion.normalize(origin, target);
         }
         if (step >= 1.0f) {
            return Quaternion.normalize(dest, target);
         }
         return this.applyUnclamped(origin, dest, step, target);
      }

      /**
       * The interpolation to be defined by subclasses.
       *
       * @param origin
       *                  the origin quaternion
       * @param dest
       *                  the destination quaternion
       * @param step
       *                  a factor in [0.0, 1.0]
       * @param target
       *                  the output quaternion
       * @return the eased quaternion
       */
      public abstract Quaternion applyUnclamped ( final Quaternion origin,
            final Quaternion dest, final float step, final Quaternion target );

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
    * Compares two quaternions based on the real component and
    * on a vector comparison for the imaginary component. The
    * vector comparator is to be provided to this comparator.
    */
   public static class ComparatorRealImag
         extends AbstrComparator {

      /**
       * The comparator for the imaginary vector.
       */
      public final Comparator < Vec3 > imagComparator;

      /**
       * Constructs a quaternion comparator with the supplied
       * vector comparator.
       *
       * @param imagComparator
       *                          the vector comparator
       */
      public ComparatorRealImag (
            final Comparator < Vec3 > imagComparator ) {

         super();
         this.imagComparator = imagComparator;
      }

      /**
       * Compares two quaternions by component.
       *
       * @param a
       *             the left comparisand
       * @param b
       *             the right comparisand
       * @return the evaluation
       */
      @Override
      public int compare ( final Quaternion a, final Quaternion b ) {

         return a.real > b.real ? 1
               : a.real < b.real ? -1
                     : this.imagComparator.compare(a.imag, b.imag);
      }
   }

   /**
    * An iterator, which allows a quaternion's components to be
    * accessed in an enhanced for loop. The 'w' component is
    * listed first.
    */
   public static final class IteratorWFirst
         extends QIterator {

      /**
       * The default constructor.
       *
       * @param quat
       *                the quaternion to iterate
       */
      public IteratorWFirst ( final Quaternion quat ) {

         super(quat);
      }

      /**
       * Gets the next value in the iterator.
       *
       * @return the value
       */
      @Override
      public Float next () {

         return this.quat.getWFirst(this.index++);
      }
   }

   /**
    * An iterator, which allows a quaternion's components to be
    * accessed in an enhanced for loop. The 'w' component is
    * listed last.
    */
   public static final class IteratorWLast
         extends QIterator {

      /**
       * The default constructor.
       *
       * @param quat
       *                the quaternion to iterate
       */
      public IteratorWLast ( final Quaternion quat ) {

         super(quat);
      }

      /**
       * Gets the next value in the iterator.
       *
       * @return the value
       */
      @Override
      public Float next () {

         return this.quat.getWLast(this.index++);
      }
   }

   /**
    * A functional class to ease between two quaternions by
    * linear interpolation (lerp).
    */
   public static class Lerp extends AbstrEasing {

      /**
       * The default constructor.
       */
      public Lerp () {

         super();
      };

      /**
       * Lerps between the origin and destination quaternion by a
       * step. Normalizes the result.
       *
       * @param origin
       *                  the origin quaternion
       * @param dest
       *                  the destination quaternion
       * @param step
       *                  a factor in [0.0, 1.0]
       * @param target
       *                  the output quaternion
       * @return the eased quaternion
       */
      @Override
      public Quaternion applyUnclamped (
            final Quaternion origin,
            final Quaternion dest,
            final float step,
            final Quaternion target ) {

         final double u = step;
         final double v = 1.0d - u;

         final Vec3 a = origin.imag;
         final Vec3 b = dest.imag;

         final double cw = u * origin.real + v * dest.real;
         final double cx = u * a.x + v * b.x;
         final double cy = u * a.y + v * b.y;
         final double cz = u * a.z + v * b.z;

         /**
          * Normalize.
          */
         final double mSq = cw * cw + cx * cx + cy * cy + cz * cz;

         if (mSq == 0.0d) {
            return target.reset();
         }

         if (mSq == 1.0d) {
            return target.set(
                  (float) cw,
                  (float) cx,
                  (float) cy,
                  (float) cz);
         }

         final double mInv = 1.0d / mSq;
         return target.set(
               (float) (cw * mInv),
               (float) (cx * mInv),
               (float) (cy * mInv),
               (float) (cz * mInv));
      }
   }

   /**
    * An iterator, which allows a quaternion's components to be
    * accessed in an enhanced for loop. This class is abstract,
    * and serves as a parent for other, more specific
    * iterators.
    */
   public static abstract class QIterator implements Iterator < Float > {

      /**
       * The current index.
       */
      protected int index = 0;

      /**
       * The quaternion being iterated over.
       */
      protected final Quaternion quat;

      /**
       * The default constructor.
       *
       * @param quat
       *                the quaternion to iterate
       */
      public QIterator ( final Quaternion quat ) {

         this.quat = quat;
      }

      /**
       * Tests to see if the iterator has another value.
       *
       * @return the evaluation
       */
      @Override
      public boolean hasNext () {

         return this.index < this.quat.size();
      }

      /**
       * Gets the next value in the iterator.
       *
       * @return the value
       */
      @Override
      public abstract Float next ();

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
    * A functional class to ease between two quaternions by
    * spherical linear interpolation (slerp).
    */
   public static class Slerp extends AbstrEasing {

      /**
       * The default constructor.
       */
      public Slerp () {

         super();
      }

      /**
       * Slerps between two quaternions by a step.
       *
       * @param origin
       *                  the origin quaternion
       * @param dest
       *                  the destination quaternion
       * @param step
       *                  a factor in [0.0, 1.0]
       * @param target
       *                  the output quaternion
       * @return the eased quaternion
       */
      @Override
      public Quaternion applyUnclamped (
            final Quaternion origin,
            final Quaternion dest,
            final float step,
            final Quaternion target ) {

         /**
          * Decompose origin vector.
          */
         final Vec3 ai = origin.imag;
         final float aw = origin.real;
         final float ax = ai.x;
         final float ay = ai.y;
         final float az = ai.z;

         /**
          * Decompose destination vector.
          */
         final Vec3 bi = dest.imag;
         float bw = dest.real;
         float bx = bi.x;
         float by = bi.y;
         float bz = bi.z;

         /**
          * Prevent passage of invalid value to acos, i.e. out of the
          * bounds [-1, 1] and mitigate against quaternions that are
          * not unit size by clamping the dot product.
          */
         float dotp = Utils.clamp01(aw * bw + ax * bx + ay * by + az * bz);

         /**
          * Flip values if the orientation is negative.
          */
         if (dotp < 0.0f) {
            bw = -bw;
            bx = -bx;
            by = -by;
            bz = -bz;
            dotp = -dotp;
         }

         /**
          * Java Math functions will promote values into doubles, so
          * for precision, they'll be used until function close.
          */
         final double theta = Math.acos(dotp);
         final double sinTheta = Math.sqrt(1.0d - dotp * dotp);

         /*
          * The complementary step, i.e., 1.0 - step.
          */
         double u = 0.0f;

         /*
          * The step.
          */
         double v = 0.0f;

         if (sinTheta > Utils.EPSILON) {
            final double sInv = 1.0d / sinTheta;
            u = Math.sin((1.0f - step) * theta) * sInv;
            v = Math.sin(step * theta) * sInv;
         } else {
            u = 1.0d - step;
            v = step;
         }

         /**
          * Unclamped linear interpolation.
          */
         final double cw = u * aw + v * bw;
         final double cx = u * ax + v * bx;
         final double cy = u * ay + v * by;
         final double cz = u * az + v * bz;

         /**
          * Normalize.
          */
         final double mSq = cw * cw + cx * cx + cy * cy + cz * cz;

         if (mSq == 0.0d) {
            return target.reset();
         }

         if (mSq == 1.0d) {
            return target.set(
                  (float) cw,
                  (float) cx,
                  (float) cy,
                  (float) cz);
         }

         final double mInv = 1.0d / mSq;
         return target.set(
               (float) (cw * mInv),
               (float) (cx * mInv),
               (float) (cy * mInv),
               (float) (cz * mInv));
      }
   }

   /**
    * The default Quaternion comparator, ComparatorRealImag .
    */
   private static Comparator < Quaternion > COMPARATOR = new ComparatorRealImag(
         new Vec3.ComparatorZYX());

   /**
    * The default easing function.
    */
   private static AbstrEasing EASING = new Slerp();

   /**
    * The unique identification for serialized classes.
    */
   private static final long serialVersionUID = -7363582058797081319L;

   /**
    * Adds two quaternions.
    *
    * @param a
    *                  the left operand
    * @param b
    *                  the right operand
    * @param target
    *                  the output quaternion
    * @return the sum
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */
   public static Quaternion add (
         final Quaternion a,
         final Quaternion b,
         final Quaternion target ) {

      Vec3.add(a.imag, b.imag, target.imag);
      target.real = a.real + b.real;
      return target;
   }

   /**
    * Adds two quaternions and normalizes the result.
    *
    * @param a
    *                  the left operand
    * @param b
    *                  the right operand
    * @param target
    *                  the target quaternion
    * @return the normalized sum
    * @see Vec3#add(Vec3, Vec3, Vec3)
    * @see Quaternion#dot(Quaternion, Quaternion)
    */
   public static Quaternion addNorm (
         final Quaternion a,
         final Quaternion b,
         final Quaternion target ) {

      Vec3.add(a.imag, b.imag, target.imag);
      target.real = a.real + b.real;

      final float mSq = Quaternion.magSq(target);
      if (mSq == 0.0f) {
         return target.reset();
      }

      final float mInv = (float) (1.0d / Math.sqrt(mSq));
      final Vec3 i = target.imag;
      return target.set(
            target.real * mInv,
            i.x * mInv,
            i.y * mInv,
            i.z * mInv);
   }

   /**
    * Adds two quaternions and normalizes the result.
    *
    * @param a
    *                  the left operand
    * @param b
    *                  the right operand
    * @param target
    *                  the target quaternion
    * @param sum
    *                  the sum
    * @return the normalized sum
    * @see Quaternion#add(Quaternion, Quaternion, Quaternion)
    * @see Quaternion#normalize(Quaternion, Quaternion)
    */
   public static Quaternion addNorm (
         final Quaternion a,
         final Quaternion b,
         final Quaternion target,
         final Quaternion sum ) {

      Quaternion.add(a, b, sum);
      Quaternion.normalize(sum, target);
      return target;
   }

   /**
    * Evaluates whether or not two quaternions approximate each
    * other.
    *
    * @param a
    *             the left comparisand
    * @param b
    *             the right comparisand
    * @return the evaluation
    * @see Utils#approxFast(float, float)
    * @see Vec3#approx(Vec3, Vec3)
    */
   public static boolean approx (
         final Quaternion a,
         final Quaternion b ) {

      return Utils.approxFast(a.real, b.real)
            && Vec3.approx(a.imag, b.imag);
   }

   /**
    * Evaluates whether or not two quaternions approximate each
    * other according to a tolerance.
    *
    * @param a
    *                     the left comparisand
    * @param b
    *                     the right comparisand
    * @param tolerance
    *                     the tolerance
    * @return the evaluation
    * @see Utils#approxFast(float, float)
    * @see Vec3#approx(Vec3, Vec3)
    */
   public static boolean approx (
         final Quaternion a,
         final Quaternion b,
         final float tolerance ) {

      return Utils.approxFast(a.real, b.real, tolerance)
            && Vec3.approx(a.imag, b.imag, tolerance);
   }

   /**
    * Returns the conjugate of the quaternion, where the
    * imaginary component is negated.<br>
    * <br>
    * <em>a</em>* = ( <em>a<sub>real</sub></em>,
    * -<em>a<sub>imag</sub></em> )
    *
    * @param quat
    *                  the input quaternion
    * @param target
    *                  the output quaternion
    * @return the conjugate
    * @see Vec3#negate(Vec3, Vec3)
    */
   public static Quaternion conj (
         final Quaternion quat,
         final Quaternion target ) {

      Vec3.negate(quat.imag, target.imag);
      target.real = quat.real;
      return target;
   }

   /**
    * Divides a scalar by a quaternion.
    *
    * @param a
    *                    the numerator
    * @param b
    *                    the denominator
    * @param target
    *                    the output quaternion
    * @param inverted
    *                    the inverse
    * @return the quotient
    */
   public static Quaternion div (
         final float a,
         final Quaternion b,
         final Quaternion target,
         final Quaternion inverted ) {

      // TODO: Test inlined version.

      Vec3 bi = b.imag;
      float bw = b.real;
      float bx = bi.x;
      float by = bi.y;
      float bz = bi.z;

      float bMSq = bw * bw + bx * bx + by * by + bz * bz;

      if (bMSq == 0.0f) {
         return target.reset();
      }

      float bwInv = bw;
      float bxInv = -bx;
      float byInv = -by;
      float bzInv = -bz;

      if (bMSq != 1.0f) {
         float bMSqInv = 1.0f / bMSq;
         bwInv *= bMSqInv;
         bxInv *= bMSqInv;
         byInv *= bMSqInv;
         bzInv *= bMSqInv;
      }

      return target.set(
            a * bwInv,
            a * bxInv,
            a * byInv,
            a * bzInv);
   }

   /**
    * Divides a scalar by a quaternion.
    *
    * @param a
    *                     the numerator
    * @param b
    *                     the denominator
    * @param target
    *                     the output quaternion
    * @param inverted
    *                     the inverse
    * @param conjugate
    *                     the conjugate
    * @return the quotient
    * @see Quaternion#inverse(Quaternion, Quaternion,
    *      Quaternion)
    * @see Quaternion#mult(float, Quaternion, Quaternion)
    */
   public static Quaternion div (
         final float a,
         final Quaternion b,
         final Quaternion target,
         final Quaternion inverted,
         final Quaternion conjugate ) {

      Quaternion.inverse(b, inverted, conjugate);
      Quaternion.mult(a, inverted, target);
      return target;
   }

   /**
    * Divides a quaternion by a scalar.
    *
    * @param a
    *                  the numerator
    * @param b
    *                  the denominator
    * @param target
    *                  the output quaternion
    * @return the quotient
    * @see Vec3#mult(Vec3, float, Vec3)
    */
   public static Quaternion div (
         final Quaternion a,
         final float b,
         final Quaternion target ) {

      if (b == 0.0f) {
         return target.reset();
      }

      final float bInv = (float) (1.0d / b);
      Vec3.mult(a.imag, bInv, target.imag);
      target.real = a.real * bInv;

      return target;
   }

   /**
    * Divides one quaternion by another.
    *
    * @param a
    *                  the numerator
    * @param b
    *                  the denominator
    * @param target
    *                  the output quaternion
    * @return the quotient
    */
   public static Quaternion div (
         final Quaternion a,
         final Quaternion b,
         final Quaternion target ) {

      // TODO: Test inlined version.

      Vec3 bi = b.imag;
      float bw = b.real;
      float bx = bi.x;
      float by = bi.y;
      float bz = bi.z;

      float bMSq = bw * bw + bx * bx + by * by + bz * bz;

      if (bMSq == 0.0f) {
         return target.reset();
      }

      float bwInv = bw;
      float bxInv = -bx;
      float byInv = -by;
      float bzInv = -bz;

      if (bMSq != 1.0f) {
         float bMSqInv = 1.0f / bMSq;
         bwInv *= bMSqInv;
         bxInv *= bMSqInv;
         byInv *= bMSqInv;
         bzInv *= bMSqInv;
      }

      final Vec3 ai = a.imag;
      final float aw = a.real;

      return target.set(
            aw * bwInv - ai.x * bxInv - ai.y * byInv - ai.z * bzInv,
            ai.x * bwInv + aw * bxInv + ai.y * bzInv - ai.z * byInv,
            ai.y * bwInv + aw * byInv + ai.z * bxInv - ai.x * bzInv,
            ai.z * bwInv + aw * bzInv + ai.x * byInv - ai.y * bxInv);
   }

   /**
    * Divides one quaternion by another. Equivalent to
    * multiplying the numerator and the inverse of the
    * denominator.
    *
    * @param a
    *                     the numerator
    * @param b
    *                     the denominator
    * @param target
    *                     the output quaternion
    * @param inverted
    *                     the inverse
    * @param conjugate
    *                     the conjugate
    * @return the quotient
    * @see Quaternion#inverse(Quaternion, Quaternion,
    *      Quaternion)
    * @see Quaternion#mult(Quaternion, Quaternion, Quaternion)
    */
   public static Quaternion div (
         final Quaternion a,
         final Quaternion b,
         final Quaternion target,
         final Quaternion inverted,
         final Quaternion conjugate ) {

      Quaternion.inverse(b, inverted, conjugate);
      Quaternion.mult(a, inverted, target);
      return target;
   }

   /**
    * Finds the dot product of two quaternions by summing the
    * products of their corresponding components.<br>
    * <br>
    * <em>a</em> \u00b7 <em>b</em> := <em>a<sub>real</sub>
    * b<sub>real</sub></em> + <em>a<sub>imag</sub></em> \u00b7
    * <em>b<sub>imag</sub></em><br>
    * <br>
    * The dot product of a quaternion with itself is equal to
    * its magnitude squared.
    *
    * @param a
    *             left operand
    * @param b
    *             right operand
    * @return the dot product
    * @see Vec3#dot(Vec3, Vec3)
    */
   public static float dot (
         final Quaternion a,
         final Quaternion b ) {

      return a.real * b.real + Vec3.dot(a.imag, b.imag);
   }

   /**
    * Creates a quaternion from three axes.
    *
    * @param right
    *                   the right axis
    * @param forward
    *                   the forward axis
    * @param up
    *                   the up axis
    * @param target
    *                   the output quaternion
    * @return the quaternion
    */
   public static Quaternion fromAxes (
         final Vec3 right,
         final Vec3 forward,
         final Vec3 up,
         final Quaternion target ) {

      final float rx = right.x;
      final float uy = up.y;
      final float fz = forward.z;

      final float w = (float) (Math.sqrt(
            Utils.max(0.0f, 1.0f + rx + uy + fz)) * 0.5d);

      float x = (float) (Math.sqrt(
            Utils.max(0.0f, 1.0f + rx - uy - fz)) * 0.5d);
      float y = (float) (Math.sqrt(
            Utils.max(0.0f, 1.0f - rx + uy - fz)) * 0.5d);
      float z = (float) (Math.sqrt(
            Utils.max(0.0f, 1.0f - rx - uy + fz)) * 0.5d);

      x *= Utils.sign(up.z - forward.y);
      y *= Utils.sign(forward.x - right.z);
      z *= Utils.sign(right.y - up.x);

      return target.set(w, x, y, z);
   }

   /**
    * Creates a quaternion from an axis and angle. Normalizes
    * the axis prior to calculating the quaternion.
    *
    * @param radians
    *                   the angle in radians
    * @param axis
    *                   the axis
    * @param target
    *                   the output quaternion
    * @return the quaternion
    */
   public static Quaternion fromAxisAngle (
         final float radians,
         final Vec3 axis,
         final Quaternion target ) {

      final float amSq = Vec3.magSq(axis);
      if (amSq == 0.0f) {
         return target.reset();
      }

      float nx = axis.x;
      float ny = axis.y;
      float nz = axis.z;

      if (amSq != 1.0f) {
         final float amInv = (float) (1.0d / Math.sqrt(amSq));
         nx *= amInv;
         ny *= amInv;
         nz *= amInv;
      }

      final double halfAngle = 0.5d * radians;
      final float sinHalf = (float) Math.sin(halfAngle);
      return target.set(
            (float) Math.cos(halfAngle),
            nx * sinHalf,
            ny * sinHalf,
            nz * sinHalf);
   }

   /**
    * Creates a quaternion with reference to two vectors. This
    * assumes that the two vectors are normalized.
    *
    * @param origin
    *                  the origin vector, normalized
    * @param dest
    *                  the destination vector, normalized
    * @param target
    *                  the output quaternion
    * @return the quaternion
    */
   public static Quaternion fromTo (
         final Vec3 origin,
         final Vec3 dest,
         final Quaternion target ) {

      /*
       * Check that vectors are not co-linear.
       */
      final float dot1 = Vec3.dot(origin, dest) + 1.0f;
      if (dot1 < Utils.EPSILON) {

         /**
          * Instead of crossing the vectors, which would result in
          * (0.0, 0.0, 0.0), try to crossing with orthonormal
          * vectors.
          */
         if (Utils.abs(origin.x) > Utils.abs(origin.z)) {
            target.set(0.0f, -origin.y, origin.x, 0.0f);
         } else {
            target.set(0.0f, 0.0f, -origin.z, origin.y);
         }
      } else {
         Vec3.cross(origin, dest, target.imag);
         target.real = dot1;
      }

      /**
       * Normalize quaternion.
       */
      final float mSq = Quaternion.magSq(target);
      if (mSq == 0.0f) {
         return target.reset();
      }

      /* target's real and imag have already been set. */
      if (mSq == 1.0f) {
         return target;
      }

      final Vec3 imag = target.imag;
      final float mInv = (float) (1.0d / Math.sqrt(mSq));
      return target.set(
            target.real * mInv,
            imag.x * mInv,
            imag.y * mInv,
            imag.z * mInv);
   }

   /**
    * Gets the string representation of the default Quaternion
    * comparator.
    *
    * @return the string
    */
   public static String getComparatorString () {

      return Quaternion.COMPARATOR.toString();
   }

   /**
    * Gets the string representation of the default Quaternion
    * easing function.
    *
    * @return the string
    */
   public static String getEasingString () {

      return Quaternion.EASING.toString();
   }

   /**
    * Gets the forward axis of the rotation.
    *
    * @param quat
    *                  the quaternion
    * @param target
    *                  the output vector
    * @return the forward axis
    */
   public static Vec3 getForward (
         final Quaternion quat,
         final Vec3 target ) {

      final Vec3 imag = quat.imag;
      final float w = quat.real;
      final float x = imag.x;
      final float y = imag.y;
      final float z = imag.z;

      return target.set(
            -z * w + x * y + y * x - w * z,
            w * w - z * z + y * y - x * x,
            x * w + w * x + y * z + z * y);
   }

   /**
    * Gets the right axis of the rotation.
    *
    * @param quat
    *                  the quaternion
    * @param target
    *                  the output vector
    * @return the right axis
    */
   public static Vec3 getRight (
         final Quaternion quat,
         final Vec3 target ) {

      final Vec3 imag = quat.imag;

      final float w = quat.real;
      final float x = imag.x;
      final float y = imag.y;
      final float z = imag.z;

      return target.set(
            w * w - y * y + x * x - z * z,
            z * w + w * z + x * y + y * x,
            -y * w + z * x + x * z - w * y);
   }

   /**
    * Gets the up axis of the rotation.
    *
    * @param quat
    *                  the quaternion
    * @param target
    *                  the output vector
    * @return the up axis
    */
   public static Vec3 getUp (
         final Quaternion quat,
         final Vec3 target ) {

      // TODO: Test that forward and up are not transposed...
      final Vec3 imag = quat.imag;

      final float w = quat.real;
      final float x = imag.x;
      final float y = imag.y;
      final float z = imag.z;

      return target.set(
            y * w + w * y + z * x + x * z,
            -x * w + y * z + z * y - w * x,
            w * w - x * x + z * z - y * y);
   }

   /**
    * Sets the target to the identity quaternion, ( 1.0, 0.0,
    * 0.0, 0.0 ).
    *
    * @param target
    *                  the output quaternion
    * @return the identity
    */
   public static Quaternion identity ( final Quaternion target ) {

      return target.set(1.0f, 0.0f, 0.0f, 0.0f);
   }

   /**
    * Returns the inverse of the quaternion, which is the
    * conjugate divided by the magnitude squared.<br>
    * <br>
    * <em>a</em><sup>-1</sup> = <em>a</em>* /
    * |<em>a</em>|<sup>2</sup><br>
    * <br>
    * If a quaternion is of unit length, its inverse is
    * equal to its conjugate.
    *
    * @param quat
    *                  the input quaternion
    * @param target
    *                  the output quaternion
    * @return the inverse
    */
   public static Quaternion inverse (
         final Quaternion quat,
         final Quaternion target ) {

      final float mSq = Quaternion.magSq(quat);
      
      if (mSq == 0.0f) {
         return target.reset();
      }

      final Vec3 i = quat.imag;
      if(mSq == 1.0f) {
         return target.set(quat.real, -i.x, -i.y, -i.z);
      }

      final float mSqInv = 1.0f / mSq;
      
      return target.set(
            quat.real * mSqInv,
            -i.x * mSqInv,
            -i.y * mSqInv,
            -i.z * mSqInv);
   }

   /**
    * Returns the inverse of the quaternion, which is the its
    * conjugate divided by its magnitude squared.<br>
    * <br>
    * <em>a</em><sup>-1</sup> = <em>a</em>* /
    * |<em>a</em>|<sup>2</sup>
    *
    * @param quat
    *                     the input quaternion
    * @param target
    *                     the output quaternion
    * @param conjugate
    *                     the conjugate
    * @return the inverse
    * @see Quaternion#conj(Quaternion, Quaternion)
    * @see Quaternion#dot(Quaternion, Quaternion)
    * @see Quaternion#div(Quaternion, float, Quaternion)
    */
   public static Quaternion inverse (
         final Quaternion quat,
         final Quaternion target,
         final Quaternion conjugate ) {

      Quaternion.conj(quat, conjugate);
      Quaternion.div(conjugate, Quaternion.magSq(quat), target);
      return target;
   }

   /**
    * Tests if the quaternion is the identity, where its real
    * component is 1.0 and its imaginary components are all
    * zero.
    *
    * @param quat
    *                the quaternion to test
    * @return the evaluation
    * @see Vec3#isZero(Vec3)
    */
   public static boolean isIdentity ( final Quaternion quat ) {

      return quat.real == 1.0f && Vec3.isZero(quat.imag);
   }

   /**
    * Tests to see if all the quaternions components are
    * non-zero.
    *
    * @param quat
    *                the input quaternion
    * @return the evaluation
    */
   public static boolean isNonZero ( final Quaternion quat ) {

      return quat.real != 0.0f && Vec3.isNonZero(quat.imag);
   }

   /**
    * Tests if the quaternion is of unit magnitude.
    *
    * @param quat
    *                the quaternion
    * @return the evaluation
    * @see Quaternion#dot(Quaternion, Quaternion)
    * @see Utils#approxFast(float, float)
    */
   public static boolean isUnit ( final Quaternion quat ) {

      return Utils.approxFast(Quaternion.magSq(quat), 1.0f);
   }

   /**
    * Tests if all components of the quaternion are zero.
    *
    * @param quat
    *                the quaternion
    * @return the evaluation
    * @see Vec3#isZero(Vec3)
    */
   public static boolean isZero ( final Quaternion quat ) {

      return quat.real == 0.0f && Vec3.isZero(quat.imag);
   }

   protected static Quaternion log (
         final Quaternion quat,
         final Quaternion target ) {

      // TODO: Needs testing...After testing, make public.

      final float imSq = Vec3.magSq(quat.imag);
      final float qmSq = quat.real * quat.real + imSq;

      if (qmSq == 0.0d) {
         return target.reset();
      }

      final double qm = Math.sqrt(qmSq);
      target.real = (float) Math.log(qm);

      if (imSq == 0.0d) {
         Vec3.zero(target.imag);
      } else {
         final double wNorm = quat.real / qm;
         final double wAcos = Math.acos(wNorm);
         final double im = Math.sqrt(imSq);
         final double scalarNorm = wAcos / im;
         Vec3.mult(quat.imag, (float) scalarNorm, target.imag);
      }
      return target;
   }

   /**
    * Finds the length, or magnitude, of a quaternion.<br>
    * <br>
    * |<em>a</em>| = \u221a <em>a</em> \u00b7 <em>a</em>
    *
    * @param quat
    *                the input quaternion
    * @return the magnitude
    * @see Math#sqrt(double)
    * @see Quaternion#dot(Quaternion, Quaternion)
    */
   public static float mag ( final Quaternion quat ) {

      return (float) Math.sqrt(Quaternion.magSq(quat));
   }

   /**
    * Finds the magnitude squared of a quaternion. Equivalent
    * to the dot product of a quaternion with itself.<br>
    * <br>
    * |<em>a</em>|<sup>2</sup> = <em>a</em> \u00b7 <em>a</em>
    *
    * @param quat
    *                the quaternion
    * @return the magnitude squared
    * @see Quaternion#dot(Quaternion, Quaternion)
    */
   public static float magSq ( final Quaternion quat ) {

      return quat.real * quat.real + Vec3.magSq(quat.imag);
   }

   /**
    * Mixes two vectors together by a step in [0.0, 1.0]. Uses
    * the easing function that is a static field belonging to
    * the Quaternion class.
    *
    * @param origin
    *                  the original quaternion
    * @param dest
    *                  the destination quaternion
    * @param step
    *                  the step
    * @param target
    *                  the output quaternion
    * @return the mix
    * @see Quaternion#EASING
    */
   public static Quaternion mix (
         final Quaternion origin,
         final Quaternion dest,
         final float step,
         final Quaternion target ) {

      return Quaternion.EASING.apply(
            origin, dest, step, target);
   }

   /**
    * Mixes two quaternions together by a step in [0.0, 1.0]
    * with the help of an easing function.
    *
    * @param origin
    *                      the original quaternion
    * @param dest
    *                      the destination quaternion
    * @param step
    *                      the step
    * @param target
    *                      the output quaternion
    * @param easingFunc
    *                      the easing function
    * @return the mix
    */
   public static Quaternion mix (
         final Quaternion origin,
         final Quaternion dest,
         final float step,
         final Quaternion target,
         final AbstrEasing easingFunc ) {

      return easingFunc.apply(
            origin, dest, step, target);
   }

   /**
    * Multiplies a scalar and quaternion.
    *
    * @param a
    *                  the scalar
    * @param b
    *                  the quaternion
    * @param target
    *                  the output quaternion
    * @return the scaled quaternion
    */
   public static Quaternion mult (
         final float a,
         final Quaternion b,
         final Quaternion target ) {

      if (a == 0.0f) {
         return target.reset();
      }

      Vec3.mult(a, b.imag, target.imag);
      target.real = a * b.real;
      return target;
   }

   /**
    * Multiplies a scalar and quaternion.
    *
    * @param a
    *                  the quaternion
    * @param b
    *                  the scalar
    * @param target
    *                  the output quaternion
    * @return the scaled quaternion
    */
   public static Quaternion mult (
         final Quaternion a,
         final float b,
         final Quaternion target ) {

      if (b == 0.0f) {
         return target.reset();
      }

      Vec3.mult(a.imag, b, target.imag);
      target.real = a.real * b;
      return target;
   }

   /**
    * Multiplies two quaternions.<br>
    * <br>
    * <em>a</em> <em>b</em> := <em>a<sub>imag</sub></em> \u00d7
    * <em>b<sub>imag</sub></em> + <em>a<sub>real</sub></em>
    * <em>b<sub>imag</sub></em> + <em>b<sub>real</sub></em>
    * <em>a<sub>imag</sub></em> + <em>a<sub>real</sub></em>
    * <em>b<sub>real</sub></em> - <em>a<sub>imag</sub></em>
    * \u00b7 <em>b<sub>imag</sub></em> <br>
    * <br>
    * Quaternion multiplication is not commutative.
    *
    * @param a
    *                  left operand
    * @param b
    *                  right operand
    * @param target
    *                  the output quaternion
    * @return the product
    */
   public static Quaternion mult (
         final Quaternion a,
         final Quaternion b,
         final Quaternion target ) {

      final Vec3 ai = a.imag;
      final Vec3 bi = b.imag;
      final float aw = a.real;
      final float bw = b.real;

      return target.set(
            aw * bw - ai.x * bi.x - ai.y * bi.y - ai.z * bi.z,
            ai.x * bw + aw * bi.x + ai.y * bi.z - ai.z * bi.y,
            ai.y * bw + aw * bi.y + ai.z * bi.x - ai.x * bi.z,
            ai.z * bw + aw * bi.z + ai.x * bi.y - ai.y * bi.x);
   }

   /**
    * Multiplies a vector by a quaternion, in effect rotating
    * the vector by the quaternion.
    *
    * @param quat
    *                  the quaternion
    * @param source
    *                  the vector
    * @param target
    *                  the output vector
    * @return the rotated vector
    */
   public static Vec3 mult (
         final Quaternion quat,
         final Vec3 source,
         final Vec3 target ) {

      final float w = quat.real;
      final Vec3 i = quat.imag;
      final float qx = i.x;
      final float qy = i.y;
      final float qz = i.z;

      final float iw = -qx * source.x - qy * source.y - qz * source.z;
      final float ix = w * source.x + qy * source.z - qz * source.y;
      final float iy = w * source.y + qz * source.x - qx * source.z;
      final float iz = w * source.z + qx * source.y - qy * source.x;

      return target.set(
            ix * w + iz * qy - iw * qx - iy * qz,
            iy * w + ix * qz - iw * qy - iz * qx,
            iz * w + iy * qx - iw * qz - ix * qy);
   }

   /**
    * Multiplies two quaternions, then normalizes the product.
    *
    * @param a
    *                  the left operand
    * @param b
    *                  the right operand
    * @param target
    *                  the output quaternion
    * @return the normalized product
    */
   public static Quaternion multNorm (
         final Quaternion a,
         final Quaternion b,
         final Quaternion target ) {

      final Vec3 ai = a.imag;
      final Vec3 bi = b.imag;
      final float aw = a.real;
      final float bw = b.real;

      final float qw = aw * bw - ai.x * bi.x - ai.y * bi.y - ai.z * bi.z;
      final float qx = ai.x * bw + aw * bi.x + ai.y * bi.z - ai.z * bi.y;
      final float qy = ai.y * bw + aw * bi.y + ai.z * bi.x - ai.x * bi.z;
      final float qz = ai.z * bw + aw * bi.z + ai.x * bi.y - ai.y * bi.x;

      final float mSq = qw * qw + qx * qx + qy * qy + qz * qz;
      if (mSq == 0.0f) {
         return target.reset();
      }
      final float mInv = (float) (1.0d / Math.sqrt(mSq));
      return target.set(
            qw * mInv,
            qx * mInv,
            qy * mInv,
            qz * mInv);
   }

   /**
    * Multiplies two quaternions, then normalizes the product.
    *
    * @param a
    *                   the left operand
    * @param b
    *                   the right operand
    * @param target
    *                   the output quaternion
    * @param product
    *                   the product
    * @return the normalized product
    */
   public static Quaternion multNorm (
         final Quaternion a,
         final Quaternion b,
         final Quaternion target,
         final Quaternion product ) {

      Quaternion.mult(a, b, product);
      Quaternion.normalize(product, target);
      return target;
   }

   /**
    * Divides a quaternion by its magnitude, such that its new
    * magnitude is one and it lies on a 4D hypersphere.<br>
    * <br>
    * <em>\u00e2</em> = <em>a</em> / |<em>a</em>|
    *
    * @param quat
    *                  the input quaternion
    * @param target
    *                  the output quaternion
    * @return the normalized quaternion
    * @see Quaternion#div(Quaternion, float, Quaternion)
    * @see Quaternion#mag(Quaternion)
    */
   public static Quaternion normalize ( final Quaternion quat,
         final Quaternion target ) {

      return Quaternion.div(quat, Quaternion.mag(quat), target);
   }

   /**
    * Creates a random unit quaternion.
    *
    * @param rng
    *                  the random number generator
    * @param target
    *                  the output quaternion
    * @return the random quaternion
    */
   public static Quaternion random ( final Random rng,
         final Quaternion target ) {

      final float w = rng.uniform(-1.0f, 1.0f);
      final float x = rng.uniform(-1.0f, 1.0f);
      final float y = rng.uniform(-1.0f, 1.0f);
      final float z = rng.uniform(-1.0f, 1.0f);

      final float mSq = w * w + x * x + y * y + z * z;
      if (mSq == 0.0f) {
         return target.reset();
      }

      final float mInv = (float) (1.0d / Math.sqrt(mSq));
      return target.set(
            w * mInv,
            x * mInv,
            y * mInv,
            z * mInv);
   }

   /**
    * Rotates a quaternion around an arbitrary axis by an
    * angle.
    *
    * @param quat
    *                   the quaternion
    * @param radians
    *                   the angle in radians
    * @param axis
    *                   the axis
    * @param target
    *                   the output quaternion
    * @return the rotated quaternion
    */
   public static Quaternion rotate (
         final Quaternion quat,
         final float radians,
         final Vec3 axis,
         final Quaternion target ) {

      // TODO: Needs testing...
      final float mSq = Quaternion.magSq(quat);
      if (mSq == 0.0f) {
         return Quaternion.fromAxisAngle(
               radians,
               axis,
               target);
      }

      final float wNorm = mSq == 1.0f ? quat.real
            : (float) (quat.real / Math.sqrt(mSq));
      final float halfAngle = wNorm <= -1.0f ? Utils.PI
            : wNorm >= 1.0f ? 0.0f : (float) Math.acos(wNorm);

      return Quaternion.fromAxisAngle(
            Utils.modRadians(
                  halfAngle + halfAngle + radians),
            axis, target);
   }

   public static float toAxisAngle ( Quaternion quat, Vec3 axis ) {

      final float mSq = Quaternion.magSq(quat);

      if (mSq == 0.0f) {
         Vec3.forward(axis);
         return 0.0f;
      }

      float wNorm;
      // float xNorm;
      // float yNorm;
      // float zNorm;
      Vec3 i = quat.imag;

      /*
       * Arguably, the test should be approximately equal, not
       * exactly equal, to one, due to float imprecision. However,
       * if the mag is not exactly one, the quaternion is not a
       * unit quaternion, and normalization should be attempted
       * regardless of floating point error.
       */
      if (mSq != 1.0f) {
         final float mInv = (float) (1.0d / Math.sqrt(mSq));
         wNorm = quat.real * mInv;
         // xNorm = i.x * mInv;
         // yNorm = i.y * mInv;
         // zNorm = i.z * mInv;
      } else {
         wNorm = quat.real;
         // xNorm = i.x;
         // yNorm = i.y;
         // zNorm = i.z;
      }

      final float angle = wNorm <= -1.0f ? Utils.TAU
            : wNorm >= 1.0f ? 0.0f : (float) Math.acos(wNorm);

      final float wAsin = Utils.TAU - angle;
      if (wAsin == 0.0f) {
         Vec3.forward(axis);
         return angle;
      }

      final float sInv = 1.0f / wAsin;
      // final float ax = xNorm * sInv;
      // final float ay = yNorm * sInv;
      // final float az = zNorm * sInv;
      final float ax = i.x * sInv;
      final float ay = i.y * sInv;
      final float az = i.z * sInv;

      final float aMSq = ax * ax + ay * ay + az * az;

      if (aMSq == 0.0f) {
         Vec3.forward(axis);
         return angle;
      }

      if (aMSq == 1.0f) {
         axis.set(ax, ay, az);
         return angle;
      }

      final float mInv = (float) (1.0d / Math.sqrt(aMSq));
      axis.set(
            ax * mInv,
            ay * mInv,
            az * mInv);
      return angle;
   }

   /**
    * Rotates a quaternion about the x axis by an angle.
    *
    * Do not use sequences of ortho-normal rotations by Euler
    * angles; this will result in gimbal lock, defeating the
    * purpose behind a quaternion.
    *
    * @param quat
    *                   the input quaternion
    * @param radians
    *                   the angle in radians
    * @param target
    *                   the output quaternion
    * @return the rotated quaternion
    * @see Math#cos(double)
    * @see Math#sin(double)
    */
   public static Quaternion rotateX (
         final Quaternion quat,
         final float radians,
         final Quaternion target ) {

      final double halfAngle = radians * 0.5d;
      final float cosa = (float) Math.cos(halfAngle);
      final float sina = (float) Math.sin(halfAngle);

      final Vec3 i = quat.imag;

      return target.set(
            cosa * quat.real - sina * i.x,
            cosa * i.x + sina * quat.real,
            cosa * i.y + sina * i.z,
            cosa * i.z - sina * i.y);
   }

   /**
    * Rotates a quaternion about the y axis by an angle.
    *
    * Do not use sequences of ortho-normal rotations by Euler
    * angles; this will result in gimbal lock, defeating the
    * purpose behind a quaternion.
    *
    * @param quat
    *                   the input quaternion
    * @param radians
    *                   the angle in radians
    * @param target
    *                   the output quaternion
    * @return the rotated quaternion
    * @see Math#cos(double)
    * @see Math#sin(double)
    */
   public static Quaternion rotateY (
         final Quaternion quat,
         final float radians,
         final Quaternion target ) {

      final double halfAngle = radians * 0.5d;
      final float cosa = (float) Math.cos(halfAngle);
      final float sina = (float) Math.sin(halfAngle);

      final Vec3 i = quat.imag;

      return target.set(
            cosa * quat.real - sina * i.y,
            cosa * i.x - sina * i.z,
            cosa * i.y + sina * quat.real,
            cosa * i.z + sina * i.x);
   }

   /**
    * Rotates a quaternion about the z axis by an angle.
    *
    * Do not use sequences of ortho-normal rotations by Euler
    * angles; this will result in gimbal lock, defeating the
    * purpose behind a quaternion.
    *
    * @param quat
    *                   the input quaternion
    * @param radians
    *                   the angle in radians
    * @param target
    *                   the output quaternion
    * @return the rotated quaternion
    * @see Math#cos(double)
    * @see Math#sin(double)
    */
   public static Quaternion rotateZ (
         final Quaternion quat,
         final float radians,
         final Quaternion target ) {

      final double halfAngle = radians * 0.5d;
      final float cosa = (float) Math.cos(halfAngle);
      final float sina = (float) Math.sin(halfAngle);

      final Vec3 i = quat.imag;

      return target.set(
            cosa * quat.real - sina * i.z,
            cosa * i.x + sina * i.y,
            cosa * i.y - sina * i.x,
            cosa * i.z + sina * quat.real);
   }

   /**
    * Sets the comparator function by which collections of
    * quaternions are compared.
    *
    * @param comparator
    *                      the comparator
    */
   public static void setComparator (
         final Comparator < Quaternion > comparator ) {

      if (comparator != null) {
         Quaternion.COMPARATOR = comparator;
      }
   }

   /**
    * Sets the easing function by which quaternions are
    * interpolated.
    *
    * @param easing
    *                  the easing function
    */
   public static void setEasing ( final AbstrEasing easing ) {

      if (easing != null) {
         Quaternion.EASING = easing;
      }
   }

   /**
    * Subtracts the right quaternion from the left.
    *
    * @param a
    *                  the left operand
    * @param b
    *                  the right operand
    * @param target
    *                  the output quaternion
    * @return the difference
    * @see Vec3#sub(Vec3, Vec3, Vec3)
    */
   public static Quaternion sub (
         final Quaternion a,
         final Quaternion b,
         final Quaternion target ) {

      Vec3.sub(a.imag, b.imag, target.imag);
      target.real = a.real - b.real;
      return target;
   }

   /**
    * Subtracts the right quaternion from the left and
    * normalizes the result.
    *
    * @param a
    *                  the left operand
    * @param b
    *                  the right operand
    * @param target
    *                  the output quaternion
    * @return the normalized difference
    * @see Vec3#sub(Vec3, Vec3, Vec3)
    * @see Quaternion#dot(Quaternion, Quaternion)
    */
   public static Quaternion subNorm (
         final Quaternion a,
         final Quaternion b,
         final Quaternion target ) {

      Vec3.sub(a.imag, b.imag, target.imag);
      target.real = a.real - b.real;

      final float mSq = Quaternion.magSq(target);
      if (mSq == 0.0f) {
         return target.reset();
      }

      final float mInv = (float) (1.0d / Math.sqrt(mSq));
      final Vec3 i = target.imag;
      return target.set(
            target.real * mInv,
            i.x * mInv,
            i.y * mInv,
            i.z * mInv);
   }

   /**
    * Subtracts the right quaternion from the left and
    * normalizes the result.
    *
    * @param a
    *                  the left operand
    * @param b
    *                  the right operand
    * @param target
    *                  the output quaternion
    * @param diff
    *                  the difference
    * @return the normalized difference
    * @see Quaternion#sub(Quaternion, Quaternion, Quaternion)
    * @see Quaternion#normalize(Quaternion, Quaternion)
    */
   public static Quaternion subNorm (
         final Quaternion a,
         final Quaternion b,
         final Quaternion target,
         final Quaternion diff ) {

      Quaternion.sub(a, b, diff);
      Quaternion.normalize(diff, target);
      return target;
   }

   /**
    * The coefficients of the imaginary components i, j and k.
    */
   public final Vec3 imag = new Vec3(0.0f, 0.0f, 0.0f);

   /**
    * The real component.
    */
   public float real = 1.0f;

   /**
    * The default constructor.
    */
   public Quaternion () {

      super(4);
   }

   public Quaternion (
         final boolean real,
         final boolean xImag,
         final boolean yImag,
         final boolean zImag ) {

      super(4);
      this.set(real, xImag, yImag, zImag);
   }

   public Quaternion (
         final String real,
         final String xImag,
         final String yImag,
         final String zImag ) {

      super(4);
      this.set(real, xImag, yImag, zImag);
   }

   /**
    * Constructs a quaternion by float component.
    *
    * @param real
    *                 the real (w) component
    * @param xImag
    *                 the x component
    * @param yImag
    *                 the y component
    * @param zImag
    *                 the z component
    */
   public Quaternion (
         final float real,
         final float xImag,
         final float yImag,
         final float zImag ) {

      super(4);
      this.set(real, xImag, yImag, zImag);
   }

   /**
    * Constructs a quaternion by real component and imaginary
    * vector.
    *
    * @param real
    *                the real component
    * @param imag
    *                the imaginary component
    */
   public Quaternion (
         final float real,
         final Vec3 imag ) {

      super(4);
      this.set(real, imag);
   }

   /**
    * A copy constructor.
    *
    * @param source
    *                  the source quaternion
    */
   public Quaternion ( final Quaternion source ) {

      super(4);
      this.set(source);
   }

   /**
    * Returns a new quaternion with this quaternion's
    * components. Java's cloneable interface is problematic;
    * use set or a copy constructor instead.
    *
    * @return a new quaternion
    * @see Quaternion#set(Quaternion)
    * @see Quaternion#Quaternion(Quaternion)
    */
   @Override
   public Quaternion clone () {

      return new Quaternion(this.real, this.imag);
   }

   /**
    * Returns -1 when this quaternion is less than the
    * comparisand; 1 when it is greater than; 0 when the two
    * are 'equal'. The implementation of this method allows
    * collections of quaternions to be sorted. This depends
    * upon the static comparator of the Quaternion class, which
    * can be changed.
    *
    * @param quat
    *                the comparisand
    * @return the numeric code
    * @see Quaternion#COMPARATOR
    */
   @Override
   public int compareTo ( final Quaternion quat ) {

      return Quaternion.COMPARATOR.compare(this, quat);
   }

   /**
    * Tests this quaternion for equivalence with another
    * object.
    *
    * @param obj
    *               the object
    * @return the equivalence
    * @see Quaternion#equals(Quaternion)
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
      return this.equals((Quaternion) obj);
   }

   /**
    * Simulates bracket subscript access in an array. Alias for
    * retrieving w, or the real component, as the first
    * element.
    *
    * @param index
    *                 the index
    * @return the component at that index
    * @see Quaternion#getWFirst(int)
    */
   @Override
   public float get ( final int index ) {

      return this.getWFirst(index);
   }

   /**
    * Gets an element by index, assuming that w is the first.
    *
    * @param index
    *                 the index
    * @return the component at that index
    */
   public float getWFirst ( final int index ) {

      switch (index) {
         case 0:
         case -4:
            return this.real;
         case 1:
         case -3:
            return this.imag.x;
         case 2:
         case -2:
            return this.imag.y;
         case 3:
         case -1:
            return this.imag.z;
         default:
            return 0.0f;
      }
   }

   /**
    * Gets an element by index, assuming that w is the last.
    *
    * @param index
    *                 the index
    * @return the component at that index
    */
   public float getWLast ( final int index ) {

      switch (index) {
         case 0:
         case -4:
            return this.imag.x;
         case 1:
         case -3:
            return this.imag.y;
         case 2:
         case -2:
            return this.imag.z;
         case 3:
         case -1:
            return this.real;
         default:
            return 0.0f;
      }
   }

   /**
    * Returns a hash code for this quaternion based on its real
    * and imaginary components.
    *
    * @return the hash code
    * @see Float#floatToIntBits(float)
    */
   @Override
   public int hashCode () {

      final int prime = 31;
      int result = 1;
      result = prime * result
            + (this.imag == null ? 0 : this.imag.hashCode());
      result = prime * result + Float.floatToIntBits(this.real);
      return result;
   }

   /**
    * Returns an iterator for this quaternion, which allows its
    * components to be accessed in an enhanced for-loop.
    */
   @Override
   public QIterator iterator () {

      return new IteratorWFirst(this);
   }

   /**
    * Resets this quaternion to an initial state, ( 1.0, 0.0,
    * 0.0, 0.0 ) .
    *
    * @return this quaternion
    */
   @Chainable
   public Quaternion reset () {

      return Quaternion.identity(this);
   }

   /**
    * Sets the components of this vector.
    *
    * @param real
    *                 the real (w) component
    * @param xImag
    *                 the imag x component
    * @param yImag
    *                 the imag y component
    * @param zImag
    *                 the imag z component
    * @return this quaternion
    */
   @Chainable
   public Quaternion set (
         final float real,
         final float xImag,
         final float yImag,
         final float zImag ) {

      this.real = real;
      this.imag.set(xImag, yImag, zImag);
      return this;
   }

   @Chainable
   public Quaternion set (
         final boolean real,
         final boolean xImag,
         final boolean yImag,
         final boolean zImag ) {

      this.real = Utils.bool(real);
      this.imag.set(xImag, yImag, zImag);
      return this;
   }

   @Chainable
   public Quaternion set(
      final String wstr,
      final String xstr,
      final String ystr,
      final String zstr) {

      float real = 0.0f;

      try {
         real = Float.parseFloat(wstr);
      } catch (final NumberFormatException e) {
         real = 0.0f;
      }

      this.real = real;
      this.imag.set(xstr, ystr, zstr);
      return this;
   }

   /**
    * Sets the components of this quaternion.
    *
    * @param real
    *                the real component
    * @param imag
    *                the imaginary component
    * @return this quaternion
    */
   @Chainable
   public Quaternion set (
         final float real,
         final Vec3 imag ) {

      this.real = real;
      this.imag.set(imag);
      return this;
   }

   /**
    * Copies the components of the input quaternion to this
    * quaternion.
    *
    * @param source
    *                  the input quaternion
    * @return this quaternion
    */
   @Chainable
   public Quaternion set ( final Quaternion source ) {

      this.real = source.real;
      this.imag.set(source.imag);
      return this;
   }

   /**
    * Returns a float array of length 4 containing this
    * quaternion's components. Defaults to returning w as the
    * first element.
    *
    * @return the array
    */
   @Override
   public float[] toArray () {

      return this.toArray(true);
   }

   /**
    * Returns a float array of length 4 containing this
    * quaternion's components. When the argument supplied is
    * true, w is returned as the first element, not the last.
    *
    * @param wFirst
    *                  issue w as the first element
    * @return the array
    */
   public float[] toArray ( final boolean wFirst ) {

      final float[] result = new float[this.size];
      for (int i = 0; i < this.size; ++i) {
         if (wFirst) {
            result[i] = this.getWFirst(i);
         } else {
            result[i] = this.getWLast(i);
         }
      }
      return result;
   }

   /**
    * Returns a string representation of this quaternion
    * according to the string format.
    *
    * @return the string
    */
   @Override
   public String toString () {

      return this.toString(4);
   }

   public String toString ( final int places ) {

      return new StringBuilder(128)
            .append("{ real: ")
            .append(Utils.toFixed(this.real, places))
            .append(", imag: ")
            .append(this.imag.toString(places))
            .append(" }")
            .toString();
   }

   /**
    * Gets the real component. An alias for those accustomed to
    * calling it 'w'.
    *
    * @return the real component
    */
   public float w () {

      return this.real;
   }

   /**
    * Sets the real component. An alias for those accustomed to
    * calling it 'w'.
    *
    * @param w
    *             the real value
    */
   public void w ( final float w ) {

      this.real = w;
   }

   /**
    * Gets the imginary coefficient x.
    *
    * @return the x value
    */
   public float x () {

      return this.imag.x;
   }

   /**
    * Sets the imaginary coefficient x.
    *
    * @param x
    *             the x value
    */
   public void x ( final float x ) {

      this.imag.x = x;
   }

   /**
    * Gets the imginary coefficient y.
    *
    * @return the y value
    */
   public float y () {

      return this.imag.y;
   }

   /**
    * Sets the imaginary coefficient y.
    *
    * @param y
    *             the y value
    */
   public void y ( final float y ) {

      this.imag.y = y;
   }

   /**
    * Gets the imginary coefficient z.
    *
    * @return the z value
    */
   public float z () {

      return this.imag.z;
   }

   /**
    * Sets the imaginary coefficient z.
    *
    * @param z
    *             the z value
    */
   public void z ( final float z ) {

      this.imag.z = z;
   }

   /**
    * Tests equivalence between this and another quaternion.
    * For rough equivalence of floating point components, use
    * the static approx function instead.
    *
    * @param quat
    *                the quaternion
    * @return the evaluation
    * @see Quaternion#approx(Quaternion, Quaternion)
    * @see Quaternion#approx(Quaternion, Quaternion, float)
    */
   protected boolean equals ( final Quaternion quat ) {

      if (this.imag == null) {
         if (quat.imag != null) {
            return false;
         }
      } else if (!this.imag.equals(quat.imag)) {
         return false;
      }
      if (Float.floatToIntBits(this.real) != Float.floatToIntBits(quat.real)) {
         return false;
      }
      return true;
   }
}
