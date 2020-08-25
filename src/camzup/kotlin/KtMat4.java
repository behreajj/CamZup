package camzup.kotlin;

import camzup.core.Mat4;
import camzup.core.Vec4;

/**
 * Provides Kotlin operator overloading support for four dimensional
 * matrices.
 */
public class KtMat4 extends Mat4 {

   /**
    * The default constructor. Creates an identity matrix.
    */
   public KtMat4 ( ) { super(); }

   /**
    * Constructs a matrix from boolean values.
    *
    * @param m00 row 0, column 0
    * @param m01 row 0, column 1
    * @param m02 row 0, column 2
    * @param m03 row 0, column 3
    * @param m10 row 1, column 0
    * @param m11 row 1, column 1
    * @param m12 row 1, column 2
    * @param m13 row 1, column 3
    * @param m20 row 2, column 0
    * @param m21 row 2, column 1
    * @param m22 row 2, column 2
    * @param m23 row 2, column 3
    * @param m30 row 3, column 0
    * @param m31 row 3, column 1
    * @param m32 row 3, column 2
    * @param m33 row 3, column 3
    */
   public KtMat4 ( final boolean m00, final boolean m01, final boolean m02,
      final boolean m03, final boolean m10, final boolean m11,
      final boolean m12, final boolean m13, final boolean m20,
      final boolean m21, final boolean m22, final boolean m23,
      final boolean m30, final boolean m31, final boolean m32,
      final boolean m33 ) {

      super(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30,
         m31, m32, m33);
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
   public KtMat4 ( final float m00, final float m01, final float m02,
      final float m10, final float m11, final float m12, final float m20,
      final float m21, final float m22 ) {

      super(m00, m01, m02, m10, m11, m12, m20, m21, m22);
   }

   /**
    * Constructs a matrix from float values.
    *
    * @param m00 row 0, column 0
    * @param m01 row 0, column 1
    * @param m02 row 0, column 2
    * @param m03 row 0, column 3
    * @param m10 row 1, column 0
    * @param m11 row 1, column 1
    * @param m12 row 1, column 2
    * @param m13 row 1, column 3
    * @param m20 row 2, column 0
    * @param m21 row 2, column 1
    * @param m22 row 2, column 2
    * @param m23 row 2, column 3
    */
   public KtMat4 ( final float m00, final float m01, final float m02,
      final float m03, final float m10, final float m11, final float m12,
      final float m13, final float m20, final float m21, final float m22,
      final float m23 ) {

      super(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23);
   }

   /**
    * Constructs a matrix from float values.
    *
    * @param m00 row 0, column 0
    * @param m01 row 0, column 1
    * @param m02 row 0, column 2
    * @param m03 row 0, column 3
    * @param m10 row 1, column 0
    * @param m11 row 1, column 1
    * @param m12 row 1, column 2
    * @param m13 row 1, column 3
    * @param m20 row 2, column 0
    * @param m21 row 2, column 1
    * @param m22 row 2, column 2
    * @param m23 row 2, column 3
    * @param m30 row 3, column 0
    * @param m31 row 3, column 1
    * @param m32 row 3, column 2
    * @param m33 row 3, column 3
    */
   public KtMat4 ( final float m00, final float m01, final float m02,
      final float m03, final float m10, final float m11, final float m12,
      final float m13, final float m20, final float m21, final float m22,
      final float m23, final float m30, final float m31, final float m32,
      final float m33 ) {

      super(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30,
         m31, m32, m33);
   }

   /**
    * Constructs a matrix from a source matrix's components.
    *
    * @param source the source matrix
    */
   public KtMat4 ( final Mat4 source ) { super(source); }

   /**
    * Gets a column vector from this matrix. For interoperability with Kotlin:
    * <code>b = a[i]</code> .
    *
    * @param j the column index
    *
    * @return the column vector
    *
    * @see Mat4#getCol(int, Vec4)
    */
   public KtVec4 get ( final int j ) {

      final KtVec4 result = new KtVec4();
      this.getCol(j, result);
      return result;
   }

   /**
    * Returns a new matrix with the subtraction of the right operand from the
    * instance. For interoperability with Kotlin: <code>a - b</code> .
    * <em>Does not mutate the matrix in place</em>.
    *
    * @param b the right operand
    *
    * @return the subtraction
    */
   public KtMat4 minus ( final Mat4 b ) {

      return new KtMat4(this.m00 - b.m00, this.m01 - b.m01, this.m02 - b.m02,
         this.m03 - b.m03, this.m10 - b.m10, this.m11 - b.m11, this.m12 - b.m12,
         this.m13 - b.m13, this.m20 - b.m20, this.m21 - b.m21, this.m22 - b.m22,
         this.m23 - b.m23, this.m30 - b.m30, this.m31 - b.m31, this.m32 - b.m32,
         this.m33 - b.m33);
   }

   /**
    * Subtracts the right operand from the instance (mutates the matrix in
    * place). For interoperability with Kotlin: <code>a -= b</code> .
    *
    * @param b the right operand
    */
   public void minusAssign ( final Mat4 b ) {

      this.m00 -= b.m00;
      this.m01 -= b.m01;
      this.m02 -= b.m02;
      this.m03 -= b.m03;

      this.m10 -= b.m10;
      this.m11 -= b.m11;
      this.m12 -= b.m12;
      this.m13 -= b.m13;

      this.m20 -= b.m20;
      this.m21 -= b.m21;
      this.m22 -= b.m22;
      this.m23 -= b.m23;

      this.m30 -= b.m30;
      this.m31 -= b.m31;
      this.m32 -= b.m32;
      this.m33 -= b.m33;
   }

   /**
    * Returns a new matrix with the boolean opposite of the instance. For
    * interoperability with Kotlin: <code>!a</code> . <em>Does not mutate the
    * matrix in place</em>.
    *
    * @return the opposite matrix
    */
   public KtMat4 not ( ) {

      return new KtMat4(this.m00 == 0.0f, this.m01 == 0.0f, this.m02 == 0.0f,
         this.m03 == 0.0f, this.m10 == 0.0f, this.m11 == 0.0f, this.m12 == 0.0f,
         this.m13 == 0.0f, this.m20 == 0.0f, this.m21 == 0.0f, this.m22 == 0.0f,
         this.m23 == 0.0f, this.m30 == 0.0f, this.m31 == 0.0f, this.m32 == 0.0f,
         this.m33 == 0.0f);
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
   public KtMat4 plus ( final Mat4 b ) {

      return new KtMat4(this.m00 + b.m00, this.m01 + b.m01, this.m02 + b.m02,
         this.m03 + b.m03, this.m10 + b.m10, this.m11 + b.m11, this.m12 + b.m12,
         this.m13 + b.m13, this.m20 + b.m20, this.m21 + b.m21, this.m22 + b.m22,
         this.m23 + b.m23, this.m30 + b.m30, this.m31 + b.m31, this.m32 + b.m32,
         this.m33 + b.m33);
   }

   /**
    * Adds the right operand to the instance (mutates the matrix in place).
    * For interoperability with Kotlin: <code>a += b</code> .
    *
    * @param b the right operand
    */
   public void plusAssign ( final Mat4 b ) {

      this.m00 += b.m00;
      this.m01 += b.m01;
      this.m02 += b.m02;
      this.m03 += b.m03;

      this.m10 += b.m10;
      this.m11 += b.m11;
      this.m12 += b.m12;
      this.m13 += b.m13;

      this.m20 += b.m20;
      this.m21 += b.m21;
      this.m22 += b.m22;
      this.m23 += b.m23;

      this.m30 += b.m30;
      this.m31 += b.m31;
      this.m32 += b.m32;
      this.m33 += b.m33;
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
   public KtMat4 times ( final float b ) {

      return new KtMat4(this.m00 * b, this.m01 * b, this.m02 * b, this.m03 * b,
         this.m10 * b, this.m11 * b, this.m12 * b, this.m13 * b, this.m20 * b,
         this.m21 * b, this.m22 * b, this.m23 * b, this.m30 * b, this.m31 * b,
         this.m32 * b, this.m33 * b);
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
   public KtMat4 times ( final Mat4 b ) {

      return new KtMat4(this.m00 * b.m00 + this.m01 * b.m10 + this.m02 * b.m20
         + this.m03 * b.m30, this.m00 * b.m01 + this.m01 * b.m11 + this.m02
            * b.m21 + this.m03 * b.m31, this.m00 * b.m02 + this.m01 * b.m12
               + this.m02 * b.m22 + this.m03 * b.m32, this.m00 * b.m03
                  + this.m01 * b.m13 + this.m02 * b.m23 + this.m03 * b.m33,

         this.m10 * b.m00 + this.m11 * b.m10 + this.m12 * b.m20 + this.m13
            * b.m30, this.m10 * b.m01 + this.m11 * b.m11 + this.m12 * b.m21
               + this.m13 * b.m31, this.m10 * b.m02 + this.m11 * b.m12
                  + this.m12 * b.m22 + this.m13 * b.m32, this.m10 * b.m03
                     + this.m11 * b.m13 + this.m12 * b.m23 + this.m13 * b.m33,

         this.m20 * b.m00 + this.m21 * b.m10 + this.m22 * b.m20 + this.m23
            * b.m30, this.m20 * b.m01 + this.m21 * b.m11 + this.m22 * b.m21
               + this.m23 * b.m31, this.m20 * b.m02 + this.m21 * b.m12
                  + this.m22 * b.m22 + this.m23 * b.m32, this.m20 * b.m03
                     + this.m21 * b.m13 + this.m22 * b.m23 + this.m23 * b.m33,

         this.m30 * b.m00 + this.m31 * b.m10 + this.m32 * b.m20 + this.m33
            * b.m30, this.m30 * b.m01 + this.m31 * b.m11 + this.m32 * b.m21
               + this.m33 * b.m31, this.m30 * b.m02 + this.m31 * b.m12
                  + this.m32 * b.m22 + this.m33 * b.m32, this.m30 * b.m03
                     + this.m31 * b.m13 + this.m32 * b.m23 + this.m33 * b.m33);
   }

   /**
    * Multiplies this matrix and a vector. For interoperability with Kotlin:
    * <code>a * b</code> .
    *
    * @param b the vector
    *
    * @return the product
    */
   public KtVec4 times ( final Vec4 b ) {

      return new KtVec4(this.m00 * b.x + this.m01 * b.y + this.m02 * b.z
         + this.m03 * b.w, this.m10 * b.x + this.m11 * b.y + this.m12 * b.z
            + this.m13 * b.w, this.m20 * b.x + this.m21 * b.y + this.m22 * b.z
               + this.m23 * b.w, this.m30 * b.x + this.m31 * b.y + this.m32
                  * b.z + this.m33 * b.w);
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
      this.m03 *= b;

      this.m10 *= b;
      this.m11 *= b;
      this.m12 *= b;
      this.m13 *= b;

      this.m20 *= b;
      this.m21 *= b;
      this.m22 *= b;
      this.m23 *= b;

      this.m30 *= b;
      this.m31 *= b;
      this.m32 *= b;
      this.m33 *= b;
   }

   /**
    * Multiplies the right operand with the instance (mutates the matrix in
    * place). For interoperability with Kotlin: <code>a *= b</code> .
    *
    * @param b the right operand
    */
   public void timesAssign ( final Mat4 b ) {

      this.set(this.m00 * b.m00 + this.m01 * b.m10 + this.m02 * b.m20 + this.m03
         * b.m30, this.m00 * b.m01 + this.m01 * b.m11 + this.m02 * b.m21
            + this.m03 * b.m31, this.m00 * b.m02 + this.m01 * b.m12 + this.m02
               * b.m22 + this.m03 * b.m32, this.m00 * b.m03 + this.m01 * b.m13
                  + this.m02 * b.m23 + this.m03 * b.m33,

         this.m10 * b.m00 + this.m11 * b.m10 + this.m12 * b.m20 + this.m13
            * b.m30, this.m10 * b.m01 + this.m11 * b.m11 + this.m12 * b.m21
               + this.m13 * b.m31, this.m10 * b.m02 + this.m11 * b.m12
                  + this.m12 * b.m22 + this.m13 * b.m32, this.m10 * b.m03
                     + this.m11 * b.m13 + this.m12 * b.m23 + this.m13 * b.m33,

         this.m20 * b.m00 + this.m21 * b.m10 + this.m22 * b.m20 + this.m23
            * b.m30, this.m20 * b.m01 + this.m21 * b.m11 + this.m22 * b.m21
               + this.m23 * b.m31, this.m20 * b.m02 + this.m21 * b.m12
                  + this.m22 * b.m22 + this.m23 * b.m32, this.m20 * b.m03
                     + this.m21 * b.m13 + this.m22 * b.m23 + this.m23 * b.m33,

         this.m30 * b.m00 + this.m31 * b.m10 + this.m32 * b.m20 + this.m33
            * b.m30, this.m30 * b.m01 + this.m31 * b.m11 + this.m32 * b.m21
               + this.m33 * b.m31, this.m30 * b.m02 + this.m31 * b.m12
                  + this.m32 * b.m22 + this.m33 * b.m32, this.m30 * b.m03
                     + this.m31 * b.m13 + this.m32 * b.m23 + this.m33 * b.m33);
   }

   /**
    * Returns a new matrix with the negation of the instance. For
    * interoperability with Kotlin: <code>-a</code> . <em>Does not mutate the
    * matrix in place</em>.
    *
    * @return the negation
    */
   public KtMat4 unaryMinus ( ) {

      return new KtMat4(-this.m00, -this.m01, -this.m02, -this.m03, -this.m10,
         -this.m11, -this.m12, -this.m13, -this.m20, -this.m21, -this.m22,
         -this.m23, -this.m30, -this.m31, -this.m32, -this.m33);
   }

   /**
    * Returns a new matrix with the positive copy of the instance. For
    * interoperability with Kotlin: <code>+a</code> . <em>Does not mutate the
    * matrix in place</em>.
    *
    * @return the positive
    */
   public KtMat4 unaryPlus ( ) {

      return new KtMat4(this.m00, this.m01, this.m02, this.m03, this.m10,
         this.m11, this.m12, this.m13, this.m20, this.m21, this.m22, this.m23,
         this.m30, this.m31, this.m32, this.m33);
   }

}
