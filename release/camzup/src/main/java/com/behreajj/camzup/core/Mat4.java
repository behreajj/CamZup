package com.behreajj.camzup.core;

import java.util.Objects;

/**
 * A mutable, extensible class influenced by GLSL, OSL and PMatrix3D. Although
 * this is a 4 x 4 matrix, it is assumed to be a 3D affine transform matrix,
 * where the last row is (0.0, 0.0, 0.0, 1.0) . Instance methods are limited,
 * while most static methods require an explicit output variable to be provided.
 */
public class Mat4 {

    /**
     * Component in row 0, column 0. The right axis x component.
     */
    public float m00 = 1.0f;

    /**
     * Component in row 0, column 1. The forward axis x component.
     */
    public float m01 = 0.0f;

    /**
     * Component in row 0, column 2. The up axis x component.
     */
    public float m02 = 0.0f;

    /**
     * Component in row 0, column 3. The translation x component.
     */
    public float m03 = 0.0f;

    /**
     * Component in row 1, column 0. The right axis y component.
     */
    public float m10 = 0.0f;

    /**
     * Component in row 1, column 1. The forward axis y component.
     */
    public float m11 = 1.0f;

    /**
     * Component in row 1, column 2. The up axis y component.
     */
    public float m12 = 0.0f;

    /**
     * Component in row 1, column 3. The translation y component.
     */
    public float m13 = 0.0f;

    /**
     * Component in row 2, column 0. The right axis z component.
     */
    public float m20 = 0.0f;

    /**
     * Component in row 2, column 1. The forward axis z component.
     */
    public float m21 = 0.0f;

    /**
     * Component in row 2, column 2. The up axis z component.
     */
    public float m22 = 1.0f;

    /**
     * Component in row 2, column 3. The translation z component.
     */
    public float m23 = 0.0f;

    /**
     * Component in row 3, column 0. The right axis w component.
     */
    public float m30 = 0.0f;

    /**
     * Component in row 3, column 1. The forward axis w component.
     */
    public float m31 = 0.0f;

    /**
     * Component in row 3, column 2. The up axis w component.
     */
    public float m32 = 0.0f;

    /**
     * Component in row 3, column 3. The translation w component.
     */
    public float m33 = 1.0f;

    /**
     * The default constructor. Creates an identity matrix.
     */
    public Mat4() {

        // TODO: Include Bradford chromatic adaptation matrix?
        // http://www.brucelindbloom.com/index.html?Eqn_ChromAdapt.html
    }

    /**
     * Constructs a matrix from boolean values.
     *
     * @param m00 row 0, column 0
     * @param m01 row 0, column 1
     * @param m02 row 0, column 2
     * @param m03 row 0, column 3
     * @param m10 row 1, column 0
     * @param m11 row 1, column 1
     * @param m12 row 1, column 2
     * @param m13 row 1, column 3
     * @param m20 row 2, column 0
     * @param m21 row 2, column 1
     * @param m22 row 2, column 2
     * @param m23 row 2, column 3
     * @param m30 row 3, column 0
     * @param m31 row 3, column 1
     * @param m32 row 3, column 2
     * @param m33 row 3, column 3
     */
    public Mat4(
        final boolean m00, final boolean m01, final boolean m02, final boolean m03,
        final boolean m10, final boolean m11, final boolean m12, final boolean m13,
        final boolean m20, final boolean m21, final boolean m22, final boolean m23,
        final boolean m30, final boolean m31, final boolean m32, final boolean m33) {

        this.set(
            m00, m01, m02, m03,
            m10, m11, m12, m13,
            m20, m21, m22, m23,
            m30, m31, m32, m33);
    }

    /**
     * Constructs a matrix from float values.
     *
     * @param m00 row 0, column 0
     * @param m01 row 0, column 1
     * @param m10 row 1, column 0
     * @param m11 row 1, column 1
     */
    public Mat4(
        final float m00, final float m01,
        final float m10, final float m11) {

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
     * @param m20 row 2, column 0
     * @param m21 row 2, column 1
     * @param m22 row 2, column 2
     */
    public Mat4(
        final float m00, final float m01, final float m02,
        final float m10, final float m11, final float m12,
        final float m20, final float m21, final float m22) {

        this.set(m00, m01, m02, m10, m11, m12, m20, m21, m22);
    }

    /**
     * Constructs a matrix from float values.
     *
     * @param m00 row 0, column 0
     * @param m01 row 0, column 1
     * @param m02 row 0, column 2
     * @param m03 row 0, column 3
     * @param m10 row 1, column 0
     * @param m11 row 1, column 1
     * @param m12 row 1, column 2
     * @param m13 row 1, column 3
     * @param m20 row 2, column 0
     * @param m21 row 2, column 1
     * @param m22 row 2, column 2
     * @param m23 row 2, column 3
     */
    public Mat4(
        final float m00, final float m01, final float m02, final float m03,
        final float m10, final float m11, final float m12, final float m13,
        final float m20, final float m21, final float m22, final float m23) {

        this.set(
            m00, m01, m02, m03,
            m10, m11, m12, m13,
            m20, m21, m22, m23);
    }

    /**
     * Constructs a matrix from float values.
     *
     * @param m00 row 0, column 0
     * @param m01 row 0, column 1
     * @param m02 row 0, column 2
     * @param m03 row 0, column 3
     * @param m10 row 1, column 0
     * @param m11 row 1, column 1
     * @param m12 row 1, column 2
     * @param m13 row 1, column 3
     * @param m20 row 2, column 0
     * @param m21 row 2, column 1
     * @param m22 row 2, column 2
     * @param m23 row 2, column 3
     * @param m30 row 3, column 0
     * @param m31 row 3, column 1
     * @param m32 row 3, column 2
     * @param m33 row 3, column 3
     */
    public Mat4(
        final float m00, final float m01, final float m02, final float m03,
        final float m10, final float m11, final float m12, final float m13,
        final float m20, final float m21, final float m22, final float m23,
        final float m30, final float m31, final float m32, final float m33) {

        this.set(
            m00, m01, m02, m03,
            m10, m11, m12, m13,
            m20, m21, m22, m23,
            m30, m31, m32, m33);
    }

    /**
     * Constructs a matrix from a source matrix's components.
     *
     * @param source the source matrix
     */
    public Mat4(final Mat4 source) {

        this.m00 = source.m00;
        this.m01 = source.m01;
        this.m02 = source.m02;
        this.m03 = source.m03;

        this.m10 = source.m10;
        this.m11 = source.m11;
        this.m12 = source.m12;
        this.m13 = source.m13;

        this.m20 = source.m20;
        this.m21 = source.m21;
        this.m22 = source.m22;
        this.m23 = source.m23;

        this.m30 = source.m30;
        this.m31 = source.m31;
        this.m32 = source.m32;
        this.m33 = source.m33;
    }

    /**
     * Adds two matrices together.
     *
     * @param a      the left operand
     * @param b      the right operand
     * @param target the output matrix
     * @return the sum
     */
    public static Mat4 add(final Mat4 a, final Mat4 b, final Mat4 target) {

        return target.set(
            a.m00 + b.m00, a.m01 + b.m01, a.m02 + b.m02, a.m03 + b.m03,
            a.m10 + b.m10, a.m11 + b.m11, a.m12 + b.m12, a.m13 + b.m13,
            a.m20 + b.m20, a.m21 + b.m21, a.m22 + b.m22, a.m23 + b.m23,
            a.m30 + b.m30, a.m31 + b.m31, a.m32 + b.m32, a.m33 + b.m33);
    }

    /**
     * Tests to see if all the matrix's components are non-zero.
     *
     * @param m the input matrix
     * @return the evaluation
     */
    public static boolean all(final Mat4 m) {

        return m.m00 != 0.0f && m.m01 != 0.0f && m.m02 != 0.0f && m.m03 != 0.0f
            && m.m10 != 0.0f && m.m11 != 0.0f && m.m12 != 0.0f && m.m13 != 0.0f
            && m.m20 != 0.0f && m.m21 != 0.0f && m.m22 != 0.0f && m.m23 != 0.0f
            && m.m30 != 0.0f && m.m31 != 0.0f && m.m32 != 0.0f && m.m33 != 0.0f;
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
    public static Mat4 and(final Mat4 a, final Mat4 b, final Mat4 target) {

        return target.set(
            Utils.and(a.m00, b.m00), Utils.and(a.m01, b.m01),
            Utils.and(a.m02, b.m02), Utils.and(a.m03, b.m03),
            Utils.and(a.m10, b.m10), Utils.and(a.m11, b.m11),
            Utils.and(a.m12, b.m12), Utils.and(a.m13, b.m13),
            Utils.and(a.m20, b.m20), Utils.and(a.m21, b.m21),
            Utils.and(a.m22, b.m22), Utils.and(a.m23, b.m23),
            Utils.and(a.m30, b.m30), Utils.and(a.m31, b.m31),
            Utils.and(a.m32, b.m32), Utils.and(a.m33, b.m33));
    }

    /**
     * Tests to see if any of the matrix's components are non-zero.
     *
     * @param m the input matrix
     * @return the evaluation
     */
    public static boolean any(final Mat4 m) {

        return m.m00 != 0.0f || m.m01 != 0.0f || m.m02 != 0.0f || m.m03 != 0.0f
            || m.m10 != 0.0f || m.m11 != 0.0f || m.m12 != 0.0f || m.m13 != 0.0f
            || m.m20 != 0.0f || m.m21 != 0.0f || m.m22 != 0.0f || m.m23 != 0.0f
            || m.m30 != 0.0f || m.m31 != 0.0f || m.m32 != 0.0f || m.m33 != 0.0f;
    }

    /**
     * Returns a matrix set to the Bézier curve basis:
     *
     * <pre>
     * -1.0,  3.0, -3.0, 1.0,
     *  3.0, -6.0,  3.0, 0.0,
     * -3.0,  3.0,  0.0, 0.0,
     *  1.0,  0.0,  0.0, 0.0
     * </pre>
     *
     * @param target the output matrix
     * @return the Bezier basis
     */
    public static Mat4 bezierBasis(final Mat4 target) {

        return target.set(
            -1.0f, 3.0f, -3.0f, 1.0f,
            3.0f, -6.0f, 3.0f, 0.0f,
            -3.0f, 3.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f, 0.0f);
    }

    /**
     * Returns a matrix set to the Bézier curve basis inverse:
     *
     * <pre>
     * 0.0,        0.0,        0.0, 1.0,
     * 0.0,        0.0, 0.33333334, 1.0,
     * 0.0, 0.33333334, 0.66666666, 1.0,
     * 1.0,        1.0,        1.0, 1.0
     * </pre>
     *
     * @param target the output matrix
     * @return the Bezier basis
     */
    public static Mat4 bezierBasisInverse(final Mat4 target) {

        return target.set(
            0.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, Utils.ONE_THIRD, 1.0f,
            0.0f, Utils.ONE_THIRD, Utils.TWO_THIRDS, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f);
    }

    /**
     * Generates an orbiting camera matrix. The camera looks from its location
     * at its focal target with reference to the world up axis, which is
     * usually (0.0, 1.0, 0.0) or (0.0, 0.0, 1.0).
     *
     * @param loc        the camera location
     * @param focus      the target location
     * @param ref        the reference up
     * @param handedness the handedness
     * @param target     the output matrix
     * @param i          the right axis
     * @param j          the forward axis
     * @param k          the up axis
     * @return the camera matrix
     * @see Mat4#fromTranslation(Vec3, Mat4)
     * @see Vec3#crossNorm(Vec3, Vec3, Vec3)
     * @see Vec3#dot(Vec3, Vec3)
     * @see Vec3#subNorm(Vec3, Vec3, Vec3)
     */
    public static Mat4 camera(
        final Vec3 loc,
        final Vec3 focus,
        final Vec3 ref,
        final Handedness handedness,
        final Mat4 target,
        final Vec3 i,
        final Vec3 j,
        final Vec3 k) {

        /* Test to see if forward is parallel to reference. */
        Vec3.subNorm(loc, focus, k);
        final float dotp = Vec3.dot(k, ref);
        final float tol = 1.0f - Utils.EPSILON;
        if (dotp < -tol || dotp > tol) {
            return Mat4.fromTranslation(loc, target);
        }

        if (handedness == Handedness.LEFT) {
            Vec3.crossNorm(k, ref, i);
            Vec3.crossNorm(i, k, j);
        } else {
            Vec3.crossNorm(ref, k, i);
            Vec3.crossNorm(k, i, j);
        }

        return target.set(
            i.x, i.y, i.z,
            -loc.x * i.x - loc.y * i.y - loc.z * i.z,
            j.x, j.y, j.z,
            -loc.x * j.x - loc.y * j.y - loc.z * j.z,
            k.x, k.y, k.z,
            -loc.x * k.x - loc.y * k.y - loc.z * k.z,
            0.0f, 0.0f, 0.0f, 1.0f);

    }

    /**
     * Decomposes a matrix into its constituent transforms: translation,
     * rotation and scale. Rotation is returned from the function, while
     * translation and scale are loaded into out parameters.
     *
     * @param m     the matrix
     * @param trans the output translation
     * @param rot   the output rotation
     * @param scale the output scale
     * @see Mat4#determinant(Mat4)
     * @see Quaternion#fromAxes(float, float, float, float, float, float, float,
     * float, float, Quaternion)
     * @see Utils#div(float, float)
     * @see Utils#hypot(float, float, float)
     */
    public static void decompose(
        final Mat4 m,
        final Vec3 trans,
        final Quaternion rot,
        final Vec3 scale) {

        final float xMag = Utils.hypot(m.m00, m.m10, m.m20);
        final float yMag = Utils.hypot(m.m01, m.m11, m.m21);
        final float zMag = Utils.hypot(m.m02, m.m12, m.m22);
        final float det = Mat4.determinant(m);

        /*
         * Extract rotation matrix from affine transform matrix by dividing
         * each axis by the scale.
         */
        final float sxInv = Utils.div(1.0f, xMag);
        final float syInv = Utils.div(1.0f, yMag);
        final float szInv = Utils.div(1.0f, zMag);

        Quaternion.fromAxes(
            m.m00 * sxInv, m.m11 * syInv, m.m22 * szInv,
            m.m21 * syInv, m.m12 * szInv, m.m02 * szInv,
            m.m20 * sxInv, m.m10 * sxInv, m.m01 * syInv,
            rot);
        scale.set(xMag, det < 0.0f ? -yMag : yMag, zMag);
        trans.set(m.m03, m.m13, m.m23);
    }

    /**
     * Finds the determinant of the matrix.
     *
     * @param m the matrix
     * @return the determinant
     */
    public static float determinant(final Mat4 m) {

        return m.m00
            * (m.m11 * m.m22 * m.m33
            + m.m12 * m.m23 * m.m31
            + m.m13 * m.m21 * m.m32
            - m.m13 * m.m22 * m.m31
            - m.m11 * m.m23 * m.m32
            - m.m12 * m.m21 * m.m33)
            - m.m01
            * (m.m10 * m.m22 * m.m33
            + m.m12 * m.m23 * m.m30
            + m.m13 * m.m20 * m.m32
            - m.m13 * m.m22 * m.m30
            - m.m10 * m.m23 * m.m32
            - m.m12 * m.m20 * m.m33)
            + m.m02
            * (m.m10 * m.m21 * m.m33
            + m.m11 * m.m23 * m.m30
            + m.m13 * m.m20 * m.m31
            - m.m13 * m.m21 * m.m30
            - m.m10 * m.m23 * m.m31
            - m.m11 * m.m20 * m.m33)
            - m.m03
            * (m.m10 * m.m21 * m.m32
            + m.m11 * m.m22 * m.m30
            + m.m12 * m.m20 * m.m31
            - m.m12 * m.m21 * m.m30
            - m.m10 * m.m22 * m.m31
            - m.m11 * m.m20 * m.m32);
    }

    /**
     * Creates a matrix from two axes. The third axis, up, is assumed to be
     * (0.0, 0.0, 1.0, 0.0). The fourth row and column are assumed to be
     * (0.0, 0.0, 0.0, 1.0).
     *
     * @param right   the right axis
     * @param forward the forward axis
     * @param target  the output matrix
     * @return the matrix
     */
    public static Mat4 fromAxes(
        final Vec2 right,
        final Vec2 forward,
        final Mat4 target) {

        return target.set(
            right.x, forward.x, 0.0f, 0.0f,
            right.y, forward.y, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Creates a matrix from two axes and a translation. The third axis, up, is
     * assumed to be (0.0, 0.0, 1.0, 0.0). The fourth row, w, is assumed to be
     * (0.0, 0.0, 0.0, 1.0).
     *
     * @param right       the right axis
     * @param forward     the forward axis
     * @param translation the translation
     * @param target      the output matrix
     * @return the matrix
     */
    public static Mat4 fromAxes(
        final Vec2 right,
        final Vec2 forward,
        final Vec2 translation,
        final Mat4 target) {

        return target.set(
            right.x, forward.x, 0.0f, translation.x,
            right.y, forward.y, 0.0f, translation.y,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Creates a matrix from three axes. The fourth row and column are assumed
     * to be (0.0, 0.0, 0.0, 1.0).
     *
     * @param right   the right axis
     * @param forward the forward axis
     * @param up      the up axis
     * @param target  the output matrix
     * @return the matrix
     */
    public static Mat4 fromAxes(
        final Vec3 right,
        final Vec3 forward,
        final Vec3 up,
        final Mat4 target) {

        return target.set(
            right.x, forward.x, up.x, 0.0f,
            right.y, forward.y, up.y, 0.0f,
            right.z, forward.z, up.z, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Creates a matrix from three axes and a translation. The fourth row, w,
     * is assumed to be (0.0, 0.0, 0.0, 1.0).
     *
     * @param right       the right axis
     * @param forward     the forward axis
     * @param up          the up axis
     * @param translation the translation
     * @param target      the output matrix
     * @return the matrix
     */
    public static Mat4 fromAxes(
        final Vec3 right,
        final Vec3 forward,
        final Vec3 up,
        final Vec3 translation,
        final Mat4 target) {

        return target.set(
            right.x, forward.x, up.x, translation.x,
            right.y, forward.y, up.y, translation.y,
            right.z, forward.z, up.z, translation.z,
            0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Creates a matrix from three axes. The fourth column, translation, is
     * assumed to be (0.0, 0.0, 0.0, 1.0).
     *
     * @param right   the right axis
     * @param forward the forward axis
     * @param up      the up axis
     * @param target  the output matrix
     * @return the matrix
     */
    public static Mat4 fromAxes(
        final Vec4 right,
        final Vec4 forward,
        final Vec4 up,
        final Mat4 target) {

        return target.set(
            right.x, forward.x, up.x, 0.0f,
            right.y, forward.y, up.y, 0.0f,
            right.z, forward.z, up.z, 0.0f,
            right.w, forward.w, up.w, 1.0f);
    }

    /**
     * Creates a matrix from three axes and a translation.
     *
     * @param right       the right axis
     * @param forward     the forward axis
     * @param up          the up axis
     * @param translation the translation
     * @param target      the output matrix
     * @return the matrix
     */
    public static Mat4 fromAxes(
        final Vec4 right,
        final Vec4 forward,
        final Vec4 up,
        final Vec4 translation,
        final Mat4 target) {

        return target.set(
            right.x, forward.x, up.x, translation.x,
            right.y, forward.y, up.y, translation.y,
            right.z, forward.z, up.z, translation.z,
            right.w, forward.w, up.w, translation.w);
    }

    /**
     * Creates a reflection matrix from an axis representing a plane. The axis
     * will be normalized by the function.
     *
     * @param axis   the axis
     * @param target the output matrix
     * @return the reflection
     * @see Mat4#fromReflection(float, float, float, Mat4)
     * @see Mat4#identity(Mat4)
     */
    public static Mat4 fromReflection(final Vec3 axis, final Mat4 target) {

        final float ax = axis.x;
        final float ay = axis.y;
        final float az = axis.z;
        final float mSq = ax * ax + ay * ay + az * az;
        if (mSq != 0.0f) {
            final float mInv = Utils.invSqrtUnchecked(mSq);
            return Mat4.fromReflection(ax * mInv, ay * mInv, az * mInv, target);
        }
        return Mat4.identity(target);
    }

    /**
     * Creates a rotation matrix from an angle in radians around an axis. The
     * axis will not be checked for validity.
     *
     * @param cosa   cosine of the angle
     * @param sina   sine of the angle
     * @param axis   the axis
     * @param target the output matrix
     * @return the matrix
     * @see Mat4#fromRotation(float, float, float, float, float, Mat4)
     */
    public static Mat4 fromRotation(
        final float cosa,
        final float sina,
        final Vec3 axis,
        final Mat4 target) {

        return Mat4.fromRotation(cosa, sina, axis.x, axis.y, axis.z, target);
    }

    /**
     * Creates a rotation matrix from an angle in radians around an axis. The
     * axis will be normalized by the function.
     *
     * @param radians the angle
     * @param axis    the axis
     * @param target  the output matrix
     * @return the matrix
     * @see Mat4#fromRotation(float, float, float, float, float, Mat4)
     * @see Mat4#identity(Mat4)
     */
    public static Mat4 fromRotation(
        final float radians,
        final Vec3 axis,
        final Mat4 target) {

        final float ax = axis.x;
        final float ay = axis.y;
        final float az = axis.z;
        final float mSq = ax * ax + ay * ay + az * az;
        if (mSq != 0.0f) {
            final float mInv = Utils.invSqrtUnchecked(mSq);
            final float norm = radians * Utils.ONE_TAU;
            return Mat4.fromRotation(
                Utils.scNorm(norm), Utils.scNorm(norm - 0.25f),
                ax * mInv, ay * mInv, az * mInv, target);
        }
        return Mat4.identity(target);
    }

    /**
     * Creates a rotation matrix from a quaternion.
     *
     * @param source the quaternion
     * @param target the output matrix
     * @return the matrix
     */
    public static Mat4 fromRotation(final Quaternion source, final Mat4 target) {

        final float w = source.real;
        final Vec3 i = source.imag;
        final float x = i.x;
        final float y = i.y;
        final float z = i.z;

        final float x2 = x + x;
        final float y2 = y + y;
        final float z2 = z + z;
        final float xsq2 = x * x2;
        final float ysq2 = y * y2;
        final float zsq2 = z * z2;
        final float xy2 = x * y2;
        final float xz2 = x * z2;
        final float yz2 = y * z2;
        final float wx2 = w * x2;
        final float wy2 = w * y2;
        final float wz2 = w * z2;

        return target.set(
            1.0f - ysq2 - zsq2, xy2 - wz2, xz2 + wy2, 0.0f,
            xy2 + wz2, 1.0f - xsq2 - zsq2, yz2 - wx2, 0.0f,
            xz2 - wy2, yz2 + wx2, 1.0f - xsq2 - ysq2, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Creates a rotation matrix from a cosine and sine around the x axis.
     *
     * @param cosa   the cosine of an angle
     * @param sina   the sine of an angle
     * @param target the output matrix
     * @return the matrix
     */
    public static Mat4 fromRotX(
        final float cosa,
        final float sina,
        final Mat4 target) {

        return target.set(
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, cosa, -sina, 0.0f,
            0.0f, sina, cosa, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Creates a rotation matrix from an angle in radians around the x axis.
     *
     * @param radians the angle
     * @param target  the output matrix
     * @return the matrix
     */
    public static Mat4 fromRotX(final float radians, final Mat4 target) {

        final float norm = radians * Utils.ONE_TAU;
        return Mat4.fromRotX(Utils.scNorm(norm), Utils.scNorm(norm - 0.25f), target);
    }

    /**
     * Creates a rotation matrix from a cosine and sine around the y axis.
     *
     * @param cosa   the cosine of an angle
     * @param sina   the sine of an angle
     * @param target the output matrix
     * @return the matrix
     */
    public static Mat4 fromRotY(
        final float cosa,
        final float sina,
        final Mat4 target) {

        // RESEARCH: Is this inconsistent when compared with 4D rotation about
        // the YW axis? Should sin(a) and -sin(a) be transposed?

        return target.set(
            cosa, 0.0f, sina, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            -sina, 0.0f, cosa, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Creates a rotation matrix from an angle in radians around the y axis.
     *
     * @param radians the angle
     * @param target  the output matrix
     * @return the matrix
     */
    public static Mat4 fromRotY(final float radians, final Mat4 target) {

        final float norm = radians * Utils.ONE_TAU;
        return Mat4.fromRotY(Utils.scNorm(norm), Utils.scNorm(norm - 0.25f), target);
    }

    /**
     * Creates a rotation matrix from a cosine and sine around the z axis.
     *
     * @param cosa   the cosine of an angle
     * @param sina   the sine of an angle
     * @param target the output matrix
     * @return the matrix
     */
    public static Mat4 fromRotZ(
        final float cosa,
        final float sina,
        final Mat4 target) {

        return target.set(
            cosa, -sina, 0.0f, 0.0f,
            sina, cosa, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Creates a rotation matrix from an angle in radians around the z axis.
     *
     * @param radians the angle
     * @param target  the output matrix
     * @return the matrix
     */
    public static Mat4 fromRotZ(final float radians, final Mat4 target) {

        final float norm = radians * Utils.ONE_TAU;
        return Mat4.fromRotZ(
            Utils.scNorm(norm),
            Utils.scNorm(norm - 0.25f),
            target);
    }

    /**
     * Creates a scale matrix from a scalar. The bottom right corner, m33, is
     * set to 1.0. Returns the identity if the scalar is zero.
     *
     * @param scalar the scalar
     * @param target the output matrix
     * @return the matrix
     * @see Mat4#identity(Mat4)
     */
    public static Mat4 fromScale(final float scalar, final Mat4 target) {

        if (scalar != 0.0f) {
            return target.set(
                scalar, 0.0f, 0.0f, 0.0f,
                0.0f, scalar, 0.0f, 0.0f,
                0.0f, 0.0f, scalar, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f);
        }
        return Mat4.identity(target);
    }

    /**
     * Creates a scale matrix from a nonuniform scalar stored in a vector. The
     * scale on the z axis is assumed to be 1.0 . Returns the identity if the
     * scalar is zero.
     *
     * @param scalar the nonuniform scalar
     * @param target the output matrix
     * @return the matrix
     * @see Vec2#all(Vec2)
     * @see Mat4#identity(Mat4)
     */
    public static Mat4 fromScale(final Vec2 scalar, final Mat4 target) {

        if (Vec2.all(scalar)) {
            return target.set(
                scalar.x, 0.0f, 0.0f, 0.0f,
                0.0f, scalar.y, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f);
        }
        return Mat4.identity(target);
    }

    /**
     * Creates a scale matrix from a nonuniform scalar stored in a vector.
     * Returns the identity if the scalar is zero.
     *
     * @param scalar the nonuniform scalar
     * @param target the output matrix
     * @return the matrix
     * @see Vec3#all(Vec3)
     * @see Mat4#identity(Mat4)
     */
    public static Mat4 fromScale(final Vec3 scalar, final Mat4 target) {

        if (Vec3.all(scalar)) {
            return target.set(
                scalar.x, 0.0f, 0.0f, 0.0f,
                0.0f, scalar.y, 0.0f, 0.0f,
                0.0f, 0.0f, scalar.z, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f);
        }
        return Mat4.identity(target);
    }

    /**
     * Creates skew, or shear, matrix from an angle and axes. Vectors
     * <em>a</em> and <em>b</em> are expected to be orthonormal, i.e.,
     * perpendicular and of unit length. Returns the identity if the angle
     * is divisible by pi.
     *
     * @param radians the angle in radians
     * @param a       the skew axis
     * @param b       the orthonormal axis
     * @param target  the output matrix
     * @return the skew matrix
     * @see Mat4#fromSkew(float, float, float, float, float, float, float, Mat4)
     * @see Mat4#identity(Mat4)
     * @see Utils#approx(float, float)
     * @see Utils#invSqrtUnchecked(float)
     * @see Utils#mod(float, float)
     * @see Utils#tan(float)
     */
    public static Mat4 fromSkew(
        final float radians,
        final Vec3 a,
        final Vec3 b,
        final Mat4 target) {

        if (Utils.approx(Utils.mod(radians, Utils.PI), 0.0f)) {
            return Mat4.identity(target);
        }

        final float ax = a.x;
        final float ay = a.y;
        final float az = a.z;
        final float amSq = ax * ax + ay * ay + az * az;
        if (amSq <= Utils.EPSILON) {
            return Mat4.identity(target);
        }

        final float bx = b.x;
        final float by = b.y;
        final float bz = b.z;
        final float bmSq = bx * bx + by * by + bz * bz;
        if (bmSq <= Utils.EPSILON) {
            return Mat4.identity(target);
        }

        final float t = Utils.tan(radians);
        final float amInv = Utils.invSqrtUnchecked(amSq);
        final float bmInv = Utils.invSqrtUnchecked(bmSq);

        return Mat4.fromSkew(
            t,
            ax * amInv, ay * amInv, az * amInv,
            bx * bmInv, by * bmInv, bz * bmInv,
            target);
    }

    /**
     * Creates a matrix from spherical coordinates. The matrix's right axis
     * (m00, m10, m20) corresponds to the point on the sphere. The radius acts
     * as a scalar by which all matrix entries are multiplied. A radius of zero
     * will return the identity matrix.
     *
     * @param azimuth     the angle theta in radians
     * @param inclination the angle phi in radians
     * @param radius      rho, the magnitude
     * @param target      the output matrix
     * @return the matrix
     */
    public static Mat4 fromSpherical(
        final float azimuth,
        final float inclination,
        final float radius,
        final Mat4 target) {

        if (radius == 0.0f) {
            return Mat4.identity(target);
        }

        final float azimNorm = azimuth * Utils.ONE_TAU;
        final float cosAzim = Utils.scNorm(azimNorm);
        final float sinAzim = Utils.scNorm(azimNorm - 0.25f);

        final float inclNorm = inclination * Utils.ONE_TAU;
        final float cosIncl = Utils.scNorm(inclNorm);
        final float sinIncl = Utils.scNorm(inclNorm - 0.25f);

        final float rhoCosIncl = radius * cosIncl;
        final float rhoSinIncl = radius * sinIncl;

        return target.set(
            rhoCosIncl * cosAzim, radius * -sinAzim, -rhoSinIncl * cosAzim, 0.0f,
            rhoCosIncl * sinAzim, radius * cosAzim, -rhoSinIncl * sinAzim, 0.0f,
            rhoSinIncl, 0.0f, rhoCosIncl, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Creates a translation matrix from a vector.
     *
     * @param translation the translation
     * @param target      the output matrix
     * @return the matrix
     */
    public static Mat4 fromTranslation(
        final Vec2 translation,
        final Mat4 target) {

        return target.set(
            1.0f, 0.0f, 0.0f, translation.x,
            0.0f, 1.0f, 0.0f, translation.y,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Creates a translation matrix from a vector.
     *
     * @param translation the translation
     * @param target      the output matrix
     * @return the matrix
     */
    public static Mat4 fromTranslation(
        final Vec3 translation,
        final Mat4 target) {

        return target.set(
            1.0f, 0.0f, 0.0f, translation.x,
            0.0f, 1.0f, 0.0f, translation.y,
            0.0f, 0.0f, 1.0f, translation.z,
            0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Creates a view frustum given the edges of the view port.
     *
     * @param left   the left edge of the window
     * @param right  the right edge of the window
     * @param bottom the bottom edge of the window
     * @param top    the top edge of the window
     * @param near   the near clip plane
     * @param far    the far clip plane
     * @param target the output matrix
     * @return the view frustum
     */
    public static Mat4 frustum(
        final float left,
        final float right,
        final float bottom,
        final float top,
        final float near,
        final float far,
        final Mat4 target) {

        final float n2 = near + near;

        float w = right - left;
        float h = top - bottom;
        float d = far - near;

        w = w != 0.0f ? 1.0f / w : 1.0f;
        h = h != 0.0f ? 1.0f / h : 1.0f;
        d = d != 0.0f ? 1.0f / d : 1.0f;

        return target.set(
            n2 * w, 0.0f, (right + left) * w, 0.0f,
            0.0f, n2 * h, (top + bottom) * h, 0.0f,
            0.0f, 0.0f, (far + near) * -d, n2 * far * -d,
            0.0f, 0.0f, -1.0f, 0.0f);
    }

    /**
     * Evaluates whether the left comparisand is greater than the right
     * comparisand.
     *
     * @param a      left comparisand
     * @param b      right comparisand
     * @param target the output matrix
     * @return the evaluation
     */
    public static Mat4 gt(final Mat4 a, final Mat4 b, final Mat4 target) {

        return target.set(
            a.m00 > b.m00, a.m01 > b.m01, a.m02 > b.m02, a.m03 > b.m03,
            a.m10 > b.m10, a.m11 > b.m11, a.m12 > b.m12, a.m13 > b.m13,
            a.m20 > b.m20, a.m21 > b.m21, a.m22 > b.m22, a.m23 > b.m23,
            a.m30 > b.m30, a.m31 > b.m31, a.m32 > b.m32, a.m33 > b.m33);
    }

    /**
     * Evaluates whether the left comparisand is greater than or equal to the
     * right comparisand.
     *
     * @param a      left comparisand
     * @param b      right comparisand
     * @param target the output matrix
     * @return the evaluation
     */
    public static Mat4 gtEq(final Mat4 a, final Mat4 b, final Mat4 target) {

        return target.set(
            a.m00 >= b.m00, a.m01 >= b.m01, a.m02 >= b.m02, a.m03 >= b.m03,
            a.m10 >= b.m10, a.m11 >= b.m11, a.m12 >= b.m12, a.m13 >= b.m13,
            a.m20 >= b.m20, a.m21 >= b.m21, a.m22 >= b.m22, a.m23 >= b.m23,
            a.m30 >= b.m30, a.m31 >= b.m31, a.m32 >= b.m32, a.m33 >= b.m33);
    }

    /**
     * Returns the identity matrix:
     *
     * <pre>
     * 1.0, 0.0, 0.0, 0.0,
     * 0.0, 1.0, 0.0, 0.0,
     * 0.0, 0.0, 1.0, 0.0,
     * 0.0, 0.0, 0.0, 1.0
     * </pre>
     *
     * @param target the output matrix
     * @return the identity matrix
     */
    public static Mat4 identity(final Mat4 target) {

        return target.set(
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Inverts the input matrix. Returns the identity if the matrix's
     * determinant is zero.
     *
     * @param m      the matrix
     * @param target the output matrix
     * @return the inverse
     * @see Mat4#identity(Mat4)
     */
    public static Mat4 inverse(final Mat4 m, final Mat4 target) {

        final float b00 = m.m00 * m.m11 - m.m01 * m.m10;
        final float b01 = m.m00 * m.m12 - m.m02 * m.m10;
        final float b02 = m.m00 * m.m13 - m.m03 * m.m10;
        final float b03 = m.m01 * m.m12 - m.m02 * m.m11;
        final float b04 = m.m01 * m.m13 - m.m03 * m.m11;
        final float b05 = m.m02 * m.m13 - m.m03 * m.m12;
        final float b06 = m.m20 * m.m31 - m.m21 * m.m30;
        final float b07 = m.m20 * m.m32 - m.m22 * m.m30;
        final float b08 = m.m20 * m.m33 - m.m23 * m.m30;
        final float b09 = m.m21 * m.m32 - m.m22 * m.m31;
        final float b10 = m.m21 * m.m33 - m.m23 * m.m31;
        final float b11 = m.m22 * m.m33 - m.m23 * m.m32;

        final float det = b00 * b11
            - b01 * b10
            + b02 * b09
            + b03 * b08
            - b04 * b07
            + b05 * b06;
        if (det != 0.0f) {
            final float detInv = 1.0f / det;
            return target.set(
                (m.m11 * b11 - m.m12 * b10 + m.m13 * b09) * detInv,
                (m.m02 * b10 - m.m01 * b11 - m.m03 * b09) * detInv,
                (m.m31 * b05 - m.m32 * b04 + m.m33 * b03) * detInv,
                (m.m22 * b04 - m.m21 * b05 - m.m23 * b03) * detInv,
                (m.m12 * b08 - m.m10 * b11 - m.m13 * b07) * detInv,
                (m.m00 * b11 - m.m02 * b08 + m.m03 * b07) * detInv,
                (m.m32 * b02 - m.m30 * b05 - m.m33 * b01) * detInv,
                (m.m20 * b05 - m.m22 * b02 + m.m23 * b01) * detInv,
                (m.m10 * b10 - m.m11 * b08 + m.m13 * b06) * detInv,
                (m.m01 * b08 - m.m00 * b10 - m.m03 * b06) * detInv,
                (m.m30 * b04 - m.m31 * b02 + m.m33 * b00) * detInv,
                (m.m21 * b02 - m.m20 * b04 - m.m23 * b00) * detInv,
                (m.m11 * b07 - m.m10 * b09 - m.m12 * b06) * detInv,
                (m.m00 * b09 - m.m01 * b07 + m.m02 * b06) * detInv,
                (m.m31 * b01 - m.m30 * b03 - m.m32 * b00) * detInv,
                (m.m20 * b03 - m.m21 * b01 + m.m22 * b00) * detInv);
        }

        return Mat4.identity(target);
    }

    /**
     * Tests to see if a matrix is the identity matrix.
     *
     * @param m the matrix
     * @return the evaluation
     */
    public static boolean isIdentity(final Mat4 m) {

        return m.m33 == 1.0f && m.m22 == 1.0f && m.m11 == 1.0f && m.m00 == 1.0f
            && m.m01 == 0.0f && m.m02 == 0.0f && m.m03 == 0.0f && m.m10 == 0.0f
            && m.m12 == 0.0f && m.m13 == 0.0f && m.m20 == 0.0f && m.m21 == 0.0f
            && m.m23 == 0.0f && m.m30 == 0.0f && m.m31 == 0.0f && m.m32 == 0.0f;
    }

    /**
     * Evaluates whether the left comparisand is less than the right
     * comparisand.
     *
     * @param a      left comparisand
     * @param b      right comparisand
     * @param target the output matrix
     * @return the evaluation
     */
    public static Mat4 lt(final Mat4 a, final Mat4 b, final Mat4 target) {

        return target.set(
            a.m00 < b.m00, a.m01 < b.m01, a.m02 < b.m02, a.m03 < b.m03,
            a.m10 < b.m10, a.m11 < b.m11, a.m12 < b.m12, a.m13 < b.m13,
            a.m20 < b.m20, a.m21 < b.m21, a.m22 < b.m22, a.m23 < b.m23,
            a.m30 < b.m30, a.m31 < b.m31, a.m32 < b.m32, a.m33 < b.m33);
    }

    /**
     * Evaluates whether the left comparisand is less than or equal to the
     * right comparisand.
     *
     * @param a      left comparisand
     * @param b      right comparisand
     * @param target the output matrix
     * @return the evaluation
     */
    public static Mat4 ltEq(final Mat4 a, final Mat4 b, final Mat4 target) {

        return target.set(
            a.m00 <= b.m00, a.m01 <= b.m01, a.m02 <= b.m02, a.m03 <= b.m03,
            a.m10 <= b.m10, a.m11 <= b.m11, a.m12 <= b.m12, a.m13 <= b.m13,
            a.m20 <= b.m20, a.m21 <= b.m21, a.m22 <= b.m22, a.m23 <= b.m23,
            a.m30 <= b.m30, a.m31 <= b.m31, a.m32 <= b.m32, a.m33 <= b.m33);
    }

    /**
     * Multiplies each component in a matrix by a scalar. Not to be confused
     * with scaling affine transform matrix.
     *
     * @param s      the left operand
     * @param m      the right operand
     * @param target the output matrix
     * @return the product
     */
    public static Mat4 mul(final float s, final Mat4 m, final Mat4 target) {

        return target.set(
            s * m.m00, s * m.m01, s * m.m02, s * m.m03,
            s * m.m10, s * m.m11, s * m.m12, s * m.m13,
            s * m.m20, s * m.m21, s * m.m22, s * m.m23,
            s * m.m30, s * m.m31, s * m.m32, s * m.m33);
    }

    /**
     * Multiplies each component in a matrix by a scalar. Not to be confused
     * with scaling affine transform matrix.
     *
     * @param m      the left operand
     * @param s      the right operand
     * @param target the output matrix
     * @return the product
     */
    public static Mat4 mul(final Mat4 m, final float s, final Mat4 target) {

        return target.set(
            m.m00 * s, m.m01 * s, m.m02 * s, m.m03 * s,
            m.m10 * s, m.m11 * s, m.m12 * s, m.m13 * s,
            m.m20 * s, m.m21 * s, m.m22 * s, m.m23 * s,
            m.m30 * s, m.m31 * s, m.m32 * s, m.m33 * s);
    }

    /**
     * Multiplies two matrices.
     *
     * @param a      the left operand
     * @param b      the right operand
     * @param target the output matrix
     * @return the product
     */
    public static Mat4 mul(final Mat4 a, final Mat4 b, final Mat4 target) {

        return target.set(
            a.m00 * b.m00 + a.m01 * b.m10 + a.m02 * b.m20 + a.m03 * b.m30,
            a.m00 * b.m01 + a.m01 * b.m11 + a.m02 * b.m21 + a.m03 * b.m31,
            a.m00 * b.m02 + a.m01 * b.m12 + a.m02 * b.m22 + a.m03 * b.m32,
            a.m00 * b.m03 + a.m01 * b.m13 + a.m02 * b.m23 + a.m03 * b.m33,
            a.m10 * b.m00 + a.m11 * b.m10 + a.m12 * b.m20 + a.m13 * b.m30,
            a.m10 * b.m01 + a.m11 * b.m11 + a.m12 * b.m21 + a.m13 * b.m31,
            a.m10 * b.m02 + a.m11 * b.m12 + a.m12 * b.m22 + a.m13 * b.m32,
            a.m10 * b.m03 + a.m11 * b.m13 + a.m12 * b.m23 + a.m13 * b.m33,
            a.m20 * b.m00 + a.m21 * b.m10 + a.m22 * b.m20 + a.m23 * b.m30,
            a.m20 * b.m01 + a.m21 * b.m11 + a.m22 * b.m21 + a.m23 * b.m31,
            a.m20 * b.m02 + a.m21 * b.m12 + a.m22 * b.m22 + a.m23 * b.m32,
            a.m20 * b.m03 + a.m21 * b.m13 + a.m22 * b.m23 + a.m23 * b.m33,
            a.m30 * b.m00 + a.m31 * b.m10 + a.m32 * b.m20 + a.m33 * b.m30,
            a.m30 * b.m01 + a.m31 * b.m11 + a.m32 * b.m21 + a.m33 * b.m31,
            a.m30 * b.m02 + a.m31 * b.m12 + a.m32 * b.m22 + a.m33 * b.m32,
            a.m30 * b.m03 + a.m31 * b.m13 + a.m32 * b.m23 + a.m33 * b.m33);
    }

    /**
     * Multiplies three matrices. Useful for composing an affine transform from
     * translation, rotation and scale matrices.
     *
     * @param a      the first matrix
     * @param b      the second matrix
     * @param c      the third matrix
     * @param target the output matrix
     * @return the product
     */
    public static Mat4 mul(
        final Mat4 a,
        final Mat4 b,
        final Mat4 c,
        final Mat4 target) {

        final float n00 = a.m00 * b.m00 + a.m01 * b.m10 + a.m02 * b.m20 + a.m03 * b.m30;
        final float n01 = a.m00 * b.m01 + a.m01 * b.m11 + a.m02 * b.m21 + a.m03 * b.m31;
        final float n02 = a.m00 * b.m02 + a.m01 * b.m12 + a.m02 * b.m22 + a.m03 * b.m32;
        final float n03 = a.m00 * b.m03 + a.m01 * b.m13 + a.m02 * b.m23 + a.m03 * b.m33;

        final float n10 = a.m10 * b.m00 + a.m11 * b.m10 + a.m12 * b.m20 + a.m13 * b.m30;
        final float n11 = a.m10 * b.m01 + a.m11 * b.m11 + a.m12 * b.m21 + a.m13 * b.m31;
        final float n12 = a.m10 * b.m02 + a.m11 * b.m12 + a.m12 * b.m22 + a.m13 * b.m32;
        final float n13 = a.m10 * b.m03 + a.m11 * b.m13 + a.m12 * b.m23 + a.m13 * b.m33;

        final float n20 = a.m20 * b.m00 + a.m21 * b.m10 + a.m22 * b.m20 + a.m23 * b.m30;
        final float n21 = a.m20 * b.m01 + a.m21 * b.m11 + a.m22 * b.m21 + a.m23 * b.m31;
        final float n22 = a.m20 * b.m02 + a.m21 * b.m12 + a.m22 * b.m22 + a.m23 * b.m32;
        final float n23 = a.m20 * b.m03 + a.m21 * b.m13 + a.m22 * b.m23 + a.m23 * b.m33;

        final float n30 = a.m30 * b.m00 + a.m31 * b.m10 + a.m32 * b.m20 + a.m33 * b.m30;
        final float n31 = a.m30 * b.m01 + a.m31 * b.m11 + a.m32 * b.m21 + a.m33 * b.m31;
        final float n32 = a.m30 * b.m02 + a.m31 * b.m12 + a.m32 * b.m22 + a.m33 * b.m32;
        final float n33 = a.m30 * b.m03 + a.m31 * b.m13 + a.m32 * b.m23 + a.m33 * b.m33;

        return target.set(
            n00 * c.m00 + n01 * c.m10 + n02 * c.m20 + n03 * c.m30,
            n00 * c.m01 + n01 * c.m11 + n02 * c.m21 + n03 * c.m31,
            n00 * c.m02 + n01 * c.m12 + n02 * c.m22 + n03 * c.m32,
            n00 * c.m03 + n01 * c.m13 + n02 * c.m23 + n03 * c.m33,
            n10 * c.m00 + n11 * c.m10 + n12 * c.m20 + n13 * c.m30,
            n10 * c.m01 + n11 * c.m11 + n12 * c.m21 + n13 * c.m31,
            n10 * c.m02 + n11 * c.m12 + n12 * c.m22 + n13 * c.m32,
            n10 * c.m03 + n11 * c.m13 + n12 * c.m23 + n13 * c.m33,
            n20 * c.m00 + n21 * c.m10 + n22 * c.m20 + n23 * c.m30,
            n20 * c.m01 + n21 * c.m11 + n22 * c.m21 + n23 * c.m31,
            n20 * c.m02 + n21 * c.m12 + n22 * c.m22 + n23 * c.m32,
            n20 * c.m03 + n21 * c.m13 + n22 * c.m23 + n23 * c.m33,
            n30 * c.m00 + n31 * c.m10 + n32 * c.m20 + n33 * c.m30,
            n30 * c.m01 + n31 * c.m11 + n32 * c.m21 + n33 * c.m31,
            n30 * c.m02 + n31 * c.m12 + n32 * c.m22 + n33 * c.m32,
            n30 * c.m03 + n31 * c.m13 + n32 * c.m23 + n33 * c.m33);
    }

    /**
     * Multiplies a matrix and a quaternion. Stores the quaternion's matrix
     * representation in an output variable.
     *
     * @param m      the matrix
     * @param q      the quaternion
     * @param target the output matrix
     * @param qm     the matrix conversion
     * @return the product
     * @see Mat4#fromRotation(Quaternion, Mat4)
     * @see Mat4#mul(Mat4, Mat4, Mat4)
     */
    public static Mat4 mul(
        final Mat4 m,
        final Quaternion q,
        final Mat4 target,
        final Mat4 qm) {

        Mat4.fromRotation(q, qm);
        return Mat4.mul(m, qm, target);
    }

    /**
     * Multiplies a matrix and a color.
     *
     * @param m      the matrix
     * @param c      the color
     * @param target the output vector
     * @return the product
     */
    public static Vec4 mul(final Mat4 m, final Rgb c, final Vec4 target) {

        return target.set(
            m.m00 * c.r + m.m01 * c.g + m.m02 * c.b + m.m03 * c.alpha,
            m.m10 * c.r + m.m11 * c.g + m.m12 * c.b + m.m13 * c.alpha,
            m.m20 * c.r + m.m21 * c.g + m.m22 * c.b + m.m23 * c.alpha,
            m.m30 * c.r + m.m31 * c.g + m.m32 * c.b + m.m33 * c.alpha);
    }

    /**
     * Multiplies a matrix and a vector.
     *
     * @param m      the matrix
     * @param v      the vector
     * @param target the output matrix
     * @return the product
     */
    public static Mat4 mul(final Mat4 m, final Vec4 v, final Mat4 target) {

        return target.set(
            m.m00 * v.x, m.m01 * v.y, m.m02 * v.z, m.m03 * v.w,
            m.m10 * v.x, m.m11 * v.y, m.m12 * v.z, m.m13 * v.w,
            m.m20 * v.x, m.m21 * v.y, m.m22 * v.z, m.m23 * v.w,
            m.m30 * v.x, m.m31 * v.y, m.m32 * v.z, m.m33 * v.w);
    }

    /**
     * Multiplies a matrix and a vector.
     *
     * @param m      the matrix
     * @param v      the vector
     * @param target the output color
     * @return the product
     */
    public static Rgb mul(final Mat4 m, final Vec4 v, final Rgb target) {

        return target.set(
            m.m00 * v.x + m.m01 * v.y + m.m02 * v.z + m.m03 * v.w,
            m.m10 * v.x + m.m11 * v.y + m.m12 * v.z + m.m13 * v.w,
            m.m20 * v.x + m.m21 * v.y + m.m22 * v.z + m.m23 * v.w,
            m.m30 * v.x + m.m31 * v.y + m.m32 * v.z + m.m33 * v.w);
    }

    /**
     * Multiplies a matrix and a vector.
     *
     * @param m      the matrix
     * @param v      the vector
     * @param target the output vector
     * @return the product
     */
    public static Vec4 mul(final Mat4 m, final Vec4 v, final Vec4 target) {

        return target.set(
            m.m00 * v.x + m.m01 * v.y + m.m02 * v.z + m.m03 * v.w,
            m.m10 * v.x + m.m11 * v.y + m.m12 * v.z + m.m13 * v.w,
            m.m20 * v.x + m.m21 * v.y + m.m22 * v.z + m.m23 * v.w,
            m.m30 * v.x + m.m31 * v.y + m.m32 * v.z + m.m33 * v.w);
    }

    /**
     * Multiplies a quaternion and a matrix. Stores the quaternion's matrix
     * representation in an output variable.
     *
     * @param q      the quaternion
     * @param m      the matrix
     * @param target the output matrix
     * @param qm     the quaternion-matrix conversion
     * @return the product
     * @see Mat4#fromRotation(Quaternion, Mat4)
     * @see Mat4#mul(Mat4, Mat4, Mat4)
     */
    public static Mat4 mul(
        final Quaternion q,
        final Mat4 m,
        final Mat4 target,
        final Mat4 qm) {

        Mat4.fromRotation(q, qm);
        return Mat4.mul(qm, m, target);
    }

    /**
     * Following <a href=
     * "https://en.wikibooks.org/wiki/GLSL_Programming/Vector_and_Matrix_Operations#Operators">GLSL
     * convention</a>, multiplies a color and the transpose of a matrix.
     * <br>
     * <br>
     * v<sup>T</sup> M = ( M<sup>T</sup> c )<sup>T</sup>
     *
     * @param c      the color
     * @param m      the matrix
     * @param target the output vector
     * @return the product
     */
    public static Vec4 mul(final Rgb c, final Mat4 m, final Vec4 target) {

        return target.set(
            c.r * m.m00 + c.g * m.m10 + c.b * m.m20 + c.alpha * m.m30,
            c.r * m.m01 + c.g * m.m11 + c.b * m.m21 + c.alpha * m.m31,
            c.r * m.m02 + c.g * m.m12 + c.b * m.m22 + c.alpha * m.m32,
            c.r * m.m03 + c.g * m.m13 + c.b * m.m23 + c.alpha * m.m33);
    }

    /**
     * Multiplies a vector and the transpose of a matrix.
     *
     * @param v      the vector
     * @param m      the matrix
     * @param target the output matrix
     * @return the product
     */
    public static Mat4 mul(final Vec4 v, final Mat4 m, final Mat4 target) {

        return target.set(
            v.x * m.m00, v.y * m.m10, v.z * m.m20, v.w * m.m30,
            v.x * m.m01, v.y * m.m11, v.z * m.m21, v.w * m.m31,
            v.x * m.m02, v.y * m.m12, v.z * m.m22, v.w * m.m32,
            v.x * m.m03, v.y * m.m13, v.z * m.m23, v.w * m.m33);
    }

    /**
     * Following <a href=
     * "https://en.wikibooks.org/wiki/GLSL_Programming/Vector_and_Matrix_Operations#Operators">GLSL
     * convention</a>, multiplies a vector and the transpose of a matrix.
     * <br>
     * <br>
     * c<sup>T</sup> M = ( M<sup>T</sup> v )<sup>T</sup>
     *
     * @param v      the vector
     * @param m      the matrix
     * @param target the output color
     * @return the product
     */
    public static Rgb mul(final Vec4 v, final Mat4 m, final Rgb target) {

        return target.set(
            v.x * m.m00 + v.y * m.m10 + v.z * m.m20 + v.w * m.m30,
            v.x * m.m01 + v.y * m.m11 + v.z * m.m21 + v.w * m.m31,
            v.x * m.m02 + v.y * m.m12 + v.z * m.m22 + v.w * m.m32,
            v.x * m.m03 + v.y * m.m13 + v.z * m.m23 + v.w * m.m33);
    }

    /**
     * Following <a href=
     * "https://en.wikibooks.org/wiki/GLSL_Programming/Vector_and_Matrix_Operations#Operators">GLSL
     * convention</a>, multiplies a vector and the transpose of a matrix.
     * <br>
     * <br>
     * v<sup>T</sup> M = ( M<sup>T</sup> v )<sup>T</sup>
     *
     * @param v      the vector
     * @param m      the matrix
     * @param target the output vector
     * @return the product
     */
    public static Vec4 mul(final Vec4 v, final Mat4 m, final Vec4 target) {

        return target.set(
            v.x * m.m00 + v.y * m.m10 + v.z * m.m20 + v.w * m.m30,
            v.x * m.m01 + v.y * m.m11 + v.z * m.m21 + v.w * m.m31,
            v.x * m.m02 + v.y * m.m12 + v.z * m.m22 + v.w * m.m32,
            v.x * m.m03 + v.y * m.m13 + v.z * m.m23 + v.w * m.m33);
    }

    /**
     * Multiplies a matrix and a normal. Calculates the inverse of the matrix.
     *
     * @param n      the normal
     * @param m      the matrix
     * @param h      the inverse
     * @param target the output normal
     * @return the transformed normal
     * @see Mat4#inverse(Mat4, Mat4)
     * @see Mat4#mulNormal(Vec3, Mat4, Mat4, Vec3)
     */
    public static Vec3 mulNormal(final Vec3 n, final Mat4 m, final Mat4 h, final Vec3 target) {

        Mat4.inverse(m, h);
        return Mat4.mulNormal(n, h, target);
    }

    /**
     * Multiplies a matrix and a normal. Assumes the inverse of the matrix has
     * already been calculated.
     *
     * @param n      the normal
     * @param h      the inverse
     * @param target the output normal
     * @return the transformed normal
     * @see Vec3#normalize(Vec3, Vec3)
     */
    public static Vec3 mulNormal(final Vec3 n, final Mat4 h, final Vec3 target) {

        /*
         * Cf. Eric Lengyel, Foundations of Game Engine Development I.
         * Mathematics, page 106.
         */
        target.set(
            n.x * h.m00 + n.y * h.m10 + n.z * h.m20,
            n.x * h.m01 + n.y * h.m11 + n.z * h.m21,
            n.x * h.m02 + n.y * h.m12 + n.z * h.m22);
        return Vec3.normalize(target, target);
    }

    /**
     * Multiplies a matrix and a point. The z component of the point is assumed
     * to be 0.0. The w component of the point is assumed to be 1.0, so the
     * point is impacted by the matrix's translation.
     *
     * @param m      the matrix
     * @param p      the point
     * @param target the output point
     * @return the product
     */
    public static Vec3 mulPoint(final Mat4 m, final Vec2 p, final Vec3 target) {

        final float w = m.m30 * p.x + m.m31 * p.y + m.m33;
        if (w != 0.0f) {
            final float wInv = 1.0f / w;
            return target.set(
                (m.m00 * p.x + m.m01 * p.y + m.m03) * wInv,
                (m.m10 * p.x + m.m11 * p.y + m.m13) * wInv,
                (m.m20 * p.x + m.m21 * p.y + m.m23) * wInv);
        }
        return target.reset();
    }

    /**
     * Multiplies a matrix and a point. The w component of the point is assumed
     * to be 1.0, so the point is impacted by the matrix's translation.
     *
     * @param m      the matrix
     * @param p      the point
     * @param target the output point
     * @return the product
     */
    public static Vec3 mulPoint(final Mat4 m, final Vec3 p, final Vec3 target) {

        final float w = m.m30 * p.x + m.m31 * p.y + m.m32 * p.z + m.m33;
        if (w != 0.0f) {
            final float wInv = 1.0f / w;
            return target.set(
                (m.m00 * p.x + m.m01 * p.y + m.m02 * p.z + m.m03) * wInv,
                (m.m10 * p.x + m.m11 * p.y + m.m12 * p.z + m.m13) * wInv,
                (m.m20 * p.x + m.m21 * p.y + m.m22 * p.z + m.m23) * wInv);
        }
        return target.reset();
    }

    /**
     * Multiplies a matrix and a vector. The z and w components of the vector
     * are assumed to be 0.0, so the vector is not impacted by the matrix's
     * translation.
     *
     * @param m      the matrix
     * @param v      the vector
     * @param target the output vector
     * @return the product
     */
    public static Vec3 mulVector(final Mat4 m, final Vec2 v, final Vec3 target) {

        final float w = m.m30 * v.x + m.m31 * v.y + m.m33;
        if (w != 0.0f) {
            final float wInv = 1.0f / w;
            return target.set(
                (m.m00 * v.x + m.m01 * v.y) * wInv,
                (m.m10 * v.x + m.m11 * v.y) * wInv,
                (m.m20 * v.x + m.m21 * v.y) * wInv);
        }
        return target.reset();
    }

    /**
     * Multiplies a matrix and a vector. The w component of the vector is
     * assumed to be 0.0, so the vector is not impacted by the matrix's
     * translation.
     *
     * @param m      the matrix
     * @param v      the vector
     * @param target the output vector
     * @return the product
     */
    public static Vec3 mulVector(final Mat4 m, final Vec3 v, final Vec3 target) {

        final float w = m.m30 * v.x + m.m31 * v.y + m.m32 * v.z + m.m33;
        if (w != 0.0f) {
            final float wInv = 1.0f / w;
            return target.set(
                (m.m00 * v.x + m.m01 * v.y + m.m02 * v.z) * wInv,
                (m.m10 * v.x + m.m11 * v.y + m.m12 * v.z) * wInv,
                (m.m20 * v.x + m.m21 * v.y + m.m22 * v.z) * wInv);
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
    public static Mat4 negate(final Mat4 m, final Mat4 target) {

        return target.set(
            -m.m00, -m.m01, -m.m02, -m.m03,
            -m.m10, -m.m11, -m.m12, -m.m13,
            -m.m20, -m.m21, -m.m22, -m.m23,
            -m.m30, -m.m31, -m.m32, -m.m33);
    }

    /**
     * Tests to see if all the matrix's components are zero.
     *
     * @param m the input matrix
     * @return the evaluation
     */
    public static boolean none(final Mat4 m) {

        return m.m00 == 0.0f && m.m01 == 0.0f && m.m02 == 0.0f && m.m03 == 0.0f
            && m.m10 == 0.0f && m.m11 == 0.0f && m.m12 == 0.0f && m.m13 == 0.0f
            && m.m20 == 0.0f && m.m21 == 0.0f && m.m22 == 0.0f && m.m23 == 0.0f
            && m.m30 == 0.0f && m.m31 == 0.0f && m.m32 == 0.0f && m.m33 == 0.0f;
    }

    /**
     * Evaluates a matrix like a boolean, where n != 0.0 is true.
     *
     * @param m      the input matrix
     * @param target the output matrix
     * @return the truth table opposite
     */
    public static Mat4 not(final Mat4 m, final Mat4 target) {

        return target.set(
            m.m00 != 0.0f ? 0.0f : 1.0f, m.m01 != 0.0f ? 0.0f : 1.0f,
            m.m02 != 0.0f ? 0.0f : 1.0f, m.m03 != 0.0f ? 0.0f : 1.0f,
            m.m10 != 0.0f ? 0.0f : 1.0f, m.m11 != 0.0f ? 0.0f : 1.0f,
            m.m12 != 0.0f ? 0.0f : 1.0f, m.m13 != 0.0f ? 0.0f : 1.0f,
            m.m20 != 0.0f ? 0.0f : 1.0f, m.m21 != 0.0f ? 0.0f : 1.0f,
            m.m22 != 0.0f ? 0.0f : 1.0f, m.m23 != 0.0f ? 0.0f : 1.0f,
            m.m30 != 0.0f ? 0.0f : 1.0f, m.m31 != 0.0f ? 0.0f : 1.0f,
            m.m32 != 0.0f ? 0.0f : 1.0f, m.m33 != 0.0f ? 0.0f : 1.0f);
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
    public static Mat4 or(final Mat4 a, final Mat4 b, final Mat4 target) {

        return target.set(
            Utils.or(a.m00, b.m00), Utils.or(a.m01, b.m01),
            Utils.or(a.m02, b.m02), Utils.or(a.m03, b.m03),
            Utils.or(a.m10, b.m10), Utils.or(a.m11, b.m11),
            Utils.or(a.m12, b.m12), Utils.or(a.m13, b.m13),
            Utils.or(a.m20, b.m20), Utils.or(a.m21, b.m21),
            Utils.or(a.m22, b.m22), Utils.or(a.m23, b.m23),
            Utils.or(a.m30, b.m30), Utils.or(a.m31, b.m31),
            Utils.or(a.m32, b.m32), Utils.or(a.m33, b.m33));
    }

    /**
     * Creates an orthographic projection matrix, where objects maintain their
     * size regardless of distance from the camera.
     *
     * @param left   the left edge of the window
     * @param right  the right edge of the window
     * @param bottom the bottom edge of the window
     * @param top    the top edge of the window
     * @param near   the near clip plane
     * @param far    the far clip plane
     * @param target the output matrix
     * @return the orthographic projection
     */
    public static Mat4 orthographic(
        final float left,
        final float right,
        final float bottom,
        final float top,
        final float near,
        final float far,
        final Mat4 target) {

        float w = right - left;
        float h = top - bottom;
        float d = far - near;

        w = w != 0.0f ? 1.0f / w : 1.0f;
        h = h != 0.0f ? 1.0f / h : 1.0f;
        d = d != 0.0f ? 1.0f / d : 1.0f;

        return target.set(
            w + w, 0.0f, 0.0f, w * (left + right),
            0.0f, h + h, 0.0f, h * (top + bottom),
            0.0f, 0.0f, -(d + d), -d * (far + near),
            0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Creates a perspective projection matrix, where objects nearer to the
     * camera appear larger than objects distant from the camera.
     *
     * @param fov    the field of view
     * @param aspect the aspect ratio, width over height
     * @param near   the near clip plane
     * @param far    the far clip plane
     * @param target the output matrix
     * @return the perspective projection
     * @see Utils#cot(float)
     * @see Utils#div(float, float)
     */
    public static Mat4 perspective(
        final float fov,
        final float aspect,
        final float near,
        final float far,
        final Mat4 target) {

        final float cotfov = Utils.cot(fov * 0.5f);
        final float d = Utils.div(1.0f, far - near);
        return target.set(
            Utils.div(cotfov, aspect), 0.0f, 0.0f, 0.0f,
            0.0f, cotfov, 0.0f, 0.0f,
            0.0f, 0.0f, (far + near) * -d, (near + near) * far * -d,
            0.0f, 0.0f, -1.0f, 0.0f);
    }

    /**
     * Rotates the elements of the input matrix 90 degrees counter-clockwise.
     *
     * @param m      the input matrix
     * @param target the output matrix
     * @return the rotated matrix
     */
    public static Mat4 rotateElmsCcw(final Mat4 m, final Mat4 target) {

        return target.set(
            m.m03, m.m13, m.m23, m.m33,
            m.m02, m.m12, m.m22, m.m32,
            m.m01, m.m11, m.m21, m.m31,
            m.m00, m.m10, m.m20, m.m30);
    }

    /**
     * Rotates the elements of the input matrix 90 degrees clockwise.
     *
     * @param m      the input matrix
     * @param target the output matrix
     * @return the rotated matrix
     */
    public static Mat4 rotateElmsCw(final Mat4 m, final Mat4 target) {

        return target.set(
            m.m30, m.m20, m.m10, m.m00,
            m.m31, m.m21, m.m11, m.m01,
            m.m32, m.m22, m.m12, m.m02,
            m.m33, m.m23, m.m13, m.m03);
    }

    /**
     * Subtracts the right matrix from the left matrix.
     *
     * @param a      the left operand
     * @param b      the right operand
     * @param target the output matrix
     * @return the result
     */
    public static Mat4 sub(final Mat4 a, final Mat4 b, final Mat4 target) {

        return target.set(
            a.m00 - b.m00, a.m01 - b.m01, a.m02 - b.m02, a.m03 - b.m03,
            a.m10 - b.m10, a.m11 - b.m11, a.m12 - b.m12, a.m13 - b.m13,
            a.m20 - b.m20, a.m21 - b.m21, a.m22 - b.m22, a.m23 - b.m23,
            a.m30 - b.m30, a.m31 - b.m31, a.m32 - b.m32, a.m33 - b.m33);
    }

    /**
     * Transposes a matrix, switching its row and column indices.
     *
     * @param m      the matrix
     * @param target the output matrix
     * @return the transposed matrix
     */
    public static Mat4 transpose(final Mat4 m, final Mat4 target) {

        return target.set(
            m.m00, m.m10, m.m20, m.m30,
            m.m01, m.m11, m.m21, m.m31,
            m.m02, m.m12, m.m22, m.m32,
            m.m03, m.m13, m.m23, m.m33);
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
    public static Mat4 xor(final Mat4 a, final Mat4 b, final Mat4 target) {

        return target.set(
            Utils.xor(a.m00, b.m00), Utils.xor(a.m01, b.m01),
            Utils.xor(a.m02, b.m02), Utils.xor(a.m03, b.m03),
            Utils.xor(a.m10, b.m10), Utils.xor(a.m11, b.m11),
            Utils.xor(a.m12, b.m12), Utils.xor(a.m13, b.m13),
            Utils.xor(a.m20, b.m20), Utils.xor(a.m21, b.m21),
            Utils.xor(a.m22, b.m22), Utils.xor(a.m23, b.m23),
            Utils.xor(a.m30, b.m30), Utils.xor(a.m31, b.m31),
            Utils.xor(a.m32, b.m32), Utils.xor(a.m33, b.m33));
    }

    /**
     * Creates a reflection matrix from an axis representing a plane. The axis
     * will be normalized by the function.
     *
     * @param ax     the axis x
     * @param ay     the axis y
     * @param az     the axis z
     * @param target the output matrix
     * @return the reflection
     */
    static Mat4 fromReflection(
        final float ax,
        final float ay,
        final float az,
        final Mat4 target) {

        final float x = -(ax + ax);
        final float y = -(ay + ay);
        final float z = -(az + az);

        final float axay = x * ay;
        final float axaz = x * az;
        final float ayaz = y * az;

        return target.set(
            x * ax + 1.0f, axay, axaz, 0.0f,
            axay, y * ay + 1.0f, ayaz, 0.0f,
            axaz, ayaz, z * az + 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Creates a rotation matrix from an angle in radians around an axis. The
     * axis will not be checked for validity.
     *
     * @param cosa   cosine of the angle
     * @param sina   sine of the angle
     * @param ax     the axis x
     * @param ay     the axis y
     * @param az     the axis z
     * @param target the output matrix
     * @return the matrix
     */
    static Mat4 fromRotation(
        final float cosa, final float sina,
        final float ax, final float ay, final float az,
        final Mat4 target) {

        final float d = 1.0f - cosa;
        final float x = ax * d;
        final float y = ay * d;
        final float z = az * d;

        final float axay = x * ay;
        final float axaz = x * az;
        final float ayaz = y * az;

        return target.set(
            cosa + x * ax, axay - sina * az, axaz + sina * ay, 0.0f,
            axay + sina * az, cosa + y * ay, ayaz - sina * ax, 0.0f,
            axaz - sina * ay, ayaz + sina * ax, cosa + z * az, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Creates skew, or shear, matrix from the tangent of an angle and axes.
     * Axes <em>a</em> and <em>b</em> are expected to be orthonormal, i.e.,
     * perpendicular and of unit length.
     *
     * @param t      the tangent of the angle
     * @param ax     the skew axis x
     * @param ay     the skew axis y
     * @param az     the skew axis z
     * @param bx     the perpendicular axis x
     * @param by     the perpendicular axis y
     * @param bz     the perpendicular axis z
     * @param target the output matrix
     * @return the skew matrix
     */
    static Mat4 fromSkew(
        final float t,
        final float ax, final float ay, final float az,
        final float bx, final float by, final float bz,
        final Mat4 target) {

        final float tax = ax * t;
        final float tay = ay * t;
        final float taz = az * t;

        return target.set(
            tax * bx + 1.0f, tax * by, tax * bz, 0.0f,
            tay * bx, tay * by + 1.0f, tay * bz, 0.0f,
            taz * bx, taz * by, taz * bz + 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Tests this matrix for equivalence with another object.
     *
     * @param obj the object
     * @return the equivalence
     * @see Mat4#equals(Mat4)
     */
    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        return this.equals((Mat4) obj);
    }

    /**
     * Gets a column of this matrix with an index and vector.
     *
     * @param j      the column index
     * @param target the vector
     * @return the column
     */
    public Vec4 getCol(final int j, final Vec4 target) {

        switch (j) {
            case 0:
            case -4:
                return target.set(this.m00, this.m10, this.m20, this.m30);

            case 1:
            case -3:
                return target.set(this.m01, this.m11, this.m21, this.m31);

            case 2:
            case -2:
                return target.set(this.m02, this.m12, this.m22, this.m32);

            case 3:
            case -1:
                return target.set(this.m03, this.m13, this.m23, this.m33);

            default:
                return target.reset();
        }
    }

    /**
     * Simulates bracket subscript access in a one-dimensional, row-major
     * matrix array. Works with positive integers in [0, 15] or negative
     * integers in [-16, -1] .
     *
     * @param index the index
     * @return the component at that index
     */
    public float getElm(final int index) {

        /*
         * At the moment, there is a get function to facilitate an iterator,
         * but no set function, because setCol is the encouraged way to set
         * matrix elms.
         */

        switch (index) {
            case 0:
            case -16:
                return this.m00;
            case 1:
            case -15:
                return this.m01;
            case 2:
            case -14:
                return this.m02;
            case 3:
            case -13:
                return this.m03;

            case 4:
            case -12:
                return this.m10;
            case 5:
            case -11:
                return this.m11;
            case 6:
            case -10:
                return this.m12;
            case 7:
            case -9:
                return this.m13;

            case 8:
            case -8:
                return this.m20;
            case 9:
            case -7:
                return this.m21;
            case 10:
            case -6:
                return this.m22;
            case 11:
            case -5:
                return this.m23;

            case 12:
            case -4:
                return this.m30;
            case 13:
            case -3:
                return this.m31;
            case 14:
            case -2:
                return this.m32;
            case 15:
            case -1:
                return this.m33;

            default:
                return 0.0f;
        }

    }

    /**
     * Simulates bracket subscript access in a two-dimensional, row-major
     * matrix array. Works with positive integers in [0, 3][0, 3] or negative
     * integers in [-4, -1][-4, -1] .
     *
     * @param i the row index
     * @param j the column index
     * @return the component at that index
     */
    public float getElm(final int i, final int j) {

        switch (i) {
            case 0:
            case -4:
                switch (j) {
                    case 0:
                    case -4:
                        return this.m00;
                    case 1:
                    case -3:
                        return this.m01;
                    case 2:
                    case -2:
                        return this.m02;
                    case 3:
                    case -1:
                        return this.m03;
                    default:
                        return 0.0f;
                }

            case 1:
            case -3:
                switch (j) {
                    case 0:
                    case -4:
                        return this.m10;
                    case 1:
                    case -3:
                        return this.m11;
                    case 2:
                    case -2:
                        return this.m12;
                    case 3:
                    case -1:
                        return this.m13;
                    default:
                        return 0.0f;
                }

            case 2:
            case -2:
                switch (j) {
                    case 0:
                    case -4:
                        return this.m20;
                    case 1:
                    case -3:
                        return this.m21;
                    case 2:
                    case -2:
                        return this.m22;
                    case 3:
                    case -1:
                        return this.m23;
                    default:
                        return 0.0f;
                }

            case 3:
            case -1:
                switch (j) {
                    case 0:
                    case -4:
                        return this.m30;
                    case 1:
                    case -3:
                        return this.m31;
                    case 2:
                    case -2:
                        return this.m32;
                    case 3:
                    case -1:
                        return this.m33;
                    default:
                        return 0.0f;
                }

            default:
                return 0.0f;
        }

    }

    @Override
    public int hashCode() {
        return Objects.hash(
            this.m00, this.m01, this.m02, this.m03,
            this.m10, this.m11, this.m12, this.m13,
            this.m20, this.m21, this.m22, this.m23,
            this.m30, this.m31, this.m32, this.m33);
    }

    /**
     * Resets this matrix to an initial state:
     *
     * <pre>
     * 1.0, 0.0, 0.0, 0.0,
     * 0.0, 1.0, 0.0, 0.0,
     * 0.0, 0.0, 1.0, 0.0,
     * 0.0, 0.0, 0.0, 1.0
     * </pre>
     *
     * @return this matrix
     */
    public Mat4 reset() {

        return this.set(
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Sets the components of this matrix.
     *
     * @param m00 row 0, column 0
     * @param m01 row 0, column 1
     * @param m02 row 0, column 2
     * @param m03 row 0, column 3
     * @param m10 row 1, column 0
     * @param m11 row 1, column 1
     * @param m12 row 1, column 2
     * @param m13 row 1, column 3
     * @param m20 row 2, column 0
     * @param m21 row 2, column 1
     * @param m22 row 2, column 2
     * @param m23 row 2, column 3
     * @param m30 row 3, column 0
     * @param m31 row 3, column 1
     * @param m32 row 3, column 2
     * @param m33 row 3, column 3
     * @return this matrix
     */
    public Mat4 set(
        final boolean m00, final boolean m01, final boolean m02, final boolean m03,
        final boolean m10, final boolean m11, final boolean m12, final boolean m13,
        final boolean m20, final boolean m21, final boolean m22, final boolean m23,
        final boolean m30, final boolean m31, final boolean m32, final boolean m33) {

        this.m00 = m00 ? 1.0f : 0.0f;
        this.m01 = m01 ? 1.0f : 0.0f;
        this.m02 = m02 ? 1.0f : 0.0f;
        this.m03 = m03 ? 1.0f : 0.0f;

        this.m10 = m10 ? 1.0f : 0.0f;
        this.m11 = m11 ? 1.0f : 0.0f;
        this.m12 = m12 ? 1.0f : 0.0f;
        this.m13 = m13 ? 1.0f : 0.0f;

        this.m20 = m20 ? 1.0f : 0.0f;
        this.m21 = m21 ? 1.0f : 0.0f;
        this.m22 = m22 ? 1.0f : 0.0f;
        this.m23 = m23 ? 1.0f : 0.0f;

        this.m30 = m30 ? 1.0f : 0.0f;
        this.m31 = m31 ? 1.0f : 0.0f;
        this.m32 = m32 ? 1.0f : 0.0f;
        this.m33 = m33 ? 1.0f : 0.0f;

        return this;
    }

    /**
     * Sets the upper left 2 by 2 corner of this matrix. The remaining values
     * are set to the identity.
     *
     * @param m00 row 0, column 0
     * @param m01 row 0, column 1
     * @param m10 row 1, column 0
     * @param m11 row 1, column 1
     * @return this matrix
     */
    public Mat4 set(
        final float m00, final float m01,
        final float m10, final float m11) {

        this.m00 = m00;
        this.m01 = m01;
        this.m02 = 0.0f;
        this.m03 = 0.0f;

        this.m10 = m10;
        this.m11 = m11;
        this.m12 = 0.0f;
        this.m13 = 0.0f;

        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 1.0f;
        this.m23 = 0.0f;

        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 1.0f;

        return this;
    }

    /**
     * Sets the upper left 3 by 3 corner of this matrix. The remaining values
     * are set to the identity.
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
    public Mat4 set(
        final float m00, final float m01, final float m02,
        final float m10, final float m11, final float m12,
        final float m20, final float m21, final float m22) {

        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = 0.0f;

        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = 0.0f;

        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = 0.0f;

        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 1.0f;

        return this;
    }

    /**
     * Sets the upper three rows of this matrix. The last row is set to
     * (0.0, 0.0, 0.0, 1.0).
     *
     * @param m00 row 0, column 0
     * @param m01 row 0, column 1
     * @param m02 row 0, column 2
     * @param m03 row 0, column 3
     * @param m10 row 1, column 0
     * @param m11 row 1, column 1
     * @param m12 row 1, column 2
     * @param m13 row 1, column 3
     * @param m20 row 2, column 0
     * @param m21 row 2, column 1
     * @param m22 row 2, column 2
     * @param m23 row 2, column 3
     * @return this matrix
     */
    public Mat4 set(
        final float m00, final float m01, final float m02, final float m03,
        final float m10, final float m11, final float m12, final float m13,
        final float m20, final float m21, final float m22, final float m23) {

        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = m03;

        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;

        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;

        this.m30 = 0.0f;
        this.m31 = 0.0f;
        this.m32 = 0.0f;
        this.m33 = 1.0f;

        return this;
    }

    /**
     * Sets the components of this matrix.
     *
     * @param m00 row 0, column 0
     * @param m01 row 0, column 1
     * @param m02 row 0, column 2
     * @param m03 row 0, column 3
     * @param m10 row 1, column 0
     * @param m11 row 1, column 1
     * @param m12 row 1, column 2
     * @param m13 row 1, column 3
     * @param m20 row 2, column 0
     * @param m21 row 2, column 1
     * @param m22 row 2, column 2
     * @param m23 row 2, column 3
     * @param m30 row 3, column 0
     * @param m31 row 3, column 1
     * @param m32 row 3, column 2
     * @param m33 row 3, column 3
     * @return this matrix
     */
    public Mat4 set(
        final float m00, final float m01, final float m02, final float m03,
        final float m10, final float m11, final float m12, final float m13,
        final float m20, final float m21, final float m22, final float m23,
        final float m30, final float m31, final float m32, final float m33) {

        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = m03;

        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;

        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;

        this.m30 = m30;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;

        return this;
    }

    /**
     * Copies the components of the input matrix to this matrix.
     *
     * @param source the input matrix
     * @return this matrix
     */
    public Mat4 set(final Mat4 source) {

        return this.set(
            source.m00, source.m01, source.m02, source.m03,
            source.m10, source.m11, source.m12, source.m13,
            source.m20, source.m21, source.m22, source.m23,
            source.m30, source.m31, source.m32, source.m33);
    }

    /**
     * Sets a column of this matrix with an index and vector. If the column is
     * an axis vector, the w component is set to 0.0; if it is a translation,
     * the w component is set to 1.0.
     *
     * @param j      the column index
     * @param source the column
     * @return this matrix
     */
    public Mat4 setCol(final int j, final Vec3 source) {

        switch (j) {
            case 0:
            case -4:

                /* Right axis. */
                this.m00 = source.x;
                this.m10 = source.y;
                this.m20 = source.z;
                this.m30 = 0.0f;

                return this;

            case 1:
            case -3:

                /* Forward axis. */
                this.m01 = source.x;
                this.m11 = source.y;
                this.m21 = source.z;
                this.m31 = 0.0f;

                return this;

            case 2:
            case -2:

                /* Up axis. */
                this.m02 = source.x;
                this.m12 = source.y;
                this.m22 = source.z;
                this.m32 = 0.0f;

                return this;

            case 3:
            case -1:

                /* Translation. */
                this.m03 = source.x;
                this.m13 = source.y;
                this.m23 = source.z;
                this.m33 = 1.0f;

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
    public Mat4 setCol(final int j, final Vec4 source) {

        switch (j) {
            case 0:
            case -4:

                /* Right axis. */
                this.m00 = source.x;
                this.m10 = source.y;
                this.m20 = source.z;
                this.m30 = source.w;

                return this;

            case 1:
            case -3:

                /* Forward axis. */
                this.m01 = source.x;
                this.m11 = source.y;
                this.m21 = source.z;
                this.m31 = source.w;

                return this;

            case 2:
            case -2:

                /* Up axis. */
                this.m02 = source.x;
                this.m12 = source.y;
                this.m22 = source.z;
                this.m32 = source.w;

                return this;

            case 3:
            case -1:

                /* Translation. */
                this.m03 = source.x;
                this.m13 = source.y;
                this.m23 = source.z;
                this.m33 = source.w;

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
     * Returns a 1D float array containing this matrix's components in row
     * major order.
     *
     * @return the array
     */
    public float[] toArray1() {

        return new float[]{
            this.m00, this.m01, this.m02, this.m03,
            this.m10, this.m11, this.m12, this.m13,
            this.m20, this.m21, this.m22, this.m23,
            this.m30, this.m31, this.m32, this.m33
        };
    }

    /**
     * Returns a 2D float array containing this matrix's components.
     *
     * @return the array
     */
    public float[][] toArray2() {

        return new float[][]{
            {this.m00, this.m01, this.m02, this.m03},
            {this.m10, this.m11, this.m12, this.m13},
            {this.m20, this.m21, this.m22, this.m23},
            {this.m30, this.m31, this.m32, this.m33}
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

        return this.toString(new StringBuilder(512), places).toString();
    }

    /**
     * Returns a string representation of this matrix, where columns are
     * separated by tabs and rows are separated by new lines.
     *
     * @return the string
     */
    public String toStringCol() {

        return this.toStringCol(Utils.FIXED_PRINT, ',', ' ', '\n');
    }

    /**
     * Returns a string representation of this matrix intended for display in
     * the console.
     *
     * @param p number of decimal places
     * @param s the entry separator
     * @param t the spacer
     * @param n the row separator
     * @return the string
     */
    public String toStringCol(final int p, final char s, final char t, final char n) {

        return '\n'
            + Utils.toFixed(this.m00, p) + s + t
            + Utils.toFixed(this.m01, p) + s + t
            + Utils.toFixed(this.m02, p) + s + t
            + Utils.toFixed(this.m03, p) + s + n

            + Utils.toFixed(this.m10, p) + s + t
            + Utils.toFixed(this.m11, p) + s + t
            + Utils.toFixed(this.m12, p) + s + t
            + Utils.toFixed(this.m13, p) + s + n

            + Utils.toFixed(this.m20, p) + s + t
            + Utils.toFixed(this.m21, p) + s + t
            + Utils.toFixed(this.m22, p) + s + t
            + Utils.toFixed(this.m23, p) + s + n

            + Utils.toFixed(this.m30, p) + s + t
            + Utils.toFixed(this.m31, p) + s + t
            + Utils.toFixed(this.m32, p) + s + t
            + Utils.toFixed(this.m33, p)
            + '\n';
    }

    /**
     * Internal helper function to assist with methods that need to print many
     * matrices. Appends to an existing {@link StringBuilder}.
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
        sb.append(",\"m03\":");
        Utils.toFixed(sb, this.m03, places);
        sb.append(",\"m10\":");
        Utils.toFixed(sb, this.m10, places);
        sb.append(",\"m11\":");
        Utils.toFixed(sb, this.m11, places);
        sb.append(",\"m12\":");
        Utils.toFixed(sb, this.m12, places);
        sb.append(",\"m13\":");
        Utils.toFixed(sb, this.m13, places);
        sb.append(",\"m20\":");
        Utils.toFixed(sb, this.m20, places);
        sb.append(",\"m21\":");
        Utils.toFixed(sb, this.m21, places);
        sb.append(",\"m22\":");
        Utils.toFixed(sb, this.m22, places);
        sb.append(",\"m23\":");
        Utils.toFixed(sb, this.m23, places);
        sb.append(",\"m30\":");
        Utils.toFixed(sb, this.m30, places);
        sb.append(",\"m31\":");
        Utils.toFixed(sb, this.m31, places);
        sb.append(",\"m32\":");
        Utils.toFixed(sb, this.m32, places);
        sb.append(",\"m33\":");
        Utils.toFixed(sb, this.m33, places);
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
    protected boolean equals(final Mat4 n) {

        /* With {@link Float.floatToIntBits(float)}, -0.0f != 0.0f. */
        return this.m00 == n.m00
            && this.m01 == n.m01
            && this.m02 == n.m02
            && this.m03 == n.m03
            && this.m10 == n.m10
            && this.m11 == n.m11
            && this.m12 == n.m12
            && this.m13 == n.m13
            && this.m20 == n.m20
            && this.m21 == n.m21
            && this.m22 == n.m22
            && this.m23 == n.m23
            && this.m30 == n.m30
            && this.m31 == n.m31
            && this.m32 == n.m32
            && this.m33 == n.m33;
    }
}
