package camzup.core;

import java.util.Comparator;
import java.util.TreeSet;

/**
 * A direction that extends from an originating point.
 */
public class Ray2 {

   /**
    * The ray's direction.
    */
   public final Vec2 dir = Vec2.forward(new Vec2());

   /**
    * The ray's origin.
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
    * @param dir    the direction
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

      sb.append("{\"origin\":");
      this.origin.toString(sb, places);
      sb.append(",\"dir\":");
      this.dir.toString(sb, places);
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
    *
    * @see Utils#approx(float, float)
    * @see Utils#invSqrtUnchecked(float)
    * @see Vec2#magSq(Vec2)
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
    * Finds points of intersection, if any, between a ray and a bounds.
    *
    * @param ray    the ray
    * @param bounds the bounds
    *
    * @return the points
    *
    * @see Ray2#intersectLineSeg(float, float, float, float, float, float,
    *      float, float)
    */
   public static Vec2[] intersections ( final Ray2 ray, final Bounds2 bounds ) {

      final Vec2 rOrg = ray.origin;
      final float rx = rOrg.x;
      final float ry = rOrg.y;

      final Vec2 rDir = ray.dir;
      final float dx = rDir.x;
      final float dy = rDir.y;

      final Vec2 min = bounds.min;
      final float x0 = min.x;
      final float y0 = min.y;

      final Vec2 max = bounds.max;
      final float x1 = max.x;
      final float y1 = max.y;

      final float t0 = Ray2.intersectLineSeg(rx, ry, dx, dy, x0, y0, x1, y0);
      final float t1 = Ray2.intersectLineSeg(rx, ry, dx, dy, x1, y0, x1, y1);
      final float t2 = Ray2.intersectLineSeg(rx, ry, dx, dy, x1, y1, x0, y1);
      final float t3 = Ray2.intersectLineSeg(rx, ry, dx, dy, x0, y1, x0, y0);

      final boolean t0Val = t0 != -1.0f;
      final boolean t1Val = t1 != -1.0f;
      final boolean t2Val = t2 != -1.0f;
      final boolean t3Val = t3 != -1.0f;

      /*
       * Avoid the possibility of duplicates when a ray intersects a corner
       * where two line segments meet.
       */
      final TreeSet < Vec2 > vs = new TreeSet <>();
      if ( t0Val ) { vs.add(new Vec2( ( 1.0f - t0 ) * x0 + t0 * x1, y0)); }
      if ( t1Val ) { vs.add(new Vec2(x1, ( 1.0f - t1 ) * y0 + t1 * y1)); }
      if ( t2Val ) { vs.add(new Vec2( ( 1.0f - t2 ) * x1 + t2 * x0, y1)); }
      if ( t3Val ) { vs.add(new Vec2(x0, ( 1.0f - t3 ) * y1 + t3 * y0)); }

      return vs.toArray(new Vec2[vs.size()]);
   }

   /**
    * Finds points of intersection, if any, between a ray and a line segment.
    *
    * @param ray  the ray
    * @param orig the origin
    * @param dest the destination
    *
    * @return the points
    *
    * @see Ray2#intersectLineSeg(Ray2, Vec2, Vec2)
    */
   public static Vec2[] intersections ( final Ray2 ray, final Vec2 orig,
      final Vec2 dest ) {

      final float t = Ray2.intersectLineSeg(ray, orig, dest);
      final boolean tVal = t != -1.0f;
      final Vec2[] vs = new Vec2[tVal ? 1 : 0];
      if ( tVal ) { vs[0] = Vec2.mix(orig, dest, t, new Vec2()); }
      return vs;
   }

   /**
    * Finds an intersection between a ray and a line segment. Returns -1.0 if
    * there is no intersection. Otherwise, returns a factor in [0.0, 1.0] .
    *
    * @param ray  the ray
    * @param orig the origin
    * @param dest the destination
    *
    * @return the distance
    *
    * @see Ray2#intersectLineSeg(float, float, float, float, float, float,
    *      float, float)
    */
   public static float intersectLineSeg ( final Ray2 ray, final Vec2 orig,
      final Vec2 dest ) {

      return Ray2.intersectLineSeg(ray.origin.x, ray.origin.y, ray.dir.x,
         ray.dir.y, orig.x, orig.y, dest.x, dest.y);
   }

   /**
    * Finds an intersection between a ray and a line segment. Returns -1.0 if
    * there is no intersection. Otherwise, returns a factor in [0.0, 1.0] .
    *
    * @param xRayOrig the ray origin x
    * @param yRayOrig the ray origin y
    * @param xRayDir  the ray direction x
    * @param yRayDir  the ray direction y
    * @param xSegOrig the segment origin x
    * @param ySegOrig the segment origin y
    * @param xSegDest the segment destination x
    * @param ySegDest the segment destination y
    *
    * @return the factor
    */
   static float intersectLineSeg ( final float xRayOrig, final float yRayOrig,
      final float xRayDir, final float yRayDir, final float xSegOrig,
      final float ySegOrig, final float xSegDest, final float ySegDest ) {

      // TODO: Better naming convention to distinguish this from intersections,
      // which returns points.

      /* Subtract destination from origin to get vector. */
      final double v1x = xSegDest - xSegOrig;
      final double v1y = ySegDest - ySegOrig;

      /* Find CCW perpendicular of ray direction. */
      final double v2x = -yRayDir;
      final double v2y = xRayDir;

      /* Find dot product between vector and and perpendicular. */
      final double dot = v1x * v2x + v1y * v2y;
      if ( dot != 0.0d ) {

         /* Find vector from ray origin to segment origin. */
         final double v0x = xRayOrig - xSegOrig;
         final double v0y = yRayOrig - ySegOrig;

         /* Find 2D cross product of v1 and v0, normalize. */
         final double t1 = ( v1x * v0y - v1y * v0x ) / dot;
         if ( t1 > 0.0d ) {
            // TODO: Why is t1 > 0.0 not >= 0.0 and not <= 1.0?

            /* Find dot product of v0 and v2, normalize. */
            final double t2 = ( v0x * v2x + v0y * v2y ) / dot;
            if ( t2 >= 0.0d && t2 <= 1.0d ) { return ( float ) t2; }
         }
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
