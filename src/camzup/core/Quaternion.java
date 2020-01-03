package camzup.core;

import java.util.Comparator;
import java.util.Iterator;

/**
 * A four-dimensional complex number. The <em>x</em>,
 * <em>y</em> and <em>z</em> components are coefficients of
 * the imaginary <em>i</em>, <em>j</em> and <em>k</em>.
 * Discovered by William R. Hamilton with the formula
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
       *           the left comparisand
       * @param b
       *           the right comparisand
       * @return the comparison
       *
       */
      @Override
      public abstract int compare (
            final Quaternion a,
            final Quaternion b );

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
       *           the origin vector
       * @param dest
       *           the destination vector
       * @param step
       *           a factor in [0.0, 1.0]
       * @param target
       *           the output quaternion
       * @return the eased quaternion
       */
      @Override
      public Quaternion apply (
            final Quaternion origin,
            final Quaternion dest,
            final Float step,
            final Quaternion target ) {

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
       *           the origin quaternion
       * @param dest
       *           the destination quaternion
       * @param step
       *           a factor in [0.0, 1.0]
       * @param target
       *           the output quaternion
       * @return the eased quaternion
       */
      public abstract Quaternion applyUnclamped (
            final Quaternion origin,
            final Quaternion dest,
            final float step,
            final Quaternion target );

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
       *           the vector comparator
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
       *           the left comparisand
       * @param b
       *           the right comparisand
       * @return the evaluation
       */
      @Override
      public int compare (
            final Quaternion a,
            final Quaternion b ) {

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
       *           the quaternion to iterate
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
       *           the quaternion to iterate
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
      }

      /**
       * Lerps between the origin and destination quaternion by a
       * step. Normalizes the result.
       *
       * @param origin
       *           the origin quaternion
       * @param dest
       *           the destination quaternion
       * @param step
       *           a factor in [0.0, 1.0]
       * @param target
       *           the output quaternion
       * @return the eased quaternion
       * @see Math#abs(double)
       * @see Math#sqrt(double)
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

         /*
          * Normalize.
          */
         final double mSq = cw * cw + cx * cx + cy * cy + cz * cz;

         if (Math.abs(mSq) < Utils.EPSILON) {
            return target.reset();
         }

         if (Math.abs(1.0d - mSq) < Utils.EPSILON) {
            return target.set(
                  (float) cw,
                  (float) cx,
                  (float) cy,
                  (float) cz);
         }

         final double mInv = 1.0d / Math.sqrt(mSq);
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
       *           the quaternion to iterate
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
    * spherical linear interpolation (slerp). Slerp chooses the
    * shortest path between two orientations and maintains
    * constant speed for any step given in [0.0, 1.0].
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
       *           the origin quaternion
       * @param dest
       *           the destination quaternion
       * @param step
       *           a factor in [0.0, 1.0]
       * @param target
       *           the output quaternion
       * @return the eased quaternion
       * @see Utils#clamp(float, float, float)
       * @see Math#acos(double)
       * @see Math#sqrt(double)
       * @see Math#sin(double)
       * @see Math#abs(double)
       */
      @Override
      public Quaternion applyUnclamped (
            final Quaternion origin,
            final Quaternion dest,
            final float step,
            final Quaternion target ) {

         /*
          * Decompose origin vector.
          */
         final Vec3 ai = origin.imag;
         final float aw = origin.real;
         final float ax = ai.x;
         final float ay = ai.y;
         final float az = ai.z;

         /*
          * Decompose destination vector.
          */
         final Vec3 bi = dest.imag;
         float bw = dest.real;
         float bx = bi.x;
         float by = bi.y;
         float bz = bi.z;

         /*
          * Be sure to clamp the dot product here!
          */
         float dotp = Utils.clamp(aw * bw +
               ax * bx +
               ay * by +
               az * bz,
               -1.0f, 1.0f);

         /*
          * Flip values if the orientation is negative.
          */
         if (dotp < 0.0f) {
            // TODO Doublecheck other slerp implementations,
            // this seems to be based on the idea that dotp has had
            // one added to it.
            bw = -bw;
            bx = -bx;
            by = -by;
            bz = -bz;
            dotp = -dotp;
         }

         /*
          * Java Math functions will promote values into doubles, so
          * for precision, they'll be used until function close.
          */
         final double theta = Math.acos(dotp);
         final double sinTheta = Math.sqrt(1.0d - dotp * dotp);

         /*
          * The complementary step, i.e., 1.0 - step.
          */
         double u = 1.0d;

         /*
          * The step.
          */
         double v = 0.0d;

         if (sinTheta > Utils.EPSILON) {
            final double sInv = 1.0d / sinTheta;
            u = Math.sin((1.0f - step) * theta) * sInv;
            v = Math.sin(step * theta) * sInv;
         } else {
            u = 1.0d - step;
            v = step;
         }

         /*
          * Unclamped linear interpolation.
          */
         final double cw = u * aw + v * bw;
         final double cx = u * ax + v * bx;
         final double cy = u * ay + v * by;
         final double cz = u * az + v * bz;

         /*
          * Normalize.
          */
         final double mSq = cw * cw + cx * cx + cy * cy + cz * cz;

         if (Math.abs(mSq) < Utils.EPSILON) {
            return target.reset();
         }

         if (Math.abs(1.0d - mSq) < Utils.EPSILON) {
            return target.set(
                  (float) cw,
                  (float) cx,
                  (float) cy,
                  (float) cz);
         }

         final double mInv = 1.0d / Math.sqrt(mSq);
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
    * Raises a quaternion to the power of a real number. Uses
    * the formula<br>
    * <br>
    * <em>a</em><sup><em>b</em></sup> :=
    * |<em>a</em>|<sup><em>b</em></sup> { cos ( <em>b</em>
    * \u03b8 ), <em>n</em> sin ( <em>b</em> \u03b8 ) }<br>
    * <br>
    * where \u03b8 and <em>n</em> are the angle and axis
    * representation of the quaternion <em>a</em>.
    *
    * @param a
    *           the input quaternion
    * @param b
    *           the exponent
    * @param target
    *           the output quaternion
    * @return the result
    * @see Quaternion#magSq(Quaternion)
    * @see Math#sqrt(double)
    * @see Math#acos(double)
    * @see Math#pow(double, double)
    * @see Math#cos(double)
    * @see Math#sin(double)
    */
   protected static Quaternion pow (
         final Quaternion a,
         final float b,
         final Quaternion target ) {

      if (b == 0.0f) {
         return target.reset();
      }

      /* Normalize the quaternion's real component */
      final float mSq = Quaternion.magSq(a);
      if (mSq == 0.0f) {
         return target.reset();
      }

      double m = mSq;
      double wNorm = a.real;
      if (!Utils.approxFast(mSq, 1.0f)) {
         m = Math.sqrt(mSq);
         wNorm /= m;
      }

      /* Set new real component. */
      final double theta = wNorm <= -1.0d ? Math.PI
            : wNorm >= 1.0d ? 0.0d : Math.acos(wNorm);
      final double btheta = b * theta;
      final double scalar = Math.pow(m, b);
      target.real = (float) (scalar * Math.cos(btheta));

      /* Calculate imaginary component. */
      final double wAsin = IUtils.TAU_D - theta;
      if (wAsin == 0.0d) {
         Vec3.zero(target.imag);
         return target;
      }

      /*
       * The axis is generated by dividing the imaginary by the
       * arc-sine of the normalized real.
       */
      final double sInv = 1.0d / wAsin;
      final Vec3 i = a.imag;
      double nx = i.x * sInv;
      double ny = i.y * sInv;
      double nz = i.z * sInv;

      /* Normalize the axis. */
      final double nMSq = nx * nx + ny * ny + nz * nz;
      if (nMSq == 0.0d) {
         Vec3.zero(target.imag);
         return target;
      }

      if (nMSq != 1.0d) {
         final double nmInv = 1.0d / Math.sqrt(nMSq);
         nx *= nmInv;
         ny *= nmInv;
         nz *= nmInv;
      }

      /*
       * Scale the axis by sin(b theta), then by pow(mag(q), b).
       */
      final double sclrSinbt = scalar * Math.sin(btheta);
      target.imag.set(
            (float) (nx * sclrSinbt),
            (float) (ny * sclrSinbt),
            (float) (nz * sclrSinbt));
      return target;
   }

   /**
    * Adds two quaternions.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output quaternion
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
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the target quaternion
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
    * Adds two quaternions and normalizes the result. Emits the
    * unscaled sum as an output vector.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the target quaternion
    * @param sum
    *           the sum
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
    * Tests to see if all the quaternions components are
    * non-zero.
    *
    * @param q
    *           the input quaternion
    * @return the evaluation
    * @see Vec3#all(Vec3)
    */
   public static boolean all ( final Quaternion q ) {

      return q.real != 0.0f && Vec3.all(q.imag);
   }

   /**
    * Tests to see if all the quaternions components are
    * non-zero.
    *
    * @param q
    *           the input quaternion
    * @return the evaluation
    * @see Vec3#any(Vec3)
    */
   public static boolean any ( final Quaternion q ) {

      return q.real != 0.0f || Vec3.any(q.imag);
   }

   /**
    * Evaluates whether or not two quaternions approximate each
    * other.
    *
    * @param a
    *           the left comparisand
    * @param b
    *           the right comparisand
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
    *           the left comparisand
    * @param b
    *           the right comparisand
    * @param tolerance
    *           the tolerance
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
    * Tests to see if a quaternion has, approximately, the
    * specified magnitude according to the default EPSILON.
    *
    * @param a
    *           the quaternion
    * @param b
    *           the magnitude
    * @return the evaluation
    * @see Utils#approxFast(float, float)
    * @see Quaternion#magSq(Quaternion)
    */
   public static boolean approxMag (
         final Quaternion a,
         final float b ) {

      return Utils.approxFast(
            Quaternion.magSq(a), b * b);
   }

   /**
    * Tests to see if a quaternion has, approximately, the
    * specified magnitude.
    *
    * @param a
    *           the quaternion
    * @param b
    *           the magnitude
    * @param tolerance
    *           the tolerance
    * @return the evaluation
    * @see Utils#approxFast(float, float, float)
    * @see Quaternion#magSq(Quaternion)
    */
   public static boolean approxMag (
         final Quaternion a,
         final float b,
         final float tolerance ) {

      return Utils.approxFast(
            Quaternion.magSq(a), b * b, tolerance);
   }

   /**
    * Returns the conjugate of the quaternion, where the
    * imaginary component is negated.<br>
    * <br>
    * <em>a</em>* = { <em>a<sub>real</sub></em>,
    * -<em>a<sub>imag</sub></em> }
    *
    * @param q
    *           the input quaternion
    * @param target
    *           the output quaternion
    * @return the conjugate
    * @see Vec3#negate(Vec3, Vec3)
    */
   public static Quaternion conj (
         final Quaternion q,
         final Quaternion target ) {

      Vec3.negate(q.imag, target.imag);
      target.real = q.real;
      return target;
   }

   /**
    * Divides a scalar by a quaternion.
    *
    * @param a
    *           the numerator
    * @param b
    *           the denominator
    * @param target
    *           the output quaternion
    * @param inverted
    *           the inverse
    * @return the quotient
    * @see Utils#approxFast(float, float)
    */
   public static Quaternion div (
         final float a,
         final Quaternion b,
         final Quaternion target,
         final Quaternion inverted ) {

      // TODO: Test inlined version.

      final Vec3 bi = b.imag;
      final float bw = b.real;
      final float bx = bi.x;
      final float by = bi.y;
      final float bz = bi.z;

      final float bmSq = bw * bw + bx * bx + by * by + bz * bz;

      if (bmSq == 0.0f) {
         return target.reset();
      }

      float bwInv = bw;
      float bxInv = -bx;
      float byInv = -by;
      float bzInv = -bz;

      if (!Utils.approxFast(bmSq, 1.0f)) {
         final float bmSqInv = 1.0f / bmSq;
         bwInv *= bmSqInv;
         bxInv *= bmSqInv;
         byInv *= bmSqInv;
         bzInv *= bmSqInv;
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
    *           the numerator
    * @param b
    *           the denominator
    * @param target
    *           the output quaternion
    * @param inverted
    *           the inverse
    * @param conjugate
    *           the conjugate
    * @return the quotient
    * @see Quaternion#inverse(Quaternion, Quaternion,
    *      Quaternion)
    * @see Quaternion#mul(float, Quaternion, Quaternion)
    */
   public static Quaternion div (
         final float a,
         final Quaternion b,
         final Quaternion target,
         final Quaternion inverted,
         final Quaternion conjugate ) {

      Quaternion.inverse(b, inverted, conjugate);
      Quaternion.mul(a, inverted, target);
      return target;
   }

   /**
    * Divides a quaternion by a scalar.
    *
    * @param a
    *           the numerator
    * @param b
    *           the denominator
    * @param target
    *           the output quaternion
    * @return the quotient
    * @see Vec3#mul(Vec3, float, Vec3)
    */
   public static Quaternion div (
         final Quaternion a,
         final float b,
         final Quaternion target ) {

      if (b == 0.0f) {
         return target.reset();
      }

      final float bInv = 1.0f / b;
      Vec3.mul(a.imag, bInv, target.imag);
      target.real = a.real * bInv;

      return target;
   }

   /**
    * Divides one quaternion by another.
    *
    * @param a
    *           the numerator
    * @param b
    *           the denominator
    * @param target
    *           the output quaternion
    * @return the quotient
    * @see Utils#approxFast(float, float)
    */
   public static Quaternion div (
         final Quaternion a,
         final Quaternion b,
         final Quaternion target ) {

      // TODO: Test inlined version.

      final Vec3 bi = b.imag;
      final float bw = b.real;
      final float bx = bi.x;
      final float by = bi.y;
      final float bz = bi.z;

      final float bmSq = bw * bw + bx * bx + by * by + bz * bz;

      if (bmSq == 0.0f) {
         return target.reset();
      }

      float bwInv = bw;
      float bxInv = -bx;
      float byInv = -by;
      float bzInv = -bz;

      if (!Utils.approxFast(bmSq, 1.0f)) {
         final float bmSqInv = 1.0f / bmSq;
         bwInv *= bmSqInv;
         bxInv *= bmSqInv;
         byInv *= bmSqInv;
         bzInv *= bmSqInv;
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
    *           the numerator
    * @param b
    *           the denominator
    * @param target
    *           the output quaternion
    * @param inverted
    *           the inverse
    * @param conjugate
    *           the conjugate
    * @return the quotient
    * @see Quaternion#inverse(Quaternion, Quaternion,
    *      Quaternion)
    * @see Quaternion#mul(Quaternion, Quaternion, Quaternion)
    */
   public static Quaternion div (
         final Quaternion a,
         final Quaternion b,
         final Quaternion target,
         final Quaternion inverted,
         final Quaternion conjugate ) {

      Quaternion.inverse(b, inverted, conjugate);
      return Quaternion.mul(a, inverted, target);
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
    *           left operand
    * @param b
    *           right operand
    * @return the dot product
    * @see Vec3#dot(Vec3, Vec3)
    */
   public static float dot (
         final Quaternion a,
         final Quaternion b ) {

      return a.real * b.real + Vec3.dot(a.imag, b.imag);
   }

   /**
    * Finds the value of Euler's number <em>e</em> raised to
    * the power of the quaternion. Uses the formula:<br>
    * <br>
    * exp ( <em>q</em> ) := <em>e<sup>r</sup></em> ( { cos (
    * |<em>i</em>| ), <em>\u00ee</em> sin ( |<em>i</em>| ) }
    * )<br>
    * <br>
    *
    * where <em>r</em> is <em>q<sub>real</sub></em> and
    * <em>i</em> is <em>q<sub>imag</sub></em>.
    *
    * @param q
    *           the input quaternion
    * @param target
    *           the output quaternion
    * @return the result
    * @see Math#exp(double)
    * @see Vec3#mag(Vec3)
    * @see Vec3#zero(Vec3)
    * @see Math#sqrt(double)
    * @see Math#cos(double)
    * @see Math#sin(double)
    */
   public static Quaternion exp (
         final Quaternion q,
         final Quaternion target ) {

      final double ea = Math.exp(q.real);
      final float imSq = Vec3.mag(q.imag);
      if (imSq == 0.0f) {
         Vec3.zero(target.imag);
         target.real = (float) ea;
         return target;
      }

      final double im = Math.sqrt(imSq);
      target.real = (float) (ea * Math.cos(im));
      Vec3.mul(
            q.imag,
            (float) (ea * Math.sin(im) / im),
            target.imag);

      return target;
   }

   /**
    * Creates a quaternion from the axes of a matrix or as
    * separate vectors. This is a helper function which uses
    * only the relevant information to create a quaternion.
    *
    * @param rightx
    *           right x
    * @param forwardy
    *           forward y
    * @param upz
    *           up z
    * @param forwardz
    *           forward z
    * @param upy
    *           up y
    * @param upx
    *           up x
    * @param rightz
    *           right z
    * @param righty
    *           right y
    * @param forwardx
    *           forward x
    * @param target
    *           the output quaternion
    * @return the quaternion
    * @see Math#copySign(float, float)
    * @see Math#sqrt(double)
    */
   public static Quaternion fromAxes (
         final float rightx, final float forwardy, final float upz,
         final float forwardz, final float upy,
         final float upx, final float rightz,
         final float righty, final float forwardx,
         final Quaternion target ) {

      final float w = (float) (0.5d * Math.sqrt(
            Utils.max(0.0f, 1.0f + rightx + forwardy + upz)));

      final float x = (float) (0.5d * Math.sqrt(
            Utils.max(0.0f, 1.0f + rightx - forwardy - upz)));
      final float y = (float) (0.5d * Math.sqrt(
            Utils.max(0.0f, 1.0f - rightx + forwardy - upz)));
      final float z = (float) (0.5d * Math.sqrt(
            Utils.max(0.0f, 1.0f - rightx - forwardy + upz)));

      return target.set(w,
            Math.copySign(x, forwardz - upy),
            Math.copySign(y, upx - rightz),
            Math.copySign(z, righty - forwardx));
   }

   /**
    * Creates a quaternion from three axes. Assumes that the up
    * axis is (0.0, 0.0, 1.0). Assumes that the zero components
    * of the other axes are zero.
    *
    * @param right
    *           the right axis
    * @param forward
    *           the up axis
    * @param target
    *           the output quaternion
    * @return the quaternion
    * @see Quaternion#fromAxes(float, float, float, float,
    *      float, float, float, float, float, Quaternion)
    */
   public static Quaternion fromAxes (
         final Vec2 right,
         final Vec2 forward,
         final Quaternion target ) {

      // TODO: Needs testing...
      return Quaternion.fromAxes(
            right.x, forward.y, 1.0f,
            0.0f, 0.0f,
            0.0f, 0.0f,
            right.y, forward.x,
            target);
   }

   /**
    * Creates a quaternion from three axes.
    *
    * @param right
    *           the right axis
    * @param up
    *           the forward axis
    * @param forward
    *           the up axis
    * @param target
    *           the output quaternion
    * @return the quaternion
    * @see Quaternion#fromAxes(float, float, float, float,
    *      float, float, float, float, float, Quaternion)
    */
   public static Quaternion fromAxes (
         final Vec3 right,
         final Vec3 forward,
         final Vec3 up,
         final Quaternion target ) {

      return Quaternion.fromAxes(
            right.x, forward.y, up.z,
            forward.z, up.y,
            up.x, right.z,
            right.y, forward.x, target);
   }

   /**
    * Creates a quaternion from an axis and angle. Normalizes
    * the axis prior to calculating the quaternion.
    *
    * @param radians
    *           the angle in radians
    * @param axis
    *           the axis
    * @param target
    *           the output quaternion
    * @return the quaternion
    * @see Vec2#magSq(Vec2)
    * @see Utils#approxFast(float, float)
    * @see Math#sqrt(double)
    * @see Math#cos(double)
    * @see Math#sin(double)
    */
   public static Quaternion fromAxisAngle (
         final float radians,
         final Vec2 axis,
         final Quaternion target ) {

      // TODO: Needs testing...

      final float amSq = Vec2.magSq(axis);
      if (amSq == 0.0f) {
         return target.reset();
      }

      float nx = axis.x;
      float ny = axis.y;

      if (!Utils.approxFast(amSq, 1.0f)) {
         final float amInv = (float) (1.0d / Math.sqrt(amSq));
         nx *= amInv;
         ny *= amInv;
      }

      final double halfAngle = 0.5d * radians;
      final float sinHalf = (float) Math.sin(halfAngle);
      return target.set(
            (float) Math.cos(halfAngle),
            nx * sinHalf,
            ny * sinHalf,
            0.0f);
   }

   /**
    * Creates a quaternion from an axis and angle. Normalizes
    * the axis prior to calculating the quaternion.
    *
    * @param radians
    *           the angle in radians
    * @param axis
    *           the axis
    * @param target
    *           the output quaternion
    * @return the quaternion
    * @see Vec3#magSq(Vec3)
    * @see Utils#approxFast(float, float)
    * @see Math#sqrt(double)
    * @see Math#cos(double)
    * @see Math#sin(double)
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

      if (!Utils.approxFast(amSq, 1.0f)) {
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
    * Creates a quaternion from a 4 x 4 matrix.
    *
    * @param source
    *           the matrix
    * @param target
    *           the output quaternion
    * @return the quaternion
    * @see Quaternion#fromAxes(float, float, float, float,
    *      float, float, float, float, float, Quaternion)
    */
   public static Quaternion fromMat4 (
         final Mat4 source,
         final Quaternion target ) {

      return Quaternion.fromAxes(
            source.m00, source.m11, source.m22,
            source.m21, source.m12,
            source.m02, source.m20,
            source.m10, source.m01,
            target);
   }

   /**
    * Creates a quaternion with reference to two vectors. This
    * function creates normalized copies of the vectors. Uses
    * the formula:<br>
    * <br>
    * fromTo (a, b) := { a \u00b7 b, a \u00d7 b }<br>
    * <br>
    *
    * Assumes that the z coordinate of each vector is zero.
    *
    * @param origin
    *           the origin vector, normalized
    * @param dest
    *           the destination vector, normalized
    * @param target
    *           the output quaternion
    * @return the quaternion
    * @see Utils#approxFast(float, float)
    * @see Math#sqrt(double)
    */
   public static Quaternion fromTo (
         final Vec2 origin,
         final Vec2 dest,
         final Quaternion target ) {

      float anx = origin.x;
      float any = origin.y;
      final float amsq = anx * anx + any * any;
      if (amsq == 0.0f) {
         return target.reset();
      }

      float bnx = dest.x;
      float bny = dest.y;
      final float bmsq = bnx * bnx + bny * bny;
      if (bmsq == 0.0f) {
         return target.reset();
      }

      if (!Utils.approxFast(amsq, 1.0f)) {
         final float aminv = (float) (1.0d / Math.sqrt(amsq));
         anx *= aminv;
         any *= aminv;
      }

      if (!Utils.approxFast(bmsq, 1.0f)) {
         final float bminv = (float) (1.0d / Math.sqrt(bmsq));
         bnx *= bminv;
         bny *= bminv;
      }

      target.real = anx * bnx + any * bny;
      target.imag.set(0.0f, 0.0f, anx * bny - any * bnx);
      return target;
   }

   /**
    * Creates a quaternion with reference to two vectors. This
    * function creates normalized copies of the vectors. Uses
    * the formula:<br>
    * <br>
    * fromTo (a, b) := { a \u00b7 b, a \u00d7 b }
    *
    * @param origin
    *           the origin vector, normalized
    * @param dest
    *           the destination vector, normalized
    * @param target
    *           the output quaternion
    * @return the quaternion
    * @see Utils#approxFast(float, float)
    * @see Math#sqrt(double)
    */
   public static Quaternion fromTo (
         final Vec3 origin,
         final Vec3 dest,
         final Quaternion target ) {

      float anx = origin.x;
      float any = origin.y;
      float anz = origin.z;
      final float amsq = anx * anx + any * any + anz * anz;
      if (amsq == 0.0f) {
         return target.reset();
      }

      float bnx = dest.x;
      float bny = dest.y;
      float bnz = dest.z;
      final float bmsq = bnx * bnx + bny * bny + bnz * bnz;
      if (bmsq == 0.0f) {
         return target.reset();
      }

      if (!Utils.approxFast(amsq, 1.0f)) {
         final float aminv = (float) (1.0d / Math.sqrt(amsq));
         anx *= aminv;
         any *= aminv;
         anz *= aminv;
      }

      if (!Utils.approxFast(bmsq, 1.0f)) {
         final float bminv = (float) (1.0d / Math.sqrt(bmsq));
         bnx *= bminv;
         bny *= bminv;
         bnz *= bminv;
      }

      target.real = anx * bnx + any * bny + anz * bnz;
      final Vec3 i = target.imag;
      i.x = any * bnz - anz * bny;
      i.y = anz * bnx - anx * bnz;
      i.z = anx * bny - any * bnx;
      return target;
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
    * Gets the forward axis of the rotation. Equivalent to
    * multiplying (0.0, 1.0, 0.0) by the quaternion. If all
    * three axes need to be retrieved, use
    * {@link Quaternion#toAxes(Quaternion, Vec3, Vec3, Vec3)} .
    *
    * @param q
    *           the quaternion
    * @param target
    *           the output vector
    * @return the forward axis
    */
   public static Vec3 getForward (
         final Quaternion q,
         final Vec3 target ) {

      final Vec3 imag = q.imag;
      final float w = q.real;
      final float x = imag.x;
      final float y = imag.y;
      final float z = imag.z;

      return target.set(
            -z * w + x * y + y * x - w * z,
            w * w - z * z + y * y - x * x,
            x * w + w * x + y * z + z * y);
   }

   /**
    * Gets the right axis of the rotation. Equivalent to
    * multiplying (1.0, 0.0, 0.0) by the quaternion. If all
    * three axes need to be retrieved, use
    * {@link Quaternion#toAxes(Quaternion, Vec3, Vec3, Vec3)} .
    *
    * @param q
    *           the quaternion
    * @param target
    *           the output vector
    * @return the right axis
    */
   public static Vec3 getRight (
         final Quaternion q,
         final Vec3 target ) {

      final Vec3 imag = q.imag;

      final float w = q.real;
      final float x = imag.x;
      final float y = imag.y;
      final float z = imag.z;

      return target.set(
            w * w - y * y + x * x - z * z,
            z * w + w * z + x * y + y * x,
            -y * w + z * x + x * z - w * y);
   }

   /**
    * Gets the up axis of the rotation. Equivalent to
    * multiplying (0.0, 0.0, 1.0) by the quaternion. If all
    * three axes need to be retrieved, use
    * {@link Quaternion#toAxes(Quaternion, Vec3, Vec3, Vec3)} .
    *
    * @param q
    *           the quaternion
    * @param target
    *           the output vector
    * @return the up axis
    */
   public static Vec3 getUp (
         final Quaternion q,
         final Vec3 target ) {

      final Vec3 imag = q.imag;

      final float w = q.real;
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
    *           the output quaternion
    * @return the identity
    */
   public static Quaternion identity ( final Quaternion target ) {

      return target.set(1.0f, 0.0f, 0.0f, 0.0f);
   }

   /**
    * Finds the inverse, or reciprocal, of a quaternion, which
    * is the conjugate divided by the magnitude squared.<br>
    * <br>
    * <em>a</em><sup>-1</sup> := <em>a</em>* /
    * |<em>a</em>|<sup>2</sup><br>
    * <br>
    * If a quaternion is of unit length, its inverse is equal
    * to its conjugate.
    *
    * @param q
    *           the input quaternion
    * @param target
    *           the output quaternion
    * @return the inverse
    * @see Quaternion#magSq(Quaternion)
    */
   public static Quaternion inverse (
         final Quaternion q,
         final Quaternion target ) {

      final float mSq = Quaternion.magSq(q);
      if (mSq == 0.0f) {
         return target.reset();
      }

      final Vec3 i = q.imag;
      if (mSq == 1.0f) {
         return target.set(q.real, -i.x, -i.y, -i.z);
      }

      final float mSqInv = 1.0f / mSq;
      return target.set(
            q.real * mSqInv,
            -i.x * mSqInv,
            -i.y * mSqInv,
            -i.z * mSqInv);
   }

   /**
    * Finds the inverse, or reciprocal, of a quaternion, which
    * is the its conjugate divided by its magnitude
    * squared.<br>
    * <br>
    * <em>a</em><sup>-1</sup> := <em>a</em>* /
    * |<em>a</em>|<sup>2</sup><br>
    * <br>
    * If a quaternion is of unit length, its inverse is equal
    * to its conjugate.
    *
    * @param q
    *           the input quaternion
    * @param target
    *           the output quaternion
    * @param conjugate
    *           the conjugate
    * @return the inverse
    * @see Quaternion#conj(Quaternion, Quaternion)
    * @see Quaternion#dot(Quaternion, Quaternion)
    * @see Quaternion#div(Quaternion, float, Quaternion)
    */
   public static Quaternion inverse (
         final Quaternion q,
         final Quaternion target,
         final Quaternion conjugate ) {

      Quaternion.conj(q, conjugate);
      Quaternion.div(conjugate, Quaternion.magSq(q), target);
      return target;
   }

   /**
    * Tests if the quaternion is the identity, where its real
    * component is 1.0 and its imaginary components are all
    * zero.
    *
    * @param q
    *           the quaternion to test
    * @return the evaluation
    * @see Vec3#isZero(Vec3)
    */
   public static boolean isIdentity ( final Quaternion q ) {

      return q.real == 1.0f && Vec3.isZero(q.imag);
   }

   /**
    * Tests to see if a quaternion is a pure, i.e. if its real
    * component is zero.
    *
    * @param q
    *           the quaternion
    * @return the evaluation
    */
   public static boolean isPure ( final Quaternion q ) {

      return q.real == 0.0f;
   }

   /**
    * Tests if the quaternion is of unit magnitude.
    *
    * @param q
    *           the quaternion
    * @return the evaluation
    * @see Quaternion#dot(Quaternion, Quaternion)
    * @see Utils#approxFast(float, float)
    */
   public static boolean isUnit ( final Quaternion q ) {

      return Utils.approxFast(Quaternion.magSq(q), 1.0f);
   }

   /**
    * Tests if all components of the quaternion are zero.
    *
    * @param q
    *           the quaternion
    * @return the evaluation
    * @see Vec3#isZero(Vec3)
    */
   public static boolean isZero ( final Quaternion q ) {

      return q.real == 0.0f && Vec3.isZero(q.imag);
   }

   /**
    * Finds the natural logarithm of the quaternion. Uses the
    * formula:<br>
    * <br>
    * ln ( <em>q</em> ) := { ln ( |<em>q</em>| ),
    * <em>\u00ee</em> acos ( a<sub>real</sub> / |<em>q</em>| )
    * }<br>
    * <br>
    * where <em>i</em> is <em>q<sub>imag</sub></em>.
    *
    * @param q
    *           the quaternion
    * @param target
    *           the output quaternion
    * @return the result
    * @see Vec3#magSq(Vec3)
    * @see Math#sqrt(double)
    * @see Math#log(double)
    * @see Utils#approxFast(float, float)
    * @see Vec3#zero(Vec3)
    * @see Math#acos(double)
    * @see Vec3#mul(Vec3, float, Vec3)
    */
   public static Quaternion log (
         final Quaternion q,
         final Quaternion target ) {

      final float imSq = Vec3.magSq(q.imag);
      final float qmSq = q.real * q.real + imSq;

      if (qmSq == 0.0f) {
         return target.reset();
      }

      final double qm = Math.sqrt(qmSq);
      target.real = (float) Math.log(qm);

      if (Utils.approxFast(imSq, 0.0f)) {
         Vec3.zero(target.imag);
         return target;
      }

      final double wNorm = q.real / qm;
      final double wAcos = wNorm <= -1.0d ? Math.PI
            : wNorm >= 1.0d ? 0.0d : Math.acos(wNorm);

      if (Utils.approxFast(imSq, 1.0f)) {
         Vec3.mul(q.imag, (float) wAcos, target.imag);
         return target;
      }

      final double scalarNorm = wAcos / Math.sqrt(imSq);
      Vec3.mul(q.imag, (float) scalarNorm, target.imag);
      return target;
   }

   /**
    * Finds the length, or magnitude, of a quaternion.<br>
    * <br>
    * |<em>a</em>| := \u221a <em>a</em> \u00b7 <em>a</em><br>
    * <br>
    * |<em>a</em>| := \u221a <em>a</em> <em>a*</em>
    *
    * @param q
    *           the input quaternion
    * @return the magnitude
    * @see Math#sqrt(double)
    * @see Quaternion#dot(Quaternion, Quaternion)
    */
   public static float mag ( final Quaternion q ) {

      return (float) Math.sqrt(Quaternion.magSq(q));
   }

   /**
    * Finds the magnitude squared of a quaternion. Equivalent
    * to the dot product of a quaternion with itself and to the
    * product of a quaternion with its conjugate.<br>
    * <br>
    * |<em>a</em>|<sup>2</sup> := <em>a</em> \u00b7
    * <em>a</em><br>
    * <br>
    * |<em>a</em>|<sup>2</sup> := <em>a</em> <em>a*</em>
    *
    * @param q
    *           the quaternion
    * @return the magnitude squared
    * @see Quaternion#dot(Quaternion, Quaternion)
    */
   public static float magSq ( final Quaternion q ) {

      return q.real * q.real + Vec3.magSq(q.imag);
   }

   /**
    * Mixes two vectors together by a step in [0.0, 1.0]. Uses
    * the easing function that is a static field belonging to
    * the Quaternion class.
    *
    * @param origin
    *           the original quaternion
    * @param dest
    *           the destination quaternion
    * @param step
    *           the step
    * @param target
    *           the output quaternion
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
    *           the original quaternion
    * @param dest
    *           the destination quaternion
    * @param step
    *           the step
    * @param target
    *           the output quaternion
    * @param easingFunc
    *           the easing function
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
    *           the scalar
    * @param b
    *           the quaternion
    * @param target
    *           the output quaternion
    * @return the scaled quaternion
    */
   public static Quaternion mul (
         final float a,
         final Quaternion b,
         final Quaternion target ) {

      if (a == 0.0f) {
         return target.reset();
      }

      Vec3.mul(a, b.imag, target.imag);
      target.real = a * b.real;
      return target;
   }

   /**
    * Multiplies a scalar and quaternion.
    *
    * @param a
    *           the quaternion
    * @param b
    *           the scalar
    * @param target
    *           the output quaternion
    * @return the scaled quaternion
    */
   public static Quaternion mul (
         final Quaternion a,
         final float b,
         final Quaternion target ) {

      if (b == 0.0f) {
         return target.reset();
      }

      Vec3.mul(a.imag, b, target.imag);
      target.real = a.real * b;
      return target;
   }

   /**
    * Multiplies two quaternions. Also referred to as the
    * Hamilton product. Uses the formula<br>
    * <br>
    * <em>a</em> <em>b</em> := { <em>a<sub>real</sub></em>
    * <em>b<sub>real</sub></em> - <em>a<sub>imag</sub></em>
    * \u00b7 <em>b<sub>imag</sub></em> ,
    * <em>a<sub>imag</sub></em> \u00d7
    * <em>b<sub>imag</sub></em> + <em>a<sub>real</sub></em>
    * <em>b<sub>imag</sub></em> + <em>b<sub>real</sub></em>
    * <em>a<sub>imag</sub></em> }<br>
    * <br>
    * Quaternion multiplication is not commutative.
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @param target
    *           the output quaternion
    * @return the product
    */
   public static Quaternion mul (
         final Quaternion a,
         final Quaternion b,
         final Quaternion target ) {

      final Vec3 ai = a.imag;
      final Vec3 bi = b.imag;
      final float aw = a.real;
      final float bw = b.real;

      return target.set(
            aw * bw - (ai.x * bi.x + ai.y * bi.y + ai.z * bi.z),
            ai.x * bw + aw * bi.x + ai.y * bi.z - ai.z * bi.y,
            ai.y * bw + aw * bi.y + ai.z * bi.x - ai.x * bi.z,
            ai.z * bw + aw * bi.z + ai.x * bi.y - ai.y * bi.x);
   }

   /**
    * Multiplies a vector by a quaternion, in effect rotating
    * the vector by the quaternion. Equivalent to promoting the
    * vector to a pure quaternion, multiplying the rotation
    * quaternion and promoted vector, then multiplying the
    * product by the rotation's inverse.<br>
    * <br>
    * <em>a</em> <em>b</em> := ( <em>a</em> { 0.0, <em>b</em> }
    * ) <em>a</em><sup>-1</sup><br>
    * <br>
    * The result is then demoted to a vector, as the real
    * component should be 0.0 . This is often denoted as <em>P'
    * = RPR'</em>.
    *
    * @param q
    *           the quaternion
    * @param source
    *           the vector
    * @param target
    *           the output vector
    * @return the rotated vector
    */
   public static Vec3 mul (
         final Quaternion q,
         final Vec2 source,
         final Vec3 target ) {

      // TODO: Needs testing...

      final float w = q.real;
      final Vec3 i = q.imag;
      final float qx = i.x;
      final float qy = i.y;
      final float qz = i.z;

      final float iw = -qx * source.x - qy * source.y;
      final float ix = w * source.x - qz * source.y;
      final float iy = w * source.y + qz * source.x;
      final float iz = qx * source.y - qy * source.x;

      return target.set(
            ix * w + iz * qy - iw * qx - iy * qz,
            iy * w + ix * qz - iw * qy - iz * qx,
            iz * w + iy * qx - iw * qz - ix * qy);
   }

   /**
    * Multiplies a vector by a quaternion, in effect rotating
    * the vector by the quaternion. Equivalent to promoting the
    * vector to a pure quaternion, multiplying the rotation
    * quaternion and promoted vector, then multiplying the
    * product by the rotation's inverse.<br>
    * <br>
    * <em>a</em> <em>b</em> := ( <em>a</em> { 0.0, <em>b</em> }
    * ) <em>a</em><sup>-1</sup><br>
    * <br>
    * The result is then demoted to a vector, as the real
    * component should be 0.0 . This is often denoted as <em>P'
    * = RPR'</em>.
    *
    * @param q
    *           the quaternion
    * @param source
    *           the vector
    * @param target
    *           the output vector
    * @return the rotated vector
    */
   public static Vec3 mul (
         final Quaternion q,
         final Vec3 source,
         final Vec3 target ) {

      final float w = q.real;
      final Vec3 i = q.imag;
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
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output quaternion
    * @return the normalized product
    */
   public static Quaternion mulNorm (
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
    * Emits an unscaled quaternion as an output vector.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output quaternion
    * @param product
    *           the product
    * @return the normalized product
    */
   public static Quaternion mulNorm (
         final Quaternion a,
         final Quaternion b,
         final Quaternion target,
         final Quaternion product ) {

      Quaternion.mul(a, b, product);
      Quaternion.normalize(product, target);
      return target;
   }

   /**
    * Divides a quaternion by its magnitude, such that its new
    * magnitude is one and it lies on a 4D hypersphere. Uses
    * the formula: <br>
    * <br>
    * <em>\u00e2</em> = <em>a</em> / |<em>a</em>|
    *
    * @param q
    *           the input quaternion
    * @param target
    *           the output quaternion
    * @return the normalized quaternion
    * @see Quaternion#div(Quaternion, float, Quaternion)
    * @see Quaternion#mag(Quaternion)
    */
   public static Quaternion normalize (
         final Quaternion q,
         final Quaternion target ) {

      return Quaternion.div(q, Quaternion.mag(q), target);
   }

   /**
    * Creates a random unit quaternion. Uses an algorithm by
    * Ken Shoemake, reproduced at this Math Stack Exchange
    * discussion "<a href=
    * "https://math.stackexchange.com/questions/131336/uniform-random-quaternion-in-a-restricted-angle-range">Uniform
    * Random Quaternion In a restricted angle range</a>".
    *
    * @param rng
    *           the random number generator
    * @param target
    *           the output quaternion
    * @return the random quaternion
    * @author Ken Shoemake
    * @see Random#nextDouble()
    * @see Math#sqrt(double)
    * @see Math#sin(double)
    * @see Math#cos(double)
    */
   public static Quaternion random (
         final Random rng,
         final Quaternion target ) {

      final double t0 = IUtils.TAU_D * rng.nextDouble();
      final double t1 = IUtils.TAU_D * rng.nextDouble();

      final double r1 = rng.nextDouble();
      final double x0 = Math.sqrt(1.0d - r1);
      final double x1 = Math.sqrt(r1);

      return target.set(
            (float) (x0 * Math.sin(t0)),
            (float) (x0 * Math.cos(t0)),
            (float) (x1 * Math.sin(t1)),
            (float) (x1 * Math.cos(t1)));
   }

   /**
    * Rotates a quaternion around an arbitrary axis by an
    * angle.
    *
    * @param q
    *           the quaternion
    * @param radians
    *           the angle in radians
    * @param axis
    *           the axis
    * @param target
    *           the output quaternion
    * @return the rotated quaternion
    * @see Quaternion#magSq(Quaternion)
    * @see Math#sqrt(double)
    * @see Utils#acos(float)
    * @see Utils#modRadians(float)
    * @see Quaternion#fromAxisAngle(float, Vec3, Quaternion)
    */
   public static Quaternion rotate (
         final Quaternion q,
         final float radians,
         final Vec3 axis,
         final Quaternion target ) {

      final float mSq = Quaternion.magSq(q);
      if (mSq == 0.0f) {
         return Quaternion.fromAxisAngle(
               radians, axis, target);
      }

      final float wNorm = mSq == 1.0f ? q.real
            : (float) (q.real / Math.sqrt(mSq));
      final float halfAngle = Utils.acos(wNorm);

      return Quaternion.fromAxisAngle(
            Utils.modRadians(
                  halfAngle + halfAngle + radians),
            axis, target);
   }

   /**
    * Rotates a vector around the x axis. Accepts
    * pre-calculated sine and cosine of half the angle so that
    * collections of quaternions can be efficiently rotated
    * without repeatedly calling cos and sin.
    *
    * @param q
    *           the input quaternion
    * @param cosah
    *           cosine of the angle
    * @param sinah
    *           sine of the angle
    * @param target
    *           the output quaternion
    * @return the rotated quaternion
    */
   public static Quaternion rotateX (
         final Quaternion q,
         final float cosah,
         final float sinah,
         final Quaternion target ) {

      final Vec3 i = q.imag;

      return target.set(
            cosah * q.real - sinah * i.x,
            cosah * i.x + sinah * q.real,
            cosah * i.y + sinah * i.z,
            cosah * i.z - sinah * i.y);
   }

   /**
    * Rotates a quaternion about the x axis by an angle.
    *
    * Do not use sequences of ortho-normal rotations by Euler
    * angles; this will result in gimbal lock, defeating the
    * purpose behind a quaternion.
    *
    * @param q
    *           the input quaternion
    * @param radians
    *           the angle in radians
    * @param target
    *           the output quaternion
    * @return the rotated quaternion
    * @see Math#cos(double)
    * @see Math#sin(double)
    */
   public static Quaternion rotateX (
         final Quaternion q,
         final float radians,
         final Quaternion target ) {

      final double halfAngle = radians * 0.5d;
      return Quaternion.rotateX(
            q,
            (float) Math.cos(halfAngle),
            (float) Math.sin(halfAngle),
            target);
   }

   /**
    * Rotates a vector around the y axis. Accepts
    * pre-calculated sine and cosine of half the angle so that
    * collections of quaternions can be efficiently rotated
    * without repeatedly calling cos and sin.
    *
    * @param q
    *           the input quaternion
    * @param cosah
    *           cosine of the angle
    * @param sinah
    *           sine of the angle
    * @param target
    *           the output quaternion
    * @return the rotated quaternion
    */
   public static Quaternion rotateY (
         final Quaternion q,
         final float cosah,
         final float sinah,
         final Quaternion target ) {

      final Vec3 i = q.imag;

      return target.set(
            cosah * q.real - sinah * i.y,
            cosah * i.x - sinah * i.z,
            cosah * i.y + sinah * q.real,
            cosah * i.z + sinah * i.x);
   }

   /**
    * Rotates a quaternion about the y axis by an angle.
    *
    * Do not use sequences of ortho-normal rotations by Euler
    * angles; this will result in gimbal lock, defeating the
    * purpose behind a quaternion.
    *
    * @param q
    *           the input quaternion
    * @param radians
    *           the angle in radians
    * @param target
    *           the output quaternion
    * @return the rotated quaternion
    * @see Math#cos(double)
    * @see Math#sin(double)
    */
   public static Quaternion rotateY (
         final Quaternion q,
         final float radians,
         final Quaternion target ) {

      final double halfAngle = radians * 0.5d;
      return Quaternion.rotateY(
            q,
            (float) Math.cos(halfAngle),
            (float) Math.sin(halfAngle),
            target);
   }

   /**
    * Rotates a vector around the z axis. Accepts
    * pre-calculated sine and cosine of half the angle so that
    * collections of quaternions can be efficiently rotated
    * without repeatedly calling cos and sin.
    *
    * @param q
    *           the input quaternion
    * @param cosah
    *           cosine of the angle
    * @param sinah
    *           sine of the angle
    * @param target
    *           the output quaternion
    * @return the rotated quaternion
    */
   public static Quaternion rotateZ (
         final Quaternion q,
         final float cosah,
         final float sinah,
         final Quaternion target ) {

      final Vec3 i = q.imag;

      return target.set(
            cosah * q.real - sinah * i.z,
            cosah * i.x + sinah * i.y,
            cosah * i.y - sinah * i.x,
            cosah * i.z + sinah * q.real);
   }

   /**
    * Rotates a quaternion about the z axis by an angle.
    *
    * Do not use sequences of ortho-normal rotations by Euler
    * angles; this will result in gimbal lock, defeating the
    * purpose behind a quaternion.
    *
    * @param q
    *           the input quaternion
    * @param radians
    *           the angle in radians
    * @param target
    *           the output quaternion
    * @return the rotated quaternion
    * @see Math#cos(double)
    * @see Math#sin(double)
    */
   public static Quaternion rotateZ (
         final Quaternion q,
         final float radians,
         final Quaternion target ) {

      final double halfAngle = radians * 0.5d;
      return Quaternion.rotateZ(
            q,
            (float) Math.cos(halfAngle),
            (float) Math.sin(halfAngle),
            target);
   }

   /**
    * Sets the comparator function by which collections of
    * quaternions are compared.
    *
    * @param comparator
    *           the comparator
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
    *           the easing function
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
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output quaternion
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
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output quaternion
    * @return the normalized difference
    * @see Vec3#sub(Vec3, Vec3, Vec3)
    * @see Quaternion#magSq(Quaternion)
    * @see Math#sqrt(double)
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
    * normalizes the result. Emits the unscaled difference as
    * an output vector.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output quaternion
    * @param diff
    *           the difference
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
    * Converts a quaternion to three axes, which in turn may
    * constitute a rotation matrix.
    *
    * Use this instead of getRight, getForward and getUp if you
    * need all three axes.
    *
    * @param q
    *           the quaternion
    * @param right
    *           the right axis
    * @param forward
    *           the forward axis
    * @param up
    *           the up axis
    * @see Quaternion#getForward(Quaternion, Vec3)
    * @see Quaternion#getRight(Quaternion, Vec3)
    * @see Quaternion#getUp(Quaternion, Vec3)
    */
   public static void toAxes (
         final Quaternion q,
         final Vec3 right,
         final Vec3 forward,
         final Vec3 up ) {

      final float w = q.real;
      final Vec3 i = q.imag;
      final float x = i.x;
      final float y = i.y;
      final float z = i.z;

      final float x2 = x + x;
      final float y2 = y + y;
      final float z2 = z + z;

      final float xsq2 = x * x2;
      final float ysq2 = y * y2;
      final float zsq2 = z * z2;

      final float xy2 = x * y2;
      final float xz2 = x * z2;
      final float yz2 = y * z2;

      final float wx2 = w * x2;
      final float wy2 = w * y2;
      final float wz2 = w * z2;

      right.set(
            1.0f - ysq2 - zsq2,
            xy2 + wz2,
            xz2 - wy2);

      forward.set(
            xy2 - wz2,
            1.0f - xsq2 - zsq2,
            yz2 + wx2);

      up.set(
            xz2 + wy2,
            yz2 - wx2,
            1.0f - xsq2 - ysq2);
   }

   /**
    * Converts a quaternion to an axis and angle. The angle is
    * returned from the function. The axis is assigned to an
    * output vector.
    *
    * @param q
    *           the quaternion
    * @param axis
    *           the output axis
    * @return the angle
    * @see Quaternion#magSq(Quaternion)
    * @see Vec3#forward(Vec3)
    * @see Math#abs(float)
    * @see Math#sqrt(double)
    * @see Math#acos(double)
    */
   public static float toAxisAngle (
         final Quaternion q,
         final Vec3 axis ) {

      final float mSq = Quaternion.magSq(q);

      if (mSq == 0.0f) {
         Vec3.forward(axis);
         return 0.0f;
      }

      double wNorm;
      // float xNorm;
      // float yNorm;
      // float zNorm;
      final Vec3 i = q.imag;

      if (Math.abs(1.0d - mSq) < Utils.EPSILON) {
         final double mInv = 1.0d / Math.sqrt(mSq);
         wNorm = q.real * mInv;
         // xNorm = i.x * mInv;
         // yNorm = i.y * mInv;
         // zNorm = i.z * mInv;
      } else {
         wNorm = q.real;
         // xNorm = i.x;
         // yNorm = i.y;
         // zNorm = i.z;
      }

      final double angle = wNorm <= -1.0d ? IUtils.TAU_D
            : wNorm >= 1.0d ? 0.0d : 2.0d * Math.acos(wNorm);
      final double wAsin = IUtils.TAU_D - angle;
      if (wAsin == 0.0d) {
         Vec3.forward(axis);
         return (float) angle;
      }

      final double sInv = 1.0d / wAsin;
      // final float ax = xNorm * sInv;
      // final float ay = yNorm * sInv;
      // final float az = zNorm * sInv;
      final double ax = i.x * sInv;
      final double ay = i.y * sInv;
      final double az = i.z * sInv;

      final double amSq = ax * ax + ay * ay + az * az;

      if (amSq == 0.0d) {
         Vec3.forward(axis);
         return (float) angle;
      }

      if (Math.abs(1.0d - amSq) < Utils.EPSILON) {
         axis.set(
               (float) ax,
               (float) ay,
               (float) az);
         return (float) angle;
      }

      final double mInv = 1.0d / Math.sqrt(amSq);
      axis.set(
            (float) (ax * mInv),
            (float) (ay * mInv),
            (float) (az * mInv));
      return (float) angle;
   }

   /**
    * The coefficients of the imaginary components <em>i</em>,
    * <em>j</em> and <em>k</em>.
    */
   public final Vec3 imag = new Vec3();

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

   /**
    * Constructs a quaternion from boolean values.
    *
    * @param real
    *           the w component
    * @param xImag
    *           the x component
    * @param yImag
    *           the y component
    * @param zImag
    *           the z component
    */
   public Quaternion (
         final boolean real,
         final boolean xImag,
         final boolean yImag,
         final boolean zImag ) {

      super(4);
      this.set(real, xImag, yImag, zImag);
   }

   /**
    * Constructs a quaternion by float component.
    *
    * @param real
    *           the real (w) component
    * @param xImag
    *           the x component
    * @param yImag
    *           the y component
    * @param zImag
    *           the z component
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
    *           the real component
    * @param imag
    *           the imaginary component
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
    *           the source quaternion
    */
   public Quaternion ( final Quaternion source ) {

      super(4);
      this.set(source);
   }

   /**
    * Attempts to construct a quaternion from Strings using
    * {@link Float#parseFloat(String)} . If a
    * NumberFormatException is thrown, the component is set to
    * zero.
    *
    * @param real
    *           the w string
    * @param xImag
    *           the x string
    * @param yImag
    *           the y string
    * @param zImag
    *           the z string
    */
   public Quaternion (
         final String real,
         final String xImag,
         final String yImag,
         final String zImag ) {

      super(4);
      this.set(real, xImag, yImag, zImag);
   }

   /**
    * Tests equivalence between this and another quaternion.
    * For rough equivalence of floating point components, use
    * the static approx function instead.
    *
    * @param q
    *           the quaternion
    * @return the evaluation
    * @see Quaternion#approx(Quaternion, Quaternion)
    * @see Quaternion#approx(Quaternion, Quaternion, float)
    */
   protected boolean equals ( final Quaternion q ) {

      if (this.imag == null) {
         if (q.imag != null) {
            return false;
         }
      } else if (!this.imag.equals(q.imag)) {
         return false;
      }
      if (Float.floatToIntBits(this.real) != Float.floatToIntBits(q.real)) {
         return false;
      }
      return true;
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
    * @param q
    *           the comparisand
    * @return the numeric code
    * @see Quaternion#COMPARATOR
    */
   @Override
   public int compareTo ( final Quaternion q ) {

      return Quaternion.COMPARATOR.compare(this, q);
   }

   /**
    * Tests this quaternion for equivalence with another
    * object.
    *
    * @param obj
    *           the object
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
    *           the index
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
    *           the index
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
    *           the index
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
      result = prime * result + Float.floatToIntBits(this.real);
      result = prime * result
            + (this.imag == null ? 0 : this.imag.hashCode());
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
    * Sets the components of this quaternion from booleans,
    * where false is 0.0 and true is 1.0 .
    *
    * @param real
    *           the real (w) component
    * @param xImag
    *           the imag x component
    * @param yImag
    *           the imag y component
    * @param zImag
    *           the imag z component
    * @return this quaternion
    */
   @Chainable
   public Quaternion set (
         final boolean real,
         final boolean xImag,
         final boolean yImag,
         final boolean zImag ) {

      this.real = Utils.toFloat(real);
      this.imag.set(xImag, yImag, zImag);
      return this;
   }

   /**
    * Sets the components of this vector.
    *
    * @param real
    *           the real (w) component
    * @param xImag
    *           the imag x component
    * @param yImag
    *           the imag y component
    * @param zImag
    *           the imag z component
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

   /**
    * Sets the components of this quaternion.
    *
    * @param real
    *           the real component
    * @param imag
    *           the imaginary component
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
    *           the input quaternion
    * @return this quaternion
    */
   @Chainable
   public Quaternion set ( final Quaternion source ) {

      this.real = source.real;
      this.imag.set(source.imag);
      return this;
   }

   /**
    * Attempts to set the components of this quaternion from
    * Strings using {@link Float#parseFloat(String)} . If a
    * NumberFormatException is thrown, the component is set to
    * zero.
    *
    * @param wstr
    *           the w string
    * @param xstr
    *           the x string
    * @param ystr
    *           the y string
    * @param zstr
    *           the z string
    * @return this quaternion
    * @see Float#parseFloat(String)
    */
   @Chainable
   public Quaternion set (
         final String wstr,
         final String xstr,
         final String ystr,
         final String zstr ) {

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
    *           issue w as the first element
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

   /**
    * Returns a string representation of this quaternion
    * according to the string format.
    *
    * @param places
    *           the number of places
    * @return the string
    */
   public String toString ( final int places ) {

      return new StringBuilder(128)
            .append("{ real: ")
            .append(Utils.toFixed(this.real, places))
            .append(", imag: ")
            .append(this.imag.toString(places))
            .append(' ').append('}')
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
    *           the real value
    * @return this quaternion
    */
   public Quaternion w ( final float w ) {

      this.real = w;
      return this;
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
    *           the x value
    * @return this quaternion
    */
   public Quaternion x ( final float x ) {

      this.imag.x = x;
      return this;
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
    *           the y value
    * @return this quaternion
    */
   public Quaternion y ( final float y ) {

      this.imag.y = y;
      return this;
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
    *           the z value
    * @return this quaternion
    */
   public Quaternion z ( final float z ) {

      this.imag.z = z;
      return this;
   }
}
