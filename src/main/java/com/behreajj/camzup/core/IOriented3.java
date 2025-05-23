package com.behreajj.camzup.core;

/**
 * Maintains consistent behavior for 3D objects that can be rotated.
 */
public interface IOriented3 {

    /**
     * Gets this object's rotation
     *
     * @param target the output quaternion
     * @return the rotation
     */
    Quaternion getRotation(final Quaternion target);

    /**
     * Rotates this object by an angle around an axis.
     *
     * @param radians the angle
     * @param axis    the axis
     * @return this object
     */
    @SuppressWarnings("UnusedReturnValue")
    IOriented3 rotateBy(final float radians, final Vec3 axis);

    /**
     * Rotates this object to a quaternion.
     *
     * @param rotNew the new rotation
     * @return this object
     */
    @SuppressWarnings("UnusedReturnValue")
    IOriented3 rotateTo(final Quaternion rotNew);

    /**
     * Rotates this object to a quaternion over a step in time.
     *
     * @param rotNew the new rotation
     * @param step   the step
     * @return this object
     */
    @SuppressWarnings("UnusedReturnValue")
    IOriented3 rotateTo(final Quaternion rotNew, final float step);

    /**
     * Rotates this object by an angle around the x axis.
     *
     * @param radians the angle
     * @return this object
     */
    IOriented3 rotateX(final float radians);

    /**
     * Rotates this object by an angle around the y axis.
     *
     * @param radians the angle
     * @return this object
     */
    IOriented3 rotateY(final float radians);

    /**
     * Rotates this object by an angle around the z axis.
     *
     * @param radians the angle
     * @return this object
     */
    IOriented3 rotateZ(final float radians);
}
