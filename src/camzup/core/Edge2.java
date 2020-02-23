package camzup.core;

/**
 * Organizes components of a 2D mesh into an edge with an
 * origin and destination.
 *
 * This is not used by a mesh internally; it is created upon
 * retrieval from a mesh.
 */
public class Edge2 implements Comparable < Edge2 > {

   @Experimental
   public static Vec2 eval (
         final Edge2 edge,
         final float step,
         final Vec2 target ) {

      final Vec2 coOrigin = edge.origin.coord;
      final Vec2 coDest = edge.dest.coord;

      if (step <= 0.0f) {
         return target.set(coOrigin);
      }

      if (step >= 1.0f) {
         return target.set(coDest);
      }

      final float u = 1.0f - step;
      return target.set(
            step * coOrigin.x + u * coDest.x,
            step * coOrigin.y + u * coDest.y);
   }

   @Experimental
   public static Vert2 eval (
         final Edge2 edge,
         final float step,
         final Vert2 target ) {

      // TODO: Needs testing...

      final Vert2 origin = edge.origin;
      final Vert2 dest = edge.dest;

      final Vec2 vOrigin = origin.coord;
      final Vec2 vDest = dest.coord;
      final Vec2 vTarget = target.coord;

      final Vec2 vtOrigin = origin.texCoord;
      final Vec2 vtDest = dest.texCoord;
      final Vec2 vtTarget = target.texCoord;

      if (step <= 0.0f) {
         vTarget.set(vOrigin);
         vtTarget.set(vtOrigin);
         return target;
      }

      if (step >= 1.0f) {
         vTarget.set(vDest);
         vtTarget.set(vtDest);
         return target;
      }

      final float u = 1.0f - step;

      vTarget.set(
            step * vOrigin.x + u * vDest.x,
            step * vOrigin.y + u * vDest.y);

      vtTarget.set(
            step * vtOrigin.x + u * vtDest.x,
            step * vtOrigin.y + u * vtDest.y);

      return target;
   }

   @Experimental
   public static Vec2 projectVector (
         final Edge2 edge,
         final Vec2 v,
         final Vec2 target ) {

      final Vec2 coOrigin = edge.origin.coord;
      final Vec2 coDest = edge.dest.coord;

      final float ax = v.x - coOrigin.x;
      final float ay = v.y - coOrigin.y;

      final float bx = coDest.x - coOrigin.x;
      final float by = coDest.y - coOrigin.y;

      final float bSq = bx * bx + by * by;
      final float fac = bSq == 0.0f ? 0.0f
            : Utils.clamp01(
                  (ax * bx + ay * by) / bSq);

      return target.set(
            coOrigin.x + bx * fac,
            coOrigin.y + by * fac);
   }

   @Experimental
   public static float slope ( final Edge2 edge ) {

      final Vec2 dest = edge.dest.coord;
      final Vec2 origin = edge.origin.coord;
      return Utils.atan2(
            dest.y - origin.y,
            dest.x - origin.x);
   }

   /**
    * The destination vertex.
    */
   public Vert2 dest;

   /**
    * The origin vertex.
    */
   public Vert2 origin;

   /**
    * The default constructor. Creates two empty vertices.
    */
   public Edge2 () {

      this.origin = new Vert2();
      this.dest = new Vert2();
   }

   /**
    * Constructs an edge from the origin and destination
    * coordinate and texture coordinate data. Creates two
    * vertex objects.`
    *
    * @param coOrigin
    *           origin coordinate
    * @param txOrigin
    *           origin texture coordinate
    * @param coDest
    *           destination coordinate
    * @param txDest
    *           destination texture coordinate
    */
   public Edge2 (
         final Vec2 coOrigin,
         final Vec2 txOrigin,
         final Vec2 coDest,
         final Vec2 txDest ) {

      this.origin = new Vert2(coOrigin, txOrigin);
      this.dest = new Vert2(coDest, txDest);
   }

   /**
    * Constructs an edge from two vertices, an origin and
    * destination.
    *
    * @param origin
    *           the origin
    * @param dest
    *           the destination
    */
   public Edge2 (
         final Vert2 origin,
         final Vert2 dest ) {

      this.set(origin, dest);
   }

   /**
    * Tests this edge for equivalence with another.
    *
    * @return the evaluation
    */
   protected boolean equals ( final Edge2 edge2 ) {

      if (this.dest == null) {
         if (edge2.dest != null) {
            return false;
         }
      } else if (!this.dest.equals(edge2.dest)) {
         return false;
      }

      if (this.origin == null) {
         if (edge2.origin != null) {
            return false;
         }
      } else if (!this.origin.equals(edge2.origin)) {
         return false;
      }

      return true;
   }

   /**
    * Compares two edges based on their identity hash codes.
    *
    * @return the evaluation
    */
   @Override
   public int compareTo ( final Edge2 edge ) {

      final int a = System.identityHashCode(this);
      final int b = System.identityHashCode(edge);
      return a < b ? -1 : a > b ? 1 : 0;
   }

   /**
    * Tests this edge for equivalence with another object.
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

      return this.equals((Edge2) obj);
   }

   /**
    * Returns a hash code for this edge based on its origin and
    * destination.
    *
    * @return the hash
    */
   @Override
   public int hashCode () {

      int hash = IUtils.HASH_BASE;
      hash = hash * IUtils.HASH_MUL
            ^ (this.origin == null ? 0 : this.origin.hashCode());
      hash = hash * IUtils.HASH_MUL
            ^ (this.dest == null ? 0 : this.dest.hashCode());
      return hash;
   }

   /**
    * Rotates the coordinates of this edge by an angle in
    * radians around the z axis. The texture coordinates are
    * unaffected.
    *
    * @param radians
    *           angle
    * @return this edge
    */
   @Chainable
   public Edge2 rotateZ ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

      Vec2.rotateZ(this.origin.coord,
            cosa, sina, this.origin.coord);
      Vec2.rotateZ(this.dest.coord,
            cosa, sina, this.dest.coord);
      return this;
   }

   /**
    * Scales the coordinates of this edge. The texture
    * coordinates are unaffected.
    *
    * @param v
    *           uniform scalar
    * @return this edge
    */
   @Chainable
   public Edge2 scale ( final float v ) {

      Vec2.mul(this.origin.coord, v, this.origin.coord);
      Vec2.mul(this.dest.coord, v, this.dest.coord);
      return this;
   }

   /**
    * Scales the coordinates of this edge. The texture
    * coordinates are unaffected.
    *
    * @param v
    *           non uniform scalar
    * @return this edge
    */
   @Chainable
   public Edge2 scale ( final Vec2 v ) {

      Vec2.mul(this.origin.coord, v, this.origin.coord);
      Vec2.mul(this.dest.coord, v, this.dest.coord);
      return this;
   }

   /**
    * Sets the origin and destination coordinate, texture
    * coordinate and normal data.
    *
    * @param coOrigin
    *           origin coordinate
    * @param txOrigin
    *           origin texture coordinate
    * @param coDest
    *           destination coordinate
    * @param txDest
    *           destination texture coordinate
    * @return this edge
    */
   @Chainable
   public Edge2 set (
         final Vec2 coOrigin,
         final Vec2 txOrigin,
         final Vec2 coDest,
         final Vec2 txDest ) {

      this.origin.set(coOrigin, txOrigin);
      this.dest.set(coDest, txDest);
      return this;
   }

   /**
    * Sets this edge by vertex.
    *
    * @param origin
    *           the origin vertex
    * @param dest
    *           the destination vertex
    * @return this edge
    */
   @Chainable
   public Edge2 set (
         final Vert2 origin,
         final Vert2 dest ) {

      this.origin = origin;
      this.dest = dest;

      return this;
   }

   /**
    * Returns a string representation of this edge.
    *
    * @return the string
    */
   @Override
   public String toString () {

      return this.toString(4);
   }

   /**
    * Returns a string representation of this edge.
    *
    * @param places
    *           the number of places
    * @return the string
    */
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(512)
            .append("{ origin: ")
            .append(this.origin.toString(places))
            .append(", dest: ")
            .append(this.dest.toString(places))
            .append(' ').append('}');
      return sb.toString();
   }

   /**
    * Translates the coordinates of this edge. The texture
    * coordinates are unaffected.
    *
    * @param v
    *           translation
    * @return this edge
    */
   @Chainable
   public Edge2 translate ( final Vec2 v ) {

      Vec2.add(this.origin.coord, v, this.origin.coord);
      Vec2.add(this.dest.coord, v, this.dest.coord);
      return this;
   }
}