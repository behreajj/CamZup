package com.behreajj.camzup.friendly;

/**
 * Duplicates and clarifies the normal mode options available to
 * OpenGL renderers (P2D and P3D). The integer value of each
 * is intended to match with integer constants.The enum's ordinal should not
 * be used.
 */
public enum NormalMode {

    /**
     * Automatic.
     */
    AUTO(0),

    /**
     * Per shape (or face).
     */
    SHAPE(1),

    /**
     * Per vertex.
     */
    VERTEX(2);

    /**
     * The integer code of the constant.
     */
    private final int val;

    /**
     * The enumeration constructor.
     *
     * @param val the integer value
     */
    NormalMode(final int val) {
        this.val = val;
    }

    /**
     * Get a normal mode from an integer value.
     *
     * @param i the integer
     * @return the constant
     */
    public static NormalMode fromValue(final int i) {

        switch (i) {
            case 2:
                return VERTEX;

            case 1:
                return SHAPE;

            case 0:
            default:
                return AUTO;
        }
    }

    /**
     * Gets the integer code of the constant.
     *
     * @return the integer
     */
    public int getVal() {
        return this.val;
    }

}
