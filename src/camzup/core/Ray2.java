package camzup.core;

/**
 * A direction that extends from an originating point.
 */
public class Ray2 extends Ray {

   /**
    * The unique identification for serialized classes.
    */
   private static final long serialVersionUID = -2117690919294339509L;

   /**
    * Finds the point at a given time on a ray.
    * 
    * @param ray
    *           the ray
    * @param time
    *           the time step
    * @param target
    *           the output vector
    * @return the point
    */
   public static Vec2 eval (
         final Ray2 ray,
         final float time,
         final Vec2 target ) {

      final Vec2 origin = ray.origin;
      final Vec2 dir = ray.dir;

      final float dmSq = Vec2.magSq(dir);
      if (time <= 0.0f || dmSq == 0.0f) {
         return target.set(origin);
      }

      if (dmSq == 1.0f) {
         return target.set(
               origin.x + dir.x * time,
               origin.y + dir.y * time);
      }

      final float scalar = (float) (time / Math.sqrt(dmSq));
      return target.set(
            origin.x + dir.x * scalar,
            origin.y + dir.y * scalar);
   }

   /**
    * The ray's direction.
    */
   public final Vec2 dir = Vec2.forward(new Vec2());

   /**
    * The ray's origin point.
    */
   public final Vec2 origin = new Vec2();

   public Ray2 () {

      super();
   }

   public Ray2 ( final Ray2 source ) {

      super();
      this.set(source);
   }

   public Ray2 (
         final Vec2 origin,
         final Vec2 dir ) {

      super();
      this.set(origin, dir);
   }

   @Override
   public Ray2 clone () {

      return new Ray2(this.origin, this.dir);
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
      return this.equals((Ray2) obj);
   }

   @Override
   public int hashCode () {

      final int prime = 31;
      int result = 1;
      result = prime * result + (this.dir == null ? 0 : this.dir.hashCode());
      result = prime * result
            + (this.origin == null ? 0 : this.origin.hashCode());
      return result;
   }

   public Ray2 reset () {

      Vec2.zero(this.origin);
      Vec2.forward(this.dir);
      return this;
   }

   public Ray2 set ( final Ray2 source ) {

      return this.set(source.origin, source.dir);
   }

   public Ray2 set (
         final Vec2 origin,
         final Vec2 dir ) {

      this.origin.set(origin);
      Vec2.normalize(dir, this.dir);
      return this;
   }

   @Override
   public String toString () {

      return new StringBuilder(132)
            .append("{ origin: ")
            .append(origin.toString())
            .append(", dir: ")
            .append(dir.toString())
            .append(" }")
            .toString();
   }

   protected boolean equals ( final Ray2 ray ) {

      if (this.dir == null) {
         if (ray.dir != null) {
            return false;
         }
      } else if (!this.dir.equals(ray.dir)) {
         return false;
      }

      if (this.origin == null) {
         if (ray.origin != null) {
            return false;
         }
      } else if (!this.origin.equals(ray.origin)) {
         return false;
      }

      return true;
   }
}
