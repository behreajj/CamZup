package com.behreajj.camzup.core;

/**
 * Implements basic math utilities for single-precision numbers.
 */
public abstract class Utils {

    /**
     * An angle in degrees is multiplied by this constant to convert it to
     * radians. pi / 180.0.
     */
    public static final float DEG_TO_RAD = 0.017453292f;

    /**
     * The smallest positive non-zero value. Useful for testing approximation
     * between two floats.
     */
    public static final float EPSILON = 0.000001f;

    /**
     * The smallest positive non-zero value. Useful for testing approximation
     * between two floats.
     */
    public static final double EPSILON_D = 0.000001d;

    /**
     * The default number of decimal places to print real numbers.
     */
    public static final int FIXED_PRINT = 4;

    /**
     * Four-thirds, 4.0 / 3.0 . Useful when creating a circular shape with a
     * series of Bézier curves.
     */
    public static final float FOUR_THIRDS = 1.3333333f;

    /**
     * An approximation of tau / phi<sup>2</sup>. Useful for replicating
     * phyllotaxis. In degrees, 137.50777.
     */
    public static final float GOLDEN_ANGLE = 2.3999631f;

    /**
     * Pi divided by two.
     */
    public static final float HALF_PI = 1.5707964f;

    /**
     * Base value used by hash code functions.
     */
    public static final int HASH_BASE = -2128831035;

    /**
     * Multiplier used by hash code functions.
     */
    public static final int HASH_MUL = 16777619;

    /**
     * The hash base multiplied by the hash scalar.
     */
    @SuppressWarnings("NumericOverflow")
    public static final int MUL_BASE = Utils.HASH_BASE * Utils.HASH_MUL;

    /**
     * One-255th, 1.0 / 255.0 . Useful when converting a color with channels in
     * the range [0, 255] to a color in the range [0.0, 1.0].
     */
    public static final float ONE_255 = 0.003921569f;

    /**
     * One divided by 360 degrees, 1.0 / 360. Useful for converting an index in
     * a for-loop to an angle in degrees.
     */
    public static final float ONE_360 = 0.0027777778f;

    /**
     * One-sixth, 1.0 / 6.0 .
     */
    public static final float ONE_SIX = 0.16666667f;

    /**
     * An approximation of 1.0 / sqrt(2.0). The sine and cosine of 45 degrees.
     */
    public static final float ONE_SQRT_2 = 0.70710677f;

    /**
     * An approximation of 1.0 / sqrt(3.0).
     */
    public static final float ONE_SQRT_3 = 0.57735026f;

    /**
     * One-third, 1.0 / 3.0 . Useful for setting handles on the knot of a
     * Bézier curve.
     */
    public static final float ONE_THIRD = 0.33333334f;

    /**
     * One-third, 1.0 / 3.0 . Useful for setting handles on the knot of a
     * Bézier curve.
     */
    public static final double ONE_THIRD_D = 0.3333333333333333d;

    /**
     * One divided by tau.
     */
    public static final float ONE_TAU = 0.15915494f;

    /**
     * 1.0 / 4.0 pi. Useful when normalizing angles supplied to quaternions.
     */
    public static final double ONE_TAU_2_D = 0.07957747154594767d;

    /**
     * One divided by tau.
     */
    public static final double ONE_TAU_D = 0.15915494309189535d;

    /**
     * The golden ratio. An approximation of phi, or ( 1.0 + sqrt(5.0) ) / 2.0.
     */
    public static final float PHI = 1.618034f;

    /**
     * An approximation of pi.
     */
    public static final float PI = 3.1415927f;

    /**
     * An angle in radians is multiplied by this constant to convert it to
     * degrees. 180.0 / pi .
     */
    public static final float RAD_TO_DEG = 57.29578f;

    /**
     * An approximation of sqrt(2.0).
     */
    public static final float SQRT_2 = 1.4142137f;

    /**
     * An approximation of sqrt(3.0).
     */
    public static final float SQRT_3 = 1.7320508f;

    /**
     * An approximation of sqrt(3.0) / 2.0. The cosine of 30 degrees.
     */
    public static final float SQRT_3_2 = 0.8660254f;

    /**
     * An approximation of sqrt(3.0) / 2.0 . The cosine of 30 degrees.
     */
    public static final double SQRT_3_2_D = 0.8660254037844386d;

    /**
     * An approximation of tau. Equal to 2.0 pi.
     */
    public static final float TAU = 6.2831855f;

    /**
     * An approximation of tau. Equal to 2.0 pi.
     */
    public static final double TAU_D = 6.283185307179586d;

    /**
     * pi divided by three. 60 degrees. Useful for describing the field of
     * view in a perspective camera.
     */
    public static final float THIRD_PI = 1.0471976f;

    /**
     * Two-thirds, 2.0 / 3.0 . Useful for setting handles on the knot of a
     * Bézier curve.
     */
    public static final float TWO_THIRDS = 0.6666667f;

    /**
     * Discourage overriding with a private constructor.
     */
    private Utils() {
        // TODO: Methods for bitwise interleave and bitwise reverse?
        // https://stackoverflow.com/a/3203791/12637735
        // Application in calculating a Bayer ordered matrix:
        // https://en.wikipedia.org/wiki/Ordered_dithering
    }

    /**
     * Finds the absolute value of a single precision real number. An alias
     * for {@link Math#abs(float)}. Relies on bit-masking to remove the sign
     * bit. Equivalent to <code>Utils.max(-a, a)</code>.
     *
     * @param v the input value
     * @return the absolute value
     * @see Float#intBitsToFloat(int)
     * @see Float#floatToRawIntBits(float)
     */
    public static float abs(final float v) {

        return Float.intBitsToFloat(0x7fffffff & Float.floatToRawIntBits(v));
    }

    /**
     * A bounds checked approximation of the arc-cosine for single precision
     * real numbers. Returns a value in the range [0.0, pi] : pi when the input
     * is less than or equal to -1.0; pi / 2.0 when the input is 0.0; 0.0 when
     * the input is greater than or equal to 1.0.
     * <br>
     * <br>
     * {@link Math#acos(double) } defers to {@link StrictMath#acos(double) },
     * which is implemented natively. This is not a "fast" alternative.
     * <br>
     * <br>
     * Based on the algorithm in
     * <a href="https://developer.download.nvidia.com/cg/acos.html">Nvidia
     * Cg 3.1 Toolkit Documentation</a>. This cites M. Abramowitz and I.A.
     * Stegun, Eds., <em>Handbook of Mathematical Functions</em>, possibly p.
     * 83, which cites <em>Approximations for Digital Computers</em> by C.
     * Hastings, Jr.
     *
     * @param y the input value
     * @return the angle in radians
     * @author M. Abramowitz
     * @author C. Hastings, Jr
     * @author I. A. Stegun
     * @see Utils#sqrtUnchecked(float)
     */
    public static float acos(final float y) {

        if (y <= -1.0f) {
            return Utils.PI;
        }
        if (y >= 1.0f) {
            return 0.0f;
        }
        final boolean ltZero = y < -0.0f;
        final float x = ltZero ? -y : y;
        float ret = (0.074261f - 0.0187293f * x) * x - 0.2121144f;
        ret = (ret * x + Utils.HALF_PI) * Utils.sqrtUnchecked(1.0f - x);
        return ltZero ? Utils.PI - ret : ret;
    }

    /**
     * Evaluates two floats like booleans using the AND logic gate. Non-zero
     * inputs evaluate to true, or 1. Zero evaluates to false, or 0.
     *
     * @param a the left operand
     * @param b the right operand
     * @return the evaluation
     */
    public static int and(final float a, final float b) {

        return (a != 0.0f && !Float.isNaN(a) ? 1 : 0)
            & (b != 0.0f && !Float.isNaN(a) ? 1 : 0);
    }

    /**
     * A quick approximation test. Tests to see if the absolute of the
     * difference between two values is less than or equal to
     * {@link Utils#EPSILON}, {@value Utils#EPSILON}. Does not handle edge
     * cases.
     *
     * @param a the left comparisand
     * @param b the right comparisand
     * @return the evaluation
     * @see Utils#approx(float, float, float)
     */
    public static boolean approx(final float a, final float b) {

        return Utils.approx(a, b, Utils.EPSILON);
    }

    /**
     * A quick approximation test. Tests to see if the absolute of the
     * difference between two values is less than or equal to a tolerance. Does
     * not handle edge cases.
     *
     * @param a the left comparisand
     * @param b the right comparisand
     * @param t the tolerance
     * @return the evaluation
     */
    public static boolean approx(final float a, final float b, final float t) {

        final float diff = b - a;
        return diff <= t && diff >= -t;
    }

    /**
     * A bounds checked approximation of the arc-sine for single precision real
     * numbers. Returns a value in the range [-pi / 2.0, pi / 2.0]: -pi / 2.0
     * when the input is less than or equal to -1.0; 0.0 when the input is 0.0;
     * pi / 2.0 when the input is greater than or equal to 1.0.
     * <br>
     * <br>
     * {@link Math#asin(double) } defers to {@link StrictMath#asin(double) },
     * which is implemented natively. This is not a "fast" alternative.
     * <br>
     * <br>
     * Based on the algorithm in
     * <a href="https://developer.download.nvidia.com/cg/asin.html">Nvidia
     * Cg 3.1 Toolkit Documentation</a> . This cites M. Abramowitz and I.A.
     * Stegun, Eds., <em>Handbook of Mathematical Functions</em>, possibly p.
     * 83, which cites <em>Approximations for Digital Computers</em> by C.
     * Hastings, Jr.
     *
     * @param y the input value
     * @return the angle in radians
     * @author M. Abramowitz
     * @author C. Hastings, Jr
     * @author I. A. Stegun
     * @see Utils#sqrtUnchecked(float)
     */
    public static float asin(final float y) {

        if (y <= -1.0f) {
            return -Utils.HALF_PI;
        }
        if (y >= 1.0f) {
            return Utils.HALF_PI;
        }
        final boolean ltZero = y < -0.0f;
        final float x = ltZero ? -y : y;
        float ret = (0.074261f - 0.0187293f * x) * x - 0.2121144f;
        ret = ret * x + Utils.HALF_PI;
        ret = Utils.HALF_PI - ret * Utils.sqrtUnchecked(1.0f - x);
        return ltZero ? -ret : ret;
    }

    /**
     * Finds a single precision approximation of a signed angle given a
     * vertical and horizontal component. The vertical component precedes the
     * horizontal. The return value falls in the range [-pi, pi].
     * <br>
     * <br>
     * This is not a "fast" alternative to {@link Math#atan2(double, double)}.
     * <br>
     * <br>
     * Based on the algorithm in
     * <a href="https://developer.download.nvidia.com/cg/atan2.html">Nvidia
     * Cg 3.1 Toolkit Documentation</a> .
     *
     * @param y the y coordinate (the ordinate)
     * @param x the x coordinate (the abscissa)
     * @return the angle in radians
     */
    public static float atan2(final float y, final float x) {

        final boolean yLtZero = y < -0.0f;
        final boolean xLtZero = x < -0.0f;
        final float yAbs = yLtZero ? -y : y;
        final float xAbs = xLtZero ? -x : x;

        final boolean yGtX = yAbs > xAbs;
        float t0 = yGtX ? yAbs : xAbs;
        if (t0 != 0.0f) {
            /* t2 chooses between xAbs / yAbs and yAbs / xAbs. */
            final float t2 = (yGtX ? xAbs : yAbs) / t0;
            final float t3 = t2 * t2;
            t0 = 0.057477314f - 0.01348047f * t3;
            t0 = t0 * t3 - 0.12123907f;
            t0 = t0 * t3 + 0.19563593f;
            t0 = t0 * t3 - 0.3329946f;
            t0 = t0 * t3 + 0.99999565f;
            t0 = t0 * t2;
            t0 = yGtX ? Utils.HALF_PI - t0 : t0;
            t0 = xLtZero ? Utils.PI - t0 : t0;
            return yLtZero ? -t0 : t0;
        }
        return 0.0f;
    }

    /**
     * Appends to an array of bytes, ordered from least to most significant
     * digit (little endian). Writes 4 bytes.
     *
     * @param f4 the float
     * @param a  the array
     * @param j  the index
     * @return the byte array
     * @see Utils#byteslm(int, byte[], int)
     */
    public static byte[] byteslm(final float f4, final byte[] a, final int j) {

        return Utils.byteslm(Float.floatToIntBits(f4), a, j);
    }

    /**
     * Appends to an array of bytes, ordered from least to most significant
     * digit (little endian). Writes 4 bytes.
     *
     * @param i4 the integer
     * @param a  the array
     * @param j  the index
     * @return the byte array
     */
    public static byte[] byteslm(final int i4, final byte[] a, final int j) {

        a[j] = (byte) (i4 & 0xff);
        a[j + 1] = (byte) (i4 >> 0x08 & 0xff);
        a[j + 2] = (byte) (i4 >> 0x10 & 0xff);
        a[j + 3] = (byte) (i4 >> 0x18 & 0xff);

        return a;
    }

    /**
     * Appends to an array of bytes, ordered from least to most significant
     * digit (little endian). Writes 8 bytes.
     *
     * @param i8 the long
     * @param a  the array
     * @param j  the index
     * @return the byte array
     */
    public static byte[] byteslm(final long i8, final byte[] a, final int j) {

        a[j] = (byte) (i8 & 0xffL);
        a[j + 1] = (byte) (i8 >> 0x08L & 0xffL);
        a[j + 2] = (byte) (i8 >> 0x10L & 0xffL);
        a[j + 3] = (byte) (i8 >> 0x18L & 0xffL);

        a[j + 4] = (byte) (i8 >> 0x20L & 0xffL);
        a[j + 5] = (byte) (i8 >> 0x28L & 0xffL);
        a[j + 6] = (byte) (i8 >> 0x30L & 0xffL);
        a[j + 7] = (byte) (i8 >> 0x38L & 0xffL);

        return a;
    }

    /**
     * Appends to an array of bytes, ordered from least to most significant
     * digit (little endian). Writes 2 bytes.
     *
     * @param i2 the short
     * @param a  the array
     * @param j  the index
     * @return the byte array
     */
    public static byte[] byteslm(final short i2, final byte[] a, final int j) {

        a[j] = (byte) (i2 & 0xff);
        a[j + 1] = (byte) (i2 >> 0x08 & 0xff);
        return a;
    }

    /**
     * Appends to an array of bytes, ordered from most to least significant
     * digit (big endian). Writes 4 bytes.
     *
     * @param f4 the float
     * @param a  the array
     * @param j  the index
     * @return the byte array
     */
    public static byte[] bytesml(final float f4, final byte[] a, final int j) {

        return Utils.bytesml(Float.floatToIntBits(f4), a, j);
    }

    /**
     * Appends to an array of bytes, ordered from most to least significant
     * digit (big endian). Writes 4 bytes.
     *
     * @param i4 the integer
     * @param a  the array
     * @param j  the index
     * @return the byte array
     */
    public static byte[] bytesml(final int i4, final byte[] a, final int j) {

        a[j] = (byte) (i4 >> 0x18 & 0xff);
        a[j + 1] = (byte) (i4 >> 0x10 & 0xff);
        a[j + 2] = (byte) (i4 >> 0x08 & 0xff);
        a[j + 3] = (byte) (i4 & 0xff);

        return a;
    }

    /**
     * Appends to an array of bytes, ordered from most to least significant
     * digit (big endian). Writes 8 bytes.
     *
     * @param i8 the long
     * @param a  the array
     * @param j  the index
     * @return the byte array
     */
    public static byte[] bytesml(final long i8, final byte[] a, final int j) {

        a[j] = (byte) (i8 >> 0x38L & 0xffL);
        a[j + 1] = (byte) (i8 >> 0x30L & 0xffL);
        a[j + 2] = (byte) (i8 >> 0x28L & 0xffL);
        a[j + 3] = (byte) (i8 >> 0x20L & 0xffL);

        a[j + 4] = (byte) (i8 >> 0x18L & 0xffL);
        a[j + 5] = (byte) (i8 >> 0x10L & 0xffL);
        a[j + 6] = (byte) (i8 >> 0x08L & 0xffL);
        a[j + 7] = (byte) (i8 & 0xffL);

        return a;
    }

    /**
     * Appends to an array of bytes, ordered from most to least significant
     * digit (big endian). Writes 2 bytes.
     *
     * @param i2 the short
     * @param a  the array
     * @param j  the index
     * @return the byte array
     */
    public static byte[] bytesml(final short i2, final byte[] a, final int j) {

        a[j] = (byte) (i2 >> 0x08 & 0xff);
        a[j + 1] = (byte) (i2 & 0xff);
        return a;
    }

    /**
     * Raises a real number to the next greatest integer. An alternative to
     * {@link Math#ceil(double)}
     * . ceil ( <em>x</em> ) = - floor ( -<em>x</em> ) .
     *
     * @param v the input value
     * @return the raised value
     */
    public static int ceil(final float v) {

        return v > 0.0f ? (int) v + 1 : v < -0.0f ? (int) v : 0;
    }

    /**
     * Clamps a real number between a lower and an upper bound.
     *
     * @param v  the input value
     * @param lb the upper bound
     * @param ub the lower bound
     * @return the clamped value
     */
    @SuppressWarnings("ManualMinMaxCalculation")
    public static float clamp(final float v, final float lb, final float ub) {

        return v < lb ? lb : v > ub ? ub : v;
    }

    /**
     * Clamps an integer between a lower and an upper bound.
     *
     * @param v  the input value
     * @param lb the lower bound
     * @param ub the upper bound
     * @return the clamped value
     */
    @SuppressWarnings("ManualMinMaxCalculation")
    public static int clamp(final int v, final int lb, final int ub) {

        return v < lb ? lb : v > ub ? ub : v;
    }

    /**
     * Clamps an integer between a lower and an upper bound.
     *
     * @param v  the input value
     * @param lb the lower bound
     * @param ub the upper bound
     * @return the clamped value
     */
    @SuppressWarnings("ManualMinMaxCalculation")
    public static long clamp(final long v, final long lb, final long ub) {

        return v < lb ? lb : v > ub ? ub : v;
    }

    /**
     * Clamps a value to the range [0.0, 1.0] .
     *
     * @param v the input value
     * @return the clamped value
     */
    @SuppressWarnings("ManualMinMaxCalculation")
    public static float clamp01(final float v) {

        return v < 0.0f ? 0.0f : v > 1.0f ? 1.0f : v;
    }

    /**
     * Returns the first floating-point argument with the sign of the second
     * floating-point argument. An alias of
     * {@link Math#copySign(float, float)}.
     * <br>
     * <br>
     * When the sign is zero, the return value will depend on the sign of the
     * zero.
     *
     * @param magnitude the magnitude
     * @param sign      the sign
     * @return the magnified sign
     * @see Float#intBitsToFloat(int)
     * @see Float#floatToRawIntBits(float)
     */
    public static float copySign(final float magnitude, final float sign) {

        return Float.intBitsToFloat(
            Float.floatToRawIntBits(sign) & 0x80000000
                | Float.floatToRawIntBits(magnitude) & 0x7fffffff);
    }

    /**
     * Returns the first floating-point argument with the sign of the second
     * integer argument. When the sign is zero, the return value will be
     * positive, as integer zeroes are unsigned.
     *
     * @param magnitude the magnitude
     * @param sign      the sign
     * @return the magnified sign
     * @see Float#intBitsToFloat(int)
     * @see Float#floatToRawIntBits(float)
     */
    public static float copySign(final float magnitude, final int sign) {

        return Float.intBitsToFloat(
            sign & 0x80000000 | Float.floatToRawIntBits(magnitude) & 0x7fffffff);
    }

    /**
     * Finds the single-precision cosine of an angle in radians. Returns a
     * value in the range [-1.0, 1.0] .
     * <br>
     * <br>
     * Wraps {@link Math#cos(double)}.
     *
     * @param radians the angle in radians
     * @return the cosine of the angle
     */
    public static float cos(final float radians) {

        return (float) Math.cos(radians);
    }

    /**
     * Finds the approximate cotangent of the angle in radians. Equivalent to
     * dividing the cosine of the angle by the sine, or to
     * 1.0 / tan ( <em>a</em> ) .
     *
     * @param radians the angle in radians
     * @return the cotangent
     */
    public static float cot(final float radians) {

        final float nrmRad = radians * Utils.ONE_TAU;
        final float sint = Utils.scNorm(nrmRad - 0.25f);
        return sint != 0.0f ? Utils.scNorm(nrmRad) / sint : 0.0f;
    }

    /**
     * Finds the absolute value of the left operand minus the right.
     *
     * @param a left operand
     * @param b right operand
     * @return the difference
     * @see Float#intBitsToFloat(int)
     * @see Float#floatToRawIntBits(float)
     */
    public static float diff(final float a, final float b) {

        return Float.intBitsToFloat(0x7fffffff & Float.floatToRawIntBits(b - a));
    }

    /**
     * Finds the absolute value of the left operand minus the right.
     *
     * @param a left operand
     * @param b right operand
     * @return the difference
     */
    public static int diff(final int a, final int b) {

        final int d = b - a;
        return d < 0 ? -d : d;
    }

    /**
     * Finds the unsigned distance between two angles. Angles are expected to
     * be in radians.
     *
     * @param a left operand
     * @param b right operand
     * @return unsigned distance
     * @see Utils#modRadians(float)
     * @see Utils#abs(float)
     */
    public static float distAngleUnsigned(final float a, final float b) {

        /*
         * See https://gamedev.stackexchange.com/a/4472,
         * https://stackoverflow.com/a/28037434 (for signed)
         */

        return Utils.PI - Utils.abs(Utils.abs(Utils.modRadians(b)
            - Utils.modRadians(a)) - Utils.PI);
    }

    /**
     * Divides the left operand by the right, but returns zero when the
     * denominator is zero, avoiding the {@link java.lang.ArithmeticException} .
     *
     * @param a the numerator
     * @param b the denominator
     * @return the quotient
     */
    public static float div(final float a, final float b) {

        return b != 0.0f ? a / b : 0.0f;
    }

    /**
     * Divides the left operand by the right, but returns zero when the
     * denominator is zero, avoiding the {@link java.lang.ArithmeticException}.
     *
     * @param a the numerator
     * @param b the denominator
     * @return the quotient
     */
    public static int div(final int a, final int b) {

        return b != 0 ? a / b : 0;
    }

    /**
     * Returns the value if it is greater than the lower bound, inclusive, and
     * less than the upper bound, exclusive. Otherwise, returns 0.0.
     *
     * @param v  the input value
     * @param lb the lower bound
     * @param ub the upper bound
     * @return the filtered value
     */
    public static float filter(final float v, final float lb, final float ub) {

        return v >= lb && v < ub ? v : 0.0f;
    }

    /**
     * Parses four bytes in an array to a floating point real number, ordered
     * from least to most significant digit (little endian).
     *
     * @param arr the array
     * @param i   the index
     * @return the float
     * @see Utils#intlm(byte[], int)
     * @see Float#intBitsToFloat(int)
     */
    public static float floatlm(final byte[] arr, final int i) {

        return Float.intBitsToFloat(Utils.intlm(arr, i));
    }

    /**
     * Parses four bytes in an array to a floating point real number, ordered
     * from most to least significant digit (big endian).
     *
     * @param arr the array
     * @param i   the index
     * @return the float
     * @see Utils#intml(byte[], int)
     * @see Float#intBitsToFloat(int)
     */
    public static float floatml(final byte[] arr, final int i) {

        return Float.intBitsToFloat(Utils.intml(arr, i));
    }

    /**
     * Floors a real number to the next least integer. An alternative to
     * {@link Math#floor(double)} .
     *
     * @param v the input value
     * @return the floored value
     */
    public static int floor(final float v) {

        return v > 0.0f ? (int) v : v < -0.0f ? (int) v - 1 : 0;
    }

    /**
     * Applies the modulo operator (<code>%</code>) to the operands. Known in
     * some languages as <code>rem</code> for remainder. An analogous formula
     * would be fmod ( <em>a</em>, <em>b</em> ) := <em>a</em> - <em>b</em>
     * trunc ( <em>a</em> / <em>b</em> ) .<br>
     * <br>
     * When <em>b</em> is zero, returns <em>a</em>. When <em>a</em> is negative
     * and <em>b</em> is positive, the result will be negative.<br>
     * <br>
     * If <em>b</em> is one, use {@link Utils#fract(float)} or <em>a</em>
     * - trunc ( <em>a</em> ).
     *
     * @param a the left operand
     * @param b the right operand
     * @return the result
     */
    public static float fmod(final float a, final float b) {

        return b != 0.0f ? a % b : a;
    }

    /**
     * Applies the modulo operator (%) to the operands. Known in some languages
     * as <code>rem</code> for remainder. When <em>b</em> is zero,
     * returns <em>a</em>.
     *
     * @param a the left operand
     * @param b the right operand
     * @return the result
     */
    public static int fmod(final int a, final int b) {

        return b != 0 ? a % b : a;
    }

    /**
     * Finds the signed fractional portion of the input value by subtracting
     * the value's truncation from the value, i.e., fract ( <em>a</em> ) :=
     * <em>a</em> - trunc ( <em>a</em> ) .
     * <br>
     * <br>
     * Use this instead of fmod ( <em>a</em>, 1.0 ) or <em>a</em> % 1.0 .
     *
     * @param v the input value
     * @return the fractional portion
     */
    public static float fract(final float v) {

        return v - (int) v;
    }

    /**
     * Finds the hypotenuse between two values, sqrt ( <em>a</em><sup>2</sup> +
     * <em>b</em><sup>2</sup> ) .
     *
     * @param a the first value
     * @param b the second value
     * @return the hypotenuse
     * @see Utils#sqrtUnchecked(float)
     */
    public static float hypot(final float a, final float b) {

        /*
         * Do not use Math.hypot . It is not a HotSpotIntrinsicCandidate , and
         * it delegates to StrictMath .
         */

        return Utils.sqrtUnchecked(a * a + b * b);
    }

    /**
     * Finds the hypotenuse given three values, sqrt ( <em>a</em><sup>2</sup> +
     * <em>b</em><sup>2</sup> + <em>c</em><sup>2</sup> ) .
     *
     * @param a the first value
     * @param b the second value
     * @param c the third value
     * @return the hypotenuse
     * @see Utils#sqrtUnchecked(float)
     */
    public static float hypot(final float a, final float b, final float c) {

        return Utils.sqrtUnchecked(a * a + b * b + c * c);
    }


    /**
     * Parses 4 bytes in an array to a 32-bit integer, ordered from least to
     * most significant digit (little endian).
     *
     * @param arr the array
     * @param j   the index
     * @return the float
     */
    public static int intlm(final byte[] arr, final int j) {

        return arr[j] & 0xff
            | (arr[j + 1] & 0xff) << 0x08
            | (arr[j + 2] & 0xff) << 0x10
            | (arr[j + 3] & 0xff) << 0x18;
    }

    /**
     * Parses 4 bytes in an array to a 32-bit integer, ordered from most to
     * least significant digit (big endian).
     *
     * @param arr the array
     * @param j   the index
     * @return the float
     */
    public static int intml(final byte[] arr, final int j) {

        return (arr[j] & 0xff) << 0x18
            | (arr[j + 1] & 0xff) << 0x10
            | (arr[j + 2] & 0xff) << 0x08
            | arr[j + 3] & 0xff;
    }

    /**
     * Finds one divided by the hypotenuse of two values, 1.0 / ( sqrt (
     * <em>a</em><sup>2</sup> + <em>b</em><sup>2</sup> ) ) .
     *
     * @param a the first value
     * @param b the second value
     * @return the inverse hypotenuse
     * @see Utils#invSqrtUnchecked
     */
    public static float invHypot(final float a, final float b) {

        return Utils.invSqrtUnchecked(a * a + b * b);
    }

    /**
     * Finds one divided by the hypotenuse of three values, 1.0 / ( sqrt (
     * <em>a</em><sup>2</sup> + <em>b</em><sup>2</sup>
     * + <em>c</em><sup>2</sup> ) ) .
     *
     * @param a the first value
     * @param b the second value
     * @param c the third value
     * @return the inverse hypotenuse
     * @see Utils#invSqrtUnchecked
     */
    public static float invHypot(final float a, final float b, final float c) {

        return Utils.invSqrtUnchecked(a * a + b * b + c * c);
    }

    /**
     * An inverse square-root. Returns 0.0 when the value is less than or equal
     * to zero. Use the  unchecked version when the input value is known to be
     * positive.
     *
     * @param value the value
     * @return the inverse square root
     * @see Utils#invSqrtUnchecked(float)
     */
    public static float invSqrt(final float value) {

        return value > 0.0f ? Utils.invSqrtUnchecked(value) : 0.0f;
    }

    /**
     * An inverse square root implementation for single precision real numbers
     * based on the 'evil bit hack' from <em>Quake III</em>, as described by
     * Chris Lomont in "<a href="http://www.lomont.org/papers/2003/InvSqrt.pdf">
     * Fast Inverse Square Root</a>." For accuracy, the result is refined three
     * times with the Newton-Raphson method.
     * <br>
     * <br>
     * Useful when normalizing vectors or quaternions. Prefer this over
     * {@link Utils#sqrt(float)}, which depends on this function.
     * <br>
     * <br>
     * Contrary to the name, this should not be assumed to be faster than
     * {@link Math#sqrt(double)}.
     *
     * @param x the value
     * @return the inverse square root
     * @author Chris Lomont
     * @author Greg Walsh
     * @see Float#floatToIntBits(float)
     * @see Float#intBitsToFloat(int)
     */
    public static float invSqrtUnchecked(final float x) {

        float y = Float.intBitsToFloat(0x5f375a86 - (Float.floatToIntBits(x) >> 1));

        final float vhalf = x * 0.5f;
        y *= 1.5f - vhalf * y * y;
        y *= 1.5f - vhalf * y * y;
        y *= 1.5f - vhalf * y * y;

        return y;
    }

    /**
     * Linear interpolation from the origin to the destination value by a step.
     * If the step is less than zero, returns the origin. If the step is
     * greater than one, returns the destination.
     *
     * @param orig the origin value
     * @param dest the destination value
     * @param step the step
     * @return the interpolated value
     */
    public static float lerp(final float orig, final float dest, final float step) {

        return step <= 0.0f ? orig : step >= 1.0f ? dest : (1.0f - step) * orig + step * dest;
    }

    /**
     * Linear interpolation from the origin to the destination value by a step.
     * If the step is less than zero, returns the origin. If the step is
     * greater than one, returns the destination.
     *
     * @param orig the origin value
     * @param dest the destination value
     * @param step the step
     * @return the interpolated value
     * @see Utils#lerpUnclamped(float, float, float)
     */
    public static int lerp(final int orig, final int dest, final float step) {

        if (step <= 0.0f) {
            return orig;
        }
        if (step >= 1.0f) {
            return dest;
        }
        return Utils.lerpUnclamped(orig, dest, step);
    }

    /**
     * Linear interpolation from the origin to the destination value by a step.
     * Does not check to see if the step is beyond the range [0.0, 1.0].
     *
     * @param orig the origin value
     * @param dest the destination value
     * @param step the step
     * @return the interpolated value
     */
    public static float lerpUnclamped(
        final float orig,
        final float dest,
        final float step) {

        return (1.0f - step) * orig + step * dest;
    }

    /**
     * Linear interpolation from the origin to the destination value by a step.
     * Does not check to see if the step is beyond the range [0.0, 1.0].
     * Rounds the result to an integer.
     *
     * @param orig the origin value
     * @param dest the destination value
     * @param step the step
     * @return the interpolated value
     * @see Utils#round(float)
     */
    public static int lerpUnclamped(
        final int orig,
        final int dest,
        final float step) {

        return Utils.round((1.0f - step) * orig + step * dest);
    }

    /**
     * Finds the logarithm of a value in an arbitrary base. Equivalent to
     * finding the natural logarithm of a value, then dividing it by the
     * logarithm of the base. Returns zero if the base is less than or equal
     * to zero.
     *
     * @param a the value
     * @param b the base
     * @return the evaluation
     * @see Math#log(double)
     */
    public static float log(final float a, final float b) {

        if (a > 0.0f && b > 0.0f) {
            return (float) (Math.log(a) / Math.log(b));
        }
        return 0.0f;
    }

    /**
     * Maps an input value from an original range to a target range. If the
     * upper and lower bound of the original range are equal, will return the
     * value unchanged. Clamps the result to the lower and upper bounds of the
     * target range.
     *
     * @param v      the input value
     * @param lbOrig the lower bound of original range
     * @param ubOrig the upper bound of original range
     * @param lbDest the lower bound of destination range
     * @param ubDest the upper bound of destination range
     * @return the mapped value
     */
    public static float map(
        final float v,
        final float lbOrig,
        final float ubOrig,
        final float lbDest,
        final float ubDest) {

        final float d = ubOrig - lbOrig;
        if (d == 0.0f) {
            return v;
        }
        final float n = (v - lbOrig) / d;
        if (n <= 0.0f) {
            return lbDest;
        }
        if (n >= 1.0f) {
            return ubDest;
        }
        return lbDest + n * (ubDest - lbDest);
    }

    /**
     * Maps an input value from an original range to a target range. If the
     * upper and lower bound of the original range are equal, will return the
     * value unchanged. Clamps the result to the lower and upper bounds of the
     * target range. If gamma is zero, returns the linear remap. Takes the
     * absolute of gamma.
     *
     * @param v      the input value
     * @param lbOrig the lower bound of original range
     * @param ubOrig the upper bound of original range
     * @param lbDest the lower bound of destination range
     * @param ubDest the upper bound of destination range
     * @param gamma  the exponent
     * @return the mapped value
     */
    public static float map(
        final float v,
        final float lbOrig,
        final float ubOrig,
        final float lbDest,
        final float ubDest,
        final float gamma) {

        if (gamma == 0.0f) {
            return Utils.map(v, lbOrig, ubOrig, lbDest, ubDest);
        }

        final double d = ubOrig - lbOrig;
        if (d == 0.0d) {
            return v;
        }

        final double n = (v - lbOrig) / d;
        if (n <= 0.0d) {
            return lbDest;
        }
        if (n >= 1.0d) {
            return ubDest;
        }

        return (float) (lbDest + Math.pow(n, 1.0d / Math.abs(gamma)) * (ubDest - lbDest));
    }

    /**
     * Finds the greatest, or maximum, among a list of values. Returns negative
     * {@link Float#MAX_VALUE}, {@value Float#MAX_VALUE}, if the list's length
     * is zero.
     *
     * @param fs the list of values
     * @return the maximum value
     */
    public static float max(final float... fs) {

        float max = -Float.MAX_VALUE;
        for (final float f : fs) {
            if (f > max) {
                max = f;
            }
        }
        return max;
    }

    /**
     * Finds the greater, or maximum, of two values. An alternative to
     * {@link Math#max(float, float)} .
     *
     * @param a the left operand
     * @param b the right operand
     * @return the maximum value
     */
    @SuppressWarnings("ManualMinMaxCalculation")
    public static float max(final float a, final float b) {

        return a > b ? a : b;
    }

    /**
     * Finds the greatest, or maximum, among three values.
     *
     * @param a the first value
     * @param b the second value
     * @param c the third value
     * @return the maximum value
     */
    @SuppressWarnings("ManualMinMaxCalculation")
    public static float max(final float a, final float b,
        final float c) {

        final float d = a > b ? a : b;
        return d > c ? d : c;
    }

    /**
     * Finds the greatest, or maximum, among three values.
     *
     * @param a the first value
     * @param b the second value
     * @param c the third value
     * @return the maximum value
     */
    @SuppressWarnings("ManualMinMaxCalculation")
    public static int max(final int a, final int b, final int c) {

        final int d = a > b ? a : b;
        return d > c ? d : c;
    }

    /**
     * Finds the least, or minimum, among a list of values. Returns
     * {@link Float#MAX_VALUE}, {@value Float#MAX_VALUE}, if the list's length
     * is zero.
     *
     * @param fs the list of values
     * @return the minimum value
     */
    public static float min(final float... fs) {

        float min = Float.MAX_VALUE;
        for (final float f : fs) {
            if (f < min) {
                min = f;
            }
        }
        return min;
    }

    /**
     * Finds the lesser, or minimum, of two values. An alternative to
     * {@link Math#min(float, float)} .
     *
     * @param a the left operand
     * @param b the right operand
     * @return the minimum value
     */
    @SuppressWarnings("ManualMinMaxCalculation")
    public static float min(final float a, final float b) {

        return a < b ? a : b;
    }

    /**
     * Finds the least, or minimum, among three values.
     *
     * @param a the first value
     * @param b the second value
     * @param c the third value
     * @return the minimum value
     */
    @SuppressWarnings("ManualMinMaxCalculation")
    public static float min(final float a, final float b,
        final float c) {

        final float d = a < b ? a : b;
        return d < c ? d : c;
    }

    /**
     * Finds the least, or minimum, among three values.
     *
     * @param a the first value
     * @param b the second value
     * @param c the third value
     * @return the minimum value
     */
    @SuppressWarnings("ManualMinMaxCalculation")
    public static int min(final int a, final int b, final int c) {

        final int d = a < b ? a : b;
        return d < c ? d : c;
    }

    /**
     * Applies floor modulo to the operands. Uses the formula mod ( <em>a</em>,
     * <em>b</em> ) := <em>a</em> - <em>b</em> * floor ( <em>a</em> /
     * <em>b</em> ). When <em>b</em> is zero, returns <em>a</em>. If <em>b</em>
     * is one, use {@link Utils#mod1(float)} or <em>a</em> - floor(<em>a</em>)
     * instead.
     *
     * @param a the left operand
     * @param b the right operand
     * @return the result
     * @see Utils#modUnchecked(float, float)
     */
    public static float mod(final float a, final float b) {

        if (b != 0.0f) {
            final float quot = a / b;
            return a - b * (quot > 0.0f
                ? (int) quot
                : quot < -0.0f ? (int) quot - 1.0f
                : 0.0f);
        }
        return a;
    }

    /**
     * Applies floor modulo to the operands. Returns the left operand when the
     * right operand is zero. An alternative to {@link Math#floorMod(int, int)}.
     *
     * @param a the left operand
     * @param b the right operand
     * @return the result
     */
    public static int mod(final int a, final int b) {

        /*
         * Floor mod is not the same as Euclidean remainder. See
         * https://harry.garrood.me/blog/integer-division/ .
         */

        // return b != 0 ? ( a % b + b ) % b : a;
        if (b != 0) {
            final int result = a - b * (a / b);
            return result < 0 ? result + b : result;
        }
        return a;
    }

    /**
     * Applies floor modulo to the operands. Returns the left operand when the
     * right operand is zero. An alternative to {@link Math#floorMod(int, int)}.
     *
     * @param a the left operand
     * @param b the right operand
     * @return the result
     */
    public static long mod(final long a, final long b) {

        // return b != 0 ? ( a % b + b ) % b : a;
        if (b != 0) {
            final long result = a - b * (a / b);
            return result < 0 ? result + b : result;
        }
        return a;
    }

    /**
     * Subtracts the floor of the input value from the value. Returns a
     * positive value in the range [0.0, 1.0] . Equivalent to GLSL's
     * <code>fract</code>.
     *
     * @param v the value
     * @return the result
     */
    public static float mod1(final float v) {

        return v > 0.0f ? v - (int) v : v < -0.0f ? v - ((int) v - 1.0f) : 0.0f;
    }

    /**
     * A specialized version of modulo which shifts an angle in degrees to the
     * range [0.0, 360.0] .
     *
     * @param degrees the angle in degrees
     * @return the wrapped angle
     * @see Utils#floor(float)
     */
    public static float modDegrees(final float degrees) {

        // return degrees - 360.0f * Utils.floor(degrees * Utils.ONE_360);
        final float dNorm = degrees * Utils.ONE_360;
        return degrees - 360.0f * (dNorm > 0.0f
            ? (int) dNorm
            : dNorm < -0.0f
            ? (int) dNorm - 1.0f
            : 0.0f);
    }

    /**
     * A specialized version of modulo which shifts an angle in radians to the
     * range [0.0, tau].
     *
     * @param radians the angle in radians
     * @return the wrapped radians
     * @see Utils#mod(float, float)
     * @see Utils#floor(float)
     */
    public static float modRadians(final float radians) {

        // return radians - Utils.TAU * Utils.floor(radians * Utils.ONE_TAU);
        final float rNorm = radians * Utils.ONE_TAU;
        return radians - Utils.TAU * (rNorm > 0.0f
            ? (int) rNorm
            : rNorm < -0.0f
            ? (int) rNorm - 1.0f
            : 0.0f);
    }

    /**
     * Applies floor modulo to the operands. Does not check if the right
     * operand is zero.
     *
     * @param a the left operand
     * @param b the right operand
     * @return the result
     */
    public static float modUnchecked(final float a, final float b) {

        // return a - b * Utils.floor(a / b);
        final float q = a / b;
        return a - b * (q > 0.0f ? (int) q : q < -0.0f ? (int) q - 1.0f : 0.0f);
    }

    /**
     * Finds the next greater power of two for an integer. Returns zero if the
     * input is zero.
     *
     * @param v the integer
     * @return the power
     */
    public static int nextPowerOf2(final int v) {
        if (v != 0) {
            int vSgn = 1;
            int vAbs = v;
            if (v < 0) {
                vAbs = -v;
                vSgn = -1;
            }
            int p = 1;
            while (p < vAbs) {
                p <<= 1;
            }
            return p * vSgn;
        }
        return 0;
    }

    /**
     * Finds the logical opposite of a float holding a boolean value. Returns
     * 0 for non-zero inputs. Returns 1 for inputs equal to zero.
     *
     * @param v the input value
     * @return the opposite
     */
    public static int not(final float v) {

        return v != 0.0f && !Float.isNaN(v) ? 0 : 1;
    }

    /**
     * Evaluates two floats like booleans, using the inclusive or (OR) logic
     * gate. Non-zero inputs evaluate to true, or 1. Zero evaluates to false,
     * or zero.
     *
     * @param a the left operand
     * @param b the right operand
     * @return the evaluation
     */
    public static int or(final float a, final float b) {

        return (a != 0.0f && !Float.isNaN(a) ? 1 : 0)
            | (b != 0.0f && !Float.isNaN(a) ? 1 : 0);
    }

    /**
     * Oscillates between [0.0, 1.0] based on an input step.
     *
     * @param step the step
     * @return the oscillation
     */
    public static float pingPong(final float step) {

        return Utils.pingPong(0.0f, 1.0f, step, 1.0f);
    }

    /**
     * Oscillates between [0.0, 1.0] based on an input step and a pause factor.
     * When the pause is greater than 1.0, the value will be clamped to the
     * bound before returning to the other pole.
     *
     * @param step  the step
     * @param pause the pause factor
     * @return the oscillation
     */
    public static float pingPong(final float step, final float pause) {

        return Utils.pingPong(0.0f, 1.0f, step, pause);
    }

    /**
     * Oscillates between a lower and upper bound based on an input step.
     *
     * @param lb   the lower bound
     * @param ub   the upper bound
     * @param step the step
     * @return the oscillation
     */
    public static float pingPong(final float lb, final float ub, final float step) {

        return Utils.pingPong(lb, ub, step, 1.0f);
    }

    /**
     * Oscillates between a lower and upper bound based on an input step and
     * a pause factor. When the pause is greater than 1.0, the value will be
     * clamped to the bound before returning to the other pole.
     *
     * @param lb    the lower bound
     * @param ub    the upper bound
     * @param step  the step
     * @param pause the pause factor
     * @return the oscillation
     */
    public static float pingPong(
        final float lb, final float ub, final float step, final float pause) {

        /*
         * Cheaper alternative: float z = 2.0f * (x - floor(x)) - 1.0f; return z >
         * 0.0f ? 1.0f - z : z < -0.0f ? 1.0f + z : 1.0f; By subjecting the
         * results to smoothStep, a sine wave can be approximated.
         */

        final float t = 0.5f + 0.5f * pause * Utils.scNorm(step - 0.5f);
        if (t <= 0.0f) {
            return lb;
        }
        if (t >= 1.0f) {
            return ub;
        }
        return (1.0f - t) * lb + t * ub;
    }

    /**
     * Oscillates between a lower and upper bound based on an input step.
     *
     * @param lb   the lower bound
     * @param ub   the upper bound
     * @param step the step
     * @return the oscillation
     */
    public static int pingPong(final int lb, final int ub, final float step) {

        return (int) Utils.pingPong((float) lb, (float) ub, step);
    }

    /**
     * Oscillates between a lower and upper bound based on an input step and a
     * pause factor. When the pause is greater than 1.0, the value will be
     * clamped to the bound before returning to the other pole.
     *
     * @param lb    the lower bound
     * @param ub    the upper bound
     * @param step  the step
     * @param pause the pause factor
     * @return the oscillation
     */
    public static int pingPong(final int lb, final int ub, final float step, final float pause) {

        return (int) Utils.pingPong((float) lb, (float) ub, step, pause);
    }

    /**
     * Finds the single-precision of a number raised to the power of another.
     * Wraps {@link Math#pow(double, double)} .
     *
     * @param a left operand
     * @param b right operand
     * @return the power
     * @see Math#pow(double, double)
     */
    public static float pow(final float a, final float b) {

        return (float) Math.pow(a, b);
    }

    /**
     * Reduces the signal, or granularity, of a value. Defaults to signed
     * quantization.
     *
     * @param v      the value
     * @param levels the levels
     * @return the quantized value
     */
    public static float quantize(final float v, final int levels) {

        return Utils.quantizeSigned(v, levels);
    }

    /**
     * Reduces the signal, or granularity, of a signed value. The quantization
     * is centered about zero. Applied to a vector, this yields a crenelated
     * effect. If the levels are zero, returns the value unaltered.
     *
     * @param v      the value
     * @param levels the levels
     * @return the quantized value
     */
    public static float quantizeSigned(final float v, final int levels) {

        if (levels == 0) {
            return v;
        }
        final float levf = levels < 0 ? -levels : levels;
        return Utils.quantizeSigned(v, levf, 1.0f / levf);
    }

    /**
     * Reduces the signal, or granularity, of an unsigned value. The
     * quantization treats zero as a left edge. Applied to a color, this yields
     * a posterization effect. If the levels are 1 or -1, returns the value
     * unaltered.
     *
     * @param v      the value
     * @param levels the levels
     * @return the quantized value
     */
    public static float quantizeUnsigned(final float v, final int levels) {

        if (levels == 1 || levels == -1) {
            return v;
        }
        final float levf = levels < 0 ? -levels : levels;
        return Utils.quantizeUnsigned(v, levf, 1.0f / (levf - 1.0f));
    }

    /**
     * Rounds a value to an integer based on whether its fractional portion is
     * greater than or equal to plus or minus 0.5.
     *
     * @param v the input value
     * @return the rounded value
     */
    public static int round(final float v) {

        return v < -0.0f ? (int) (v - 0.5f) : v > 0.0f ? (int) (v + 0.5f) : 0;
    }

    /**
     * A helper method to facilitate the approximate sine and cosine of an angle
     * with single precision real numbers. The radians supplied to this function
     * should be normalized through division by tau. Subtract <code>0.25</code>
     * from the input value to return the sine instead of the cosine.
     * <br>
     * <br>
     * This is based on the algorithm described in <a
     * href="https://developer.download.nvidia.com/cg/sin.html">Nvidia Cg 3.1
     * Toolkit Documentation</a> .
     *
     * @param normRad the normalized radians
     * @return the approximate value
     */
    public static float scNorm(final float normRad) {

        // float r1y = Utils.mod1(normRad);
        float r1y = normRad > 0.0f
            ? normRad - (int) normRad
            : normRad < 0.0f ? normRad - ((int) normRad - 1.0f) : 0.0f;

        final boolean r2x = r1y < 0.25f;
        float r1x = 0.0f;
        if (r2x) {
            final float r0x = r1y * r1y;
            r1x = 24.980804f * r0x - 60.14581f;
            r1x = r1x * r0x + 85.45379f;
            r1x = r1x * r0x - 64.939354f;
            r1x = r1x * r0x + 19.739208f;
            r1x = r1x * r0x - 1.0f;
        }

        final boolean r2z = r1y >= 0.75f;
        float r1z = 0.0f;
        if (r2z) {
            float r0z = 1.0f - r1y;
            r0z = r0z * r0z;
            r1z = 24.980804f * r0z - 60.14581f;
            r1z = r1z * r0z + 85.45379f;
            r1z = r1z * r0z - 64.939354f;
            r1z = r1z * r0z + 19.739208f;
            r1z = r1z * r0z - 1.0f;
        }

        float r0y = 0.5f - r1y;
        r1y = 0.0f;
        if (!r2x && !r2z) {
            r0y = r0y * r0y;
            r1y = 60.14581f - r0y * 24.980804f;
            r1y = r1y * r0y - 85.45379f;
            r1y = r1y * r0y + 64.939354f;
            r1y = r1y * r0y - 19.739208f;
            r1y = r1y * r0y + 1.0f;
        }

        return -r1x - r1z - r1y;
    }

    /**
     * Parses two bytes in an array to a 16-bit integer (short), ordered from
     * least to most significant digit (little endian).
     *
     * @param arr the array
     * @param i   the index
     * @return the short
     */
    public static short shortlm(final byte[] arr, final int i) {

        return (short) (arr[i] & 0xff | (arr[i + 1] & 0xff) << 0x08);
    }

    /**
     * Parses two bytes in an array to a 16-bit integer (short), ordered from
     * most to least significant digit (big endian).
     *
     * @param arr the array
     * @param i   the index
     * @return the short
     */
    public static short shortml(final byte[] arr, final int i) {

        return (short) ((arr[i] & 0xff) << 0x08 | arr[i + 1] & 0xff);
    }

    /**
     * An alternative to the {@link Math#signum(float)} function. Returns the
     * integer 0 for both -0.0 (signed negative zero) and 0.0 (signed positive
     * zero).
     *
     * @param v the value
     * @return the sign
     * @see Math#signum(float)
     */
    public static int sign(final float v) {

        return v < -0.0f ? -1 : v > 0.0f ? 1 : 0;
    }

    /**
     * Finds the single precision sine of an angle in radians. Returns a value
     * in the range [-1.0, 1.0] .
     * <br>
     * <br>
     * Wraps {@link Math#sin(double)}.
     *
     * @param radians the angle in radians
     * @return the sine of the angle
     */
    public static float sin(final float radians) {

        return (float) Math.sin(radians);
    }

    /**
     * Eases between an origin and destination by a step in [0.0, 1.0] .
     *
     * @param orig the origin
     * @param dest the destination
     * @param step the step
     * @return the eased value
     */
    public static float smoothStep(
        final float orig,
        final float dest,
        final float step) {

        /*
         * smoothStepInverse: 0.5 - sin(arcsin(1 - 2 * x) / 3)
         *
         * Approximations:
         * https://www.desmos.com/calculator/zfilktih8o
         *
         * Cubic:
         * 1.3472x^{3} - 2.0208x^{2} + 1.59425x + 0.0396757
         *
         * Quartic:
         * (-4.0368318457351 * 10^{-8})x^{4}+1.3472x^{3}-2.0208x^{2}+1.59425x+0.0396757
         *
         * Quintic:
         * 8.73264x^{5}-21.8316x^{4}+20.6117x^{3}-9.08597x^{2}+2.54322x+0.014996
         * */

        if (step <= 0.0f) {
            return orig;
        }
        if (step >= 1.0f) {
            return dest;
        }
        final float t = step * step * (3.0f - (step + step));
        return (1.0f - t) * orig + t * dest;
    }

    /**
     * Finds the approximate square root of a value. Returns 0.0 when the
     * value is less than zero. An alternative to {@link Math#sqrt(double)} .
     * Use {@link Utils#sqrtUnchecked(float)} when the value is known to be
     * positive.
     *
     * @param v the value
     * @return the square root
     * @see Utils#sqrtUnchecked(float)
     */
    public static float sqrt(final float v) {

        return v > 0.0f ? Utils.sqrtUnchecked(v) : 0.0f;
    }

    /**
     * Finds the approximate square root of a value. Does so by multiplying the
     * value by its inverse square root.
     *
     * @param v the value
     * @return the square root
     * @see Utils#invSqrtUnchecked(float)
     */
    public static float sqrtUnchecked(final float v) {

        return v * Utils.invSqrtUnchecked(v);
    }

    /**
     * Finds the tangent of an angle. Equivalent to dividing the sine of the
     * angle by the cosine. An alternative to the double precision
     * {@link Math#tan(double)} , this function uses single-precision numbers.
     *
     * @param radians the angle in radians
     * @return the tangent
     * @see Utils#scNorm(float)
     */
    public static float tan(final float radians) {

        final float nrmRad = radians * Utils.ONE_TAU;
        final float cost = Utils.scNorm(nrmRad);
        return cost != 0.0f ? Utils.scNorm(nrmRad - 0.25f) / cost : 0.0f;
    }

    /**
     * Returns a string representation of an array of bytes. Places a space
     * every byte, a second space every word, and a line break every 16 bytes.
     *
     * @param arr the byte array
     * @return the string
     */
    public static String toDiagnosticString(final byte[] arr) {

        final StringBuilder sb = new StringBuilder(256);
        final int len = arr.length;
        for (int i = 0; i < len; ++i) {
            Utils.toHexDigit(sb, arr[i]);
            if (i % 16 < 15) {
                sb.append(' ');
                if (i % 4 == 3) {
                    sb.append(' ');
                }
            } else {
                sb.append("\r\n");
            }
        }

        return sb.toString().toUpperCase();
    }

    /**
     * A quick, dirty representation of a single precision real number as a
     * String to a number of places. Truncates the final digit right of the
     * decimal place. Edge cases:
     *
     * <ul>
     * <li>When the number of places is less than one, returns the String of
     * the truncated value.</li>
     * <li>When the value is not a number ({@link Float#NaN}), returns
     * "0.0".</li>
     * <li>When the value is greater than {@link Float#MAX_VALUE}, returns the
     * max value; when less than {@link Float#MIN_VALUE}, the minimum value.
     * This truncates positive and negative infinities to these bounds.</li>
     * <li>When the integral contains so many digits that accurate
     * representation is unlikely, such as with scientific notation, defers to
     * {@link Float#toString(float)}.</li>
     * </ul>
     * <p>
     * Intended to serve as an alternative to
     * {@link String#format(String, Object...)}, which is very
     * slow, and DecimalFormat, which extrapolates values beyond the last
     * decimal place.
     *
     * @param v      the real number
     * @param places the number of decimal places
     * @return the string
     */
    public static String toFixed(final float v, final int places) {

        return Utils.toFixed(new StringBuilder(16), v, places).toString();
    }

    /**
     * Returns an integer formatted as a string padded by initial zeroes.
     *
     * @param v      the integer
     * @param places the number of places
     * @return the string
     */
    public static String toPadded(final int v, final int places) {

        return Utils.toPadded(new StringBuilder(16), v, places).toString();
    }

    /**
     * Returns a String representation of a one dimensional array of
     * <code>float</code>s.
     *
     * @param arr    the array
     * @param places the print precision
     * @return the String
     */
    public static String toString(final float[] arr, final int places) {

        return Utils.toString(new StringBuilder(256), arr, places).toString();
    }

    /**
     * Returns a String representation of a one dimensional array of
     * <code>int</code>s.
     *
     * @param arr     the array
     * @param padding the digit padding
     * @return the String
     */
    public static String toString(final int[] arr, final int padding) {

        return Utils.toString(new StringBuilder(256), arr, padding).toString();
    }

    /**
     * Truncates the input value. This is an alias for explicitly casting a
     * float to an integer, then implicitly casting the integral to a float.
     *
     * @param v the input value
     * @return the integral
     */
    public static float trunc(final float v) {
        return (int) v;
    }

    /**
     * An alias for {@link Byte#toUnsignedInt(byte)} . Converts a signed byte
     * in the range [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] to an
     * unsigned byte in the range [0, 255], promoted to an integer.
     *
     * @param v the signed byte
     * @return the unsigned byte, promoted
     */
    public static int ubyte(final byte v) {
        return v & 0xff;
    }

    /**
     * An alias for {@link Integer#toUnsignedLong(int)}. Converts a signed
     * integer in the range [{@value Integer#MIN_VALUE},
     * {@value Integer#MAX_VALUE}] to an unsigned integer in the range
     * [0, 4294967295], promoted to a long.
     *
     * @param v the signed integer
     * @return the unsigned integer, promoted
     */
    public static long uint(final int v) {
        return v & 0xffffffffL;
    }

    /**
     * An alias for {@link Short#toUnsignedInt(short)} . Converts a signed
     * short in the range [{@value Short#MIN_VALUE}, {@value Short#MAX_VALUE}]
     * to an unsigned short in the range [0, 65535], promoted to an integer.
     *
     * @param v the signed byte
     * @return the unsigned byte, promoted
     */
    public static int ushort(final short v) {
        return v & 0xffff;
    }

    /**
     * Wraps a value around a periodic range as defined by an upper and lower
     * bound. The lower bound is inclusive; the upper bound is exclusive.
     * Returns the value unchanged if the range is zero.
     *
     * @param v  the value
     * @param lb the lower bound
     * @param ub the upper bound
     * @return the wrapped value
     */
    public static float wrap(final float v, final float lb, final float ub) {

        final float range = ub - lb;
        if (range != 0.0f) {
            final float b = (v - lb) / range;
            return v - range * (b > 0.0f ? (int) b : b < 0.0f ? (int) b - 1.0f : 0.0f);
        }
        return v;
    }

    /**
     * Evaluates two floats like booleans using the exclusive or (XOR) logic
     * gate. Non-zero inputs evaluate to true, or 1. Zero evaluates to false,
     * or 0.
     *
     * @param a the left operand
     * @param b the right operand
     * @return the evaluation
     */
    public static int xor(final float a, final float b) {

        return (a != 0.0f && !Float.isNaN(a) ? 1 : 0)
            ^ (b != 0.0f && !Float.isNaN(b) ? 1 : 0);
    }

    /**
     * An internal helper function. Reduces the signal, or granularity, of a
     * signed value. The quantization is centered about zero. Applied to a
     * vector, this yields a crenelated effect.
     *
     * @param v      the value
     * @param levels the levels
     * @return the quantized value
     * @see Utils#floor(float)
     */
    static float quantizeSigned(
        final float v,
        final float levels,
        final float delta) {

        return Utils.floor(0.5f + v * levels) * delta;
    }

    /**
     * An internal helper function. Reduces the signal, or granularity, of an
     * unsigned value. The quantization treats zero as a left edge. Applied to
     * a color, this yields a posterization effect.
     *
     * @param v      the value
     * @param levels the levels
     * @return the quantized value
     * @see Utils#ceil(float)
     */
    static float quantizeUnsigned(
        final float v,
        final float levels,
        final float delta) {

        return Math.max((Utils.ceil(v * levels) - 1.0f) * delta, 0.0f);
    }

    /**
     * An internal helper function to the {@link Utils#toFixed(float, int)}
     * method and other String representation functions. Appends to a
     * {@link StringBuilder} passed in by reference.
     *
     * @param sb     the string builder
     * @param v      the real number
     * @param places the number of decimal places
     * @return the string builder
     */
    static StringBuilder toFixed(
        final StringBuilder sb,
        final float v,
        final int places) {

        final int raw = Float.floatToRawIntBits(v);
        switch (raw) {
            case 0x0: /* Positive zero. */
            case 0x80000000: /* Negative zero. */
            case 0x7fc00000: /* Not a number (NaN). */
                return sb.append("0.0");

            case 0xff800000: /* Negative infinity. */
            case 0xff7fffff:
                return sb.append("-3.4028235E38");

            case 0x1: /* Minimum value. */
                return sb.append("1.4E-45");

            case 0x7f800000: /* Positive infinity. */
            case 0x7f7fffff: /* Max value. */
                return sb.append("3.4028235E38");

            default:
        }

        if (places < 0) {
            return sb.append((int) v);
        }
        if (places < 1) {
            return sb.append(Float.toString((int) v));
        }

        final float sign = Float.intBitsToFloat(raw & -2147483648 | 1065353216);
        final float abs = Float.intBitsToFloat(raw & 2147483647);
        final int trunc = (int) abs;

        /* Append integral to StringBuilder. */
        final int oldLen = sb.length();
        final int len;
        if (sign < -0.0f) {
            sb.append('-').append(trunc);
            len = sb.length() - oldLen - 1;
        } else {
            sb.append(trunc);
            len = sb.length() - oldLen;
        }
        sb.append('.');

        /*
         * Hard-coded limit on the number of worthwhile decimal places beyond
         * which single precision is no longer worth representing accurately.
         */
        final int maxPlaces = 9 - len;

        /*
         * The integral has so many digits that it has consumed the allotment.
         * (Might be scientific notation?)
         */
        if (maxPlaces < 1) {
            return sb.append(v);
        }

        final int vetPlaces = Math.min(places, maxPlaces);
        float frac = abs - trunc;

        /* Truncation. */
        for (int i = 0; i < vetPlaces; ++i) {
            frac *= 10.0f;
            final int tr = (int) frac;
            frac -= tr;
            sb.append(tr);
        }

        return sb;
    }

    /**
     * A helper function to translate a byte to a hexadecimal string. Does
     * <em>not</em> prefix the String with a hexadecimal indicator, '0x'; this
     * is so that Strings can be concatenated together.
     *
     * @param sb the string builder
     * @param b  the byte
     */
    static void toHexDigit(final StringBuilder sb, final int b) {

        final int digit0 = b >> 0x4 & 0xf;
        final int digit1 = b & 0xf;

        switch (digit0) {
            case 0xa:
                sb.append('a');
                break;
            case 0xb:
                sb.append('b');
                break;
            case 0xc:
                sb.append('c');
                break;
            case 0xd:
                sb.append('d');
                break;
            case 0xe:
                sb.append('e');
                break;
            case 0xf:
                sb.append('f');
                break;
            default:
                sb.append(digit0);
        }

        switch (digit1) {
            case 0xa:
                sb.append('a');
                break;
            case 0xb:
                sb.append('b');
                break;
            case 0xc:
                sb.append('c');
                break;
            case 0xd:
                sb.append('d');
                break;
            case 0xe:
                sb.append('e');
                break;
            case 0xf:
                sb.append('f');
                break;
            default:
                sb.append(digit1);
        }
    }

    /**
     * An internal helper function to the {@link Utils#toPadded(int, int)}
     * method and other String representation functions. Appends to a
     * {@link StringBuilder} passed in by reference.
     *
     * @param sb     the string builder
     * @param v      the integer
     * @param places the number of places
     * @return the string builder
     */
    static StringBuilder toPadded(
        final StringBuilder sb,
        final int v,
        final int places) {

        /*
         * Double precision is needed to preserve accuracy. The max integer
         * value is 2147483647, which is 10 digits long. The sign needs to be
         * flipped because working with positive absolute value would allow
         * Integer#MIN_VALUE to overflow to zero.
         */

        final boolean isNeg = v < 0;
        int nAbsVal = isNeg ? v : -v;

        final int[] digits = new int[10];
        int filled = 0;
        while (nAbsVal < 0) {
            final double y = nAbsVal * 0.1d;
            nAbsVal = (int) y;
            digits[filled] = -(int) ((y - nAbsVal) * 10.0d - 0.5d);
            ++filled;
        }

        if (isNeg) {
            sb.append('-');
        }
        int vplaces = Math.max(places, 1);
        vplaces = Math.max(filled, vplaces);
        for (int n = vplaces - 1; n > -1; --n) {
            sb.append(digits[n]);
        }

        return sb;
    }

    /**
     * An internal helper function. Appends to a {@link StringBuilder} passed
     * in by reference.
     *
     * @param sb     the string builder
     * @param arr    the array
     * @param places the number of places
     * @return the string builder
     * @see Utils#toFixed(StringBuilder, float, int)
     */
    static StringBuilder toString(
        final StringBuilder sb,
        final float[] arr,
        final int places) {

        final int len = arr.length;
        final int last = len - 1;

        sb.append('[');
        for (int i = 0; i < last; ++i) {
            Utils.toFixed(sb, arr[i], places);
            sb.append(',');
        }
        Utils.toFixed(sb, arr[last], places);
        sb.append(']');
        return sb;
    }

    /**
     * An internal helper function. Appends to a {@link StringBuilder} passed
     * in by reference.
     *
     * @param sb      the string builder
     * @param arr     the array
     * @param padding the padding
     * @return the string builder
     * @see Utils#toPadded(StringBuilder, int, int)
     */
    static StringBuilder toString(
        final StringBuilder sb,
        final int[] arr,
        final int padding) {

        final int len = arr.length;
        final int last = len - 1;

        sb.append('[');
        for (int i = 0; i < last; ++i) {
            Utils.toPadded(sb, arr[i], padding);
            sb.append(',');
        }
        Utils.toPadded(sb, arr[last], padding);
        sb.append(']');
        return sb;
    }

    /**
     * A functional interface for an easing function which interpolates an
     * array.
     *
     * @param <T> the parameter type
     */
    public interface EasingFuncArr<T> {

        /**
         * Apply the function.
         *
         * @param arr    the array
         * @param step   the step
         * @param target the target object
         * @return the eased object
         */
        T apply(final T[] arr, Float step, T target);
    }

    /**
     * A functional interface for an easing function which interpolates an
     * object from an origin to a destination by a float, with a final output
     * parameter.
     *
     * @param <T> the parameter type
     */
    public interface EasingFuncObj<T> extends QuadFunction<T, T, Float, T, T> {

        /**
         * Apply the function.
         *
         * @param orig   the origin
         * @param dest   the destination
         * @param step   the step
         * @param target the target object
         * @return the eased object
         */
        @Override
        T apply(final T orig, final T dest, final Float step, T target);
    }

    /**
     * A functional interface for an easing function which interpolates a
     * primitive data type from an origin to a destination by a float.
     *
     * @param <T> the parameter type
     */
    public interface EasingFuncPrm<T> extends TriFunction<T, T, Float, T> {

        /**
         * Apply the function.
         *
         * @param orig the origin
         * @param dest the destination
         * @param step the step
         * @return the eased object
         */
        @Override
        T apply(final T orig, final T dest, final Float step);
    }

    /**
     * A functional interface for functions with four input parameters.
     *
     * @param <T> first parameter
     * @param <U> second parameter
     * @param <V> third parameter
     * @param <W> fourth parameter
     * @param <R> return type
     */
    public interface QuadFunction<T, U, V, W, R> {

        /**
         * Apply the function.
         *
         * @param t first parameter
         * @param u second parameter
         * @param v third parameter
         * @param w fourth parameter
         * @return return type
         */
        R apply(T t, U u, V v, W w);
    }

    /**
     * A functional interface for functions with three input parameters.
     *
     * @param <T> first parameter
     * @param <U> second parameter
     * @param <V> third parameter
     * @param <R> return type
     */
    public interface TriFunction<T, U, V, R> {

        /**
         * Apply the function.
         *
         * @param t first parameter
         * @param u second parameter
         * @param v third parameter
         * @return result
         */
        R apply(T t, U u, V v);
    }

    /**
     * Linear interpolation for a periodic value in the counter-clockwise
     * direction.
     */
    public static class LerpCCW extends PeriodicEasing {

        /**
         * Constructs the lerp CCW functional object with a default range.
         */
        public LerpCCW() {
        }

        /**
         * Constructs the lerp CCW functional object with a specified range.
         *
         * @param range the range of the period
         */
        public LerpCCW(final float range) {
            super(range);
        }

        /**
         * Applies the lerp CCW function.
         *
         * @param step the step
         * @return the eased value
         * @see Utils#modUnchecked(float, float)
         */
        @Override
        protected float applyPartial(final float step) {

            if (this.diff == 0.0f) {
                return this.o;
            }
            if (this.oGtd) {
                return Utils.modUnchecked(
                    (1.0f - step) * this.o
                        + step * (this.d + this.range), this.range);
            }
            return (1.0f - step) * this.o + step * this.d;
        }
    }

    /**
     * Linear interpolation for a periodic value in the clockwise direction.
     */
    public static class LerpCW extends PeriodicEasing {

        /**
         * Constructs the lerp CW functional object with a default range.
         */
        public LerpCW() {
        }

        /**
         * Constructs the lerp CW functional object with a specified range.
         *
         * @param range the range of the period
         */
        public LerpCW(final float range) {
            super(range);
        }

        /**
         * Applies the lerp CW function.
         *
         * @param step the step
         * @return the eased value
         * @see Utils#modUnchecked(float, float)
         */
        @Override
        protected float applyPartial(final float step) {

            if (this.diff == 0.0f) {
                return this.d;
            }
            if (this.oLtd) {
                return Utils.modUnchecked(
                    (1.0f - step) * (this.o + this.range)
                        + step * this.d, this.range);
            }
            return (1.0f - step) * this.o + step * this.d;
        }
    }

    /**
     * Linear interpolation for a periodic value in the nearest direction.
     */
    public static class LerpNear extends PeriodicEasing {

        /**
         * Constructs the lerp near functional object with a default range.
         */
        public LerpNear() {
        }

        /**
         * Constructs the lerp near functional object with a specified range.
         *
         * @param range the range of the period
         */
        public LerpNear(final float range) {
            super(range);
        }

        /**
         * Applies the lerp near function.
         *
         * @param step the step
         * @return the eased value
         * @see Utils#modUnchecked(float, float)
         */
        @Override
        protected float applyPartial(final float step) {

            if (this.diff == 0.0f) {
                return this.o;
            }
            if (this.oLtd && this.diff > this.halfRange) {
                return Utils.modUnchecked(
                    (1.0f - step) * (this.o + this.range)
                        + step * this.d, this.range);
            }
            if (this.oGtd && this.diff < -this.halfRange) {
                return Utils.modUnchecked(
                    (1.0f - step) * this.o
                        + step * (this.d + this.range), this.range);
            }
            return (1.0f - step) * this.o + step * this.d;
        }
    }

    /**
     * An abstract class to cover the easing periodic of values, such as angles
     * and hues in polar color space. Allows the range to be set to,
     * for example, TAU radians, 360.0 degrees or 1.0 color channel.
     */
    public abstract static class PeriodicEasing implements Utils.EasingFuncPrm<Float> {

        /**
         * The default range of the period, τ .
         */
        public static final float DEFAULT_RANGE = Utils.TAU;

        /**
         * The stop angle, modulated by the range.
         */
        protected float d = 0.0f;

        /**
         * The difference between the stop and start angle.
         */
        protected float diff = 0.0f;

        /**
         * One-half of the range of the period.
         */
        protected float halfRange = 0.5f;

        /**
         * The start angle, modulated by the range.
         */
        protected float o = 0.0f;

        /**
         * Whether the start angle is greater than the stop angle.
         */
        protected boolean oGtd = false;

        /**
         * Whether the start angle is less than the stop angle.
         */
        protected boolean oLtd = false;

        /**
         * The range of the period.
         */
        protected float range = 1.0f;

        /**
         * Constructs the easing functional object with a
         * {@link PeriodicEasing#DEFAULT_RANGE}.
         */
        protected PeriodicEasing() {

            this.setRange(PeriodicEasing.DEFAULT_RANGE);
        }

        /**
         * Constructs the easing functional object with a specified range.
         *
         * @param range the range of the period
         */
        protected PeriodicEasing(final float range) {

            this.setRange(range);
        }

        /**
         * Applies the easing function. The abstract class's implementation
         * check to see if the step is out of bounds, [0.0, 1.0] and if
         * mod(origin, range) is equal to mod(dest, range). The origin
         * is returned when the step is less than 0.0; the destination, when
         * the step is greater than 1.0.
         *
         * @see PeriodicEasing#eval(float, float)
         */
        @Override
        public Float apply(
            final Float orig,
            final Float dest,
            final Float step) {

            this.eval(orig, dest);
            final float tf = step;
            if (tf <= 0.0f) {
                return this.o;
            }
            if (tf >= 1.0f) {
                return this.d;
            }
            return this.applyPartial(tf);
        }

        /**
         * Gets the range of the easing function.
         *
         * @return the range
         */
        public float getRange() {
            return this.range;
        }

        /**
         * Sets the range of the easing function. The range should be a
         * positive non-zero value.
         *
         * @param range the range
         * @see Utils#abs(float)
         */
        public void setRange(final float range) {

            this.range = Math.max(Utils.abs(range), Utils.EPSILON);
            this.halfRange = this.range * 0.5f;
        }

        /**
         * Returns the simple name of this class, allowing the functional
         * interface to be identified without being directly accessible.
         */
        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }

        /**
         * Applies the easing function without checking whether the step is out
         * of bounds, [0.0, 1.0].
         * <br>
         * <br>
         * This function needs to be protected because the public apply above
         * verifies the data upon which applyUnclamped operates.
         *
         * @param step the step
         * @return the interpolated value
         */
        protected abstract float applyPartial(final float step);

        /**
         * A helper function that mods the origin and destination by the range.
         * It then finds the signed distance between the mod origin and
         * destination . Lastly, it evaluates which of the two is greater than
         * the other.
         *
         * @param orig the origin value
         * @param dest the destination value
         * @see Utils#modUnchecked(float, float)
         */
        protected void eval(final float orig, final float dest) {

            this.o = Utils.modUnchecked(orig, this.range);
            this.d = Utils.modUnchecked(dest, this.range);
            this.diff = this.d - this.o;
            this.oLtd = this.o < this.d;
            this.oGtd = this.o > this.d;
        }
    }
}
