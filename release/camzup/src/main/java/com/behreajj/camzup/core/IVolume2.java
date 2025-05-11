package com.behreajj.camzup.core;

/**
 * Maintains consistent behavior between 2D objects with volume.
 */
public interface IVolume2 extends IVolume {

    /**
     * Flips the object's scale horizontally.
     *
     * @return this object
     */
    IVolume2 flipX();

    /**
     * Flips the object's scale vertically.
     *
     * @return this object
     */
    IVolume2 flipY();

    /**
     * Gets the nonuniform scale of the volume.
     *
     * @param target the output target
     * @return the scale
     */
    Vec2 getScale(final Vec2 target);

    /**
     * Scales the object by a non-uniform scalar.
     *
     * @param scalar the scalar
     * @return the object
     */
    IVolume2 scaleBy(final Vec2 scalar);

    /**
     * Scales the object to a non-uniform size.
     *
     * @param scalar the size
     * @return the object
     */
    IVolume2 scaleTo(final Vec2 scalar);

    /**
     * Eases the object to a scale by a step over time.
     *
     * @param scalar the scalar
     * @param step   the step
     * @return this object
     */
    IVolume2 scaleTo(final Vec2 scalar, final float step);
}
