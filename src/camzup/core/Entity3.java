package camzup.core;

/**
 * An entity in three dimensional space that can be moved around with
 * a transform, but contains no other data. Useful when a 'target'
 * entity is needed.
 */
public class Entity3 extends Entity implements ISpatial3 {

  /**
   * The entity's transform.
   */
  public final Transform3 transform;

  /**
   * The default constructor.
   */
  public Entity3 ( ) {

    super();
    this.transform = new Transform3();
  }

  /**
   * Constructs a named entity. A new transform is created by the
   * constructor.
   *
   * @param name the name
   */
  public Entity3 ( final String name ) {

    super(name);
    this.transform = new Transform3();
  }

  /**
   * Constructs a named entity with a transform. The transform is
   * assigned by reference, and so it can be changed outside the entity.
   *
   * @param name      the name
   * @param transform the transform
   */
  public Entity3 (
      final String name,
      final Transform3 transform ) {

    super(name);
    this.transform = transform;
  }

  /**
   * Constructs an entity with a transform. The transform is assigned by
   * reference, and so it can be changed outside the entity.
   *
   * @param transform the transform
   */
  public Entity3 ( final Transform3 transform ) {

    super();
    this.transform = transform;
  }

  /**
   * Tests this entity for equivalence with another.
   *
   * @param entity the entity
   * @return the evaluation
   */
  protected boolean equals ( final Entity3 entity ) {

    if ( this.transform == null ) {
      if ( entity.transform != null ) { return false; }
    } else if ( !this.transform.equals(entity.transform) ) { return false; }

    return true;
  }

  /**
   * Tests this entity for equivalence with an object.
   *
   * @param obj the object
   * @return the evaluation
   */
  @Override
  public boolean equals ( final Object obj ) {

    if ( this == obj ) { return true; }
    if ( !super.equals(obj) ) { return false; }
    if ( this.getClass() != obj.getClass() ) { return false; }
    return this.equals((Entity3) obj);
  }

  /**
   * Returns a hash code for this entity.
   *
   * @return the hash code
   */
  @Override
  public int hashCode ( ) {

    int hash = super.hashCode();
    hash = hash * IUtils.HASH_MUL
        ^ (this.transform == null ? 0 : this.transform.hashCode());
    return hash;
  }

  /**
   * Moves this entity by a vector.
   *
   * @param dir the vector
   * @return this entity
   */
  @Override
  @Chainable
  public Entity3 moveBy ( final Vec3 dir ) {

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
  @Chainable
  public Entity3 moveTo ( final Vec3 locNew ) {

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
  @Chainable
  public Entity3 moveTo (
      final Vec3 locNew,
      final float step ) {

    this.transform.moveTo(locNew, step);
    return this;
  }

  /**
   * Rotates this entity by an axis and angle in radians.
   *
   * @param radians the angle in radians
   * @param axis    the axis
   * @return this entity
   */
  @Override
  @Chainable
  public Entity3 rotateBy (
      final float radians,
      final Vec3 axis ) {

    this.transform.rotateBy(radians, axis);
    return this;
  }

  /**
   * Rotates this entity by a quaternion.
   *
   * @param rot the quaternion
   * @return this entity
   */
  @Override
  @Chainable
  public Entity3 rotateBy ( final Quaternion rot ) {

    this.transform.rotateBy(rot);
    return this;
  }

  /**
   * Rotates this entity to a quaternion.
   *
   * @param rotNew the new rotation
   * @return this entity
   */
  @Override
  @Chainable
  public Entity3 rotateTo ( final Quaternion rotNew ) {

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
  @Chainable
  public Entity3 rotateTo (
      final Quaternion rotNew,
      final float step ) {

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
  @Chainable
  public Entity3 rotateX ( final float radians ) {

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
  @Chainable
  public Entity3 rotateY ( final float radians ) {

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
  @Chainable
  public Entity3 rotateZ ( final float radians ) {

    this.transform.rotateZ(radians);
    return this;
  }

  /**
   * Returns a string representation of this entity
   *
   * @return the string
   */
  @Override
  public String toString ( ) {

    return this.toString(4);
  }

  /**
   * Returns a string representation of this entity
   *
   * @param places number of places
   * @return the string
   */
  public String toString ( final int places ) {

    return new StringBuilder()
        .append("{ name: \"")
        .append(this.name)
        .append('\"')
        .append(", transform: ")
        .append(this.transform.toString(places))
        .append(' ')
        .append('}')
        .toString();
  }
}
