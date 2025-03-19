package com.behreajj.camzup.core;

/**
 * An abstract parent class for data held by an entity.
 */
public abstract class EntityData {

    /**
     * The name of the data.
     */
    public String name;

    /**
     * The default constructor. The data's name is derived from its identity hash
     * code.
     */
    protected EntityData() {
        this.name = Integer.toHexString(System.identityHashCode(this));
    }

    /**
     * Constructs an entity data with a name.
     *
     * @param name the name
     */
    protected EntityData(final String name) {
        this.name = name;
    }

    /**
     * Tests this entity data for equivalence with an object.
     *
     * @param obj the object
     * @return the evaluation
     */
    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        return this.equals((EntityData) obj);
    }

    /**
     * Gets the entity data's name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the entity data's name.
     *
     * @param name the name
     * @return this entity
     */
    public EntityData setName(final String name) {

        if (name != null) {
            this.name = name;
        }
        return this;
    }

    /**
     * Returns the entity data as a class simple name followed by its name.
     *
     * @return the string
     */
    @Override
    public String toString() {

        return "{\"name\":\"" + this.name + '\"' + '}';
    }

    /**
     * Tests this entity data for equivalence with another.
     *
     * @param other the other entity
     * @return the evaluation
     */
    protected boolean equals(final EntityData other) {

        if (this.name == null) {
            return other.name == null;
        }
        return this.name.equals(other.name);
    }
}
