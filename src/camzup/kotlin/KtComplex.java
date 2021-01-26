package camzup.kotlin;

import camzup.core.Complex;
import camzup.core.Utils;

/**
 * Provides Kotlin operator overloading support for complex numbers.
 */
public class KtComplex extends Complex {

   /**
    * The default constructor.
    */
   public KtComplex ( ) {}

   /**
    * Constructs a complex number from the source's components.
    *
    * @param source the source complex number
    */
   public KtComplex ( final Complex source ) { super(source); }

   /**
    * Constructs a complex number from float values.
    *
    * @param real the real component
    * @param imag the imaginary component
    */
   public KtComplex ( final float real, final float imag ) {

      super(real, imag);
   }

   /**
    * Tests to see if the complex number contains a value.
    *
    * @param v the value
    *
    * @return the evaluation
    */
   public boolean contains ( final float v ) {

      return Utils.approx(this.real, v) || Utils.approx(this.imag, v);
   }

   /**
    * Returns a new complex number with the division of the instance by the
    * right operand. For interoperability with Kotlin: <code>a / b</code> .
    * <em>Does not mutate the complex number in place</em>.
    *
    * @param b the right operand
    *
    * @return the quotient
    */
   public KtComplex div ( final Complex b ) {

      final float bAbsSq = Complex.absSq(b);
      if ( bAbsSq != 0.0f ) {
         final float bInvAbsSq = 1.0f / bAbsSq;
         final float cReal = b.real * bInvAbsSq;
         final float cImag = -b.imag * bInvAbsSq;
         return new KtComplex(this.real * cReal - this.imag * cImag, this.real
            * cImag + this.imag * cReal);
      }
      return new KtComplex();
   }

   /**
    * Returns a new complex number with the division of the instance by the
    * right operand. For interoperability with Kotlin: <code>a / b</code> .
    * <em>Does not mutate the complex number in place</em>.
    *
    * @param b the right operand
    *
    * @return the quotient
    */
   public KtComplex div ( final float b ) {

      return b != 0.0f ? new KtComplex(this.real / b, this.imag / b)
         : new KtComplex();
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

      final float bAbsSq = Complex.absSq(b);
      if ( bAbsSq != 0.0f ) {
         final float bInvAbsSq = 1.0f / bAbsSq;
         final float cReal = b.real * bInvAbsSq;
         final float cImag = -b.imag * bInvAbsSq;
         this.set(this.real * cReal - this.imag * cImag, this.real * cImag
            + this.imag * cReal);
      } else {
         this.real = 0.0f;
         this.imag = 0.0f;
      }
   }

   /**
    * Divides the instance by the right operand (mutates the complex number in
    * place). For interoperability with Kotlin: <code>a /= b</code> .
    *
    * @param b the right operand
    */
   public void divAssign ( final float b ) {

      if ( b != 0.0f ) {
         final float bInv = 1.0f / b;
         this.real *= bInv;
         this.imag *= bInv;
      } else {
         this.real = 0.0f;
         this.imag = 0.0f;
      }
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
   public KtComplex minus ( final Complex b ) {

      return new KtComplex(this.real - b.real, this.imag - b.imag);
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
   public KtComplex minus ( final float b ) {

      return new KtComplex(this.real - b, this.imag);
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
    * the instance. For interoperability with Kotlin: <code>a + b</code> .
    * <em>Does not mutate the complex number in place</em>.
    *
    * @param b the right operand
    *
    * @return the sum
    */
   public KtComplex plus ( final Complex b ) {

      return new KtComplex(this.real + b.real, this.imag + b.imag);
   }

   /**
    * Returns a new complex number with the addition of the right operand to
    * the instance. For interoperability with Kotlin: <code>a + b</code> .
    * <em>Does not mutate the complex number in place</em>.
    *
    * @param b the right operand
    *
    * @return the sum
    */
   public KtComplex plus ( final float b ) {

      return new KtComplex(this.real + b, this.imag);
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
    * Returns a new complex number with the product of the instance and the
    * right operand. For interoperability with Kotlin: <code>a * b</code> .
    * <em>Does not mutate the complex number in place</em>.
    *
    * @param b the right operand
    *
    * @return the product
    */
   public KtComplex times ( final Complex b ) {

      return new KtComplex(this.real * b.real - this.imag * b.imag, this.real
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
    */
   public KtComplex times ( final float b ) {

      return new KtComplex(this.real * b, this.imag * b);
   }

   /**
    * Multiplies the right operand with the instance (mutates the complex
    * number in place). For interoperability with Kotlin: <code>a *= b</code>
    * .
    *
    * @param b the right operand
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
    */
   public void timesAssign ( final float b ) {

      this.real *= b;
      this.imag *= b;
   }

   /**
    * Returns a new complex number that negates the instance. For
    * interoperability with Kotlin: <code>-a</code> . <em>Does not mutate the
    * complex number in place</em>.
    *
    * @return the negation
    */
   public KtComplex unaryMinus ( ) {

      return new KtComplex(-this.real, -this.imag);
   }

   /**
    * Returns a new complex number with the positive copy of the instance. For
    * interoperability with Kotlin: <code>+a</code> . <em>Does not mutate the
    * complex number in place</em>.
    *
    * @return the positive
    */
   public KtComplex unaryPlus ( ) {

      return new KtComplex(this.real, this.imag);
   }

}
