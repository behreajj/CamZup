package com.behreajj.camzup.core;

import java.util.Objects;

/**
 * An abstract material which holds data to display textured materials. Holds a
 * transform that may be applied to UV coordinates. This class expects to be
 * extended by a subclass that holds a given image implementation.
 */
public abstract class MaterialUv extends Material implements IOriented2, ISpatial2, IVolume2 {

    /**
     * The texture tint.
     */
    public final Rgb tint = Rgb.white(new Rgb());

    /**
     * The UV coordinate transform.
     */
    public final Transform2 transform;

    /**
     * The default constructor.
     */
    protected MaterialUv() {
        this("MaterialUv");
    }

    /**
     * Creates a named texture material.
     *
     * @param name the name
     */
    protected MaterialUv(final String name) {

        super(name);
        this.transform = new Transform2();
    }

    /**
     * Creates a named texture with a transform.
     *
     * @param name      the name
     * @param transform the UV transform
     */
    protected MaterialUv(final String name, final Transform2 transform) {

        super(name);
        this.transform = transform;
    }

    /**
     * Creates a named texture with a tint and transform.
     *
     * @param name      the name
     * @param transform the UV transform
     * @param tint      the tint color
     */
    protected MaterialUv(final String name, final Transform2 transform, final Rgb tint) {

        super(name);
        this.transform = transform;
        Rgb.clamp01(tint, this.tint);
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final MaterialUv that))
            return false;
        if (!super.equals(o))
            return false;
        return Objects.equals(this.tint, that.tint) && Objects.equals(this.transform, that.transform);
    }

    /**
     * Flips the material horizontally.
     *
     * @return this entity
     */
    @Override
    public MaterialUv flipX() {

        this.transform.flipX();
        return this;
    }

    /**
     * Flips the material vertically.
     *
     * @return this entity
     */
    @Override
    public MaterialUv flipY() {

        this.transform.flipY();
        return this;
    }

    /**
     * Gets this material's transform's texture coordinate location.
     *
     * @param target the output vector
     * @return the location
     */
    @Override
    public Vec2 getLocation(final Vec2 target) {

        return this.transform.getLocation(target);
    }

    /**
     * Gets this material's transform's texture coordinate location.
     *
     * @return the rotation
     */
    @Override
    public float getRotation() {
        return this.transform.getRotation();
    }

    /**
     * Gets this material's transform's scale.
     *
     * @param target the output vector
     * @return the scale
     */
    @Override
    public Vec2 getScale(final Vec2 target) {

        return this.transform.getScale(target);
    }

    /**
     * Returns the hash code for this material.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {

        // TODO: Replace this with IntelliJ generated hash code.
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (this.tint == null ? 0 : this.tint.hashCode());
        return prime * result + (this.transform == null ? 0 : this.transform.hashCode());
    }

    /**
     * Moves this material by a vector.
     *
     * @param dir the vector
     * @return this material
     */
    @Override
    public MaterialUv moveBy(final Vec2 dir) {

        this.transform.moveBy(dir);
        return this;
    }

    /**
     * Moves this material to a location.
     *
     * @param locNew the location
     * @return this material
     */
    @Override
    public MaterialUv moveTo(final Vec2 locNew) {

        this.transform.moveTo(locNew);
        return this;
    }

    /**
     * Moves this material to a location over a step in time.
     *
     * @param locNew the location
     * @param step   the step
     * @return this material
     */
    @Override
    public MaterialUv moveTo(final Vec2 locNew, final float step) {

        this.transform.moveTo(locNew, step);
        return this;
    }

    /**
     * Rotates this material to an angle.
     *
     * @param rotNew the rotation
     * @return this material
     */
    @Override
    public MaterialUv rotateTo(final float rotNew) {

        this.transform.rotateTo(rotNew);
        return this;
    }

    /**
     * Rotates this material to an angle over a step in time.
     *
     * @param rotNew the angle
     * @param step   the step
     * @return this material
     */
    @Override
    public MaterialUv rotateTo(final float rotNew, final float step) {

        this.transform.rotateTo(rotNew, step);
        return this;
    }

    /**
     * Rotates this material by an angle around the z axis.
     *
     * @param radians the angle
     * @return this material
     */
    @Override
    public MaterialUv rotateZ(final float radians) {

        this.transform.rotateZ(radians);
        return this;
    }

    /**
     * Scales the material by a scalar.
     *
     * @param scalar the scalar
     * @return this material
     */
    @Override
    public MaterialUv scaleBy(final float scalar) {

        this.transform.scaleBy(scalar);
        return this;
    }

    /**
     * Scales the material by a non-uniform scalar.
     *
     * @param scalar the scalar
     * @return the material
     */
    @Override
    public MaterialUv scaleBy(final Vec2 scalar) {

        this.transform.scaleBy(scalar);
        return this;
    }

    /**
     * Scales the material to a uniform size.
     *
     * @param scalar the size
     * @return this material
     */
    @Override
    public MaterialUv scaleTo(final float scalar) {

        this.transform.scaleTo(scalar);
        return this;
    }

    /**
     * Scales the material to a non-uniform size.
     *
     * @param scalar the size
     * @return this material
     */
    @Override
    public MaterialUv scaleTo(final Vec2 scalar) {

        this.transform.scaleTo(scalar);
        return this;
    }

    /**
     * Eases the material to a scale by a step over time.
     *
     * @param scalar the scalar
     * @param step   the step
     * @return this material
     */
    @Override
    public MaterialUv scaleTo(final Vec2 scalar, final float step) {

        this.transform.scaleTo(scalar, step);
        return this;
    }

    /**
     * Sets the material's tint color.
     *
     * @param tint the tint color
     * @return this material
     */
    public MaterialUv setTint(final Rgb tint) {

        Rgb.clamp01(tint, this.tint);
        return this;
    }

    /**
     * Returns a string representation of this material.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return this.toString(Utils.FIXED_PRINT);
    }

    /**
     * Returns a string representation of this material.
     *
     * @param places the number of places
     * @return the string
     */
    public String toString(final int places) {

        final StringBuilder sb = new StringBuilder(256);
        sb.append("{\"name\":\"");
        sb.append(this.name);
        sb.append("\",\"tint\":");
        this.tint.toString(sb, places);
        sb.append(",\"transform\":");
        this.transform.toString(sb, places);
        sb.append('}');
        return sb.toString();
    }
}
