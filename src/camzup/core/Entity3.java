package camzup.core;

/**
 * An entity in three dimensional space that can be moved
 * around with a transform, but contains no other data.
 * Useful when a 'target' entity is needed.
 *
 */
public class Entity3 extends Entity {

   /**
    * The entity's transform.
    */
   public final Transform3 transform;

   /**
    * The order in which the transform is applied.
    */
   public Transform.Order transformOrder = Transform.Order.TRS;

   /**
    * The default constructor.
    */
   Entity3 () {

      super();
      this.transform = new Transform3();
   }

   /**
    * Constructs a named entity. A new transform is created by
    * the constructor.
    * 
    * @param name
    *           the name
    */
   Entity3 ( final String name ) {

      super(name);
      this.transform = new Transform3();
   }

   /**
    * Constructs a named entity with a transform. The transform
    * is assigned by reference, and so it can be changed
    * outside the entity.
    * 
    * @param name
    *           the name
    * @param transform
    *           the transform
    */
   Entity3 (
         final String name,
         final Transform3 transform ) {

      super(name);
      this.transform = transform;
   }

   /**
    * Constructs an entity with a transform. The transform is
    * assigned by reference, and so it can be changed outside
    * the entity.
    * 
    * @param transform
    *           the transform
    */
   Entity3 ( final Transform3 transform ) {

      super();
      this.transform = transform;
   }

   /**
    * Tests this entity for equivalence with an object.
    *
    * @param obj
    *           the object
    * @return the evaluation
    */
   @Override
   public boolean equals ( final Object obj ) {

      if (this == obj) {
         return true;
      }

      if (!super.equals(obj)) {
         return false;
      }

      if (this.getClass() != obj.getClass()) {
         return false;
      }

      final Entity3 other = (Entity3) obj;

      if (this.transform == null) {
         if (other.transform != null) {
            return false;
         }
      } else if (!this.transform.equals(other.transform)) {
         return false;
      }

      if (this.transformOrder != other.transformOrder) {
         return false;
      }

      return true;
   }

   /**
    * Returns a hash code for this entity.
    *
    * @return the hash code
    */
   @Override
   public int hashCode () {

      int hash = super.hashCode();
      hash = hash * IUtils.HASH_MUL
            ^ (this.transform == null ? 0 : this.transform.hashCode());
      hash = hash * IUtils.HASH_MUL
            ^ (this.transformOrder == null ? 0
                  : this.transformOrder.hashCode());
      return hash;
   }

   /**
    * Moves this entity by a vector.
    *
    * @param dir
    *           the vector
    * @return this entity
    */
   @Chainable
   public Entity3 moveBy ( final Vec3 dir ) {

      this.transform.moveBy(dir);
      return this;
   }

   /**
    * Moves this entity to a location.
    *
    * @param locNew
    *           the location
    * @return this entity
    */
   @Chainable
   public Entity3 moveTo ( final Vec3 locNew ) {

      this.transform.moveTo(locNew);
      return this;
   }

   /**
    * Moves this entity to a location over a step in time.
    *
    * @param locNew
    *           the location
    * @param step
    *           the step
    * @return this entity
    */
   @Chainable
   public Entity3 moveTo (
         final Vec3 locNew,
         final float step ) {

      this.transform.moveTo(locNew, step);
      return this;
   }

   /**
    * Rotates the transform by an axis and angle in radians.
    * 
    * @param radians
    *           the angle in radians
    * @param axis
    *           the axis
    * @return this entity
    */
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
    * @param rot
    *           the quaternion
    * @return this entity
    */
   @Chainable
   public Entity3 rotateBy ( final Quaternion rot ) {

      this.transform.rotateBy(rot);
      return this;
   }

   /**
    * Rotates this entity to a quaternion.
    * 
    * @param rotNew
    *           the new rotation
    * @return this entity
    */
   @Chainable
   public Entity3 rotateTo ( final Quaternion rotNew ) {

      this.transform.rotateTo(rotNew);
      return this;
   }

   /**
    * Rotates this entity to a quaternion over a step in time.
    * 
    * @param rotNew
    *           the new rotation
    * @param step
    *           the step
    * @return this entity
    */
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
    * @param radians
    *           the angle
    * @return this entity
    */
   @Chainable
   public Entity3 rotateX ( final float radians ) {

      this.transform.rotateX(radians);
      return this;
   }

   /**
    * Rotates this entity by an angle around the y axis.
    *
    * @param radians
    *           the angle
    * @return this entity
    */
   @Chainable
   public Entity3 rotateY ( final float radians ) {

      this.transform.rotateY(radians);
      return this;
   }

   /**
    * Rotates this entity by an angle around the z axis.
    *
    * @param radians
    *           the angle
    * @return this entity
    */
   @Chainable
   public Entity3 rotateZ ( final float radians ) {

      this.transform.rotateZ(radians);
      return this;
   }

   /**
    * Scales the entity by a scalar.
    * 
    * @param scalar
    *           the scalar
    * @return this entity
    */
   @Chainable
   public Entity3 scaleBy ( final float scalar ) {

      this.transform.scaleBy(scalar);
      return this;
   }

   /**
    * Scales the entity by a non-uniform scalar.
    * 
    * @param scalar
    *           the scalar
    * @return the entity
    */
   @Chainable
   public Entity3 scaleBy ( final Vec3 scalar ) {

      this.transform.scaleBy(scalar);
      return this;
   }

   /**
    * Scales the entity to a uniform size.
    *
    * @param scalar
    *           the size
    * @return this entity
    */
   @Chainable
   public Entity3 scaleTo ( final float scalar ) {

      this.transform.scaleTo(scalar);
      return this;
   }

   /**
    * Scales the entity to a non-uniform size.
    *
    * @param scalar
    *           the size
    * @return this entity
    */
   @Chainable
   public Entity3 scaleTo ( final Vec3 scalar ) {

      this.transform.scaleTo(scalar);
      return this;
   }

   /**
    * Eases the entity to a scale by a step over time.
    * 
    * @param scalar
    *           the scalar
    * @param step
    *           the step
    * @return this entity
    */
   @Chainable
   public Entity3 scaleTo (
         final Vec3 scalar,
         final float step ) {

      this.transform.scaleTo(scalar, step);
      return this;
   }

   /**
    * Returns a string representation of this entity
    * 
    * @return the string
    */
   @Override
   public String toString () {

      return this.toString(4);
   }

   /**
    * Returns a string representation of this entity
    * 
    * @param places
    *           number of places
    * @return the string
    */
   public String toString ( final int places ) {

      return new StringBuilder()
            .append("{ name: \"")
            .append(this.name)
            .append('\"')
            .append(", transform: ")
            .append(this.transform.toString(places))
            .append(", transformOrder: \"")
            .append(this.transformOrder)
            .append('\"')
            .append(' ')
            .append('}')
            .toString();
   }
}
