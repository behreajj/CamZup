package camzup.core;

/**
 * Structures the indices used by a 3D mesh to refer to arrays of data from
 * a vertex. Includes indices for a coordinate, and a texture coordinate.
 */
@Experimental
class Indices2 implements Comparable < Indices2 > {

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
   public Indices2 ( ) {}

   /**
    * Constructs an indices set from a source's components.
    *
    * @param source the source indices
    */
   public Indices2 ( final Indices2 source ) { this.set(source); }

   /**
    * Constructs an indices set from components.
    *
    * @param vIdx  the coordinate index
    * @param vtIdx the texture coordinate index
    */
   public Indices2 ( final int vIdx, final int vtIdx ) {

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
   public int compareTo ( final Indices2 idx ) {

      final int a = this.hashCode();
      final int b = idx.hashCode();
      return a > b ? 1 : a < b ? -1 : 0;
   }

   /**
    * Tests this indices set for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the equivalence
    *
    * @see Indices2#equals(Indices2)
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null ) { return false; }
      if ( this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Indices2 ) obj);
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
   public Indices2 set ( final Indices2 source ) {

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
   public Indices2 set ( final int vIdx, final int vtIdx ) {

      this.v = vIdx;
      this.vt = vtIdx;
      return this;
   }

   /**
    * Attempts to set the components of this indices set from Strings using
    * {@link Integer#parseInt(String)} . If a NumberFormatException is thrown,
    * the component is set to zero.
    *
    * @param vStr  the coordinate index
    * @param vtStr the texture coordinate index
    *
    * @return the indices set
    */
   public Indices2 set ( final String vStr, final String vtStr ) {

      int vIdx = 0;
      int vtIdx = 0;

      try {
         vIdx = Integer.parseInt(vStr);
      } catch ( final Exception e ) {
         vIdx = 0;
      }

      try {
         vtIdx = Integer.parseInt(vtStr);
      } catch ( final Exception e ) {
         vtIdx = 0;
      }

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
      sb.append(Utils.toPadded(this.v, padding));
      sb.append(", vt: ");
      sb.append(Utils.toPadded(this.vt, padding));
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
   protected boolean equals ( final Indices2 idx ) {

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
   public static Indices2[] resize ( final Indices2[] arr, final int sz ) {

      if ( sz < 1 ) { return new Indices2[] {}; }
      final Indices2[] result = new Indices2[sz];

      if ( arr == null ) {
         for ( int i = 0; i < sz; ++i ) { result[i] = new Indices2(); }
         return result;
      }

      final int last = arr.length - 1;
      for ( int i = 0; i < sz; ++i ) {
         if ( i > last || arr[i] == null ) {
            result[i] = new Indices2();
         } else {
            result[i] = arr[i];
         }
      }

      return result;
   }

}
