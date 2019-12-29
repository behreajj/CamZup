package camzup.core;

import java.util.Iterator;

public class Mat2 extends Matrix {

   /**
    * An iterator, which allows a matrix's components to be
    * accessed in an enhanced for loop.
    */
   public static final class M2Iterator implements Iterator < Float > {

      /**
       * The current index.
       */
      private int index = 0;

      /**
       * The matrix being iterated over.
       */
      private final Mat2 mtx;

      /**
       * The default constructor.
       *
       * @param mtx
       *           the matrix to iterate
       */
      public M2Iterator ( final Mat2 mtx ) {

         this.mtx = mtx;
      }

      /**
       * Tests to see if the iterator has another value.
       *
       * @return the evaluation
       */
      @Override
      public boolean hasNext () {

         return this.index < this.mtx.size();
      }

      /**
       * Gets the next value in the iterator.
       *
       * @return the value
       * @see Mat3#get(int)
       */
      @Override
      public Float next () {

         return this.mtx.get(this.index++);
      }

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString () {

         return this.getClass().getSimpleName();
      }

   }

   /**
    * The unique identification for serialized classes.
    */
   private static final long serialVersionUID = -1885014730022429740L;

   /**
    * Adds two matrices together.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output matrix
    * @return the sum
    */
   public static Mat2 add (
         final Mat2 a,
         final Mat2 b,
         final Mat2 target ) {

      return target.set(
            a.m00 + b.m00, a.m01 + b.m01,
            a.m10 + b.m10, a.m11 + b.m11);
   }

   /**
    * Returns the identity matrix,<br>
    * <br>
    * 1.0, 0.0,<br>
    * 0.0, 1.0
    *
    * @param target
    *           the output matrix
    * @return the identity matrix
    */
   public static Mat2 identity ( final Mat2 target ) {

      return target.set(
            1.0f, 0.0f,
            0.0f, 1.0f);
   }

   /**
    * Tests to see if a matrix is the identity matrix.
    *
    * @param m
    *           the matrix
    * @return the evaluation
    */
   public static boolean isIdentity ( final Mat2 m ) {

      return m.m11 == 1.0f && m.m00 == 1.0f &&
            m.m01 == 0.0f && m.m10 == 0.0f;
   }
   
   /**
    * Multiplies two matrices by component.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output matrix
    * @return the product
    */
   public static Mat2 mul(
         final Mat2 a,
         final Mat2 b,
         final Mat2 target) {
      
      return target.set(
            a.m00 * b.m00 + a.m01 * b.m10,
            a.m00 * b.m01 + a.m01 * b.m11,

            a.m10 * b.m00 + a.m11 * b.m10,
            a.m10 * b.m01 + a.m11 * b.m11);
   }

   /**
    * Multiplies a matrix and a vector.
    *
    * @param a
    *           the matrix
    * @param b
    *           the vector
    * @param target
    *           the output vector
    * @return the product
    */
   public static Vec2 mul (
         final Mat2 a,
         final Vec2 b,
         final Vec2 target ) {

      return target.set(
            a.m00 * b.x + a.m01 * b.y,
            a.m10 * b.x + a.m11 * b.y);
   }
   
   /**
    * Subtracts the right matrix from the left matrix.
    *
    * @param a
    *           the left operand
    * @param b
    *           the right operand
    * @param target
    *           the output matrix
    * @return the result
    */
   public static Mat2 sub (
         final Mat2 a,
         final Mat2 b,
         final Mat2 target ) {

      return target.set(
            a.m00 - b.m00, a.m01 - b.m01,
            a.m10 - b.m10, a.m11 - b.m11);
   }

   /**
    * Transposes a matrix, switching its row and column
    * indices.
    *
    * @param m
    *           the matrix
    * @param target
    *           the output matrix
    * @return the tranposed matrix
    */
   public static Mat2 transpose (
         final Mat2 m,
         final Mat2 target ) {

      return target.set(
            m.m00, m.m10,
            m.m01, m.m11);
   }

   /**
    * Component in row 0, column 0.
    */
   public float m00 = 1.0f;

   /**
    * Component in row 0, column 1.
    */
   public float m01 = 0.0f;

   /**
    * Component in row 1, column 0. 
    */
   public float m10 = 0.0f;

   /**
    * Component in row 1, column 1.
    */
   public float m11 = 1.0f;

   /**
    * The default constructor. Creates an identity matrix.
    */
   public Mat2 () {

      super(4);
   }

   /**
    * Constructs a matrix from float values.
    *
    * @param m00
    *           row 0, column 0
    * @param m01
    *           row 0, column 1
    * @param m10
    *           row 1, column 0
    * @param m11
    *           row 1, column 1
    */
   public Mat2 (
         final float m00, final float m01,
         final float m10, final float m11 ) {

      super(4);
      this.set(
            m00, m01,
            m10, m11);
   }

   /**
    * Constructs a matrix from a source matrix's components.
    *
    * @param source
    *           the source matrix
    */
   public Mat2 ( final Mat2 source ) {

      super(4);
      this.set(source);
   }

   /**
    * Tests for equivalence between this and another matrix.
    *
    * @param n
    *           the matrix
    * @return the evaluation
    * @see Float#floatToIntBits(float)
    */
   protected boolean equals ( final Mat2 n ) {

      if (Float.floatToIntBits(this.m00) != Float.floatToIntBits(n.m00)) {
         return false;
      }
      if (Float.floatToIntBits(this.m01) != Float.floatToIntBits(n.m01)) {
         return false;
      }

      if (Float.floatToIntBits(this.m10) != Float.floatToIntBits(n.m10)) {
         return false;
      }
      if (Float.floatToIntBits(this.m11) != Float.floatToIntBits(n.m11)) {
         return false;
      }

      return true;
   }

   /**
    * Returns a new matrix with this matrix's components.
    * Java's cloneable interface is problematic; use set or a
    * copy constructor instead.
    *
    * @return a new matrix
    */
   @Override
   public Mat2 clone () {

      return new Mat2(
            this.m00, this.m01,
            this.m10, this.m11);
   }

   /**
    * Tests this matrix for equivalence with another object.
    *
    * @param obj
    *           the object
    * @return the equivalence
    * @see Mat2#equals(Mat2)
    */
   @Override
   public boolean equals ( final Object obj ) {

      if (this == obj) {
         return true;
      }

      if (obj == null) {
         return false;
      }

      if (this.getClass() != obj.getClass()) {
         return false;
      }

      return this.equals((Mat2) obj);
   }

   /**
    * Simulates bracket subscript access in a one-dimensional,
    * row-major matrix array. Works with positive integers in
    * [0, 3] or negative integers in [-4, -1].
    *
    * @param index
    *           the index
    * @return the component at that index
    */
   @Override
   public float get ( final int index ) {

      switch (index) {
         case 0:
         case -4:
            return this.m00;
         case 1:
         case -3:
            return this.m01;
         case 2:
         case -2:
            return this.m10;
         case 3:
         case -1:
            return this.m11;
         default:
            return 0.0f;
      }
   }
   
   /**
    * Simulates bracket subscript access in a two-dimensional,
    * row-major matrix array. Works with positive integers in
    * [0, 1][0, 1] or negative integers in [-2, -1][-2, -1].
    *
    * @param i
    *           the row index
    * @param j
    *           the column index
    * @return the component at that index
    */
   @Override
   public float get ( final int i, final int j ) {

      switch (i) {
         case 0:
         case -2:
            switch (j) {
               case 0:
               case -2:
                  return this.m00;
               case 1:
               case -1:
                  return this.m01;
               default:
                  return 0.0f;
            }
         case 1:
         case -1:
            switch (i) {
               case 0:
               case -2:
                  return this.m10;
               case 1:
               case -1:
                  return this.m11;
               default:
                  return 0.0f;
            }
         default:
            return 0.0f;
      }
   }

   /**
    * Gets a column of this matrix with an index and vector.
    *
    * @param j
    *           the index
    * @param target
    *           the vector
    * @return the column
    */
   public Vec2 getCol ( final int j, final Vec2 target ) {

      switch (j) {
         case 0:
         case -2:
            return target.set(this.m00, this.m10);
         case 1:
         case -1:
            return target.set(this.m01, this.m11);
         default:
            return target.reset();
      }
   }

   /**
    * Returns a hash code for this matrix based on its 16
    * components.
    *
    * @return the hash code
    * @see Float#floatToIntBits(float)
    */
   @Override
   public int hashCode () {

      final int prime = 31;
      int result = 1;

      result = prime * result + Float.floatToIntBits(this.m00);
      result = prime * result + Float.floatToIntBits(this.m01);

      result = prime * result + Float.floatToIntBits(this.m10);
      result = prime * result + Float.floatToIntBits(this.m11);

      return result;
   }

   /**
    * Returns an iterator for this matrix, which allows its
    * components to be accessed in an enhanced for-loop.
    *
    * @return the iterator
    */
   @Override
   public Iterator < Float > iterator () {

      return new M2Iterator(this);
   }

   /**
    * Resets this matrix to an initial state,<br>
    * <br>
    * 1.0, 0.0,<br>
    * 0.0, 1.0
    *
    * @return this matrix
    * @see Mat2#identity(Mat2)
    */
   @Chainable
   public Mat2 reset () {

      return this.set(
            1.0f, 0.0f,
            0.0f, 1.0f);
   }

   /**
    * Sets the components of this matrix.
    *
    * @param m00
    *           row 0, column 0
    * @param m01
    *           row 0, column 1
    * @param m10
    *           row 1, column 0
    * @param m11
    *           row 1, column 1
    * @return this matrix
    */
   @Chainable
   public Mat2 set (
         final float m00, final float m01,
         final float m10, final float m11 ) {

      this.m00 = m00;
      this.m01 = m01;
      this.m10 = m10;
      this.m11 = m11;

      return this;
   }

   /**
    * Copies the components of the input matrix to this matrix.
    *
    * @param source
    *           the input matrix
    * @return this matrix
    */
   @Chainable
   public Mat2 set ( final Mat2 source ) {

      return this.set(
            source.m00, source.m01,
            source.m10, source.m11);
   }

   /**
    * Sets a column of this matrix with an index and vector.
    *
    * @param j
    *           the column index
    * @param source
    *           the column
    * @return this matrix
    */
   @Chainable
   public Mat2 setCol ( final int j, final Vec2 source ) {

      switch (j) {
         case 0:
         case -2:
            this.m00 = source.x;
            this.m10 = source.y;
            return this;
         case 1:
         case -1:
            this.m01 = source.x;
            this.m11 = source.y;
            return this;
         default:
            return this;
      }
   }

   /**
    * Returns a float array of length 4 containing this
    * matrix's components.
    *
    * @return the array
    */
   @Override
   public float[] toArray () {

      return new float[] {
            this.m00, this.m01,
            this.m10, this.m11 };
   }

   /**
    * Returns a string representation of this matrix.
    *
    * @return the string
    */
   @Override
   public String toString () {

      return this.toString(4);
   }

   /**
    * Returns a string representation of this matrix.
    *
    * @param places
    *           number of decimal places
    * @return the string
    */
   public String toString ( final int places ) {

      return new StringBuilder()
            .append("{ m00: ")
            .append(Utils.toFixed(this.m00, places))
            .append(", m01: ")
            .append(Utils.toFixed(this.m01, places))

            .append(", m10: ")
            .append(Utils.toFixed(this.m10, places))
            .append(", m11: ")
            .append(Utils.toFixed(this.m11, places))

            .append(' ')
            .append('}')
            .toString();
   }

   /**
    * Returns a string representation of this matrix, where
    * columns are separated by tabs and rows are separated by
    * new lines.
    *
    * @return the string
    */
   public String toStringTab () {

      return this.toStringTab(4);
   }

   /**
    * Returns a string representation of this matrix intended
    * for display in the console.
    *
    * @param places
    *           number of decimal places
    * @return the string
    */
   public String toStringTab ( final int places ) {

      return new StringBuilder()
            // .append(this.hashIdentityString())
            .append('\n')
            .append(Utils.toFixed(this.m00, places))
            .append(',').append(' ')
            .append(Utils.toFixed(this.m01, places))

            .append(',').append('\n')
            .append(Utils.toFixed(this.m10, places))
            .append(',').append(' ')
            .append(Utils.toFixed(this.m11, places))

            .append('\n')
            .toString();
   }
}
