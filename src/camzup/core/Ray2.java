package camzup.core;

import java.util.Comparator;

/**
 * A direction that extends from an originating point.
 */
public class Ray2 {

   /**
    * The ray's direction.
    */
   public final Vec2 dir = Vec2.forward(new Vec2());

   /**
    * The ray's origin point.
    */
   public final Vec2 origin = new Vec2();

   /**
    * The default constructor.
    */
   public Ray2 ( ) {}

   /**
    * Creates a new ray from a source.
    *
    * @param source the source
    */
   public Ray2 ( final Ray2 source ) { this.set(source); }

   /**
    * Creates a new ray from an origin and direction.
    *
    * @param origin the origin
    * @param dir    the direction.
    */
   public Ray2 ( final Vec2 origin, final Vec2 dir ) {

      this.set(origin, dir);
   }

   /**
    * Tests this ray for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the equivalence
    *
    * @see Ray2#equals(Ray2)
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null || this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Ray2 ) obj);
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
    * @see Vec2#subNorm(Vec2, Vec2, Vec2)
    */
   public Ray2 lookAt ( final Vec2 target ) {

      Vec2.subNorm(target, this.origin, this.dir);
      return this;
   }

   /**
    * Resets this ray to a default.
    *
    * @return this ray
    *
    * @see Vec2#zero(Vec2)
    * @see Vec2#forward(Vec2)
    */
   public Ray2 reset ( ) {

      Vec2.zero(this.origin);
      Vec2.forward(this.dir);
      return this;
   }

   /**
    * Sets this ray from a source.
    *
    * @param source the source ray
    *
    * @return this ray
    */
   public Ray2 set ( final Ray2 source ) {

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
    * @see Vec2#normalize(Vec2, Vec2)
    */
   public Ray2 set ( final Vec2 origin, final Vec2 dir ) {

      this.origin.set(origin);
      Vec2.normalize(dir, this.dir);
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
   protected boolean equals ( final Ray2 ray ) {

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
    */
   public static Vec2 eval ( final Ray2 ray, final float time,
      final Vec2 target ) {

      final Vec2 origin = ray.origin;
      final Vec2 dir = ray.dir;
      final float dmsq = Vec2.magSq(dir);
      if ( time > 0.0f && dmsq > 0.0f ) {
         if ( Utils.approx(dmsq, 1.0f) ) {
            return target.set(origin.x + dir.x * time, origin.y + dir.y * time);
         }
         final float tm = time * Utils.invSqrtUnchecked(dmsq);
         return target.set(origin.x + dir.x * tm, origin.y + dir.y * tm);
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
    * @see Vec2#subNorm(Vec2, Vec2, Vec2)
    */
   public static Ray2 fromPoints ( final Vec2 origin, final Vec2 dest,
      final Ray2 target ) {

      target.origin.set(origin);
      Vec2.subNorm(dest, origin, target.dir);
      return target;
   }

   /**
    * Finds an intersection between a ray and a line segment. Returns -1.0 if
    * there is no intersection. Otherwise, returns a value in [0.0, 1.0] .
    *
    * @param ray    the ray
    * @param origin the origin
    * @param dest   the destination
    *
    * @return the distance
    */
   @Experimental
   public static float intersectLineSeg ( final Ray2 ray, final Vec2 origin,
      final Vec2 dest ) {

      // TEST

      final Vec2 v1 = Vec2.sub(dest, origin, new Vec2());
      final Vec2 v2 = Vec2.perpendicularCCW(ray.dir, new Vec2());
      final float dot = Vec2.dot(v1, v2);
      if ( Utils.approx(dot, 0.0f) ) { return -1.0f; }

      final Vec2 v0 = Vec2.sub(ray.origin, origin, new Vec2());
      final float t1 = Vec2.cross(v1, v0) / dot;
      if ( t1 >= 0.0f ) {
         final float t2 = Vec2.dot(v0, v2) / dot;
         if ( t2 >= 0.0f && t2 <= 1.0f ) { return t1; }
      }

      return -1.0f;
   }

   /**
    * An abstract class that may serve as an umbrella for any custom
    * comparators of Ray2 s.
    */
   public abstract static class AbstrComparator implements Comparator < Ray2 > {

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
