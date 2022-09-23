package camzup.core;

import java.util.Comparator;

/**
 * A direction that extends from an originating point.
 */
public class Ray3 {

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
   public Ray3 ( ) {}

   /**
    * Creates a new ray from a source.
    *
    * @param source the source
    */
   public Ray3 ( final Ray3 source ) { this.set(source); }

   /**
    * Creates a new ray from an origin and direction.
    *
    * @param origin the origin
    * @param dir    the direction.
    */
   public Ray3 ( final Vec3 origin, final Vec3 dir ) {

      this.set(origin, dir);
   }

   /**
    * Tests this ray for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the equivalence
    *
    * @see Ray3#equals(Ray3)
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null || this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Ray3 ) obj);
   }

   /**
    * Returns a hash code for this ray based on its origin and direction.
    *
    * @return the hash code
    */
   @Override
   public int hashCode ( ) {

      int hash = IUtils.HASH_BASE;
      hash = hash * IUtils.HASH_MUL ^ this.origin.hashCode();
      return hash * IUtils.HASH_MUL ^ this.dir.hashCode();
   }

   /**
    * Sets this ray to look at a target point.
    *
    * @param target the target point
    *
    * @return this ray
    *
    * @see Vec3#subNorm(Vec3, Vec3, Vec3)
    */
   public Ray3 lookAt ( final Vec3 target ) {

      Vec3.subNorm(target, this.origin, this.dir);
      return this;
   }

   /**
    * Resets this ray to a default.
    *
    * @return this ray
    */
   public Ray3 reset ( ) {

      Vec3.zero(this.origin);
      Vec3.forward(this.dir);
      return this;
   }

   /**
    * Sets this ray from a source.
    *
    * @param source the source ray
    *
    * @return this ray
    */
   public Ray3 set ( final Ray3 source ) {

      return this.set(source.origin, source.dir);
   }

   /**
    * Sets the origin and direction of this ray. Normalizes the direction.
    *
    * @param origin the origin
    * @param dir    the direction
    *
    * @return this ray
    *
    * @see Vec3#normalize(Vec3, Vec3)
    */
   public Ray3 set ( final Vec3 origin, final Vec3 dir ) {

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
   public String toString ( ) { return this.toString(IUtils.FIXED_PRINT); }

   /**
    * Returns a string representation of this ray.
    *
    * @param places number of decimal places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      return this.toString(new StringBuilder(256), places).toString();
   }

   /**
    * Internal helper function to assist with methods that need to print many
    * rays. Appends to an existing {@link StringBuilder}.
    *
    * @param sb     the string builder
    * @param places the number of places
    *
    * @return the string builder
    */
   StringBuilder toString ( final StringBuilder sb, final int places ) {

      sb.append("{ origin: ");
      this.origin.toString(sb, places);
      sb.append(", dir: ");
      this.dir.toString(sb, places);
      sb.append(' ');
      sb.append('}');
      return sb;
   }

   /**
    * Tests equivalence between this and another ray.
    *
    * @param ray the other ray
    *
    * @return the evaluation
    */
   protected boolean equals ( final Ray3 ray ) {

      return this.origin.equals(ray.origin) && this.dir.equals(ray.dir);
   }

   /**
    * Finds the point at a given time on a ray.
    *
    * @param ray    the ray
    * @param time   the time step
    * @param target the output vector
    *
    * @return the point
    *
    * @see Utils#approx(float, float)
    * @see Utils#invSqrtUnchecked(float)
    * @see Vec3#magSq(Vec3)
    */
   public static Vec3 eval ( final Ray3 ray, final float time,
      final Vec3 target ) {

      final Vec3 origin = ray.origin;
      final Vec3 dir = ray.dir;
      final float dmsq = Vec3.magSq(dir);
      if ( time > 0.0f && dmsq > 0.0f ) {
         if ( Utils.approx(dmsq, 1.0f) ) {
            return target.set(origin.x + dir.x * time, origin.y + dir.y * time,
               origin.z + dir.z * time);
         }
         final float tm = time * Utils.invSqrtUnchecked(dmsq);
         return target.set(origin.x + dir.x * tm, origin.y + dir.y * tm,
            origin.z + dir.z * tm);
      }
      return target.set(origin);
   }

   /**
    * Sets a ray from an origin and destination point.
    *
    * @param origin the origin
    * @param dest   the destination
    * @param target the output ray
    *
    * @return the ray
    *
    * @see Vec3#subNorm(Vec3, Vec3, Vec3)
    */
   public static Ray3 fromPoints ( final Vec3 origin, final Vec3 dest,
      final Ray3 target ) {

      target.origin.set(origin);
      Vec3.subNorm(dest, origin, target.dir);
      return target;
   }

   /**
    * An abstract class that may serve as an umbrella for any custom
    * comparators of Ray2 s.
    */
   public abstract static class AbstrComparator implements Comparator < Ray3 > {

      /**
       * The default constructor.
       */
      protected AbstrComparator ( ) {}

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

}
