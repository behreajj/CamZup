package camzup.core;

/**
 * Organizes components of a 2D mesh into an edge with an origin and
 * destination.
 *
 * This is not used by a mesh internally; it is created upon retrieval
 * from a mesh.
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
  public Edge2 (
      final Vec2 coOrigin,
      final Vec2 txOrigin,
      final Vec2 coDest,
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
  public Edge2 (
      final Vert2 origin,
      final Vert2 dest ) {

    this.set(origin, dest);
  }

  /**
   * Tests this edge for equivalence with another.
   *
   * @return the evaluation
   */
  protected boolean equals ( final Edge2 edge2 ) {

    if ( this.dest == null ) {
      if ( edge2.dest != null ) { return false; }
    } else if ( !this.dest.equals(edge2.dest) ) { return false; }

    if ( this.origin == null ) {
      if ( edge2.origin != null ) { return false; }
    } else if ( !this.origin.equals(edge2.origin) ) { return false; }

    return true;
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
    if ( obj == null ) { return false; }
    if ( this.getClass() != obj.getClass() ) { return false; }
    return this.equals((Edge2) obj);
  }

  /**
   * Returns a hash code for this edge based on its origin and
   * destination.
   *
   * @return the hash
   */
  @Override
  public int hashCode ( ) {

    return (IUtils.MUL_BASE
        ^ (this.origin == null ? 0 : this.origin.hashCode()))
        * IUtils.HASH_MUL
        ^ (this.dest == null ? 0 : this.dest.hashCode());
  }

  /**
   * Rotates the coordinates of this edge by an angle in radians around
   * the z axis. The texture coordinates are unaffected.
   *
   * @param radians angle
   * @return this edge
   * @see Edge2#rotateZGlobal(float)
   */
  @Chainable
  public Edge2 rotateZ ( final float radians ) {

    return this.rotateZGlobal(radians);
  }

  /**
   * Rotates the coordinates of this edge by an angle in radians around
   * the z axis. Uses global coordinates, i.e. doesn't consider the
   * edge's position. The texture coordinates are unaffected.
   *
   * @param radians the angle
   * @return this edge
   */
  @Chainable
  public Edge2 rotateZGlobal ( final float radians ) {

    final float cosa = Utils.cos(radians);
    final float sina = Utils.sin(radians);

    Vec2.rotateZ(this.origin.coord,
        cosa, sina, this.origin.coord);
    Vec2.rotateZ(this.dest.coord,
        cosa, sina, this.dest.coord);
    return this;
  }

  /**
   * Rotates the coordinates of this edge by an angle in radians around
   * the z axis. The edge's midpoint acts as a pivot. The texture
   * coordinates are unaffected.
   *
   * @param radians the angle
   * @return this edge
   */
  @Chainable
  public Edge2 rotateZLocal ( final float radians ) {

    final float cosa = Utils.cos(radians);
    final float sina = Utils.sin(radians);

    final Vec2 coOrigin = this.origin.coord;
    final Vec2 coDest = this.dest.coord;

    final Vec2 mp = new Vec2(
        (coOrigin.x + coDest.x) * 0.5f,
        (coOrigin.y + coDest.y) * 0.5f);

    Vec2.sub(coOrigin, mp, coOrigin);
    Vec2.rotateZ(coOrigin, cosa, sina, coOrigin);
    Vec2.add(coOrigin, mp, coOrigin);

    Vec2.sub(coDest, mp, coDest);
    Vec2.rotateZ(coDest, cosa, sina, coDest);
    Vec2.add(coDest, mp, coDest);

    return this;
  }

  /**
   * Scales the coordinates of this edge. The texture coordinates are
   * unaffected.
   *
   * @param scale uniform scalar
   * @return this edge
   * @see Edge2#scaleGlobal(float)
   */
  @Chainable
  public Edge2 scale ( final float scale ) {

    return this.scaleGlobal(scale);
  }

  /**
   * Scales the coordinates of this edge. The texture coordinates are
   * unaffected.
   *
   * @param scalar non uniform scalar
   * @return this edge
   * @see Edge2#scaleGlobal(Vec2)
   */
  @Chainable
  public Edge2 scale ( final Vec2 scalar ) {

    return this.scaleGlobal(scalar);
  }

  /**
   * Scales the coordinates of this edge. The texture coordinates are
   * unaffected. Uses global coordinates, i.e., doesn't consider the
   * face's position.
   *
   * @param scalar the scalar
   * @return this edge
   * @see Vec2#mul(Vec2, float, Vec2)
   */
  @Chainable
  public Edge2 scaleGlobal ( final float scalar ) {

    if ( scalar == 0.0f ) { return this; }

    Vec2.mul(this.origin.coord, scalar, this.origin.coord);
    Vec2.mul(this.dest.coord, scalar, this.dest.coord);
    return this;
  }

  /**
   * Scales the coordinates of this edge. The texture coordinates are
   * unaffected. Uses global coordinates, i.e., doesn't consider the
   * face's position.
   *
   * @param scalar the nonuniform scalar
   * @return this edge
   * @see Vec2#mul(Vec2, Vec2, Vec2)
   */
  @Chainable
  public Edge2 scaleGlobal ( final Vec2 scalar ) {

    if ( Vec2.none(scalar) ) { return this; }

    Vec2.mul(this.origin.coord, scalar, this.origin.coord);
    Vec2.mul(this.dest.coord, scalar, this.dest.coord);
    return this;
  }

  /**
   * Scales the coordinates of this edge. Subtracts the edge's centroid
   * from each vertex, scales, then adds the centroid.
   *
   * @param scalar the uniform scalar
   * @return this edge
   * @see Vec2#sub(Vec2, Vec2, Vec2)
   * @see Vec2#mul(Vec2, float, Vec2)
   * @see Vec2#add(Vec2, Vec2, Vec2)
   */
  @Chainable
  public Edge2 scaleLocal ( final float scalar ) {

    if ( scalar == 0.0f ) { return this; }

    final Vec2 coOrigin = this.origin.coord;
    final Vec2 coDest = this.dest.coord;

    final Vec2 mp = new Vec2(
        (coOrigin.x + coDest.x) * 0.5f,
        (coOrigin.y + coDest.y) * 0.5f);

    Vec2.sub(coOrigin, mp, coOrigin);
    Vec2.mul(coOrigin, scalar, coOrigin);
    Vec2.add(coOrigin, mp, coOrigin);

    Vec2.sub(coDest, mp, coDest);
    Vec2.mul(coDest, scalar, coDest);
    Vec2.add(coDest, mp, coDest);

    return this;
  }

  /**
   * Scales the coordinates of this edge. Subtracts the edge's centroid
   * from each vertex, scales, then adds the centroid.
   *
   * @param scalar the nonuniform scalar
   * @return this edge
   * @see Vec2#none(Vec2)
   * @see Vec2#sub(Vec2, Vec2, Vec2)
   * @see Vec2#mul(Vec2, Vec2, Vec2)
   * @see Vec2#add(Vec2, Vec2, Vec2)
   */
  @Chainable
  @Experimental
  public Edge2 scaleLocal ( final Vec2 scalar ) {

    if ( Vec2.none(scalar) ) { return this; }

    final Vec2 coOrigin = this.origin.coord;
    final Vec2 coDest = this.dest.coord;

    final Vec2 mp = new Vec2(
        (coOrigin.x + coDest.x) * 0.5f,
        (coOrigin.y + coDest.y) * 0.5f);

    Vec2.sub(coOrigin, mp, coOrigin);
    Vec2.mul(coOrigin, scalar, coOrigin);
    Vec2.add(coOrigin, mp, coOrigin);

    Vec2.sub(coDest, mp, coDest);
    Vec2.mul(coDest, scalar, coDest);
    Vec2.add(coDest, mp, coDest);

    return this;
  }

  /**
   * Sets the origin and destination coordinate, texture coordinate and
   * normal data.
   *
   * @param coOrigin origin coordinate
   * @param txOrigin origin texture coordinate
   * @param coDest   destination coordinate
   * @param txDest   destination texture coordinate
   * @return this edge
   */
  @Chainable
  public Edge2 set (
      final Vec2 coOrigin,
      final Vec2 txOrigin,
      final Vec2 coDest,
      final Vec2 txDest ) {

    this.origin.set(coOrigin, txOrigin);
    this.dest.set(coDest, txDest);
    return this;
  }

  /**
   * Sets this edge by vertex.
   *
   * @param origin the origin vertex
   * @param dest   the destination vertex
   * @return this edge
   */
  @Chainable
  public Edge2 set (
      final Vert2 origin,
      final Vert2 dest ) {

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
  public String toString ( ) { return this.toString(4); }

  /**
   * Returns a string representation of this edge.
   *
   * @param places the number of places
   * @return the string
   */
  public String toString ( final int places ) {

    return new StringBuilder(512)
        .append("{ origin: ")
        .append(this.origin.toString(places))
        .append(", dest: ")
        .append(this.dest.toString(places))
        .append(' ')
        .append('}')
        .toString();
  }

  /**
   * Translates the coordinates of this edge. The texture coordinates
   * are unaffected.
   *
   * @param v translation
   * @return this edge
   * @see Vec2#add(Vec2, Vec2, Vec2)
   */
  @Chainable
  public Edge2 translate ( final Vec2 v ) {

    Vec2.add(this.origin.coord, v, this.origin.coord);
    Vec2.add(this.dest.coord, v, this.dest.coord);

    return this;
  }

  /**
   * Finds a point on the edge given a factor in the range [0.0, 1.0] .
   * Uses linear interpolation from the origin coordinate to that of the
   * destination.
   *
   * @param edge   the edge
   * @param step   the step
   * @param target the output vector
   * @return the point
   */
  public static Vec2 eval (
      final Edge2 edge,
      final float step,
      final Vec2 target ) {

    final Vec2 coOrigin = edge.origin.coord;
    final Vec2 coDest = edge.dest.coord;

    if ( step <= 0.0f ) { return target.set(coOrigin); }
    if ( step >= 1.0f ) { return target.set(coDest); }

    final float u = 1.0f - step;
    return target.set(
        u * coOrigin.x + step * coDest.x,
        u * coOrigin.y + step * coDest.y);
  }

  /**
   * Finds the heading of an edge. Subtracts the destination coordinate
   * from that of the origin, then supplies the difference to atan2 .
   *
   * @param edge the edge
   * @return the heading
   * @see Utils#atan2(float, float)
   */
  public static float heading ( final Edge2 edge ) {

    final Vec2 dest = edge.dest.coord;
    final Vec2 origin = edge.origin.coord;
    return Utils.atan2(
        dest.y - origin.y,
        dest.x - origin.x);
  }

  /**
   * Finds the Euclidean distance from the edge's origin coordinate to
   * that of its destination.
   *
   * @param edge the edge
   * @return the magnitude
   * @see Vec2#distEuclidean(Vec2, Vec2)
   */
  public static float mag ( final Edge2 edge ) {

    return Vec2.distEuclidean(
        edge.origin.coord,
        edge.dest.coord);
  }

  /**
   * Finds the squared Euclidean distance from the edge's origin
   * coordinate to that of its destination.
   *
   * @param edge the edge
   * @return the magnitude
   * @see Vec2#distSq(Vec2, Vec2)
   */
  public static float magSq ( final Edge2 edge ) {

    return Vec2.distSq(
        edge.origin.coord,
        edge.dest.coord);
  }

  /**
   * Projects a vector, representing a point, onto an edge. The scalar
   * projection is clamped to the range [0.0, 1.0], meaning the
   * projection will not exceed the edge's origin and destination.
   *
   * @param edge   the edge
   * @param v      the input vector
   * @param target the output vector
   * @return the projection
   * @see Utils#clamp01(float)
   */
  public static Vec2 projectVector (
      final Edge2 edge,
      final Vec2 v,
      final Vec2 target ) {

    final Vec2 coOrigin = edge.origin.coord;
    final Vec2 coDest = edge.dest.coord;

    final float bx = coDest.x - coOrigin.x;
    final float by = coDest.y - coOrigin.y;
    final float bSq = bx * bx + by * by;

    if ( bSq <= 0.0f ) { return target.set(coOrigin); }

    final float ax = v.x - coOrigin.x;
    final float ay = v.y - coOrigin.y;
    final float fac = (ax * bx + ay * by) / bSq;

    if ( fac >= 1.0f ) { return target.set(coDest); }

    final float u = 1.0f - fac;
    return target.set(
        u * coOrigin.x + fac * coDest.x,
        u * coOrigin.y + fac * coDest.y);
  }
}