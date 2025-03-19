package com.behreajj.camzup.core;

import java.util.Comparator;
import java.util.Objects;
import java.util.Random;

/**
 * A mutable, extensible class influenced by GLSL. Instance methods are limited,
 * while most static
 * methods require an explicit output variable to be provided.<br>
 * <br>
 * May also be used to store alternative color representations.
 */
public class Vec4 implements Comparable<Vec4> {

    /**
     * Component on the w axis. Commonly used to store 1.0 for points and 0.0 for
     * vectors when
     * multiplying with a 4 x 4 matrix.
     */
    public float w = 0.0f;

    /**
     * Component on the x-axis.
     */
    public float x = 0.0f;

    /**
     * Component on the y-axis.
     */
    public float y = 0.0f;

    /**
     * Component on the z axis.
     */
    public float z = 0.0f;

    /**
     * The default vector constructor.
     */
    public Vec4() {
    }

    /**
     * Constructs a vector from boolean values.
     *
     * @param x the x component
     * @param y the y component
     * @param z the z component
     * @param w the w component
     */
    public Vec4(final boolean x, final boolean y, final boolean z, final boolean w) {

        this.set(x, y, z, w);
    }

    /**
     * Constructs a vector from float values.
     *
     * @param x the x component
     * @param y the y component
     * @param z the z component
     * @param w the w component
     */
    public Vec4(final float x, final float y, final float z, final float w) {

        this.set(x, y, z, w);
    }

    /**
     * Promotes a Vec2 to a Vec4.
     *
     * @param v2 the vector
     */
    public Vec4(final Vec2 v2) {
        this.set(v2);
    }

    /**
     * Promotes a Vec3 to a Vec4.
     *
     * @param v3 the vector
     */
    public Vec4(final Vec3 v3) {
        this.set(v3);
    }

    /**
     * Promotes a Vec3 to a Vec4 with an extra component.<br>
     * <br>
     * Useful for multiplying a 4 x 4 matrix with either a 3D vector or a 3D point.
     * (For points, w is
     * 1.0; for vectors, w is 0.0 .)
     *
     * @param v3 the vector
     * @param w  the w component
     */
    public Vec4(final Vec3 v3, final float w) {
        this.set(v3, w);
    }

    /**
     * Constructs a vector from a source vector's components.
     *
     * @param v the source vector
     */
    public Vec4(final Vec4 v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.w = v.w;
    }

    /**
     * Finds the absolute value of each vector component.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the absolute vector
     * @see Utils#abs(float)
     */
    public static Vec4 abs(final Vec4 v, final Vec4 target) {

        return target.set(Utils.abs(v.x), Utils.abs(v.y), Utils.abs(v.z), Utils.abs(v.w));
    }

    /**
     * Adds two vectors together.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the sum
     */
    public static Vec4 add(final Vec4 a, final Vec4 b, final Vec4 target) {

        return target.set(a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
    }

    /**
     * Tests to see if all the vector's components are non-zero.
     *
     * @param v the input vector
     * @return the evaluation
     */
    public static boolean all(final Vec4 v) {

        return v.w != 0.0f && v.z != 0.0f && v.y != 0.0f && v.x != 0.0f;
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
    public static Vec4 and(final Vec4 a, final Vec4 b, final Vec4 target) {

        return target.set(
            Utils.and(a.x, b.x), Utils.and(a.y, b.y), Utils.and(a.z, b.z), Utils.and(a.w, b.w));
    }

    /**
     * Tests to see if any of the vector's components are non-zero.
     *
     * @param v the input vector
     * @return the evaluation
     */
    public static boolean any(final Vec4 v) {

        return v.w != 0.0f || v.z != 0.0f || v.y != 0.0f || v.x != 0.0f;
    }

    /**
     * Tests to see if two vectors approximate each other.
     *
     * @param a left comparisand
     * @param b right comparisand
     * @return the evaluation
     */
    public static boolean approx(final Vec4 a, final Vec4 b) {

        return Vec4.approx(a, b, Utils.EPSILON);
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
    public static boolean approx(final Vec4 a, final Vec4 b, final float tolerance) {

        return a == b
            || Utils.approx(a.w, b.w, tolerance)
            && Utils.approx(a.z, b.z, tolerance)
            && Utils.approx(a.y, b.y, tolerance)
            && Utils.approx(a.x, b.x, tolerance);
    }

    /**
     * Tests to see if a vector has, approximately, the specified magnitude.
     *
     * @param a the input vector
     * @param b the magnitude
     * @return the evaluation
     * @see Vec4#approxMag(Vec4, float, float)
     */
    public static boolean approxMag(final Vec4 a, final float b) {

        return Vec4.approxMag(a, b, Utils.EPSILON);
    }

    /**
     * Tests to see if a vector has, approximately, the specified magnitude.
     *
     * @param a         the input vector
     * @param b         the magnitude
     * @param tolerance the tolerance
     * @return the evaluation
     * @see Utils#approx(float, float, float)
     * @see Vec4#magSq(Vec4)
     */
    public static boolean approxMag(final Vec4 a, final float b, final float tolerance) {

        return Utils.approx(Vec4.magSq(a), b * b, tolerance);
    }

    /**
     * Returns to a vector with a negative value on the y-axis, (0.0, -1.0, 0.0,
     * 0.0) .
     *
     * @param target the output vector
     * @return the back vector
     */
    public static Vec4 back(final Vec4 target) {

        return target.set(0.0f, -1.0f, 0.0f, 0.0f);
    }

    /**
     * Returns a point on a Bézier curve described by two anchor points and two
     * control points
     * according to a step in [0.0, 1.0] . When the step is less than zero, returns
     * the first anchor
     * point. When the step is greater than one, returns the second anchor point.
     *
     * @param ap0    the first anchor point
     * @param cp0    the first control point
     * @param cp1    the second control point
     * @param ap1    the second anchor point
     * @param step   the step
     * @param target the output vector
     * @return the point along the curve
     */
    public static Vec4 bezierPoint(
        final Vec4 ap0,
        final Vec4 cp0,
        final Vec4 cp1,
        final Vec4 ap1,
        final float step,
        final Vec4 target) {

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
            ap0.z * ucb + cp0.z * usq3t + cp1.z * tsq3u + ap1.z * tcb,
            ap0.w * ucb + cp0.w * usq3t + cp1.w * tsq3u + ap1.w * tcb);
    }

    /**
     * Returns a tangent on a Bézier curve described by two anchor points and two
     * control points
     * according to a step in [0.0, 1.0] . When the step is less than zero, returns
     * the first anchor
     * point subtracted from the first control point. When the step is greater than
     * one, returns the
     * second anchor point subtracted from the second control point.
     *
     * @param ap0    the first anchor point
     * @param cp0    the first control point
     * @param cp1    the second control point
     * @param ap1    the second anchor point
     * @param step   the step
     * @param target the output vector
     * @return the tangent along the curve
     * @see Vec4#sub(Vec4, Vec4, Vec4)
     */
    public static Vec4 bezierTangent(
        final Vec4 ap0,
        final Vec4 cp0,
        final Vec4 cp1,
        final Vec4 ap1,
        final float step,
        final Vec4 target) {

        if (step <= 0.0f) {
            return Vec4.sub(cp0, ap0, target);
        }
        if (step >= 1.0f) {
            return Vec4.sub(ap1, cp1, target);
        }

        final float u = 1.0f - step;
        final float t3 = step + step + step;
        final float usq3 = u * (u + u + u);
        final float tsq3 = step * t3;
        final float ut6 = u * (t3 + t3);

        /* @formatter:off */
    return target.set(
        (cp0.x - ap0.x) * usq3 + (cp1.x - cp0.x) * ut6 + (ap1.x - cp1.x) * tsq3,
        (cp0.y - ap0.y) * usq3 + (cp1.y - cp0.y) * ut6 + (ap1.y - cp1.y) * tsq3,
        (cp0.z - ap0.z) * usq3 + (cp1.z - cp0.z) * ut6 + (ap1.z - cp1.z) * tsq3,
        (cp0.w - ap0.w) * usq3 + (cp1.w - cp0.w) * ut6 + (ap1.w - cp1.w) * tsq3);
    /* @formatter:on */
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
     * @see Vec4#bezierTangent(Vec4, Vec4, Vec4, Vec4, float, Vec4)
     * @see Utils#invSqrt(float)
     */
    public static Vec4 bezierTanUnit(
        final Vec4 ap0,
        final Vec4 cp0,
        final Vec4 cp1,
        final Vec4 ap1,
        final float step,
        final Vec4 target) {

        Vec4.bezierTangent(ap0, cp0, cp1, ap1, step, target);
        final float mInv = Utils.invSqrt(
            target.x * target.x + target.y * target.y + target.z * target.z + target.w * target.w);
        return target.set(target.x * mInv, target.y * mInv, target.z * mInv, target.w * mInv);
    }

    /**
     * Appends to an array of bytes, ordered from least to most significant digit
     * (little endian).
     * Writes 16 bytes.
     *
     * @param v   the vector
     * @param arr the array
     * @param i   the index
     * @return the byte array
     * @see Utils#byteslm(float, byte[], int)
     */
    public static byte[] byteslm(final Vec4 v, final byte[] arr, final int i) {

        Utils.byteslm(v.x, arr, i);
        Utils.byteslm(v.y, arr, i + 4);
        Utils.byteslm(v.z, arr, i + 8);
        Utils.byteslm(v.w, arr, i + 12);

        return arr;
    }

    /**
     * Appends to an array of bytes, ordered from most to least significant digit
     * (big endian). Writes
     * 16 bytes.
     *
     * @param v   the vector
     * @param arr the array
     * @param i   the index
     * @return the byte array
     * @see Utils#bytesml(float, byte[], int)
     */
    public static byte[] bytesml(final Vec4 v, final byte[] arr, final int i) {

        Utils.bytesml(v.x, arr, i);
        Utils.bytesml(v.y, arr, i + 4);
        Utils.bytesml(v.z, arr, i + 8);
        Utils.bytesml(v.w, arr, i + 12);

        return arr;
    }

    /**
     * Raises each component of the vector to the nearest greater integer.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the raised vector
     * @see Utils#ceil(float)
     */
    public static Vec4 ceil(final Vec4 v, final Vec4 target) {

        return target.set(Utils.ceil(v.x), Utils.ceil(v.y), Utils.ceil(v.z), Utils.ceil(v.w));
    }

    /**
     * Clamps the vector to a range in [0.0, 1.0].
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the clamped vector
     * @see Utils#clamp01(float)
     */
    public static Vec4 clamp01(final Vec4 v, final Vec4 target) {

        return target.set(
            Utils.clamp01(v.x), Utils.clamp01(v.y), Utils.clamp01(v.z), Utils.clamp01(v.w));
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
    public static Vec4 copySign(final Vec4 magnitude, final Vec4 sign, final Vec4 target) {

        return target.set(
            Utils.copySign(magnitude.x, sign.x),
            Utils.copySign(magnitude.y, sign.y),
            Utils.copySign(magnitude.z, sign.z),
            Utils.copySign(magnitude.w, sign.w));
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
    public static Vec4 diff(final Vec4 a, final Vec4 b, final Vec4 target) {

        return target.set(
            Utils.diff(a.x, b.x), Utils.diff(a.y, b.y), Utils.diff(a.z, b.z), Utils.diff(a.w, b.w));
    }

    /**
     * Finds the Euclidean distance between two vectors.
     *
     * @param a left operand
     * @param b right operand
     * @return the distance
     * @see Vec4#distEuclidean(Vec4, Vec4)
     */
    public static float dist(final Vec4 a, final Vec4 b) {

        return Vec4.distEuclidean(a, b);
    }

    /**
     * Finds the Chebyshev distance between two vectors.
     *
     * @param a left operand
     * @param b right operand
     * @return the distance
     * @see Utils#diff(float, float)
     */
    public static float distChebyshev(final Vec4 a, final Vec4 b) {

        final float xd = Utils.diff(a.x, b.x);
        final float yd = Utils.diff(a.y, b.y);
        final float zd = Utils.diff(a.z, b.z);
        final float wd = Utils.diff(a.w, b.w);

        final float c0 = Math.max(xd, yd);
        final float c1 = Math.max(c0, zd);
        return Math.max(c1, wd);
    }

    /**
     * Finds the Euclidean distance between two vectors. Where possible, use
     * distance squared to avoid
     * the computational cost of the square-root.
     *
     * @param a left operand
     * @param b right operand
     * @return the Euclidean distance
     * @see Vec4#distSq(Vec4, Vec4)
     * @see Utils#sqrtUnchecked(float)
     */
    public static float distEuclidean(final Vec4 a, final Vec4 b) {

        return Utils.sqrtUnchecked(Vec4.distSq(a, b));
    }

    /**
     * Finds the Manhattan distance between two vectors.
     *
     * @param a left operand
     * @param b right operand
     * @return the Manhattan distance
     * @see Utils#diff(float, float)
     */
    public static float distManhattan(final Vec4 a, final Vec4 b) {

        return Utils.diff(a.x, b.x)
            + Utils.diff(a.y, b.y)
            + Utils.diff(a.z, b.z)
            + Utils.diff(a.w, b.w);
    }

    /**
     * Finds the Minkowski distance between two vectors. This is a generalization of
     * other distance
     * formulae. When the exponent value, c, is 1.0, the Minkowski distance equals
     * the Manhattan
     * distance; when it is 2.0, Minkowski equals the Euclidean distance.
     *
     * @param a left operand
     * @param b right operand
     * @param c exponent
     * @return the Minkowski distance
     * @see Math#pow(double, double)
     * @see Math#abs(double)
     */
    public static float distMinkowski(final Vec4 a, final Vec4 b, final float c) {

        if (c != 0.0f) {
            /* @formatter:off */
      return (float)
          Math.pow(
              Math.pow(Math.abs((double) (b.x - a.x)), c)
                  + Math.pow(Math.abs((double) (b.y - a.y)), c)
                  + Math.pow(Math.abs((double) (b.z - a.z)), c)
                  + Math.pow(Math.abs((double) (b.w - a.w)), c),
              1.0d / (double) c);
      /* @formatter:on */
        }

        return 0.0f;
    }

    /**
     * Finds the Euclidean distance squared between two vectors. Equivalent to
     * subtracting one vector
     * from the other, then finding the dot product of the difference with itself.
     *
     * @param a left operand
     * @param b right operand
     * @return the distance squared
     */
    public static float distSq(final Vec4 a, final Vec4 b) {

        final float xDist = b.x - a.x;
        final float yDist = b.y - a.y;
        final float zDist = b.z - a.z;
        final float wDist = b.w - a.w;
        return xDist * xDist + yDist * yDist + zDist * zDist + wDist * wDist;
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
    public static Vec4 div(final float a, final Vec4 b, final Vec4 target) {

        return target.set(Utils.div(a, b.x), Utils.div(a, b.y), Utils.div(a, b.z), Utils.div(a, b.w));
    }

    /**
     * Divides a vector by a scalar.
     *
     * @param a      vector, numerator
     * @param b      scalar, denominator
     * @param target the output vector
     * @return the quotient
     */
    public static Vec4 div(final Vec4 a, final float b, final Vec4 target) {

        if (b != 0.0f) {
            final float denom = 1.0f / b;
            return target.set(a.x * denom, a.y * denom, a.z * denom, a.w * denom);
        }
        return target.reset();
    }

    /**
     * Divides the left operand by the right, component-wise.
     *
     * @param a      numerator
     * @param b      denominator
     * @param target the output vector
     * @return the quotient
     * @see Utils#div(float, float)
     */
    public static Vec4 div(final Vec4 a, final Vec4 b, final Vec4 target) {

        return target.set(
            Utils.div(a.x, b.x), Utils.div(a.y, b.y), Utils.div(a.z, b.z), Utils.div(a.w, b.w));
    }

    /**
     * Finds the dot product of two vectors by summing the products of their
     * corresponding components.
     * The dot product of a vector with itself is equal to its magnitude squared.
     *
     * @param a left operand
     * @param b right operand
     * @return the dot product
     */
    public static float dot(final Vec4 a, final Vec4 b) {

        return a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w;
    }

    /**
     * Returns to a vector with a negative value on the z axis, (0.0, 0.0, -1.0,
     * 0.0) .
     *
     * @param target the output vector
     * @return the down vector
     */
    public static Vec4 down(final Vec4 target) {

        return target.set(0.0f, 0.0f, -1.0f, 0.0f);
    }

    /**
     * Returns a vector with all components set to epsilon, a small positive
     * non-zero value.
     *
     * @param target the output vector
     * @return epsilon
     */
    public static Vec4 epsilon(final Vec4 target) {

        return target.set(Utils.EPSILON, Utils.EPSILON, Utils.EPSILON, Utils.EPSILON);
    }

    /**
     * Filters a vector by setting each component to the input component if it is in
     * bounds and 0.0 if
     * it is out of bounds.
     *
     * @param v      the vector
     * @param lb     the lower bound
     * @param ub     the upper bound
     * @param target the output vector
     * @return the filtered vector
     * @see Utils#filter(float, float, float)
     */
    public static Vec4 filter(final Vec4 v, final Vec4 lb, final Vec4 ub, final Vec4 target) {

        return target.set(
            Utils.filter(v.x, lb.x, ub.x),
            Utils.filter(v.y, lb.y, ub.y),
            Utils.filter(v.z, lb.z, ub.z),
            Utils.filter(v.w, lb.w, ub.w));
    }

    /**
     * Flattens a two-dimensional array of vectors to a one dimensional array.
     *
     * @param arr the 2D array
     * @return the 1D array
     * @see System#arraycopy(Object, int, Object, int, int)
     */
    public static Vec4[] flat(final Vec4[][] arr) {

        int totalLen = 0;
        for (Vec4[] vec4s : arr) {
            totalLen += vec4s.length;
        }

        /*
         * Copy each inner array to the result array, then move the cursor by the
         * length of each array.
         */
        int j = 0;
        final Vec4[] result = new Vec4[totalLen];
        for (final Vec4[] arrInner : arr) {
            final int len = arrInner.length;
            System.arraycopy(arrInner, 0, result, j, len);
            j += len;
        }
        return result;
    }

    /**
     * Flattens a three-dimensional array of vectors to a one dimensional array.
     *
     * @param arr the 3D array
     * @return the 1D array
     * @see System#arraycopy(Object, int, Object, int, int)
     */
    public static Vec4[] flat(final Vec4[][][] arr) {

        int totalLen = 0;
        for (final Vec4[][] arrInner : arr) {
            for (Vec4[] vec4s : arrInner) {
                totalLen += vec4s.length;
            }
        }

        int k = 0;
        final Vec4[] result = new Vec4[totalLen];
        for (final Vec4[][] arrInner1 : arr) {
            for (final Vec4[] arrInner2 : arrInner1) {
                final int sourceLen2 = arrInner2.length;
                System.arraycopy(arrInner2, 0, result, k, sourceLen2);
                k += sourceLen2;
            }
        }

        return result;
    }

    /**
     * Flattens a four dimensional array of vectors to a one dimensional array.
     *
     * @param arr the 4D array
     * @return the 1D array
     * @see System#arraycopy(Object, int, Object, int, int)
     */
    public static Vec4[] flat(final Vec4[][][][] arr) {

        int totalLen = 0;
        for (final Vec4[][][] arrInner1 : arr) {
            for (final Vec4[][] arrInner2 : arrInner1) {
                for (Vec4[] vec4s : arrInner2) {
                    totalLen += vec4s.length;
                }
            }
        }

        int m = 0;
        final Vec4[] result = new Vec4[totalLen];
        for (final Vec4[][][] arrInner1 : arr) {
            for (final Vec4[][] arrInner2 : arrInner1) {
                for (final Vec4[] arrInner3 : arrInner2) {
                    final int sourceLen3 = arrInner3.length;
                    System.arraycopy(arrInner3, 0, result, m, sourceLen3);
                    m += sourceLen3;
                }
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
    public static Vec4 floor(final Vec4 v, final Vec4 target) {

        return target.set(Utils.floor(v.x), Utils.floor(v.y), Utils.floor(v.z), Utils.floor(v.w));
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
    public static Vec4 fmod(final float a, final Vec4 b, final Vec4 target) {

        return target.set(
            Utils.fmod(a, b.x), Utils.fmod(a, b.y), Utils.fmod(a, b.z), Utils.fmod(a, b.w));
    }

    /**
     * Applies the % operator (truncation-based modulo) to each component of the
     * left operand.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the result
     */
    public static Vec4 fmod(final Vec4 a, final float b, final Vec4 target) {

        if (b != 0.0f) {
            return target.set(a.x % b, a.y % b, a.z % b, a.w % b);
        }
        return target.set(a);
    }

    /**
     * Applies the % operator (truncation-based modulo) to each component of the
     * left operand.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the result
     * @see Utils#fmod(float, float)
     */
    public static Vec4 fmod(final Vec4 a, final Vec4 b, final Vec4 target) {

        return target.set(
            Utils.fmod(a.x, b.x), Utils.fmod(a.y, b.y), Utils.fmod(a.z, b.z), Utils.fmod(a.w, b.w));
    }

    /**
     * Returns to a vector with a positive value on the y-axis, (0.0, 1.0, 0.0, 0.0)
     * .
     *
     * @param target the output vector
     * @return the forward vector
     */
    public static Vec4 forward(final Vec4 target) {

        return target.set(0.0f, 1.0f, 0.0f, 0.0f);
    }

    /**
     * Returns the fractional portion of the vector's components.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the fractional portion
     * @see Utils#fract(float)
     */
    public static Vec4 fract(final Vec4 v, final Vec4 target) {

        return target.set(Utils.fract(v.x), Utils.fract(v.y), Utils.fract(v.z), Utils.fract(v.w));
    }

    /**
     * Generates a 4D array of vectors. Defaults to the coordinate range of [-0.5,
     * 0.5] .
     *
     * @param res the resolution
     * @return the array
     * @see Vec4#grid(int, int, int, int)
     */
    public static Vec4[][][][] grid(final int res) {

        return Vec4.grid(res, res, res, res);
    }

    /**
     * Generates a 4D array of vectors. Defaults to the coordinate range of [-0.5,
     * 0.5] .
     *
     * @param cols   number of columns
     * @param rows   number of rows
     * @param layers number of layers
     * @param strata number of strata
     * @return the array
     * @see Vec4#grid(int, int, int, int, float, float, float, float, float, float,
     * float, float)
     */
    public static Vec4[][][][] grid(
        final int cols, final int rows, final int layers, final int strata) {

        return Vec4.grid(
            cols, rows, layers, strata, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f);
    }

    /**
     * Generates a 4D array of vectors.
     *
     * @param cols       number of columns
     * @param rows       number of rows
     * @param layers     number of layers
     * @param strata     number of strata
     * @param lowerBound the lower bound
     * @param upperBound the upper bound
     * @return the array
     * @see Vec4#grid(int, int, int, int, float, float, float, float, float, float,
     * float, float)
     */
    public static Vec4[][][][] grid(
        final int cols,
        final int rows,
        final int layers,
        final int strata,
        final float lowerBound,
        final float upperBound) {

        return Vec4.grid(
            cols,
            rows,
            layers,
            strata,
            lowerBound,
            lowerBound,
            lowerBound,
            lowerBound,
            upperBound,
            upperBound,
            upperBound,
            upperBound);
    }

    /**
     * Generates a 4D array of vectors.
     *
     * @param cols       number of columns
     * @param rows       number of rows
     * @param layers     number of layers
     * @param strata     number of strata
     * @param lowerBound the lower bound
     * @param upperBound the upper bound
     * @return the array
     * @see Vec4#grid(int, int, int, int, float, float, float, float, float, float,
     * float, float)
     */
    public static Vec4[][][][] grid(
        final int cols,
        final int rows,
        final int layers,
        final int strata,
        final Vec4 lowerBound,
        final Vec4 upperBound) {

        return Vec4.grid(
            cols,
            rows,
            layers,
            strata,
            lowerBound.x,
            lowerBound.y,
            lowerBound.z,
            lowerBound.w,
            upperBound.x,
            upperBound.y,
            upperBound.z,
            upperBound.w);
    }

    /**
     * Evaluates whether all components of the left comparisand are greater than
     * those of the right
     * comparisand.
     *
     * @param a left comparisand
     * @param b right comparisand
     * @return the evaluation
     */
    public static boolean gt(final Vec4 a, final Vec4 b) {

        return a.x > b.x && a.y > b.y && a.z > b.z && a.w > b.w;
    }

    /**
     * Evaluates whether the left comparisand is greater than the right comparisand.
     *
     * @param a      left comparisand
     * @param b      right comparisand
     * @param target the output vector
     * @return the evaluation
     */
    public static Vec4 gt(final Vec4 a, final Vec4 b, final Vec4 target) {

        return target.set(a.x > b.x, a.y > b.y, a.z > b.z, a.w > b.w);
    }

    /**
     * Evaluates whether all components of the left comparisand are greater than or
     * equal to those of
     * the right comparisand.
     *
     * @param a left comparisand
     * @param b right comparisand
     * @return the evaluation
     */
    public static boolean gtEq(final Vec4 a, final Vec4 b) {

        return a.x >= b.x && a.y >= b.y && a.z >= b.z && a.w >= b.w;
    }

    /**
     * Evaluates whether the left comparisand is greater than or equal to the right
     * comparisand.
     *
     * @param a      left comparisand
     * @param b      right comparisand
     * @param target the output vector
     * @return the evaluation
     */
    public static Vec4 gtEq(final Vec4 a, final Vec4 b, final Vec4 target) {

        return target.set(a.x >= b.x, a.y >= b.y, a.z >= b.z, a.w >= b.w);
    }

    /**
     * Multiplies two vectors, component-wise.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the product
     */
    public static Vec4 hadamard(final Vec4 a, final Vec4 b, final Vec4 target) {

        return target.set(a.x * b.x, a.y * b.y, a.z * b.z, a.w * b.w);
    }

    /**
     * Tests to see if the vector is on the unit hyper-sphere, i.e., has a magnitude
     * of approximately
     * 1.0.
     *
     * @param v the input vector
     * @return the evaluation
     * @see Utils#approx(float, float)
     * @see Vec4#magSq(Vec4)
     */
    public static boolean isUnit(final Vec4 v) {

        return Utils.approx(Vec4.magSq(v), 1.0f);
    }

    /**
     * Returns a vector with a negative value on the x-axis, (-1.0, 0.0, 0.0, 0.0).
     *
     * @param target the output vector
     * @return the left vector
     */
    public static Vec4 left(final Vec4 target) {

        return target.set(-1.0f, 0.0f, 0.0f, 0.0f);
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
    public static Vec4 limit(final Vec4 v, final float limit, final Vec4 target) {

        final float mSq = v.x * v.x + v.y * v.y + v.z * v.z + v.w * v.w;
        if (limit > 0.0f && mSq > limit * limit) {
            final float scalar = limit * Utils.invSqrtUnchecked(mSq);
            return target.set(v.x * scalar, v.y * scalar, v.z * scalar, v.w * scalar);
        }

        return target.set(v);
    }

    /**
     * Evaluates whether all components of the left comparisand are less than those
     * of the right
     * comparisand.
     *
     * @param a left comparisand
     * @param b right comparisand
     * @return the evaluation
     */
    public static boolean lt(final Vec4 a, final Vec4 b) {

        return a.x < b.x && a.y < b.y && a.z < b.z && a.w < b.w;
    }

    /**
     * Evaluates whether the left comparisand is less than the right comparisand.
     *
     * @param a      left comparisand
     * @param b      right comparisand
     * @param target the output vector
     * @return the evaluation
     */
    public static Vec4 lt(final Vec4 a, final Vec4 b, final Vec4 target) {

        return target.set(a.x < b.x, a.y < b.y, a.z < b.z, a.w < b.w);
    }

    /**
     * Evaluates whether all components of the left comparisand are less than or
     * equal to those of the
     * right comparisand.
     *
     * @param a left comparisand
     * @param b right comparisand
     * @return the evaluation
     */
    public static boolean ltEq(final Vec4 a, final Vec4 b) {

        return a.x <= b.x && a.y <= b.y && a.z <= b.z && a.w <= b.w;
    }

    /**
     * Evaluates whether the left comparisand is less than or equal to the right
     * comparisand.
     *
     * @param a      left comparisand
     * @param b      right comparisand
     * @param target the output vector
     * @return the evaluation
     */
    public static Vec4 ltEq(final Vec4 a, final Vec4 b, final Vec4 target) {

        return target.set(a.x <= b.x, a.y <= b.y, a.z <= b.z, a.w <= b.w);
    }

    /**
     * Finds the length, or magnitude, of a vector, |<em>a</em>| . Uses the formula
     * √ <em>a</em> ·
     * <em>a</em> . Where possible, use magSq or dot to avoid the computational cost
     * of the
     * square-root.
     *
     * @param v the input vector
     * @return the magnitude
     * @see Utils#sqrtUnchecked(float)
     */
    public static float mag(final Vec4 v) {

        return Utils.sqrtUnchecked(v.x * v.x + v.y * v.y + v.z * v.z + v.w * v.w);
    }

    /**
     * Finds the length-, or magnitude-, squared of a vector,
     * |<em>a</em>|<sup>2</sup>. Returns the
     * same result as <em>a</em> · <em>a</em> . Useful when calculating the lengths
     * of many vectors, *
     * to avoid the computational cost of the square-root.
     *
     * @param v the input vector
     * @return the magnitude squared
     * @see Vec4#dot(Vec4, Vec4)
     * @see Vec4#mag(Vec4)
     */
    public static float magSq(final Vec4 v) {

        return v.x * v.x + v.y * v.y + v.z * v.z + v.w * v.w;
    }

    /**
     * Maps an input vector from an original range to a target range.
     *
     * @param v        the input vector
     * @param lbOrigin lower bound of original range
     * @param ubOrigin upper bound of original range
     * @param lbDest   lower bound of destination range
     * @param ubDest   upper bound of destination range
     * @param target   the output vector
     * @return the mapped value
     * @see Utils#map(float, float, float, float, float)
     */
    public static Vec4 map(
        final Vec4 v,
        final Vec4 lbOrigin,
        final Vec4 ubOrigin,
        final Vec4 lbDest,
        final Vec4 ubDest,
        final Vec4 target) {

        return target.set(
            Utils.map(v.x, lbOrigin.x, ubOrigin.x, lbDest.x, ubDest.x),
            Utils.map(v.y, lbOrigin.y, ubOrigin.y, lbDest.y, ubDest.y),
            Utils.map(v.z, lbOrigin.z, ubOrigin.z, lbDest.z, ubDest.z),
            Utils.map(v.w, lbOrigin.w, ubOrigin.w, lbDest.w, ubDest.w));
    }

    /**
     * Sets the target vector to the maximum of the input vector and a lower bound.
     *
     * @param a          the input value
     * @param lowerBound the lower bound
     * @param target     the output vector
     * @return the maximum values
     */
    public static Vec4 max(final Vec4 a, final float lowerBound, final Vec4 target) {

        return target.set(
            Math.max(a.x, lowerBound),
            Math.max(a.y, lowerBound),
            Math.max(a.z, lowerBound),
            Math.max(a.w, lowerBound));
    }

    /**
     * Sets the target vector to the maximum components of the input vector and a
     * lower bound.
     *
     * @param a          the input vector
     * @param lowerBound the lower bound
     * @param target     the output vector
     * @return the maximum values
     */
    public static Vec4 max(final Vec4 a, final Vec4 lowerBound, final Vec4 target) {

        return target.set(
            Math.max(a.x, lowerBound.x),
            Math.max(a.y, lowerBound.y),
            Math.max(a.z, lowerBound.z),
            Math.max(a.w, lowerBound.w));
    }

    /**
     * Sets the target vector to the minimum components of the input vector and an
     * upper bound.
     *
     * @param a          the input value
     * @param upperBound the upper bound
     * @param target     the output vector
     * @return the minimum values
     */
    public static Vec4 min(final Vec4 a, final float upperBound, final Vec4 target) {

        return target.set(
            Math.min(a.x, upperBound),
            Math.min(a.y, upperBound),
            Math.min(a.z, upperBound),
            Math.min(a.w, upperBound));
    }

    /**
     * Sets the target vector to the minimum components of the input vector and an
     * upper bound.
     *
     * @param a          the input vector
     * @param upperBound the upper bound
     * @param target     the output vector
     * @return the minimal values
     */
    public static Vec4 min(final Vec4 a, final Vec4 upperBound, final Vec4 target) {

        return target.set(
            Math.min(a.x, upperBound.x),
            Math.min(a.y, upperBound.y),
            Math.min(a.z, upperBound.z),
            Math.min(a.w, upperBound.w));
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
    public static Vec4 mix(final Vec4 orig, final Vec4 dest, final float step, final Vec4 target) {

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
            u * orig.z + step * dest.z,
            u * orig.w + step * dest.w);
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
    public static Vec4 mod(final float a, final Vec4 b, final Vec4 target) {

        return target.set(Utils.mod(a, b.x), Utils.mod(a, b.y), Utils.mod(a, b.z), Utils.mod(a, b.w));
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
    public static Vec4 mod(final Vec4 a, final float b, final Vec4 target) {

        if (b != 0.0f) {
            return target.set(
                Utils.modUnchecked(a.x, b),
                Utils.modUnchecked(a.y, b),
                Utils.modUnchecked(a.z, b),
                Utils.modUnchecked(a.w, b));
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
    public static Vec4 mod(final Vec4 a, final Vec4 b, final Vec4 target) {

        return target.set(
            Utils.mod(a.x, b.x), Utils.mod(a.y, b.y), Utils.mod(a.z, b.z), Utils.mod(a.w, b.w));
    }

    /**
     * A specialized form of modulo which subtracts the floor of the vector from the
     * vector.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the result
     * @see Utils#mod1(float)
     */
    public static Vec4 mod1(final Vec4 v, final Vec4 target) {

        return target.set(Utils.mod1(v.x), Utils.mod1(v.y), Utils.mod1(v.z), Utils.mod1(v.w));
    }

    /**
     * Multiplies a vector by a scalar.
     *
     * @param a      left operand, the scalar
     * @param b      right operand, the vector
     * @param target the output vector
     * @return the product
     */
    public static Vec4 mul(final float a, final Vec4 b, final Vec4 target) {

        return target.set(a * b.x, a * b.y, a * b.z, a * b.w);
    }

    /**
     * Multiplies a vector by a scalar.
     *
     * @param a      left operand, the vector
     * @param b      right operand, the scalar
     * @param target the output vector
     * @return the product
     */
    public static Vec4 mul(final Vec4 a, final float b, final Vec4 target) {

        return target.set(a.x * b, a.y * b, a.z * b, a.w * b);
    }

    /**
     * Negates the input vector.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the negation
     */
    public static Vec4 negate(final Vec4 v, final Vec4 target) {

        return target.set(-v.x, -v.y, -v.z, -v.w);
    }

    /**
     * Returns a vector with all components set to negative one.
     *
     * @param target the output vector
     * @return negative one
     */
    public static Vec4 negOne(final Vec4 target) {

        return target.set(-1.0f, -1.0f, -1.0f, -1.0f);
    }

    /**
     * Tests to see if all the vector's components are zero.
     *
     * @param v the input vector
     * @return the evaluation
     */
    public static boolean none(final Vec4 v) {

        return v.w == 0.0f && v.z == 0.0f && v.y == 0.0f && v.x == 0.0f;
    }

    /**
     * Divides a vector by its magnitude, such that the new magnitude is 1.0.
     * <em>â</em> = <em>a</em>
     * / |<em>a</em>|. The result is a unit vector, as it lies on the unit
     * hyper-sphere.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the unit vector
     * @see Utils#invSqrt(float)
     */
    public static Vec4 normalize(final Vec4 v, final Vec4 target) {

        final float mInv = Utils.invSqrt(v.x * v.x + v.y * v.y + v.z * v.z + v.w * v.w);
        return target.set(v.x * mInv, v.y * mInv, v.z * mInv, v.w * mInv);
    }

    /**
     * Evaluates a vector like a boolean, where n != 0.0 is true.
     *
     * @param v      the vector
     * @param target the output vector
     * @return the truth table opposite
     */
    public static Vec4 not(final Vec4 v, final Vec4 target) {

        return target.set(
            v.x != 0.0f ? 0.0f : 1.0f,
            v.y != 0.0f ? 0.0f : 1.0f,
            v.z != 0.0f ? 0.0f : 1.0f,
            v.w != 0.0f ? 0.0f : 1.0f);
    }

    /**
     * Returns a vector with both components set to one.
     *
     * @param target the output vector
     * @return one
     */
    public static Vec4 one(final Vec4 target) {

        return target.set(1.0f, 1.0f, 1.0f, 1.0f);
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
    public static Vec4 or(final Vec4 a, final Vec4 b, final Vec4 target) {

        return target.set(
            Utils.or(a.x, b.x), Utils.or(a.y, b.y), Utils.or(a.z, b.z), Utils.or(a.w, b.w));
    }

    /**
     * Oscillates between an origin and destination vector based on an input step
     * and a pause factor.
     * When the pause is greater than 1.0, the value will be clamped to the pole.
     *
     * @param origin the original vector
     * @param dest   the destination vector
     * @param step   the step
     * @param pause  the pause factor
     * @param target the output vector
     * @return the oscillation
     */
    public static Vec4 pingPong(
        final Vec4 origin, final Vec4 dest, final float step, final float pause, final Vec4 target) {

        final float t = 0.5f + 0.5f * pause * Utils.scNorm(step - 0.5f);
        if (t <= 0.0f) {
            return target.set(origin);
        }
        if (t >= 1.0f) {
            return target.set(dest);
        }
        final float u = 1.0f - t;
        return target.set(
            u * origin.x + t * dest.x,
            u * origin.y + t * dest.y,
            u * origin.z + t * dest.z,
            u * origin.w + t * dest.w);
    }

    /**
     * Oscillates between an origin and destination vector based on an input step.
     *
     * @param origin the original vector
     * @param dest   the destination vector
     * @param step   the step
     * @param target the output vector
     * @return the oscillation
     */
    public static Vec4 pingPong(
        final Vec4 origin, final Vec4 dest, final float step, final Vec4 target) {

        return Vec4.pingPong(origin, dest, step, 1.0f, target);
    }

    /**
     * Projects one vector onto another.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the projection
     * @see Vec4#projectVector(Vec4, Vec4, Vec4)
     */
    public static Vec4 project(final Vec4 a, final Vec4 b, final Vec4 target) {

        return Vec4.projectVector(a, b, target);
    }

    /**
     * Returns the scalar projection of <em>a</em> onto <em>b</em>. Defined as<br>
     * <br>
     * project ( <em>a</em>, <em>b</em> ) := <em>a</em> · <em>b</em> / <em>b</em> ·
     * <em>b</em>
     *
     * @param a left operand
     * @param b right operand
     * @return the scalar projection
     */
    public static float projectScalar(final Vec4 a, final Vec4 b) {

        final float bSq = b.x * b.x + b.y * b.y + b.z * b.z + b.w * b.w;
        if (bSq > 0.0f) {
            return (a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w) / bSq;
        }
        return 0.0f;
    }

    /**
     * Projects one vector onto another. Defined as<br>
     * <br>
     * project ( <em>a</em>, <em>b</em> ) := <em>b</em> ( <em>a</em> · <em>b</em> /
     * <em>b</em> ·
     * <em>b</em> )
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the projection
     * @see Vec3#projectScalar(Vec3, Vec3)
     * @see Vec3#mul(Vec3, float, Vec3)
     */
    public static Vec4 projectVector(final Vec4 a, final Vec4 b, final Vec4 target) {

        return Vec4.mul(b, Vec4.projectScalar(a, b), target);
    }

    /**
     * Reduces the signal, or granularity, of a vector's components. A level of zero
     * will copy the
     * input vector to the target.
     *
     * @param v      the input vector
     * @param levels the levels
     * @param target the output vector
     * @return the quantized vector
     * @see Utils#quantizeSigned(float, float, float)
     */
    public static Vec4 quantize(final Vec4 v, final int levels, final Vec4 target) {

        if (levels == 0) {
            return target.set(v);
        }
        final float levf = levels < 0 ? -levels : levels;
        final float delta = 1.0f / levf;
        return target.set(
            Utils.quantizeSigned(v.x, levf, delta),
            Utils.quantizeSigned(v.y, levf, delta),
            Utils.quantizeSigned(v.z, levf, delta),
            Utils.quantizeSigned(v.w, levf, delta));
    }

    /**
     * Generates a random vector.
     *
     * @param rng    the random number generator
     * @param target the output vector
     * @return the vector
     * @see Vec4#randomSpherical(Random, float, float, Vec4)
     */
    public static Vec4 random(final Random rng, final Vec4 target) {

        return Vec4.randomSpherical(rng, 1.0f, 1.0f, target);
    }

    /**
     * Creates a random point in the Cartesian coordinate system given a lower and
     * an upper bound.
     *
     * @param rng        the random number generator
     * @param lowerBound the lower bound
     * @param upperBound the upper bound
     * @param target     the output vector
     * @return the random vector
     */
    public static Vec4 randomCartesian(
        final Random rng, final float lowerBound, final float upperBound, final Vec4 target) {

        final float rx = rng.nextFloat();
        final float ry = rng.nextFloat();
        final float rz = rng.nextFloat();
        final float rw = rng.nextFloat();
        return target.set(
            (1.0f - rx) * lowerBound + rx * upperBound,
            (1.0f - ry) * lowerBound + ry * upperBound,
            (1.0f - rz) * lowerBound + rz * upperBound,
            (1.0f - rw) * lowerBound + rw * upperBound);
    }

    /**
     * Generates a random vector in a rectilinear coordinate system given a lower
     * and an upper bound.
     *
     * @param rng        the random number generator
     * @param lowerBound the lower bound
     * @param upperBound the upper bound
     * @param target     the output vector
     * @return the random vector
     */
    public static Vec4 randomCartesian(
        final Random rng, final Vec4 lowerBound, final Vec4 upperBound, final Vec4 target) {

        final float rx = rng.nextFloat();
        final float ry = rng.nextFloat();
        final float rz = rng.nextFloat();
        final float rw = rng.nextFloat();
        return target.set(
            (1.0f - rx) * lowerBound.x + rx * upperBound.x,
            (1.0f - ry) * lowerBound.y + ry * upperBound.y,
            (1.0f - rz) * lowerBound.z + rz * upperBound.z,
            (1.0f - rw) * lowerBound.w + rw * upperBound.w);
    }

    /**
     * Generates a random coordinate on a sphere. Uses the same formula as that for
     * a random
     * quaternion.
     *
     * @param rng    the random number generator
     * @param rhoMin the minimum radius
     * @param rhoMax the maximum radius
     * @param target the output vector
     * @return the vector
     */
    public static Vec4 randomSpherical(
        final Random rng, final float rhoMin, final float rhoMax, final Vec4 target) {

        final double rr = rng.nextDouble();
        final double rho = (1.0d - rr) * rhoMin + rr * rhoMax;
        final double t0 = Utils.TAU_D * rng.nextDouble();
        final double t1 = Utils.TAU_D * rng.nextDouble();
        final double r1 = rng.nextDouble();
        final double x0 = rho * Math.sqrt(1.0d - r1);
        final double x1 = rho * Math.sqrt(r1);
        return target.set(
            (float) (x0 * Math.sin(t0)),
            (float) (x0 * Math.cos(t0)),
            (float) (x1 * Math.sin(t1)),
            (float) (x1 * Math.cos(t1)));
    }

    /**
     * Normalizes a vector, then multiplies it by a scalar, in effect setting its
     * magnitude to that
     * scalar.
     *
     * @param v      the vector
     * @param scalar the scalar
     * @param target the output vector
     * @return the rescaled vector
     * @see Utils#invSqrtUnchecked(float)
     */
    public static Vec4 rescale(final Vec4 v, final float scalar, final Vec4 target) {

        final float msq = v.x * v.x + v.y * v.y + v.z * v.z + v.w * v.w;
        if (scalar != 0.0f && msq > 0.0f) {
            final float sclMg = scalar * Utils.invSqrtUnchecked(msq);
            return target.set(v.x * sclMg, v.y * sclMg, v.z * sclMg, v.w * sclMg);
        }
        return target.reset();
    }

    /**
     * Normalizes a vector, then multiplies it by a scalar, in effect setting its
     * magnitude to that
     * scalar.
     *
     * @param v          the vector
     * @param scalar     the scalar
     * @param target     the output vector
     * @param normalized the normalized vector
     * @return the rescaled vector
     * @see Vec4#normalize(Vec4, Vec4)
     * @see Vec4#mul(Vec4, float, Vec4)
     */
    public static Vec4 rescale(
        final Vec4 v, final float scalar, final Vec4 target, final Vec4 normalized) {

        if (scalar != 0.0f) {
            Vec4.normalize(v, normalized);
            return Vec4.mul(normalized, scalar, target);
        }
        normalized.reset();
        return target.reset();
    }

    /**
     * Returns to a vector with a positive value on the x-axis, (1.0, 0.0, 0.0, 0.0)
     * .
     *
     * @param target the output vector
     * @return the right vector
     */
    public static Vec4 right(final Vec4 target) {

        return target.set(1.0f, 0.0f, 0.0f, 0.0f);
    }

    /**
     * Rounds each component of the vector to the nearest whole number.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the rounded vector
     * @see Utils#round(float)
     */
    public static Vec4 round(final Vec4 v, final Vec4 target) {

        return target.set(Utils.round(v.x), Utils.round(v.y), Utils.round(v.z), Utils.round(v.w));
    }

    /**
     * Finds the sign of the vector: -1, if negative; 1, if positive.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the sign
     */
    public static Vec4 sign(final Vec4 v, final Vec4 target) {

        /* @formatter:off */
    return target.set(
        v.x < -0.0f ? -1.0f : v.x > 0.0f ? 1.0f : 0.0f,
        v.y < -0.0f ? -1.0f : v.y > 0.0f ? 1.0f : 0.0f,
        v.z < -0.0f ? -1.0f : v.z > 0.0f ? 1.0f : 0.0f,
        v.w < -0.0f ? -1.0f : v.w > 0.0f ? 1.0f : 0.0f);
    /* @formatter:on */
    }

    /**
     * Subtracts the right vector from the left vector.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the difference
     */
    public static Vec4 sub(final Vec4 a, final Vec4 b, final Vec4 target) {

        return target.set(a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
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
    public static StringBuilder toString(final StringBuilder sb, final Vec4[] arr, final int places) {

        sb.append('[');

        if (arr != null) {
            final int len = arr.length;
            final int last = len - 1;

            for (int i = 0; i < last; ++i) {
                final Vec4 v = arr[i];
                v.toString(sb, places);
                sb.append(',');
            }

            final Vec4 vl = arr[last];
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
    public static String toString(final Vec4[] arr) {

        return Vec4.toString(arr, Utils.FIXED_PRINT);
    }

    /**
     * Returns a string representation of an array of vectors.
     *
     * @param arr    the array
     * @param places the print precision
     * @return the string
     */
    public static String toString(final Vec4[] arr, final int places) {

        return Vec4.toString(new StringBuilder(1024), arr, places).toString();
    }

    /**
     * Truncates each component of the vector.
     *
     * @param v      the input vector
     * @param target the output vector
     * @return the truncation
     */
    public static Vec4 trunc(final Vec4 v, final Vec4 target) {

        return target.set((int) v.x, (int) v.y, (int) v.z, (int) v.w);
    }

    /**
     * Returns to a vector with a positive value on the z axis, (0.0, 0.0, 1.0, 0.0)
     * .
     *
     * @param target the output vector
     * @return the up vector
     */
    public static Vec4 up(final Vec4 target) {

        return target.set(0.0f, 0.0f, 1.0f, 0.0f);
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
    public static Vec4 wrap(final Vec4 v, final Vec4 lb, final Vec4 ub, final Vec4 target) {

        return target.set(
            Utils.wrap(v.x, lb.x, ub.x),
            Utils.wrap(v.y, lb.y, ub.y),
            Utils.wrap(v.z, lb.z, ub.z),
            Utils.wrap(v.w, lb.w, ub.w));
    }

    /**
     * Evaluates two vectors like booleans, using the exclusive or (XOR) logic gate.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output vector
     * @return the evaluation
     * @see Utils#xor(float, float)
     */
    public static Vec4 xor(final Vec4 a, final Vec4 b, final Vec4 target) {

        return target.set(
            Utils.xor(a.x, b.x), Utils.xor(a.y, b.y), Utils.xor(a.z, b.z), Utils.xor(a.w, b.w));
    }

    /**
     * Returns a vector with all components set to zero.
     *
     * @param target the output vector
     * @return the zero vector
     */
    public static Vec4 zero(final Vec4 target) {

        return target.set(0.0f, 0.0f, 0.0f, 0.0f);
    }

    /**
     * Generates a 4D array of vectors. The order of the arrays is the reverse of
     * the order in which
     * parameters are supplied: strata, layers, columns then rows.<br>
     * <br>
     * This is separated to make overriding the public grid functions easier. This
     * is protected
     * because it is too easy for integers to be quietly promoted to floats if the
     * signature
     * parameters are confused.
     *
     * @param cols   number of columns
     * @param rows   number of rows
     * @param layers number of layers
     * @param strata number of strata
     * @param lbx    lower bound x
     * @param lby    lower bound y
     * @param lbz    lower bound z
     * @param lbw    lower bound w
     * @param ubx    upper bound x
     * @param uby    upper bound y
     * @param ubz    upper bound z
     * @param ubw    upper bound w
     * @return the array
     */
    protected static Vec4[][][][] grid(
        final int cols,
        final int rows,
        final int layers,
        final int strata,
        final float lbx,
        final float lby,
        final float lbz,
        final float lbw,
        final float ubx,
        final float uby,
        final float ubz,
        final float ubw) {

        final int sVrf = Math.max(strata, 1);
        final int lVrf = Math.max(layers, 1);
        final int rVrf = Math.max(rows, 1);
        final int cVrf = Math.max(cols, 1);

        final Vec4[][][][] result = new Vec4[sVrf][lVrf][rVrf][cVrf];

        final boolean sOne = sVrf == 1;
        final boolean lOne = lVrf == 1;
        final boolean rOne = rVrf == 1;
        final boolean cOne = cVrf == 1;

        final float gToStep = sOne ? 0.0f : 1.0f / (sVrf - 1.0f);
        final float hToStep = lOne ? 0.0f : 1.0f / (lVrf - 1.0f);
        final float iToStep = rOne ? 0.0f : 1.0f / (rVrf - 1.0f);
        final float jToStep = cOne ? 0.0f : 1.0f / (cVrf - 1.0f);

        final float gOff = sOne ? 0.5f : 0.0f;
        final float hOff = lOne ? 0.5f : 0.0f;
        final float iOff = rOne ? 0.5f : 0.0f;
        final float jOff = cOne ? 0.5f : 0.0f;

        final int rcVal = rVrf * cVrf;
        final int lrcVal = lVrf * rcVal;
        final int len = sVrf * lrcVal;
        for (int k = 0; k < len; ++k) {
            final int g = k / lrcVal;
            final int m = k - g * lrcVal;
            final int h = m / rcVal;
            final int n = m - h * rcVal;
            final int i = n / cVrf;
            final int j = n % cVrf;

            final float gStep = g * gToStep + gOff;
            final float hStep = h * hToStep + hOff;
            final float iStep = i * iToStep + iOff;
            final float jStep = j * jToStep + jOff;

            result[g][h][i][j] = new Vec4(
                (1.0f - jStep) * lbx + jStep * ubx,
                (1.0f - iStep) * lby + iStep * uby,
                (1.0f - hStep) * lbz + hStep * ubz,
                (1.0f - gStep) * lbw + gStep * ubw);
        }

        return result;
    }

    /**
     * Returns -1 when this vector is less than the comparisand; 1 when it is
     * greater than; 0 when the
     * two are 'equal'. The implementation of this method allows collections of
     * vectors to be sorted.
     *
     * @param v the comparisand
     * @return the numeric code
     */
    @Override
    public int compareTo(final Vec4 v) {

        /* @formatter:off */
    return this.w < v.w ? -1
            : this.w > v.w ? 1
            : this.z < v.z ? -1
            : this.z > v.z ? 1
            : this.y < v.y ? -1
            : this.y > v.y ? 1
            : this.x < v.x ? -1
            : this.x > v.x ? 1
            : 0;
    /* @formatter:on */
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        return this.equals((Vec4) o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(w, x, y, z);
    }

    /**
     * Resets this vector to an initial state, ( 0.0, 0.0, 0.0, 0.0 ) .
     *
     * @return this vector
     */
    public Vec4 reset() {
        return this.set(0.0f, 0.0f, 0.0f, 0.0f);
    }

    /**
     * Sets the components of this vector from booleans, where false is 0.0 and true
     * is 1.0 .
     *
     * @param x the x component
     * @param y the y component
     * @param z the z component
     * @param w the w component
     * @return this vector
     */
    public Vec4 set(final boolean x, final boolean y, final boolean z, final boolean w) {

        this.x = x ? 1.0f : 0.0f;
        this.y = y ? 1.0f : 0.0f;
        this.z = z ? 1.0f : 0.0f;
        this.w = w ? 1.0f : 0.0f;
        return this;
    }

    /**
     * Sets the components of this vector.
     *
     * @param x the x component
     * @param y the y component
     * @param z the z component
     * @param w the w component
     * @return this vector
     */
    public Vec4 set(final float x, final float y, final float z, final float w) {

        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    /**
     * Promotes a Vec2 to a Vec4.
     *
     * @param v the source vector
     * @return this vector
     */
    public Vec4 set(final Vec2 v) {

        return this.set(v.x, v.y, 0.0f, 0.0f);
    }

    /**
     * Promotes a Vec3 to a Vec4.
     *
     * @param v the source vector
     * @return this vector
     */
    public Vec4 set(final Vec3 v) {
        return this.set(v.x, v.y, v.z, 0.0f);
    }

    /**
     * Promotes a Vec3 to a Vec4 with an extra component.<br>
     * <br>
     * Useful for multiplying a 4 x 4 matrix with either a 3D vector or a 3D point.
     * (For points, w is
     * 1.0; for vectors, w is 0.0 .)
     *
     * @param v the source vector
     * @param w the w component
     * @return this vector
     */
    public Vec4 set(final Vec3 v, final float w) {

        return this.set(v.x, v.y, v.z, w);
    }

    /**
     * Copies the components of the source vector to this vector.
     *
     * @param v the source vector
     * @return this vector
     */
    public Vec4 set(final Vec4 v) {
        return this.set(v.x, v.y, v.z, v.w);
    }

    /**
     * Returns a float array of length 4 containing this vector's components.
     *
     * @return the array
     */
    public float[] toArray() {
        return this.toArray(new float[4], 0);
    }

    /**
     * Puts the vector's components into an existing array at the index provided.
     * The vector's x
     * component is assigned to element <code>i</code>; its y component, to element
     * <code>i + 1</code>
     * ; its z component, to element <code>i + 2</code>; its w component, to element
     * <code>i + 3
     * </code>.
     *
     * @param arr the array
     * @param i   the index
     * @return the array
     */
    public float[] toArray(final float[] arr, final int i) {

        arr[i] = this.x;
        arr[i + 1] = this.y;
        arr[i + 2] = this.z;
        arr[i + 3] = this.w;
        return arr;
    }

    /**
     * Returns a string representation of this vector according to the string
     * format.
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
     * Internal helper function to assist with methods that need to print many
     * vectors. Appends to an
     * existing {@link StringBuilder}.
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
        sb.append(",\"w\":");
        Utils.toFixed(sb, this.w, places);
        sb.append('}');
        return sb;
    }

    /**
     * Tests equivalence between this and another vector.
     *
     * @param v the vector
     * @return the evaluation
     * @see Float#floatToIntBits(float)
     */
    protected boolean equals(final Vec4 v) {

        /* With {@link Float.floatToIntBits(float)}, -0.0f != 0.0f. */
        return this.w == v.w && this.z == v.z && this.y == v.y && this.x == v.x;
    }

    /**
     * An abstract class that may serve as an umbrella for any custom comparators of
     * Vec4 s.
     */
    public abstract static class AbstrComparator implements Comparator<Vec4> {

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
    public abstract static class AbstrEasing implements Utils.EasingFuncObj<Vec4> {

        /**
         * The default constructor.
         */
        protected AbstrEasing() {
        }

        /**
         * A clamped interpolation between the origin and destination. Defers to an
         * unclamped
         * interpolation, which is to be defined by subclasses of this class.
         *
         * @param orig   the origin vector
         * @param dest   the destination vector
         * @param step   a factor in [0.0, 1.0]
         * @param target the output vector
         * @return the eased vector
         */
        @Override
        public Vec4 apply(final Vec4 orig, final Vec4 dest, final Float step, final Vec4 target) {

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
         * @param origin the origin vector
         * @param dest   the destination vector
         * @param step   a factor in [0.0, 1.0]
         * @param target the output vector
         * @return the eased vector
         */
        public abstract Vec4 applyUnclamped(
            final Vec4 origin, final Vec4 dest, final float step, Vec4 target);

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
         * Eases between two vectors by a step using the formula ( 1.0 - <em>t</em> )
         * <em>a</em> +
         * <em>t</em> <em>b</em>.
         *
         * @param origin the origin vector
         * @param dest   the destination vector
         * @param step   the step
         * @param target the output vector
         * @return the result
         */
        @Override
        public Vec4 applyUnclamped(
            final Vec4 origin, final Vec4 dest, final float step, final Vec4 target) {

            final float uf = 1.0f - step;
            return target.set(
                uf * origin.x + step * dest.x, uf * origin.y + step * dest.y,
                uf * origin.z + step * dest.z, uf * origin.w + step * dest.w);
        }
    }
}
