package com.behreajj.camzup.core;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.TreeSet;

/**
 * A direction that extends from an origin point.
 */
public class Ray3 {

    /**
     * The ray's direction.
     */
    public final Vec3 dir = Vec3.forward(new Vec3());

    /**
     * The ray's origin.
     */
    public final Vec3 origin = new Vec3();

    /**
     * The default constructor.
     */
    public Ray3() {

        // TODO: Implement any intersects plane, mesh methods you can find?
    }

    /**
     * Creates a new ray from a source.
     *
     * @param source the source
     */
    public Ray3(final Ray3 source) {
        this.set(source);
    }

    /**
     * Creates a new ray from an origin and direction.
     *
     * @param orig the origin
     * @param dir  the direction
     */
    public Ray3(final Vec3 orig, final Vec3 dir) {

        this.set(orig, dir);
    }

    /**
     * Finds the point at a given time on a ray.
     *
     * @param ray    the ray
     * @param time   the time step
     * @param target the output vector
     * @return the point
     */
    public static Vec3 eval(
        final Ray3 ray,
        final float time,
        final Vec3 target) {

        if (time <= 0.0f) {
            return target.set(ray.origin);
        }

        final Vec3 rDir = ray.dir;
        final double dx = rDir.x;
        final double dy = rDir.y;
        final double dz = rDir.z;

        final double dmsq = dx * dx + dy * dy + dz * dz;
        if (dmsq <= Utils.EPSILON_D) {
            return target.set(ray.origin);
        }

        final double tm = time / Math.sqrt(dmsq);
        final Vec3 rOrig = ray.origin;
        return target.set(
            (float) (rOrig.x + tm * dx),
            (float) (rOrig.y + tm * dy),
            (float) (rOrig.z + tm * dz));
    }

    /**
     * Sets a ray from an origin and destination point.
     *
     * @param orig   the origin
     * @param dest   the destination
     * @param target the output ray
     * @return the ray
     * @see Vec3#subNorm(Vec3, Vec3, Vec3)
     */
    public static Ray3 fromPoints(
        final Vec3 orig,
        final Vec3 dest,
        final Ray3 target) {

        target.origin.set(orig);
        Vec3.subNorm(dest, orig, target.dir);
        return target;
    }

    /**
     * Finds points of intersection, if any, between a ray and a sphere.
     *
     * @param ray    the ray
     * @param center the circle center
     * @param radius the circle radius
     * @return the points
     */
    public static Vec3[] intersect(
        final Ray3 ray,
        final Vec3 center,
        final float radius) {

        // TEST

        final Vec3 rOrig = ray.origin;
        final Vec3 rDir = ray.dir;

        final double rx = rOrig.x;
        final double ry = rOrig.y;
        final double rz = rOrig.z;

        final double dx = rDir.x;
        final double dy = rDir.y;
        final double dz = rDir.z;

        final double cx = center.x;
        final double cy = center.y;
        final double cz = center.z;

        final double ux = cx - rx;
        final double uy = cy - ry;
        final double uz = cz - rz;

        /* Project u onto d. */
        final double dotuv = ux * dx + uy * dy + uz * dz;
        final double vmsq = dx * dx + dy * dy + dz * dz;
        final double uvScalarProj = vmsq > 0.0d ? dotuv / vmsq : 0.0d;

        final double u1x = dx * uvScalarProj;
        final double u1y = dy * uvScalarProj;
        final double u1z = dz * uvScalarProj;

        final double u2x = ux - u1x;
        final double u2y = uy - u1y;
        final double u2z = uz - u1z;

        final double u2msq = u2x * u2x + u2y * u2y + u2z * u2z;
        final double rSq = radius * radius;
        final TreeSet<Vec3> uniques = new TreeSet<>();

        if (u2msq <= rSq) {
            final double opu1x = rx + u1x;
            final double opu1y = ry + u1y;
            final double opu1z = rz + u1z;

            final double m = Math.sqrt(rSq - u2msq);
            final double mvx = m * dx;
            final double mvy = m * dy;
            final double mvz = m * dz;

            uniques.add(new Vec3(
                (float) (opu1x + mvx),
                (float) (opu1y + mvy),
                (float) (opu1z + mvz)));
            uniques.add(new Vec3(
                (float) (opu1x - mvx),
                (float) (opu1y - mvy),
                (float) (opu1z - mvz)));
        }

        final Vec3[] arr = uniques.toArray(new Vec3[0]);
        Arrays.sort(arr, new Vec3.SortDistSq(ray.origin));
        return arr;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        return this.equals((Ray3) o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.dir, this.origin);
    }

    /**
     * Sets this ray to look at a target point.
     *
     * @param target the target point
     * @return this ray
     * @see Vec3#subNorm(Vec3, Vec3, Vec3)
     */
    public Ray3 lookAt(final Vec3 target) {

        Vec3.subNorm(target, this.origin, this.dir);
        return this;
    }

    /**
     * Resets this ray to a default.
     *
     * @return this ray
     */
    public Ray3 reset() {

        Vec3.zero(this.origin);
        Vec3.forward(this.dir);
        return this;
    }

    /**
     * Sets this ray from a source.
     *
     * @param source the source ray
     * @return this ray
     */
    public Ray3 set(final Ray3 source) {

        return this.set(source.origin, source.dir);
    }

    /**
     * Sets the origin and direction of this ray. Normalizes the direction.
     *
     * @param orig the origin
     * @param dir  the direction
     * @return this ray
     * @see Vec3#normalize(Vec3, Vec3)
     */
    public Ray3 set(final Vec3 orig, final Vec3 dir) {

        this.origin.set(orig);
        Vec3.normalize(dir, this.dir);
        return this;
    }

    /**
     * Returns a string representation of this ray.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return this.toString(Utils.FIXED_PRINT);
    }

    /**
     * Returns a string representation of this ray.
     *
     * @param places number of decimal places
     * @return the string
     */
    public String toString(final int places) {

        return this.toString(new StringBuilder(256), places).toString();
    }

    /**
     * Internal helper function to assist with methods that need to print many
     * rays. Appends to an existing {@link StringBuilder}.
     *
     * @param sb     the string builder
     * @param places the number of places
     * @return the string builder
     */
    StringBuilder toString(final StringBuilder sb, final int places) {

        sb.append("{\"origin\":");
        this.origin.toString(sb, places);
        sb.append(",\"dir\":");
        this.dir.toString(sb, places);
        sb.append('}');
        return sb;
    }

    /**
     * Tests equivalence between this and another ray.
     *
     * @param ray the other ray
     * @return the evaluation
     */
    protected boolean equals(final Ray3 ray) {

        return this.origin.equals(ray.origin)
            && this.dir.equals(ray.dir);
    }

    /**
     * An abstract class that may serve as an umbrella for any custom
     * comparators of rays.
     */
    public abstract static class AbstrComparator implements Comparator<Ray3> {

        /**
         * The default constructor.
         */
        protected AbstrComparator() {
        }

        /**
         * Returns the simple name of this class.
         *
         * @return the string
         */
        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
