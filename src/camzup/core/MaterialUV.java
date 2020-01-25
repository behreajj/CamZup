package camzup.core;

/**
 * A material which holds data to display textured
 * materials. Holds a transform that may be applied to UV
 * coordinates.
 */
@Experimental
public class MaterialUV extends Material {

   /**
    * The texture tint.
    */
   public final Color tint;

   /**
    * The UV coordinate transform.
    */
   public final Transform2 transform;

   {
      /*
       * Instance variables should never be assigned constants
       * when the constants are objects, as the constants will be
       * overwritten. Rather, instance variables should receive
       * copies of constants. To make this clearer, an initializer
       * block.
       */

      this.tint = new Color(IMaterial.DEFAULT_FILL);
      this.transform = new Transform2();
   }

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
   public MaterialUV ( final String name ) {

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
