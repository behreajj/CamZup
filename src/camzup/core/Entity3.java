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

   Entity3 () {

      super();
      this.transform = new Transform3();
   }

   Entity3 ( final String name ) {

      super(name);
      this.transform = new Transform3();
   }

   Entity3 (
         final String name,
         final Transform3 transform ) {

      super(name);
      this.transform = transform;
   }

   Entity3 ( final Transform3 transform ) {

      super();
      this.transform = transform;
   }

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

   @Chainable
   public Entity3 moveBy ( final Vec3 dir ) {

      this.transform.moveBy(dir);
      return this;
   }

   @Chainable
   public Entity3 moveTo ( final Vec3 locNew ) {

      this.transform.moveTo(locNew);
      return this;
   }

   @Chainable
   public Entity3 moveTo (
         final Vec3 locNew,
         final float step ) {

      this.transform.moveTo(locNew, step);
      return this;
   }

   @Chainable
   public Entity3 rotateBy ( final Quaternion rotNew ) {

      this.transform.rotateBy(rotNew);
      return this;
   }

   @Chainable
   public Entity3 rotateTo ( final Quaternion rotNew ) {

      this.transform.rotateTo(rotNew);
      return this;
   }

   @Chainable
   public Entity3 rotateTo (
         final Quaternion rotNew,
         final float step ) {

      this.transform.rotateTo(rotNew, step);
      return this;
   }

   @Chainable
   public Entity3 rotateX ( final float radians ) {

      this.transform.rotateX(radians);
      return this;
   }

   @Chainable
   public Entity3 rotateY ( final float radians ) {

      this.transform.rotateY(radians);
      return this;
   }

   @Chainable
   public Entity3 rotateZ ( final float radians ) {

      this.transform.rotateZ(radians);
      return this;
   }

   @Chainable
   public Entity3 scaleBy ( final float scalar ) {

      this.transform.scaleBy(scalar);
      return this;
   }

   @Chainable
   public Entity3 scaleBy ( final Vec3 scalar ) {

      this.transform.scaleBy(scalar);
      return this;
   }

   @Chainable
   public Entity3 scaleTo ( final float scalar ) {

      this.transform.scaleTo(scalar);
      return this;
   }

   @Chainable
   public Entity3 scaleTo ( final Vec3 scalar ) {

      this.transform.scaleTo(scalar);
      return this;
   }

   @Chainable
   public Entity3 scaleTo (
         final Vec3 scalar,
         final float step ) {

      this.transform.scaleTo(scalar, step);
      return this;
   }

   @Override
   public String toString () {

      return this.toString(4);
   }

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
