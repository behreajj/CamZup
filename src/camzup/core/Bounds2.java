package camzup.core;

/**
 * An axis aligned bounding box (AABB) for a 2D area, represented with a
 * minimum and maximum coordinate.
 */
public class Bounds2 implements Comparable < Bounds2 > {

   /**
    * The maximum corner.
    */
   public final Vec2 max = new Vec2(0.5f, 0.5f);

   /**
    * The minimum corner.
    */
   public final Vec2 min = new Vec2(-0.5f, -0.5f);

   /**
    * The default constructor.
    */
   public Bounds2 ( ) {}

   /**
    * Constructs a new bounds from a source.
    *
    * @param source the source
    */
   public Bounds2 ( final Bounds2 source ) { this.set(source); }

   /**
    * Creates a bounds from a minimum and maximum.
    *
    * @param min the minimum
    * @param max the maximum
    */
   public Bounds2 ( final float min, final float max ) {

      this.set(min, max);
   }

   /**
    * Creates a bounds from a minimum and maximum.
    *
    * @param xMin the minimum x
    * @param yMin the minimum y
    * @param xMax the maximum x
    * @param yMax the maximum y
    */
   public Bounds2 ( final float xMin, final float yMin, final float xMax,
      final float yMax ) {

      this.set(xMin, yMin, xMax, yMax);
   }

   /**
    * Creates a bounds from a minimum and maximum.
    *
    * @param min the minimum
    * @param max the maximum
    */
   public Bounds2 ( final Vec2 min, final Vec2 max ) { this.set(min, max); }

   /**
    * Compares two bounds according to their center points. Evaluates the y
    * coordinate before the x coordinate.
    *
    * @return the evaluation
    *
    * @see Utils#diff(float, float)
    */
   @Override
   public int compareTo ( final Bounds2 b ) {

      final float ayCenter = Utils.diff(this.max.y, this.min.y) * 0.5f;
      final float byCenter = Utils.diff(b.max.y, b.min.y) * 0.5f;

      if ( ayCenter < byCenter ) { return -1; }
      if ( ayCenter > byCenter ) { return 1; }

      final float axCenter = Utils.diff(this.max.x, this.min.x) * 0.5f;
      final float bxCenter = Utils.diff(b.max.x, b.min.x) * 0.5f;

      if ( axCenter < bxCenter ) { return -1; }
      if ( axCenter > bxCenter ) { return 1; }

      return 0;
   }

   /**
    * Tests this bounds for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the equivalence
    *
    * @see Bounds2#equals(Bounds2)
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null || this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Bounds2 ) obj);
   }

   /**
    * Grows this bounds so as to include a point.
    *
    * @param v the point
    *
    * @return this bounds
    *
    * @see Bounds2#growToInclude(Vec2)
    */
   public Bounds2 grow ( final Vec2 v ) { return this.growToInclude(v); }

   /**
    * Grows this bounds so as to include a point.
    *
    * @param v the point
    *
    * @return this bounds
    */
   public Bounds2 growToInclude ( final Vec2 v ) {

      if ( v.x >= this.max.x ) {
         this.max.x = v.x + IUtils.EPSILON;
      } else if ( v.x <= this.min.x ) { this.min.x = v.x - IUtils.EPSILON; }

      if ( v.y >= this.max.y ) {
         this.max.y = v.y + IUtils.EPSILON;
      } else if ( v.y <= this.min.y ) { this.min.y = v.y - IUtils.EPSILON; }

      return this;
   }

   /**
    * Returns a hash code for this bounds based on its minimum and maximum.
    *
    * @return the hash code
    */
   @Override
   public int hashCode ( ) {

      int hash = IUtils.HASH_BASE;
      hash = hash * IUtils.HASH_MUL ^ this.min.hashCode();
      hash = hash * IUtils.HASH_MUL ^ this.max.hashCode();
      return hash;
   }

   /**
    * Scales a bounding area from its center.
    *
    * @param v the scalar
    *
    * @return this bounds
    *
    * @see Bounds2#scale(float, float)
    */
   public Bounds2 scale ( final float v ) { return this.scale(v, v); }

   /**
    * Scales a bounding area from its center.
    *
    * @param v the scalar
    *
    * @return this bounds
    *
    * @see Bounds2#scale(float, float)
    */
   public Bounds2 scale ( final Vec2 v ) { return this.scale(v.x, v.y); }

   /**
    * Sets this bounds to the components of a source bounds.
    *
    * @param source the source
    *
    * @return this bounds
    */
   public Bounds2 set ( final Bounds2 source ) {

      this.min.set(source.min);
      this.max.set(source.max);

      return this;
   }

   /**
    * Sets the components of this bounding area with scalars.
    *
    * @param min the minimum
    * @param max the maximum
    *
    * @return this bounds
    */
   public Bounds2 set ( final float min, final float max ) {

      this.min.set(min, min);
      this.max.set(max, max);

      return this;
   }

   /**
    * Sets the components of this bounding area.
    *
    * @param xMin the minimum x
    * @param yMin the minimum y
    * @param xMax the maximum x
    * @param yMax the maximum y
    *
    * @return this bounds
    */
   public Bounds2 set ( final float xMin, final float yMin, final float xMax,
      final float yMax ) {

      this.min.set(xMin, yMin);
      this.max.set(xMax, yMax);

      return this;
   }

   /**
    * Sets the components of this bounding area.
    *
    * @param min the minimum
    * @param max the maximum
    *
    * @return this bounds
    */
   public Bounds2 set ( final Vec2 min, final Vec2 max ) {

      this.min.set(min);
      this.max.set(max);

      return this;
   }

   /**
    * Shrinks this bounds so as to exclude a point.
    *
    * @param v the point
    *
    * @return this bounds
    *
    * @see Bounds2#shrinkToExclude(Vec2)
    */
   public Bounds2 shrink ( final Vec2 v ) { return this.shrinkToExclude(v); }

   /**
    * Shrinks this bounds so as to exclude a point.
    *
    * @param v the point
    *
    * @return this bounds
    */
   public Bounds2 shrinkToExclude ( final Vec2 v ) {

      if ( v.x <= this.max.x ) {
         this.max.x = v.x - IUtils.EPSILON;
      } else if ( v.x >= this.min.x ) { this.min.x = v.x + IUtils.EPSILON; }

      if ( v.y <= this.max.y ) {
         this.max.y = v.y - IUtils.EPSILON;
      } else if ( v.y >= this.min.y ) { this.min.y = v.y + IUtils.EPSILON; }

      return this;
   }

   /**
    * Returns a string representation of this bounding area.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(IUtils.FIXED_PRINT); }

   /**
    * Returns a string representation of this bounding area.
    *
    * @param places the print precision
    *
    * @return the string
    */
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(96);
      sb.append("{ min: ");
      this.min.toString(sb, places);
      sb.append(", max: ");
      this.max.toString(sb, places);
      sb.append(' ');
      sb.append('}');
      return sb.toString();
   }

   /**
    * Translates this bounds by a vector.
    *
    * @param v the vector
    *
    * @return this bounds
    *
    * @see Vec2#add(Vec2, Vec2, Vec2)
    */
   public Bounds2 translate ( final Vec2 v ) {

      Vec2.add(this.min, v, this.min);
      Vec2.add(this.max, v, this.max);

      return this;
   }

   /**
    * Verifies that all components of minimum are less than those of the
    * maximum, and that the minimums and maximums do not equal each other.
    *
    * @return this bounds
    */
   public Bounds2 verify ( ) {

      final Vec2 mn = this.min;
      final Vec2 mx = this.max;

      final float xMin = mn.x;
      final float yMin = mn.y;

      final float xMax = mx.x;
      final float yMax = mx.y;

      mn.x = xMin < xMax ? xMin : xMax;
      mn.y = yMin < yMax ? yMin : yMax;

      mx.x = xMax > xMin ? xMax : xMin;
      mx.y = yMax > yMin ? yMax : yMin;

      if ( mn.x == mx.x ) {
         mn.x -= IUtils.EPSILON;
         mx.x += IUtils.EPSILON;
      }

      if ( mn.y == mx.y ) {
         mn.y -= IUtils.EPSILON;
         mx.y += IUtils.EPSILON;
      }

      return this;
   }

   /**
    * Tests equivalence between this and another bounds
    *
    * @param b the other bounds
    *
    * @return the evaluation
    */
   protected boolean equals ( final Bounds2 b ) {

      return this.min.equals(b.min) && this.max.equals(b.max);
   }

   /**
    * Scales a bounding area from its center.
    *
    * @param w horizontal scalar
    * @param h vertical scalar
    *
    * @return this bounds
    *
    * @see Utils#diff(float, float)
    * @see Utils#max(float, float)
    */
   protected Bounds2 scale ( final float w, final float h ) {

      final float xCenter = Utils.diff(this.max.x, this.min.x) * 0.5f;
      final float yCenter = Utils.diff(this.max.y, this.min.y) * 0.5f;

      final float vw = Utils.max(IUtils.EPSILON, w);
      final float vh = Utils.max(IUtils.EPSILON, h);

      this.min.x = ( this.min.x - xCenter ) * vw + xCenter;
      this.min.y = ( this.min.y - yCenter ) * vh + yCenter;

      this.max.x = ( this.max.x - xCenter ) * vw + xCenter;
      this.max.y = ( this.max.y - yCenter ) * vh + yCenter;

      return this;
   }

   /**
    * Finds the area of the bounds.
    *
    * @param b the bounds
    *
    * @return the area
    *
    * @see Utils#diff(float, float)
    */
   public static float area ( final Bounds2 b ) {

      return Utils.diff(b.min.x, b.max.x) * Utils.diff(b.min.y, b.max.y);
   }

   /**
    * Finds the center of a bounding box.
    *
    * @param b      the bounding box
    * @param target the output vector
    *
    * @return the center
    *
    * @see Vec2#mul(Vec2, float, Vec2)
    * @see Vec2#add(Vec2, Vec2, Vec2)
    */
   public static Vec2 center ( final Bounds2 b, final Vec2 target ) {

      return Vec2.mul(Vec2.add(b.min, b.max, target), 0.5f, target);
   }

   /**
    * Evaluates whether a point is within the bounding area, lower bounds
    * inclusive, upper bounds exclusive. For cases where multiple bounds must
    * cover an area without overlap or gaps.
    *
    * @param b the bounds
    * @param v the vector
    *
    * @return the evaluation
    */
   public static boolean contains ( final Bounds2 b, final Vec2 v ) {

      return v.x >= b.min.x && v.x < b.max.x && v.y >= b.min.y && v.y < b.max.y;
   }

   /**
    * Evaluates whether a point is within the bounding area, lower bounds
    * inclusive upper bounds exclusive. For cases where multiple bounds must
    * cover an area without overlap or gaps.<br>
    * <br>
    * A boolean vector is returned; useful for cases where a point may be
    * contained in one dimension but not in another.
    *
    * @param b      the bounds
    * @param v      the vector
    * @param target the output vector
    *
    * @return the evaluation
    */
   public static Vec2 contains ( final Bounds2 b, final Vec2 v,
      final Vec2 target ) {

      return target.set(v.x >= b.min.x && v.x < b.max.x, v.y >= b.min.y && v.y
         < b.max.y);
   }

   /**
    * Evaluates whether a point is within the bounding area, excluding the
    * bound's edges (i.e., the evaluation is made with greater than and less
    * than).
    *
    * @param b the bounds
    * @param v the point
    *
    * @return the evaluation
    */
   public static boolean containsExclusive ( final Bounds2 b, final Vec2 v ) {

      return v.x > b.min.x && v.x < b.max.x && v.y > b.min.y && v.y < b.max.y;
   }

   /**
    * Evaluates whether a point is within the bounding area, excluding the
    * bound's edges (i.e., the evaluation is made with greater than and less
    * than).<br>
    * <br>
    * A boolean vector is returned; useful for cases where a point may be
    * contained in one dimension but not in another.
    *
    * @param b      the bounds
    * @param v      the point
    * @param target the output vector
    *
    * @return the evaluation
    */
   public static Vec2 containsExclusive ( final Bounds2 b, final Vec2 v,
      final Vec2 target ) {

      return target.set(v.x > b.min.x && v.x < b.max.x, v.y > b.min.y && v.y
         < b.max.y);
   }

   /**
    * Evaluates whether a point is within the bounding area, including the
    * bound's edges (i.e., the evaluation is made with greater than or equal
    * to and less than or equal to).
    *
    * @param b the bounds
    * @param v the point
    *
    * @return the evaluation
    */
   public static boolean containsInclusive ( final Bounds2 b, final Vec2 v ) {

      return v.x >= b.min.x && v.x <= b.max.x && v.y >= b.min.y && v.y
         <= b.max.y;
   }

   /**
    * Evaluates whether a point is within the bounding area, including the
    * bound's edges (i.e., the evaluation is made with greater than or equal
    * to and less than or equal to).<br>
    * <br>
    * A boolean vector is returned; useful for cases where a point may be
    * contained in one dimension but not in another.
    *
    * @param b      the bounds
    * @param v      the point
    * @param target the output vector
    *
    * @return the evaluation
    */
   public static Vec2 containsInclusive ( final Bounds2 b, final Vec2 v,
      final Vec2 target ) {

      return target.set(v.x >= b.min.x && v.x <= b.max.x, v.y >= b.min.y && v.y
         <= b.max.y);
   }

   /**
    * Finds the extent of the bounds, the absolute difference between its
    * minimum and maximum corners.
    *
    * @param b      the bounds
    * @param target the output vector
    *
    * @return the extent
    *
    * @see Vec2#diff(Vec2, Vec2, Vec2)
    */
   public static Vec2 extent ( final Bounds2 b, final Vec2 target ) {

      return Vec2.diff(b.max, b.min, target);
   }

   /**
    * Creates a bounding area from a center and the area's extent.
    *
    * @param center the center
    * @param extent the extent
    * @param target the output bounds
    *
    * @return the bounds
    */
   public static Bounds2 fromCenterExtent ( final Vec2 center,
      final Vec2 extent, final Bounds2 target ) {

      final float hw = extent.x * 0.5f;
      final float hh = extent.y * 0.5f;
      return target.set(center.x - hw, center.y - hh, center.x + hw, center.y
         + hh);
   }

   /**
    * Creates a bounding area from a center and half the area's extent.
    *
    * @param center the center
    * @param he     half the extent
    * @param target the output bounds
    *
    * @return the bounds
    */
   public static Bounds2 fromCenterHalfExtent ( final Vec2 center,
      final Vec2 he, final Bounds2 target ) {

      return target.set(center.x - he.x, center.y - he.y, center.x + he.x,
         center.y + he.y);
   }

   /**
    * Sets a bounding volume to encompass an array of points.
    *
    * @param points the points
    * @param target the output volume
    *
    * @return the volume
    */
   public static Bounds2 fromPoints ( final Vec2[] points,
      final Bounds2 target ) {

      final Vec2 lb = target.min;
      final Vec2 ub = target.max;

      lb.set(Float.MAX_VALUE, Float.MAX_VALUE);
      ub.set(Float.MIN_VALUE, Float.MIN_VALUE);

      final int len = points.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec2 p = points[i];
         final float x = p.x;
         final float y = p.y;

         /* Minimum, maximum need separate if checks, not if-else. */
         if ( x < lb.x ) { lb.x = x; }
         if ( x > ub.x ) { ub.x = x; }
         if ( y < lb.y ) { lb.y = y; }
         if ( y > ub.y ) { ub.y = y; }
      }

      return target;
   }

   /**
    * Finds half the extent of the bounds.
    *
    * @param b      the bounds
    * @param target the output vector
    *
    * @return the extent
    *
    * @see Bounds2#extent(Bounds2, Vec2)
    * @see Vec2#mul(Vec2, float, Vec2)
    */
   public static Vec2 halfExtent ( final Bounds2 b, final Vec2 target ) {

      return Vec2.mul(Bounds2.extent(b, target), 0.5f, target);
   }

   /**
    * Evaluates whether two bounding areas intersect.
    *
    * @param a left comparisand
    * @param b right comparisand
    *
    * @return the evaluation
    */
   public static boolean intersect ( final Bounds2 a, final Bounds2 b ) {

      return a.max.x > b.min.x || a.min.x < b.max.x || a.max.y > b.min.y
         || a.min.y < b.max.y;
   }

   /**
    * Evaluates whether two bounding areas intersect. A boolean vector holds
    * the evaluation.
    *
    * @param a      left comparisand
    * @param b      right comparisand
    * @param target the output vector
    *
    * @return the evaluation
    */
   public static Vec2 intersect ( final Bounds2 a, final Bounds2 b,
      final Vec2 target ) {

      return target.set(a.max.x > b.min.x || a.min.x < b.max.x, a.max.y
         > b.min.y || a.min.y < b.max.y);
   }

   /**
    * Evaluates whether a bounding area intersects a circle. Intersection is
    * boundary inclusive.
    *
    * @param a      the bounding area
    * @param origin the circle origin
    * @param radius the circle radius
    *
    * @return the evaluation
    */
   public static boolean intersect ( final Bounds2 a, final Vec2 origin,
      final float radius ) {

      // TODO: Fix this, it is probably wrong.
      final float xDist = origin.x < a.min.x ? origin.x - a.min.x : origin.x
         > origin.x - a.max.x ? a.max.x : 0.0f;
      final float yDist = origin.y < a.min.y ? origin.y - a.min.y : origin.y
         > origin.y - a.max.y ? a.max.y : 0.0f;

      return xDist * xDist + yDist * yDist < radius * radius;
   }

   /**
    * Splits a bounding area into quadrants.
    *
    * @param b  the bounds
    * @param sw south west target bounds
    * @param se south east target bounds
    * @param nw north west target bounds
    * @param ne north east target bounds
    *
    * @see Bounds2#split(Bounds2, float, float, Bounds2, Bounds2, Bounds2,
    *      Bounds2)
    */
   public static void split ( final Bounds2 b, final Bounds2 sw,
      final Bounds2 se, final Bounds2 nw, final Bounds2 ne ) {

      Bounds2.split(b, 0.5f, 0.5f, sw, se, nw, ne);
   }

   /**
    * Splits a bounding area into four quadrants according to two factors in
    * [0.0, 1.0] . The factor on the x axis governs the vertical split; on the
    * y axis, the horizontal split.
    *
    * @param b    bounds
    * @param xFac vertical factor
    * @param yFac horizontal factor
    * @param sw   south west target bounds
    * @param se   south east target bounds
    * @param nw   north west target bounds
    * @param ne   north east target bounds
    *
    * @see Utils#clamp(float, float, float)
    */
   public static void split ( final Bounds2 b, final float xFac,
      final float yFac, final Bounds2 sw, final Bounds2 se, final Bounds2 nw,
      final Bounds2 ne ) {

      final Vec2 bMin = b.min;
      final Vec2 bMax = b.max;

      final float tx = Utils.clamp(xFac, IUtils.EPSILON, 1.0f - IUtils.EPSILON);
      final float ty = Utils.clamp(yFac, IUtils.EPSILON, 1.0f - IUtils.EPSILON);

      final float x = ( 1.0f - tx ) * bMin.x + tx * bMax.x;
      final float y = ( 1.0f - ty ) * bMin.y + ty * bMax.y;

      /* @formatter:off */
      sw.set(bMin.x, bMin.y,      x,      y);
      se.set(     x, bMin.y, bMax.x,      y);
      nw.set(bMin.x,      y,      x, bMax.y);
      ne.set(     x,      y, bMax.x, bMax.y);
      /* @formatter:on */
   }

   /**
    * Splits a bounding area into four quadrants according to a point. If the
    * point is inside the bounding area, assigns the result to target bounds
    * and returns <code>true</code>. If the point is outside the bounding
    * area, returns <code>false</code>.
    *
    * @param b  the bounds
    * @param v  the point
    * @param sw south west target bounds
    * @param se south east target bounds
    * @param nw north west target bounds
    * @param ne north east target bounds
    *
    * @return point is in bounds
    *
    * @see Utils#div(float, float)
    * @see Bounds2#split(Bounds2, float, float, Bounds2, Bounds2, Bounds2,
    *      Bounds2)
    */
   public static boolean split ( final Bounds2 b, final Vec2 v,
      final Bounds2 sw, final Bounds2 se, final Bounds2 nw, final Bounds2 ne ) {

      final Vec2 bMin = b.min;
      final Vec2 bMax = b.max;

      if ( v.x > bMin.x && v.x < bMax.x && v.y > bMin.y && v.y < bMax.y ) {
         final float xFac = Utils.div(v.x - bMin.x, bMax.x - bMin.x);
         final float yFac = Utils.div(v.y - bMin.y, bMax.y - bMin.y);
         Bounds2.split(b, xFac, yFac, sw, se, nw, ne);
         return true;
      }

      return false;
   }

}
