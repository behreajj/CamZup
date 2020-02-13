package camzup.pfriendly;

import camzup.core.Color;
import camzup.core.Experimental;
import camzup.core.MaterialUv;
import camzup.core.Transform2;
import processing.core.PImage;

/**
 * A material which supports PImages as textures.
 */
@Experimental
public class MaterialPImage extends MaterialUv {

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

      super("MaterialPImage");
      this.texture = texture;
   }

   /**
    * Constructs a name material with a fill and texture.
    *
    * @param name
    *           the name
    * @param transform
    *           the UV transform
    * @param tint
    *           the fill or tint
    * @param texture
    *           the texture
    */
   public MaterialPImage (
         final String name,
         final Transform2 transform,
         final Color tint,
         final PImage texture ) {

      super(name, transform, tint);
      this.texture = texture;
   }

   /**
    * Returns a string representation of this material.
    *
    * @return the string
    */
   @Override
   public String toString () {

      return this.toString(4);
   }

   /**
    * Returns a string representation of this material.
    *
    * @param places
    *           the number of places
    * @return the string
    */
   @Override
   public String toString ( final int places ) {

      return new StringBuilder(256)
            .append("{ name: \"")
            .append(this.name)
            .append("\", tint: ")
            .append(this.tint.toString(places))
            .append(", texture: ")
            .append(ZImage.toString(this.texture))
            .append(", transform: ")
            .append(this.transform.toString(places))
            .append(' ').append('}')
            .toString();
   }
}
