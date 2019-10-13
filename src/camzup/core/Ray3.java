package camzup.core;

/**
 * A direction that extends from an originating point.
 */
public class Ray3 extends Ray {

   /**
    * The unique identification for serialized classes.
    */
   private static final long serialVersionUID = -8386381837024621749L;

   public static Vec3 eval (
         final Ray3 ray,
         final float time,
         final Vec3 target ) {

      final Vec3 origin = ray.origin;
      final Vec3 dir = ray.dir;

      final float dmSq = Vec3.magSq(dir);
      if (time <= 0.0f || dmSq == 0.0f) {
         return target.set(origin);
      }

      if (dmSq == 1.0f) {
         return target.set(
               origin.x + dir.x * time,
               origin.y + dir.y * time,
               origin.z + dir.z * time);
      }

      final float scalar = (float) (time / Math.sqrt(dmSq));
      return target.set(
            origin.x + dir.x * scalar,
            origin.y + dir.y * scalar,
            origin.z + dir.z * scalar);
   }

   /**
    * The ray's direction.
    */
   final Vec3 dir = Vec3.forward(new Vec3());

   /**
    * The ray's origin point.
    */
   final Vec3 origin = new Vec3();

   public Ray3 () {

      super();
   }

   public Ray3 ( final Ray3 source ) {

      super();
      this.set(source);
   }

   public Ray3 (
         final Vec3 origin,
         final Vec3 dir ) {

      super();
      this.set(origin, dir);
   }

   @Override
   public Ray3 clone () {

      return new Ray3(this.origin, this.dir);
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
      return this.equals((Ray3) obj);
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

   public Ray3 reset () {

      Vec3.zero(this.origin);
      Vec3.forward(this.dir);
      return this;
   }

   public Ray3 set ( final Ray3 source ) {

      return this.set(source.origin, source.dir);
   }

   public Ray3 set (
         final Vec3 origin,
         final Vec3 dir ) {

      this.origin.set(origin);
      Vec3.normalize(dir, this.dir);
      return this;
   }

   @Override
   public String toString () {

      return new StringBuilder(196)
         .append("{ origin: ")
         .append(origin.toString())
         .append(", dir: ")
         .append(dir.toString())
         .append(" }")
         .toString();
   }

   protected boolean equals ( final Ray3 ray ) {

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
