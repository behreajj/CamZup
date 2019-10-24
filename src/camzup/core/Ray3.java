package camzup.core;

/**
 * A direction that extends from an originating point.
 */
public class Ray3 extends Ray {

   /**
    * The unique identification for serialized classes.
    */
   private static final long serialVersionUID = -8386381837024621749L;

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

      if (Utils.approxFast(dmSq, 1.0f)) {
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
   public final Vec3 dir = Vec3.forward(new Vec3());

   /**
    * The ray's origin point.
    */
   public final Vec3 origin = new Vec3();

   /**
    * The default constructor.
    */
   public Ray3 () {

      super();
   }

   /**
    * Creates a new ray from a source.
    *
    * @param source
    *           the source
    */
   public Ray3 ( final Ray3 source ) {

      super();
      this.set(source);
   }

   /**
    * Creates a new ray from an origin and direction.
    *
    * @param origin
    *           the origin
    * @param dir
    *           the direction.
    */
   public Ray3 (
         final Vec3 origin,
         final Vec3 dir ) {

      super();
      this.set(origin, dir);
   }

   /**
    * Tests equivalence between this and another ray.
    *
    * @param ray
    *           the other ray
    * @return the evaluation
    */
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

   /**
    * Returns a new ray with this ray's components. Java's
    * cloneable interface is problematic; use set or a copy
    * constructor instead.
    *
    * @return a new ray
    * @see Ray3#set(Ray3)
    * @see Ray3#Ray3(Ray3)
    */
   @Override
   public Ray3 clone () {

      return new Ray3(this.origin, this.dir);
   }

   /**
    * Tests this ray for equivalence with another object.
    *
    * @param obj
    *           the object
    * @return the equivalence
    * @see Ray3#equals(Ray3)
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
      return this.equals((Ray3) obj);
   }

   /**
    * Returns a hash code for this ray based on its origin and
    * direction.
    *
    * @return the hash code
    */
   @Override
   public int hashCode () {

      final int prime = 31;
      int result = 1;
      result = prime * result + (this.dir == null ? 0 : this.dir.hashCode());
      result = prime * result
            + (this.origin == null ? 0 : this.origin.hashCode());
      return result;
   }

   /**
    * Resets this ray to a default.
    *
    * @return this ray
    */
   public Ray3 reset () {

      Vec3.zero(this.origin);
      Vec3.forward(this.dir);
      return this;
   }

   /**
    * Sets this ray from a source.
    *
    * @param source
    *           the source ray
    * @return this ray
    */
   public Ray3 set ( final Ray3 source ) {

      return this.set(source.origin, source.dir);
   }

   /**
    * Sets the origin and direction of this ray.
    *
    * @param origin
    *           the origin
    * @param dir
    *           the direction
    * @return this ray
    */
   public Ray3 set (
         final Vec3 origin,
         final Vec3 dir ) {

      this.origin.set(origin);
      Vec3.normalize(dir, this.dir);
      return this;
   }

   /**
    * Returns a string representation of this ray.
    *
    * @return the string
    */
   @Override
   public String toString () {

      return this.toString(4);
   }

   /**
    * Returns a string representation of this ray.
    *
    * @param places
    *           number of decimal places
    * @return the string
    */
   public String toString ( final int places ) {

      return new StringBuilder(196)
            .append("{ origin: ")
            .append(this.origin.toString(places))
            .append(", dir: ")
            .append(this.dir.toString(places))
            .append(" }")
            .toString();
   }
}
