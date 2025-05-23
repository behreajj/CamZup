package com.behreajj.camzup.core;

import java.util.Comparator;
import java.util.Objects;
import java.util.Random;

/**
 * A mutable, extensible class influenced by the GLSL, OSL and Processing
 * PVector. This is intended for storing points and directions in
 * two-dimensional graphics programs. Instance methods are limited, while most
 * static methods require an explicit output variable to be provided.
 */
public class Vec2 implements Comparable<Vec2> {

    /**
     * Component on the x-axis in the Cartesian coordinate system.
     */
    public float x = 0.0f;

    /**
     * Component on the y-axis in the Cartesian coordinate system.
     */
    public float y = 0.0f;

    /**
     * The default vector constructor.
     */
    public Vec2() {
    }

    /**
     * Constructs a vector from boolean values.
     *
     * @param x the x component
     * @param y the y component
     */
    public Vec2(final boolean x, final boolean y) {
        this.set(x, y);
    }

    /**
     * Constructs a vector from float values.
     *
     * @param x the x component
     * @param y the y component
     */
    public Vec2(final float x, final float y) {
        this.set(x, y);
    }

    /**
     * Constructs a vector from a source vector's components.
     *
     * @param v the source vector
     */
    public Vec2(final Vec2 v) {
        this.x = v.x;
        this.y = v.y;
    }

    /**
     * Finds the absolute value of each vector component.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the absolute vector
     * @see Utils#abs(float)
     */
    public static Vec2 abs(final Vec2 v, final Vec2 target) {

        return target.set(Utils.abs(v.x), Utils.abs(v.y));
    }

    /**
     * Adds two vectors together.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the sum
     */
    public static Vec2 add(final Vec2 a, final Vec2 b, final Vec2 target) {

        return target.set(a.x + b.x, a.y + b.y);
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
    public static Vec2 addNorm(final Vec2 a, final Vec2 b, final Vec2 target) {

        final float dx = a.x + b.x;
        final float dy = a.y + b.y;
        final float mInv = Utils.invSqrtUnchecked(dx * dx + dy * dy);
        return target.set(dx * mInv, dy * mInv);
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
     * @see Vec2#add(Vec2, Vec2, Vec2)
     * @see Vec2#normalize(Vec2, Vec2)
     */
    public static Vec2 addNorm(final Vec2 a, final Vec2 b, final Vec2 target, final Vec2 sum) {

        Vec2.add(a, b, sum);
        return Vec2.normalize(sum, target);
    }

    /**
     * Tests to see if all the vector's components are non-zero. Useful for
     * finding valid dimensions (width and depth) stored in vectors.
     *
     * @param v the input vector
     * @return the evaluation
     */
    public static boolean all(final Vec2 v) {

        return v.y != 0.0f && v.x != 0.0f;
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
    public static Vec2 and(final Vec2 a, final Vec2 b, final Vec2 target) {

        return target.set(Utils.and(a.x, b.x), Utils.and(a.y, b.y));
    }

    /**
     * Finds the angle between two vectors. Returns zero when either vector is
     * zero.
     *
     * @param a the first vector
     * @param b the second vector
     * @return the angle
     * @see Vec2#any(Vec2)
     */
    public static float angleBetween(final Vec2 a, final Vec2 b) {

        /* To match distAngleUnsigned, this requires double precision. */

        if (Vec2.any(a) && Vec2.any(b)) {
            final double ax = a.x;
            final double ay = a.y;

            final double bx = b.x;
            final double by = b.y;

            return (float) Math.acos(
                (ax * bx + ay * by)
                    / (Math.sqrt(ax * ax + ay * ay)
                    * Math.sqrt(bx * bx + by * by)));
        }
        return 0.0f;
    }

    /**
     * Tests to see if any of the vector's components are non-zero.
     *
     * @param v the input vector
     * @return the evaluation
     */
    public static boolean any(final Vec2 v) {

        return v.y != 0.0f || v.x != 0.0f;
    }

    /**
     * Appends a vector to a one-dimensional vector array. Returns a new array.
     *
     * @param a the array
     * @param b the vector
     * @return a new array
     * @see System#arraycopy(Object, int, Object, int, int)
     */
    public static Vec2[] append(final Vec2[] a, final Vec2 b) {

        final boolean aNull = a == null;
        final boolean bNull = b == null;
        if (aNull && bNull) {
            return new Vec2[]{};
        }
        if (aNull) {
            return new Vec2[]{b};
        }
        if (bNull) {
            final Vec2[] result0 = new Vec2[a.length];
            System.arraycopy(a, 0, result0, 0, a.length);
            return result0;
        }

        final int aLen = a.length;
        final Vec2[] result1 = new Vec2[aLen + 1];
        System.arraycopy(a, 0, result1, 0, aLen);
        result1[aLen] = b;
        return result1;
    }

    /**
     * Tests to see if two vectors approximate each other.
     *
     * @param a left operand
     * @param b right operand
     * @return the evaluation
     * @see Vec2#approx(Vec2, Vec2, float)
     */
    public static boolean approx(final Vec2 a, final Vec2 b) {

        return Vec2.approx(a, b, Utils.EPSILON);
    }

    /**
     * Tests to see if two vectors approximate each other.
     *
     * @param a         left comparisand
     * @param b         right comparisand
     * @param tolerance the tolerance
     * @return the evaluation
     * @see Utils#approx(float, float, float)
     */
    public static boolean approx(final Vec2 a, final Vec2 b, final float tolerance) {

        return a == b || Utils.approx(a.y, b.y, tolerance) && Utils.approx(a.x, b.x, tolerance);
    }

    /**
     * Tests to see if a vector has, approximately, the specified magnitude.
     *
     * @param a the input vector
     * @param b the magnitude
     * @return the evaluation
     * @see Vec2#approxMag(Vec2, float, float)
     */
    public static boolean approxMag(final Vec2 a, final float b) {

        return Vec2.approxMag(a, b, Utils.EPSILON);
    }

    /**
     * Tests to see if a vector has, approximately, the specified magnitude.
     *
     * @param a         the input vector
     * @param b         the magnitude
     * @param tolerance the tolerance
     * @return the evaluation
     * @see Utils#approx(float, float, float)
     * @see Vec2#magSq(Vec2)
     */
    public static boolean approxMag(final Vec2 a, final float b, final float tolerance) {

        return Utils.approx(Vec2.magSq(a), b * b, tolerance);
    }

    /**
     * Tests to see if two vectors are parallel.
     *
     * @param a left comparisand
     * @param b right comparisand
     * @return the evaluation
     * @see Vec2#areParallel(Vec2, Vec2, float)
     */
    public static boolean areParallel(final Vec2 a, final Vec2 b) {

        return Vec2.areParallel(a, b, Utils.EPSILON);
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
    public static boolean areParallel(final Vec2 a, final Vec2 b, final float tolerance) {

        return Utils.abs(a.x * b.y - a.y * b.x) <= tolerance;
    }

    /**
     * Returns to a vector with a negative value on the y-axis, (0.0, -1.0) .
     *
     * @param target the output vector
     * @return the back vector
     */
    public static Vec2 back(final Vec2 target) {

        return target.set(0.0f, -1.0f);
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
    public static Vec2 bezierPoint(
        final Vec2 ap0,
        final Vec2 cp0,
        final Vec2 cp1,
        final Vec2 ap1,
        final float step,
        final Vec2 target) {

        /*
         * QUADRATIC: final float u = 1.0f - step; final float usq = u * u; final
         * float tsq = step * step; final float ut2 = u * step * 2.0f; return
         * target.set(usq * ap0.x + ut2 * cp.x + tsq * ap1.x, usq * ap0.y + ut2 *
         * cp.y + tsq * ap1.y);
         */

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
            ap0.y * ucb + cp0.y * usq3t + cp1.y * tsq3u + ap1.y * tcb);
    }

    /**
     * Returns a tangent on a Bézier curve described by two anchor points and
     * two control points according to a step in [0.0, 1.0]. When the step is
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
     * @see Vec2#sub(Vec2, Vec2, Vec2)
     */
    public static Vec2 bezierTangent(
        final Vec2 ap0,
        final Vec2 cp0,
        final Vec2 cp1,
        final Vec2 ap1,
        final float step,
        final Vec2 target) {

        if (step <= 0.0f) {
            return Vec2.sub(cp0, ap0, target);
        }
        if (step >= 1.0f) {
            return Vec2.sub(ap1, cp1, target);
        }

        final float u = 1.0f - step;
        final float t3 = step + step + step;

        final float usq3 = u * (u + u + u);
        final float tsq3 = step * t3;
        final float ut6 = u * (t3 + t3);

        return target.set(
            (cp0.x - ap0.x) * usq3 + (cp1.x - cp0.x) * ut6 + (ap1.x - cp1.x) * tsq3,
            (cp0.y - ap0.y) * usq3 + (cp1.y - cp0.y) * ut6 + (ap1.y - cp1.y) * tsq3);
    }

    /**
     * Returns a normalized tangent on a Bezier curve.
     *
     * @param ap0    the first anchor point
     * @param cp0    the first control point
     * @param cp1    the second control point
     * @param ap1    the second anchor point
     * @param step   the step
     * @param target the output vector
     * @return the tangent along the curve
     * @see Vec2#bezierTangent(Vec2, Vec2, Vec2, Vec2, float, Vec2)
     * @see Utils#invHypot(float, float)
     */
    public static Vec2 bezierTanUnit(
        final Vec2 ap0,
        final Vec2 cp0,
        final Vec2 cp1,
        final Vec2 ap1,
        final float step,
        final Vec2 target) {

        Vec2.bezierTangent(ap0, cp0, cp1, ap1, step, target);
        final float mInv = Utils.invHypot(target.x, target.y);
        return target.set(target.x * mInv, target.y * mInv);
    }

    /**
     * Appends to an array of bytes, ordered from least to most significant digit
     * (little endian). Writes 8 bytes.
     *
     * @param v   the vector
     * @param arr the array
     * @param i   the index
     * @return the byte array
     * @see Utils#byteslm(float, byte[], int)
     */
    public static byte[] byteslm(final Vec2 v, final byte[] arr, final int i) {

        Utils.byteslm(v.x, arr, i);
        Utils.byteslm(v.y, arr, i + 4);

        return arr;
    }

    /**
     * Appends to an array of bytes, ordered from most to least significant digit
     * (big endian). Writes 8 bytes.
     *
     * @param v   the vector
     * @param arr the array
     * @param i   the index
     * @return the byte array
     * @see Utils#bytesml(float, byte[], int)
     */
    public static byte[] bytesml(final Vec2 v, final byte[] arr, final int i) {

        Utils.bytesml(v.x, arr, i);
        Utils.bytesml(v.y, arr, i + 4);

        return arr;
    }

    /**
     * Raises each component of the vector to the nearest greater integer.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the raised number
     * @see Utils#ceil(float)
     */
    public static Vec2 ceil(final Vec2 v, final Vec2 target) {

        return target.set(Utils.ceil(v.x), Utils.ceil(v.y));
    }

    /**
     * Concatenates two one-dimensional Vec2 arrays.
     *
     * @param a the first array
     * @param b the second array
     * @return the concatenated array.
     * @see System#arraycopy(Object, int, Object, int, int)
     */
    public static Vec2[] concat(final Vec2[] a, final Vec2[] b) {

        final boolean aNull = a == null;
        final boolean bNull = b == null;

        if (aNull && bNull) {
            return new Vec2[]{};
        }

        if (aNull) {
            final Vec2[] result0 = new Vec2[b.length];
            System.arraycopy(b, 0, result0, 0, b.length);
            return result0;
        }

        if (bNull) {
            final Vec2[] result1 = new Vec2[a.length];
            System.arraycopy(a, 0, result1, 0, a.length);
            return result1;
        }

        final int aLen = a.length;
        final int bLen = b.length;
        final Vec2[] result = new Vec2[aLen + bLen];
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
    public static Vec2 copySign(final Vec2 magnitude, final Vec2 sign, final Vec2 target) {

        return target.set(
            Utils.copySign(magnitude.x, sign.x),
            Utils.copySign(magnitude.y, sign.y));
    }

    /**
     * Returns the z component of the cross product between two vectors. The x
     * and y components of the cross between 2D vectors are zero. For that
     * reason, the normalized cross product is equal to the sign of the cross
     * product.
     *
     * @param a left operand
     * @param b right operand
     * @return the cross z component
     */
    public static float cross(final Vec2 a, final Vec2 b) {

        return a.x * b.y - a.y * b.x;
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
    public static Vec2 diff(final Vec2 a, final Vec2 b, final Vec2 target) {

        return target.set(
            Utils.diff(a.x, b.x),
            Utils.diff(a.y, b.y));
    }

    /**
     * Finds the Euclidean distance between two vectors.
     *
     * @param a left operand
     * @param b right operand
     * @return the distance
     * @see Vec2#distEuclidean(Vec2, Vec2)
     */
    public static float dist(final Vec2 a, final Vec2 b) {

        return Vec2.distEuclidean(a, b);
    }

    /**
     * Finds the Chebyshev distance between two vectors. Forms a square pattern
     * when plotted.
     *
     * @param a left operand
     * @param b right operand
     * @return the distance
     * @see Utils#diff(float, float)
     */
    public static float distChebyshev(final Vec2 a, final Vec2 b) {

        return Utils.max(Utils.diff(a.x, b.x), Utils.diff(a.y, b.y));
    }

    /**
     * Finds the Euclidean distance between two vectors. Where possible, use
     * distance squared to avoid the computational cost of the square-root.
     *
     * @param a left operand
     * @param b right operand
     * @return the Euclidean distance
     * @see Vec2#distSq(Vec2, Vec2)
     * @see Utils#sqrtUnchecked(float)
     */
    public static float distEuclidean(final Vec2 a, final Vec2 b) {

        return Utils.sqrtUnchecked(Vec2.distSq(a, b));
    }

    /**
     * Finds the Manhattan distance between two vectors. Forms a diamond
     * pattern when plotted.
     *
     * @param a left operand
     * @param b right operand
     * @return the Manhattan distance
     * @see Utils#diff(float, float)
     */
    public static float distManhattan(final Vec2 a, final Vec2 b) {

        return Utils.diff(a.x, b.x) + Utils.diff(a.y, b.y);
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
    public static float distMinkowski(final Vec2 a, final Vec2 b, final float c) {

        if (c != 0.0f) {
            return (float)
                Math.pow(
                    Math.pow(Math.abs((b.x - a.x)), c)
                        + Math.pow(Math.abs((b.y - a.y)), c),
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
    public static float distSq(final Vec2 a, final Vec2 b) {

        final float xDist = b.x - a.x;
        final float yDist = b.y - a.y;
        return xDist * xDist + yDist * yDist;
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
    public static Vec2 div(final float a, final Vec2 b, final Vec2 target) {

        return target.set(Utils.div(a, b.x), Utils.div(a, b.y));
    }

    /**
     * Divides a vector by a scalar.
     *
     * @param a      vector, numerator
     * @param b      scalar, denominator
     * @param target the output vector
     * @return the quotient
     */
    public static Vec2 div(final Vec2 a, final float b, final Vec2 target) {

        if (b != 0.0f) {
            final float denom = 1.0f / b;
            return target.set(a.x * denom, a.y * denom);
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
    public static Vec2 div(final Vec2 a, final Vec2 b, final Vec2 target) {

        return target.set(Utils.div(a.x, b.x), Utils.div(a.y, b.y));
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
    public static float dot(final Vec2 a, final Vec2 b) {

        return a.x * b.x + a.y * b.y;
    }

    /**
     * Returns a vector with all components set to epsilon, a small positive
     * non-zero value.
     *
     * @param target the output vector
     * @return epsilon
     */
    public static Vec2 epsilon(final Vec2 target) {

        return target.set(Utils.EPSILON, Utils.EPSILON);
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
    public static Vec2 filter(final Vec2 v, final Vec2 lb, final Vec2 ub, final Vec2 target) {

        return target.set(
            Utils.filter(v.x, lb.x, ub.x),
            Utils.filter(v.y, lb.y, ub.y));
    }

    /**
     * Flattens a two-dimensional array of vectors to a one dimensional array.
     *
     * @param arr the 2D array
     * @return the 1D array
     * @see System#arraycopy(Object, int, Object, int, int)
     */
    public static Vec2[] flat(final Vec2[][] arr) {

        /* Sum the lengths of inner arrays. */
        int totalLen = 0;
        for (final Vec2[] vec2s : arr) {
            totalLen += vec2s.length;
        }

        /*
         * Copy each inner array to the result array, then move the cursor by
         * the length of each array.
         */
        int j = 0;
        final Vec2[] result = new Vec2[totalLen];
        for (final Vec2[] arrInner : arr) {
            final int len = arrInner.length;
            System.arraycopy(arrInner, 0, result, j, len);
            j += len;
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
    public static Vec2 floor(final Vec2 v, final Vec2 target) {

        return target.set(Utils.floor(v.x), Utils.floor(v.y));
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
    public static Vec2 fmod(final float a, final Vec2 b, final Vec2 target) {

        return target.set(Utils.fmod(a, b.x), Utils.fmod(a, b.y));
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
    public static Vec2 fmod(final Vec2 a, final float b, final Vec2 target) {

        if (b != 0.0f) {
            return target.set(a.x % b, a.y % b);
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
    public static Vec2 fmod(final Vec2 a, final Vec2 b, final Vec2 target) {

        return target.set(Utils.fmod(a.x, b.x), Utils.fmod(a.x, b.y));
    }

    /**
     * Returns to a vector with a positive value on the y-axis, (0.0, 1.0).
     *
     * @param target the output vector
     * @return the forward vector
     */
    public static Vec2 forward(final Vec2 target) {

        return target.set(0.0f, 1.0f);
    }

    /**
     * Returns the fractional portion of the vector's components.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the fractional portion
     * @see Utils#fract(float)
     */
    public static Vec2 fract(final Vec2 v, final Vec2 target) {

        return target.set(Utils.fract(v.x), Utils.fract(v.y));
    }

    /**
     * Creates a vector from polar coordinates: (1) theta, an angle in radians,
     * the vector's heading; (2) rho, a radius, the vector's magnitude.
     *
     * @param heading the angle in radians
     * @param radius  the radius
     * @param target  the output vector
     * @return the vector
     */
    public static Vec2 fromPolar(final float heading, final float radius, final Vec2 target) {

        final float nrm = heading * Utils.ONE_TAU;
        return target.set(radius * Utils.scNorm(nrm), radius * Utils.scNorm(nrm - 0.25f));
    }

    /**
     * Creates a vector with a magnitude of 1.0 from an angle, such that the
     * vector is on the unit circle.
     *
     * @param heading the angle in radians
     * @param target  the output vector
     * @return the vector
     */
    public static Vec2 fromPolar(final float heading, final Vec2 target) {

        final float nrm = heading * Utils.ONE_TAU;
        return target.set(Utils.scNorm(nrm), Utils.scNorm(nrm - 0.25f));
    }

    /**
     * Generates a 2D array of vectors. Defaults to the coordinate range of
     * [-0.5, 0.5].
     *
     * @param res the resolution
     * @return the array
     * @see Vec2#grid(int, int)
     */
    public static Vec2[][] grid(final int res) {

        return Vec2.grid(res, res);
    }

    /**
     * Generates a 2D array of vectors. The result is in row-major order, but
     * the parameters are supplied in reverse: columns first, then rows.
     * Defaults to the coordinate range of [-0.5, 0.5].
     *
     * @param cols number of columns
     * @param rows number of rows
     * @return the array
     * @see Vec2#grid(int, int, float, float, float, float)
     */
    public static Vec2[][] grid(final int cols, final int rows) {

        return Vec2.grid(cols, rows, -0.5f, -0.5f, 0.5f, 0.5f);
    }

    /**
     * Generates a 2D array of vectors. The result is in row-major order, but
     * the parameters are supplied in reverse: columns first, then rows.
     *
     * @param cols       number of columns
     * @param rows       number of rows
     * @param lowerBound the lower bound
     * @param upperBound the upper bound
     * @return the array
     * @see Vec2#grid(int, int, float, float, float, float)
     */
    public static Vec2[][] grid(final int cols, final int rows,
        final float lowerBound, final float upperBound) {

        return Vec2.grid(cols, rows, lowerBound, lowerBound, upperBound,
            upperBound);
    }

    /**
     * Generates a 2D array of vectors. The result is in row-major order, but
     * the parameters are supplied in reverse: columns first, then rows.
     *
     * @param cols       number of columns
     * @param rows       number of rows
     * @param lowerBound the lower bound
     * @param upperBound the upper bound
     * @return the array
     * @see Vec2#grid(int, int, float, float, float, float)
     */
    public static Vec2[][] grid(final int cols, final int rows,
        final Vec2 lowerBound, final Vec2 upperBound) {

        return Vec2.grid(cols, rows, lowerBound.x, lowerBound.y, upperBound.x,
            upperBound.y);
    }

    /**
     * Generates a 2D array of vectors. The array is ordered by rings, then
     * sectors; the parameters are supplied in reverse order.
     *
     * @param sectors the sectors, headings
     * @param radius  the radius
     * @return the array
     * @see Vec2#gridPolar(int, int, float, float, float, boolean)
     */
    public static Vec2[][] gridPolar(final int sectors, final float radius) {

        return Vec2.gridPolar(sectors, 1, radius, radius, 0.0f, true);
    }

    /**
     * Generates a 2D array of vectors. The array is ordered by rings, then
     * sectors; the parameters are supplied in reverse order.
     *
     * @param sectors   the sectors, headings
     * @param rings     the rings, radii
     * @param radiusMin minimum radius
     * @param radiusMax maximum radius
     * @return the array
     * @see Vec2#gridPolar(int, int, float, float, float, boolean)
     */
    public static Vec2[][] gridPolar(final int sectors, final int rings,
        final float radiusMin, final float radiusMax) {

        return Vec2.gridPolar(sectors, rings, radiusMin, radiusMax, 0.0f, true);
    }

    /**
     * Generates a 2D array of vectors. The array is ordered by rings, then
     * sectors; the parameters are supplied in reverse order.
     *
     * @param sectors   the sectors, headings
     * @param rings     the rings, radii
     * @param radiusMin minimum radius
     * @param radiusMax maximum radius
     * @param angOffset angular offset per ring
     * @return the array
     * @see Vec2#gridPolar(int, int, float, float, float, boolean)
     */
    public static Vec2[][] gridPolar(
        final int sectors,
        final int rings,
        final float radiusMin,
        final float radiusMax,
        final float angOffset) {

        return Vec2.gridPolar(sectors, rings, radiusMin, radiusMax, angOffset, true);
    }

    /**
     * Generates a 2D array of vectors. The array is ordered by rings, then
     * sectors; the parameters are supplied in reverse order.
     *
     * @param sectors       the sectors, headings
     * @param rings         the rings, radii
     * @param radiusMin     minimum radius
     * @param radiusMax     maximum radius
     * @param angOffset     angular offset per ring
     * @param includeCenter include the center
     * @return the array
     * @see Utils#mod1(float)
     */
    public static Vec2[][] gridPolar(
        final int sectors,
        final int rings,
        final float radiusMin,
        final float radiusMax,
        final float angOffset,
        final boolean includeCenter) {

        final int vSect = Math.max(sectors, 3);
        final int vRing = Math.max(rings, 1);
        final float angNorm = Utils.mod1(angOffset * Utils.ONE_TAU);

        final boolean oneRing = vRing == 1;
        final float vrMax = Utils.max(Utils.EPSILON, radiusMin, radiusMax);
        final float vrMin = oneRing
            ? vrMax
            : Utils.max(Utils.EPSILON, Utils.min(radiusMin, radiusMax));

        final int ringLen = includeCenter ? rings + 1 : rings;
        final Vec2[][] result = new Vec2[ringLen][vSect];
        if (includeCenter) {
            result[0] = new Vec2[]{new Vec2()};
        }

        final float toStep = oneRing ? 1.0f : 1.0f / (vRing - 1.0f);
        final float toTheta = 1.0f / vSect;

        final int flatLen = vRing * vSect;
        for (int k = 0; k < flatLen; ++k) {
            final int i = k / vSect;
            final int j = k % vSect;

            final float iStep = (float) i * toStep;
            final float offset = (float) i * angNorm;

            final float radius = (1.0f - iStep) * vrMin + iStep * vrMax;
            final float theta = offset + j * toTheta;

            result[includeCenter ? 1 + i : i][j] = new Vec2(
                radius * Utils.scNorm(theta),
                radius * Utils.scNorm(theta - 0.25f));
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
    public static boolean gt(final Vec2 a, final Vec2 b) {

        return a.x > b.x && a.y > b.y;
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
    public static Vec2 gt(final Vec2 a, final Vec2 b, final Vec2 target) {

        return target.set(a.x > b.x, a.y > b.y);
    }

    /**
     * Evaluates whether all components of the left comparisand are greater
     * than or equal to those of the right comparisand.
     *
     * @param a left comparisand
     * @param b right comparisand
     * @return the evaluation
     */
    public static boolean gtEq(final Vec2 a, final Vec2 b) {

        return a.x >= b.x && a.y >= b.y;
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
    public static Vec2 gtEq(final Vec2 a, final Vec2 b, final Vec2 target) {

        return target.set(a.x >= b.x, a.y >= b.y);
    }

    /**
     * Multiplies two vectors, component-wise.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the product
     */
    public static Vec2 hadamard(final Vec2 a, final Vec2 b, final Vec2 target) {

        return target.set(a.x * b.x, a.y * b.y);
    }

    /**
     * Finds the vector's heading. Defaults to headingSigned.
     *
     * @param v the input vector
     * @return the angle in radians
     * @see Vec2#headingSigned(Vec2)
     */
    public static float heading(final Vec2 v) {

        return Vec2.headingSigned(v);
    }

    /**
     * Finds the vector's heading in the range [-pi, pi].
     *
     * @param v the input vector
     * @return the angle in radians
     * @see Utils#atan2(float, float)
     */
    public static float headingSigned(final Vec2 v) {

        return Utils.atan2(v.y, v.x);
    }

    /**
     * Finds the vector's heading in the range [0.0, tau].
     *
     * @param v the input vector
     * @return the angle in radians
     * @see Vec2#headingSigned(Vec2)
     */
    public static float headingUnsigned(final Vec2 v) {

        final float h = Vec2.headingSigned(v);
        return h < -0.0f ? h + Utils.TAU : h;
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
    public static Vec2[] insert(final Vec2[] arr, final int index, final Vec2[] insert) {

        final int alen = arr.length;
        final int blen = insert.length;
        final int valIdx = Utils.mod(index, alen + 1);

        final Vec2[] result = new Vec2[alen + blen];
        System.arraycopy(arr, 0, result, 0, valIdx);
        System.arraycopy(insert, 0, result, valIdx, blen);
        System.arraycopy(arr, valIdx, result, valIdx + blen, alen - valIdx);

        return result;
    }

    /**
     * Tests to see if the vector is on the unit circle, i.e., has a magnitude
     * of approximately 1.0 .
     *
     * @param v the input vector
     * @return the evaluation
     * @see Utils#approx(float, float)
     * @see Vec2#magSq(Vec2)
     */
    public static boolean isUnit(final Vec2 v) {

        return Utils.approx(Vec2.magSq(v), 1.0f);
    }

    /**
     * Returns a vector with a negative value on the x-axis, (-1.0, 0.0).
     *
     * @param target the output vector
     * @return the left vector
     */
    public static Vec2 left(final Vec2 target) {

        return target.set(-1.0f, 0.0f);
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
    public static Vec2 limit(final Vec2 v, final float limit, final Vec2 target) {

        final float mSq = v.x * v.x + v.y * v.y;
        if (limit > 0.0f && mSq > limit * limit) {
            final float scalar = limit * Utils.invSqrtUnchecked(mSq);
            return target.set(v.x * scalar, v.y * scalar);
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
    public static boolean lt(final Vec2 a, final Vec2 b) {

        return a.x < b.x && a.y < b.y;
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
    public static Vec2 lt(final Vec2 a, final Vec2 b, final Vec2 target) {

        return target.set(a.x < b.x, a.y < b.y);
    }

    /**
     * Evaluates whether all components of the left comparisand are less than
     * or equal to those of the right comparisand.
     *
     * @param a left comparisand
     * @param b right comparisand
     * @return the evaluation
     */
    public static boolean ltEq(final Vec2 a, final Vec2 b) {

        return a.x <= b.x && a.y <= b.y;
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
    public static Vec2 ltEq(final Vec2 a, final Vec2 b, final Vec2 target) {

        return target.set(a.x <= b.x, a.y <= b.y);
    }

    /**
     * Finds the length, or magnitude, of a vector, |<em>a</em>| . Also
     * referred to as the radius when using polar coordinates. Uses the formula
     * √ <em>a</em> . <em>a</em> . Where possible, use magSq or dot to avoid
     * the computational cost of the square-root.
     *
     * @param v the input vector
     * @return the magnitude
     * @see Utils#sqrtUnchecked(float)
     */
    public static float mag(final Vec2 v) {

        return Utils.sqrtUnchecked(v.x * v.x + v.y * v.y);
    }

    /**
     * Finds the length-, or magnitude-, squared of a vector,
     * |<em>a</em>|<sup>2</sup>. Returns the same result as
     * <em>a</em> . <em>a</em> . Useful when calculating the lengths of many
     * vectors, to avoid the computational cost of the square-root.
     *
     * @param v the input vector
     * @return the magnitude squared
     */
    public static float magSq(final Vec2 v) {

        return v.x * v.x + v.y * v.y;
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
    public static Vec2 map(
        final Vec2 v,
        final Vec2 lbOrig,
        final Vec2 ubOrig,
        final Vec2 lbDest,
        final Vec2 ubDest,
        final Vec2 target) {

        return target.set(
            Utils.map(v.x, lbOrig.x, ubOrig.x, lbDest.x, ubDest.x),
            Utils.map(v.y, lbOrig.y, ubOrig.y, lbDest.y, ubDest.y));
    }

    /**
     * Sets the target vector to the maximum of the input vector and a lower
     * bound.
     *
     * @param a          the input value
     * @param lowerBound the lower bound
     * @param target     the output vector
     * @return the maximum values
     */
    public static Vec2 max(final Vec2 a, final float lowerBound, final Vec2 target) {

        return target.set(
            Utils.max(a.x, lowerBound),
            Utils.max(a.y, lowerBound));
    }

    /**
     * Sets the target vector to the maximum components of the input vector and
     * a lower bound.
     *
     * @param a          the input vector
     * @param lowerBound the lower bound
     * @param target     the output vector
     * @return the maximum values
     */
    public static Vec2 max(final Vec2 a, final Vec2 lowerBound, final Vec2 target) {

        return target.set(
            Utils.max(a.x, lowerBound.x),
            Utils.max(a.y, lowerBound.y));
    }

    /**
     * Sets the target vector to the minimum components of the input vector and
     * an upper bound.
     *
     * @param a          the input value
     * @param upperBound the upper bound
     * @param target     the output vector
     * @return the minimum values
     */
    public static Vec2 min(final Vec2 a, final float upperBound, final Vec2 target) {

        return target.set(
            Utils.min(a.x, upperBound),
            Utils.min(a.y, upperBound));
    }

    /**
     * Sets the target vector to the minimum components of the input vector and
     * an upper bound.
     *
     * @param a          the input vector
     * @param upperBound the upper bound
     * @param target     the output vector
     * @return the minimal values
     */
    public static Vec2 min(final Vec2 a, final Vec2 upperBound, final Vec2 target) {

        return target.set(
            Utils.min(a.x, upperBound.x),
            Utils.min(a.y, upperBound.y));
    }

    /**
     * Mixes two vectors together by a step in [0.0, 1.0] .
     *
     * @param orig   the original vector
     * @param dest   the destination vector
     * @param step   the step
     * @param target the output vector
     * @return the mix
     */
    public static Vec2 mix(
        final Vec2 orig,
        final Vec2 dest,
        final float step,
        final Vec2 target) {

        if (step <= 0.0f) {
            return target.set(orig);
        }
        if (step >= 1.0f) {
            return target.set(dest);
        }

        final float u = 1.0f - step;
        return target.set(
            u * orig.x + step * dest.x,
            u * orig.y + step * dest.y);
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
    public static Vec2 mod(final float a, final Vec2 b, final Vec2 target) {

        return target.set(Utils.mod(a, b.x), Utils.mod(a, b.y));
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
    public static Vec2 mod(final Vec2 a, final float b, final Vec2 target) {

        if (b != 0.0f) {
            return target.set(
                Utils.modUnchecked(a.x, b),
                Utils.modUnchecked(a.y, b));
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
    public static Vec2 mod(final Vec2 a, final Vec2 b, final Vec2 target) {

        return target.set(Utils.mod(a.x, b.x), Utils.mod(a.y, b.y));
    }

    /**
     * A specialized form of modulo which subtracts the floor of the vector
     * from the vector. Useful for managing texture coordinates in the range
     * [0.0, 1.0].
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the result
     * @see Utils#mod1(float)
     */
    public static Vec2 mod1(final Vec2 v, final Vec2 target) {

        return target.set(Utils.mod1(v.x), Utils.mod1(v.y));
    }

    /**
     * Multiplies a vector by a scalar.
     *
     * @param a      left operand, the scalar
     * @param b      right operand, the vector
     * @param target the output vector
     * @return the product
     */
    public static Vec2 mul(final float a, final Vec2 b, final Vec2 target) {

        return target.set(a * b.x, a * b.y);
    }

    /**
     * Multiplies a vector by a scalar.
     *
     * @param a      left operand, the vector
     * @param b      right operand, the scalar
     * @param target the output vector
     * @return the product
     */
    public static Vec2 mul(final Vec2 a, final float b, final Vec2 target) {

        return target.set(a.x * b, a.y * b);
    }

    /**
     * Negates the input vector.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the negation
     */
    public static Vec2 negate(final Vec2 v, final Vec2 target) {

        return target.set(-v.x, -v.y);
    }

    /**
     * Returns a vector with all components set to negative one.
     *
     * @param target the output vector
     * @return negative one
     */
    public static Vec2 negOne(final Vec2 target) {

        return target.set(-1.0f, -1.0f);
    }

    /**
     * Tests to see if all the vector's components are zero. Useful when
     * safeguarding against invalid directions.
     *
     * @param v the input vector
     * @return the evaluation
     */
    public static boolean none(final Vec2 v) {

        return v.y == 0.0f && v.x == 0.0f;
    }

    /**
     * Divides a vector by its magnitude, such that the new magnitude is 1.0 .
     * <em>â</em> = <em>a</em> / |<em>a</em>| . The result is a unit vector,
     * as it lies on the circumference of a unit circle.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the unit vector
     * @see Utils#invSqrt(float)
     */
    public static Vec2 normalize(final Vec2 v, final Vec2 target) {

        final float mInv = Utils.invSqrt(v.x * v.x + v.y * v.y);
        return target.set(v.x * mInv, v.y * mInv);
    }

    /**
     * Evaluates a vector like a boolean, where n != 0.0 is true.
     *
     * @param v      the vector
     * @param target the output vector
     * @return the truth table opposite
     */
    public static Vec2 not(final Vec2 v, final Vec2 target) {

        return target.set(v.x != 0.0f ? 0.0f : 1.0f, v.y != 0.0f ? 0.0f : 1.0f);
    }

    /**
     * Returns a vector with both components set to one.
     *
     * @param target the output vector
     * @return one
     */
    public static Vec2 one(final Vec2 target) {

        return target.set(1.0f, 1.0f);
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
    public static Vec2 or(final Vec2 a, final Vec2 b, final Vec2 target) {

        return target.set(Utils.or(a.x, b.x), Utils.or(a.y, b.y));
    }

    /**
     * Finds the perpendicular of a vector. Defaults to counter-clockwise
     * rotation, such that the perpendicular of ( 1.0, 0.0 ) is ( 0.0, 1.0 ).
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the perpendicular
     * @see Vec2#perpendicularCCW(Vec2, Vec2)
     */
    public static Vec2 perpendicular(final Vec2 v, final Vec2 target) {

        return Vec2.perpendicularCCW(v, target);
    }

    /**
     * Finds the perpendicular of a vector in the counter-clockwise direction,
     * such that
     *
     * <ul>
     * <li>perp ( right ) = forward, perp ( 1.0, 0.0 ) = ( 0.0, 1.0 )</li>
     * <li>perp ( forward ) = left, perp ( 0.0, 1.0 ) = ( -1.0, 0.0 )</li>
     * <li>perp ( left ) = back, perp ( -1.0, 0.0 ) = ( 0.0, -1.0 )</li>
     * <li>perp ( back ) = right, perp ( 0.0, -1.0 ) = ( 1.0, 0.0 )</li>
     * </ul>
     * <p>
     * In terms of the components, perp ( x, y ) = ( -y, x ) .
     *
     * @param a      the input vector
     * @param target the output vector
     * @return the perpendicular
     */
    public static Vec2 perpendicularCCW(final Vec2 a, final Vec2 target) {

        // noinspection SuspiciousNameCombination
        return target.set(-a.y, a.x);
    }

    /**
     * Finds the perpendicular of a vector in the clockwise direction, such
     * that
     *
     * <ul>
     * <li>perp ( right ) = back, perp( 1.0, 0.0 ) = ( 0.0, -1.0 )</li>
     * <li>perp ( back ) = left, perp( 0.0, -1.0 ) = ( -1.0, 0.0 )</li>
     * <li>perp ( left ) = forward, perp( -1.0, 0.0 ) = ( 0.0, 1.0 )</li>
     * <li>perp ( forward ) = right, perp( 0.0, 1.0 ) = ( 1.0, 0.0 )</li>
     * </ul>
     * <p>
     * In terms of the components, perp ( x, y ) = ( y, -x ) .
     *
     * @param a      the input vector
     * @param target the output vector
     * @return the perpendicular
     */
    public static Vec2 perpendicularCW(final Vec2 a, final Vec2 target) {

        // noinspection SuspiciousNameCombination
        return target.set(a.y, -a.x);
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
    public static Vec2 pingPong(
        final Vec2 orig,
        final Vec2 dest,
        final float step,
        final float pause,
        final Vec2 target) {

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
            u * orig.y + t * dest.y);
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
    public static Vec2 pingPong(
        final Vec2 orig,
        final Vec2 dest,
        final float step,
        final Vec2 target) {

        return Vec2.pingPong(orig, dest, step, 1.0f, target);
    }

    /**
     * Projects one vector onto another.
     *
     * @param a left operand
     * @param b right operand
     * @return the projection
     * @see Vec2#projectScalar(Vec2, Vec2)
     */
    public static float project(final Vec2 a, final Vec2 b) {

        return Vec2.projectScalar(a, b);
    }

    /**
     * Projects one vector onto another.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the projection
     * @see Vec2#projectVector(Vec2, Vec2, Vec2)
     */
    public static Vec2 project(final Vec2 a, final Vec2 b, final Vec2 target) {

        return Vec2.projectVector(a, b, target);
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
    public static float projectScalar(final Vec2 a, final Vec2 b) {

        final float bSq = b.x * b.x + b.y * b.y;
        if (bSq > 0.0f) {
            return (a.x * b.x + a.y * b.y) / bSq;
        }
        return 0.0f;
    }

    /**
     * Projects one vector onto another. Defined as
     * <br>
     * <br>
     * project ( <em>a</em>, <em>b</em> ) := <em>b</em> ( <em>a</em> .
     * <em>b</em> / <em>b</em> . <em>b</em> )
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the projection
     * @see Vec2#projectScalar(Vec2, Vec2)
     * @see Vec2#mul(Vec2, float, Vec2)
     */
    public static Vec2 projectVector(final Vec2 a, final Vec2 b, final Vec2 target) {

        return Vec2.mul(b, Vec2.projectScalar(a, b), target);
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
    public static Vec2 quantize(final Vec2 v, final int levels, final Vec2 target) {

        if (levels == 0) {
            return target.set(v);
        }
        final float levf = levels < 0 ? -levels : levels;
        final float delta = 1.0f / levf;
        return target.set(
            Utils.quantizeSigned(v.x, levf, delta),
            Utils.quantizeSigned(v.y, levf, delta));
    }

    /**
     * Generates a vector with a random heading and a magnitude of 1.0, such
     * that it lies on the unit circle.
     *
     * @param rng    the random number generator
     * @param target the output vector
     * @return the random vector
     * @see Vec2#randomPolar(Random, float, Vec2)
     */
    public static Vec2 random(final Random rng, final Vec2 target) {

        return Vec2.randomPolar(rng, target);
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
    public static Vec2 randomCartesian(
        final Random rng,
        final float lowerBound,
        final float upperBound,
        final Vec2 target) {

        final float rx = rng.nextFloat();
        final float ry = rng.nextFloat();
        return target.set(
            (1.0f - rx) * lowerBound + rx * upperBound,
            (1.0f - ry) * lowerBound + ry * upperBound);
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
    public static Vec2 randomCartesian(
        final Random rng,
        final Vec2 lowerBound,
        final Vec2 upperBound,
        final Vec2 target) {

        final float rx = rng.nextFloat();
        final float ry = rng.nextFloat();
        return target.set(
            (1.0f - rx) * lowerBound.x + rx * upperBound.x,
            (1.0f - ry) * lowerBound.y + ry * upperBound.y);
    }

    /**
     * Creates a random vector that lies on a circle. Uses 2 random numbers
     * with normal distribution, then rescales to the sphere radius.
     *
     * @param rng    the random number generator
     * @param radius the radius
     * @param target the output vector
     * @return the random vector
     * @see Random#nextGaussian()
     */
    public static Vec2 randomPolar(final Random rng, final float radius, final Vec2 target) {

        final double x = rng.nextGaussian();
        final double y = rng.nextGaussian();
        final double sqMag = x * x + y * y;
        if (sqMag != 0.0d) {
            final double invMag = radius / Math.sqrt(sqMag);
            return target.set((float) (x * invMag), (float) (y * invMag));
        }
        return target.reset();
    }

    /**
     * Creates a vector with a magnitude of 1.0 at a random heading, such that
     * it lies on the unit circle.
     *
     * @param rng    the random number generator
     * @param target the output vector
     * @return the random vector
     * @see Vec2#randomPolar(Random, float, Vec2)
     */
    public static Vec2 randomPolar(final Random rng, final Vec2 target) {

        return Vec2.randomPolar(rng, 1.0f, target);
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
     * @see Vec2#magSq(Vec2)
     * @see Utils#invSqrtUnchecked(float)
     */
    public static Vec2 reflect(
        final Vec2 incident,
        final Vec2 normal,
        final Vec2 target) {

        final float nMSq = Vec2.magSq(normal);
        if (nMSq < Utils.EPSILON) {
            return target.reset();
        }

        final float mInv = Utils.invSqrtUnchecked(nMSq);
        final float nx = normal.x * mInv;
        final float ny = normal.y * mInv;
        final float scalar = 2.0f * (nx * incident.x + ny * incident.y);
        return target.set(incident.x - scalar * nx, incident.y - scalar * ny);
    }

    /**
     * Refracts a vector through a volume using Snell's law.
     *
     * @param incident the incident vector
     * @param normal   the normal vector
     * @param eta      ratio of refraction indices
     * @param target   the output vector
     * @return the refraction
     * @see Vec2#dot(Vec2, Vec2)
     * @see Utils#sqrtUnchecked(float)
     */
    public static Vec2 refract(
        final Vec2 incident,
        final Vec2 normal,
        final float eta,
        final Vec2 target) {

        final float nDotI = Vec2.dot(normal, incident);
        final float k = 1.0f - eta * eta * (1.0f - nDotI * nDotI);
        if (k <= 0.0f) {
            return target.reset();
        }
        final float s = eta * nDotI + Utils.sqrtUnchecked(k);
        return target.set(
            eta * incident.x - s * normal.x,
            eta * incident.y - s * normal.y);
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
    public static Vec2 rescale(final Vec2 v, final float scalar, final Vec2 target) {

        final float mSq = v.x * v.x + v.y * v.y;
        if (scalar != 0.0f && mSq > 0.0f) {
            final float sclMg = scalar * Utils.invSqrtUnchecked(mSq);
            return target.set(v.x * sclMg, v.y * sclMg);
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
     * @see Vec2#normalize(Vec2, Vec2)
     * @see Vec2#mul(Vec2, float, Vec2)
     */
    public static Vec2 rescale(
        final Vec2 v,
        final float scalar,
        final Vec2 target,
        final Vec2 normalized) {

        if (scalar != 0.0f) {
            Vec2.normalize(v, normalized);
            return Vec2.mul(normalized, scalar, target);
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
     * This does <em>not</em> use
     * {@link System#arraycopy(Object, int, Object, int, int)} because this
     * function iterates through the entire array checking for null entries.
     *
     * @param arr the array
     * @param sz  the new size
     * @return the array
     */
    public static Vec2[] resize(final Vec2[] arr, final int sz) {

        if (sz < 1) {
            return new Vec2[]{};
        }
        final Vec2[] result = new Vec2[sz];

        if (arr == null) {
            for (int i = 0; i < sz; ++i) {
                result[i] = new Vec2();
            }
            return result;
        }

        final int last = arr.length - 1;
        for (int i = 0; i < sz; ++i) {
            if (i > last || arr[i] == null) {
                result[i] = new Vec2();
            } else {
                result[i] = arr[i];
            }
        }

        return result;
    }

    /**
     * Returns to a vector with a positive value on the x-axis, (1.0, 0.0).
     *
     * @param target the output vector
     * @return the right vector
     */
    public static Vec2 right(final Vec2 target) {

        return target.set(1.0f, 0.0f);
    }

    /**
     * Rotates a vector around the x-axis by an angle in radians. For 2D
     * vectors, this scales the y component by cosine of the angle.
     *
     * @param v       the input vector
     * @param radians the angle in radians
     * @param target  the output vector
     * @return the rotated vector
     */
    public static Vec2 rotateX(final Vec2 v, final float radians, final Vec2 target) {

        return target.set(v.x, v.y * (float) Math.cos(radians));
    }

    /**
     * Rotates a vector around the y-axis by an angle in radians. For 2D
     * vectors, this scales the x component by cosine of the angle.
     *
     * @param v       the input vector
     * @param radians the angle in radians
     * @param target  the output vector
     * @return the rotated vector
     */
    public static Vec2 rotateY(final Vec2 v, final float radians, final Vec2 target) {

        return target.set(v.x * (float) Math.cos(radians), v.y);
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
    public static Vec2 rotateZ(final Vec2 v, final float cosa, final float sina, final Vec2 target) {

        return target.set(cosa * v.x - sina * v.y, cosa * v.y + sina * v.x);
    }

    /**
     * Rotates a vector around the z axis by an angle in radians.
     *
     * @param v       the input vector
     * @param radians the angle in radians
     * @param target  the output vector
     * @return the rotated vector
     */
    public static Vec2 rotateZ(final Vec2 v, final float radians, final Vec2 target) {

        final float nrm = radians * Utils.ONE_TAU;
        final float cosa = Utils.scNorm(nrm);
        final float sina = Utils.scNorm(nrm - 0.25f);
        return target.set(cosa * v.x - sina * v.y, cosa * v.y + sina * v.x);
    }

    /**
     * Rounds each component of the vector to the nearest whole number.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the rounded vector
     * @see Utils#round(float)
     */
    public static Vec2 round(final Vec2 v, final Vec2 target) {

        return target.set(Utils.round(v.x), Utils.round(v.y));
    }

    /**
     * Finds the sign of the vector: -1, if negative; 1, if positive.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the sign
     */
    public static Vec2 sign(final Vec2 v, final Vec2 target) {

        /* Sign returns an integer; this is inlined to avoid cast. */
        return target.set(
            v.x < -0.0f ? -1.0f : v.x > 0.0f ? 1.0f : 0.0f,
            v.y < -0.0f ? -1.0f : v.y > 0.0f ? 1.0f : 0.0f);
    }

    /**
     * Subtracts the right vector from the left vector.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the difference
     */
    public static Vec2 sub(final Vec2 a, final Vec2 b, final Vec2 target) {

        return target.set(a.x - b.x, a.y - b.y);
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
    public static Vec2 subNorm(final Vec2 a, final Vec2 b, final Vec2 target) {

        final float dx = a.x - b.x;
        final float dy = a.y - b.y;
        final float mInv = Utils.invSqrtUnchecked(dx * dx + dy * dy);
        return target.set(dx * mInv, dy * mInv);
    }

    /**
     * Subtracts the right from the left vector and then normalizes the
     * difference.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @param dir    the direction with magnitude
     * @return the normalized difference
     * @see Vec2#sub(Vec2, Vec2, Vec2)
     * @see Vec2#normalize(Vec2, Vec2)
     */
    public static Vec2 subNorm(final Vec2 a, final Vec2 b, final Vec2 target, final Vec2 dir) {

        Vec2.sub(a, b, dir);
        return Vec2.normalize(dir, target);
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
    public static StringBuilder toString(
        final StringBuilder sb,
        final Vec2[] arr,
        final int places) {

        /* Caches array element to a variable in case of null check. */

        sb.append('[');
        if (arr != null) {
            final int len = arr.length;
            final int last = len - 1;

            for (int i = 0; i < last; ++i) {
                final Vec2 v = arr[i];
                v.toString(sb, places);
                sb.append(',');
            }
            final Vec2 vl = arr[last];
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
    public static String toString(final Vec2[] arr) {

        return Vec2.toString(arr, Utils.FIXED_PRINT);
    }

    /**
     * Returns a string representation of an array of vectors.
     *
     * @param arr    the array
     * @param places the print precision
     * @return the string
     */
    public static String toString(final Vec2[] arr, final int places) {

        return Vec2.toString(new StringBuilder(1024), arr, places).toString();
    }

    /**
     * Truncates each component of the vector.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the truncation
     */
    public static Vec2 trunc(final Vec2 v, final Vec2 target) {

        return target.set((int) v.x, (int) v.y);
    }

    /**
     * Returns a vector representing the center of the texture coordinate
     * system, (0.5, 0.5).
     *
     * @param target the output vector
     * @return the texture coordinate center
     */
    public static Vec2 uvCenter(final Vec2 target) {

        return target.set(0.5f, 0.5f);
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
    public static Vec2 wrap(
        final Vec2 v,
        final Vec2 lb,
        final Vec2 ub,
        final Vec2 target) {

        return target.set(
            Utils.wrap(v.x, lb.x, ub.x),
            Utils.wrap(v.y, lb.y, ub.y));
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
    public static Vec2 xor(final Vec2 a, final Vec2 b, final Vec2 target) {

        return target.set(Utils.xor(a.x, b.x), Utils.xor(a.y, b.y));
    }

    /**
     * Returns a vector with all components set to zero.
     *
     * @param target the output vector
     * @return the zero vector
     */
    public static Vec2 zero(final Vec2 target) {

        return target.set(0.0f, 0.0f);
    }

    /**
     * Internal function for generating 2D array of vectors. The result is in
     * row-major order, but the parameters are supplied in reverse: columns
     * first, then rows.
     * <br>
     * <br>
     * This is separated to make overriding the public grid functions easier.
     *
     * @param cols number of columns
     * @param rows number of rows
     * @param lbx  lower bound x
     * @param lby  lower bound y
     * @param ubx  upper bound x
     * @param uby  upper bound y
     * @return the array
     */
    protected static Vec2[][] grid(
        final int cols,
        final int rows,
        final float lbx,
        final float lby,
        final float ubx,
        final float uby) {

        final int rVrf = Math.max(rows, 1);
        final int cVrf = Math.max(cols, 1);

        final Vec2[][] result = new Vec2[rVrf][cVrf];

        final boolean rOne = rVrf == 1;
        final boolean cOne = cVrf == 1;

        final float iToStep = rOne ? 0.0f : 1.0f / (rVrf - 1.0f);
        final float jToStep = cOne ? 0.0f : 1.0f / (cVrf - 1.0f);

        final float iOff = rOne ? 0.5f : 0.0f;
        final float jOff = cOne ? 0.5f : 0.0f;

        final int len = rVrf * cVrf;
        for (int k = 0; k < len; ++k) {
            final int i = k / cVrf;
            final int j = k % cVrf;

            final float iStep = i * iToStep + iOff;
            final float jStep = j * jToStep + jOff;

            result[i][j] = new Vec2(
                (1.0f - jStep) * lbx + jStep * ubx,
                (1.0f - iStep) * lby + iStep * uby);
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
    public int compareTo(final Vec2 v) {

        return this.y < v.y ? -1
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
        return this.equals((Vec2) o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    /**
     * Resets this vector to an initial state, ( 0.0, 0.0 ).
     *
     * @return this vector
     */
    public Vec2 reset() {
        return this.set(0.0f, 0.0f);
    }

    /**
     * Sets the components of this vector from booleans, where false is 0.0 and
     * true is 1.0.
     *
     * @param x the x component
     * @param y the y component
     * @return this vector
     */
    public Vec2 set(final boolean x, final boolean y) {

        this.x = x ? 1.0f : 0.0f;
        this.y = y ? 1.0f : 0.0f;
        return this;
    }

    /**
     * Sets the components of this vector.
     *
     * @param x the x component
     * @param y the y component
     * @return this vector
     */
    public Vec2 set(final float x, final float y) {

        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * Copies the components of the input vector to this vector.
     *
     * @param v the source vector
     * @return this vector
     */
    public Vec2 set(final Vec2 v) {
        return this.set(v.x, v.y);
    }

    /**
     * Returns a float array of length 2 containing this vector's components.
     *
     * @return the array
     */
    public float[] toArray() {
        return this.toArray(new float[2], 0);
    }

    /**
     * Puts the vector's components into an existing array at the index
     * provided. The vector's x component is assigned to element
     * <code>i</code>; its y component, to element <code>i + 1</code>
     * .
     *
     * @param arr the array
     * @param i   the index
     * @return the array
     */
    public float[] toArray(final float[] arr, final int i) {

        arr[i] = this.x;
        arr[i + 1] = this.y;
        return arr;
    }

    /**
     * Returns a string representation of this vector.
     *
     * @return the string
     */
    @Override
    public String toString() {

        return this.toString(new StringBuilder(64), Utils.FIXED_PRINT).toString();
    }

    /**
     * Returns a string representation of this vector.
     *
     * @param places number of decimal places
     * @return the string
     */
    public String toString(final int places) {

        return this.toString(new StringBuilder(64), places).toString();
    }

    /**
     * An internal helper function to format a vector as a Python tuple, then
     * append it to a {@link StringBuilder}. Used for testing purposes to
     * compare results with Blender 4.x.
     * <br>
     * <br>
     * If this is a UV coordinate, provides the option to flip the v
     * coordinate.
     *
     * @param pyCd  string builder
     * @param flipv whether to subtract y from 1.0
     */
    @SuppressWarnings("SameParameterValue")
    void toBlenderCode(final StringBuilder pyCd, final boolean flipv) {

        pyCd.append('(');
        Utils.toFixed(pyCd, flipv ? this.x : 1.0f - this.x, 6);
        pyCd.append(',');
        pyCd.append(' ');
        Utils.toFixed(pyCd, flipv ? 1.0f - this.y : this.y, 6);
        pyCd.append(')');
    }

    /**
     * An internal helper function to format a vector as a Python tuple, then
     * append it to a {@link StringBuilder}. Used for testing purposes to
     * compare results with Blender 4.x. Appends a z component to promote the
     * vector to 3D.
     *
     * @param pyCd string builder
     * @param z    the z component
     */
    void toBlenderCode(final StringBuilder pyCd, final float z) {

        pyCd.append('(');
        Utils.toFixed(pyCd, this.x, 6);
        pyCd.append(',');
        pyCd.append(' ');
        Utils.toFixed(pyCd, this.y, 6);
        pyCd.append(',');
        pyCd.append(' ');
        Utils.toFixed(pyCd, z, 6);
        pyCd.append(')');
    }

    /**
     * Internal helper method that appends a representation of this vector in
     * the Wavefront OBJ file format to a {@link StringBuilder}.
     *
     * @param objs  the string builder
     * @param flipv whether to subtract y from 1.0
     */
    void toObjString(final StringBuilder objs, final boolean flipv) {

        Utils.toFixed(objs, flipv ? this.x : 1.0f - this.x, 6);
        objs.append(' ');
        Utils.toFixed(objs, flipv ? 1.0f - this.y : this.y, 6);
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
        sb.append('}');
        return sb;
    }

    /**
     * An internal helper function to append a representation of this vector as
     * a comma separated value to a {@link StringBuilder}. For use by SVG
     * formatting functions.
     * <br>
     * <br>
     * This uses a print precision of six decimal places to avoid glitches when
     * small shapes are scaled up.
     *
     * @param svgp  string builder
     * @param delim the delimiter
     */
    @SuppressWarnings("SameParameterValue")
    void toSvgString(final StringBuilder svgp, final char delim) {

        Utils.toFixed(svgp, this.x, ISvgWritable.FIXED_PRINT);
        svgp.append(delim);
        Utils.toFixed(svgp, this.y, ISvgWritable.FIXED_PRINT);
    }

    /**
     * Tests equivalence between this and another vector. For rough equivalence
     * of floating point components, use the static approximation function
     * instead.
     *
     * @param v the vector
     * @return the evaluation
     */
    protected boolean equals(final Vec2 v) {

        /* With {@link Float.floatToIntBits(float)}, -0.0f != 0.0f. */
        return this.y == v.y && this.x == v.x;
    }

    /**
     * An abstract class that may serve as an umbrella for any custom
     * comparators of Vec2 s.
     */
    public abstract static class AbstrComparator implements Comparator<Vec2> {

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
    public abstract static class AbstrEasing implements Utils.EasingFuncObj<Vec2> {

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
        public Vec2 apply(
            final Vec2 orig,
            final Vec2 dest,
            final Float step,
            final Vec2 target) {

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
        public abstract Vec2 applyUnclamped(
            final Vec2 orig,
            final Vec2 dest,
            final float step,
            final Vec2 target);

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
         * @return the eased vector
         */
        @Override
        public Vec2 applyUnclamped(
            final Vec2 orig,
            final Vec2 dest,
            final float step,
            final Vec2 target) {

            final float uf = 1.0f - step;
            return target.set(
                uf * orig.x + step * dest.x,
                uf * orig.y + step * dest.y);
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
        public Vec2 applyUnclamped(
            final Vec2 orig,
            final Vec2 dest,
            final float step,
            final Vec2 target) {

            final float tf = step * step * (3.0f - (step + step));
            final float uf = 1.0f - tf;
            return target.set(
                uf * orig.x + tf * dest.x,
                uf * orig.y + tf * dest.y);
        }
    }

    /**
     * Compares two vectors against a locus with squared Euclidean distance.
     */
    public static class SortDistSq extends AbstrComparator {

        /**
         * The point against which distances are compared.
         */
        public final Vec2 locus = new Vec2();

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
        public SortDistSq(final Vec2 locus) {

            this.locus.set(locus);
        }

        /**
         * The compare function.
         *
         * @param a the left comparisand
         * @param b the right comparisand
         * @return the comparison
         * @see Vec2#distSq(Vec2, Vec2)
         */
        @Override
        public int compare(final Vec2 a, final Vec2 b) {

            final float ad = Vec2.distSq(this.locus, a);
            final float bd = Vec2.distSq(this.locus, b);
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
        public int compare(final Vec2 a, final Vec2 b) {

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
        public int compare(final Vec2 a, final Vec2 b) {

            return a.y < b.y ? -1 : a.y > b.y ? 1 : 0;
        }
    }
}
