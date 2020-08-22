package camzup.core;

import java.util.Iterator;

/**
 * A mutable, extensible class influenced by GLSL, OSL and Processing's
 * PMatrix3D. Although this is a 4 x 4 matrix, it is generally assumed to
 * be a 3D affine transform matrix, where the last row is (0.0, 0.0, 0.0,
 * 1.0) . Instance methods are limited, while most static methods require
 * an explicit output variable to be provided.
 */
public class Mat4 implements IUtils, Cloneable, Iterable < Float > {

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
   public Mat4 ( ) {}

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
   public Mat4 ( final boolean m00, final boolean m01, final boolean m02,
      final boolean m03, final boolean m10, final boolean m11,
      final boolean m12, final boolean m13, final boolean m20,
      final boolean m21, final boolean m22, final boolean m23,
      final boolean m30, final boolean m31, final boolean m32,
      final boolean m33 ) {

      this.set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30,
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
   public Mat4 ( final float m00, final float m01, final float m02,
      final float m10, final float m11, final float m12, final float m20,
      final float m21, final float m22 ) {

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

      this.set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30,
         m31, m32, m33);
   }

   /**
    * Constructs a matrix from a source matrix's components.
    *
    * @param source the source matrix
    */
   public Mat4 ( final Mat4 source ) { this.set(source); }

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

      // TODO: Move to KtMat4.
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
    * Returns the number of elements in the matrix.
    *
    * @return the length
    */
   public int length ( ) { return 16; }

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
   public Mat4 set ( final boolean m00, final boolean m01, final boolean m02,
      final boolean m03, final boolean m10, final boolean m11,
      final boolean m12, final boolean m13, final boolean m20,
      final boolean m21, final boolean m22, final boolean m23,
      final boolean m30, final boolean m31, final boolean m32,
      final boolean m33 ) {

      this.m00 = m00 ? 1.0f : 0.0f;
      this.m01 = m01 ? 1.0f : 0.0f;
      this.m02 = m02 ? 1.0f : 0.0f;
      this.m03 = m03 ? 1.0f : 0.0f;

      this.m10 = m10 ? 1.0f : 0.0f;
      this.m11 = m11 ? 1.0f : 0.0f;
      this.m12 = m12 ? 1.0f : 0.0f;
      this.m13 = m13 ? 1.0f : 0.0f;

      this.m20 = m20 ? 1.0f : 0.0f;
      this.m21 = m21 ? 1.0f : 0.0f;
      this.m22 = m22 ? 1.0f : 0.0f;
      this.m23 = m23 ? 1.0f : 0.0f;

      this.m30 = m30 ? 1.0f : 0.0f;
      this.m31 = m31 ? 1.0f : 0.0f;
      this.m32 = m32 ? 1.0f : 0.0f;
      this.m33 = m33 ? 1.0f : 0.0f;

      return this;
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
      return this;
      /* @formatter:on */
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
    * Returns a float array containing this matrix's components.
    *
    * @return the array
    */
   public float[] toArray ( ) { return this.toArray1(); }

   /**
    * Returns a 1D float array containing this matrix's components in row
    * major order.
    *
    * @return the array
    */
   public float[] toArray1 ( ) {

      /* @formatter:off */
      return new float[] {
         this.m00, this.m01, this.m02, this.m03,
         this.m10, this.m11, this.m12, this.m13,
         this.m20, this.m21, this.m22, this.m23,
         this.m30, this.m31, this.m32, this.m33 };
      /* @formatter:on */
   }

   /**
    * Returns a 2D float array containing this matrix's components.
    *
    * @return the array
    */
   public float[][] toArray2 ( ) {

      /* @formatter:off */
      return new float[][] {
         { this.m00, this.m01, this.m02, this.m03 },
         { this.m10, this.m11, this.m12, this.m13 },
         { this.m20, this.m21, this.m22, this.m23 },
         { this.m30, this.m31, this.m32, this.m33 } };
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

      final StringBuilder sb = new StringBuilder(512);
      sb.append("{ m00: ");
      sb.append(Utils.toFixed(this.m00, places));
      sb.append(", m01: ");
      sb.append(Utils.toFixed(this.m01, places));
      sb.append(", m02: ");
      sb.append(Utils.toFixed(this.m02, places));
      sb.append(", m03: ");
      sb.append(Utils.toFixed(this.m03, places));
      sb.append(", m10: ");
      sb.append(Utils.toFixed(this.m10, places));
      sb.append(", m11: ");
      sb.append(Utils.toFixed(this.m11, places));
      sb.append(", m12: ");
      sb.append(Utils.toFixed(this.m12, places));
      sb.append(", m13: ");
      sb.append(Utils.toFixed(this.m13, places));
      sb.append(", m20: ");
      sb.append(Utils.toFixed(this.m20, places));
      sb.append(", m21: ");
      sb.append(Utils.toFixed(this.m21, places));
      sb.append(", m22: ");
      sb.append(Utils.toFixed(this.m22, places));
      sb.append(", m23: ");
      sb.append(Utils.toFixed(this.m23, places));
      sb.append(", m30: ");
      sb.append(Utils.toFixed(this.m30, places));
      sb.append(", m31: ");
      sb.append(Utils.toFixed(this.m31, places));
      sb.append(", m32: ");
      sb.append(Utils.toFixed(this.m32, places));
      sb.append(", m33: ");
      sb.append(Utils.toFixed(this.m33, places));
      sb.append(' ');
      sb.append('}');
      return sb.toString();
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

      /* With {@link Float.floatToIntBits(float)}, -0.0f != 0.0f. */
      return this.m00 == n.m00 && this.m01 == n.m01 && this.m02 == n.m02
         && this.m03 == n.m03 && this.m10 == n.m10 && this.m11 == n.m11
         && this.m12 == n.m12 && this.m13 == n.m13 && this.m20 == n.m20
         && this.m21 == n.m21 && this.m22 == n.m22 && this.m23 == n.m23
         && this.m30 == n.m30 && this.m31 == n.m31 && this.m32 == n.m32
         && this.m33 == n.m33;
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
    * Tests to see if all the matrix's components are non-zero.
    *
    * @param m the input matrix
    *
    * @return the evaluation
    */
   public static boolean all ( final Mat4 m ) {

      return m.m00 != 0.0f && m.m01 != 0.0f && m.m02 != 0.0f && m.m03 != 0.0f
         && m.m10 != 0.0f && m.m11 != 0.0f && m.m12 != 0.0f && m.m13 != 0.0f
         && m.m20 != 0.0f && m.m21 != 0.0f && m.m22 != 0.0f && m.m23 != 0.0f
         && m.m30 != 0.0f && m.m31 != 0.0f && m.m32 != 0.0f && m.m33 != 0.0f;
   }

   /**
    * Evaluates two matrices like booleans, using the AND logic gate.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output matrix
    *
    * @return the evaluation
    *
    * @see Utils#and(float, float)
    */
   public static Mat4 and ( final Mat4 a, final Mat4 b, final Mat4 target ) {

      /* @formatter:off */
      return target.set(
         Utils.and(a.m00, b.m00), Utils.and(a.m01, b.m01),
         Utils.and(a.m02, b.m02), Utils.and(a.m03, b.m03),
         Utils.and(a.m10, b.m10), Utils.and(a.m11, b.m11),
         Utils.and(a.m12, b.m12), Utils.and(a.m13, b.m13),
         Utils.and(a.m20, b.m20), Utils.and(a.m21, b.m21),
         Utils.and(a.m22, b.m22), Utils.and(a.m23, b.m23),
         Utils.and(a.m30, b.m30), Utils.and(a.m31, b.m31),
         Utils.and(a.m32, b.m32), Utils.and(a.m33, b.m33));
      /* @formatter:on */
   }

   /**
    * Tests to see if any of the matrix's components are non-zero.
    *
    * @param m the input matrix
    *
    * @return the evaluation
    */
   public static boolean any ( final Mat4 m ) {

      return m.m00 != 0.0f || m.m01 != 0.0f || m.m02 != 0.0f || m.m03 != 0.0f
         || m.m10 != 0.0f || m.m11 != 0.0f || m.m12 != 0.0f || m.m13 != 0.0f
         || m.m20 != 0.0f || m.m21 != 0.0f || m.m22 != 0.0f || m.m23 != 0.0f
         || m.m30 != 0.0f || m.m31 != 0.0f || m.m32 != 0.0f || m.m33 != 0.0f;
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

      return target.set(0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, IUtils.ONE_THIRD,
         1.0f, 0.0f, IUtils.ONE_THIRD, IUtils.TWO_THIRDS, 1.0f, 1.0f, 1.0f,
         1.0f, 1.0f);
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

      // RESEARCH: Shoemake on polar decomposition, which is also used in matrix
      // lerp.

      final float xMag = Utils.hypot(m.m00, m.m10, m.m20);
      final float yMag = Utils.hypot(m.m01, m.m11, m.m21);
      final float zMag = Utils.hypot(m.m02, m.m12, m.m22);
      final float det = Mat4.determinant(m);

      /*
       * Extract rotation matrix from affine transform matrix by dividing each
       * axis by the scale.
       */
      final float sxInv = Utils.div(1.0f, xMag);
      final float syInv = Utils.div(1.0f, yMag);
      final float szInv = Utils.div(1.0f, zMag);

      Quaternion.fromAxes(m.m00 * sxInv, m.m11 * syInv, m.m22 * szInv, m.m21
         * syInv, m.m12 * szInv, m.m02 * szInv, m.m20 * sxInv, m.m10 * sxInv,
         m.m01 * syInv, rot);
      scale.set(xMag, det < 0.0f ? -yMag : yMag, zMag);
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
    * Creates a matrix from two axes. The third axis, up, is assumed to be
    * (0.0, 0.0, 1.0, 0.0) . The fourth row and column are assumed to be (0.0,
    * 0.0, 0.0, 1.0) .
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
    * assumed to be (0.0, 0.0, 1.0, 0.0) . The fourth row, w, is assumed to be
    * (0.0, 0.0, 0.0, 1.0) .
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
    * to be (0.0, 0.0, 0.0, 1.0) .
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
    * is assumed to be (0.0, 0.0, 0.0, 1.0) .
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
    * assumed to be (0.0, 0.0, 0.0, 1.0) .
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

      return target.set(right.x, forward.x, up.x, 0.0f, right.y, forward.y,
         up.y, 0.0f, right.z, forward.z, up.z, 0.0f, right.w, forward.w, up.w,
         1.0f);
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

      return target.set(right.x, forward.x, up.x, translation.x, right.y,
         forward.y, up.y, translation.y, right.z, forward.z, up.z,
         translation.z, right.w, forward.w, up.w, translation.w);
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
      if ( mSq != 0.0f ) {
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
            axay + sina * az, cosa + y * ay, ayaz - sina * ax, 0.0f,
            axaz - sina * ay, ayaz + sina * ax, cosa + z * az, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
         /* @formatter:on */
      }
      return target.reset();
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

      // return Mat4.fromRotation(Utils.cos(radians), Utils.sin(radians),
      // axis, target);

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
    * Creates a scale matrix from a nonuniform scalar stored in a vector. The
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
    * Creates a scale matrix from a nonuniform scalar stored in a vector.
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
    * Creates skew, or shear, matrix from an angle and axes. Vectors
    * <em>a</em> and <em>b</em> are expected to be orthonormal, i.e.
    * perpendicular and of unit length.
    *
    * @param radians the angle in radians
    * @param a       the skew axis
    * @param b       the orthonormal axis
    * @param target  the output matrix
    *
    * @return the skew matrix
    */
   public static Mat4 fromSkew ( final float radians, final Vec3 a,
      final Vec3 b, final Mat4 target ) {

      final float t = Utils.tan(radians);
      final float tax = a.x * t;
      final float tay = a.y * t;
      final float taz = a.z * t;

      return target.set(tax * b.x + 1.0f, tax * b.y, tax * b.z, 0.0f, tay * b.x,
         tay * b.y + 1.0f, tay * b.z, 0.0f, taz * b.x, taz * b.y, taz * b.z
            + 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
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

      /* @formatter:off */
      return target.set(
         1.0f, 0.0f, 0.0f, translation.x,
         0.0f, 1.0f, 0.0f, translation.y,
         0.0f, 0.0f, 1.0f, 0.0f,
         0.0f, 0.0f, 0.0f, 1.0f);
      /* @formatter:on */
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
    * Evaluates whether the left comparisand is greater than the right
    * comparisand.
    *
    * @param a      left comparisand
    * @param b      right comparisand
    * @param target the output matrix
    *
    * @return the evaluation
    */
   public static Mat4 gt ( final Mat4 a, final Mat4 b, final Mat4 target ) {

      return target.set(a.m00 > b.m00, a.m01 > b.m01, a.m02 > b.m02, a.m03
         > b.m03, a.m10 > b.m10, a.m11 > b.m11, a.m12 > b.m12, a.m13 > b.m13,
         a.m20 > b.m20, a.m21 > b.m21, a.m22 > b.m22, a.m23 > b.m23, a.m30
            > b.m30, a.m31 > b.m31, a.m32 > b.m32, a.m33 > b.m33);
   }

   /**
    * Evaluates whether the left comparisand is greater than or equal to the
    * right comparisand.
    *
    * @param a      left comparisand
    * @param b      right comparisand
    * @param target the output matrix
    *
    * @return the evaluation
    */
   public static Mat4 gtEq ( final Mat4 a, final Mat4 b, final Mat4 target ) {

      return target.set(a.m00 >= b.m00, a.m01 >= b.m01, a.m02 >= b.m02, a.m03
         >= b.m03, a.m10 >= b.m10, a.m11 >= b.m11, a.m12 >= b.m12, a.m13
            >= b.m13, a.m20 >= b.m20, a.m21 >= b.m21, a.m22 >= b.m22, a.m23
               >= b.m23, a.m30 >= b.m30, a.m31 >= b.m31, a.m32 >= b.m32, a.m33
                  >= b.m33);
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
    * Evaluates whether the left comparisand is less than the right
    * comparisand.
    *
    * @param a      left comparisand
    * @param b      right comparisand
    * @param target the output matrix
    *
    * @return the evaluation
    */
   public static Mat4 lt ( final Mat4 a, final Mat4 b, final Mat4 target ) {

      return target.set(a.m00 < b.m00, a.m01 < b.m01, a.m02 < b.m02, a.m03
         < b.m03, a.m10 < b.m10, a.m11 < b.m11, a.m12 < b.m12, a.m13 < b.m13,
         a.m20 < b.m20, a.m21 < b.m21, a.m22 < b.m22, a.m23 < b.m23, a.m30
            < b.m30, a.m31 < b.m31, a.m32 < b.m32, a.m33 < b.m33);
   }

   /**
    * Evaluates whether the left comparisand is less than or equal to the
    * right comparisand.
    *
    * @param a      left comparisand
    * @param b      right comparisand
    * @param target the output matrix
    *
    * @return the evaluation
    */
   public static Mat4 ltEq ( final Mat4 a, final Mat4 b, final Mat4 target ) {

      return target.set(a.m00 <= b.m00, a.m01 <= b.m01, a.m02 <= b.m02, a.m03
         <= b.m03, a.m10 <= b.m10, a.m11 <= b.m11, a.m12 <= b.m12, a.m13
            <= b.m13, a.m20 <= b.m20, a.m21 <= b.m21, a.m22 <= b.m22, a.m23
               <= b.m23, a.m30 <= b.m30, a.m31 <= b.m31, a.m32 <= b.m32, a.m33
                  <= b.m33);
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
    * Multiplies two matrices.
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
    * Multiplies three matrices. Useful for composing an affine transform from
    * translation, rotation and scale matrices.
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
    * Multiplies a matrix and a normal. Calculates the inverse of the matrix.
    *
    * @param n      the normal
    * @param m      the matrix
    * @param h      the inverse
    * @param target the output normal
    *
    * @return the transformed normal
    *
    * @see Mat4#inverse(Mat4, Mat4)
    */
   @Experimental
   public static Vec3 mulNormal ( final Vec3 n, final Mat4 m, final Mat4 h,
      final Vec3 target ) {

      Mat4.inverse(m, h);
      return Mat4.mulNormal(n, h, target);
   }

   /**
    * Multiplies a matrix and a normal. Assumes the inverse of the matrix has
    * already been calculated.
    *
    * @param n      the normal
    * @param h      the inverse
    * @param target the output normal
    *
    * @return the transformed normal
    *
    * @see Mat4#inverse(Mat4, Mat4)
    */
   @Experimental
   public static Vec3 mulNormal ( final Vec3 n, final Mat4 h,
      final Vec3 target ) {

      /*
       * Cf. Eric Lengyel, Foundations of Game Engine Development I.
       * Mathematics, page 106.
       */
      return target.set(n.x * h.m00 + n.y * h.m10 + n.z * h.m20, n.x * h.m01
         + n.y * h.m11 + n.z * h.m21, n.x * h.m02 + n.y * h.m12 + n.z * h.m22);
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

      final float w = a.m30 * b.x + a.m31 * b.y + a.m33;
      if ( w != 0.0f ) {
         final float wInv = 1.0f / w;
         return target.set( ( a.m00 * b.x + a.m01 * b.y + a.m03 ) * wInv,
            ( a.m10 * b.x + a.m11 * b.y + a.m13 ) * wInv, ( a.m20 * b.x + a.m21
               * b.y + a.m23 ) * wInv);
      }
      return target.reset();
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

      final float w = a.m30 * b.x + a.m31 * b.y + a.m32 * b.z + a.m33;
      if ( w != 0.0f ) {
         final float wInv = 1.0f / w;
         return target.set( ( a.m00 * b.x + a.m01 * b.y + a.m02 * b.z + a.m03 )
            * wInv, ( a.m10 * b.x + a.m11 * b.y + a.m12 * b.z + a.m13 ) * wInv,
            ( a.m20 * b.x + a.m21 * b.y + a.m22 * b.z + a.m23 ) * wInv);
      }
      return target.reset();
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

      final float w = a.m30 * b.x + a.m31 * b.y + a.m33;
      if ( w != 0.0f ) {
         final float wInv = 1.0f / w;
         return target.set( ( a.m00 * b.x + a.m01 * b.y ) * wInv, ( a.m10 * b.x
            + a.m11 * b.y ) * wInv, ( a.m20 * b.x + a.m21 * b.y ) * wInv);
      }
      return target.reset();
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

      final float w = a.m30 * b.x + a.m31 * b.y + a.m32 * b.z + a.m33;
      if ( w != 0.0f ) {
         final float wInv = 1.0f / w;
         return target.set( ( a.m00 * b.x + a.m01 * b.y + a.m02 * b.z ) * wInv,
            ( a.m10 * b.x + a.m11 * b.y + a.m12 * b.z ) * wInv, ( a.m20 * b.x
               + a.m21 * b.y + a.m22 * b.z ) * wInv);
      }
      return target.reset();
   }

   /**
    * Negates the input matrix.
    *
    * @param m      the input matrix
    * @param target the output matrix
    *
    * @return the negation
    */
   public static Mat4 negate ( final Mat4 m, final Mat4 target ) {

      /* @formatter:off */
      return target.set(
         -m.m00, -m.m01, -m.m02, -m.m03,
         -m.m10, -m.m11, -m.m12, -m.m13,
         -m.m20, -m.m21, -m.m22, -m.m23,
         -m.m30, -m.m31, -m.m32, -m.m33);
      /* @formatter:on */
   }

   /**
    * Tests to see if all the matrix's components are zero.
    *
    * @param m the input matrix
    *
    * @return the evaluation
    */
   public static boolean none ( final Mat4 m ) {

      return m.m00 == 0.0f && m.m01 == 0.0f && m.m02 == 0.0f && m.m03 == 0.0f
         && m.m10 == 0.0f && m.m11 == 0.0f && m.m12 == 0.0f && m.m13 == 0.0f
         && m.m20 == 0.0f && m.m21 == 0.0f && m.m22 == 0.0f && m.m23 == 0.0f
         && m.m30 == 0.0f && m.m31 == 0.0f && m.m32 == 0.0f && m.m33 == 0.0f;
   }

   /**
    * Evaluates a matrix like a boolean, where n != 0.0 is true.
    *
    * @param m      the input matrix
    * @param target the output matrix
    *
    * @return the truth table opposite
    */
   public static Mat4 not ( final Mat4 m, final Mat4 target ) {

      /* @formatter:off */
      return target.set(
         m.m00 != 0.0f ? 0.0f : 1.0f, m.m01 != 0.0f ? 0.0f : 1.0f,
         m.m02 != 0.0f ? 0.0f : 1.0f, m.m03 != 0.0f ? 0.0f : 1.0f,
         m.m10 != 0.0f ? 0.0f : 1.0f, m.m11 != 0.0f ? 0.0f : 1.0f,
         m.m12 != 0.0f ? 0.0f : 1.0f, m.m13 != 0.0f ? 0.0f : 1.0f,
         m.m20 != 0.0f ? 0.0f : 1.0f, m.m21 != 0.0f ? 0.0f : 1.0f,
         m.m22 != 0.0f ? 0.0f : 1.0f, m.m23 != 0.0f ? 0.0f : 1.0f,
         m.m30 != 0.0f ? 0.0f : 1.0f, m.m31 != 0.0f ? 0.0f : 1.0f,
         m.m32 != 0.0f ? 0.0f : 1.0f, m.m33 != 0.0f ? 0.0f : 1.0f);
      /* @formatter:on */
   }

   /**
    * Evaluates two matrices like booleans, using the OR logic gate.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output matrix
    *
    * @return the evaluation
    *
    * @see Utils#or(float, float)
    */
   public static Mat4 or ( final Mat4 a, final Mat4 b, final Mat4 target ) {

      /* @formatter:off */
      return target.set(
         Utils.or(a.m00, b.m00), Utils.or(a.m01, b.m01),
         Utils.or(a.m02, b.m02), Utils.or(a.m03, b.m03),
         Utils.or(a.m10, b.m10), Utils.or(a.m11, b.m11),
         Utils.or(a.m12, b.m12), Utils.or(a.m13, b.m13),
         Utils.or(a.m20, b.m20), Utils.or(a.m21, b.m21),
         Utils.or(a.m22, b.m22), Utils.or(a.m23, b.m23),
         Utils.or(a.m30, b.m30), Utils.or(a.m31, b.m31),
         Utils.or(a.m32, b.m32), Utils.or(a.m33, b.m33));
      /* @formatter:on */
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
         w + w,  0.0f,        0.0f,  w * ( left + right ),
          0.0f, h + h,        0.0f,  h * ( top + bottom ),
          0.0f,  0.0f, - ( d + d ), -d * ( far + near ),
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
    * Rotates the elements of the input matrix 90 degrees counter-clockwise.
    *
    * @param m      the input matrix
    * @param target the output matrix
    *
    * @return the rotated matrix
    */
   public static Mat4 rotateElmsCcw ( final Mat4 m, final Mat4 target ) {

      /* @formatter:off */
      return target.set(
         m.m03, m.m13, m.m23, m.m33,
         m.m02, m.m12, m.m22, m.m32,
         m.m01, m.m11, m.m21, m.m31,
         m.m00, m.m10, m.m20, m.m30);
      /* @formatter:on */
   }

   /**
    * Rotates the elements of the input matrix 90 degrees clockwise.
    *
    * @param m      the input matrix
    * @param target the output matrix
    *
    * @return the rotated matrix
    */
   public static Mat4 rotateElmsCw ( final Mat4 m, final Mat4 target ) {

      /* @formatter:off */
      return target.set(
         m.m30, m.m20, m.m10, m.m00,
         m.m31, m.m21, m.m11, m.m01,
         m.m32, m.m22, m.m12, m.m02,
         m.m33, m.m23, m.m13, m.m03);
      /* @formatter:on */
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
    * Evaluates two matrices like booleans, using the exclusive or (XOR) logic
    * gate.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output matrix
    *
    * @return the evaluation
    *
    * @see Utils#xor(float, float)
    */
   public static Mat4 xor ( final Mat4 a, final Mat4 b, final Mat4 target ) {

      /* @formatter:off */
      return target.set(
         Utils.xor(a.m00, b.m00), Utils.xor(a.m01, b.m01),
         Utils.xor(a.m02, b.m02), Utils.xor(a.m03, b.m03),
         Utils.xor(a.m10, b.m10), Utils.xor(a.m11, b.m11),
         Utils.xor(a.m12, b.m12), Utils.xor(a.m13, b.m13),
         Utils.xor(a.m20, b.m20), Utils.xor(a.m21, b.m21),
         Utils.xor(a.m22, b.m22), Utils.xor(a.m23, b.m23),
         Utils.xor(a.m30, b.m30), Utils.xor(a.m31, b.m31),
         Utils.xor(a.m32, b.m32), Utils.xor(a.m33, b.m33));
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
