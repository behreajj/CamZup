package camzup.core;

/**
 * Organizes components of a 2D mesh into an edge with an origin and
 * destination. This is not used by a mesh internally; it is created upon
 * retrieval from a mesh.
 */
public class Edge2 implements Comparable < Edge2 > {

   /**
    * The destination vertex.
    */
   public Vert2 dest;

   /**
    * The origin vertex.
    */
   public Vert2 origin;

   /**
    * The default constructor. Creates two empty vertices.
    */
   public Edge2 ( ) {

      this.origin = new Vert2();
      this.dest = new Vert2();
   }

   /**
    * Constructs an edge from the origin and destination coordinate and
    * texture coordinate data. Creates two vertex objects.`
    *
    * @param coOrigin origin coordinate
    * @param txOrigin origin texture coordinate
    * @param coDest   destination coordinate
    * @param txDest   destination texture coordinate
    */
   public Edge2 ( final Vec2 coOrigin, final Vec2 txOrigin, final Vec2 coDest,
      final Vec2 txDest ) {

      this.origin = new Vert2(coOrigin, txOrigin);
      this.dest = new Vert2(coDest, txDest);
   }

   /**
    * Constructs an edge from two vertices, an origin and destination.
    *
    * @param origin the origin
    * @param dest   the destination
    */
   public Edge2 ( final Vert2 origin, final Vert2 dest ) {

      this.set(origin, dest);
   }

   /**
    * Compares two edges based on their identity hash codes.
    *
    * @return the evaluation
    */
   @Override
   public int compareTo ( final Edge2 edge ) {

      final int a = System.identityHashCode(this);
      final int b = System.identityHashCode(edge);
      return a < b ? -1 : a > b ? 1 : 0;
   }

   /**
    * Tests this edge for equivalence with another object.
    *
    * @return the evaluation
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null || this.getClass() != obj.getClass() ) { return false; }
      return this.equalsDirected(( Edge2 ) obj);
   }

   /**
    * Returns a hash code for this edge based on its origin and destination.
    *
    * @return the hash
    */
   @Override
   public int hashCode ( ) {

      return ( IUtils.MUL_BASE ^ ( this.origin == null ? 0 : this.origin
         .hashCode() ) ) * IUtils.HASH_MUL ^ ( this.dest == null ? 0 : this.dest
            .hashCode() );
   }

   /**
    * Rotates the coordinates of this edge by an angle in radians around the z
    * axis. The texture coordinates are unaffected.
    *
    * @param radians angle
    *
    * @return this edge
    *
    * @see Edge2#rotateZGlobal(float)
    */
   public Edge2 rotateZ ( final float radians ) {

      return this.rotateZGlobal(radians);
   }

   /**
    * Rotates the coordinates of this edge by an angle in radians around the z
    * axis. Uses global coordinates, i.e. doesn't consider the edge's
    * position. The texture coordinates are unaffected.
    *
    * @param radians the angle
    *
    * @return this edge
    *
    * @see Edge2#rotateZGlobal(float, float)
    */
   public Edge2 rotateZGlobal ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

      return this.rotateZGlobal(cosa, sina);
   }

   /**
    * Rotates the coordinates of this edge by the sine and cosine of an angle
    * around the z axis. Uses global coordinates, i.e. doesn't consider the
    * edge's position. The texture coordinates are unaffected.
    *
    * @param cosa cosine of the angle
    * @param sina sine of the angle
    *
    * @return this edge
    *
    * @see Vec2#rotateZ(Vec2, float, float, Vec2)
    */
   public Edge2 rotateZGlobal ( final float cosa, final float sina ) {

      Vec2.rotateZ(this.origin.coord, cosa, sina, this.origin.coord);
      Vec2.rotateZ(this.dest.coord, cosa, sina, this.dest.coord);

      return this;
   }

   /**
    * Rotates the coordinates of this edge by the sine and cosine of an angle
    * around the z axis. The edge's center (midpoint) acts as a pivot. The
    * texture coordinates are unaffected.
    *
    * @param cosa   cosine of the angle
    * @param sina   sine of the angle
    * @param center the center
    *
    * @return this edge
    *
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    * @see Vec2#rotateZ(Vec2, float, float, Vec2)
    * @see Vec2#add(Vec2, Vec2, Vec2)
    */
   public Edge2 rotateZLocal ( final float cosa, final float sina,
      final Vec2 center ) {

      final Vec2 coOrigin = this.origin.coord;
      final Vec2 coDest = this.dest.coord;

      center.set( ( coOrigin.x + coDest.x ) * 0.5f, ( coOrigin.y + coDest.y )
         * 0.5f);

      Vec2.sub(coOrigin, center, coOrigin);
      Vec2.rotateZ(coOrigin, cosa, sina, coOrigin);
      Vec2.add(coOrigin, center, coOrigin);

      Vec2.sub(coDest, center, coDest);
      Vec2.rotateZ(coDest, cosa, sina, coDest);
      Vec2.add(coDest, center, coDest);

      return this;
   }

   /**
    * Rotates the coordinates of this edge by an angle in radians around the z
    * axis. The edge's center (midpoint) acts as a pivot. The texture
    * coordinates are unaffected.
    *
    * @param radians the angle
    * @param center  the center
    *
    * @return this edge
    *
    * @see Edge2#rotateZLocal(float, float, Vec2)
    */
   public Edge2 rotateZLocal ( final float radians, final Vec2 center ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);
      return this.rotateZLocal(cosa, sina, center);
   }

   /**
    * Scales the coordinates of this edge. The texture coordinates are
    * unaffected.
    *
    * @param scale uniform scalar
    *
    * @return this edge
    *
    * @see Edge2#scaleGlobal(float)
    */
   public Edge2 scale ( final float scale ) {

      return this.scaleGlobal(scale);
   }

   /**
    * Scales the coordinates of this edge. The texture coordinates are
    * unaffected.
    *
    * @param scalar non uniform scalar
    *
    * @return this edge
    *
    * @see Edge2#scaleGlobal(Vec2)
    */
   public Edge2 scale ( final Vec2 scalar ) {

      return this.scaleGlobal(scalar);
   }

   /**
    * Scales the coordinates of this edge. The texture coordinates are
    * unaffected. Uses global coordinates, i.e., doesn't consider the face's
    * position.
    *
    * @param scalar the scalar
    *
    * @return this edge
    *
    * @see Vec2#mul(Vec2, float, Vec2)
    */
   public Edge2 scaleGlobal ( final float scalar ) {

      if ( scalar != 0.0f ) {
         Vec2.mul(this.origin.coord, scalar, this.origin.coord);
         Vec2.mul(this.dest.coord, scalar, this.dest.coord);
      }

      return this;
   }

   /**
    * Scales the coordinates of this edge. The texture coordinates are
    * unaffected. Uses global coordinates, i.e., doesn't consider the face's
    * position.
    *
    * @param scalar the nonuniform scalar
    *
    * @return this edge
    *
    * @see Vec2#hadamard(Vec2, Vec2, Vec2)
    */
   public Edge2 scaleGlobal ( final Vec2 scalar ) {

      if ( Vec2.all(scalar) ) {
         Vec2.hadamard(this.origin.coord, scalar, this.origin.coord);
         Vec2.hadamard(this.dest.coord, scalar, this.dest.coord);
      }

      return this;
   }

   /**
    * Scales the coordinates of this edge. Subtracts the edge's mean center
    * (midpoint) from each vertex, scales, then adds the mean center.
    *
    * @param scalar the uniform scalar
    * @param center the edge center
    *
    * @return this edge
    *
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    * @see Vec2#mul(Vec2, float, Vec2)
    * @see Vec2#add(Vec2, Vec2, Vec2)
    */
   public Edge2 scaleLocal ( final float scalar, final Vec2 center ) {

      if ( scalar != 0.0f ) {

         final Vec2 coOrigin = this.origin.coord;
         final Vec2 coDest = this.dest.coord;

         center.set( ( coOrigin.x + coDest.x ) * 0.5f, ( coOrigin.y + coDest.y )
            * 0.5f);

         Vec2.sub(coOrigin, center, coOrigin);
         Vec2.mul(coOrigin, scalar, coOrigin);
         Vec2.add(coOrigin, center, coOrigin);

         Vec2.sub(coDest, center, coDest);
         Vec2.mul(coDest, scalar, coDest);
         Vec2.add(coDest, center, coDest);
      }

      return this;
   }

   /**
    * Scales the coordinates of this edge. Subtracts the edge's center
    * (midpoint) from each vertex, scales, then adds the center.
    *
    * @param scalar the nonuniform scalar
    * @param center the edge center
    *
    * @return this edge
    *
    * @see Vec2#none(Vec2)
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    * @see Vec2#hadamard(Vec2, Vec2, Vec2)
    * @see Vec2#add(Vec2, Vec2, Vec2)
    */
   @Experimental
   public Edge2 scaleLocal ( final Vec2 scalar, final Vec2 center ) {

      if ( Vec2.all(scalar) ) {

         final Vec2 coOrigin = this.origin.coord;
         final Vec2 coDest = this.dest.coord;

         center.set( ( coOrigin.x + coDest.x ) * 0.5f, ( coOrigin.y + coDest.y )
            * 0.5f);

         Vec2.sub(coOrigin, center, coOrigin);
         Vec2.hadamard(coOrigin, scalar, coOrigin);
         Vec2.add(coOrigin, center, coOrigin);

         Vec2.sub(coDest, center, coDest);
         Vec2.hadamard(coDest, scalar, coDest);
         Vec2.add(coDest, center, coDest);
      }

      return this;
   }

   /**
    * Sets the origin and destination coordinate, texture coordinate and
    * normal data by reference.
    *
    * @param coOrigin origin coordinate
    * @param txOrigin origin texture coordinate
    * @param coDest   destination coordinate
    * @param txDest   destination texture coordinate
    *
    * @return this edge
    */
   public Edge2 set ( final Vec2 coOrigin, final Vec2 txOrigin,
      final Vec2 coDest, final Vec2 txDest ) {

      this.origin.set(coOrigin, txOrigin);
      this.dest.set(coDest, txDest);
      return this;
   }

   /**
    * Sets this edge by vertex.
    *
    * @param origin the origin vertex
    * @param dest   the destination vertex
    *
    * @return this edge
    */
   public Edge2 set ( final Vert2 origin, final Vert2 dest ) {

      this.origin = origin;
      this.dest = dest;

      return this;
   }

   /**
    * Returns a string representation of this edge.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(IUtils.FIXED_PRINT); }

   /**
    * Returns a string representation of this edge.
    *
    * @param places the number of places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(512);
      sb.append("{ origin: ");
      sb.append(this.origin.toString(places));
      sb.append(", dest: ");
      sb.append(this.dest.toString(places));
      sb.append(' ');
      sb.append('}');
      return sb.toString();
   }

   /**
    * Transforms this edge by a matrix.
    *
    * @param m the matrix
    *
    * @return this edge
    *
    * @see Mat3#mulPoint(Mat3, Vec2, Vec2)
    */
   public Edge2 transform ( final Mat3 m ) {

      Mat3.mulPoint(m, this.origin.coord, this.origin.coord);
      Mat3.mulPoint(m, this.dest.coord, this.dest.coord);

      return this;
   }

   /**
    * Transforms this edge by a transform.
    *
    * @param tr the transform
    *
    * @return this edge
    *
    * @see Transform2#mulPoint(Transform2, Vec2, Vec2)
    */
   public Edge2 transform ( final Transform2 tr ) {

      Transform2.mulPoint(tr, this.origin.coord, this.origin.coord);
      Transform2.mulPoint(tr, this.dest.coord, this.dest.coord);

      return this;
   }

   /**
    * Translates the coordinates of this edge. The texture coordinates are
    * unaffected.
    *
    * @param v translation
    *
    * @return this edge
    *
    * @see Vec2#add(Vec2, Vec2, Vec2)
    */
   public Edge2 translate ( final Vec2 v ) {

      Vec2.add(this.origin.coord, v, this.origin.coord);
      Vec2.add(this.dest.coord, v, this.dest.coord);

      return this;
   }

   /**
    * Tests this edge for equivalence with another. To be true the edges'
    * origins must be equal and their destinations must be equal.
    *
    * @return the evaluation
    */
   protected boolean equalsDirected ( final Edge2 edge2 ) {

      if ( this.dest == null ) {
         if ( edge2.dest != null ) { return false; }
      } else if ( !this.dest.equals(edge2.dest) ) { return false; }

      if ( this.origin == null ) {
         if ( edge2.origin != null ) { return false; }
      } else if ( !this.origin.equals(edge2.origin) ) { return false; }

      return true;
   }

   /**
    * Evaluates whether two edges are complements or neighbors, i.e., whether
    * one edge's origin is the other's destination.
    *
    * @param a left comparisand
    * @param b right comparisand
    *
    * @return the evaluation
    */
   public static boolean areNeighbors ( final Edge2 a, final Edge2 b ) {

      return Vert2.approxCoord(a.dest, b.origin) && Vert2.approxCoord(a.origin,
         b.dest);
   }

   /**
    * Finds the distance between an edge and a point.
    *
    * @param a the edge
    * @param b the point
    *
    * @return the distance
    */
   @Experimental
   public static float dist ( final Edge2 a, final Vec2 b ) {

      // TEST

      /*
       * The magnitude of the rejection of u from v, where u equals b -
       * a.origin.
       */
      final Vec2 aOrigin = a.origin.coord;
      final Vec2 aDest = a.dest.coord;

      /* Represent edge as point and vector. */
      final float vx = aDest.x - aOrigin.x;
      final float vy = aDest.y - aOrigin.y;
      final float dotvv = vx * vx + vy * vy;
      if ( dotvv <= 0.0f ) { return 0.0f; }

      /* Find difference between point and edge origin. */
      final float ux = b.x - aOrigin.x;
      final float uy = b.y - aOrigin.y;

      /* Cross difference with edge vector. */
      final float az = ux * vy - uy * vx;
      final float dotaa = az * az;

      return Utils.sqrtUnchecked(dotaa / dotvv);
   }

   /**
    * Finds a point on the edge given a step in the range [0.0, 1.0] . Uses
    * linear interpolation from the origin coordinate to that of the
    * destination. To find an edge's midpoint, use the step 0.5 .
    *
    * @param edge   the edge
    * @param step   the step
    * @param target the output vector
    *
    * @return the point
    */
   public static Vec2 eval ( final Edge2 edge, final float step,
      final Vec2 target ) {

      final Vec2 coOrigin = edge.origin.coord;
      final Vec2 coDest = edge.dest.coord;

      if ( step <= 0.0f ) { return target.set(coOrigin); }
      if ( step >= 1.0f ) { return target.set(coDest); }

      final float u = 1.0f - step;
      return target.set(u * coOrigin.x + step * coDest.x, u * coOrigin.y + step
         * coDest.y);
   }

   /**
    * Finds the heading of an edge. Subtracts the destination coordinate from
    * that of the origin, then supplies the difference to atan2 .
    *
    * @param edge the edge
    *
    * @return the heading
    *
    * @see Utils#atan2(float, float)
    */
   public static float heading ( final Edge2 edge ) {

      final Vec2 dest = edge.dest.coord;
      final Vec2 origin = edge.origin.coord;
      return Utils.atan2(dest.y - origin.y, dest.x - origin.x);
   }

   /**
    * Finds the Euclidean distance from the edge's origin coordinate to that
    * of its destination.
    *
    * @param edge the edge
    *
    * @return the magnitude
    *
    * @see Vec2#distEuclidean(Vec2, Vec2)
    */
   public static float mag ( final Edge2 edge ) {

      return Vec2.distEuclidean(edge.origin.coord, edge.dest.coord);
   }

   /**
    * Finds the squared Euclidean distance from the edge's origin coordinate
    * to that of its destination.
    *
    * @param edge the edge
    *
    * @return the magnitude
    *
    * @see Vec2#distSq(Vec2, Vec2)
    */
   public static float magSq ( final Edge2 edge ) {

      return Vec2.distSq(edge.origin.coord, edge.dest.coord);
   }

   /**
    * Projects a vector, representing a point, onto an edge. The scalar
    * projection is clamped to the range [0.0, 1.0] , meaning the projection
    * will not exceed the edge's origin and destination.
    *
    * @param edge   the edge
    * @param v      the input vector
    * @param target the output vector
    *
    * @return the projection
    *
    * @see Utils#clamp01(float)
    */
   public static Vec2 projectVector ( final Edge2 edge, final Vec2 v,
      final Vec2 target ) {

      final Vec2 coOrigin = edge.origin.coord;
      final Vec2 coDest = edge.dest.coord;

      final float bx = coDest.x - coOrigin.x;
      final float by = coDest.y - coOrigin.y;
      final float bSq = bx * bx + by * by;

      if ( bSq <= 0.0f ) { return target.set(coOrigin); }

      final float ax = v.x - coOrigin.x;
      final float ay = v.y - coOrigin.y;
      final float fac = ( ax * bx + ay * by ) / bSq;

      if ( fac >= 1.0f ) { return target.set(coDest); }

      final float u = 1.0f - fac;
      return target.set(u * coOrigin.x + fac * coDest.x, u * coOrigin.y + fac
         * coDest.y);
   }

   /**
    * Tests to see if two edges share a vertex coordinate:
    * <ul>
    * <li>Returns -1 when <em>a</em>'s origin is <em>b</em>'s
    * destination.</li>
    * <li>Returns -2 when <em>a</em>'s destination is <em>b</em>'s
    * destination</li>
    * <li>Returns 1 when <em>a</em>'s destination is <em>b</em>'s origin.</li>
    * <li>Returns 2 when <em>a</em>'s origin is <em>b</em>'s origin.</li>
    * <li>Returns 0 when none of the above conditions are met.</li>
    * </ul>
    * Assuming a mesh is properly wound, a value of 1 implies that two edges
    * belong to the same face; of 2, different neighboring faces. A positive
    * value implies counter-clockwise winding (CCW), or right-handedness; a
    * negative value, clockwise winding (CW), or left-handedness.<br>
    * <br>
    * If the left and right comparisand are the same, returns 2.
    *
    * @param a the left comparisand
    * @param b the right comparisand
    *
    * @return the evaluation
    */
   public static int sharedCoord ( final Edge2 a, final Edge2 b ) {

      final Vert2 aOrigin = a.origin;
      final Vert2 aDest = a.dest;
      final Vert2 bOrigin = b.origin;
      final Vert2 bDest = b.dest;

      if ( Vert2.approxCoord(aDest, bOrigin) ) { return 1; }
      if ( Vert2.approxCoord(aOrigin, bOrigin) ) { return 2; }
      if ( Vert2.approxCoord(aOrigin, bDest) ) { return -1; }
      if ( Vert2.approxCoord(aDest, bDest) ) { return -2; }
      return 0;
   }
}