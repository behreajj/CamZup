package com.behreajj.camzup.core;

import java.util.Comparator;

/**
 * Stores a color at a given step (or percent) in the range [0.0, 1.0] .
 * Equality and hash are based
 * solely on the step, not on the color it holds.
 */
public class ColorKey implements Comparable<ColorKey> {

    /**
     * The key's color. Abbreviated to 'clr' because 'color' is a data type in
     * Processing IDE.
     */
    public final Lab clr = Lab.clearBlack(new Lab());

    /**
     * The key's step, expected to be in the range [0.0, 1.0] .
     */
    public float step = 0.0f;

    /**
     * The default constructor. Creates a clear black color at 0.0 .
     */
    public ColorKey() {
    }

    /**
     * Creates a key from a source.
     *
     * @param source the source key
     */
    public ColorKey(final ColorKey source) {
        this.step = Utils.clamp01(source.step);
        this.clr.set(source.clr);
    }

    /**
     * Creates a new key with a step and color.
     *
     * @param step  the step
     * @param color the color
     */
    public ColorKey(final float step, final Lab color) {

        this.set(step, color);
    }

    /**
     * Creates a key by step and color channel. The color's alpha is assumed to be
     * 1.0 . This is for
     * package-level use only, so that the step can be set without clamp protection.
     *
     * @param step the step
     * @param l    the light component
     * @param a    the green-magenta component
     * @param b    the blue-yellow component
     */
    ColorKey(final float step, final float l, final float a, final float b) {

        this.set(step, l, a, b);
    }

    /**
     * Creates a key by step and color channel. This is for package-level use only,
     * so that the step
     * can be set without clamp protection.
     *
     * @param step  the step
     * @param l     the light component
     * @param a     the green-magenta component
     * @param b     the blue-yellow component
     * @param alpha the transparency channel
     */
    ColorKey(final float step, final float l, final float a, final float b, final float alpha) {

        this.set(step, l, a, b, alpha);
    }

    /**
     * Returns -1 when this key is less than the comparisand; 1 when it is greater
     * than; 0 when the
     * two are 'equal'. The implementation of this method allows collections of keys
     * to be sorted.
     *
     * @param key the comparisand
     * @return the numeric code
     */
    @Override
    public int compareTo(final ColorKey key) {

        return this.step < key.step ? -1 : this.step > key.step ? 1 : 0;
    }

    /**
     * Tests this key for equivalence with another object.
     *
     * @param obj the object
     * @return the equivalence
     * @see ColorKey#equals(ColorKey)
     */
    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        return this.equals((ColorKey) obj);
    }

    /**
     * Returns a hash code for this key based on its step, not based on its color.
     *
     * @return the hash code
     * @see Float#floatToIntBits(float)
     */
    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.step);
    }

    /**
     * Resets this key to an initial condition.
     *
     * @return this key
     */
    public ColorKey reset() {

        this.step = 0.0f;
        Lab.clearBlack(this.clr);
        return this;
    }

    /**
     * Sets this key from a source.
     *
     * @param source the source key
     * @return this key
     */
    public ColorKey set(final ColorKey source) {

        return this.set(source.step, source.clr);
    }

    /**
     * Sets this key with a step and color.
     *
     * @param step the step
     * @param c    the color
     * @return this key
     * @see Utils#clamp01(float)
     */
    public ColorKey set(final float step, final Lab c) {

        this.step = Utils.clamp01(step);
        this.clr.set(c);

        return this;
    }

    /**
     * Returns a string representation of this key.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return this.toString(Utils.FIXED_PRINT);
    }

    /**
     * Returns a string representation of this key.
     *
     * @param places number of decimal places
     * @return the string
     */
    public String toString(final int places) {

        return this.toString(new StringBuilder(96), places).toString();
    }

    /**
     * Sets this key by step and color channel. The color's alpha is assumed to be
     * 1.0 . This is for
     * package-level use only, so that the step can be set without clamp protection.
     *
     * @param step the step
     * @param l    the light component
     * @param a    the green-magenta component
     * @param b    the blue-yellow component
     * @return this key
     */
    ColorKey set(final float step, final float l, final float a, final float b) {

        this.step = step;
        this.clr.set(l, a, b);

        return this;
    }

    /**
     * Sets this key by step and color channel. This is for package-level use only,
     * so that the step
     * can be set without clamp protection.
     *
     * @param step  the step
     * @param l     the light component
     * @param a     the green-magenta component
     * @param b     the blue-yellow component
     * @param alpha the transparency channel
     * @return this key
     */
    ColorKey set(final float step, final float l, final float a, final float b, final float alpha) {

        this.step = step;
        this.clr.set(l, a, b, alpha);

        return this;
    }

    /**
     * Returns a String of Python code targeted toward the Blender 4.x API. This
     * code is brittle and
     * is used for internal testing purposes.
     *
     * @param pyCd  the string builder
     * @param gamma the gamma adjustment
     * @return the string builder
     * @see Utils#toFixed(StringBuilder, float, int)
     */
    StringBuilder toBlenderCode(final StringBuilder pyCd, final float gamma) {

        pyCd.append("{\"position\": ");
        Utils.toFixed(pyCd, Utils.clamp01(this.step), 3);
        pyCd.append(", \"color\": ");
        final Rgb srgb = Rgb.srLab2TosRgb(this.clr, new Rgb(), new Rgb(), new Vec4());
        srgb.toBlenderCode(pyCd, gamma, true);
        pyCd.append('}');
        return pyCd;
    }

    /**
     * Internal helper function to assist with methods that need to print many color
     * keys. Appends to
     * an existing {@link StringBuilder}.
     *
     * @param sb     the string builder
     * @param places the number of places
     * @return the string builder
     * @see Utils#toFixed(StringBuilder, float, int)
     */
    StringBuilder toString(final StringBuilder sb, final int places) {

        sb.append("{\"step\":");
        Utils.toFixed(sb, this.step, places);
        sb.append(",\"clr\":");
        this.clr.toString(sb, places);
        sb.append('}');
        return sb;
    }

    /**
     * Returns a String representation of this color stop for an SVG gradient.
     * Includes the offset,
     * color and opacity.
     *
     * @param svgp the string builder
     * @return the string builder
     */
    StringBuilder toSvgString(final StringBuilder svgp) {

        svgp.append("<stop offset=\"");
        Utils.toFixed(svgp, this.step, ISvgWritable.FIXED_PRINT);
        svgp.append("\" stop-opacity=\"");
        Utils.toFixed(svgp, Utils.clamp01(this.clr.alpha), ISvgWritable.FIXED_PRINT);
        svgp.append("\" stop-color=\"");
        final Rgb srgb = Rgb.srLab2TosRgb(this.clr, new Rgb(), new Rgb(), new Vec4());
        Rgb.toHexWeb(svgp, srgb);
        svgp.append("\" />");
        return svgp;
    }

    /**
     * Tests this key for equality to another based on its step, not color.
     *
     * @param key the key
     * @return the evaluation
     */
    protected boolean equals(final ColorKey key) {

        return this.hashCode() == key.hashCode();
    }

    /**
     * The default comparator used to compare color keys.
     */
    public static class SortQuantized implements Comparator<ColorKey> {

        /**
         * Default quantization factor for color key steps,
         */
        public static final int DEFAULT_LEVEL = 16;

        /**
         * Quantization level.
         */
        public final int level;

        /**
         * The default constructor.
         */
        SortQuantized() {
            this(SortQuantized.DEFAULT_LEVEL);
        }

        /**
         * Creates a quantized sorter with the specified number of levels.
         *
         * @param level quantization levels
         */
        SortQuantized(final int level) {

            this.level = Math.max(level, 2);
        }

        /**
         * Compares the quantized steps of the comparisand keys.
         *
         * @param a the left comparisand
         * @param b the right comparisand
         * @return the evaluation
         * @see Utils#quantizeUnsigned(float, int)
         */
        @Override
        public int compare(final ColorKey a, final ColorKey b) {

            final float aq = Utils.quantizeUnsigned(a.step, this.level);
            final float bq = Utils.quantizeUnsigned(b.step, this.level);
            return aq < bq ? -1 : aq > bq ? 1 : 0;
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
}
