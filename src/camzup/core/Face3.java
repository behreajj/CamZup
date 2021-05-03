package camzup.core;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeSet;

/**
 * Organizes components of a 3D mesh into a list of vertices that form a
 * face. This is not used by a mesh internally; it is created upon
 * retrieval from a mesh.
 */
public class Face3 implements Iterable < Edge3 >, Comparable < Face3 > {

   /**
    * The array of vertices in the face's edge loop.
    */
   public Vert3[] vertices;

   /**
    * The default constructor. When used, initializes an empty array.
    */
   public Face3 ( ) { this.vertices = new Vert3[] {}; }

   /**
    * Creates a face from an array of edges.
    *
    * @param edges the edges
    */
   public Face3 ( final Edge3... edges ) { this.set(edges); }

   /**
    * Creates a face from an array of vertices.
    *
    * @param vertices the vertices
    */
   public Face3 ( final Vert3... vertices ) { this.set(vertices); }

   /**
    * Compares this face to another by hash code.
    *
    * @param face the comparisand
    *
    * @return the comparison
    *
    * @see Face3#centerMean(Face3, Vec3)
    */
   @Override
   public int compareTo ( final Face3 face ) {

      return Face3.centerMean(this, new Vec3()).compareTo(Face3.centerMean(face,
         new Vec3()));
   }

   /**
    * Returns an edge iterator for this face, which allows its vertices to be
    * accessed in an enhanced for-loop.
    *
    * @return the iterator
    */
   public Edge3Iterator edgeIterator ( ) {

      return new Edge3Iterator(this);
   }

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
      return Arrays.equals(this.vertices, ( ( Face3 ) obj ).vertices);
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
   public Edge3 getEdge ( final int i, final Edge3 target ) {

      final int len = this.vertices.length;
      final int j = Utils.mod(i, len);
      return target.set(this.vertices[j], this.vertices[ ( j + 1 ) % len]);
   }

   /**
    * Gets all the edges in this face.
    *
    * @return the edges
    */
   public Edge3[] getEdges ( ) {

      final int len = this.vertices.length;
      final int last = len - 1;
      final Edge3[] result = new Edge3[len];
      for ( int i = 0; i < last; ++i ) {
         result[i] = new Edge3(this.vertices[i], this.vertices[i + 1]);
      }
      result[last] = new Edge3(this.vertices[last], this.vertices[0]);
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
   public Iterator < Edge3 > iterator ( ) { return this.edgeIterator(); }

   /**
    * Returns the number of vertices in this face.
    *
    * @return the vertex count
    */
   public int length ( ) { return this.vertices.length; }

   /**
    * Rotates this face by a quaternion.
    *
    * @param q the quaternion
    *
    * @return this face
    */
   public Face3 rotate ( final Quaternion q ) {

      return this.rotateGlobal(q);
   }

   /**
    * Rotates all coordinates in the mesh by the sine and cosine of an angle
    * around an arbitrary axis.
    *
    * @param cosa cosine of the angle
    * @param sina sine of the angle
    * @param axis the axis
    *
    * @return this face
    *
    * @see Vec3#rotate(Vec3, float, float, Vec3, Vec3)
    */
   public Face3 rotateGlobal ( final float cosa, final float sina,
      final Vec3 axis ) {

      final int len = this.vertices.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec3 c = this.vertices[i].coord;
         Vec3.rotate(c, cosa, sina, axis, c);
      }

      return this;
   }

   /**
    * Rotates all coordinates in the mesh by an angle around an arbitrary
    * axis.
    *
    * @param radians the angle in radians
    * @param axis    the axis of rotation
    *
    * @return this face
    *
    * @see Face3#rotateGlobal(float, float, Vec3)
    */
   public Face3 rotateGlobal ( final float radians, final Vec3 axis ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);
      return this.rotateGlobal(cosa, sina, axis);
   }

   /**
    * Rotates all coordinates in the face by a quaternion.
    *
    * @param q the quaternion
    *
    * @return this face
    *
    * @see Quaternion#mulVector(Quaternion, Vec3, Vec3)
    */
   public Face3 rotateGlobal ( final Quaternion q ) {

      final int len = this.vertices.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec3 c = this.vertices[i].coord;
         Quaternion.mulVector(q, c, c);
      }

      return this;
   }

   /**
    * Rotates all coordinates in the mesh by the sine and cosine of an angle
    * around an arbitrary axis. The face's mean center acts as a pivot point.
    *
    * @param cosa   cosine of the angle
    * @param sina   sine of the angle
    * @param axis   the axis
    * @param center the center
    *
    * @return this face
    *
    * @see Vec3#sub(Vec3, Vec3, Vec3)
    * @see Vec3#rotate(Vec3, float, float, Vec3, Vec3)
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */
   public Face3 rotateLocal ( final float cosa, final float sina,
      final Vec3 axis, final Vec3 center ) {

      Face3.centerMean(this, center);

      final int len = this.vertices.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec3 c = this.vertices[i].coord;
         Vec3.sub(c, center, c);
         Vec3.rotate(c, cosa, sina, axis, c);
         Vec3.add(c, center, c);
      }

      return this;
   }

   /**
    * Rotates all coordinates in the mesh by an angle around an arbitrary
    * axis. The face's mean center acts as a pivot point.
    *
    * @param radians the angle
    * @param axis    the axis
    * @param center  the center
    *
    * @return this face
    *
    * @see Face3#rotateLocal(float, float, Vec3, Vec3)
    */
   public Face3 rotateLocal ( final float radians, final Vec3 axis,
      final Vec3 center ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);
      return this.rotateLocal(cosa, sina, axis, center);
   }

   /**
    * Rotates all coordinates in the face by a quaternion. The face's mean
    * center acts as a pivot point.
    *
    * @param q      the quaternion
    * @param center the center
    *
    * @return this face
    */
   public Face3 rotateLocal ( final Quaternion q, final Vec3 center ) {

      Face3.centerMean(this, center);

      final int len = this.vertices.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec3 c = this.vertices[i].coord;
         Vec3.sub(c, center, c);
         Quaternion.mulVector(q, c, c);
         Vec3.add(c, center, c);
      }

      return this;
   }

   /**
    * Rotates all coordinates in the face by an angle around the x axis.
    *
    * @param radians the angle in radians
    *
    * @return this face
    *
    * @see Face3#rotateXGlobal(float, float)
    */
   public Face3 rotateXGlobal ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);
      return this.rotateXGlobal(cosa, sina);
   }

   /**
    * Rotates all coordinates in the face by the sine and cosine of an angle
    * around the x axis.
    *
    * @param cosa cosine of the angle
    * @param sina sine of the angle
    *
    * @return this face
    *
    * @see Vec3#rotateX(Vec3, float, Vec3)
    */
   public Face3 rotateXGlobal ( final float cosa, final float sina ) {

      final int len = this.vertices.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec3 c = this.vertices[i].coord;
         Vec3.rotateX(c, cosa, sina, c);
      }

      return this;
   }

   /**
    * Rotates all coordinates in the face by the sine and cosine of an angle
    * around the x axis. The face's mean center acts as a pivot point.
    *
    * @param cosa   cosine of the angle
    * @param sina   sine of the angle
    * @param center the center
    *
    * @return this face
    *
    * @see Vec3#sub(Vec3, Vec3, Vec3)
    * @see Vec3#rotateX(Vec3, float, float, Vec3)
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */
   public Face3 rotateXLocal ( final float cosa, final float sina,
      final Vec3 center ) {

      Face3.centerMean(this, center);

      final int len = this.vertices.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec3 c = this.vertices[i].coord;
         Vec3.sub(c, center, c);
         Vec3.rotateX(c, cosa, sina, c);
         Vec3.add(c, center, c);
      }

      return this;
   }

   /**
    * Rotates all coordinates in the mesh by an angle around the x axis. The
    * face's mean center acts as a pivot point.
    *
    * @param radians the angle
    * @param center  the center
    *
    * @return this face
    *
    * @see Face3#rotateXLocal(float, float, Vec3)
    */
   public Face3 rotateXLocal ( final float radians, final Vec3 center ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);
      return this.rotateXLocal(cosa, sina, center);
   }

   /**
    * Rotates all coordinates in the face by an angle around the y axis.
    *
    * @param radians the angle in radians
    *
    * @return this face
    *
    * @see Face3#rotateYGlobal(float, float)
    */
   public Face3 rotateYGlobal ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);
      return this.rotateYGlobal(cosa, sina);
   }

   /**
    * Rotates all coordinates in the face by the sine and cosine of an angle
    * around the y axis.
    *
    * @param cosa cosine of the angle
    * @param sina sine of the angle
    *
    * @return this face
    *
    * @see Vec3#rotateY(Vec3, float, Vec3)
    */
   public Face3 rotateYGlobal ( final float cosa, final float sina ) {

      final int len = this.vertices.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec3 c = this.vertices[i].coord;
         Vec3.rotateY(c, cosa, sina, c);
      }

      return this;
   }

   /**
    * Rotates all coordinates in the face by the sine and cosine of an angle
    * around the y axis. The face's mean center acts as a pivot point.
    *
    * @param cosa   cosine of the angle
    * @param sina   sine of the angle
    * @param center the center
    *
    * @return this face
    *
    * @see Vec3#sub(Vec3, Vec3, Vec3)
    * @see Vec3#rotateY(Vec3, float, float, Vec3)
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */
   public Face3 rotateYLocal ( final float cosa, final float sina,
      final Vec3 center ) {

      Face3.centerMean(this, center);

      final int len = this.vertices.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec3 c = this.vertices[i].coord;
         Vec3.sub(c, center, c);
         Vec3.rotateY(c, cosa, sina, c);
         Vec3.add(c, center, c);
      }

      return this;
   }

   /**
    * Rotates all coordinates in the mesh by an angle around the y axis. The
    * face's mean center acts as a pivot point.
    *
    * @param radians the angle
    * @param center  the center
    *
    * @return this face
    *
    * @see Face3#rotateYLocal(float, float, Vec3)
    */
   public Face3 rotateYLocal ( final float radians, final Vec3 center ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);
      return this.rotateYLocal(cosa, sina, center);
   }

   /**
    * Rotates all coordinates in the face by an angle around the z axis.
    *
    * @param radians the angle in radians
    *
    * @return this face
    *
    * @see Face3#rotateZGlobal(float, float)
    */
   public Face3 rotateZGlobal ( final float radians ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);
      return this.rotateZGlobal(cosa, sina);
   }

   /**
    * Rotates all coordinates in the face by the sine and cosine of an angle
    * around the z axis.
    *
    * @param cosa cosine of the angle
    * @param sina sine of the angle
    *
    * @return this face
    *
    * @see Vec3#rotateZ(Vec3, float, Vec3)
    */
   public Face3 rotateZGlobal ( final float cosa, final float sina ) {

      final int len = this.vertices.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec3 c = this.vertices[i].coord;
         Vec3.rotateZ(c, cosa, sina, c);
      }

      return this;
   }

   /**
    * Rotates all coordinates in the face by the sine and cosine of an angle
    * around the z axis. The face's mean center acts as a pivot point.
    *
    * @param cosa   cosine of the angle
    * @param sina   sine of the angle
    * @param center the center
    *
    * @return this face
    *
    * @see Vec3#sub(Vec3, Vec3, Vec3)
    * @see Vec3#rotateZ(Vec3, float, float, Vec3)
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */
   public Face3 rotateZLocal ( final float cosa, final float sina,
      final Vec3 center ) {

      Face3.centerMean(this, center);

      final int len = this.vertices.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec3 c = this.vertices[i].coord;
         Vec3.sub(c, center, c);
         Vec3.rotateZ(c, cosa, sina, c);
         Vec3.add(c, center, c);
      }

      return this;
   }

   /**
    * Rotates all coordinates in the mesh by an angle around the z axis. The
    * face's mean center acts as a pivot point.
    *
    * @param radians the angle
    * @param center  the center
    *
    * @return this face
    *
    * @see Face3#rotateZLocal(float, float, Vec3)
    */
   public Face3 rotateZLocal ( final float radians, final Vec3 center ) {

      final float cosa = Utils.cos(radians);
      final float sina = Utils.sin(radians);
      return this.rotateZLocal(cosa, sina, center);
   }

   /**
    * Scales all coordinates in the face by a scalar.
    *
    * @param scale the scalar
    *
    * @return this face
    *
    * @see Vec3#mul(Vec3, float, Vec3)
    */
   public Face3 scale ( final float scale ) {

      return this.scaleGlobal(scale);
   }

   /**
    * Scales all coordinates in the face by a scalar.<br>
    * <br>
    * Beware, non-uniform scaling requires that normals be recalculated for
    * correct shading.
    *
    * @param scale the nonuniform scalar
    *
    * @return this face
    *
    * @see Vec3#mul(Vec3, float, Vec3)
    */
   public Face3 scale ( final Vec3 scale ) {

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
    * @see Vec3#mul(Vec3, float, Vec3)
    */
   public Face3 scaleGlobal ( final float scale ) {

      if ( scale != 0.0f ) {
         final int len = this.vertices.length;
         for ( int i = 0; i < len; ++i ) {
            final Vec3 c = this.vertices[i].coord;
            Vec3.mul(c, scale, c);
         }
      }

      return this;
   }

   /**
    * Scales all coordinates in the face by a scalar; uses global coordinates,
    * i.e., doesn't consider the face's position. <br>
    * <br>
    * Beware, non-uniform scaling requires that normals be recalculated for
    * correct shading.
    *
    * @param scale the nonuniform scalar
    *
    * @return this face
    *
    * @see Vec3#hadamard(Vec3, Vec3, Vec3)
    */
   public Face3 scaleGlobal ( final Vec3 scale ) {

      if ( Vec3.all(scale) ) {
         final int len = this.vertices.length;
         for ( int i = 0; i < len; ++i ) {
            final Vec3 c = this.vertices[i].coord;
            Vec3.hadamard(c, scale, c);
         }
      }

      return this;
   }

   /**
    * Scales all coordinates in the face by a scalar; subtracts the face's
    * center from each vertex, scales, then adds the center.
    *
    * @param scale  the scalar
    * @param center the center
    *
    * @return this face
    *
    * @see Face3#centerMean(Face3, Vec3)
    * @see Vec3#sub(Vec3, Vec3, Vec3)
    * @see Vec3#mul(Vec3, float, Vec3)
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */
   public Face3 scaleLocal ( final float scale, final Vec3 center ) {

      Face3.centerMean(this, center);

      if ( scale != 0.0f ) {
         final int len = this.vertices.length;
         for ( int i = 0; i < len; ++i ) {
            final Vec3 c = this.vertices[i].coord;
            Vec3.sub(c, center, c);
            Vec3.mul(c, scale, c);
            Vec3.add(c, center, c);
         }
      }

      return this;
   }

   /**
    * Scales all coordinates in the face by a scalar; subtracts the face's
    * center from each vertex, scales, then adds the center.<br>
    * <br>
    * Beware, non-uniform scaling requires that normals be recalculated for
    * correct shading.
    *
    * @param scale  the nonuniform scalar
    * @param center the center
    *
    * @return this face
    *
    * @see Face3#centerMean(Face3, Vec3)
    * @see Vec3#sub(Vec3, Vec3, Vec3)
    * @see Vec3#hadamard(Vec3, Vec3, Vec3)
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */
   public Face3 scaleLocal ( final Vec3 scale, final Vec3 center ) {

      Face3.centerMean(this, center);

      if ( Vec3.all(scale) ) {
         final int len = this.vertices.length;
         for ( int i = 0; i < len; ++i ) {
            final Vec3 c = this.vertices[i].coord;
            Vec3.sub(c, center, c);
            Vec3.hadamard(c, scale, c);
            Vec3.add(c, center, c);
         }
      }

      return this;
   }

   /**
    * Sets this face's vertices to refer to those in an array of edges.
    * Assumes that each edge's origin matches the preceding edge's
    * destination, and so only the origins need to be registered.
    *
    * @param edges the edges
    *
    * @return this face
    */
   public Face3 set ( final Edge3... edges ) {

      final int len = edges.length;
      this.vertices = new Vert3[len];
      for ( int i = 0; i < len; ++i ) { this.vertices[i] = edges[i].origin; }

      return this;
   }

   /**
    * Sets this face's vertices to refer to a an array of vertices.
    *
    * @param vertices the array of vertices
    *
    * @return this face
    */
   public Face3 set ( final Vert3... vertices ) {

      this.vertices = vertices;
      return this;
   }

   /**
    * Returns a string representation of this face.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(IUtils.FIXED_PRINT); }

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
      final StringBuilder sb = new StringBuilder(len * 512);
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
    * @see Mat4#mulPoint(Mat4, Vec3, Vec3)
    */
   public Face3 transform ( final Mat4 m ) {

      final int len = this.vertices.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec3 c = this.vertices[i].coord;
         Mat4.mulPoint(m, c, c);
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
    * @see Transform3#mulPoint(Transform3, Vec3, Vec3)
    */
   public Face3 transform ( final Transform3 tr ) {

      final int len = this.vertices.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec3 c = this.vertices[i].coord;
         Transform3.mulPoint(tr, c, c);
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
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */
   public Face3 translate ( final Vec3 v ) {

      return this.translateGlobal(v);
   }

   /**
    * Translates all coordinates in a face by a vector; uses global
    * coordinates, i.e., doesn't consider the face's orientation.
    *
    * @param v the vector
    *
    * @return this face
    *
    * @see Vec3#add(Vec3, Vec3, Vec3)
    */
   public Face3 translateGlobal ( final Vec3 v ) {

      final int len = this.vertices.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec3 c = this.vertices[i].coord;
         Vec3.add(c, v, c);
      }

      return this;
   }

   /**
    * Translates the face in local space. This is done by (1) finding the
    * orientation of the face; (2) multiplying the input vector by the
    * orientation; (3) subtracting the face's center point from the face's
    * vertices; (4) adding the local vector; (5) then adding the center point.
    *
    * @param v          the vector
    * @param handedness the handedness
    *
    * @return the face
    */
   @Experimental
   public Face3 translateLocal ( final Vec3 v, final Handedness handedness ) {

      final Transform3 tr = Face3.orientation(this, handedness,
         new Transform3());
      final Vec3 vLocal = Transform3.mulDir(tr, v, new Vec3());

      final int len = this.vertices.length;
      for ( int i = 0; i < len; ++i ) {
         final Vec3 c = this.vertices[i].coord;
         Vec3.sub(c, tr.location, c);
         Vec3.add(c, vLocal, c);
         Vec3.add(c, tr.location, c);
      }

      return this;
   }

   /**
    * Returns an vertex iterator for this face, which allows its vertices to
    * be accessed in an enhanced for-loop.
    *
    * @return the iterator
    */
   public Vert3Iterator vertIterator ( ) { return new Vert3Iterator(this); }

   /**
    * Finds the center of a face by averaging all the coordinates in its list
    * of vertices.
    *
    * @param face   the face
    * @param target the output vector
    *
    * @return the center
    *
    * @see Vec3#add(Vec3, Vec3, Vec3)
    * @see Vec3#div(Vec3, Vec3, Vec3)
    */
   public static Vec3 centerMean ( final Face3 face, final Vec3 target ) {

      target.reset();
      final Vert3[] verts = face.vertices;
      final int len = verts.length;
      for ( int i = 0; i < len; ++i ) {
         Vec3.add(target, verts[i].coord, target);
      }
      return Vec3.div(target, len, target);
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
   public static Vec3 eval ( final Face3 face, final float step,
      final Vec3 target ) {

      final Vert3[] verts = face.vertices;
      final int len = verts.length;
      final float tScaled = len * Utils.mod1(step);
      final int i = ( int ) tScaled;
      final Vec3 a = verts[i].coord;
      final Vec3 b = verts[ ( i + 1 ) % len].coord;

      final float t = tScaled - i;
      final float u = 1.0f - t;
      return target.set(u * a.x + t * b.x, u * a.y + t * b.y, u * a.z + t
         * b.z);
   }

   /**
    * Finds the normal of a face by averaging all the normals in its list of
    * vertices, then normalizing the average.
    *
    * @param face   the face
    * @param target the output vector
    *
    * @return the normal
    *
    * @see Vec3#add(Vec3, Vec3, Vec3)
    * @see Vec3#div(Vec3, Vec3, Vec3)
    * @see Vec3#normalize(Vec3, Vec3)
    */
   @Experimental
   public static Vec3 normal ( final Face3 face, final Vec3 target ) {

      target.reset();
      final Vert3[] verts = face.vertices;
      final int len = verts.length;
      for ( int i = 0; i < len; ++i ) {
         Vec3.add(target, verts[i].normal, target);
      }
      // Vec3.div(target, len, target);
      return Vec3.normalize(target, target);

      // target.reset();
      // Vec3 prev = null;
      // Vec3 curr = null;
      // Vec3 next = null;
      // final Vec3 edge0 = new Vec3();
      // final Vec3 edge1 = new Vec3();
      // final Vec3 vertNorm = new Vec3();
      // final Vert3[] verts = face.vertices;
      // final int len = verts.length;
      // prev = verts[len - 1].coord;
      // for ( int i = 0; i < len; ++i ) {
      // curr = verts[i].coord;
      // next = verts[ ( i + 1 ) % len].coord;
      // Vec3.sub(prev, curr, edge0);
      // Vec3.sub(curr, next, edge1);
      // Vec3.crossNorm(edge0, edge1, vertNorm);
      // Vec3.add(target, vertNorm, target);
      // prev = curr;
      // }
      // Vec3.div(target, len, target);
      // return target;
   }

   /**
    * Returns the orientation of the vertex as a quaternion based on the
    * face's normal.
    *
    * @param face       the face
    * @param handedness the handedness
    * @param target     the output transform
    *
    * @return the transform
    */
   @Experimental
   public static Quaternion orientation ( final Face3 face,
      final Handedness handedness, final Quaternion target ) {

      /*
       * Use quaternion imaginary as a temporary holder for the average of the
       * face's normals.
       */
      final Vec3 imag = target.imag;
      Face3.normal(face, imag);
      return Quaternion.fromDir(imag, handedness, target);
   }

   /**
    * Returns the orientation of the vertex as a transform based on the face's
    * normal.
    *
    * @param face       the face
    * @param handedness the handedness
    * @param target     the output transform
    *
    * @return the transform
    */
   @Experimental
   public static Transform3 orientation ( final Face3 face,
      final Handedness handedness, final Transform3 target ) {

      final Quaternion rot = target.rotation;
      target.locPrev.set(target.location);
      target.rotPrev.set(rot);
      target.scalePrev.set(target.scale);
      Vec3.one(target.scale);

      Face3.normal(face, target.forward);
      Quaternion.fromDir(target.forward, handedness, rot, target.right,
         target.forward, target.up);
      Face3.centerMean(face, target.location);

      return target;
   }

   /**
    * Returns the orientation of the vertex as a ray based on the face's
    * normal.
    *
    * @param face   the face
    * @param target the output transform
    *
    * @return the transform
    */
   public static Ray3 orientation ( final Face3 face, final Ray3 target ) {

      Face3.centerMean(face, target.origin);
      Face3.normal(face, target.dir);
      return target;
   }

   /**
    * Calculates the perimeter of a face by summing the Euclidean distance
    * between vertices.
    *
    * @param face the face
    *
    * @return the perimeter
    *
    * @see Vec3#distEuclidean(Vec3, Vec3)
    */
   public static float perimeter ( final Face3 face ) {

      float sum = 0.0f;
      final Vert3[] verts = face.vertices;
      final int len = verts.length;
      Vec3 prev = verts[len - 1].coord;
      for ( int i = 0; i < len; ++i ) {
         final Vec3 curr = verts[i].coord;
         sum += Vec3.distEuclidean(prev, curr);
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
   public static Vec3[] sharedCoords ( final Face3 a, final Face3 b ) {

      final TreeSet < Vec3 > aList = new TreeSet <>(Mesh.SORT_3);
      final Vert3[] aVerts = a.vertices;
      final int aLen = aVerts.length;
      for ( int i = 0; i < aLen; ++i ) { aList.add(aVerts[i].coord); }

      final TreeSet < Vec3 > bList = new TreeSet <>(Mesh.SORT_3);
      final Vert3[] bVerts = b.vertices;
      final int bLen = bVerts.length;
      for ( int j = 0; j < bLen; ++j ) { bList.add(bVerts[j].coord); }

      aList.retainAll(bList);

      return aList.toArray(new Vec3[aList.size()]);
   }

   /**
    * An iterator, which allows a face's edges to be accessed in an enhanced
    * for loop.
    */
   public static final class Edge3Iterator implements Iterator < Edge3 > {

      /**
       * The face being iterated over.
       */
      private final Face3 face;

      /**
       * The current index.
       */
      private int index = 0;

      /**
       * The default constructor.
       *
       * @param face the face to iterate
       */
      public Edge3Iterator ( final Face3 face ) { this.face = face; }

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
      public Edge3 next ( ) {

         if ( !this.hasNext() ) { throw new NoSuchElementException(); }
         return this.face.getEdge(this.index++, new Edge3());
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
   public static final class Vert3Iterator implements Iterator < Vert3 > {

      /**
       * The face being iterated over.
       */
      private final Face3 face;

      /**
       * The current index.
       */
      private int index = 0;

      /**
       * The default constructor.
       *
       * @param face the face to iterate
       */
      public Vert3Iterator ( final Face3 face ) { this.face = face; }

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
      public Vert3 next ( ) {

         if ( !this.hasNext() ) { throw new NoSuchElementException(); }
         return this.face.vertices[this.index++];
      }

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

}