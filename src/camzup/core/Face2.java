package camzup.core;

import java.util.Arrays;

public class Face2 implements Comparable < Face2 > {

   /**
    * The array of vertices in a face.
    */
   public Vert2[] vertices;

   /**
    * The default constructor. When used, initializes an empty
    * array.
    */
   public Face2 () {

      this.vertices = new Vert2[] {};
   }

   /**
    * Creates a face from an array of vertices.
    *
    * @param vertices
    *           the vertices
    */
   public Face2 ( final Vert2[] vertices ) {

      this.set(vertices);
   }

   /**
    * Compares this face to another by hash code.
    *
    * @param face
    *           the comparisand
    * @return the comparison
    */
   @Override
   public int compareTo ( final Face2 face ) {

      final int a = System.identityHashCode(this);
      final int b = System.identityHashCode(face);
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
      final Face2 other = (Face2) obj;
      if (!Arrays.equals(this.vertices, other.vertices)) {
         return false;
      }
      return true;
   }

   @Experimental
   public Edge2 getEdge ( final int i, final Edge2 target ) {

      final int len = this.vertices.length;
      final int j = Math.floorMod(i, len);
      final int k = Math.floorMod(i + 1, len);
      return target.set(
            this.vertices[j],
            this.vertices[k]);
   }

   @Experimental
   public Edge2[] getEdges () {

      final int len = this.vertices.length;
      final int last = len - 1;
      final Edge2[] result = new Edge2[len];
      for (int i = 0; i < last; ++i) {
         result[i] = new Edge2(
               this.vertices[i],
               this.vertices[i + 1]);
      }
      result[last] = new Edge2(
            this.vertices[last],
            this.vertices[0]);
      return result;
   }

   @Override
   public int hashCode () {

      int hash = IUtils.HASH_BASE;
      hash = hash * IUtils.HASH_MUL ^ Arrays.hashCode(this.vertices);
      return hash;
   }

   /**
    * Returns the number of vertices in this face.
    *
    * @return the vertex count
    */
   public int length () {

      return this.vertices.length;
   }

   /**
    * Rotates all coordinates in the face by an angle around
    * the z axis.
    *
    * @param radians
    *           the angle in radians
    * @return this mesh
    * @see Vec2#rotateZ(Vec2, float, Vec2)
    */
   @Chainable
   public Face2 rotateZ ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);
      Vec2 c;

      final int len = this.vertices.length;
      for (int i = 0; i < len; ++i) {
         c = this.vertices[i].coord;
         Vec2.rotateZ(c, cosa, sina, c);
      }

      return this;
   }

   /**
    * Scales all coordinates in the face by a scalar.
    *
    * @param scale
    *           the vector
    * @return this face
    * @see Vec2#mul(Vec2, float, Vec2)
    */
   @Chainable
   public Face2 scale ( final float scale ) {

      Vec2 c;
      final int len = this.vertices.length;
      for (int i = 0; i < len; ++i) {
         c = this.vertices[i].coord;
         Vec2.mul(c, scale, c);
      }

      return this;
   }

   /**
    * Scales all coordinates in the face by a vector.
    *
    * @param scale
    *           the vector
    * @return this face
    * @see Vec2#mul(Vec2, Vec2, Vec2)
    */
   @Chainable
   public Face2 scale ( final Vec2 scale ) {

      Vec2 c;
      final int len = this.vertices.length;
      for (int i = 0; i < len; ++i) {
         c = this.vertices[i].coord;
         Vec2.mul(c, scale, c);
      }

      return this;
   }

   /**
    * Sets this face's vertices to refer to a an array.
    *
    * @param vertices
    *           the array of vertices
    * @return this face
    */
   @Chainable
   public Face2 set ( final Vert2[] vertices ) {

      this.vertices = vertices;
      return this;
   }

   @Override
   public String toString () {

      return this.toString(4);
   }

   public String toString ( final int places ) {

      final int len = this.vertices.length;
      final int last = len - 1;
      final StringBuilder sb = new StringBuilder(len * 256)
            .append("{ vertices: [ \n");
      for (int i = 0; i < len; ++i) {
         sb.append(this.vertices[i].toString(places));
         if (i < last) {
            sb.append(',').append('\n');
         }
      }
      sb.append(" ] }");
      return sb.toString();
   }

   /**
    * Translates all coordinates in the face by a vector.
    *
    * @param v
    *           the vector
    * @return this face
    * @see Vec2#add(Vec2, Vec2, Vec2)
    */
   @Chainable
   public Face2 translate ( final Vec2 v ) {

      Vec2 c;
      final int len = this.vertices.length;
      for (int i = 0; i < len; ++i) {
         c = this.vertices[i].coord;
         Vec2.add(c, v, c);
      }

      return this;
   }
}