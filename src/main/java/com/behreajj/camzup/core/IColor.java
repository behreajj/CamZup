package com.behreajj.camzup.core;

/**
 * Maintains consistent behavior between color objects.
 */
public interface IColor extends Comparable<IColor> {

    /**
     * Returns -1 when this color is less than the comparisand; 1 when it is
     * greater than; 0 when the two are 'equal'. The implementation of this
     * method allows collections of colors to be sorted.
     *
     * @param d the comparisand
     * @return the numeric code
     */
    @Override
    default int compareTo(final IColor d) {

        final int left = this.toHexInt();
        final int right = d.toHexInt();
        return left < right ? -1 : left > right ? 1 : 0;
    }

    /**
     * Tests this color for equivalence with another object.
     *
     * @param obj the object
     * @return the equivalence
     */
    @Override
    boolean equals(final Object obj);

    /**
     * Returns a hash code for this color.
     *
     * @return the hash code
     */
    @Override
    int hashCode();

    /**
     * Converts a color to an integer where hexadecimal represents the color
     * components.
     *
     * @return the color in hexadecimal
     */
    int toHexInt();

    /**
     * Converts a color to an integer where hexadecimal represents the color
     * channels. Uses saturation arithmetic.
     *
     * @return the color in hexadecimal
     */
    int toHexIntSat();

    /**
     * Converts a color to an integer where hexadecimal represents the color
     * channels. Uses modular arithmetic.
     *
     * @return the color in hexadecimal
     */
    int toHexIntWrap();

    /**
     * Returns a string representation of this color.
     *
     * @return the string
     */
    @Override
    String toString();

    /**
     * Eases between hues in the clockwise direction.
     */
    final class HueCCW extends HueEasing {

        /**
         * The default constructor.
         */
        public HueCCW() {
        }

        /**
         * Applies the function.
         *
         * @param step the step in a range 0 to 1
         * @return the eased hue
         * @see Utils#mod1(float)
         */
        @Override
        protected float applyPartial(final float step) {

            if (this.diff == 0.0f) {
                return this.o;
            }
            if (this.oGtd) {
                return Utils.mod1((1.0f - step) * this.o + step * (this.d + 1.0f));
            }
            return (1.0f - step) * this.o + step * this.d;
        }
    }

    /**
     * Eases the hue in the counter-clockwise direction.
     */
    final class HueCW extends HueEasing {

        /**
         * The default constructor.
         */
        public HueCW() {
        }

        /**
         * Applies the function.
         *
         * @param step the step in a range 0 to 1
         * @return the eased hue
         * @see Utils#mod1(float)
         */
        @Override
        protected float applyPartial(final float step) {

            if (this.diff == 0.0f) {
                return this.d;
            }
            if (this.oLtd) {
                return Utils.mod1((1.0f - step) * (this.o + 1.0f) + step * this.d);
            }
            return (1.0f - step) * this.o + step * this.d;
        }
    }

    /**
     * An abstract parent class for hue easing functions.
     */
    abstract class HueEasing implements Utils.EasingFuncPrm<Float> {

        /**
         * The modulated destination hue.
         */
        protected float d = 0.0f;

        /**
         * The difference between the stop and start hue.
         */
        protected float diff = 0.0f;

        /**
         * The modulated origin hue.
         */
        protected float o = 0.0f;

        /**
         * Whether the origin is greater than the destination.
         */
        protected boolean oGtd = false;

        /**
         * Whether the origin is less than the destination.
         */
        protected boolean oLtd = false;

        /**
         * The default constructor.
         */
        protected HueEasing() {
        }

        /**
         * The clamped easing function.
         *
         * @param orig the origin hue
         * @param dest the destination hue
         * @param step the step in range 0 to 1
         * @return the eased hue
         */
        @Override
        public Float apply(final Float orig, final Float dest, final Float step) {

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
         * Returns the simple name of this class.
         *
         * @return the string
         */
        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }

        /**
         * The application function to be defined by subclasses of this class.
         *
         * @param step the step
         * @return the eased hue
         */
        protected abstract float applyPartial(final float step);

        /**
         * A helper function to pass on to subclasses of this class.
         *
         * @param orig the origin hue
         * @param dest the destination hue
         * @see Utils#mod1(float)
         */
        protected void eval(final float orig, final float dest) {

            this.o = Utils.mod1(orig);
            this.d = Utils.mod1(dest);
            this.diff = this.d - this.o;
            this.oLtd = this.o < this.d;
            this.oGtd = this.o > this.d;
        }
    }

    /**
     * Eases between hues by the farthest rotational direction.
     */
    final class HueFar extends HueEasing {

        /**
         * The default constructor.
         */
        public HueFar() {
        }

        /**
         * Applies the function.
         *
         * @param step the step in a range 0 to 1
         * @return the eased hue
         * @see Utils#mod1(float)
         */
        @Override
        protected float applyPartial(final float step) {

            if (this.diff == 0.0f || this.oLtd && this.diff < 0.5f) {
                return Utils.mod1((1.0f - step) * (this.o + 1.0f) + step * this.d);
            }
            if (this.oGtd && this.diff > -0.5f) {
                return Utils.mod1((1.0f - step) * this.o + step * (this.d + 1.0f));
            }
            return (1.0f - step) * this.o + step * this.d;
        }
    }

    /**
     * Eases between hues by the nearest rotational direction.
     */
    final class HueNear extends HueEasing {

        /**
         * The default constructor.
         */
        public HueNear() {
        }

        /**
         * Applies the function.
         *
         * @param step the step in a range 0 to 1
         * @return the eased hue
         * @see Utils#mod1(float)
         */
        @Override
        protected float applyPartial(final float step) {

            if (this.diff == 0.0f) {
                return this.o;
            }
            if (this.oLtd && this.diff > 0.5f) {
                return Utils.mod1((1.0f - step) * (this.o + 1.0f) + step * this.d);
            }
            if (this.oGtd && this.diff < -0.5f) {
                return Utils.mod1((1.0f - step) * this.o + step * (this.d + 1.0f));
            }
            return (1.0f - step) * this.o + step * this.d;
        }
    }
}
