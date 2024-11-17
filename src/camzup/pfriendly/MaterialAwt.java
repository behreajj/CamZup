package camzup.pfriendly;

import java.awt.Image;
import java.awt.image.BufferedImage;

import camzup.core.Experimental;
import camzup.core.IMaterial;
import camzup.core.IUtils;
import camzup.core.Material;
import camzup.core.Rgb;
import camzup.core.Utils;

/**
 * A material which supports {@link java.awt.Image}s as textures.
 */
@Experimental
public class MaterialAwt extends Material {

   /**
    * The texture sample coordinates.
    */
   public final Sample sample = new Sample();

   /**
    * The stroke color.
    */
   public final Rgb stroke = Rgb.fromHex(IMaterial.DEFAULT_STROKE, new Rgb());

   /**
    * The weight, or width, of the stroke.
    */
   public float strokeWeight = IMaterial.DEFAULT_STROKE_WEIGHT;

   /**
    * The material's texture.
    */
   public Image texture;

   /**
    * Whether or not to display a shape with a stroke.
    */
   public boolean useStroke = false;

   /**
    * The default constructor.
    */
   public MaterialAwt ( ) { this("MaterialAwt"); }

   /**
    * Constructs a material from an image.
    *
    * @param texture the texture
    */
   public MaterialAwt ( final Image texture ) {

      this.texture = texture;
      this.sample.set(0, 0, texture.getWidth(null), texture.getHeight(null));
   }

   /**
    * Constructs a material from a name.
    *
    * @param name the name.
    */
   public MaterialAwt ( final String name ) {

      super(name);

      final BufferedImage img = new BufferedImage(128, 128,
         BufferedImage.TYPE_INT_ARGB);
      final int[] px = new int[128 * 128];
      final int len = px.length;
      for ( int i = 0; i < len; ++i ) { px[i] = 0xffffffff; }
      img.getRaster().setDataElements(0, 0, 128, 128, px);
      this.texture = img;
      this.sample.set(0, 0, 128, 128);
   }

   /**
    * Constructs a material from a name and image.
    *
    * @param name    the name
    * @param texture the texture
    */
   public MaterialAwt ( final String name, final Image texture ) {

      super(name);
      this.texture = texture;
   }

   /**
    * Tests this material for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the evaluation
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( !super.equals(obj) || this.getClass() != obj.getClass() ) {
         return false;
      }
      final MaterialAwt other = ( MaterialAwt ) obj;

      if ( !this.sample.equals(other.sample) ) { return false; }

      if ( this.stroke == null ) {
         if ( other.stroke != null ) { return false; }
      } else if ( !this.stroke.equals(other.stroke) ) { return false; }

      if ( Float.floatToIntBits(this.strokeWeight) != Float.floatToIntBits(
         other.strokeWeight) ) {
         return false;
      }

      if ( this.texture == null ) {
         if ( other.texture != null ) { return false; }
      } else if ( !this.texture.equals(other.texture) ) { return false; }

      return this.useStroke == other.useStroke;
   }

   /**
    * Gets the material's texture.
    *
    * @return the texture
    */
   public Image getTexture ( ) { return this.texture; }

   /**
    * Calculates the hash code for this material.
    *
    * @return the hash code
    */
   @Override
   public int hashCode ( ) {

      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + this.sample.hashCode();
      result = prime * result + ( this.stroke == null ? 0 : this.stroke
         .hashCode() );
      result = prime * result + Float.floatToIntBits(this.strokeWeight);
      result = prime * result + ( this.texture == null ? 0 : this.texture
         .hashCode() );
      return prime * result + ( this.useStroke ? 1231 : 1237 );
   }

   /**
    * Sets whether or not to use a stroke with a boolean.
    *
    * @param stroke the boolean
    *
    * @return this material
    */
   public MaterialAwt setStroke ( final boolean stroke ) {

      this.useStroke = stroke;
      return this;
   }

   /**
    * Sets the material's stroke color.
    *
    * @param r red
    * @param g green
    * @param b blue
    *
    * @return this material
    */
   public MaterialAwt setStroke ( final float r, final float g,
      final float b ) {

      this.stroke.set(r, g, b);
      return this;
   }

   /**
    * Sets the material's stroke color.
    *
    * @param r red
    * @param g green
    * @param b blue
    * @param a transparency
    *
    * @return this material
    */
   public MaterialAwt setStroke ( final float r, final float g, final float b,
      final float a ) {

      this.stroke.set(r, g, b, a);
      return this;
   }

   /**
    * Sets the material's stroke color from a hexadecimal value.
    *
    * @param stroke the color
    *
    * @return this material
    */
   public MaterialAwt setStroke ( final int stroke ) {

      Rgb.fromHex(stroke, this.stroke);
      return this;
   }

   /**
    * Sets the material's stroke color.
    *
    * @param stroke the color
    *
    * @return this material
    */
   public MaterialAwt setStroke ( final Rgb stroke ) {

      this.stroke.set(stroke);
      return this;
   }

   /**
    * Sets the material's stroke weight.
    *
    * @param strokeWeight the stroke weight
    *
    * @return this material
    */
   public MaterialAwt setStrokeWeight ( final float strokeWeight ) {

      this.strokeWeight = Utils.max(IUtils.EPSILON, strokeWeight);
      return this;
   }

   /**
    * Sets the material's texture. If the supplied texture is null, then a new
    * texture is created and filled with a color.
    *
    * @param texture the texture
    *
    * @return this material
    */
   public MaterialAwt setTexture ( final Image texture ) {

      if ( texture != null ) {
         this.texture = texture;
      } else {
         final BufferedImage img = new BufferedImage(128, 128,
            BufferedImage.TYPE_INT_ARGB);
         final int[] px = new int[128 * 128];
         final int len = px.length;
         for ( int i = 0; i < len; ++i ) { px[i] = 0xffffffff; }
         img.getRaster().setDataElements(0, 0, 128, 128, px);
         this.texture = img;
      }

      return this;
   }

   /**
    * Toggles the material's stroke.
    *
    * @return this material
    */
   public MaterialAwt toggleStroke ( ) {

      this.useStroke = !this.useStroke;
      return this;
   }

   /**
    * Returns a string representation of this material.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(IUtils.FIXED_PRINT); }

   /**
    * Returns a string representation of this material.
    *
    * @param places the number of places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      final StringBuilder sb = new StringBuilder(256);
      sb.append("{\"name\":\"");
      sb.append(this.name);
      sb.append("\",\"sample\":");
      this.sample.toString(sb, 0);
      sb.append(",\"stroke\":");
      sb.append(this.stroke.toString(places));
      sb.append(",\"strokeWeight\":");
      sb.append(Utils.toFixed(this.strokeWeight, places));
      sb.append(",\"texture\":");
      sb.append(this.texture.toString());
      sb.append(",\"useStroke\":");
      sb.append(this.useStroke);
      sb.append('}');
      return sb.toString();
   }

   /**
    * Organizes the corners used to sample an image, expressed as integer
    * pixel coordinates.
    */
   public static class Sample {

      /**
       * The bottom right corner x.
       */
      int xBottomRight = 0;

      /**
       * The top left corner x.
       */
      int xTopLeft = 0;

      /**
       * The bottom right corner y.
       */
      int yBottomRight = 0;

      /**
       * The top left corner y.
       */
      int yTopLeft = 0;

      /**
       * The default constructor.
       */
      public Sample ( ) {}

      /**
       * Constructs a sample from integer coordinates.
       *
       * @param xtl the top left corner x
       * @param ytl the top left corner y
       * @param xbr the bottom right corner x
       * @param ybr the bottom right corner y
       */
      public Sample ( final int xtl, final int ytl, final int xbr,
         final int ybr ) {

         this.set(xtl, ytl, xbr, ybr);
      }

      /**
       * Tests this sample for equivalence with another object.
       *
       * @return the evaluation
       */
      @Override
      public boolean equals ( final Object obj ) {

         if ( this == obj ) { return true; }
         if ( obj == null || this.getClass() != obj.getClass() ) {
            return false;
         }
         final Sample other = ( Sample ) obj;
         return this.xBottomRight == other.xBottomRight && this.xTopLeft
            == other.xTopLeft && this.yBottomRight == other.yBottomRight
            && this.yTopLeft == other.yTopLeft;
      }

      /**
       * Generates a hash code for this sample.
       *
       * @return the hash code
       */
      @Override
      public int hashCode ( ) {

         final int prime = 31;
         int result = 1;
         result = prime * result + this.xBottomRight;
         result = prime * result + this.xTopLeft;
         result = prime * result + this.yBottomRight;
         return prime * result + this.yTopLeft;
      }

      /**
       * Sets a sample from integer coordinates.
       *
       * @param xtl the top left corner x
       * @param ytl the top left corner y
       * @param xbr the bottom right corner x
       * @param ybr the bottom right corner y
       *
       * @return the sample
       */
      public Sample set ( final int xtl, final int ytl, final int xbr,
         final int ybr ) {

         this.xTopLeft = xtl;
         this.yTopLeft = ytl;
         this.xBottomRight = xbr;
         this.yBottomRight = ybr;

         return this;
      }

      /**
       * Returns a string representation of this sample.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.toString(0); }

      /**
       * Returns a string representation of this sample.
       *
       * @param padding the padding
       *
       * @return the string
       */
      public String toString ( final int padding ) {

         return this.toString(new StringBuilder(96), padding).toString();
      }

      /**
       * Internal helper function to assist with methods that need to print many
       * samples. Appends to an existing {@link StringBuilder}.
       *
       * @param sb      the string builder
       * @param padding the padding
       *
       * @return the string builder
       */
      StringBuilder toString ( final StringBuilder sb, final int padding ) {

         sb.append("{\"xTopLeft\":");
         sb.append(Utils.toPadded(this.xTopLeft, padding));
         sb.append(",\"yTopLeft\":");
         sb.append(Utils.toPadded(this.yTopLeft, padding));
         sb.append(",\"xBottomRight\":");
         sb.append(Utils.toPadded(this.xBottomRight, padding));
         sb.append(",\"yBottomRight\":");
         sb.append(Utils.toPadded(this.yBottomRight, padding));
         sb.append('}');
         return sb;
      }

   }

}
