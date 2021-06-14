package camzup.core;

/**
 * An axis aligned bounding box (AABB) for a 3D volume, represented with a
 * minimum and maximum coordinate.
 */
public class Bounds3 implements Comparable < Bounds3 > {

   /**
    * The maximum corner.
    */
   public final Vec3 max = new Vec3(-0.5f, -0.5f, -0.5f);

   /**
    * The minimum corner.
    */
   public final Vec3 min = new Vec3(0.5f, 0.5f, 0.5f);

   /**
    * The default constructor.
    */
   public Bounds3 ( ) {}

   /**
    * Constructs a new bounds from a source.
    *
    * @param source the source
    */
   public Bounds3 ( final Bounds3 source ) { this.set(source); }

   /**
    * Creates a bounds from a minimum and maximum.
    *
    * @param min the minimum
    * @param max the maximum
    */
   public Bounds3 ( final float min, final float max ) {

      this.set(min, max);
   }

   /**
    * Creates a bounds from a minimum and maximum.
    *
    * @param xMin the minimum x
    * @param yMin the minimum y
    * @param zMin the minimum z
    * @param xMax the maximum x
    * @param yMax the maximum y
    * @param zMax the maximum z
    */
   public Bounds3 ( final float xMin, final float yMin, final float zMin,
      final float xMax, final float yMax, final float zMax ) {

      this.set(xMin, yMin, zMin, xMax, yMax, zMax);
   }

   /**
    * Creates a bounds from a minimum and maximum.
    *
    * @param min the minimum
    * @param max the maximum
    */
   public Bounds3 ( final Vec3 min, final Vec3 max ) { this.set(min, max); }

   /**
    * Compares two bounds according to their center points. Evaluates the y
    * coordinate before the x coordinate.
    *
    * @return the evaluation
    *
    * @see Utils#diff(float, float)
    */
   @Override
   public int compareTo ( final Bounds3 b ) {

      final float azCenter = Utils.diff(this.max.z, this.min.z) * 0.5f;
      final float bzCenter = Utils.diff(b.max.z, b.min.z) * 0.5f;

      if ( azCenter < bzCenter ) { return -1; }
      if ( azCenter > bzCenter ) { return 1; }

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
    * @see Bounds3#equals(Bounds3)
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null ) { return false; }
      if ( this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Bounds3 ) obj);
   }

   /**
    * Grows this bounds so as to include a point.
    *
    * @param v the point
    *
    * @return this bounds
    *
    * @see Bounds3#growToInclude(Vec3)
    */
   public Bounds3 grow ( final Vec3 v ) { return this.growToInclude(v); }

   /**
    * Grows this bounds so as to include a point.
    *
    * @param v the point
    *
    * @return this bounds
    */
   public Bounds3 growToInclude ( final Vec3 v ) {

      if ( v.x >= this.max.x ) {
         this.max.x = v.x + IUtils.EPSILON;
      } else if ( v.x <= this.min.x ) { this.min.x = v.x - IUtils.EPSILON; }

      if ( v.y >= this.max.y ) {
         this.max.y = v.y + IUtils.EPSILON;
      } else if ( v.y <= this.min.y ) { this.min.y = v.y - IUtils.EPSILON; }

      if ( v.z >= this.max.z ) {
         this.max.z = v.z + IUtils.EPSILON;
      } else if ( v.z <= this.min.z ) { this.min.z = v.z - IUtils.EPSILON; }

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
    * Scales a bounding volume from its center.
    *
    * @param v the scalar
    *
    * @return this bounds
    *
    * @see Bounds3#scale(float, float, float)
    */
   public Bounds3 scale ( final float v ) { return this.scale(v, v, v); }

   /**
    * Scales a bounding volume from its center.
    *
    * @param v the scalar
    *
    * @return this bounds
    *
    * @see Bounds3#scale(float, float, float)
    */
   public Bounds3 scale ( final Vec3 v ) {

      return this.scale(v.x, v.y, v.z);
   }

   /**
    * Sets this bounds to the components of a source bounds.
    *
    * @param source the source
    *
    * @return this bounds
    */
   public Bounds3 set ( final Bounds3 source ) {

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
   public Bounds3 set ( final float min, final float max ) {

      this.min.set(min, min, min);
      this.max.set(max, max, max);

      return this;
   }

   /**
    * Sets the components of this bounding volume.
    *
    * @param xMin the minimum x
    * @param yMin the minimum y
    * @param zMin the minimum z
    * @param xMax the maximum x
    * @param yMax the maximum y
    * @param zMax the maximum z
    *
    * @return this bounds
    */
   public Bounds3 set ( final float xMin, final float yMin, final float zMin,
      final float xMax, final float yMax, final float zMax ) {

      this.min.set(xMin, yMin, zMin);
      this.max.set(xMax, yMax, zMax);

      return this;
   }

   /**
    * Sets the components of this bounding volume.
    *
    * @param min the minimum
    * @param max the maximum
    *
    * @return this bounds
    */
   public Bounds3 set ( final Vec3 min, final Vec3 max ) {

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
    * @see Bounds3#shrinkToExclude(Vec3)
    */
   public Bounds3 shrink ( final Vec3 v ) { return this.shrinkToExclude(v); }

   /**
    * Shrinks this bounds so as to exclude a point.
    *
    * @param v the point
    *
    * @return this bounds
    */
   public Bounds3 shrinkToExclude ( final Vec3 v ) {

      if ( v.x <= this.max.x ) {
         this.max.x = v.x - IUtils.EPSILON;
      } else if ( v.x >= this.min.x ) { this.min.x = v.x + IUtils.EPSILON; }

      if ( v.y <= this.max.y ) {
         this.max.y = v.y - IUtils.EPSILON;
      } else if ( v.y >= this.min.y ) { this.min.y = v.y + IUtils.EPSILON; }

      if ( v.z <= this.max.z ) {
         this.max.z = v.z - IUtils.EPSILON;
      } else if ( v.z >= this.min.z ) { this.min.z = v.z + IUtils.EPSILON; }

      return this;
   }

   /**
    * Returns a string representation of this bounding volume.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(IUtils.FIXED_PRINT); }

   /**
    * Returns a string representation of this bounding volume.
    *
    * @param places the print precision
    *
    * @return the string
    */
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(128);
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
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */
   public Bounds3 translate ( final Vec3 v ) {

      Vec3.add(this.min, v, this.min);
      Vec3.add(this.max, v, this.max);

      return this;
   }

   /**
    * Verifies that all components of minimum are less than those of the
    * maximum, and that the minimums and maximums do not equal each other.
    *
    * @return this bounds
    */
   public Bounds3 verify ( ) {

      final Vec3 mn = this.min;
      final Vec3 mx = this.max;

      final float xMin = mn.x;
      final float yMin = mn.y;
      final float zMin = mn.z;

      final float xMax = mx.x;
      final float yMax = mx.y;
      final float zMax = mx.z;

      mn.x = xMin < xMax ? xMin : xMax;
      mn.y = yMin < yMax ? yMin : yMax;
      mn.z = zMin < zMax ? zMin : zMax;

      mx.x = xMax > xMin ? xMax : xMin;
      mx.y = yMax > yMin ? yMax : yMin;
      mx.z = zMax > zMin ? zMax : zMin;

      if ( mn.x == mx.x ) {
         mn.x -= IUtils.EPSILON;
         mx.x += IUtils.EPSILON;
      }

      if ( mn.y == mx.y ) {
         mn.y -= IUtils.EPSILON;
         mx.y += IUtils.EPSILON;
      }

      if ( mn.z == mx.z ) {
         mn.z -= IUtils.EPSILON;
         mx.z += IUtils.EPSILON;
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
   protected boolean equals ( final Bounds3 b ) {

      return this.min.equals(b.min) && this.max.equals(b.max);
   }

   /**
    * Scales a bounding volume from its center.
    *
    * @param w horizontal scalar
    * @param h vertical scalar
    * @param d the depth scalar
    *
    * @return this bounds
    *
    * @see Utils#diff(float, float)
    * @see Utils#max(float, float)
    */
   protected Bounds3 scale ( final float w, final float h, final float d ) {

      final float xCenter = Utils.diff(this.max.x, this.min.x) * 0.5f;
      final float yCenter = Utils.diff(this.max.y, this.min.y) * 0.5f;
      final float zCenter = Utils.diff(this.max.z, this.min.z) * 0.5f;

      final float vw = Utils.max(IUtils.EPSILON, w);
      final float vh = Utils.max(IUtils.EPSILON, h);
      final float vd = Utils.max(IUtils.EPSILON, d);

      this.min.x = ( this.min.x - xCenter ) * vw + xCenter;
      this.min.y = ( this.min.y - yCenter ) * vh + yCenter;
      this.min.z = ( this.min.z - zCenter ) * vd + zCenter;

      this.max.x = ( this.max.x - xCenter ) * vw + xCenter;
      this.max.y = ( this.max.y - yCenter ) * vh + yCenter;
      this.max.z = ( this.max.z - zCenter ) * vd + zCenter;

      return this;
   }

   /**
    * Finds the center of a bounding box.
    *
    * @param b      the bounding box
    * @param target the output vector
    *
    * @return the center
    *
    * @see Vec3#mul(Vec3, float, Vec3)
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */
   public static Vec3 center ( final Bounds3 b, final Vec3 target ) {

      return Vec3.mul(Vec3.add(b.min, b.max, target), 0.5f, target);
   }

   /**
    * Evaluates whether a point is within the bounding volume, lower bounds
    * inclusive, upper bounds exclusive. For cases where multiple bounds must
    * cover an volume without overlap or gaps.
    *
    * @param b the bounds
    * @param v the vector
    *
    * @return the evaluation
    */
   public static boolean contains ( final Bounds3 b, final Vec3 v ) {

      return v.x >= b.min.x && v.x < b.max.x && v.y >= b.min.y && v.y < b.max.y
         && v.z >= b.min.z && v.z < b.max.z;
   }

   /**
    * Evaluates whether a point is within the bounding volume, lower bounds
    * inclusive upper bounds exclusive. For cases where multiple bounds must
    * cover an volume without overlap or gaps.<br>
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
   public static Vec3 contains ( final Bounds3 b, final Vec3 v,
      final Vec3 target ) {

      return target.set(v.x >= b.min.x && v.x < b.max.x, v.y >= b.min.y && v.y
         < b.max.y, v.z >= b.min.z && v.z < b.max.z);
   }

   /**
    * Evaluates whether a point is within the bounding volume, excluding the
    * bound's edges (i.e., the evaluation is made with greater than and less
    * than).
    *
    * @param b the bounds
    * @param v the point
    *
    * @return the evaluation
    */
   public static boolean containsExclusive ( final Bounds3 b, final Vec3 v ) {

      return v.x > b.min.x && v.x < b.max.x && v.y > b.min.y && v.y < b.max.y
         && v.z > b.min.z && v.z < b.max.z;
   }

   /**
    * Evaluates whether a point is within the bounding volume, excluding the
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
   public static Vec3 containsExclusive ( final Bounds3 b, final Vec3 v,
      final Vec3 target ) {

      return target.set(v.x > b.min.x && v.x < b.max.x, v.y > b.min.y && v.y
         < b.max.y, v.z > b.min.z && v.z < b.max.z);
   }

   /**
    * Evaluates whether a point is within the bounding volume, including the
    * bound's edges (i.e., the evaluation is made with greater than or equal
    * to and less than or equal to).
    *
    * @param b the bounds
    * @param v the point
    *
    * @return the evaluation
    */
   public static boolean containsInclusive ( final Bounds3 b, final Vec3 v ) {

      return v.x >= b.min.x && v.x <= b.max.x && v.y >= b.min.y && v.y
         <= b.max.y && v.z >= b.min.z && v.z <= b.max.z;
   }

   /**
    * Evaluates whether a point is within the bounding volume, including the
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
   public static Vec3 containsInclusive ( final Bounds3 b, final Vec3 v,
      final Vec3 target ) {

      return target.set(v.x >= b.min.x && v.x <= b.max.x, v.y >= b.min.y && v.y
         <= b.max.y, v.z >= b.min.z && v.z <= b.max.z);
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
    * @see Vec3#diff(Vec3, Vec3, Vec3)
    */
   public static Vec3 extent ( final Bounds3 b, final Vec3 target ) {

      return Vec3.diff(b.max, b.min, target);
   }

   /**
    * Creates a bounding volume from a center and the volume's extent.
    *
    * @param center the center
    * @param extent the extent
    * @param target the output bounds
    *
    * @return the bounds
    */
   public static Bounds3 fromCenterExtent ( final Vec3 center,
      final Vec3 extent, final Bounds3 target ) {

      final float hw = extent.x * 0.5f;
      final float hh = extent.y * 0.5f;
      final float hd = extent.z * 0.5f;

      return target.set(center.x - hw, center.y - hh, center.z - hd, center.x
         + hw, center.y + hh, center.z + hd);
   }

   /**
    * Creates a bounding volume from a center and half the volume's extent.
    *
    * @param center the center
    * @param he     half the extent
    * @param target the output bounds
    *
    * @return the bounds
    */
   public static Bounds3 fromCenterHalfExtent ( final Vec3 center,
      final Vec3 he, final Bounds3 target ) {

      return target.set(center.x - he.x, center.y - he.y, center.z - he.z,
         center.x + he.x, center.y + he.y, center.z + he.z);
   }

   /**
    * Sets a bounding volume to encompass an array of points.
    *
    * @param points the points
    * @param target the output volume
    *
    * @return the volume
    */
   public static Bounds3 fromPoints ( final Vec3[] points,
      final Bounds3 target ) {

      final Vec3 lb = target.min;
      final Vec3 ub = target.max;

      lb.set(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
      ub.set(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);

      final int len = points.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec3 p = points[i];
         final float x = p.x;
         final float y = p.y;
         final float z = p.z;

         /* Minimum, maximum need separate if checks, not if-else. */
         if ( x < lb.x ) { lb.x = x; }
         if ( x > ub.x ) { ub.x = x; }
         if ( y < lb.y ) { lb.y = y; }
         if ( y > ub.y ) { ub.y = y; }
         if ( z < lb.z ) { lb.z = z; }
         if ( z > ub.z ) { ub.z = z; }
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
    * @see Bounds3#extent(Bounds3, Vec3)
    * @see Vec3#mul(Vec3, float, Vec3)
    */
   public static Vec3 halfExtent ( final Bounds3 b, final Vec3 target ) {

      return Vec3.mul(Bounds3.extent(b, target), 0.5f, target);
   }

   /**
    * Evaluates whether two bounding volumes intersect.
    *
    * @param a left comparisand
    * @param b right comparisand
    *
    * @return the evaluation
    */
   public static boolean intersect ( final Bounds3 a, final Bounds3 b ) {

      return a.max.x > b.min.x || a.min.x < b.max.x || a.max.y > b.min.y
         || a.min.y < b.max.y || a.max.z > b.min.z || a.min.z < b.max.z;
   }

   /**
    * Evaluates whether two bounding volumes intersect. A boolean vector holds
    * the evaluation.
    *
    * @param a      left comparisand
    * @param b      right comparisand
    * @param target the output vector
    *
    * @return the evaluation
    */
   public static Vec3 intersect ( final Bounds3 a, final Bounds3 b,
      final Vec3 target ) {

      return target.set(a.max.x > b.min.x || a.min.x < b.max.x, a.max.y
         > b.min.y || a.min.y < b.max.y, a.max.z > b.min.z || a.min.z
            < b.max.z);
   }

   /**
    * Evaluates whether a bounding area intersects a sphere.
    *
    * @param a      the bounding area
    * @param origin the sphere origin
    * @param radius the sphere radius
    *
    * @return the evaluation
    */
   public static boolean intersect ( final Bounds3 a, final Vec3 origin,
      final float radius ) {

      final float xDist = origin.x < a.min.x ? origin.x - a.min.x : origin.x
         > origin.x - a.max.x ? a.max.x : 0.0f;
      final float yDist = origin.y < a.min.y ? origin.y - a.min.y : origin.y
         > origin.y - a.max.y ? a.max.y : 0.0f;
      final float zDist = origin.z < a.min.z ? origin.z - a.min.z : origin.z
         > origin.z - a.max.z ? a.max.z : 0.0f;
      return Utils.sqrtUnchecked(xDist * xDist + yDist * yDist + zDist * zDist)
         < radius;
   }

   /**
    * Splits a bounding volume into octants.
    *
    * @param b   the bounds
    * @param bsw back south west
    * @param bse back south east
    * @param bnw back north west
    * @param bne back north east
    * @param fsw front south west
    * @param fse front south east
    * @param fnw front north west
    * @param fne front north east
    *
    * @see Bounds3#split(Bounds3, float, float, float, Bounds3, Bounds3,
    *      Bounds3, Bounds3, Bounds3, Bounds3, Bounds3, Bounds3)
    */
   public static void split ( final Bounds3 b, final Bounds3 bsw,
      final Bounds3 bse, final Bounds3 bnw, final Bounds3 bne,
      final Bounds3 fsw, final Bounds3 fse, final Bounds3 fnw,
      final Bounds3 fne ) {

      Bounds3.split(b, 0.5f, 0.5f, 0.5f, bsw, bse, bnw, bne, fsw, fse, fnw,
         fne);
   }

   /**
    * Splits a bounding volume into eight octants according to three factors
    * in the range [0.0, 1.0] . The factor on the x axis governs the vertical
    * split; on the y axis, the horizontal split; on the z axis, the depth
    * split.
    *
    * @param b    the bounds
    * @param xFac vertical factor
    * @param yFac horizontal factor
    * @param zFac depth factor
    * @param bsw  back south west
    * @param bse  back south east
    * @param bnw  back north west
    * @param bne  back north east
    * @param fsw  front south west
    * @param fse  front south east
    * @param fnw  front north west
    * @param fne  front north east
    *
    * @see Utils#clamp(float, float, float)
    */
   public static void split ( final Bounds3 b, final float xFac,
      final float yFac, final float zFac, final Bounds3 bsw, final Bounds3 bse,
      final Bounds3 bnw, final Bounds3 bne, final Bounds3 fsw,
      final Bounds3 fse, final Bounds3 fnw, final Bounds3 fne ) {

      final Vec3 bMin = b.min;
      final Vec3 bMax = b.max;

      final float tx = Utils.clamp(xFac, IUtils.EPSILON, 1.0f - IUtils.EPSILON);
      final float ty = Utils.clamp(yFac, IUtils.EPSILON, 1.0f - IUtils.EPSILON);
      final float tz = Utils.clamp(zFac, IUtils.EPSILON, 1.0f - IUtils.EPSILON);

      final float x = ( 1.0f - tx ) * bMin.x + tx * bMax.x;
      final float y = ( 1.0f - ty ) * bMin.y + ty * bMax.y;
      final float z = ( 1.0f - tz ) * bMin.z + tz * bMax.z;

      /* @formatter:off */
      bsw.set(bMin.x, bMin.y, bMin.z,      x,      y,      z);
      bse.set(     x, bMin.y, bMin.z, bMax.x,      y,      z);
      bnw.set(bMin.x,      y, bMin.z,      x, bMax.y,      z);
      bne.set(     x,      y, bMin.z, bMax.x, bMax.y,      z);
      fsw.set(bMin.x, bMin.y,      z,      x,      y, bMax.z);
      fse.set(     x, bMin.y,      z, bMax.x,      y, bMax.z);
      fnw.set(bMin.x,      y,      z,      x, bMax.y, bMax.z);
      fne.set(     x,      y,      z, bMax.x, bMax.y, bMax.z);
      /* @formatter:on */
   }

   /**
    * Splits a bounding volume into eight octants according to a point. If the
    * point is inside the bounding volume, assigns the result to target bounds
    * and returns <code>true</code>. If the point is outside the bounding
    * volume, returns <code>false</code>.
    *
    * @param b   the bounds
    * @param v   the point
    * @param bsw back south west
    * @param bse back south east
    * @param bnw back north west
    * @param bne back north east
    * @param fsw front south west
    * @param fse front south east
    * @param fnw front north west
    * @param fne front north east
    *
    * @return point is in bounds
    *
    * @see Utils#div(float, float)
    * @see Bounds3#split(Bounds3, float, float, float, Bounds3, Bounds3,
    *      Bounds3, Bounds3, Bounds3, Bounds3, Bounds3, Bounds3)
    */
   public static boolean split ( final Bounds3 b, final Vec3 v,
      final Bounds3 bsw, final Bounds3 bse, final Bounds3 bnw,
      final Bounds3 bne, final Bounds3 fsw, final Bounds3 fse,
      final Bounds3 fnw, final Bounds3 fne ) {

      final Vec3 bMin = b.min;
      final Vec3 bMax = b.max;

      if ( v.x > bMin.x && v.x < bMax.x && v.y > bMin.y && v.y < bMax.y && v.z
         > bMin.z && v.z < bMax.z ) {
         final float xFac = Utils.div(v.x - bMin.x, bMax.x - bMin.x);
         final float yFac = Utils.div(v.y - bMin.y, bMax.y - bMin.y);
         final float zFac = Utils.div(v.z - bMin.z, bMax.z - bMin.z);
         Bounds3.split(b, xFac, yFac, zFac, bsw, bse, bnw, bne, fsw, fse, fnw,
            fne);
         return true;
      }

      return false;
   }

   /**
    * Finds the volume of the bounds.
    *
    * @param b the bounds
    *
    * @return the volume
    *
    * @see Utils#diff(float, float)
    */
   public static float volume ( final Bounds3 b ) {

      return Utils.diff(b.min.x, b.max.x) * Utils.diff(b.min.y, b.max.y) * Utils
         .diff(b.min.z, b.max.z);
   }

}
