package camzup.core;

import java.util.Arrays;

/**
 * Organizes components of a 3D mesh into a list of vertices that form
 * a face.
 *
 * This is not used by a mesh internally; it is created upon retrieval
 * from a mesh.
 */
public class Face3 implements Comparable < Face3 > {

  /**
   * The array of vertices in a face.
   */
  public Vert3[] vertices;

  /**
   * The default constructor. When used, initializes an empty array.
   */
  public Face3 ( ) {

    this.vertices = new Vert3[] {};
  }

  /**
   * Creates a face from an array of vertices.
   *
   * @param vertices the vertices
   */
  public Face3 ( final Vert3 ... vertices ) {

    this.set(vertices);
  }

  @Experimental
  @Chainable
  Face3 translateLocal ( final Vec3 v ) {

    final Vec3 centroid = new Vec3();
    Face3.centroid(this, centroid);

    Vert3 vert;
    Vec3 c;
    Vec3 n;

    final Vec3 refUp = Vec3.up(new Vec3());
    final Vec3 i = new Vec3();
    final Vec3 j = new Vec3();
    final Vec3 k = new Vec3();
    final Quaternion rot = new Quaternion();
    final Vec3 rotv = new Vec3();

    // TODO: Easiest way to make a look at matrix?
    final int len = this.vertices.length;
    for ( int q = 0; q < len; ++q ) {
      vert = this.vertices[q];
      c = vert.coord;
      n = vert.normal;

      Vec3.normalize(n, k);
      Vec3.crossNorm(refUp, k, i);
      Vec3.crossNorm(k, i, j);
      Quaternion.fromAxes(i, j, k, rot);
      // Quaternion.fromAxes(i, k, j, rot);
      Quaternion.mulVector(rot, v, rotv);
      // Vec3.crossNorm(a, b, target);

      Vec3.sub(c, centroid, c);
      Vec3.add(c, rotv, c);
      Vec3.add(c, centroid, c);
    }

    return this;
  }

  /**
   * Compares this face to another by hash code.
   *
   * @param face the comparisand
   * @return the comparison
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
   */
  @Experimental
  public Edge3 getEdge (
      final int i,
      final Edge3 target ) {

    final int len = this.vertices.length;
    return target.set(
        this.vertices[Math.floorMod(i, len)],
        this.vertices[Math.floorMod(i + 1, len)]);
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
  public int hashCode ( ) {

    return Arrays.hashCode(this.vertices);
  }

  /**
   * Returns the number of vertices in this face.
   *
   * @return the vertex count
   */
  public int length ( ) {

    return this.vertices.length;
  }

  /**
   * Rotates all coordinates in the mesh by an angle around an arbitrary
   * axis.
   *
   * @param radians the angle in radians
   * @param axis    the axis of rotation
   * @return this mesh
   * @see Vec3#rotate(Vec3, float, Vec3, Vec3)
   */
  @Chainable
  public Face3 rotate (
      final float radians,
      final Vec3 axis ) {

    final float cosa = Utils.cos(radians);
    final float sina = Utils.sin(radians);

    Vert3 vt3;
    Vec3 c;

    final int len = this.vertices.length;
    for ( int i = 0; i < len; ++i ) {
      vt3 = this.vertices[i];
      c = vt3.coord;
      Vec3.rotate(c, cosa, sina, axis, c);
    }

    return this;
  }

  /**
   * Rotates all coordinates in the face by a quaternion.
   *
   * @param q the quaternion
   * @return the mesh
   */
  @Chainable
  public Face3 rotate ( final Quaternion q ) {

    Vert3 vt3;
    Vec3 c;

    final int len = this.vertices.length;
    for ( int i = 0; i < len; ++i ) {
      vt3 = this.vertices[i];
      c = vt3.coord;
      Quaternion.mulVector(q, c, c);
    }

    return this;
  }

  /**
   * Rotates all coordinates in the face by an angle around the x axis.
   *
   * @param radians the angle in radians
   * @return this mesh
   * @see Vec3#rotateX(Vec3, float, Vec3)
   */
  @Chainable
  public Face3 rotateX ( final float radians ) {

    final float cosa = Utils.cos(radians);
    final float sina = Utils.sin(radians);

    Vert3 vt3;
    Vec3 c;

    final int len = this.vertices.length;
    for ( int i = 0; i < len; ++i ) {
      vt3 = this.vertices[i];
      c = vt3.coord;
      Vec3.rotateX(c, cosa, sina, c);
    }

    return this;
  }

  /**
   * Rotates all coordinates in the face by an angle around the y axis.
   *
   * @param radians the angle in radians
   * @return this mesh
   * @see Vec3#rotateY(Vec3, float, Vec3)
   */
  @Chainable
  public Face3 rotateY ( final float radians ) {

    final float cosa = Utils.cos(radians);
    final float sina = Utils.sin(radians);

    Vert3 vt3;
    Vec3 c;

    final int len = this.vertices.length;
    for ( int i = 0; i < len; ++i ) {
      vt3 = this.vertices[i];
      c = vt3.coord;
      Vec3.rotateY(c, cosa, sina, c);
    }

    return this;
  }

  /**
   * Rotates all coordinates in the face by an angle around the z axis.
   *
   * @param radians the angle in radians
   * @return this mesh
   * @see Vec3#rotateZ(Vec3, float, Vec3)
   */
  @Chainable
  public Face3 rotateZ ( final float radians ) {

    final float cosa = Utils.cos(radians);
    final float sina = Utils.sin(radians);

    Vert3 vt3;
    Vec3 c;

    final int len = this.vertices.length;
    for ( int i = 0; i < len; ++i ) {
      vt3 = this.vertices[i];
      c = vt3.coord;
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
   * Scales all coordinates in the face by a scalar.
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
   */
  @Chainable
  public Face3 scaleGlobal ( final float scale ) {

    if ( scale == 0.0f ) { return this; }

    Vec3 c;
    final int len = this.vertices.length;
    for ( int i = 0; i < len; ++i ) {
      c = this.vertices[i].coord;
      Vec3.mul(c, scale, c);
    }

    return this;
  }

  /**
   * Scales all coordinates in the face by a scalar; uses global
   * coordinates, i.e., doesn't consider the face's position.
   *
   * @param scale the nonuniform scalar
   * @return this face
   */
  @Chainable
  public Face3 scaleGlobal ( final Vec3 scale ) {

    if ( Vec3.none(scale) ) { return this; }

    Vec3 c;
    final int len = this.vertices.length;
    for ( int i = 0; i < len; ++i ) {
      c = this.vertices[i].coord;
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
   */
  @Chainable
  public Face3 scaleLocal ( final float scale ) {

    if ( scale == 0.0f ) { return this; }

    final Vec3 centroid = new Vec3();
    Face3.centroid(this, centroid);

    Vec3 c;
    final int len = this.vertices.length;
    for ( int i = 0; i < len; ++i ) {
      c = this.vertices[i].coord;
      Vec3.sub(c, centroid, c);
      Vec3.mul(c, scale, c);
      Vec3.add(c, centroid, c);
    }

    return this;
  }

  /**
   * Scales all coordinates in the face by a scalar; subtracts the
   * face's centroid from each vertex, scales, then adds the centroid.
   *
   * @param scale the nonuniform scalar
   * @return this face
   */
  @Chainable
  public Face3 scaleLocal ( final Vec3 scale ) {

    if ( Vec3.none(scale) ) { return this; }

    final Vec3 centroid = new Vec3();
    Face3.centroid(this, centroid);

    Vec3 c;
    final int len = this.vertices.length;
    for ( int i = 0; i < len; ++i ) {
      c = this.vertices[i].coord;
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
  public String toString ( ) {

    return this.toString(4);
  }

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
        .append("{ vertices: [ \n");
    for ( int i = 0; i < len; ++i ) {
      sb.append(this.vertices[i].toString(places));
      if ( i < last ) { sb.append(',').append('\n'); }
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
   * @return this face.
   */
  @Chainable
  public Face3 translateGlobal ( final Vec3 v ) {

    Vec3 c;
    final int len = this.vertices.length;
    for ( int i = 0; i < len; ++i ) {
      c = this.vertices[i].coord;
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
}