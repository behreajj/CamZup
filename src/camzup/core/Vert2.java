package camzup.core;

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
      final Vert2 other = (Vert2) obj;
      if (this.coord == null) {
         if (other.coord != null) {
            return false;
         }
      } else if (!this.coord.equals(other.coord)) {
         return false;
      }
      if (this.texCoord == null) {
         if (other.texCoord != null) {
            return false;
         }
      } else if (!this.texCoord.equals(other.texCoord)) {
         return false;
      }
      return true;
   }

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

   @Override
   public String toString () {

      return this.toString(4);
   }

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