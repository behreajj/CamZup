package camzup.core;

import java.util.Comparator;

/**
 * A direction that extends from an originating point.
 */
public class Ray2 extends Ray {

   /**
    * An abstract class that may serve as an umbrella for any
    * custom comparators of Ray2 s.
    */
   public static abstract class AbstrComparator implements Comparator < Ray2 > {

      /**
       * The default constructor.
       */
      public AbstrComparator () {

      }

      /**
       * The compare function which must be implemented by sub-
       * (child) classes of this class. Negative one should be
       * returned when the left comparisand, a, is less than the
       * right comparisand, b, by a measure. One should be
       * returned when it is greater. Zero should be returned as a
       * last resort, when a and b are equal or uncomparable.
       *
       * @param a
       *           the left comparisand
       * @param b
       *           the right comparisand
       * @return the comparison
       *
       */
      @Override
      public abstract int compare ( final Ray2 a, final Ray2 b );

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString () {

         return this.getClass().getSimpleName();
      }
   }

   /**
    * The unique identification for serialized classes.
    */
   private static final long serialVersionUID = -2117690919294339509l;

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

      if (Utils.approx(dmSq, 1.0f)) {
         return target.set(
               origin.x + dir.x * time,
               origin.y + dir.y * time);
      }

      final float scalar = time * Utils.invSqrtUnchecked(dmSq);
      return target.set(
            origin.x + dir.x * scalar,
            origin.y + dir.y * scalar);
   }

   /**
    * The ray's direction.
    */
   public final Vec2 dir;

   /**
    * The ray's origin point.
    */
   public final Vec2 origin;

   {
      this.origin = new Vec2();
      this.dir = Vec2.forward(new Vec2());
   }

   public Ray2 () {

      super();
   }

   /**
    * Creates a new ray from a source.
    *
    * @param source
    *           the source
    */
   public Ray2 ( final Ray2 source ) {

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
   public Ray2 (
         final Vec2 origin,
         final Vec2 dir ) {

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
   protected boolean equals ( final Ray2 ray ) {

      // if (this.dir == null) {
      // if (ray.dir != null) {
      // return false;
      // }
      // } else if (!this.dir.equals(ray.dir)) {
      // return false;
      // }

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
    * @see Ray2#set(Ray2)
    * @see Ray2#Ray2(Ray2)
    */
   @Override
   public Ray2 clone () {

      return new Ray2(this.origin, this.dir);
   }

   /**
    * Tests this ray for equivalence with another object.
    *
    * @param obj
    *           the object
    * @return the equivalence
    * @see Ray2#equals(Ray2)
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

      return this.equals((Ray2) obj);
   }

   /**
    * Returns a hash code for this ray based on its origin and
    * direction.
    *
    * @return the hash code
    */
   @Override
   public int hashCode () {

      int hash = IUtils.HASH_BASE;
      hash = hash * IUtils.HASH_MUL
            ^ (this.origin == null ? 0 : this.origin.hashCode());
      // hash = (hash * HASH_MUL)
      // ^ (this.dir == null ? 0 : this.dir.hashCode());
      return hash;
   }

   /**
    * Resets this ray to a default.
    *
    * @return this ray
    */
   public Ray2 reset () {

      Vec2.zero(this.origin);
      Vec2.forward(this.dir);
      return this;
   }

   /**
    * Sets this ray from a source.
    *
    * @param source
    *           the source ray
    * @return this ray
    */
   public Ray2 set ( final Ray2 source ) {

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
   public Ray2 set (
         final Vec2 origin,
         final Vec2 dir ) {

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

      return new StringBuilder(132)
            .append("{ origin: ")
            .append(this.origin.toString(places))
            .append(", dir: ")
            .append(this.dir.toString(places))
            .append(' ').append('}')
            .toString();
   }
}
