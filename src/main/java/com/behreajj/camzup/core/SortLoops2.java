package com.behreajj.camzup.core;

import java.util.Comparator;

/**
 * Compares two face indices (an array of vertex indices) by averaging the
 * vectors referenced by
 * them, then comparing the averages.
 */
final class SortLoops2 implements Comparator<int[][]> {

    /**
     * Internal vector used to store the average coordinate for the left
     * comparisand.
     */
    private final Vec2 aAvg = new Vec2();

    /**
     * Internal vector used to store the average coordinate for the right
     * comparisand.
     */
    private final Vec2 bAvg = new Vec2();

    /**
     * The coordinates array.
     */
    private final Vec2[] coords;

    /**
     * Constructs a comparator with reference to the mesh's coordinates.
     *
     * @param coords the coordinate array.
     */
    SortLoops2(final Vec2[] coords) {

        this.coords = coords;
    }

    /**
     * Compares two face loops' indices.
     *
     * @param a the left comparisand
     * @param b the right comparisand
     */
    @Override
    public int compare(final int[][] a, final int[][] b) {

        this.aAvg.reset();
        for (int[] ints : a) {
            Vec2.add(this.aAvg, this.coords[ints[0]], this.aAvg);
        }
        Vec2.div(this.aAvg, a.length, this.aAvg);

        this.bAvg.reset();
        for (int[] ints : b) {
            Vec2.add(this.bAvg, this.coords[ints[0]], this.bAvg);
        }
        Vec2.div(this.bAvg, b.length, this.bAvg);

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
