package camzup.kotlin;

import java.util.Iterator;
import java.util.NoSuchElementException;

import camzup.core.Utils;
import camzup.core.Vec2;
import camzup.core.Vec3;
import camzup.core.Vec4;

/**
 * Provides Kotlin operator overloading support for four dimensional
 * vectors.
 */
public class KtVec4 extends Vec4 implements Iterable < Float > {

   /**
    * The default vector constructor.
    */
   public KtVec4 ( ) { super(); }

   /**
    * Constructs a vector from boolean values.
    *
    * @param x the x component
    * @param y the y component
    * @param z the z component
    * @param w the w component
    */
   public KtVec4 ( final boolean x, final boolean y, final boolean z,
      final boolean w ) {

      super(x, y, z, w);
   }

   /**
    * Constructs a vector from float values.
    *
    * @param x the x component
    * @param y the y component
    * @param z the z component
    * @param w the w component
    */
   public KtVec4 ( final float x, final float y, final float z,
      final float w ) {

      super(x, y, z, w);
   }

   /**
    * Promotes a Vec2 to a KtVec4.
    *
    * @param v2 the vector
    */
   public KtVec4 ( final Vec2 v2 ) { super(v2); }

   /**
    * Promotes a Vec3 to a KtVec4.
    *
    * @param v3 the vector
    */
   public KtVec4 ( final Vec3 v3 ) { super(v3); }

   /**
    * Promotes a Vec3 to a KtVec4 with an extra component.<br>
    * <br>
    * Useful for multiplying a 4 x 4 matrix with either a 3D vector or a 3D
    * point. (For points, w is 1.0; for vectors, w is 0.0 .)
    *
    * @param v3 the vector
    * @param w  the w component
    */
   public KtVec4 ( final Vec3 v3, final float w ) { super(v3, w); }

   /**
    * Constructs a vector from a source vector's components.
    *
    * @param source the source vector
    */
   public KtVec4 ( final Vec4 source ) { super(source); }

   /**
    * Tests to see if the vector contains a value.
    *
    * @param v the value
    *
    * @return the evaluation
    */
   public boolean contains ( final float v ) {

      return Utils.approx(this.w, v) || Utils.approx(this.z, v) || Utils.approx(
         this.y, v) || Utils.approx(this.x, v);
   }

   /**
    * Returns a new vector decremented by one. For interoperability with
    * Kotlin: <code>--a</code> (prefix) or <code>a--</code> (postfix). Per the
    * specification, <em>does not mutate the vector in place</em>.
    *
    * @return the decremented vector
    */
   public KtVec4 dec ( ) { return this.minus(1.0f); }

   /**
    * Returns a new vector with the division of the instance by the right
    * operand. For interoperability with Kotlin: <code>a / b</code> . <em>Does
    * not mutate the vector in place</em>.
    *
    * @param b the right operand
    *
    * @return the quotient
    */
   public KtVec4 div ( final float b ) {

      return b != 0.0f ? new KtVec4(this.x / b, this.y / b, this.z / b, this.w
         / b) : new KtVec4();
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
   public KtVec4 div ( final Vec4 b ) {

      return new KtVec4(Utils.div(this.x, b.x), Utils.div(this.y, b.y), Utils
         .div(this.z, b.z), Utils.div(this.w, b.w));
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
         this.w *= bInv;
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
   public void divAssign ( final Vec4 b ) {

      this.x = Utils.div(this.x, b.x);
      this.y = Utils.div(this.y, b.y);
      this.z = Utils.div(this.z, b.z);
      this.w = Utils.div(this.w, b.w);
   }

   /**
    * Simulates bracket subscript access in an array. When the provided index
    * is 3 or -1, returns w; 2 or -2, z; 1 or -3, y; 0 or -4, x.
    *
    * @param index the index
    *
    * @return the component at that index
    */
   public float get ( final int index ) {

      switch ( index ) {
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
    * Returns a new vector incremented by one. For interoperability with
    * Kotlin: <code>++a</code> (prefix) or <code>a++</code> (postfix). Per the
    * specification, <em>does not mutate the vector in place</em>.
    *
    * @return the incremented vector
    */
   public KtVec4 inc ( ) { return this.plus(1.0f); }

   /**
    * Returns an iterator for this vector, which allows its components to be
    * accessed in an enhanced for-loop.
    *
    * @return the iterator
    */
   @Override
   public Iterator < Float > iterator ( ) { return new V4Iterator(this); }

   /**
    * Returns a new vector with the subtraction of the right operand from the
    * instance. For interoperability with Kotlin: <code>a - b</code> .
    * <em>Does not mutate the vector in place</em>.
    *
    * @param b the right operand
    *
    * @return the subtraction
    */
   public KtVec4 minus ( final float b ) {

      return new KtVec4(this.x - b, this.y - b, this.z - b, this.w - b);
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
   public KtVec4 minus ( final Vec4 b ) {

      return new KtVec4(this.x - b.x, this.y - b.y, this.z - b.z, this.w - b.w);
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
      this.w -= b;
   }

   /**
    * Subtracts the right operand from the instance (mutates the vector in
    * place). For interoperability with Kotlin: <code>a -= b</code> .
    *
    * @param b the right operand
    */
   public void minusAssign ( final Vec4 b ) {

      this.x -= b.x;
      this.y -= b.y;
      this.z -= b.z;
      this.w -= b.w;
   }

   /**
    * Returns a new vector with the boolean opposite of the instance. For
    * interoperability with Kotlin: <code>!a</code> . <em>Does not mutate the
    * vector in place</em>.
    *
    * @return the opposite vector
    */
   public KtVec4 not ( ) {

      return new KtVec4(this.x == 0.0f, this.y == 0.0f, this.z == 0.0f, this.w
         == 0.0f);
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
   public KtVec4 plus ( final float b ) {

      return new KtVec4(this.x + b, this.y + b, this.z + b, this.w + b);
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
   public KtVec4 plus ( final Vec4 b ) {

      return new KtVec4(this.x + b.x, this.y + b.y, this.z + b.z, this.w + b.w);
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
      this.w += b;
   }

   /**
    * Adds the right operand to the instance (mutates the vector in place).
    * For interoperability with Kotlin: <code>a += b</code> .
    *
    * @param b the right operand
    */
   public void plusAssign ( final Vec4 b ) {

      this.x += b.x;
      this.y += b.y;
      this.z += b.z;
      this.w += b.w;
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
   public KtVec4 rem ( final float b ) {

      return b != 0.0f ? new KtVec4(this.x % b, this.y % b, this.z % b, this.w
         % b) : new KtVec4(this.x, this.y, this.z, this.w);
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
   public KtVec4 rem ( final Vec4 b ) {

      return new KtVec4(b.x != 0.0f ? this.x % b.x : this.x, b.y != 0.0f
         ? this.y % b.y : this.y, b.z != 0.0f ? this.z % b.z : this.z, b.w
            != 0.0f ? this.w % b.w : this.w);
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
         this.w %= b;
      }
   }

   /**
    * Assigns the signed remainder (<code>fmod</code>) of the instance and the
    * right operand to the instance (mutates the vector in place). For
    * interoperability with Kotlin: <code>a %= b</code> .
    *
    * @param b the right operand
    */
   public void remAssign ( final Vec4 b ) {

      if ( b.x != 0.0f ) { this.x %= b.x; }
      if ( b.y != 0.0f ) { this.y %= b.y; }
      if ( b.z != 0.0f ) { this.z %= b.z; }
      if ( b.w != 0.0f ) { this.w %= b.w; }
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
         case -4:
            this.x = value;
            break;

         case 1:
         case -3:
            this.y = value;
            break;

         case 2:
         case -2:
            this.z = value;
            break;

         case 3:
         case -1:
            this.w = value;
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
   public KtVec4 times ( final float b ) {

      return new KtVec4(this.x * b, this.y * b, this.z * b, this.w * b);
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
   public KtVec4 times ( final Vec4 b ) {

      return new KtVec4(this.x * b.x, this.y * b.y, this.z * b.z, this.w * b.w);
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
      this.w *= b;
   }

   /**
    * Multiplies the right operand with the instance (mutates the vector in
    * place). For interoperability with Kotlin: <code>a *= b</code> .
    *
    * @param b the right operand
    */
   public void timesAssign ( final Vec4 b ) {

      this.x *= b.x;
      this.y *= b.y;
      this.z *= b.z;
      this.w *= b.w;
   }

   /**
    * Returns a new vector with the negation of the instance. For
    * interoperability with Kotlin: <code>-a</code> . <em>Does not mutate the
    * vector in place</em>.
    *
    * @return the negation
    */
   public KtVec4 unaryMinus ( ) {

      return new KtVec4(-this.x, -this.y, -this.z, -this.w);
   }

   /**
    * Returns a new vector with the positive copy of the instance. For
    * interoperability with Kotlin: <code>+a</code> . <em>Does not mutate the
    * vector in place</em>.
    *
    * @return the positive
    */
   public KtVec4 unaryPlus ( ) {

      return new KtVec4(this.x, this.y, this.z, this.w);
   }

   /**
    * An iterator, which allows a vector's components to be accessed in an
    * enhanced for loop.
    */
   public static final class V4Iterator implements Iterator < Float > {

      /**
       * The current index.
       */
      private int index = 0;

      /**
       * The vector being iterated over.
       */
      private final KtVec4 vec;

      /**
       * The default constructor.
       *
       * @param vec the vector to iterate
       */
      public V4Iterator ( final KtVec4 vec ) { this.vec = vec; }

      /**
       * Tests to see if the iterator has another value.
       *
       * @return the evaluation
       */
      @Override
      public boolean hasNext ( ) { return this.index < V4Iterator.LENGTH; }

      /**
       * Gets the next value in the iterator.
       *
       * @return the value
       *
       * @see KtVec4#get(int)
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
      public static final int LENGTH = 4;

   }

}
