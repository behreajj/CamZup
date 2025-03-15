package camzup.core;

/**
 * An entity to be drawn by a renderer, typically holding a transform and
 * some other kind of data.
 */
public abstract class Entity implements IEntity {

   /**
    * The entity's name.
    */
   public String name;

   /**
    * The default constructor. Assigns the entity's name with the hash
    * identity string.
    *
    * @see IUtils#hashIdentityString()
    */
   protected Entity ( ) { this.name = this.hashIdentityString(); }

   /**
    * Creates an entity with a given name.
    *
    * @param name the name
    */
   protected Entity ( final String name ) { this.name = name; }

   /**
    * Tests this entity for equivalence with an object.
    *
    * @param obj the object
    *
    * @return the evaluation
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null || this.getClass() != obj.getClass() ) { return false; }
      final Entity other = ( Entity ) obj;
      if ( this.name == null ) { return other.name == null; }
      return this.name.equals(other.name);
   }

   /**
    * Gets the entity's name.
    *
    * @return the name
    */
   public String getName ( ) { return this.name; }

   /**
    * Returns a hash code for this entity.
    *
    * @return the hash code
    */
   @Override
   public int hashCode ( ) {

      return IUtils.HASH_BASE * IUtils.HASH_MUL ^ ( this.name == null ? 0
         : this.name.hashCode() );
   }

   /**
    * Sets the entity's name.
    *
    * @param name the name
    *
    * @return this entity
    */
   public Entity setName ( final String name ) {

      if ( name != null ) { this.name = name; }
      return this;
   }

   /**
    * Represents an entity as a class simple name followed by the entity's
    * name.
    *
    * @return the string
    */
   @Override
   public String toString ( ) {

      return "{\"name\":\"" + this.name + '\"' + '}';
   }

}
