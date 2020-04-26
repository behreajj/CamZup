package camzup.core;

import java.util.Comparator;
import java.util.Iterator;

/**
 * A two-dimensional complex number. The <code>imag</code> component is a
 * coefficient of <em>i</em>, or the square-root of negative one.
 */
public class Complex implements Comparable < Complex >, Cloneable, Iterable <
   Float > {

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
    * Attempts to construct a complex numbers from Strings using
    * {@link Float#parseFloat(String)} . If a NumberFormatException is thrown,
    * the component is set to zero.
    *
    * @param realstr the real string
    * @param imagstr the imaginary string
    *
    * @see Float#parseFloat(String)
    */
   public Complex ( final String realstr, final String imagstr ) {

      this.set(realstr, imagstr);
   }

   /**
    * Returns a new complex number with this complex number's components.
    * Java's cloneable interface is problematic; use set or a copy constructor
    * instead.
    *
    * @return a new complex number
    */
   @Override
   public Complex clone ( ) { return new Complex(this.real, this.imag); }

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
    * Tests to see if the complex number contains a value.
    *
    * @param v the value
    *
    * @return the evaluation
    */
   public boolean contains ( final float v ) {

      if ( Utils.approx(this.real, v) ) { return true; }
      if ( Utils.approx(this.imag, v) ) { return true; }
      return false;
   }

   /**
    * Returns a new complex number decremented by one. For interoperability
    * with Kotlin: <code>--a</code> (prefix) or <code>a--</code> (postfix).
    * Per the specification, <em>does not mutate the complex number in
    * place</em>.
    *
    * @return the decremented vector
    */
   public Complex dec ( ) {

      return new Complex(this.real - 1.0f, this.imag - 1.0f);
   }

   /**
    * Returns a new complex number with the division of the instance by the
    * right operand. For interoperability with Kotlin: <code>a / b</code> .
    * <em>Does not mutate the complex number in place</em>.
    *
    * @param b the right operand
    *
    * @return the quotient
    *
    * @see Complex#div(Complex, Complex, Complex)
    */
   public Complex div ( final Complex b ) {

      return Complex.div(this, b, new Complex());
   }

   /**
    * Returns a new complex number with the division of the instance by the
    * right operand. For interoperability with Kotlin: <code>a / b</code> .
    * <em>Does not mutate the complex number in place</em>.
    *
    * @param b the right operand
    *
    * @return the quotient
    *
    * @see Complex#div(Complex, float, Complex)
    */
   public Complex div ( final float b ) {

      return Complex.div(this, b, new Complex());
   }

   /**
    * Divides the instance by the right operand (mutates the complex number in
    * place). For interoperability with Kotlin: <code>a /= b</code> .
    *
    * @param b the right operand
    *
    * @see Complex#div(Complex, Complex, Complex)
    */
   public void divAssign ( final Complex b ) {

      Complex.div(this, b, this);
   }

   /**
    * Divides the instance by the right operand (mutates the complex number in
    * place). For interoperability with Kotlin: <code>a /= b</code> .
    *
    * @param b the right operand
    *
    * @see Complex#div(Complex, float, Complex)
    */
   public void divAssign ( final float b ) {

      Complex.div(this, b, this);
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
    * Simulates bracket subscript access in an array..
    *
    * @param index the index
    *
    * @return the component at that index
    */
   public float get ( final int index ) {

      switch ( index ) {
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
    * Returns a new complex number incremented by one. For interoperability
    * with Kotlin: <code>++a</code> (prefix) or <code>a++</code> (postfix).
    * Per the specification, <em>does not mutate the complex number in
    * place</em>.
    *
    * @return the incremented vector
    */
   public Complex inc ( ) {

      return new Complex(this.real + 1.0f, this.imag + 1.0f);
   }

   /**
    * Returns an iterator for this complex number, which allows its components
    * to be accessed in an enhanced for-loop.
    *
    * @return the iterator
    */
   @Override
   public Iterator < Float > iterator ( ) { return new CIterator(this); }

   /**
    * Gets the number of components held by the complex number.
    *
    * @return the size
    */
   public int length ( ) { return 2; }

   /**
    * Returns a new complex number with the subtraction of the right operand
    * from the instance. For interoperability with Kotlin: <code>a - b</code>
    * . <em>Does not mutate the complex number in place</em>.
    *
    * @param b the right operand
    *
    * @return the subtraction
    */
   public Complex minus ( final Complex b ) {

      return new Complex(this.real - b.real, this.imag - b.imag);
   }

   /**
    * Returns a new complex number with the subtraction of the right operand
    * from the instance. For interoperability with Kotlin: <code>a - b</code>
    * . <em>Does not mutate the complex number in place</em>.
    *
    * @param b the right operand
    *
    * @return the subtraction
    */
   public Complex minus ( final float b ) {

      return new Complex(this.real - b, this.imag);
   }

   /**
    * Subtracts the right operand from the instance (mutates the complex
    * number in place). For interoperability with Kotlin: <code>a -= b</code>
    * .
    *
    * @param b the right operand
    */
   public void minusAssign ( final Complex b ) {

      this.real -= b.real;
      this.imag -= b.imag;
   }

   /**
    * Subtracts the right operand from the instance (mutates the complex
    * number in place). For interoperability with Kotlin: <code>a -= b</code>
    * .
    *
    * @param b the right operand
    */
   public void minusAssign ( final float b ) {

      this.real -= b;
   }

   /**
    * Returns a new complex number with the addition of the right operand to
    * the instance. For interoperability with Kotlin: <code>a - b</code> .
    * <em>Does not mutate the complex number in place</em>.
    *
    * @param b the right operand
    *
    * @return the sum
    */
   public Complex plus ( final Complex b ) {

      return new Complex(this.real + b.real, this.imag + b.imag);
   }

   /**
    * Returns a new complex number with the addition of the right operand to
    * the instance. For interoperability with Kotlin: <code>a - b</code> .
    * <em>Does not mutate the complex number in place</em>.
    *
    * @param b the right operand
    *
    * @return the sum
    */
   public Complex plus ( final float b ) {

      return new Complex(this.real + b, this.imag);
   }

   /**
    * Adds the right operand to the instance (mutates the complex number in
    * place). For interoperability with Kotlin: <code>a += b</code> .
    *
    * @param b the right operand
    */
   public void plusAssign ( final Complex b ) {

      this.real += b.real;
      this.imag += b.imag;
   }

   /**
    * Adds the right operand to the instance (mutates the complex number in
    * place). For interoperability with Kotlin: <code>a += b</code> .
    *
    * @param b the right operand
    */
   public void plusAssign ( final float b ) {

      this.real += b;
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
    * Attempts to set the components of this complex number from Strings using
    * {@link Float#parseFloat(String)} . If a NumberFormatException is thrown,
    * the component is set to zero.
    *
    * @param realstr the real string
    * @param imagstr the imaginary string
    *
    * @return this complex number
    *
    * @see Float#parseFloat(String)
    */

   public Complex set ( final String realstr, final String imagstr ) {

      float rlstr = 0.0f;
      float imstr = 0.0f;

      try {
         rlstr = Float.parseFloat(realstr);
      } catch ( final NumberFormatException e ) {
         rlstr = 0.0f;
      }

      try {
         imstr = Float.parseFloat(imagstr);
      } catch ( final NumberFormatException e ) {
         imstr = 0.0f;
      }

      this.real = rlstr;
      this.imag = imstr;

      return this;
   }

   /**
    * Returns a new complex number with the product of the instance and the
    * right operand. For interoperability with Kotlin: <code>a * b</code> .
    * <em>Does not mutate the complex number in place</em>.
    *
    * @param b the right operand
    *
    * @return the product
    *
    * @see Complex#mul(Complex, Complex, Complex)
    */
   public Complex times ( final Complex b ) {

      return new Complex(this.real * b.real - this.imag * b.imag, this.real
         * b.imag + this.imag * b.real);
   }

   /**
    * Returns a new complex number with the product of the instance and the
    * right operand. For interoperability with Kotlin: <code>a * b</code> .
    * <em>Does not mutate the complex number in place</em>.
    *
    * @param b the right operand
    *
    * @return the product
    *
    * @see Complex#mul(Complex, float, Complex)
    */
   public Complex times ( final float b ) {

      return new Complex(this.real * b, this.imag * b);
   }

   /**
    * Multiplies the right operand with the instance (mutates the complex
    * number in place). For interoperability with Kotlin: <code>a *= b</code>
    * .
    *
    * @param b the right operand
    *
    * @see Complex#mul(Complex, Complex, Complex)
    */
   public void timesAssign ( final Complex b ) {

      this.set(this.real * b.real - this.imag * b.imag, this.real * b.imag
         + this.imag * b.real);
   }

   /**
    * Multiplies the right operand with the instance (mutates the complex
    * number in place). For interoperability with Kotlin: <code>a *= b</code>
    * .
    *
    * @param b the right operand
    *
    * @see Complex#mul(Complex, float, Complex)
    */
   public void timesAssign ( final float b ) {

      this.real *= b;
      this.imag *= b;
   }

   /**
    * Returns a float array of length 2 containing this complex number's
    * components.
    *
    * @return the array
    */
   public float[] toArray ( ) {

      return new float[] { this.real, this.imag };
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
    * Returns a new complex number with the negation of the instance. For
    * interoperability with Kotlin: <code>-a</code> . <em>Does not mutate the
    * complex in place</em>.
    *
    * @return the negation
    */
   public Complex unaryMinus ( ) {

      return new Complex(-this.real, -this.imag);
   }

   /**
    * Returns a new complex number with the positive copy of the instance. For
    * interoperability with Kotlin: <code>+a</code> . <em>Does not mutate the
    * complex number in place</em>.
    *
    * @return the positive
    */
   public Complex unaryPlus ( ) {

      return new Complex(+this.real, +this.imag);
   }

   /**
    * Returns a String of Python code targeted toward the Blender 2.8x API.
    * This code is brittle and is used for internal testing purposes.
    *
    * @return the string
    */
   @Experimental
   String toBlenderCode ( ) {

      final StringBuilder pyCd = new StringBuilder(64);
      pyCd.append('(');
      pyCd.append(Utils.toFixed(this.real, 6));
      pyCd.append(this.imag < -0.0f ? '-' : '+');
      pyCd.append(Utils.toFixed(Utils.abs(this.imag), 6));
      pyCd.append('j');
      pyCd.append(')');
      return pyCd.toString();
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

      return Float.floatToIntBits(this.imag) == Float.floatToIntBits(z.imag)
         && Float.floatToIntBits(this.real) == Float.floatToIntBits(z.real);
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
    * Tests to see if all the complex number's components are non-zero.
    *
    * @param z the complex number
    *
    * @return the evaluation
    */
   public static boolean all ( final Complex z ) {

      return z.real != 0.0f && z.imag != 0.0f;
   }

   /**
    * Tests to see if all the complex number's components are non-zero.
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

      return Complex.approx(a, b, IUtils.DEFAULT_EPSILON);
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
    * is negates.
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
      if ( bAbsSq == 0.0f ) { return target.reset(); }

      final float bInvAbsSq = 1.0f / bAbsSq;
      final float cReal = b.real * bInvAbsSq;
      final float cImag = -b.imag * bInvAbsSq;
      return target.set(a.real * cReal - a.imag * cImag, a.real * cImag + a.imag
         * cReal);
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

      if ( b == 0.0f ) { return target.reset(); }

      final float bInv = 1.0f / b;
      return target.set(a.real * bInv, a.imag * bInv);
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
      if ( bAbsSq == 0.0f ) { return target.reset(); }

      final float abInvAbsSq = a / bAbsSq;
      return target.set(b.real * abInvAbsSq, -b.imag * abInvAbsSq);
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

      return Complex.rect(( float ) Math.exp(z.real), z.imag, target);
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
      if ( absSq == 0.0f ) { return target.reset(); }
      final float invAbsSq = 1.0f / absSq;
      return target.set(z.real * invAbsSq, -z.imag * invAbsSq);
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
      if ( absSq == 0.0f ) { return target.reset(); }
      Complex.conj(z, conj);
      final float invAbsSq = 1.0f / absSq;
      return target.set(conj.real * invAbsSq, conj.imag * invAbsSq);
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
    * @see Complex#abs(Complex)
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
    * formula <em>a</em> <em>z</em> + <em>b</em> / <em>c</em> <em>z</em> +
    * <em>d</em> <br>
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
       * cz + d -- the denominator -- first. Minimize the number of calculations
       * needed before the function can determine whether or not to return
       * early.
       */
      final float czdr = c.real * z.real - c.imag * z.imag + d.real;
      final float czdi = c.real * z.imag + c.imag * z.real + d.imag;
      final float mSq = czdr * czdr + czdi * czdi;

      if ( mSq < IUtils.DEFAULT_EPSILON ) { return target.reset(); }

      /* az + b -- the numerator -- second. */
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
    * Finds the signed phase of a complex number. Similar to a vector's
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
    * Raises a complex number to the power of another. Discloses the product
    * and log.
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
    * Raises a float to the power of a complex number.
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
    * Creates a complex number with an absolute of 1.0.
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

      return a > 0.0f ? target.set(Utils.sqrtUnchecked(a), 0.0f) : a < 0.0f
         ? target.set(0.0f, Utils.sqrtUnchecked(-a)) : target.reset();
   }

   /**
    * Subtracts two complex numbers.
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
   public static abstract class AbstrComparator implements Comparator <
      Complex > {

      /**
       * The default constructor.
       */
      public AbstrComparator ( ) {}

      /**
       * The compare function which must be implemented by sub- (child) classes
       * of this class. Negative one should be returned when the left
       * comparisand, a, is less than the right comparisand, b, by a measure.
       * One should be returned when it is greater. Zero should be returned as a
       * last resort, when a and b are equal or incomparable.
       *
       * @param a the left comparisand
       * @param b the right comparisand
       *
       * @return the comparison
       */
      @Override
      public abstract int compare ( final Complex a, final Complex b );

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

   /**
    * An iterator, which allows a complex number's components to be accessed
    * in an enhanced for loop.
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
       * @param z the complex number to iterator
       */
      public CIterator ( final Complex z ) { this.z = z; }

      /**
       * Tests to see if the iterator has another value.
       *
       * @return the evaluation
       */
      @Override
      public boolean hasNext ( ) { return this.index < this.z.length(); }

      /**
       * Gets the next value in the iterator
       *
       * @return the value
       *
       * @see Complex#get(int)
       */
      @Override
      public Float next ( ) { return this.z.get(this.index++); }

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

}