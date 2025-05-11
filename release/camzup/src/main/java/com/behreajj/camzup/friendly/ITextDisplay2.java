package com.behreajj.camzup.friendly;

import com.behreajj.camzup.core.Utils;

/**
 * Maintains consistent behavior between 2D renderers which display text to the
 * sketch.
 */
public interface ITextDisplay2 extends ITextDisplay {

    /**
     * Displays a character as text at a 2D location. Ignores the z coordinate.
     *
     * @param c the character
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    default void text(
        final char c,
        final float x,
        final float y,
        final float z) {

        this.text(c, x, y);
    }

    /**
     * Displays an array of characters at a 2D location. Ignores the z
     * coordinate.
     *
     * @param chars the character array
     * @param start the start index, inclusive
     * @param stop  the stop index, exclusive
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param z     the z coordinate
     */
    default void text(
        final char[] chars,
        final int start,
        final int stop,
        final float x,
        final float y,
        final float z) {

        this.text(chars, start, stop, x, y);
    }

    /**
     * Displays a real number as text at a 2D location. Ignores the z
     * coordinate.
     *
     * @param r the real number
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @see Utils#toFixed(float, int)
     */
    default void text(
        final float r,
        final float x,
        final float y,
        final float z) {

        this.text(Utils.toFixed(r, Utils.FIXED_PRINT), x, y);
    }

    /**
     * Displays an integer as text at a 2D location. Ignores the z coordinate.
     *
     * @param i the integer
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    default void text(
        final int i,
        final float x,
        final float y,
        final float z) {

        this.text(i, x, y);
    }

    /**
     * Displays a string of text at a 2D location. Ignores the z coordinate.
     *
     * @param s the string
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    default void text(
        final String s,
        final float x,
        final float y,
        final float z) {

        this.text(s, x, y);
    }
}
