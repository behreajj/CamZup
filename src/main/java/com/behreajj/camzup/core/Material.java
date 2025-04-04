package com.behreajj.camzup.core;

/**
 * Holds data that governs how an entity is displayed by a renderer.
 */
public abstract class Material extends EntityData {

    /**
     * The default stroke color used when none is specified. Expressed as a
     * 32-bit integer formatted as 0xAARRGGBB.
     */
    public static final int DEFAULT_STROKE = 0xff232323;

    /**
     * The default stroke weight used when none is specified.
     */
    public static final float DEFAULT_STROKE_WEIGHT = 1.0f;

    /**
     * The default fill color used when none is specified, (0.6039, 0.8471,
     * 0.8863) in RGB. Expressed as a 32-bit integer formatted as 0xAARRGGBB.
     */
    public static int DEFAULT_FILL = 0xff9ad8e2;

    /**
     * The default constructor.
     */
    protected Material() {
    }

    /**
     * Creates a material with a name.
     *
     * @param name the name
     */
    protected Material(final String name) {
        super(name);
    }
}
