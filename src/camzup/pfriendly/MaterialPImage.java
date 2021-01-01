package camzup.pfriendly;

import java.io.BufferedReader;

import java.util.ArrayList;
import java.util.regex.Pattern;

import camzup.core.Color;
import camzup.core.MaterialUv;
import camzup.core.Transform2;
import camzup.core.Vec2;

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
    * Tests this material for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the equivalence
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( !super.equals(obj) ) { return false; }
      if ( this.getClass() != obj.getClass() ) { return false; }
      final MaterialPImage other = ( MaterialPImage ) obj;
      if ( this.texture == null ) {
         if ( other.texture != null ) { return false; }
      } else if ( !this.texture.equals(other.texture) ) { return false; }
      return true;
   }

   /**
    * Gets this material's transform's texture coordinate location.
    *
    * @param target the output vector
    *
    * @return the location
    */
   @Override
   public Vec2 getLocation ( final Vec2 target ) {

      return this.transform.getLocation(target);
   }

   /**
    * Gets this material's transform's texture coordinate location.
    *
    * @return the rotation
    */
   @Override
   public float getRotation ( ) {

      return this.transform.getRotation();
   }

   /**
    * Gets this material's transform's scale.
    *
    * @param target the output vector
    *
    * @return the scale
    */
   @Override
   public Vec2 getScale ( final Vec2 target ) {

      return this.transform.getScale(target);
   }

   /**
    * Gets the material's texture.
    *
    * @return the texture
    */
   public PImage getTexture ( ) { return this.texture; }

   /**
    * Returns a hash code for this material.
    *
    * @return the hash code
    */
   @Override
   public int hashCode ( ) {

      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ( this.texture == null ? 0 : this.texture
         .hashCode() );
      return result;
   }

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
    * Creates a material from a buffered reader that holds a Wavefront .mtl
    * file. The support for this file format is <em>very</em> minimal, as it
    * is unlikely that its contents would be reproducible between a variety of
    * renderers. The material's tint is set to the diffuse color. If an image
    * file is referenced for the diffuse map, then a message is printed to the
    * console.
    *
    * @param in buffered reader
    *
    * @return the material
    */
   public static MaterialPImage[] fromMtl ( final BufferedReader in ) {

      String[] tokens;
      final ArrayList < MaterialPImage > result = new ArrayList <>();
      MaterialPImage current = null;

      float alpha = 1.0f;
      final Pattern spacePattern = Pattern.compile("\\s+");
      try {
         try {
            for ( String ln = in.readLine(); ln != null; ln = in.readLine() ) {
               /* Split line by spaces. */
               tokens = spacePattern.split(ln, 0);

               /* Skip empty lines. */
               if ( tokens.length > 0 ) {
                  final String initialToken = tokens[0].toLowerCase();

                  if ( initialToken.equals("newmtl") ) {

                     current = new MaterialPImage();
                     result.add(current);
                     current.name = tokens[1];

                  } else if ( initialToken.equals("d") ) {

                     alpha = Float.parseFloat(tokens[1]);

                  } else if ( current != null && initialToken.equals("kd") ) {

                     current.tint.set(Float.parseFloat(tokens[1]), Float
                        .parseFloat(tokens[2]), Float.parseFloat(tokens[3]),
                        alpha);

                  } else if ( initialToken.equals("map_kd") ) {
                     final StringBuilder sb = new StringBuilder(128);
                     sb.append("The .mtl file refers to the image file ");
                     sb.append(tokens[1]);
                     sb.append(" .");
                     System.out.println(sb.toString());
                  }
               }
            }
         } catch ( final Exception e ) {
            e.printStackTrace();
         } finally {
            in.close();
         }
      } catch ( final Exception e ) {
         e.printStackTrace();
      }

      return result.toArray(new MaterialPImage[result.size()]);
   }

}
