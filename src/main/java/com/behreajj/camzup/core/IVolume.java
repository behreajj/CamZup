package com.behreajj.camzup.core;

/**
 * Maintains consistent behavior between objects with volume.
 */
public interface IVolume {

    /**
     * Scales the object by a scalar.
     *
     * @param scalar the scalar
     * @return this object
     */
    IVolume scaleBy(final float scalar);

    /**
     * Scales the object to a uniform size.
     *
     * @param scalar the size
     * @return this object
     */
    IVolume scaleTo(final float scalar);
}
