package camzup.core;

import java.io.Serializable;

/**
 * Structures the indices used by a 3D mesh to refer to arrays of data from a
 * vertex. Includes indices for a coordinate, and a texture coordinate.
 */
@Experimental
class Indices2 implements Cloneable, Comparable < Indices2 >, Serializable {

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
   public Indices2 (
      final int vIdx,
      final int vtIdx ) {

      this.set(vIdx, vtIdx);
   }

   /**
    * Returns a new indices set with this indices set's components.
    */
   @Override
   public Indices2 clone ( ) { return new Indices2(this.v, this.vt); }

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
   public Indices2 set (
      final int vIdx,
      final int vtIdx ) {

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
   public Indices2 set (
      final String vStr,
      final String vtStr ) {

      int vIdx = 0;
      int vtIdx = 0;

      try {
         vIdx = Integer.parseInt(vStr);
      } catch ( final NumberFormatException e ) {
         vIdx = 0;
      }

      try {
         vtIdx = Integer.parseInt(vtStr);
      } catch ( final NumberFormatException e ) {
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

      return new StringBuilder(64)
         .append("{ v: ")
         .append(Utils.toPadded(this.v, padding))
         .append(", vt: ")
         .append(Utils.toPadded(this.vt, padding))
         .append(' ')
         .append('}')
         .toString();
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
    * The unique identification for serialized classes.
    */
   private static final long serialVersionUID = 8718380790276620810L;

}
