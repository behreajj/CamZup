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
      this.tint = Color.white(new Color());
   }

   /**
    * The default constructor.
    */
   public MaterialUV () {

      this("MaterialUV");
   }

   /**
    * Creates a named texture material.
    *
    * @param name
    *           the name
    */
   public MaterialUV ( final String name ) {

      super(name);
      this.transform = new Transform2();
      this.tint.set(this.tint);
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
      this.transform = transform;
      this.tint.set(tint);
   }
}
