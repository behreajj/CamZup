package camzup.core;

/**
 * Holds data that governs how an entity is displayed by a renderer.
 */
public abstract class Material extends EntityData implements IMaterial {

   /**
    * The default constructor.
    */
   protected Material ( ) {}

   /**
    * Creates a material with a name.
    *
    * @param name the name
    */
   protected Material ( final String name ) { super(name); }

}
