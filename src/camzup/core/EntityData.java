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

      final EntityData other = (EntityData) obj;
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
      // + (this.name == null ? 0 : this.name.hashCode());
      // return result;

      final int hashBase = -2128831035;
      final int hashMul = 16777619;
      int hash = hashBase;
      hash = hash * hashMul
            ^ (this.name == null ? 0 : this.name.hashCode());
      return hash;
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
