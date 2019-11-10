package camzup.core;

import java.util.Comparator;
import java.util.Iterator;

/**
 * A mutable, extensible class influenced by GLSL. This is
 * intended to serve as a parent class for colors. Instance
 * methods are limited, while most static methods require an
 * explicit output variable to be provided.
 */
public class Vec4 extends Vec implements Comparable < Vec4 > {

   /**
    * An abstract class that may serve as an umbrella for any
    * custom comparators of Vec4 s.
    */
   public static abstract class AbstrComparator implements Comparator < Vec4 > {

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
      public abstract int compare ( final Vec4 a, final Vec4 b );

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
    * Compares two vectors by their w, z, y and then x
    * components.
    */
   public static class ComparatorWZYX extends AbstrComparator {

      /**
       * The default constructor.
       */
      public ComparatorWZYX () {

         super();
      }

      /**
       * Compares the w, z, y and x component.
       *
       * @param a
       *           the left operand
       * @param b
       *           the right operand
       * @return the comparison
       */
      @Override
      public int compare ( final Vec4 a, final Vec4 b ) {

         return a.w > b.w ? 1
               : a.w < b.w ? -1
                     : a.z > b.z ? 1
                           : a.z < b.z ? -1
                                 : a.y > b.y ? 1
                                       : a.y < b.y ? -1
                                             : a.x > b.x ? 1
                                                   : a.x < b.x ? -1 : 0;
      }
   }

   /**
    * An iterator, which allows a vector's components to be
    * accessed in an enhanced for loop.
    */
   public static final class V4Iterator implements Iterator < Float > {

      /**
       * The current index.
       */
      private int index = 0;

      /**
       * The vector being iterated over.
       */
      private final Vec4 vec;

      /**
       * The default constructor.
       *
       * @param vec
       *           the vector to iterate
       */
      public V4Iterator ( final Vec4 vec ) {

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
       * @see Vec4#get(int)
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
    * The unique identification for serialized classes.
    */
   private static final long serialVersionUID = -7601802836396728054L;

   /**
    * The default Vec4 comparator, ComparatorWZYX .
    */
   public static Comparator < Vec4 > COMPARATOR = new ComparatorWZYX();

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
   public static Vec4 abs (
         final Vec4 v,
         final Vec4 target ) {

      return target.set(
            Utils.abs(v.x),
            Utils.abs(v.y),
            Utils.abs(v.z),
            Utils.abs(v.w));
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
   public static Vec4 add (
         final Vec4 a,
         final Vec4 b,
         final Vec4 target ) {

      return target.set(
            a.x + b.x,
            a.y + b.y,
            a.z + b.z,
            a.w + b.w);
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
         final Vec4 a,
         final Vec4 b ) {

      return Utils.approxFast(a.w, b.w)
            && Utils.approxFast(a.z, b.z)
            && Utils.approxFast(a.y, b.y)
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
         final Vec4 a,
         final Vec4 b,
         final float tolerance ) {

      return Utils.approxFast(a.w, b.w, tolerance)
            && Utils.approxFast(a.z, b.z, tolerance)
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
    * @see Vec4#dot(Vec4, Vec4)
    */
   public static boolean approxMag (
         final Vec4 a,
         final float b ) {

      return Utils.approxFast(Vec4.magSq(a), b * b);
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
    * @see Vec4#dot(Vec4, Vec4)
    */
   public static boolean approxMag (
         final Vec4 a,
         final float b,
         final float tolerance ) {

      return Utils.approxFast(Vec4.magSq(a), b * b, tolerance);
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
   public static Vec4 ceil (
         final Vec4 v,
         final Vec4 target ) {

      return target.set(
            Utils.ceil(v.x),
            Utils.ceil(v.y),
            Utils.ceil(v.z),
            Utils.ceil(v.w));
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
   public static Vec4 clamp (
         final Vec4 v,
         final Vec4 lowerBound,
         final Vec4 upperBound,
         final Vec4 target ) {

      return target.set(
            Utils.clamp(v.x, lowerBound.x, upperBound.x),
            Utils.clamp(v.y, lowerBound.y, upperBound.y),
            Utils.clamp(v.z, lowerBound.z, upperBound.z),
            Utils.clamp(v.w, lowerBound.w, upperBound.w));
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
   public static Vec4 clamp01 (
         final Vec4 v,
         final Vec4 target ) {

      return target.set(
            Utils.clamp01(v.x),
            Utils.clamp01(v.y),
            Utils.clamp01(v.z),
            Utils.clamp01(v.w));
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
   public static Vec4 copySign (
         final Vec4 magnitude,
         final Vec4 sign,
         final Vec4 target ) {

      return target.set(
            Math.copySign(magnitude.x, sign.x),
            Math.copySign(magnitude.y, sign.y),
            Math.copySign(magnitude.z, sign.z),
            Math.copySign(magnitude.w, sign.w));
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
   public static Vec4 diff (
         final Vec4 a,
         final Vec4 b,
         final Vec4 target ) {

      return target.set(
            Utils.diff(a.x, b.x),
            Utils.diff(a.y, b.y),
            Utils.diff(a.z, b.z),
            Utils.diff(a.w, b.w));
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
   public static Vec4 div (
         final float a,
         final Vec4 b,
         final Vec4 target ) {

      return target.set(
            Utils.div(a, b.x),
            Utils.div(a, b.y),
            Utils.div(a, b.z),
            Utils.div(a, b.w));
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
   public static Vec4 div (
         final Vec4 a,
         final float b,
         final Vec4 target ) {

      if (b == 0.0f) {
         return target.reset();
      }

      final float denom = 1.0f / b;
      return target.set(
            a.x * denom,
            a.y * denom,
            a.z * denom,
            a.w * denom);
   }

   /**
    * Divides the left operand by the right, component-wise.
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
   public static Vec4 div (
         final Vec4 a,
         final Vec4 b,
         final Vec4 target ) {

      return target.set(
            Utils.div(a.x, b.x),
            Utils.div(a.y, b.y),
            Utils.div(a.z, b.z),
            Utils.div(a.w, b.w));
   }

   /**
    * Finds the dot product of two vectors by summing the
    * products of their corresponding components. <em>a</em>
    * \u00b7 <em>b</em> := <em>a<sub>x</sub> b<sub>x</sub></em>
    * + <em>a<sub>y</sub> b<sub>y</sub></em> +
    * <em>a<sub>z</sub> b<sub>z</sub></em> + <em>a<sub>w</sub>
    * b<sub>w</sub></em><br>
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
   public static float dot (
         final Vec4 a,
         final Vec4 b ) {

      return a.x * b.x +
            a.y * b.y +
            a.z * b.z +
            a.w * b.w;
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
   public static Vec4 floor (
         final Vec4 v,
         final Vec4 target ) {

      return target.set(
            Utils.floor(v.x),
            Utils.floor(v.y),
            Utils.floor(v.z),
            Utils.floor(v.w));
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
   public static Vec4 fmod (
         final float a,
         final Vec4 b,
         final Vec4 target ) {

      return target.set(
            Utils.fmod(a, b.x),
            Utils.fmod(a, b.y),
            Utils.fmod(a, b.z),
            Utils.fmod(a, b.w));
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
   public static Vec4 fmod (
         final Vec4 a,
         final float b,
         final Vec4 target ) {

      if (b == 0.0f) {
         return target.set(a);
      }

      return target.set(
            a.x % b,
            a.y % b,
            a.z % b,
            a.w % b);
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
   public static Vec4 fmod (
         final Vec4 a,
         final Vec4 b,
         final Vec4 target ) {

      return target.set(
            Utils.fmod(a.x, b.x),
            Utils.fmod(a.y, b.y),
            Utils.fmod(a.z, b.z),
            Utils.fmod(a.w, b.w));
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
   public static Vec4 fract (
         final Vec4 v,
         final Vec4 target ) {

      return target.set(
            Utils.fract(v.x),
            Utils.fract(v.y),
            Utils.fract(v.z),
            Utils.fract(v.w));
   }

   /**
    * Gets the string representation of the default Vec4
    * comparator.
    *
    * @return the string
    */
   public static String getComparatorString () {

      return Vec4.COMPARATOR.toString();
   }

   /**
    * Tests to see if all the vector's components are non-zero.
    *
    * @param v
    *           the input vector
    * @return the evaluation
    */
   public static boolean isNonZero ( final Vec4 v ) {

      return v.x != 0.0f &&
            v.y != 0.0f &&
            v.z != 0.0f &&
            v.w != 0.0f;
   }

   /**
    * Tests to see if the vector is on the unit hypersphere,
    * i.e., has a magnitude of approximately 1.0.
    *
    * @param v
    *           the input vector
    * @return the evaluation
    * @see Utils#approxFast(float, float)
    * @see Vec4#dot(Vec4, Vec4)
    */
   public static boolean isUnit ( final Vec4 v ) {

      return Utils.approxFast(Vec4.magSq(v), 1.0f);
   }

   /**
    * Tests to see if all the vector's components are zero.
    *
    * @param v
    *           the input vector
    * @return the evaluation
    */
   public static boolean isZero ( final Vec4 v ) {

      return v.x == 0.0f &&
            v.y == 0.0f &&
            v.z == 0.0f &&
            v.w == 0.0f;
   }

   /**
    * Finds the length, or magnitude, of a vector, |<em>a</em>|
    * . Uses the formula \u221a <em>a</em> \u00b7 <em>a</em> .
    * Where possible, use magSq or dot to avoid the
    * computational cost of the square-root.
    *
    * @param v
    *           the input vector
    * @return the magnitude
    * @see Vec4#dot(Vec4, Vec4)
    * @see Math#sqrt(double)
    * @see Vec4#magSq(Vec4)
    */
   public static float mag ( final Vec4 v ) {

      return (float) Math.sqrt(Vec4.magSq(v));
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
    * @see Vec4#dot(Vec4, Vec4)
    * @see Vec4#mag(Vec4)
    */
   public static float magSq ( final Vec4 v ) {

      return v.x * v.x + v.y * v.y + v.z * v.z + v.w * v.w;
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
   public static Vec4 map (
         final Vec4 v,
         final Vec4 lbOrigin,
         final Vec4 ubOrigin,
         final Vec4 lbDest,
         final Vec4 ubDest,
         final Vec4 target ) {

      return target.set(
            Utils.map(v.x, lbOrigin.x, ubOrigin.x, lbDest.x, ubDest.x),
            Utils.map(v.y, lbOrigin.y, ubOrigin.y, lbDest.y, ubDest.y),
            Utils.map(v.z, lbOrigin.z, ubOrigin.z, lbDest.z, ubDest.z),
            Utils.map(v.w, lbOrigin.w, ubOrigin.w, lbDest.w, ubDest.w));
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
   public static Vec4 mod (
         final float a,
         final Vec4 b,
         final Vec4 target ) {

      return target.set(
            Utils.mod(a, b.x),
            Utils.mod(a, b.y),
            Utils.mod(a, b.z),
            Utils.mod(a, b.w));
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
   public static Vec4 mod (
         final Vec4 a,
         final float b,
         final Vec4 target ) {

      if (b == 0.0f) {
         return target.set(a);
      }

      return target.set(
            Utils.modUnchecked(a.x, b),
            Utils.modUnchecked(a.y, b),
            Utils.modUnchecked(a.z, b),
            Utils.modUnchecked(a.w, b));
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
   public static Vec4 mod (
         final Vec4 a,
         final Vec4 b,
         final Vec4 target ) {

      return target.set(
            Utils.mod(a.x, b.x),
            Utils.mod(a.y, b.y),
            Utils.mod(a.z, b.z),
            Utils.mod(a.w, b.w));
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
   public static Vec4 mod1 (
         final Vec4 v,
         final Vec4 target ) {

      return target.set(
            Utils.mod1(v.x),
            Utils.mod1(v.y),
            Utils.mod1(v.z),
            Utils.mod1(v.w));
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
   public static Vec4 mult (
         final float a,
         final Vec4 b,
         final Vec4 target ) {

      return target.set(
            a * b.x,
            a * b.y,
            a * b.z,
            a * b.w);
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
   public static Vec4 mult (
         final Vec4 a,
         final float b,
         final Vec4 target ) {

      return target.set(
            a.x * b,
            a.y * b,
            a.z * b,
            a.w * b);
   }

   /**
    * Multiplies two vectors, component-wise.
    *
    * @param a
    *           left operand
    * @param b
    *           right operand
    * @param target
    *           the output vector
    * @return the product
    */
   public static Vec4 mult (
         final Vec4 a,
         final Vec4 b,
         final Vec4 target ) {

      return target.set(
            a.x * b.x,
            a.y * b.y,
            a.z * b.z,
            a.w * b.w);
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
   public static Vec4 negate (
         final Vec4 v,
         final Vec4 target ) {

      return target.set(-v.x, -v.y, -v.z, -v.w);
   }

   /**
    * Divides a vector by its magnitude, such that the new
    * magnitude is 1.0. <em>\u00e2</em> = <em>a</em> /
    * |<em>a</em>|. The result is a unit vector, as it lies on
    * the unit hypersphere.
    *
    * @param v
    *           the input vector
    * @param target
    *           the output vector
    * @return the unit vector
    * @see Vec4#div(Vec4, float, Vec4)
    * @see Vec4#mag(Vec4)
    */
   public static Vec4 normalize (
         final Vec4 v,
         final Vec4 target ) {

      final float mSq = v.x * v.x +
            v.y * v.y +
            v.z * v.z +
            v.w * v.w;

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
            v.z * mInv,
            v.w * mInv);
   }

   /**
    * Returns a vector with both components set to one.
    *
    * @param target
    *           the output vector
    * @return one
    */
   public static Vec4 one ( final Vec4 target ) {

      return target.set(1.0f, 1.0f, 1.0f, 1.0f);
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
   public static Vec4 pow (
         final float a,
         final Vec4 b,
         final Vec4 target ) {

      return target.set(
            (float) Math.pow(a, b.x),
            (float) Math.pow(a, b.y),
            (float) Math.pow(a, b.z),
            (float) Math.pow(a, b.w));
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
   public static Vec4 pow (
         final Vec4 a,
         final float b,
         final Vec4 target ) {

      return target.set(
            (float) Math.pow(a.x, b),
            (float) Math.pow(a.y, b),
            (float) Math.pow(a.z, b),
            (float) Math.pow(a.w, b));
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
   public static Vec4 pow (
         final Vec4 a,
         final Vec4 b,
         final Vec4 target ) {

      return target.set(
            (float) Math.pow(a.x, b.x),
            (float) Math.pow(a.y, b.y),
            (float) Math.pow(a.z, b.z),
            (float) Math.pow(a.w, b.w));
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
   public static Vec4 round (
         final Vec4 v,
         final Vec4 target ) {

      return target.set(
            Math.round(v.x),
            Math.round(v.y),
            Math.round(v.z),
            Math.round(v.w));
   }

   /**
    * Sets the comparator function by which collections of
    * vectors are compared.
    *
    * @param comparator
    *           the comparator
    */
   public static void setComparator ( final Comparator < Vec4 > comparator ) {

      Vec4.COMPARATOR = comparator;
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
   public static Vec4 sub (
         final Vec4 a,
         final Vec4 b,
         final Vec4 target ) {

      return target.set(
            a.x - b.x,
            a.y - b.y,
            a.z - b.z,
            a.w - b.w);
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
   public static Vec4 trunc (
         final Vec4 v,
         final Vec4 target ) {

      return target.set(
            (int) v.x,
            (int) v.y,
            (int) v.z,
            (int) v.w);
   }

   /**
    * Returns a vector with all components set to zero.
    *
    * @param target
    *           the output vector
    * @return the zero vector
    */
   public static Vec4 zero ( final Vec4 target ) {

      return target.set(0.0f, 0.0f, 0.0f, 0.0f);
   }

   /**
    * Component on the w axis. Commonly used to store 1.0 for
    * points and 0.0 for vectors when multiplying with a 4 x 4
    * matrix. Also used to store alpha (transparency) for
    * colors.
    */
   public float w = 0.0f;

   /**
    * Component on the x axis.
    */
   public float x = 0.0f;

   /**
    * Component on the y axis.
    */
   public float y = 0.0f;

   /**
    * Component on the z axis.
    */
   public float z = 0.0f;

   /**
    * The default vector constructor.
    */
   public Vec4 () {

      super(4);
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
    * @param w
    *           the w component
    */
   public Vec4 (
         final boolean x,
         final boolean y,
         final boolean z,
         final boolean w ) {

      super(4);
      this.set(x, y, z, w);
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
    * @param w
    *           the w component
    */
   public Vec4 (
         final float x,
         final float y,
         final float z,
         final float w ) {

      super(4);
      this.set(x, y, z, w);
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
    * @param wstr
    *           the w string
    * @see Float#parseFloat(String)
    */
   public Vec4 (
         final String xstr,
         final String ystr,
         final String zstr,
         final String wstr ) {

      super(4);
      this.set(xstr, ystr, zstr, wstr);
   }

   /**
    * Promotes a Vec3 to a Vec4 with an extra component.
    *
    * @param v3
    *           the vector
    * @param w
    *           the w component
    */
   public Vec4 ( final Vec3 v3, final float w ) {

      super(4);
      this.set(v3, w);
   }

   /**
    * Constructs a vector from a source vector's components.
    *
    * @param source
    *           the source vector
    */
   public Vec4 ( final Vec4 source ) {

      super(4);
      this.set(source);
   }

   /**
    * Tests equivalence between this and another vector.
    *
    * @param v
    *           the vector
    * @return the evaluation
    * @see Float#floatToIntBits(float)
    */
   protected boolean equals ( final Vec4 v ) {

      if (Float.floatToIntBits(this.w) != Float.floatToIntBits(v.w)) {
         return false;
      }

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
    * @see Vec4#set(Vec4)
    * @see Vec4#Vec4(Vec4)
    */
   @Override
   public Vec4 clone () {

      return new Vec4(
            this.x,
            this.y,
            this.z,
            this.w);
   }

   /**
    * Returns -1 when this vector is less than the comparisand;
    * 1 when it is greater than; 0 when the two are 'equal'.
    * The implementation of this method allows collections of
    * vectors to be sorted. This depends upon the static
    * comparator of the Vec4 class, which can be changed.
    *
    * @param v
    *           the comparisand
    * @return the numeric code
    * @see Vec4#COMPARATOR
    */
   @Override
   public int compareTo ( final Vec4 v ) {

      return Vec4.COMPARATOR.compare(this, v);
   }

   /**
    * Tests this vector for equivalence with another object.
    *
    * @param obj
    *           the object
    * @return the equivalence
    * @see Vec4#equals(Vec4)
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
      return this.equals((Vec4) obj);

   }

   /**
    * Simulates bracket subscript access in an array. When the
    * provided index is 3 or -1, returns w; 2 or -2, z; 1 or
    * -3, y; 0 or -4, x.
    *
    * @param index
    *           the index
    * @return the component at that index
    */
   @Override
   public float get ( final int index ) {

      switch (index) {
         case 0:
         case -4:
            return this.x;
         case 1:
         case -3:
            return this.y;
         case 2:
         case -2:
            return this.z;
         case 3:
         case -1:
            return this.w;
         default:
            return 0.0f;
      }
   }

   /**
    * Returns a hash code for this vector based on its x, y, z
    * and w components.
    *
    * @return the hash code
    * @see Float#floatToIntBits(float)
    */
   @Override
   public int hashCode () {

      final int prime = 31;
      int result = 1;
      result = prime * result + Float.floatToIntBits(this.w);
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
   public V4Iterator iterator () {

      return new V4Iterator(this);
   }

   /**
    * Resets this vector to an initial state, ( 0.0, 0.0, 0.0,
    * 0.0 ) .
    *
    * @return this vector
    */
   @Chainable
   public Vec4 reset () {

      return this.set(0.0f, 0.0f, 0.0f, 0.0f);
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
    * @param w
    *           the w component
    * @return this vector
    * @see Utils#toFloat(boolean)
    */
   @Chainable
   public Vec4 set (
         final boolean x,
         final boolean y,
         final boolean z,
         final boolean w ) {

      this.x = Utils.toFloat(x);
      this.y = Utils.toFloat(y);
      this.z = Utils.toFloat(z);
      this.w = Utils.toFloat(w);
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
    * @param w
    *           the w component
    * @return this vector
    */
   @Chainable
   public Vec4 set (
         final float x,
         final float y,
         final float z,
         final float w ) {

      this.x = x;
      this.y = y;
      this.z = z;
      this.w = w;
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
    * @param wstr
    *           the w string
    * @return this vector
    * @see Float#parseFloat(String)
    */
   @Chainable
   public Vec4 set (
         final String xstr,
         final String ystr,
         final String zstr,
         final String wstr ) {

      float x = 0.0f;
      float y = 0.0f;
      float z = 0.0f;
      float w = 0.0f;

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

      try {
         w = Float.parseFloat(wstr);
      } catch (final NumberFormatException e) {
         w = 0.0f;
      }

      this.x = x;
      this.y = y;
      this.z = z;
      this.w = w;

      return this;
   }

   /**
    * Promotes a Vec3 to a Vec4 with an extra component.
    *
    * @param v3
    *           the vector
    * @param w
    *           the w component
    * @return this vector
    */
   public Vec4 set ( final Vec3 v3, final float w ) {

      return this.set(v3.x, v3.y, v3.z, w);
   }

   /**
    * Copies the components of the input vector to this vector.
    *
    * @param source
    *           the input vector
    * @return this vector
    */
   @Chainable
   public Vec4 set ( final Vec4 source ) {

      return this.set(
            source.x,
            source.y,
            source.z,
            source.w);
   }

   /**
    * Returns a float array of length 4 containing this
    * vector's components.
    *
    * @return the array
    */
   @Override
   public float[] toArray () {

      return new float[] {
            this.x, this.y, this.z, this.w
      };
   }

   /**
    * Returns a string representation of this vector according
    * to the string format.
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

      return new StringBuilder(96)
            .append("{ x: ")
            .append(Utils.toFixed(this.x, places))
            .append(", y: ")
            .append(Utils.toFixed(this.y, places))
            .append(", z: ")
            .append(Utils.toFixed(this.z, places))
            .append(", z: ")
            .append(Utils.toFixed(this.w, places))
            .append(' ').append('}')
            .toString();
   }
}
