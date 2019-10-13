package camzup.core;

/**
 * A material which holds data to display textured
 * materials. Holds a transform that may be applied to UV
 * coordinates.
 */
public class MaterialUV extends Material {

   /**
    * The texture tint.
    */
   public final Color tint = DEFAULT_FILL;

   /**
    * The UV coordinate transform.
    */
   public final Transform2 transform = new Transform2();

   /**
    * The default constructor.
    */
   public MaterialUV () {

      super();
   }

   /**
    * Creates a named texture material.
    * 
    * @param name
    *           the name
    */
   public MaterialUV ( String name ) {

      super(name);
   }

   /**
    * Creates a named texture with a tint and transform.
    * 
    * @param name
    *           the name
    * @param transform
    *           the UV transform
    * @param tint
    *           the tint color
    */
   public MaterialUV (
         final String name,
         final Transform2 transform,
         final Color tint ) {

      super(name);
      this.transform.set(transform);
      this.tint.set(tint);
   }
}
