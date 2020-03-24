package camzup.core;

/**
 * Organizes components of a 3D mesh into an edge with an origin and
 * destination.
 *
 * This is not used by a mesh internally; it is created upon retrieval
 * from a mesh.
 */
public class Edge3 implements Comparable < Edge3 > {

  /**
   * The destination vertex.
   */
  public Vert3 dest;

  /**
   * The origin vertex.
   */
  public Vert3 origin;

  /**
   * The default constructor. Creates two empty vertices.
   */
  public Edge3 ( ) {

    this.origin = new Vert3();
    this.dest = new Vert3();
  }

  /**
   * Constructs an edge from the origin and destination coordinate,
   * texture coordinate and normal data. Creates two vertex objects.
   *
   * @param coOrigin origin coordinate
   * @param txOrigin origin texture coordinate
   * @param nmOrigin origin normal
   * @param coDest   destination coordinate
   * @param txDest   destination texture coordinate
   * @param nmDest   destination normal
   */
  public Edge3 (
      final Vec3 coOrigin,
      final Vec2 txOrigin,
      final Vec3 nmOrigin,
      final Vec3 coDest,
      final Vec2 txDest,
      final Vec3 nmDest ) {

    this.origin = new Vert3(coOrigin, txOrigin, nmOrigin);
    this.dest = new Vert3(coDest, txDest, nmDest);
  }

  /**
   * Constructs an edge from two vertices, an origin and destination.
   *
   * @param origin the origin
   * @param dest   the destination
   */
  public Edge3 (
      final Vert3 origin,
      final Vert3 dest ) {

    this.set(origin, dest);
  }

  /**
   * Tests this edge for equivalence with another.
   *
   * @return the evaluation
   */
  protected boolean equals ( final Edge3 edge3 ) {

    if ( this.dest == null ) {
      if ( edge3.dest != null ) { return false; }
    } else if ( !this.dest.equals(edge3.dest) ) { return false; }

    if ( this.origin == null ) {
      if ( edge3.origin != null ) { return false; }
    } else if ( !this.origin.equals(edge3.origin) ) { return false; }

    return true;
  }

  /**
   * Compares two edges based on their identity hash codes.
   *
   * @return the evaluation
   */
  @Override
  public int compareTo ( final Edge3 edge ) {

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
    return this.equals((Edge3) obj);
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
   * Rotates the coordinates and normals of this edge by an angle and
   * axis. The texture coordinates are unaffected.
   *
   * @param radians angle
   * @param axis    axis
   * @return this edge
   */
  @Chainable
  public Edge3 rotate (
      final float radians,
      final Vec3 axis ) {

    final float cosa = Utils.cos(radians);
    final float sina = Utils.sin(radians);

    Vec3.rotate(this.origin.coord,
        cosa, sina, axis, this.origin.coord);
    Vec3.rotate(this.dest.coord,
        cosa, sina, axis, this.dest.coord);

    return this;
  }

  /**
   * Rotates the coordinates of this edge by a quaternion. The texture
   * coordinates are unaffected.
   *
   * @param q the quaternion
   * @return this edge
   */
  @Chainable
  public Edge3 rotate ( final Quaternion q ) {

    Quaternion.mulVector(q, this.origin.coord, this.origin.coord);
    Quaternion.mulVector(q, this.dest.coord, this.dest.coord);

    return this;
  }

  /**
   * Rotates the coordinates of this edge by an angle in radians around
   * the x axis. The texture coordinates are unaffected.
   *
   * @param radians angle
   * @return this edge
   */
  @Chainable
  public Edge3 rotateX ( final float radians ) {

    final float cosa = Utils.cos(radians);
    final float sina = Utils.sin(radians);

    Vec3.rotateX(this.origin.coord,
        cosa, sina, this.origin.coord);
    Vec3.rotateX(this.dest.coord,
        cosa, sina, this.dest.coord);

    return this;
  }

  /**
   * Rotates the coordinates of this edge by an angle in radians around
   * the y axis. The texture coordinates are unaffected.
   *
   * @param radians angle
   * @return this edge
   */
  @Chainable
  public Edge3 rotateY ( final float radians ) {

    final float cosa = Utils.cos(radians);
    final float sina = Utils.sin(radians);

    Vec3.rotateY(this.origin.coord,
        cosa, sina, this.origin.coord);
    Vec3.rotateY(this.dest.coord,
        cosa, sina, this.dest.coord);

    return this;
  }

  /**
   * Rotates the coordinates of this edge by an angle in radians around
   * the z axis. The texture coordinates are unaffected.
   *
   * @param radians angle
   * @return this edge
   */
  @Chainable
  public Edge3 rotateZ ( final float radians ) {

    final float cosa = Utils.cos(radians);
    final float sina = Utils.sin(radians);

    Vec3.rotateZ(this.origin.coord,
        cosa, sina, this.origin.coord);
    Vec3.rotateZ(this.dest.coord,
        cosa, sina, this.dest.coord);

    return this;
  }

  /**
   * Scales the coordinates of this edge. The texture coordinates and
   * normals are unaffected.
   *
   * @param scalar uniform scalar
   * @return this edge
   */
  @Chainable
  public Edge3 scale ( final float scalar ) {

    return this.scaleGlobal(scalar);
  }

  /**
   * Scales the coordinates of this edge. The texture coordinates and
   * normals are unaffected.
   *
   * @param scalar non uniform scalar
   * @return this edge
   */
  @Chainable
  public Edge3 scale ( final Vec3 scalar ) {

    return this.scaleGlobal(scalar);
  }

  /**
   * Scales the coordinates of this edge. The texture coordinates are
   * unaffected. Uses global coordinates, i.e., doesn't consider the
   * face's position.
   *
   * @param scalar the scalar
   * @return this edge
   */
  @Chainable
  public Edge3 scaleGlobal ( final float scalar ) {

    if ( scalar == 0.0f ) { return this; }

    Vec3.mul(this.origin.coord, scalar, this.origin.coord);
    Vec3.mul(this.dest.coord, scalar, this.dest.coord);
    return this;
  }

  /**
   * Scales the coordinates of this edge. The texture coordinates are
   * unaffected. Uses global coordinates, i.e., doesn't consider the
   * face's position.
   *
   * @param scalar the nonuniform scalar
   * @return this edge
   */
  @Chainable
  public Edge3 scaleGlobal ( final Vec3 scalar ) {

    if ( Vec3.none(scalar) ) { return this; }

    Vec3.mul(this.origin.coord, scalar, this.origin.coord);
    Vec3.mul(this.dest.coord, scalar, this.dest.coord);
    return this;
  }

  /**
   * Scales the coordinates of this edge. Subtracts the edge's centroid
   * from each vertex, scales, then adds the centroid.
   *
   * @param scalar the uniform scalar
   * @return this edge
   */
  @Chainable
  public Edge3 scaleLocal ( final float scalar ) {

    // TEST

    if ( scalar == 0.0f ) { return this; }

    final Vec3 coOrigin = this.origin.coord;
    final Vec3 coDest = this.dest.coord;

    final Vec3 mp = new Vec3(
        (coOrigin.x + coDest.x) * 0.5f,
        (coOrigin.y + coDest.y) * 0.5f,
        (coOrigin.z + coDest.z) * 0.5f);

    Vec3.sub(coOrigin, mp, coOrigin);
    Vec3.mul(coOrigin, scalar, coOrigin);
    Vec3.add(coOrigin, mp, coOrigin);

    Vec3.sub(coDest, mp, coDest);
    Vec3.mul(coDest, scalar, coDest);
    Vec3.add(coDest, mp, coDest);

    return this;
  }

  /**
   * Scales the coordinates of this edge. Subtracts the edge's centroid
   * from each vertex, scales, then adds the centroid.
   *
   * @param scalar the nonuniform scalar
   * @return this edge
   */
  @Chainable
  public Edge3 scaleLocal ( final Vec3 scalar ) {

    // TEST

    if ( Vec3.none(scalar) ) { return this; }

    final Vec3 coOrigin = this.origin.coord;
    final Vec3 coDest = this.dest.coord;

    final Vec3 mp = new Vec3(
        (coOrigin.x + coDest.x) * 0.5f,
        (coOrigin.y + coDest.y) * 0.5f,
        (coOrigin.z + coDest.z) * 0.5f);

    Vec3.sub(coOrigin, mp, coOrigin);
    Vec3.mul(coOrigin, scalar, coOrigin);
    Vec3.add(coOrigin, mp, coOrigin);

    Vec3.sub(coDest, mp, coDest);
    Vec3.mul(coDest, scalar, coDest);
    Vec3.add(coDest, mp, coDest);

    return this;
  }

  /**
   * Sets the origin and destination coordinate, texture coordinate and
   * normal data.
   *
   * @param coOrigin origin coordinate
   * @param txOrigin origin texture coordinate
   * @param nmOrigin origin normal
   * @param coDest   destination coordinate
   * @param txDest   destination texture coordinate
   * @param nmDest   destination normal
   * @return this edge
   */
  @Chainable
  public Edge3 set (
      final Vec3 coOrigin,
      final Vec2 txOrigin,
      final Vec3 nmOrigin,
      final Vec3 coDest,
      final Vec2 txDest,
      final Vec3 nmDest ) {

    this.origin.set(coOrigin, txOrigin, nmOrigin);
    this.dest.set(coDest, txDest, nmDest);
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
  public Edge3 set (
      final Vert3 origin,
      final Vert3 dest ) {

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
  public String toString ( ) {

    return this.toString(4);
  }

  /**
   * Returns a string representation of this edge.
   *
   * @param places the number of places
   * @return the string
   */
  public String toString ( final int places ) {

    return new StringBuilder(1024)
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
   * and normals are unaffected.
   *
   * @param v translation
   * @return this edge
   */
  @Chainable
  public Edge3 translate ( final Vec3 v ) {

    Vec3.add(this.origin.coord, v, this.origin.coord);
    Vec3.add(this.dest.coord, v, this.dest.coord);
    return this;
  }

  /**
   * Finds the azimuth of an edge. Subtracts the destination coordinate
   * from that of the origin, then supplies the difference to atan2 .
   *
   * @param edge the edge
   * @return the heading
   * @see Utils#atan2(float, float)
   */
  public static float azimuth ( final Edge3 edge ) {

    final Vec3 dest = edge.dest.coord;
    final Vec3 origin = edge.origin.coord;
    return Utils.atan2(
        dest.y - origin.y,
        dest.x - origin.x);
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
  @Experimental
  public static Vec3 eval (
      final Edge3 edge,
      final float step,
      final Vec3 target ) {

    final Vec3 coOrigin = edge.origin.coord;
    final Vec3 coDest = edge.dest.coord;

    if ( step <= 0.0f ) { return target.set(coOrigin); }
    if ( step >= 1.0f ) { return target.set(coDest); }

    final float u = 1.0f - step;
    return target.set(
        u * coOrigin.x + step * coDest.x,
        u * coOrigin.y + step * coDest.y,
        u * coOrigin.z + step * coDest.z);
  }

  /**
   * Finds the inclination of an edge. Subtracts the destination
   * coordinate from that of the origin, then supplies the difference to
   * arc sine .
   *
   * @param edge the edge
   * @return the heading
   * @see Utils#invHypot(float, float, float)
   * @see Utils#asin(float)
   */
  public static float inclination ( final Edge3 edge ) {

    final Vec3 dest = edge.dest.coord;
    final Vec3 origin = edge.origin.coord;

    final float dx = dest.x - origin.x;
    final float dy = dest.y - origin.y;
    final float dz = dest.z - origin.z;

    return Utils.asin(dz * Utils.invHypot(dx, dy, dz));
  }

  /**
   * Finds the Euclidean distance from the edge's origin coordinate to
   * that of its destination.
   *
   * @param edge the edge
   * @return the magnitude
   * @see Vec3#distEuclidean(Vec3, Vec3)
   */
  public static float mag ( final Edge3 edge ) {

    return Vec3.distEuclidean(
        edge.origin.coord,
        edge.dest.coord);
  }

  /**
   * Finds the squared Euclidean distance from the edge's origin
   * coordinate to that of its destination.
   *
   * @param edge the edge
   * @return the magnitude
   * @see Vec3#distSq(Vec3, Vec3)
   */
  public static float magSq ( final Edge3 edge ) {

    return Vec3.distSq(
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
  @Experimental
  public static Vec3 projectVector (
      final Edge3 edge,
      final Vec3 v,
      final Vec3 target ) {

    final Vec3 coOrigin = edge.origin.coord;
    final Vec3 coDest = edge.dest.coord;

    final float bx = coDest.x - coOrigin.x;
    final float by = coDest.y - coOrigin.y;
    final float bz = coDest.z - coOrigin.z;
    final float bSq = bx * bx + by * by + bz * bz;

    if ( bSq <= 0.0f ) { return target.set(coOrigin); }

    final float ax = v.x - coOrigin.x;
    final float ay = v.y - coOrigin.y;
    final float az = v.z - coOrigin.z;
    final float fac = (ax * bx + ay * by + az * bz) / bSq;

    if ( fac >= 1.0f ) { return target.set(coDest); }

    final float u = 1.0f - fac;
    return target.set(
        u * coOrigin.x + fac * coDest.x,
        u * coOrigin.y + fac * coDest.y,
        u * coOrigin.z + fac * coDest.z);
  }
}