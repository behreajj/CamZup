package com.behreajj.camzup.core;

import java.util.Comparator;
import java.util.Objects;
import java.util.Random;

/**
 * A mutable, extensible class influenced by GLSL, OSL and Processing PVector.
 * This is intended for storing points and directions in three-dimensional
 * graphics programs. Instance methods are limited, while most static methods
 * require an explicit output variable to be provided.
 */
public class Vec3 implements Comparable<Vec3> {

    /**
     * Component on the x-axis in the Cartesian coordinate system.
     */
    public float x = 0.0f;

    /**
     * Component on the y-axis in the Cartesian coordinate system.
     */
    public float y = 0.0f;

    /**
     * Component on the z axis in the Cartesian coordinate system.
     */
    public float z = 0.0f;

    /**
     * The default vector constructor.
     */
    public Vec3() {
    }

    /**
     * Constructs a vector from boolean values.
     *
     * @param x the x component
     * @param y the y component
     * @param z the z component
     */
    public Vec3(final boolean x, final boolean y, final boolean z) {

        this.set(x, y, z);
    }

    /**
     * Constructs a vector from float values.
     *
     * @param x the x component
     * @param y the y component
     * @param z the z component
     */
    public Vec3(final float x, final float y, final float z) {

        this.set(x, y, z);
    }

    /**
     * Promotes a Vec2 to a Vec3.
     *
     * @param v2 the vector
     */
    public Vec3(final Vec2 v2) {
        this.set(v2);
    }

    /**
     * Promotes a Vec2 to a Vec3 with an extra component.
     *
     * @param v2 the vector
     * @param z  the z component
     */
    public Vec3(final Vec2 v2, final float z) {
        this.set(v2, z);
    }

    /**
     * Constructs a vector from a source vector's components.
     *
     * @param v the source vector
     */
    public Vec3(final Vec3 v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    /**
     * Finds the absolute value of each vector component.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the absolute vector
     * @see Utils#abs(float)
     */
    public static Vec3 abs(final Vec3 v, final Vec3 target) {

        return target.set(Utils.abs(v.x), Utils.abs(v.y), Utils.abs(v.z));
    }

    /**
     * Adds two vectors together.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the sum
     */
    public static Vec3 add(final Vec3 a, final Vec3 b, final Vec3 target) {

        return target.set(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    /**
     * Adds and then normalizes two vectors.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the normalized sum
     * @see Utils#invSqrtUnchecked(float)
     */
    public static Vec3 addNorm(final Vec3 a, final Vec3 b, final Vec3 target) {

        final float dx = a.x + b.x;
        final float dy = a.y + b.y;
        final float dz = a.z + b.z;
        final float mInv = Utils.invSqrtUnchecked(dx * dx + dy * dy + dz * dz);
        return target.set(dx * mInv, dy * mInv, dz * mInv);
    }

    /**
     * Adds and then normalizes two vectors. Discloses the intermediate sum as
     * an output.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @param sum    the sum
     * @return the normalized sum
     * @see Vec3#add(Vec3, Vec3, Vec3)
     * @see Vec3#normalize(Vec3, Vec3)
     */
    public static Vec3 addNorm(final Vec3 a, final Vec3 b, final Vec3 target, final Vec3 sum) {

        Vec3.add(a, b, sum);
        return Vec3.normalize(sum, target);
    }

    /**
     * Tests to see if all the vector's components are non-zero. Useful for
     * finding valid dimensions (width, depth and height) stored in vectors.
     *
     * @param v the input vector
     * @return the evaluation
     */
    public static boolean all(final Vec3 v) {

        return v.z != 0.0f && v.y != 0.0f && v.x != 0.0f;
    }

    /**
     * Evaluates two vectors like booleans, using the AND logic gate.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the evaluation
     * @see Utils#and(float, float)
     */
    public static Vec3 and(final Vec3 a, final Vec3 b, final Vec3 target) {

        return target.set(Utils.and(a.x, b.x), Utils.and(a.y, b.y), Utils.and(a.z, b.z));
    }

    /**
     * Finds the angle between two vectors. Returns zero when either vector is
     * zero.
     *
     * @param a the first vector
     * @param b the second vector
     * @return the angle
     * @see Vec3#any(Vec3)
     */
    public static float angleBetween(final Vec3 a, final Vec3 b) {

        /* To match distAngleUnsigned, this requires double precision. */

        if (Vec3.any(a) && Vec3.any(b)) {
            final double ax = a.x;
            final double ay = a.y;
            final double az = a.z;

            final double bx = b.x;
            final double by = b.y;
            final double bz = b.z;

            return (float) Math.acos(
                (ax * bx + ay * by + az * bz)
                    / (Math.sqrt(ax * ax + ay * ay + az * az)
                    * Math.sqrt(bx * bx + by * by + bz * bz)));
        }
        return 0.0f;
    }

    /**
     * Tests to see if any of the vector's components are non-zero.
     *
     * @param v the input vector
     * @return the evaluation
     */
    public static boolean any(final Vec3 v) {

        return v.z != 0.0f || v.y != 0.0f || v.x != 0.0f;
    }

    /**
     * Appends a vector to a one-dimensional vector array. Returns a new array.
     *
     * @param a the array
     * @param b the vector
     * @return a new array
     * @see System#arraycopy(Object, int, Object, int, int)
     */
    public static Vec3[] append(final Vec3[] a, final Vec3 b) {

        final boolean aNull = a == null;
        final boolean bNull = b == null;
        if (aNull && bNull) {
            return new Vec3[]{};
        }
        if (aNull) {
            return new Vec3[]{b};
        }
        if (bNull) {
            final Vec3[] result0 = new Vec3[a.length];
            System.arraycopy(a, 0, result0, 0, a.length);
            return result0;
        }

        final int aLen = a.length;
        final Vec3[] result1 = new Vec3[aLen + 1];
        System.arraycopy(a, 0, result1, 0, aLen);
        result1[aLen] = b;
        return result1;
    }

    /**
     * Tests to see if two vectors approximate each other.
     *
     * @param a left comparisand
     * @param b right comparisand
     * @return the evaluation
     * @see Vec3#approx(Vec3, Vec3, float)
     */
    public static boolean approx(final Vec3 a, final Vec3 b) {

        return Vec3.approx(a, b, Utils.EPSILON);
    }

    /**
     * Tests to see if two vectors approximate each other.
     *
     * @param a         left operand
     * @param b         right operand
     * @param tolerance the tolerance
     * @return the evaluation
     * @see Utils#approx(float, float, float)
     */
    public static boolean approx(final Vec3 a, final Vec3 b, final float tolerance) {

        return a == b
            || Utils.approx(a.z, b.z, tolerance)
            && Utils.approx(a.y, b.y, tolerance)
            && Utils.approx(a.x, b.x, tolerance);
    }

    /**
     * Tests to see if a vector has, approximately, the specified magnitude.
     *
     * @param a the input vector
     * @param b the magnitude
     * @return the evaluation
     * @see Vec3#approxMag(Vec3, float, float)
     */
    public static boolean approxMag(final Vec3 a, final float b) {

        return Vec3.approxMag(a, b, Utils.EPSILON);
    }

    /**
     * Tests to see if a vector has, approximately, the specified magnitude.
     *
     * @param a         the input vector
     * @param b         the magnitude
     * @param tolerance the tolerance
     * @return the evaluation
     * @see Utils#approx(float, float, float)
     * @see Vec3#magSq(Vec3)
     */
    public static boolean approxMag(final Vec3 a, final float b, final float tolerance) {

        return Utils.approx(Vec3.magSq(a), b * b, tolerance);
    }

    /**
     * Tests to see if two vectors are parallel. Does so by evaluating whether
     * the cross product of the two approximate zero.
     *
     * @param a the left comparisand
     * @param b the right comparisand
     * @return the evaluation
     * @see Vec3#areParallel(Vec3, Vec3, float)
     */
    public static boolean areParallel(final Vec3 a, final Vec3 b) {

        return Vec3.areParallel(a, b, Utils.EPSILON);
    }

    /**
     * Tests to see if two vectors are parallel. Does so by evaluating whether
     * the cross product of the two approximates zero.
     *
     * @param a         the left comparisand
     * @param b         the right comparisand
     * @param tolerance the tolerance
     * @return the evaluation
     * @see Utils#abs(float)
     */
    public static boolean areParallel(final Vec3 a, final Vec3 b, final float tolerance) {

        return Utils.abs(a.y * b.z - a.z * b.y) <= tolerance
            && Utils.abs(a.z * b.x - a.x * b.z) <= tolerance
            && Utils.abs(a.x * b.y - a.y * b.x) <= tolerance;
    }

    /**
     * Finds the vector's azimuth.
     *
     * @param v the input vector
     * @return the angle in radians
     */
    public static float azimuth(final Vec3 v) {

        return Vec3.azimuthSigned(v);
    }

    /**
     * Finds the vector's azimuth in the range [-pi, pi] .
     *
     * @param v the input vector
     * @return the angle in radians
     * @see Utils#atan2(float, float)
     */
    public static float azimuthSigned(final Vec3 v) {

        return Utils.atan2(v.y, v.x);
    }

    /**
     * Finds the vector's azimuth in the range [0.0, tau] .
     *
     * @param v the input vector
     * @return the angle in radians
     * @see Vec3#azimuthSigned(Vec3)
     */
    public static float azimuthUnsigned(final Vec3 v) {

        final float a = Vec3.azimuthSigned(v);
        return a < -0.0f ? a + Utils.TAU : a;
    }

    /**
     * Returns to a vector with a negative value on the y-axis,
     * (0.0, -1.0, 0.0).
     *
     * @param target the output vector
     * @return the back vector
     */
    public static Vec3 back(final Vec3 target) {

        return target.set(0.0f, -1.0f, 0.0f);
    }

    /**
     * Returns a point on a Bézier curve described by two anchor points and two
     * control points according to a step in [0.0, 1.0]. When the step is less
     * than zero, returns the first anchor point. When the step is greater than
     * one, returns the second anchor point.
     *
     * @param ap0    the first anchor point
     * @param cp0    the first control point
     * @param cp1    the second control point
     * @param ap1    the second anchor point
     * @param step   the step
     * @param target the output vector
     * @return the point along the curve
     */
    public static Vec3 bezierPoint(
        final Vec3 ap0,
        final Vec3 cp0,
        final Vec3 cp1,
        final Vec3 ap1,
        final float step,
        final Vec3 target) {

        if (step <= 0.0f) {
            return target.set(ap0);
        }
        if (step >= 1.0f) {
            return target.set(ap1);
        }

        final float u = 1.0f - step;
        float tcb = step * step;
        float ucb = u * u;
        final float usq3t = ucb * (step + step + step);
        final float tsq3u = tcb * (u + u + u);
        ucb *= u;
        tcb *= step;

        return target.set(
            ap0.x * ucb + cp0.x * usq3t + cp1.x * tsq3u + ap1.x * tcb,
            ap0.y * ucb + cp0.y * usq3t + cp1.y * tsq3u + ap1.y * tcb,
            ap0.z * ucb + cp0.z * usq3t + cp1.z * tsq3u + ap1.z * tcb);
    }

    /**
     * Returns a tangent on a Bézier curve described by two anchor points and
     * two control points according to a step in [0.0, 1.0] . When the step is
     * less than zero, returns the first anchor point subtracted from the first
     * control point. When the step is greater than one, returns the second
     * anchor point subtracted from the second control point.
     *
     * @param ap0    the first anchor point
     * @param cp0    the first control point
     * @param cp1    the second control point
     * @param ap1    the second anchor point
     * @param step   the step
     * @param target the output vector
     * @return the tangent along the curve
     * @see Vec3#sub(Vec3, Vec3, Vec3)
     */
    public static Vec3 bezierTangent(
        final Vec3 ap0,
        final Vec3 cp0,
        final Vec3 cp1,
        final Vec3 ap1,
        final float step,
        final Vec3 target) {

        if (step <= 0.0f) {
            return Vec3.sub(cp0, ap0, target);
        }
        if (step >= 1.0f) {
            return Vec3.sub(ap1, cp1, target);
        }

        final float u = 1.0f - step;
        final float t3 = step + step + step;
        final float usq3 = u * (u + u + u);
        final float tsq3 = step * t3;
        final float ut6 = u * (t3 + t3);

        return target.set(
            (cp0.x - ap0.x) * usq3 + (cp1.x - cp0.x) * ut6 + (ap1.x - cp1.x) * tsq3,
            (cp0.y - ap0.y) * usq3 + (cp1.y - cp0.y) * ut6 + (ap1.y - cp1.y) * tsq3,
            (cp0.z - ap0.z) * usq3 + (cp1.z - cp0.z) * ut6 + (ap1.z - cp1.z) * tsq3);
    }

    /**
     * Returns a normalized tangent on a Bézier curve.
     *
     * @param ap0    the first anchor point
     * @param cp0    the first control point
     * @param cp1    the second control point
     * @param ap1    the second anchor point
     * @param step   the step
     * @param target the output vector
     * @return the tangent along the curve
     * @see Vec3#bezierTangent(Vec3, Vec3, Vec3, Vec3, float, Vec3)
     * @see Utils#invHypot(float, float, float)
     */
    public static Vec3 bezierTanUnit(
        final Vec3 ap0,
        final Vec3 cp0,
        final Vec3 cp1,
        final Vec3 ap1,
        final float step,
        final Vec3 target) {

        Vec3.bezierTangent(ap0, cp0, cp1, ap1, step, target);
        final float mInv = Utils.invHypot(target.x, target.y, target.z);
        return target.set(target.x * mInv, target.y * mInv, target.z * mInv);
    }

    /**
     * Appends to an array of bytes, ordered from least to most significant
     * digit (little endian). Writes 12 bytes.
     *
     * @param v   the vector
     * @param arr the array
     * @param i   the index
     * @return the byte array
     * @see Utils#byteslm(float, byte[], int)
     */
    public static byte[] byteslm(final Vec3 v, final byte[] arr, final int i) {

        Utils.byteslm(v.x, arr, i);
        Utils.byteslm(v.y, arr, i + 4);
        Utils.byteslm(v.z, arr, i + 8);

        return arr;
    }

    /**
     * Appends to an array of bytes, ordered from most to least significant
     * digit (big endian). Writes 12 bytes.
     *
     * @param v   the vector
     * @param arr the array
     * @param i   the index
     * @return the byte array
     * @see Utils#bytesml(float, byte[], int)
     */
    public static byte[] bytesml(final Vec3 v, final byte[] arr, final int i) {

        Utils.bytesml(v.x, arr, i);
        Utils.bytesml(v.y, arr, i + 4);
        Utils.bytesml(v.z, arr, i + 8);

        return arr;
    }

    /**
     * Raises each component of the vector to the nearest greater integer.
     *
     * @param a      the input vector
     * @param target the output vector
     * @return the raised vector
     * @see Utils#ceil(float)
     */
    public static Vec3 ceil(final Vec3 a, final Vec3 target) {

        return target.set(Utils.ceil(a.x), Utils.ceil(a.y), Utils.ceil(a.z));
    }

    /**
     * Concatenates two one-dimensional Vec3 arrays.
     *
     * @param a the first array
     * @param b the second array
     * @return the concatenated array.
     * @see System#arraycopy(Object, int, Object, int, int)
     */
    public static Vec3[] concat(final Vec3[] a, final Vec3[] b) {

        final boolean aNull = a == null;
        final boolean bNull = b == null;

        if (aNull && bNull) {
            return new Vec3[]{};
        }

        if (aNull) {
            final Vec3[] result0 = new Vec3[b.length];
            System.arraycopy(b, 0, result0, 0, b.length);
            return result0;
        }

        if (bNull) {
            final Vec3[] result1 = new Vec3[a.length];
            System.arraycopy(a, 0, result1, 0, a.length);
            return result1;
        }

        final int aLen = a.length;
        final int bLen = b.length;
        final Vec3[] result = new Vec3[aLen + bLen];
        System.arraycopy(a, 0, result, 0, aLen);
        System.arraycopy(b, 0, result, aLen, bLen);
        return result;
    }

    /**
     * Finds first vector argument with the sign of the second vector argument.
     *
     * @param magnitude the magnitude
     * @param sign      the sign
     * @param target    the output vector
     * @return the signed vector
     * @see Utils#copySign(float, float)
     */
    public static Vec3 copySign(final Vec3 magnitude, final Vec3 sign, final Vec3 target) {

        return target.set(
            Utils.copySign(magnitude.x, sign.x),
            Utils.copySign(magnitude.y, sign.y),
            Utils.copySign(magnitude.z, sign.z));
    }

    /**
     * The cross product returns a vector perpendicular to both <em>a</em> and
     * <em>b</em>, and therefore normal to the plane on which <em>a</em> and
     * <em>b</em> rest. The cross product is anti-commutative, meaning
     * <em>a</em> x <em>b</em> = - ( <em>b</em> x <em>a</em> ) . A unit vector
     * does not necessarily result from the cross of two unit vectors. The 3D
     * equivalent to {@link Vec2#perpendicular(Vec2, Vec2)}.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the cross product
     */
    public static Vec3 cross(final Vec3 a, final Vec3 b, final Vec3 target) {

        return target.set(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
    }

    /**
     * A specialized form of the cross product which normalizes the result.
     * This is to facilitate the creation of lookAt matrices. Crossed
     * orthonormal vectors are as follows:
     *
     * <ul>
     * <li>right x forward = up, <br>
     * ( 1.0, 0.0, 0.0 ) x ( 0.0, 1.0, 0.0 ) = ( 0.0, 0.0, 1.0 )</li>
     * <li>forward x up = right, <br>
     * ( 0.0, 1.0, 0.0 ) x ( 0.0, 0.0, 1.0 ) = ( 1.0, 0.0, 0.0 )</li>
     * <li>up x right = forward, <br>
     * ( 0.0, 0.0, 1.0 ) x ( 1.0, 0.0, 0.0 ) = ( 0.0, 1.0, 0.0 )</li>
     * </ul>
     * <p>
     * The cross product is anti-commutative, meaning <em>a</em> x <em>b</em> =
     * - ( <em>b</em> x <em>a</em> ) .
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the normalized cross product
     * @see Utils#invSqrtUnchecked(float)
     */
    public static Vec3 crossNorm(final Vec3 a, final Vec3 b, final Vec3 target) {

        final float x = a.y * b.z - a.z * b.y;
        final float y = a.z * b.x - a.x * b.z;
        final float z = a.x * b.y - a.y * b.x;
        final float mInv = Utils.invSqrtUnchecked(x * x + y * y + z * z);
        return target.set(x * mInv, y * mInv, z * mInv);
    }

    /**
     * A specialized form of the cross product which normalizes the result. The
     * cross product is disclosed as an output.
     *
     * @param a       left operand
     * @param b       right operand
     * @param target  the output vector
     * @param crossed the cross product
     * @return the normalized cross product
     * @see Vec3#cross(Vec3, Vec3, Vec3)
     * @see Vec3#normalize(Vec3, Vec3)
     */
    public static Vec3 crossNorm(final Vec3 a, final Vec3 b, final Vec3 target, final Vec3 crossed) {

        Vec3.cross(a, b, crossed);
        return Vec3.normalize(crossed, target);
    }

    /**
     * Finds the absolute value of the difference between two vectors.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the absolute difference
     * @see Utils#diff(float, float)
     */
    public static Vec3 diff(final Vec3 a, final Vec3 b, final Vec3 target) {

        return target.set(Utils.diff(a.x, b.x), Utils.diff(a.y, b.y), Utils.diff(a.z, b.z));
    }

    /**
     * Finds the Euclidean distance between two vectors.
     *
     * @param a left operand
     * @param b right operand
     * @return the distance
     * @see Vec3#distEuclidean(Vec3, Vec3)
     */
    public static float dist(final Vec3 a, final Vec3 b) {

        return Vec3.distEuclidean(a, b);
    }

    /**
     * Finds the Chebyshev distance between two vectors. Forms a cube pattern
     * when plotted.
     *
     * @param a left operand
     * @param b right operand
     * @return the distance
     * @see Utils#diff(float, float)
     */
    public static float distChebyshev(final Vec3 a, final Vec3 b) {

        return Utils.max(Utils.diff(a.x, b.x), Utils.diff(a.y, b.y), Utils.diff(a.z, b.z));
    }

    /**
     * Finds the Euclidean distance between two vectors. Where possible, use
     * distance squared to avoid the computational cost of the square-root.
     *
     * @param a left operand
     * @param b right operand
     * @return the Euclidean distance
     * @see Vec3#distSq(Vec3, Vec3)
     */
    public static float distEuclidean(final Vec3 a, final Vec3 b) {

        return Utils.sqrtUnchecked(Vec3.distSq(a, b));
    }

    /**
     * Finds the Manhattan distance between two vectors. Forms an octahedron
     * pattern when plotted.
     *
     * @param a left operand
     * @param b right operand
     * @return the Manhattan distance
     * @see Utils#diff(float, float)
     */
    public static float distManhattan(final Vec3 a, final Vec3 b) {

        return Utils.diff(a.x, b.x) + Utils.diff(a.y, b.y) + Utils.diff(a.z, b.z);
    }

    /**
     * Finds the Minkowski distance between two vectors. This is a
     * generalization of other distance formulae. When the exponent value, c,
     * is 1.0, the Minkowski distance equals the Manhattan distance; when it is
     * 2.0, Minkowski equals the Euclidean distance.
     *
     * @param a left operand
     * @param b right operand
     * @param c exponent
     * @return the Minkowski distance
     * @see Math#pow(double, double)
     * @see Math#abs(double)
     */
    public static float distMinkowski(final Vec3 a, final Vec3 b, final float c) {

        if (c != 0.0f) {
            return (float)
                Math.pow(
                    Math.pow(Math.abs((b.x - a.x)), c)
                        + Math.pow(Math.abs((b.y - a.y)), c)
                        + Math.pow(Math.abs((b.z - a.z)), c),
                    1.0d / c);
        }

        return 0.0f;
    }

    /**
     * Finds the Euclidean distance squared between two vectors. Equivalent to
     * subtracting one vector from the other, then finding the dot product of
     * the difference with itself.
     *
     * @param a left operand
     * @param b right operand
     * @return the distance squared
     */
    public static float distSq(final Vec3 a, final Vec3 b) {

        final float xDist = b.x - a.x;
        final float yDist = b.y - a.y;
        final float zDist = b.z - a.z;
        return xDist * xDist + yDist * yDist + zDist * zDist;
    }

    /**
     * Divides a scalar by a vector.
     *
     * @param a      scalar, numerator
     * @param b      vector, denominator
     * @param target the output vector
     * @return the quotient
     * @see Utils#div(float, float)
     */
    public static Vec3 div(final float a, final Vec3 b, final Vec3 target) {

        return target.set(
            Utils.div(a, b.x),
            Utils.div(a, b.y),
            Utils.div(a, b.z));
    }

    /**
     * Divides a vector by a scalar.
     *
     * @param a      vector, numerator
     * @param b      scalar, denominator
     * @param target the output vector
     * @return the quotient
     */
    public static Vec3 div(final Vec3 a, final float b, final Vec3 target) {

        if (b != 0.0f) {
            final float denom = 1.0f / b;
            return target.set(a.x * denom, a.y * denom, a.z * denom);
        }
        return target.reset();
    }

    /**
     * Divides the left operand by the right, component-wise. Serves as a
     * shortcut for transforming a vector by the inverse of a scalar matrix.
     *
     * @param a      numerator
     * @param b      denominator
     * @param target the output vector
     * @return the quotient
     * @see Utils#div(float, float)
     */
    public static Vec3 div(final Vec3 a, final Vec3 b, final Vec3 target) {

        return target.set(
            Utils.div(a.x, b.x),
            Utils.div(a.y, b.y),
            Utils.div(a.z, b.z));
    }

    /**
     * Finds the dot product of two vectors by summing the products of their
     * corresponding components. The dot product of a vector with itself is
     * equal to its magnitude squared.
     *
     * @param a left operand
     * @param b right operand
     * @return the dot product
     */
    public static float dot(final Vec3 a, final Vec3 b) {

        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    /**
     * Returns to a vector with a negative value on the z axis,
     * (0.0, 0.0, -1.0).
     *
     * @param target the output vector
     * @return the down vector
     */
    public static Vec3 down(final Vec3 target) {

        return target.set(0.0f, 0.0f, -1.0f);
    }

    /**
     * Returns a vector with all components set to epsilon, a small positive
     * non-zero value.
     *
     * @param target the output vector
     * @return epsilon
     */
    public static Vec3 epsilon(final Vec3 target) {

        return target.set(Utils.EPSILON, Utils.EPSILON, Utils.EPSILON);
    }

    /**
     * Filters a vector by setting each component to the input component if it
     * is in bounds and 0.0 if it is out of bounds.
     *
     * @param v      the vector
     * @param lb     the lower bound
     * @param ub     the upper bound
     * @param target the output vector
     * @return the filtered vector
     * @see Utils#filter(float, float, float)
     */
    public static Vec3 filter(final Vec3 v, final Vec3 lb, final Vec3 ub, final Vec3 target) {

        return target.set(
            Utils.filter(v.x, lb.x, ub.x),
            Utils.filter(v.y, lb.y, ub.y),
            Utils.filter(v.z, lb.z, ub.z));
    }

    /**
     * Flattens a two-dimensional array of vectors to a one dimensional array.
     *
     * @param arr the 2D array
     * @return the 1D array
     * @see System#arraycopy(Object, int, Object, int, int)
     */
    public static Vec3[] flat(final Vec3[][] arr) {

        int totalLen = 0;
        for (final Vec3[] vec3s : arr) {
            totalLen += vec3s.length;
        }

        /*
         * Copy each inner array to the result array, then move the cursor by
         * the length of each array.
         */
        int j = 0;
        final Vec3[] result = new Vec3[totalLen];
        for (final Vec3[] arrInner : arr) {
            final int len = arrInner.length;
            System.arraycopy(arrInner, 0, result, j, len);
            j += len;
        }
        return result;
    }

    /**
     * Flattens a three-dimensional array of vectors to a one dimensional
     * array.
     *
     * @param arr the 3D array
     * @return the 1D array
     * @see System#arraycopy(Object, int, Object, int, int)
     */
    public static Vec3[] flat(final Vec3[][][] arr) {

        int totalLen = 0;
        for (final Vec3[][] arrInner : arr) {
            for (final Vec3[] vec3s : arrInner) {
                totalLen += vec3s.length;
            }
        }

        int k = 0;
        final Vec3[] result = new Vec3[totalLen];
        for (final Vec3[][] arrInner1 : arr) {
            for (final Vec3[] arrInner2 : arrInner1) {
                final int sourceLen2 = arrInner2.length;
                System.arraycopy(arrInner2, 0, result, k, sourceLen2);
                k += sourceLen2;
            }
        }

        return result;
    }

    /**
     * Floors each component of the vector.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the floored vector
     * @see Utils#floor(float)
     */
    public static Vec3 floor(final Vec3 v, final Vec3 target) {

        return target.set(Utils.floor(v.x), Utils.floor(v.y), Utils.floor(v.z));
    }

    /**
     * Applies the % operator (truncation-based modulo) to the left operand.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the result
     * @see Utils#fmod(float, float)
     */
    public static Vec3 fmod(final float a, final Vec3 b, final Vec3 target) {

        return target.set(
            Utils.fmod(a, b.x),
            Utils.fmod(a, b.y),
            Utils.fmod(a, b.z));
    }

    /**
     * Applies the % operator (truncation-based modulo) to each component of
     * the left operand.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the result
     */
    public static Vec3 fmod(final Vec3 a, final float b, final Vec3 target) {

        if (b != 0.0f) {
            return target.set(a.x % b, a.y % b, a.z % b);
        }
        return target.set(a);
    }

    /**
     * Applies the % operator (truncation-based modulo) to each component of
     * the left operand.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the result
     * @see Utils#fmod(float, float)
     */
    public static Vec3 fmod(final Vec3 a, final Vec3 b, final Vec3 target) {

        return target.set(
            Utils.fmod(a.x, b.x),
            Utils.fmod(a.y, b.y),
            Utils.fmod(a.z, b.z));
    }

    /**
     * Returns to a vector with a positive value on the y-axis,
     * (0.0, 1.0, 0.0).
     *
     * @param target the output vector
     * @return the forward vector
     */
    public static Vec3 forward(final Vec3 target) {

        return target.set(0.0f, 1.0f, 0.0f);
    }

    /**
     * Returns the fractional portion of the vector's components.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the fractional portion
     * @see Utils#fract(float)
     */
    public static Vec3 fract(final Vec3 v, final Vec3 target) {

        return target.set(
            Utils.fract(v.x),
            Utils.fract(v.y),
            Utils.fract(v.z));
    }

    /**
     * Creates a vector from the cosine and sine of an azimuth and inclination.
     *
     * @param cosAzim the cosine of azimuth
     * @param sinAzim the sine of azimuth
     * @param cosIncl the cosine of inclination
     * @param sinIncl the sine of inclination
     * @param radius  the radius
     * @param target  the output vector
     * @return the vector
     */
    public static Vec3 fromSpherical(
        final float cosAzim,
        final float sinAzim,
        final float cosIncl,
        final float sinIncl,
        final float radius,
        final Vec3 target) {

        final float rhoCosIncl = radius * cosIncl;
        return target.set(
            rhoCosIncl * cosAzim,
            rhoCosIncl * sinAzim,
            radius * sinIncl);
    }

    /**
     * Creates a vector from spherical coordinates: (1) theta, the azimuth,
     * yaw or longitude; (2) phi, the inclination, pitch or latitude; (3) rho,
     * the radius or magnitude. The poles will be upright in a z-up coordinate
     * system; sideways in a y-up coordinate system.
     *
     * @param azimuth     the angle theta in radians
     * @param inclination the angle phi in radians
     * @param radius      rho, the vector's magnitude
     * @param target      the output vector
     * @return the vector
     */
    public static Vec3 fromSpherical(
        final float azimuth,
        final float inclination,
        final float radius,
        final Vec3 target) {

        final float inclNorm = inclination * Utils.ONE_TAU;
        final float azNorm = azimuth * Utils.ONE_TAU;

        return Vec3.fromSpherical(
            Utils.scNorm(azNorm),
            Utils.scNorm(azNorm - 0.25f),
            Utils.scNorm(inclNorm),
            Utils.scNorm(inclNorm - 0.25f),
            radius,
            target);
    }

    /**
     * Creates a vector with a magnitude of 1.0 from an azimuth and
     * inclination, such that the vector is on the unit sphere.
     *
     * @param azimuth     the azimuth in radians
     * @param inclination the inclination in radians
     * @param target      the output vector
     * @return the vector
     * @see Vec3#fromSpherical(float, float, float, Vec3)
     */
    public static Vec3 fromSpherical(
        final float azimuth,
        final float inclination,
        final Vec3 target) {

        return Vec3.fromSpherical(azimuth, inclination, 1.0f, target);
    }

    /**
     * Generates a 3D array of vectors. Defaults to the coordinate range of
     * [-0.5, 0.5].
     *
     * @param res the resolution
     * @return the array
     * @see Vec3#grid(int, int, int)
     */
    public static Vec3[][][] grid(final int res) {

        return Vec3.grid(res, res, res);
    }

    /**
     * Generates a 3D array of vectors. The result is in layer-row-major order,
     * but the parameters are supplied in reverse: columns first, then rows,
     * then layers. Defaults to the coordinate range of [-0.5, 0.5].
     *
     * @param cols   number of columns
     * @param rows   number of rows
     * @param layers number of layers
     * @return the array
     * @see Vec3#grid(int, int, int, float, float, float, float, float, float)
     */
    public static Vec3[][][] grid(final int cols, final int rows, final int layers) {

        return Vec3.grid(
            cols, rows, layers,
            -0.5f, -0.5f, -0.5f,
            0.5f, 0.5f, 0.5f);
    }

    /**
     * Generates a 3D array of vectors. The result is in layer-row-major order,
     * but the parameters are supplied in reverse: columns first, then rows,
     * then layers.
     *
     * @param cols       number of columns
     * @param rows       number of rows
     * @param layers     number of layers
     * @param lowerBound the lower bound
     * @param upperBound the upper bound
     * @return the array
     * @see Vec3#grid(int, int, int, float, float, float, float, float, float)
     */
    public static Vec3[][][] grid(
        final int cols,
        final int rows,
        final int layers,
        final float lowerBound,
        final float upperBound) {

        return Vec3.grid(
            cols, rows, layers,
            lowerBound, lowerBound, lowerBound,
            upperBound, upperBound, upperBound);
    }

    /**
     * Generates a 3D array of vectors. The result is in layer-row-major order,
     * but the parameters are supplied in reverse: columns first, then rows,
     * then layers.
     *
     * @param cols       number of columns
     * @param rows       number of rows
     * @param layers     number of layers
     * @param lowerBound the lower bound
     * @param upperBound the upper bound
     * @return the array
     * @see Vec3#grid(int, int, int, float, float, float, float, float, float)
     */
    public static Vec3[][][] grid(
        final int cols,
        final int rows,
        final int layers,
        final Vec3 lowerBound,
        final Vec3 upperBound) {

        return Vec3.grid(
            cols, rows, layers,
            lowerBound.x, lowerBound.y, lowerBound.z,
            upperBound.x, upperBound.y, upperBound.z);
    }

    /**
     * Generates a 1D array of vectors placed on the surface of a sphere,
     * distributed according to the golden ratio {@link Utils#PHI}.
     *
     * @param count  number of vectors
     * @param radius radius of sphere
     * @return the array
     * @see Utils#asin(float)
     */
    public static Vec3[] gridFibonacci(final int count, final float radius) {

        // TODO: Redo on account of spherical coordinate system?

        final int vcount = Math.max(count, 3);
        final float vrad = Math.max(Utils.EPSILON, radius);

        final Vec3[] result = new Vec3[vcount];
        final float toStep = 2.0f / vcount;
        for (int i = 0; i < vcount; ++i) {

            /*
             * A few calculations can be saved by using normalized angle
             * instead of an angle in [0.0, TAU] .
             */
            final float azNorm = Utils.PHI * (float) i;
            final float inclNorm = Utils.ONE_TAU * Utils.asin(1.0f - (float) i * toStep);
            final float rhoCosPhi = vrad * Utils.scNorm(inclNorm);

            /* Convert from spherical coordinates to Cartesian. */
            result[i] = new Vec3(
                rhoCosPhi * Utils.scNorm(azNorm),
                rhoCosPhi * Utils.scNorm(azNorm - 0.25f),
                vrad * -Utils.scNorm(inclNorm - 0.25f));
        }

        return result;
    }

    /**
     * Generates a 3D array of vectors arranged on a UV Sphere. The array is
     * ordered by layers, latitudes, then longitudes; the parameters are
     * supplied in reverse order.
     *
     * @param longitudes the longitudes, azimuths
     * @param latitudes  the latitudes, inclinations
     * @return the array
     * @see Vec3#gridSpherical(int, int, boolean)
     */
    public static Vec3[][][] gridSpherical(final int longitudes, final int latitudes) {

        return Vec3.gridSpherical(longitudes, latitudes, true);
    }

    /**
     * Generates a 3D array of vectors arranged on a UV Sphere. The array is
     * ordered by layers, latitudes, then longitudes; the parameters are
     * supplied in reverse order.
     *
     * @param longitudes   the longitudes, azimuths
     * @param latitudes    the latitudes, inclinations
     * @param includePoles include the poles
     * @return the array
     * @see Vec3#gridSpherical(int, int, int, float, float, boolean)
     */
    public static Vec3[][][] gridSpherical(
        final int longitudes, final int latitudes, final boolean includePoles) {

        return Vec3.gridSpherical(
            longitudes, latitudes, 1,
            0.5f, 0.5f, includePoles);
    }

    /**
     * Generates a 3D array of vectors arranged on a UV Sphere. The array is
     * ordered by layers, latitudes, then longitudes; the parameters are
     * supplied in reverse order.
     *
     * @param longitudes the longitudes, azimuths
     * @param latitudes  the latitudes, inclinations
     * @param layers     the layers, radii
     * @param radiusMin  minimum radius
     * @param radiusMax  maximum radius
     * @return the array
     * @see Vec3#gridSpherical(int, int, int, float, float, boolean)
     */
    public static Vec3[][][] gridSpherical(
        final int longitudes,
        final int latitudes,
        final int layers,
        final float radiusMin,
        final float radiusMax) {

        return Vec3.gridSpherical(
            longitudes, latitudes, layers,
            radiusMin, radiusMax, true);
    }

    /**
     * Generates a 3D array of vectors arranged on a UV Sphere. The array is
     * ordered by layers, latitudes, then longitudes; the parameters are
     * supplied in reverse order.
     *
     * @param longitudes   the longitudes, azimuths
     * @param latitudes    the latitudes, inclinations
     * @param layers       the layers, radii
     * @param radiusMin    minimum radius
     * @param radiusMax    maximum radius
     * @param includePoles include the poles
     * @return the array
     */
    public static Vec3[][][] gridSpherical(
        final int longitudes,
        final int latitudes,
        final int layers,
        final float radiusMin,
        final float radiusMax,
        final boolean includePoles) {

        final int vLons = Math.max(longitudes, 3);
        final int vLats = Math.max(latitudes, 3);
        final int vLayers = Math.max(layers, 1);

        final boolean oneLayer = vLayers == 1;
        final float vrMax = Utils.max(Utils.EPSILON, radiusMin, radiusMax);
        final float vrMin = oneLayer
            ? vrMax
            : Math.max(Utils.EPSILON, Math.min(radiusMin, radiusMax));

        final int latLen = includePoles ? vLats + 2 : vLats;
        final int latOff = includePoles ? 1 : 0;
        final Vec3[][][] result = new Vec3[vLayers][latLen][vLons];

        final float toPrc = oneLayer ? 1.0f : 1.0f / (vLayers - 1.0f);
        final float toIncl = 0.5f / (vLats + 1.0f);
        final float toAzim = 1.0f / vLons;

        final int len2 = vLats * vLons;
        final int len3 = vLayers * len2;
        for (int k = 0; k < len3; ++k) {
            final int h = k / len2;
            final int m = k - h * len2;
            final int i = m / vLons;
            final int j = m % vLons;

            final float prc = h * toPrc;
            final float radius = (1.0f - prc) * vrMin + prc * vrMax;

            final float incl = 0.25f - (i + 1.0f) * toIncl;
            final float rhoCosIncl = radius * Utils.scNorm(incl);
            final float rhoSinIncl = radius * Utils.scNorm(incl - 0.25f);

            final float azim = j * toAzim;
            final float cosAzim = Utils.scNorm(azim);
            final float sinAzim = Utils.scNorm(azim - 0.25f);

            result[h][latOff + i][j] = new Vec3(
                rhoCosIncl * cosAzim,
                rhoCosIncl * sinAzim,
                -rhoSinIncl);
        }

        /* Add single element arrays to beginning and end of layers. */
        if (includePoles) {
            for (int h = 0; h < vLayers; ++h) {
                final float prc = h * toPrc;
                final float radius = (1.0f - prc) * vrMin + prc * vrMax;
                final Vec3[][] layer = result[h];
                layer[0] = new Vec3[]{new Vec3(0.0f, 0.0f, -radius)};
                layer[latLen - 1] = new Vec3[]{new Vec3(0.0f, 0.0f, radius)};
            }
        }

        return result;
    }

    /**
     * Evaluates whether all components of the left comparisand are greater
     * than those of the right comparisand.
     *
     * @param a left comparisand
     * @param b right comparisand
     * @return the evaluation
     */
    public static boolean gt(final Vec3 a, final Vec3 b) {

        return a.x > b.x && a.y > b.y && a.z > b.z;
    }

    /**
     * Evaluates whether the left comparisand is greater than the right
     * comparisand.
     *
     * @param a      left comparisand
     * @param b      right comparisand
     * @param target the output vector
     * @return the evaluation
     */
    public static Vec3 gt(final Vec3 a, final Vec3 b, final Vec3 target) {

        return target.set(a.x > b.x, a.y > b.y, a.z > b.z);
    }

    /**
     * Evaluates whether all components of the left comparisand are greater
     * than or equal to those of the right comparisand.
     *
     * @param a left comparisand
     * @param b right comparisand
     * @return the evaluation
     */
    public static boolean gtEq(final Vec3 a, final Vec3 b) {

        return a.x >= b.x && a.y >= b.y && a.z >= b.z;
    }

    /**
     * Evaluates whether the left comparisand is greater than or equal to the
     * right comparisand.
     *
     * @param a      left comparisand
     * @param b      right comparisand
     * @param target the output vector
     * @return the evaluation
     */
    public static Vec3 gtEq(final Vec3 a, final Vec3 b, final Vec3 target) {

        return target.set(a.x >= b.x, a.y >= b.y, a.z >= b.z);
    }

    /**
     * Multiplies two vectors, component-wise.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the product
     */
    public static Vec3 hadamard(final Vec3 a, final Vec3 b, final Vec3 target) {

        return target.set(a.x * b.x, a.y * b.y, a.z * b.z);
    }

    /**
     * Finds the vector's inclination.
     *
     * @param v the input vector
     * @return the inclination
     */
    public static float inclination(final Vec3 v) {

        return Vec3.inclinationSigned(v);
    }

    /**
     * Finds the vector's inclination in the range [-pi / 2.0, pi / 2.0] .
     * It is necessary to calculate the vector's magnitude in order to find its
     * inclination.
     *
     * @param v the input vector
     * @return the inclination
     */
    public static float inclinationSigned(final Vec3 v) {

        return Utils.HALF_PI - Vec3.inclinationUnsigned(v);
    }

    /**
     * Finds the vector's inclination in the range [pi, 0.0].
     *
     * @param v the input vector
     * @return the inclination
     */
    public static float inclinationUnsigned(final Vec3 v) {

        return Utils.acos(v.z * Utils.invHypot(v.x, v.y, v.z));
    }

    /**
     * Inserts an array of vectors in the midst of another. The insertion point
     * is before, or to the left of, the existing element at a given index.
     *
     * @param arr    the array
     * @param index  the insertion index
     * @param insert the inserted array
     * @return the new array
     * @see Utils#mod(int, int)
     * @see System#arraycopy(Object, int, Object, int, int)
     */
    public static Vec3[] insert(final Vec3[] arr, final int index, final Vec3[] insert) {

        final int alen = arr.length;
        final int blen = insert.length;
        final int valIdx = Utils.mod(index, alen + 1);

        final Vec3[] result = new Vec3[alen + blen];
        System.arraycopy(arr, 0, result, 0, valIdx);
        System.arraycopy(insert, 0, result, valIdx, blen);
        System.arraycopy(arr, valIdx, result, valIdx + blen, alen - valIdx);

        return result;
    }

    /**
     * Tests to see if the vector is on the unit sphere, i.e., has a magnitude
     * of approximately 1.0.
     *
     * @param v the input vector
     * @return the evaluation
     * @see Utils#approx(float, float)
     * @see Vec3#magSq(Vec3)
     */
    public static boolean isUnit(final Vec3 v) {

        return Utils.approx(Vec3.magSq(v), 1.0f);
    }

    /**
     * Returns a vector with a negative value on the x-axis, (-1.0, 0.0, 0.0).
     *
     * @param target the output vector
     * @return the left vector
     */
    public static Vec3 left(final Vec3 target) {

        return target.set(-1.0f, 0.0f, 0.0f);
    }

    /**
     * Limits a vector's magnitude to a scalar. Does nothing if the vector is
     * beneath the limit.
     *
     * @param v      the input vector
     * @param limit  the limit
     * @param target the output vector
     * @return the limited vector
     * @see Utils#invSqrtUnchecked(float)
     */
    public static Vec3 limit(final Vec3 v, final float limit, final Vec3 target) {

        final float mSq = v.x * v.x + v.y * v.y + v.z * v.z;
        if (limit > 0.0f && mSq > limit * limit) {
            final float scalar = limit * Utils.invSqrtUnchecked(mSq);
            return target.set(v.x * scalar, v.y * scalar, v.z * scalar);
        }

        return target.set(v);
    }

    /**
     * Evaluates whether all components of the left comparisand are less than
     * those of the right comparisand.
     *
     * @param a left comparisand
     * @param b right comparisand
     * @return the evaluation
     */
    public static boolean lt(final Vec3 a, final Vec3 b) {

        return a.x < b.x && a.y < b.y && a.z < b.z;
    }

    /**
     * Evaluates whether the left comparisand is less than the right
     * comparisand.
     *
     * @param a      left comparisand
     * @param b      right comparisand
     * @param target the output vector
     * @return the evaluation
     */
    public static Vec3 lt(final Vec3 a, final Vec3 b, final Vec3 target) {

        return target.set(a.x < b.x, a.y < b.y, a.z < b.z);
    }

    /**
     * Evaluates whether all components of the left comparisand are less than
     * or equal to those of the right comparisand.
     *
     * @param a left comparisand
     * @param b right comparisand
     * @return the evaluation
     */
    public static boolean ltEq(final Vec3 a, final Vec3 b) {

        return a.x <= b.x && a.y <= b.y && a.z <= b.z;
    }

    /**
     * Evaluates whether the left comparisand is less than or equal to the
     * right comparisand.
     *
     * @param a      left comparisand
     * @param b      right comparisand
     * @param target the output vector
     * @return the evaluation
     */
    public static Vec3 ltEq(final Vec3 a, final Vec3 b, final Vec3 target) {

        return target.set(a.x <= b.x, a.y <= b.y, a.z <= b.z);
    }

    /**
     * Finds the length, or magnitude, of a vector, |<em>a</em>| . Also
     * referred to as the radius when using spherical coordinates. Uses the
     * formula √ <em>a</em> . <em>a</em> . Where possible, use magSq or dot to
     * avoid the computational cost of the square-root.
     *
     * @param v the input vector
     * @return the magnitude
     * @see Utils#sqrtUnchecked(float)
     */
    public static float mag(final Vec3 v) {

        return Utils.sqrtUnchecked(v.x * v.x + v.y * v.y + v.z * v.z);
    }

    /**
     * Finds the length-, or magnitude-, squared of a vector,
     * |<em>a</em>|<sup>2</sup>. Returns the same result as <em>a</em> .
     * <em>a</em> . Useful when calculating the lengths of many vectors, to
     * avoid the computational cost of the square-root.
     *
     * @param v the input vector
     * @return the magnitude squared
     */
    public static float magSq(final Vec3 v) {

        return v.x * v.x + v.y * v.y + v.z * v.z;
    }

    /**
     * Maps an input vector from an original range to a target range.
     *
     * @param v      the input vector
     * @param lbOrig lower bound of original range
     * @param ubOrig upper bound of original range
     * @param lbDest lower bound of destination range
     * @param ubDest upper bound of destination range
     * @param target the output vector
     * @return the mapped value
     * @see Utils#map(float, float, float, float, float)
     */
    public static Vec3 map(
        final Vec3 v,
        final Vec3 lbOrig,
        final Vec3 ubOrig,
        final Vec3 lbDest,
        final Vec3 ubDest,
        final Vec3 target) {

        return target.set(
            Utils.map(v.x, lbOrig.x, ubOrig.x, lbDest.x, ubDest.x),
            Utils.map(v.y, lbOrig.y, ubOrig.y, lbDest.y, ubDest.y),
            Utils.map(v.z, lbOrig.z, ubOrig.z, lbDest.z, ubDest.z));
    }

    /**
     * Sets the output vector to the maximum of the input vector and a lower
     * bound.
     *
     * @param a          the input value
     * @param lowerBound the lower bound
     * @param target     the output vector
     * @return the maximum values
     */
    public static Vec3 max(final Vec3 a, final float lowerBound, final Vec3 target) {

        return target.set(
            Math.max(a.x, lowerBound),
            Math.max(a.y, lowerBound),
            Math.max(a.z, lowerBound));
    }

    /**
     * Sets the output vector to the maximum components of the input vector and
     * a lower bound.
     *
     * @param a          the input vector
     * @param lowerBound the lower bound
     * @param target     the output vector
     * @return the maximum values
     */
    public static Vec3 max(final Vec3 a, final Vec3 lowerBound, final Vec3 target) {

        return target.set(
            Math.max(a.x, lowerBound.x),
            Math.max(a.y, lowerBound.y),
            Math.max(a.z, lowerBound.z));
    }

    /**
     * Sets the output vector to the minimum components of the input vector and
     * an upper bound.
     *
     * @param a          the input value
     * @param upperBound the upper bound
     * @param target     the output vector
     * @return the minimum values
     */
    public static Vec3 min(final Vec3 a, final float upperBound, final Vec3 target) {

        return target.set(
            Math.min(a.x, upperBound),
            Math.min(a.y, upperBound),
            Math.min(a.z, upperBound));
    }

    /**
     * Sets the output vector to the minimum components of the input vector and
     * an upper bound.
     *
     * @param a          the input vector
     * @param upperBound the upper bound
     * @param target     the output vector
     * @return the minimal values
     */
    public static Vec3 min(final Vec3 a, final Vec3 upperBound, final Vec3 target) {

        return target.set(
            Math.min(a.x, upperBound.x),
            Math.min(a.y, upperBound.y),
            Math.min(a.z, upperBound.z));
    }

    /**
     * Mixes two vectors together by a step in [0.0, 1.0].
     *
     * @param orig   the original vector
     * @param dest   the destination vector
     * @param step   the step
     * @param target the output vector
     * @return the mix
     */
    public static Vec3 mix(
        final Vec3 orig,
        final Vec3 dest,
        final float step,
        final Vec3 target) {

        if (step <= 0.0f) {
            return target.set(orig);
        }
        if (step >= 1.0f) {
            return target.set(dest);
        }

        final float u = 1.0f - step;
        return target.set(
            u * orig.x + step * dest.x,
            u * orig.y + step * dest.y,
            u * orig.z + step * dest.z);
    }

    /**
     * Wraps a scalar by each component of a vector.
     *
     * @param a      the scalar
     * @param b      the vector
     * @param target the output vector
     * @return the modulated vector
     * @see Utils#mod(float, float)
     */
    public static Vec3 mod(final float a, final Vec3 b, final Vec3 target) {

        return target.set(
            Utils.mod(a, b.x),
            Utils.mod(a, b.y),
            Utils.mod(a, b.z));
    }

    /**
     * Wraps each component of a vector by a scalar
     *
     * @param a      the vector
     * @param b      the scalar
     * @param target the output vector
     * @return the modulated vector
     * @see Utils#modUnchecked(float, float)
     */
    public static Vec3 mod(final Vec3 a, final float b, final Vec3 target) {

        if (b != 0.0f) {
            return target.set(
                Utils.modUnchecked(a.x, b),
                Utils.modUnchecked(a.y, b),
                Utils.modUnchecked(a.z, b));
        }
        return target.set(a);
    }

    /**
     * Wraps each component of the left vector by those of the right.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the modulated vector
     * @see Utils#mod(float, float)
     */
    public static Vec3 mod(final Vec3 a, final Vec3 b, final Vec3 target) {

        return target.set(
            Utils.mod(a.x, b.x),
            Utils.mod(a.y, b.y),
            Utils.mod(a.z, b.z));
    }

    /**
     * A specialized form of modulo which subtracts the floor of the vector
     * from the vector.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the result
     * @see Utils#mod1(float)
     */
    public static Vec3 mod1(final Vec3 v, final Vec3 target) {

        return target.set(
            Utils.mod1(v.x),
            Utils.mod1(v.y),
            Utils.mod1(v.z));
    }

    /**
     * Multiplies a vector by a scalar.
     *
     * @param a      left operand, the scalar
     * @param b      right operand, the vector
     * @param target the output vector
     * @return the product
     */
    public static Vec3 mul(final float a, final Vec3 b, final Vec3 target) {

        return target.set(a * b.x, a * b.y, a * b.z);
    }

    /**
     * Multiplies a vector by a scalar.
     *
     * @param a      left operand, the vector
     * @param b      right operand, the scalar
     * @param target the output vector
     * @return the product
     */
    public static Vec3 mul(final Vec3 a, final float b, final Vec3 target) {

        return target.set(a.x * b, a.y * b, a.z * b);
    }

    /**
     * Negates the input vector.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the negation
     */
    public static Vec3 negate(final Vec3 v, final Vec3 target) {

        return target.set(-v.x, -v.y, -v.z);
    }

    /**
     * Returns a vector with all components set to negative one.
     *
     * @param target the output vector
     * @return negative one
     */
    public static Vec3 negOne(final Vec3 target) {

        return target.set(-1.0f, -1.0f, -1.0f);
    }

    /**
     * Tests to see if all the vector's components are zero. Useful when
     * safeguarding against invalid directions.
     *
     * @param v the input vector
     * @return the evaluation
     */
    public static boolean none(final Vec3 v) {

        return v.z == 0.0f && v.y == 0.0f && v.x == 0.0f;
    }

    /**
     * Divides a vector by its magnitude, such that the new magnitude is 1.0.
     * <em>â</em> = <em>a</em> / |<em>a</em>|. The result is a unit vector, as
     * it lies on the unit sphere.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the unit vector
     * @see Utils#invSqrt(float)
     */
    public static Vec3 normalize(final Vec3 v, final Vec3 target) {

        final float mInv = Utils.invSqrt(v.x * v.x + v.y * v.y + v.z * v.z);
        return target.set(v.x * mInv, v.y * mInv, v.z * mInv);
    }

    /**
     * Evaluates a vector like a boolean, where n != 0.0 is true.
     *
     * @param v      the vector
     * @param target the output vector
     * @return the truth table opposite
     */
    public static Vec3 not(final Vec3 v, final Vec3 target) {

        return target.set(
            v.x != 0.0f ? 0.0f : 1.0f,
            v.y != 0.0f ? 0.0f : 1.0f,
            v.z != 0.0f ? 0.0f : 1.0f);
    }

    /**
     * Returns a vector with all components set to one.
     *
     * @param target the output vector
     * @return one
     */
    public static Vec3 one(final Vec3 target) {

        return target.set(1.0f, 1.0f, 1.0f);
    }

    /**
     * Evaluates two vectors like booleans, using the OR logic gate.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the evaluation
     * @see Utils#or(float, float)
     */
    public static Vec3 or(final Vec3 a, final Vec3 b, final Vec3 target) {

        return target.set(
            Utils.or(a.x, b.x),
            Utils.or(a.y, b.y),
            Utils.or(a.z, b.z));
    }

    /**
     * Oscillates between an origin and destination vector based on an input
     * step and a pause factor. When the pause is greater than 1.0, the value
     * will be clamped to the pole.
     *
     * @param orig   the original vector
     * @param dest   the destination vector
     * @param step   the step
     * @param pause  the pause factor
     * @param target the output vector
     * @return the oscillation
     */
    public static Vec3 pingPong(
        final Vec3 orig,
        final Vec3 dest,
        final float step,
        final float pause,
        final Vec3 target) {

        final float t = 0.5f + 0.5f * pause * Utils.scNorm(step - 0.5f);
        if (t <= 0.0f) {
            return target.set(orig);
        }
        if (t >= 1.0f) {
            return target.set(dest);
        }
        final float u = 1.0f - t;
        return target.set(
            u * orig.x + t * dest.x,
            u * orig.y + t * dest.y,
            u * orig.z + t * dest.z);
    }

    /**
     * Oscillates between an origin and destination vector based on an input
     * step.
     *
     * @param orig   the original vector
     * @param dest   the destination vector
     * @param step   the step
     * @param target the output vector
     * @return the oscillation
     */
    public static Vec3 pingPong(
        final Vec3 orig,
        final Vec3 dest,
        final float step,
        final Vec3 target) {

        return Vec3.pingPong(orig, dest, step, 1.0f, target);
    }

    /**
     * Projects one vector onto another.
     *
     * @param a left operand
     * @param b right operand
     * @return the projection
     * @see Vec3#projectScalar(Vec3, Vec3)
     */
    public static float project(final Vec3 a, final Vec3 b) {

        return Vec3.projectScalar(a, b);
    }

    /**
     * Projects one vector onto another.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the projection
     * @see Vec3#projectVector(Vec3, Vec3, Vec3)
     */
    public static Vec3 project(final Vec3 a, final Vec3 b, final Vec3 target) {

        return Vec3.projectVector(a, b, target);
    }

    /**
     * Returns the scalar projection of <em>a</em> onto <em>b</em>. Defined as
     * <br>
     * <br>
     * project ( <em>a</em>, <em>b</em> ) := <em>a</em> . <em>b</em> / <em>b</em> .
     * <em>b</em>
     * <br>
     * <br>
     * If the square magnitude of <em>b</em> is zero, then returns zero.
     *
     * @param a left operand
     * @param b right operand
     * @return the scalar projection
     */
    public static float projectScalar(final Vec3 a, final Vec3 b) {

        final float bSq = b.x * b.x + b.y * b.y + b.z * b.z;
        if (bSq > 0.0f) {
            return (a.x * b.x + a.y * b.y + a.z * b.z) / bSq;
        }
        return 0.0f;
    }

    /**
     * Projects one vector onto another. Defined as
     * <br>
     * <br>
     * project ( <em>a</em>, <em>b</em> ) := <em>b</em> ( <em>a</em> . <em>b</em> /
     * <em>b</em> .
     * <em>b</em> )
     * <br>
     * <br>
     * Returns a zero vector if the right operand, <em>b</em>, has zero
     * magnitude.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the projection
     * @see Vec3#projectScalar(Vec3, Vec3)
     * @see Vec3#mul(Vec3, float, Vec3)
     */
    public static Vec3 projectVector(final Vec3 a, final Vec3 b, final Vec3 target) {

        return Vec3.mul(b, Vec3.projectScalar(a, b), target);
    }

    /**
     * Reduces the signal, or granularity, of a vector's components. A level of
     * zero will copy the input vector to the target.
     *
     * @param v      the input vector
     * @param levels the levels
     * @param target the output vector
     * @return the quantized vector
     * @see Utils#quantizeSigned(float, float, float)
     */
    public static Vec3 quantize(final Vec3 v, final int levels, final Vec3 target) {

        if (levels == 0) {
            return target.set(v);
        }
        final float levf = levels < 0 ? -levels : levels;
        final float delta = 1.0f / levf;
        return target.set(
            Utils.quantizeSigned(v.x, levf, delta),
            Utils.quantizeSigned(v.y, levf, delta),
            Utils.quantizeSigned(v.z, levf, delta));
    }

    /**
     * Creates a vector with a magnitude of 1.0 at a random azimuth and
     * inclination, such that it lies on the unit sphere.
     *
     * @param rng    the random number generator
     * @param target the output vector
     * @return the random vector
     */
    public static Vec3 random(final Random rng, final Vec3 target) {

        return Vec3.randomSpherical(rng, target);
    }

    /**
     * Creates a random point in the Cartesian coordinate system given a lower
     * and an upper bound.
     *
     * @param rng        the random number generator
     * @param lowerBound the lower bound
     * @param upperBound the upper bound
     * @param target     the output vector
     * @return the random vector
     */
    public static Vec3 randomCartesian(
        final Random rng,
        final float lowerBound,
        final float upperBound,
        final Vec3 target) {

        final float rx = rng.nextFloat();
        final float ry = rng.nextFloat();
        final float rz = rng.nextFloat();
        return target.set(
            (1.0f - rx) * lowerBound + rx * upperBound,
            (1.0f - ry) * lowerBound + ry * upperBound,
            (1.0f - rz) * lowerBound + rz * upperBound);
    }

    /**
     * Creates a random point in the Cartesian coordinate system given a lower
     * and an upper bound.
     *
     * @param rng        the random number generator
     * @param lowerBound the lower bound
     * @param upperBound the upper bound
     * @param target     the output vector
     * @return the random vector
     */
    public static Vec3 randomCartesian(
        final Random rng,
        final Vec3 lowerBound,
        final Vec3 upperBound,
        final Vec3 target) {

        final float rx = rng.nextFloat();
        final float ry = rng.nextFloat();
        final float rz = rng.nextFloat();
        return target.set(
            (1.0f - rx) * lowerBound.x + rx * upperBound.x,
            (1.0f - ry) * lowerBound.y + ry * upperBound.y,
            (1.0f - rz) * lowerBound.z + rz * upperBound.z);
    }

    /**
     * Creates a random vector that lies on a sphere. Uses 3 random numbers
     * with normal distribution, then rescales to the sphere radius.
     *
     * @param rng    the random number generator
     * @param radius the sphere radius
     * @param target the output vector
     * @return the random vector
     * @see Random#nextGaussian()
     */
    public static Vec3 randomSpherical(
        final Random rng,
        final float radius,
        final Vec3 target) {

        final double x = rng.nextGaussian();
        final double y = rng.nextGaussian();
        final double z = rng.nextGaussian();
        final double sqMag = x * x + y * y + z * z;
        if (sqMag != 0.0d) {
            final double invMag = radius / Math.sqrt(sqMag);
            return target.set((float) (x * invMag), (float) (y * invMag), (float) (z * invMag));
        }
        return target.reset();
    }

    /**
     * Creates a random vector that lies on the unit sphere.
     *
     * @param rng    the random number generator
     * @param target the output vector
     * @return the random vector
     * @see Vec3#randomSpherical(Random, float, Vec3)
     */
    public static Vec3 randomSpherical(final Random rng, final Vec3 target) {

        return Vec3.randomSpherical(rng, 1.0f, target);
    }

    /**
     * Reflects an incident vector off a normal vector. Uses the formula
     * <br>
     * <br>
     * <em>i</em> - 2.0 (<em>n</em> . <em>i</em>) <em>n</em>
     *
     * @param incident the incident vector
     * @param normal   the normal vector
     * @param target   the output vector
     * @return the reflected vector
     * @see Vec3#magSq(Vec3)
     * @see Utils#invSqrtUnchecked(float)
     */
    public static Vec3 reflect(final Vec3 incident, final Vec3 normal, final Vec3 target) {

        final float nMSq = Vec3.magSq(normal);
        if (nMSq < Utils.EPSILON) {
            return target.reset();
        }

        final float mInv = Utils.invSqrtUnchecked(nMSq);
        final float nx = normal.x * mInv;
        final float ny = normal.y * mInv;
        final float nz = normal.z * mInv;
        final float scalar = 2.0f * (nx * incident.x + ny * incident.y + nz * incident.z);
        return target.set(
            incident.x - scalar * nx,
            incident.y - scalar * ny,
            incident.z - scalar * nz);
    }

    /**
     * Refracts a vector through a volume using Snell's law.
     *
     * @param incident the incident vector
     * @param normal   the normal vector
     * @param eta      ratio of refraction indices
     * @param target   the output vector
     * @return the refraction
     * @see Vec3#dot(Vec3, Vec3)
     * @see Utils#sqrtUnchecked(float)
     */
    public static Vec3 refract(
        final Vec3 incident,
        final Vec3 normal,
        final float eta,
        final Vec3 target) {

        final float nDotI = Vec3.dot(normal, incident);
        final float k = 1.0f - eta * eta * (1.0f - nDotI * nDotI);
        if (k <= 0.0f) {
            return target.reset();
        }
        final float scalar = eta * nDotI + Utils.sqrtUnchecked(k);
        return target.set(
            eta * incident.x - normal.x * scalar,
            eta * incident.y - normal.y * scalar,
            eta * incident.z - normal.z * scalar);
    }

    /**
     * Finds the rejection of <em>b</em> from <em>a</em>. Defined as
     * <br>
     * <br>
     * reject ( <em>a</em> , <em>b</em> ) := a - proj ( <em>a</em>, <em>b</em> )
     * <br>
     * <br>
     * the subtraction of the projection of <em>a</em> onto <em>b</em> from
     * <em>a</em>. Emits the projection as an output.
     *
     * @param a          left operand
     * @param b          right operand
     * @param target     the output vector
     * @param projection the projection
     * @return the rejection
     * @see Vec3#sub(Vec3, Vec3, Vec3)
     * @see Vec3#projectVector(Vec3, Vec3, Vec3)
     */
    public static Vec3 reject(final Vec3 a, final Vec3 b, final Vec3 target, final Vec3 projection) {

        return Vec3.sub(a, Vec3.projectVector(a, b, projection), target);
    }

    /**
     * Normalizes a vector, then multiplies it by a scalar, in effect setting
     * its magnitude to that scalar.
     *
     * @param v      the vector
     * @param scalar the scalar
     * @param target the output vector
     * @return the rescaled vector
     * @see Utils#invSqrtUnchecked(float)
     */
    public static Vec3 rescale(final Vec3 v, final float scalar, final Vec3 target) {

        final float mSq = v.x * v.x + v.y * v.y + v.z * v.z;
        if (scalar != 0.0f && mSq > 0.0f) {
            final float sclMg = scalar * Utils.invSqrtUnchecked(mSq);
            return target.set(v.x * sclMg, v.y * sclMg, v.z * sclMg);
        }
        return target.reset();
    }

    /**
     * Normalizes a vector, then multiplies it by a scalar, in effect setting
     * its magnitude to that scalar.
     *
     * @param v          the vector
     * @param scalar     the scalar
     * @param target     the output vector
     * @param normalized the normalized vector
     * @return the rescaled vector
     * @see Vec3#normalize(Vec3, Vec3)
     * @see Vec3#mul(Vec3, float, Vec3)
     */
    public static Vec3 rescale(
        final Vec3 v,
        final float scalar,
        final Vec3 target,
        final Vec3 normalized) {

        if (scalar != 0.0f) {
            Vec3.normalize(v, normalized);
            return Vec3.mul(normalized, scalar, target);
        }
        normalized.reset();
        return target.reset();
    }

    /**
     * Resizes an array of vectors to a requested length. If the new length is
     * greater than the current length, the new elements are filled with new
     * vectors.
     * <br>
     * <br>
     * This does <em>not</em> use {@link System#arraycopy(Object, int, Object, int, int)}
     * because this function iterates through the entire array checking for
     * null entries.
     *
     * @param arr the array
     * @param sz  the new size
     * @return the array
     */
    public static Vec3[] resize(final Vec3[] arr, final int sz) {

        if (sz < 1) {
            return new Vec3[]{};
        }
        final Vec3[] result = new Vec3[sz];

        if (arr == null) {
            for (int i = 0; i < sz; ++i) {
                result[i] = new Vec3();
            }
            return result;
        }

        final int last = arr.length - 1;
        for (int i = 0; i < sz; ++i) {
            if (i > last || arr[i] == null) {
                result[i] = new Vec3();
            } else {
                result[i] = arr[i];
            }
        }

        return result;
    }

    /**
     * Returns to a vector with a positive value on the x-axis,
     * (1.0, 0.0, 0.0).
     *
     * @param target the output vector
     * @return the right vector
     */
    public static Vec3 right(final Vec3 target) {

        return target.set(1.0f, 0.0f, 0.0f);
    }

    /**
     * Rotates a vector around an axis by an angle in radians. The axis is
     * assumed to be of unit length. Accepts calculated sine and cosine of an
     * angle, so that collections of vectors can be efficiently rotated without
     * repeatedly calling cos and sin.
     *
     * @param v      the vector to rotate
     * @param cosa   cosine of the angle
     * @param sina   sine of the angle
     * @param axis   the axis of rotation
     * @param target the output vector
     * @return the rotated vector
     */
    public static Vec3 rotate(
        final Vec3 v,
        final float cosa, final float sina,
        final Vec3 axis,
        final Vec3 target) {

        final float complcos = 1.0f - cosa;
        final float complxy = complcos * axis.x * axis.y;
        final float complxz = complcos * axis.x * axis.z;
        final float complyz = complcos * axis.y * axis.z;

        final float sinx = sina * axis.x;
        final float siny = sina * axis.y;
        final float sinz = sina * axis.z;

        return target.set(
            (complcos * axis.x * axis.x + cosa) * v.x + (complxy - sinz) * v.y + (complxz + siny) * v.z,
            (complxy + sinz) * v.x + (complcos * axis.y * axis.y + cosa) * v.y + (complyz - sinx) * v.z,
            (complxz - siny) * v.x
                + (complyz + sinx) * v.y
                + (complcos * axis.z * axis.z + cosa) * v.z);
    }

    /**
     * Rotates a vector around an axis by an angle in radians. The axis is
     * assumed to be of unit length.
     *
     * @param v       the vector to rotate
     * @param radians the angle in radians
     * @param axis    the axis of rotation
     * @param target  the output vector
     * @return the rotated vector
     * @see Vec3#rotate(Vec3, float, float, Vec3, Vec3)
     */
    public static Vec3 rotate(final Vec3 v, final float radians, final Vec3 axis, final Vec3 target) {

        final float n = radians * Utils.ONE_TAU;
        final float c = Utils.scNorm(n);
        final float s = Utils.scNorm(n - 0.25f);

        return Vec3.rotate(v, c, s, axis, target);
    }

    /**
     * Rotates a vector around the x-axis. Accepts calculated sine and cosine
     * of an angle, so that collections of vectors can be efficiently rotated
     * without repeatedly calling cos and sin.
     *
     * @param v      the input vector
     * @param cosa   cosine of the angle
     * @param sina   sine of the angle
     * @param target the output vector
     * @return the rotated vector
     */
    public static Vec3 rotateX(final Vec3 v, final float cosa, final float sina, final Vec3 target) {

        return target.set(v.x, cosa * v.y - sina * v.z, cosa * v.z + sina * v.y);
    }

    /**
     * Rotates a vector around the x-axis by an angle in radians. Do not use
     * sequences of orthonormal rotations by Euler angles; this will result in
     * gimbal lock. Instead, rotate by an angle around an axis or create a
     * quaternion.
     *
     * @param v       the input vector
     * @param radians the angle in radians
     * @param target  the output vector
     * @return the rotated vector
     * @see Vec3#rotate(Vec3, float, Vec3, Vec3)
     */
    public static Vec3 rotateX(final Vec3 v, final float radians, final Vec3 target) {

        final float n = radians * Utils.ONE_TAU;
        final float c = Utils.scNorm(n);
        final float s = Utils.scNorm(n - 0.25f);

        return Vec3.rotateX(v, c, s, target);
    }

    /**
     * Rotates a vector around the y-axis. Accepts pre-calculated sine and
     * cosine of an angle, so that collections of vectors can be efficiently
     * rotated without repeatedly calling cos and sin.
     *
     * @param v      the input vector
     * @param cosa   cosine of the angle
     * @param sina   sine of the angle
     * @param target the output vector
     * @return the rotated vector
     */
    public static Vec3 rotateY(final Vec3 v, final float cosa, final float sina, final Vec3 target) {

        return target.set(cosa * v.x + sina * v.z, v.y, cosa * v.z - sina * v.x);
    }

    /**
     * Rotates a vector around the y-axis by an angle in radians. Do not use
     * sequences of orthonormal rotations by Euler angles; this will result in
     * gimbal lock. Instead, rotate by an angle around an axis or create a
     * quaternion.
     *
     * @param v       the input vector
     * @param radians the angle in radians
     * @param target  the output vector
     * @return the rotated vector
     * @see Vec3#rotate(Vec3, float, Vec3, Vec3)
     */
    public static Vec3 rotateY(final Vec3 v, final float radians, final Vec3 target) {

        final float n = radians * Utils.ONE_TAU;
        final float c = Utils.scNorm(n);
        final float s = Utils.scNorm(n - 0.25f);

        return Vec3.rotateY(v, c, s, target);
    }

    /**
     * Rotates a vector around the z axis. Accepts pre-calculated sine and
     * cosine of an angle, so that collections of vectors can be efficiently
     * rotated without repeatedly calling cos and sin.
     *
     * @param v      the input vector
     * @param cosa   cosine of the angle
     * @param sina   sine of the angle
     * @param target the output vector
     * @return the rotated vector
     */
    public static Vec3 rotateZ(final Vec3 v, final float cosa, final float sina, final Vec3 target) {

        return target.set(cosa * v.x - sina * v.y, cosa * v.y + sina * v.x, v.z);
    }

    /**
     * Rotates a vector around the z axis by an angle in radians. Do not use
     * sequences of orthonormal rotations by Euler angles; this will result in
     * gimbal lock. Instead, rotate by an angle around an axis or create a
     * quaternion.
     *
     * @param v       the input vector
     * @param radians the angle in radians
     * @param target  the output vector
     * @return the rotated vector
     * @see Vec3#rotate(Vec3, float, Vec3, Vec3)
     */
    public static Vec3 rotateZ(final Vec3 v, final float radians, final Vec3 target) {

        final float n = radians * Utils.ONE_TAU;
        final float c = Utils.scNorm(n);
        final float s = Utils.scNorm(n - 0.25f);

        return Vec3.rotateZ(v, c, s, target);
    }

    /**
     * Rounds each component of the vector to the nearest whole number.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the rounded vector
     * @see Utils#round(float)
     */
    public static Vec3 round(final Vec3 v, final Vec3 target) {

        return target.set(Utils.round(v.x), Utils.round(v.y), Utils.round(v.z));
    }

    /**
     * Finds the sign of the vector: -1, if negative; 1, if positive.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the sign
     */
    public static Vec3 sign(final Vec3 v, final Vec3 target) {

        return target.set(
            v.x < -0.0f ? -1.0f : v.x > 0.0f ? 1.0f : 0.0f,
            v.y < -0.0f ? -1.0f : v.y > 0.0f ? 1.0f : 0.0f,
            v.z < -0.0f ? -1.0f : v.z > 0.0f ? 1.0f : 0.0f);
    }

    /**
     * Subtracts the right vector from the left vector.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the difference
     */
    public static Vec3 sub(final Vec3 a, final Vec3 b, final Vec3 target) {

        return target.set(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    /**
     * Subtracts the right from the left vector and then normalizes the
     * difference.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the normalized difference
     * @see Utils#invSqrtUnchecked(float)
     */
    public static Vec3 subNorm(final Vec3 a, final Vec3 b, final Vec3 target) {

        final float dx = a.x - b.x;
        final float dy = a.y - b.y;
        final float dz = a.z - b.z;
        final float mInv = Utils.invSqrtUnchecked(dx * dx + dy * dy + dz * dz);
        return target.set(dx * mInv, dy * mInv, dz * mInv);
    }

    /**
     * Subtracts the right from the left vector and then normalizes the
     * difference.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @param dir    the direction
     * @return the normalized difference
     * @see Vec3#sub(Vec3, Vec3, Vec3)
     * @see Vec3#normalize(Vec3, Vec3)
     */
    public static Vec3 subNorm(final Vec3 a, final Vec3 b, final Vec3 target, final Vec3 dir) {

        Vec3.sub(a, b, dir);
        return Vec3.normalize(dir, target);
    }

    /**
     * Appends a string representation of an array of vectors to a
     * {@link StringBuilder}.
     *
     * @param sb     the string builder
     * @param arr    the array
     * @param places the print precision
     * @return the string builder
     */
    public static StringBuilder toString(final StringBuilder sb, final Vec3[] arr, final int places) {

        /* Caches array element to a variable in case of null check. */

        sb.append('[');
        if (arr != null) {
            final int len = arr.length;
            final int last = len - 1;
            for (int i = 0; i < last; ++i) {
                final Vec3 v = arr[i];
                v.toString(sb, places);
                sb.append(',');
            }
            final Vec3 vl = arr[last];
            vl.toString(sb, places);
        }
        sb.append(']');

        return sb;
    }

    /**
     * Returns a string representation of an array of vectors.
     *
     * @param arr the array
     * @return the string
     */
    public static String toString(final Vec3[] arr) {

        return Vec3.toString(arr, Utils.FIXED_PRINT);
    }

    /**
     * Returns a string representation of an array of vectors.
     *
     * @param arr    the array
     * @param places the print precision
     * @return the string
     */
    public static String toString(final Vec3[] arr, final int places) {

        return Vec3.toString(new StringBuilder(1024), arr, places).toString();
    }

    /**
     * Truncates each component of the vector.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the truncation
     */
    public static Vec3 trunc(final Vec3 v, final Vec3 target) {

        return target.set((int) v.x, (int) v.y, (int) v.z);
    }

    /**
     * Returns to a vector with a positive value on the z-axis,
     * (0.0, 0.0, 1.0).
     *
     * @param target the output vector
     * @return the up vector
     */
    public static Vec3 up(final Vec3 target) {

        return target.set(0.0f, 0.0f, 1.0f);
    }

    /**
     * Wraps a vector around a periodic range as defined by an upper and lower
     * bound.
     *
     * @param v      the vector
     * @param lb     the lower bound
     * @param ub     the upper bound
     * @param target the output vector
     * @return the wrapped vector
     * @see Utils#wrap(float, float, float)
     */
    public static Vec3 wrap(final Vec3 v, final Vec3 lb, final Vec3 ub, final Vec3 target) {

        return target.set(
            Utils.wrap(v.x, lb.x, ub.x),
            Utils.wrap(v.y, lb.y, ub.y),
            Utils.wrap(v.z, lb.z, ub.z));
    }

    /**
     * Evaluates two vectors like booleans, using the exclusive or (XOR) logic
     * gate.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the evaluation
     * @see Utils#xor(float, float)
     */
    public static Vec3 xor(final Vec3 a, final Vec3 b, final Vec3 target) {

        return target.set(
            Utils.xor(a.x, b.x),
            Utils.xor(a.y, b.y),
            Utils.xor(a.z, b.z));
    }

    /**
     * Returns a vector with all components set to zero.
     *
     * @param target the output vector
     * @return the zero vector
     */
    public static Vec3 zero(final Vec3 target) {

        return target.set(0.0f, 0.0f, 0.0f);
    }

    /**
     * Generates a 3D array of vectors. The result is in layer-row-major order,
     * but the parameters are supplied in reverse: columns first, then rows,
     * then layers.
     * <br>
     * <br>
     * This is separated to make overriding the public grid functions easier.
     * This is protected because it is too easy for integers to be quietly
     * promoted to floats if the signature parameters are confused.
     *
     * @param cols   number of columns
     * @param rows   number of rows
     * @param layers number of layers
     * @param lbx    lower bound x
     * @param lby    lower bound y
     * @param lbz    lower bound z
     * @param ubx    upper bound x
     * @param uby    upper bound y
     * @param ubz    upper bound z
     * @return the array
     */
    protected static Vec3[][][] grid(
        final int cols, final int rows, final int layers,
        final float lbx, final float lby, final float lbz,
        final float ubx, final float uby, final float ubz) {

        final int lVrf = Math.max(layers, 1);
        final int rVrf = Math.max(rows, 1);
        final int cVrf = Math.max(cols, 1);

        final Vec3[][][] result = new Vec3[lVrf][rVrf][cVrf];

        final boolean lOne = lVrf == 1;
        final boolean rOne = rVrf == 1;
        final boolean cOne = cVrf == 1;

        final float hToStep = lOne ? 0.0f : 1.0f / (lVrf - 1.0f);
        final float iToStep = rOne ? 0.0f : 1.0f / (rVrf - 1.0f);
        final float jToStep = cOne ? 0.0f : 1.0f / (cVrf - 1.0f);

        final float hOff = lOne ? 0.5f : 0.0f;
        final float iOff = rOne ? 0.5f : 0.0f;
        final float jOff = cOne ? 0.5f : 0.0f;

        final int rcVal = rVrf * cVrf;
        final int len = lVrf * rcVal;
        for (int k = 0; k < len; ++k) {
            final int h = k / rcVal;
            final int m = k - h * rcVal;
            final int i = m / cVrf;
            final int j = m % cVrf;

            final float hStep = h * hToStep + hOff;
            final float iStep = i * iToStep + iOff;
            final float jStep = j * jToStep + jOff;

            result[h][i][j] = new Vec3(
                (1.0f - jStep) * lbx + jStep * ubx,
                (1.0f - iStep) * lby + iStep * uby,
                (1.0f - hStep) * lbz + hStep * ubz);
        }

        return result;
    }

    /**
     * Returns -1 when this vector is less than the comparisand; 1 when it is
     * greater than; 0 when the two are 'equal'. The implementation of this
     * method allows collections of vectors to be sorted.
     *
     * @param v the comparisand
     * @return the numeric code
     */
    @Override
    public int compareTo(final Vec3 v) {

        return this.z < v.z ? -1
            : this.z > v.z ? 1
            : this.y < v.y ? -1
            : this.y > v.y ? 1
            : this.x < v.x ? -1
            : this.x > v.x ? 1
            : 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        return this.equals((Vec3) o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y, this.z);
    }

    /**
     * Resets this vector to an initial state, ( 0.0, 0.0, 0.0 ) .
     *
     * @return this vector
     */
    public Vec3 reset() {
        return this.set(0.0f, 0.0f, 0.0f);
    }

    /**
     * Sets the components of this vector from booleans, where false is 0.0 and
     * true is 1.0 .
     *
     * @param x the x component
     * @param y the y component
     * @param z the z component
     * @return this vector
     */
    public Vec3 set(final boolean x, final boolean y, final boolean z) {

        this.x = x ? 1.0f : 0.0f;
        this.y = y ? 1.0f : 0.0f;
        this.z = z ? 1.0f : 0.0f;
        return this;
    }

    /**
     * Sets the components of this vector.
     *
     * @param x the x component
     * @param y the y component
     * @param z the z component
     * @return this vector
     */
    public Vec3 set(final float x, final float y, final float z) {

        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    /**
     * Promotes a Vec2 to a Vec3.
     *
     * @param v the source vector
     * @return this vector
     */
    public Vec3 set(final Vec2 v) {
        return this.set(v.x, v.y, 0.0f);
    }

    /**
     * Promotes a Vec2 to a Vec3 with an extra component.
     *
     * @param v the source vector
     * @param z the w component
     * @return this vector
     */
    public Vec3 set(final Vec2 v, final float z) {

        return this.set(v.x, v.y, z);
    }

    /**
     * Copies the components of the source vector to this vector.
     *
     * @param v the source vector
     * @return this vector
     */
    public Vec3 set(final Vec3 v) {
        return this.set(v.x, v.y, v.z);
    }

    /**
     * Returns a float array of length 3 containing this vector's components.
     *
     * @return the array
     */
    public float[] toArray() {
        return this.toArray(new float[3], 0);
    }

    /**
     * Puts the vector's components into an existing array at the index
     * provided. The vector's x component is assigned to element
     * <code>i</code>; its y component, to element <code>i + 1</code>; its z
     * component, to element <code>i + 2</code>.
     *
     * @param arr the array
     * @param i   the index
     * @return the array
     */
    public float[] toArray(final float[] arr, final int i) {

        arr[i] = this.x;
        arr[i + 1] = this.y;
        arr[i + 2] = this.z;
        return arr;
    }

    /**
     * Returns a string representation of this vector.
     *
     * @return the string
     */
    @Override
    public String toString() {

        return this.toString(new StringBuilder(96), Utils.FIXED_PRINT).toString();
    }

    /**
     * Returns a string representation of this vector.
     *
     * @param places number of decimal places
     * @return the string
     */
    public String toString(final int places) {

        return this.toString(new StringBuilder(96), places).toString();
    }

    /**
     * An internal helper function to format a vector as a Python tuple, then
     * append it to a {@link StringBuilder}. Used for testing purposes to
     * compare results with Blender 4.x.
     *
     * @param pyCd string builder
     */
    void toBlenderCode(final StringBuilder pyCd) {

        pyCd.append('(');
        Utils.toFixed(pyCd, this.x, 6);
        pyCd.append(',');
        pyCd.append(' ');
        Utils.toFixed(pyCd, this.y, 6);
        pyCd.append(',');
        pyCd.append(' ');
        Utils.toFixed(pyCd, this.z, 6);
        pyCd.append(')');
    }

    /**
     * Internal helper method that appends a representation of this vector in
     * the Wavefront OBJ file format to a {@link StringBuilder}.
     *
     * @param objs the string builder
     */
    void toObjString(final StringBuilder objs) {

        Utils.toFixed(objs, this.x, 6);
        objs.append(' ');
        Utils.toFixed(objs, this.y, 6);
        objs.append(' ');
        Utils.toFixed(objs, this.z, 6);
    }

    /**
     * Internal helper function to assist with methods that need to print many
     * vectors. Appends to an existing {@link StringBuilder}.
     *
     * @param sb     the string builder
     * @param places the number of places
     * @return the string builder
     */
    StringBuilder toString(final StringBuilder sb, final int places) {

        sb.append("{\"x\":");
        Utils.toFixed(sb, this.x, places);
        sb.append(",\"y\":");
        Utils.toFixed(sb, this.y, places);
        sb.append(",\"z\":");
        Utils.toFixed(sb, this.z, places);
        sb.append('}');
        return sb;
    }

    /**
     * Tests equivalence between this and another vector. For rough equivalence
     * of floating point components, use the static approximate function
     * instead.
     *
     * @param v the vector
     * @return the evaluation
     */
    protected boolean equals(final Vec3 v) {

        /* With {@link Float.floatToIntBits(float)}, -0.0f != 0.0f. */
        return this.z == v.z && this.y == v.y && this.x == v.x;
    }

    /**
     * An abstract class that may serve as an umbrella for any custom
     * comparators of Vec3 s.
     */
    public abstract static class AbstrComparator implements Comparator<Vec3> {

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

    /**
     * An abstract class to facilitate the creation of vector easing functions.
     */
    public abstract static class AbstrEasing implements Utils.EasingFuncObj<Vec3> {

        /**
         * The default constructor.
         */
        protected AbstrEasing() {
        }

        /**
         * A clamped interpolation between the origin and destination. Defers
         * to an unclamped interpolation, which is to be defined by subclasses
         * of this class.
         *
         * @param orig   the origin vector
         * @param dest   the destination vector
         * @param step   a factor in [0.0, 1.0]
         * @param target the output vector
         * @return the eased vector
         */
        @Override
        public Vec3 apply(
            final Vec3 orig,
            final Vec3 dest,
            final Float step,
            final Vec3 target) {

            final float tf = step;
            if (Float.isNaN(tf)) {
                return this.applyUnclamped(orig, dest, 0.5f, target);
            }
            if (tf <= 0.0f) {
                return target.set(orig);
            }
            if (tf >= 1.0f) {
                return target.set(dest);
            }
            return this.applyUnclamped(orig, dest, tf, target);
        }

        /**
         * The interpolation to be defined by subclasses.
         *
         * @param orig   the origin vector
         * @param dest   the destination vector
         * @param step   a factor in [0.0, 1.0]
         * @param target the output vector
         * @return the eased vector
         */
        public abstract Vec3 applyUnclamped(
            final Vec3 orig,
            final Vec3 dest,
            final float step,
            final Vec3 target);

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

    /**
     * A linear interpolation functional class.
     */
    public static class Lerp extends AbstrEasing {

        /**
         * The default constructor.
         */
        public Lerp() {
        }

        /**
         * Eases between two vectors by a step using the formula
         * ( 1.0 - <em>t</em> ) <em>a</em> + <em>t</em> <em>b</em>.
         *
         * @param orig   the origin vector
         * @param dest   the destination vector
         * @param step   the step
         * @param target the output vector
         * @return the result
         */
        @Override
        public Vec3 applyUnclamped(
            final Vec3 orig,
            final Vec3 dest,
            final float step,
            final Vec3 target) {

            final float uf = 1.0f - step;
            return target.set(
                uf * orig.x + step * dest.x,
                uf * orig.y + step * dest.y,
                uf * orig.z + step * dest.z);
        }
    }

    /**
     * Eases between two vectors with the smooth step formula:
     * <em>t</em><sup>2</sup> ( 3.0 - 2.0 <em>t</em> ) .
     */
    public static class SmoothStep extends AbstrEasing {

        /**
         * The default constructor.
         */
        public SmoothStep() {
        }

        /**
         * Applies the function.
         *
         * @param orig   the origin vector
         * @param dest   the destination vector
         * @param step   the step in a range 0 to 1
         * @param target the output vector
         * @return the smoothed vector
         */
        @Override
        public Vec3 applyUnclamped(
            final Vec3 orig,
            final Vec3 dest,
            final float step,
            final Vec3 target) {

            final float tf = step * step * (3.0f - (step + step));
            final float uf = 1.0f - tf;
            return target.set(
                uf * orig.x + tf * dest.x,
                uf * orig.y + tf * dest.y,
                uf * orig.z + tf * dest.z);
        }
    }

    /**
     * Compares two vectors against a locus with squared Euclidean distance.
     */
    public static class SortDistSq extends AbstrComparator {

        /**
         * The point against which distances are compared.
         */
        public final Vec3 locus = new Vec3();

        /**
         * The default constructor.
         */
        public SortDistSq() {
        }

        /**
         * Constructs a sorting function with a locus against which points are
         * compared in sorting.
         *
         * @param locus the locus
         */
        public SortDistSq(final Vec3 locus) {

            this.locus.set(locus);
        }

        /**
         * The compare function.
         *
         * @param a the left comparisand
         * @param b the right comparisand
         * @return the comparison
         * @see Vec3#distSq(Vec3, Vec3)
         */
        @Override
        public int compare(final Vec3 a, final Vec3 b) {

            final float ad = Vec3.distSq(this.locus, a);
            final float bd = Vec3.distSq(this.locus, b);
            return ad < bd ? -1 : ad > bd ? 1 : 0;
        }
    }

    /**
     * Compares two vectors on the x-axis.
     */
    public static class SortX extends AbstrComparator {

        /**
         * The default constructor.
         */
        public SortX() {
        }

        /**
         * The compare function.
         *
         * @param a the left comparisand
         * @param b the right comparisand
         * @return the comparison
         */
        @Override
        public int compare(final Vec3 a, final Vec3 b) {

            return a.x < b.x ? -1 : a.x > b.x ? 1 : 0;
        }
    }

    /**
     * Compares two vectors on the y-axis.
     */
    public static class SortY extends AbstrComparator {

        /**
         * The default constructor.
         */
        public SortY() {
        }

        /**
         * The compare function.
         *
         * @param a the left comparisand
         * @param b the right comparisand
         * @return the comparison
         */
        @Override
        public int compare(final Vec3 a, final Vec3 b) {

            return a.y < b.y ? -1 : a.y > b.y ? 1 : 0;
        }
    }

    /**
     * Compares two vectors on the z axis.
     */
    public static class SortZ extends AbstrComparator {

        /**
         * The default constructor.
         */
        public SortZ() {
        }

        /**
         * The compare function.
         *
         * @param a the left comparisand
         * @param b the right comparisand
         * @return the comparison
         */
        @Override
        public int compare(final Vec3 a, final Vec3 b) {

            return a.z < b.z ? -1 : a.z > b.z ? 1 : 0;
        }
    }
}
