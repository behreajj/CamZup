package camzup.core;

/**
 * An abstract parent for mesh objects.
 */
public abstract class Mesh extends EntityData {

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
}