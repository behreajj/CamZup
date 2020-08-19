package camzup.kotlin;

import camzup.core.Quaternion;
import camzup.core.Vec3;

/**
 * Provides Kotlin operator overloading support for quaternions.
 */
public class KtQuat extends Quaternion {

   public KtQuat ( ) { super(); }

   public KtQuat ( final float real, final float xImag, final float yImag,
      final float zImag ) {

      super(real, xImag, yImag, zImag);
   }

   public KtQuat ( final float real, final Vec3 imag ) { super(real, imag); }

   public KtQuat ( final Quaternion source ) { super(source); }

   public KtQuat ( final String real, final String xImag, final String yImag,
      final String zImag ) {

      super(real, xImag, yImag, zImag);
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
      return new KtQuat();
   }

   /**
    * Divides the instance by the right operand (mutates the quaternion in
    * place). For interoperability with Kotlin: <code>a /= b</code> .
    *
    * @param b the right operand
    *
    * @see Quaternion#div(Quaternion, float, Quaternion)
    */
   public void divAssign ( final float b ) {

      if ( b != 0.0f ) {
         this.real /= b;
         final Vec3 i = this.imag;
         i.x /= b;
         i.y /= b;
         i.z /= b;
      } else {
         this.reset();
      }
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
   public KtQuat minus ( final float b ) {

      return new KtQuat(this.real - b, this.imag.x, this.imag.y, this.imag.z);
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
    * Returns a new quaternion with the subtraction of the right operand from
    * the instance. For interoperability with Kotlin: <code>a - b</code> .
    * <em>Does not mutate the quaternion in place</em>.
    *
    * @param b the right operand
    *
    * @return the subtraction
    */
   public KtQuat minus ( final Vec3 b ) {

      return new KtQuat(this.real, this.imag.x - b.x, this.imag.y - b.y,
         this.imag.z - b.z);
   }

   /**
    * Subtracts the right operand from the instance (mutates the quaternion in
    * place). For interoperability with Kotlin: <code>a -= b</code> .
    *
    * @param b the right operand
    */
   public void minusAssign ( final float b ) { this.real -= b; }

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
    * Subtracts the right operand from the instance (mutates the quaternion in
    * place). For interoperability with Kotlin: <code>a -= b</code> .
    *
    * @param b the right operand
    */
   public void minusAssign ( final Vec3 b ) {

      final Vec3 i = this.imag;
      i.x -= b.x;
      i.y -= b.y;
      i.z -= b.z;
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
   public KtQuat plus ( final float b ) {

      return new KtQuat(this.real + b, this.imag.x, this.imag.y, this.imag.z);
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
    * Returns a new quaternion with the addition of the right operand to the
    * instance. For interoperability with Kotlin: <code>a + b</code> .
    * <em>Does not mutate the quaternion in place</em>.
    *
    * @param b the right operand
    *
    * @return the addition
    */
   public KtQuat plus ( final Vec3 b ) {

      return new KtQuat(this.real, this.imag.x + b.x, this.imag.y + b.y,
         this.imag.z + b.z);
   }

   /**
    * Adds the right operand to the instance (mutates the quaternion in
    * place). For interoperability with Kotlin: <code>a += b</code> .
    *
    * @param b the right operand
    */
   public void plusAssign ( final float b ) { this.real += b; }

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
    * place). For interoperability with Kotlin: <code>a += b</code> .
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

      return new KtQuat(this.real * b, this.imag.x * b, this.imag.y * b,
         this.imag.z * b);
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
    * Returns a new quaternion with the product of the instance and the right
    * operand. For interoperability with Kotlin: <code>a * b</code> . <em>Does
    * not mutate the quaternion in place</em>.
    *
    * @param b the right operand
    *
    * @return the product
    */
   public KtQuat times ( final Vec3 b ) {

      return new KtQuat(- ( this.imag.x * b.x + this.imag.y * b.y + this.imag.z
         * b.z ), this.real * b.x + this.imag.y * b.z - this.imag.z * b.y,
         this.real * b.y + this.imag.z * b.x - this.imag.x * b.z, this.real
            * b.z + this.imag.x * b.y - this.imag.y * b.x);
   }

   /**
    * Multiplies the right operand with the instance (mutates the quaternion
    * in place). For interoperability with Kotlin: <code>a *= b</code> .
    *
    * @param b the right operand
    */
   public void timesAssign ( final float b ) {

      this.real *= b;
      final Vec3 i = this.imag;
      i.x *= b;
      i.y *= b;
      i.z *= b;
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
    * Multiplies the right operand with the instance (mutates the quaternion
    * in place). For interoperability with Kotlin: <code>a *= b</code> .
    *
    * @param b the right operand
    */
   public void timesAssign ( final Vec3 b ) {

      this.set(- ( this.imag.x * b.x + this.imag.y * b.y + this.imag.z * b.z ),
         this.real * b.x + this.imag.y * b.z - this.imag.z * b.y, this.real
            * b.y + this.imag.z * b.x - this.imag.x * b.z, this.real * b.z
               + this.imag.x * b.y - this.imag.y * b.x);
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
