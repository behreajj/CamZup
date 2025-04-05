package com.behreajj.camzup.core;

import java.util.Objects;

/**
 * An entity to be drawn by a renderer, typically holding a transform and some
 * other kind of data.
 */
public abstract class Entity {

    /**
     * The entity's name.
     */
    public String name;

    /**
     * The default constructor. Assigns the entity's name with the hash
     * identity string.
     */
    protected Entity() {
        this.name = Integer.toHexString(System.identityHashCode(this));
    }

    /**
     * Creates an entity with a given name.
     *
     * @param name the name
     */
    protected Entity(final String name) {
        this.name = name;
    }

    /**
     * Gets the entity's name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the entity's name.
     *
     * @param name the name
     * @return this entity
     */
    public Entity setName(final String name) {

        if (name != null) {
            this.name = name;
        }
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof final Entity entity))
            return false;
        return Objects.equals(this.name, entity.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.name);
    }

    @Override
    public String toString() {

        return "{\"name\":\"" + this.name + '\"' + '}';
    }
}
