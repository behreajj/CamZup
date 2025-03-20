package com.behreajj.camzup.friendly;

/**
 * Duplicates and clarifies the texture sampling options available to
 * OpenGL renderers (P2D and P3D). The integer value of each
 * is intended to match with integer constants in the
 * {@link processing.opengl.Texture} class. The enum's ordinal should not
 * be used.
 */
public enum TextureSampling {

    /**
     * s
     * Bilinear sampling.
     */
    BILINEAR(4),

    /**
     * Linear sampling. Magnification filtering is nearest, minification set to
     * linear.
     */
    LINEAR(3),

    /**
     * Point sampling. Magnification and minification filtering are set to
     * nearest.
     */
    POINT(2),

    /**
     * Trilinear sampling.
     */
    TRILINEAR(5);

    /**
     * The integer code of the constant.
     */
    private final int val;

    /**
     * The enumeration constructor.
     *
     * @param val the integer value
     */
    TextureSampling(final int val) {
        this.val = val;
    }

    /**
     * Gets a sampling constant from an integer value.
     *
     * @param i the integer
     * @return the constant
     */
    public static TextureSampling fromValue(final int i) {

        switch (i) {
            case 5:
                return TRILINEAR;

            case 4:
                return BILINEAR;

            case 3:
                return LINEAR;

            case 2:
            default:
                return POINT;
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
