package camzup.core;

import java.util.Random;
import java.util.regex.Pattern;

/**
 * A mutable, extensible color class. Supports RGBA, HSLA and HSVA color
 * spaces. Converts to and from integers where color channels are in the
 * format 0xAARRGGBB.
 */
public class Color implements Comparable < Color > {

   /**
    * The alpha channel (opacity).
    */
   public float a = 1.0f;

   /**
    * The blue channel.
    */
   public float b = 1.0f;

   /**
    * The green channel.
    */
   public float g = 1.0f;

   /**
    * The red channel.
    */
   public float r = 1.0f;

   /**
    * The default constructor. Creates a white color.
    */
   public Color ( ) {}

   /**
    * Creates a color from bytes. In Java, bytes are signed, within the range
    * [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] .
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    */
   public Color ( final byte red, final byte green, final byte blue ) {

      this.set(red, green, blue);
   }

   /**
    * Creates a color from bytes. In Java, bytes are signed, within the range
    * [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] .
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    * @param alpha the alpha channel
    */
   public Color ( final byte red, final byte green, final byte blue,
      final byte alpha ) {

      this.set(red, green, blue, alpha);
   }

   /**
    * Creates a color from a source.
    *
    * @param c the source color
    */
   public Color ( final Color c ) { this.set(c); }

   /**
    * Creates a color out of red, green and blue channels. The alpha channel
    * defaults to 1.0 .
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    */
   public Color ( final float red, final float green, final float blue ) {

      this.set(red, green, blue, 1.0f);
   }

   /**
    * Creates a color out of red, green, blue and alpha channels. The expected
    * range for each channel is [0.0, 1.0], however these bounds are not
    * checked so as to facilitate color mixing in other color spaces.
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    * @param alpha the alpha channel
    */
   public Color ( final float red, final float green, final float blue,
      final float alpha ) {

      this.set(red, green, blue, alpha);
   }

   /**
    * Returns -1 when this color is less than the comparisand; 1 when it is
    * greater than; 0 when the two are 'equal'. The implementation of this
    * method allows collections of colors to be sorted.
    *
    * @param c the comparisand
    *
    * @return the numeric code
    *
    * @see Color#toHexInt(Color)
    */
   @Override
   public int compareTo ( final Color c ) {

      final int left = Color.toHexInt(this);
      final int right = Color.toHexInt(c);
      return left > right ? 1 : left < right ? -1 : 0;
   }

   /**
    * Tests this color for equivalence to another based on its hexadecimal
    * representation.
    *
    * @param other the color integer
    *
    * @return the equivalence
    *
    * @see Color#toHexInt(Color)
    */
   public boolean equals ( final int other ) {

      return Color.toHexInt(this) == other;
   }

   /**
    * Tests this color for equivalence to another based on its hexadecimal
    * representation.
    *
    * @param other the color long
    *
    * @return the equivalence
    *
    * @see Color#toHexLong(Color)
    */
   public boolean equals ( final long other ) {

      return Color.toHexLong(this) == other;
   }

   /**
    * Tests this color for equivalence with another object.
    *
    * @param obj the object
    *
    * @return the equivalence
    *
    * @see Color#equals(Color)
    */
   @Override
   public boolean equals ( final Object obj ) {

      if ( this == obj ) { return true; }
      if ( obj == null || this.getClass() != obj.getClass() ) { return false; }
      return this.equals(( Color ) obj);
   }

   /**
    * Simulates bracket subscript access in an array.
    *
    * @param index the index
    *
    * @return the element
    */
   public float get ( final int index ) { return this.getAlphaLast(index); }

   /**
    * Simulates bracket access in an array. The alpha channel is treated as
    * the first channel.
    *
    * @param index the index
    *
    * @return the element
    */
   public float getAlphaFirst ( final int index ) {

      switch ( index ) {
         case 0:
         case -4:
            return this.a;

         case 1:
         case -3:
            return this.r;

         case 2:
         case -2:
            return this.g;

         case 3:
         case -1:
            return this.b;

         default:
            return 0.0f;
      }
   }

   /**
    * Simulates bracket access in an array. The alpha channel is treated as
    * the last channel.
    *
    * @param index the index
    *
    * @return the element
    */
   public float getAlphaLast ( final int index ) {

      switch ( index ) {
         case 0:
         case -4:
            return this.r;

         case 1:
         case -3:
            return this.g;

         case 2:
         case -2:
            return this.b;

         case 3:
         case -1:
            return this.a;

         default:
            return 0.0f;
      }
   }

   /**
    * Returns a hash code for this color based on its hexadecimal value.
    *
    * @return the hash code
    */
   @Override
   public int hashCode ( ) { return Color.toHexInt(this); }

   /**
    * Resets this color to opaque white.
    *
    * @return this color
    */
   public Color reset ( ) { return this.set(1.0f, 1.0f, 1.0f, 1.0f); }

   /**
    * Sets a color with bytes. In Java, bytes are signed, within the range
    * [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] .
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    *
    * @return this color
    */
   public Color set ( final byte red, final byte green, final byte blue ) {

      return this.set(IUtils.ONE_255 * ( red & 0xff ), IUtils.ONE_255 * ( green
         & 0xff ), IUtils.ONE_255 * ( blue & 0xff ), 1.0f);
   }

   /**
    * Sets a color with bytes. In Java, bytes are signed, within the range
    * [{@value Byte#MIN_VALUE}, {@value Byte#MAX_VALUE}] .
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    * @param alpha the alpha channel
    *
    * @return this color
    */
   public Color set ( final byte red, final byte green, final byte blue,
      final byte alpha ) {

      return this.set(IUtils.ONE_255 * ( red & 0xff ), IUtils.ONE_255 * ( green
         & 0xff ), IUtils.ONE_255 * ( blue & 0xff ), IUtils.ONE_255 * ( alpha
            & 0xff ));
   }

   /**
    * Sets this color to the source color.
    *
    * @param c the source color
    *
    * @return this color
    */
   public Color set ( final Color c ) {

      return this.set(c.r, c.g, c.b, c.a);
   }

   /**
    * Sets the red, green and blue color channels of this color. The alpha
    * channel is set to 1.0 by default.
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    *
    * @return this color
    */
   public Color set ( final float red, final float green, final float blue ) {

      return this.set(red, green, blue, 1.0f);
   }

   /**
    * Overrides the parent set function for the sake of making RGB parameters
    * clearer and for chainability. The expected range for each channel is
    * [0.0, 1.0], however these bounds are not checked so as to facilitate
    * color mixing in other color spaces.
    *
    * @param red   the red channel
    * @param green the green channel
    * @param blue  the blue channel
    * @param alpha the alpha channel
    *
    * @return this color
    */
   public Color set ( final float red, final float green, final float blue,
      final float alpha ) {

      this.r = red;
      this.g = green;
      this.b = blue;
      this.a = alpha;
      return this;
   }

   /**
    * Returns a float array of length 4 containing this color's components.
    * Defaults to {@link Color.ChannelOrder#ARGB}.
    *
    * @return the array
    */
   public float[] toArray ( ) {

      return this.toArray(ChannelOrder.ARGB);
   }

   /**
    * Puts the colors's components into an existing array at the index
    * provided. Consumes four elements, but the ordering depends on the
    * {@link Color.ChannelOrder}.
    *
    * @param arr   the array
    * @param i     the index
    * @param order the channel order
    *
    * @return the array
    */
   public byte[] toArray ( final byte[] arr, final int i,
      final ChannelOrder order ) {

      final byte rb = ( byte ) ( this.r * 0xff + 0.5f );
      final byte gb = ( byte ) ( this.g * 0xff + 0.5f );
      final byte bb = ( byte ) ( this.b * 0xff + 0.5f );
      final byte ab = ( byte ) ( this.a * 0xff + 0.5f );

      switch ( order ) {

         case ABGR:

            arr[i] = ab;
            arr[i + 1] = bb;
            arr[i + 2] = gb;
            arr[i + 3] = rb;

            break;

         case ARGB:

            arr[i] = ab;
            arr[i + 1] = rb;
            arr[i + 2] = gb;
            arr[i + 3] = bb;

            break;

         case RGBA:

         default:

            arr[i] = rb;
            arr[i + 1] = gb;
            arr[i + 2] = bb;
            arr[i + 3] = ab;

      }

      return arr;
   }

   /**
    * Returns a float array of length 4 containing this color's components.
    *
    * @param order the channel order
    *
    * @return the array
    */
   public float[] toArray ( final ChannelOrder order ) {

      return this.toArray(new float[4], 0, order);
   }

   /**
    * Puts the colors's components into an existing array at the index
    * provided. Consumes four elements, but the ordering depends on the
    * {@link Color.ChannelOrder}.
    *
    * @param arr   the array
    * @param i     the index
    * @param order the channel order
    *
    * @return the array
    */
   public float[] toArray ( final float[] arr, final int i,
      final ChannelOrder order ) {

      switch ( order ) {

         case ABGR:

            arr[i] = this.a;
            arr[i + 1] = this.b;
            arr[i + 2] = this.g;
            arr[i + 3] = this.r;

            break;

         case ARGB:

            arr[i] = this.a;
            arr[i + 1] = this.r;
            arr[i + 2] = this.g;
            arr[i + 3] = this.b;

            break;

         case RGBA:

         default:

            arr[i] = this.r;
            arr[i + 1] = this.g;
            arr[i + 2] = this.b;
            arr[i + 3] = this.a;

      }

      return arr;
   }

   /**
    * Returns a string representation of this color.
    *
    * @return the string
    */
   @Override
   public String toString ( ) { return this.toString(IUtils.FIXED_PRINT); }

   /**
    * Returns a string representation of this color.
    *
    * @param places number of decimal places
    *
    * @return the string
    */
   public String toString ( final int places ) {

      return this.toString(new StringBuilder(96), places).toString();
   }

   /**
    * An internal helper function to format a vector as a Python tuple, then
    * append it to a {@link StringBuilder}. Used for testing purposes to
    * compare results with Blender 2.9x.<br>
    * <br>
    * This is formatted as a tuple where red, green and blue channels have
    * been raised to the power of gamma (usually 2.2, Blender's default sRGB
    * color management setting).<br>
    * <br>
    * If alpha is included, then a four tuple is appended, where alpha is the
    * last element; if not then a three tuple is appended.
    *
    * @param pyCd      the string builder
    * @param gamma     the exponent
    * @param inclAlpha include the alpha channel
    *
    * @return the string
    */
   StringBuilder toBlenderCode ( final StringBuilder pyCd, final float gamma,
      final boolean inclAlpha ) {

      final double gd = gamma;

      pyCd.append('(');
      Utils.toFixed(pyCd, ( float ) Math.pow(this.r, gd), 6);
      pyCd.append(',');
      pyCd.append(' ');
      Utils.toFixed(pyCd, ( float ) Math.pow(this.g, gd), 6);
      pyCd.append(',');
      pyCd.append(' ');
      Utils.toFixed(pyCd, ( float ) Math.pow(this.b, gd), 6);

      if ( inclAlpha ) {
         pyCd.append(',');
         pyCd.append(' ');
         Utils.toFixed(pyCd, this.a, 6);
      }

      pyCd.append(')');
      return pyCd;
   }

   /**
    * Returns a String representation of the color compatible with .ggr (GIMP
    * gradient) file formats. Each channel, including alpha, is represented as
    * a float in [0.0, 1.0] separated by a space.
    *
    * @return the string
    */
   String toGgrString ( ) {

      /*
       * This does not append to a pre-existing StringBuilder because
       * differences between GGR and core gradients mean multiple String
       * conversions of one color.
       */

      final StringBuilder ggr = new StringBuilder(96);
      Utils.toFixed(ggr, this.r, 6);
      ggr.append(' ');
      Utils.toFixed(ggr, this.g, 6);
      ggr.append(' ');
      Utils.toFixed(ggr, this.b, 6);
      ggr.append(' ');
      Utils.toFixed(ggr, this.a, 6);
      return ggr.toString();
   }

   /**
    * Appends a representation of the color compatible with .gpl (GIMP
    * palette) file formats to a {@link StringBuilder}. Each channel,
    * including alpha, is represented an unsigned byte in [0, 255] separated
    * by a space.
    *
    * @param gpl the string builder
    *
    * @return the string builder
    */
   StringBuilder toGplString ( final StringBuilder gpl ) {

      gpl.append(( int ) ( this.r * 0xff + 0.5f ));
      gpl.append(' ');
      gpl.append(( int ) ( this.g * 0xff + 0.5f ));
      gpl.append(' ');
      gpl.append(( int ) ( this.b * 0xff + 0.5f ));
      return gpl;
   }

   /**
    * Internal helper function to assist with methods that need to print many
    * color. Appends to an existing {@link StringBuilder}.
    *
    * @param sb     the string builder
    * @param places the number of places
    *
    * @return the string builder
    */
   StringBuilder toString ( final StringBuilder sb, final int places ) {

      sb.append("{ r: ");
      Utils.toFixed(sb, this.r, places);
      sb.append(", g: ");
      Utils.toFixed(sb, this.g, places);
      sb.append(", b: ");
      Utils.toFixed(sb, this.b, places);
      sb.append(", a: ");
      Utils.toFixed(sb, this.a, places);
      sb.append(' ');
      sb.append('}');
      return sb;
   }

   /**
    * Tests equivalence between this and another color. Converts both to
    * hexadecimal integers.
    *
    * @param c the color
    *
    * @return the evaluation
    *
    * @see Color#toHexInt(Color)
    */
   protected boolean equals ( final Color c ) {

      return Color.toHexInt(this) == Color.toHexInt(c);
   }

   /**
    * Arbitrary hue in HSL and HSV assigned to colors with no saturation that
    * are closer to light, {@value Color#HSL_HUE_LIGHT}. Defaults to a yellow.
    */
   public static final float HSL_HUE_LIGHT = 48.0f / 360.0f;

   /**
    * Arbitrary hue in HSL and HSV assigned to colors with no saturation that
    * are closer to shadow, {@value Color#HSL_HUE_SHADOW}. Defaults to a
    * violet.
    */
   public static final float HSL_HUE_SHADOW = 255.0f / 360.0f;

   /**
    * Arbitrary hue in LCh assigned to colors with no saturation that are
    * closer to light, {@value Color#LCH_HUE_LIGHT}. Defaults to a yellow.
    */
   public static final float LCH_HUE_LIGHT = 99.0f / 360.0f;

   /**
    * Arbitrary hue in LCh assigned to colors with no saturation that are
    * closer to shadow, {@value Color#LCH_HUE_SHADOW}. Defaults to a violet.
    */
   public static final float LCH_HUE_SHADOW = 308.0f / 360.0f;

   /**
    * Look up table for converting colors from linear to standard RGB.
    */
   private static final int[] LTS_LUT = new int[] { 0, 13, 22, 28, 34, 38, 42,
      46, 50, 53, 56, 59, 61, 64, 66, 69, 71, 73, 75, 77, 79, 81, 83, 85, 86,
      88, 90, 92, 93, 95, 96, 98, 99, 101, 102, 104, 105, 106, 108, 109, 110,
      112, 113, 114, 115, 117, 118, 119, 120, 121, 122, 124, 125, 126, 127, 128,
      129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143,
      144, 145, 146, 147, 148, 148, 149, 150, 151, 152, 153, 154, 155, 155, 156,
      157, 158, 159, 159, 160, 161, 162, 163, 163, 164, 165, 166, 167, 167, 168,
      169, 170, 170, 171, 172, 173, 173, 174, 175, 175, 176, 177, 178, 178, 179,
      180, 180, 181, 182, 182, 183, 184, 185, 185, 186, 187, 187, 188, 189, 189,
      190, 190, 191, 192, 192, 193, 194, 194, 195, 196, 196, 197, 197, 198, 199,
      199, 200, 200, 201, 202, 202, 203, 203, 204, 205, 205, 206, 206, 207, 208,
      208, 209, 209, 210, 210, 211, 212, 212, 213, 213, 214, 214, 215, 215, 216,
      216, 217, 218, 218, 219, 219, 220, 220, 221, 221, 222, 222, 223, 223, 224,
      224, 225, 226, 226, 227, 227, 228, 228, 229, 229, 230, 230, 231, 231, 232,
      232, 233, 233, 234, 234, 235, 235, 236, 236, 237, 237, 238, 238, 238, 239,
      239, 240, 240, 241, 241, 242, 242, 243, 243, 244, 244, 245, 245, 246, 246,
      246, 247, 247, 248, 248, 249, 249, 250, 250, 251, 251, 251, 252, 252, 253,
      253, 254, 254, 255, 255 };

   /**
    * Look up table for converting colors from standard to linear RGB.
    */
   private static final int[] STL_LUT = new int[] { 0, 0, 0, 0, 0, 0, 0, 1, 1,
      1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 4, 4,
      4, 4, 4, 5, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 8, 8, 8, 8, 9, 9, 9, 10, 10, 10,
      11, 11, 12, 12, 12, 13, 13, 13, 14, 14, 15, 15, 16, 16, 17, 17, 17, 18,
      18, 19, 19, 20, 20, 21, 22, 22, 23, 23, 24, 24, 25, 25, 26, 27, 27, 28,
      29, 29, 30, 30, 31, 32, 32, 33, 34, 35, 35, 36, 37, 37, 38, 39, 40, 41,
      41, 42, 43, 44, 45, 45, 46, 47, 48, 49, 50, 51, 51, 52, 53, 54, 55, 56,
      57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74,
      76, 77, 78, 79, 80, 81, 82, 84, 85, 86, 87, 88, 90, 91, 92, 93, 95, 96,
      97, 99, 100, 101, 103, 104, 105, 107, 108, 109, 111, 112, 114, 115, 116,
      118, 119, 121, 122, 124, 125, 127, 128, 130, 131, 133, 134, 136, 138, 139,
      141, 142, 144, 146, 147, 149, 151, 152, 154, 156, 157, 159, 161, 163, 164,
      166, 168, 170, 171, 173, 175, 177, 179, 181, 183, 184, 186, 188, 190, 192,
      194, 196, 198, 200, 202, 204, 206, 208, 210, 212, 214, 216, 218, 220, 222,
      224, 226, 229, 231, 233, 235, 237, 239, 242, 244, 246, 248, 250, 253,
      255 };

   /**
    * Tests to see if all color channels are greater than zero.
    *
    * @param c the color
    *
    * @return the evaluation
    */
   public static boolean all ( final Color c ) {

      return c.a > 0.0f && c.r > 0.0f && c.g > 0.0f && c.b > 0.0f;
   }

   /**
    * Tests to see if the alpha channel of the color is greater than zero,
    * i.e. if it has some opacity.
    *
    * @param c the color
    *
    * @return the evaluation
    */
   public static boolean any ( final Color c ) { return c.a > 0.0f; }

   /**
    * Converts two colors to integers, performs the bitwise and operation on
    * them, then converts the result to a color.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output color
    *
    * @return the result
    */
   public static Color bitAnd ( final Color a, final Color b,
      final Color target ) {

      /* @formatter:off */
      return target.set(
         ( ( int ) ( a.r * 0xff + 0.5f ) &
           ( int ) ( b.r * 0xff + 0.5f ) ) * IUtils.ONE_255,
         ( ( int ) ( a.g * 0xff + 0.5f ) &
           ( int ) ( b.g * 0xff + 0.5f ) ) * IUtils.ONE_255,
         ( ( int ) ( a.b * 0xff + 0.5f ) &
           ( int ) ( b.b * 0xff + 0.5f ) ) * IUtils.ONE_255,
         ( ( int ) ( a.a * 0xff + 0.5f ) &
           ( int ) ( b.a * 0xff + 0.5f ) ) * IUtils.ONE_255);
      /* @formatter:on */
   }

   /**
    * Converts a color to an integer, performs the bitwise not operation on
    * it, then converts the result to a color.
    *
    * @param a      the input color
    * @param target the output color
    *
    * @return the negation
    */
   public static Color bitNot ( final Color a, final Color target ) {

      /* @formatter:off */
      return target.set(
         ( ~( int ) ( a.r * 0xff + 0.5f ) & 0xff ) * IUtils.ONE_255,
         ( ~( int ) ( a.g * 0xff + 0.5f ) & 0xff ) * IUtils.ONE_255,
         ( ~( int ) ( a.b * 0xff + 0.5f ) & 0xff ) * IUtils.ONE_255,
         ( ~( int ) ( a.a * 0xff + 0.5f ) & 0xff ) * IUtils.ONE_255);
      /* @formatter:on */
   }

   /**
    * Converts two colors to integers, performs the bitwise inclusive or
    * operation on them, then converts the result to a color.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output color
    *
    * @return the color
    */
   public static Color bitOr ( final Color a, final Color b,
      final Color target ) {

      /* @formatter:off */
      return target.set(
         ( ( int ) ( a.r * 0xff + 0.5f ) |
           ( int ) ( b.r * 0xff + 0.5f ) ) * IUtils.ONE_255,
         ( ( int ) ( a.g * 0xff + 0.5f ) |
           ( int ) ( b.g * 0xff + 0.5f ) ) * IUtils.ONE_255,
         ( ( int ) ( a.b * 0xff + 0.5f ) |
           ( int ) ( b.b * 0xff + 0.5f ) ) * IUtils.ONE_255,
         ( ( int ) ( a.a * 0xff + 0.5f ) |
           ( int ) ( b.a * 0xff + 0.5f ) ) * IUtils.ONE_255);
      /* @formatter:on */
   }

   /**
    * Converts a color to an integer, rotates to the left by the number of
    * places, then converts the result to a color. The rotate a whole color
    * channel, use increments of 8 (8, 16, 24).
    *
    * @param a      the color
    * @param places the number of places
    * @param target the output color
    *
    * @return the rotated color
    *
    * @see Color#fromHex(int, Color)
    * @see Color#toHexInt(Color)
    */
   public static Color bitRotateLeft ( final Color a, final int places,
      final Color target ) {

      final int i = Color.toHexInt(a);
      return Color.fromHex(i << places | i >>> -places, target);
   }

   /**
    * Converts a color to an integer, rotates to the right by the number of
    * places, then converts the result to a color. The rotate a whole color
    * channel, use increments of 8 (8, 16, 24).
    *
    * @param a      the color
    * @param places the number of places
    * @param target the output color
    *
    * @return the rotated color
    *
    * @see Color#fromHex(int, Color)
    * @see Color#toHexInt(Color)
    */
   public static Color bitRotateRight ( final Color a, final int places,
      final Color target ) {

      final int i = Color.toHexInt(a);
      return Color.fromHex(i >>> places | i << -places, target);
   }

   /**
    * Converts a color to an integer, performs a bitwise left shift operation,
    * then converts the result to a color. To shift a whole color channel, use
    * increments of 8 (8, 16, 24).
    *
    * @param a      the color
    * @param places the number of places
    * @param target the output color
    *
    * @return the shifted color
    *
    * @see Color#fromHex(int, Color)
    * @see Color#toHexInt(Color)
    */
   public static Color bitShiftLeft ( final Color a, final int places,
      final Color target ) {

      return Color.fromHex(Color.toHexInt(a) << places, target);
   }

   /**
    * Converts a color to an integer, performs a bitwise right shift
    * operation, then converts the result to a color. To shift a whole color
    * channel, use increments of 8 (8, 16, 24).
    *
    * @param a      the color
    * @param places the number of places
    * @param target the output color
    *
    * @return the shifted color
    *
    * @see Color#fromHex(int, Color)
    * @see Color#toHexInt(Color)
    */
   public static Color bitShiftRight ( final Color a, final int places,
      final Color target ) {

      return Color.fromHex(Color.toHexInt(a) >> places, target);
   }

   /**
    * Converts a color to an integer, performs an unsigned bitwise right shift
    * operation, then converts the result to a color. To shift a whole color
    * channel, use increments of 8 (8, 16, 24).
    *
    * @param a      the color
    * @param places the number of places
    * @param target the output color
    *
    * @return the shifted color
    *
    * @see Color#fromHex(int, Color)
    * @see Color#toHexInt(Color)
    */
   public static Color bitShiftRightUnsigned ( final Color a, final int places,
      final Color target ) {

      return Color.fromHex(Color.toHexInt(a) >>> places, target);
   }

   /**
    * Converts two colors to integers, performs the bitwise exclusive or
    * operation on them, then converts the result to a color.
    *
    * @param a      the left operand
    * @param b      the right operand
    * @param target the output color
    *
    * @return the color
    */
   public static Color bitXor ( final Color a, final Color b,
      final Color target ) {

      /* @formatter:off */
      return target.set(
         ( ( int ) ( a.r * 0xff + 0.5f ) ^
           ( int ) ( b.r * 0xff + 0.5f ) ) * IUtils.ONE_255,
         ( ( int ) ( a.g * 0xff + 0.5f ) ^
           ( int ) ( b.g * 0xff + 0.5f ) ) * IUtils.ONE_255,
         ( ( int ) ( a.b * 0xff + 0.5f ) ^
           ( int ) ( b.b * 0xff + 0.5f ) ) * IUtils.ONE_255,
         ( ( int ) ( a.a * 0xff + 0.5f ) ^
           ( int ) ( b.a * 0xff + 0.5f ) ) * IUtils.ONE_255);
      /* @formatter:on */
   }

   /**
    * Returns the color black, ( 0.0, 0.0, 0.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return black
    */
   public static Color black ( final Color target ) {

      return target.set(0.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Returns the color blue, ( 0.0, 0.0, 1.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return blue
    */
   public static Color blue ( final Color target ) {

      return target.set(0.0f, 0.0f, 1.0f, 1.0f);
   }

   /**
    * Clamps all color channels to the range [0.0, 1.0] .
    *
    * @param a      the color
    * @param target the output color
    *
    * @return the clamped color
    *
    * @see Utils#clamp01(float)
    */
   public static Color clamp01 ( final Color a, final Color target ) {

      return target.set(Utils.clamp01(a.r), Utils.clamp01(a.g), Utils.clamp01(
         a.b), Utils.clamp01(a.a));
   }

   /**
    * Returns the color clear black, ( 0.0, 0.0, 0.0, 0.0 ) .
    *
    * @param target the output color
    *
    * @return clear black
    */
   public static Color clearBlack ( final Color target ) {

      return target.set(0.0f, 0.0f, 0.0f, 0.0f);
   }

   /**
    * Returns the color clear white, ( 1.0, 1.0, 1.0, 0.0 ) .
    *
    * @param target the output color
    *
    * @return clear white
    */
   public static Color clearWhite ( final Color target ) {

      return target.set(1.0f, 1.0f, 1.0f, 0.0f);
   }

   /**
    * Returns the color cyan, ( 0.0, 1.0, 1.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return cyan
    */
   public static Color cyan ( final Color target ) {

      return target.set(0.0f, 1.0f, 1.0f, 1.0f);
   }

   /**
    * Converts a direction to a color. Normalizes the direction, multiplies it
    * by 0.5, then adds 0.5 .
    *
    * @param v      the direction
    * @param target the output color
    *
    * @return the color
    *
    * @see Utils#invSqrtUnchecked(float)
    */
   public static Color fromDir ( final Vec2 v, final Color target ) {

      final float mSq = v.x * v.x + v.y * v.y;
      if ( mSq > 0.0f ) {
         final float mInv = 0.5f * Utils.invSqrtUnchecked(mSq);
         return target.set(v.x * mInv + 0.5f, v.y * mInv + 0.5f, 0.5f, 1.0f);
      }
      return target.set(0.5f, 0.5f, 0.5f, 1.0f);
   }

   /**
    * Converts a direction to a color. Normalizes the direction, multiplies it
    * by 0.5, then adds 0.5 .
    *
    * @param v      the direction
    * @param target the output color
    *
    * @return the color
    *
    * @see Utils#invSqrtUnchecked(float)
    */
   public static Color fromDir ( final Vec3 v, final Color target ) {

      final float mSq = v.x * v.x + v.y * v.y + v.z * v.z;
      if ( mSq > 0.0f ) {
         final float mInv = 0.5f * Utils.invSqrtUnchecked(mSq);
         return target.set(v.x * mInv + 0.5f, v.y * mInv + 0.5f, v.z * mInv
            + 0.5f, 1.0f);
      }
      return target.set(0.5f, 0.5f, 0.5f, 1.0f);
   }

   /**
    * Converts a direction to a color. Normalizes the direction, multiplies it
    * by 0.5, then adds 0.5 .
    *
    * @param v      the direction
    * @param target the output color
    *
    * @return the color
    *
    * @see Utils#invSqrtUnchecked(float)
    */
   public static Color fromDir ( final Vec4 v, final Color target ) {

      final float mSq = v.x * v.x + v.y * v.y + v.z * v.z + v.w * v.w;
      if ( mSq > 0.0f ) {
         final float mInv = 0.5f * Utils.invSqrtUnchecked(mSq);
         return target.set(v.x * mInv + 0.5f, v.y * mInv + 0.5f, v.z * mInv
            + 0.5f, v.w * mInv + 0.5f);
      }
      return target.set(0.5f, 0.5f, 0.5f, 0.5f);
   }

   /**
    * Convert a hexadecimal representation of a color stored as 0xAARRGGBB
    * into a color. Does so by dividing each color channel by 255.
    *
    * @param c      the color in hexadecimal
    * @param target the output color
    *
    * @return the color
    */
   public static Color fromHex ( final int c, final Color target ) {

      /* @formatter:off */
      return target.set(
         ( c >> 0x10 & 0xff ) * IUtils.ONE_255,
         ( c >> 0x08 & 0xff ) * IUtils.ONE_255,
         ( c         & 0xff ) * IUtils.ONE_255,
         ( c >> 0x18 & 0xff ) * IUtils.ONE_255);
      /* @formatter:on */
   }

   /**
    * Converts an array of integers that represent colors in hexadecimal into
    * an array of colors
    *
    * @param cs the colors
    *
    * @return the array
    */
   public static Color[] fromHex ( final int[] cs ) {

      final int len = cs.length;
      final Color[] result = new Color[len];
      for ( int i = 0; i < len; ++i ) {
         result[i] = Color.fromHex(cs[i], new Color());
      }
      return result;
   }

   /**
    * Convert a hexadecimal representation of a color stored as 0xAARRGGBB
    * into a color.
    *
    * @param c      the color in hexadecimal
    * @param target the output color
    *
    * @return the color
    */
   public static Color fromHex ( final long c, final Color target ) {

      /* @formatter:off */
      return target.set(
         ( c >> 0x10 & 0xffL ) * IUtils.ONE_255,
         ( c >> 0x08 & 0xffL ) * IUtils.ONE_255,
         ( c         & 0xffL ) * IUtils.ONE_255,
         ( c >> 0x18 & 0xffL ) * IUtils.ONE_255);
      /* @formatter:on */
   }

   /**
    * Converts an array of longs that represent colors in hexadecimal into an
    * array of colors
    *
    * @param cs the colors
    *
    * @return the array
    */
   public static Color[] fromHex ( final long[] cs ) {

      final int len = cs.length;
      final Color[] result = new Color[len];
      for ( int i = 0; i < len; ++i ) {
         result[i] = Color.fromHex(cs[i], new Color());
      }
      return result;
   }

   /**
    * Attempts to convert a hexadecimal String to a color. Recognized formats
    * include:
    * <ul>
    * <li>"abc" - RGB, one digit per channel.</li>
    * <li>"#abc" - hash tag, RGB, one digit per channel.</li>
    * <li>"aabbcc" - RRGGBB, two digits per channel.
    * <li>"#aabbcc" - hash tag, RRGGBB, two digits per channel.</li>
    * <li>"aabbccdd" - AARRGGBB, two digits per channel.</li>
    * <li>"0xaabbccdd" - '0x' prefix, AARRGGBB, two digits per channel.</li>
    * </ul>
    * The output color will be reset if no suitable format is recognized.
    *
    * @param c      the input String
    * @param target the output color
    *
    * @return the color
    *
    * @see Integer#parseInt(String, int)
    * @see Long#parseLong(String, int)
    * @see Color#fromHex(int, Color)
    * @see String#replaceAll(String, String)
    * @see String#substring(int)
    */
   public static Color fromHex ( final String c, final Color target ) {

      try {

         final int len = c.length();
         switch ( len ) {

            case 3:

               /* Example: "abc" */
               return Color.fromHex(0xff000000 | Integer.parseInt(Pattern
                  .compile("^(.)(.)(.)$").matcher(c).replaceAll("$1$1$2$2$3$3"),
                  16), target);

            case 4:

               /* Example: "#abc" */
               return Color.fromHex(0xff000000 | Integer.parseInt(Pattern
                  .compile("^#(.)(.)(.)$").matcher(c).replaceAll(
                     "#$1$1$2$2$3$3").substring(1), 16), target);

            case 6:

               /* Example: "aabbcc" */
               return Color.fromHex(0xff000000 | Integer.parseInt(c, 16),
                  target);

            case 7:

               /* Example: "#aabbcc" */
               return Color.fromHex(0xff000000 | Integer.parseInt(c.substring(
                  1), 16), target);

            case 8:

               /* Example: "aabbccdd" */
               return Color.fromHex(Long.parseLong(c, 16), target);

            case 10:

               /* Example: "0xaabbccdd" */
               return Color.fromHex(Long.parseLong(c.substring(2), 16), target);

            default:

               return target.reset();
         }

      } catch ( final Exception e ) {
         System.err.println("Could not parse input String correctly.");
      }

      return target.reset();
   }

   /**
    * Converts an array of Strings that represent colors in hexadecimal into
    * an array of colors
    *
    * @param cs the colors
    *
    * @return the array
    */
   public static Color[] fromHex ( final String[] cs ) {

      final int len = cs.length;
      final Color[] result = new Color[len];
      for ( int i = 0; i < len; ++i ) {
         result[i] = Color.fromHex(cs[i], new Color());
      }
      return result;
   }

   /**
    * Raises a color's red, green and blue channels to an exponent. If alpha
    * is true, then the alpha channel is included. <em>Does not clamp the
    * results</em>; a color channel may exceed the range [0.0, 1.0].
    *
    * @param c         the color
    * @param gamma     the gamma
    * @param amplitude the amplitude
    * @param offset    the offset
    * @param alpha     adjust the alpha
    * @param target    the output color
    *
    * @return the result
    */
   public static Color gammaAdjust ( final Color c, final float gamma,
      final float amplitude, final float offset, final boolean alpha,
      final Color target ) {

      final double gd = gamma;
      final double ad = amplitude;
      final double od = offset;

      return target.set(( float ) ( Math.pow(c.r, gd) * ad + od ),
         ( float ) ( Math.pow(c.g, gd) * ad + od ), ( float ) ( Math.pow(c.b,
            gd) * ad + od ), alpha ? ( float ) ( Math.pow(c.a, gd) * ad + od )
               : c.a);
   }

   /**
    * Returns the color lime green, ( 0.0, 1.0, 0.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return green
    */
   public static Color green ( final Color target ) {

      return target.set(0.0f, 1.0f, 0.0f, 1.0f);
   }

   /**
    * Converts from hue, saturation, lightness and alpha to a color with red,
    * green, blue and alpha channels. All arguments are expected to be in the
    * range [0.0, 1.0] .
    *
    * @param hue    the hue
    * @param sat    the saturation
    * @param light  the lightness
    * @param alpha  the transparency
    * @param target the output color
    *
    * @return the color
    *
    * @see Utils#clamp01(float)
    * @see Utils#mod1(float)
    */
   public static Color hslaToRgba ( final float hue, final float sat,
      final float light, final float alpha, final Color target ) {

      final float acl = Utils.clamp01(alpha);
      if ( light <= 0.0f ) { return target.set(0.0f, 0.0f, 0.0f, acl); }
      if ( light >= 1.0f ) { return target.set(1.0f, 1.0f, 1.0f, acl); }
      if ( sat <= 0.0f ) { return target.set(light, light, light, acl); }

      final float scl = sat > 1.0f ? 1.0f : sat;
      final float q = light < 0.5f ? light * ( 1.0f + scl ) : light + scl
         - light * scl;
      final float p = light + light - q;
      final float qnp6 = ( q - p ) * 6.0f;

      final float rHue = Utils.mod1(hue + IUtils.ONE_THIRD);
      final float gHue = Utils.mod1(hue);
      final float bHue = Utils.mod1(hue - IUtils.ONE_THIRD);

      float r = p;
      if ( rHue < IUtils.ONE_SIX ) {
         r = p + qnp6 * rHue;
      } else if ( rHue < 0.5f ) {
         r = q;
      } else if ( rHue < IUtils.TWO_THIRDS ) {
         r = p + qnp6 * ( IUtils.TWO_THIRDS - rHue );
      }

      float g = p;
      if ( gHue < IUtils.ONE_SIX ) {
         g = p + qnp6 * gHue;
      } else if ( gHue < 0.5f ) {
         g = q;
      } else if ( gHue < IUtils.TWO_THIRDS ) {
         g = p + qnp6 * ( IUtils.TWO_THIRDS - gHue );
      }

      float b = p;
      if ( bHue < IUtils.ONE_SIX ) {
         b = p + qnp6 * bHue;
      } else if ( bHue < 0.5f ) {
         b = q;
      } else if ( bHue < IUtils.TWO_THIRDS ) {
         b = p + qnp6 * ( IUtils.TWO_THIRDS - bHue );
      }

      return target.set(r, g, b, acl);
   }

   /**
    * Converts from hue, saturation, lightness and alpha to a color with red,
    * green, blue and alpha channels.
    *
    * @param hsla   the HSLA vector
    * @param target the output color
    *
    * @return the color
    */
   public static Color hslaToRgba ( final Vec4 hsla, final Color target ) {

      return Color.hslaToRgba(hsla.x, hsla.y, hsla.z, hsla.w, target);
   }

   /**
    * Converts from hue, saturation, value and alpha to a color with red,
    * green, blue and alpha channels. All arguments are expected to be in the
    * range [0.0, 1.0] .
    *
    * @param hue    the hue
    * @param sat    the saturation
    * @param val    the value
    * @param alpha  the transparency
    * @param target the output color
    *
    * @return the color
    *
    * @see Utils#clamp01(float)
    * @see Utils#mod1(float)
    */
   public static Color hsvaToRgba ( final float hue, final float sat,
      final float val, final float alpha, final Color target ) {

      final float h = Utils.mod1(hue) * 6.0f;
      final float s = Utils.clamp01(sat);
      final float v = Utils.clamp01(val);
      final float a = Utils.clamp01(alpha);

      final int sector = ( int ) h;
      final float secf = sector;
      final float tint1 = v * ( 1.0f - s );
      final float tint2 = v * ( 1.0f - s * ( h - secf ) );
      final float tint3 = v * ( 1.0f - s * ( 1.0f + secf - h ) );

      switch ( sector ) {
         case 0:
            return target.set(v, tint3, tint1, a);

         case 1:
            return target.set(tint2, v, tint1, a);

         case 2:
            return target.set(tint1, v, tint3, a);

         case 3:
            return target.set(tint1, tint2, v, a);

         case 4:
            return target.set(tint3, tint1, v, a);

         case 5:
            return target.set(v, tint1, tint2, a);

         default:
            return target.reset();
      }
   }

   /**
    * Converts from hue, saturation, value and alpha to a color with red,
    * green, blue and alpha channels.
    *
    * @param hsva   the HSVA vector
    * @param target the output color
    *
    * @return the color
    */
   public static Color hsvaToRgba ( final Vec4 hsva, final Color target ) {

      return Color.hsvaToRgba(hsva.x, hsva.y, hsva.z, hsva.w, target);
   }

   /**
    * Converts a color from CIE L*a*b* to CIE LCh. The output is organized as
    * z: L or lightness, y: C or chroma, x: h or hue, w: alpha. The returned
    * hue is in the range [0.0, 1.0] .
    *
    * @param l      the lightness
    * @param a      the green to red range
    * @param b      the blue to yellow range
    * @param alpha  the alpha channel
    * @param target the output vector
    *
    * @return the LCh vector
    */
   public static Vec4 labaToLcha ( final float l, final float a, final float b,
      final float alpha, final Vec4 target ) {

      final float cSq = a * a + b * b;
      if ( cSq < 0.00005f ) {
         final float fac = Utils.clamp01(l * 0.01f);
         return target.set(Utils.mod1( ( 1.0f - fac ) * Color.LCH_HUE_SHADOW
            + fac * ( 1.0f + Color.LCH_HUE_LIGHT )), 0.0f, l, alpha);
      } else {
         return target.set(Utils.mod1(IUtils.ONE_TAU * ( float ) Math.atan2(b,
            a)), ( float ) Math.sqrt(cSq), l, alpha);
      }
   }

   /**
    * Converts a color from CIE L*a*b* to CIE LCh. The source should be
    * organized as z: L or lightness, x: a or green-red, y: b or blue-yellow,
    * w: alpha. The returned hue is in the range [0.0, 1.0] .
    *
    * @param source the lab vector
    * @param target the output vector
    *
    * @return the LCh color
    */
   public static Vec4 labaToLcha ( final Vec4 source, final Vec4 target ) {

      return Color.labaToLcha(source.z, source.x, source.y, source.w, target);
   }

   /**
    * Converts a color from CIE L*a*b* to CIE XYZ. Assumes D65 illuminant, CIE
    * 1931 2 degrees referents.
    *
    * @param l      the lightness
    * @param a      the green to red range
    * @param b      the blue to yellow range
    * @param alpha  the alpha channel
    * @param target the output vector
    *
    * @return the CIE XYZ color
    */
   public static Vec4 labaToXyza ( final float l, final float a, final float b,
      final float alpha, final Vec4 target ) {

      /*
       * http://www.easyrgb.com/en/math.php D65, CIE 1931 2 degrees; 95.047,
       * 100.0, 108.883; 16.0 / 116.0 = 0.13793103448275862; 1.0 / 116.0 =
       * 0.008620689655172414; 1.0 / 7.787 = 0.12841751101180157
       */

      float vy = ( l + 16.0f ) * 0.00862069f;
      float vx = a * 0.002f + vy;
      float vz = vy - b * 0.005f;

      final float vye3 = vy * vy * vy;
      if ( vye3 > 0.008856f ) {
         vy = vye3;
      } else {
         vy = ( vy - 0.13793103f ) * 0.1284175f;
      }

      final float vxe3 = vx * vx * vx;
      if ( vxe3 > 0.008856f ) {
         vx = vxe3;
      } else {
         vx = ( vx - 0.13793103f ) * 0.1284175f;
      }

      final float vze3 = vz * vz * vz;
      if ( vze3 > 0.008856f ) {
         vz = vze3;
      } else {
         vz = ( vz - 0.13793103f ) * 0.1284175f;
      }

      return target.set(vx * 0.95047f, vy, vz * 1.08883f, alpha);

   }

   /**
    * Converts a color from CIE L*a*b* to CIE XYZ. The source should be
    * organized as z: L or lightness, x: a or green-red, y: b or blue-yellow,
    * w: alpha.
    *
    * @param source the XYZ vector
    * @param target the output vector
    *
    * @return the lab color
    *
    * @see Color#labaToXyza(float, float, float, float, Vec4)
    */
   public static Vec4 labaToXyza ( final Vec4 source, final Vec4 target ) {

      return Color.labaToXyza(source.z, source.x, source.y, source.w, target);
   }

   /**
    * Converts a color from CIE LCh to CIE L*a*b*. The output is organized as
    * z: L or lightness, x: a or green-red, y: b or blue-yellow, w: alpha.
    *
    * @param l      the lightness
    * @param c      the chroma
    * @param h      the hue
    * @param a      the alpha channel
    * @param target the output vector
    *
    * @return the lab vector
    */
   public static Vec4 lchaToLaba ( final float l, final float c, final float h,
      final float a, final Vec4 target ) {

      final double hRad = Utils.mod1(h) * IUtils.TAU_D;
      return target.set(c * ( float ) Math.cos(hRad), c * ( float ) Math.sin(
         hRad), l, a);
   }

   /**
    * Converts a color from CIE LCh to CIE L*a*b*. The output should be
    * organized as z: L or lightness, y: C or chroma, x: h or hue, w: alpha.
    *
    * @param source the lch vector
    * @param target the output vector
    *
    * @return the lab vector
    */
   public static Vec4 lchaToLaba ( final Vec4 source, final Vec4 target ) {

      return Color.lchaToLaba(source.z, source.y, source.x, source.w, target);
   }

   /**
    * Finds the linear to standard conversion for a single color channel in
    * [0, 255] .
    *
    * @param v the value
    *
    * @return the conversion
    */
   public static int linearToStandard ( final int v ) {

      return Color.LTS_LUT[v];
   }

   /**
    * Converts a color from linear RGB to
    * <a href="https://www.wikiwand.com/en/SRGB">standard RGB</a> (sRGB).
    *
    * @param source the linear color
    * @param alpha  adjust the alpha channel
    * @param target the output color
    *
    * @return the standard color
    */
   public static Color lRgbaTosRgba ( final Color source, final boolean alpha,
      final Color target ) {

      /* pow(x, y) := exp(y * ln(x)) does not lead to better performance. */

      return target.set(source.r <= 0.0031308f ? source.r * 12.92f
         : ( float ) ( Math.pow(source.r, 0.4166666666666667d) * 1.055d
            - 0.055d ), source.g <= 0.0031308f ? source.g * 12.92f
               : ( float ) ( Math.pow(source.g, 0.4166666666666667d) * 1.055d
                  - 0.055d ), source.b <= 0.0031308f ? source.b * 12.92f
                     : ( float ) ( Math.pow(source.b, 0.4166666666666667d)
                        * 1.055d - 0.055d ), alpha ? source.a <= 0.0031308f
                           ? source.a * 12.92f : ( float ) ( Math.pow(source.a,
                              0.4166666666666667d) * 1.055d - 0.055d )
                           : source.a);
   }

   /**
    * Converts a color stored in a hexadecimal integer from linear to standard
    * RGB by using a look up table.
    *
    * @param c     the linear color
    * @param alpha transform alpha
    *
    * @return the standard color
    */
   public static int lRgbaTosRgba ( final int c, final boolean alpha ) {

      final int lai = c >> 0x18 & 0xff;
      return ( alpha ? Color.LTS_LUT[lai] : lai ) << 0x18 | Color.LTS_LUT[c
         >> 0x10 & 0xff] << 0x10 | Color.LTS_LUT[c >> 0x08 & 0xff] << 0x08
         | Color.LTS_LUT[c & 0xff];
   }

   /**
    * Converts a color from linear RGB to CIE XYZ.
    *
    * @param c      the color
    * @param target the output vector
    *
    * @return the XYZ color
    *
    * @see Color#lRgbaToXyza(float, float, float, float, Vec4)
    */
   public static Vec4 lRgbaToXyza ( final Color c, final Vec4 target ) {

      return Color.lRgbaToXyza(c.r, c.g, c.b, c.a, target);
   }

   /**
    * Converts a color from linear RGB to CIE XYZ. The values returned are in
    * the range [0.0, 1.0]. References Pharr, Jakob, and Humphreys'
    * <a href="http://www.pbr-book.org/">Physically Based Rendering</a>,
    * section 5.2.2, page 328.
    *
    * @param r      the red component
    * @param g      the green component
    * @param b      the blue component
    * @param a      the alpha component
    * @param target the output vector
    *
    * @return the XYZ values.
    *
    * @see Color#xyzaTolRgba(float, float, float, float, Color)
    */
   public static Vec4 lRgbaToXyza ( final float r, final float g, final float b,
      final float a, final Vec4 target ) {

      // http://www.brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html
      // 0.4124564 0.3575761 0.1804375
      // 0.2126729 0.7151522 0.0721750
      // 0.0193339 0.1191920 0.9503041

      return target.set(0.41241086f * r + 0.35758457f * g + 0.1804538f * b,
         0.21264935f * r + 0.71516913f * g + 0.07218152f * b, 0.019331759f * r
            + 0.11919486f * g + 0.95039004f * b, a);
   }

   /**
    * Returns the relative luminance of the linear RGB color, based on
    * <a href="https://www.wikiwand.com/en/Rec._709#/Luma_coefficients"> Rec.
    * 709 relative luminance</a> coefficients: <code>0.2126</code> for red,
    * <code>0.7152</code> for green and <code>0.0722</code> for blue.
    *
    * @param c the input color
    *
    * @return the luminance
    */
   public static float lRgbLuminance ( final Color c ) {

      return 0.21264935f * c.r + 0.71516913f * c.g + 0.07218152f * c.b;
   }

   /**
    * Returns the relative luminance of the linear RGB color, based on
    * <a href="https://www.wikiwand.com/en/Rec._709#/Luma_coefficients"> Rec.
    * 709 relative luminance</a>.<br>
    * <br>
    * Due to single precision, this may not yield the same result as
    * {@link Color#lRgbLuminance(Color)} .
    *
    * @param c the input color
    *
    * @return the luminance
    */
   public static float lRgbLuminance ( final int c ) {

      /*
       * Coefficients: 0.21264935 / 255.0, 0.71516913 / 255.0, 0.07218152 /
       * 255.0, 0.000833919019607843, 0.002804584823529412,
       * 0.0002830647843137255
       */

      /* @formatter:off */
      return (( c >> 0x10 & 0xff ) *  83391901.0f +
              ( c >> 0x08 & 0xff ) * 280458482.0f +
              ( c         & 0xff ) *  28306478.0f) * 10E-12f;
      /* @formatter:on */
   }

   /**
    * Returns the relative luminance of a color; assumes the color is in sRGB.
    *
    * @param c the color
    *
    * @return the luminance
    *
    * @see Color#sRgbLuminance(Color)
    */
   public static float luminance ( final Color c ) {

      return Color.sRgbLuminance(c);
   }

   /**
    * Returns the relative luminance of a color; assumes the color is in sRGB.
    *
    * @param c the color
    *
    * @return the luminance
    *
    * @see Color#sRgbLuminance(int)
    */
   public static float luminance ( final int c ) {

      return Color.sRgbLuminance(c);
   }

   /**
    * Returns the color magenta, ( 1.0, 0.0, 1.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return magenta
    */
   public static Color magenta ( final Color target ) {

      return target.set(1.0f, 0.0f, 1.0f, 1.0f);
   }

   /**
    * Maps an input vector from an original range to a target range. Useful
    * for mapping colors back into [0.0, 1.0] after they have been mixed in
    * another color space.
    *
    * @param c        the input vector
    * @param lbOrigin lower bound of original range
    * @param ubOrigin upper bound of original range
    * @param lbDest   lower bound of destination range
    * @param ubDest   upper bound of destination range
    * @param target   the output vector
    *
    * @return the mapped value
    *
    * @see Utils#map(float, float, float, float, float)
    */
   public static Color map ( final Color c, final Color lbOrigin,
      final Color ubOrigin, final Color lbDest, final Color ubDest,
      final Color target ) {

      return target.set(Utils.map(c.r, lbOrigin.r, ubOrigin.r, lbDest.r,
         ubDest.r), Utils.map(c.g, lbOrigin.g, ubOrigin.g, lbDest.g, ubDest.g),
         Utils.map(c.b, lbOrigin.b, ubOrigin.b, lbDest.b, ubDest.b), Utils.map(
            c.a, lbOrigin.a, ubOrigin.a, lbDest.a, ubDest.a));
   }

   /**
    * Tests to see if the alpha channel of this color is less than or equal to
    * zero, i.e., if it is completely transparent.
    *
    * @param c the color
    *
    * @return the evaluation
    */
   public static boolean none ( final Color c ) { return c.a <= 0.0f; }

   /**
    * Oscillates between an origin and destination color based on an input
    * step and a pause factor. When the pause is greater than 1.0, the value
    * will be clamped to the pole.
    *
    * @param origin the original color
    * @param dest   the destination color
    * @param step   the step
    * @param pause  the pause factor
    * @param target the output color
    *
    * @return the oscillation
    */
   public static Color pingPong ( final Color origin, final Color dest,
      final float step, final float pause, final Color target ) {

      final float t = 0.5f + 0.5f * pause * Utils.scNorm(step - 0.5f);
      if ( t <= 0.0f ) { return target.set(origin); }
      if ( t >= 1.0f ) { return target.set(dest); }
      final float u = 1.0f - t;
      return target.set(u * origin.r + t * dest.r, u * origin.g + t * dest.g, u
         * origin.b + t * dest.b, u * origin.a + t * dest.a);
   }

   /**
    * Multiplies the red, green and blue color channels of a color by the
    * alpha channel.
    *
    * @param c      the input color
    * @param target the output color
    *
    * @return the multiplied color
    */
   public static Color premul ( final Color c, final Color target ) {

      if ( c.a <= 0.0f ) {
         return target.set(0.0f, 0.0f, 0.0f, 0.0f);
      } else if ( c.a >= 1.0f ) {
         return target.set(c.r, c.g, c.b, 1.0f);
      } else {
         return target.set(c.r * c.a, c.g * c.a, c.b * c.a, c.a);
      }
   }

   /**
    * Reduces the signal, or granularity, of a color's channels. Any level
    * less than 2 or greater than 255 returns sets the target to the input.
    *
    * @param c      the color
    * @param levels the levels
    * @param target the output color
    *
    * @return the posterized color
    *
    * @see Utils#floor(float)
    */
   public static Color quantize ( final Color c, final int levels,
      final Color target ) {

      if ( levels < 2 || levels > 255 ) { return target.set(c); }

      final float levf = levels;
      final float delta = 1.0f / levf;
      return target.set(delta * Utils.floor(0.5f + c.r * levf), delta * Utils
         .floor(0.5f + c.g * levf), delta * Utils.floor(0.5f + c.b * levf),
         delta * Utils.floor(0.5f + c.a * levf));
   }

   /**
    * Creates a random color. Defaults to a random RGB channel.
    *
    * @param rng    the random number generator
    * @param target the output color
    *
    * @return the color
    */
   public static Color random ( final Random rng, final Color target ) {

      return Color.randomRgb(rng, target);
   }

   /**
    * Creates a random color from red, green and blue channels. The alpha
    * channel is not included.
    *
    * @param rng    the random number generator
    * @param target the output color
    *
    * @return the color
    *
    * @see Random#nextFloat()
    */
   public static Color randomRgb ( final Random rng, final Color target ) {

      return target.set(rng.nextFloat(), rng.nextFloat(), rng.nextFloat(),
         1.0f);
   }

   /**
    * Creates a random color from a lower- and upper-bound. The alpha channel
    * is not included.
    *
    * @param rng        the random number generator
    * @param lowerBound the lower bound
    * @param upperBound the upper bound
    * @param target     the output color
    *
    * @return the color
    */
   public static Color randomRgb ( final Random rng, final Color lowerBound,
      final Color upperBound, final Color target ) {

      final float rx = rng.nextFloat();
      final float ry = rng.nextFloat();
      final float rz = rng.nextFloat();
      return target.set( ( 1.0f - rx ) * lowerBound.r + rx * upperBound.r,
         ( 1.0f - ry ) * lowerBound.g + ry * upperBound.g, ( 1.0f - rz )
            * lowerBound.b + rz * upperBound.b, 1.0f);
   }

   /**
    * Creates a random color from red, green, blue and alpha channels.
    *
    * @param rng    the random number generator
    * @param target the output color
    *
    * @return the color
    */
   public static Color randomRgba ( final Random rng, final Color target ) {

      return target.set(rng.nextFloat(), rng.nextFloat(), rng.nextFloat(), rng
         .nextFloat());
   }

   /**
    * Creates a random color from a lower- and upper-bound.
    *
    * @param rng        the random number generator
    * @param lowerBound the lower bound
    * @param upperBound the upper bound
    * @param target     the output color
    *
    * @return the color
    */
   public static Color randomRgba ( final Random rng, final Color lowerBound,
      final Color upperBound, final Color target ) {

      /* @formatter:off */
      final float rx = rng.nextFloat();
      final float ry = rng.nextFloat();
      final float rz = rng.nextFloat();
      final float rw = rng.nextFloat();
      return target.set(
         ( 1.0f - rx ) * lowerBound.r + rx * upperBound.r,
         ( 1.0f - ry ) * lowerBound.g + ry * upperBound.g,
         ( 1.0f - rz ) * lowerBound.b + rz * upperBound.b,
         ( 1.0f - rw ) * lowerBound.a + rw * upperBound.a);
      /* @formatter:on */
   }

   /**
    * Returns the color red, ( 1.0, 0.0, 0.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return red
    */
   public static Color red ( final Color target ) {

      return target.set(1.0f, 0.0f, 0.0f, 1.0f);
   }

   /**
    * Convert a color to gray-scale based on its perceived luminance.
    *
    * @param c      the input color
    * @param target the output color
    *
    * @return the gray scale color
    *
    * @see Color#sRgbLuminance(Color)
    */
   public static Color rgbaToGray ( final Color c, final Color target ) {

      final float lum = Color.sRgbLuminance(c);
      final float vf = lum <= 0.0031308f ? lum * 12.92f : ( float ) ( Math.pow(
         lum, 0.4166666666666667d) * 1.055d - 0.055d );
      return target.set(vf, vf, vf, c.a);
   }

   /**
    * Converts a color to a vector which holds hue, saturation, lightness and
    * alpha.
    *
    * @param c      the color
    * @param target the output vector
    *
    * @return the HSLA vector
    *
    * @see Color#rgbaToHsla(float, float, float, float, Vec4)
    */
   public static Vec4 rgbaToHsla ( final Color c, final Vec4 target ) {

      return Color.rgbaToHsla(c.r, c.g, c.b, c.a, target);
   }

   /**
    * Converts RGBA channels to a vector which holds hue, saturation,
    * lightness and alpha.
    *
    * @param red    the red channel
    * @param green  the green channel
    * @param blue   the blue channel
    * @param alpha  the alpha channel
    * @param target the output vector
    *
    * @return the HSLA values
    */
   public static Vec4 rgbaToHsla ( final float red, final float green,
      final float blue, final float alpha, final Vec4 target ) {

      final float r = red;
      final float g = green;
      final float b = blue;
      final float a = alpha;

      // if ( r < 0.0f ) {
      // r = 0.0f;
      // } else if ( r > 1.0f ) { r = 1.0f; }
      // if ( g < 0.0f ) {
      // g = 0.0f;
      // } else if ( g > 1.0f ) { g = 1.0f; }
      // if ( b < 0.0f ) {
      // b = 0.0f;
      // } else if ( b > 1.0f ) { b = 1.0f; }
      // if ( a < 0.0f ) {
      // a = 0.0f;
      // } else if ( a > 1.0f ) { a = 1.0f; }

      final float gbmx = g > b ? g : b;
      final float gbmn = g < b ? g : b;
      final float mx = gbmx > r ? gbmx : r;
      final float mn = gbmn < r ? gbmn : r;

      final float sum = mx + mn;
      final float diff = mx - mn;
      final float light = sum * 0.5f;

      if ( light < IUtils.ONE_255 ) {
         return target.set(Color.HSL_HUE_SHADOW, 0.0f, 0.0f, a);
      } else if ( light > 1.0f - IUtils.ONE_255 ) {
         return target.set(Color.HSL_HUE_LIGHT, 0.0f, 1.0f, a);
      } else if ( diff < IUtils.ONE_255 ) {
         final float hue = light * ( 1.0f + Color.HSL_HUE_LIGHT ) + ( 1.0f
            - light ) * Color.HSL_HUE_SHADOW;
         return target.set(hue % 1.0f, 0.0f, light, a);
      } else {
         float hue;
         if ( Utils.approx(r, mx, IUtils.ONE_255) ) {
            hue = ( g - b ) / diff;
            if ( g < b ) { hue += 6.0f; }
         } else if ( Utils.approx(g, mx, IUtils.ONE_255) ) {
            hue = 2.0f + ( b - r ) / diff;
         } else {
            hue = 4.0f + ( r - g ) / diff;
         }

         final float sat = light > 0.5f ? diff / ( 2.0f - sum ) : diff / sum;
         return target.set(hue * IUtils.ONE_SIX, sat, light, a);
      }
   }

   /**
    * Converts a color to a vector which holds hue, saturation, value and
    * alpha.
    *
    * @param c      the color
    * @param target the output vector
    *
    * @return the HSVA vector
    *
    * @see Color#rgbaToHsva(float, float, float, float, Vec4)
    */
   public static Vec4 rgbaToHsva ( final Color c, final Vec4 target ) {

      return Color.rgbaToHsva(c.r, c.g, c.b, c.a, target);
   }

   /**
    * Converts RGBA channels to a vector which holds hue, saturation, value
    * and alpha.
    *
    * @param red    the red channel
    * @param green  the green channel
    * @param blue   the blue channel
    * @param alpha  the alpha channel
    * @param target the output vector
    *
    * @return the HSVA values
    */
   public static Vec4 rgbaToHsva ( final float red, final float green,
      final float blue, final float alpha, final Vec4 target ) {

      final float r = red;
      final float g = green;
      final float b = blue;
      final float a = alpha;

      // if ( r < 0.0f ) {
      // r = 0.0f;
      // } else if ( r > 1.0f ) { r = 1.0f; }
      // if ( g < 0.0f ) {
      // g = 0.0f;
      // } else if ( g > 1.0f ) { g = 1.0f; }
      // if ( b < 0.0f ) {
      // b = 0.0f;
      // } else if ( b > 1.0f ) { b = 1.0f; }
      // if ( a < 0.0f ) {
      // a = 0.0f;
      // } else if ( a > 1.0f ) { a = 1.0f; }

      final float gbmx = g > b ? g : b;
      final float gbmn = g < b ? g : b;
      final float mx = gbmx > r ? gbmx : r;

      if ( mx < IUtils.ONE_255 ) {
         return target.set(Color.HSL_HUE_SHADOW, 0.0f, 0.0f, a);
      } else {
         final float mn = gbmn < r ? gbmn : r;
         final float diff = mx - mn;
         if ( diff < IUtils.ONE_255 ) {
            final float light = ( mx + mn ) * 0.5f;
            if ( light > 1.0f - IUtils.ONE_255 ) {
               return target.set(Color.HSL_HUE_LIGHT, 0.0f, 1.0f, a);
            } else {
               final float hue = light * ( 1.0f + Color.HSL_HUE_LIGHT ) + ( 1.0f
                  - light ) * Color.HSL_HUE_SHADOW;
               return target.set(hue % 1.0f, 0.0f, mx, a);
            }
         } else {
            float hue;
            if ( Utils.approx(r, mx, IUtils.ONE_255) ) {
               hue = ( g - b ) / diff;
               if ( g < b ) { hue += 6.0f; }
            } else if ( Utils.approx(g, mx, IUtils.ONE_255) ) {
               hue = 2.0f + ( b - r ) / diff;
            } else {
               hue = 4.0f + ( r - g ) / diff;
            }

            return target.set(hue * IUtils.ONE_SIX, diff / mx, mx, a);
         }
      }
   }

   /**
    * Evaluates whether the color is within the standard RGB gamut of [0.0,
    * 1.0] .
    *
    * @param c color
    *
    * @return the evaluation
    */
   public static boolean rgbIsInGamut ( final Color c ) {

      return Color.rgbIsInGamut(c, 0.0f);
   }

   /**
    * Evaluates whether the color is within the standard RGB gamut of [0.0,
    * 1.0], according to a tolerance.
    *
    * @param c   color
    * @param tol tolerance
    *
    * @return the evaluation
    */
   public static boolean rgbIsInGamut ( final Color c, final float tol ) {

      final float oneptol = 1.0f + tol;
      return c.r >= -tol && c.r <= oneptol && c.g >= -tol && c.g <= oneptol
         && c.b >= -tol && c.b <= oneptol;

   }

   /**
    * Finds the maximum color channel of a color, excluding alpha.
    *
    * @param c the color
    *
    * @return the maximum channel
    *
    * @see Utils#max(float, float, float)
    */
   public static float rgbMax ( final Color c ) {

      return Utils.max(c.r, c.g, c.b);
   }

   /**
    * Finds the minimum color channel of a color, excluding alpha.
    *
    * @param c the color
    *
    * @return the minimum channel
    *
    * @see Utils#min(float, float, float)
    */
   public static float rgbMin ( final Color c ) {

      return Utils.min(c.r, c.g, c.b);
   }

   /**
    * Shifts a color's hue, saturation, lightness and alpha by a vector.
    *
    * @param c      the input color
    * @param shift  the shift
    * @param target the output color
    * @param hsla   the color in HSL
    *
    * @return the shifted color
    *
    * @see Color#rgbaToHsla(Color, Vec4)
    * @see Color#hslaToRgba(Vec4, Color)
    */
   public static Color shiftHsla ( final Color c, final Vec4 shift,
      final Color target, final Vec4 hsla ) {

      Color.rgbaToHsla(c, hsla);

      final float oldSat = hsla.y;
      if ( oldSat > 0.0f ) {
         hsla.x += shift.x;
         hsla.y += shift.y;
      }

      hsla.z += shift.z;
      hsla.w += shift.w;

      return Color.hslaToRgba(hsla, target);
   }

   /**
    * Shifts a color's hue, saturation, value and alpha by a vector.
    *
    * @param c      the input color
    * @param shift  the shift
    * @param target the output color
    * @param hsva   the color in HSB
    *
    * @return the shifted color
    *
    * @see Color#rgbaToHsva(Color, Vec4)
    * @see Color#hsvaToRgba(Vec4, Color)
    */
   public static Color shiftHsva ( final Color c, final Vec4 shift,
      final Color target, final Vec4 hsva ) {

      Color.rgbaToHsva(c, hsva);

      final float oldSat = hsva.y;
      if ( oldSat > 0.0f ) {
         hsva.x += shift.x;
         hsva.y += shift.y;
      }

      hsva.z += shift.z;
      hsva.w += shift.w;

      return Color.hsvaToRgba(hsva, target);
   }

   /**
    * Converts a color from
    * <a href="https://www.wikiwand.com/en/SRGB">standard RGB</a> (sRGB) to
    * linear RGB.
    *
    * @param source the standard color
    * @param alpha  adjust the alpha channel
    * @param target the output color
    *
    * @return the linear color
    */
   public static Color sRgbaTolRgba ( final Color source, final boolean alpha,
      final Color target ) {

      /*
       * pow(x, y) := exp(y * log(x)) does not lead to better performance.
       * pow(x, 2.4) := x * x * pow(x, 0.4) needs more testing...
       */

      return target.set(source.r <= 0.04045f ? source.r * 0.07739938f
         : ( float ) Math.pow( ( source.r + 0.055d ) * 0.9478672985781991d,
            2.4d), source.g <= 0.04045f ? source.g * 0.07739938f
               : ( float ) Math.pow( ( source.g + 0.055d )
                  * 0.9478672985781991d, 2.4d), source.b <= 0.04045f ? source.b
                     * 0.07739938f : ( float ) Math.pow( ( source.b + 0.055d )
                        * 0.9478672985781991d, 2.4d), alpha ? source.a
                           <= 0.04045f ? source.a * 0.07739938f : ( float ) Math
                              .pow( ( source.a + 0.055d ) * 0.9478672985781991d,
                                 2.4d) : source.a);
   }

   /**
    * Converts a color stored in a hexadecimal integer from standard to linear
    * RGB by using a look up table.
    *
    * @param c     the standard color
    * @param alpha transform alpha
    *
    * @return the linear color
    */
   public static int sRgbaTolRgba ( final int c, final boolean alpha ) {

      final int sai = c >> 0x18 & 0xff;
      return ( alpha ? Color.STL_LUT[sai] : sai ) << 0x18 | Color.STL_LUT[c
         >> 0x10 & 0xff] << 0x10 | Color.STL_LUT[c >> 0x08 & 0xff] << 0x08
         | Color.STL_LUT[c & 0xff];
   }

   /**
    * Returns the relative luminance of the standard RGB color, based on
    * <a href="https://www.wikiwand.com/en/Rec._709#/Luma_coefficients"> Rec.
    * 709 relative luminance</a> coefficients: <code>0.2126</code> for red,
    * <code>0.7152</code> for green and <code>0.0722</code> for blue.
    *
    * @param c the input color
    *
    * @return the luminance
    */
   public static float sRgbLuminance ( final Color c ) {

      final double lr = c.r <= 0.04045f ? c.r * 0.07739938080495357d : Math.pow(
         ( c.r + 0.055d ) * 0.9478672985781991d, 2.4d);
      final double lg = c.g <= 0.04045f ? c.g * 0.07739938080495357d : Math.pow(
         ( c.g + 0.055d ) * 0.9478672985781991d, 2.4d);
      final double lb = c.b <= 0.04045f ? c.b * 0.07739938080495357d : Math.pow(
         ( c.b + 0.055d ) * 0.9478672985781991d, 2.4d);

      return ( float ) ( 0.21264934272065283d * lr + 0.7151691357059038d * lg
         + 0.07218152157344333d * lb );
   }

   /**
    * Returns the relative luminance of the standard RGB color, based on
    * <a href="https://www.wikiwand.com/en/Rec._709#/Luma_coefficients"> Rec.
    * 709 relative luminance</a> coefficients: <code>0.2126</code> for red,
    * <code>0.7152</code> for green and <code>0.0722</code> for blue.
    *
    * @param c the input color
    *
    * @return the luminance
    */
   public static float sRgbLuminance ( final int c ) {

      return ( float ) ( 0.0008339189910613837d * Color.STL_LUT[c >> 0x10
         & 0xff] + 0.002804584845905505d * Color.STL_LUT[c >> 0x08 & 0xff]
         + 0.0002830647904840915d * Color.STL_LUT[c & 0xff] );
   }

   /**
    * Finds the standard to linear conversion for a single color channel in
    * [0, 255] .
    *
    * @param v the value
    *
    * @return the conversion
    */
   public static int standardToLinear ( final int v ) {

      return Color.STL_LUT[v];
   }

   /**
    * Returns a String representing the color array in the GIMP palette file
    * format.
    *
    * @param arr the array
    *
    * @return the string
    */
   public static String toGplString ( final Color[] arr ) {

      return Color.toGplString(arr, "Palette");
   }

   /**
    * Returns a String representing the color array in the GIMP palette file
    * format. The number of columns defaults to the square root of the array's
    * length; for example, 16 colors would have 4 columns.
    *
    * @param arr  the array
    * @param name the palette name
    *
    * @return the string
    *
    * @see Utils#ceil(float)
    * @see Utils#sqrt(float)
    */
   public static String toGplString ( final Color[] arr, final String name ) {

      return Color.toGplString(arr, name, Utils.ceil(Utils.sqrt(arr.length)));
   }

   /**
    * Returns a String representing the color array in the GIMP palette file
    * format. The number of columns is for displaying the palette.
    *
    * @param arr  the array
    * @param name the palette name
    * @param cols the display columns
    *
    * @return the string
    *
    * @see Utils#ceil(float)
    * @see Utils#sqrt(float)
    */
   public static String toGplString ( final Color[] arr, final String name,
      final int cols ) {

      return Color.toGplString(arr, name, cols, false);
   }

   /**
    * Returns a String representing the color array in the GIMP palette file
    * format. The number of columns is for displaying the palette. A flag
    * indicates whether or not to append the color's index as the final column
    * of each row. Indices begin at 1, not 0.
    *
    * @param arr    the array
    * @param name   the palette name
    * @param cols   the display columns
    * @param useIdx append the index
    *
    * @return the string
    *
    * @see Color#toHexWeb(Color)
    */
   public static String toGplString ( final Color[] arr, final String name,
      final int cols, final boolean useIdx ) {

      final StringBuilder sb = new StringBuilder(1024);
      sb.append("GIMP Palette");
      sb.append("\nName: ");
      sb.append(name);
      sb.append("\nColumns: ");
      sb.append(cols);
      sb.append("\n# https://github.com/behreajj/CamZup\n");

      final int len = arr.length;
      final int last = len - 1;
      for ( int i = 0; i < len; ++i ) {
         final Color clr = arr[i];
         clr.toGplString(sb);
         sb.append(' ');
         sb.append(Color.toHexWeb(clr).substring(1).toUpperCase());
         if ( useIdx ) {
            sb.append(' ');
            sb.append(i + 1);
         }
         if ( i < last ) { sb.append('\n'); }
      }

      return sb.toString();
   }

   /**
    * Converts a color to an integer where hexadecimal represents the ARGB
    * color channels: 0xAARRGGB .
    *
    * @param c the input color
    *
    * @return the color in hexadecimal
    */
   public static int toHexInt ( final Color c ) {

      /* @formatter:off */
      return ( int ) ( c.a * 0xff + 0.5f ) << 0x18
           | ( int ) ( c.r * 0xff + 0.5f ) << 0x10
           | ( int ) ( c.g * 0xff + 0.5f ) << 0x08
           | ( int ) ( c.b * 0xff + 0.5f );
      /* @formatter:on */
   }

   /**
    * Converts a color to an <code>long</code> where hexadecimal represents
    * the ARGB color channels: 0xAARRGGB .
    *
    * @param c the input color
    *
    * @return the color in hexadecimal
    *
    * @see Color#toHexInt(Color)
    */
   public static long toHexLong ( final Color c ) {

      return Color.toHexInt(c) & 0xffffffffL;
   }

   /**
    * Returns a representation of the color as a hexadecimal code, preceded by
    * a '0x', in the format AARRGGBB.
    *
    * @param c the color
    *
    * @return the string
    *
    * @see Color#toHexString(StringBuilder, Color)
    */
   public static String toHexString ( final Color c ) {

      return Color.toHexString(new StringBuilder(10), c).toString();
   }

   /**
    * Returns a representation of the color as a hexadecimal code, preceded by
    * a '0x', in the format AARRGGBB.
    *
    * @param c the color
    *
    * @return the string
    *
    * @see Color#toHexDigit(StringBuilder, int)
    */
   public static String toHexString ( final int c ) {

      return Color.toHexString(new StringBuilder(10), c).toString();
   }

   /**
    * Returns a representation of the color as a hexadecimal code, preceded by
    * a '0x', in the format AARRGGBB. Appends to an existing
    * {@link StringBuilder}.<br>
    *
    * @param sb the string builder
    * @param c  the color
    *
    * @return the string builder
    *
    * @see Utils#clamp01(float)
    * @see Color#toHexString(StringBuilder, int, int, int, int)
    */
   public static StringBuilder toHexString ( final StringBuilder sb,
      final Color c ) {

      return Color.toHexString(sb, ( int ) ( Utils.clamp01(c.a) * 0xff + 0.5f ),
         ( int ) ( Utils.clamp01(c.r) * 0xff + 0.5f ), ( int ) ( Utils.clamp01(
            c.g) * 0xff + 0.5f ), ( int ) ( Utils.clamp01(c.b) * 0xff + 0.5f ));
   }

   /**
    * Returns a representation of the color as a hexadecimal code, preceded by
    * a '0x', in the format AARRGGBB. Appends to an existing
    * {@link StringBuilder}.<br>
    *
    * @param sb the string builder
    * @param c  the color
    *
    * @return the string builder
    *
    * @see Color#toHexString(StringBuilder, int, int, int, int)
    */
   public static StringBuilder toHexString ( final StringBuilder sb,
      final int c ) {

      return Color.toHexString(sb, c >> 0x18 & 0xff, c >> 0x10 & 0xff, c >> 0x08
         & 0xff, c & 0xff);
   }

   /**
    * Returns a representation of the color as a hexadecimal code, preceded by
    * a '0x', in the format AARRGGBB. Appends to an existing
    * {@link StringBuilder}.<br>
    *
    * @param sb the string builder
    * @param a  the alpha byte
    * @param r  the red byte
    * @param g  the green byte
    * @param b  the blue byte
    *
    * @return the string builder
    *
    * @see Color#toHexDigit(StringBuilder, int)
    */
   public static StringBuilder toHexString ( final StringBuilder sb,
      final int a, final int r, final int g, final int b ) {

      sb.append("0x");
      Color.toHexDigit(sb, a);
      Color.toHexDigit(sb, r);
      Color.toHexDigit(sb, g);
      Color.toHexDigit(sb, b);
      return sb;
   }

   /**
    * Creates a web-friendly representation of the color as a hexadecimal
    * code, preceded by a hash tag, '#', with no alpha.
    *
    * @param c the color
    *
    * @return the string
    *
    * @see Color#toHexWeb(StringBuilder, Color)
    */
   public static String toHexWeb ( final Color c ) {

      return Color.toHexWeb(new StringBuilder(7), c).toString();
   }

   /**
    * Returns a web-friendly representation of the color as a hexadecimal
    * code, preceded by a hash tag, '#', with no alpha. Assumes the number
    * will be formatted as <code>0xAARRGGBB</code> , where alpha is the first
    * channel, followed by red, green and blue.
    *
    * @param c the color
    *
    * @return the string
    *
    * @see Color#toHexWeb(StringBuilder, int)
    */
   public static String toHexWeb ( final int c ) {

      return Color.toHexWeb(new StringBuilder(7), c).toString();
   }

   /**
    * Creates a web-friendly representation of the color as a hexadecimal
    * code, preceded by a hash tag, '#', with no alpha. Appends to an existing
    * {@link StringBuilder}.
    *
    * @param sb the string builder
    * @param c  color
    *
    * @return the string builder
    *
    * @see Utils#clamp01(float)
    * @see Color#toHexWeb(StringBuilder, int, int, int)
    */
   public static StringBuilder toHexWeb ( final StringBuilder sb,
      final Color c ) {

      return Color.toHexWeb(sb, ( int ) ( Utils.clamp01(c.r) * 0xff + 0.5f ),
         ( int ) ( Utils.clamp01(c.g) * 0xff + 0.5f ), ( int ) ( Utils.clamp01(
            c.b) * 0xff + 0.5f ));
   }

   /**
    * Creates a web-friendly representation of the color as a hexadecimal
    * code, preceded by a hash tag, '#', with no alpha. Appends to an existing
    * {@link StringBuilder}.<br>
    * <br>
    * Assumes the number will be formatted as <code>0xAARRGGBB</code> , where
    * alpha is the first channel, followed by red, green and blue.
    *
    * @param sb the string builder
    * @param c  the color
    *
    * @return the string builder
    *
    * @see Color#toHexWeb(StringBuilder, int, int, int)
    */
   public static StringBuilder toHexWeb ( final StringBuilder sb,
      final int c ) {

      return Color.toHexWeb(sb, c >> 0x10 & 0xff, c >> 0x08 & 0xff, c & 0xff);
   }

   /**
    * Creates a web-friendly representation of the color as a hexadecimal
    * code, preceded by a hash tag, '#', with no alpha. Appends to an existing
    * {@link StringBuilder}.<br>
    *
    * @param sb the string builder
    * @param r  the red byte
    * @param g  the green byte
    * @param b  the blue byte
    *
    * @return the string builder
    *
    * @see Color#toHexDigit(StringBuilder, int)
    */
   public static StringBuilder toHexWeb ( final StringBuilder sb, final int r,
      final int g, final int b ) {

      sb.append('#');
      Color.toHexDigit(sb, r);
      Color.toHexDigit(sb, g);
      Color.toHexDigit(sb, b);
      return sb;
   }

   /**
    * Returns a String representing the color array in the JASC-PAL palette
    * file format.
    *
    * @param arr the array
    *
    * @return the string
    */
   public static String toPalString ( final Color[] arr ) {

      final int len = arr.length;
      final int last = len - 1;
      final StringBuilder sb = new StringBuilder(32 + len * 12);
      sb.append("JASC-PAL\n0100\n");
      sb.append(len);
      sb.append('\n');
      for ( int i = 0; i < last; ++i ) {
         arr[i].toGplString(sb);
         sb.append('\n');
      }
      arr[last].toGplString(sb);

      return sb.toString();
   }

   /**
    * Returns a string representation of an array of Colors.
    *
    * @param arr the array
    *
    * @return the string
    */
   public static String toString ( final Color[] arr ) {

      return Color.toString(arr, IUtils.FIXED_PRINT);
   }

   /**
    * Returns a string representation of an array of colors.
    *
    * @param arr    the array
    * @param places the print precision
    *
    * @return the string
    */
   public static String toString ( final Color[] arr, final int places ) {

      final StringBuilder sb = new StringBuilder(1024);
      sb.append('[').append(' ');

      if ( arr != null ) {
         final int len = arr.length;
         final int last = len - 1;

         for ( int i = 0; i < last; ++i ) {
            final Color c = arr[i];
            c.toString(sb, places);
            sb.append(',').append(' ');
         }

         final Color cl = arr[last];
         cl.toString(sb, places);
         sb.append(' ');
      }

      sb.append(']');
      return sb.toString();
   }

   /**
    * Divides the red, green and blue color channels of a color by the alpha
    * channel; reverses {@link Color#premul(Color, Color)}.
    *
    * @param c      the input color
    * @param target the output color
    *
    * @return the multiplied color
    */
   public static Color unpremul ( final Color c, final Color target ) {

      if ( c.a <= 0.0f ) {
         return target.set(0.0f, 0.0f, 0.0f, 0.0f);
      } else if ( c.a >= 1.0f ) {
         return target.set(c.r, c.g, c.b, 1.0f);
      } else {
         final float aInv = 1.0f / c.a;
         return target.set(c.r * aInv, c.g * aInv, c.b * aInv, c.a);
      }
   }

   /**
    * Returns the color white, ( 1.0, 1.0, 1.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return white
    */
   public static Color white ( final Color target ) {

      return target.set(1.0f, 1.0f, 1.0f, 1.0f);
   }

   /**
    * Converts a color from CIE XYZ to CIE L*a*b*. Assumes D65 illuminant, CIE
    * 1931 2 degrees referents.<br>
    * <br>
    * The target is packaged as z: L or lightness, x: a or green-red, y: b or
    * blue-yellow, w: alpha.
    *
    * @param x      the x coordinate
    * @param y      the y coordinate
    * @param z      the z coordinate
    * @param a      the alpha component
    * @param target the output vector
    *
    * @return the Lab color
    *
    * @see Math#pow(double, double)
    */
   public static Vec4 xyzaToLaba ( final float x, final float y, final float z,
      final float a, final Vec4 target ) {

      /*
       * http://www.easyrgb.com/en/math.php 100.0d / 95.047d =
       * 1.0521110608435826d; 100.0d / 108.883d = 0.9184170164304805d; 16.0d /
       * 116.0d = 0.13793103448275862d
       */

      double vx = x * 1.0521110608435826d;
      if ( vx > 0.008856d ) {
         vx = Math.pow(vx, 0.3333333333333333d);
      } else {
         vx = 7.787d * vx + 0.13793103448275862d;
      }

      double vy = y;
      if ( vy > 0.008856d ) {
         vy = Math.pow(vy, 0.3333333333333333d);
      } else {
         vy = 7.787d * vy + 0.13793103448275862d;
      }

      double vz = z * 0.9184170164304805d;
      if ( vz > 0.008856d ) {
         vz = Math.pow(vz, 0.3333333333333333d);
      } else {
         vz = 7.787d * vz + 0.13793103448275862d;
      }

      return target.set(( float ) ( 500.0d * ( vx - vy ) ), ( float ) ( 200.0d
         * ( vy - vz ) ), ( float ) ( 116.0d * vy - 16.0d ), a);
   }

   /**
    * Converts a color from CIE XYZ to CIE L*a*b*. The target is packaged as
    * z: L or lightness, x: a or green-red, y: b or blue-yellow, w: alpha.
    *
    * @param source the XYZ vector
    * @param target the output vector
    *
    * @return the Lab color
    *
    * @see Color#xyzaToLaba(float, float, float, float, Vec4)
    */
   public static Vec4 xyzaToLaba ( final Vec4 source, final Vec4 target ) {

      return Color.xyzaToLaba(source.x, source.y, source.z, source.w, target);
   }

   /**
    * Converts a color from CIE XYZ to linear RGB. Expects input values to be
    * in the range [0.0, 1.0]. References Pharr, Jakob, and Humphreys'
    * <a href="http://www.pbr-book.org/">Physically Based Rendering</a>,
    * section 5.2.2, page 327.
    *
    * @param x      the x coordinate
    * @param y      the y coordinate
    * @param z      the z coordinate
    * @param a      the alpha component
    * @param target the output color
    *
    * @return the color
    *
    * @see Color#lRgbaToXyza(float, float, float, float, Vec4)
    */
   public static Color xyzaTolRgba ( final float x, final float y,
      final float z, final float a, final Color target ) {

      // http://www.brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html
      // 3.2404542 -1.5371385 -0.4985314
      // -0.9692660 1.8760108 0.0415560
      // 0.0556434 -0.2040259 1.0572252

      return target.set(3.2408123f * x - 1.5373085f * y - 0.49858654f * z,
         -0.969243f * x + 1.8759663f * y + 0.041555032f * z, 0.0556384f * x
            - 0.20400746f * y + 1.0571296f * z, a);
   }

   /**
    * Converts a color from CIE XYZ to linear RGB.
    *
    * @param source the XYZ vector
    * @param target the output color
    *
    * @return the color
    *
    * @see Color#xyzaTolRgba(float, float, float, float, Color)
    */
   public static Color xyzaTolRgba ( final Vec4 source, final Color target ) {

      return Color.xyzaTolRgba(source.x, source.y, source.z, source.w, target);
   }

   /**
    * Returns the color yellow, ( 1.0, 1.0, 0.0, 1.0 ) .
    *
    * @param target the output color
    *
    * @return yellow
    */
   public static Color yellow ( final Color target ) {

      return target.set(1.0f, 1.0f, 0.0f, 1.0f);
   }

   /**
    * Mixes two colors by a step in the range [0.0, 1.0] with linear
    * interpolation, then returns an integer.<br>
    * <br>
    * Internal helper function for {@link Gradient} so that a new color does
    * not need to be created as a target.
    *
    * @param origin the origin color
    * @param dest   the destination color
    * @param step   the step
    * @param target the output color
    *
    * @return the mixed color
    */
   static int mix ( final Color origin, final Color dest, final float step ) {

      if ( step <= 0.0f ) { return Color.toHexInt(origin); }
      if ( step >= 1.0f ) { return Color.toHexInt(dest); }

      final float u = 1.0f - step;
      return ( int ) ( ( u * origin.a + step * dest.a ) * 0xff + 0.5f ) << 0x18
         | ( int ) ( ( u * origin.r + step * dest.r ) * 0xff + 0.5f ) << 0x10
         | ( int ) ( ( u * origin.g + step * dest.g ) * 0xff + 0.5f ) << 0x08
         | ( int ) ( ( u * origin.b + step * dest.b ) * 0xff + 0.5f );
   }

   /**
    * Mixes two colors by a step in the range [0.0, 1.0] with linear
    * interpolation in standard RGB.
    *
    * @param origin the origin color
    * @param dest   the destination color
    * @param step   the step
    * @param target the output color
    *
    * @return the mixed color
    */
   static Color mix ( final Color origin, final Color dest, final float step,
      final Color target ) {

      if ( step <= 0.0f ) { return target.set(origin); }
      if ( step >= 1.0f ) { return target.set(dest); }

      final float u = 1.0f - step;
      return target.set(u * origin.r + step * dest.r, u * origin.g + step
         * dest.g, u * origin.b + step * dest.b, u * origin.a + step * dest.a);
   }

   /**
    * A helper function to translate a byte to a hexadecimal string. Does
    * <em>not</em> prefix the String with a hexadecimal indicator, '0x'; this
    * is so that Strings can be concatenated together.
    *
    * @param sb the string builder
    * @param b  the byte
    *
    * @return the string
    */
   static StringBuilder toHexDigit ( final StringBuilder sb, final int b ) {

      final int digit0 = b >> 0x4 & 0xf;
      final int digit1 = b & 0xf;

      /* @formatter:off */
      switch ( digit0 ) {
         case 0xa: sb.append('a'); break;
         case 0xb: sb.append('b'); break;
         case 0xc: sb.append('c'); break;
         case 0xd: sb.append('d'); break;
         case 0xe: sb.append('e'); break;
         case 0xf: sb.append('f'); break;
         default: sb.append(( char ) ( '0' + digit0 ));
      }

      switch ( digit1 ) {
         case 0xa: sb.append('a'); break;
         case 0xb: sb.append('b'); break;
         case 0xc: sb.append('c'); break;
         case 0xd: sb.append('d'); break;
         case 0xe: sb.append('e'); break;
         case 0xf: sb.append('f'); break;
         default: sb.append(( char ) ( '0' + digit1 ));
      }
      /* @formatter:on */

      return sb;
   }

   /**
    * An abstract class to facilitate the creation of color easing functions.
    */
   public abstract static class AbstrEasing implements Utils.EasingFuncObj <
      Color > {

      /**
       * The default constructor.
       */
      protected AbstrEasing ( ) {}

      /**
       * A clamped interpolation between the origin and destination. Defers to
       * an unclamped interpolation, which is to be defined by sub-classes of
       * this class.
       *
       * @param origin the origin color
       * @param dest   the destination color
       * @param step   a factor in [0, 1]
       * @param target the output color
       *
       * @return the eased color
       */
      @Override
      public Color apply ( final Color origin, final Color dest,
         final Float step, final Color target ) {

         final float tf = step;
         if ( tf <= 0.0f ) { return target.set(origin); }
         if ( tf >= 1.0f ) { return target.set(dest); }
         return this.applyUnclamped(origin, dest, step, target);
      }

      /**
       * The interpolation to be defined by subclasses.
       *
       * @param origin the origin color
       * @param dest   the destination color
       * @param step   a factor in [0, 1]
       * @param target the output color
       *
       * @return the eased color
       */
      public abstract Color applyUnclamped ( final Color origin,
         final Color dest, final Float step, final Color target );

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

   }

   /**
    * Order in which to arrange color channels when flattening color to an
    * array.
    */
   public enum ChannelOrder {

      /**
       * Alpha, Blue, Green, Red.
       */
      ABGR ( ),

      /**
       * Alpha, Red, Green, Blue.
       */
      ARGB ( ),

      /**
       * Red, Green, Blue, Alpha.
       */
      RGBA ( );

      /**
       * The default constructor.
       */
      ChannelOrder ( ) {}

   }

   /**
    * Eases between hues in the clockwise direction.
    */
   public static class HueCCW extends HueEasing {

      /**
       * Applies the function.
       *
       * @param origin the origin hue
       * @param dest   the destination hue
       * @param step   the step in a range 0 to 1
       *
       * @return the eased hue
       *
       * @see Utils#mod1(float)
       */
      @Override
      protected float applyPartial ( final float origin, final float dest,
         final float step ) {

         if ( this.diff == 0.0f ) { return this.o; }

         if ( this.oGtd ) {
            return Utils.mod1( ( 1.0f - step ) * this.o + step * ( this.d
               + 1.0f ));
         }

         return ( 1.0f - step ) * this.o + step * this.d;
      }

   }

   /**
    * Eases the hue in the counter-clockwise direction.
    */
   public static class HueCW extends HueEasing {

      /**
       * Applies the function.
       *
       * @param origin the origin hue
       * @param dest   the destination hue
       * @param step   the step in a range 0 to 1
       *
       * @return the eased hue
       *
       * @see Utils#mod1(float)
       */
      @Override
      protected float applyPartial ( final float origin, final float dest,
         final float step ) {

         if ( this.diff == 0.0f ) { return this.d; }

         if ( this.oLtd ) {
            return Utils.mod1( ( 1.0f - step ) * ( this.o + 1.0f ) + step
               * this.d);
         }

         return ( 1.0f - step ) * this.o + step * this.d;
      }

   }

   /**
    * An abstract parent class for hue easing functions.
    */
   public abstract static class HueEasing implements Utils.EasingFuncPrm <
      Float > {

      /**
       * The modulated destination hue.
       */
      protected float d = 0.0f;

      /**
       * The difference between the stop and start hue.
       */
      protected float diff = 0.0f;

      /**
       * The modulated origin hue.
       */
      protected float o = 0.0f;

      /**
       * Whether or not {@link o} is greater than {@link d}.
       */
      protected boolean oGtd = false;

      /**
       * Whether or not {@link o} is less than {@link d}.
       */
      protected boolean oLtd = false;

      /**
       * The default constructor.
       */
      protected HueEasing ( ) {}

      /**
       * The clamped easing function.
       *
       * @param origin the origin hue
       * @param dest   the destination hue
       * @param step   the step in range 0 to 1
       *
       * @return the eased hue
       */
      @Override
      public Float apply ( final Float origin, final Float dest,
         final Float step ) {

         this.eval(origin, dest);
         final float t = step;
         if ( t <= 0.0f ) { return this.o; }
         if ( t >= 1.0f ) { return this.d; }
         return this.applyPartial(origin, dest, t);
      }

      /**
       * Returns the simple name of this class.
       *
       * @return the string
       */
      @Override
      public String toString ( ) { return this.getClass().getSimpleName(); }

      /**
       * The application function to be defined by sub-classes of this class.
       *
       * @param origin the origin hue
       * @param dest   the destination hue
       * @param step   the step
       *
       * @return the eased hue
       */
      protected abstract float applyPartial ( final float origin,
         final float dest, final float step );

      /**
       * A helper function to pass on to sub-classes of this class. Mutates the
       * fields {@link o}, {@link d}, {@link diff}, {@link oLtd} and
       * {@link oGtd}.
       *
       * @param origin the origin hue
       * @param dest   the destination hue
       *
       * @see Utils#mod1(float)
       */
      protected void eval ( final float origin, final float dest ) {

         this.o = Utils.mod1(origin);
         this.d = Utils.mod1(dest);
         this.diff = this.d - this.o;
         this.oLtd = this.o < this.d;
         this.oGtd = this.o > this.d;
      }

   }

   /**
    * Eases between hues by the farthest rotational direction.
    */
   public static class HueFar extends HueEasing {

      /**
       * Applies the function.
       *
       * @param origin the origin hue
       * @param dest   the destination hue
       * @param step   the step in a range 0 to 1
       *
       * @return the eased hue
       *
       * @see Utils#mod1(float)
       */
      @Override
      protected float applyPartial ( final float origin, final float dest,
         final float step ) {

         if ( this.diff == 0.0f || this.oLtd && this.diff < 0.5f ) {
            return Utils.mod1( ( 1.0f - step ) * ( this.o + 1.0f ) + step
               * this.d);
         }

         if ( this.oGtd && this.diff > -0.5f ) {
            return Utils.mod1( ( 1.0f - step ) * this.o + step * ( this.d
               + 1.0f ));
         }

         return ( 1.0f - step ) * this.o + step * this.d;
      }

   }

   /**
    * Eases between hues by the nearest rotational direction.
    */
   public static class HueNear extends HueEasing {

      /**
       * Applies the function.
       *
       * @param origin the origin hue
       * @param dest   the destination hue
       * @param step   the step in a range 0 to 1
       *
       * @return the eased hue
       *
       * @see Utils#mod1(float)
       */
      @Override
      protected float applyPartial ( final float origin, final float dest,
         final float step ) {

         if ( this.diff == 0.0f ) { return this.o; }

         if ( this.oLtd && this.diff > 0.5f ) {
            return Utils.mod1( ( 1.0f - step ) * ( this.o + 1.0f ) + step
               * this.d);
         }

         if ( this.oGtd && this.diff < -0.5f ) {
            return Utils.mod1( ( 1.0f - step ) * this.o + step * ( this.d
               + 1.0f ));
         }

         return ( 1.0f - step ) * this.o + step * this.d;
      }

   }

   /**
    * Converts two colors from
    * <a href="https://www.wikiwand.com/en/SRGB">standard RGB</a> to linear
    * RGB, mixes them with linear interpolation, then converts back to
    * standard RGB.
    */
   public static class LerpLrgba extends AbstrEasing {

      /**
       * Whether or not to include the alpha in the adjustment.
       */
      final boolean alpha;

      /**
       * The mixed color in linear RGB.
       */
      protected final Color cLinear = new Color();

      /**
       * The destination color in linear RGB.
       */
      protected final Color dLinear = new Color();

      /**
       * The origin color in linear RGB.
       */
      protected final Color oLinear = new Color();

      /**
       * Construct a new linear color mixer. Defaults to not including alpha, or
       * transparency, in the adjustment.
       */
      public LerpLrgba ( ) { this(false); }

      /**
       * Construct a new linear color mixer. The flag specifies whether or not
       * alpha should be included in the adjustment.
       *
       * @param alpha flag to adjust alpha
       */
      public LerpLrgba ( final boolean alpha ) {

         this.alpha = alpha;
      }

      /**
       * Applies the function.
       *
       * @param origin the origin color
       * @param dest   the destination color
       * @param step   the step in a range 0 to 1
       * @param target the output color
       *
       * @return the eased color
       *
       * @see Color#sRgbaTolRgba(Color, boolean, Color)
       * @see Color#lRgbaTosRgba(Color, boolean, Color)
       */
      @Override
      public Color applyUnclamped ( final Color origin, final Color dest,
         final Float step, final Color target ) {

         Color.sRgbaTolRgba(origin, this.alpha, this.oLinear);
         Color.sRgbaTolRgba(dest, this.alpha, this.dLinear);

         final float t = step;
         final float u = 1.0f - t;
         this.cLinear.set(u * this.oLinear.r + t * this.dLinear.r, u
            * this.oLinear.g + t * this.dLinear.g, u * this.oLinear.b + t
               * this.dLinear.b, u * this.oLinear.a + t * this.dLinear.a);

         return Color.lRgbaTosRgba(this.cLinear, this.alpha, target);
      }

   }

   /**
    * Eases between two colors in sRGB, i.e., with no gamma correction.
    */
   public static class LerpSrgba extends AbstrEasing {

      /**
       * Applies the function.
       *
       * @param origin the origin color
       * @param dest   the destination color
       * @param step   the step in a range 0 to 1
       * @param target the output color
       *
       * @return the eased color
       */
      @Override
      public Color applyUnclamped ( final Color origin, final Color dest,
         final Float step, final Color target ) {

         final float t = step;
         final float u = 1.0f - t;
         return target.set(u * origin.r + t * dest.r, u * origin.g + t * dest.g,
            u * origin.b + t * dest.b, u * origin.a + t * dest.a);
      }

   }

   /**
    * Eases between colors by hue, saturation and lightness.
    */
   public static class MixHsla extends AbstrEasing {

      /**
       * The origin color in HSLA.
       */
      protected final Vec4 aHsl = new Vec4();

      /**
       * The destination color in HSLA.
       */
      protected final Vec4 bHsl = new Vec4();

      /**
       * The new HSLA color.
       */
      protected final Vec4 cHsl = new Vec4();

      /**
       * The hue easing function.
       */
      protected HueEasing hueFunc;

      /**
       * The default constructor. Creates a mixer with nearest hue interpolation
       * and linear interpolation for saturation and lightness.
       */
      public MixHsla ( ) { this(new HueNear()); }

      /**
       * Creates a color HSLA mixing function with the given easing functions
       * for hue.
       *
       * @param hueFunc the hue easing function
       */
      public MixHsla ( final HueEasing hueFunc ) {

         this.hueFunc = hueFunc;
      }

      /**
       * Applies the function.
       *
       * @param origin the origin color
       * @param dest   the destination color
       * @param step   the step in a range 0 to 1
       * @param target the output color
       *
       * @return the eased color
       *
       * @see Color#rgbaToHsla(Color, Vec4)
       * @see Color#hslaToRgba(Vec4, Color)
       */
      @Override
      public Color applyUnclamped ( final Color origin, final Color dest,
         final Float step, final Color target ) {

         Color.rgbaToHsla(origin, this.aHsl);
         Color.rgbaToHsla(dest, this.bHsl);

         final float aSat = this.aHsl.y;
         final float bSat = this.bHsl.y;

         final float t = step;
         final float u = 1.0f - t;

         if ( aSat > IUtils.ONE_255 && bSat > IUtils.ONE_255 ) {
            this.cHsl.set(this.hueFunc.apply(this.aHsl.x, this.bHsl.x, step), u
               * aSat + t * bSat, u * this.aHsl.z + t * this.bHsl.z, u
                  * this.aHsl.w + t * this.bHsl.w);
            return Color.hslaToRgba(this.cHsl, target);
         } else {
            return target.set(u * origin.r + t * dest.r, u * origin.g + t
               * dest.g, u * origin.b + t * dest.b, u * origin.a + t * dest.a);
         }
      }

      /**
       * Gets the string identifier for the hue easing function.
       *
       * @return the string
       */
      public String getHueFuncString ( ) { return this.hueFunc.toString(); }

      /**
       * Sets the hue easing function.
       *
       * @param hueFunc the easing function
       */
      public void setHueFunc ( final HueEasing hueFunc ) {

         if ( hueFunc != null ) { this.hueFunc = hueFunc; }
      }

   }

   /**
    * Eases between colors by hue, saturation and value.
    */
   public static class MixHsva extends AbstrEasing {

      /**
       * The origin color in HSVA.
       */
      protected final Vec4 aHsv = new Vec4();

      /**
       * The destination color in HSVA.
       */
      protected final Vec4 bHsv = new Vec4();

      /**
       * The new HSVA color.
       */
      protected final Vec4 cHsv = new Vec4();

      /**
       * The hue easing function.
       */
      protected HueEasing hueFunc;

      /**
       * The default constructor. Creates a mixer with nearest hue interpolation
       * and linear interpolation for saturation and value.
       */
      public MixHsva ( ) { this(new HueNear()); }

      /**
       * Creates a color HSVA mixing function with the given easing functions
       * for hue.
       *
       * @param hueFunc the hue easing function
       */
      public MixHsva ( final HueEasing hueFunc ) {

         this.hueFunc = hueFunc;
      }

      /**
       * Applies the function.
       *
       * @param origin the origin color
       * @param dest   the destination color
       * @param step   the step in a range 0 to 1
       * @param target the output color
       *
       * @return the eased color
       *
       * @see Color#rgbaToHsva(Color, Vec4)
       * @see Color#hsvaToRgba(Vec4, Color)
       */
      @Override
      public Color applyUnclamped ( final Color origin, final Color dest,
         final Float step, final Color target ) {

         Color.rgbaToHsva(origin, this.aHsv);
         Color.rgbaToHsva(dest, this.bHsv);

         final float aSat = this.aHsv.y;
         final float bSat = this.bHsv.y;

         final float t = step;
         final float u = 1.0f - t;

         if ( aSat > IUtils.ONE_255 && bSat > IUtils.ONE_255 ) {
            this.cHsv.set(this.hueFunc.apply(this.aHsv.x, this.bHsv.x, step), u
               * aSat + t * bSat, u * this.aHsv.z + t * this.bHsv.z, u
                  * this.aHsv.w + t * this.bHsv.w);
            return Color.hsvaToRgba(this.cHsv, target);
         } else {
            return target.set(u * origin.r + t * dest.r, u * origin.g + t
               * dest.g, u * origin.b + t * dest.b, u * origin.a + t * dest.a);
         }
      }

      /**
       * Gets the string identifier for the hue easing function.
       *
       * @return the string
       */
      public String getHueFuncString ( ) { return this.hueFunc.toString(); }

      /**
       * Sets the hue easing function.
       *
       * @param hueFunc the easing function
       */
      public void setHueFunc ( final HueEasing hueFunc ) {

         if ( hueFunc != null ) { this.hueFunc = hueFunc; }
      }

   }

   /**
    * Eases between two colors in CIE L*a*b* color space. May return colors
    * outside the range [0.0, 1.0] .
    */
   public static class MixLab extends AbstrEasing {

      /**
       * The mixed color in CIE L*a*b*.
       */
      protected final Vec4 cLab = new Vec4();

      /**
       * The mixed color in linear RGB.
       */
      protected final Color cLinear = new Color();

      /**
       * The mixed color in CIE XYZ.
       */
      protected final Vec4 cXyz = new Vec4();

      /**
       * The destination color in CIE L*a*b*.
       */
      protected final Vec4 dLab = new Vec4();

      /**
       * The destination color in linear RGB.
       */
      protected final Color dLinear = new Color();

      /**
       * The destination color in CIE XYZ.
       */
      protected final Vec4 dXyz = new Vec4();

      /**
       * The origin color in CIE L*a*b*.
       */
      protected final Vec4 oLab = new Vec4();

      /**
       * The origin color in linear RGB.
       */
      protected final Color oLinear = new Color();

      /**
       * The origin color in CIE XYZ.
       */
      protected final Vec4 oXyz = new Vec4();

      /**
       * Applies the function.
       *
       * @param origin the origin color
       * @param dest   the destination color
       * @param step   the step in a range 0 to 1
       * @param target the output color
       *
       * @return the eased color
       *
       * @see Color#sRgbaTolRgba(Color, boolean, Color)
       * @see Color#lRgbaToXyza(Color, Vec4)
       * @see Color#xyzaToLaba(Vec4, Vec4)
       * @see Color#labaToXyza(Vec4, Vec4)
       * @see Color#xyzaTolRgba(Vec4, Color)
       * @see Color#lRgbaTosRgba(Color, boolean, Color)
       * @see Vec4#mix(Vec4, Vec4, float, Vec4)
       */
      @Override
      public Color applyUnclamped ( final Color origin, final Color dest,
         final Float step, final Color target ) {

         Color.sRgbaTolRgba(origin, false, this.oLinear);
         Color.lRgbaToXyza(this.oLinear, this.oXyz);
         Color.xyzaToLaba(this.oXyz, this.oLab);

         Color.sRgbaTolRgba(dest, false, this.dLinear);
         Color.lRgbaToXyza(this.dLinear, this.dXyz);
         Color.xyzaToLaba(this.dXyz, this.dLab);

         Vec4.mix(this.oLab, this.dLab, step, this.cLab);

         Color.labaToXyza(this.cLab, this.cXyz);
         Color.xyzaTolRgba(this.cXyz, this.cLinear);
         Color.lRgbaTosRgba(this.cLinear, false, target);

         return target;
      }

   }

   /**
    * Eases between two colors in sRGB, i.e., with no gamma correction, using
    * the smooth step formula: <em>t</em><sup>2</sup> ( 3.0 - 2.0 <em>t</em> )
    * .
    */
   public static class SmoothStepSrgba extends AbstrEasing {

      /**
       * Applies the function.
       *
       * @param origin the origin color
       * @param dest   the destination color
       * @param step   the step in a range 0 to 1
       * @param target the output color
       *
       * @return the eased color
       */
      @Override
      public Color applyUnclamped ( final Color origin, final Color dest,
         final Float step, final Color target ) {

         final float s = step;
         final float t = s * s * ( 3.0f - ( s + s ) );
         final float u = 1.0f - t;
         return target.set(u * origin.r + t * dest.r, u * origin.g + t * dest.g,
            u * origin.b + t * dest.b, u * origin.a + t * dest.a);
      }

   }

}
