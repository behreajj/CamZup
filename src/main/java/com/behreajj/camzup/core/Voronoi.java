package com.behreajj.camzup.core;

/**
 * Generates a Voronoi noise in two to four dimensions.
 */
public class Voronoi extends Generative {

    /**
     * Discourage overriding with a private constructor.
     */
    private Voronoi() {
    }

    /**
     * Generates 2D Voronoi noise. Returns the minimum Euclidean distance; the
     * Voronoi point is stored
     * in an output vector.
     *
     * @param coord  the coordinate
     * @param seed   the seed
     * @param scale  the scale
     * @param target the output vector
     * @return the distance
     */
    public static float eval(final Vec2 coord, final int seed, final float scale, final Vec2 target) {

        /*
         * As many functions as is reasonable are inlined for performance purposes
         * (both to avoid function call overhead and to avoid creating vectors).
         */

        if (scale == 0.0f) {
            target.reset();
            return 0.0f;
        }

        final float scaleInv = 1.0f / scale;
        final float xScaled = coord.x * scaleInv;
        final float yScaled = coord.y * scaleInv;

        final float xCell = xScaled > 0.0f ? (int) xScaled : xScaled < 0.0f ? (int) xScaled - 1.0f : 0.0f;
        final float yCell = yScaled > 0.0f ? (int) yScaled : yScaled < 0.0f ? (int) yScaled - 1.0f : 0.0f;

        final float xLocal = xScaled - xCell;
        final float yLocal = yScaled - yCell;

        float minDistSq = Float.MAX_VALUE;

        /* For 2D, 3 x 3 = 9. */
        for (int k = 0; k < 9; ++k) {

            /* Convert from linear index to [0, 2]. */
            final int i = k / 3;
            final int j = k % 3;

            /* Convert from [0, 2] to [-1.0, 1.0]. */
            final float in1 = i - 1.0f;
            final float jn1 = j - 1.0f;

            /* Calculate an offset step for the vector. */
            final float xSum = xCell + jn1;
            final float ySum = yCell + in1;

            final float st = Utils.sqrtUnchecked(xSum * xSum + ySum * ySum) * Generative.STEP_2;

            /* Create a random vector [0.0, 1.0] . Add cell offset. */
            final float xHsh = jn1
                + Float.intBitsToFloat(
                Generative.hash(
                    (Utils.MUL_BASE ^ Float.floatToIntBits(xSum + st)) * Utils.HASH_MUL
                        ^ Float.floatToIntBits(ySum),
                    seed,
                    0)
                    & 0x007fffff
                    | 0x3f800000)
                - 1.0f;

            final float yHsh = in1
                + Float.intBitsToFloat(
                Generative.hash(
                    (Utils.MUL_BASE ^ Float.floatToIntBits(xSum)) * Utils.HASH_MUL
                        ^ Float.floatToIntBits(ySum + st),
                    seed,
                    0)
                    & 0x007fffff
                    | 0x3f800000)
                - 1.0f;

            /*
             * Find the Euclidean distance between the local coordinate and the
             * random point.
             */
            final float xDist = xLocal - xHsh;
            final float yDist = yLocal - yHsh;
            final float distSq = xDist * xDist + yDist * yDist;

            /* Reassign minimums. */
            if (distSq < minDistSq) {
                minDistSq = distSq;
                target.x = xHsh;
                target.y = yHsh;
            }
        }

        target.x += xCell;
        target.y += yCell;

        target.x *= scale;
        target.y *= scale;

        return Utils.sqrtUnchecked(minDistSq);
    }

    /**
     * Generates 3D voronoi noise. Returns the minimum Euclidean distance; the
     * voronoi point is stored
     * in an output vector.
     *
     * @param coord  the coordinate
     * @param seed   the seed
     * @param scale  the scale
     * @param target the output vector
     * @return the distance
     */
    public static float eval(final Vec3 coord, final int seed, final float scale, final Vec3 target) {

        if (scale == 0.0f) {
            target.reset();
            return 0.0f;
        }

        final float scaleInv = 1.0f / scale;
        final float xScaled = coord.x * scaleInv;
        final float yScaled = coord.y * scaleInv;
        final float zScaled = coord.z * scaleInv;

        final float xCell = xScaled > 0.0f ? (int) xScaled : xScaled < 0.0f ? (int) xScaled - 1.0f : 0.0f;
        final float yCell = yScaled > 0.0f ? (int) yScaled : yScaled < 0.0f ? (int) yScaled - 1.0f : 0.0f;
        final float zCell = zScaled > 0.0f ? (int) zScaled : zScaled < 0.0f ? (int) zScaled - 1.0f : 0.0f;

        final float xLocal = xScaled - xCell;
        final float yLocal = yScaled - yCell;
        final float zLocal = zScaled - zCell;

        float minDistSq = Float.MAX_VALUE;

        /* For 3D, 3 x 3 x 3 = 27. */
        for (int k = 0; k < 27; ++k) {
            final int h = k / 9;
            final int m = k - h * 9;
            final int i = m / 3;
            final int j = m % 3;

            final float hn1 = h - 1.0f;
            final float in1 = i - 1.0f;
            final float jn1 = j - 1.0f;

            final float zSum = zCell + hn1;
            final float ySum = yCell + in1;
            final float xSum = xCell + jn1;

            final float st = Generative.STEP_3 * Utils.sqrtUnchecked(xSum * xSum + ySum * ySum + zSum * zSum);

            final int zBit = Float.floatToIntBits(zSum);
            final int yBit = Float.floatToIntBits(ySum);
            final int xBitBase = (Utils.MUL_BASE ^ Float.floatToIntBits(xSum)) * Utils.HASH_MUL;

            final float xHsh = jn1
                + Float.intBitsToFloat(
                Generative.hash(
                    ((Utils.MUL_BASE ^ Float.floatToIntBits(xSum + st)) * Utils.HASH_MUL
                        ^ yBit)
                        * Utils.HASH_MUL
                        ^ zBit,
                    seed,
                    0)
                    & 0x007fffff
                    | 0x3f800000)
                - 1.0f;
            final float yHsh = in1
                + Float.intBitsToFloat(
                Generative.hash(
                    (xBitBase ^ Float.floatToIntBits(ySum + st)) * Utils.HASH_MUL ^ zBit,
                    seed,
                    0)
                    & 0x007fffff
                    | 0x3f800000)
                - 1.0f;
            final float zHsh = hn1
                + Float.intBitsToFloat(
                Generative.hash(
                    (xBitBase ^ yBit) * Utils.HASH_MUL ^ Float.floatToIntBits(zSum + st),
                    seed,
                    0)
                    & 0x007fffff
                    | 0x3f800000)
                - 1.0f;

            final float xDist = xLocal - xHsh;
            final float yDist = yLocal - yHsh;
            final float zDist = zLocal - zHsh;
            final float distSq = xDist * xDist + yDist * yDist + zDist * zDist;

            if (distSq < minDistSq) {
                minDistSq = distSq;
                target.x = xHsh;
                target.y = yHsh;
                target.z = zHsh;
            }
        }

        target.x += xCell;
        target.y += yCell;
        target.z += zCell;

        target.x *= scale;
        target.y *= scale;
        target.z *= scale;

        return Utils.sqrtUnchecked(minDistSq);
    }

    /**
     * Generates 4D voronoi noise. Returns the minimum Euclidean distance; the
     * voronoi point is stored
     * in an output vector.
     *
     * @param coord  the coordinate
     * @param seed   the seed
     * @param scale  the scale
     * @param target the output vector
     * @return the distance
     */
    public static float eval(final Vec4 coord, final int seed, final float scale, final Vec4 target) {

        if (scale == 0.0f) {
            target.reset();
            return 0.0f;
        }

        final float scaleInv = 1.0f / scale;
        final float xScaled = coord.x * scaleInv;
        final float yScaled = coord.y * scaleInv;
        final float zScaled = coord.z * scaleInv;
        final float wScaled = coord.w * scaleInv;

        final float xCell = xScaled > 0.0f ? (int) xScaled : xScaled < 0.0f ? (int) xScaled - 1.0f : 0.0f;
        final float yCell = yScaled > 0.0f ? (int) yScaled : yScaled < 0.0f ? (int) yScaled - 1.0f : 0.0f;
        final float zCell = zScaled > 0.0f ? (int) zScaled : zScaled < 0.0f ? (int) zScaled - 1.0f : 0.0f;
        final float wCell = wScaled > 0.0f ? (int) wScaled : wScaled < 0.0f ? (int) wScaled - 1.0f : 0.0f;

        final float xLocal = xScaled - xCell;
        final float yLocal = yScaled - yCell;
        final float zLocal = zScaled - zCell;
        final float wLocal = wScaled - wCell;

        float minDist = Float.MAX_VALUE;

        /* For 4D, 3 x 3 x 3 x 3 = 81. */
        for (int k = 0; k < 81; ++k) {
            final int g = k / 27;
            final int m = k - g * 27;
            final int h = m / 9;
            final int n = m - h * 9;
            final int i = n / 3;
            final int j = n % 3;

            final float gn1 = g - 1.0f;
            final float hn1 = h - 1.0f;
            final float in1 = i - 1.0f;
            final float jn1 = j - 1.0f;

            final float wSum = wCell + gn1;
            final float zSum = zCell + hn1;
            final float ySum = yCell + in1;
            final float xSum = xCell + jn1;

            final float st = Generative.STEP_4
                * Utils.sqrtUnchecked(xSum * xSum + ySum * ySum + zSum * zSum + wSum * wSum);

            final int wBit = Float.floatToIntBits(wSum);
            final int zBit = Float.floatToIntBits(zSum);
            final int yBit = Float.floatToIntBits(ySum);

            final int xBitBase = Utils.MUL_BASE ^ Float.floatToIntBits(xSum);
            final int yBitBase = (xBitBase * Utils.HASH_MUL ^ yBit) * Utils.HASH_MUL;

            final float xHsh = jn1
                + Float.intBitsToFloat(
                Generative.hash(
                    (((Utils.MUL_BASE ^ Float.floatToIntBits(xSum + st)) * Utils.HASH_MUL
                        ^ yBit)
                        * Utils.HASH_MUL
                        ^ zBit)
                        * Utils.HASH_MUL
                        ^ wBit,
                    seed,
                    0)
                    & 0x007fffff
                    | 0x3f800000)
                - 1.0f;
            final float yHsh = in1
                + Float.intBitsToFloat(
                Generative.hash(
                    ((xBitBase * Utils.HASH_MUL ^ Float.floatToIntBits(ySum + st))
                        * Utils.HASH_MUL
                        ^ zBit)
                        * Utils.HASH_MUL
                        ^ wBit,
                    seed,
                    0)
                    & 0x007fffff
                    | 0x3f800000)
                - 1.0f;
            final float zHsh = hn1
                + Float.intBitsToFloat(
                Generative.hash(
                    (yBitBase ^ Float.floatToIntBits(zSum + st)) * Utils.HASH_MUL ^ wBit,
                    seed,
                    0)
                    & 0x007fffff
                    | 0x3f800000)
                - 1.0f;
            final float wHsh = gn1
                + Float.intBitsToFloat(
                Generative.hash(
                    (yBitBase ^ zBit) * Utils.HASH_MUL ^ Float.floatToIntBits(wSum + st),
                    seed,
                    0)
                    & 0x007fffff
                    | 0x3f800000)
                - 1.0f;

            /*
             * Find the Euclidean distance between the local coordinate and the
             * random point.
             */
            final float xDist = xLocal - xHsh;
            final float yDist = yLocal - yHsh;
            final float zDist = zLocal - zHsh;
            final float wDist = wLocal - wHsh;
            final float dist = xDist * xDist + yDist * yDist + zDist * zDist + wDist * wDist;

            /* Reassign minimums. */
            if (dist < minDist) {
                minDist = dist;
                target.x = xHsh;
                target.y = yHsh;
                target.z = zHsh;
                target.w = wHsh;
            }
        }

        target.x += xCell;
        target.y += yCell;
        target.z += zCell;
        target.w += wCell;

        target.x *= scale;
        target.y *= scale;
        target.z *= scale;
        target.w *= scale;

        return Utils.sqrtUnchecked(minDist);
    }
}
