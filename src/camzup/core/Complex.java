package camzup.core;

import java.util.Comparator;
import java.util.Iterator;

/**
 * A two-dimensional complex number.
 */
public class Complex extends Imaginary implements Comparable < Complex > {

   /**
    * An abstract class that may serve as an umbrella for any
    * custom comparators of complex numbers.
    */
   public static abstract class AbstrComparator
         implements Comparator < Complex > {

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
      public abstract int compare (
            final Complex a,
            final Complex b );

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
    * An iterator, which allows a complex number's components
    * to be accessed in an enhanced for loop.
    */
   public final class CIterator implements Iterator < Float > {

      /**
       * The current index.
       */
      private int index = 0;

      /**
       * The complex number being iterated over.
       */
      private final Complex z;

      /**
       * The default constructor.
       *
       * @param z
       *           the complex number to iterator
       */
      public CIterator ( final Complex z ) {

         this.z = z;
      }

      /**
       * Tests to see if the iterator has another value.
       *
       * @return the evaluation
       */
      @Override
      public boolean hasNext () {

         return this.index < this.z.size();
      }

      /**
       * Gets the next value in the iterator
       *
       * @return the value
       * @see Complex#get(int)
       */
      @Override
      public Float next () {

         return this.z.get(this.index++);
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
    * Compares two complex numbers by their imaginary and real
    * components.
    */
   public static class ComparatorRealImag extends AbstrComparator {

      /**
       * The default constructor.
       */
      public ComparatorRealImag () {

         super();
      }

      /**
       * Compares two complex numbers by imaginary, then by real
       * components.
       *
       * @param a
       *           the left comparisand
       * @param b
       *           the right comparisand
       * @return the evaluation
       */
      @Override
      public int compare ( final Complex a, final Complex b ) {

         return a.imag > b.imag ? 1
               : a.imag < b.imag ? -1
                     : a.real > b.real ? 1
                           : a.real < b.real ? -1 : 0;
      }
   }

   /**
    * The default complex comparator.
    */
   private static Comparator < Complex > COMPARATOR = new ComparatorRealImag();

   /**
    * The unique identification for serialized classes.
    */
   private static final long serialVersionUID = 1389157472482304159L;

   /**
    * Finds the absolute of a complex number. Similar to a
    * vector's magnitude.
    *
    * @param z
    *           the complex number
    * @return the absolute
    */
   public static float abs ( final Complex z ) {

      return (float) Math.sqrt(Complex.absSq(z));
   }

   /**
    * Finds the absolute squared of a complex number. Similar
    * to a vector's magnitude squared.
    *
    * @param z
    *           the complex number
    * @return the absolute squared
    */
   public static float absSq ( final Complex z ) {

      return z.real * z.real + z.imag * z.imag;
   }

   /**
    * Adds two complex numbers.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output complex quaternion
    * @return the sum
    */
   public static Complex add (
         final Complex a,
         final Complex b,
         final Complex target ) {

      return target.set(
            a.real + b.real,
            a.imag + b.imag);
   }

   /**
    * Adds a complex and real number.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output complex quaternion
    * @return the sum
    */
   public static Complex add (
         final Complex a,
         final float b,
         final Complex target ) {

      return target.set(a.real + b, a.imag);
   }

   /**
    * Adds a real and complex number.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output complex quaternion
    * @return the sum
    */
   public static Complex add (
         final float a,
         final Complex b,
         final Complex target ) {

      return target.set(a + b.real, b.imag);
   }

   /**
    * Evaluates whether or not two complex numbers approximate
    * each other.
    *
    * @param a
    *           the left comparisand
    * @param b
    *           the right comparisand
    * @return the evaluation
    * @see Utils#approxFast(float, float)
    */
   public static boolean approx (
         final Complex a,
         final Complex b ) {

      return Utils.approxFast(a.imag, b.imag)
            && Utils.approxFast(a.real, b.real);
   }

   /**
    * Evaluates whether or not two complex numbers approximate
    * each other according to a tolerance.
    *
    * @param a
    *           the left comparisand
    * @param b
    *           the right comparisand
    * @param tolerance
    *           the tolerance
    * @return the evaluation
    * @see Utils#approxFast(float, float, float)
    */
   public static boolean approx (
         final Complex a,
         final Complex b,
         final float tolerance ) {

      return Utils.approxFast(a.imag, b.imag, tolerance)
            && Utils.approxFast(a.real, b.real, tolerance);
   }

   /**
    * Tests to see if a complex number has, approximately, the
    * specified absolute.
    *
    * @param z
    *           the complex number
    * @param abs
    *           the absolute
    * @return the evaluation
    */
   public static boolean approxAbs (
         final Complex z,
         final float abs ) {

      return Utils.approxFast(Complex.absSq(z), abs * abs);
   }

   /**
    * Finds the conjugate of the complex number, where the
    * imaginary component is negates.
    *
    * @param z
    *           the input complex number
    * @param target
    *           the output complex number
    * @return the conjugate
    */
   public static Complex conj (
         final Complex z,
         final Complex target ) {

      return target.set(z.real, -z.imag);
   }

   /**
    * Finds the cosine of a complex number.
    *
    * @param z
    *           the complex number
    * @param target
    *           the output complex number
    * @return the cosine
    */
   public static Complex cos (
         final Complex z,
         final Complex target ) {

      return target.set(
            (float) (Math.cos(z.real) * Math.cosh(z.imag)),
            (float) (-Math.sin(z.real) * Math.sinh(z.imag)));
   }

   /**
    * Divides one complex number by another.
    *
    * @param a
    *           the numerator
    * @param b
    *           the denominator
    * @param target
    *           the output complex number
    * @return the quotient
    * @see Complex#absSq(Complex)
    */
   public static Complex div (
         final Complex a,
         final Complex b,
         final Complex target ) {

      final float bAbsSq = Complex.absSq(b);
      if (bAbsSq == 0.0f) {
         return target.reset();
      }

      final float bInvAbsSq = 1.0f / bAbsSq;
      final float cReal = b.real * bInvAbsSq;
      final float cImag = -b.imag * bInvAbsSq;
      return target.set(
            a.real * cReal - a.imag * cImag,
            a.real * cImag + a.imag * cReal);
   }

   /**
    * Divides one complex number by another. Equivalent to
    * multiplying the numerator and the inverse of the
    * denominator.
    *
    * @param a
    *           the numerator
    * @param b
    *           the denominator
    * @param target
    *           the output complex number
    * @param inverse
    *           the inverse
    * @param conj
    *           the conjugate
    * @return the quotient
    * @see Complex#inverse(Complex, Complex, Complex)
    * @see Complex#mult(Complex, Complex, Complex)
    */
   public static Complex div (
         final Complex a,
         final Complex b,
         final Complex target,
         final Complex inverse,
         final Complex conj ) {

      Complex.inverse(b, inverse, conj);
      return Complex.mult(a, inverse, target);
   }

   /**
    * Divides a complex number by a real number.
    *
    * @param a
    *           the numerator
    * @param b
    *           the denominator
    * @param target
    *           the output complex number
    * @return the quotient
    */
   public static Complex div (
         final Complex a,
         final float b,
         final Complex target ) {

      if (b == 0.0f) {
         return target.reset();
      }

      final float bInv = 1.0f / b;
      return target.set(
            a.real * bInv,
            a.imag * bInv);
   }

   /**
    * Divides a real number by a complex number.
    *
    * @param a
    *           the numerator
    * @param b
    *           the denominator
    * @param target
    *           the output complex number
    * @return the quotient
    * @see Complex#absSq(Complex)
    */
   public static Complex div (
         final float a,
         final Complex b,
         final Complex target ) {

      final float bAbsSq = Complex.absSq(b);
      if (bAbsSq == 0.0f) {
         return target.reset();
      }

      final float abInvAbsSq = a / bAbsSq;
      return target.set(
            b.real * abInvAbsSq,
            -b.imag * abInvAbsSq);
   }

   /**
    * Returns Euler's number, <em>e</em>, raised to a complex
    * number.
    *
    * @param z
    *           the complex number
    * @param target
    *           the output complex number
    * @return the result
    * @see Math#exp(double)
    * @see Math#cos(double)
    * @see Math#sin(double)
    */
   public static Complex exp (
         final Complex z,
         final Complex target ) {

      final double r = Math.exp(z.real);
      return target.set(
            (float) (r * Math.cos(z.imag)),
            (float) (r * Math.sin(z.imag)));
   }

   /**
    * Gets the string representation of the default Complex
    * comparator.
    *
    * @return the string
    */
   public static String getComparatorString () {

      return Complex.COMPARATOR.toString();
   }

   /**
    * Returns the inverse, or reciprocal, of the complex
    * number.
    *
    * @param z
    *           the input complex number
    * @param target
    *           the output complex number
    * @return the inverse
    * @see Complex#absSq(Complex)
    */
   public static Complex inverse (
         final Complex z,
         final Complex target ) {

      final float absSq = Complex.absSq(z);
      if (absSq <= 0.0f) {
         return target.reset();
      }
      final float invAbsSq = 1.0f / absSq;
      return target.set(
            z.real * invAbsSq,
            -z.imag * invAbsSq);
   }

   /**
    * Returns the inverse, or reciprocal, of the complex
    * number.
    *
    * @param z
    *           the input complex number
    * @param target
    *           the output complex number
    * @param conj
    *           the conjugate
    * @return the inverse
    * @see Complex#absSq(Complex)
    * @see Complex#conj(Complex, Complex)
    */
   public static Complex inverse (
         final Complex z,
         final Complex target,
         final Complex conj ) {

      final float absSq = Complex.absSq(z);
      if (absSq <= 0.0f) {
         return target.reset();
      }
      Complex.conj(z, conj);
      final float invAbsSq = 1.0f / absSq;
      return target.set(
            conj.real * invAbsSq,
            conj.imag * invAbsSq);
   }

   /**
    * Tests to see if all the complex number's components are
    * non-zero.
    *
    * @param z
    *           the complex number
    * @return the evaluation
    */
   public static boolean isNonZero ( final Complex z ) {

      return z.real != 0.0f && z.imag != 0.0f;
   }

   /**
    * Tests to see if the complex number's absolute is
    * approximately 1.0.
    *
    * @param z
    *           the complex number
    * @return the evaluation
    * @see Utils#approxFast(float, float)
    * @see Complex#absSq(Complex)
    */
   public static boolean isUnit ( final Complex z ) {

      return Utils.approxFast(Complex.absSq(z), 1.0f);
   }

   /**
    * Tests to see if all the complex number's components are
    * zero.
    *
    * @param z
    *           the complex number
    * @return the evaluation
    */
   public static boolean isZero ( final Complex z ) {

      return z.real == 0.0f && z.imag == 0.0f;
   }

   /**
    * Finds the complex logarithm.
    *
    * @param z
    *           the complex number
    * @param target
    *           the output complex number
    * @return the logarithm
    * @see Math#log(double)
    * @see Complex#abs(Complex)
    * @see Math#atan2(double, double)
    */
   public static Complex log (
         final Complex z,
         final Complex target ) {

      return target.set(
            (float) Math.log(Complex.abs(z)),
            (float) Math.atan2(z.imag, z.real));
   }

   /**
    * Performs a Mobius transformation on the variable
    * <em>z</em>. Uses the formula <em>a</em> <em>z</em> +
    * <em>b</em> / <em>c</em> <em>z</em> + <em>d</em> <br>
    * where constants <em>a</em>, <em>b</em>, <em>c</em>, and
    * <em>d</em> satisfy <em>a</em> <em>d</em> - <em>b</em>
    * <em>c</em> \u2260 0.0 .
    *
    * @param a
    *           the a constant
    * @param b
    *           the b constant
    * @param c
    *           the c constant
    * @param d
    *           the d constant
    * @param z
    *           the input complex number
    * @param target
    *           the output complex number
    * @return the mobius transformation
    */
   public static Complex mobius (
         final Complex a,
         final Complex b,
         final Complex c,
         final Complex d,
         final Complex z,
         final Complex target ) {

      /*
       * cz + d -- the denominator -- first. Minimize the number
       * of calculations needed before the function can determine
       * whether or not to return early.
       */
      final float czdr = c.real * z.real - c.imag * z.imag + d.real;
      final float czdi = c.real * z.imag + c.imag * z.real + d.imag;
      final float mSq = czdr * czdr + czdi * czdi;

      if (Utils.abs(mSq) < Utils.EPSILON) {
         return target.reset();
      }

      /*
       * az + b -- the numerator -- second.
       */
      final float azbr = a.real * z.real - a.imag * z.imag + b.real;
      final float azbi = a.real * z.imag + a.imag * z.real + b.imag;

      final float mSqInv = 1.0f / mSq;
      final float czdrInv = czdr * mSqInv;
      final float czdiInv = -czdi * mSqInv;

      return target.set(
            azbr * czdrInv - azbi * czdiInv,
            azbr * czdiInv + azbi * czdrInv);
   }

   /**
    * Multiplies two complex numbers. Complex multiplication is
    * not commutative.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output complex number
    * @return the product
    */
   public static Complex mult (
         final Complex a,
         final Complex b,
         final Complex target ) {

      return target.set(
            a.real * b.real - a.imag * b.imag,
            a.real * b.imag + a.imag * b.real);
   }

   /**
    * Multiplies a complex and real number.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output complex number
    * @return the product
    */
   public static Complex mult (
         final Complex a,
         final float b,
         final Complex target ) {

      return target.set(a.real * b, a.imag * b);
   }

   /**
    * Multiplies a real and complex number.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output complex number
    * @return the product
    */
   public static Complex mult (
         final float a,
         final Complex b,
         final Complex target ) {

      return target.set(a * b.real, a * b.imag);
   }

   /**
    * Finds the signed phase of a complex number. Similar to a
    * vector's heading.
    *
    * @param z
    *           the complex number
    * @return the phase
    */
   public static float phase ( final Complex z ) {

      return Utils.atan2(z.imag, z.real);
   }

   /**
    * Raises a complex number to the power of another.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output complex number
    * @return the result
    * @see Math#log(double)
    * @see Math#sqrt(double)
    * @see Math#atan2(double, double)
    * @see Math#exp(double)
    * @see Math#cos(double)
    * @see Math#sin(double)
    */
   public static Complex pow (
         final Complex a,
         final Complex b,
         final Complex target ) {

      final double logReal = Math.log(Math.sqrt(
            a.real * a.real + a.imag * a.imag));
      final double logImag = Math.atan2(a.imag, a.real);
      final double phi = b.real * logImag + b.imag * logReal;
      final double r = Math.exp(b.real * logReal - b.imag * logImag);

      return target.set(
            (float) (r * Math.cos(phi)),
            (float) (r * Math.sin(phi)));
   }

   /**
    * Raises a complex number to the power of another.
    * Discloses the product and log.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output complex number
    * @param prod
    *           the product
    * @param log
    *           the log
    * @return the result
    * @see Complex#exp(Complex, Complex)
    * @see Complex#mult(Complex, Complex, Complex)
    * @see Complex#log(Complex, Complex)
    */
   public static Complex pow (
         final Complex a,
         final Complex b,
         final Complex target,
         final Complex prod,
         final Complex log ) {

      return Complex.exp(Complex.mult(b, Complex.log(a, log), prod), target);
   }

   /**
    * Raises a complex number to the power of a real number.
    *
    * @param a
    *           the complex number
    * @param b
    *           the real exponent
    * @param target
    *           the output complex number
    * @return the result
    * @see Math#log(double)
    * @see Math#sqrt(double)
    * @see Math#atan2(double, double)
    * @see Math#exp(double)
    * @see Math#cos(double)
    * @see Math#sin(double)
    */
   public static Complex pow (
         final Complex a,
         final float b,
         final Complex target ) {

      final double logReal = Math.log(Math.sqrt(
            a.real * a.real + a.imag * a.imag));
      final double logImag = Math.atan2(a.imag, a.real);
      final double phi = b * logImag;
      final double r = Math.exp(b * logReal);

      return target.set(
            (float) (r * Math.cos(phi)),
            (float) (r * Math.sin(phi)));
   }

   /**
    * Raises a float to the power of a complex number.
    *
    * @param a
    *           the real number
    * @param b
    *           the complex exponent
    * @param target
    *           the output complex number
    * @return the result
    * @see Math#log(double)
    * @see Math#sqrt(double)
    * @see Math#exp(double)
    * @see Math#cos(double)
    * @see Math#sin(double)
    */
   public static Complex pow (
         final float a,
         final Complex b,
         final Complex target ) {

      final double logReal = Math.log(Math.sqrt(a * a));
      final double logImag = a < 0.0d ? Math.PI : 0.0d;
      final double phi = b.real * logImag + b.imag * logReal;
      final double r = Math.exp(b.real * logReal - b.imag * logImag);

      return target.set(
            (float) (r * Math.cos(phi)),
            (float) (r * Math.sin(phi)));
   }

   /**
    * Creates a complex number with an absolute of 1.0.
    *
    * @param rng
    *           the random number generator
    * @param target
    *           the output complex number
    * @return the random complex number
    * @see Random#uniform(float, float)
    * @see Math#sqrt(double)
    */
   public static Complex random (
         final Random rng,
         final Complex target ) {

      final float real = rng.uniform(-1.0f, 1.0f);
      final float imag = rng.uniform(-1.0f, 1.0f);
      final float mSq = real * real + imag * imag;
      if (mSq == 0.0f) {
         return target.reset();
      }
      final float mInv = (float) (1.0d / Math.sqrt(mSq));
      return target.set(real * mInv, imag * mInv);
   }

   /**
    * Converts from polar to rectilinear coordinates.
    *
    * @param r
    *           the radius
    * @param phi
    *           the angle in radians
    * @param target
    *           the output complex number
    * @return the complex number
    * @see Math#cos(double)
    * @see Math#sin(double)
    */
   public static Complex rect (
         final float r,
         final float phi,
         final Complex target ) {

      return target.set(
            r * (float) Math.cos(phi),
            r * (float) Math.sin(phi));
   }

   /**
    * Sets the comparator function by which collections of
    * complex numbers are compared.
    *
    * @param comparator
    *           the comparator
    */
   public static void setComparator (
         final Comparator < Complex > comparator ) {

      if (comparator != null) {
         Complex.COMPARATOR = comparator;
      }
   }

   /**
    * Finds the sine of a complex number.
    *
    * @param z
    *           the complex number
    * @param target
    *           the output complex number
    * @return the sine
    */
   public static Complex sin (
         final Complex z,
         final Complex target ) {

      return target.set(
            (float) (Math.sin(z.real) * Math.cosh(z.imag)),
            (float) (Math.cos(z.real) * Math.sinh(z.imag)));
   }

   /**
    * Subtracts two complex numbers.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output complex number
    * @return the difference
    */
   public static Complex sub (
         final Complex a,
         final Complex b,
         final Complex target ) {

      return target.set(
            a.real - b.real,
            a.imag - b.imag);
   }

   /**
    * Subtracts a real number from a complex number.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output complex number
    * @return the difference
    */
   public static Complex sub (
         final Complex a,
         final float b,
         final Complex target ) {

      return target.set(a.real - b, a.imag);
   }

   /**
    * Subtracts a complex number from a real number.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output complex number
    * @return the difference
    */
   public static Complex sub (
         final float a,
         final Complex b,
         final Complex target ) {

      return target.set(a - b.real, -b.imag);
   }

   /**
    * Returns a complex number with all components set to zero.
    *
    * @param target
    *           the output number
    * @return zero
    */
   public static Complex zero ( final Complex target ) {

      return target.set(0.0f, 0.0f);
   }

   /**
    * The coefficient of the imaginary component <em>i</em>.
    */
   public float imag = 0.0f;

   /**
    * The real component.
    */
   public float real = 0.0f;

   /**
    * The default constructor.
    */
   public Complex () {

      super(2);
   }

   /**
    * Constructs a complex number from boolean values.
    *
    * @param real
    *           the real component
    * @param imag
    *           the imaginary component
    */
   public Complex (
         final boolean real,
         final boolean imag ) {

      super(2);
      this.set(real, imag);
   }

   /**
    * Constructs a complex number from the source's components.
    *
    * @param source
    *           the source complex number
    */
   public Complex ( final Complex source ) {

      super(2);
      this.set(source);
   }

   /**
    * Constructs a complex number from float values.
    *
    * @param real
    *           the real component
    * @param imag
    *           the imaginary component
    */
   public Complex (
         final float real,
         final float imag ) {

      super(2);
      this.set(real, imag);
   }

   /**
    * Attempts to construct a complex numbers from Strings
    * using {@link Float#parseFloat(String)} . If a
    * NumberFormatException is thrown, the component is set to
    * zero.
    *
    * @param realstr
    *           the real string
    * @param imagstr
    *           the imaginary string
    * @see Float#parseFloat(String)
    */
   public Complex (
         final String realstr,
         final String imagstr ) {

      super(2);
      this.set(realstr, imagstr);
   }

   /**
    * Tests equivalence between this and another complex
    * number. For rough equivalence of floating point
    * components, use the static approx function instead.
    *
    * @param z
    *           the complex number
    * @return the evaluation
    * @see Float#floatToIntBits(float)
    * @see Complex#approx(Complex, Complex)
    * @see Complex#approx(Complex, Complex, float)
    */
   protected boolean equals ( final Complex z ) {

      if (Float.floatToIntBits(this.imag) != Float.floatToIntBits(z.imag)) {
         return false;
      }

      if (Float.floatToIntBits(this.real) != Float.floatToIntBits(z.real)) {
         return false;
      }

      return true;
   }

   /**
    * Returns a new complex number with this complex number's
    * components. Java's cloneable interface is problematic;
    * use set or a copy constructor instead.
    *
    * @return a new complex number
    */
   @Override
   public Complex clone () {

      return new Complex(this.real, this.imag);
   }

   /**
    * Returns -1 when this complex number is less than the
    * comparisand; 1 when it is greater than; 0 when the two
    * are 'equal'. The implementation of this method allows
    * collections of complex number to be sorted. This depends
    * upon the static comparator of the Complex class, which
    * can be changed.
    *
    * @param z
    *           the comparisand
    * @return the numeric code
    * @see Complex#COMPARATOR
    */
   @Override
   public int compareTo ( final Complex z ) {

      return Complex.COMPARATOR.compare(this, z);
   }

   /**
    * Tests this complex number for equivalence with another
    * object.
    *
    * @param obj
    *           the object
    * @return the equivalence
    * @see Complex#equals(Complex)
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
      return this.equals((Complex) obj);
   }

   /**
    * Simulates bracket subscript access in an array..
    *
    * @param index
    *           the index
    * @return the component at that index
    */
   @Override
   public float get ( final int index ) {

      switch (index) {
         case 0:
         case -1:
            return this.real;
         case 1:
         case -2:
            return this.imag;
         default:
            return 0.0f;
      }
   }

   /**
    * Returns a hash code for this complex number based on its
    * real and imaginary components.
    *
    * @return the hash code
    * @see Float#floatToIntBits(float)
    */
   @Override
   public int hashCode () {

      final int prime = 31;
      int result = 1;
      result = prime * result + Float.floatToIntBits(this.real);
      result = prime * result + Float.floatToIntBits(this.imag);
      return result;
   }

   /**
    * Returns an iterator for this complex number, which allows
    * its components to be accessed in an enhanced for-loop.
    *
    * @return the iterator
    */
   @Override
   public CIterator iterator () {

      return new CIterator(this);
   }

   /**
    * Resets this complex number to an inital state ( 0.0, 0.0
    * ) .
    *
    * @return this complex number
    */
   @Chainable
   public Complex reset () {

      return Complex.zero(this);
   }

   /**
    * Sets the components of this complex number from booleans,
    * where false is 0.0 and true is 1.0 .
    *
    * @param real
    *           the real component
    * @param imag
    *           the imaginary component
    * @return this complex number
    * @see Utils#toFloat(boolean)
    */
   @Chainable
   public Complex set (
         final boolean real,
         final boolean imag ) {

      this.real = Utils.toFloat(real);
      this.imag = Utils.toFloat(imag);
      return this;
   }

   /**
    * Copies the components of the input complex numnber to
    * this complex number.
    *
    * @param source
    *           the input complex number
    * @return this complex number
    */
   @Chainable
   public Complex set ( final Complex source ) {

      return this.set(source.real, source.imag);
   }

   /**
    * Sets the components of this complex number.
    *
    * @param real
    *           the real component
    * @param imag
    *           the imaginary component
    * @return this complex number
    */
   @Chainable
   public Complex set (
         final float real,
         final float imag ) {

      this.real = real;
      this.imag = imag;
      return this;
   }

   /**
    * Attempts to set the components of this complex number
    * from Strings using {@link Float#parseFloat(String)} . If
    * a NumberFormatException is thrown, the component is set
    * to zero.
    *
    * @param realstr
    *           the real string
    * @param imagstr
    *           the imaginary string
    * @return this complex number
    * @see Float#parseFloat(String)
    */
   @Chainable
   public Complex set (
         final String realstr,
         final String imagstr ) {

      float real = 0.0f;
      float imag = 0.0f;

      try {
         real = Float.parseFloat(realstr);
      } catch (final NumberFormatException e) {
         real = 0.0f;
      }

      try {
         imag = Float.parseFloat(imagstr);
      } catch (final NumberFormatException e) {
         imag = 0.0f;
      }

      this.real = real;
      this.imag = imag;

      return this;
   }

   /**
    * Returns a float array of length 2 containing this complex
    * number's components.
    *
    * @return the array
    */
   @Override
   public float[] toArray () {

      return new float[] {
            this.real, this.imag
      };
   }

   /**
    * Returns a string representation of this complex number.
    *
    * @return the string
    */
   @Override
   public String toString () {

      return this.toString(4);
   }

   /**
    * Returns a string representation of this complex number.
    *
    * @param places
    *           the number of places
    * @return the string
    * @see Utils#toFixed(float, int)
    */
   public String toString ( final int places ) {

      return new StringBuilder(64)
            .append("{ real: ")
            .append(Utils.toFixed(this.real, places))
            .append(", imag: ")
            .append(Utils.toFixed(this.imag, places))
            .append(' ')
            .append('}')
            .toString();
   }
}