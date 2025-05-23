package com.behreajj.camzup.core;

import java.util.Objects;

/**
 * Organizes components of a 3D mesh into an edge with an origin and
 * destination. This is not used by a mesh internally; it is created upon
 * retrieval from a mesh.
 */
public class Edge3 implements Comparable<Edge3> {

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
    public Edge3() {

        this.origin = new Vert3();
        this.dest = new Vert3();
    }

    /**
     * Constructs an edge from the origin and destination coordinate, texture
     * coordinate and normal data. Creates two vertex objects.
     *
     * @param coOrigin origin coordinate
     * @param txOrig   origin texture coordinate
     * @param nmOrig   origin normal
     * @param coDest   destination coordinate
     * @param txDest   destination texture coordinate
     * @param nmDest   destination normal
     */
    public Edge3(
        final Vec3 coOrigin,
        final Vec2 txOrig,
        final Vec3 nmOrig,
        final Vec3 coDest,
        final Vec2 txDest,
        final Vec3 nmDest) {

        this.origin = new Vert3(coOrigin, txOrig, nmOrig);
        this.dest = new Vert3(coDest, txDest, nmDest);
    }

    /**
     * Constructs an edge from two vertices, an origin and destination.
     *
     * @param origin the origin
     * @param dest   the destination
     */
    public Edge3(final Vert3 origin, final Vert3 dest) {

        this.set(origin, dest);
    }

    /**
     * Evaluates whether two edges are complements or neighbors, i.e., whether
     * one edge's origin is the other's destination.
     *
     * @param a the left comparisand
     * @param b the right comparisand
     * @return the evaluation
     * @see Vert3#approxCoord(Vert3, Vert3)
     */
    public static boolean areNeighbors(final Edge3 a, final Edge3 b) {

        return Vert3.approxCoord(a.dest, b.origin)
            && Vert3.approxCoord(a.origin, b.dest);
    }

    /**
     * Finds the azimuth of an edge. Subtracts the destination coordinate from
     * that of the origin, then supplies the difference to atan2 .
     *
     * @param edge the edge
     * @return the heading
     * @see Utils#atan2(float, float)
     */
    public static float azimuth(final Edge3 edge) {

        final Vec3 orig = edge.origin.coord;
        final Vec3 dest = edge.dest.coord;
        return Utils.atan2(dest.y - orig.y, dest.x - orig.x);
    }

    /**
     * Finds the distance between an edge and a point.
     *
     * @param a the edge
     * @param b the point
     * @return the distance
     */
    public static float dist(final Edge3 a, final Vec3 b) {

        // TEST

        /*
         * The magnitude of the rejection of u from v, where u equals b -
         * a.origin.
         */
        final Vec3 aOrig = a.origin.coord;
        final Vec3 aDest = a.dest.coord;

        /* Represent edge as point and vector. */
        final float vx = aDest.x - aOrig.x;
        final float vy = aDest.y - aOrig.y;
        final float vz = aDest.z - aOrig.z;
        final float dotvv = vx * vx + vy * vy + vz * vz;
        if (dotvv <= 0.0f) {
            return 0.0f;
        }

        /* Find difference between point and edge origin. */
        final float ux = b.x - aOrig.x;
        final float uy = b.y - aOrig.y;
        final float uz = b.z - aOrig.z;

        /* Cross difference with edge vector. */
        final float ax = uy * vz - uz * vy;
        final float ay = uz * vx - ux * vz;
        final float az = ux * vy - uy * vx;
        final float dotaa = ax * ax + ay * ay + az * az;

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
     * @return the point
     */
    public static Vec3 eval(final Edge3 edge, final float step, final Vec3 target) {

        final Vec3 coOrig = edge.origin.coord;
        final Vec3 coDest = edge.dest.coord;

        if (step <= 0.0f) {
            return target.set(coOrig);
        }
        if (step >= 1.0f) {
            return target.set(coDest);
        }

        final float u = 1.0f - step;
        return target.set(
            u * coOrig.x + step * coDest.x,
            u * coOrig.y + step * coDest.y,
            u * coOrig.z + step * coDest.z);
    }

    /**
     * Finds the inclination of an edge. Subtracts the destination coordinate
     * from that of the origin, then supplies the difference to arc sine.
     *
     * @param edge the edge
     * @return the heading
     * @see Utils#invHypot(float, float, float)
     * @see Utils#asin(float)
     */
    public static float inclination(final Edge3 edge) {

        final Vec3 orig = edge.origin.coord;
        final Vec3 dest = edge.dest.coord;

        final float dx = dest.x - orig.x;
        final float dy = dest.y - orig.y;
        final float dz = dest.z - orig.z;

        return Utils.asin(dz * Utils.invHypot(dx, dy, dz));
    }

    /**
     * Finds the Euclidean distance from the edge's origin coordinate to that
     * of its destination.
     *
     * @param edge the edge
     * @return the magnitude
     * @see Vec3#distEuclidean(Vec3, Vec3)
     */
    public static float mag(final Edge3 edge) {

        return Vec3.distEuclidean(edge.origin.coord, edge.dest.coord);
    }

    /**
     * Finds the squared Euclidean distance from the edge's origin coordinate
     * to that of its destination.
     *
     * @param edge the edge
     * @return the magnitude
     * @see Vec3#distSq(Vec3, Vec3)
     */
    public static float magSq(final Edge3 edge) {

        return Vec3.distSq(edge.origin.coord, edge.dest.coord);
    }

    /**
     * Projects a vector, representing a point, onto an edge. The scalar
     * projection is clamped to the range [0.0, 1.0], meaning the projection
     * will not exceed the edge's origin and destination.
     *
     * @param edge   the edge
     * @param v      the input vector
     * @param target the output vector
     * @return the projection
     */
    public static Vec3 projectVector(final Edge3 edge, final Vec3 v, final Vec3 target) {

        final Vec3 coOrig = edge.origin.coord;
        final Vec3 coDest = edge.dest.coord;

        final float bx = coDest.x - coOrig.x;
        final float by = coDest.y - coOrig.y;
        final float bz = coDest.z - coOrig.z;
        final float bSq = bx * bx + by * by + bz * bz;

        if (bSq <= 0.0f) {
            return target.set(coOrig);
        }

        final float ax = v.x - coOrig.x;
        final float ay = v.y - coOrig.y;
        final float az = v.z - coOrig.z;
        final float fac = (ax * bx + ay * by + az * bz) / bSq;

        if (fac >= 1.0f) {
            return target.set(coDest);
        }

        final float u = 1.0f - fac;
        return target.set(
            u * coOrig.x + fac * coDest.x,
            u * coOrig.y + fac * coDest.y,
            u * coOrig.z + fac * coDest.z);
    }

    /**
     * Tests to see if two edges share a vertex coordinate:
     *
     * <ul>
     * <li>Returns -1 when <em>a</em>'s origin is <em>b</em>'s destination.</li>
     * <li>Returns -2 when <em>a</em>'s destination is <em>b</em>'s destination.</li>
     * <li>Returns 1 when <em>a</em>'s destination is <em>b</em>'s origin.</li>
     * <li>Returns 2 when <em>a</em>'s origin is <em>b</em>'s origin.</li>
     * <li>Returns 0 when none of the above conditions are met.</li>
     * </ul>
     * <p>
     * If the left and right comparisand are the same, returns 2.
     *
     * @param a the left comparisand
     * @param b the right comparisand
     * @return the evaluation
     * @see Vert3#approxCoord(Vert3, Vert3)
     */
    public static int sharedCoord(final Edge3 a, final Edge3 b) {

        final Vert3 aOrig = a.origin;
        final Vert3 aDest = a.dest;
        final Vert3 bOrig = b.origin;
        final Vert3 bDest = b.dest;

        if (Vert3.approxCoord(aDest, bOrig)) {
            return 1;
        }
        if (Vert3.approxCoord(aOrig, bOrig)) {
            return 2;
        }
        if (Vert3.approxCoord(aOrig, bDest)) {
            return -1;
        }
        if (Vert3.approxCoord(aDest, bDest)) {
            return -2;
        }
        return 0;
    }

    /**
     * Compares two edges based on their identity hash codes.
     *
     * @return the evaluation
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(final Edge3 edge) {

        final int a = System.identityHashCode(this);
        final int b = System.identityHashCode(edge);
        return a < b ? -1 : a > b ? 1 : 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final Edge3 edge3))
            return false;
        return Objects.equals(this.dest, edge3.dest) && Objects.equals(this.origin, edge3.origin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.dest, this.origin);
    }

    /**
     * Rotates this edge by a quaternion.
     *
     * @param q the quaternion
     * @return this edge
     */
    public Edge3 rotate(final Quaternion q) {

        return this.rotateGlobal(q);
    }

    /**
     * Rotates the coordinates of this edge by the cosine and sine of an angle
     * and an axis. The texture coordinates are unaffected.
     *
     * @param cosa cosine of the angle
     * @param sina sine of the angle
     * @param axis axis
     * @return this edge
     * @see Vec3#rotate(Vec3, float, float, Vec3, Vec3)
     */
    public Edge3 rotateGlobal(final float cosa, final float sina, final Vec3 axis) {

        Vec3.rotate(this.origin.coord, cosa, sina, axis, this.origin.coord);
        Vec3.rotate(this.dest.coord, cosa, sina, axis, this.dest.coord);

        return this;
    }

    /**
     * Rotates the coordinates of this edge by an angle and axis. The texture
     * coordinates are unaffected.
     *
     * @param radians angle
     * @param axis    axis
     * @return this edge
     * @see Edge3#rotateGlobal(float, float, Vec3)
     */
    public Edge3 rotateGlobal(final float radians, final Vec3 axis) {

        final float cosa = (float) Math.cos(radians);
        final float sina = (float) Math.sin(radians);
        return this.rotateGlobal(cosa, sina, axis);
    }

    /**
     * Rotates the coordinates of this edge by a quaternion. The texture
     * coordinates are unaffected.
     *
     * @param q the quaternion
     * @return this edge
     * @see Quaternion#any(Quaternion)
     * @see Quaternion#mulVector(Quaternion, Vec3, Vec3)
     */
    public Edge3 rotateGlobal(final Quaternion q) {

        if (Quaternion.any(q)) {
            Quaternion.mulVector(q, this.origin.coord, this.origin.coord);
            Quaternion.mulVector(q, this.dest.coord, this.dest.coord);

            Quaternion.mulVector(q, this.origin.normal, this.origin.normal);
            Quaternion.mulVector(q, this.dest.normal, this.dest.normal);
        }

        return this;
    }

    /**
     * Rotates the coordinates of this edge by the sine and cosine of an angle
     * and an axis. The edge's center (midpoint) acts as a pivot. The texture
     * coordinates are unaffected.
     *
     * @param cosa   cosine of the angle
     * @param sina   sine of the angle
     * @param axis   axis
     * @param center center
     * @return this edge
     * @see Vec3#sub(Vec3, Vec3, Vec3)
     * @see Vec3#rotate(Vec3, float, float, Vec3, Vec3)
     * @see Vec3#add(Vec3, Vec3, Vec3)
     */
    public Edge3 rotateLocal(
        final float cosa,
        final float sina,
        final Vec3 axis,
        final Vec3 center) {

        final Vec3 coOrig = this.origin.coord;
        final Vec3 coDest = this.dest.coord;

        center.set(
            (coOrig.x + coDest.x) * 0.5f,
            (coOrig.y + coDest.y) * 0.5f,
            (coOrig.z + coDest.z) * 0.5f);

        Vec3.sub(coOrig, center, coOrig);
        Vec3.rotate(coOrig, cosa, sina, axis, coOrig);
        Vec3.add(coOrig, center, coOrig);

        Vec3.sub(coDest, center, coDest);
        Vec3.rotate(coDest, cosa, sina, axis, coDest);
        Vec3.add(coDest, center, coDest);

        Vec3.rotate(this.origin.normal, cosa, sina, axis, this.origin.normal);
        Vec3.rotate(this.dest.normal, cosa, sina, axis, this.dest.normal);

        return this;
    }

    /**
     * Rotates the coordinates of this edge by an angle and axis. The edge's
     * center (midpoint) acts as a pivot. The texture coordinates are
     * unaffected.
     *
     * @param radians angle
     * @param axis    axis
     * @param center  center
     * @return this edge
     * @see Edge3#rotateLocal(float, float, Vec3, Vec3)
     */
    public Edge3 rotateLocal(final float radians, final Vec3 axis, final Vec3 center) {

        final float cosa = (float) Math.cos(radians);
        final float sina = (float) Math.sin(radians);
        return this.rotateLocal(cosa, sina, axis, center);
    }

    /**
     * Rotates the coordinates of this edge by a quaternion. The edge's center
     * (midpoint) acts as a pivot. The texture coordinates are unaffected.
     *
     * @param q      the quaternion
     * @param center the edge center
     * @return this edge
     * @see Quaternion#any(Quaternion)
     * @see Quaternion#mulVector(Quaternion, Vec3, Vec3)
     * @see Vec3#add(Vec3, Vec3, Vec3)
     * @see Vec3#sub(Vec3, Vec3, Vec3)
     */
    public Edge3 rotateLocal(final Quaternion q, final Vec3 center) {

        if (Quaternion.any(q)) {
            final Vec3 coOrig = this.origin.coord;
            final Vec3 coDest = this.dest.coord;

            center.set(
                (coOrig.x + coDest.x) * 0.5f,
                (coOrig.y + coDest.y) * 0.5f,
                (coOrig.z + coDest.z) * 0.5f);

            Vec3.sub(coOrig, center, coOrig);
            Quaternion.mulVector(q, coOrig, coOrig);
            Vec3.add(coOrig, center, coOrig);

            Vec3.sub(coDest, center, coDest);
            Quaternion.mulVector(q, coDest, coDest);
            Vec3.add(coDest, center, coDest);
        }

        return this;
    }

    /**
     * Rotates the coordinates of this edge by an angle in radians around the x
     * axis. The texture coordinates are unaffected.
     *
     * @param radians angle
     * @return this edge
     * @see Edge3#rotateXGlobal(float, float)
     */
    public Edge3 rotateXGlobal(final float radians) {

        final float cosa = (float) Math.cos(radians);
        final float sina = (float) Math.sin(radians);
        return this.rotateXGlobal(cosa, sina);
    }

    /**
     * Rotates the coordinates of this edge by the sine and cosine of an angle
     * around the x axis. The texture coordinates are unaffected.
     *
     * @param cosa cosine of the angle
     * @param sina sine of the angle
     * @return this edge
     * @see Vec3#rotateX(Vec3, float, float, Vec3)
     */
    public Edge3 rotateXGlobal(final float cosa, final float sina) {

        Vec3.rotateX(this.origin.coord, cosa, sina, this.origin.coord);
        Vec3.rotateX(this.dest.coord, cosa, sina, this.dest.coord);

        return this;
    }

    /**
     * Rotates the coordinates of this edge by the sine and cosine of an angle
     * around the x axis. The edge's center (midpoint) acts as a pivot. The
     * texture coordinates are unaffected.
     *
     * @param cosa   cosine of the angle
     * @param sina   sine of the angle
     * @param center the center
     * @return this edge
     * @see Vec3#sub(Vec3, Vec3, Vec3)
     * @see Vec3#rotateX(Vec3, float, float, Vec3)
     * @see Vec3#add(Vec3, Vec3, Vec3)
     */
    public Edge3 rotateXLocal(final float cosa, final float sina, final Vec3 center) {

        final Vec3 coOrig = this.origin.coord;
        final Vec3 coDest = this.dest.coord;

        center.set(
            (coOrig.x + coDest.x) * 0.5f,
            (coOrig.y + coDest.y) * 0.5f,
            (coOrig.z + coDest.z) * 0.5f);

        Vec3.sub(coOrig, center, coOrig);
        Vec3.rotateX(coOrig, cosa, sina, coOrig);
        Vec3.add(coOrig, center, coOrig);

        Vec3.sub(coDest, center, coDest);
        Vec3.rotateX(coDest, cosa, sina, coDest);
        Vec3.add(coDest, center, coDest);

        return this;
    }

    /**
     * Rotates the coordinates of this edge by an angle in radians around the x
     * axis. The edge's center (midpoint) acts as a pivot. The texture
     * coordinates are unaffected.
     *
     * @param radians the angle
     * @param center  the center
     * @return this edge
     * @see Edge3#rotateXLocal(float, float, Vec3)
     */
    public Edge3 rotateXLocal(final float radians, final Vec3 center) {

        final float cosa = (float) Math.cos(radians);
        final float sina = (float) Math.sin(radians);
        return this.rotateXLocal(cosa, sina, center);
    }

    /**
     * Rotates the coordinates of this edge by an angle in radians around the y
     * axis. The texture coordinates are unaffected.
     *
     * @param radians angle
     * @return this edge
     * @see Edge3#rotateYGlobal(float, float)
     */
    public Edge3 rotateYGlobal(final float radians) {

        final float cosa = (float) Math.cos(radians);
        final float sina = (float) Math.sin(radians);
        return this.rotateYGlobal(cosa, sina);
    }

    /**
     * Rotates the coordinates of this edge by the sine and cosine of an angle
     * around the y axis. The texture coordinates are unaffected.
     *
     * @param cosa cosine of the angle
     * @param sina sine of the angle
     * @return this edge
     * @see Vec3#rotateY(Vec3, float, float, Vec3)
     */
    public Edge3 rotateYGlobal(final float cosa, final float sina) {

        Vec3.rotateY(this.origin.coord, cosa, sina, this.origin.coord);
        Vec3.rotateY(this.dest.coord, cosa, sina, this.dest.coord);

        return this;
    }

    /**
     * Rotates the coordinates of this edge by the sine and cosine of an angle
     * around the y axis. The edge's center (midpoint) acts as a pivot. The
     * texture coordinates are unaffected.
     *
     * @param cosa   cosine of the angle
     * @param sina   sine of the angle
     * @param center the center
     * @return this edge
     * @see Vec3#sub(Vec3, Vec3, Vec3)
     * @see Vec3#rotateY(Vec3, float, float, Vec3)
     * @see Vec3#add(Vec3, Vec3, Vec3)
     */
    public Edge3 rotateYLocal(final float cosa, final float sina, final Vec3 center) {

        final Vec3 coOrig = this.origin.coord;
        final Vec3 coDest = this.dest.coord;

        center.set(
            (coOrig.x + coDest.x) * 0.5f,
            (coOrig.y + coDest.y) * 0.5f,
            (coOrig.z + coDest.z) * 0.5f);

        Vec3.sub(coOrig, center, coOrig);
        Vec3.rotateY(coOrig, cosa, sina, coOrig);
        Vec3.add(coOrig, center, coOrig);

        Vec3.sub(coDest, center, coDest);
        Vec3.rotateY(coDest, cosa, sina, coDest);
        Vec3.add(coDest, center, coDest);

        return this;
    }

    /**
     * Rotates the coordinates of this edge by an angle in radians around the y
     * axis. The edge's center (midpoint) acts as a pivot. The texture
     * coordinates are unaffected.
     *
     * @param radians the angle
     * @param center  the center
     * @return this edge
     * @see Edge3#rotateYLocal(float, float, Vec3)
     */
    public Edge3 rotateYLocal(final float radians, final Vec3 center) {

        final float cosa = (float) Math.cos(radians);
        final float sina = (float) Math.sin(radians);
        return this.rotateYLocal(cosa, sina, center);
    }

    /**
     * Rotates the coordinates of this edge by an angle in radians around the z
     * axis. The texture coordinates are unaffected.
     *
     * @param radians angle
     * @return this edge
     * @see Edge3#rotateZGlobal(float, float)
     */
    public Edge3 rotateZGlobal(final float radians) {

        final float cosa = (float) Math.cos(radians);
        final float sina = (float) Math.sin(radians);
        return this.rotateZGlobal(cosa, sina);
    }

    /**
     * Rotates the coordinates of this edge by the sine and cosine of an angle
     * around the z axis. The texture coordinates are unaffected.
     *
     * @param cosa cosine of the angle
     * @param sina sine of the angle
     * @return this edge
     * @see Vec3#rotateZ(Vec3, float, float, Vec3)
     */
    public Edge3 rotateZGlobal(final float cosa, final float sina) {

        Vec3.rotateZ(this.origin.coord, cosa, sina, this.origin.coord);
        Vec3.rotateZ(this.dest.coord, cosa, sina, this.dest.coord);

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
     * @return this edge
     * @see Vec3#sub(Vec3, Vec3, Vec3)
     * @see Vec3#rotateZ(Vec3, float, float, Vec3)
     * @see Vec3#add(Vec3, Vec3, Vec3)
     */
    public Edge3 rotateZLocal(final float cosa, final float sina, final Vec3 center) {

        final Vec3 coOrig = this.origin.coord;
        final Vec3 coDest = this.dest.coord;

        center.set(
            (coOrig.x + coDest.x) * 0.5f,
            (coOrig.y + coDest.y) * 0.5f,
            (coOrig.z + coDest.z) * 0.5f);

        Vec3.sub(coOrig, center, coOrig);
        Vec3.rotateZ(coOrig, cosa, sina, coOrig);
        Vec3.add(coOrig, center, coOrig);

        Vec3.sub(coDest, center, coDest);
        Vec3.rotateZ(coDest, cosa, sina, coDest);
        Vec3.add(coDest, center, coDest);

        return this;
    }

    /**
     * Rotates the coordinates of this edge by an angle in radians around the z
     * axis. The edge's center (midpoint) acts as a pivot. The texture
     * coordinates are unaffected.
     *
     * @param radians the angle
     * @param center  the center
     * @return this edge
     * @see Edge3#rotateZLocal(float, float, Vec3)
     */
    public Edge3 rotateZLocal(final float radians, final Vec3 center) {

        final float cosa = (float) Math.cos(radians);
        final float sina = (float) Math.sin(radians);
        return this.rotateZLocal(cosa, sina, center);
    }

    /**
     * Scales the coordinates of this edge. The texture coordinates are
     * unaffected.
     *
     * @param scalar uniform scalar
     * @return this edge
     */
    public Edge3 scale(final float scalar) {

        return this.scaleGlobal(scalar);
    }

    /**
     * Scales the coordinates of this edge. The texture coordinates are
     * unaffected.
     *
     * @param scalar non uniform scalar
     * @return this edge
     */
    public Edge3 scale(final Vec3 scalar) {

        return this.scaleGlobal(scalar);
    }

    /**
     * Scales the coordinates of this edge. The texture coordinates are
     * unaffected. Uses global coordinates, i.e., doesn't consider the edge's
     * position.
     *
     * @param scalar the scalar
     * @return this edge
     * @see Vec3#mul(Vec3, float, Vec3)
     */
    public Edge3 scaleGlobal(final float scalar) {

        if (scalar != 0.0f) {
            Vec3.mul(this.origin.coord, scalar, this.origin.coord);
            Vec3.mul(this.dest.coord, scalar, this.dest.coord);
        }

        return this;
    }

    /**
     * Scales the coordinates of this edge. The texture coordinates are
     * unaffected. Uses global coordinates, i.e., doesn't consider the edge's
     * position.
     *
     * @param scalar the nonuniform scalar
     * @return this edge
     * @see Vec3#all(Vec3)
     * @see Vec3#hadamard(Vec3, Vec3, Vec3)
     */
    public Edge3 scaleGlobal(final Vec3 scalar) {

        if (Vec3.all(scalar)) {
            Vec3.hadamard(this.origin.coord, scalar, this.origin.coord);
            Vec3.hadamard(this.dest.coord, scalar, this.dest.coord);
        }

        return this;
    }

    /**
     * Scales the coordinates of this edge. Subtracts the edge's mean center
     * (midpoint) from each vertex, scales, then adds the mean center.
     *
     * @param scalar the uniform scalar
     * @param center the edge center
     * @return this edge
     * @see Vec3#sub(Vec3, Vec3, Vec3)
     * @see Vec3#mul(Vec3, float, Vec3)
     * @see Vec3#add(Vec3, Vec3, Vec3)
     */
    public Edge3 scaleLocal(final float scalar, final Vec3 center) {

        if (scalar != 0.0f) {
            final Vec3 coOrig = this.origin.coord;
            final Vec3 coDest = this.dest.coord;

            center.set(
                (coOrig.x + coDest.x) * 0.5f,
                (coOrig.y + coDest.y) * 0.5f,
                (coOrig.z + coDest.z) * 0.5f);

            Vec3.sub(coOrig, center, coOrig);
            Vec3.mul(coOrig, scalar, coOrig);
            Vec3.add(coOrig, center, coOrig);

            Vec3.sub(coDest, center, coDest);
            Vec3.mul(coDest, scalar, coDest);
            Vec3.add(coDest, center, coDest);
        }

        return this;
    }

    /**
     * Scales the coordinates of this edge. Subtracts the edge's mean center
     * (midpoint) from each vertex, scales, then adds the mean center.
     *
     * @param scalar the nonuniform scalar
     * @param center the edge's center
     * @return this edge
     * @see Vec3#add(Vec3, Vec3, Vec3)
     * @see Vec3#all(Vec3)
     * @see Vec3#hadamard(Vec3, Vec3, Vec3)
     * @see Vec3#none(Vec3)
     * @see Vec3#sub(Vec3, Vec3, Vec3)
     */
    public Edge3 scaleLocal(final Vec3 scalar, final Vec3 center) {

        if (Vec3.all(scalar)) {
            final Vec3 coOrig = this.origin.coord;
            final Vec3 coDest = this.dest.coord;

            center.set(
                (coOrig.x + coDest.x) * 0.5f,
                (coOrig.y + coDest.y) * 0.5f,
                (coOrig.z + coDest.z) * 0.5f);

            Vec3.sub(coOrig, center, coOrig);
            Vec3.hadamard(coOrig, scalar, coOrig);
            Vec3.add(coOrig, center, coOrig);

            Vec3.sub(coDest, center, coDest);
            Vec3.hadamard(coDest, scalar, coDest);
            Vec3.add(coDest, center, coDest);
        }

        return this;
    }

    /**
     * Sets the origin and destination coordinate, texture coordinate and
     * normal data by reference.
     *
     * @param coOrigin origin coordinate
     * @param txOrig   origin texture coordinate
     * @param nmOrigin origin normal
     * @param coDest   destination coordinate
     * @param txDest   destination texture coordinate
     * @param nmDest   destination normal
     * @return this edge
     */
    public Edge3 set(
        final Vec3 coOrigin,
        final Vec2 txOrig,
        final Vec3 nmOrigin,
        final Vec3 coDest,
        final Vec2 txDest,
        final Vec3 nmDest) {

        this.origin.set(coOrigin, txOrig, nmOrigin);
        this.dest.set(coDest, txDest, nmDest);
        return this;
    }

    /**
     * Sets this edge by vertex.
     *
     * @param orig the origin vertex
     * @param dest the destination vertex
     * @return this edge
     */
    public Edge3 set(final Vert3 orig, final Vert3 dest) {

        this.origin = orig;
        this.dest = dest;
        return this;
    }

    /**
     * Returns a string representation of this edge.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return this.toString(Utils.FIXED_PRINT);
    }

    /**
     * Returns a string representation of this edge.
     *
     * @param places the number of places
     * @return the string
     */
    public String toString(final int places) {

        return this.toString(new StringBuilder(768), places).toString();
    }

    /**
     * Transforms this edge by a matrix.
     *
     * @param m the matrix
     * @return this edge
     * @see Mat4#mulPoint(Mat4, Vec3, Vec3)
     */
    public Edge3 transform(final Mat4 m) {

        Mat4.mulPoint(m, this.origin.coord, this.origin.coord);
        Mat4.mulPoint(m, this.dest.coord, this.dest.coord);

        return this;
    }

    /**
     * Transforms this edge by a transform.
     *
     * @param tr the transform
     * @return this edge
     * @see Transform3#mulPoint(Transform3, Vec3, Vec3)
     */
    public Edge3 transform(final Transform3 tr) {

        Transform3.mulPoint(tr, this.origin.coord, this.origin.coord);
        Transform3.mulPoint(tr, this.dest.coord, this.dest.coord);

        return this;
    }

    /**
     * Translates the coordinates of this edge. The texture coordinates and
     * normals are unaffected.
     *
     * @param v translation
     * @return this edge
     * @see Vec3#add(Vec3, Vec3, Vec3)
     */
    public Edge3 translate(final Vec3 v) {

        Vec3.add(this.origin.coord, v, this.origin.coord);
        Vec3.add(this.dest.coord, v, this.dest.coord);

        return this;
    }

    /**
     * Internal helper function to assist with methods that need to print many
     * edges. Appends to an existing {@link StringBuilder}.
     *
     * @param sb     the string builder
     * @param places the number of places
     * @return the string builder
     */
    StringBuilder toString(final StringBuilder sb, final int places) {

        sb.append("{\"origin\":");
        this.origin.toString(sb, places);
        sb.append(",\"dest\":");
        this.dest.toString(sb, places);
        sb.append('}');
        return sb;
    }

    /**
     * Tests this edge for equivalence with another. To be true, the edges'
     * origins must be equal and their destinations must be equal.
     *
     * @param edge3 the comparisand
     * @return the evaluation
     */
    protected boolean equalsDirected(final Edge3 edge3) {

        if (this.dest == null) {
            if (edge3.dest != null) {
                return false;
            }
        } else if (!this.dest.equals(edge3.dest)) {
            return false;
        }

        if (this.origin == null) {
            return edge3.origin == null;
        }
        return this.origin.equals(edge3.origin);
    }
}
