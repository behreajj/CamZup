package camzup.core;

/**
 * Organizes the components of a 2D mesh into a group of
 * coordinate and texture coordinate such that they can be
 * edited together.
 *
 * This is not used by a mesh internally; it is created upon
 * retrieval from a mesh. All of its components should be
 * treated as references to data within the mesh, not as
 * unique values.
 */
public class Vert2 implements Comparable < Vert2 > {

   /**
    * The coordinate of the vertex in world space.
    */
   public Vec2 coord;

   /**
    * The texture (UV) coordinate for an image mapped onto the
    * mesh.
    */
   public Vec2 texCoord;

   /**
    * The default constructor. When used, the vertex's coord,
    * normal and texCoord will remain null.
    */
   public Vert2 () {

   }

   /**
    * Constructs a vertex from a coordinate and texture
    * coordinate.
    *
    * @param coord
    *           the coordinate
    * @param texCoord
    *           the texture coordinate
    */
   public Vert2 (
         final Vec2 coord,
         final Vec2 texCoord ) {

      this.set(coord, texCoord);
   }

   /**
    * Tests this vertex for equivalence with another.
    *
    * @return the evaluation
    */
   protected boolean equals ( final Vert2 vert2 ) {

      if (this.coord == null) {
         if (vert2.coord != null) {
            return false;
         }
      } else if (!this.coord.equals(vert2.coord)) {
         return false;
      }

      if (this.texCoord == null) {
         if (vert2.texCoord != null) {
            return false;
         }
      } else if (!this.texCoord.equals(vert2.texCoord)) {
         return false;
      }

      return true;
   }

   /**
    * Compares this vertex to another by hash code.
    *
    * @param vert
    *           the comparisand
    * @return the comparison
    */
   @Override
   public int compareTo ( final Vert2 vert ) {

      final int a = System.identityHashCode(this);
      final int b = System.identityHashCode(vert);
      return a < b ? -1 : a > b ? 1 : 0;
   }

   /**
    * Tests this vertex for equivalence with another object.
    *
    * @return the evaluation
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

      return this.equals((Vert2) obj);
   }

   /**
    * Returns a hash code for this vertex based on its
    * coordinate and texture coordinate.
    *
    * @return the hash
    */
   @Override
   public int hashCode () {

      int hash = IUtils.HASH_BASE;
      hash = hash * IUtils.HASH_MUL
            ^ (this.coord == null ? 0 : this.coord.hashCode());
      hash = hash * IUtils.HASH_MUL
            ^ (this.texCoord == null ? 0 : this.texCoord.hashCode());
      return hash;
   }

   /**
    * Sets the coordinate and texture coordinate of the vertex
    * by reference.
    *
    * @param coord
    *           the coordinate
    * @param texCoord
    *           the texture coordinate
    * @return this vertex
    */
   @Chainable
   public Vert2 set (
         final Vec2 coord,
         final Vec2 texCoord ) {

      this.coord = coord;
      this.texCoord = texCoord;
      return this;
   }

   /**
    * Returns a string representation of this vertex.
    *
    * @return the string
    */
   @Override
   public String toString () {

      return this.toString(4);
   }

   /**
    * Returns a string representation of this vertex.
    *
    * @param places
    *           the number of places
    * @return the string
    */
   public String toString ( final int places ) {

      return new StringBuilder(256)
            .append("{ coord: ")
            .append(this.coord.toString(places))
            .append(", texCoord: ")
            .append(this.texCoord.toString(places))
            .append(' ').append('}')
            .toString();
   }
}