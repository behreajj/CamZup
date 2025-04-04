package com.behreajj.camzup.core;

import java.io.Serial;
import java.util.Arrays;
import java.util.Random;

/**
 * A convenience for working in the Processing IDE: extends
 * {@link java.util.Random} so an additional import does not need to be added.
 * Its name is truncated to avoid collisions.
 */
public class Rng extends Random {

    /**
     * The unique identification for serialized classes.
     */
    @Serial
    private static final long serialVersionUID = 259933270658058430L;

    /**
     * The default constructor. Sets the seed to the system's current time in
     * milliseconds.
     *
     * @see System#currentTimeMillis()
     */
    public Rng() {
        this(System.currentTimeMillis());
    }

    /**
     * Creates a new generator with a seed value.
     *
     * @param seed the seed
     */
    public Rng(final long seed) {

        super(seed);
        this.nextFloat();
    }

    /**
     * Returns a pseudo-random, uniformly distributed integer value between 0
     * (inclusive) and the specified value (exclusive), drawn from this random
     * number generator's sequence. Overrides parent's functionality to clamp
     * the bound to a positive non-zero lower bound rather than throwing an
     * exception.
     *
     * @param bound the upper bound
     * @return the pseudo-random integer
     * @see java.util.Random#nextInt(int)
     */
    @Override
    public int nextInt(final int bound) {

        final int vb = Math.max(bound, 1);

        int r = this.next(31);
        final int m = vb - 1;
        if ((vb & m) == 0) {
            r = (int) (vb * (long) r >> 31);
        } else {
            //noinspection StatementWithEmptyBody
            for (int u = r; u - (r = u % vb) + m < 0; u = this.next(31)) {
            }
        }
        return r;
    }

    /**
     * Finds an array of random numbers that sum to 1.0 . Due to floating point
     * precision, the sum of the numbers may be approximate.
     *
     * @param count the number of elements
     * @return the array
     */
    public float[] segment(final int count) {

        return this.segment(count, 1.0f);
    }

    /**
     * Finds an array of random numbers that sum to a value. Due to floating
     * point precision, the sum of the numbers may be approximate.
     * <br>
     * <br>
     * The function first calculates a series of segment end points on a number
     * line from 0.0 to 1.0. The result array contains the difference between
     * an end point and its preceding neighbor multiplied by the sum.
     *
     * @param count the number of elements
     * @param sum   the sum
     * @return the array
     */
    public float[] segment(final int count, final float sum) {

        /*
         * Doesn't have uniform distribution. final float[] result = new
         * float[count]; float trueSum = 0.0f; for ( int i = 0; i < count; ++i ) {
         * trueSum += result[i] = this.nextFloat(); } final float scalar =
         * Utils.div(sum, trueSum); for ( int i = 0; i < count; ++i ) { result[i]
         * *= scalar; }
         */

        final float[] x = new float[count + 2];
        x[0] = 0.0f;
        x[count] = 1.0f;
        for (int i = 1; i < count; ++i) {
            x[i] = this.nextFloat();
        }

        Arrays.sort(x);

        final float[] result = new float[count];
        for (int i = count - 1; i > -1; --i) {
            result[i] = sum * (x[i + 2] - x[i + 1]);
        }
        return result;
    }

    /**
     * Sets the seed for this random number generator and calls next float. For
     * sequences of similar seeds (e.g., 100, 101, 102), the initial random
     * value fetched will be similar. See
     * "<a href="https://stackoverflow.com/a/27761175">First random number
     * after setSeed in Java always similar</a>".
     *
     * @param seed the seed
     */
    @Override
    public synchronized void setSeed(final long seed) {

        super.setSeed(seed);
        this.nextFloat();
    }

    /**
     * Returns a double precision real number between 0.0 and the upper bound.
     *
     * @param upper the upper bound
     * @return the random number
     */
    public double uniform(final double upper) {

        return upper * this.nextDouble();
    }

    /**
     * Returns a double precision real number between the lower and upper bound.
     *
     * @param lower the lower bound
     * @param upper the upper bound
     * @return the random number
     */
    public double uniform(final double lower, final double upper) {

        final double r = this.nextDouble();
        return (1.0d - r) * lower + r * upper;
    }

    /**
     * Returns a single precision real number between 0.0 and the upper bound.
     *
     * @param upper the upper bound
     * @return the random number
     */
    public float uniform(final float upper) {

        return upper * this.nextFloat();
    }

    /**
     * Returns a single precision real number within the lower and upper bound.
     *
     * @param lower the lower bound
     * @param upper the upper bound
     * @return the random number
     */
    public float uniform(final float lower, final float upper) {

        final float r = this.nextFloat();
        return (1.0f - r) * lower + r * upper;
    }

    /**
     * Returns an integer within the lower and upper bound: lower bound
     * inclusive, upper bound exclusive.
     *
     * @param lower the lower bound
     * @param upper the upper bound
     * @return the random number
     */
    public int uniform(final int lower, final int upper) {

        final float r = this.nextFloat();
        return Utils.floor((1.0f - r) * lower + r * upper);
    }
}
