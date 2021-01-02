package camzup.core;

import java.util.Comparator;

/**
 * A two-dimensional complex number. The <code>imag</code> component is a
 * coefficient of <em>i</em>, or the square-root of negative one.
 */
public class Complex implements Comparable < Complex > {

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
   public Complex ( ) {}

   /**
    * Constructs a complex number from the source's components.
    *
    * @param source the source complex number
    */
   public Complex ( final Complex source ) { this.set(source); }

   /**
    * Constructs a complex number from float values.
    *
    * @param real the real component
    * @param imag the imaginary component
    */
   public Complex ( final float real, final float imag ) {

      this.set(real, imag);
   }

   /**
    * Returns -1 when this complex number is less than the comparisand; 1 when
    * it is greater than; 0 when the two are 'equal'. The implementation of
    * this method allows collections of complex number to be sorted.
    *
    * @param z the comparisand
    *
    * @return the numeric code
    */
   @Override
   public int compareTo ( final Complex z ) {

      /* @formatter:off */
      return this.imag > z.imag ?  1
           : this.imag < z.imag ? -1
           : this.real > z.real ?  1
           : this.real < z.real ? -1 : 0;
      /* @formatter:on */
   }

   /**
    * Tests this complex number for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the equivalence
    *
    * @see Complex#equals(Complex)
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null ) { return false; }
      if ( this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Complex ) obj);
   }

   /**
    * Returns a hash code for this complex number based on its real and
    * imaginary components.
    *
    * @return the hash code
    *
    * @see Float#floatToIntBits(float)
    */
   @Override
   public int hashCode ( ) {

      return ( IUtils.MUL_BASE ^ Float.floatToIntBits(this.real) )
         * IUtils.HASH_MUL ^ Float.floatToIntBits(this.imag);
   }

   /**
    * Resets this complex number to an initial state ( 0.0, 0.0 ) .
    *
    * @return this complex number
    */

   public Complex reset ( ) { return this.set(0.0f, 0.0f); }

   /**
    * Copies the components of the input complex number to this complex
    * number.
    *
    * @param source the input complex number
    *
    * @return this complex number
    */

   public Complex set ( final Complex source ) {

      return this.set(source.real, source.imag);
   }

   /**
    * Sets the components of this complex number.
    *
    * @param real the real component
    * @param imag the imaginary component
    *
    * @return this complex number
    */

   public Complex set ( final float real, final float imag ) {

      this.real = real;
      this.imag = imag;

      return this;
   }

   /**
    * Returns a float array of length 2 containing this complex number's
    * components.
    *
    * @return the array
    */
   public float[] toArray ( ) { return this.toArray(new float[2], 0); }

   /**
    * Puts the complex number's components into an existing array at the index
    * provided. The real component is assigned to element <code>i</code>; the
    * imaginary component, to element <code>i + 1</code>.
    *
    * @param arr the array
    * @param i   the index
    *
    * @return the array
    */
   public float[] toArray ( final float[] arr, final int i ) {

      arr[i] = this.real;
      arr[i + 1] = this.imag;
      return arr;
   }

   /**
    * Returns a string representation of this complex number.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(4); }

   /**
    * Returns a string representation of this complex number.
    *
    * @param places the number of places
    *
    * @return the string
    *
    * @see Utils#toFixed(float, int)
    */
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(64);
      sb.append("{ real: ");
      sb.append(Utils.toFixed(this.real, places));
      sb.append(", imag: ");
      sb.append(Utils.toFixed(this.imag, places));
      sb.append(' ');
      sb.append('}');
      return sb.toString();
   }

   /**
    * Tests equivalence between this and another complex number. For rough
    * equivalence of floating point components, use the static approximates
    * function instead.
    *
    * @param z the complex number
    *
    * @return the evaluation
    *
    * @see Float#floatToIntBits(float)
    * @see Complex#approx(Complex, Complex)
    * @see Complex#approx(Complex, Complex, float)
    */
   protected boolean equals ( final Complex z ) {

      /* With {@link Float.floatToIntBits(float)}, -0.0f != 0.0f. */
      return this.imag == z.imag && this.real == z.real;
   }

   /**
    * Finds the absolute of a complex number. Similar to a vector's magnitude.
    *
    * @param z the complex number
    *
    * @return the absolute
    */
   public static float abs ( final Complex z ) {

      return Utils.hypot(z.real, z.imag);
   }

   /**
    * Finds the absolute squared of a complex number. Similar to a vector's
    * magnitude squared.
    *
    * @param z the complex number
    *
    * @return the absolute squared
    */
   public static float absSq ( final Complex z ) {

      return z.real * z.real + z.imag * z.imag;
   }

   /**
    * Adds two complex numbers.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output complex quaternion
    *
    * @return the sum
    */
   public static Complex add ( final Complex a, final Complex b,
      final Complex target ) {

      return target.set(a.real + b.real, a.imag + b.imag);
   }

   /**
    * Adds a complex and real number.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output complex quaternion
    *
    * @return the sum
    */
   public static Complex add ( final Complex a, final float b,
      final Complex target ) {

      return target.set(a.real + b, a.imag);
   }

   /**
    * Adds a real and complex number.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output complex quaternion
    *
    * @return the sum
    */
   public static Complex add ( final float a, final Complex b,
      final Complex target ) {

      return target.set(a + b.real, b.imag);
   }

   /**
    * Tests to see if all of the complex number's components are non-zero.
    *
    * @param z the complex number
    *
    * @return the evaluation
    */
   public static boolean all ( final Complex z ) {

      return z.real != 0.0f && z.imag != 0.0f;
   }

   /**
    * Tests to see if any of the complex number's components are non-zero.
    *
    * @param z the complex number
    *
    * @return the evaluation
    */
   public static boolean any ( final Complex z ) {

      return z.real != 0.0f || z.imag != 0.0f;
   }

   /**
    * Evaluates whether or not two complex numbers approximate each other.
    *
    * @param a the left comparisand
    * @param b the right comparisand
    *
    * @return the evaluation
    */
   public static boolean approx ( final Complex a, final Complex b ) {

      return Complex.approx(a, b, IUtils.EPSILON);
   }

   /**
    * Evaluates whether or not two complex numbers approximate each other
    * according to a tolerance.
    *
    * @param a         the left comparisand
    * @param b         the right comparisand
    * @param tolerance the tolerance
    *
    * @return the evaluation
    *
    * @see Utils#approx(float, float, float)
    */
   public static boolean approx ( final Complex a, final Complex b,
      final float tolerance ) {

      /* @formatter:off */
      return a == b ||
         Utils.approx(a.imag, b.imag, tolerance) &&
         Utils.approx(a.real, b.real, tolerance);
      /* @formatter:on */
   }

   /**
    * Finds the conjugate of the complex number, where the imaginary component
    * is negated.
    *
    * @param z      the input complex number
    * @param target the output complex number
    *
    * @return the conjugate
    */
   public static Complex conj ( final Complex z, final Complex target ) {

      return target.set(z.real, -z.imag);
   }

   /**
    * Finds the cosine of a complex number.
    *
    * @param z      the complex number
    * @param target the output complex number
    *
    * @return the cosine
    */
   public static Complex cos ( final Complex z, final Complex target ) {

      final double zr = z.real;
      final double zi = z.imag;

      return target.set(( float ) ( Math.cos(zr) * Math.cosh(zi) ),
         ( float ) ( -Math.sin(zr) * Math.sinh(zi) ));
   }

   /**
    * Divides one complex number by another.
    *
    * @param a      the numerator
    * @param b      the denominator
    * @param target the output complex number
    *
    * @return the quotient
    *
    * @see Complex#absSq(Complex)
    */
   public static Complex div ( final Complex a, final Complex b,
      final Complex target ) {

      final float bAbsSq = Complex.absSq(b);
      if ( bAbsSq != 0.0f ) {
         final float bInvAbsSq = 1.0f / bAbsSq;
         final float cReal = b.real * bInvAbsSq;
         final float cImag = -b.imag * bInvAbsSq;
         return target.set(a.real * cReal - a.imag * cImag, a.real * cImag
            + a.imag * cReal);
      }
      return target.reset();
   }

   /**
    * Divides one complex number by another. Equivalent to multiplying the
    * numerator and the inverse of the denominator.
    *
    * @param a       the numerator
    * @param b       the denominator
    * @param target  the output complex number
    * @param inverse the inverse
    * @param conj    the conjugate
    *
    * @return the quotient
    *
    * @see Complex#inverse(Complex, Complex, Complex)
    * @see Complex#mul(Complex, Complex, Complex)
    */
   public static Complex div ( final Complex a, final Complex b,
      final Complex target, final Complex inverse, final Complex conj ) {

      Complex.inverse(b, inverse, conj);
      return Complex.mul(a, inverse, target);
   }

   /**
    * Divides a complex number by a real number.
    *
    * @param a      the numerator
    * @param b      the denominator
    * @param target the output complex number
    *
    * @return the quotient
    */
   public static Complex div ( final Complex a, final float b,
      final Complex target ) {

      if ( b != 0.0f ) {
         final float bInv = 1.0f / b;
         return target.set(a.real * bInv, a.imag * bInv);
      }
      return target.reset();
   }

   /**
    * Divides a real number by a complex number.
    *
    * @param a      the numerator
    * @param b      the denominator
    * @param target the output complex number
    *
    * @return the quotient
    *
    * @see Complex#absSq(Complex)
    */
   public static Complex div ( final float a, final Complex b,
      final Complex target ) {

      final float bAbsSq = Complex.absSq(b);
      if ( bAbsSq != 0.0f ) {
         final float abInvAbsSq = a / bAbsSq;
         return target.set(b.real * abInvAbsSq, -b.imag * abInvAbsSq);
      }
      return target.reset();
   }

   /**
    * Returns Euler's number, <em>e</em>, raised to a complex number.
    *
    * @param z      the complex number
    * @param target the output complex number
    *
    * @return the result
    *
    * @see Math#exp(double)
    * @see Math#cos(double)
    * @see Math#sin(double)
    */
   public static Complex exp ( final Complex z, final Complex target ) {

      final double rd = Math.exp(z.real);
      final double phid = z.imag;
      return target.set(( float ) ( rd * Math.cos(phid) ), ( float ) ( rd * Math
         .sin(phid) ));
   }

   /**
    * Returns the inverse, or reciprocal, of the complex number.
    *
    * @param z      the input complex number
    * @param target the output complex number
    *
    * @return the inverse
    *
    * @see Complex#absSq(Complex)
    */
   public static Complex inverse ( final Complex z, final Complex target ) {

      final float absSq = Complex.absSq(z);
      if ( absSq != 0.0f ) {
         final float invAbsSq = 1.0f / absSq;
         return target.set(z.real * invAbsSq, -z.imag * invAbsSq);
      }
      return target.reset();
   }

   /**
    * Returns the inverse, or reciprocal, of the complex number.
    *
    * @param z      the input complex number
    * @param target the output complex number
    * @param conj   the conjugate
    *
    * @return the inverse
    *
    * @see Complex#absSq(Complex)
    * @see Complex#conj(Complex, Complex)
    */
   public static Complex inverse ( final Complex z, final Complex target,
      final Complex conj ) {

      final float absSq = Complex.absSq(z);
      if ( absSq != 0.0f ) {
         Complex.conj(z, conj);
         final float invAbsSq = 1.0f / absSq;
         return target.set(conj.real * invAbsSq, conj.imag * invAbsSq);
      }
      return target.reset();
   }

   /**
    * Tests to see if the complex number's absolute is approximately 1.0.
    *
    * @param z the complex number
    *
    * @return the evaluation
    *
    * @see Utils#approx(float, float)
    * @see Complex#absSq(Complex)
    */
   public static boolean isUnit ( final Complex z ) {

      return Utils.approx(Complex.absSq(z), 1.0f);
   }

   /**
    * Finds the complex logarithm.
    *
    * @param z      the complex number
    * @param target the output complex number
    *
    * @return the logarithm
    *
    * @see Math#log(double)
    * @see Math#sqrt(double)
    * @see Math#atan2(double, double)
    */
   public static Complex log ( final Complex z, final Complex target ) {

      final double zr = z.real;
      final double zi = z.imag;
      return target.set(( float ) Math.log(Math.sqrt(zr * zr + zi * zi)),
         ( float ) Math.atan2(zi, zr));
   }

   /**
    * Performs a Mobius transformation on the variable <em>z</em>. Uses the
    * formula ( <em>a</em> <em>z</em> + <em>b</em> ) / ( <em>c</em> <em>z</em>
    * + <em>d</em> ) <br>
    * where constants <em>a</em>, <em>b</em>, <em>c</em>, and <em>d</em>
    * satisfy <em>a</em> <em>d</em> - <em>b</em> <em>c</em> \u2260 0.0 .
    *
    * @param a      the a constant
    * @param b      the b constant
    * @param c      the c constant
    * @param d      the d constant
    * @param z      the input complex number
    * @param target the output complex number
    *
    * @return the Mobius transformation
    */
   public static Complex mobius ( final Complex a, final Complex b,
      final Complex c, final Complex d, final Complex z,
      final Complex target ) {

      /*
       * Find denominator first. Minimize the number of calculations needed
       * before the function can determine whether or not to return early.
       */
      final float czdr = c.real * z.real - c.imag * z.imag + d.real;
      final float czdi = c.real * z.imag + c.imag * z.real + d.imag;
      final float mSq = czdr * czdr + czdi * czdi;

      if ( mSq < IUtils.EPSILON ) { return target.reset(); }

      /* Find numerator. */
      final float azbr = a.real * z.real - a.imag * z.imag + b.real;
      final float azbi = a.real * z.imag + a.imag * z.real + b.imag;

      final float mSqInv = 1.0f / mSq;
      final float czdrInv = czdr * mSqInv;
      final float czdiInv = -czdi * mSqInv;

      return target.set(azbr * czdrInv - azbi * czdiInv, azbr * czdiInv + azbi
         * czdrInv);
   }

   /**
    * Multiplies two complex numbers. Complex multiplication is not
    * commutative.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output complex number
    *
    * @return the product
    */
   public static Complex mul ( final Complex a, final Complex b,
      final Complex target ) {

      return target.set(a.real * b.real - a.imag * b.imag, a.real * b.imag
         + a.imag * b.real);
   }

   /**
    * Multiplies a complex and real number.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output complex number
    *
    * @return the product
    */
   public static Complex mul ( final Complex a, final float b,
      final Complex target ) {

      return target.set(a.real * b, a.imag * b);
   }

   /**
    * Multiplies a real and complex number.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output complex number
    *
    * @return the product
    */
   public static Complex mul ( final float a, final Complex b,
      final Complex target ) {

      return target.set(a * b.real, a * b.imag);
   }

   /**
    * Tests to see if all the complex number's components are zero.
    *
    * @param z the complex number
    *
    * @return the evaluation
    */
   public static boolean none ( final Complex z ) {

      return z.real == 0.0f && z.imag == 0.0f;
   }

   /**
    * Finds the signed phase of a complex number. Similar to a 2D vector's
    * heading.
    *
    * @param z the complex number
    *
    * @return the phase
    */
   public static float phase ( final Complex z ) {

      return Utils.atan2(z.imag, z.real);
   }

   /**
    * Raises a complex number to the power of another.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output complex number
    *
    * @return the result
    *
    * @see Math#log(double)
    * @see Math#sqrt(double)
    * @see Math#atan2(double, double)
    * @see Math#exp(double)
    * @see Math#cos(double)
    * @see Math#sin(double)
    */
   public static Complex pow ( final Complex a, final Complex b,
      final Complex target ) {

      final double ar = a.real;
      final double ai = a.imag;
      final double br = b.real;
      final double bi = b.imag;

      final double logReal = Math.log(Math.sqrt(ar * ar + ai * ai));
      final double logImag = Math.atan2(ai, ar);

      final double rd = Math.exp(br * logReal - bi * logImag);
      final double phid = br * logImag + bi * logReal;

      return target.set(( float ) ( rd * Math.cos(phid) ), ( float ) ( rd * Math
         .sin(phid) ));
   }

   /**
    * Raises a complex number to the power of another. Uses the formula<br>
    * <br>
    * pow ( <em>a</em>, <em>b</em> ) := exp ( <em>b</em> log ( <em>a</em> )
    * )<br>
    * <br>
    * Discloses the product and log.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output complex number
    * @param prod   the product
    * @param log    the log
    *
    * @return the result
    *
    * @see Complex#exp(Complex, Complex)
    * @see Complex#mul(Complex, Complex, Complex)
    * @see Complex#log(Complex, Complex)
    */
   public static Complex pow ( final Complex a, final Complex b,
      final Complex target, final Complex prod, final Complex log ) {

      return Complex.exp(Complex.mul(b, Complex.log(a, log), prod), target);
   }

   /**
    * Raises a complex number to the power of a real number.
    *
    * @param a      the complex number
    * @param b      the real exponent
    * @param target the output complex number
    *
    * @return the result
    *
    * @see Math#log(double)
    * @see Math#sqrt(double)
    * @see Math#atan2(double, double)
    * @see Math#exp(double)
    * @see Math#cos(double)
    * @see Math#sin(double)
    */
   public static Complex pow ( final Complex a, final float b,
      final Complex target ) {

      final double ar = a.real;
      final double ai = a.imag;
      final double bd = b;

      final double rd = Math.exp(bd * Math.log(Math.sqrt(ar * ar + ai * ai)));
      final double phid = bd * Math.atan2(ai, ar);

      return target.set(( float ) ( rd * Math.cos(phid) ), ( float ) ( rd * Math
         .sin(phid) ));
   }

   /**
    * Raises a real number to the power of a complex number.
    *
    * @param a      the real number
    * @param b      the complex exponent
    * @param target the output complex number
    *
    * @return the result
    *
    * @see Math#log(double)
    * @see Math#sqrt(double)
    * @see Math#exp(double)
    * @see Math#cos(double)
    * @see Math#sin(double)
    */
   public static Complex pow ( final float a, final Complex b,
      final Complex target ) {

      final double ad = a;
      final double br = b.real;
      final double bi = b.imag;

      final double logReal = Math.log(Math.sqrt(ad * ad));
      final double logImag = ad < 0.0d ? Math.PI : 0.0d;

      final double rd = Math.exp(br * logReal - bi * logImag);
      final double phid = br * logImag + bi * logReal;

      return target.set(( float ) ( rd * Math.cos(phid) ), ( float ) ( rd * Math
         .sin(phid) ));
   }

   /**
    * Creates a random complex number.
    *
    * @param rng    the random number generator
    * @param rMin   the radius minimum
    * @param rMax   the radius maximum
    * @param target the output complex number
    *
    * @return the random complex number
    *
    * @see Complex#rect(float, float, Complex)
    */
   public static Complex random ( final java.util.Random rng, final float rMin,
      final float rMax, final Complex target ) {

      final float rt = rng.nextFloat();
      final float rr = rng.nextFloat();
      return Complex.rect( ( 1.0f - rt ) * -IUtils.PI + rt * IUtils.PI, ( 1.0f
         - rr ) * rMin + rr * rMax, target);
   }

   /**
    * Converts from polar to rectilinear coordinates.
    *
    * @param r      the radius
    * @param phi    the angle in radians
    * @param target the output complex number
    *
    * @return the complex number
    *
    * @see Math#cos(double)
    * @see Math#sin(double)
    */
   public static Complex rect ( final float r, final float phi,
      final Complex target ) {

      final double rd = r;
      final double phid = phi;
      return target.set(( float ) ( rd * Math.cos(phid) ), ( float ) ( rd * Math
         .sin(phid) ));
   }

   /**
    * Finds the sine of a complex number.
    *
    * @param z      the complex number
    * @param target the output complex number
    *
    * @return the sine
    */
   public static Complex sin ( final Complex z, final Complex target ) {

      final double zr = z.real;
      final double zi = z.imag;

      return target.set(( float ) ( Math.sin(zr) * Math.cosh(zi) ),
         ( float ) ( Math.cos(zr) * Math.sinh(zi) ));
   }

   /**
    * Finds the square root of a real number which could be either positive or
    * negative.
    *
    * @param a      the value
    * @param target the output complex number
    *
    * @return the square root
    */
   public static Complex sqrt ( final float a, final Complex target ) {

      return a > 0.0f ? target.set(Utils.sqrtUnchecked(a), 0.0f) : a < -0.0f
         ? target.set(0.0f, Utils.sqrtUnchecked(-a)) : target.reset();
   }

   /**
    * Subtracts the left complex number from the right.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output complex number
    *
    * @return the difference
    */
   public static Complex sub ( final Complex a, final Complex b,
      final Complex target ) {

      return target.set(a.real - b.real, a.imag - b.imag);
   }

   /**
    * Subtracts a real number from a complex number.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output complex number
    *
    * @return the difference
    */
   public static Complex sub ( final Complex a, final float b,
      final Complex target ) {

      return target.set(a.real - b, a.imag);
   }

   /**
    * Subtracts a complex number from a real number.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output complex number
    *
    * @return the difference
    */
   public static Complex sub ( final float a, final Complex b,
      final Complex target ) {

      return target.set(a - b.real, -b.imag);
   }

   /**
    * Returns a complex number with all components set to zero.
    *
    * @param target the output number
    *
    * @return zero
    */
   public static Complex zero ( final Complex target ) {

      return target.set(0.0f, 0.0f);
   }

   /**
    * An abstract class that may serve as an umbrella for any custom
    * comparators of complex numbers.
    */
   public abstract static class AbstrComparator implements Comparator <
      Complex > {

      /**
       * The default constructor.
       */
      protected AbstrComparator ( ) {}

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

}