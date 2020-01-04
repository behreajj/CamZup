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

   @Override
   public boolean equals ( final Object obj ) {

      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (this.getClass() != obj.getClass()) {
         return false;
      }
      final Entity other = (Entity) obj;
      if (this.name == null) {
         if (other.name != null) {
            return false;
         }
      } else if (!this.name.equals(other.name)) {
         return false;
      }
      return true;
   }

   @Override
   public int hashCode () {

      // final int prime = 31;
      // int result = 1;
      // result = prime * result
      // + ((this.name == null) ? 0 : this.name.hashCode());
      // return result;

      final int hashBase = -2128831035;
      final int hashMul = 16777619;
      int hash = hashBase;
      hash = hash * hashMul
            ^ (this.name == null ? 0 : this.name.hashCode());
      return hash;
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
