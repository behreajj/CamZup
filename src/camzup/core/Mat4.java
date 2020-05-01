package camzup.core;

import java.util.Iterator;

/**
 * A mutable, extensible class influenced by GLSL, OSL and Processing's
 * PMatrix3D. ALthough this is a 4 x 4 matrix, it is generally assumed to
 * be a 3D affine transform matrix, where the last row is (0.0, 0.0, 0.0,
 * 1.0) . Instance methods are limited, while most static methods require
 * an explicit output variable to be provided.
 */
public class Mat4 extends Matrix {

   /**
    * Component in row 0, column 0. The right axis x component.
    */
   public float m00 = 1.0f;

   /**
    * Component in row 0, column 1. The forward axis x component.
    */
   public float m01 = 0.0f;

   /**
    * Component in row 0, column 2. The up axis x component.
    */
   public float m02 = 0.0f;

   /**
    * Component in row 0, column 3. The translation x component.
    */
   public float m03 = 0.0f;

   /**
    * Component in row 1, column 0. The right axis y component.
    */
   public float m10 = 0.0f;

   /**
    * Component in row 1, column 1. The forward axis y component.
    */
   public float m11 = 1.0f;

   /**
    * Component in row 1, column 2. The up axis y component.
    */
   public float m12 = 0.0f;

   /**
    * Component in row 1, column 3. The translation y component.
    */
   public float m13 = 0.0f;

   /**
    * Component in row 2, column 0. The right axis z component.
    */
   public float m20 = 0.0f;

   /**
    * Component in row 2, column 1. The forward axis z component.
    */
   public float m21 = 0.0f;

   /**
    * Component in row 2, column 2. The up axis z component.
    */
   public float m22 = 1.0f;

   /**
    * Component in row 2, column 3. The translation z component.
    */
   public float m23 = 0.0f;

   /**
    * Component in row 3, column 0. The right axis w component.
    */
   public float m30 = 0.0f;

   /**
    * Component in row 3, column 1. The forward axis w component.
    */
   public float m31 = 0.0f;

   /**
    * Component in row 3, column 2. The up axis w component.
    */
   public float m32 = 0.0f;

   /**
    * Component in row 3, column 3. The translation w component.
    */
   public float m33 = 1.0f;

   /**
    * The default constructor. Creates an identity matrix.
    */
   public Mat4 ( ) { super(16); }

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
   public Mat4 ( final float m00, final float m01, final float m02,
      final float m10, final float m11, final float m12, final float m20,
      final float m21, final float m22 ) {

      super(16);
      this.set(m00, m01, m02, m10, m11, m12, m20, m21, m22);
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
   public Mat4 ( final float m00, final float m01, final float m02,
      final float m03, final float m10, final float m11, final float m12,
      final float m13, final float m20, final float m21, final float m22,
      final float m23 ) {

      super(16);
      this.set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23);
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
   public Mat4 ( final float m00, final float m01, final float m02,
      final float m03, final float m10, final float m11, final float m12,
      final float m13, final float m20, final float m21, final float m22,
      final float m23, final float m30, final float m31, final float m32,
      final float m33 ) {

      super(16);
      this.set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30,
         m31, m32, m33);
   }

   /**
    * Constructs a matrix from a source matrix's components.
    *
    * @param source the source matrix
    */
   public Mat4 ( final Mat4 source ) {

      super(16);
      this.set(source);
   }

   /**
    * Returns a new matrix with this matrix's components. Java's cloneable
    * interface is problematic; use set or a copy constructor instead.
    *
    * @return a new matrix
    */
   @Override
   public Mat4 clone ( ) {

      /* @formatter:off */
      return new Mat4(
         this.m00, this.m01, this.m02, this.m03,
         this.m10, this.m11, this.m12, this.m13,
         this.m20, this.m21, this.m22, this.m23,
         this.m30, this.m31, this.m32, this.m33);
      /* @formatter:on */
   }

   /**
    * Tests to see if the matrix contains a value.
    *
    * @param v the value
    *
    * @return the evaluation
    */
   public boolean contains ( final float v ) {

      if ( Utils.approx(this.m00, v) ) { return true; }
      if ( Utils.approx(this.m01, v) ) { return true; }
      if ( Utils.approx(this.m02, v) ) { return true; }
      if ( Utils.approx(this.m03, v) ) { return true; }
      if ( Utils.approx(this.m10, v) ) { return true; }
      if ( Utils.approx(this.m11, v) ) { return true; }
      if ( Utils.approx(this.m12, v) ) { return true; }
      if ( Utils.approx(this.m13, v) ) { return true; }
      if ( Utils.approx(this.m20, v) ) { return true; }
      if ( Utils.approx(this.m21, v) ) { return true; }
      if ( Utils.approx(this.m22, v) ) { return true; }
      if ( Utils.approx(this.m23, v) ) { return true; }
      if ( Utils.approx(this.m30, v) ) { return true; }
      if ( Utils.approx(this.m31, v) ) { return true; }
      if ( Utils.approx(this.m32, v) ) { return true; }
      if ( Utils.approx(this.m33, v) ) { return true; }
      return false;
   }

   /**
    * Tests this matrix for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the equivalence
    *
    * @see Mat4#equals(Mat4)
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null ) { return false; }
      if ( this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Mat4 ) obj);
   }

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
   public Vec4 get ( final int j ) {

      return this.getCol(j, new Vec4());
   }

   /**
    * Gets a column of this matrix with an index and vector.
    *
    * @param j      the column index
    * @param target the vector
    *
    * @return the column
    */
   public Vec4 getCol ( final int j, final Vec4 target ) {

      switch ( j ) {
         case 0:
         case -4:
            return target.set(this.m00, this.m10, this.m20, this.m30);

         case 1:
         case -3:
            return target.set(this.m01, this.m11, this.m21, this.m31);

         case 2:
         case -2:
            return target.set(this.m02, this.m12, this.m22, this.m32);

         case 3:
         case -1:
            return target.set(this.m03, this.m13, this.m23, this.m33);

         default:
            return target.reset();
      }
   }

   /**
    * Simulates bracket subscript access in a one-dimensional, row-major
    * matrix array. Works with positive integers in [0, 15] or negative
    * integers in [-16, -1] .
    *
    * @param index the index
    *
    * @return the component at that index
    */
   @Override
   public float getElm ( final int index ) {

      /*
       * At the moment, there is a get function to facilitate an iterator, but
       * no set function, because setCol is the encouraged way to set matrix
       * elms.
       */

      /* @formatter:off */
      switch ( index ) {

         case  0: case -16: return this.m00;
         case  1: case -15: return this.m01;
         case  2: case -14: return this.m02;
         case  3: case -13: return this.m03;

         case  4: case -12: return this.m10;
         case  5: case -11: return this.m11;
         case  6: case -10: return this.m12;
         case  7: case  -9: return this.m13;

         case  8: case  -8: return this.m20;
         case  9: case  -7: return this.m21;
         case 10: case  -6: return this.m22;
         case 11: case  -5: return this.m23;

         case 12: case  -4: return this.m30;
         case 13: case  -3: return this.m31;
         case 14: case  -2: return this.m32;
         case 15: case  -1: return this.m33;

         default: return 0.0f;
      }
      /* @formatter:on */
   }

   /**
    * Simulates bracket subscript access in a two-dimensional, row-major
    * matrix array. Works with positive integers in [0, 3][0, 3] or negative
    * integers in [-4, -1][-4, -1] .
    *
    * @param i the row index
    * @param j the column index
    *
    * @return the component at that index
    */
   @Override
   public float getElm ( final int i, final int j ) {

      /* @formatter:off */
      switch ( i ) {

         case 0: case -4:

            switch ( j ) {
               case 0: case -4: return this.m00;
               case 1: case -3: return this.m01;
               case 2: case -2: return this.m02;
               case 3: case -1: return this.m03;
               default: return 0.0f;
            }

         case 1: case -3:

            switch ( j ) {
               case 0: case -4: return this.m10;
               case 1: case -3: return this.m11;
               case 2: case -2: return this.m12;
               case 3: case -1: return this.m13;
               default: return 0.0f;
            }

         case 2: case -2:

            switch ( j ) {
               case 0: case -4: return this.m20;
               case 1: case -3: return this.m21;
               case 2: case -2: return this.m22;
               case 3: case -1: return this.m23;
               default: return 0.0f;
            }

         case 3: case -1:

            switch ( j ) {
               case 0: case -4: return this.m30;
               case 1: case -3: return this.m31;
               case 2: case -2: return this.m32;
               case 3: case -1: return this.m33;
               default: return 0.0f;
            }

         default: return 0.0f;

      }
      /* @formatter:on */
   }

   /**
    * Returns a hash code for this matrix based on its 16 components.
    *
    * @return the hash code
    *
    * @see Float#floatToIntBits(float)
    */
   @Override
   public int hashCode ( ) {

      int hash = IUtils.MUL_BASE ^ Float.floatToIntBits(this.m00);
      hash = hash * IUtils.HASH_MUL ^ Float.floatToIntBits(this.m01);
      hash = hash * IUtils.HASH_MUL ^ Float.floatToIntBits(this.m02);
      hash = hash * IUtils.HASH_MUL ^ Float.floatToIntBits(this.m03);

      hash = hash * IUtils.HASH_MUL ^ Float.floatToIntBits(this.m10);
      hash = hash * IUtils.HASH_MUL ^ Float.floatToIntBits(this.m11);
      hash = hash * IUtils.HASH_MUL ^ Float.floatToIntBits(this.m12);
      hash = hash * IUtils.HASH_MUL ^ Float.floatToIntBits(this.m13);

      hash = hash * IUtils.HASH_MUL ^ Float.floatToIntBits(this.m20);
      hash = hash * IUtils.HASH_MUL ^ Float.floatToIntBits(this.m21);
      hash = hash * IUtils.HASH_MUL ^ Float.floatToIntBits(this.m22);
      hash = hash * IUtils.HASH_MUL ^ Float.floatToIntBits(this.m23);

      hash = hash * IUtils.HASH_MUL ^ Float.floatToIntBits(this.m30);
      hash = hash * IUtils.HASH_MUL ^ Float.floatToIntBits(this.m31);
      hash = hash * IUtils.HASH_MUL ^ Float.floatToIntBits(this.m32);
      hash = hash * IUtils.HASH_MUL ^ Float.floatToIntBits(this.m33);

      return hash;
   }

   /**
    * Returns an iterator for this matrix, which allows its components to be
    * accessed in an enhanced for-loop.
    *
    * @return the iterator
    */
   @Override
   public Iterator < Float > iterator ( ) { return new M4Iterator(this); }

   /**
    * Returns a new matrix with the subtraction of the right operand from the
    * instance. For interoperability with Kotlin: <code>a - b</code> .
    * <em>Does not mutate the matrix in place</em>.
    *
    * @param b the right operand
    *
    * @return the subtraction
    */
   public Mat4 minus ( final Mat4 b ) {

      return Mat4.sub(this, b, new Mat4());
   }

   /**
    * Subtracts the right operand from the instance (mutates the matrix in
    * place). For interoperability with Kotlin: <code>a -= b</code> .
    *
    * @param b the right operand
    */
   public void minusAssign ( final Mat4 b ) { Mat4.sub(this, b, this); }

   /**
    * Returns a new matrix with the addition of the right operand to the
    * instance. For interoperability with Kotlin: <code>a + b</code> .
    * <em>Does not mutate the matrix in place</em>.
    *
    * @param b the right operand
    *
    * @return the sum
    */
   public Mat4 plus ( final Mat4 b ) {

      return Mat4.add(this, b, new Mat4());
   }

   /**
    * Adds the right operand to the instance (mutates the matrix in place).
    * For interoperability with Kotlin: <code>a += b</code> .
    *
    * @param b the right operand
    */
   public void plusAssign ( final Mat4 b ) { Mat4.add(this, b, this); }

   /**
    * Resets this matrix to an initial state:
    *
    * <pre>
    * 1.0, 0.0, 0.0, 0.0,
    * 0.0, 1.0, 0.0, 0.0,
    * 0.0, 0.0, 1.0, 0.0,
    * 0.0, 0.0, 0.0, 1.0
    * </pre>
    *
    * @return this matrix
    *
    * @see Mat4#identity(Mat4)
    */
   public Mat4 reset ( ) {

      return this.set(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
         0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Sets the three axis columns of this matrix. The last row and column are
    * set to (0.0, 0.0, 0.0, 1.0).
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
    *
    * @return this matrix
    */
   public Mat4 set ( final float m00, final float m01, final float m02,
      final float m10, final float m11, final float m12, final float m20,
      final float m21, final float m22 ) {

      return this.set(m00, m01, m02, 0.0f, m10, m11, m12, 0.0f, m20, m21, m22,
         0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Sets the upper three rows of this matrix. The last row is set to (0.0,
    * 0.0, 0.0, 1.0).
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
    *
    * @return this matrix
    */
   public Mat4 set ( final float m00, final float m01, final float m02,
      final float m03, final float m10, final float m11, final float m12,
      final float m13, final float m20, final float m21, final float m22,
      final float m23 ) {

      return this.set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22,
         m23, 0.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Sets the components of this matrix.
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
    *
    * @return this matrix
    */
   public Mat4 set ( final float m00, final float m01, final float m02,
      final float m03, final float m10, final float m11, final float m12,
      final float m13, final float m20, final float m21, final float m22,
      final float m23, final float m30, final float m31, final float m32,
      final float m33 ) {

      /* @formatter:off */
      this.m00 = m00; this.m10 = m10; this.m20 = m20; this.m30 = m30;
      this.m01 = m01; this.m11 = m11; this.m21 = m21; this.m31 = m31;
      this.m02 = m02; this.m12 = m12; this.m22 = m22; this.m32 = m32;
      this.m03 = m03; this.m13 = m13; this.m23 = m23; this.m33 = m33;
      /* @formatter:on */

      return this;
   }

   /**
    * Sets a column of this matrix with an index and vector. For
    * interoperability with Kotlin: <code>a[i] = b</code> .
    *
    * @param j      the column index
    * @param source the source vector
    *
    * @see Mat4#setCol(int, Vec3)
    */
   public void set ( final int j, final Vec3 source ) {

      this.setCol(j, source);
   }

   /**
    * Sets a column of this matrix with an index and vector. For
    * interoperability with Kotlin: <code>a[i] = b</code> .
    *
    * @param j      the column index
    * @param source the source vector
    *
    * @see Mat4#setCol(int, Vec4)
    */
   public void set ( final int j, final Vec4 source ) {

      this.setCol(j, source);
   }

   /**
    * Copies the components of the input matrix to this matrix.
    *
    * @param source the input matrix
    *
    * @return this matrix
    */

   public Mat4 set ( final Mat4 source ) {

      return this.set(source.m00, source.m01, source.m02, source.m03,
         source.m10, source.m11, source.m12, source.m13, source.m20, source.m21,
         source.m22, source.m23, source.m30, source.m31, source.m32,
         source.m33);
   }

   /**
    * Sets a column of this matrix with an index and vector. If the column is
    * an axis vector, the w component is set to 0.0; if it is a translation,
    * the w component is set to 1.0.
    *
    * @param j      the column index
    * @param source the column
    *
    * @return this matrix
    */

   public Mat4 setCol ( final int j, final Vec3 source ) {

      switch ( j ) {
         case 0:
         case -4:

            /* Right axis. */

            this.m00 = source.x;
            this.m10 = source.y;
            this.m20 = source.z;
            this.m30 = 0.0f;

            return this;

         case 1:
         case -3:

            /* Forward axis. */

            this.m01 = source.x;
            this.m11 = source.y;
            this.m21 = source.z;
            this.m31 = 0.0f;

            return this;

         case 2:
         case -2:

            /* Up axis. */

            this.m02 = source.x;
            this.m12 = source.y;
            this.m22 = source.z;
            this.m32 = 0.0f;

            return this;

         case 3:
         case -1:

            /* Translation. */

            this.m03 = source.x;
            this.m13 = source.y;
            this.m23 = source.z;
            this.m33 = 1.0f;

            return this;

         default:

            return this;
      }
   }

   /**
    * Sets a column of this matrix with an index and vector.
    *
    * @param j      the column index
    * @param source the column
    *
    * @return this matrix
    */

   public Mat4 setCol ( final int j, final Vec4 source ) {

      switch ( j ) {
         case 0:
         case -4:

            /* Right axis. */

            this.m00 = source.x;
            this.m10 = source.y;
            this.m20 = source.z;
            this.m30 = source.w;

            return this;

         case 1:
         case -3:

            /* Forward axis. */

            this.m01 = source.x;
            this.m11 = source.y;
            this.m21 = source.z;
            this.m31 = source.w;

            return this;

         case 2:
         case -2:

            /* Up axis. */

            this.m02 = source.x;
            this.m12 = source.y;
            this.m22 = source.z;
            this.m32 = source.w;

            return this;

         case 3:
         case -1:

            /* Translation. */

            this.m03 = source.x;
            this.m13 = source.y;
            this.m23 = source.z;
            this.m33 = source.w;

            return this;

         default:

            return this;
      }
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
   public Mat4 times ( final float b ) {

      return Mat4.mul(this, b, new Mat4());
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
   public Mat4 times ( final Mat4 b ) {

      return Mat4.mul(this, b, new Mat4());
   }

   /**
    * Multiplies this matrix and a vector. For interoperability with Kotlin:
    * <code>a * b</code> .
    *
    * @param b the vector
    *
    * @return the product
    */
   public Vec4 times ( final Vec4 b ) {

      return Mat4.mul(this, b, new Vec4());
   }

   /**
    * Multiplies the right operand with the instance (mutates the matrix in
    * place). For interoperability with Kotlin: <code>a *= b</code> .
    *
    * @param b the right operand
    */
   public void timesAssign ( final float b ) { Mat4.mul(this, b, this); }

   /**
    * Multiplies the right operand with the instance (mutates the matrix in
    * place). For interoperability with Kotlin: <code>a *= b</code> .
    *
    * @param b the right operand
    */
   public void timesAssign ( final Mat4 b ) { Mat4.mul(this, b, this); }

   /**
    * Returns a float array of length 16 containing this matrix's components.
    *
    * @return the array
    */
   @Override
   public float[] toArray ( ) {

      /* @formatter:off */
      return new float[] {
         this.m00, this.m01, this.m02, this.m03,
         this.m10, this.m11, this.m12, this.m13,
         this.m20, this.m21, this.m22, this.m23,
         this.m30, this.m31, this.m32, this.m33 };
      /* @formatter:on */
   }

   /**
    * Returns a string representation of this matrix.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(4); }

   /**
    * Returns a string representation of this matrix.
    *
    * @param places number of decimal places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      /* @formatter:off */
      return new StringBuilder(512)
         .append("{ m00: ").append(Utils.toFixed(this.m00, places))
         .append(", m01: ").append(Utils.toFixed(this.m01, places))
         .append(", m02: ").append(Utils.toFixed(this.m02, places))
         .append(", m03: ").append(Utils.toFixed(this.m03, places))

         .append(", m10: ").append(Utils.toFixed(this.m10, places))
         .append(", m11: ").append(Utils.toFixed(this.m11, places))
         .append(", m12: ").append(Utils.toFixed(this.m12, places))
         .append(", m13: ").append(Utils.toFixed(this.m13, places))

         .append(", m20: ").append(Utils.toFixed(this.m20, places))
         .append(", m21: ").append(Utils.toFixed(this.m21, places))
         .append(", m22: ").append(Utils.toFixed(this.m22, places))
         .append(", m23: ").append(Utils.toFixed(this.m23, places))

         .append(", m30: ").append(Utils.toFixed(this.m30, places))
         .append(", m31: ").append(Utils.toFixed(this.m31, places))
         .append(", m32: ").append(Utils.toFixed(this.m32, places))
         .append(", m33: ").append(Utils.toFixed(this.m33, places))

         .append(' ').append('}')
         .toString();
      /* @formatter:on */
   }

   /**
    * Returns a string representation of this matrix, where columns are
    * separated by tabs and rows are separated by new lines.
    *
    * @return the string
    */
   public String toStringCol ( ) {

      return this.toStringCol(4, ',', ' ', '\n');
   }

   /**
    * Returns a string representation of this matrix intended for display in
    * the console.
    *
    * @param p number of decimal places
    * @param s the entry separator
    * @param t the spacer
    * @param n the row separator
    *
    * @return the string
    */
   public String toStringCol ( final int p, final char s, final char t,
      final char n ) {

      /* @formatter:off */
      return new StringBuilder(256)
         .append('\n')
                             .append(Utils.toFixed(this.m00, p))
         .append(s).append(t).append(Utils.toFixed(this.m01, p))
         .append(s).append(t).append(Utils.toFixed(this.m02, p))
         .append(s).append(t).append(Utils.toFixed(this.m03, p))
         .append(s)

                   .append(n).append(Utils.toFixed(this.m10, p))
         .append(s).append(t).append(Utils.toFixed(this.m11, p))
         .append(s).append(t).append(Utils.toFixed(this.m12, p))
         .append(s).append(t).append(Utils.toFixed(this.m13, p))
         .append(s)

                   .append(n).append(Utils.toFixed(this.m20, p))
         .append(s).append(t).append(Utils.toFixed(this.m21, p))
         .append(s).append(t).append(Utils.toFixed(this.m22, p))
         .append(s).append(t).append(Utils.toFixed(this.m23, p))
         .append(s)

                   .append(n).append(Utils.toFixed(this.m30, p))
         .append(s).append(t).append(Utils.toFixed(this.m31, p))
         .append(s).append(t).append(Utils.toFixed(this.m32, p))
         .append(s).append(t).append(Utils.toFixed(this.m33, p))

         .append('\n')
         .toString();
      /* @formatter:on */
   }

   /**
    * Tests for equivalence between this and another matrix.
    *
    * @param n the matrix
    *
    * @return the evaluation
    *
    * @see Float#floatToIntBits(float)
    */
   protected boolean equals ( final Mat4 n ) {

      if ( Float.floatToIntBits(this.m00) != Float.floatToIntBits(n.m00) ) {
         return false;
      }
      if ( Float.floatToIntBits(this.m01) != Float.floatToIntBits(n.m01) ) {
         return false;
      }
      if ( Float.floatToIntBits(this.m02) != Float.floatToIntBits(n.m02) ) {
         return false;
      }
      if ( Float.floatToIntBits(this.m03) != Float.floatToIntBits(n.m03) ) {
         return false;
      }

      if ( Float.floatToIntBits(this.m10) != Float.floatToIntBits(n.m10) ) {
         return false;
      }
      if ( Float.floatToIntBits(this.m11) != Float.floatToIntBits(n.m11) ) {
         return false;
      }
      if ( Float.floatToIntBits(this.m12) != Float.floatToIntBits(n.m12) ) {
         return false;
      }
      if ( Float.floatToIntBits(this.m13) != Float.floatToIntBits(n.m13) ) {
         return false;
      }

      if ( Float.floatToIntBits(this.m20) != Float.floatToIntBits(n.m20) ) {
         return false;
      }
      if ( Float.floatToIntBits(this.m21) != Float.floatToIntBits(n.m21) ) {
         return false;
      }
      if ( Float.floatToIntBits(this.m22) != Float.floatToIntBits(n.m22) ) {
         return false;
      }
      if ( Float.floatToIntBits(this.m23) != Float.floatToIntBits(n.m23) ) {
         return false;
      }

      if ( Float.floatToIntBits(this.m30) != Float.floatToIntBits(n.m30) ) {
         return false;
      }
      if ( Float.floatToIntBits(this.m31) != Float.floatToIntBits(n.m31) ) {
         return false;
      }
      if ( Float.floatToIntBits(this.m32) != Float.floatToIntBits(n.m32) ) {
         return false;
      }
      if ( Float.floatToIntBits(this.m33) != Float.floatToIntBits(n.m33) ) {
         return false;
      }

      return true;
   }

   /**
    * Adds two matrices together.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output matrix
    *
    * @return the sum
    */
   public static Mat4 add ( final Mat4 a, final Mat4 b, final Mat4 target ) {

      /* @formatter:off */
      return target.set(
         a.m00 + b.m00, a.m01 + b.m01, a.m02 + b.m02, a.m03 + b.m03,
         a.m10 + b.m10, a.m11 + b.m11, a.m12 + b.m12, a.m13 + b.m13,
         a.m20 + b.m20, a.m21 + b.m21, a.m22 + b.m22, a.m23 + b.m23,
         a.m30 + b.m30, a.m31 + b.m31, a.m32 + b.m32, a.m33 + b.m33);
      /* @formatter:on */
   }

   /**
    * Returns a matrix set to the Bezier curve basis:
    *
    * <pre>
    * -1.0,  3.0, -3.0, 1.0,
    *  3.0, -6.0,  3.0, 0.0,
    * -3.0,  3.0,  0.0, 0.0,
    *  1.0,  0.0,  0.0, 0.0
    * </pre>
    *
    * @param target the output matrix
    *
    * @return the Bezier basis
    */
   public static Mat4 bezierBasis ( final Mat4 target ) {

      /* @formatter:off */
      return target.set(
         -1.0f,  3.0f, -3.0f, 1.0f,
          3.0f, -6.0f,  3.0f, 0.0f,
         -3.0f,  3.0f,  0.0f, 0.0f,
          1.0f,  0.0f,  0.0f, 0.0f);
      /* @formatter:on */
   }

   /**
    * Returns a matrix set to the Bezier curve basis inverse:
    *
    * <pre>
    * 0.0,        0.0,        0.0, 1.0,
    * 0.0,        0.0, 0.33333334, 1.0,
    * 0.0, 0.33333334, 0.66666666, 1.0,
    * 1.0,        1.0,        1.0, 1.0
    * </pre>
    *
    * @param target the output matrix
    *
    * @return the Bezier basis
    */
   public static Mat4 bezierBasisInverse ( final Mat4 target ) {

      /* @formatter:off */
      return target.set(
         0.0f,        0.0f,        0.0f, 1.0f,
         0.0f,        0.0f, 0.33333334f, 1.0f,
         0.0f, 0.33333334f, 0.66666666f, 1.0f,
         1.0f,        1.0f,        1.0f, 1.0f);
      /* @formatter:on */
   }

   /**
    * Decomposes a matrix into its constituent transforms: translation,
    * rotation and scale. Rotation is returned from the function, while
    * translation and scale are loaded into out parameters.
    *
    * @param m     the matrix
    * @param trans the output translation
    * @param rot   the output rotation
    * @param scale the output scale
    *
    * @see Utils#hypot(float, float, float)
    * @see Mat4#determinant(Mat4)
    * @see Utils#div(float, float)
    * @see Quaternion#fromAxes(float, float, float, float, float, float,
    *      float, float, float, Quaternion)
    */
   public static void decompose ( final Mat4 m, final Vec3 trans,
      final Quaternion rot, final Vec3 scale ) {

      final float xMag = Utils.hypot(m.m00, m.m10, m.m20);
      final float yMag = Utils.hypot(m.m01, m.m11, m.m21);
      final float zMag = Utils.hypot(m.m02, m.m12, m.m22);
      final float det = Mat4.determinant(m);
      scale.set(xMag, det < 0.0f ? -yMag : yMag, zMag);

      /*
       * Extract rotation matrix from affine transform matrix by dividing each
       * axis by the scale.
       */
      final float sxInv = Utils.div(1.0f, xMag);
      final float syInv = Utils.div(1.0f, yMag);
      final float szInv = Utils.div(1.0f, zMag);

      final float rightx = m.m00 * sxInv;
      final float righty = m.m10 * sxInv;
      final float rightz = m.m20 * sxInv;

      final float forwardx = m.m01 * syInv;
      final float forwardy = m.m11 * syInv;
      final float forwardz = m.m21 * syInv;

      final float upx = m.m02 * szInv;
      final float upy = m.m12 * szInv;
      final float upz = m.m22 * szInv;

      Quaternion.fromAxes(rightx, forwardy, upz, forwardz, upy, upx, rightz,
         righty, forwardx, rot);

      trans.set(m.m03, m.m13, m.m23);
   }

   /**
    * Finds the determinant of the matrix.
    *
    * @param m the matrix
    *
    * @return the determinant
    */
   public static float determinant ( final Mat4 m ) {

      /* @formatter:off */
      return m.m00 * ( m.m11 * m.m22 * m.m33 +
                       m.m12 * m.m23 * m.m31 +
                       m.m13 * m.m21 * m.m32 -
                       m.m13 * m.m22 * m.m31 -
                       m.m11 * m.m23 * m.m32 -
                       m.m12 * m.m21 * m.m33 )

           - m.m01 * ( m.m10 * m.m22 * m.m33 +
                       m.m12 * m.m23 * m.m30 +
                       m.m13 * m.m20 * m.m32 -
                       m.m13 * m.m22 * m.m30 -
                       m.m10 * m.m23 * m.m32 -
                       m.m12 * m.m20 * m.m33 )

           + m.m02 * ( m.m10 * m.m21 * m.m33 +
                       m.m11 * m.m23 * m.m30 +
                       m.m13 * m.m20 * m.m31 -
                       m.m13 * m.m21 * m.m30 -
                       m.m10 * m.m23 * m.m31 -
                       m.m11 * m.m20 * m.m33 )

           - m.m03 * ( m.m10 * m.m21 * m.m32 +
                       m.m11 * m.m22 * m.m30 +
                       m.m12 * m.m20 * m.m31 -
                       m.m12 * m.m21 * m.m30 -
                       m.m10 * m.m22 * m.m31 -
                       m.m11 * m.m20 * m.m32 );
      /* @formatter:on */
   }

   /**
    * Divides one matrix by another. Equivalent to multiplying the numerator
    * and the inverse of the denominator.
    *
    * @param a       numerator
    * @param b       denominator
    * @param target  the output matrix
    * @param inverse the inverse matrix
    *
    * @return the quotient
    *
    * @see Mat4#mul(Mat4, Mat4, Mat4)
    * @see Mat4#inverse
    */
   public static Mat4 div ( final Mat4 a, final Mat4 b, final Mat4 target,
      final Mat4 inverse ) {

      return Mat4.mul(a, Mat4.inverse(b, inverse), target);
   }

   /**
    * Creates a matrix from two axes. The third axis, up, is assumed to be
    * (0.0, 0.0, 1.0, 0.0). The fourth row and column are assumed to be (0.0,
    * 0.0, 0.0, 1.0).
    *
    * @param right   the right axis
    * @param forward the forward axis
    * @param target  the output matrix
    *
    * @return the matrix
    */
   public static Mat4 fromAxes ( final Vec2 right, final Vec2 forward,
      final Mat4 target ) {

      return target.set(right.x, forward.x, 0.0f, 0.0f, right.y, forward.y,
         0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Creates a matrix from two axes and a translation. The third axis, up, is
    * assumed to be (0.0, 0.0, 1.0, 0.0). The fourth row, w, is assumed to be
    * (0.0, 0.0, 0.0, 1.0).
    *
    * @param right       the right axis
    * @param forward     the forward axis
    * @param translation the translation
    * @param target      the output matrix
    *
    * @return the matrix
    */
   public static Mat4 fromAxes ( final Vec2 right, final Vec2 forward,
      final Vec2 translation, final Mat4 target ) {

      return target.set(right.x, forward.x, 0.0f, translation.x, right.y,
         forward.y, 0.0f, translation.y, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
         0.0f, 1.0f);
   }

   /**
    * Creates a matrix from three axes. The fourth row and column are assumed
    * to be (0.0, 0.0, 0.0, 1.0).
    *
    * @param right   the right axis
    * @param forward the forward axis
    * @param up      the up axis
    * @param target  the output matrix
    *
    * @return the matrix
    */
   public static Mat4 fromAxes ( final Vec3 right, final Vec3 forward,
      final Vec3 up, final Mat4 target ) {

      return target.set(right.x, forward.x, up.x, 0.0f, right.y, forward.y,
         up.y, 0.0f, right.z, forward.z, up.z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Creates a matrix from three axes and a translation. The fourth row, w,
    * is assumed to be (0.0, 0.0, 0.0, 1.0).
    *
    * @param right       the right axis
    * @param forward     the forward axis
    * @param up          the up axis
    * @param translation the translation
    * @param target      the output matrix
    *
    * @return the matrix
    */
   public static Mat4 fromAxes ( final Vec3 right, final Vec3 forward,
      final Vec3 up, final Vec3 translation, final Mat4 target ) {

      return target.set(right.x, forward.x, up.x, translation.x, right.y,
         forward.y, up.y, translation.y, right.z, forward.z, up.z,
         translation.z, 0.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Creates a matrix from three axes. The fourth column, translation, is
    * assumed to be (0.0, 0.0, 0.0, 1.0).
    *
    * @param right   the right axis
    * @param forward the forward axis
    * @param up      the up axis
    * @param target  the output matrix
    *
    * @return the matrix
    */
   public static Mat4 fromAxes ( final Vec4 right, final Vec4 forward,
      final Vec4 up, final Mat4 target ) {

      /* @formatter:off */
      return target.set(
         right.x, forward.x, up.x, 0.0f,
         right.y, forward.y, up.y, 0.0f,
         right.z, forward.z, up.z, 0.0f,
         right.w, forward.w, up.w, 1.0f);
      /* @formatter:on */
   }

   /**
    * Creates a matrix from three axes and a translation.
    *
    * @param right       the right axis
    * @param forward     the forward axis
    * @param up          the up axis
    * @param translation the translation
    * @param target      the output matrix
    *
    * @return the matrix
    */
   public static Mat4 fromAxes ( final Vec4 right, final Vec4 forward,
      final Vec4 up, final Vec4 translation, final Mat4 target ) {

      /* @formatter:off */
      return target.set(
         right.x, forward.x, up.x, translation.x,
         right.y, forward.y, up.y, translation.y,
         right.z, forward.z, up.z, translation.z,
         right.w, forward.w, up.w, translation.w);
      /* @formatter:on */
   }

   /**
    * Creates a rotation matrix from the cosine and sine of an angle around an
    * axis. The axis will be normalized by the function.
    *
    * @param cosa   the cosine of an angle
    * @param sina   the sine of an angle
    * @param axis   the axis
    * @param target the output matrix
    *
    * @return the matrix
    */
   public static Mat4 fromRotation ( final float cosa, final float sina,
      final Vec3 axis, final Mat4 target ) {

      final float mSq = Vec3.magSq(axis);
      if ( mSq == 0.0f ) { return target.reset(); }

      final float mInv = Utils.invSqrtUnchecked(mSq);
      final float ax = axis.x * mInv;
      final float ay = axis.y * mInv;
      final float az = axis.z * mInv;

      final float d = 1.0f - cosa;
      final float x = ax * d;
      final float y = ay * d;
      final float z = az * d;

      final float axay = x * ay;
      final float axaz = x * az;
      final float ayaz = y * az;

      /* @formatter:off */
      return target.set(
            cosa + x * ax, axay - sina * az, axaz + sina * ay, 0.0f,
         axay + sina * az,    cosa + y * ay, ayaz - sina * ax, 0.0f,
         axaz - sina * ay, ayaz + sina * ax,    cosa + z * az, 0.0f,
         0.0f, 0.0f, 0.0f, 1.0f);
      /* @formatter:on */
   }

   /**
    * Creates a rotation matrix from an angle in radians around an axis. The
    * axis will be normalized by the function.
    *
    * @param radians the angle
    * @param axis    the axis
    * @param target  the output matrix
    *
    * @return the matrix
    */
   public static Mat4 fromRotation ( final float radians, final Vec3 axis,
      final Mat4 target ) {

      // return Mat4.fromRotation(
      // Utils.cos(radians),
      // Utils.sin(radians),
      // axis,
      // target);

      final float norm = radians * IUtils.ONE_TAU;
      return Mat4.fromRotation(Utils.scNorm(norm), Utils.scNorm(norm - 0.25f),
         axis, target);
   }

   /**
    * Creates a rotation matrix from a quaternion.
    *
    * @param source the quaternion
    * @param target the output matrix
    *
    * @return the matrix
    */
   public static Mat4 fromRotation ( final Quaternion source,
      final Mat4 target ) {

      final float w = source.real;
      final Vec3 i = source.imag;
      final float x = i.x;
      final float y = i.y;
      final float z = i.z;

      final float x2 = x + x;
      final float y2 = y + y;
      final float z2 = z + z;
      final float xsq2 = x * x2;
      final float ysq2 = y * y2;
      final float zsq2 = z * z2;
      final float xy2 = x * y2;
      final float xz2 = x * z2;
      final float yz2 = y * z2;
      final float wx2 = w * x2;
      final float wy2 = w * y2;
      final float wz2 = w * z2;

      /* @formatter:off */
      return target.set(
         1.0f - ysq2 - zsq2,          xy2 - wz2,          xz2 + wy2, 0.0f,
                  xy2 + wz2, 1.0f - xsq2 - zsq2,          yz2 - wx2, 0.0f,
                  xz2 - wy2,          yz2 + wx2, 1.0f - xsq2 - ysq2, 0.0f,
                       0.0f,               0.0f,               0.0f, 1.0f);
      /* @formatter:on */
   }

   /**
    * Creates a rotation matrix from a cosine and sine around the x axis.
    *
    * @param cosa   the cosine of an angle
    * @param sina   the sine of an angle
    * @param target the output matrix
    *
    * @return the matrix
    */
   public static Mat4 fromRotX ( final float cosa, final float sina,
      final Mat4 target ) {

      /* @formatter:off */
      return target.set(
         1.0f, 0.0f,  0.0f, 0.0f,
         0.0f, cosa, -sina, 0.0f,
         0.0f, sina,  cosa, 0.0f,
         0.0f, 0.0f,  0.0f, 1.0f);
      /* @formatter:on */
   }

   /**
    * Creates a rotation matrix from an angle in radians around the x axis.
    *
    * @param radians the angle
    * @param target  the output matrix
    *
    * @return the matrix
    */
   public static Mat4 fromRotX ( final float radians, final Mat4 target ) {

      // return Mat4.fromRotX(
      // Utils.cos(radians),
      // Utils.sin(radians),
      // target);

      final float norm = radians * IUtils.ONE_TAU;
      return Mat4.fromRotX(Utils.scNorm(norm), Utils.scNorm(norm - 0.25f),
         target);
   }

   /**
    * Creates a rotation matrix from a cosine and sine around the y axis.
    *
    * @param cosa   the cosine of an angle
    * @param sina   the sine of an angle
    * @param target the output matrix
    *
    * @return the matrix
    */
   public static Mat4 fromRotY ( final float cosa, final float sina,
      final Mat4 target ) {

      // RESEARCH: Is this inconsistent when compared with 4D rotation about
      // the YW axis? Should sin(a) and -sin(a) be transposed?

      /* @formatter:off */
      return target.set(
          cosa, 0.0f, sina, 0.0f,
          0.0f, 1.0f, 0.0f, 0.0f,
         -sina, 0.0f, cosa, 0.0f,
          0.0f, 0.0f, 0.0f, 1.0f);
      /* @formatter:on */
   }

   /**
    * Creates a rotation matrix from an angle in radians around the y axis.
    *
    * @param radians the angle
    * @param target  the output matrix
    *
    * @return the matrix
    */
   public static Mat4 fromRotY ( final float radians, final Mat4 target ) {

      // return Mat4.fromRotY(
      // Utils.cos(radians),
      // Utils.sin(radians),
      // target);

      final float norm = radians * IUtils.ONE_TAU;
      return Mat4.fromRotY(Utils.scNorm(norm), Utils.scNorm(norm - 0.25f),
         target);
   }

   /**
    * Creates a rotation matrix from a cosine and sine around the z axis.
    *
    * @param cosa   the cosine of an angle
    * @param sina   the sine of an angle
    * @param target the output matrix
    *
    * @return the matrix
    */
   public static Mat4 fromRotZ ( final float cosa, final float sina,
      final Mat4 target ) {

      /* @formatter:off */
      return target.set(
         cosa, -sina, 0.0f, 0.0f,
         sina,  cosa, 0.0f, 0.0f,
         0.0f,  0.0f, 1.0f, 0.0f,
         0.0f,  0.0f, 0.0f, 1.0f);
      /* @formatter:on */
   }

   /**
    * Creates a rotation matrix from an angle in radians around the z axis.
    *
    * @param radians the angle
    * @param target  the output matrix
    *
    * @return the matrix
    */
   public static Mat4 fromRotZ ( final float radians, final Mat4 target ) {

      // return Mat4.fromRotZ(
      // Utils.cos(radians),
      // Utils.sin(radians),
      // target);

      final float norm = radians * IUtils.ONE_TAU;
      return Mat4.fromRotZ(Utils.scNorm(norm), Utils.scNorm(norm - 0.25f),
         target);
   }

   /**
    * Creates a scale matrix from a scalar. The bottom right corner, m33, is
    * set to 1.0 .
    *
    * @param scalar the scalar
    * @param target the output matrix
    *
    * @return the matrix
    */
   public static Mat4 fromScale ( final float scalar, final Mat4 target ) {

      if ( scalar != 0.0f ) {
         return target.set(scalar, 0.0f, 0.0f, 0.0f, 0.0f, scalar, 0.0f, 0.0f,
            0.0f, 0.0f, scalar, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);

      }
      return target.reset();
   }

   /**
    * Creates a scale matrix from a nonuniform scalar, stored in a vector. The
    * scale on the z axis is assumed to be 1.0 .
    *
    * @param scalar the nonuniform scalar
    * @param target the output matrix
    *
    * @return the matrix
    */
   public static Mat4 fromScale ( final Vec2 scalar, final Mat4 target ) {

      if ( Vec2.all(scalar) ) {
         return target.set(scalar.x, 0.0f, 0.0f, 0.0f, 0.0f, scalar.y, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
      }
      return target.reset();
   }

   /**
    * Creates a scale matrix from a nonuniform scalar, stored in a vector.
    *
    * @param scalar the nonuniform scalar
    * @param target the output matrix
    *
    * @return the matrix
    */
   public static Mat4 fromScale ( final Vec3 scalar, final Mat4 target ) {

      if ( Vec3.all(scalar) ) {
         return target.set(scalar.x, 0.0f, 0.0f, 0.0f, 0.0f, scalar.y, 0.0f,
            0.0f, 0.0f, 0.0f, scalar.z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
      }
      return target.reset();
   }

   /**
    * Creates a translation matrix from a vector.
    *
    * @param translation the translation
    * @param target      the output matrix
    *
    * @return the matrix
    */
   public static Mat4 fromTranslation ( final Vec2 translation,
      final Mat4 target ) {

      return target.set(1.0f, 0.0f, 0.0f, translation.x, 0.0f, 1.0f, 0.0f,
         translation.y, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Creates a translation matrix from a vector.
    *
    * @param translation the translation
    * @param target      the output matrix
    *
    * @return the matrix
    */
   public static Mat4 fromTranslation ( final Vec3 translation,
      final Mat4 target ) {

      /* @formatter:off */
      return target.set(
         1.0f, 0.0f, 0.0f, translation.x,
         0.0f, 1.0f, 0.0f, translation.y,
         0.0f, 0.0f, 1.0f, translation.z,
         0.0f, 0.0f, 0.0f, 1.0f);
      /* @formatter:on */
   }

   /**
    * Creates a view frustum given the edges of the view port.
    *
    * @param left   the left edge of the window
    * @param right  the right edge of the window
    * @param bottom the bottom edge of the window
    * @param top    the top edge of the window
    * @param near   the near clip plane
    * @param far    the far clip plane
    * @param target the output matrix
    *
    * @return the view frustum
    */
   public static Mat4 frustum ( final float left, final float right,
      final float bottom, final float top, final float near, final float far,
      final Mat4 target ) {

      final float n2 = near + near;

      float w = right - left;
      float h = top - bottom;
      float d = far - near;

      w = w != 0.0f ? 1.0f / w : 1.0f;
      h = h != 0.0f ? 1.0f / h : 1.0f;
      d = d != 0.0f ? 1.0f / d : 1.0f;

      /* @formatter:off */
      return target.set(
         n2 * w,   0.0f, ( right + left ) *  w,          0.0f,
           0.0f, n2 * h, ( top + bottom ) *  h,          0.0f,
           0.0f,   0.0f,   ( far + near ) * -d, n2 * far * -d,
           0.0f,   0.0f,                 -1.0f,          0.0f);
      /* @formatter:on */
   }

   /**
    * Returns the identity matrix:
    *
    * <pre>
    * 1.0, 0.0, 0.0, 0.0,
    * 0.0, 1.0, 0.0, 0.0,
    * 0.0, 0.0, 1.0, 0.0,
    * 0.0, 0.0, 0.0, 1.0
    * </pre>
    *
    * @param target the output matrix
    *
    * @return the identity matrix
    */
   public static Mat4 identity ( final Mat4 target ) {

      return target.set(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
         0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Inverts the input matrix.
    *
    * @param m      the matrix
    * @param target the output matrix
    *
    * @return the inverse
    */
   public static Mat4 inverse ( final Mat4 m, final Mat4 target ) {

      final float b00 = m.m00 * m.m11 - m.m01 * m.m10;
      final float b01 = m.m00 * m.m12 - m.m02 * m.m10;
      final float b02 = m.m00 * m.m13 - m.m03 * m.m10;
      final float b03 = m.m01 * m.m12 - m.m02 * m.m11;
      final float b04 = m.m01 * m.m13 - m.m03 * m.m11;
      final float b05 = m.m02 * m.m13 - m.m03 * m.m12;
      final float b06 = m.m20 * m.m31 - m.m21 * m.m30;
      final float b07 = m.m20 * m.m32 - m.m22 * m.m30;
      final float b08 = m.m20 * m.m33 - m.m23 * m.m30;
      final float b09 = m.m21 * m.m32 - m.m22 * m.m31;
      final float b10 = m.m21 * m.m33 - m.m23 * m.m31;
      final float b11 = m.m22 * m.m33 - m.m23 * m.m32;

      /* @formatter:off */
      final float det = b00 * b11 - b01 * b10 +
                        b02 * b09 + b03 * b08 -
                        b04 * b07 + b05 * b06;
      if ( det != 0.0f ) {
         final float detInv = 1.0f / det;
         return target.set(
            ( m.m11 * b11 - m.m12 * b10 + m.m13 * b09 ) * detInv,
            ( m.m02 * b10 - m.m01 * b11 - m.m03 * b09 ) * detInv,
            ( m.m31 * b05 - m.m32 * b04 + m.m33 * b03 ) * detInv,
            ( m.m22 * b04 - m.m21 * b05 - m.m23 * b03 ) * detInv,
            ( m.m12 * b08 - m.m10 * b11 - m.m13 * b07 ) * detInv,
            ( m.m00 * b11 - m.m02 * b08 + m.m03 * b07 ) * detInv,
            ( m.m32 * b02 - m.m30 * b05 - m.m33 * b01 ) * detInv,
            ( m.m20 * b05 - m.m22 * b02 + m.m23 * b01 ) * detInv,
            ( m.m10 * b10 - m.m11 * b08 + m.m13 * b06 ) * detInv,
            ( m.m01 * b08 - m.m00 * b10 - m.m03 * b06 ) * detInv,
            ( m.m30 * b04 - m.m31 * b02 + m.m33 * b00 ) * detInv,
            ( m.m21 * b02 - m.m20 * b04 - m.m23 * b00 ) * detInv,
            ( m.m11 * b07 - m.m10 * b09 - m.m12 * b06 ) * detInv,
            ( m.m00 * b09 - m.m01 * b07 + m.m02 * b06 ) * detInv,
            ( m.m31 * b01 - m.m30 * b03 - m.m32 * b00 ) * detInv,
            ( m.m20 * b03 - m.m21 * b01 + m.m22 * b00 ) * detInv);
      }
      return target.reset();
      /* @formatter:on */
   }

   /**
    * Tests to see if a matrix is the identity matrix.
    *
    * @param m the matrix
    *
    * @return the evaluation
    */
   public static boolean isIdentity ( final Mat4 m ) {

      /* @formatter:off */
      return m.m33 == 1.0f && m.m22 == 1.0f && m.m11 == 1.0f && m.m00 == 1.0f
          && m.m01 == 0.0f && m.m02 == 0.0f && m.m03 == 0.0f && m.m10 == 0.0f
          && m.m12 == 0.0f && m.m13 == 0.0f && m.m20 == 0.0f && m.m21 == 0.0f
          && m.m23 == 0.0f && m.m30 == 0.0f && m.m31 == 0.0f && m.m32 == 0.0f;
      /* @formatter:on */
   }

   /**
    * Multiplies each component in a matrix by a scalar. Not to be confused
    * with scaling affine transform matrix.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output matrix
    *
    * @return the product
    */
   public static Mat4 mul ( final float a, final Mat4 b, final Mat4 target ) {

      /* @formatter:off */
      return target.set(
         a * b.m00, a * b.m01, a * b.m02, a * b.m03,
         a * b.m10, a * b.m11, a * b.m12, a * b.m13,
         a * b.m20, a * b.m21, a * b.m22, a * b.m23,
         a * b.m30, a * b.m31, a * b.m32, a * b.m33);
      /* @formatter:on */
   }

   /**
    * Multiplies each component in a matrix by a scalar. Not to be confused
    * with scaling affine transform matrix.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output matrix
    *
    * @return the product
    */
   public static Mat4 mul ( final Mat4 a, final float b, final Mat4 target ) {

      /* @formatter:off */
      return target.set(
         a.m00 * b, a.m01 * b, a.m02 * b, a.m03 * b,
         a.m10 * b, a.m11 * b, a.m12 * b, a.m13 * b,
         a.m20 * b, a.m21 * b, a.m22 * b, a.m23 * b,
         a.m30 * b, a.m31 * b, a.m32 * b, a.m33 * b);
      /* @formatter:on */
   }

   /**
    * Multiplies two matrices by component.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output matrix
    *
    * @return the product
    */
   public static Mat4 mul ( final Mat4 a, final Mat4 b, final Mat4 target ) {

      /* @formatter:off */
      return target.set(
         a.m00 * b.m00 + a.m01 * b.m10 + a.m02 * b.m20 + a.m03 * b.m30,
         a.m00 * b.m01 + a.m01 * b.m11 + a.m02 * b.m21 + a.m03 * b.m31,
         a.m00 * b.m02 + a.m01 * b.m12 + a.m02 * b.m22 + a.m03 * b.m32,
         a.m00 * b.m03 + a.m01 * b.m13 + a.m02 * b.m23 + a.m03 * b.m33,

         a.m10 * b.m00 + a.m11 * b.m10 + a.m12 * b.m20 + a.m13 * b.m30,
         a.m10 * b.m01 + a.m11 * b.m11 + a.m12 * b.m21 + a.m13 * b.m31,
         a.m10 * b.m02 + a.m11 * b.m12 + a.m12 * b.m22 + a.m13 * b.m32,
         a.m10 * b.m03 + a.m11 * b.m13 + a.m12 * b.m23 + a.m13 * b.m33,

         a.m20 * b.m00 + a.m21 * b.m10 + a.m22 * b.m20 + a.m23 * b.m30,
         a.m20 * b.m01 + a.m21 * b.m11 + a.m22 * b.m21 + a.m23 * b.m31,
         a.m20 * b.m02 + a.m21 * b.m12 + a.m22 * b.m22 + a.m23 * b.m32,
         a.m20 * b.m03 + a.m21 * b.m13 + a.m22 * b.m23 + a.m23 * b.m33,

         a.m30 * b.m00 + a.m31 * b.m10 + a.m32 * b.m20 + a.m33 * b.m30,
         a.m30 * b.m01 + a.m31 * b.m11 + a.m32 * b.m21 + a.m33 * b.m31,
         a.m30 * b.m02 + a.m31 * b.m12 + a.m32 * b.m22 + a.m33 * b.m32,
         a.m30 * b.m03 + a.m31 * b.m13 + a.m32 * b.m23 + a.m33 * b.m33);
      /* @formatter:on */
   }

   /**
    * Multiplies three matrices by component. Useful for composing an affine
    * transform from translation, rotation and scale matrices.
    *
    * @param a      the first matrix
    * @param b      the second matrix
    * @param c      the third matrix
    * @param target the output matrix
    *
    * @return the product
    */
   public static Mat4 mul ( final Mat4 a, final Mat4 b, final Mat4 c,
      final Mat4 target ) {

      /* @formatter:off */
      final float n00 = a.m00 * b.m00 + a.m01 * b.m10 +
                        a.m02 * b.m20 + a.m03 * b.m30;
      final float n01 = a.m00 * b.m01 + a.m01 * b.m11 +
                        a.m02 * b.m21 + a.m03 * b.m31;
      final float n02 = a.m00 * b.m02 + a.m01 * b.m12 +
                        a.m02 * b.m22 + a.m03 * b.m32;
      final float n03 = a.m00 * b.m03 + a.m01 * b.m13 +
                        a.m02 * b.m23 + a.m03 * b.m33;

      final float n10 = a.m10 * b.m00 + a.m11 * b.m10 +
                        a.m12 * b.m20 + a.m13 * b.m30;
      final float n11 = a.m10 * b.m01 + a.m11 * b.m11 +
                        a.m12 * b.m21 + a.m13 * b.m31;
      final float n12 = a.m10 * b.m02 + a.m11 * b.m12 +
                        a.m12 * b.m22 + a.m13 * b.m32;
      final float n13 = a.m10 * b.m03 + a.m11 * b.m13 +
                        a.m12 * b.m23 + a.m13 * b.m33;

      final float n20 = a.m20 * b.m00 + a.m21 * b.m10 +
                        a.m22 * b.m20 + a.m23 * b.m30;
      final float n21 = a.m20 * b.m01 + a.m21 * b.m11 +
                        a.m22 * b.m21 + a.m23 * b.m31;
      final float n22 = a.m20 * b.m02 + a.m21 * b.m12 +
                        a.m22 * b.m22 + a.m23 * b.m32;
      final float n23 = a.m20 * b.m03 + a.m21 * b.m13 +
                        a.m22 * b.m23 + a.m23 * b.m33;

      final float n30 = a.m30 * b.m00 + a.m31 * b.m10 +
                        a.m32 * b.m20 + a.m33 * b.m30;
      final float n31 = a.m30 * b.m01 + a.m31 * b.m11 +
                        a.m32 * b.m21 + a.m33 * b.m31;
      final float n32 = a.m30 * b.m02 + a.m31 * b.m12 +
                        a.m32 * b.m22 + a.m33 * b.m32;
      final float n33 = a.m30 * b.m03 + a.m31 * b.m13 +
                        a.m32 * b.m23 + a.m33 * b.m33;

      return target.set(
         n00 * c.m00 + n01 * c.m10 + n02 * c.m20 + n03 * c.m30,
         n00 * c.m01 + n01 * c.m11 + n02 * c.m21 + n03 * c.m31,
         n00 * c.m02 + n01 * c.m12 + n02 * c.m22 + n03 * c.m32,
         n00 * c.m03 + n01 * c.m13 + n02 * c.m23 + n03 * c.m33,

         n10 * c.m00 + n11 * c.m10 + n12 * c.m20 + n13 * c.m30,
         n10 * c.m01 + n11 * c.m11 + n12 * c.m21 + n13 * c.m31,
         n10 * c.m02 + n11 * c.m12 + n12 * c.m22 + n13 * c.m32,
         n10 * c.m03 + n11 * c.m13 + n12 * c.m23 + n13 * c.m33,

         n20 * c.m00 + n21 * c.m10 + n22 * c.m20 + n23 * c.m30,
         n20 * c.m01 + n21 * c.m11 + n22 * c.m21 + n23 * c.m31,
         n20 * c.m02 + n21 * c.m12 + n22 * c.m22 + n23 * c.m32,
         n20 * c.m03 + n21 * c.m13 + n22 * c.m23 + n23 * c.m33,

         n30 * c.m00 + n31 * c.m10 + n32 * c.m20 + n33 * c.m30,
         n30 * c.m01 + n31 * c.m11 + n32 * c.m21 + n33 * c.m31,
         n30 * c.m02 + n31 * c.m12 + n32 * c.m22 + n33 * c.m32,
         n30 * c.m03 + n31 * c.m13 + n32 * c.m23 + n33 * c.m33);
      /* @formatter:on */
   }

   /**
    * Multiplies a matrix and a quaternion. Stores the quaternion's matrix
    * representation in an output variable.
    *
    * @param a      matrix
    * @param b      quaternion
    * @param target the output matrix
    * @param bm     the matrix conversion
    *
    * @return the product
    */
   public static Mat4 mul ( final Mat4 a, final Quaternion b, final Mat4 target,
      final Mat4 bm ) {

      Mat4.fromRotation(b, bm);
      return Mat4.mul(a, bm, target);
   }

   /**
    * Multiplies a matrix and a vector.
    *
    * @param a      the matrix
    * @param b      the vector
    * @param target the output vector
    *
    * @return the product
    */
   public static Vec4 mul ( final Mat4 a, final Vec4 b, final Vec4 target ) {

      /* @formatter:off */
      return target.set(
         a.m00 * b.x + a.m01 * b.y + a.m02 * b.z + a.m03 * b.w,
         a.m10 * b.x + a.m11 * b.y + a.m12 * b.z + a.m13 * b.w,
         a.m20 * b.x + a.m21 * b.y + a.m22 * b.z + a.m23 * b.w,
         a.m30 * b.x + a.m31 * b.y + a.m32 * b.z + a.m33 * b.w);
      /* @formatter:on */
   }

   /**
    * Multiplies a quaternion and a matrix. Stores the quaternion's matrix
    * representation in an output variable.
    *
    * @param a      quaternion
    * @param b      matrix
    * @param target the output matrix
    * @param am     the matrix conversion
    *
    * @return the product
    */
   public static Mat4 mul ( final Quaternion a, final Mat4 b, final Mat4 target,
      final Mat4 am ) {

      Mat4.fromRotation(a, am);
      return Mat4.mul(am, b, target);
   }

   /**
    * Multiplies a matrix and a point. The z component of the point is assumed
    * to be 0.0 . The w component of the point is assumed to be 1.0 , so the
    * point is impacted by the matrix's translation.
    *
    * @param a      the matrix
    * @param b      the point
    * @param target the output point
    *
    * @return the product
    */
   @Experimental
   public static Vec3 mulPoint ( final Mat4 a, final Vec2 b,
      final Vec3 target ) {

      // TEST

      final float w = a.m30 * b.x + a.m31 * b.y + a.m33;
      if ( w == 0.0f ) { return target.reset(); }
      final float wInv = 1.0f / w;

      return target.set( ( a.m00 * b.x + a.m01 * b.y + a.m03 ) * wInv, ( a.m10
         * b.x + a.m11 * b.y + a.m13 ) * wInv, ( a.m20 * b.x + a.m21 * b.y
            + a.m23 ) * wInv);
   }

   /**
    * Multiplies a matrix and a point. The w component of the point is assumed
    * to be 1.0 , so the point is impacted by the matrix's translation.
    *
    * @param a      the matrix
    * @param b      the point
    * @param target the output point
    *
    * @return the product
    */
   @Experimental
   public static Vec3 mulPoint ( final Mat4 a, final Vec3 b,
      final Vec3 target ) {

      // TEST

      final float w = a.m30 * b.x + a.m31 * b.y + a.m32 * b.z + a.m33;
      if ( w == 0.0f ) { return target.reset(); }
      final float wInv = 1.0f / w;

      return target.set( ( a.m00 * b.x + a.m01 * b.y + a.m02 * b.z + a.m03 )
         * wInv, ( a.m10 * b.x + a.m11 * b.y + a.m12 * b.z + a.m13 ) * wInv,
         ( a.m20 * b.x + a.m21 * b.y + a.m22 * b.z + a.m23 ) * wInv);
   }

   /**
    * Multiplies a matrix and a vector. The z and w components of the vector
    * are assumed to be 0.0 , so the vector is not impacted by the matrix's
    * translation.
    *
    * @param a      the matrix
    * @param b      the vector
    * @param target the output vector
    *
    * @return the product
    */
   @Experimental
   public static Vec3 mulVector ( final Mat4 a, final Vec2 b,
      final Vec3 target ) {

      // TEST

      final float w = a.m30 * b.x + a.m31 * b.y + a.m33;
      if ( w == 0.0f ) { return target.reset(); }
      final float wInv = 1.0f / w;

      return target.set( ( a.m00 * b.x + a.m01 * b.y ) * wInv, ( a.m10 * b.x
         + a.m11 * b.y ) * wInv, ( a.m20 * b.x + a.m21 * b.y ) * wInv);
   }

   /**
    * Multiplies a matrix and a vector. The w component of the vector is
    * assumed to be 0.0 , so the vector is not impacted by the matrix's
    * translation.
    *
    * @param a      the matrix
    * @param b      the vector
    * @param target the output vector
    *
    * @return the product
    */
   @Experimental
   public static Vec3 mulVector ( final Mat4 a, final Vec3 b,
      final Vec3 target ) {

      // TEST

      final float w = a.m30 * b.x + a.m31 * b.y + a.m32 * b.z + a.m33;
      if ( w == 0.0f ) { return target.reset(); }
      final float wInv = 1.0f / w;

      return target.set( ( a.m00 * b.x + a.m01 * b.y + a.m02 * b.z ) * wInv,
         ( a.m10 * b.x + a.m11 * b.y + a.m12 * b.z ) * wInv, ( a.m20 * b.x
            + a.m21 * b.y + a.m22 * b.z ) * wInv);
   }

   /**
    * Creates an orthographic projection matrix, where objects maintain their
    * size regardless of distance from the camera.
    *
    * @param left   the left edge of the window
    * @param right  the right edge of the window
    * @param bottom the bottom edge of the window
    * @param top    the top edge of the window
    * @param near   the near clip plane
    * @param far    the far clip plane
    * @param target the output matrix
    *
    * @return the orthographic projection
    */
   public static Mat4 orthographic ( final float left, final float right,
      final float bottom, final float top, final float near, final float far,
      final Mat4 target ) {

      float w = right - left;
      float h = top - bottom;
      float d = far - near;

      w = w != 0.0f ? 1.0f / w : 1.0f;
      h = h != 0.0f ? 1.0f / h : 1.0f;
      d = d != 0.0f ? 1.0f / d : 1.0f;

      /* @formatter:off */
      return target.set(
         w + w,  0.0f,        0.0f, w * ( left + right ),
          0.0f, h + h,        0.0f, h * ( top + bottom ),
          0.0f,  0.0f, - ( d + d ),  -d * ( far + near ),
          0.0f,  0.0f,        0.0f,                 1.0f);
      /* @formatter:on */
   }

   /**
    * Creates a perspective projection matrix, where objects nearer to the
    * camera appear larger than objects distant from the camera.
    *
    * @param fov    the field of view
    * @param aspect the aspect ratio, width over height
    * @param near   the near clip plane
    * @param far    the far clip plane
    * @param target the output matrix
    *
    * @return the perspective projection
    */
   public static Mat4 perspective ( final float fov, final float aspect,
      final float near, final float far, final Mat4 target ) {

      final float cotfov = Utils.cot(fov * 0.5f);
      final float d = Utils.div(1.0f, far - near);
      return target.set(Utils.div(cotfov, aspect), 0.0f, 0.0f, 0.0f, 0.0f,
         cotfov, 0.0f, 0.0f, 0.0f, 0.0f, ( far + near ) * -d, ( near + near )
            * far * -d, 0.0f, 0.0f, -1.0f, 0.0f);
   }

   /**
    * Subtracts the right matrix from the left matrix.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output matrix
    *
    * @return the result
    */
   public static Mat4 sub ( final Mat4 a, final Mat4 b, final Mat4 target ) {

      /* @formatter:off */
      return target.set(
         a.m00 - b.m00, a.m01 - b.m01, a.m02 - b.m02, a.m03 - b.m03,
         a.m10 - b.m10, a.m11 - b.m11, a.m12 - b.m12, a.m13 - b.m13,
         a.m20 - b.m20, a.m21 - b.m21, a.m22 - b.m22, a.m23 - b.m23,
         a.m30 - b.m30, a.m31 - b.m31, a.m32 - b.m32, a.m33 - b.m33);
      /* @formatter:on */
   }

   /**
    * Transposes a matrix, switching its row and column indices.
    *
    * @param m      the matrix
    * @param target the output matrix
    *
    * @return the transposed matrix
    */
   public static Mat4 transpose ( final Mat4 m, final Mat4 target ) {

      /* @formatter:off */
      return target.set(
         m.m00, m.m10, m.m20, m.m30,
         m.m01, m.m11, m.m21, m.m31,
         m.m02, m.m12, m.m22, m.m32,
         m.m03, m.m13, m.m23, m.m33);
      /* @formatter:on */
   }

   /**
    * An iterator, which allows a matrix's components to be accessed in an
    * enhanced for loop.
    */
   public static final class M4Iterator implements Iterator < Float > {

      /**
       * The current index.
       */
      private int index = 0;

      /**
       * The matrix being iterated over.
       */
      private final Mat4 mtx;

      /**
       * The default constructor.
       *
       * @param mtx the matrix to iterate
       */
      public M4Iterator ( final Mat4 mtx ) { this.mtx = mtx; }

      /**
       * Tests to see if the iterator has another value.
       *
       * @return the evaluation
       */
      @Override
      public boolean hasNext ( ) { return this.index < this.mtx.length(); }

      /**
       * Gets the next value in the iterator.
       *
       * @return the value
       *
       * @see Mat4#getElm(int)
       */
      @Override
      public Float next ( ) { return this.mtx.getElm(this.index++); }

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

}
