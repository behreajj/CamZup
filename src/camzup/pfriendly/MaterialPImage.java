package camzup.pfriendly;

import camzup.core.Color;
import camzup.core.Experimental;
import camzup.core.MaterialUV;
import camzup.core.Transform2;
import processing.core.PImage;

/**
 * A material which supports PImages as textures.
 */
@Experimental
public class MaterialPImage extends MaterialUV {

   /**
    * The texture.
    */
   public final PImage texture;

   /**
    * The default constructor.
    *
    * @param texture
    *           the texture
    */
   public MaterialPImage ( final PImage texture ) {

      super();
      this.texture = texture;
   }

   /**
    * Constructs a name material with a fill and texture.
    *
    * @param name
    *           the name
    * @param transform
    *           the UV transform
    * @param fill
    *           the fill or tint
    * @param texture
    *           the texture
    */
   public MaterialPImage (
         final String name,
         final Transform2 transform,
         final Color fill,
         final PImage texture ) {

      super(name, transform, fill);
      this.texture = texture;
   }
}
