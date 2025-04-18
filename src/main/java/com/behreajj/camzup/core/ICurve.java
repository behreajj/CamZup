package com.behreajj.camzup.core;

/**
 * Maintains consistent behavior for curves of different dimensions.
 */
public interface ICurve {

    /**
     * Gets the number of knots in the curve.
     *
     * @return the knot count
     */
    int length();

    /**
     * Toggles whether this is a closed loop.
     *
     * @return this curve
     */
    ICurve toggleLoop();
}
