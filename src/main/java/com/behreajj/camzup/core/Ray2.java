package com.behreajj.camzup.core;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.TreeSet;

/**
 * A direction that extends from an originating point.
 */
public class Ray2 {

    /**
     * The ray's direction.
     */
    public final Vec2 dir = Vec2.forward(new Vec2());

    /**
     * The ray's origin.
     */
    public final Vec2 origin = new Vec2();

    /**
     * The default constructor.
     */
    public Ray2() {
    }

    /**
     * Creates a new ray from a source.
     *
     * @param source the source
     */
    public Ray2(final Ray2 source) {
        this.set(source);
    }

    /**
     * Creates a new ray from an origin and direction.
     *
     * @param orig the origin
     * @param dir  the direction
     */
    public Ray2(final Vec2 orig, final Vec2 dir) {

        this.set(orig, dir);
    }

    /**
     * Finds the point at a given time on a ray.
     *
     * @param ray    the ray
     * @param time   the time step
     * @param target the output vector
     * @return the point
     * @see Utils#approx(float, float)
     * @see Utils#invSqrtUnchecked(float)
     * @see Vec2#magSq(Vec2)
     */
    public static Vec2 eval(final Ray2 ray, final float time, final Vec2 target) {

        final Vec2 origin = ray.origin;
        final Vec2 dir = ray.dir;
        final float dmsq = Vec2.magSq(dir);
        if (time > 0.0f && dmsq > 0.0f) {
            if (Utils.approx(dmsq, 1.0f)) {
                return target.set(origin.x + dir.x * time, origin.y + dir.y * time);
            }
            final float tm = time * Utils.invSqrtUnchecked(dmsq);
            return target.set(origin.x + dir.x * tm, origin.y + dir.y * tm);
        }
        return target.set(origin);
    }

    /**
     * Sets a ray from an origin and destination point.
     *
     * @param orig   the origin
     * @param dest   the destination
     * @param target the output ray
     * @return the ray
     * @see Vec2#subNorm(Vec2, Vec2, Vec2)
     */
    public static Ray2 fromPoints(final Vec2 orig, final Vec2 dest, final Ray2 target) {

        target.origin.set(orig);
        Vec2.subNorm(dest, orig, target.dir);
        return target;
    }

    /**
     * Finds points of intersection, if any, between a ray and a bounds.
     *
     * @param ray    the ray
     * @param bounds the bounds
     * @return the points
     */
    public static Vec2[] intersect(final Ray2 ray, final Bounds2 bounds) {

        /*
         * Avoid the possibility of duplicates when a ray intersects a corner
         * where two line segments meet.
         */
        final TreeSet<Vec2> uniques = new TreeSet<>();

        final Vec2 rOrig = ray.origin;
        final Vec2 rDir = ray.dir;
        final Vec2 min = bounds.min;
        final Vec2 max = bounds.max;

        final double x0 = min.x;
        final double y0 = min.y;
        final double x1 = max.x;
        final double y1 = max.y;

        final double rx = rOrig.x;
        final double ry = rOrig.y;
        final double dx = rDir.x;
        final double dy = rDir.y;

        final double xEdge0 = x1 - x0;
        final double yEdge0 = y1 - y0;
        final double xEdge1 = x0 - x1;
        final double yEdge1 = y0 - y1;

        final double xDist0 = rx - x0;
        final double yDist0 = ry - y0;
        final double xDist1 = rx - x1;
        final double yDist1 = ry - y1;

        final double dot0 = -dy * xEdge0;
        final double dot1 = dx * yEdge0;
        final double dot2 = -dy * xEdge1;
        final double dot3 = dx * yEdge1;

        final double dxry = dx * ry;
        final double dyrx = dy * rx;

        if (dot0 != 0.0d) {
            final double t10 = xEdge0 * yDist0 / dot0;
            if (t10 > 0.0d) {
                final double t20 = (dx * yDist0 - dyrx + dy * x0) / dot0;
                if (t20 >= 0.0d && t20 <= 1.0d) {
                    uniques.add(new Vec2((float) ((1.0d - t20) * x0 + t20 * x1), min.y));
                }
            }
        }

        if (dot1 != 0.0d) {
            final double t11 = -yEdge0 * xDist1 / dot1;
            if (t11 > 0.0d) {
                final double t21 = (dxry - dx * y0 - dy * xDist1) / dot1;
                if (t21 >= 0.0d && t21 <= 1.0d) {
                    uniques.add(new Vec2(max.x, (float) ((1.0d - t21) * y0 + t21 * y1)));
                }
            }
        }

        if (dot2 != 0.0d) {
            final double t12 = xEdge1 * yDist1 / dot2;
            if (t12 > 0.0d) {
                final double t22 = (dx * yDist1 - dyrx + dy * x1) / dot2;
                if (t22 >= 0.0d && t22 <= 1.0d) {
                    uniques.add(new Vec2((float) ((1.0d - t22) * x1 + t22 * x0), max.y));
                }
            }
        }

        if (dot3 != 0.0d) {
            final double t13 = -yEdge1 * xDist0 / dot3;
            if (t13 > 0.0d) {
                final double t23 = (dxry - dx * y1 - dy * xDist0) / dot3;
                if (t23 >= 0.0d && t23 <= 1.0d) {
                    uniques.add(new Vec2(min.x, (float) ((1.0d - t23) * y1 + t23 * y0)));
                }
            }
        }

        final Vec2[] arr = uniques.toArray(new Vec2[0]);
        Arrays.sort(arr, new Vec2.SortDistSq(rOrig));

        return arr;
    }

    /**
     * Finds points of intersection, if any, between a ray and a mesh entity.
     *
     * @param r the ray
     * @param m the mesh
     * @return the points
     * @see Ray2#intersect(Ray2, Mesh2, TreeSet)
     */
    public static Vec2[] intersect(final Ray2 r, final Mesh2 m) {

        final TreeSet<Vec2> uniques = new TreeSet<>();
        Ray2.intersect(r, m, uniques);
        final Vec2[] arr = uniques.toArray(new Vec2[0]);
        Arrays.sort(arr, new Vec2.SortDistSq(r.origin));

        return arr;
    }

    /**
     * Finds points of intersection, if any, between a ray and a mesh entity.
     * Transforms the ray to
     * local space.
     *
     * @param r  the ray
     * @param me the mesh entity
     * @return the points
     * @see Transform2#invMul(Transform2, Ray2, Ray2)
     * @see Ray2#intersect(Ray2, Mesh2, TreeSet)
     */
    public static Vec2[] intersect(final Ray2 r, final MeshEntity2 me) {

        final Transform2 t = me.transform;
        final Ray2 local = Transform2.invMul(t, r, new Ray2());
        final TreeSet<Vec2> uniques = new TreeSet<>();
        for (final Mesh2 m : me) {
            Ray2.intersect(local, m, uniques);
        }

        final Vec2[] arr = uniques.toArray(new Vec2[0]);
        for (Vec2 vec2 : arr) {
            Transform2.mulPoint(t, vec2, vec2);
        }

        Arrays.sort(arr, new Vec2.SortDistSq(r.origin));

        return arr;
    }

    /**
     * Finds points of intersection, if any, between a ray and a mesh entity.
     * Transforms the ray to
     * local space.
     *
     * @param ray    the ray
     * @param center the circle center
     * @param radius the circle radius
     * @return the points
     */
    public static Vec2[] intersect(final Ray2 ray, final Vec2 center, final float radius) {

        final TreeSet<Vec2> uniques = new TreeSet<>();

        final Vec2 rOrig = ray.origin;
        final Vec2 rDir = ray.dir;

        final double rx = rOrig.x;
        final double ry = rOrig.y;
        final double dx = rDir.x;
        final double dy = rDir.y;

        final double cx = center.x;
        final double cy = center.y;

        final double ux = cx - rx;
        final double uy = cy - ry;

        /* Project u onto d. */
        final double dotuv = ux * dx + uy * dy;
        final double vmsq = dx * dx + dy * dy;
        final double uvScalarProj = vmsq > 0.0d ? dotuv / vmsq : 0.0d;
        final double u1x = dx * uvScalarProj;
        final double u1y = dy * uvScalarProj;

        final double u2x = ux - u1x;
        final double u2y = uy - u1y;
        final double u2msq = u2x * u2x + u2y * u2y;
        final double rSq = (double) radius * (double) radius;

        if (u2msq <= rSq) {
            final double opu1x = rx + u1x;
            final double opu1y = ry + u1y;

            final double m = Math.sqrt(rSq - u2msq);
            final double mvx = m * dx;
            final double mvy = m * dy;

            uniques.add(new Vec2((float) (opu1x + mvx), (float) (opu1y + mvy)));
            uniques.add(new Vec2((float) (opu1x - mvx), (float) (opu1y - mvy)));
        }

        final Vec2[] arr = uniques.toArray(new Vec2[0]);
        Arrays.sort(arr, new Vec2.SortDistSq(ray.origin));

        return arr;
    }

    /**
     * Finds points of intersection, if any, between a ray and a line segment.
     *
     * @param ray  the ray
     * @param orig the origin
     * @param dest the destination
     * @return the points
     */
    public static Vec2[] intersect(final Ray2 ray, final Vec2 orig, final Vec2 dest) {

        final TreeSet<Vec2> uniques = new TreeSet<>();
        Ray2.factorEdge(
            ray.origin.x, ray.origin.y, ray.dir.x, ray.dir.y, orig.x, orig.y, dest.x, dest.y, uniques);
        return uniques.toArray(new Vec2[0]);
    }

    /**
     * Finds an intersection between a ray and a line segment as a factor in [0.0,
     * 1.0] . Returns -1.0
     * if there is no intersection.
     *
     * @param xRayOrig the ray origin x
     * @param yRayOrig the ray origin y
     * @param xRayDir  the ray direction x
     * @param yRayDir  the ray direction y
     * @param xSegOrig the segment origin x
     * @param ySegOrig the segment origin y
     * @param xSegDest the segment destination x
     * @param ySegDest the segment destination y
     * @param uniques  the unique vectors
     */
    static void factorEdge(
        final float xRayOrig,
        final float yRayOrig,
        final float xRayDir,
        final float yRayDir,
        final float xSegOrig,
        final float ySegOrig,
        final float xSegDest,
        final float ySegDest,
        final TreeSet<Vec2> uniques) {

        /* Subtract destination from origin to get vector. */
        final double v1x = (double) xSegDest - (double) xSegOrig;
        final double v1y = (double) ySegDest - (double) ySegOrig;

        /* Find CCW perpendicular of ray direction. */
        final double v2x = -yRayDir;

        /*
         * This allows for one return statement at the end of the function instead
         * of multiple return statements in if blocks, which makes it easier to
         * automatically inline.
         */
        float fac = -1.0f;

        /* Find dot product between vector and perpendicular. */
        final double dot = v1x * v2x + v1y * (double) xRayDir;
        if (dot != 0.0d) {

            /* Find vector from ray origin to segment origin. */
            final double v0x = xRayOrig - xSegOrig;
            final double v0y = yRayOrig - ySegOrig;

            /* Find 2D cross product of v1 and v0, normalize. */
            final double t1 = (v1x * v0y - v1y * v0x) / dot;
            if (t1 > 0.0d) {

                /* Find dot product of v0 and v2, normalize. */
                final double t2 = (v0x * v2x + v0y * (double) xRayDir) / dot;
                if (t2 >= 0.0d && t2 <= 1.0d) {
                    final double u2 = 1.0d - t2;
                    final double x = u2 * (double) xSegOrig + t2 * (double) xSegDest;
                    final double y = u2 * (double) ySegOrig + t2 * (double) ySegDest;
                    uniques.add(new Vec2((float) x, (float) y));
                }
            }
        }

    }

    /**
     * Internal helper function to find intersections between a ray and a mesh.
     *
     * @param local   the ray in local space
     * @param m       the mesh
     * @param uniques the points
     * @return the points
     */
    static TreeSet<Vec2> intersect(final Ray2 local, final Mesh2 m, final TreeSet<Vec2> uniques) {

        final Vec2 rOrig = local.origin;
        final float rx = rOrig.x;
        final float ry = rOrig.y;

        final Vec2 rDir = local.dir;
        final float dx = rDir.x;
        final float dy = rDir.y;

        final Vec2[] vs = m.coords;
        final int[][][] fs = m.faces;

        for (final int[][] f : fs) {

            final int fLen = f.length;

            for (int j = 0; j < fLen; ++j) {

                final int k = (j + 1) % fLen;
                final Vec2 curr = vs[f[j][0]];
                final Vec2 next = vs[f[k][0]];
                Ray2.factorEdge(rx, ry, dx, dy, curr.x, curr.y, next.x, next.y, uniques);
            }
        }

        return uniques;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        return this.equals((Ray2) o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dir, origin);
    }

    /**
     * Sets this ray to look at a target point.
     *
     * @param target the target point
     * @return this ray
     * @see Vec2#subNorm(Vec2, Vec2, Vec2)
     */
    public Ray2 lookAt(final Vec2 target) {

        Vec2.subNorm(target, this.origin, this.dir);
        return this;
    }

    /**
     * Resets this ray to a default.
     *
     * @return this ray
     * @see Vec2#zero(Vec2)
     * @see Vec2#forward(Vec2)
     */
    public Ray2 reset() {

        Vec2.zero(this.origin);
        Vec2.forward(this.dir);
        return this;
    }

    /**
     * Sets this ray from a source.
     *
     * @param source the source ray
     * @return this ray
     */
    public Ray2 set(final Ray2 source) {

        return this.set(source.origin, source.dir);
    }

    /**
     * Sets the origin and direction of this ray. Normalizes the direction.
     *
     * @param orig the origin
     * @param dir  the direction
     * @return this ray
     * @see Vec2#normalize(Vec2, Vec2)
     */
    public Ray2 set(final Vec2 orig, final Vec2 dir) {

        this.origin.set(orig);
        Vec2.normalize(dir, this.dir);
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
     * Internal helper function to assist with methods that need to print many rays.
     * Appends to an
     * existing {@link StringBuilder}.
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
    protected boolean equals(final Ray2 ray) {

        return this.origin.equals(ray.origin) && this.dir.equals(ray.dir);
    }

    /**
     * An abstract class that may serve as an umbrella for any custom comparators of
     * Ray2 s.
     */
    public abstract static class AbstrComparator implements Comparator<Ray2> {

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
