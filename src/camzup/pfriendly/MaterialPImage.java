package camzup.pfriendly;

import java.util.ArrayList;

import camzup.core.Color;
import camzup.core.MaterialUv;
import camzup.core.Transform2;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

/**
 * A material which supports PImages as textures.
 */
public class MaterialPImage extends MaterialUv {

   /**
    * The material's texture.
    */
   public PImage texture;

   /**
    * The default constructor.
    */
   public MaterialPImage ( ) {

      super("MaterialPImage");
      this.texture = new PImage(128, 128, PConstants.ARGB, 1);
      ZImage.fill(0xffffffff, this.texture);
   }

   /**
    * Constructs a material from a texture.
    *
    * @param texture the texture
    */
   public MaterialPImage ( final PImage texture ) {

      super("MaterialPImage");
      this.setTexture(texture);
   }

   /**
    * Constructs a name material with a fill and texture.
    *
    * @param name      the name
    * @param transform the UV transform
    * @param texture   the texture
    */
   public MaterialPImage ( final String name, final Transform2 transform,
      final PImage texture ) {

      super(name, transform);
      this.setTexture(texture);
   }

   /**
    * Constructs a name material with a texture.
    *
    * @param name      the name
    * @param transform the UV transform
    * @param texture   the texture
    * @param tint      the fill or tint
    */
   public MaterialPImage ( final String name, final Transform2 transform,
      final PImage texture, final Color tint ) {

      super(name, transform, tint);
      this.setTexture(texture);
   }

   /**
    * Gets the material's texture.
    *
    * @return the texture
    */
   public PImage getTexture ( ) { return this.texture; }

   /**
    * Sets the material's texture. If the supplied texture is null, then a new
    * texture is created and filled with a color.
    *
    * @param texture the texture
    *
    * @return this material
    */
   public MaterialPImage setTexture ( final PImage texture ) {

      if ( texture != null ) {
         this.texture = texture;
      } else {
         this.texture = new PImage(128, 128, PConstants.ARGB, 1);
         ZImage.fill(0xffffffff, this.texture);
      }
      return this;
   }

   /**
    * Sets the texture's parent applet. Useful when working with PImages
    * created with a constructor that will be saved to file.
    *
    * @param parent the parent applet
    *
    * @return this material
    */
   public MaterialPImage setTextureParent ( final PApplet parent ) {

      this.texture.parent = parent;
      return this;
   }

   /**
    * Returns a string representation of this material.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(4); }

   /**
    * Returns a string representation of this material.
    *
    * @param places the number of places
    *
    * @return the string
    */
   @Override
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(256);
      sb.append("{ name: \"");
      sb.append(this.name);
      sb.append("\", tint: ");
      sb.append(this.tint.toString(places));
      sb.append(", texture: ");
      sb.append(ZImage.toString(this.texture));
      sb.append(", transform: ");
      sb.append(this.transform.toString(places));
      sb.append(' ');
      sb.append('}');
      return sb.toString();
   }

   /**
    * Creates a material from an array of strings representing a Wavefront
    * .mtl file. The support for this file format is <em>very</em> minimal, as
    * it is unlikely that its contents would be reproducible between a variety
    * of renderers. The material's tint is set to the diffuse color. If an
    * image file is referenced for the diffuse map, then a message is printed
    * to the console.
    *
    * @param lines the String tokens
    *
    * @return the material
    */
   public static MaterialPImage[] fromMtl ( final String[] lines ) {

      final int len = lines.length;
      String[] tokens;
      final ArrayList < MaterialPImage > result = new ArrayList <>();
      MaterialPImage current = null;

      String alpha = "1.0";

      for ( int i = 0; i < len; ++i ) {

         /* Split line by spaces. */
         tokens = lines[i].split("\\s+");

         /* Skip empty lines. */
         if ( tokens.length > 0 ) {
            final String initialToken = tokens[0].toLowerCase();

            if ( initialToken.equals("newmtl") ) {

               current = new MaterialPImage();
               result.add(current);
               current.name = tokens[1];

            } else if ( initialToken.equals("kd") ) {

               current.tint.set(tokens[1], tokens[2], tokens[3], alpha);

            } else if ( initialToken.equals("d") ) {

               alpha = tokens[1];

            } else if ( initialToken.equals("map_kd") ) {

               final StringBuilder sb = new StringBuilder(128);
               sb.append("The .mtl file refers to the image file ");
               sb.append(tokens[1]);
               sb.append(" .");
               System.out.println(sb.toString());
            }
         }
      }

      return result.toArray(new MaterialPImage[result.size()]);
   }

}
