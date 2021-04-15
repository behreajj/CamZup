package camzup.kotlin;

import java.util.Iterator;
import java.util.NoSuchElementException;

import camzup.core.Mat3;
import camzup.core.Quaternion;
import camzup.core.Utils;
import camzup.core.Vec2;
import camzup.core.Vec3;

/**
 * Provides Kotlin operator overloading support for three dimensional
 * vectors.
 */
public class KtVec3 extends Vec3 implements Iterable < Float > {

   /**
    * The default vector constructor.
    */
   public KtVec3 ( ) {}

   /**
    * Constructs a vector from boolean values.
    *
    * @param x the x component
    * @param y the y component
    * @param z the z component
    */
   public KtVec3 ( final boolean x, final boolean y, final boolean z ) {

      super(x, y, z);
   }

   /**
    * Constructs a vector from float values.
    *
    * @param x the x component
    * @param y the y component
    * @param z the z component
    */
   public KtVec3 ( final float x, final float y, final float z ) {

      super(x, y, z);
   }

   /**
    * Promotes a Vec2 to a KtVec3.
    *
    * @param v2 the vector
    */
   public KtVec3 ( final Vec2 v2 ) { super(v2); }

   /**
    * Promotes a Vec2 to a KtVec3 with an extra component.
    *
    * @param v2 the vector
    * @param z  the z component
    */
   public KtVec3 ( final Vec2 v2, final float z ) { super(v2, z); }

   /**
    * Promotes a Vec2 to a Vec3 with an extra component.
    *
    * @param source the vector
    */
   public KtVec3 ( final Vec3 source ) { super(source); }

   /**
    * Tests to see if the vector contains a value.
    *
    * @param v the value
    *
    * @return the evaluation
    *
    * @see Utils#approx(float, float)
    */
   public boolean contains ( final float v ) {

      return Utils.approx(this.z, v) || Utils.approx(this.y, v) || Utils.approx(
         this.x, v);
   }

   /**
    * Returns a new vector decremented by one. For interoperability with
    * Kotlin: <code>--a</code> (prefix) or <code>a--</code> (postfix). Per the
    * specification, <em>does not mutate the vector in place</em>.
    *
    * @return the decremented vector
    */
   public KtVec3 dec ( ) { return this.minus(1.0f); }

   /**
    * Returns a new vector with the division of the instance by the right
    * operand. For interoperability with Kotlin: <code>a / b</code> . <em>Does
    * not mutate the vector in place</em>.
    *
    * @param b the right operand
    *
    * @return the quotient
    */
   public KtVec3 div ( final float b ) {

      return b != 0.0f ? new KtVec3(this.x / b, this.y / b, this.z / b)
         : new KtVec3();
   }

   /**
    * Returns a new vector with the division of the instance by the right
    * operand. For interoperability with Kotlin: <code>a / b</code> . <em>Does
    * not mutate the vector in place</em>.
    *
    * @param b the right operand
    *
    * @return the quotient
    */
   public KtVec3 div ( final Vec3 b ) {

      return new KtVec3(Utils.div(this.x, b.x), Utils.div(this.y, b.y), Utils
         .div(this.z, b.z));
   }

   /**
    * Divides the instance by the right operand (mutates the vector in place).
    * For interoperability with Kotlin: <code>a /= b</code> .
    *
    * @param b the right operand
    */
   public void divAssign ( final float b ) {

      if ( b != 0.0f ) {
         final float bInv = 1.0f / b;
         this.x *= bInv;
         this.y *= bInv;
         this.z *= bInv;
      } else {
         this.reset();
      }
   }

   /**
    * Divides the instance by the right operand (mutates the vector in place).
    * For interoperability with Kotlin: <code>a /= b</code> .
    *
    * @param b the right operand
    */
   public void divAssign ( final Vec3 b ) {

      this.x = Utils.div(this.x, b.x);
      this.y = Utils.div(this.y, b.y);
      this.z = Utils.div(this.z, b.z);
   }

   /**
    * Simulates bracket subscript access in an array. When the provided index
    * is 2 or -1, returns z; 1 or -2, y; 0 or -3, x.
    *
    * @param index the index
    *
    * @return the component at that index
    */
   public float get ( final int index ) {

      switch ( index ) {
         case 0:
         case -3:
            return this.x;

         case 1:
         case -2:
            return this.y;

         case 2:
         case -1:
            return this.z;

         default:
            return 0.0f;
      }
   }

   /**
    * Returns a new vector incremented by one. For interoperability with
    * Kotlin: <code>++a</code> (prefix) or <code>a++</code> (postfix). Per the
    * specification, <em>does not mutate the vector in place</em>.
    *
    * @return the incremented vector
    */
   public KtVec3 inc ( ) { return this.plus(1.0f); }

   /**
    * Returns an iterator for this vector, which allows its components to be
    * accessed in an enhanced for-loop.
    *
    * @return the iterator
    */
   @Override
   public Iterator < Float > iterator ( ) { return new V3Iterator(this); }

   /**
    * Returns a new vector with the subtraction of the right operand from the
    * instance. For interoperability with Kotlin: <code>a - b</code> .
    * <em>Does not mutate the vector in place</em>.
    *
    * @param b the right operand
    *
    * @return the subtraction
    */
   public KtVec3 minus ( final float b ) {

      return new KtVec3(this.x - b, this.y - b, this.z - b);
   }

   /**
    * Returns a new vector with the subtraction of the right operand from the
    * instance. For interoperability with Kotlin: <code>a - b</code> .
    * <em>Does not mutate the vector in place</em>.
    *
    * @param b the right operand
    *
    * @return the subtraction
    */
   public KtVec3 minus ( final Vec3 b ) {

      return new KtVec3(this.x - b.x, this.y - b.y, this.z - b.z);
   }

   /**
    * Subtracts the right operand from the instance (mutates the vector in
    * place). For interoperability with Kotlin: <code>a -= b</code> .
    *
    * @param b the right operand
    */
   public void minusAssign ( final float b ) {

      this.x -= b;
      this.y -= b;
      this.z -= b;
   }

   /**
    * Subtracts the right operand from the instance (mutates the vector in
    * place). For interoperability with Kotlin: <code>a -= b</code> .
    *
    * @param b the right operand
    */
   public void minusAssign ( final Vec3 b ) {

      this.x -= b.x;
      this.y -= b.y;
      this.z -= b.z;
   }

   /**
    * Returns a new vector with the boolean opposite of the instance. For
    * interoperability with Kotlin: <code>!a</code> . <em>Does not mutate the
    * vector in place</em>.
    *
    * @return the opposite vector
    */
   public KtVec3 not ( ) {

      return new KtVec3(this.x == 0.0f, this.y == 0.0f, this.z == 0.0f);
   }

   /**
    * Returns a new vector with the addition of the right operand to the
    * instance. For interoperability with Kotlin: <code>a + b</code> .
    * <em>Does not mutate the vector in place</em>.
    *
    * @param b the right operand
    *
    * @return the sum
    */
   public KtVec3 plus ( final float b ) {

      return new KtVec3(this.x + b, this.y + b, this.z + b);
   }

   /**
    * Returns a new vector with the addition of the right operand to the
    * instance. For interoperability with Kotlin: <code>a + b</code> .
    * <em>Does not mutate the vector in place</em>.
    *
    * @param b the right operand
    *
    * @return the sum
    */
   public KtVec3 plus ( final Vec3 b ) {

      return new KtVec3(this.x + b.x, this.y + b.y, this.z + b.z);
   }

   /**
    * Adds the right operand to the instance (mutates the vector in place).
    * For interoperability with Kotlin: <code>a += b</code> .
    *
    * @param b the right operand
    */
   public void plusAssign ( final float b ) {

      this.x += b;
      this.y += b;
      this.z += b;
   }

   /**
    * Adds the right operand to the instance (mutates the vector in place).
    * For interoperability with Kotlin: <code>a += b</code> .
    *
    * @param b the right operand
    */
   public void plusAssign ( final Vec3 b ) {

      this.x += b.x;
      this.y += b.y;
      this.z += b.z;
   }

   /**
    * Returns a new vector with the signed remainder (<code>fmod</code>) of
    * the instance and the right operand. For interoperability with Kotlin:
    * <code>a % b</code> . <em>Does not mutate the vector in place</em>.
    *
    * @param b the right operand
    *
    * @return the signed remainder
    */
   public KtVec3 rem ( final float b ) {

      return b != 0.0f ? new KtVec3(this.x % b, this.y % b, this.z % b)
         : new KtVec3(this.x, this.y, this.z);
   }

   /**
    * Returns a new vector with the signed remainder (<code>fmod</code>) of
    * the instance and the right operand. For interoperability with Kotlin:
    * <code>a % b</code> . <em>Does not mutate the vector in place</em>.
    *
    * @param b the right operand
    *
    * @return the signed remainder
    */
   public KtVec3 rem ( final Vec3 b ) {

      return new KtVec3(b.x != 0.0f ? this.x % b.x : this.x, b.y != 0.0f
         ? this.y % b.y : this.y, b.z != 0.0f ? this.z % b.z : this.z);
   }

   /**
    * Assigns the signed remainder (<code>fmod</code>) of the instance and the
    * right operand to the instance (mutates the vector in place). For
    * interoperability with Kotlin: <code>a %= b</code> .
    *
    * @param b the right operand
    */
   public void remAssign ( final float b ) {

      if ( b != 0.0f ) {
         this.x %= b;
         this.y %= b;
         this.z %= b;
      }
   }

   /**
    * Assigns the signed remainder (<code>fmod</code>) of the instance and the
    * right operand to the instance (mutates the vector in place). For
    * interoperability with Kotlin: <code>a %= b</code> .
    *
    * @param b the right operand
    */
   public void remAssign ( final Vec3 b ) {

      if ( b.x != 0.0f ) { this.x %= b.x; }
      if ( b.y != 0.0f ) { this.y %= b.y; }
      if ( b.z != 0.0f ) { this.z %= b.z; }
   }

   /**
    * Simulates bracket subscript access in an array. When the provided index
    * is 3 or -1, sets w; 2 or -2, z; 1 or -3, y; 0 or -4, x.
    *
    * @param index the index
    * @param value the value
    */
   public void set ( final int index, final float value ) {

      switch ( index ) {
         case 0:
         case -3:
            this.x = value;
            break;

         case 1:
         case -2:
            this.y = value;
            break;

         case 2:
         case -1:
            this.z = value;
            break;

         default:
      }
   }

   /**
    * Returns a new vector with the product of the instance and the right
    * operand. For interoperability with Kotlin: <code>a * b</code> . <em>Does
    * not mutate the vector in place</em>.
    *
    * @param b the right operand
    *
    * @return the product
    */
   public KtVec3 times ( final float b ) {

      return new KtVec3(this.x * b, this.y * b, this.z * b);
   }

   /**
    * Returns a new vector with the product of the instance and the right
    * operand. Following GLSL convention, multiplies a vector and the
    * transpose of a matrix.<br>
    * <br>
    * v<sup>T</sup> M = ( M<sup>T</sup> v )<sup>T</sup><br>
    * <br>
    * For interoperability with Kotlin: <code>a * b</code> . <em>Does not
    * mutate the vector in place</em>.
    *
    * @param b the right operand
    *
    * @return the product
    */
   public KtVec3 times ( final Mat3 b ) {

      return new KtVec3(b.m00 * this.x + b.m10 * this.y + b.m20 * this.z, b.m01
         * this.x + b.m11 * this.y + b.m21 * this.z, b.m02 * this.x + b.m12
            * this.y + b.m22 * this.z);
   }

   /**
    * The product of the instance, promoted to a quaternion, and another
    * quaternion. Returns a quaternion. For interoperability with Kotlin:
    * <code>a * b</code> . <em>Does not mutate the vector in place</em>.
    *
    * @param b the quaternion
    *
    * @return the product
    */
   public KtQuat times ( final Quaternion b ) {

      final Vec3 bi = b.imag;
      final float bw = b.real;
      return new KtQuat(- ( this.x * bi.x + this.y * bi.y + this.z * bi.z ),
         this.x * bw + this.y * bi.z - this.z * bi.y, this.y * bw + this.z
            * bi.x - this.x * bi.z, this.z * bw + this.x * bi.y - this.y
               * bi.x);
   }

   /**
    * Returns a new vector with the product of the instance and the right
    * operand. For interoperability with Kotlin: <code>a * b</code> . <em>Does
    * not mutate the vector in place</em>.
    *
    * @param b the right operand
    *
    * @return the product
    */
   public KtVec3 times ( final Vec3 b ) {

      return new KtVec3(this.x * b.x, this.y * b.y, this.z * b.z);
   }

   /**
    * Multiplies the right operand with the instance (mutates the vector in
    * place). For interoperability with Kotlin: <code>a *= b</code> .
    *
    * @param b the right operand
    */
   public void timesAssign ( final float b ) {

      this.x *= b;
      this.y *= b;
      this.z *= b;
   }

   /**
    * Multiplies the right operand with the instance (mutates the vector in
    * place). Following GLSL convention, multiplies a vector and the transpose
    * of a matrix.<br>
    * <br>
    * v<sup>T</sup> M = ( M<sup>T</sup> v )<sup>T</sup><br>
    * <br>
    * For interoperability with Kotlin: <code>a *= b</code> .
    *
    * @param b the right operand
    */
   public void timesAssign ( final Mat3 b ) {

      final float ax = this.x;
      final float ay = this.y;
      final float az = this.z;

      this.x = b.m00 * ax + b.m10 * ay + b.m20 * az;
      this.y = b.m01 * ax + b.m11 * ay + b.m21 * az;
      this.z = b.m02 * ax + b.m12 * ay + b.m22 * az;
   }

   /**
    * Multiplies the right operand with the instance (mutates the vector in
    * place). For interoperability with Kotlin: <code>a *= b</code> .
    *
    * @param b the right operand
    */
   public void timesAssign ( final Vec3 b ) {

      this.x *= b.x;
      this.y *= b.y;
      this.z *= b.z;
   }

   /**
    * Returns a new vector with the negation of the instance. For
    * interoperability with Kotlin: <code>-a</code> . <em>Does not mutate the
    * vector in place</em>.
    *
    * @return the negation
    */
   public KtVec3 unaryMinus ( ) {

      return new KtVec3(-this.x, -this.y, -this.z);
   }

   /**
    * Returns a new vector with the positive copy of the instance. For
    * interoperability with Kotlin: <code>+a</code> . <em>Does not mutate the
    * vector in place</em>.
    *
    * @return the positive
    */
   public KtVec3 unaryPlus ( ) {

      return new KtVec3(this.x, this.y, this.z);
   }

   /**
    * An iterator, which allows a vector's components to be accessed in an
    * enhanced for loop.
    */
   public static final class V3Iterator implements Iterator < Float > {

      /**
       * The current index.
       */
      private int index = 0;

      /**
       * The vector being iterated over.
       */
      private final KtVec3 vec;

      /**
       * The default constructor.
       *
       * @param vec the vector to iterate
       */
      public V3Iterator ( final KtVec3 vec ) { this.vec = vec; }

      /**
       * Tests to see if the iterator has another value.
       *
       * @return the evaluation
       */
      @Override
      public boolean hasNext ( ) { return this.index < V3Iterator.LENGTH; }

      /**
       * Gets the next value in the iterator.
       *
       * @return the value
       *
       * @see KtVec3#get(int)
       */
      @Override
      public Float next ( ) {

         if ( !this.hasNext() ) { throw new NoSuchElementException(); }
         return this.vec.get(this.index++);
      }

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

      /**
       * The length of the vector.
       */
      public static final int LENGTH = 3;

   }

}
