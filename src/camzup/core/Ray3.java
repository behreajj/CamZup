package camzup.core;

import java.util.Comparator;

/**
 * A direction that extends from an originating point.
 */
public class Ray3 implements Cloneable {

   /**
    * The ray's direction.
    */
   public final Vec3 dir;

   /**
    * The ray's origin point.
    */
   public final Vec3 origin;

   {
      this.origin = new Vec3();
      this.dir = Vec3.forward(new Vec3());
   }

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
    * Returns a new ray with this ray's components. Java's cloneable interface
    * is problematic; use set or a copy constructor instead.
    *
    * @return a new ray
    *
    * @see Ray3#set(Ray3)
    * @see Ray3#Ray3(Ray3)
    */
   @Override
   public Ray3 clone ( ) { return new Ray3(this.origin, this.dir); }

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
      if ( obj == null ) { return false; }
      if ( this.getClass() != obj.getClass() ) { return false; }
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
      hash = hash * IUtils.HASH_MUL ^ ( this.origin == null ? 0 : this.origin
         .hashCode() );
      hash = hash * IUtils.HASH_MUL ^ ( this.dir == null ? 0 : this.dir
         .hashCode() );
      return hash;
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
   public String toString ( ) { return this.toString(4); }

   /**
    * Returns a string representation of this ray.
    *
    * @param places number of decimal places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(196);
      sb.append("{ origin: ");
      sb.append(this.origin.toString(places));
      sb.append(", dir: ");
      sb.append(this.dir.toString(places));
      sb.append(' ');
      sb.append('}');
      return sb.toString();
   }

   /**
    * Tests equivalence between this and another ray.
    *
    * @param ray the other ray
    *
    * @return the evaluation
    */
   protected boolean equals ( final Ray3 ray ) {

      if ( this.dir == null ) {
         if ( ray.dir != null ) { return false; }
      } else if ( !this.dir.equals(ray.dir) ) { return false; }

      if ( this.origin == null ) {
         if ( ray.origin != null ) { return false; }
      } else if ( !this.origin.equals(ray.origin) ) { return false; }

      return true;
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
         } else {
            final float tm = time * Utils.invSqrtUnchecked(dmsq);
            return target.set(origin.x + dir.x * tm, origin.y + dir.y * tm,
               origin.z + dir.z * tm);
         }
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
   public static abstract class AbstrComparator implements Comparator < Ray3 > {

      /**
       * The default constructor.
       */
      public AbstrComparator ( ) {}

      /**
       * The compare function which must be implemented by sub- (child) classes
       * of this class. Negative one should be returned when the left
       * comparisand, a, is less than the right comparisand, b, by a measure.
       * One should be returned when it is greater. Zero should be returned as a
       * last resort, when a and b are equal or incomparable.
       *
       * @param a the left comparisand
       * @param b the right comparisand
       *
       * @return the comparison
       */
      @Override
      public abstract int compare ( final Ray3 a, final Ray3 b );

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

}
