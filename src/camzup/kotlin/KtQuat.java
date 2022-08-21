package camzup.kotlin;

import camzup.core.Quaternion;
import camzup.core.Utils;
import camzup.core.Vec3;

/**
 * Provides Kotlin operator overloading support for quaternions.
 */
public class KtQuat extends Quaternion {

   /**
    * The default constructor. Defaults to the identity, (1.0, 0.0, 0.0, 0.0)
    * .
    */
   public KtQuat ( ) {}

   /**
    * Constructs a quaternion from float values.
    *
    * @param real  the real component (w)
    * @param xImag the x component
    * @param yImag the y component
    * @param zImag the z component
    */
   public KtQuat ( final float real, final float xImag, final float yImag,
      final float zImag ) {

      super(real, xImag, yImag, zImag);
   }

   /**
    * Constructs a quaternion by real component and imaginary vector.
    *
    * @param real the real component
    * @param imag the imaginary component
    */
   public KtQuat ( final float real, final Vec3 imag ) { super(real, imag); }

   /**
    * Constructs a quaternion from integer values.
    *
    * @param real  the real component (w)
    * @param xImag the x component
    * @param yImag the y component
    * @param zImag the z component
    */
   public KtQuat ( final int real, final int xImag, final int yImag,
      final int zImag ) {

      super(real, xImag, yImag, zImag);
   }

   /**
    * A copy constructor.
    *
    * @param source the source quaternion
    */
   public KtQuat ( final Quaternion source ) { super(source); }

   /**
    * Tests to see if the quaternion contains a value.
    *
    * @param v the value
    *
    * @return the evaluation
    *
    * @see Utils#approx(float, float)
    */
   public boolean contains ( final float v ) {

      return Utils.approx(this.real, v) || Utils.approx(this.imag.y, v) || Utils
         .approx(this.imag.x, v) || Utils.approx(this.imag.z, v);
   }

   /**
    * Returns a new quaternion with the division of the instance by the right
    * operand. For interoperability with Kotlin: <code>a / b</code> . <em>Does
    * not mutate the quaternion in place</em>.
    *
    * @param b the right operand
    *
    * @return the quotient
    */
   public KtQuat div ( final float b ) {

      if ( b != 0.0f ) {
         return new KtQuat(this.real / b, this.imag.x / b, this.imag.y / b,
            this.imag.z / b);
      }
      return new KtQuat(1.0f, 0.0f, 0.0f, 0.0f);
   }

   /**
    * Returns a new quaternion with the division of the instance by the right
    * operand. For interoperability with Kotlin: <code>a / b</code> . <em>Does
    * not mutate the quaternion in place</em>.
    *
    * @param b the right operand
    *
    * @return the quotient
    *
    * @see Quaternion#div(Quaternion, Quaternion, Quaternion)
    */
   public KtQuat div ( final Quaternion b ) {

      final KtQuat result = new KtQuat();
      Quaternion.div(this, b, result);
      return result;
   }

   /**
    * Divides the instance by the right operand (mutates the quaternion in
    * place). For interoperability with Kotlin: <code>a /= b</code> .
    *
    * @param b the right operand
    *
    * @see Quaternion#identity(Quaternion)
    */
   public void divAssign ( final float b ) {

      if ( b != 0.0f ) {
         final float bInv = 1.0f / b;
         final Vec3 i = this.imag;
         this.real *= bInv;
         i.x *= bInv;
         i.y *= bInv;
         i.z *= bInv;
      } else {
         Quaternion.identity(this);
      }
   }

   /**
    * Divides the instance by the right operand (mutates the quaternion in
    * place). For interoperability with Kotlin: <code>a /= b</code> .
    *
    * @param b the right operand
    *
    * @see Quaternion#div(Quaternion, Quaternion, Quaternion)
    */
   public void divAssign ( final Quaternion b ) {

      Quaternion.div(this, b, this);
   }

   /**
    * Returns a new quaternion with the subtraction of the right operand from
    * the instance. For interoperability with Kotlin: <code>a - b</code> .
    * <em>Does not mutate the quaternion in place</em>.
    *
    * @param b the right operand
    *
    * @return the subtraction
    */
   public KtQuat minus ( final Quaternion b ) {

      final Vec3 bi = b.imag;
      return new KtQuat(this.real - b.real, this.imag.x - bi.x, this.imag.y
         - bi.y, this.imag.z - bi.z);
   }

   /**
    * Subtracts the right operand from the instance (mutates the quaternion in
    * place). For interoperability with Kotlin: <code>a -= b</code> .
    *
    * @param b the right operand
    */
   public void minusAssign ( final Quaternion b ) {

      this.real -= b.real;
      final Vec3 i = this.imag;
      i.x -= b.imag.x;
      i.y -= b.imag.y;
      i.z -= b.imag.z;
   }

   /**
    * Returns a new quaternion with the addition of the right operand to the
    * instance. For interoperability with Kotlin: <code>a + b</code> .
    * <em>Does not mutate the quaternion in place</em>.
    *
    * @param b the right operand
    *
    * @return the addition
    */
   public KtQuat plus ( final Quaternion b ) {

      final Vec3 bi = b.imag;
      return new KtQuat(this.real + b.real, this.imag.x + bi.x, this.imag.y
         + bi.y, this.imag.z + bi.z);
   }

   /**
    * Adds the right operand to the instance (mutates the quaternion in
    * place). For interoperability with Kotlin: <code>a += b</code> .
    *
    * @param b the right operand
    */
   public void plusAssign ( final Quaternion b ) {

      this.real += b.real;
      final Vec3 i = this.imag;
      i.x += b.imag.x;
      i.y += b.imag.y;
      i.z += b.imag.z;
   }

   /**
    * Adds the right operand to the instance (mutates the quaternion in
    * place). Promotes the vector to a pure quaternion. For interoperability
    * with Kotlin: <code>a += b</code> .
    *
    * @param b the right operand
    */
   public void plusAssign ( final Vec3 b ) {

      final Vec3 i = this.imag;
      i.x += b.x;
      i.y += b.y;
      i.z += b.z;
   }

   /**
    * Returns a new quaternion with the product of the instance and the right
    * operand. For interoperability with Kotlin: <code>a * b</code> . <em>Does
    * not mutate the quaternion in place</em>.
    *
    * @param b the right operand
    *
    * @return the product
    */
   public KtQuat times ( final float b ) {

      if ( b != 0.0f ) {
         return new KtQuat(this.real * b, this.imag.x * b, this.imag.y * b,
            this.imag.z * b);
      }
      return new KtQuat(1.0f, 0.0f, 0.0f, 0.0f);
   }

   /**
    * Returns a new quaternion with the product of the instance and the right
    * operand. For interoperability with Kotlin: <code>a * b</code> . <em>Does
    * not mutate the quaternion in place</em>.
    *
    * @param b the right operand
    *
    * @return the product
    */
   public KtQuat times ( final Quaternion b ) {

      final Vec3 bi = b.imag;
      final float bw = b.real;
      return new KtQuat(this.real * bw - ( this.imag.x * bi.x + this.imag.y
         * bi.y + this.imag.z * bi.z ), this.imag.x * bw + this.real * bi.x
            + this.imag.y * bi.z - this.imag.z * bi.y, this.imag.y * bw
               + this.real * bi.y + this.imag.z * bi.x - this.imag.x * bi.z,
         this.imag.z * bw + this.real * bi.z + this.imag.x * bi.y - this.imag.y
            * bi.x);
   }

   /**
    * Multiplies the right operand with the instance (mutates the quaternion
    * in place). For interoperability with Kotlin: <code>a *= b</code> .
    *
    * @param b the right operand
    *
    * @see Quaternion#identity(Quaternion)
    */
   public void timesAssign ( final float b ) {

      final Vec3 i = this.imag;
      if ( b != 0.0f ) {
         this.real *= b;
         i.x *= b;
         i.y *= b;
         i.z *= b;
      } else {
         Quaternion.identity(this);
      }
   }

   /**
    * Multiplies the right operand with the instance (mutates the quaternion
    * in place). For interoperability with Kotlin: <code>a *= b</code> .
    *
    * @param b the right operand
    *
    * @see Quaternion#mul(Quaternion, Quaternion, Quaternion)
    */
   public void timesAssign ( final Quaternion b ) {

      Quaternion.mul(this, b, this);
   }

   /**
    * Returns a new quaternion with the negation of the instance. For
    * interoperability with Kotlin: <code>-a</code> . <em>Does not mutate the
    * quaternion in place</em>.
    *
    * @return the positive
    */
   public KtQuat unaryMinus ( ) {

      return new KtQuat(-this.real, -this.imag.x, -this.imag.y, -this.imag.z);
   }

   /**
    * Returns a new quaternion with the positive copy of the instance. For
    * interoperability with Kotlin: <code>+a</code> . <em>Does not mutate the
    * quaternion in place</em>.
    *
    * @return the positive
    */
   public KtQuat unaryPlus ( ) {

      return new KtQuat(this.real, this.imag.x, this.imag.y, this.imag.z);
   }

}
