package camzup.core;

/**
 * An entity to be drawn by a renderer, typically holding a
 * transform and some other kind of data.
 */
public abstract class Entity implements IEntity {

   /**
    * The entity's name.
    */
   public String name = "";

   /**
    * The default constructor. Assigns the entity's name with
    * the hash identity string.
    * 
    * @see IUtils#hashIdentityString()
    */
   public Entity () {

      this.name = this.hashIdentityString();
   }

   /**
    * Creates an entity with a given name.
    * 
    * @param name
    *           the name
    */
   public Entity ( final String name ) {

      this.name = name;
   }

   /**
    * Represents an entity as a class simple name followed by
    * the entity's name.
    * 
    * @return the string
    */
   @Override
   public String toString () {

      return new StringBuilder()
            .append(this.getClass().getSimpleName())
            .append(": ")
            .append(this.name)
            .toString();
   }
}
