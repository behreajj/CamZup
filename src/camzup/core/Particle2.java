package camzup.core;

import java.util.Comparator;
import java.util.TreeSet;

/**
 * A particle class written with reference to Daniel
 * Shiffman's <em>The Nature of Code</em>, which in turn
 * references Valentino Braitenberg and Craig Reynolds.
 */
public class Particle2 implements Comparable < Particle2 > {

   /**
    * An abstract class that may serve as an umbrella for any
    * custom comparators of Particle2 s.
    */
   public static abstract class AbstrComparator
         implements Comparator < Particle2 > {

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
      public abstract int compare ( final Particle2 a, final Particle2 b );

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
    * Compares two particles by location.
    */
   public static class ComparatorLoc extends AbstrComparator {

      /**
       * The location comparator.
       */
      public final Comparator < Vec2 > locCmp;

      /**
       * The default constructor.
       */
      public ComparatorLoc () {

         super();
         this.locCmp = new Vec2.ComparatorYX();
      }

      /**
       * A constructor which sets the comparator by which each
       * ray's origins will be compared.
       *
       * @param comparator
       *           the vector comparator
       */
      public ComparatorLoc ( final Comparator < Vec2 > comparator ) {

         super();
         this.locCmp = comparator;
      }

      /**
       * Compares two particles by their origin.
       *
       * @param a
       *           the left comparisand
       * @param b
       *           the right comparisand
       * @return the comparison
       */
      @Override
      public int compare ( final Particle2 a, final Particle2 b ) {

         return this.locCmp.compare(a.loc, b.loc);
      }

   }

   private static Comparator < Particle2 > COMPARATOR = new ComparatorLoc();

   /**
    * Gets the string representation of the default Ray2
    * comparator.
    *
    * @return the string
    */
   public static String getComparatorString () {

      return Particle2.COMPARATOR.toString();
   }

   /**
    * Sets the comparator function by which collections of rays
    * are compared.
    *
    * @param comparator
    *           the comparator
    */
   public static void setComparator (
         final Comparator < Particle2 > comparator ) {

      if (comparator != null) {
         Particle2.COMPARATOR = comparator;
      }
   }

   protected final transient Ray2 query = new Ray2();

   protected final transient Vec2 scaledForce = new Vec2();

   public final Vec2 accel = new Vec2();

   public final Vec2 desired = new Vec2();

   public final Vec2 loc = new Vec2();

   public float mass = 1.0f;

   public float maxForce = 0.1f;

   public float maxSpeed = 4.0f;

   public final Vec2 steer = new Vec2();

   public final Vec2 velocity = new Vec2();

   public Particle2 () {

   }

   public Particle2 ( final Vec2 loc ) {

      this.loc.set(loc);
   }

   public Particle2 (
         final Vec2 loc,
         final float maxForce,
         final float maxSpeed,
         final float mass ) {

      this.loc.set(loc);
      this.maxForce = maxForce;
      this.maxSpeed = maxSpeed;
      this.mass = mass;

   }

   public Particle2 applyForce ( final Vec2 force ) {

      Vec2.add(Vec2.div(force, this.mass, this.scaledForce),
            this.accel, this.accel);

      return this;
   }

   @Override
   public int compareTo ( final Particle2 particle ) {

      return Particle2.COMPARATOR.compare(this, particle);
   }

   public Particle2 follow ( final TreeSet < Ray2 > flow ) {

      this.query.set(this.loc, this.velocity);
      final Ray2 fl = flow.floor(this.query);
      final Ray2 ce = flow.ceiling(this.query);

      final boolean flNonNull = fl != null;
      final boolean ceNonNull = ce != null;
      if (flNonNull && ceNonNull) {
         Vec2.mix(fl.dir, ce.dir, 0.5f, this.desired);

         // final float a = Vec2.headingSigned(fl.dir);
         // final float b = Vec2.headingSigned(ce.dir);
         // final float c = Particle2.LERP_ANGLE.apply(a, b, 0.5f);
         // Vec2.fromPolar(c, this.desired);
      } else if (flNonNull) {
         this.desired.set(fl.dir);
      } else if (ceNonNull) {
         this.desired.set(ce.dir);
      } else {
         return this;
      }

      Vec2.rescale(this.desired, this.maxSpeed, this.desired);
      Vec2.sub(this.desired, this.velocity, this.steer);
      Vec2.limit(this.steer, this.maxForce, this.steer);
      return this.applyForce(this.steer);
   }

   public Particle2 seek ( final Vec2 target ) {

      Vec2.subNorm(target, this.loc, this.desired);
      Vec2.mul(this.desired, this.maxSpeed, this.desired);
      Vec2.sub(this.desired, this.velocity, this.steer);
      Vec2.limit(this.steer, this.maxForce, this.steer);

      return this.applyForce(this.steer);
   }

   public Particle2 update () {

      Vec2.add(this.velocity, this.accel, this.velocity);
      Vec2.limit(this.velocity, this.maxSpeed, this.velocity);
      Vec2.add(this.loc, this.velocity, this.loc);
      Vec2.zero(this.accel);

      return this;
   }

   public Particle2 wrapBorders ( final Vec2 dimension ) {

      final float wHalf = dimension.x * 0.5f;
      final float hHalf = dimension.y * 0.5f;

      if (this.loc.x < -wHalf) {
         this.loc.x = wHalf - 1.0f;
      } else if (this.loc.x > wHalf) {
         this.loc.x = 1.0f - wHalf;
      }

      if (this.loc.y < -hHalf) {
         this.loc.y = hHalf - 1.0f;
      } else if (this.loc.y > hHalf) {
         this.loc.y = 1.0f - hHalf;
      }

      return this;
   }

}
