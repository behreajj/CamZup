package camzup.core;

/**
 * Structures the indices used by a 3D mesh to refer to arrays of data from
 * a vertex. Includes indices for a coordinate, and a texture coordinate.
 */
@Experimental
class Index2 implements Comparable < Index2 > {

   /**
    * The coordinate index.
    */
   int v = 0;

   /**
    * The texture coordinate index.
    */
   int vt = 0;

   /**
    * The default constructor.
    */
   public Index2 ( ) {}

   /**
    * Constructs an indices set from a source's components.
    *
    * @param source the source indices
    */
   public Index2 ( final Index2 source ) { this.set(source); }

   /**
    * Constructs an indices set from components.
    *
    * @param vIdx  the coordinate index
    * @param vtIdx the texture coordinate index
    */
   public Index2 ( final int vIdx, final int vtIdx ) {

      this.set(vIdx, vtIdx);
   }

   /**
    * Returns -1 when this indices set is less than the comparisand; 1 when it
    * is greater than; 0 when the two are 'equal'. The implementation of this
    * method allows collections of indices sets to be sorted.
    *
    * @param idx the comparisand
    *
    * @return the numeric code
    */
   @Override
   public int compareTo ( final Index2 idx ) {

      final int a = this.hashCode();
      final int b = idx.hashCode();
      return a < b ? -1 : a > b ? 1 : 0;
   }

   /**
    * Tests this indices set for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the equivalence
    *
    * @see Index2#equals(Index2)
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null || this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Index2 ) obj);
   }

   /**
    * Returns a hash code for this indices set based on its components.
    *
    * @return the hash code
    */
   @Override
   public int hashCode ( ) {

      return ( IUtils.MUL_BASE ^ this.v ) * IUtils.HASH_MUL ^ this.vt;
   }

   /**
    * Copies the components of the input indices set to this one.
    *
    * @param source the input set
    *
    * @return this set
    */
   public Index2 set ( final Index2 source ) {

      return this.set(source.v, source.vt);
   }

   /**
    * Sets the components of this indices set.
    *
    * @param vIdx  the coordinate index
    * @param vtIdx the texture coordinate index
    *
    * @return this indices set
    */
   public Index2 set ( final int vIdx, final int vtIdx ) {

      this.v = vIdx;
      this.vt = vtIdx;
      return this;
   }

   /**
    * Returns an integer array of length 2 containing this indices set's
    * components.
    *
    * @return the array
    */
   public int[] toArray ( ) { return new int[] { this.v, this.vt }; }

   /**
    * Returns a string representation of this indices set.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(1); }

   /**
    * Returns a string representation of this indices set.
    *
    * @param padding initial zeroes padding
    *
    * @return the string
    */
   public String toString ( final int padding ) {

      final StringBuilder sb = new StringBuilder(64);
      sb.append("{ v: ");
      Utils.toPadded(sb, this.v, padding);
      sb.append(", vt: ");
      Utils.toPadded(sb, this.vt, padding);
      sb.append(' ');
      sb.append('}');
      return sb.toString();
   }

   /**
    * Tests equivalence between this and another indices set.
    *
    * @param idx the other set
    *
    * @return the evaluation
    */
   protected boolean equals ( final Index2 idx ) {

      return this.v == idx.v && this.vt == idx.vt;
   }

   /**
    * Resizes an array of indices to a requested length. If the new length is
    * greater than the current length, the new elements are filled with new
    * indices.<br>
    * <br>
    * This does <em>not</em> use
    * {@link System#arraycopy(Object, int, Object, int, int)} because this
    * function iterates through the entire array checking for null entries.
    *
    * @param arr the array
    * @param sz  the new size
    *
    * @return the array
    */
   public static Index2[] resize ( final Index2[] arr, final int sz ) {

      if ( sz < 1 ) { return new Index2[] {}; }
      final Index2[] result = new Index2[sz];

      if ( arr == null ) {
         for ( int i = 0; i < sz; ++i ) { result[i] = new Index2(); }
         return result;
      }

      final int last = arr.length - 1;
      for ( int i = 0; i < sz; ++i ) {
         if ( i > last || arr[i] == null ) {
            result[i] = new Index2();
         } else {
            result[i] = arr[i];
         }
      }

      return result;
   }

}
