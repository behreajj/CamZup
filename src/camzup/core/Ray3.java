package camzup.core;

import java.util.Comparator;

/**
 * A direction that extends from an originating point.
 */
public class Ray3 extends Ray implements Comparable < Ray3 > {

   /**
    * An abstract class that may serve as an umbrella for any
    * custom comparators of Ray2 s.
    */
   public static abstract class AbstrComparator implements Comparator < Ray3 > {

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
      public abstract int compare ( final Ray3 a, final Ray3 b );

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
    * Compares two rays by their origin.
    */
   public static class ComparatorOrigin extends AbstrComparator {

      /**
       * The location comparator.
       */
      public final Comparator < Vec3 > locCmp;

      /**
       * The default constructor.
       */
      public ComparatorOrigin () {

         super();
         this.locCmp = new Vec3.ComparatorZYX();
      }

      /**
       * A constructor which sets the comparator by which each
       * ray's origins will be compared.
       *
       * @param comparator
       *           the vector comparator
       */
      public ComparatorOrigin ( final Comparator < Vec3 > comparator ) {

         super();
         this.locCmp = comparator;
      }

      /**
       * Compares two rays by their origin.
       *
       * @param a
       *           the left comparisand
       * @param b
       *           the right comparisand
       * @return the comparison
       */
      @Override
      public int compare ( final Ray3 a, final Ray3 b ) {

         return this.locCmp.compare(a.origin, b.origin);
      }

   }

   /**
    * The default Ray2 comparator.
    */
   private static Comparator < Ray3 > COMPARATOR = new ComparatorOrigin();

   /**
    * The unique identification for serialized classes.
    */
   private static final long serialVersionUID = -8386381837024621749L;

   @Experimental
   static float march (
         final Ray3 ray,
         final int maxStep,
         final Sdf.FieldFunc func,
         final Object... args ) {

      float result = 0.0f;
      float t = 0.0f;
      final Vec3 point = new Vec3();
      for (int i = 0; i < maxStep; ++i) {
         Ray3.eval(ray, t, point);
         final float d = func.execute(args);
         if (d < Utils.EPSILON) {
            result = 0.5f;
            break;
         }
         t += d;
      }
      return result;
   }

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
    * Gets the string representation of the default Ray2
    * comparator.
    *
    * @return the string
    */
   public static String getComparatorString () {

      return Ray3.COMPARATOR.toString();
   }

   /**
    * Sets the comparator function by which collections of rays
    * are compared.
    *
    * @param comparator
    *           the comparator
    */
   public static void setComparator (
         final Comparator < Ray3 > comparator ) {

      if (comparator != null) {
         Ray3.COMPARATOR = comparator;
      }
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
    * @see Ray3#set(Ray3)
    * @see Ray3#Ray3(Ray3)
    */
   @Override
   public Ray3 clone () {

      return new Ray3(this.origin, this.dir);
   }

   @Override
   public int compareTo ( final Ray3 o ) {

      return Ray3.COMPARATOR.compare(this, o);
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
            .append(' ').append('}')
            .toString();
   }
}
