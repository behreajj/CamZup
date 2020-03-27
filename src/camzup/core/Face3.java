package camzup.core;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Organizes components of a 3D mesh into a list of vertices that form
 * a face.
 *
 * This is not used by a mesh internally; it is created upon retrieval
 * from a mesh.
 */
public class Face3 implements Iterable < Vert3 >, Comparable < Face3 > {

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
    public Vert3 next ( ) { return this.face.vertices[this.index++]; }

    /**
     * Returns the simple name of this class.
     *
     * @return the string
     */
    @Override
    public String toString ( ) {

      return this.getClass().getSimpleName();
    }
  }

  /**
   * The array of vertices in a face.
   */
  public Vert3[] vertices;

  /**
   * The default constructor. When used, initializes an empty array.
   */
  public Face3 ( ) { this.vertices = new Vert3[] {}; }

  /**
   * Creates a face from an array of vertices.
   *
   * @param vertices the vertices
   */
  public Face3 ( final Vert3 ... vertices ) { this.set(vertices); }

  @Experimental
  Face3 translateLocal ( final Vec3 v ) {

    // TEST

    final Transform3 tr = Face3.orientation(this, new Transform3());
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
   * Compares this face to another by hash code.
   *
   * @param face the comparisand
   * @return the comparison
   * @see Face3#centroid(Face3, Vec3)
   */
  @Override
  public int compareTo ( final Face3 face ) {

    return Face3.centroid(this, new Vec3()).compareTo(
        Face3.centroid(face, new Vec3()));
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
    if ( !Arrays.equals(this.vertices, ((Face3) obj).vertices) ) {
      return false;
    }
    return true;
  }

  /**
   * Gets an edge from this face. Wraps the index by the number of
   * vertices in the face.
   *
   * @param i      index
   * @param target output edge
   * @return the edge
   * @see Utils#mod(int, int)
   */
  @Experimental
  public Edge3 getEdge (
      final int i,
      final Edge3 target ) {

    final int len = this.vertices.length;
    return target.set(
        this.vertices[Utils.mod(i, len)],
        this.vertices[Utils.mod(i + 1, len)]);
  }

  /**
   * Gets all the edges in this face.
   *
   * @return the edges
   */
  @Experimental
  public Edge3[] getEdges ( ) {

    final int len = this.vertices.length;
    final int last = len - 1;
    final Edge3[] result = new Edge3[len];
    for ( int i = 0; i < last; ++i ) {
      result[i] = new Edge3(
          this.vertices[i],
          this.vertices[i + 1]);
    }
    result[last] = new Edge3(
        this.vertices[last],
        this.vertices[0]);
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
   * Returns an iterator for this face, which allows its vertices to be
   * accessed in an enhanced for-loop.
   *
   * @return the iterator
   */
  @Override
  public Vert3Iterator iterator ( ) { return new Vert3Iterator(this); }

  /**
   * Returns the number of vertices in this face.
   *
   * @return the vertex count
   */
  public int length ( ) { return this.vertices.length; }

  /**
   * Rotates all coordinates in the mesh by an angle around an arbitrary
   * axis.
   *
   * @param radians the angle in radians
   * @param axis    the axis of rotation
   * @return this mesh
   * @see Utils#cos(float)
   * @see Utils#sin(float)
   * @see Vec3#rotate(Vec3, float, Vec3, Vec3)
   */
  @Chainable
  public Face3 rotate (
      final float radians,
      final Vec3 axis ) {

    final float cosa = Utils.cos(radians);
    final float sina = Utils.sin(radians);

    final int len = this.vertices.length;
    for ( int i = 0; i < len; ++i ) {
      final Vert3 vt3 = this.vertices[i];
      final Vec3 c = vt3.coord;
      Vec3.rotate(c, cosa, sina, axis, c);
    }

    return this;
  }

  /**
   * Rotates all coordinates in the face by a quaternion.
   *
   * @param q the quaternion
   * @return the mesh
   * @see Quaternion#mulVector(Quaternion, Vec3, Vec3)
   */
  @Chainable
  public Face3 rotate ( final Quaternion q ) {

    final int len = this.vertices.length;
    for ( int i = 0; i < len; ++i ) {
      final Vert3 vt3 = this.vertices[i];
      final Vec3 c = vt3.coord;
      Quaternion.mulVector(q, c, c);
    }

    return this;
  }

  /**
   * Rotates all coordinates in the face by an angle around the x axis.
   *
   * @param radians the angle in radians
   * @return this mesh
   * @see Utils#cos(float)
   * @see Utils#sin(float)
   * @see Vec3#rotateX(Vec3, float, Vec3)
   */
  @Chainable
  public Face3 rotateX ( final float radians ) {

    final float cosa = Utils.cos(radians);
    final float sina = Utils.sin(radians);

    final int len = this.vertices.length;
    for ( int i = 0; i < len; ++i ) {
      final Vert3 vt3 = this.vertices[i];
      final Vec3 c = vt3.coord;
      Vec3.rotateX(c, cosa, sina, c);
    }

    return this;
  }

  /**
   * Rotates all coordinates in the face by an angle around the y axis.
   *
   * @param radians the angle in radians
   * @return this mesh
   * @see Utils#cos(float)
   * @see Utils#sin(float)
   * @see Vec3#rotateY(Vec3, float, Vec3)
   */
  @Chainable
  public Face3 rotateY ( final float radians ) {

    final float cosa = Utils.cos(radians);
    final float sina = Utils.sin(radians);

    final int len = this.vertices.length;
    for ( int i = 0; i < len; ++i ) {
      final Vert3 vt3 = this.vertices[i];
      final Vec3 c = vt3.coord;
      Vec3.rotateY(c, cosa, sina, c);
    }

    return this;
  }

  /**
   * Rotates all coordinates in the face by an angle around the z axis.
   *
   * @param radians the angle in radians
   * @return this mesh
   * @see Utils#cos(float)
   * @see Utils#sin(float)
   * @see Vec3#rotateZ(Vec3, float, Vec3)
   */
  @Chainable
  public Face3 rotateZ ( final float radians ) {

    final float cosa = Utils.cos(radians);
    final float sina = Utils.sin(radians);

    final int len = this.vertices.length;
    for ( int i = 0; i < len; ++i ) {
      final Vert3 vt3 = this.vertices[i];
      final Vec3 c = vt3.coord;
      Vec3.rotateZ(c, cosa, sina, c);
    }

    return this;
  }

  /**
   * Scales all coordinates in the face by a scalar.
   *
   * @param scale the scalar
   * @return this face
   * @see Vec3#mul(Vec3, float, Vec3)
   */
  @Chainable
  public Face3 scale ( final float scale ) {

    return this.scaleGlobal(scale);
  }

  /**
   * Scales all coordinates in the face by a scalar.<br>
   * <br>
   * Beware, non-uniform scaling requires that normals be recalculated
   * for correct shading.
   *
   * @param scale the nonuniform scalar
   * @return this face
   * @see Vec3#mul(Vec3, float, Vec3)
   */
  @Chainable
  public Face3 scale ( final Vec3 scale ) {

    return this.scaleGlobal(scale);
  }

  /**
   * Scales all coordinates in the face by a scalar; uses global
   * coordinates, i.e., doesn't consider the face's position.
   *
   * @param scale the scalar
   * @return this face
   * @see Vec3#mul(Vec3, float, Vec3)
   */
  @Chainable
  public Face3 scaleGlobal ( final float scale ) {

    if ( scale == 0.0f ) { return this; }

    final int len = this.vertices.length;
    for ( int i = 0; i < len; ++i ) {
      final Vec3 c = this.vertices[i].coord;
      Vec3.mul(c, scale, c);
    }

    return this;
  }

  /**
   * Scales all coordinates in the face by a scalar; uses global
   * coordinates, i.e., doesn't consider the face's position. <br>
   * <br>
   * Beware, non-uniform scaling requires that normals be recalculated
   * for correct shading.
   *
   * @param scale the nonuniform scalar
   * @return this face
   * @see Vec3#mul(Vec3, Vec3, Vec3)
   */
  @Chainable
  public Face3 scaleGlobal ( final Vec3 scale ) {

    if ( Vec3.none(scale) ) { return this; }

    final int len = this.vertices.length;
    for ( int i = 0; i < len; ++i ) {
      final Vec3 c = this.vertices[i].coord;
      Vec3.mul(c, scale, c);
    }

    return this;
  }

  /**
   * Scales all coordinates in the face by a scalar; subtracts the
   * face's centroid from each vertex, scales, then adds the centroid.
   *
   * @param scale the scalar
   * @return this face
   * @see Face3#centroid(Face3, Vec3)
   * @see Vec3#sub(Vec3, Vec3, Vec3)
   * @see Vec3#mul(Vec3, float, Vec3)
   * @see Vec3#add(Vec3, Vec3, Vec3)
   */
  @Chainable
  public Face3 scaleLocal ( final float scale ) {

    if ( scale == 0.0f ) { return this; }

    final Vec3 centroid = new Vec3();
    Face3.centroid(this, centroid);

    final int len = this.vertices.length;
    for ( int i = 0; i < len; ++i ) {
      final Vec3 c = this.vertices[i].coord;
      Vec3.sub(c, centroid, c);
      Vec3.mul(c, scale, c);
      Vec3.add(c, centroid, c);
    }

    return this;
  }

  /**
   * Scales all coordinates in the face by a scalar; subtracts the
   * face's centroid from each vertex, scales, then adds the
   * centroid.<br>
   * <br>
   * Beware, non-uniform scaling requires that normals be recalculated
   * for correct shading.
   *
   * @param scale the nonuniform scalar
   * @return this face
   * @see Face3#centroid(Face3, Vec3)
   * @see Vec3#sub(Vec3, Vec3, Vec3)
   * @see Vec3#mul(Vec3, Vec3, Vec3)
   * @see Vec3#add(Vec3, Vec3, Vec3)
   */
  @Chainable
  public Face3 scaleLocal ( final Vec3 scale ) {

    if ( Vec3.none(scale) ) { return this; }

    final Vec3 centroid = new Vec3();
    Face3.centroid(this, centroid);

    final int len = this.vertices.length;
    for ( int i = 0; i < len; ++i ) {
      final Vec3 c = this.vertices[i].coord;
      Vec3.sub(c, centroid, c);
      Vec3.mul(c, scale, c);
      Vec3.add(c, centroid, c);
    }

    return this;
  }

  /**
   * Sets this face's vertices to refer to a an array.
   *
   * @param vertices the array of vertices
   * @return this face
   */
  @Chainable
  public Face3 set ( final Vert3 ... vertices ) {

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
   * @return the string
   */
  public String toString ( final int places ) {

    final int len = this.vertices.length;
    final int last = len - 1;
    final StringBuilder sb = new StringBuilder(len * 512)
        .append("{ vertices: [ ");
    for ( int i = 0; i < len; ++i ) {
      sb.append(this.vertices[i].toString(places));
      if ( i < last ) {
        sb.append(',').append(' ');
        // sb.append('\n');
      }
    }
    sb.append(" ] }");
    return sb.toString();
  }

  /**
   * Translates all coordinates in the face by a vector.
   *
   * @param v the vector
   * @return this face
   * @see Vec3#add(Vec3, Vec3, Vec3)
   */
  @Chainable
  public Face3 translate ( final Vec3 v ) {

    return this.translateGlobal(v);
  }

  /**
   * Translates all coordinates in a face by a vector; uses global
   * coordinates, i.e., doesn't consider the face's orientation.
   *
   * @param v the vector
   * @return this face
   * @see Vec3#add(Vec3, Vec3, Vec3)
   */
  @Chainable
  public Face3 translateGlobal ( final Vec3 v ) {

    final int len = this.vertices.length;
    for ( int i = 0; i < len; ++i ) {
      final Vec3 c = this.vertices[i].coord;
      Vec3.add(c, v, c);
    }

    return this;
  }

  /**
   * Finds the centroid of a face by averaging all the coordinates in
   * its list of vertices.
   *
   * @param face   the face
   * @param target the output vector
   * @return the centroid
   * @see Vec3#add(Vec3, Vec3, Vec3)
   * @see Vec3#div(Vec3, Vec3, Vec3)
   */
  public static Vec3 centroid (
      final Face3 face,
      final Vec3 target ) {

    target.reset();
    final Vert3[] verts = face.vertices;
    final int len = verts.length;
    for ( int i = 0; i < len; ++i ) {
      Vec3.add(target, verts[i].coord, target);
    }
    return Vec3.div(target, len, target);
  }

  /**
   * Finds the normal of a face by averaging all the normals in its list
   * of vertices, then normalizing the average.
   *
   * @param face   the face
   * @param target the output vector
   * @return the normal
   * @see Vec3#add(Vec3, Vec3, Vec3)
   * @see Vec3#div(Vec3, Vec3, Vec3)
   * @see Vec3#normalize(Vec3, Vec3)
   */
  @Experimental
  public static Vec3 normal (
      final Face3 face,
      final Vec3 target ) {

    // RESEARCH: Should this depend not on the pre-calculated normals but
    // instead calculate upon request?

    target.reset();
    final Vert3[] verts = face.vertices;
    final int len = verts.length;
    for ( int i = 0; i < len; ++i ) {
      Vec3.add(target, verts[i].normal, target);
    }
    Vec3.div(target, len, target);
    return Vec3.normalize(target, target);
  }

  @Experimental
  public static Quaternion orientation (
      final Face3 face,
      final Quaternion target ) {

    /*
     * Use quaternion imaginary as a placeholder to hold the average of
     * the face's normals.
     */
    final Vec3 imag = target.imag;
    Face3.normal(face, imag);
    return Quaternion.fromDir(imag, target);
  }

  @Experimental
  public static Transform3 orientation (
      final Face3 face,
      final Transform3 target ) {

    final Quaternion rot = target.rotation;
    target.locPrev.set(target.location);
    target.rotPrev.set(rot);
    target.scalePrev.set(target.scale);
    Vec3.one(target.scale);

    // TODO: Why not use Transform3 fromDir?
    Face3.normal(face, target.forward);
    Quaternion.fromDir(
        target.forward,
        rot,
        target.right,
        target.forward,
        target.up);
    Face3.centroid(face, target.location);

    // if ( target.location.z < 0.0f ) {
    // Vec3.negate(target.up, target.up);
    // Vec3.negate(target.right, target.right);
    // Quaternion.fromAxes(
    // target.right,
    // target.forward,
    // target.up,
    // target.rotation);
    // }

    return target;
  }

  /**
   * Calculates the perimeter of a face by summing the Euclidean
   * distance between vertices.
   *
   * @param face the face
   * @return the perimeter
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
}