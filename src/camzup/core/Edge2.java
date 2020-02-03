package camzup.core;

@Experimental
public class Edge2 implements Comparable < Edge2 > {

   public Vert2 dest;
   public Vert2 origin;

   public Edge2 () {

      // TODO: Needs testing.

      this.origin = new Vert2();
      this.dest = new Vert2();
   }

   public Edge2 (
         final Vec2 coOrigin,
         final Vec2 txOrigin,
         final Vec2 coDest,
         final Vec2 txDest ) {

      this.set(
            coOrigin, txOrigin,
            coDest, txDest);
   }

   public Edge2 (
         final Vert2 origin,
         final Vert2 dest ) {

      this.set(origin, dest);
   }

   @Override
   public int compareTo ( final Edge2 edge ) {

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

      final Edge2 other = (Edge2) obj;

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
   public Edge2 rotateZ ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

      Vec2.rotateZ(this.origin.coord,
            cosa, sina, this.origin.coord);
      Vec2.rotateZ(this.dest.coord,
            cosa, sina, this.dest.coord);
      return this;
   }

   @Chainable
   public Edge2 scale ( final float v ) {

      Vec2.mul(this.origin.coord, v, this.origin.coord);
      Vec2.mul(this.dest.coord, v, this.dest.coord);
      return this;
   }

   @Chainable
   public Edge2 scale ( final Vec2 v ) {

      Vec2.mul(this.origin.coord, v, this.origin.coord);
      Vec2.mul(this.dest.coord, v, this.dest.coord);
      return this;
   }

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

   @Chainable
   public Edge2 set (
         final Vert2 origin,
         final Vert2 dest ) {

      this.origin = origin;
      this.dest = dest;

      return this;
   }

   @Override
   public String toString () {

      return this.toString(4);
   }

   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(512)
            .append("{ origin: ")
            .append(this.origin.toString(places))
            .append(", dest: ")
            .append(this.dest.toString(places))
            .append(' ').append('}');
      return sb.toString();
   }

   @Chainable
   public Edge2 translate ( final Vec2 v ) {

      Vec2.add(this.origin.coord, v, this.origin.coord);
      Vec2.add(this.dest.coord, v, this.dest.coord);
      return this;
   }
}