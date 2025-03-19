package com.behreajj.camzup.core;

import java.util.Objects;

/**
 * An entity in two-dimensional space that can be moved around with a transform,
 * but contains no
 * other data. Useful when a 'target' entity is needed.
 */
public class Entity2 extends Entity implements Comparable<Entity2>, IOriented2, ISpatial2 {

    /**
     * The entity's transform.
     */
    public final Transform2 transform;

    /**
     * The default constructor.
     */
    public Entity2() {
        this.transform = new Transform2();
    }

    /**
     * Constructs a named entity. A new transform is created by the constructor.
     *
     * @param name the name
     */
    public Entity2(final String name) {

        super(name);
        this.transform = new Transform2();
    }

    /**
     * Constructs a named entity with a transform. The transform is assigned by
     * reference, and so it
     * can be changed outside the entity.
     *
     * @param name      the name
     * @param transform the transform
     */
    public Entity2(final String name, final Transform2 transform) {

        super(name);
        this.transform = transform;
    }

    /**
     * Constructs an entity with a transform. The transform is assigned by
     * reference, and so it can be
     * changed outside the entity.
     *
     * @param transform the transform
     */
    public Entity2(final Transform2 transform) {

        this.transform = transform;
    }

    /**
     * Compares this entity to another based on transform.
     *
     * @param entity the other entity
     * @return the evaluation
     */
    @Override
    public int compareTo(final Entity2 entity) {

        return this.transform.compareTo(entity.transform);
    }

    /**
     * Tests this entity for equivalence with an object.
     *
     * @param obj the object
     * @return the evaluation
     */
    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || this.getClass() != obj.getClass()) {
            return false;
        }
        return this.equals(obj);
    }

    /**
     * Gets this entity's location.
     *
     * @param target the output vector
     * @return the location
     */
    @Override
    public Vec2 getLocation(final Vec2 target) {

        return this.transform.getLocation(target);
    }

    /**
     * Gets this entity's rotation.
     */
    @Override
    public float getRotation() {

        return this.transform.getRotation();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), transform);
    }

    /**
     * Moves this entity by a vector.
     *
     * @param dir the vector
     * @return this entity
     */
    @Override
    public Entity2 moveBy(final Vec2 dir) {

        this.transform.moveBy(dir);
        return this;
    }

    /**
     * Moves this entity to a location.
     *
     * @param locNew the location
     * @return this entity
     */
    @Override
    public Entity2 moveTo(final Vec2 locNew) {

        this.transform.moveTo(locNew);
        return this;
    }

    /**
     * Moves this entity to a location over a step in time.
     *
     * @param locNew the location
     * @param step   the step
     * @return this entity
     */
    @Override
    public Entity2 moveTo(final Vec2 locNew, final float step) {

        this.transform.moveTo(locNew, step);
        return this;
    }

    /**
     * Resets the entity to an initial state by setting its transform to the
     * identity.
     *
     * @return this entity
     */
    public Entity2 reset() {

        Transform2.identity(this.transform);
        return this;
    }

    /**
     * Rotates this entity to an angle.
     *
     * @param rotNew the rotation
     * @return this entity
     */
    @Override
    public Entity2 rotateTo(final float rotNew) {

        this.transform.rotateTo(rotNew);
        return this;
    }

    /**
     * Rotates this entity to an angle over a step in time.
     *
     * @param rotNew the angle
     * @param step   the step
     * @return this entity
     */
    @Override
    public Entity2 rotateTo(final float rotNew, final float step) {

        this.transform.rotateTo(rotNew, step);
        return this;
    }

    /**
     * Rotates this entity by an angle around the z axis.
     *
     * @param radians the angle
     * @return this entity
     */
    @Override
    public Entity2 rotateZ(final float radians) {

        this.transform.rotateZ(radians);
        return this;
    }

    /**
     * Returns a string representation of this entity.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return this.toString(Utils.FIXED_PRINT);
    }

    /**
     * Returns a string representation of this entity.
     *
     * @param places number of places
     * @return the string
     */
    public String toString(final int places) {

        final StringBuilder sb = new StringBuilder(512);
        sb.append("{\"name\":\"");
        sb.append(this.name);
        sb.append('\"');
        sb.append(",\"transform\":");
        this.transform.toString(sb, places);
        sb.append('}');
        return sb.toString();
    }
}
