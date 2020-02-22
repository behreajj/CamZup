package camzup.core;

/**
 * Organizes the components of a 3D mesh into a group of
 * coordinate, normal and texture coordinate such that they
 * can be edited together.
 *
 * This is not used by a mesh internally; it is created upon
 * retrieval from a mesh. All of its components should be
 * treated as references to data within the mesh, not as
 * unique values.
 */
public class Vert3 implements Comparable < Vert3 > {

   /**
    * The coordinate of the vertex in world space.
    */
   public Vec3 coord;

   /**
    * The direction in which light will bounce from the surface
    * of the mesh at the vertex.
    */
   public Vec3 normal;

   /**
    * The texture (UV) coordinate for an image mapped onto the
    * mesh.
    */
   public Vec2 texCoord;

   /**
    * The default constructor. When used, the vertex's coord,
    * normal and texCoord will remain null.
    */
   public Vert3 () {

   }

   /**
    * Constructs a vertex from a coordinate, texture coordinate
    * and normal.
    *
    * @param coord
    *           the coordinate
    * @param texCoord
    *           the texture coordinate
    * @param normal
    *           the normal
    */
   public Vert3 (
         final Vec3 coord,
         final Vec2 texCoord,
         final Vec3 normal ) {

      this.set(coord, texCoord, normal);
   }

   /**
    * Tests this vertex for equivalence with another.
    *
    * @return the evaluation
    */
   protected boolean equals ( final Vert3 vert3 ) {

      if (this.coord == null) {
         if (vert3.coord != null) {
            return false;
         }
      } else if (!this.coord.equals(vert3.coord)) {
         return false;
      }

      if (this.texCoord == null) {
         if (vert3.texCoord != null) {
            return false;
         }
      } else if (!this.texCoord.equals(vert3.texCoord)) {
         return false;
      }

      if (this.normal == null) {
         if (vert3.normal != null) {
            return false;
         }
      } else if (!this.normal.equals(vert3.normal)) {
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
   public int compareTo ( final Vert3 vert ) {

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

      return this.equals((Vert3) obj);
   }

   /**
    * Returns a hash code for this vertex based on its
    * coordinate, texture coordinate and normal.
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
      hash = hash * IUtils.HASH_MUL
            ^ (this.normal == null ? 0 : this.normal.hashCode());
      return hash;
   }

   /**
    * Sets the coordinate, texture coordinate and normal of the
    * vertex by reference.
    *
    * @param coord
    *           the coordinate
    * @param texCoord
    *           the texture coordinate
    * @param normal
    *           the normal
    * @return this vertex
    */
   @Chainable
   public Vert3 set (
         final Vec3 coord,
         final Vec2 texCoord,
         final Vec3 normal ) {

      this.coord = coord;
      this.texCoord = texCoord;
      this.normal = normal;
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

      return new StringBuilder(512)
            .append("{ coord: ")
            .append(this.coord.toString(places))
            .append(", texCoord: ")
            .append(this.texCoord.toString(places))
            .append(", normal: ")
            .append(this.normal.toString(places))
            .append(' ').append('}')
            .toString();
   }
}