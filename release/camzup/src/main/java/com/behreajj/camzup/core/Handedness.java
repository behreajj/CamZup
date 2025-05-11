package com.behreajj.camzup.core;

/**
 * Tracks the chirality, or handedness, of a 3D renderer. Describes the
 * orientation of the renderer's coordinate axes wherein a human hand's thumb
 * points out (the x-axis), index finger points forward (the y-axis) and middle
 * finger points up (the z-axis).
 * <br>
 * <br>
 * Associates left with -1; right, with 1.
 * <br>
 * <br>
 * In right-handed renderers, a positive angle rotates counter-clockwise; in
 * left-handed renderers, clockwise.
 */
public enum Handedness {

    /**
     * Left-handed
     */
    LEFT(-1),

    /**
     * Right-handed
     */
    RIGHT(1);

    /**
     * The sign of the handedness: -1 for left; 1 for right.
     */
    private final int sign;

    /**
     * The default constructor
     *
     * @param sign the sign
     */
    Handedness(final int sign) {
        this.sign = sign;
    }

    /**
     * Converts a single precision sign to a handedness enumeration.
     *
     * @param v the sign
     * @return the handedness
     */
    public static Handedness fromSign(final float v) {

        return v < -0.0f ? Handedness.LEFT : Handedness.RIGHT;
    }

    /**
     * Converts an integer sign to a handedness enumeration.
     *
     * @param v the sign
     * @return the handedness
     */
    public static Handedness fromSign(final int v) {

        return v < 0 ? Handedness.LEFT : Handedness.RIGHT;
    }

    /**
     * Returns the opposite of the input handedness.
     *
     * @param h the input handedness
     * @return the reverse
     */
    public static Handedness reverse(final Handedness h) {

        return h == Handedness.RIGHT ? Handedness.LEFT : Handedness.RIGHT;
    }

    /**
     * Gets the sign of the handedness: -1 for left; 1 for right.
     *
     * @return the sign
     */
    public int getSign() {
        return this.sign;
    }
}
