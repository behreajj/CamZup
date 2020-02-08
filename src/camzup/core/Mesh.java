package camzup.core;

/**
 * An abstract parent for mesh objects.
 */
public abstract class Mesh extends EntityData implements IMesh {

   /**
    * The material associated with this mesh in a mesh entity.
    */
   public int materialIndex = 0;

   /**
    * The default constructor.
    */
   protected Mesh () {

      super();
   }

   /**
    * Construct a mesh and give it a name.
    *
    * @param name
    *           the name
    */
   protected Mesh ( final String name ) {

      super(name);
   }

   /**
    * Gets this mesh's material index.
    *
    * @return the material index
    */
   public int getMaterialIndex () {

      return this.materialIndex;
   }

   /**
    * Sets this mesh's material index.
    *
    * @param i
    *           the index
    * @return this mesh
    */
   @Chainable
   public Mesh setMaterialIndex ( final int i ) {

      this.materialIndex = i;
      return this;
   }
}