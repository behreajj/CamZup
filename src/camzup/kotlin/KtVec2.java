package camzup.kotlin;

import java.util.Iterator;
import java.util.NoSuchElementException;

import camzup.core.Utils;
import camzup.core.Vec2;

/**
 * Provides Kotlin operator overloading support for two dimensional
 * vectors.
 */
public class KtVec2 extends Vec2 implements Iterable < Float > {

   /**
    * The default vector constructor.
    */
   public KtVec2 ( ) { super(); }

   /**
    * Constructs a vector from boolean values.
    *
    * @param x the x component
    * @param y the y component
    */
   public KtVec2 ( final boolean x, final boolean y ) { super(x, y); }

   /**
    * Constructs a vector from float values.
    *
    * @param x the x component
    * @param y the y component
    */
   public KtVec2 ( final float x, final float y ) { super(x, y); }

   /**
    * Attempts to construct a vector from Strings using
    * {@link Float#parseFloat(String)} . If a NumberFormatException is thrown,
    * the component is set to zero.
    *
    * @param x the x string
    * @param y the y string
    */
   public KtVec2 ( final String x, final String y ) { super(x, y); }

   /**
    * Constructs a vector from a source vector's components.
    *
    * @param v the source vector
    */
   public KtVec2 ( final Vec2 v ) { super(v); }

   /**
    * Returns a new vector decremented by one. For interoperability with
    * Kotlin: <code>--a</code> (prefix) or <code>a--</code> (postfix). Per the
    * specification, <em>does not mutate the vector in place</em>.
    *
    * @return the decremented vector
    */
   public KtVec2 dec ( ) { return this.minus(1.0f); }

   /**
    * Returns a new vector with the division of the instance by the right
    * operand. For interoperability with Kotlin: <code>a / b</code> . <em>Does
    * not mutate the vector in place</em>.
    *
    * @param b the right operand
    *
    * @return the quotient
    */
   public KtVec2 div ( final float b ) {

      return b != 0.0f ? new KtVec2(this.x / b, this.y / b) : new KtVec2();
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
   public KtVec2 div ( final Vec2 b ) {

      return new KtVec2(Utils.div(this.x, b.x), Utils.div(this.y, b.y));
   }

   /**
    * Divides the instance by the right operand (mutates the vector in place).
    * For interoperability with Kotlin: <code>a /= b</code> .
    *
    * @param b the right operand
    */
   public void divAssign ( final float b ) {

      if ( b != 0.0f ) {
         this.x /= b;
         this.y /= b;
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
   public void divAssign ( final Vec2 b ) {

      this.x = Utils.div(this.x, b.x);
      this.y = Utils.div(this.y, b.y);
   }

   /**
    * Simulates bracket subscript access in an array. When the provided index
    * is 1 or -1, returns y; 0 or -2, x.
    *
    * @param index the index
    *
    * @return the component at that index
    */
   public float get ( final int index ) {

      switch ( index ) {
         case 0:
         case -2:
            return this.x;

         case 1:
         case -1:
            return this.y;

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
   public KtVec2 inc ( ) { return this.plus(1.0f); }

   /**
    * Returns an iterator for this vector, which allows its components to be
    * accessed in an enhanced for-loop.
    *
    * @return the iterator
    */
   @Override
   public Iterator < Float > iterator ( ) { return new V2Iterator(this); }

   /**
    * Returns a new vector with the subtraction of the right operand from the
    * instance. For interoperability with Kotlin: <code>a - b</code> .
    * <em>Does not mutate the vector in place</em>.
    *
    * @param b the right operand
    *
    * @return the subtraction
    */
   public KtVec2 minus ( final float b ) {

      return new KtVec2(this.x - b, this.y - b);
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
   public KtVec2 minus ( final Vec2 b ) {

      return new KtVec2(this.x - b.x, this.y - b.y);
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
   }

   /**
    * Subtracts the right operand from the instance (mutates the vector in
    * place). For interoperability with Kotlin: <code>a -= b</code> .
    *
    * @param b the right operand
    */
   public void minusAssign ( final Vec2 b ) {

      this.x -= b.x;
      this.y -= b.y;
   }

   /**
    * Returns a new vector with the boolean opposite of the instance. For
    * interoperability with Kotlin: <code>!a</code> . <em>Does not mutate the
    * vector in place</em>.
    *
    * @return the opposite vector
    */
   public KtVec2 not ( ) {

      return new KtVec2(this.x == 0.0f, this.y == 0.0f);
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
   public KtVec2 plus ( final float b ) {

      return new KtVec2(this.x + b, this.y + b);
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
   public KtVec2 plus ( final Vec2 b ) {

      return new KtVec2(this.x + b.x, this.y + b.y);
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
   }

   /**
    * Adds the right operand to the instance (mutates the vector in place).
    * For interoperability with Kotlin: <code>a += b</code> .
    *
    * @param b the right operand
    */
   public void plusAssign ( final Vec2 b ) {

      this.x += b.x;
      this.y += b.y;
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
   public KtVec2 rem ( final float b ) {

      return b != 0.0f ? new KtVec2(this.x % b, this.y % b) : new KtVec2(this.x,
         this.y);
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
   public KtVec2 rem ( final Vec2 b ) {

      return new KtVec2(b.x != 0.0f ? this.x % b.x : this.x, b.y != 0.0f
         ? this.y % b.y : this.y);
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
      }
   }

   /**
    * Assigns the signed remainder (<code>fmod</code>) of the instance and the
    * right operand to the instance (mutates the vector in place). For
    * interoperability with Kotlin: <code>a %= b</code> .
    *
    * @param b the right operand
    */
   public void remAssign ( final Vec2 b ) {

      if ( b.x != 0.0f ) { this.x %= b.x; }
      if ( b.y != 0.0f ) { this.y %= b.y; }
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
         case -2:
            this.x = value;
            break;

         case 1:
         case -1:
            this.y = value;
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
   public KtVec2 times ( final float b ) {

      return new KtVec2(this.x * b, this.y * b);
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
   public KtVec2 times ( final Vec2 b ) {

      return new KtVec2(this.x * b.x, this.y * b.y);
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
   }

   /**
    * Multiplies the right operand with the instance (mutates the vector in
    * place). For interoperability with Kotlin: <code>a *= b</code> .
    *
    * @param b the right operand
    */
   public void timesAssign ( final Vec2 b ) {

      this.x *= b.x;
      this.y *= b.y;
   }

   /**
    * Returns a new vector with the negation of the instance. For
    * interoperability with Kotlin: <code>-a</code> . <em>Does not mutate the
    * vector in place</em>.
    *
    * @return the negation
    */
   public KtVec2 unaryMinus ( ) { return new KtVec2(-this.x, -this.y); }

   /**
    * Returns a new vector with the positive copy of the instance. For
    * interoperability with Kotlin: <code>+a</code> . <em>Does not mutate the
    * vector in place</em>.
    *
    * @return the positive
    */
   public KtVec2 unaryPlus ( ) { return new KtVec2(this.x, this.y); }

   /**
    * An iterator, which allows a vector's components to be accessed in an
    * enhanced for loop.
    */
   public static final class V2Iterator implements Iterator < Float > {

      /**
       * The current index.
       */
      private int index = 0;

      /**
       * The vector being iterated over.
       */
      private final KtVec2 vec;

      /**
       * The default constructor.
       *
       * @param vec the vector to iterate
       */
      public V2Iterator ( final KtVec2 vec ) { this.vec = vec; }

      /**
       * Tests to see if the iterator has another value.
       *
       * @return the evaluation
       */
      @Override
      public boolean hasNext ( ) { return this.index < this.vec.length(); }

      /**
       * Gets the next value in the iterator.
       *
       * @return the value
       *
       * @see KtVec2#get(int)
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

   }

}
