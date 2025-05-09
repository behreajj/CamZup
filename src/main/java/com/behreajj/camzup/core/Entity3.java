package com.behreajj.camzup.core;

import java.util.Objects;

/**
 * An entity in three-dimensional space that can be moved around with a
 * transform, but contains no other data. Useful when a 'target' entity is
 * needed.
 */
public class Entity3 extends Entity implements Comparable<Entity3>, IOriented3, ISpatial3 {

    /**
     * The entity's transform.
     */
    public final Transform3 transform;

    /**
     * The default constructor.
     */
    public Entity3() {
        this.transform = new Transform3();
    }

    /**
     * Constructs a named entity. A new transform is created by the constructor.
     *
     * @param name the name
     */
    public Entity3(final String name) {

        super(name);
        this.transform = new Transform3();
    }

    /**
     * Constructs a named entity with a transform. The transform is assigned
     * by reference, and so it can be changed outside the entity.
     *
     * @param name      the name
     * @param transform the transform
     */
    public Entity3(final String name, final Transform3 transform) {

        super(name);
        this.transform = transform;
    }

    /**
     * Constructs an entity with a transform. The transform is assigned by
     * reference, and so it can be changed outside the entity.
     *
     * @param transform the transform
     */
    public Entity3(final Transform3 transform) {

        this.transform = transform;
    }

    /**
     * Compares this entity to another based on transform.
     *
     * @param entity the other entity
     * @return the evaluation
     */
    @Override
    public int compareTo(final Entity3 entity) {

        return this.transform.compareTo(entity.transform);
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final Entity3 entity3))
            return false;
        if (!super.equals(o))
            return false;
        return Objects.equals(this.transform, entity3.transform);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.transform);
    }

    /**
     * Gets this entity's location.
     *
     * @param target the output vector
     * @return the location
     */
    @Override
    public Vec3 getLocation(final Vec3 target) {

        return this.transform.getLocation(target);
    }

    /**
     * Gets this entity's rotation.
     *
     * @param target the output quaternion
     * @return the rotation
     */
    @Override
    public Quaternion getRotation(final Quaternion target) {

        return this.transform.getRotation(target);
    }

    /**
     * Orients the entity to look at a target point.
     *
     * @param point      the target point
     * @param step       the step
     * @param handedness the handedness
     * @return this transform
     * @see Transform3#lookAt(Vec3, float, Handedness)
     */
    public Entity3 lookAt(final Vec3 point, final float step, final Handedness handedness) {

        this.transform.lookAt(point, step, handedness);
        return this;
    }

    /**
     * Orients the entity to look in a direction.
     *
     * @param dir        the direction
     * @param step       the step
     * @param handedness the handedness
     * @return this transform
     * @see Transform3#lookIn(Vec3, float, Handedness)
     */
    public Entity3 lookIn(final Vec3 dir, final float step, final Handedness handedness) {

        this.transform.lookIn(dir, step, handedness);
        return this;
    }

    /**
     * Moves this entity by a vector.
     *
     * @param dir the vector
     * @return this entity
     */
    @Override
    public Entity3 moveBy(final Vec3 dir) {

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
    public Entity3 moveTo(final Vec3 locNew) {

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
    public Entity3 moveTo(final Vec3 locNew, final float step) {

        this.transform.moveTo(locNew, step);
        return this;
    }

    /**
     * Resets the entity to an initial state by setting its transform to the
     * identity.
     *
     * @return this entity
     */
    public Entity3 reset() {

        Transform3.identity(this.transform);
        return this;
    }

    /**
     * Rotates this object by an angle around an axis.
     *
     * @param radians the angle
     * @param axis    the axis
     * @return this object
     */
    @Override
    public Entity3 rotateBy(final float radians, final Vec3 axis) {

        this.transform.rotateBy(radians, axis);
        return this;
    }

    /**
     * Rotates this object by a quaternion.
     *
     * @param q the quaternion
     * @return this object
     */
    public Entity3 rotateBy(final Quaternion q) {

        this.transform.rotateBy(q);
        return this;
    }

    /**
     * Rotates this entity to a quaternion.
     *
     * @param rotNew the new rotation
     * @return this entity
     */
    @Override
    public Entity3 rotateTo(final Quaternion rotNew) {

        this.transform.rotateTo(rotNew);
        return this;
    }

    /**
     * Rotates this entity to a quaternion over a step in time.
     *
     * @param rotNew the new rotation
     * @param step   the step
     * @return this entity
     */
    @Override
    public Entity3 rotateTo(final Quaternion rotNew, final float step) {

        this.transform.rotateTo(rotNew, step);
        return this;
    }

    /**
     * Rotates this entity by an angle around the x axis.
     *
     * @param radians the angle
     * @return this entity
     */
    @Override
    public Entity3 rotateX(final float radians) {

        this.transform.rotateX(radians);
        return this;
    }

    /**
     * Rotates this entity by an angle around the y axis.
     *
     * @param radians the angle
     * @return this entity
     */
    @Override
    public Entity3 rotateY(final float radians) {

        this.transform.rotateY(radians);
        return this;
    }

    /**
     * Rotates this entity by an angle around the z axis.
     *
     * @param radians the angle
     * @return this entity
     */
    @Override
    public Entity3 rotateZ(final float radians) {

        this.transform.rotateZ(radians);
        return this;
    }

    /**
     * Returns a string representation of this entity
     *
     * @return the string
     */
    @Override
    public String toString() {
        return this.toString(Utils.FIXED_PRINT);
    }

    /**
     * Returns a string representation of this entity
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

    /**
     * Tests this entity for equivalence with another.
     *
     * @param entity the entity
     * @return the evaluation
     */
    protected boolean equals(final Entity3 entity) {

        if (this.transform == null) {
            return entity.transform == null;
        }
        return this.transform.equals(entity.transform);
    }
}
