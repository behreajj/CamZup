package com.behreajj.camzup.core;

import java.util.Comparator;

/**
 * Compares two face indices (an array of vertex indices) by averaging the
 * vectors referenced by them, then comparing the averages.
 */
final class SortLoops3 implements Comparator<int[][]> {

    /**
     * Internal vector used to store the average coordinate for the left
     * comparisand.
     */
    private final Vec3 aAvg = new Vec3();

    /**
     * Internal vector used to store the average coordinate for the right
     * comparisand.
     */
    private final Vec3 bAvg = new Vec3();

    /**
     * The coordinates array.
     */
    private final Vec3[] coords;

    /**
     * Constructs a comparator with reference to the mesh's coordinates.
     *
     * @param coords the coordinate array.
     */
    SortLoops3(final Vec3[] coords) {
        this.coords = coords;
    }

    /**
     * Compares two face loops' indices.
     *
     * @param a the left comparisand
     * @param b the right comparisandS
     */
    @Override
    public int compare(final int[][] a, final int[][] b) {

        this.aAvg.reset();
        for (final int[] value : a) {
            Vec3.add(this.aAvg, this.coords[value[0]], this.aAvg);
        }
        Vec3.div(this.aAvg, a.length, this.aAvg);

        this.bAvg.reset();
        for (final int[] ints : b) {
            Vec3.add(this.bAvg, this.coords[ints[0]], this.bAvg);
        }
        Vec3.div(this.bAvg, b.length, this.bAvg);

        return this.aAvg.compareTo(this.bAvg);
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
