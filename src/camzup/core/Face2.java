package camzup.core;

import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Organizes components of a 2D mesh into a list of vertices that form a
 * face. This is not used by a mesh internally; it is created upon
 * retrieval from a mesh.
 */
public class Face2 implements Iterable < Edge2 >, Comparable < Face2 > {

   /**
    * The array of vertices in a face.
    */
   public Vert2[] vertices;

   /**
    * The default constructor. When used, initializes an empty array.
    */
   public Face2 ( ) { this.vertices = new Vert2[] {}; }

   /**
    * Creates a face from an array of vertices.
    *
    * @param vertices the vertices
    */
   public Face2 ( final Vert2... vertices ) { this.set(vertices); }

   /**
    * Compares this face to another by hash code.
    *
    * @param face the comparisand
    *
    * @return the comparison
    *
    * @see Face2#centerMean(Face2, Vec2)
    */
   @Override
   public int compareTo ( final Face2 face ) {

      return Face2.centerMean(this, new Vec2()).compareTo(Face2.centerMean(face,
         new Vec2()));
   }

   /**
    * Returns an edge iterator for this face, which allows its vertices to be
    * accessed in an enhanced for-loop.
    *
    * @return the iterator
    */
   public Edge2Iterator edgeIterator ( ) { return new Edge2Iterator(this); }

   /**
    * Tests this face for equivalence with another object.
    *
    * @return the evaluation
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null ) { return false; }
      if ( this.getClass() != obj.getClass() ) { return false; }
      if ( !Arrays.equals(this.vertices, ( ( Face2 ) obj ).vertices) ) {
         return false;
      }

      return true;
   }

   /**
    * Gets an edge from this face. Wraps the index by the number of vertices
    * in the face.
    *
    * @param i      index
    * @param target output edge
    *
    * @return the edge
    *
    * @see Utils#mod(int, int)
    */
   @Experimental
   public Edge2 getEdge ( final int i, final Edge2 target ) {

      final int len = this.vertices.length;
      return target.set(this.vertices[Utils.mod(i, len)], this.vertices[Utils
         .mod(i + 1, len)]);
   }

   /**
    * Gets all the edges in this face.
    *
    * @return the edges
    */
   @Experimental
   public Edge2[] getEdges ( ) {

      final int len = this.vertices.length;
      final int last = len - 1;
      final Edge2[] result = new Edge2[len];
      for ( int i = 0; i < last; ++i ) {
         result[i] = new Edge2(this.vertices[i], this.vertices[i + 1]);
      }
      result[last] = new Edge2(this.vertices[last], this.vertices[0]);
      return result;
   }

   /**
    * Returns a hash code for this face.
    *
    * @return the hash
    */
   @Override
   public int hashCode ( ) { return Arrays.hashCode(this.vertices); }

   /**
    * Returns an iterator for this face, which allows its elements to be
    * accessed in an enhanced for-loop.
    *
    * @return the iterator
    */
   @Override
   public Iterator < Edge2 > iterator ( ) { return this.edgeIterator(); }

   /**
    * Returns the number of vertices in this face.
    *
    * @return the vertex count
    */
   public int length ( ) { return this.vertices.length; }

   /**
    * Rotates all coordinates in the face by an angle around the z axis.
    *
    * @param radians the angle in radians
    *
    * @return this mesh
    *
    * @see Vec2#rotateZ(Vec2, float, Vec2)
    */
   public Face2 rotateZ ( final float radians ) {

      return this.rotateZGlobal(radians);
   }

   /**
    * Rotates all coordinates in the face by an angle around the z axis. Does
    * not consider the face's pivot or present orientation.
    *
    * @param radians the angle in radians
    *
    * @return this mesh
    *
    * @see Vec2#rotateZ(Vec2, float, Vec2)
    */
   public Face2 rotateZGlobal ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

      final int len = this.vertices.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec2 c = this.vertices[i].coord;
         Vec2.rotateZ(c, cosa, sina, c);
      }

      return this;
   }

   /**
    * Rotates all coordinates in the face by an angle around the z axis. The
    * face's mean center is used as the pivot point.
    *
    * @param radians the angle in radians
    *
    * @return this mesh
    *
    * @see Face2#centerMean(Face2, Vec2)
    * @see Utils#cos(float)
    * @see Utils#sin(float)
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    * @see Vec2#rotateZ(Vec2, float, Vec2)
    * @see Vec2#add(Vec2, Vec2, Vec2)
    */
   public Face2 rotateZLocal ( final float radians ) {

      final Vec2 center = new Vec2();
      Face2.centerMean(this, center);

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);

      final int len = this.vertices.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec2 c = this.vertices[i].coord;
         Vec2.sub(c, center, c);
         Vec2.rotateZ(c, cosa, sina, c);
         Vec2.add(c, center, c);
      }

      return this;
   }

   /**
    * Scales all coordinates in the face by a scalar.
    *
    * @param scale the scale
    *
    * @return this face
    *
    * @see Vec2#mul(Vec2, float, Vec2)
    */
   public Face2 scale ( final float scale ) {

      return this.scaleGlobal(scale);
   }

   /**
    * Scales all coordinates in the face by a vector.
    *
    * @param scale the nonuniform scalar
    *
    * @return this face
    *
    * @see Vec2#mul(Vec2, Vec2, Vec2)
    */
   public Face2 scale ( final Vec2 scale ) {

      return this.scaleGlobal(scale);
   }

   /**
    * Scales all coordinates in the face by a scalar; uses global coordinates,
    * i.e., doesn't consider the face's position.
    *
    * @param scale the scalar
    *
    * @return this face
    *
    * @see Vec2#mul(Vec2, float, Vec2)
    */
   public Face2 scaleGlobal ( final float scale ) {

      if ( scale != 0.0f ) {
         final int len = this.vertices.length;
         for ( int i = 0; i < len; ++i ) {
            final Vec2 c = this.vertices[i].coord;
            Vec2.mul(c, scale, c);
         }
      }

      return this;
   }

   /**
    * Scales all coordinates in the face by a scalar; uses global coordinates,
    * i.e., doesn't consider the face's position.
    *
    * @param scale the nonuniform scalar
    *
    * @return this face
    *
    * @see Vec2#none(Vec2)
    * @see Vec2#mul(Vec2, Vec2, Vec2)
    */
   public Face2 scaleGlobal ( final Vec2 scale ) {

      if ( Vec2.all(scale) ) {
         final int len = this.vertices.length;
         for ( int i = 0; i < len; ++i ) {
            final Vec2 c = this.vertices[i].coord;
            Vec2.mul(c, scale, c);
         }
      }

      return this;
   }

   /**
    * Scales all coordinates in the face by a scalar; subtracts the face's
    * center from each vertex, scales, then adds the center.
    *
    * @param scale the scalar
    *
    * @return this face
    *
    * @see Face2#centerMean(Face2, Vec2)
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    * @see Vec2#mul(Vec2, float, Vec2)
    * @see Vec2#add(Vec2, Vec2, Vec2)
    */
   public Face2 scaleLocal ( final float scale ) {

      if ( scale == 0.0f ) { return this; }

      final Vec2 center = new Vec2();
      Face2.centerMean(this, center);

      final int len = this.vertices.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec2 c = this.vertices[i].coord;
         Vec2.sub(c, center, c);
         Vec2.mul(c, scale, c);
         Vec2.add(c, center, c);
      }

      return this;
   }

   /**
    * Scales all coordinates in the face by a scalar; subtracts the face's
    * center from each vertex, scales, then adds the center.
    *
    * @param scale the nonuniform scalar
    *
    * @return this face
    *
    * @see Vec2#none(Vec2)
    * @see Face2#centerMean(Face2, Vec2)
    * @see Vec2#sub(Vec2, Vec2, Vec2)
    * @see Vec2#mul(Vec2, Vec2, Vec2)
    * @see Vec2#add(Vec2, Vec2, Vec2)
    */
   public Face2 scaleLocal ( final Vec2 scale ) {

      if ( Vec2.none(scale) ) { return this; }

      final Vec2 center = new Vec2();
      Face2.centerMean(this, center);

      final int len = this.vertices.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec2 c = this.vertices[i].coord;
         Vec2.sub(c, center, c);
         Vec2.mul(c, scale, c);
         Vec2.add(c, center, c);
      }

      return this;
   }

   /**
    * Sets this face's vertices to refer to a an array.
    *
    * @param vertices the array of vertices
    *
    * @return this face
    */
   public Face2 set ( final Vert2... vertices ) {

      this.vertices = vertices;
      return this;
   }

   /**
    * Returns a string representation of this face.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(4); }

   /**
    * Returns a string representation of this face.
    *
    * @param places the number of places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      final int len = this.vertices.length;
      final int last = len - 1;
      final StringBuilder sb = new StringBuilder(len * 256);
      sb.append("{ vertices: [ ");
      for ( int i = 0; i < len; ++i ) {
         sb.append(this.vertices[i].toString(places));
         if ( i < last ) { sb.append(',').append(' '); }
      }
      sb.append(" ] }");
      return sb.toString();
   }

   /**
    * Transforms all coordinates in the face by a matrix.
    *
    * @param m the matrix
    *
    * @return this face
    *
    * @see Mat3#mulPoint(Mat3, Vec2, Vec2)
    */
   public Face2 transform ( final Mat3 m ) {

      final int len = this.vertices.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec2 c = this.vertices[i].coord;
         Mat3.mulPoint(m, c, c);
      }

      return this;
   }

   /**
    * Transforms all coordinates in the face by a transform.
    *
    * @param tr the matrix
    *
    * @return this face
    *
    * @see Transform2#mulPoint(Transform2, Vec2, Vec2)
    */
   public Face2 transform ( final Transform2 tr ) {

      final int len = this.vertices.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec2 c = this.vertices[i].coord;
         Transform2.mulPoint(tr, c, c);
      }

      return this;
   }

   /**
    * Translates all coordinates in the face by a vector.
    *
    * @param v the vector
    *
    * @return this face
    *
    * @see Vec2#add(Vec2, Vec2, Vec2)
    */
   public Face2 translate ( final Vec2 v ) {

      return this.translateGlobal(v);
   }

   /**
    * Translates all coordinates in the face by a vector; uses global
    * coordinates, i.e., doesn't consider the face's orientation.
    *
    * @param v the vector
    *
    * @return this face
    *
    * @see Vec2#add(Vec2, Vec2, Vec2)
    */
   public Face2 translateGlobal ( final Vec2 v ) {

      final int len = this.vertices.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec2 c = this.vertices[i].coord;
         Vec2.add(c, v, c);
      }

      return this;
   }

   /**
    * Returns an vertex iterator for this face, which allows its vertices to
    * be accessed in an enhanced for-loop.
    *
    * @return the iterator
    */
   public Vert2Iterator vertIterator ( ) { return new Vert2Iterator(this); }

   /**
    * Finds the center of a face by averaging all the coordinates in its list
    * of vertices.
    *
    * @param face   the face
    * @param target the output vector
    *
    * @return the center
    *
    * @see Vec2#add(Vec2, Vec2, Vec2)
    * @see Vec2#div(Vec2, float, Vec2)
    */
   public static Vec2 centerMean ( final Face2 face, final Vec2 target ) {

      target.reset();
      final Vert2[] verts = face.vertices;
      final int len = verts.length;
      for ( int i = 0; i < len; ++i ) {
         Vec2.add(target, verts[i].coord, target);
      }
      return Vec2.div(target, len, target);
   }

   /**
    * Evaluates whether the face contains a point. Uses vertex winding (as
    * opposed to casting a ray).
    *
    * @param face  the face
    * @param point the point
    *
    * @return the evaluation
    */
   public static boolean contains ( final Face2 face, final Vec2 point ) {

      int wn = 0;
      final Vert2[] verts = face.vertices;
      final int len = verts.length;

      for ( int i = 0; i < len; ++i ) {

         final Vec2 curr = verts[i].coord;
         final Vec2 next = verts[ ( i + 1 ) % len].coord;

         if ( curr.y <= point.y && next.y > point.y ) {
            /* @formatter:off */
            final float eval = ( next.x - curr.x ) * ( point.y - curr.y ) -
                               ( point.x - curr.x ) * ( next.y - curr.y );
            if ( eval > 0.0f ) { ++wn; }
         } else if ( next.y <= point.y ) {
            final float eval = ( next.x - curr.x ) * ( point.y - curr.y ) -
                               ( point.x - curr.x ) * ( next.y - curr.y );
            if ( eval < 0.0f ) { --wn; }
            /* @formatter:on */
         }
      }

      return wn > 0;
   }

   /**
    * Finds a point on the face's perimeter given a step in the range [0.0,
    * 1.0] .
    *
    * @param face   the face
    * @param step   the step
    * @param target the output vector
    *
    * @return the vector
    */
   public static Vec2 eval ( final Face2 face, final float step,
      final Vec2 target ) {

      final Vert2[] verts = face.vertices;
      final int len = verts.length;
      final float tScaled = len * Utils.mod1(step);
      final int i = ( int ) tScaled;
      final Vec2 a = verts[i].coord;
      final Vec2 b = verts[ ( i + 1 ) % len].coord;

      final float t = tScaled - i;
      final float u = 1.0f - t;
      return target.set(u * a.x + t * b.x, u * a.y + t * b.y);
   }

   /**
    * Returns whether a face is wound in the counter-clockwise direction,
    * i.e., if its winding number is greater than zero.
    *
    * @param face the face
    *
    * @return the evaluation
    *
    * @see Face2#winding(Face2)
    */
   public static boolean isCCW ( final Face2 face ) {

      return Face2.winding(face) > 0.0f;
   }

   /**
    * Returns whether a face is wound in the clockwise direction, i.e., if its
    * winding number is less than zero.
    *
    * @param face the face
    *
    * @return the evaluation
    *
    * @see Face2#winding(Face2)
    */
   public static boolean isCW ( final Face2 face ) {

      return Face2.winding(face) < 0.0f;
   }

   /**
    * Calculates the perimeter of a face by summing the Euclidean distance
    * between vertices.
    *
    * @param face the face
    *
    * @return the perimeter
    *
    * @see Vec2#distEuclidean(Vec2, Vec2)
    */
   public static float perimeter ( final Face2 face ) {

      float sum = 0.0f;
      final Vert2[] verts = face.vertices;
      final int len = verts.length;
      Vec2 prev = verts[len - 1].coord;
      for ( int i = 0; i < len; ++i ) {
         final Vec2 curr = verts[i].coord;
         sum += Vec2.distEuclidean(prev, curr);
         prev = curr;
      }
      return sum;
   }

   /**
    * Finds the shared coordinates, if any, between two faces. Returns an
    * array of the coordinates.
    *
    * @param a the left comparisand
    * @param b the right comparisand
    *
    * @return the coordinate array
    */
   public static Vec2[] sharedCoords ( final Face2 a, final Face2 b ) {

      final TreeSet < Vec2 > aList = new TreeSet <>(Mesh.SORT_2);
      final Vert2[] aVerts = a.vertices;
      final int aLen = aVerts.length;
      for ( int i = 0; i < aLen; ++i ) {
         aList.add(aVerts[i].coord);
      }

      final TreeSet < Vec2 > bList = new TreeSet <>(Mesh.SORT_2);
      final Vert2[] bVerts = b.vertices;
      final int bLen = bVerts.length;
      for ( int j = 0; j < bLen; ++j ) {
         bList.add(bVerts[j].coord);
      }

      aList.retainAll(bList);

      return aList.toArray(new Vec2[aList.size()]);
   }

   /**
    * Calculates the winding number of the face by summing the cross products
    * of any two pair of edges.
    *
    * @param face the face
    *
    * @return the winding number
    */
   public static float winding ( final Face2 face ) {

      float wn = 0.0f;
      final Vert2[] verts = face.vertices;
      final int len = verts.length;
      Vec2 prev = verts[len - 1].coord;
      Vec2 curr = verts[0].coord;
      for ( int i = 0; i < len; ++i ) {
         final Vec2 next = verts[ ( i + 1 ) % len].coord;
         final float edge0x = curr.x - prev.x;
         final float edge0y = curr.y - prev.y;
         final float edge1x = next.x - curr.x;
         final float edge1y = next.y - curr.y;
         wn += edge0x * edge1y - edge0y * edge1x;
         prev = curr;
         curr = next;
      }
      return wn;
   }

   /**
    * An iterator, which allows a face's edges to be accessed in an enhanced
    * for loop.
    */
   public static final class Edge2Iterator implements Iterator < Edge2 > {

      /**
       * The face being iterated over.
       */
      private final Face2 face;

      /**
       * The current index.
       */
      private int index = 0;

      /**
       * The default constructor.
       *
       * @param face the face to iterate
       */
      public Edge2Iterator ( final Face2 face ) { this.face = face; }

      /**
       * Tests to see if the iterator has another value.
       *
       * @return the evaluation
       */
      @Override
      public boolean hasNext ( ) { return this.index < this.face.length(); }

      /**
       * Gets the next value in the iterator.
       *
       * @return the value
       */
      @Override
      public Edge2 next ( ) {

         return this.face.getEdge(this.index++, new Edge2());
      }

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

   /**
    * An iterator, which allows a face's vertices to be accessed in an
    * enhanced for loop.
    */
   public static final class Vert2Iterator implements Iterator < Vert2 > {

      /**
       * The face being iterated over.
       */
      private final Face2 face;

      /**
       * The current index.
       */
      private int index = 0;

      /**
       * The default constructor.
       *
       * @param face the face to iterate
       */
      public Vert2Iterator ( final Face2 face ) { this.face = face; }

      /**
       * Tests to see if the iterator has another value.
       *
       * @return the evaluation
       */
      @Override
      public boolean hasNext ( ) { return this.index < this.face.length(); }

      /**
       * Gets the next value in the iterator.
       *
       * @return the value
       */
      @Override
      public Vert2 next ( ) { return this.face.vertices[this.index++]; }

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

}