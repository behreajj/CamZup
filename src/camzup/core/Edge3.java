package camzup.core;

@Experimental
public class Edge3 implements Comparable < Edge3 > {

   public Vert3 dest;
   public Vert3 origin;

   public Edge3 () {

      // TODO: Needs testing.

      this.origin = new Vert3();
      this.dest = new Vert3();
   }

   public Edge3 (
         final Vec3 coOrigin,
         final Vec2 txOrigin,
         final Vec3 nmOrigin,
         final Vec3 coDest,
         final Vec2 txDest,
         final Vec3 nmDest ) {

      this.set(
            coOrigin, txOrigin, nmOrigin,
            coDest, txDest, nmDest);
   }

   public Edge3 (
         final Vert3 origin,
         final Vert3 dest ) {

      this.set(origin, dest);
   }

   @Override
   public int compareTo ( final Edge3 edge ) {

      final int a = System.identityHashCode(this);
      final int b = System.identityHashCode(edge);
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

      final Edge3 other = (Edge3) obj;

      if (this.dest == null) {
         if (other.dest != null) {
            return false;
         }
      } else if (!this.dest.equals(other.dest)) {
         return false;
      }

      if (this.origin == null) {
         if (other.origin != null) {
            return false;
         }
      } else if (!this.origin.equals(other.origin)) {
         return false;
      }

      return true;
   }

   @Override
   public int hashCode () {

      int hash = IUtils.HASH_BASE;
      hash = hash * IUtils.HASH_MUL
            ^ (this.origin == null ? 0 : this.origin.hashCode());
      hash = hash * IUtils.HASH_MUL
            ^ (this.dest == null ? 0 : this.dest.hashCode());
      return hash;
   }

   @Chainable
   public Edge3 rotate ( final float radians, final Vec3 axis ) {

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

   @Chainable
   public Edge3 scale ( final float v ) {

      Vec3.mul(this.origin.coord, v, this.origin.coord);
      Vec3.mul(this.dest.coord, v, this.dest.coord);
      return this;
   }

   @Chainable
   public Edge3 scale ( final Vec3 v ) {

      Vec3.mul(this.origin.coord, v, this.origin.coord);
      Vec3.mul(this.dest.coord, v, this.dest.coord);
      return this;
   }

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

   @Chainable
   public Edge3 set (
         final Vert3 origin,
         final Vert3 dest ) {

      this.origin = origin;
      this.dest = dest;
      return this;
   }

   @Override
   public String toString () {

      return this.toString(4);
   }

   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(1024)
            .append("{ origin: ")
            .append(this.origin.toString(places))
            .append(", dest: ")
            .append(this.dest.toString(places))
            .append(' ').append('}');
      return sb.toString();
   }

   @Chainable
   public Edge3 translate ( final Vec3 v ) {

      Vec3.add(this.origin.coord, v, this.origin.coord);
      Vec3.add(this.dest.coord, v, this.dest.coord);
      return this;
   }
}