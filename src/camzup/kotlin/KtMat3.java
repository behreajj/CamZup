package camzup.kotlin;

import java.util.Iterator;
import java.util.NoSuchElementException;

import camzup.core.Mat3;
import camzup.core.Utils;
import camzup.core.Vec2;
import camzup.core.Vec3;

/**
 * Provides Kotlin operator overloading support for three dimensional
 * matrices.
 */
public class KtMat3 extends Mat3 implements Iterable < KtVec3 > {

   /**
    * The default constructor. Creates an identity matrix.
    */
   public KtMat3 ( ) {}

   /**
    * Constructs a matrix from boolean values.
    *
    * @param m00 row 0, column 0
    * @param m01 row 0, column 1
    * @param m02 row 0, column 2
    * @param m10 row 1, column 0
    * @param m11 row 1, column 1
    * @param m12 row 1, column 2
    * @param m20 row 2, column 0
    * @param m21 row 2, column 1
    * @param m22 row 2, column 2
    */
   public KtMat3 ( final boolean m00, final boolean m01, final boolean m02,
      final boolean m10, final boolean m11, final boolean m12,
      final boolean m20, final boolean m21, final boolean m22 ) {

      super(m00, m01, m02, m10, m11, m12, m20, m21, m22);
   }

   /**
    * Constructs a matrix from float values.
    *
    * @param m00 row 0, column 0
    * @param m01 row 0, column 1
    * @param m10 row 1, column 0
    * @param m11 row 1, column 1
    */
   public KtMat3 ( final float m00, final float m01, final float m10,
      final float m11 ) {

      super(m00, m01, m10, m11);
   }

   /**
    * Constructs a matrix from float values.
    *
    * @param m00 row 0, column 0
    * @param m01 row 0, column 1
    * @param m02 row 0, column 2
    * @param m10 row 1, column 0
    * @param m11 row 1, column 1
    * @param m12 row 1, column 2
    */
   public KtMat3 ( final float m00, final float m01, final float m02,
      final float m10, final float m11, final float m12 ) {

      super(m00, m01, m02, m10, m11, m12);
   }

   /**
    * Constructs a matrix from float values.
    *
    * @param m00 row 0, column 0
    * @param m01 row 0, column 1
    * @param m02 row 0, column 2
    * @param m10 row 1, column 0
    * @param m11 row 1, column 1
    * @param m12 row 1, column 2
    * @param m20 row 2, column 0
    * @param m21 row 2, column 1
    * @param m22 row 2, column 2
    */
   public KtMat3 ( final float m00, final float m01, final float m02,
      final float m10, final float m11, final float m12, final float m20,
      final float m21, final float m22 ) {

      super(m00, m01, m02, m10, m11, m12, m20, m21, m22);
   }

   /**
    * Constructs a matrix from a source matrix's components.
    *
    * @param source the source matrix
    */
   public KtMat3 ( final Mat3 source ) { super(source); }

   /**
    * Tests to see if the matrix contains a value.
    *
    * @param v the value
    *
    * @return the evaluation
    *
    * @see Utils#approx(float, float)
    */
   public boolean contains ( final float v ) {

      if ( Utils.approx(this.m00, v) || Utils.approx(this.m01, v) || Utils
         .approx(this.m02, v) || Utils.approx(this.m10, v) ) {
         return true;
      }
      if ( Utils.approx(this.m11, v) || Utils.approx(this.m12, v) || Utils
         .approx(this.m20, v) || Utils.approx(this.m21, v) ) {
         return true;
      }
      return Utils.approx(this.m22, v);
   }

   /**
    * Returns a new matrix with the division of the instance by the right
    * operand. For interoperability with Kotlin: <code>a / b</code> . <em>Does
    * not mutate the matrix in place</em>.
    *
    * @param b the right operand
    *
    * @return the quotient
    *
    * @see KtMat3#times(float)
    */
   public KtMat3 div ( final float b ) {

      if ( b != 0.0f ) { return this.times(1.0f / b); }
      return new KtMat3();
   }

   /**
    * Divides the instance by the right operand (mutates the matrix in place).
    * For interoperability with Kotlin: <code>a /= b</code> .
    *
    * @param b the right operand
    *
    * @see KtMat3#timesAssign(float)
    * @see Mat3#identity(Mat3)
    */
   public void divAssign ( final float b ) {

      if ( b != 0.0f ) {
         this.timesAssign(1.0f / b);
      } else {
         Mat3.identity(this);
      }
   }

   /**
    * Gets a column vector from this matrix. For interoperability with Kotlin:
    * <code>b = a[i]</code> .
    *
    * @param j the column index
    *
    * @return the column vector
    *
    * @see Mat3#getCol(int, Vec3)
    */
   public KtVec3 get ( final int j ) {

      final KtVec3 result = new KtVec3();
      this.getCol(j, result);
      return result;
   }

   /**
    * Returns an iterator for this matrix, which allows its components to be
    * accessed in an enhanced for-loop.
    *
    * @return the iterator
    */
   @Override
   public Iterator < KtVec3 > iterator ( ) { return new M3Iterator(this); }

   /**
    * Returns a new matrix with the subtraction of the right operand from the
    * instance. For interoperability with Kotlin: <code>a - b</code> .
    * <em>Does not mutate the matrix in place</em>.
    *
    * @param b the right operand
    *
    * @return the subtraction
    */
   public KtMat3 minus ( final Mat3 b ) {

      return new KtMat3(this.m00 - b.m00, this.m01 - b.m01, this.m02 - b.m02,
         this.m10 - b.m10, this.m11 - b.m11, this.m12 - b.m12, this.m20 - b.m20,
         this.m21 - b.m21, this.m22 - b.m22);
   }

   /**
    * Subtracts the right operand from the instance (mutates the matrix in
    * place). For interoperability with Kotlin: <code>a -= b</code> .
    *
    * @param b the right operand
    */
   public void minusAssign ( final Mat3 b ) {

      this.m00 -= b.m00;
      this.m01 -= b.m01;
      this.m02 -= b.m02;

      this.m10 -= b.m10;
      this.m11 -= b.m11;
      this.m12 -= b.m12;

      this.m20 -= b.m20;
      this.m21 -= b.m21;
      this.m22 -= b.m22;
   }

   /**
    * Returns a new matrix with the boolean opposite of the instance. For
    * interoperability with Kotlin: <code>!a</code> . <em>Does not mutate the
    * matrix in place</em>.
    *
    * @return the opposite matrix
    */
   public KtMat3 not ( ) {

      return new KtMat3(this.m00 == 0.0f, this.m01 == 0.0f, this.m02 == 0.0f,
         this.m10 == 0.0f, this.m11 == 0.0f, this.m12 == 0.0f, this.m20 == 0.0f,
         this.m21 == 0.0f, this.m22 == 0.0f);
   }

   /**
    * Returns a new matrix with the addition of the right operand to the
    * instance. For interoperability with Kotlin: <code>a + b</code> .
    * <em>Does not mutate the matrix in place</em>.
    *
    * @param b the right operand
    *
    * @return the sum
    */
   public KtMat3 plus ( final Mat3 b ) {

      return new KtMat3(this.m00 + b.m00, this.m01 + b.m01, this.m02 + b.m02,
         this.m10 + b.m10, this.m11 + b.m11, this.m12 + b.m12, this.m20 + b.m20,
         this.m21 + b.m21, this.m22 + b.m22);
   }

   /**
    * Adds the right operand to the instance (mutates the matrix in place).
    * For interoperability with Kotlin: <code>a += b</code> .
    *
    * @param b the right operand
    */
   public void plusAssign ( final Mat3 b ) {

      this.m00 += b.m00;
      this.m01 += b.m01;
      this.m02 += b.m02;

      this.m10 += b.m10;
      this.m11 += b.m11;
      this.m12 += b.m12;

      this.m20 += b.m20;
      this.m21 += b.m21;
      this.m22 += b.m22;
   }

   /**
    * Sets a column vector for this matrix. For interoperability with Kotlin:
    * <code>a[i] = b</code>.
    *
    * @param j     the column index
    * @param value the column vector
    *
    * @see Mat3#setCol(int, Vec2)
    */
   public void set ( final int j, final Vec2 value ) {

      this.setCol(j, value);
   }

   /**
    * Sets a column vector for this matrix. For interoperability with Kotlin:
    * <code>a[i] = b</code>.
    *
    * @param j     the column index
    * @param value the column vector
    *
    * @see Mat3#setCol(int, Vec3)
    */
   public void set ( final int j, final Vec3 value ) {

      this.setCol(j, value);
   }

   /**
    * Returns a new matrix with the product of the instance and the right
    * operand. For interoperability with Kotlin: <code>a * b</code> . <em>Does
    * not mutate the matrix in place</em>.
    *
    * @param b the right operand
    *
    * @return the product
    */
   public KtMat3 times ( final float b ) {

      return new KtMat3(this.m00 * b, this.m01 * b, this.m02 * b, this.m10 * b,
         this.m11 * b, this.m12 * b, this.m20 * b, this.m21 * b, this.m22 * b);
   }

   /**
    * Returns a new matrix with the product of the instance and the right
    * operand. For interoperability with Kotlin: <code>a * b</code> . <em>Does
    * not mutate the matrix in place</em>.
    *
    * @param b the right operand
    *
    * @return the product
    */
   public KtMat3 times ( final Mat3 b ) {

      return new KtMat3(this.m00 * b.m00 + this.m01 * b.m10 + this.m02 * b.m20,
         this.m00 * b.m01 + this.m01 * b.m11 + this.m02 * b.m21, this.m00
            * b.m02 + this.m01 * b.m12 + this.m02 * b.m22, this.m10 * b.m00
               + this.m11 * b.m10 + this.m12 * b.m20, this.m10 * b.m01
                  + this.m11 * b.m11 + this.m12 * b.m21, this.m10 * b.m02
                     + this.m11 * b.m12 + this.m12 * b.m22, this.m20 * b.m00
                        + this.m21 * b.m10 + this.m22 * b.m20, this.m20 * b.m01
                           + this.m21 * b.m11 + this.m22 * b.m21, this.m20
                              * b.m02 + this.m21 * b.m12 + this.m22 * b.m22);
   }

   /**
    * Multiplies this matrix and a vector. For interoperability with Kotlin:
    * <code>a * b</code> .
    *
    * @param b the vector
    *
    * @return the product
    */
   public KtVec3 times ( final Vec3 b ) {

      return new KtVec3(this.m00 * b.x + this.m01 * b.y + this.m02 * b.z,
         this.m10 * b.x + this.m11 * b.y + this.m12 * b.z, this.m20 * b.x
            + this.m21 * b.y + this.m22 * b.z);
   }

   /**
    * Multiplies the right operand with the instance (mutates the matrix in
    * place). For interoperability with Kotlin: <code>a *= b</code> .
    *
    * @param b the right operand
    */
   public void timesAssign ( final float b ) {

      this.m00 *= b;
      this.m01 *= b;
      this.m02 *= b;

      this.m10 *= b;
      this.m11 *= b;
      this.m12 *= b;

      this.m20 *= b;
      this.m21 *= b;
      this.m22 *= b;
   }

   /**
    * Multiplies the right operand with the instance (mutates the matrix in
    * place). For interoperability with Kotlin: <code>a *= b</code> .
    *
    * @param b the right operand
    */
   public void timesAssign ( final Mat3 b ) {

      this.set(this.m00 * b.m00 + this.m01 * b.m10 + this.m02 * b.m20, this.m00
         * b.m01 + this.m01 * b.m11 + this.m02 * b.m21, this.m00 * b.m02
            + this.m01 * b.m12 + this.m02 * b.m22, this.m10 * b.m00 + this.m11
               * b.m10 + this.m12 * b.m20, this.m10 * b.m01 + this.m11 * b.m11
                  + this.m12 * b.m21, this.m10 * b.m02 + this.m11 * b.m12
                     + this.m12 * b.m22, this.m20 * b.m00 + this.m21 * b.m10
                        + this.m22 * b.m20, this.m20 * b.m01 + this.m21 * b.m11
                           + this.m22 * b.m21, this.m20 * b.m02 + this.m21
                              * b.m12 + this.m22 * b.m22);
   }

   /**
    * Returns a new matrix with the negation of the instance. For
    * interoperability with Kotlin: <code>-a</code> . <em>Does not mutate the
    * matrix in place</em>.
    *
    * @return the negation
    */
   public KtMat3 unaryMinus ( ) {

      return new KtMat3(-this.m00, -this.m01, -this.m02, -this.m10, -this.m11,
         -this.m12, -this.m20, -this.m21, -this.m22);
   }

   /**
    * Returns a new matrix with the positive copy of the instance. For
    * interoperability with Kotlin: <code>+a</code> . <em>Does not mutate the
    * matrix in place</em>.
    *
    * @return the positive
    */
   public KtMat3 unaryPlus ( ) {

      return new KtMat3(this.m00, this.m01, this.m02, this.m10, this.m11,
         this.m12, this.m20, this.m21, this.m22);
   }

   /**
    * An iterator, which allows a matrix's components to be accessed in an
    * enhanced for loop.
    */
   public static final class M3Iterator implements Iterator < KtVec3 > {

      /**
       * The current index.
       */
      private int index = 0;

      /**
       * The vector being iterated over.
       */
      private final KtMat3 mat;

      /**
       * The default constructor.
       *
       * @param m the matrix to iterate
       */
      public M3Iterator ( final KtMat3 m ) { this.mat = m; }

      /**
       * Tests to see if the iterator has another value.
       *
       * @return the evaluation
       */
      @Override
      public boolean hasNext ( ) { return this.index < M3Iterator.LENGTH; }

      /**
       * Gets the next value in the iterator.
       *
       * @return the value
       *
       * @see KtMat3#get(int)
       */
      @Override
      public KtVec3 next ( ) {

         if ( !this.hasNext() ) { throw new NoSuchElementException(); }
         return this.mat.get(this.index++);
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
