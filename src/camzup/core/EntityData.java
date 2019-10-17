package camzup.core;

/**
 * An abstract parent class for data held by an entity.
 */
public abstract class EntityData implements IEntityData {

   /**
    * The name of the data.
    */
   public String name;

   /**
    * The default constructor. The data's name is derived from
    * its identity hash code.
    */
   public EntityData () {

      this.name = this.hashIdentityString();
   }

   /**
    * Constructs an entity data with a name.
    *
    * @param name
    *           the name
    */
   public EntityData ( final String name ) {

      this.name = name;
   }

   /**
    * Returns the entity data as a class simple name followed
    * by its name.
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
