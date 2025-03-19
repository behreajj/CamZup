package com.behreajj.camzup.friendly;

import com.behreajj.camzup.core.Utils;
import com.behreajj.camzup.core.Vec2;
import processing.core.PApplet;

/**
 * Maintains consistent behavior between renderers which display text to the
 * sketch.
 */
public interface ITextDisplay {

    /**
     * Displays a boolean as text at a location.
     *
     * @param bool the boolean
     * @param x    the x coordinate
     * @param y    the y coordinate
     */
    default void text(final boolean bool, final float x, final float y) {

        this.text(bool ? "true" : "false", x, y);
    }

    /**
     * Displays a boolean as text at a location.
     *
     * @param bool the boolean
     * @param v    the location
     */
    default void text(final boolean bool, final Vec2 v) {

        this.text(bool ? "true" : "false", v.x, v.y);
    }

    /**
     * Displays a character at a location.
     *
     * @param c the character
     * @param x the x coordinate
     * @param y the y coordinate
     */
    void text(final char c, final float x, final float y);

    /**
     * Displays a character at a location.
     *
     * @param c the character
     * @param v the location
     */
    default void text(final char c, final Vec2 v) {

        this.text(c, v.x, v.y);
    }

    /**
     * Displays an array of characters as text at a location.
     *
     * @param chars the character array
     * @param x     the x coordinate
     * @param y     the y coordinate
     */
    default void text(final char[] chars, final float x, final float y) {

        this.text(chars, 0, chars.length, x, y);
    }

    /**
     * Displays an array of characters as text at a location.
     *
     * @param chars the character array
     * @param start the start index, inclusive
     * @param stop  the stop index, exclusive
     * @param x     the x coordinate
     * @param y     the y coordinate
     */
    void text(final char[] chars, final int start, final int stop, final float x, final float y);

    /**
     * Displays an array of characters as text at a location.
     *
     * @param chars the character array
     * @param start the start index, inclusive
     * @param stop  the stop index, exclusive
     * @param v     the location
     */
    default void text(final char[] chars, final int start, final int stop, final Vec2 v) {

        this.text(chars, 0, chars.length, v.x, v.y);
    }

    /**
     * Displays an array of characters as text at a location.
     *
     * @param chars the character array
     * @param v     the location
     */
    default void text(final char[] chars, final Vec2 v) {

        this.text(chars, v.x, v.y);
    }

    /**
     * Displays a real number as text at a location. Registers up to four decimal
     * places.
     *
     * @param real the real number
     * @param x    the x coordinate
     * @param y    the y coordinate
     * @see Utils#toFixed(float, int)
     */
    default void text(final float real, final float x, final float y) {

        this.text(Utils.toFixed(real, Utils.FIXED_PRINT), x, y);
    }

    /**
     * Displays a number as text at a location. Registers up to four decimal places.
     *
     * @param real the real number
     * @param v    the location
     * @see Utils#toFixed(float, int)
     */
    default void text(final float real, final Vec2 v) {

        this.text(Utils.toFixed(real, Utils.FIXED_PRINT), v.x, v.y);
    }

    /**
     * Displays a real number at a 2D location. Fixes the number display to four
     * decimal places.
     *
     * @param integer the integer
     * @param x       the x coordinate
     * @param y       the y coordinate
     * @see Utils#toPadded(int, int)
     */
    default void text(final int integer, final float x, final float y) {

        this.text(Utils.toPadded(integer, 1), x, y);
    }

    /**
     * Displays an integer as text at a location.
     *
     * @param integer the integer
     * @param v       the location
     * @see Utils#toPadded(int, int)
     */
    default void text(final int integer, final Vec2 v) {

        this.text(Utils.toPadded(integer, 1), v.x, v.y);
    }

    /**
     * Displays an object as text at a location. Calls the object's toString
     * function.
     *
     * @param obj the object
     * @param x   the x coordinate
     * @param y   the y coordinate
     */
    default void text(final Object obj, final float x, final float y) {

        final String str = obj.toString();
        if (str.length() > 96) {
            this.text(str.substring(0, 95), x, y);
        } else {
            this.text(str, x, y);
        }
    }

    /**
     * Displays a string at a location.
     *
     * @param str the string
     * @param x   the x coordinate
     * @param y   the y coordinate
     */
    default void text(final String str, final float x, final float y) {

        this.text(str.toCharArray(), x, y);
    }

    /**
     * Displays a string at a location. This version of text is not supported, so
     * only x1 and y1 are
     * used.
     *
     * @param str the string
     * @param x1  the x coordinate
     * @param y1  the y coordinate
     * @param x2  the second x coordinate
     * @param y2  the second y coordinate
     */
    default void text(
        final String str, final float x1, final float y1, final float x2, final float y2) {

        PApplet.showMissingWarning("text");
        this.text(str, x1, y1);
    }

    /**
     * Displays a string at a location.
     *
     * @param str the string
     * @param v   the location
     */
    default void text(final String str, final Vec2 v) {

        this.text(str, v.x, v.y);
    }
}
