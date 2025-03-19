package com.behreajj.camzup.core;

import java.util.Objects;

/**
 * A mutable, extensible class influenced by GLSL, OSL and PMatrix2D. Although
 * this is a 3 x 3
 * matrix, it is assumed to be a 2D affine transform matrix, where the last row
 * is (0.0, 0.0, 1.0) .
 * Instance methods are limited, while most static methods require an explicit
 * output variable to be
 * provided.
 */
public class Mat3 {

    /**
     * Component in row 0, column 0. The right axis x component.
     */
    public float m00 = 1.0f;

    /**
     * Component in row 0, column 1. The forward axis x component.
     */
    public float m01 = 0.0f;

    /**
     * Component in row 0, column 2. The translation x component.
     */
    public float m02 = 0.0f;

    /**
     * Component in row 1, column 0. The right axis y component.
     */
    public float m10 = 0.0f;

    /**
     * Component in row 1, column 1. The forward axis y component.
     */
    public float m11 = 1.0f;

    /**
     * Component in row 1, column 2. The translation y component.
     */
    public float m12 = 0.0f;

    /**
     * Component in row 2, column 0. The right axis z component.
     */
    public float m20 = 0.0f;

    /**
     * Component in row 2, column 1. The forward axis z component.
     */
    public float m21 = 0.0f;

    /**
     * Component in row 2, column 2. The translation z component.
     */
    public float m22 = 1.0f;

    /**
     * The default constructor. Creates an identity matrix.
     */
    public Mat3() {
    }

    /**
     * Constructs a matrix from boolean values.
     *
     * @param m00 row 0, column 0
     * @param m01 row 0, column 1
     * @param m02 row 0, column 2
     * @param m10 row 1, column 0
     * @param m11 row 1, column 1
     * @param m12 row 1, column 2
     * @param m20 row 2, column 0
     * @param m21 row 2, column 1
     * @param m22 row 2, column 2
     */
    public Mat3(
        final boolean m00,
        final boolean m01,
        final boolean m02,
        final boolean m10,
        final boolean m11,
        final boolean m12,
        final boolean m20,
        final boolean m21,
        final boolean m22) {

        this.set(m00, m01, m02, m10, m11, m12, m20, m21, m22);
    }

    /**
     * Constructs a matrix from float values.
     *
     * @param m00 row 0, column 0
     * @param m01 row 0, column 1
     * @param m10 row 1, column 0
     * @param m11 row 1, column 1
     */
    public Mat3(final float m00, final float m01, final float m10, final float m11) {

        this.set(m00, m01, m10, m11);
    }

    /**
     * Constructs a matrix from float values.
     *
     * @param m00 row 0, column 0
     * @param m01 row 0, column 1
     * @param m02 row 0, column 2
     * @param m10 row 1, column 0
     * @param m11 row 1, column 1
     * @param m12 row 1, column 2
     */
    public Mat3(
        final float m00,
        final float m01,
        final float m02,
        final float m10,
        final float m11,
        final float m12) {

        this.set(m00, m01, m02, m10, m11, m12);
    }

    /**
     * Constructs a matrix from float values.
     *
     * @param m00 row 0, column 0
     * @param m01 row 0, column 1
     * @param m02 row 0, column 2
     * @param m10 row 1, column 0
     * @param m11 row 1, column 1
     * @param m12 row 1, column 2
     * @param m20 row 2, column 0
     * @param m21 row 2, column 1
     * @param m22 row 2, column 2
     */
    public Mat3(
        final float m00,
        final float m01,
        final float m02,
        final float m10,
        final float m11,
        final float m12,
        final float m20,
        final float m21,
        final float m22) {

        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
    }

    /**
     * Constructs a matrix from a source matrix's components.
     *
     * @param m the source matrix
     */
    public Mat3(final Mat3 m) {

        this.m00 = m.m00;
        this.m01 = m.m01;
        this.m02 = m.m02;
        this.m10 = m.m10;
        this.m11 = m.m11;
        this.m12 = m.m12;
        this.m20 = m.m20;
        this.m21 = m.m21;
        this.m22 = m.m22;
    }

    /**
     * Adds two matrices together.
     *
     * @param a      the left operand
     * @param b      the right operand
     * @param target the output matrix
     * @return the sum
     */
    public static Mat3 add(final Mat3 a, final Mat3 b, final Mat3 target) {

        /* @formatter:off */
    target.m00 = a.m00 + b.m00;
    target.m01 = a.m01 + b.m01;
    target.m02 = a.m02 + b.m02;
    target.m10 = a.m10 + b.m10;
    target.m11 = a.m11 + b.m11;
    target.m12 = a.m12 + b.m12;
    target.m20 = a.m20 + b.m20;
    target.m21 = a.m21 + b.m21;
    target.m22 = a.m22 + b.m22;

    return target;
    /* @formatter:on */
    }

    /**
     * Tests to see if all the matrix's components are non-zero.
     *
     * @param m the input matrix
     * @return the evaluation
     */
    public static boolean all(final Mat3 m) {

        return m.m00 != 0.0f
            && m.m01 != 0.0f
            && m.m02 != 0.0f
            && m.m10 != 0.0f
            && m.m11 != 0.0f
            && m.m12 != 0.0f
            && m.m20 != 0.0f
            && m.m21 != 0.0f
            && m.m22 != 0.0f;
    }

    /**
     * Evaluates two matrices like booleans, using the AND logic gate.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output matrix
     * @return the evaluation
     * @see Utils#and(float, float)
     */
    public static Mat3 and(final Mat3 a, final Mat3 b, final Mat3 target) {

        target.m00 = (float) Utils.and(a.m00, b.m00);
        target.m01 = (float) Utils.and(a.m01, b.m01);
        target.m02 = (float) Utils.and(a.m02, b.m02);
        target.m10 = (float) Utils.and(a.m10, b.m10);
        target.m11 = (float) Utils.and(a.m11, b.m11);
        target.m12 = (float) Utils.and(a.m12, b.m12);
        target.m20 = (float) Utils.and(a.m20, b.m20);
        target.m21 = (float) Utils.and(a.m21, b.m21);
        target.m22 = (float) Utils.and(a.m22, b.m22);

        return target;
    }

    /**
     * Tests to see if any of the matrix's components are non-zero.
     *
     * @param m the input matrix
     * @return the evaluation
     */
    public static boolean any(final Mat3 m) {

        return m.m00 != 0.0f
            || m.m01 != 0.0f
            || m.m02 != 0.0f
            || m.m10 != 0.0f
            || m.m11 != 0.0f
            || m.m12 != 0.0f
            || m.m20 != 0.0f
            || m.m21 != 0.0f
            || m.m22 != 0.0f;
    }

    /**
     * Decomposes a matrix into its constituent transforms: translation, rotation
     * and scale. Rotation
     * is returned from the function, while translation and scale are loaded into
     * out parameters.
     *
     * @param m     the matrix
     * @param trans the output translation
     * @param scale the output scale
     * @return the rotation
     */
    public static float decompose(final Mat3 m, final Vec2 trans, final Vec2 scale) {

        final float xMag = Utils.hypot(m.m00, m.m10);
        final float yMag = Utils.hypot(m.m01, m.m11);
        final float det = Mat3.determinant(m);
        scale.set(xMag, det < 0.0f ? -yMag : yMag);
        trans.set(m.m02, m.m12);
        return Utils.modRadians(Utils.atan2(m.m10, m.m00));
    }

    /**
     * Finds the determinant of the matrix. Equivalent to the scalar triple product
     * of the matrix's
     * rows or columns.<br>
     * <br>
     * det ( <em>m</em> ) := <em>m<sub>i</sub></em> . ( <em>m<sub>j</sub></em> x
     * <em>m<sub>k</sub></em> )<br>
     * <br>
     * See <a href=
     * "https://en.wikipedia.org/wiki/Triple_product">https://en.wikipedia.org/wiki/Triple_product</a>
     * .
     *
     * @param m the matrix
     * @return the determinant
     */
    public static float determinant(final Mat3 m) {

        /* @formatter:off */
    return m.m00 * (m.m11 * m.m22 - m.m12 * m.m21)
        + m.m01 * (m.m12 * m.m20 - m.m10 * m.m22)
        + m.m02 * (m.m10 * m.m21 - m.m11 * m.m20);
    /* @formatter:on */
    }

    /**
     * Creates a matrix from two axes. The third row and column are assumed to be
     * (0.0, 0.0, 1.0) .
     *
     * @param right   the right axis
     * @param forward the forward axis
     * @param target  the output matrix
     * @return the matrix
     */
    public static Mat3 fromAxes(final Vec2 right, final Vec2 forward, final Mat3 target) {

        /* @formatter:off */
    target.m00 = right.x;
    target.m01 = forward.x;
    target.m02 = 0.0f;
    target.m10 = right.y;
    target.m11 = forward.y;
    target.m12 = 0.0f;
    target.m20 = 0.0f;
    target.m21 = 0.0f;
    target.m22 = 1.0f;

    return target;
    /* @formatter:on */
    }

    /**
     * Creates a matrix from two axes and a translation. The third row, z or w, is
     * assumed to be (0.0,
     * 0.0, 1.0) .
     *
     * @param right       the right axis
     * @param forward     the forward axis
     * @param translation the translation
     * @param target      the output matrix
     * @return the matrix
     */
    public static Mat3 fromAxes(
        final Vec2 right, final Vec2 forward, final Vec2 translation, final Mat3 target) {

        /* @formatter:off */
    target.m00 = right.x;
    target.m01 = forward.x;
    target.m02 = translation.x;
    target.m10 = right.y;
    target.m11 = forward.y;
    target.m12 = translation.y;
    target.m20 = 0.0f;
    target.m21 = 0.0f;
    target.m22 = 1.0f;

    return target;
    /* @formatter:on */
    }

    /**
     * Creates a matrix from two axes. The third column, translation, is assumed to
     * be (0.0, 0.0, 1.0)
     * .
     *
     * @param right   the right axis
     * @param forward the forward axis
     * @param target  the output matrix
     * @return the matrix
     */
    public static Mat3 fromAxes(final Vec3 right, final Vec3 forward, final Mat3 target) {

        /* @formatter:off */
    target.m00 = right.x;
    target.m01 = forward.x;
    target.m02 = 0.0f;
    target.m10 = right.y;
    target.m11 = forward.y;
    target.m12 = 0.0f;
    target.m20 = right.z;
    target.m21 = forward.z;
    target.m22 = 1.0f;

    return target;
    /* @formatter:on */
    }

    /**
     * Creates a matrix from two axes and a translation.
     *
     * @param right       the right axis
     * @param forward     the forward axis
     * @param translation the translation
     * @param target      the output matrix
     * @return the matrix
     */
    public static Mat3 fromAxes(
        final Vec3 right, final Vec3 forward, final Vec3 translation, final Mat3 target) {

        /* @formatter:off */
    target.m00 = right.x;
    target.m01 = forward.x;
    target.m02 = translation.x;
    target.m10 = right.y;
    target.m11 = forward.y;
    target.m12 = translation.y;
    target.m20 = right.z;
    target.m21 = forward.z;
    target.m22 = translation.z;

    return target;
    /* @formatter:on */
    }

    /**
     * Creates a reflection matrix from an axis representing a plane. The axis will
     * be normalized by
     * the function.
     *
     * @param axis   the axis
     * @param target the output matrix
     * @return the reflection
     * @see Mat3#fromReflection(float, float, Mat3)
     * @see Mat3#identity(Mat3)
     */
    public static Mat3 fromReflection(final Vec2 axis, final Mat3 target) {

        final float ax = axis.x;
        final float ay = axis.y;
        final float mSq = ax * ax + ay * ay;
        if (mSq != 0.0f) {
            final float mInv = Utils.invSqrtUnchecked(mSq);
            return Mat3.fromReflection(ax * mInv, ay * mInv, target);
        }
        return Mat3.identity(target);
    }

    /**
     * Creates a rotation matrix from a cosine and sine around the z axis.
     *
     * @param cosa   the cosine of an angle
     * @param sina   the sine of an angle
     * @param target the output matrix
     * @return the matrix
     */
    public static Mat3 fromRotZ(final float cosa, final float sina, final Mat3 target) {

        /* @formatter:off */
    target.m00 = cosa;
    target.m01 = -sina;
    target.m02 = 0.0f;
    target.m10 = sina;
    target.m11 = cosa;
    target.m12 = 0.0f;
    target.m20 = 0.0f;
    target.m21 = 0.0f;
    target.m22 = 1.0f;

    return target;
    /* @formatter:on */
    }

    /**
     * Creates a rotation matrix from an angle in radians around the z axis.
     *
     * @param radians the angle
     * @param target  the output matrix
     * @return the matrix
     */
    public static Mat3 fromRotZ(final float radians, final Mat3 target) {

        final float norm = radians * Utils.ONE_TAU;
        return Mat3.fromRotZ(Utils.scNorm(norm), Utils.scNorm(norm - 0.25f), target);
    }

    /**
     * Creates a scale matrix from a scalar.
     *
     * @param scalar the scalar
     * @param target the output matrix
     * @return the matrix
     * @see Mat3#fromScale(float, float, Mat3)
     */
    public static Mat3 fromScale(final float scalar, final Mat3 target) {

        return Mat3.fromScale(scalar, scalar, target);
    }

    /**
     * Creates a scale matrix from a nonuniform scalar stored in a vector.
     *
     * @param scalar the nonuniform scalar
     * @param target the output matrix
     * @return the matrix
     * @see Mat3#fromScale(float, float, Mat3)
     */
    public static Mat3 fromScale(final Vec2 scalar, final Mat3 target) {

        return Mat3.fromScale(scalar.x, scalar.y, target);
    }

    /**
     * Creates skew, or shear, matrix from an angle and axes. Vectors <em>a</em> and
     * <em>b</em> are
     * expected to be orthonormal, i.e. perpendicular and of unit length. Returns
     * the identity if the
     * angle is divisible by pi.
     *
     * @param radians the angle in radians
     * @param a       the skew axis
     * @param b       the orthonormal axis
     * @param target  the output matrix
     * @return the skew matrix
     * @see Mat3#fromSkew(float, float, float, float, float, Mat3)
     * @see Mat3#identity(Mat3)
     * @see Utils#approx(float, float)
     * @see Utils#invSqrtUnchecked(float)
     * @see Utils#mod(float, float)
     * @see Utils#tan(float)
     */
    public static Mat3 fromSkew(final float radians, final Vec2 a, final Vec2 b, final Mat3 target) {

        if (Utils.approx(Utils.mod(radians, Utils.PI), 0.0f)) {
            return Mat3.identity(target);
        }

        final float ax = a.x;
        final float ay = a.y;
        final float amSq = ax * ax + ay * ay;
        if (amSq <= Utils.EPSILON) {
            return Mat3.identity(target);
        }

        final float bx = b.x;
        final float by = b.y;
        final float bmSq = bx * bx + by * by;
        if (bmSq <= Utils.EPSILON) {
            return Mat3.identity(target);
        }

        final float t = Utils.tan(radians);
        final float amInv = Utils.invSqrtUnchecked(amSq);
        final float bmInv = Utils.invSqrtUnchecked(bmSq);

        return Mat3.fromSkew(t, ax * amInv, ay * amInv, bx * bmInv, by * bmInv, target);
    }

    /**
     * Creates a translation matrix from a vector.
     *
     * @param tr     the translation
     * @param target the output matrix
     * @return the matrix
     * @see Mat3#fromTranslation(float, float, Mat3)
     */
    public static Mat3 fromTranslation(final Vec2 tr, final Mat3 target) {

        return Mat3.fromTranslation(tr.x, tr.y, target);
    }

    /**
     * Evaluates whether the left comparisand is greater than the right comparisand.
     *
     * @param a      left comparisand
     * @param b      right comparisand
     * @param target the output matrix
     * @return the evaluation
     */
    public static Mat3 gt(final Mat3 a, final Mat3 b, final Mat3 target) {

        /* @formatter:off */
    return target.set(
        a.m00 > b.m00,
        a.m01 > b.m01,
        a.m02 > b.m02,
        a.m10 > b.m10,
        a.m11 > b.m11,
        a.m12 > b.m12,
        a.m20 > b.m20,
        a.m21 > b.m21,
        a.m22 > b.m22);
    /* @formatter:on */
    }

    /**
     * Evaluates whether the left comparisand is greater than or equal to the right
     * comparisand.
     *
     * @param a      left comparisand
     * @param b      right comparisand
     * @param target the output matrix
     * @return the evaluation
     */
    public static Mat3 gtEq(final Mat3 a, final Mat3 b, final Mat3 target) {

        /* @formatter:off */
    return target.set(
        a.m00 >= b.m00,
        a.m01 >= b.m01,
        a.m02 >= b.m02,
        a.m10 >= b.m10,
        a.m11 >= b.m11,
        a.m12 >= b.m12,
        a.m20 >= b.m20,
        a.m21 >= b.m21,
        a.m22 >= b.m22);
    /* @formatter:on */
    }

    /**
     * Returns the identity matrix,
     *
     * <pre>
     * 1.0, 0.0, 0.0,
     * 0.0, 1.0, 0.0,
     * 0.0, 0.0, 1.0
     * </pre>
     *
     * @param target the output matrix
     * @return the identity matrix
     */
    public static Mat3 identity(final Mat3 target) {

        /* @formatter:off */
    target.m00 = 1.0f;
    target.m01 = 0.0f;
    target.m02 = 0.0f;
    target.m10 = 0.0f;
    target.m11 = 1.0f;
    target.m12 = 0.0f;
    target.m20 = 0.0f;
    target.m21 = 0.0f;
    target.m22 = 1.0f;

    return target;
    /* @formatter:on */
    }

    /**
     * Inverts the input matrix. Returns the identity if the matrix's determinant is
     * zero.
     *
     * @param m      the matrix
     * @param target the output matrix
     * @return the inverse
     * @see Mat3#identity(Mat3)
     */
    public static Mat3 inverse(final Mat3 m, final Mat3 target) {

        final float b01 = m.m22 * m.m11 - m.m12 * m.m21;
        final float b11 = m.m12 * m.m20 - m.m22 * m.m10;
        final float b21 = m.m21 * m.m10 - m.m11 * m.m20;
        final float det = m.m00 * b01 + m.m01 * b11 + m.m02 * b21;

        if (det != 0.0f) {
            final float detInv = 1.0f / det;

            target.m00 = b01 * detInv;
            target.m01 = (m.m02 * m.m21 - m.m22 * m.m01) * detInv;
            target.m02 = (m.m12 * m.m01 - m.m02 * m.m11) * detInv;

            target.m10 = b11 * detInv;
            target.m11 = (m.m22 * m.m00 - m.m02 * m.m20) * detInv;
            target.m12 = (m.m02 * m.m10 - m.m12 * m.m00) * detInv;

            target.m20 = b21 * detInv;
            target.m21 = (m.m01 * m.m20 - m.m21 * m.m00) * detInv;
            target.m22 = (m.m11 * m.m00 - m.m01 * m.m10) * detInv;

            return target;
        }

        return Mat3.identity(target);
    }

    /**
     * Tests to see if a matrix is the identity matrix.
     *
     * @param m the matrix
     * @return the evaluation
     */
    public static boolean isIdentity(final Mat3 m) {

        /* @formatter:off */
    return m.m22 == 1.0f
        && m.m11 == 1.0f
        && m.m00 == 1.0f
        && m.m01 == 0.0f
        && m.m02 == 0.0f
        && m.m10 == 0.0f
        && m.m12 == 0.0f
        && m.m20 == 0.0f
        && m.m21 == 0.0f;
    /* @formatter:on */
    }

    /**
     * Evaluates whether the left comparisand is less than the right comparisand.
     *
     * @param a      left comparisand
     * @param b      right comparisand
     * @param target the output matrix
     * @return the evaluation
     */
    public static Mat3 lt(final Mat3 a, final Mat3 b, final Mat3 target) {

        /* @formatter:off */
    return target.set(
        a.m00 < b.m00,
        a.m01 < b.m01,
        a.m02 < b.m02,
        a.m10 < b.m10,
        a.m11 < b.m11,
        a.m12 < b.m12,
        a.m20 < b.m20,
        a.m21 < b.m21,
        a.m22 < b.m22);
    /* @formatter:on */
    }

    /**
     * Evaluates whether the left comparisand is less than or equal to the right
     * comparisand.
     *
     * @param a      left comparisand
     * @param b      right comparisand
     * @param target the output matrix
     * @return the evaluation
     */
    public static Mat3 ltEq(final Mat3 a, final Mat3 b, final Mat3 target) {

        /* @formatter:off */
    return target.set(
        a.m00 <= b.m00,
        a.m01 <= b.m01,
        a.m02 <= b.m02,
        a.m10 <= b.m10,
        a.m11 <= b.m11,
        a.m12 <= b.m12,
        a.m20 <= b.m20,
        a.m21 <= b.m21,
        a.m22 <= b.m22);
    /* @formatter:on */
    }

    /**
     * Multiplies each component in a matrix by a scalar. Not to be confused with
     * scaling affine
     * transform matrix.
     *
     * @param s      the left operand
     * @param m      the right operand
     * @param target the output matrix
     * @return the product
     */
    public static Mat3 mul(final float s, final Mat3 m, final Mat3 target) {

        /* @formatter:off */
    target.m00 = s * m.m00;
    target.m01 = s * m.m01;
    target.m02 = s * m.m02;
    target.m10 = s * m.m10;
    target.m11 = s * m.m11;
    target.m12 = s * m.m12;
    target.m20 = s * m.m20;
    target.m21 = s * m.m21;
    target.m22 = s * m.m22;

    return target;
    /* @formatter:on */
    }

    /**
     * Multiplies each component in a matrix by a scalar. Not to be confused with
     * scaling affine
     * transform matrix.
     *
     * @param m      the left operand
     * @param s      the right operand
     * @param target the output matrix
     * @return the product
     */
    public static Mat3 mul(final Mat3 m, final float s, final Mat3 target) {

        /* @formatter:off */
    target.m00 = m.m00 * s;
    target.m01 = m.m01 * s;
    target.m02 = m.m02 * s;
    target.m10 = m.m10 * s;
    target.m11 = m.m11 * s;
    target.m12 = m.m12 * s;
    target.m20 = m.m20 * s;
    target.m21 = m.m21 * s;
    target.m22 = m.m22 * s;

    return target;
    /* @formatter:on */
    }

    /**
     * Multiplies two matrices.
     *
     * @param a      the left operand
     * @param b      the right operand
     * @param target the output matrix
     * @return the product
     */
    public static Mat3 mul(final Mat3 a, final Mat3 b, final Mat3 target) {

        target.m00 = a.m00 * b.m00 + a.m01 * b.m10 + a.m02 * b.m20;
        target.m01 = a.m00 * b.m01 + a.m01 * b.m11 + a.m02 * b.m21;
        target.m02 = a.m00 * b.m02 + a.m01 * b.m12 + a.m02 * b.m22;

        target.m10 = a.m10 * b.m00 + a.m11 * b.m10 + a.m12 * b.m20;
        target.m11 = a.m10 * b.m01 + a.m11 * b.m11 + a.m12 * b.m21;
        target.m12 = a.m10 * b.m02 + a.m11 * b.m12 + a.m12 * b.m22;

        target.m20 = a.m20 * b.m00 + a.m21 * b.m10 + a.m22 * b.m20;
        target.m21 = a.m20 * b.m01 + a.m21 * b.m11 + a.m22 * b.m21;
        target.m22 = a.m20 * b.m02 + a.m21 * b.m12 + a.m22 * b.m22;

        return target;
    }

    /**
     * Multiplies three matrices. Useful for composing an affine transform from
     * translation, rotation
     * and scale matrices.
     *
     * @param a      the first matrix
     * @param b      the second matrix
     * @param c      the third matrix
     * @param target the output matrix
     * @return the product
     */
    public static Mat3 mul(final Mat3 a, final Mat3 b, final Mat3 c, final Mat3 target) {

        final float n00 = a.m00 * b.m00 + a.m01 * b.m10 + a.m02 * b.m20;
        final float n01 = a.m00 * b.m01 + a.m01 * b.m11 + a.m02 * b.m21;
        final float n02 = a.m00 * b.m02 + a.m01 * b.m12 + a.m02 * b.m22;

        final float n10 = a.m10 * b.m00 + a.m11 * b.m10 + a.m12 * b.m20;
        final float n11 = a.m10 * b.m01 + a.m11 * b.m11 + a.m12 * b.m21;
        final float n12 = a.m10 * b.m02 + a.m11 * b.m12 + a.m12 * b.m22;

        final float n20 = a.m20 * b.m00 + a.m21 * b.m10 + a.m22 * b.m20;
        final float n21 = a.m20 * b.m01 + a.m21 * b.m11 + a.m22 * b.m21;
        final float n22 = a.m20 * b.m02 + a.m21 * b.m12 + a.m22 * b.m22;

        target.m00 = n00 * c.m00 + n01 * c.m10 + n02 * c.m20;
        target.m01 = n00 * c.m01 + n01 * c.m11 + n02 * c.m21;
        target.m02 = n00 * c.m02 + n01 * c.m12 + n02 * c.m22;
        target.m10 = n10 * c.m00 + n11 * c.m10 + n12 * c.m20;
        target.m11 = n10 * c.m01 + n11 * c.m11 + n12 * c.m21;
        target.m12 = n10 * c.m02 + n11 * c.m12 + n12 * c.m22;
        target.m20 = n20 * c.m00 + n21 * c.m10 + n22 * c.m20;
        target.m21 = n20 * c.m01 + n21 * c.m11 + n22 * c.m21;
        target.m22 = n20 * c.m02 + n21 * c.m12 + n22 * c.m22;

        return target;
    }

    /**
     * Multiplies a matrix and a vector.
     *
     * @param m      the matrix
     * @param v      the vector
     * @param target the output matrix
     * @return the product
     */
    public static Mat3 mul(final Mat3 m, final Vec3 v, final Mat3 target) {

        /* @formatter:off */
    target.m00 = m.m00 * v.x;
    target.m01 = m.m01 * v.y;
    target.m02 = m.m02 * v.z;
    target.m10 = m.m10 * v.x;
    target.m11 = m.m11 * v.y;
    target.m12 = m.m12 * v.z;
    target.m20 = m.m20 * v.x;
    target.m21 = m.m21 * v.y;
    target.m22 = m.m22 * v.z;

    return target;
    /* @formatter:on */
    }

    /**
     * Multiplies a matrix and a vector.
     *
     * @param m      the matrix
     * @param v      the vector
     * @param target the output vector
     * @return the product
     */
    public static Vec3 mul(final Mat3 m, final Vec3 v, final Vec3 target) {

        /* @formatter:off */
    return target.set(
        m.m00 * v.x + m.m01 * v.y + m.m02 * v.z,
        m.m10 * v.x + m.m11 * v.y + m.m12 * v.z,
        m.m20 * v.x + m.m21 * v.y + m.m22 * v.z);
    /* @formatter:on */
    }

    /**
     * Multiplies a vector and the transpose of a matrix.
     *
     * @param v      the vector
     * @param m      the matrix
     * @param target the output matrix
     * @return the product
     */
    public static Mat3 mul(final Vec3 v, final Mat3 m, final Mat3 target) {

        /* @formatter:off */
    target.m00 = v.x * m.m00;
    target.m01 = v.y * m.m10;
    target.m02 = v.z * m.m20;
    target.m10 = v.x * m.m01;
    target.m11 = v.y * m.m11;
    target.m12 = v.z * m.m21;
    target.m20 = v.x * m.m02;
    target.m21 = v.y * m.m12;
    target.m22 = v.z * m.m22;

    return target;
    /* @formatter:on */
    }

    /**
     * Following <a href=
     * "https://en.wikibooks.org/wiki/GLSL_Programming/Vector_and_Matrix_Operations#Operators">GLSL
     * convention</a>, multiplies a vector and the transpose of a matrix.<br>
     * <br>
     * v<sup>T</sup> M = ( M<sup>T</sup> v )<sup>T</sup>
     *
     * @param m      the matrix
     * @param v      the vector
     * @param target the output vector
     * @return the product
     */
    public static Vec3 mul(final Vec3 v, final Mat3 m, final Vec3 target) {

        /* @formatter:off */
    return target.set(
        v.x * m.m00 + v.y * m.m10 + v.z * m.m20,
        v.x * m.m01 + v.y * m.m11 + v.z * m.m21,
        v.x * m.m02 + v.y * m.m12 + v.z * m.m22);
    /* @formatter:on */
    }

    /**
     * Multiplies a matrix and a point. The z component of the point is assumed to
     * be 1.0, so the
     * point is impacted by the matrix's translation.
     *
     * @param m      the matrix
     * @param p      the point
     * @param target the output point
     * @return the product
     */
    public static Vec2 mulPoint(final Mat3 m, final Vec2 p, final Vec2 target) {

        final float w = m.m20 * p.x + m.m21 * p.y + m.m22;
        if (w != 0.0f) {
            final float wInv = 1.0f / w;
            return target.set(
                (m.m00 * p.x + m.m01 * p.y + m.m02) * wInv, (m.m10 * p.x + m.m11 * p.y + m.m12) * wInv);
        }
        return target.reset();
    }

    /**
     * Multiplies a matrix and a vector. The z component of the vector is assumed to
     * be 0.0 , so the
     * vector is not impacted by the matrix's translation.
     *
     * @param m      the matrix
     * @param v      the vector
     * @param target the output vector
     * @return the product
     */
    public static Vec2 mulVector(final Mat3 m, final Vec2 v, final Vec2 target) {

        final float w = m.m20 * v.x + m.m21 * v.y + m.m22;
        if (w != 0.0f) {
            final float wInv = 1.0f / w;
            return target.set((m.m00 * v.x + m.m01 * v.y) * wInv, (m.m10 * v.x + m.m11 * v.y) * wInv);
        }
        return target.reset();
    }

    /**
     * Negates the input matrix.
     *
     * @param m      the input matrix
     * @param target the output matrix
     * @return the negation
     */
    public static Mat3 negate(final Mat3 m, final Mat3 target) {

        target.m00 = -m.m00;
        target.m01 = -m.m01;
        target.m02 = -m.m02;
        target.m10 = -m.m10;
        target.m11 = -m.m11;
        target.m12 = -m.m12;
        target.m20 = -m.m20;
        target.m21 = -m.m21;
        target.m22 = -m.m22;

        return target;
    }

    /**
     * Tests to see if all the matrix's components are zero.
     *
     * @param m the input matrix
     * @return the evaluation
     */
    public static boolean none(final Mat3 m) {

        return m.m00 == 0.0f
            && m.m01 == 0.0f
            && m.m02 == 0.0f
            && m.m10 == 0.0f
            && m.m11 == 0.0f
            && m.m12 == 0.0f
            && m.m20 == 0.0f
            && m.m21 == 0.0f
            && m.m22 == 0.0f;
    }

    /**
     * Evaluates a matrix like a boolean, where n != 0.0 is true.
     *
     * @param m      the input matrix
     * @param target the output matrix
     * @return the truth table opposite
     */
    public static Mat3 not(final Mat3 m, final Mat3 target) {

        target.m00 = m.m00 != 0.0f ? 0.0f : 1.0f;
        target.m01 = m.m01 != 0.0f ? 0.0f : 1.0f;
        target.m02 = m.m02 != 0.0f ? 0.0f : 1.0f;
        target.m10 = m.m10 != 0.0f ? 0.0f : 1.0f;
        target.m11 = m.m11 != 0.0f ? 0.0f : 1.0f;
        target.m12 = m.m12 != 0.0f ? 0.0f : 1.0f;
        target.m20 = m.m20 != 0.0f ? 0.0f : 1.0f;
        target.m21 = m.m21 != 0.0f ? 0.0f : 1.0f;
        target.m22 = m.m22 != 0.0f ? 0.0f : 1.0f;

        return target;
    }

    /**
     * Evaluates two matrices like booleans, using the OR logic gate.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output matrix
     * @return the evaluation
     * @see Utils#or(float, float)
     */
    public static Mat3 or(final Mat3 a, final Mat3 b, final Mat3 target) {

        target.m00 = (float) Utils.or(a.m00, b.m00);
        target.m01 = (float) Utils.or(a.m01, b.m01);
        target.m02 = (float) Utils.or(a.m02, b.m02);
        target.m10 = (float) Utils.or(a.m10, b.m10);
        target.m11 = (float) Utils.or(a.m11, b.m11);
        target.m12 = (float) Utils.or(a.m12, b.m12);
        target.m20 = (float) Utils.or(a.m20, b.m20);
        target.m21 = (float) Utils.or(a.m21, b.m21);
        target.m22 = (float) Utils.or(a.m22, b.m22);

        return target;
    }

    /**
     * Rotates the elements of the input matrix 90 degrees counter-clockwise.
     *
     * @param m      the input matrix
     * @param target the output matrix
     * @return the rotated matrix
     */
    public static Mat3 rotateElmsCcw(final Mat3 m, final Mat3 target) {

        /* @formatter:off */
    target.m00 = m.m02;
    target.m01 = m.m12;
    target.m02 = m.m22;
    target.m10 = m.m01;
    target.m11 = m.m11;
    target.m12 = m.m21;
    target.m20 = m.m00;
    target.m21 = m.m10;
    target.m22 = m.m20;

    return target;
    /* @formatter:on */
    }

    /**
     * Rotates the elements of the input matrix 90 degrees clockwise.
     *
     * @param m      the input matrix
     * @param target the output matrix
     * @return the rotated matrix
     */
    public static Mat3 rotateElmsCw(final Mat3 m, final Mat3 target) {

        /* @formatter:off */
    target.m00 = m.m20;
    target.m01 = m.m10;
    target.m02 = m.m00;
    target.m10 = m.m21;
    target.m11 = m.m11;
    target.m12 = m.m01;
    target.m20 = m.m22;
    target.m21 = m.m12;
    target.m22 = m.m02;

    return target;
    /* @formatter:on */
    }

    /**
     * Subtracts the right matrix from the left matrix.
     *
     * @param a      the left operand
     * @param b      the right operand
     * @param target the output matrix
     * @return the result
     */
    public static Mat3 sub(final Mat3 a, final Mat3 b, final Mat3 target) {

        /* @formatter:off */
    target.m00 = a.m00 - b.m00;
    target.m01 = a.m01 - b.m01;
    target.m02 = a.m02 - b.m02;
    target.m10 = a.m10 - b.m10;
    target.m11 = a.m11 - b.m11;
    target.m12 = a.m12 - b.m12;
    target.m20 = a.m20 - b.m20;
    target.m21 = a.m21 - b.m21;
    target.m22 = a.m22 - b.m22;

    return target;
    /* @formatter:on */
    }

    /**
     * Transposes a matrix, switching its row and column elements.
     *
     * @param m      the matrix
     * @param target the output matrix
     * @return the transposed matrix
     */
    public static Mat3 transpose(final Mat3 m, final Mat3 target) {

        /* @formatter:off */
    target.m00 = m.m00;
    target.m01 = m.m10;
    target.m02 = m.m20;
    target.m10 = m.m01;
    target.m11 = m.m11;
    target.m12 = m.m21;
    target.m20 = m.m02;
    target.m21 = m.m12;
    target.m22 = m.m22;

    return target;
    /* @formatter:on */
    }

    /**
     * Evaluates two matrices like booleans, using the exclusive or (XOR) logic
     * gate.
     *
     * @param a      left operand
     * @param b      right operand
     * @param target the output matrix
     * @return the evaluation
     * @see Utils#xor(float, float)
     */
    public static Mat3 xor(final Mat3 a, final Mat3 b, final Mat3 target) {

        target.m00 = (float) Utils.xor(a.m00, b.m00);
        target.m01 = (float) Utils.xor(a.m01, b.m01);
        target.m02 = (float) Utils.xor(a.m02, b.m02);
        target.m10 = (float) Utils.xor(a.m10, b.m10);
        target.m11 = (float) Utils.xor(a.m11, b.m11);
        target.m12 = (float) Utils.xor(a.m12, b.m12);
        target.m20 = (float) Utils.xor(a.m20, b.m20);
        target.m21 = (float) Utils.xor(a.m21, b.m21);
        target.m22 = (float) Utils.xor(a.m22, b.m22);

        return target;
    }

    /**
     * Creates a reflection matrix from an axis representing a plane.
     *
     * @param ax     the axis x
     * @param ay     the axis y
     * @param target the output matrix
     * @return the reflection
     */
    static Mat3 fromReflection(final float ax, final float ay, final Mat3 target) {

        final float x = -(ax + ax);
        final float y = -(ay + ay);
        final float axay = x * ay;

        /* @formatter:off */
    target.m00 = x * ax + 1.0f;
    target.m01 = axay;
    target.m02 = 0.0f;
    target.m10 = axay;
    target.m11 = y * ay + 1.0f;
    target.m12 = 0.0f;
    target.m20 = 0.0f;
    target.m21 = 0.0f;
    target.m22 = 1.0f;
    /* @formatter:on */

        return target;
    }

    /**
     * Creates a scale matrix from a nonuniform scalar. The bottom right corner,
     * m22, is set to 1.0 .
     * Returns the identity if the scalar is zero.<br>
     * <br>
     * A package level function that uses loose floats to facilitate parsing of SVG
     * transforms.
     *
     * @param sx     the x scalar
     * @param sy     the y scalar
     * @param target the output matrix
     * @return the matrix
     * @see Mat3#identity(Mat3)
     */
    static Mat3 fromScale(final float sx, final float sy, final Mat3 target) {

        if (sx != 0.0f && sy != 0.0f) {
            /* @formatter:off */
      target.m00 = sx;
      target.m01 = 0.0f;
      target.m02 = 0.0f;
      target.m10 = 0.0f;
      target.m11 = sy;
      target.m12 = 0.0f;
      target.m20 = 0.0f;
      target.m21 = 0.0f;
      target.m22 = 1.0f;

      return target;
      /* @formatter:on */
        }
        return Mat3.identity(target);
    }

    /**
     * Creates skew, or shear, matrix from the tangent of an angle and axes. Axes
     * <em>a</em> and
     * <em>b</em> are expected to be orthonormal, i.e. perpendicular and of unit
     * length.
     *
     * @param t      the tangent of the angle
     * @param ax     the skew axis x
     * @param ay     the skew axis y
     * @param bx     the perpendicular axis x
     * @param by     the perpendicular axis y
     * @param target the output matrix
     * @return the skew matrix
     */
    static Mat3 fromSkew(
        final float t,
        final float ax,
        final float ay,
        final float bx,
        final float by,
        final Mat3 target) {

        final float tax = ax * t;
        final float tay = ay * t;

        target.m00 = tax * bx + 1.0f;
        target.m01 = tax * by;
        target.m02 = 0.0f;

        target.m10 = tay * bx;
        target.m11 = tay * by + 1.0f;
        target.m12 = 0.0f;

        target.m20 = 0.0f;
        target.m21 = 0.0f;
        target.m22 = 1.0f;

        return target;
    }

    /**
     * Creates a horizontal skew matrix from an angle in radians. Returns the
     * identity if the angle is
     * divisible by pi.<br>
     * <br>
     * A package level function that uses loose floats to facilitate parsing of SVG
     * transforms.
     *
     * @param radians the angle
     * @param target  the output matrix
     * @return the skew matrix
     * @see Mat3#identity(Mat3)
     * @see Utils#approx(float, float)
     * @see Utils#mod(float, float)
     * @see Utils#tan(float)
     */
    static Mat3 fromSkewX(final float radians, final Mat3 target) {

        if (Utils.approx(Utils.mod(radians, Utils.PI), 0.0f)) {
            return Mat3.identity(target);
        }

        target.m00 = 1.0f;
        target.m01 = Utils.tan(radians);
        target.m02 = 0.0f;
        target.m10 = 0.0f;
        target.m11 = 1.0f;
        target.m12 = 0.0f;
        target.m20 = 0.0f;
        target.m21 = 0.0f;
        target.m22 = 1.0f;

        return target;
    }

    /**
     * Creates a vertical skew matrix from an angle in radians. Returns the identity
     * if the angle is
     * divisible by pi.<br>
     * <br>
     * A package level function that uses loose floats to facilitate parsing of SVG
     * transforms.
     *
     * @param radians the angle
     * @param target  the output matrix
     * @return the skew matrix
     * @see Mat3#identity(Mat3)
     * @see Utils#approx(float, float)
     * @see Utils#mod(float, float)
     * @see Utils#tan(float)
     */
    static Mat3 fromSkewY(final float radians, final Mat3 target) {

        if (Utils.approx(Utils.mod(radians, Utils.PI), 0.0f)) {
            return Mat3.identity(target);
        }

        target.m00 = 1.0f;
        target.m01 = 0.0f;
        target.m02 = 0.0f;
        target.m10 = Utils.tan(radians);
        target.m11 = 1.0f;
        target.m12 = 0.0f;
        target.m20 = 0.0f;
        target.m21 = 0.0f;
        target.m22 = 1.0f;

        return target;
    }

    /**
     * Creates a translation matrix from a vector.<br>
     * <br>
     * A package level function that uses loose floats to facilitate parsing of SVG
     * transforms.
     *
     * @param tx     the translation x
     * @param ty     the translation y
     * @param target the output matrix
     * @return the matrix
     */
    static Mat3 fromTranslation(final float tx, final float ty, final Mat3 target) {

        /* @formatter:off */
    target.m00 = 1.0f;
    target.m01 = 0.0f;
    target.m02 = tx;
    target.m10 = 0.0f;
    target.m11 = 1.0f;
    target.m12 = ty;
    target.m20 = 0.0f;
    target.m21 = 0.0f;
    target.m22 = 1.0f;

    return target;
    /* @formatter:on */
    }

    /**
     * Tests this matrix for equivalence with another object.
     *
     * @param obj the object
     * @return the equivalence
     * @see Mat3#equals(Mat3)
     */
    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        return this.equals((Mat3) obj);
    }

    /**
     * Gets a column of this matrix with an index and vector.
     *
     * @param j      the index
     * @param target the vector
     * @return the column
     */
    public Vec3 getCol(final int j, final Vec3 target) {

        switch (j) {
            case 0:
            case -3:
                return target.set(this.m00, this.m10, this.m20);

            case 1:
            case -2:
                return target.set(this.m01, this.m11, this.m21);

            case 2:
            case -1:
                return target.set(this.m02, this.m12, this.m22);

            default:
                return target.reset();
        }
    }

    /**
     * Simulates bracket subscript access in a one-dimensional, row-major matrix
     * array. Works with
     * positive integers in [0, 8] or negative integers in [-9, -1] .
     *
     * @param index the index
     * @return the component at that index
     */
    public float getElm(final int index) {

        /* @formatter:off */
    switch (index) {
      case 0:
      case -9:
        return this.m00;
      case 1:
      case -8:
        return this.m01;
      case 2:
      case -7:
        return this.m02;

      case 3:
      case -6:
        return this.m10;
      case 4:
      case -5:
        return this.m11;
      case 5:
      case -4:
        return this.m12;

      case 6:
      case -3:
        return this.m20;
      case 7:
      case -2:
        return this.m21;
      case 8:
      case -1:
        return this.m22;

      default:
        return 0.0f;
    }
    /* @formatter:on */
    }

    /**
     * Simulates bracket subscript access in a two-dimensional, row-major matrix
     * array. Works with
     * positive integers in [0, 2][0, 2] or negative integers in [-3, -1][-3, -1] .
     *
     * @param i the row index
     * @param j the column index
     * @return the component at that index
     */
    public float getElm(final int i, final int j) {

        /* @formatter:off */
    switch (i) {
      case 0:
      case -3:
        switch (j) {
          case 0:
          case -3:
            return this.m00;
          case 1:
          case -2:
            return this.m01;
          case 2:
          case -1:
            return this.m02;
          default:
            return 0.0f;
        }

      case 1:
      case -2:
        switch (j) {
          case 0:
          case -3:
            return this.m10;
          case 1:
          case -2:
            return this.m11;
          case 2:
          case -1:
            return this.m12;
          default:
            return 0.0f;
        }

      case 2:
      case -1:
        switch (j) {
          case 0:
          case -3:
            return this.m20;
          case 1:
          case -2:
            return this.m21;
          case 2:
          case -1:
            return this.m22;
          default:
            return 0.0f;
        }

      default:
        return 0.0f;
    }
    /* @formatter:on */
    }

    @Override
    public int hashCode() {
        return Objects.hash(m00, m01, m02, m10, m11, m12, m20, m21, m22);
    }

    /**
     * Resets this matrix to an initial state,
     *
     * <pre>
     * 1.0, 0.0, 0.0,
     * 0.0, 1.0, 0.0,
     * 0.0, 0.0, 1.0
     * </pre>
     *
     * @return this matrix
     */
    public Mat3 reset() {

        /* @formatter:off */
    this.m00 = 1.0f;
    this.m01 = 0.0f;
    this.m02 = 0.0f;
    this.m10 = 0.0f;
    this.m11 = 1.0f;
    this.m12 = 0.0f;
    this.m20 = 0.0f;
    this.m21 = 0.0f;
    this.m22 = 1.0f;

    return this;
    /* @formatter:on */
    }

    /**
     * Sets the components of this matrix.
     *
     * @param m00 row 0, column 0
     * @param m01 row 0, column 1
     * @param m02 row 0, column 2
     * @param m10 row 1, column 0
     * @param m11 row 1, column 1
     * @param m12 row 1, column 2
     * @param m20 row 2, column 0
     * @param m21 row 2, column 1
     * @param m22 row 2, column 2
     * @return this matrix
     */
    public Mat3 set(
        final boolean m00,
        final boolean m01,
        final boolean m02,
        final boolean m10,
        final boolean m11,
        final boolean m12,
        final boolean m20,
        final boolean m21,
        final boolean m22) {

        this.m00 = m00 ? 1.0f : 0.0f;
        this.m01 = m01 ? 1.0f : 0.0f;
        this.m02 = m02 ? 1.0f : 0.0f;

        this.m10 = m10 ? 1.0f : 0.0f;
        this.m11 = m11 ? 1.0f : 0.0f;
        this.m12 = m12 ? 1.0f : 0.0f;

        this.m20 = m20 ? 1.0f : 0.0f;
        this.m21 = m21 ? 1.0f : 0.0f;
        this.m22 = m22 ? 1.0f : 0.0f;

        return this;
    }

    /**
     * Sets the upper left 2 by 2 corner of this matrix. The remaining values are
     * set to the identity.
     *
     * @param m00 row 0, column 0
     * @param m01 row 0, column 1
     * @param m10 row 1, column 0
     * @param m11 row 1, column 1
     * @return this matrix
     */
    public Mat3 set(final float m00, final float m01, final float m10, final float m11) {

        /* @formatter:off */
    this.m00 = m00;
    this.m01 = m01;
    this.m02 = 0.0f;
    this.m10 = m10;
    this.m11 = m11;
    this.m12 = 0.0f;
    this.m20 = 0.0f;
    this.m21 = 0.0f;
    this.m22 = 1.0f;

    return this;
    /* @formatter:on */
    }

    /**
     * Sets the upper two rows of this matrix. The last row is set to (0.0, 0.0,
     * 1.0) .
     *
     * @param m00 row 0, column 0
     * @param m01 row 0, column 1
     * @param m02 row 0, column 2
     * @param m10 row 1, column 0
     * @param m11 row 1, column 1
     * @param m12 row 1, column 2
     * @return this matrix
     */
    public Mat3 set(
        final float m00,
        final float m01,
        final float m02,
        final float m10,
        final float m11,
        final float m12) {

        /* @formatter:off */
    this.m00 = m00;
    this.m01 = m01;
    this.m02 = m02;
    this.m10 = m10;
    this.m11 = m11;
    this.m12 = m12;
    this.m20 = 0.0f;
    this.m21 = 0.0f;
    this.m22 = 1.0f;

    return this;
    /* @formatter:on */
    }

    /**
     * Sets the components of this matrix.
     *
     * @param m00 row 0, column 0
     * @param m01 row 0, column 1
     * @param m02 row 0, column 2
     * @param m10 row 1, column 0
     * @param m11 row 1, column 1
     * @param m12 row 1, column 2
     * @param m20 row 2, column 0
     * @param m21 row 2, column 1
     * @param m22 row 2, column 2
     * @return this matrix
     */
    public Mat3 set(
        float m00,
        float m01,
        float m02,
        float m10,
        float m11,
        float m12,
        float m20,
        float m21,
        float m22) {

        /* @formatter:off */
    this.m00 = m00;
    this.m01 = m01;
    this.m02 = m02;
    this.m10 = m10;
    this.m11 = m11;
    this.m12 = m12;
    this.m20 = m20;
    this.m21 = m21;
    this.m22 = m22;

    return this;
    /* @formatter:on */
    }

    /**
     * Sets a column of this matrix with an index and vector. If the column is an
     * axis vector, the w
     * or z component is set to 0.0; if it is a translation, the w or z component is
     * set to 1.0.
     *
     * @param j      the column index
     * @param source the column
     * @return this matrix
     */
    public Mat3 setCol(final int j, final Vec2 source) {

        switch (j) {
            case 0:
            case -3:

                /* Right axis. */
                this.m00 = source.x;
                this.m10 = source.y;
                this.m20 = 0.0f;
                return this;

            case 1:
            case -2:

                /* Forward axis. */
                this.m01 = source.x;
                this.m11 = source.y;
                this.m21 = 0.0f;
                return this;

            case 2:
            case -1:

                /* Translation. */
                this.m02 = source.x;
                this.m12 = source.y;
                this.m22 = 1.0f;
                return this;

            default:
                return this;
        }
    }

    /**
     * Sets a column of this matrix with an index and vector.
     *
     * @param j      the column index
     * @param source the column
     * @return this matrix
     */
    public Mat3 setCol(final int j, final Vec3 source) {

        switch (j) {
            case 0:
            case -3:

                /* Right axis. */
                this.m00 = source.x;
                this.m10 = source.y;
                this.m20 = source.z;
                return this;

            case 1:
            case -2:

                /* Forward axis. */
                this.m01 = source.x;
                this.m11 = source.y;
                this.m21 = source.z;
                return this;

            case 2:
            case -1:

                /* Translation. */
                this.m02 = source.x;
                this.m12 = source.y;
                this.m22 = source.z;
                return this;

            default:
                return this;
        }
    }

    /**
     * Returns a float array containing this matrix's components.
     *
     * @return the array
     */
    public float[] toArray() {
        return this.toArray1();
    }

    /**
     * Returns a 1D float array containing this matrix's components in row major
     * order.
     *
     * @return the array
     */
    public float[] toArray1() {

        return new float[]{
            this.m00, this.m01, this.m02, this.m10, this.m11, this.m12, this.m20, this.m21, this.m22
        };
    }

    /**
     * Returns a 2D float array containing this matrix's components.
     *
     * @return the array
     */
    public float[][] toArray2() {

        return new float[][]{
            {this.m00, this.m01, this.m02}, {this.m10, this.m11, this.m12}, {this.m20, this.m21, this.m22}
        };
    }

    /**
     * Returns a string representation of this matrix.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return this.toString(Utils.FIXED_PRINT);
    }

    /**
     * Returns a string representation of this matrix.
     *
     * @param places number of decimal places
     * @return the string
     */
    public String toString(final int places) {

        return this.toString(new StringBuilder(128), places).toString();
    }

    /**
     * Returns a string representation of this matrix, where columns are separated
     * by tabs and rows
     * are separated by new lines.
     *
     * @return the string
     */
    public String toStringCol() {

        return this.toStringCol(Utils.FIXED_PRINT, ',', ' ', '\n');
    }

    /**
     * Returns a string representation of this matrix intended for display in the
     * console.
     *
     * @param p number of decimal places
     * @param s the entry separator
     * @param t the spacer
     * @param n the row separator
     * @return the string
     */
    public String toStringCol(final int p, final char s, final char t, final char n) {

        return '\n'
            + Utils.toFixed(this.m00, p)
            + s
            + t
            + Utils.toFixed(this.m01, p)
            + s
            + t
            + Utils.toFixed(this.m02, p)
            + s
            + n
            + Utils.toFixed(this.m10, p)
            + s
            + t
            + Utils.toFixed(this.m11, p)
            + s
            + t
            + Utils.toFixed(this.m12, p)
            + s
            + n
            + Utils.toFixed(this.m20, p)
            + s
            + t
            + Utils.toFixed(this.m21, p)
            + s
            + t
            + Utils.toFixed(this.m22, p)
            + '\n';
    }

    /**
     * Internal helper function to assist with methods that need to print many
     * matrices. Appends to an
     * existing {@link StringBuilder}.
     *
     * @param sb     the string builder
     * @param places the number of places
     * @return the string builder
     * @see Utils#toFixed(StringBuilder, float, int)
     */
    StringBuilder toString(final StringBuilder sb, final int places) {

        sb.append("{\"m00\":");
        Utils.toFixed(sb, this.m00, places);
        sb.append(",\"m01\":");
        Utils.toFixed(sb, this.m01, places);
        sb.append(",\"m02\":");
        Utils.toFixed(sb, this.m02, places);
        sb.append(",\"m10\":");
        Utils.toFixed(sb, this.m10, places);
        sb.append(",\"m11\":");
        Utils.toFixed(sb, this.m11, places);
        sb.append(",\"m12\":");
        Utils.toFixed(sb, this.m12, places);
        sb.append(",\"m20\":");
        Utils.toFixed(sb, this.m20, places);
        sb.append(",\"m21\":");
        Utils.toFixed(sb, this.m21, places);
        sb.append(",\"m22\":");
        Utils.toFixed(sb, this.m22, places);
        sb.append('}');
        return sb;
    }

    /**
     * Tests for equivalence between this and another matrix.
     *
     * @param n the matrix
     * @return the evaluation
     * @see Float#floatToIntBits(float)
     */
    protected boolean equals(final Mat3 n) {

        /* With {@link Float.floatToIntBits(float)}, -0.0f != 0.0f. */
        return this.m00 == n.m00
            && this.m01 == n.m01
            && this.m02 == n.m02
            && this.m10 == n.m10
            && this.m11 == n.m11
            && this.m12 == n.m12
            && this.m20 == n.m20
            && this.m21 == n.m21
            && this.m22 == n.m22;
    }
}
