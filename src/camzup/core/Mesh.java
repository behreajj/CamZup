package camzup.core;

/**
 * An abstract parent for mesh objects.
 */
public abstract class Mesh extends EntityData {

   /**
    * Default count of sectors in a regular convex polygon, so
    * as to approximate a circle.
    */
   public static final int DEFAULT_CIRCLE_SECTORS = 32;

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