package camzup.core;

import java.util.Comparator;
import java.util.List;

/**
 * A direction that extends from an originating point.
 */
public class Ray2 extends Ray {

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

   /**
    * The default constructor.
    */
   public Ray2 ( ) { super(); }

   /**
    * Creates a new ray from a source.
    *
    * @param source the source
    */
   public Ray2 ( final Ray2 source ) {

      super();
      this.set(source);
   }

   /**
    * Creates a new ray from an origin and direction.
    *
    * @param origin the origin
    * @param dir    the direction.
    */
   public Ray2 (
      final Vec2 origin,
      final Vec2 dir ) {

      super();
      this.set(origin, dir);
   }

   /**
    * Returns a new ray with this ray's components. Java's cloneable interface
    * is problematic; use set or a copy constructor instead.
    *
    * @return a new ray
    *
    * @see Ray2#set(Ray2)
    * @see Ray2#Ray2(Ray2)
    */
   @Override
   public Ray2 clone ( ) {

      return new Ray2(this.origin, this.dir);
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
      if ( obj == null ) { return false; }
      if ( this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Ray2 ) obj);
   }

   /**
    * Returns a hash code for this ray based on its origin and direction.
    *
    * @return the hash code
    */
   @Override
   public int hashCode ( ) {

      /*
       * The hash code includes only the ray's origin so that, in a flow field,
       * a ray can be retrieved using its origin as a key.
       */

      int hash = IUtils.HASH_BASE;
      hash = hash * IUtils.HASH_MUL ^ ( this.origin == null ? 0
         : this.origin.hashCode() );
      // hash = (hash * HASH_MUL)
      // ^ (this.dir == null ? 0 : this.dir.hashCode());
      return hash;
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
   public String toString ( ) { return this.toString(4); }

   /**
    * Returns a string representation of this ray.
    *
    * @param places number of decimal places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      /* @formatter:off */
      return new StringBuilder(196)
         .append("{ origin: ")
         .append(this.origin.toString(places))
         .append(", dir: ")
         .append(this.dir.toString(places))
         .append(' ').append('}')
         .toString();
      /* @formatter:on */
   }

   /**
    * Tests equivalence between this and another ray.
    *
    * @param ray the other ray
    *
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
    */
   public static Vec2 eval (
      final Ray2 ray,
      final float time,
      final Vec2 target ) {

      final Vec2 origin = ray.origin;
      final Vec2 dir = ray.dir;

      final float dmSq = Vec2.magSq(dir);
      if ( time <= 0.0f || dmSq == 0.0f ) { return target.set(origin); }

      if ( Utils.approx(dmSq, 1.0f) ) {
         return target.set(origin.x + dir.x * time, origin.y + dir.y * time);
      }

      final float scalar = time * Utils.invSqrtUnchecked(dmSq);
      return target.set(origin.x + dir.x * scalar, origin.y + dir.y * scalar);
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
   public static Ray2 fromPoints (
      final Vec2 origin,
      final Vec2 dest,
      final Ray2 target ) {

      target.origin.set(origin);
      Vec2.subNorm(dest, origin, target.dir);
      return target;
   }

   /**
    * Finds an intersection between a ray and an edge.
    *
    * @param ray  the ray
    * @param edge the edge
    *
    * @return the list of points
    *
    * @see Ray2#intersectLineSeg(Ray2, Vec2, Vec2)
    */
   @Experimental
   public static float intersectEdge (
      final Ray2 ray,
      final Edge2 edge ) {

      return Ray2.intersectLineSeg(ray, edge.origin.coord, edge.dest.coord);
   }

   /**
    * Finds an intersection between a ray and an face.
    *
    * @param ray  the ray
    * @param face the face
    * @param hits the list of points
    *
    * @return the list of points
    */
   @Experimental
   public static float intersectFace (
      final Ray2 ray,
      final Face2 face,
      final List < Vec2 > hits ) {

      // TEST

      final Vec2 v1 = new Vec2();
      final Vec2 v2 = new Vec2();
      final Vec2 v3 = new Vec2();

      final Vert2[] vertices = face.vertices;
      hits.clear();
      final int len = vertices.length;
      float minDist = -1.0f;
      for ( int i = 0; i < len; ++i ) {
         final Vert2 vert0 = vertices[i];
         final Vert2 vert1 = vertices[ ( i + 1 ) % len];

         final Vec2 origin = vert0.coord;
         final Vec2 dest = vert1.coord;

         Vec2.sub(ray.origin, origin, v1);
         Vec2.sub(dest, origin, v2);
         Vec2.perpendicularCCW(ray.dir, v3);

         final float dot = Vec2.dot(v2, v3);
         if ( !Utils.approx(dot, 0.0f) ) {
            final float t1 = Vec2.cross(v2, v1) / dot;
            final float t2 = Vec2.dot(v1, v3) / dot;
            if ( t1 >= 0.0f && t2 >= 0.0f && t2 <= 1.0f ) {
               if ( t1 < minDist ) { minDist = t1; }
               final Vec2 hit = Ray2.eval(ray, t1, new Vec2());
               hits.add(hit);
            }
         }
      }

      return minDist;
   }

   /**
    * Finds an intersection between a ray and a line segment. Returns -1.0 if
    * there is no intersection. Otherwise, returns a value in [0.0, 1.0] .
    *
    * @param ray    the ray
    * @param origin the origin
    * @param dest   the destination
    *
    * @return the list of points
    */
   @Experimental
   public static float intersectLineSeg (
      final Ray2 ray,
      final Vec2 origin,
      final Vec2 dest ) {

      // TEST

      final Vec2 v1 = Vec2.sub(ray.origin, origin, new Vec2());
      final Vec2 v2 = Vec2.sub(dest, origin, new Vec2());
      final Vec2 v3 = Vec2.perpendicularCCW(ray.dir, new Vec2());
      final float dot = Vec2.dot(v2, v3);
      if ( Utils.approx(dot, 0.0f) ) { return -1.0f; }

      final float t1 = Vec2.cross(v2, v1) / dot;
      final float t2 = Vec2.dot(v1, v3) / dot;
      if ( t1 >= 0.0f && t2 >= 0.0f && t2 <= 1.0f ) { return t1; }

      return -1.0f;
   }

   /**
    * Finds the intersection points between a ray and a mesh.
    *
    * @param ray  the ray
    * @param mesh the mesh
    * @param hits the list of points
    *
    * @return the list of points
    */
   @Experimental
   public static float intersectMesh (
      final Ray2 ray,
      final Mesh2 mesh,
      final List < Vec2 > hits ) {

      // TEST

      float minDist = -1.0f;

      final Vec2 v1 = new Vec2();
      final Vec2 v2 = new Vec2();
      final Vec2 v3 = new Vec2();

      final int[][][] faces = mesh.faces;
      final Vec2[] vs = mesh.coords;

      final int len0 = faces.length;
      for ( int i = 0; i < len0; ++i ) {
         final int[][] verts = faces[i];
         final int len1 = verts.length;

         for ( int j = 0; j < len1; ++j ) {
            final int[] vert0 = verts[j];
            final int[] vert1 = verts[ ( j + 1 ) % len1];

            final Vec2 origin = vs[vert0[0]];
            final Vec2 dest = vs[vert1[0]];

            Vec2.sub(ray.origin, origin, v1);
            Vec2.sub(dest, origin, v2);
            Vec2.perpendicularCCW(ray.dir, v3);

            final float dot = Vec2.dot(v2, v3);
            if ( !Utils.approx(dot, 0.0f) ) {
               final float t1 = Vec2.cross(v2, v1) / dot;
               final float t2 = Vec2.dot(v1, v3) / dot;
               if ( t1 >= 0.0f && t2 >= 0.0f && t2 <= 1.0f ) {
                  if ( t1 < minDist ) { minDist = t1; }
                  final Vec2 hit = Ray2.eval(ray, t1, new Vec2());
                  hits.add(hit);
               }
            }
         }
      }

      return minDist;
   }

   /**
    * An abstract class that may serve as an umbrella for any custom
    * comparators of Ray2 s.
    */
   public static abstract class AbstrComparator implements Comparator < Ray2 > {

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
      public abstract int compare ( final Ray2 a, final Ray2 b );

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

}
