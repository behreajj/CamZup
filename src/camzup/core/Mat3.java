package camzup.core;

import java.util.Iterator;

/**
 * A mutable, extensible class influenced by GLSL, OSL and Processing's
 * PMatrix2D. Although this is a 3 x 3 matrix, it is generally assumed to
 * be a 2D affine transform matrix, where the last row is (0.0, 0.0, 1.0) .
 * Instance methods are limited, while most static methods require an
 * explicit output variable to be provided.
 */
public class Mat3 implements Iterable < Float > {

   /**
    * Component in row 0, column 0. The right axis x component.
    */
   public float m00 = 1.0f;

   /**
    * Component in row 0, column 1. The forward axis x component.
    */
   public float m01 = 0.0f;

   /**
    * Component in row 0, column 2. The translation x component.
    */
   public float m02 = 0.0f;

   /**
    * Component in row 1, column 0. The right axis y component.
    */
   public float m10 = 0.0f;

   /**
    * Component in row 1, column 1. The forward axis y component.
    */
   public float m11 = 1.0f;

   /**
    * Component in row 1, column 2. The translation y component.
    */
   public float m12 = 0.0f;

   /**
    * Component in row 2, column 0. The right axis z component.
    */
   public float m20 = 0.0f;

   /**
    * Component in row 2, column 1. The forward axis z component.
    */
   public float m21 = 0.0f;

   /**
    * Component in row 2, column 2. The translation z component.
    */
   public float m22 = 1.0f;

   /**
    * The default constructor. Creates an identity matrix.
    */
   public Mat3 ( ) {}

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
   public Mat3 ( final boolean m00, final boolean m01, final boolean m02,
      final boolean m10, final boolean m11, final boolean m12,
      final boolean m20, final boolean m21, final boolean m22 ) {

      this.set(m00, m01, m02, m10, m11, m12, m20, m21, m22);
   }

   /**
    * Constructs a matrix from float values.
    *
    * @param m00 row 0, column 0
    * @param m01 row 0, column 1
    * @param m10 row 1, column 0
    * @param m11 row 1, column 1
    */
   public Mat3 ( final float m00, final float m01, final float m10,
      final float m11 ) {

      this.set(m00, m01, m10, m11);
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
   public Mat3 ( final float m00, final float m01, final float m02,
      final float m10, final float m11, final float m12 ) {

      this.set(m00, m01, m02, m10, m11, m12);
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
   public Mat3 ( final float m00, final float m01, final float m02,
      final float m10, final float m11, final float m12, final float m20,
      final float m21, final float m22 ) {

      this.set(m00, m01, m02, m10, m11, m12, m20, m21, m22);
   }

   /**
    * Constructs a matrix from a source matrix's components.
    *
    * @param source the source matrix
    */
   public Mat3 ( final Mat3 source ) { this.set(source); }

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
      if ( Utils.approx(this.m10, v) ) { return true; }
      if ( Utils.approx(this.m11, v) ) { return true; }
      if ( Utils.approx(this.m12, v) ) { return true; }
      if ( Utils.approx(this.m20, v) ) { return true; }
      if ( Utils.approx(this.m21, v) ) { return true; }
      return Utils.approx(this.m22, v);
   }

   /**
    * Tests this matrix for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the equivalence
    *
    * @see Mat3#equals(Mat3)
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null ) { return false; }
      if ( this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Mat3 ) obj);
   }

   /**
    * Gets a column of this matrix with an index and vector.
    *
    * @param j      the index
    * @param target the vector
    *
    * @return the column
    */
   public Vec3 getCol ( final int j, final Vec3 target ) {

      switch ( j ) {
         case 0:
         case -3:
            return target.set(this.m00, this.m10, this.m20);

         case 1:
         case -2:
            return target.set(this.m01, this.m11, this.m21);

         case 2:
         case -1:
            return target.set(this.m02, this.m12, this.m22);

         default:
            return target.reset();
      }
   }

   /**
    * Simulates bracket subscript access in a one-dimensional, row-major
    * matrix array. Works with positive integers in [0, 8] or negative
    * integers in [-9, -1] .
    *
    * @param index the index
    *
    * @return the component at that index
    */
   public float getElm ( final int index ) {

      /* @formatter:off */
      switch ( index ) {

         case 0: case -9: return this.m00;
         case 1: case -8: return this.m01;
         case 2: case -7: return this.m02;

         case 3: case -6: return this.m10;
         case 4: case -5: return this.m11;
         case 5: case -4: return this.m12;

         case 6: case -3: return this.m20;
         case 7: case -2: return this.m21;
         case 8: case -1: return this.m22;

         default: return 0.0f;
      }
      /* @formatter:on */
   }

   /**
    * Simulates bracket subscript access in a two-dimensional, row-major
    * matrix array. Works with positive integers in [0, 2][0, 2] or negative
    * integers in [-3, -1][-3, -1] .
    *
    * @param i the row index
    * @param j the column index
    *
    * @return the component at that index
    */
   public float getElm ( final int i, final int j ) {

      /* @formatter:off */
      switch ( i ) {

         case 0: case -3:

            switch ( j ) {
               case 0: case -3: return this.m00;
               case 1: case -2: return this.m01;
               case 2: case -1: return this.m02;
               default: return 0.0f;
            }

         case 1: case -2:

            switch ( j ) {
               case 0: case -3: return this.m10;
               case 1: case -2: return this.m11;
               case 2: case -1: return this.m12;
               default: return 0.0f;
            }

         case 2: case -1:

            switch ( j ) {
               case 0: case -3: return this.m20;
               case 1: case -2: return this.m21;
               case 2: case -1: return this.m22;
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

      hash = hash * IUtils.HASH_MUL ^ Float.floatToIntBits(this.m10);
      hash = hash * IUtils.HASH_MUL ^ Float.floatToIntBits(this.m11);
      hash = hash * IUtils.HASH_MUL ^ Float.floatToIntBits(this.m12);

      hash = hash * IUtils.HASH_MUL ^ Float.floatToIntBits(this.m20);
      hash = hash * IUtils.HASH_MUL ^ Float.floatToIntBits(this.m21);
      hash = hash * IUtils.HASH_MUL ^ Float.floatToIntBits(this.m22);

      return hash;
   }

   /**
    * Returns an iterator for this matrix, which allows its components to be
    * accessed in an enhanced for-loop.
    *
    * @return the iterator
    */
   @Override
   public Iterator < Float > iterator ( ) { return new M3Iterator(this); }

   /**
    * Returns the number of elements in the matrix.
    *
    * @return the length
    */
   public int length ( ) { return 9; }

   /**
    * Resets this matrix to an initial state,
    *
    * <pre>
    * 1.0, 0.0, 0.0,
    * 0.0, 1.0, 0.0,
    * 0.0, 0.0, 1.0
    * </pre>
    *
    * @return this matrix
    *
    * @see Mat3#identity(Mat3)
    */
   public Mat3 reset ( ) {

      return this.set(1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Sets the components of this matrix.
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
   public Mat3 set ( final boolean m00, final boolean m01, final boolean m02,
      final boolean m10, final boolean m11, final boolean m12,
      final boolean m20, final boolean m21, final boolean m22 ) {

      this.m00 = m00 ? 1.0f : 0.0f;
      this.m01 = m01 ? 1.0f : 0.0f;
      this.m02 = m02 ? 1.0f : 0.0f;

      this.m10 = m10 ? 1.0f : 0.0f;
      this.m11 = m11 ? 1.0f : 0.0f;
      this.m12 = m12 ? 1.0f : 0.0f;

      this.m20 = m20 ? 1.0f : 0.0f;
      this.m21 = m21 ? 1.0f : 0.0f;
      this.m22 = m22 ? 1.0f : 0.0f;

      return this;
   }

   /**
    * Sets the two axis columns of this matrix. The last row and column are
    * set to (0.0, 0.0, 1.0) .
    *
    * @param m00 row 0, column 0
    * @param m01 row 0, column 1
    * @param m10 row 1, column 0
    * @param m11 row 1, column 1
    *
    * @return this matrix
    */
   public Mat3 set ( final float m00, final float m01, final float m10,
      final float m11 ) {

      return this.set(m00, m01, 0.0f, m10, m11, 0.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Sets the upper two rows of this matrix. The last row is set to (0.0,
    * 0.0, 1.0) .
    *
    * @param m00 row 0, column 0
    * @param m01 row 0, column 1
    * @param m02 row 0, column 2
    * @param m10 row 1, column 0
    * @param m11 row 1, column 1
    * @param m12 row 1, column 2
    *
    * @return this matrix
    */
   public Mat3 set ( final float m00, final float m01, final float m02,
      final float m10, final float m11, final float m12 ) {

      return this.set(m00, m01, m02, m10, m11, m12, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Sets the components of this matrix.
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
   public Mat3 set ( final float m00, final float m01, final float m02,
      final float m10, final float m11, final float m12, final float m20,
      final float m21, final float m22 ) {

      /* @formatter:off */
      this.m00 = m00; this.m10 = m10; this.m20 = m20;
      this.m01 = m01; this.m11 = m11; this.m21 = m21;
      this.m02 = m02; this.m12 = m12; this.m22 = m22;
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
    * @see Mat3#setCol(int, Vec2)
    */
   public void set ( final int j, final Vec2 source ) {

      this.setCol(j, source);
   }

   /**
    * Sets a column of this matrix with an index and vector. For
    * interoperability with Kotlin: <code>a[i] = b</code> .
    *
    * @param j      the column index
    * @param source the source vector
    *
    * @see Mat3#setCol(int, Vec3)
    */
   public void set ( final int j, final Vec3 source ) {

      this.setCol(j, source);
   }

   /**
    * Copies the components of the input matrix to this matrix.
    *
    * @param source the input matrix
    *
    * @return this matrix
    */
   public Mat3 set ( final Mat3 source ) {

      return this.set(source.m00, source.m01, source.m02, source.m10,
         source.m11, source.m12, source.m20, source.m21, source.m22);
   }

   /**
    * Sets a column of this matrix with an index and vector. If the column is
    * an axis vector, the w or z component is set to 0.0; if it is a
    * translation, the w or z component is set to 1.0.
    *
    * @param j      the column index
    * @param source the column
    *
    * @return this matrix
    */
   public Mat3 setCol ( final int j, final Vec2 source ) {

      switch ( j ) {
         case 0:
         case -3:

            /* Right axis. */
            this.m00 = source.x;
            this.m10 = source.y;
            this.m20 = 0.0f;
            return this;

         case 1:
         case -2:

            /* Forward axis. */
            this.m01 = source.x;
            this.m11 = source.y;
            this.m21 = 0.0f;
            return this;

         case 2:
         case -1:

            /* Translation. */
            this.m02 = source.x;
            this.m12 = source.y;
            this.m22 = 1.0f;
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
   public Mat3 setCol ( final int j, final Vec3 source ) {

      switch ( j ) {
         case 0:
         case -3:

            /* Right axis. */
            this.m00 = source.x;
            this.m10 = source.y;
            this.m20 = source.z;
            return this;

         case 1:
         case -2:

            /* Forward axis. */
            this.m01 = source.x;
            this.m11 = source.y;
            this.m21 = source.z;
            return this;

         case 2:
         case -1:

            /* Translation. */
            this.m02 = source.x;
            this.m12 = source.y;
            this.m22 = source.z;
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

      return new float[] { this.m00, this.m01, this.m02, this.m10, this.m11,
         this.m12, this.m20, this.m21, this.m22 };
   }

   /**
    * Returns a 2D float array containing this matrix's components.
    *
    * @return the array
    */
   public float[][] toArray2 ( ) {

      return new float[][] { { this.m00, this.m01, this.m02 }, { this.m10,
         this.m11, this.m12 }, { this.m20, this.m21, this.m22 } };
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

      final StringBuilder sb = new StringBuilder(128);
      sb.append("{ m00: ");
      sb.append(Utils.toFixed(this.m00, places));
      sb.append(", m01: ");
      sb.append(Utils.toFixed(this.m01, places));
      sb.append(", m02: ");
      sb.append(Utils.toFixed(this.m02, places));
      sb.append(", m10: ");
      sb.append(Utils.toFixed(this.m10, places));
      sb.append(", m11: ");
      sb.append(Utils.toFixed(this.m11, places));
      sb.append(", m12: ");
      sb.append(Utils.toFixed(this.m12, places));
      sb.append(", m20: ");
      sb.append(Utils.toFixed(this.m20, places));
      sb.append(", m21: ");
      sb.append(Utils.toFixed(this.m21, places));
      sb.append(", m22: ");
      sb.append(Utils.toFixed(this.m22, places));
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
      return new StringBuilder(128)
         .append('\n')
                             .append(Utils.toFixed(this.m00, p))
         .append(s).append(t).append(Utils.toFixed(this.m01, p))
         .append(s).append(t).append(Utils.toFixed(this.m02, p))
         .append(s)

                   .append(n).append(Utils.toFixed(this.m10, p))
         .append(s).append(t).append(Utils.toFixed(this.m11, p))
         .append(s).append(t).append(Utils.toFixed(this.m12, p))
         .append(s)

                   .append(n).append(Utils.toFixed(this.m20, p))
         .append(s).append(t).append(Utils.toFixed(this.m21, p))
         .append(s).append(t).append(Utils.toFixed(this.m22, p))

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
   protected boolean equals ( final Mat3 n ) {

      /* With {@link Float.floatToIntBits(float)}, -0.0f != 0.0f. */
      return this.m00 == n.m00 && this.m01 == n.m01 && this.m02 == n.m02
         && this.m10 == n.m10 && this.m11 == n.m11 && this.m12 == n.m12
         && this.m20 == n.m20 && this.m21 == n.m21 && this.m22 == n.m22;
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
   public static Mat3 add ( final Mat3 a, final Mat3 b, final Mat3 target ) {

      /* @formatter:off */
      return target.set(
         a.m00 + b.m00, a.m01 + b.m01, a.m02 + b.m02,
         a.m10 + b.m10, a.m11 + b.m11, a.m12 + b.m12,
         a.m20 + b.m20, a.m21 + b.m21, a.m22 + b.m22);
      /* @formatter:on */
   }

   /**
    * Tests to see if all the matrix's components are non-zero.
    *
    * @param m the input matrix
    *
    * @return the evaluation
    */
   public static boolean all ( final Mat3 m ) {

      return m.m00 != 0.0f && m.m01 != 0.0f && m.m02 != 0.0f && m.m10 != 0.0f
         && m.m11 != 0.0f && m.m12 != 0.0f && m.m20 != 0.0f && m.m21 != 0.0f
         && m.m22 != 0.0f;
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
   public static Mat3 and ( final Mat3 a, final Mat3 b, final Mat3 target ) {

      return target.set(Utils.and(a.m00, b.m00), Utils.and(a.m01, b.m01), Utils
         .and(a.m02, b.m02), Utils.and(a.m10, b.m10), Utils.and(a.m11, b.m11),
         Utils.and(a.m12, b.m12), Utils.and(a.m20, b.m20), Utils.and(a.m21,
            b.m21), Utils.and(a.m22, b.m22));
   }

   /**
    * Tests to see if any of the matrix's components are non-zero.
    *
    * @param m the input matrix
    *
    * @return the evaluation
    */
   public static boolean any ( final Mat3 m ) {

      return m.m00 != 0.0f || m.m01 != 0.0f || m.m02 != 0.0f || m.m10 != 0.0f
         || m.m11 != 0.0f || m.m12 != 0.0f || m.m20 != 0.0f || m.m21 != 0.0f
         || m.m22 != 0.0f;
   }

   /**
    * Decomposes a matrix into its constituent transforms: translation,
    * rotation and scale. Rotation is returned from the function, while
    * translation and scale are loaded into out parameters.
    *
    * @param m     the matrix
    * @param trans the output translation
    * @param scale the output scale
    *
    * @return the rotation
    */
   public static float decompose ( final Mat3 m, final Vec2 trans,
      final Vec2 scale ) {

      final float xMag = Utils.hypot(m.m00, m.m10);
      final float yMag = Utils.hypot(m.m01, m.m11);
      final float det = Mat3.determinant(m);
      scale.set(xMag, det < 0.0f ? -yMag : yMag);
      trans.set(m.m02, m.m12);
      return Utils.modRadians(( float ) Math.atan2(m.m10, m.m00));
   }

   /**
    * Finds the determinant of the matrix.
    *
    * @param m the matrix
    *
    * @return the determinant
    */
   public static float determinant ( final Mat3 m ) {

      /* @formatter:off */
      return m.m00 * ( m.m22 * m.m11 - m.m12 * m.m21 ) +
             m.m01 * ( m.m12 * m.m20 - m.m22 * m.m10 ) +
             m.m02 * ( m.m21 * m.m10 - m.m11 * m.m20 );
      /* @formatter:on */
   }

   /**
    * Creates a matrix from two axes. The third row and column are assumed to
    * be (0.0, 0.0, 1.0) .
    *
    * @param right   the right axis
    * @param forward the forward axis
    * @param target  the output matrix
    *
    * @return the matrix
    */
   public static Mat3 fromAxes ( final Vec2 right, final Vec2 forward,
      final Mat3 target ) {

      return target.set(right.x, forward.x, 0.0f, right.y, forward.y, 0.0f,
         0.0f, 0.0f, 1.0f);
   }

   /**
    * Creates a matrix from two axes and a translation. The third row, z or w,
    * is assumed to be (0.0, 0.0, 1.0) .
    *
    * @param right       the right axis
    * @param forward     the forward axis
    * @param translation the translation
    * @param target      the output matrix
    *
    * @return the matrix
    */
   public static Mat3 fromAxes ( final Vec2 right, final Vec2 forward,
      final Vec2 translation, final Mat3 target ) {

      return target.set(right.x, forward.x, translation.x, right.y, forward.y,
         translation.y, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Creates a matrix from two axes. The third column, translation, is
    * assumed to be (0.0, 0.0, 1.0) .
    *
    * @param right   the right axis
    * @param forward the forward axis
    * @param target  the output matrix
    *
    * @return the matrix
    */
   public static Mat3 fromAxes ( final Vec3 right, final Vec3 forward,
      final Mat3 target ) {

      return target.set(right.x, forward.x, 0.0f, right.y, forward.y, 0.0f,
         right.z, forward.z, 1.0f);
   }

   /**
    * Creates a matrix from two axes and a translation.
    *
    * @param right       the right axis
    * @param forward     the forward axis
    * @param translation the translation
    * @param target      the output matrix
    *
    * @return the matrix
    */
   public static Mat3 fromAxes ( final Vec3 right, final Vec3 forward,
      final Vec3 translation, final Mat3 target ) {

      return target.set(right.x, forward.x, translation.x, right.y, forward.y,
         translation.y, right.z, forward.z, translation.z);
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
   public static Mat3 fromRotZ ( final float cosa, final float sina,
      final Mat3 target ) {

      return target.set(cosa, -sina, 0.0f, sina, cosa, 0.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Creates a rotation matrix from an angle in radians around the z axis.
    *
    * @param radians the angle
    * @param target  the output matrix
    *
    * @return the matrix
    */
   public static Mat3 fromRotZ ( final float radians, final Mat3 target ) {

      final float norm = radians * IUtils.ONE_TAU;
      return Mat3.fromRotZ(Utils.scNorm(norm), Utils.scNorm(norm - 0.25f),
         target);
   }

   /**
    * Creates a scale matrix from a scalar. The bottom right corner, m22, is
    * set to 1.0 .
    *
    * @param scalar the scalar
    * @param target the output matrix
    *
    * @return the matrix
    */
   public static Mat3 fromScale ( final float scalar, final Mat3 target ) {

      if ( scalar != 0.0f ) {
         return target.set(scalar, 0.0f, 0.0f, 0.0f, scalar, 0.0f, 0.0f, 0.0f,
            1.0f);
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
   public static Mat3 fromScale ( final Vec2 scalar, final Mat3 target ) {

      return Mat3.fromScale(scalar.x, scalar.y, target);
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
   public static Mat3 fromSkew ( final float radians, final Vec2 a,
      final Vec2 b, final Mat3 target ) {

      final float t = Utils.tan(radians);
      final float tax = a.x * t;
      final float tay = a.y * t;

      return target.set(tax * b.x + 1.0f, tax * b.y, 0.0f, tay * b.x, tay * b.y
         + 1.0f, 0.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Creates a translation matrix from a vector.
    *
    * @param tr     the translation
    * @param target the output matrix
    *
    * @return the matrix
    */
   public static Mat3 fromTranslation ( final Vec2 tr, final Mat3 target ) {

      return Mat3.fromTranslation(tr.x, tr.y, target);
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
   public static Mat3 gt ( final Mat3 a, final Mat3 b, final Mat3 target ) {

      return target.set(a.m00 > b.m00, a.m01 > b.m01, a.m02 > b.m02, a.m10
         > b.m10, a.m11 > b.m11, a.m12 > b.m12, a.m20 > b.m20, a.m21 > b.m21,
         a.m22 > b.m22);
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
   public static Mat3 gtEq ( final Mat3 a, final Mat3 b, final Mat3 target ) {

      return target.set(a.m00 >= b.m00, a.m01 >= b.m01, a.m02 >= b.m02, a.m10
         >= b.m10, a.m11 >= b.m11, a.m12 >= b.m12, a.m20 >= b.m20, a.m21
            >= b.m21, a.m22 >= b.m22);
   }

   /**
    * Returns the identity matrix,
    *
    * <pre>
    * 1.0, 0.0, 0.0,
    * 0.0, 1.0, 0.0,
    * 0.0, 0.0, 1.0
    * </pre>
    *
    * @param target the output matrix
    *
    * @return the identity matrix
    */
   public static Mat3 identity ( final Mat3 target ) {

      return target.set(1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Inverts the input matrix.
    *
    * @param m      the matrix
    * @param target the output matrix
    *
    * @return the inverse
    */
   public static Mat3 inverse ( final Mat3 m, final Mat3 target ) {

      final float b01 = m.m22 * m.m11 - m.m12 * m.m21;
      final float b11 = m.m12 * m.m20 - m.m22 * m.m10;
      final float b21 = m.m21 * m.m10 - m.m11 * m.m20;

      final float det = m.m00 * b01 + m.m01 * b11 + m.m02 * b21;

      /* @formatter:off */
      if ( det != 0.0f ) {
         final float detInv = 1.0f / det;
         return target.set(
            b01 * detInv,
            ( m.m02 * m.m21 - m.m22 * m.m01 ) * detInv,
            ( m.m12 * m.m01 - m.m02 * m.m11 ) * detInv,
            b11 * detInv,
            ( m.m22 * m.m00 - m.m02 * m.m20 ) * detInv,
            ( m.m02 * m.m10 - m.m12 * m.m00 ) * detInv,
            b21 * detInv,
            ( m.m01 * m.m20 - m.m21 * m.m00 ) * detInv,
            ( m.m11 * m.m00 - m.m01 * m.m10 ) * detInv);
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
   public static boolean isIdentity ( final Mat3 m ) {

      /* @formatter:off */
      return m.m22 == 1.0f && m.m11 == 1.0f && m.m00 == 1.0f
          && m.m01 == 0.0f && m.m02 == 0.0f && m.m10 == 0.0f
          && m.m12 == 0.0f && m.m20 == 0.0f && m.m21 == 0.0f;
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
   public static Mat3 lt ( final Mat3 a, final Mat3 b, final Mat3 target ) {

      return target.set(a.m00 < b.m00, a.m01 < b.m01, a.m02 < b.m02, a.m10
         < b.m10, a.m11 < b.m11, a.m12 < b.m12, a.m20 < b.m20, a.m21 < b.m21,
         a.m22 < b.m22);
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
   public static Mat3 ltEq ( final Mat3 a, final Mat3 b, final Mat3 target ) {

      return target.set(a.m00 <= b.m00, a.m01 <= b.m01, a.m02 <= b.m02, a.m10
         <= b.m10, a.m11 <= b.m11, a.m12 <= b.m12, a.m20 <= b.m20, a.m21
            <= b.m21, a.m22 <= b.m22);
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
   public static Mat3 mul ( final float a, final Mat3 b, final Mat3 target ) {

      return target.set(a * b.m00, a * b.m01, a * b.m02, a * b.m10, a * b.m11, a
         * b.m12, a * b.m20, a * b.m21, a * b.m22);
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
   public static Mat3 mul ( final Mat3 a, final float b, final Mat3 target ) {

      return target.set(a.m00 * b, a.m01 * b, a.m02 * b, a.m10 * b, a.m11 * b,
         a.m12 * b, a.m20 * b, a.m21 * b, a.m22 * b);
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
   public static Mat3 mul ( final Mat3 a, final Mat3 b, final Mat3 target ) {

      /* @formatter:off */
      return target.set(
         a.m00 * b.m00 + a.m01 * b.m10 + a.m02 * b.m20,
         a.m00 * b.m01 + a.m01 * b.m11 + a.m02 * b.m21,
         a.m00 * b.m02 + a.m01 * b.m12 + a.m02 * b.m22,

         a.m10 * b.m00 + a.m11 * b.m10 + a.m12 * b.m20,
         a.m10 * b.m01 + a.m11 * b.m11 + a.m12 * b.m21,
         a.m10 * b.m02 + a.m11 * b.m12 + a.m12 * b.m22,

         a.m20 * b.m00 + a.m21 * b.m10 + a.m22 * b.m20,
         a.m20 * b.m01 + a.m21 * b.m11 + a.m22 * b.m21,
         a.m20 * b.m02 + a.m21 * b.m12 + a.m22 * b.m22);
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
   public static Mat3 mul ( final Mat3 a, final Mat3 b, final Mat3 c,
      final Mat3 target ) {

      final float n00 = a.m00 * b.m00 + a.m01 * b.m10 + a.m02 * b.m20;
      final float n01 = a.m00 * b.m01 + a.m01 * b.m11 + a.m02 * b.m21;
      final float n02 = a.m00 * b.m02 + a.m01 * b.m12 + a.m02 * b.m22;

      final float n10 = a.m10 * b.m00 + a.m11 * b.m10 + a.m12 * b.m20;
      final float n11 = a.m10 * b.m01 + a.m11 * b.m11 + a.m12 * b.m21;
      final float n12 = a.m10 * b.m02 + a.m11 * b.m12 + a.m12 * b.m22;

      final float n20 = a.m20 * b.m00 + a.m21 * b.m10 + a.m22 * b.m20;
      final float n21 = a.m20 * b.m01 + a.m21 * b.m11 + a.m22 * b.m21;
      final float n22 = a.m20 * b.m02 + a.m21 * b.m12 + a.m22 * b.m22;

      /* @formatter:off */
      return target.set(
         n00 * c.m00 + n01 * c.m10 + n02 * c.m20,
         n00 * c.m01 + n01 * c.m11 + n02 * c.m21,
         n00 * c.m02 + n01 * c.m12 + n02 * c.m22,

         n10 * c.m00 + n11 * c.m10 + n12 * c.m20,
         n10 * c.m01 + n11 * c.m11 + n12 * c.m21,
         n10 * c.m02 + n11 * c.m12 + n12 * c.m22,

         n20 * c.m00 + n21 * c.m10 + n22 * c.m20,
         n20 * c.m01 + n21 * c.m11 + n22 * c.m21,
         n20 * c.m02 + n21 * c.m12 + n22 * c.m22);
      /* @formatter:on */
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
   public static Vec3 mul ( final Mat3 a, final Vec3 b, final Vec3 target ) {

      return target.set(a.m00 * b.x + a.m01 * b.y + a.m02 * b.z, a.m10 * b.x
         + a.m11 * b.y + a.m12 * b.z, a.m20 * b.x + a.m21 * b.y + a.m22 * b.z);
   }

   /**
    * Multiplies a matrix and a point. The z component of the point is assumed
    * to be 1.0, so the point is impacted by the matrix's translation.
    *
    * @param a      the matrix
    * @param b      the point
    * @param target the output point
    *
    * @return the product
    */
   @Experimental
   public static Vec2 mulPoint ( final Mat3 a, final Vec2 b,
      final Vec2 target ) {

      final float w = a.m20 * b.x + a.m21 * b.y + a.m22;
      if ( w != 0.0f ) {
         final float wInv = 1.0f / w;
         return target.set( ( a.m00 * b.x + a.m01 * b.y + a.m02 ) * wInv,
            ( a.m10 * b.x + a.m11 * b.y + a.m12 ) * wInv);
      }
      return target.reset();
   }

   /**
    * Multiplies a matrix and a vector. The z component of the vector is
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
   public static Vec2 mulVector ( final Mat3 a, final Vec2 b,
      final Vec2 target ) {

      final float w = a.m20 * b.x + a.m21 * b.y + a.m22;
      if ( w != 0.0f ) {
         final float wInv = 1.0f / w;
         return target.set( ( a.m00 * b.x + a.m01 * b.y ) * wInv, ( a.m10 * b.x
            + a.m11 * b.y ) * wInv);
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
   public static Mat3 negate ( final Mat3 m, final Mat3 target ) {

      return target.set(-m.m00, -m.m01, -m.m02, -m.m10, -m.m11, -m.m12, -m.m20,
         -m.m21, -m.m22);
   }

   /**
    * Tests to see if all the matrix's components are zero.
    *
    * @param m the input matrix
    *
    * @return the evaluation
    */
   public static boolean none ( final Mat3 m ) {

      return m.m00 == 0.0f && m.m01 == 0.0f && m.m02 == 0.0f && m.m10 == 0.0f
         && m.m11 == 0.0f && m.m12 == 0.0f && m.m20 == 0.0f && m.m21 == 0.0f
         && m.m22 == 0.0f;
   }

   /**
    * Evaluates a matrix like a boolean, where n != 0.0 is true.
    *
    * @param m      the input matrix
    * @param target the output matrix
    *
    * @return the truth table opposite
    */
   public static Mat3 not ( final Mat3 m, final Mat3 target ) {

      return target.set(m.m00 != 0.0f ? 0.0f : 1.0f, m.m01 != 0.0f ? 0.0f
         : 1.0f, m.m02 != 0.0f ? 0.0f : 1.0f, m.m10 != 0.0f ? 0.0f : 1.0f, m.m11
            != 0.0f ? 0.0f : 1.0f, m.m12 != 0.0f ? 0.0f : 1.0f, m.m20 != 0.0f
               ? 0.0f : 1.0f, m.m21 != 0.0f ? 0.0f : 1.0f, m.m22 != 0.0f ? 0.0f
                  : 1.0f);
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
   public static Mat3 or ( final Mat3 a, final Mat3 b, final Mat3 target ) {

      return target.set(Utils.or(a.m00, b.m00), Utils.or(a.m01, b.m01), Utils
         .or(a.m02, b.m02), Utils.or(a.m10, b.m10), Utils.or(a.m11, b.m11),
         Utils.or(a.m12, b.m12), Utils.or(a.m20, b.m20), Utils.or(a.m21, b.m21),
         Utils.or(a.m22, b.m22));
   }

   /**
    * Rotates the elements of the input matrix 90 degrees counter-clockwise.
    *
    * @param m      the input matrix
    * @param target the output matrix
    *
    * @return the rotated matrix
    */
   public static Mat3 rotateElmsCcw ( final Mat3 m, final Mat3 target ) {

      return target.set(m.m02, m.m12, m.m22, m.m01, m.m11, m.m21, m.m00, m.m10,
         m.m20);
   }

   /**
    * Rotates the elements of the input matrix 90 degrees clockwise.
    *
    * @param m      the input matrix
    * @param target the output matrix
    *
    * @return the rotated matrix
    */
   public static Mat3 rotateElmsCw ( final Mat3 m, final Mat3 target ) {

      return target.set(m.m20, m.m10, m.m00, m.m21, m.m11, m.m01, m.m22, m.m12,
         m.m02);
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
   public static Mat3 sub ( final Mat3 a, final Mat3 b, final Mat3 target ) {

      /* @formatter:off */
      return target.set(
         a.m00 - b.m00, a.m01 - b.m01, a.m02 - b.m02,
         a.m10 - b.m10, a.m11 - b.m11, a.m12 - b.m12,
         a.m20 - b.m20, a.m21 - b.m21, a.m22 - b.m22);
      /* @formatter:on */
   }

   /**
    * Transposes a matrix, switching its row and column elements.
    *
    * @param m      the matrix
    * @param target the output matrix
    *
    * @return the transposed matrix
    */
   public static Mat3 transpose ( final Mat3 m, final Mat3 target ) {

      return target.set(m.m00, m.m10, m.m20, m.m01, m.m11, m.m21, m.m02, m.m12,
         m.m22);
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
   public static Mat3 xor ( final Mat3 a, final Mat3 b, final Mat3 target ) {

      return target.set(Utils.xor(a.m00, b.m00), Utils.xor(a.m01, b.m01), Utils
         .xor(a.m02, b.m02), Utils.xor(a.m10, b.m10), Utils.xor(a.m11, b.m11),
         Utils.xor(a.m12, b.m12), Utils.xor(a.m20, b.m20), Utils.xor(a.m21,
            b.m21), Utils.xor(a.m22, b.m22));
   }

   /**
    * Creates a scale matrix from a nonuniform scalar.<br>
    * <br>
    * A package level function that uses loose floats to facilitate parsing of
    * SVG transforms.
    *
    * @param sx     the x scalar
    * @param sy     the y scalar
    * @param target the output matrix
    *
    * @return the matrix
    */
   static Mat3 fromScale ( final float sx, final float sy, final Mat3 target ) {

      if ( sx != 0.0f && sy != 0.0f ) {
         return target.set(sx, 0.0f, 0.0f, 0.0f, sy, 0.0f, 0.0f, 0.0f, 1.0f);
      }
      return target.reset();
   }

   /**
    * Creates a horizontal skew matrix from an angle in radians.<br>
    * <br>
    * A package level function that uses loose floats to facilitate parsing of
    * SVG transforms.
    *
    * @param radians the angle
    * @param target  the output matrix
    *
    * @return the skew matrix
    */
   static Mat3 fromSkewX ( final float radians, final Mat3 target ) {

      return target.set(1.0f, Utils.tan(radians), 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
         0.0f, 1.0f);
   }

   /**
    * Creates a vertical skew matrix from an angle in radians.<br>
    * <br>
    * A package level function that uses loose floats to facilitate parsing of
    * SVG transforms.
    *
    * @param radians the angle
    * @param target  the output matrix
    *
    * @return the skew matrix
    */
   static Mat3 fromSkewY ( final float radians, final Mat3 target ) {

      return target.set(1.0f, 0.0f, 0.0f, Utils.tan(radians), 1.0f, 0.0f, 0.0f,
         0.0f, 1.0f);
   }

   /**
    * Creates a translation matrix from a vector.<br>
    * <br>
    * A package level function that uses loose floats to facilitate parsing of
    * SVG transforms.
    *
    * @param tx     the translation x
    * @param ty     the translation y
    * @param target the output matrix
    *
    * @return the matrix
    */
   static Mat3 fromTranslation ( final float tx, final float ty,
      final Mat3 target ) {

      return target.set(1.0f, 0.0f, tx, 0.0f, 1.0f, ty, 0.0f, 0.0f, 1.0f);
   }

   /**
    * An iterator, which allows a matrix's components to be accessed in an
    * enhanced for loop.
    */
   public static final class M3Iterator implements Iterator < Float > {

      /**
       * The current index.
       */
      private int index = 0;

      /**
       * The matrix being iterated over.
       */
      private final Mat3 mtx;

      /**
       * The default constructor.
       *
       * @param mtx the matrix to iterate
       */
      public M3Iterator ( final Mat3 mtx ) { this.mtx = mtx; }

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
       * @see Mat3#getElm(int)
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
