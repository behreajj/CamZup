package camzup.core;

import java.util.Arrays;

/**
 * Organizes components of a 2D mesh into a list of vertices that form
 * a face.
 *
 * This is not used by a mesh internally; it is created upon retrieval
 * from a mesh.
 */
public class Face2 implements Comparable < Face2 > {

  /**
   * The array of vertices in a face.
   */
  public Vert2[] vertices;

  /**
   * The default constructor. When used, initializes an empty array.
   */
  public Face2 ( ) {

    this.vertices = new Vert2[] {};
  }

  /**
   * Creates a face from an array of vertices.
   *
   * @param vertices the vertices
   */
  public Face2 ( final Vert2 ... vertices ) {

    this.set(vertices);
  }

  /**
   * Compares this face to another by hash code.
   *
   * @param face the comparisand
   * @return the comparison
   */
  @Override
  public int compareTo ( final Face2 face ) {

    return Face2.centroid(this, new Vec2()).compareTo(
        Face2.centroid(face, new Vec2()));
  }

  /**
   * Tests this face for equivalence with another object.
   *
   * @return the evaluation
   */
  @Override
  public boolean equals ( final Object obj ) {

    if (this == obj) { return true; }

    if (obj == null) { return false; }

    if (this.getClass() != obj.getClass()) { return false; }

    if (!Arrays.equals(this.vertices, ((Face2) obj).vertices)) { return false; }

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
  public Edge2 getEdge (
      final int i,
      final Edge2 target ) {

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
  public Edge2[] getEdges ( ) {

    final int len = this.vertices.length;
    final int last = len - 1;
    final Edge2[] result = new Edge2[len];
    for (int i = 0; i < last; ++i) {
      result[i] = new Edge2(
          this.vertices[i],
          this.vertices[i + 1]);
    }
    result[last] = new Edge2(
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
   * Rotates all coordinates in the face by an angle around the z axis.
   *
   * @param radians the angle in radians
   * @return this mesh
   * @see Vec2#rotateZ(Vec2, float, Vec2)
   */
  @Chainable
  public Face2 rotateZ ( final float radians ) {

    final float cosa = Utils.cos(radians);
    final float sina = Utils.sin(radians);
    Vec2 c;

    final int len = this.vertices.length;
    for (int i = 0; i < len; ++i) {
      c = this.vertices[i].coord;
      Vec2.rotateZ(c, cosa, sina, c);
    }

    return this;
  }

  /**
   * Scales all coordinates in the face by a scalar.
   *
   * @param scale the vector
   * @return this face
   * @see Vec2#mul(Vec2, float, Vec2)
   */
  @Chainable
  public Face2 scale ( final float scale ) {

    if (scale == 0.0f) { return this; }

    Vec2 c;
    final int len = this.vertices.length;
    for (int i = 0; i < len; ++i) {
      c = this.vertices[i].coord;
      Vec2.mul(c, scale, c);
    }

    return this;
  }

  /**
   * Scales all coordinates in the face by a vector.
   *
   * @param scale the vector
   * @return this face
   * @see Vec2#mul(Vec2, Vec2, Vec2)
   */
  @Chainable
  public Face2 scale ( final Vec2 scale ) {

    if (Vec2.none(scale)) { return this; }

    Vec2 c;
    final int len = this.vertices.length;
    for (int i = 0; i < len; ++i) {
      c = this.vertices[i].coord;
      Vec2.mul(c, scale, c);
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
  public Face2 set ( final Vert2 ... vertices ) {

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
    final StringBuilder sb = new StringBuilder(len * 256)
        .append("{ vertices: [ \n");
    for (int i = 0; i < len; ++i) {
      sb.append(this.vertices[i].toString(places));
      if (i < last) { sb.append(',').append('\n'); }
    }
    sb.append(" ] }");
    return sb.toString();
  }

  /**
   * Translates all coordinates in the face by a vector.
   *
   * @param v the vector
   * @return this face
   * @see Vec2#add(Vec2, Vec2, Vec2)
   */
  @Chainable
  public Face2 translate ( final Vec2 v ) {

    Vec2 c;
    final int len = this.vertices.length;
    for (int i = 0; i < len; ++i) {
      c = this.vertices[i].coord;
      Vec2.add(c, v, c);
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
  public static Vec2 centroid (
      final Face2 face,
      final Vec2 target ) {

    target.reset();
    final Vert2[] verts = face.vertices;
    final int len = verts.length;
    for (int i = 0; i < len; ++i) {
      Vec2.add(target, verts[i].coord, target);
    }
    return Vec2.div(target, len, target);
  }
}