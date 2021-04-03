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
    * Creates a color out of red, green, blue and alpha channels.
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
      if ( obj == null ) { return false; }
      if ( this.getClass() != obj.getClass() ) { return false; }
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
    * Resets this color to the color white.
    *
    * @return this color
    *
    * @see Color#white(Color)
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
    * clearer and for chainability.
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
    * Clamps a color to a lower and upper bound.
    *
    * @param a          the input color
    * @param lowerBound the lower bound
    * @param upperBound the upper bound
    * @param target     the output color
    *
    * @return the clamped color
    *
    * @see Utils#clamp(float, float, float)
    */
   public static Color clamp ( final Color a, final Color lowerBound,
      final Color upperBound, final Color target ) {

      return target.set(Utils.clamp(a.r, lowerBound.r, upperBound.r), Utils
         .clamp(a.g, lowerBound.g, upperBound.g), Utils.clamp(a.b, lowerBound.b,
            upperBound.b), Utils.clamp(a.a, lowerBound.a, upperBound.a));
   }

   /**
    * Ensures that the values of the color are clamped to the range [0.0, 1.0]
    * .
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
    * Converts a color from CMYK to RGBA.
    *
    * @param cyan    the cyan channel
    * @param magenta the magenta channel
    * @param yellow  the yellow channel
    * @param black   the black channel
    * @param alpha   the transparency
    * @param target  the output color
    *
    * @return the color
    *
    * @see Utils#clamp01(float)
    */
   public static Color cmykaToRgba ( final float cyan, final float magenta,
      final float yellow, final float black, final float alpha,
      final Color target ) {

      final float j = 1.0f - black;
      return target.set(Utils.clamp01( ( 1.0f - cyan ) * j), Utils.clamp01(
         ( 1.0f - magenta ) * j), Utils.clamp01( ( 1.0f - yellow ) * j), Utils
            .clamp01(alpha));
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
    * into a color.
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
    * Returns one of the 16 web safe colors from a string key word. See
    * <a href="https://en.wikipedia.org/wiki/Web_colors#HTML_color_names">HTML
    * color names</a>.<br>
    * <br>
    * Differences in naming conventions include:
    * <ul>
    * <li>(0.0, 1.0, 1.0) may be either aqua or cyan;</li>
    * <li>(1.0, 0.0, 1.0) may be either fuchsia or magenta;</li>
    * <li>(0.0, 1.0, 0.0) is lime, not green;</li>
    * <li>(0.0, 0.5, 0.0) is green.</li>
    * </ul>
    *
    * @param keyword the key word
    * @param target  the output color
    *
    * @return the color by keyword
    */
   public static Color fromKeyword ( final String keyword,
      final Color target ) {

      /*
       * Switch casing with strings is messy. Decompiled code uses the string's
       * hash code instead. See Stack Overflow for discussions on how stable
       * Strings are across platforms.
       */

      final int hsh = keyword.toLowerCase().trim().hashCode();
      switch ( hsh ) {
         case -1081301904:
            /* "maroon" */
            return target.set(0.5f, 0.0f, 0.0f, 1.0f);

         case -976943172:
            /* "purple" */
            return target.set(0.5f, 0.0f, 0.5f, 1.0f);

         case -902311155:
            /* "silver" */
            return target.set(0.75f, 0.75f, 0.75f, 1.0f);

         case -734239628:
            return Color.yellow(target);

         case -519653673:
         case 828922025:
            /* "fuchsia" or "magenta" */
            return Color.magenta(target);

         case 112785:
            return Color.red(target);

         case 3002044:
         case 3068707:
            /* "aqua" or "cyan" */
            return Color.cyan(target);

         case 3027034:
            return Color.blue(target);

         case 3181155:
            /* "gray" */
            return target.set(0.5f, 0.5f, 0.5f, 1.0f);

         case 3321813:
            /* "lime" */
            return Color.green(target);

         case 3374006:
            /* "navy" */
            return target.set(0.0f, 0.0f, 0.5f, 1.0f);

         case 3555932:
            /* "teal" */
            return target.set(0.0f, 0.5f, 0.5f, 1.0f);

         case 93818879:
            return Color.black(target);

         case 98619139:
            /* "green" */
            return target.set(0.0f, 0.5f, 0.0f, 1.0f);

         case 105832923:
            /* "olive" */
            return target.set(0.5f, 0.5f, 0.0f, 1.0f);

         case 113101865:
            return Color.white(target);

         default:
            return target.reset();
      }
   }

   /**
    * Raises a color's red, green and blue channels to the power of a scalar.
    * The alpha channel is unaffected. Useful when adjusting the color's
    * gamma.
    *
    * @param a      left operand
    * @param b      right operand
    * @param target the output color
    *
    * @return the result
    */
   public static Color gammaAdjust ( final Color a, final float b,
      final Color target ) {

      final double bd = b;
      return target.set(( float ) Math.pow(a.r, bd), ( float ) Math.pow(a.g,
         bd), ( float ) Math.pow(a.b, bd), a.a);
   }

   /**
    * Returns the color green, ( 0.0, 1.0, 0.0, 1.0 ) .
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
    * green, blue and alpha channels.
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
      float g = p;
      float b = p;

      if ( rHue < IUtils.ONE_SIX ) {
         r = p + qnp6 * rHue;
      } else if ( rHue < 0.5f ) {
         r = q;
      } else if ( rHue < IUtils.TWO_THIRDS ) {
         r = p + qnp6 * ( IUtils.TWO_THIRDS - rHue );
      }

      if ( gHue < IUtils.ONE_SIX ) {
         g = p + qnp6 * gHue;
      } else if ( gHue < 0.5f ) {
         g = q;
      } else if ( gHue < IUtils.TWO_THIRDS ) {
         g = p + qnp6 * ( IUtils.TWO_THIRDS - gHue );
      }

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
    * green, blue and alpha channels.
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
    * Inverts a color by subtracting the red, green and blue channels from
    * one. Similar to bitNot, except alpha is unaffected. Also similar to
    * adding 0.5 to the x component of a {@link Vec4} storing hue, saturation
    * and value.
    *
    * @param c      the color
    * @param target the output color
    *
    * @return the inverse
    *
    * @see Utils#clamp01(float)
    */
   public static Color inverse ( final Color c, final Color target ) {

      return target.set(Utils.clamp01(1.0f - c.r), Utils.clamp01(1.0f - c.g),
         Utils.clamp01(1.0f - c.b), Utils.clamp01(c.a));
   }

   /**
    * Returns the relative luminance of the color, based on
    * <a href="https://www.wikiwand.com/en/Rec._709#/Luma_coefficients"> Rec.
    * 709 relative luminance</a> coefficients: <code>0.2126</code> for red,
    * <code>0.7152</code> for green and <code>0.0722</code> for blue.
    *
    * @param c the input color
    *
    * @return the luminance
    */
   public static float luminance ( final Color c ) {

      return 0.2126f * c.r + 0.7152f * c.g + 0.0722f * c.b;
   }

   /**
    * Returns the relative luminance of the color, based on
    * <a href="https://www.wikiwand.com/en/Rec._709#/Luma_coefficients"> Rec.
    * 709 relative luminance</a>.<br>
    * <br>
    * Colors stored as integers are less precise than those stored as
    * <code>float</code>s (1.0 / 255.0 being the smallest difference between a
    * channel of two integer colors). Combined with single precision when
    * multiplying small numbers (all weighting factors must be divided by
    * 255.0 ), this will not yield the same result as
    * {@link Color#luminance(Color)} .
    *
    * @param c the input color
    *
    * @return the luminance
    */
   public static float luminance ( final int c ) {

      /*
       * Coefficients: 0.2126 / 255.0 ; 0.7152 / 255.0 ; 0.0722 / 255.0 . In
       * double precision: (a) 0.0008337254901960785d ; (b)
       * 0.002804705882352941d ; (c) 0.0002831372549019608d .
       */

      /* @formatter:off */
      return (( c >> 0x10 & 0xff ) *  83372550.0f +
              ( c >> 0x08 & 0xff ) * 280470590.0f +
              ( c         & 0xff ) *  28313725.0f) * 10E-12f;
      /* @formatter:on */
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
   public static Color preMul ( final Color c, final Color target ) {

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
    * Converts a color from RGBA to CYMKA. Stores the output in a float array
    * that is assumed to be 5 elements long.
    *
    * @param c      the color
    * @param target the output array
    *
    * @return the array
    */
   public static float[] rgbaToCmyka ( final Color c, final float[] target ) {

      return Color.rgbaToCmyka(c.r, c.g, c.b, c.a, target);
   }

   /**
    * Converts a color from RGBA to CYMKA. Stores the output in a float array
    * that is assumed to be 5 elements long.
    *
    * @param red    the red channel
    * @param green  the green channel
    * @param blue   the blue channel
    * @param alpha  the transparency
    * @param target the output array
    *
    * @return the array
    *
    * @see Utils#clamp01(float)
    */
   public static float[] rgbaToCmyka ( final float red, final float green,
      final float blue, final float alpha, final float[] target ) {

      final float k = 1.0f - Utils.max(red, green, blue);
      final float j = k != 0.0f ? 1.0f / ( 1.0f - k ) : 0.0f;
      target[0] = Utils.clamp01(j * ( 1.0f - red - k ));
      target[1] = Utils.clamp01(j * ( 1.0f - green - k ));
      target[2] = Utils.clamp01(j * ( 1.0f - blue - k ));
      target[3] = Utils.clamp01(k);
      target[4] = Utils.clamp01(alpha);
      return target;
   }

   /**
    * Convert a color to gray-scale based on its perceived luminance.
    *
    * @param c      the input color
    * @param target the output color
    *
    * @return the gray scale color
    *
    * @see Color#luminance(Color)
    */
   public static Color rgbaToGray ( final Color c, final Color target ) {

      final float lum = Color.luminance(c);
      return target.set(lum, lum, lum, c.a);
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

      final float gbmx = green > blue ? green : blue;
      final float gbmn = green < blue ? green : blue;
      final float mx = gbmx > red ? gbmx : red;
      final float mn = gbmn < red ? gbmn : red;

      final float light = ( mx + mn ) * 0.5f;
      if ( mx == mn ) {
         return target.set(0.0f, 0.0f, light, alpha);
      } else {
         final float diff = mx - mn;
         final float sum = mx + mn;
         float hue;
         if ( mx == red ) {
            hue = ( green - blue ) / diff;
            if ( green < blue ) { hue += 6.0f; }
         } else if ( mx == green ) {
            hue = 2.0f + ( blue - red ) / diff;
         } else {
            hue = 4.0f + ( red - green ) / diff;
         }
         hue *= IUtils.ONE_SIX;
         return target.set(hue, light > 0.5f ? diff / ( 2.0f - sum ) : diff
            / sum, light, alpha);
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

      final float gbmx = green > blue ? green : blue;
      final float gbmn = green < blue ? green : blue;
      final float mx = gbmx > red ? gbmx : red;
      final float mn = gbmn < red ? gbmn : red;

      final float diff = mx - mn;
      float hue = 0.0f;

      if ( diff != 0.0f ) {
         if ( red == mx ) {
            hue = ( green - blue ) / diff;
            if ( green < blue ) { hue += 6.0f; }
         } else if ( green == mx ) {
            hue = 2.0f + ( blue - red ) / diff;
         } else {
            hue = 4.0f + ( red - green ) / diff;
         }

         hue *= IUtils.ONE_SIX;
      }

      return target.set(hue, mx != 0.0f ? diff / mx : 0.0f, mx, alpha);
   }

   /**
    * Converts a color from RGB to CIE XYZ.
    *
    * @param c      the color
    * @param target the output vector
    *
    * @return the XYZ color
    *
    * @see Color#rgbaToXyza(float, float, float, float, Vec4)
    */
   public static Vec4 rgbaToXyza ( final Color c, final Vec4 target ) {

      return Color.rgbaToXyza(c.r, c.g, c.b, c.a, target);
   }

   /**
    * Converts a color from RGB to CIE XYZ. References Pharr, Jakob, and
    * Humphreys' <a href="http://www.pbr-book.org/">Physically Based
    * Rendering</a>, section 5.2, page 328.
    *
    * @param r      the red component
    * @param g      the green component
    * @param b      the blue component
    * @param a      the alpha component
    * @param target the output vector
    *
    * @return the XYZ values.
    */
   public static Vec4 rgbaToXyza ( final float r, final float g, final float b,
      final float a, final Vec4 target ) {

      return target.set(0.412453f * r + 0.35758f * g + 0.180423f * b, 0.212671f
         * r + 0.71516f * g + 0.072169f * b, 0.019334f * r + 0.119193f * g
            + 0.950227f * b, a);
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
    * @see Utils#clamp01(float)
    * @see Color#rgbaToHsla(Color, Vec4)
    * @see Color#hslaToRgba(Vec4, Color)
    */
   public static Color shiftHsla ( final Color c, final Vec4 shift,
      final Color target, final Vec4 hsla ) {

      Color.rgbaToHsla(c, hsla);
      Vec4.add(hsla, shift, hsla);
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
    * @see Utils#clamp01(float)
    * @see Color#rgbaToHsva(Color, Vec4)
    * @see Color#hsvaToRgba(Vec4, Color)
    */
   public static Color shiftHsva ( final Color c, final Vec4 shift,
      final Color target, final Vec4 hsva ) {

      Color.rgbaToHsva(c, hsva);
      Vec4.add(hsva, shift, hsva);
      return Color.hsvaToRgba(hsva, target);
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
    */
   public static String toGplString ( final Color[] arr, final String name,
      final int cols, boolean useIdx ) {

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
    * @see Color#toHexInt(Color)
    * @see Color#toHexString(int)
    */
   public static String toHexString ( final Color c ) {

      return Color.toHexString(Color.toHexInt(c));
   }

   /**
    * Returns a representation of the color as a hexadecimal code, preceded by
    * a '0x', in the format AARRGGBB.
    *
    * @param c the color
    *
    * @return the string
    *
    * @see Integer#toHexString(int)
    */
   public static String toHexString ( final int c ) {

      return "0x" + Integer.toHexString(c);
   }

   /**
    * Returns a web-friendly representation of the color as a hexadecimal
    * code, preceded by a hash tag, '#', with no alpha.
    *
    * @param c the color
    *
    * @return the string
    *
    * @see Color#toHexString(byte, StringBuilder)
    */
   public static String toHexWeb ( final Color c ) {

      final StringBuilder sb = new StringBuilder(7);
      sb.append('#');
      Color.toHexString(( byte ) ( c.r * 0xff + 0.5f ), sb);
      Color.toHexString(( byte ) ( c.g * 0xff + 0.5f ), sb);
      Color.toHexString(( byte ) ( c.b * 0xff + 0.5f ), sb);
      return sb.toString();
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
    * @see Color#toHexString(byte, StringBuilder)
    */
   public static String toHexWeb ( final int c ) {

      final StringBuilder sb = new StringBuilder(7);
      sb.append('#');
      Color.toHexString(( byte ) ( c >> 0x10 & 0xff ), sb);
      Color.toHexString(( byte ) ( c >> 0x08 & 0xff ), sb);
      Color.toHexString(( byte ) ( c & 0xff ), sb);
      return sb.toString();
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
    * Converts a color from CIE XYZ to RGB. References Pharr, Jakob, and
    * Humphreys' <a href="http://www.pbr-book.org/">Physically Based
    * Rendering</a>, section 5.2, page 327.
    *
    * @param x      the x coordinate
    * @param y      the y coordinate
    * @param z      the z coordinate
    * @param a      the alpha component
    * @param target the output color
    *
    * @return the color
    */
   public static Color xyzaToRgba ( final float x, final float y, final float z,
      final float a, final Color target ) {

      return target.set(3.240479f * x - 1.53715f * y - 0.498535f * z, -0.969256f
         * x + 1.875991f * y + 0.041556f * z, 0.055648f * x - 0.204043f * y
            + 1.057311f * z, a);
   }

   /**
    * Converts a color from CIE XYZ to RGB.
    *
    * @param v      the XYZ vector
    * @param target the output color
    *
    * @return the color
    *
    * @see Color#xyzaToRgba(float, float, float, float, Color)
    */
   public static Color xyzaToRgba ( final Vec4 v, final Color target ) {

      return Color.xyzaToRgba(v.x, v.y, v.z, v.w, target);
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
    * interpolation.
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
    * @param b the byte
    *
    * @return the string
    */
   protected static StringBuilder toHexString ( final byte b,
      final StringBuilder sb ) {

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
    * Eases between two colors.
    */
   public static class LerpRgba extends AbstrEasing {

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
       * The lightness easing function.
       */
      protected Utils.LerpUnclamped lightFunc;

      /**
       * The saturation easing function.
       */
      protected Utils.LerpUnclamped satFunc;

      /**
       * The default constructor. Creates a mixer with nearest hue interpolation
       * and linear interpolation for saturation and lightness.
       */
      public MixHsla ( ) { this(new HueNear()); }

      /**
       * Creates a color HSLA mixing function with the given hue easing
       * function. Saturation and lightness are governed by linear
       * interpolation.
       *
       * @param hueFunc the hue easing function
       */
      public MixHsla ( final HueEasing hueFunc ) {

         this(hueFunc, new Utils.Lerp(), new Utils.Lerp());
      }

      /**
       * Creates a color HSLA mixing function with the given easing functions
       * for hue, saturation and lightness.
       *
       * @param hueFunc   the hue easing function
       * @param satFunc   the saturation easing function
       * @param lightFunc the lightness easing function
       */
      public MixHsla ( final HueEasing hueFunc,
         final Utils.LerpUnclamped satFunc,
         final Utils.LerpUnclamped lightFunc ) {

         this.hueFunc = hueFunc;
         this.satFunc = satFunc;
         this.lightFunc = lightFunc;
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

         /* @formatter:off */
         final float t = step;
         Color.rgbaToHsla(origin, this.aHsl);
         Color.rgbaToHsla(dest, this.bHsl);
         this.cHsl.set(
            this.hueFunc.apply(this.aHsl.x, this.bHsl.x, step),
            this.satFunc.apply(this.aHsl.y, this.bHsl.y, step),
            this.lightFunc.apply(this.aHsl.z, this.bHsl.z, step),
            ( 1.0f - t ) * this.aHsl.w + t * this.bHsl.w);
         return Color.hslaToRgba(this.cHsl, target);
         /* @formatter:on */
      }

      /**
       * Gets the string identifier for the hue easing function.
       *
       * @return the string
       */
      public String getHueFuncString ( ) { return this.hueFunc.toString(); }

      /**
       * Gets the string identifier for the lightness easing function.
       *
       * @return the string
       */
      public String getLightFuncString ( ) {

         return this.lightFunc.toString();
      }

      /**
       * Gets the string identifier for the saturation easing function.
       *
       * @return the string
       */
      public String getSatFuncString ( ) { return this.satFunc.toString(); }

      /**
       * Sets the hue easing function.
       *
       * @param hueFunc the easing function
       */
      public void setHueFunc ( final HueEasing hueFunc ) {

         if ( hueFunc != null ) { this.hueFunc = hueFunc; }
      }

      /**
       * Sets the lightness easing function.
       *
       * @param lightFunc the lightness function
       */
      public void setLightFunc ( final Utils.LerpUnclamped lightFunc ) {

         if ( lightFunc != null ) { this.lightFunc = lightFunc; }
      }

      /**
       * Sets the saturation easing function.
       *
       * @param satFunc the saturation function
       */
      public void setSatFunc ( final Utils.LerpUnclamped satFunc ) {

         if ( satFunc != null ) { this.satFunc = satFunc; }
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
       * The saturation easing function.
       */
      protected Utils.LerpUnclamped satFunc;

      /**
       * The value easing function.
       */
      protected Utils.LerpUnclamped valFunc;

      /**
       * The default constructor. Creates a mixer with nearest hue interpolation
       * and linear interpolation for saturation and value.
       */
      public MixHsva ( ) { this(new HueNear()); }

      /**
       * Creates a color HSVA mixing function with the given hue easing
       * function. Saturation and value are governed by linear interpolation.
       *
       * @param hueFunc the hue easing function
       */
      public MixHsva ( final HueEasing hueFunc ) {

         this(hueFunc, new Utils.Lerp(), new Utils.Lerp());
      }

      /**
       * Creates a color HSVA mixing function with the given easing functions
       * for hue, saturation and value.
       *
       * @param hueFunc the hue easing function
       * @param satFunc the saturation easing function
       * @param valFunc the value easing function
       */
      public MixHsva ( final HueEasing hueFunc,
         final Utils.LerpUnclamped satFunc,
         final Utils.LerpUnclamped valFunc ) {

         this.hueFunc = hueFunc;
         this.satFunc = satFunc;
         this.valFunc = valFunc;
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

         /* @formatter:off */
         final float t = step;
         Color.rgbaToHsva(origin, this.aHsv);
         Color.rgbaToHsva(dest, this.bHsv);
         this.cHsv.set(
            this.hueFunc.apply(this.aHsv.x, this.bHsv.x, step),
            this.satFunc.apply(this.aHsv.y, this.bHsv.y, step),
            this.valFunc.apply(this.aHsv.z, this.bHsv.z, step),
            ( 1.0f - t ) * this.aHsv.w + t * this.bHsv.w);
         return Color.hsvaToRgba(this.cHsv, target);
         /* @formatter:on */
      }

      /**
       * Gets the string identifier for the hue easing function.
       *
       * @return the string
       */
      public String getHueFuncString ( ) { return this.hueFunc.toString(); }

      /**
       * Gets the string identifier for the saturation easing function.
       *
       * @return the string
       */
      public String getSatFuncString ( ) { return this.satFunc.toString(); }

      /**
       * Gets the string identifier for the value easing function.
       *
       * @return the string
       */
      public String getValFuncString ( ) { return this.valFunc.toString(); }

      /**
       * Sets the hue easing function.
       *
       * @param hueFunc the easing function
       */
      public void setHueFunc ( final HueEasing hueFunc ) {

         if ( hueFunc != null ) { this.hueFunc = hueFunc; }
      }

      /**
       * Sets the saturation easing function.
       *
       * @param satFunc the saturation function
       */
      public void setSatFunc ( final Utils.LerpUnclamped satFunc ) {

         if ( satFunc != null ) { this.satFunc = satFunc; }
      }

      /**
       * Sets the value easing function.
       *
       * @param valFunc the easing function
       */
      public void setValFunc ( final Utils.LerpUnclamped valFunc ) {

         if ( valFunc != null ) { this.valFunc = valFunc; }
      }

   }

   /**
    * Eases between two colors with the smooth step formula:
    * <em>t</em><sup>2</sup> ( 3.0 - 2.0 <em>t</em> ) .
    */
   public static class SmoothStepRgba extends AbstrEasing {

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
