package camzup.core;

/**
 * Organizes components of a 3D mesh into an edge with an
 * origin and destination.
 *
 * This is not used by a mesh internally; it is created upon
 * retrieval from a mesh.
 */
public class Edge3 implements Comparable < Edge3 > {

   @Experimental
   public static float azimuth ( final Edge3 edge ) {

      final Vec3 dest = edge.dest.coord;
      final Vec3 origin = edge.origin.coord;
      return Utils.atan2(
            dest.y - origin.y,
            dest.x - origin.x);
   }

   @Experimental
   public static Vec3 eval (
         final Edge3 edge,
         final float step,
         final Vec3 target ) {

      final Vec3 coOrigin = edge.origin.coord;
      final Vec3 coDest = edge.dest.coord;

      if (step <= 0.0f) {
         return target.set(coOrigin);
      }

      if (step >= 1.0f) {
         return target.set(coDest);
      }

      final float u = 1.0f - step;
      return target.set(
            step * coOrigin.x + u * coDest.x,
            step * coOrigin.y + u * coDest.y,
            step * coOrigin.z + u * coDest.z);
   }

   @Experimental
   public static Vert3 eval (
         final Edge3 edge,
         final float step,
         final Vert3 target ) {

      // TODO: Needs testing...

      final Vert3 origin = edge.origin;
      final Vert3 dest = edge.dest;

      final Vec3 vOrigin = origin.coord;
      final Vec3 vDest = dest.coord;
      final Vec3 vTarget = target.coord;

      final Vec2 vtOrigin = origin.texCoord;
      final Vec2 vtDest = dest.texCoord;
      final Vec2 vtTarget = target.texCoord;

      final Vec3 vnOrigin = origin.normal;
      final Vec3 vnDest = dest.normal;
      final Vec3 vnTarget = target.normal;

      if (step <= 0.0f) {
         vTarget.set(vOrigin);
         vtTarget.set(vtOrigin);
         vnTarget.set(vnOrigin);
         return target;
      }

      if (step >= 1.0f) {
         vTarget.set(vDest);
         vtTarget.set(vtDest);
         vnTarget.set(vnDest);
         return target;
      }

      final float u = 1.0f - step;

      vTarget.set(
            step * vOrigin.x + u * vDest.x,
            step * vOrigin.y + u * vDest.y,
            step * vOrigin.z + u * vDest.z);

      vtTarget.set(
            step * vtOrigin.x + u * vtDest.x,
            step * vtOrigin.y + u * vtDest.y);

      vnTarget.set(
            step * vnOrigin.x + u * vnDest.x,
            step * vnOrigin.y + u * vnDest.y,
            step * vnOrigin.z + u * vnDest.z);

      Vec3.normalize(vnTarget, vnTarget);

      return target;
   }

   @Experimental
   public static Vec3 projectVector (
         final Edge3 edge,
         final Vec3 v,
         final Vec3 target ) {

      final Vec3 coOrigin = edge.origin.coord;
      final Vec3 coDest = edge.dest.coord;

      final float ax = v.x - coOrigin.x;
      final float ay = v.y - coOrigin.y;
      final float az = v.z - coOrigin.z;

      final float bx = coDest.x - coOrigin.x;
      final float by = coDest.y - coOrigin.y;
      final float bz = coDest.z - coOrigin.z;

      final float bSq = bx * bx + by * by + bz * bz;
      final float fac = bSq == 0.0f ? 0.0f
            : Utils.clamp01(
                  (ax * bx + ay * by + az * bz) / bSq);

      return target.set(
            coOrigin.x + bx * fac,
            coOrigin.y + by * fac,
            coOrigin.z + bz * fac);
   }

   /**
    * The destination vertex.
    */
   public Vert3 dest;

   /**
    * The origin vertex.
    */
   public Vert3 origin;

   /**
    * The default constructor. Creates two empty vertices.
    */
   public Edge3 () {

      this.origin = new Vert3();
      this.dest = new Vert3();
   }

   /**
    * Constructs an edge from the origin and destination
    * coordinate, texture coordinate and normal data. Creates
    * two vertex objects.
    *
    * @param coOrigin
    *           origin coordinate
    * @param txOrigin
    *           origin texture coordinate
    * @param nmOrigin
    *           origin normal
    * @param coDest
    *           destination coordinate
    * @param txDest
    *           destination texture coordinate
    * @param nmDest
    *           destination normal
    */
   public Edge3 (
         final Vec3 coOrigin,
         final Vec2 txOrigin,
         final Vec3 nmOrigin,
         final Vec3 coDest,
         final Vec2 txDest,
         final Vec3 nmDest ) {

      this.origin = new Vert3(coOrigin, txOrigin, nmOrigin);
      this.dest = new Vert3(coDest, txDest, nmDest);
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
   public Edge3 (
         final Vert3 origin,
         final Vert3 dest ) {

      this.set(origin, dest);
   }

   /**
    * Tests this edge for equivalence with another.
    *
    * @return the evaluation
    */
   protected boolean equals ( final Edge3 edge3 ) {

      if (this.dest == null) {
         if (edge3.dest != null) {
            return false;
         }
      } else if (!this.dest.equals(edge3.dest)) {
         return false;
      }

      if (this.origin == null) {
         if (edge3.origin != null) {
            return false;
         }
      } else if (!this.origin.equals(edge3.origin)) {
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
   public int compareTo ( final Edge3 edge ) {

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

      return this.equals((Edge3) obj);
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
    * Rotates the coordinates and normals of this edge by an
    * angle and axis. The texture coordinates are unaffected.
    *
    * @param radians
    *           angle
    * @param axis
    *           axis
    * @return this edge
    */
   @Experimental
   @Chainable
   public Edge3 rotate (
         final float radians,
         final Vec3 axis ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

      Vec3.rotate(this.origin.coord,
            cosa, sina, axis, this.origin.coord);
      Vec3.rotate(this.dest.coord,
            cosa, sina, axis, this.dest.coord);

      Vec3.rotate(this.origin.normal,
            cosa, sina, axis, this.origin.normal);
      Vec3.rotate(this.dest.normal,
            cosa, sina, axis, this.dest.normal);

      return this;
   }

   /**
    * Rotates the coordinates and normals of this edge by a
    * quaternion. The texture coordinates are unaffected.
    *
    * @param q
    *           the quaternion
    * @return this edge
    */
   @Experimental
   @Chainable
   public Edge3 rotate ( final Quaternion q ) {

      Quaternion.mulVector(q, this.origin.coord,
            this.origin.coord);
      Quaternion.mulVector(q, this.dest.coord,
            this.dest.coord);

      Quaternion.mulVector(q, this.origin.normal,
            this.origin.normal);
      Quaternion.mulVector(q, this.dest.normal,
            this.dest.normal);

      return this;
   }

   /**
    * Rotates the coordinates and normals of this edge by an
    * angle in radians around the x axis. The texture
    * coordinates are unaffected.
    *
    * @param radians
    *           angle
    * @return this edge
    */
   @Experimental
   @Chainable
   public Edge3 rotateX ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

      Vec3.rotateX(this.origin.coord,
            cosa, sina, this.origin.coord);
      Vec3.rotateX(this.dest.coord,
            cosa, sina, this.dest.coord);

      Vec3.rotateX(this.origin.normal,
            cosa, sina, this.origin.normal);
      Vec3.rotateX(this.dest.normal,
            cosa, sina, this.dest.normal);

      return this;
   }

   /**
    * Rotates the coordinates and normals of this edge by an
    * angle in radians around the y axis. The texture
    * coordinates are unaffected.
    *
    * @param radians
    *           angle
    * @return this edge
    */
   @Experimental
   @Chainable
   public Edge3 rotateY ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

      Vec3.rotateY(this.origin.coord,
            cosa, sina, this.origin.coord);
      Vec3.rotateY(this.dest.coord,
            cosa, sina, this.dest.coord);

      Vec3.rotateY(this.origin.normal,
            cosa, sina, this.origin.normal);
      Vec3.rotateY(this.dest.normal,
            cosa, sina, this.dest.normal);

      return this;
   }

   /**
    * Rotates the coordinates and normals of this edge by an
    * angle in radians around the z axis. The texture
    * coordinates are unaffected.
    *
    * @param radians
    *           angle
    * @return this edge
    */
   @Experimental
   @Chainable
   public Edge3 rotateZ ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

      Vec3.rotateZ(this.origin.coord,
            cosa, sina, this.origin.coord);
      Vec3.rotateZ(this.dest.coord,
            cosa, sina, this.dest.coord);

      Vec3.rotateZ(this.origin.normal,
            cosa, sina, this.origin.normal);
      Vec3.rotateZ(this.dest.normal,
            cosa, sina, this.dest.normal);

      return this;
   }

   /**
    * Scales the coordinates of this edge. The texture
    * coordinates and normals are unaffected.
    *
    * @param v
    *           uniform scalar
    * @return this edge
    */
   @Chainable
   public Edge3 scale ( final float v ) {

      Vec3.mul(this.origin.coord, v, this.origin.coord);
      Vec3.mul(this.dest.coord, v, this.dest.coord);
      return this;
   }

   /**
    * Scales the coordinates of this edge. The texture
    * coordinates and normals are unaffected.
    *
    * @param v
    *           non uniform scalar
    * @return this edge
    */
   @Experimental
   @Chainable
   public Edge3 scale ( final Vec3 v ) {

      Vec3.mul(this.origin.coord, v, this.origin.coord);
      Vec3.mul(this.dest.coord, v, this.dest.coord);
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
    * @param nmOrigin
    *           origin normal
    * @param coDest
    *           destination coordinate
    * @param txDest
    *           destination texture coordinate
    * @param nmDest
    *           destination normal
    * @return this edge
    */
   @Chainable
   public Edge3 set (
         final Vec3 coOrigin,
         final Vec2 txOrigin,
         final Vec3 nmOrigin,
         final Vec3 coDest,
         final Vec2 txDest,
         final Vec3 nmDest ) {

      this.origin.set(coOrigin, txOrigin, nmOrigin);
      this.dest.set(coDest, txDest, nmDest);
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
   public Edge3 set (
         final Vert3 origin,
         final Vert3 dest ) {

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

      final StringBuilder sb = new StringBuilder(1024)
            .append("{ origin: ")
            .append(this.origin.toString(places))
            .append(", dest: ")
            .append(this.dest.toString(places))
            .append(' ').append('}');
      return sb.toString();
   }

   /**
    * Translates the coordinates of this edge. The texture
    * coordinates and normals are unaffected.
    *
    * @param v
    *           translation
    * @return this edge
    */
   @Chainable
   public Edge3 translate ( final Vec3 v ) {

      Vec3.add(this.origin.coord, v, this.origin.coord);
      Vec3.add(this.dest.coord, v, this.dest.coord);
      return this;
   }
}